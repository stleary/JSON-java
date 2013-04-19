package org.json.zip;

import java.io.IOException;
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
 * JSONzip is a compression scheme for JSON text.
 *
 * @author JSON.org
 * @version 2013-04-18
 */

/**
 * A compressor implements the compression behavior of JSONzip. It provides a
 * zip method that takes a JSONObject or JSONArray and delivers a stream of
 * bits to a BitWriter.
 *
 * FOR EVALUATION PURPOSES ONLY. THIS PACKAGE HAS NOT BEEN TESTED ADEQUATELY
 * FOR PRODUCTION USE.
 */
public class Compressor extends JSONzip {

    /**
     * A compressor outputs to a BitWriter.
     */
    final BitWriter bitwriter;

    /**
     * Create a new compressor. It may be used for an entire session or
     * subsession.
     *
     * @param bitwriter
     *            The BitWriter this Compressor will output to. Don't forget to
     *            flush.
     */
    public Compressor(BitWriter bitwriter) {
        super();
        this.bitwriter = bitwriter;
    }

    /**
     * Return a 4 bit code for a character in a JSON number. The digits '0' to
     * '9' get the codes 0 to 9. '.' is 10, '-' is 11, '+' is 12, and 'E' or
     * 'e' is 13.
     *
     * @param digit
     *            An ASCII character from a JSIN number.
     * @return
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
     * @throws IOException
     */
    private void one() throws JSONException {
        if (probe) {
            log(1);
        }
        write(1, 1);
    }

