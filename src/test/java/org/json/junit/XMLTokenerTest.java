package org.json.junit;

import org.json.XMLTokener;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.*;

/**
 * Tests for JSON-Java XMLTokener.java
 */
public class XMLTokenerTest {

    /**
     * Tests that nextCDATA() correctly extracts content from within a CDATA section.
     */
    @Test
    public void testNextCDATA() {
        String xml = "This is <![CDATA[ some <CDATA> content ]]> after";
        XMLTokener tokener = new XMLTokener(new StringReader(xml));
        tokener.skipPast("<![CDATA[");
        String cdata = tokener.nextCDATA();
        assertEquals(" some <CDATA> content ", cdata);
    }

    /**
     * Tests that nextContent() returns plain text content before a tag.
     */
    @Test
    public void testNextContentWithText() {
        String xml = "Some content<nextTag>";
        XMLTokener tokener = new XMLTokener(xml);
        Object content = tokener.nextContent();
        assertEquals("Some content", content);
    }

    /**
     * Tests that nextContent() returns '<' character when starting with a tag.
     */
    @Test
    public void testNextContentWithTag() {
        String xml = "<tag>";
        XMLTokener tokener = new XMLTokener(xml);
        Object content = tokener.nextContent();
        assertEquals('<', content);
    }

    /**
     * Tests that nextEntity() resolves a known entity like &amp; correctly.
     */
    @Test
    public void testNextEntityKnown() {
        XMLTokener tokener = new XMLTokener("amp;");
        Object result = tokener.nextEntity('&');
        assertEquals("&", result);
    }

    /**
     * Tests that nextEntity() preserves unknown entities by returning them unchanged.
     */
    @Test
    public void testNextEntityUnknown() {
        XMLTokener tokener = new XMLTokener("unknown;");
        tokener.next(); // skip 'u'
        Object result = tokener.nextEntity('&');
        assertEquals("&nknown;", result);  // malformed start to simulate unknown
    }

    /**
     * Tests skipPast() to ensure the cursor moves past the specified string.
     */
    @Test
    public void testSkipPast() {
        String xml = "Ignore this... endHere more text";
        XMLTokener tokener = new XMLTokener(xml);
        tokener.skipPast("endHere");
        assertEquals(' ', tokener.next()); // should be the space after "endHere"
    }

}