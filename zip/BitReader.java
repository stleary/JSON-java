package org.json.zip;

import java.io.IOException;

public interface BitReader {
    /**
     * Read one bit.
     *
     * @return true if it is a 1 bit.
     */
    public boolean bit() throws IOException;

    /**
     * Returns the number of bits that have been read from this bitreader.
     *
     * @return The number of bits read so far.
     */
    public long nrBits();

    /**
     * Check that the rest of the block has been padded with zeroes.
     *
     * @param factor
     *            The size in bits of the block to pad. This will typically be
     *            8, 16, 32, 64, 128, 256, etc.
     * @return true if the block was zero padded, or false if the the padding
     *         contained any one bits.
     * @throws IOException
     */
    public boolean pad(int factor) throws IOException;

    /**
     * Read some bits.
     *
     * @param width
     *            The number of bits to read. (0..32)
     * @throws IOException
     * @return the bits
     */
    public int read(int width) throws IOException;
}
