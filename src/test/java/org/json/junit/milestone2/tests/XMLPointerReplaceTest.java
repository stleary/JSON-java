package org.json.junit.milestone2.tests;

import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.XML;
import org.junit.Test;
import static org.junit.Assert.*;


import java.io.StringReader;

public class XMLPointerReplaceTest {

    @Test
    public void testReplaceSubObject_success() {
        String xml = "<book><title><content>Old Title</content></title><author>John</author></book>";
        StringReader reader = new StringReader(xml);

        JSONObject replacement = new JSONObject().put("content", "New Title");
        JSONPointer pointer = new JSONPointer("/book/title");

        JSONObject result = XML.toJSONObject(reader, pointer, replacement);

        assertEquals("New Title", result.getJSONObject("book").getJSONObject("title").get("content"));
        assertEquals("John", result.getJSONObject("book").get("author"));
    }





    @Test(expected = RuntimeException.class)
    public void testReplaceSubObject_invalidPath() {
        String xml = "<book><title>Old Title</title></book>";
        StringReader reader = new StringReader(xml);

        JSONObject replacement = new JSONObject().put("name", "Unknown");
        JSONPointer pointer = new JSONPointer("/abcde/fghijk");

        XML.toJSONObject(reader, pointer, replacement);
    }

    @Test(expected = RuntimeException.class)
    public void testReplaceSubObject_nullReader() {
        JSONObject replacement = new JSONObject().put("title", "Anything");
        JSONPointer pointer = new JSONPointer("/book/title");

        XML.toJSONObject(null, pointer, replacement);
    }
}


