package org.json;

import java.io.Reader;

/**
 * a Reader to read from a {@link CharSequence} e.g. a {@link String}.
 */
public class CharSequenceReader extends Reader {

    private final CharSequence charSequence;
    private int index;
    private int markedIndex;

    /**
     * creates a new CharSequenceReader to read from the given CharSequence
     * @param charSequence the input to read.
     */
    public CharSequenceReader(CharSequence charSequence) {
        this.charSequence = charSequence;
    }

    /**
     * reads the next character from the input.
     * @return returns the read character or -1 if the end of the input is reached.
     */
    @Override
    public int read() {
        if (index < charSequence.length()) {
            return charSequence.charAt(index++);
        }
        return -1;
    }


    /**
     * reads a maximum number of characters from the input into the charBuffer starting at the offset
     * and returns the number of actual read characters.
     *
     * @param charBuffer the target charBuffer to write the read characters into
     * @param offset the offset index where to start writing into the charBuffer
     * @param length the maximum number of characters to read
     * @return the number of characters read into the charBuffer or -1 if the end of the input is reached.
     */
    @Override
    public int read(char[] charBuffer, int offset, int length) {
        if (charBuffer == null) {
            throw new NullPointerException("The target charBuffer cannot be null");
        } else if (offset < 0) {
            throw new IndexOutOfBoundsException("Offset must not be negative");
        } else if (length < 0) {
            throw new IndexOutOfBoundsException("Length must not be negative");
        } else if (offset + length > charBuffer.length) {
            throw new IndexOutOfBoundsException("Offset + length must not be larger than the charBuffer length");
        } else if (index >= charSequence.length()) {
            return -1;
        }

        int charsToRead = Math.min(length, charSequence.length() - index);
        for (int i = 0; i < charsToRead; i++) {
            charBuffer[offset + i] = charSequence.charAt(index++);
        }

        return charsToRead;
    }

    /**
     * returns true to indicate that mark is supported.
     * @return true
     */
    @Override
    public boolean markSupported() {
        return true;
    }

    /**
     * marks the reader on the current position to later jump back to it by using {@link #reset()}.
     * @param readAheadLimit will be ignored. This reader has no readAheadLimit.
     */
    @Override
    public void mark(int readAheadLimit) {
        markedIndex = this.index;
    }

    /**
     * resets the reader to the previous marked position used by {@link #mark(int)}.
     */
    @Override
    public void reset() {
        index = markedIndex;
    }


    /**
     * does nothing.
     */
    @Override
    public void close() {

    }

}
