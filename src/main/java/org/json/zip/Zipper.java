package org.json.zip;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.Kim;

/*
 Copyright (c) 2013 JSON.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 The Software shall be used for Good, not Evil.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

/**
 * JSONzip is a binary compression scheme for JSON text.
 *
 * @author JSON.org
 * @version 2014-05-03
 */

/**
 * An encoder implements the compression behavior of JSONzip. It provides a
 * zip method that takes a JSONObject or JSONArray and delivers a stream of
 * bits to a BitWriter.
 *
 * FOR EVALUATION PURPOSES ONLY. THIS PACKAGE HAS NOT BEEN TESTED ADEQUATELY
 * FOR PRODUCTION USE.
 */
public class Zipper extends JSONzip {

    /**
     * An encoder outputs to a BitWriter.
     */
    final BitWriter bitwriter;

    /**
     * Create a new encoder. It may be used for an entire session or
     * subsession.
     *
     * @param bitwriter
     *            The BitWriter this encoder will output to.
     *            Don't forget to flush.
     */
    public Zipper(BitWriter bitwriter) {
        super();
        this.bitwriter = bitwriter;
    }

    /**
     * Return a 4 bit code for a character in a JSON number. The digits '0' to
     * '9' get the codes 0 to 9. '.' is 10, '-' is 11, '+' is 12, and 'E' or
     * 'e' is 13.
     *
     * @param digit
     *            An ASCII character from a JSON number.
     * @return The number code.
     */
    private static int bcd(char digit) {
        if (digit >= '0' && digit <= '9') {
            return digit - '0';
        }
        switch (digit) {
        case '.':
            return 10;
        case '-':
            return 11;
        case '+':
            return 12;
        default:
            return 13;
        }
    }

    /**
     * Finish the final byte and flush the bitwriter. This does the same thing
     * as pad(8).
     *
     * @throws JSONException
     */
    public void flush() throws JSONException {
        pad(8);
    }

    /**
     * Output a one bit.
     *
     * @throws JSONException
     */
    private void one() throws JSONException {
        write(1, 1);
    }

