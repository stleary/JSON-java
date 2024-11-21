package org.json.junit;

import org.json.CharSequenceReader;
import org.junit.Test;

import static org.junit.Assert.*;

public class CharSequenceReaderTest {

    @Test
    public void testReadSingleCharacter() {
        CharSequenceReader reader = new CharSequenceReader("test");
        assertEquals('t', reader.read());
        assertEquals('e', reader.read());
        assertEquals('s', reader.read());
        assertEquals('t', reader.read());
        assertEquals(-1, reader.read());
        assertEquals(-1, reader.read());
    }

    @Test
    public void testReadToCharBuffer() {
        CharSequenceReader reader = new CharSequenceReader("test");
        char[] buffer = new char[10];
        int read = reader.read(buffer, 2, 3);
        assertEquals(3, read);
        assertArrayEquals(new char[]{0, 0, 't', 'e', 's', 0, 0, 0, 0, 0}, buffer);
    }

    @Test
    public void testReadPastEnd() {
        CharSequenceReader reader = new CharSequenceReader("test");
        char[] buffer = new char[5];
        assertEquals(4, reader.read(buffer, 0, 5));
        assertEquals(-1, reader.read(buffer, 0, 5));
    }

    @Test
    public void testMarkAndReset() {
        CharSequenceReader reader = new CharSequenceReader("test");
        assertEquals('t', reader.read());
        reader.mark(0);
        assertEquals('e', reader.read());
        assertEquals('s', reader.read());
        reader.reset();
        assertEquals('e', reader.read());
    }

    @Test(expected = NullPointerException.class)
    public void testReadToNullBuffer() {
        CharSequenceReader reader = new CharSequenceReader("test");
        reader.read(null, 0, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testNegativeOffset() {
        CharSequenceReader reader = new CharSequenceReader("test");
        char[] buffer = new char[10];
        reader.read(buffer, -1, 5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testNegativeLength() {
        CharSequenceReader reader = new CharSequenceReader("test");
        char[] buffer = new char[10];
        reader.read(buffer, 0, -5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testOffsetPlusLengthExceedsBuffer() {
        CharSequenceReader reader = new CharSequenceReader("test");
        char[] buffer = new char[5];
        reader.read(buffer, 3, 3);
    }

    @Test
    public void testCloseDoesNothing() {
        CharSequenceReader reader = new CharSequenceReader("test");
        reader.close();
        assertEquals('t', reader.read());
    }

    @Test
    public void testMarkSupported() {
        CharSequenceReader reader = new CharSequenceReader("test");
        assertTrue(reader.markSupported());
    }
}