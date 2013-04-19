package org.json.zip;

import java.io.IOException;

/**
 * A bitwriter is a an interface that allows for doing output at the bit level.
 * Most IO interfaces only allow for writing at the byte level or higher.
 */
public interface BitWriter {
    /**
     * Returns the number of bits that have been written to this bitwriter.
     */
    public long nrBits();

    /**
     * Write a 1 bit.
     *
     * @throws IOException
     */
    public void one() throws IOException;

    /**
     * Pad the rest of the block with zeros and flush.
     *
     * @param factor
     *            The size in bits of the block to pad. This will typically be
     *            8, 16, 32, 64, 128, 256, etc.
     * @return true if the block was zero padded, or false if the the padding
     *         contains any one bits.
     * @throws IOException
     */
    public void pad(int factor) throws IOException;

    /**
     * Write some bits. Up to 32 bits can be written at a time.
     *
     * @param bits
     *            The bits to be written.
     * @param width
     *            The number of bits to write. (0..32)
     * @throws IOException
     */
    public void write(int bits, int width) throws IOException;

    /**
     * Write a 0 bit.
     *
     * @throws IOException
     */
    public void zero() throws IOException;
}