    /**
     * Pad the output to fill an allotment of bits.
     *
     * @param width
     *            The size of the bit allotment. A value of 8 will complete and
     *            flush the current byte. If you don't pad, then some of the
     *            last bits might not be sent to the Output Stream.
     * @throws JSONException
     */
    public void pad(int width) throws JSONException {
        try {
            this.bitwriter.pad(width);
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    /**
     * Write a number, using the number of bits necessary to hold the number.
     *
     * @param integer
     *            The value to be encoded.
     * @param width
     *            The number of bits to encode the value, between 0 and 32.
     * @throws JSONException
     */
    private void write(int integer, int width) throws JSONException {
        try {
            this.bitwriter.write(integer, width);
            if (probe) {
                log(integer, width);
            }
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    /**
     * Write an integer with Huffman encoding. The bit pattern that is written
     * will be determined by the Huffman encoder.
     *
     * @param integer
     *            The value to be written.
     * @param huff
     *            The Huffman encoder.
     * @throws JSONException
     */
    private void write(int integer, Huff huff) throws JSONException {
        huff.write(integer, this.bitwriter);
    }

    /**
     * Write each of the bytes in a kim with Huffman encoding.
     *
     * @param kim
     *            A kim containing the bytes to be written.
     * @param huff
     *            The Huffman encoder.
     * @param ext
     *            The Huffman encoder for the extended bytes.
     * @throws JSONException
     */
    private void write(Kim kim, Huff huff, Huff ext) throws JSONException {
        for (int at = 0; at < kim.length; at += 1) {
            int c = kim.get(at);
            write(c, huff);
            while ((c & 128) == 128) {
                at += 1;
                c = kim.get(at);
                write(c, ext);
            }
        }
    }

    /**
     * Write an integer, using the number of bits necessary to hold the number
     * as determined by its keep, and increment its usage count in the keep.
     *
     * @param integer
     *            The value to be encoded.
     * @param keep
     *            The Keep that the integer is one of.
     * @throws JSONException
     */
    private void write(int integer, Keep keep) throws JSONException {
        int width = keep.bitsize();
        keep.tick(integer);
        if (probe) {
            log("\"" + keep.value(integer) + "\"");
        }
        write(integer, width);
    }

    /**
     * Write a JSON Array.
     *
     * @param jsonarray The JSONArray to write.
     * @throws JSONException If the write fails.
     */
    private void write(JSONArray jsonarray) throws JSONException {

// JSONzip has three encodings for arrays:
// The array is empty (zipEmptyArray).
// First value in the array is a string (zipArrayString).
// First value in the array is not a string (zipArrayValue).

        boolean stringy = false;
        int length = jsonarray.length();
        if (length == 0) {
            write(zipEmptyArray, 3);
        } else {
            Object value = jsonarray.get(0);
            if (value == null) {
                value = JSONObject.NULL;
            }
            if (value instanceof String) {
                stringy = true;
                write(zipArrayString, 3);
                writeString((String) value);
            } else {
                write(zipArrayValue, 3);
                writeValue(value);
            }
            for (int i = 1; i < length; i += 1) {
                if (probe) {
                    log();
                }
                value = jsonarray.get(i);
                if (value == null) {
                    value = JSONObject.NULL;
                }
                if (value instanceof String != stringy) {
                    zero();
                }
                one();
                if (value instanceof String) {
                    writeString((String) value);
                } else {
                    writeValue(value);
                }
            }
            zero();
            zero();

        }
    }

    /**
     * Write a JSON value.
     *
     * @param value
     *            One of these types: JSONObject, JSONArray (or Map or
     *            Collection or array), Number (or Integer or Long or Double),
     *            or String, or Boolean, or JSONObject.NULL, or null.
     * @throws JSONException
     */
    private void writeJSON(Object value) throws JSONException {
        if (JSONObject.NULL.equals(value)) {
            write(zipNull, 3);
        } else if (Boolean.FALSE.equals(value)) {
            write(zipFalse, 3);
        } else if (Boolean.TRUE.equals(value)) {
            write(zipTrue, 3);
        } else {
            if (value instanceof Map) {
                value = new JSONObject((Map) value);
            } else if (value instanceof Collection) {
                value = new JSONArray((Collection) value);
            } else if (value.getClass().isArray()) {
                value = new JSONArray(value);
            }
            if (value instanceof JSONObject) {
                write((JSONObject) value);
            } else if (value instanceof JSONArray) {
                write((JSONArray) value);
            } else {
                throw new JSONException("Unrecognized object");
            }
        }
    }

    /**
     * Write the name of an object property. Names have their own Keep and
     * Huffman encoder because they are expected to be a more restricted set.
     *
     * @param name The name string.
     * @throws JSONException
     */
    private void writeName(String name) throws JSONException {

// If this name has already been registered, then emit its integer and
// increment its usage count.

        Kim kim = new Kim(name);
        int integer = this.namekeep.find(kim);
        if (integer != none) {
            one();
            write(integer, this.namekeep);
        } else {

// Otherwise, emit the string with Huffman encoding, and register it.

            zero();
            write(kim, this.namehuff, this.namehuffext);
            write(end, namehuff);
            this.namekeep.register(kim);
        }
    }

    /**
     * Write a JSON object.
     *
     * @param jsonobject The JSONObject to be written.
     * @throws JSONException
     */
    private void write(JSONObject jsonobject) throws JSONException {

// JSONzip has two encodings for objects: Empty Objects (zipEmptyObject) and
// non-empty objects (zipObject).

        boolean first = true;
        Iterator<String> keys = jsonobject.keys();
        while (keys.hasNext()) {
            if (probe) {
                log();
            }
            Object key = keys.next();
            if (key instanceof String) {
                if (first) {
                    first = false;
                    write(zipObject, 3);
                } else {
                    one();
                }
                writeName((String) key);
                Object value = jsonobject.get((String) key);
                if (value instanceof String) {
                    zero();
                    writeString((String) value);
                } else {
                    one();
                    writeValue(value);
                }
            }
        }
        if (first) {
            write(zipEmptyObject, 3);
        } else {
            zero();
        }
    }

    /**
     * Write a string.
     *
     * @param string The string to write.
     * @throws JSONException
     */
    private void writeString(String string) throws JSONException {

// Special case for empty strings.

        if (string.length() == 0) {
            zero();
            write(end, this.stringhuff);
        } else {
            Kim kim = new Kim(string);

// Look for the string in the strings keep. If it is found, emit its
// integer and count that as a use.

            int integer = this.stringkeep.find(kim);
            if (integer != none) {
                one();
                write(integer, this.stringkeep);
            } else {

// But if it is not found, emit the string's characters. Register the string
// so that a later lookup can succeed.

                zero();
                write(kim, this.stringhuff, this.stringhuffext);
                write(end, this.stringhuff);
                this.stringkeep.register(kim);
            }
        }
    }

    /**
     * Write a value.
     *
     * @param value
     *            One of these types: Boolean, Number, etc.
     * @throws JSONException
     */
    private void writeValue(Object value) throws JSONException {
        if (value instanceof Number) {
            String string = JSONObject.numberToString((Number) value);
            int integer = this.valuekeep.find(string);
            if (integer != none) {
                write(2, 2);
                write(integer, this.valuekeep);
                return;
            }
            if (value instanceof Integer || value instanceof Long) {
                long longer = ((Number) value).longValue();
                if (longer >= 0 && longer < int14) {
                    write(0, 2);
                    if (longer < int4) {
                        zero();
                        write((int) longer, 4);
                        return;
                    }
                    one();
                    if (longer < int7) {
                        zero();
                        write((int)(longer - int4), 7);
                        return;
                    }
                    one();
                    write((int)(longer - int7), 14);
                    return;
                }
            }
            write(1, 2);
            for (int i = 0; i < string.length(); i += 1) {
                write(bcd(string.charAt(i)), 4);
            }
            write(endOfNumber, 4);
            this.valuekeep.register(string);
        } else {
            write(3, 2);
            writeJSON(value);
        }
    }

    /**
     * Output a zero bit.
     *
     * @throws JSONException
     */
    private void zero() throws JSONException {
        write(0, 1);
    }

    /**
     * Encode a JSONObject.
     *
     * @param jsonobject The JSONObject.
     * @throws JSONException
     */
    public void encode(JSONObject jsonobject) throws JSONException {
        generate();
        writeJSON(jsonobject);
    }

    /**
     * Encode a JSONArray.
     *
     * @param jsonarray The JSONArray.
     * @throws JSONException
     */
    public void encode(JSONArray jsonarray) throws JSONException {
        generate();
        writeJSON(jsonarray);
    }
}
