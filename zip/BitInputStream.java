package org.json.zip;

import java.io.IOException;
import java.io.InputStream;

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
 * This is a big endian bit reader. It reads its bits from an InputStream.
 *
 * @version 2013-04-18
 *
 */
public class BitInputStream implements BitReader {
    /**
     * 2^n - 1
     */
    static final int[] mask = { 0, 1, 3, 7, 15, 31, 63, 127, 255 };

    /**
     * The number of bits remaining in the current byte.
     */
    private int available = 0;

    /**
     * Up to a byte's worth of unread bits.
     */
    private int unread = 0;

    /**
     * The source of the bits.
     */
    private InputStream in;

    /**
     * The number of bits read so far. This is used in padding.
     */
    private long nrBits = 0;

    /**
     * Make a BitReader from an InputStream. The BitReader will take bytes from
     * the InputStream and unpack them into bits.
     *
     * @param in
     *            An InputStream.
     */
    public BitInputStream(InputStream in) {
        this.in = in;
    }

    /**
     * Make a BitReader. The first byte is passed in explicitly, the remaining
     * bytes are obtained from the InputStream. This makes it possible to look
     * at the first byte of a stream before deciding that it should be read as
     * bits.
     *
     * @param in
     *            An InputStream
     * @param firstByte
     *            The first byte, which was probably read from in.
     */
    public BitInputStream(InputStream in, int firstByte) {
        this.in = in;
        this.unread = firstByte;
        this.available = 8;
    }

    /**
     * Read one bit.
     *
     * @return true if it is a 1 bit.
     */
    public boolean bit() throws IOException {
        return read(1) != 0;
    }

    /**
     * Get the number of bits that have been read from this BitInputStream.
     * This includes pad bits that have been skipped, but might not include
     * bytes that have been read from the underlying InputStream that have not
     * yet been delivered as bits.
     *
     * @return The number of bits read so far.
     */
    public long nrBits() {
        return this.nrBits;
    }

    /**
     * Check that the rest of the block has been padded with zeroes.
     *
     * @param factor
     *            The size of the block to pad. This will typically be 8, 16,
     *            32, 64, 128, 256, etc.
     * @return true if the block was zero padded, or false if the the padding
     *         contains any one bits.
     * @throws IOException
     */
    public boolean pad(int factor) throws IOException {
        int padding = factor - (int) (this.nrBits % factor);
        boolean result = true;

        for (int i = 0; i < padding; i += 1) {
            if (bit()) {
                result = false;
            }
        }
        return result;
    }

    /**
     * Read some bits.
     *
     * @param width
     *            The number of bits to read. (0..32)
     * @throws IOException
     * @return the bits
     */
    public int read(int width) throws IOException {
        if (width == 0) {
            return 0;
        }
        if (width < 0 || width > 32) {
            throw new IOException("Bad read width.");
        }
        int result = 0;
        while (width > 0) {
            if (this.available == 0) {
                this.unread = this.in.read();
                if (this.unread < 0) {
                    throw new IOException("Attempt to read past end.");
                }
                this.available = 8;
            }
            int take = width;
            if (take > this.available) {
                take = this.available;
            }
            result |= ((this.unread >>> (this.available - take)) & mask[take])
                    << (width - take);
            this.nrBits += take;
            this.available -= take;
            width -= take;
        }
        return result;
    }
}
