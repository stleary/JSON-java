package org.json;

import java.io.IOException;
import java.io.Writer;

/**
 * Performance optimised alternative for {@link java.io.StringWriter}
 * using internally a {@link StringBuilder} instead of a {@link StringBuffer}.
 */
public class StringBuilderWriter extends Writer {
    private final StringBuilder builder;

    /**
     * Create a new string builder writer using the default initial string-builder buffer size.
     */
    public StringBuilderWriter() {
        builder = new StringBuilder();
        lock = builder;
    }

    /**
     * Create a new string builder writer using the specified initial string-builder buffer size.
     *
     * @param initialSize The number of {@code char} values that will fit into this buffer
     *                    before it is automatically expanded
     *
     * @throws IllegalArgumentException If {@code initialSize} is negative
     */
    public StringBuilderWriter(int initialSize) {
        builder = new StringBuilder(initialSize);
        lock = builder;
    }

    @Override
    public void write(int c) {
        builder.append((char) c);
    }

    @Override
    public void write(char[] cbuf, int offset, int length) {
        if ((offset < 0) || (offset > cbuf.length) || (length < 0) ||
                ((offset + length) > cbuf.length) || ((offset + length) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (length == 0) {
            return;
        }
        builder.append(cbuf, offset, length);
    }

    @Override
    public void write(String str) {
        builder.append(str);
    }

    @Override
    public void write(String str, int offset, int length) {
        builder.append(str, offset, offset + length);
    }

    @Override
    public StringBuilderWriter append(CharSequence csq) {
        write(String.valueOf(csq));
        return this;
    }

    @Override
    public StringBuilderWriter append(CharSequence csq, int start, int end) {
        if (csq == null) {
            csq = "null";
        }
        return append(csq.subSequence(start, end));
    }

    @Override
    public StringBuilderWriter append(char c) {
        write(c);
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws IOException {
    }
}
