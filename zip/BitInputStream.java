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
 * @version 2013-05-03
 *
 */
public class BitInputStream implements BitReader {
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
     * @param width
     *            The size of the block to pad in bits.
     *            This will typically be 8, 16, 32, 64, 128, 256, etc.
     * @return true if the block was zero padded, or false if the the padding
     *         contains any one bits.
     * @throws IOException
     */
    public boolean pad(int width) throws IOException {
        boolean result = true;
        int gap = (int)this.nrBits % width;
        if (gap < 0) {
            gap += width;
        }
        if (gap != 0) {
            int padding = width - gap;
            while (padding > 0) {
                if (bit()) {
                    result = false;
                }
                padding -= 1;
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
            result |= ((this.unread >>> (this.available - take)) &
                    ((1 << take) - 1)) << (width - take);
            this.nrBits += take;
            this.available -= take;
            width -= take;
        }
        return result;
    }
}
