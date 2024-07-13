package org.json.junit;

import org.json.NonSynchronizedStringReader;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class NonSynchronizedStringReaderTest {

    @Test
    public void testReadSingleCharacter() throws IOException {
        NonSynchronizedStringReader reader = new NonSynchronizedStringReader("Hello, world!");
        assertEquals('H', reader.read());
        assertEquals('e', reader.read());
    }

    @Test
    public void testReadUntilEnd() throws IOException {
        NonSynchronizedStringReader reader = new NonSynchronizedStringReader("Hello, world!");
        for (int i = 0; i < 13; i++) {
            reader.read();
        }
        assertEquals(-1, reader.read());
    }

    @Test
    public void testReadIntoCharArray() throws IOException {
        NonSynchronizedStringReader reader = new NonSynchronizedStringReader("Hello, world!");
        char[] buffer = new char[5];
        int numRead = reader.read(buffer, 0, buffer.length);
        assertEquals(5, numRead);
        assertArrayEquals(new char[] {'H', 'e', 'l', 'l', 'o'}, buffer);
    }

    @Test
    public void testReadIntoCharArrayWithOffset() throws IOException {
        NonSynchronizedStringReader reader = new NonSynchronizedStringReader("Hello, world!");
        char[] buffer = new char[7];
        int numRead = reader.read(buffer, 2, 5);
        assertEquals(5, numRead);
        assertArrayEquals(new char[] {0, 0, 'H', 'e', 'l', 'l', 'o'}, buffer);
    }

    @Test
    public void testReadIntoCharArrayExceedingLength() throws IOException {
        NonSynchronizedStringReader reader = new NonSynchronizedStringReader("Hello, world!");
        char[] buffer = new char[20];
        int numRead = reader.read(buffer, 0, buffer.length);
        assertEquals(13, numRead);
    }

    @Test
    public void testMarkAndReset() throws IOException {
        NonSynchronizedStringReader reader = new NonSynchronizedStringReader("Hello, world!");
        reader.read();
        reader.mark(0);
        reader.read();
        reader.reset();
        assertEquals('e', reader.read());
    }

    @Test
    public void testMarkSupported() {
        NonSynchronizedStringReader reader = new NonSynchronizedStringReader("Hello, world!");
        assertTrue(reader.markSupported());
    }

    @Test
    public void testReadAfterClose() {
        NonSynchronizedStringReader reader = new NonSynchronizedStringReader("Hello, world!");
        reader.close();
        assertThrows(IOException.class, () -> reader.read());
    }

    @Test
    public void testMarkWithNegativeReadAheadLimit() {
        NonSynchronizedStringReader reader = new NonSynchronizedStringReader("Hello, world!");
        assertThrows(IllegalArgumentException.class, () -> reader.mark(-1));
    }

    @Test
    public void testReadWithInvalidParameters() {
        NonSynchronizedStringReader reader = new NonSynchronizedStringReader("Hello, world!");
        char[] buffer = new char[10];
        assertThrows(IndexOutOfBoundsException.class, () -> reader.read(buffer, -1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> reader.read(buffer, 0, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> reader.read(buffer, 10, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> reader.read(buffer, 5, 10));
    }

    @Test
    public void testReadLengthZero() throws IOException {
        NonSynchronizedStringReader reader = new NonSynchronizedStringReader("Hello, world!");
        char[] buffer = new char[10];
        int read = reader.read(buffer, 0, 0);
        assertEquals(0, read);
        assertArrayEquals(new char[10], buffer);
    }

    @Test
    public void testReadWhenNextIndexExceedsLength() throws IOException {
        NonSynchronizedStringReader reader = new NonSynchronizedStringReader("Hello, world!");
        char[] buffer = new char[20];
        int numRead = reader.read(buffer, 0, buffer.length);
        assertEquals(13, numRead);

        // Try to read again, should return -1 indicating end of stream
        numRead = reader.read(buffer, 0, buffer.length);
        assertEquals(-1, numRead);

        // Read single character, should also return -1 indicating end of stream
        assertEquals(-1, reader.read());
    }
}
