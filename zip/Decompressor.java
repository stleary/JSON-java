package org.json.zip;

import java.io.UnsupportedEncodingException;

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
 * JSONzip is a compression scheme for JSON text.
 *
 * @author JSON.org
 * @version 2013-04-18
 */

public class Decompressor extends JSONzip {

    /**
     * A decompressor reads bits from a BitReader.
     */
    BitReader bitreader;

    /**
     * Create a new compressor. It may be used for an entire session or
     * subsession.
     *
     * @param bitreader
     *            The bitreader that this decompressor will read from.
     */
    public Decompressor(BitReader bitreader) {
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
     * @param keep
     * @param bitreader
     * @return
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
     * @param factor
     * @return true if all of the padding bits were zero.
     * @throws JSONException
     */
    public boolean pad(int factor) throws JSONException {
        try {
            return this.bitreader.pad(factor);
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
     * Read a JSONArray.
     *
     * @param stringy
     *            true if the first element is a string.
     * @return
     * @throws JSONException
     */
    private JSONArray readArray(boolean stringy) throws JSONException {
        JSONArray jsonarray = new JSONArray();
        jsonarray.put(stringy ? readString() : readValue());
        while (true) {
            if (probe) {
                log("\n");
            }
            if (!bit()) {
                if (!bit()) {
                    return jsonarray;
                }
                jsonarray.put(stringy ? readValue() : readString());
            } else {
                jsonarray.put(stringy ? readString() : readValue());
            }
        }
    }

    /**
     * Read a JSON value. The type of value is determined by the next 3 bits.
     *
     * @return
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

    private String readName() throws JSONException {
        byte[] bytes = new byte[65536];
        int length = 0;
        if (!bit()) {
            while (true) {
                int c = this.namehuff.read(this.bitreader);
                if (c == end) {
                    break;
                }
                bytes[length] = (byte) c;
                length += 1;
            }
            if (length == 0) {
                return "";
            }
            Kim kim = new Kim(bytes, length);
            this.namekeep.register(kim);
            return kim.toString();
        }
        return getAndTick(this.namekeep, this.bitreader).toString();
    }

    private JSONObject readObject() throws JSONException {
        JSONObject jsonobject = new JSONObject();
        while (true) {
            if (probe) {
                log("\n");
            }
            String name = readName();
            jsonobject.put(name, !bit() ? readString() : readValue());
            if (!bit()) {
                return jsonobject;
            }
        }
    }

    private String readString() throws JSONException {
        Kim kim;
        int from = 0;
        int thru = 0;
        int previousFrom = none;
        int previousThru = 0;
        if (bit()) {
            return getAndTick(this.stringkeep, this.bitreader).toString();
        }
        byte[] bytes = new byte[65536];
        boolean one = bit();
        this.substringkeep.reserve();
        while (true) {
            if (one) {
                from = thru;
                kim = (Kim) getAndTick(this.substringkeep, this.bitreader);
                thru = kim.copy(bytes, from);
                if (previousFrom != none) {
                    this.substringkeep.registerOne(new Kim(bytes, previousFrom,
                            previousThru + 1));
                }
                previousFrom = from;
                previousThru = thru;
                one = bit();
            } else {
                from = none;
                while (true) {
                    int c = this.substringhuff.read(this.bitreader);
                    if (c == end) {
                        break;
                    }
                    bytes[thru] = (byte) c;
                    thru += 1;
                    if (previousFrom != none) {
                        this.substringkeep.registerOne(new Kim(bytes,
                                previousFrom, previousThru + 1));
                        previousFrom = none;
                    }
                }
                if (!bit()) {
                    break;
                }
                one = true;
            }
        }
        if (thru == 0) {
            return "";
        }
        kim = new Kim(bytes, thru);
        this.stringkeep.register(kim);
        this.substringkeep.registerMany(kim);
        return kim.toString();
    }

    private Object readValue() throws JSONException {
        switch (read(2)) {
        case 0:
            return new Integer(read(!bit() ? 4 : !bit() ? 7 : 14));
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
            } catch (UnsupportedEncodingException e) {
                throw new JSONException(e);
            }
            this.values.register(value);
            return value;
        case 2:
            return getAndTick(this.values, this.bitreader);
        case 3:
            return readJSON();
        default:
            throw new JSONException("Impossible.");
        }
    }

    public Object unzip() throws JSONException {
        begin();
        return readJSON();
    }
}
