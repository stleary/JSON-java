package org.json.zip;

import java.io.IOException;
import java.io.OutputStream;

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
 * This is a big endian bit writer. It writes its bits to an OutputStream.
 *
 * @version 2013-05-03
 *
 */
public class BitOutputStream implements BitWriter {

    /**
     * The number of bits written.
     */
    private long nrBits = 0;

    /**
     * The destination of the bits.
     */
    private OutputStream out;

    /**
     * Holder of bits not yet written.
     */
    private int unwritten;

    /**
     * The number of unused bits in this.unwritten.
     */
    private int vacant = 8;

    /**
     * Use an OutputStream to produce a BitWriter. The BitWriter will send its
     * bits to the OutputStream as each byte is filled.
     *
     * @param out
     *            An Output Stream
     */
    public BitOutputStream(OutputStream out) {
        this.out = out;
    }

    /**
     * Returns the number of bits that have been written to this
     * bitOutputStream. This may include bits that have not yet been written
     * to the underlying outputStream.
     */
    public long nrBits() {
        return this.nrBits;
    }

    /**
     * Write a 1 bit.
     *
     * @throws IOException
     */
    public void one() throws IOException {
        write(1, 1);
    }

    /**
     * Pad the rest of the block with zeros and flush. pad(8) flushes the last
     * unfinished byte. The underlying OutputStream will be flushed.
     *
     * @param width
     *            The size of the block to pad in bits.
     *            This will typically be 8, 16, 32, 64, 128, 256, etc.
     * @throws IOException
     */
    public void pad(int width) throws IOException {
        int gap = (int)this.nrBits % width;
        if (gap < 0) {
            gap += width;
        }
        if (gap != 0) {
            int padding = width - gap;
            while (padding > 0) {
                this.zero();
                padding -= 1;
            }
        }
        this.out.flush();
    }

    /**
     * Write some bits. Up to 32 bits can be written at a time.
     *
     * @param bits
     *            The bits to be written.
     * @param width
     *            The number of bits to write. (0..32)
     * @throws IOException
     */
    public void write(int bits, int width) throws IOException {
        if (bits == 0 && width == 0) {
            return;
        }
        if (width <= 0 || width > 32) {
            throw new IOException("Bad write width.");
        }
        while (width > 0) {
            int actual = width;
            if (actual > this.vacant) {
                actual = this.vacant;
            }
            this.unwritten |= ((bits >>> (width - actual)) &
                    ((1 << actual) - 1)) << (this.vacant - actual);
            width -= actual;
            nrBits += actual;
            this.vacant -= actual;
            if (this.vacant == 0) {
                this.out.write(this.unwritten);
                this.unwritten = 0;
                this.vacant = 8;
            }
        }
    }

    /**
     * Write a 0 bit.
     *
     * @throws IOException
     */
    public void zero() throws IOException {
        write(0, 1);

    }
}
