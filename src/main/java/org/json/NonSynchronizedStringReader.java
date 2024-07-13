package org.json;
/*
Public Domain.
*/

import java.io.IOException;
import java.io.Reader;

/**
 * a not synchronized and therefore not thread-safe implementation of {@link java.io.StringReader} for faster read operations.
 */
public class NonSynchronizedStringReader extends Reader {

    private final int length;
    private String source;
    private int nextIndex = 0;
    private int markIndex = 0;

    /**
     * Creates a new NonSynchronizedStringReader.
     *
     * @param input the String to read.
     */
    public NonSynchronizedStringReader(String input) {
        this.source = input;
        this.length = input.length();
    }

    /**
     * Reads the next character.
     *
     * @return The next character read, or -1 if the end of the stream has been reached.
     *
     * @throws IOException If the reader is closed
     */
    @Override
    public int read() throws IOException {
        throwIfClosed();
        if (nextIndex >= length) {
            return -1;
        }
        return source.charAt(nextIndex++);
    }

    private void throwIfClosed() throws IOException {
        if (source == null) {
            throw new IOException("the Stream was closed");
        }
    }

    /**
     * Reads characters into a portion of an array.
     *
     * <p> If {@code length} is zero, then no characters are read and {@code 0} is returned.
     * If no character is available, because the stream is at its end, {@code -1} is returned.
     * Otherwise, at least one character is read and stored into {@code charBuffer}.
     *
     * @param  charBuffer {@inheritDoc}
     * @param  offset {@inheritDoc}
     * @param  length {@inheritDoc}
     *
     * @return {@inheritDoc}
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public int read(char[] charBuffer, int offset, int length) throws IOException {
        throwIfClosed();
        if ((offset < 0) || (offset > charBuffer.length) || (length < 0) ||
                ((offset + length) > charBuffer.length) || ((offset + length) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (length == 0) {
            return 0;
        }
        if (nextIndex >= this.length) {
            return -1;
        }
        int numberToRead = Math.min(this.length - nextIndex, length);
        source.getChars(nextIndex, nextIndex + numberToRead, charBuffer, offset);
        nextIndex += numberToRead;
        return numberToRead;
    }

    /**
     * Tells whether this stream supports the {@link #mark(int)} operation, which it does.
     * @return {@code true}
     */
    @Override
    public boolean markSupported() {
        return true;
    }

    /**
     * Marks the present position in the stream.
     * Subsequent calls to {@link #reset()} will reposition the stream to this point.
     *
     * @param readAheadLimit the input is ignored, but must not be negative, otherwise an exception is thrown.
     *
     * @throws IllegalArgumentException  If {@code readAheadLimit < 0}
     * @throws IOException  If an I/O error occurs
     */
    @Override
    public void mark(int readAheadLimit) throws IOException {
        if (readAheadLimit < 0) {
            throw new IllegalArgumentException("Read-ahead limit cannot be < 0 but was " + readAheadLimit);
        }
        throwIfClosed();
        markIndex = nextIndex;
    }

    /**
     * Resets the stream to the most recent mark, or to the beginning of the string if it has never been marked.
     *
     * @throws IOException If the reader is closed
     */
    public void reset() throws IOException {
        throwIfClosed();
        nextIndex = markIndex;
    }

    /**
     * Closes the stream and releases any system resources associated with it.
     * Once the stream has been closed, further {@link #read()}, mark(), or reset() invocations will throw an IOException.
     * Closing a previously closed stream has no effect.
     */
    public void close() {
        source = null;
    }
}