    /**
     * Pad the output to fill an allotment of bits.
     *
     * @param factor
     *            The size of the bit allotment. A value of 8 will complete and
     *            flush the current byte. If you don't pad, then some of the
     *            last bits might not be sent to the Output Stream.
     * @throws JSONException
     */
    public void pad(int factor) throws JSONException {
        try {
            this.bitwriter.pad(factor);
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
     * @throws JSONException
     */
    private void write(Kim kim, Huff huff) throws JSONException {
        write(kim, 0, kim.length, huff);
    }

    /**
     * Write a range of bytes from a Kim with Huffman encoding.
     *
     * @param kim
     *            A Kim containing the bytes to be written.
     * @param from
     *            The index of the first byte to write.
     * @param thru
     *            The index after the last byte to write.
     * @param huff
     *            The Huffman encoder.
     * @throws JSONException
     */
    private void write(Kim kim, int from, int thru, Huff huff)
            throws JSONException {
        for (int at = from; at < thru; at += 1) {
            write(kim.get(at), huff);
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
    private void writeAndTick(int integer, Keep keep) throws JSONException {
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
     * @param jsonarray
     * @throws JSONException
     */
    private void writeArray(JSONArray jsonarray) throws JSONException {

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
                writeObject((JSONObject) value);
            } else if (value instanceof JSONArray) {
                writeArray((JSONArray) value);
            } else {
                throw new JSONException("Unrecognized object");
            }
        }
    }

    /**
     * Write the name of an object property. Names have their own Keep and
     * Huffman encoder because they are expected to be a more restricted set.
     *
     * @param name
     * @throws JSONException
     */
    private void writeName(String name) throws JSONException {

// If this name has already been registered, then emit its integer and
// increment its usage count.

        Kim kim = new Kim(name);
        int integer = this.namekeep.find(kim);
        if (integer != none) {
            one();
            writeAndTick(integer, this.namekeep);
        } else {

// Otherwise, emit the string with Huffman encoding, and register it.

            zero();
            write(kim, this.namehuff);
            write(end, namehuff);
            this.namekeep.register(kim);
        }
    }

    /**
     * Write a JSON object.
     *
     * @param jsonobject
     * @return
     * @throws JSONException
     */
    private void writeObject(JSONObject jsonobject) throws JSONException {

// JSONzip has two encodings for objects: Empty Objects (zipEmptyObject) and
// non-empty objects (zipObject).

        boolean first = true;
        Iterator keys = jsonobject.keys();
        while (keys.hasNext()) {
            if (probe) {
                log("\n");
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
     * @param string
     * @throws JSONException
     */
    private void writeString(String string) throws JSONException {

// Special case for empty strings.

        if (string.length() == 0) {
            zero();
            zero();
            write(end, this.substringhuff);
            zero();
        } else {
            Kim kim = new Kim(string);

// Look for the string in the strings keep. If it is found, emit its
// integer and count that as a use.

            int integer = this.stringkeep.find(kim);
            if (integer != none) {
                one();
                writeAndTick(integer, this.stringkeep);
            } else {

// But if it is not found, emit the string's substrings. Register the string
// so that the next lookup will succeed.

                writeSubstring(kim);
                this.stringkeep.register(kim);
            }
        }
    }

    /**
     * Write a string, attempting to match registered substrings.
     *
     * @param kim
     * @throws JSONException
     */
    private void writeSubstring(Kim kim) throws JSONException {
        this.substringkeep.reserve();
        zero();
        int from = 0;
        int thru = kim.length;
        int until = thru - JSONzip.minSubstringLength;
        int previousFrom = none;
        int previousThru = 0;

// Find a substring from the substring keep.

        while (true) {
            int at;
            int integer = none;
            for (at = from; at <= until; at += 1) {
                integer = this.substringkeep.match(kim, at, thru);
                if (integer != none) {
                    break;
                }
            }
            if (integer == none) {
                break;
            }

// If a substring is found, emit any characters that were before the matched
// substring. Then emit the substring's integer and loop back to match the
// remainder with another substring.

            if (from != at) {
                zero();
                write(kim, from, at, this.substringhuff);
                write(end, this.substringhuff);
                if (previousFrom != none) {
                    this.substringkeep.registerOne(kim, previousFrom,
                            previousThru);
                    previousFrom = none;
                }
            }
            one();
            writeAndTick(integer, this.substringkeep);
            from = at + this.substringkeep.length(integer);
            if (previousFrom != none) {
                this.substringkeep.registerOne(kim, previousFrom,
                        previousThru);
                previousFrom = none;
            }
            previousFrom = at;
            previousThru = from + 1;
        }

// If a substring is not found, then emit the remaining characters.

        zero();
        if (from < thru) {
            write(kim, from, thru, this.substringhuff);
            if (previousFrom != none) {
                this.substringkeep.registerOne(kim, previousFrom, previousThru);
            }
        }
        write(end, this.substringhuff);
        zero();

// Register the string's substrings in the trie in hopes of future substring
// matching.

        substringkeep.registerMany(kim);
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
            int integer = this.values.find(string);
            if (integer != none) {
                write(2, 2);
                writeAndTick(integer, this.values);
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
                        write((int) longer, 7);
                        return;
                    }
                    one();
                    write((int) longer, 14);
                    return;
                }
            }
            write(1, 2);
            for (int i = 0; i < string.length(); i += 1) {
                write(bcd(string.charAt(i)), 4);
            }
            write(endOfNumber, 4);
            this.values.register(string);
        } else {
            write(3, 2);
            writeJSON(value);
        }
    }

    /**
     * Output a zero bit.
     *
     * @throws JSONException
     *
     * @throws IOException
     */
    private void zero() throws JSONException {
        if (probe) {
            log(0);
        }
        write(0, 1);
    }

    /**
     * Compress a JSONObject.
     *
     * @param jsonobject
     * @throws JSONException
     */
    public void zip(JSONObject jsonobject) throws JSONException {
        begin();
        writeJSON(jsonobject);
    }

    /**
     * Compress a JSONArray.
     *
     * @param jsonarray
     * @throws JSONException
     */
    public void zip(JSONArray jsonarray) throws JSONException {
        begin();
        writeJSON(jsonarray);
    }
}
