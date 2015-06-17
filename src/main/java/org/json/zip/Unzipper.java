package org.json.zip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.Kim;

/*
 Copyright (c) 2012 JSON.org

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

public class Unzipper extends JSONzip {

    /**
     * A decoder reads bits from a BitReader.
     */
    BitReader bitreader;

    /**
     * Create a new unzipper. It may be used for an entire session or
     * subsession.
     *
     * @param bitreader
     *            The bitreader that this decoder will read from.
     */
    public Unzipper(BitReader bitreader) {
        super();
        this.bitreader = bitreader;
    }

    /**
     * Read one bit.
     *
     * @return true if 1, false if 0.
     * @throws JSONException
     */
    private boolean bit() throws JSONException {
        boolean value;
        try {
            value = this.bitreader.bit();
            if (probe) {
                log(value ? 1 : 0);
            }
            return value;
        } catch (Throwable e) {
            throw new JSONException(e);
        }

    }

    /**
     * Read enough bits to obtain an integer from the keep, and increase that
     * integer's weight.
     *
     * @param keep The keep providing the context.
     * @param bitreader The bitreader that is the source of bits.
     * @return The value associated with the number.
     * @throws JSONException
     */
    private Object getAndTick(Keep keep, BitReader bitreader)
            throws JSONException {
        try {
            int width = keep.bitsize();
            int integer = bitreader.read(width);
            Object value = keep.value(integer);
            if (JSONzip.probe) {
                JSONzip.log("\"" + value + "\"");
                JSONzip.log(integer, width);
            }
            if (integer >= keep.length) {
                throw new JSONException("Deep error.");
            }
            keep.tick(integer);
            return value;
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    /**
     * The pad method skips the bits that padded a stream to fit some
     * allocation. pad(8) will skip over the remainder of a byte.
     *
     * @param width The width of the pad field in bits.
     * @return true if all of the padding bits were zero.
     * @throws JSONException
     */
    public boolean pad(int width) throws JSONException {
        try {
            return this.bitreader.pad(width);
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    /**
     * Read an integer, specifying its width in bits.
     *
     * @param width
     *            0 to 32.
     * @return An unsigned integer.
     * @throws JSONException
     */
    private int read(int width) throws JSONException {
        try {
            int value = this.bitreader.read(width);
            if (probe) {
                log(value, width);
            }
            return value;
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    /**
     * Read Huffman encoded characters into a keep.
     * @param huff A Huffman decoder.
     * @param ext A Huffman decoder for the extended bytes.
     * @param keep The keep that will receive the kim.
     * @return The string that was read.
     * @throws JSONException
     */
    private String read(Huff huff, Huff ext, Keep keep) throws JSONException {
        Kim kim;
        int at = 0;
        int allocation = 256;
        byte[] bytes = new byte[allocation];
        if (bit()) {
            return getAndTick(keep, this.bitreader).toString();
        }
        while (true) {
            if (at >= allocation) {
                allocation *= 2;
                bytes = java.util.Arrays.copyOf(bytes, allocation);
            }
            int c = huff.read(this.bitreader);
            if (c == end) {
                break;
            }
            while ((c & 128) == 128) {
                bytes[at] = (byte) c;
                at += 1;
                c = ext.read(this.bitreader);
            }
            bytes[at] = (byte) c;
            at += 1;
        }
        if (at == 0) {
            return "";
        }
        kim = new Kim(bytes, at);
        keep.register(kim);
        return kim.toString();
    }

    /**
     * Read a JSONArray.
     *
     * @param stringy
     *            true if the first element is a string.
     * @throws JSONException
     */
    private JSONArray readArray(boolean stringy) throws JSONException {
        JSONArray jsonarray = new JSONArray();
        jsonarray.put(stringy
                ? read(this.stringhuff, this.stringhuffext, this.stringkeep)
                : readValue());
        while (true) {
            if (probe) {
                log();
            }
            if (!bit()) {
                if (!bit()) {
                    return jsonarray;
                }
                jsonarray.put(stringy
                        ? readValue()
                        : read(this.stringhuff, this.stringhuffext,
                                this.stringkeep));
            } else {
                jsonarray.put(stringy
                        ? read(this.stringhuff, this.stringhuffext,
                                this.stringkeep)
                        : readValue());
            }
        }
    }

    /**
     * Read a JSON value. The type of value is determined by the next 3 bits.
     *
     * @return The read value.
     * @throws JSONException
     */
    private Object readJSON() throws JSONException {
        switch (read(3)) {
        case zipObject:
            return readObject();
        case zipArrayString:
            return readArray(true);
        case zipArrayValue:
            return readArray(false);
        case zipEmptyObject:
            return new JSONObject();
        case zipEmptyArray:
            return new JSONArray();
        case zipTrue:
            return Boolean.TRUE;
        case zipFalse:
            return Boolean.FALSE;
        default:
            return JSONObject.NULL;
        }
    }

    private JSONObject readObject() throws JSONException {
        JSONObject jsonobject = new JSONObject();
        while (true) {
            if (probe) {
                log();
            }
            String name = read(this.namehuff, this.namehuffext, this.namekeep);
            if (jsonobject.opt(name) != null) {
                throw new JSONException("Duplicate key.");
            }
            jsonobject.put(name, !bit()
                    ? read(this.stringhuff, this.stringhuffext, this.stringkeep)
                    : readValue());
            if (!bit()) {
                return jsonobject;
            }
        }
    }

    private Object readValue() throws JSONException {
        switch (read(2)) {
        case 0:
            int nr_bits = !bit() ? 4 : !bit() ? 7 : 14;
            int integer = read(nr_bits);
            switch (nr_bits) {
            case 7:
                integer += int4;
                break;
            case 14:
                integer += int7;
                break;
            }
            return integer;
        case 1:
            byte[] bytes = new byte[256];
            int length = 0;
            while (true) {
                int c = read(4);
                if (c == endOfNumber) {
                    break;
                }
                bytes[length] = bcd[c];
                length += 1;
            }
            Object value;
            try {
                value = JSONObject.stringToValue(new String(bytes, 0, length,
                        "US-ASCII"));
            } catch (java.io.UnsupportedEncodingException e) {
                throw new JSONException(e);
            }
            this.valuekeep.register(value);
            return value;
        case 2:
            return getAndTick(this.valuekeep, this.bitreader);
        case 3:
            return readJSON();
        default:
            throw new JSONException("Impossible.");
        }
    }

    public Object decode() throws JSONException {
        generate();
        return readJSON();
    }
}
