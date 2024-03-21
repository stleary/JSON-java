package org.json.junit;

import static org.junit.Assert.assertEquals;

import org.json.StringBuilderWriter;
import org.junit.Before;
import org.junit.Test;

public class StringBuilderWriterTest {
    private StringBuilderWriter writer;

    @Before
    public void setUp() {
        writer = new StringBuilderWriter();
    }

    @Test
    public void testWriteChar() {
        writer.write('a');
        assertEquals("a", writer.toString());
    }

    @Test
    public void testWriteCharArray() {
        char[] chars = {'a', 'b', 'c'};
        writer.write(chars, 0, 3);
        assertEquals("abc", writer.toString());
    }

    @Test
    public void testWriteString() {
        writer.write("hello");
        assertEquals("hello", writer.toString());
    }

    @Test
    public void testWriteStringWithOffsetAndLength() {
        writer.write("hello world", 6, 5);
        assertEquals("world", writer.toString());
    }

    @Test
    public void testAppendCharSequence() {
        writer.append("hello");
        assertEquals("hello", writer.toString());
    }

    @Test
    public void testAppendCharSequenceWithStartAndEnd() {
        CharSequence csq = "hello world";
        writer.append(csq, 6, 11);
        assertEquals("world", writer.toString());
    }

    @Test
    public void testAppendChar() {
        writer.append('a');
        assertEquals("a", writer.toString());
    }
}