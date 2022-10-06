package org.json.junit;

/*
Public Domain.
*/

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;
import org.json.XMLParserConfiguration;
import org.json.XMLXsiTypeConverter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for JSON-Java XML.java
 * Note: noSpace() will be tested by JSONMLTest
 */
public class XMLTest {
    /**
     * JUnit supports temporary files and folders that are cleaned up after the test.
     * https://garygregory.wordpress.com/2010/01/20/junit-tip-use-rules-to-manage-temporary-files-and-folders/ 
     */
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    
    /**
     * JSONObject from a null XML string.
     * Expects a NullPointerException
     */
    @Test(expected=NullPointerException.class)
    public void shouldHandleNullXML() {
        String xmlStr = null;
        JSONObject jsonObject = XML.toJSONObject(xmlStr);
        assertTrue("jsonObject should be empty", jsonObject.isEmpty());
    }

    /**
     * Empty JSONObject from an empty XML string.
     */
    @Test
    public void shouldHandleEmptyXML() {

        String xmlStr = "";
        JSONObject jsonObject = XML.toJSONObject(xmlStr);
        assertTrue("jsonObject should be empty", jsonObject.isEmpty());
    }

    /**
     * Empty JSONObject from a non-XML string.
     */
    @Test
    public void shouldHandleNonXML() {
        String xmlStr = "{ \"this is\": \"not xml\"}";
        JSONObject jsonObject = XML.toJSONObject(xmlStr);
        assertTrue("xml string should be empty", jsonObject.isEmpty());
    }

    /**
     * Invalid XML string (tag contains a frontslash).
     * Expects a JSONException
     */
    @Test
    public void shouldHandleInvalidSlashInTag() {
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "    <address>\n"+
            "       <name/x>\n"+
            "       <street>abc street</street>\n"+
            "   </address>\n"+
            "</addresses>";
        try {
            XML.toJSONObject(xmlStr);
            fail("Expecting a JSONException");
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "Misshaped tag at 176 [character 14 line 4]",
                    e.getMessage());
        }
    }

    /**
     * Invalid XML string ('!' char in tag)
     * Expects a JSONException
     */
    @Test
    public void shouldHandleInvalidBangInTag() {
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "    <address>\n"+
            "       <name/>\n"+
            "       <!>\n"+
            "   </address>\n"+
            "</addresses>";
        try {
            XML.toJSONObject(xmlStr);
            fail("Expecting a JSONException");
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "Misshaped meta tag at 214 [character 12 line 7]",
                    e.getMessage());
        }
    }

    /**
     * Invalid XML string ('!' char and no closing tag brace)
     * Expects a JSONException
     */
    @Test
    public void shouldHandleInvalidBangNoCloseInTag() {
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "    <address>\n"+
            "       <name/>\n"+
            "       <!\n"+
            "   </address>\n"+
            "</addresses>";
        try {
            XML.toJSONObject(xmlStr);
            fail("Expecting a JSONException");
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "Misshaped meta tag at 213 [character 12 line 7]",
                    e.getMessage());
        }
    }

    /**
     * Invalid XML string (no end brace for tag)
     * Expects JSONException
     */
    @Test
    public void shouldHandleNoCloseStartTag() {
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "    <address>\n"+
            "       <name/>\n"+
            "       <abc\n"+
            "   </address>\n"+
            "</addresses>";
        try {
            XML.toJSONObject(xmlStr);
            fail("Expecting a JSONException");
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "Misplaced '<' at 193 [character 4 line 6]",
                    e.getMessage());
        }
    }

    /**
     * Invalid XML string (partial CDATA chars in tag name)
     * Expects JSONException
     */
    @Test
    public void shouldHandleInvalidCDATABangInTag() {
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "    <address>\n"+
            "       <name>Joe Tester</name>\n"+
            "       <![[]>\n"+
            "   </address>\n"+
            "</addresses>";
        try {
            XML.toJSONObject(xmlStr);
            fail("Expecting a JSONException");
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "Expected 'CDATA[' at 204 [character 11 line 5]",
                    e.getMessage());
        }
    }

    /**
     * Null JSONObject in XML.toString()
     */
    @Test
    public void shouldHandleNullJSONXML() {
        JSONObject jsonObject= null;
        String actualXml=XML.toString(jsonObject);
        assertEquals("generated XML does not equal expected XML","\"null\"",actualXml);
    }

    /**
     * Empty JSONObject in XML.toString()
     */
    @Test
    public void shouldHandleEmptyJSONXML() {
        JSONObject jsonObject= new JSONObject();
        String xmlStr = XML.toString(jsonObject);
        assertTrue("xml string should be empty", xmlStr.isEmpty());
    }

    /**
     * No SML start tag. The ending tag ends up being treated as content.
     */
    @Test
    public void shouldHandleNoStartTag() {
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "    <address>\n"+
            "       <name/>\n"+
            "       <nocontent/>>\n"+
            "   </address>\n"+
            "</addresses>";
        String expectedStr = 
            "{\"addresses\":{\"address\":{\"name\":\"\",\"nocontent\":\"\",\""+
            "content\":\">\"},\"xsi:noNamespaceSchemaLocation\":\"test.xsd\",\""+
            "xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\"}}";
        JSONObject jsonObject = XML.toJSONObject(xmlStr);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    /**
     * Valid XML to JSONObject
     */
    @Test
    public void shouldHandleSimpleXML() {
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "   <address>\n"+
            "       <name>Joe Tester</name>\n"+
            "       <street>[CDATA[Baker street 5]</street>\n"+
            "       <NothingHere/>\n"+
            "       <TrueValue>true</TrueValue>\n"+
            "       <FalseValue>false</FalseValue>\n"+
            "       <NullValue>null</NullValue>\n"+
            "       <PositiveValue>42</PositiveValue>\n"+
            "       <NegativeValue>-23</NegativeValue>\n"+
            "       <DoubleValue>-23.45</DoubleValue>\n"+
            "       <Nan>-23x.45</Nan>\n"+
            "       <ArrayOfNum>1, 2, 3, 4.1, 5.2</ArrayOfNum>\n"+
            "   </address>\n"+
            "</addresses>";

        String expectedStr = 
            "{\"addresses\":{\"address\":{\"street\":\"[CDATA[Baker street 5]\","+
            "\"name\":\"Joe Tester\",\"NothingHere\":\"\",TrueValue:true,\n"+
            "\"FalseValue\":false,\"NullValue\":null,\"PositiveValue\":42,\n"+
            "\"NegativeValue\":-23,\"DoubleValue\":-23.45,\"Nan\":-23x.45,\n"+
            "\"ArrayOfNum\":\"1, 2, 3, 4.1, 5.2\"\n"+
            "},\"xsi:noNamespaceSchemaLocation\":"+
            "\"test.xsd\",\"xmlns:xsi\":\"http://www.w3.org/2001/"+
            "XMLSchema-instance\"}}";

        compareStringToJSONObject(xmlStr, expectedStr);
        compareReaderToJSONObject(xmlStr, expectedStr);
        compareFileToJSONObject(xmlStr, expectedStr);
    }

    /**
     * Tests to verify that supported escapes in XML are converted to actual values.
     */
    @Test
    public void testXmlEscapeToJson(){
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<root>"+
            "<rawQuote>\"</rawQuote>"+
            "<euro>A &#8364;33</euro>"+
            "<euroX>A &#x20ac;22&#x20AC;</euroX>"+
            "<unknown>some text &copy;</unknown>"+
            "<known>&#x0022; &quot; &amp; &apos; &lt; &gt;</known>"+
            "<high>&#x1D122; &#x10165;</high>" +
            "</root>";
        String expectedStr = 
            "{\"root\":{" +
            "\"rawQuote\":\"\\\"\"," +
            "\"euro\":\"A €33\"," +
            "\"euroX\":\"A €22€\"," +
            "\"unknown\":\"some text &copy;\"," +
            "\"known\":\"\\\" \\\" & ' < >\"," +
            "\"high\":\"𝄢 𐅥\""+
            "}}";
        
        compareStringToJSONObject(xmlStr, expectedStr);
        compareReaderToJSONObject(xmlStr, expectedStr);
        compareFileToJSONObject(xmlStr, expectedStr);
    }
    
    /**
     * Tests that control characters are escaped.
     */
    @Test
    public void testJsonToXmlEscape(){
        final String jsonSrc = "{\"amount\":\"10,00 €\","
                + "\"description\":\"Ação Válida\u0085\","
                + "\"xmlEntities\":\"\\\" ' & < >\""
                + "}";
        JSONObject json = new JSONObject(jsonSrc);
        String xml = XML.toString(json);
        //test control character not existing
        assertFalse("Escaping \u0085 failed. Found in XML output.", xml.contains("\u0085"));
        assertTrue("Escaping \u0085 failed. Entity not found in XML output.", xml.contains("&#x85;"));
        // test normal unicode existing
        assertTrue("Escaping € failed. Not found in XML output.", xml.contains("€"));
        assertTrue("Escaping ç failed. Not found in XML output.", xml.contains("ç"));
        assertTrue("Escaping ã failed. Not found in XML output.", xml.contains("ã"));
        assertTrue("Escaping á failed. Not found in XML output.", xml.contains("á"));
        // test XML Entities converted
        assertTrue("Escaping \" failed. Not found in XML output.", xml.contains("&quot;"));
        assertTrue("Escaping ' failed. Not found in XML output.", xml.contains("&apos;"));
        assertTrue("Escaping & failed. Not found in XML output.", xml.contains("&amp;"));
        assertTrue("Escaping < failed. Not found in XML output.", xml.contains("&lt;"));
        assertTrue("Escaping > failed. Not found in XML output.", xml.contains("&gt;"));
    }

    /**
     * Valid XML with comments to JSONObject
     */
    @Test
    public void shouldHandleCommentsInXML() {

        String xmlStr = 
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
                "<!-- this is a comment -->\n"+
                "<addresses>\n"+
                "   <address>\n"+
                "       <![CDATA[ this is -- <another> comment ]]>\n"+
                "       <name>Joe Tester</name>\n"+
                "       <!-- this is a - multi line \n"+
                "            comment -->\n"+
                "       <street>Baker street 5</street>\n"+
                "   </address>\n"+
                "</addresses>";
        JSONObject jsonObject = XML.toJSONObject(xmlStr);
        String expectedStr = "{\"addresses\":{\"address\":{\"street\":\"Baker "+
                "street 5\",\"name\":\"Joe Tester\",\"content\":\" this is -- "+
                "<another> comment \"}}}";
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    /**
     * Valid XML to XML.toString()
     */
    @Test
    public void shouldHandleToString() {
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "   <address>\n"+
            "       <name>[CDATA[Joe &amp; T &gt; e &lt; s &quot; t &apos; er]]</name>\n"+
            "       <street>Baker street 5</street>\n"+
            "       <ArrayOfNum>1, 2, 3, 4.1, 5.2</ArrayOfNum>\n"+
            "   </address>\n"+
            "</addresses>";

        String expectedStr = 
                "{\"addresses\":{\"address\":{\"street\":\"Baker street 5\","+
                "\"name\":\"[CDATA[Joe & T > e < s \\\" t \\\' er]]\","+
                "\"ArrayOfNum\":\"1, 2, 3, 4.1, 5.2\"\n"+
                "},\"xsi:noNamespaceSchemaLocation\":"+
                "\"test.xsd\",\"xmlns:xsi\":\"http://www.w3.org/2001/"+
                "XMLSchema-instance\"}}";
        
        JSONObject jsonObject = XML.toJSONObject(xmlStr);
        String xmlToStr = XML.toString(jsonObject);
        JSONObject finalJsonObject = XML.toJSONObject(xmlToStr);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }

    /**
     * Converting a JSON doc containing '>' content to JSONObject, then
     * XML.toString() should result in valid XML.
     */
    @Test
    public void shouldHandleContentNoArraytoString() {
        String expectedStr = "{\"addresses\":{\"content\":\">\"}}";
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        String finalStr = XML.toString(expectedJsonObject);
        String expectedFinalStr = "<addresses>&gt;</addresses>";
        assertEquals("Should handle expectedFinal: ["+expectedStr+"] final: ["+
                finalStr+"]", expectedFinalStr, finalStr);
    }

    /**
     * Converting a JSON doc containing a 'content' array to JSONObject, then
     * XML.toString() should result in valid XML.
     * TODO: This is probably an error in how the 'content' keyword is used.
     */
    @Test
    public void shouldHandleContentArraytoString() {
        String expectedStr = 
            "{\"addresses\":{" +
            "\"content\":[1, 2, 3]}}";
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        String finalStr = XML.toString(expectedJsonObject);
        String expectedFinalStr = "<addresses>"+
                "1\n2\n3</addresses>";
        assertEquals("Should handle expectedFinal: ["+expectedStr+"] final: ["+
                finalStr+"]", expectedFinalStr, finalStr);
    }

    /**
     * Converting a JSON doc containing a named array to JSONObject, then
     * XML.toString() should result in valid XML.
     */
    @Test
    public void shouldHandleArraytoString() {
        String expectedStr = 
            "{\"addresses\":{"+
            "\"something\":[1, 2, 3]}}";
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        String finalStr = XML.toString(expectedJsonObject);
        String expectedFinalStr = "<addresses>"+
                "<something>1</something><something>2</something><something>3</something>"+
                "</addresses>";
        assertEquals("Should handle expectedFinal: ["+expectedStr+"] final: ["+
                finalStr+"]", expectedFinalStr, finalStr);
    }
    
    /**
     * Tests that the XML output for empty arrays is consistent.
     */
    @Test
    public void shouldHandleEmptyArray(){
        final JSONObject jo1 = new JSONObject();
        jo1.put("array",new Object[]{});
        final JSONObject jo2 = new JSONObject();
        jo2.put("array",new JSONArray());

        final String expected = "<jo></jo>";
        String output1 = XML.toString(jo1,"jo");
        assertEquals("Expected an empty root tag", expected, output1);
        String output2 = XML.toString(jo2,"jo");
        assertEquals("Expected an empty root tag", expected, output2);
    }
    
    /**
     * Tests that the XML output for arrays is consistent when an internal array is empty.
     */
    @Test
    public void shouldHandleEmptyMultiArray(){
        final JSONObject jo1 = new JSONObject();
        jo1.put("arr",new Object[]{"One", new String[]{}, "Four"});
        final JSONObject jo2 = new JSONObject();
        jo2.put("arr",new JSONArray(new Object[]{"One", new JSONArray(new String[]{}), "Four"}));

        final String expected = "<jo><arr>One</arr><arr></arr><arr>Four</arr></jo>";
        String output1 = XML.toString(jo1,"jo");
        assertEquals("Expected a matching array", expected, output1);
        String output2 = XML.toString(jo2,"jo");
        assertEquals("Expected a matching array", expected, output2);
    }
   
    /**
     * Tests that the XML output for arrays is consistent when arrays are not empty.
     */
    @Test
    public void shouldHandleNonEmptyArray(){
        final JSONObject jo1 = new JSONObject();
        jo1.put("arr",new String[]{"One", "Two", "Three"});
        final JSONObject jo2 = new JSONObject();
        jo2.put("arr",new JSONArray(new String[]{"One", "Two", "Three"}));

        final String expected = "<jo><arr>One</arr><arr>Two</arr><arr>Three</arr></jo>";
        String output1 = XML.toString(jo1,"jo");
        assertEquals("Expected a non empty root tag", expected, output1);
        String output2 = XML.toString(jo2,"jo");
        assertEquals("Expected a non empty root tag", expected, output2);
    }

    /**
     * Tests that the XML output for arrays is consistent when arrays are not empty and contain internal arrays.
     */
    @Test
    public void shouldHandleMultiArray(){
        final JSONObject jo1 = new JSONObject();
        jo1.put("arr",new Object[]{"One", new String[]{"Two", "Three"}, "Four"});
        final JSONObject jo2 = new JSONObject();
        jo2.put("arr",new JSONArray(new Object[]{"One", new JSONArray(new String[]{"Two", "Three"}), "Four"}));

        final String expected = "<jo><arr>One</arr><arr><array>Two</array><array>Three</array></arr><arr>Four</arr></jo>";
        String output1 = XML.toString(jo1,"jo");
        assertEquals("Expected a matching array", expected, output1);
        String output2 = XML.toString(jo2,"jo");
        assertEquals("Expected a matching array", expected, output2);
    }

    /**
     * Converting a JSON doc containing a named array of nested arrays to
     * JSONObject, then XML.toString() should result in valid XML.
     */
    @Test
    public void shouldHandleNestedArraytoString() {
        String xmlStr = 
            "{\"addresses\":{\"address\":{\"name\":\"\",\"nocontent\":\"\","+
            "\"outer\":[[1], [2], [3]]},\"xsi:noNamespaceSchemaLocation\":\"test.xsd\",\""+
            "xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\"}}";
        JSONObject jsonObject = new JSONObject(xmlStr);
        String finalStr = XML.toString(jsonObject);
        JSONObject finalJsonObject = XML.toJSONObject(finalStr);
        String expectedStr = "<addresses><address><name/><nocontent/>"+
                "<outer><array>1</array></outer><outer><array>2</array>"+
                "</outer><outer><array>3</array></outer>"+
                "</address><xsi:noNamespaceSchemaLocation>test.xsd</xsi:noName"+
                "spaceSchemaLocation><xmlns:xsi>http://www.w3.org/2001/XMLSche"+
                "ma-instance</xmlns:xsi></addresses>";
        JSONObject expectedJsonObject = XML.toJSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }


    /**
     * Possible bug: 
     * Illegal node-names must be converted to legal XML-node-names.
     * The given example shows 2 nodes which are valid for JSON, but not for XML.
     * Therefore illegal arguments should be converted to e.g. an underscore (_).
     */
    @Test
    public void shouldHandleIllegalJSONNodeNames()
    {
        JSONObject inputJSON = new JSONObject();
        inputJSON.append("123IllegalNode", "someValue1");
        inputJSON.append("Illegal@node", "someValue2");

        String result = XML.toString(inputJSON);

        /*
         * This is invalid XML. Names should not begin with digits or contain
         * certain values, including '@'. One possible solution is to replace
         * illegal chars with '_', in which case the expected output would be:
         * <___IllegalNode>someValue1</___IllegalNode><Illegal_node>someValue2</Illegal_node>
         */
        String expected = "<123IllegalNode>someValue1</123IllegalNode><Illegal@node>someValue2</Illegal@node>";

        assertEquals("length",expected.length(), result.length());
        assertTrue("123IllegalNode",result.contains("<123IllegalNode>someValue1</123IllegalNode>"));
        assertTrue("Illegal@node",result.contains("<Illegal@node>someValue2</Illegal@node>"));
    }

    /**
     * JSONObject with NULL value, to XML.toString()
     */
    @Test
    public void shouldHandleNullNodeValue()
    {
        JSONObject inputJSON = new JSONObject();
        inputJSON.put("nullValue", JSONObject.NULL);
        // This is a possible preferred result
        // String expectedXML = "<nullValue/>";
        /**
         * This is the current behavior. JSONObject.NULL is emitted as 
         * the string, "null".
         */
        String actualXML = "<nullValue>null</nullValue>";
        String resultXML = XML.toString(inputJSON);
        assertEquals(actualXML, resultXML);
    }

    /**
     * Investigate exactly how the "content" keyword works
     */
    @Test
    public void contentOperations() {
        /*
         * When a standalone <!CDATA[...]] structure is found while parsing XML into a
         * JSONObject, the contents are placed in a string value with key="content".
         */
        String xmlStr = "<tag1></tag1><![CDATA[if (a < b && a > 0) then return]]><tag2></tag2>";
        JSONObject jsonObject = XML.toJSONObject(xmlStr);
        assertTrue("1. 3 items", 3 == jsonObject.length());
        assertTrue("1. empty tag1", "".equals(jsonObject.get("tag1")));
        assertTrue("1. empty tag2", "".equals(jsonObject.get("tag2")));
        assertTrue("1. content found", "if (a < b && a > 0) then return".equals(jsonObject.get("content")));

        // multiple consecutive standalone cdatas are accumulated into an array
        xmlStr = "<tag1></tag1><![CDATA[if (a < b && a > 0) then return]]><tag2></tag2><![CDATA[here is another cdata]]>";
        jsonObject = XML.toJSONObject(xmlStr);
        assertTrue("2. 3 items", 3 == jsonObject.length());
        assertTrue("2. empty tag1", "".equals(jsonObject.get("tag1")));
        assertTrue("2. empty tag2", "".equals(jsonObject.get("tag2")));
        assertTrue("2. content array found", jsonObject.get("content") instanceof JSONArray);
        JSONArray jsonArray = jsonObject.getJSONArray("content");
        assertTrue("2. array size", jsonArray.length() == 2);
        assertTrue("2. content array entry 0", "if (a < b && a > 0) then return".equals(jsonArray.get(0)));
        assertTrue("2. content array entry 1", "here is another cdata".equals(jsonArray.get(1)));

        /*
         * text content is accumulated in a "content" inside a local JSONObject.
         * If there is only one instance, it is saved in the context (a different JSONObject 
         * from the calling code. and the content element is discarded. 
         */
        xmlStr =  "<tag1>value 1</tag1>";
        jsonObject = XML.toJSONObject(xmlStr);
        assertTrue("3. 2 items", 1 == jsonObject.length());
        assertTrue("3. value tag1", "value 1".equals(jsonObject.get("tag1")));

        /*
         * array-style text content (multiple tags with the same name) is 
         * accumulated in a local JSONObject with key="content" and value=JSONArray,
         * saved in the context, and then the local JSONObject is discarded.
         */
        xmlStr =  "<tag1>value 1</tag1><tag1>2</tag1><tag1>true</tag1>";
        jsonObject = XML.toJSONObject(xmlStr);
        assertTrue("4. 1 item", 1 == jsonObject.length());
        assertTrue("4. content array found", jsonObject.get("tag1") instanceof JSONArray);
        jsonArray = jsonObject.getJSONArray("tag1");
        assertTrue("4. array size", jsonArray.length() == 3);
        assertTrue("4. content array entry 0", "value 1".equals(jsonArray.get(0)));
        assertTrue("4. content array entry 1", jsonArray.getInt(1) == 2);
        assertTrue("4. content array entry 2", jsonArray.getBoolean(2) == true);

        /*
         * Complex content is accumulated in a "content" field. For example, an element
         * may contain a mix of child elements and text. Each text segment is 
         * accumulated to content. 
         */
        xmlStr =  "<tag1>val1<tag2/>val2</tag1>";
        jsonObject = XML.toJSONObject(xmlStr);
        assertTrue("5. 1 item", 1 == jsonObject.length());
        assertTrue("5. jsonObject found", jsonObject.get("tag1") instanceof JSONObject);
        jsonObject = jsonObject.getJSONObject("tag1");
        assertTrue("5. 2 contained items", 2 == jsonObject.length());
        assertTrue("5. contained tag", "".equals(jsonObject.get("tag2")));
        assertTrue("5. contained content jsonArray found", jsonObject.get("content") instanceof JSONArray);
        jsonArray = jsonObject.getJSONArray("content");
        assertTrue("5. array size", jsonArray.length() == 2);
        assertTrue("5. content array entry 0", "val1".equals(jsonArray.get(0)));
        assertTrue("5. content array entry 1", "val2".equals(jsonArray.get(1)));

        /*
         * If there is only 1 complex text content, then it is accumulated in a 
         * "content" field as a string.
         */
        xmlStr =  "<tag1>val1<tag2/></tag1>";
        jsonObject = XML.toJSONObject(xmlStr);
        assertTrue("6. 1 item", 1 == jsonObject.length());
        assertTrue("6. jsonObject found", jsonObject.get("tag1") instanceof JSONObject);
        jsonObject = jsonObject.getJSONObject("tag1");
        assertTrue("6. contained content found", "val1".equals(jsonObject.get("content")));
        assertTrue("6. contained tag2", "".equals(jsonObject.get("tag2")));

        /*
         * In this corner case, the content sibling happens to have key=content
         * We end up with an array within an array, and no content element.
         * This is probably a bug. 
         */
        xmlStr =  "<tag1>val1<content/></tag1>";
        jsonObject = XML.toJSONObject(xmlStr);
        assertTrue("7. 1 item", 1 == jsonObject.length());
        assertTrue("7. jsonArray found", jsonObject.get("tag1") instanceof JSONArray);
        jsonArray = jsonObject.getJSONArray("tag1");
        assertTrue("array size 1", jsonArray.length() == 1);
        assertTrue("7. contained array found", jsonArray.get(0) instanceof JSONArray);
        jsonArray = jsonArray.getJSONArray(0);
        assertTrue("7. inner array size 2", jsonArray.length() == 2);
        assertTrue("7. inner array item 0", "val1".equals(jsonArray.get(0)));
        assertTrue("7. inner array item 1", "".equals(jsonArray.get(1)));

        /*
         * Confirm behavior of original issue
         */
        String jsonStr = 
                "{"+
                    "\"Profile\": {"+
                        "\"list\": {"+
                            "\"history\": {"+
                                "\"entries\": ["+
                                    "{"+
                                        "\"deviceId\": \"id\","+
                                        "\"content\": {"+
                                            "\"material\": ["+
                                                "{"+
                                                    "\"stuff\": false"+
                                                "}"+
                                            "]"+
                                        "}"+
                                    "}"+
                                "]"+
                            "}"+
                        "}"+
                    "}"+
                "}";
        jsonObject = new JSONObject(jsonStr);
        xmlStr = XML.toString(jsonObject);
        /*
         * This is the created XML. Looks like content was mistaken for
         * complex (child node + text) XML. 
         *  <Profile>
         *      <list>
         *          <history>
         *              <entries>
         *                  <deviceId>id</deviceId>
         *                  {&quot;material&quot;:[{&quot;stuff&quot;:false}]}
         *              </entries>
         *          </history>
         *      </list>
         *  </Profile>
         */
        assertTrue("nothing to test here, see comment on created XML, above", true);
    }

    /**
     * Convenience method, given an input string and expected result,
     * convert to JSONObject and compare actual to expected result.
     * @param xmlStr the string to parse
     * @param expectedStr the expected JSON string
     */
    private void compareStringToJSONObject(String xmlStr, String expectedStr) {
        JSONObject jsonObject = XML.toJSONObject(xmlStr);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    /**
     * Convenience method, given an input string and expected result,
     * convert to JSONObject via reader and compare actual to expected result.
     * @param xmlStr the string to parse
     * @param expectedStr the expected JSON string
     */
    private void compareReaderToJSONObject(String xmlStr, String expectedStr) {
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Reader reader = new StringReader(xmlStr);
        JSONObject jsonObject = XML.toJSONObject(reader);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    /**
     * Convenience method, given an input string and expected result, convert to
     * JSONObject via file and compare actual to expected result.
     * 
     * @param xmlStr
     *            the string to parse
     * @param expectedStr
     *            the expected JSON string
     * @throws IOException
     */
    private void compareFileToJSONObject(String xmlStr, String expectedStr) {
        try {
            JSONObject expectedJsonObject = new JSONObject(expectedStr);
            File tempFile = this.testFolder.newFile("fileToJSONObject.xml");
            FileWriter fileWriter = new FileWriter(tempFile);
            try {
                fileWriter.write(xmlStr);
            } finally {
                fileWriter.close();
            }

            Reader reader = new FileReader(tempFile);
            try {
                JSONObject jsonObject = XML.toJSONObject(reader);
                Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            fail("Error: " +e.getMessage());
        }
    }

    /**
     * JSON string lost leading zero and converted "True" to true.
     */
    @Test
    public void testToJSONArray_jsonOutput() {
        final String originalXml = "<root><id>01</id><id>1</id><id>00</id><id>0</id><item id=\"01\"/><title>True</title></root>";
        final JSONObject expectedJson = new JSONObject("{\"root\":{\"item\":{\"id\":\"01\"},\"id\":[\"01\",1,\"00\",0],\"title\":true}}");
        final JSONObject actualJsonOutput = XML.toJSONObject(originalXml, false);

        Util.compareActualVsExpectedJsonObjects(actualJsonOutput,expectedJson);
    }

    /**
     * JSON string cannot be reverted to original xml.
     */
    @Test
    public void testToJSONArray_reversibility() {
        final String originalXml = "<root><id>01</id><id>1</id><id>00</id><id>0</id><item id=\"01\"/><title>True</title></root>";
        final String revertedXml = XML.toString(XML.toJSONObject(originalXml, false));

        assertNotEquals(revertedXml, originalXml);
    }

    /**
     * test passes when using the new method toJsonArray.
     */
    @Test
    public void testToJsonXML() {
        final String originalXml = "<root><id>01</id><id>1</id><id>00</id><id>0</id><item id=\"01\"/><title>True</title></root>";
        final JSONObject expected = new JSONObject("{\"root\":{\"item\":{\"id\":\"01\"},\"id\":[\"01\",\"1\",\"00\",\"0\"],\"title\":\"True\"}}");

        final JSONObject actual = XML.toJSONObject(originalXml,true);
        
        Util.compareActualVsExpectedJsonObjects(actual, expected);
        
        final String reverseXml = XML.toString(actual);
        // this reversal isn't exactly the same. use JSONML for an exact reversal
        // the order of the elements may be differnet as well.
        final String expectedReverseXml = "<root><item><id>01</id></item><id>01</id><id>1</id><id>00</id><id>0</id><title>True</title></root>";

        assertEquals("length",expectedReverseXml.length(), reverseXml.length());
        assertTrue("array contents", reverseXml.contains("<id>01</id><id>1</id><id>00</id><id>0</id>"));
        assertTrue("item contents", reverseXml.contains("<item><id>01</id></item>"));
        assertTrue("title contents", reverseXml.contains("<title>True</title>"));
    }
    
    /**
     * test to validate certain conditions of XML unescaping.
     */
    @Test
    public void testUnescape() {
        assertEquals("{\"xml\":\"Can cope <;\"}",
                XML.toJSONObject("<xml>Can cope &lt;; </xml>").toString());
        assertEquals("Can cope <; ", XML.unescape("Can cope &lt;; "));

        assertEquals("{\"xml\":\"Can cope & ;\"}",
                XML.toJSONObject("<xml>Can cope &amp; ; </xml>").toString());
        assertEquals("Can cope & ; ", XML.unescape("Can cope &amp; ; "));

        assertEquals("{\"xml\":\"Can cope &;\"}",
                XML.toJSONObject("<xml>Can cope &amp;; </xml>").toString());
        assertEquals("Can cope &; ", XML.unescape("Can cope &amp;; "));

        // unicode entity
        assertEquals("{\"xml\":\"Can cope 4;\"}",
                XML.toJSONObject("<xml>Can cope &#x34;; </xml>").toString());
        assertEquals("Can cope 4; ", XML.unescape("Can cope &#x34;; "));

        // double escaped
        assertEquals("{\"xml\":\"Can cope &lt;\"}",
                XML.toJSONObject("<xml>Can cope &amp;lt; </xml>").toString());
        assertEquals("Can cope &lt; ", XML.unescape("Can cope &amp;lt; "));
        
        assertEquals("{\"xml\":\"Can cope &#x34;\"}",
                XML.toJSONObject("<xml>Can cope &amp;#x34; </xml>").toString());
        assertEquals("Can cope &#x34; ", XML.unescape("Can cope &amp;#x34; "));

   }

    /**
     * test passes when xsi:nil="true" converting to null (JSON specification-like nil conversion enabled)
     */
    @Test
    public void testToJsonWithNullWhenNilConversionEnabled() {
        final String originalXml = "<root><id xsi:nil=\"true\"/></root>";
        final String expectedJsonString = "{\"root\":{\"id\":null}}";

        final JSONObject json = XML.toJSONObject(originalXml,
                new XMLParserConfiguration()
                    .withKeepStrings(false)
                    .withcDataTagName("content")
                    .withConvertNilAttributeToNull(true));
        assertEquals(expectedJsonString, json.toString());
    }

    /**
     * test passes when xsi:nil="true" not converting to null (JSON specification-like nil conversion disabled)
     */
    @Test
    public void testToJsonWithNullWhenNilConversionDisabled() {
        final String originalXml = "<root><id xsi:nil=\"true\"/></root>";
        final String expectedJsonString = "{\"root\":{\"id\":{\"xsi:nil\":true}}}";

        final JSONObject json = XML.toJSONObject(originalXml, new XMLParserConfiguration());
        assertEquals(expectedJsonString, json.toString());
    }

    /**
     * Tests to verify that supported escapes in XML are converted to actual values.
     */
    @Test
    public void testIssue537CaseSensitiveHexEscapeMinimal(){
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<root>Neutrophils.Hypersegmented &#X7C; Bld-Ser-Plas</root>";
        String expectedStr = 
            "{\"root\":\"Neutrophils.Hypersegmented | Bld-Ser-Plas\"}";
        JSONObject xmlJSONObj = XML.toJSONObject(xmlStr, true);
        JSONObject expected = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(xmlJSONObj, expected);
    }

    /**
     * Tests to verify that supported escapes in XML are converted to actual values.
     */
    @Test
    public void testIssue537CaseSensitiveHexEscapeFullFile(){
        try {
            InputStream xmlStream = null;
            try {
                xmlStream = XMLTest.class.getClassLoader().getResourceAsStream("Issue537.xml");
                Reader xmlReader = new InputStreamReader(xmlStream);
                JSONObject actual = XML.toJSONObject(xmlReader, true);
                InputStream jsonStream = null;
                try {
                    jsonStream = XMLTest.class.getClassLoader().getResourceAsStream("Issue537.json");
                    final JSONObject expected = new JSONObject(new JSONTokener(jsonStream));
                    Util.compareActualVsExpectedJsonObjects(actual,expected);
                } finally {
                    if (jsonStream != null) {
                        jsonStream.close();
                    }
                }
            } finally {
                if (xmlStream != null) {
                    xmlStream.close();
                }
            }
        } catch (IOException e) {
            fail("file writer error: " +e.getMessage());
        }
    }

    /**
     * Tests to verify that supported escapes in XML are converted to actual values.
     */
    @Test
    public void testIssue537CaseSensitiveHexUnEscapeDirect(){
        String origStr = 
            "Neutrophils.Hypersegmented &#X7C; Bld-Ser-Plas";
        String expectedStr = 
            "Neutrophils.Hypersegmented | Bld-Ser-Plas";
        String actualStr = XML.unescape(origStr);
        
        assertEquals("Case insensitive Entity unescape",  expectedStr, actualStr);
    }

    /**
     * test passes when xsi:type="java.lang.String" not converting to string
     */
    @Test
    public void testToJsonWithTypeWhenTypeConversionDisabled() {
        String originalXml = "<root><id xsi:type=\"string\">1234</id></root>";
        String expectedJsonString = "{\"root\":{\"id\":{\"xsi:type\":\"string\",\"content\":1234}}}";
        JSONObject expectedJson = new JSONObject(expectedJsonString);
        JSONObject actualJson = XML.toJSONObject(originalXml, new XMLParserConfiguration());
        Util.compareActualVsExpectedJsonObjects(actualJson,expectedJson);
    }

    /**
     * test passes when xsi:type="java.lang.String" converting to String
     */
    @Test
    public void testToJsonWithTypeWhenTypeConversionEnabled() {
        String originalXml = "<root><id1 xsi:type=\"string\">1234</id1>"
                + "<id2 xsi:type=\"integer\">1234</id2></root>";
        String expectedJsonString = "{\"root\":{\"id2\":1234,\"id1\":\"1234\"}}";
        JSONObject expectedJson = new JSONObject(expectedJsonString);
        Map<String, XMLXsiTypeConverter<?>> xsiTypeMap = new HashMap<String, XMLXsiTypeConverter<?>>();
        xsiTypeMap.put("string", new XMLXsiTypeConverter<String>() {
            @Override public String convert(final String value) {
                return value;
            }
        });
        xsiTypeMap.put("integer", new XMLXsiTypeConverter<Integer>() {
            @Override public Integer convert(final String value) {
                return Integer.valueOf(value);
            }
        });
        JSONObject actualJson = XML.toJSONObject(originalXml, new XMLParserConfiguration().withXsiTypeMap(xsiTypeMap));
        Util.compareActualVsExpectedJsonObjects(actualJson,expectedJson);
    }

    @Test
    public void testToJsonWithXSITypeWhenTypeConversionEnabled() {
        String originalXml = "<root><asString xsi:type=\"string\">12345</asString><asInt "
                + "xsi:type=\"integer\">54321</asInt></root>";
        String expectedJsonString = "{\"root\":{\"asString\":\"12345\",\"asInt\":54321}}";
        JSONObject expectedJson = new JSONObject(expectedJsonString);
        Map<String, XMLXsiTypeConverter<?>> xsiTypeMap = new HashMap<String, XMLXsiTypeConverter<?>>();
        xsiTypeMap.put("string", new XMLXsiTypeConverter<String>() {
            @Override public String convert(final String value) {
                return value;
            }
        });
        xsiTypeMap.put("integer", new XMLXsiTypeConverter<Integer>() {
            @Override public Integer convert(final String value) {
                return Integer.valueOf(value);
            }
        });
        JSONObject actualJson = XML.toJSONObject(originalXml, new XMLParserConfiguration().withXsiTypeMap(xsiTypeMap));
        Util.compareActualVsExpectedJsonObjects(actualJson,expectedJson);
    }

    @Test
    public void testToJsonWithXSITypeWhenTypeConversionNotEnabledOnOne() {
        String originalXml = "<root><asString xsi:type=\"string\">12345</asString><asInt>54321</asInt></root>";
        String expectedJsonString = "{\"root\":{\"asString\":\"12345\",\"asInt\":54321}}";
        JSONObject expectedJson = new JSONObject(expectedJsonString);
        Map<String, XMLXsiTypeConverter<?>> xsiTypeMap = new HashMap<String, XMLXsiTypeConverter<?>>();
        xsiTypeMap.put("string", new XMLXsiTypeConverter<String>() {
            @Override public String convert(final String value) {
                return value;
            }
        });
        JSONObject actualJson = XML.toJSONObject(originalXml, new XMLParserConfiguration().withXsiTypeMap(xsiTypeMap));
        Util.compareActualVsExpectedJsonObjects(actualJson,expectedJson);
    }

    @Test
    public void testXSITypeMapNotModifiable() {
        Map<String, XMLXsiTypeConverter<?>> xsiTypeMap = new HashMap<String, XMLXsiTypeConverter<?>>();
        XMLParserConfiguration config = new XMLParserConfiguration().withXsiTypeMap(xsiTypeMap);
        xsiTypeMap.put("string", new XMLXsiTypeConverter<String>() {
            @Override public String convert(final String value) {
                return value;
            }
        });
        assertEquals("Config Conversion Map size is expected to be 0", 0, config.getXsiTypeMap().size());

        try {
            config.getXsiTypeMap().put("boolean", new XMLXsiTypeConverter<Boolean>() {
                @Override public Boolean convert(final String value) {
                    return Boolean.valueOf(value);
                }
            });
            fail("Expected to be unable to modify the config");
        } catch (Exception ignored) { }
    }

    @Test
    public void testIndentComplicatedJsonObject(){
        String str = "{\n" +
                "  \"success\": true,\n" +
                "  \"error\": null,\n" +
                "  \"response\": [\n" +
                "    {\n" +
                "      \"timestamp\": 1664917200,\n" +
                "      \"dateTimeISO\": \"2022-10-05T00:00:00+03:00\",\n" +
                "      \"loc\": {\n" +
                "        \"lat\": 39.91987,\n" +
                "        \"long\": 32.85427\n" +
                "      },\n" +
                "      \"place\": {\n" +
                "        \"name\": \"ankara\",\n" +
                "        \"state\": \"an\",\n" +
                "        \"country\": \"tr\"\n" +
                "      },\n" +
                "      \"profile\": {\n" +
                "        \"tz\": \"Europe/Istanbul\"\n" +
                "      },\n" +
                "      \"sun\": {\n" +
                "        \"rise\": 1664941721,\n" +
                "        \"riseISO\": \"2022-10-05T06:48:41+03:00\",\n" +
                "        \"set\": 1664983521,\n" +
                "        \"setISO\": \"2022-10-05T18:25:21+03:00\",\n" +
                "        \"transit\": 1664962621,\n" +
                "        \"transitISO\": \"2022-10-05T12:37:01+03:00\",\n" +
                "        \"midnightSun\": false,\n" +
                "        \"polarNight\": false,\n" +
                "        \"twilight\": {\n" +
                "          \"civilBegin\": 1664940106,\n" +
                "          \"civilBeginISO\": \"2022-10-05T06:21:46+03:00\",\n" +
                "          \"civilEnd\": 1664985136,\n" +
                "          \"civilEndISO\": \"2022-10-05T18:52:16+03:00\",\n" +
                "          \"nauticalBegin\": 1664938227,\n" +
                "          \"nauticalBeginISO\": \"2022-10-05T05:50:27+03:00\",\n" +
                "          \"nauticalEnd\": 1664987015,\n" +
                "          \"nauticalEndISO\": \"2022-10-05T19:23:35+03:00\",\n" +
                "          \"astronomicalBegin\": 1664936337,\n" +
                "          \"astronomicalBeginISO\": \"2022-10-05T05:18:57+03:00\",\n" +
                "          \"astronomicalEnd\": 1664988905,\n" +
                "          \"astronomicalEndISO\": \"2022-10-05T19:55:05+03:00\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"moon\": {\n" +
                "        \"rise\": 1664976480,\n" +
                "        \"riseISO\": \"2022-10-05T16:28:00+03:00\",\n" +
                "        \"set\": 1664921520,\n" +
                "        \"setISO\": \"2022-10-05T01:12:00+03:00\",\n" +
                "        \"transit\": 1664994240,\n" +
                "        \"transitISO\": \"2022-10-05T21:24:00+03:00\",\n" +
                "        \"underfoot\": 1664949360,\n" +
                "        \"underfootISO\": \"2022-10-05T08:56:00+03:00\",\n" +
                "        \"phase\": {\n" +
                "          \"phase\": 0.3186,\n" +
                "          \"name\": \"waxing gibbous\",\n" +
                "          \"illum\": 71,\n" +
                "          \"age\": 9.41,\n" +
                "          \"angle\": 0.55\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}" ;
        JSONObject jsonObject = new JSONObject(str);
        String actualIndentedXmlString = XML.toString(jsonObject, 1);
        String expected = "<success>true</success>\n" +
                "<response>\n" +
                " <dateTimeISO>2022-10-05T00:00:00+03:00</dateTimeISO>\n" +
                " <loc>\n" +
                "  <lat>39.91987</lat>\n" +
                "  <long>32.85427</long>\n" +
                " </loc>\n" +
                " <moon>\n" +
                "  <phase>\n" +
                "   <phase>0.3186</phase>\n" +
                "   <name>waxing gibbous</name>\n" +
                "   <angle>0.55</angle>\n" +
                "   <illum>71</illum>\n" +
                "   <age>9.41</age>\n" +
                "  </phase>\n" +
                "  <setISO>2022-10-05T01:12:00+03:00</setISO>\n" +
                "  <underfoot>1664949360</underfoot>\n" +
                "  <set>1664921520</set>\n" +
                "  <transit>1664994240</transit>\n" +
                "  <transitISO>2022-10-05T21:24:00+03:00</transitISO>\n" +
                "  <riseISO>2022-10-05T16:28:00+03:00</riseISO>\n" +
                "  <rise>1664976480</rise>\n" +
                "  <underfootISO>2022-10-05T08:56:00+03:00</underfootISO>\n" +
                " </moon>\n" +
                " <profile>\n" +
                "  <tz>Europe/Istanbul</tz>\n" +
                " </profile>\n" +
                " <place>\n" +
                "  <country>tr</country>\n" +
                "  <name>ankara</name>\n" +
                "  <state>an</state>\n" +
                " </place>\n" +
                " <sun>\n" +
                "  <setISO>2022-10-05T18:25:21+03:00</setISO>\n" +
                "  <midnightSun>false</midnightSun>\n" +
                "  <set>1664983521</set>\n" +
                "  <transit>1664962621</transit>\n" +
                "  <polarNight>false</polarNight>\n" +
                "  <transitISO>2022-10-05T12:37:01+03:00</transitISO>\n" +
                "  <riseISO>2022-10-05T06:48:41+03:00</riseISO>\n" +
                "  <rise>1664941721</rise>\n" +
                "  <twilight>\n" +
                "   <civilEnd>1664985136</civilEnd>\n" +
                "   <astronomicalBegin>1664936337</astronomicalBegin>\n" +
                "   <astronomicalEnd>1664988905</astronomicalEnd>\n" +
                "   <astronomicalBeginISO>2022-10-05T05:18:57+03:00</astronomicalBeginISO>\n" +
                "   <civilBegin>1664940106</civilBegin>\n" +
                "   <nauticalEndISO>2022-10-05T19:23:35+03:00</nauticalEndISO>\n" +
                "   <astronomicalEndISO>2022-10-05T19:55:05+03:00</astronomicalEndISO>\n" +
                "   <nauticalBegin>1664938227</nauticalBegin>\n" +
                "   <nauticalEnd>1664987015</nauticalEnd>\n" +
                "   <nauticalBeginISO>2022-10-05T05:50:27+03:00</nauticalBeginISO>\n" +
                "   <civilBeginISO>2022-10-05T06:21:46+03:00</civilBeginISO>\n" +
                "   <civilEndISO>2022-10-05T18:52:16+03:00</civilEndISO>\n" +
                "  </twilight>\n" +
                " </sun>\n" +
                " <timestamp>1664917200</timestamp>\n" +
                "</response>\n" +
                "<error>null</error>\n";
        assertEquals(actualIndentedXmlString, expected);


    }
    @Test
    public void testIndentSimpleJsonObject(){
        String str = "{    \"employee\": {  \n" +
                "        \"name\":       \"sonoo\",   \n" +
                "        \"salary\":      56000,   \n" +
                "        \"married\":    true  \n" +
                "    }}";
        JSONObject jsonObject = new JSONObject(str);
        String actual = XML.toString(jsonObject, "Test", 2);
        String expected = "<Test>\n" +
                "  <employee>\n" +
                "    <name>sonoo</name>\n" +
                "    <salary>56000</salary>\n" +
                "    <married>true</married>\n" +
                "  </employee>\n" +
                "</Test>\n";
        assertEquals(actual, expected);
    }

    @Test
    public void testIndentSimpleJsonArray(){
        String str = "[  \n" +
                "    {\"name\":\"Ram\", \"email\":\"Ram@gmail.com\"},  \n" +
                "    {\"name\":\"Bob\", \"email\":\"bob32@gmail.com\"}  \n" +
                "]  ";
        JSONArray jsonObject = new JSONArray(str);
        String actual = XML.toString(jsonObject, 2);
        String expected = "<array>\n" +
                "  <name>Ram</name>\n" +
                "  <email>Ram@gmail.com</email>\n" +
                "</array>\n" +
                "<array>\n" +
                "  <name>Bob</name>\n" +
                "  <email>bob32@gmail.com</email>\n" +
                "</array>\n";
        assertEquals(actual, expected);


    }

    @Test
    public void testIndentComplicatedJsonObjectWithArrayAndWithConfig(){
        String str = "{\n" +
                "  \"success\": true,\n" +
                "  \"error\": null,\n" +
                "  \"response\": [\n" +
                "    {\n" +
                "      \"loc\": {\n" +
                "        \"long\": 31.25,\n" +
                "        \"lat\": 30.063\n" +
                "      },\n" +
                "      \"interval\": \"day\",\n" +
                "      \"place\": {\n" +
                "        \"name\": \"cairo\",\n" +
                "        \"state\": \"qh\",\n" +
                "        \"country\": \"eg\"\n" +
                "      },\n" +
                "      \"periods\": [\n" +
                "        {\n" +
                "          \"timestamp\": 1665032400,\n" +
                "          \"validTime\": \"2022-10-06T07:00:00+02:00\",\n" +
                "          \"dateTimeISO\": \"2022-10-06T07:00:00+02:00\",\n" +
                "          \"maxTempC\": 32,\n" +
                "          \"maxTempF\": 90,\n" +
                "          \"minTempC\": 19,\n" +
                "          \"minTempF\": 66,\n" +
                "          \"avgTempC\": 25,\n" +
                "          \"avgTempF\": 78,\n" +
                "          \"tempC\": null,\n" +
                "          \"tempF\": null,\n" +
                "          \"maxFeelslikeC\": 32,\n" +
                "          \"maxFeelslikeF\": 89,\n" +
                "          \"minFeelslikeC\": 21,\n" +
                "          \"minFeelslikeF\": 70,\n" +
                "          \"avgFeelslikeC\": 26,\n" +
                "          \"avgFeelslikeF\": 80,\n" +
                "          \"feelslikeC\": 21,\n" +
                "          \"feelslikeF\": 70,\n" +
                "          \"maxDewpointC\": 17,\n" +
                "          \"maxDewpointF\": 63,\n" +
                "          \"minDewpointC\": 11,\n" +
                "          \"minDewpointF\": 52,\n" +
                "          \"avgDewpointC\": 14,\n" +
                "          \"avgDewpointF\": 58,\n" +
                "          \"dewpointC\": 17,\n" +
                "          \"dewpointF\": 63,\n" +
                "          \"maxHumidity\": 77,\n" +
                "          \"minHumidity\": 29,\n" +
                "          \"humidity\": 77,\n" +
                "          \"pop\": 0,\n" +
                "          \"precipMM\": 0,\n" +
                "          \"precipIN\": 0,\n" +
                "          \"iceaccum\": null,\n" +
                "          \"iceaccumMM\": null,\n" +
                "          \"iceaccumIN\": null,\n" +
                "          \"snowCM\": 0,\n" +
                "          \"snowIN\": 0,\n" +
                "          \"pressureMB\": 1015,\n" +
                "          \"pressureIN\": 29.97,\n" +
                "          \"windDir\": \"N\",\n" +
                "          \"windDirDEG\": 353,\n" +
                "          \"windSpeedKTS\": 5,\n" +
                "          \"windSpeedKPH\": 9,\n" +
                "          \"windSpeedMPH\": 6,\n" +
                "          \"windGustKTS\": 21,\n" +
                "          \"windGustKPH\": 40,\n" +
                "          \"windGustMPH\": 25,\n" +
                "          \"windDirMax\": \"NNW\",\n" +
                "          \"windDirMaxDEG\": 342,\n" +
                "          \"windSpeedMaxKTS\": 9,\n" +
                "          \"windSpeedMaxKPH\": 16,\n" +
                "          \"windSpeedMaxMPH\": 10,\n" +
                "          \"windDirMin\": \"N\",\n" +
                "          \"windDirMinDEG\": 353,\n" +
                "          \"windSpeedMinKTS\": 1,\n" +
                "          \"windSpeedMinKPH\": 2,\n" +
                "          \"windSpeedMinMPH\": 1,\n" +
                "          \"windDir80m\": \"N\",\n" +
                "          \"windDir80mDEG\": 11,\n" +
                "          \"windSpeed80mKTS\": 12,\n" +
                "          \"windSpeed80mKPH\": 22,\n" +
                "          \"windSpeed80mMPH\": 13,\n" +
                "          \"windGust80mKTS\": 22,\n" +
                "          \"windGust80mKPH\": 41,\n" +
                "          \"windGust80mMPH\": 25,\n" +
                "          \"windDirMax80m\": \"NNW\",\n" +
                "          \"windDirMax80mDEG\": 343,\n" +
                "          \"windSpeedMax80mKTS\": 22,\n" +
                "          \"windSpeedMax80mKPH\": 41,\n" +
                "          \"windSpeedMax80mMPH\": 25,\n" +
                "          \"windDirMin80m\": \"E\",\n" +
                "          \"windDirMin80mDEG\": 95,\n" +
                "          \"windSpeedMin80mKTS\": 8,\n" +
                "          \"windSpeedMin80mKPH\": 15,\n" +
                "          \"windSpeedMin80mMPH\": 10,\n" +
                "          \"sky\": 22,\n" +
                "          \"cloudsCoded\": \"FW\",\n" +
                "          \"weather\": \"Mostly Sunny\",\n" +
                "          \"weatherCoded\": [],\n" +
                "          \"weatherPrimary\": \"Mostly Sunny\",\n" +
                "          \"weatherPrimaryCoded\": \"::FW\",\n" +
                "          \"icon\": \"fair.png\",\n" +
                "          \"visibilityKM\": 24.135,\n" +
                "          \"visibilityMI\": 15,\n" +
                "          \"uvi\": 6,\n" +
                "          \"solradWM2\": 5608,\n" +
                "          \"solradMinWM2\": 0,\n" +
                "          \"solradMaxWM2\": 778,\n" +
                "          \"isDay\": true,\n" +
                "          \"maxCoverage\": \"\",\n" +
                "          \"sunrise\": 1665028274,\n" +
                "          \"sunset\": 1665070502,\n" +
                "          \"sunriseISO\": \"2022-10-06T05:51:14+02:00\",\n" +
                "          \"sunsetISO\": \"2022-10-06T17:35:02+02:00\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"timestamp\": 1665118800,\n" +
                "          \"validTime\": \"2022-10-07T07:00:00+02:00\",\n" +
                "          \"dateTimeISO\": \"2022-10-07T07:00:00+02:00\",\n" +
                "          \"maxTempC\": 30,\n" +
                "          \"maxTempF\": 86,\n" +
                "          \"minTempC\": 19,\n" +
                "          \"minTempF\": 66,\n" +
                "          \"avgTempC\": 24,\n" +
                "          \"avgTempF\": 76,\n" +
                "          \"tempC\": null,\n" +
                "          \"tempF\": null,\n" +
                "          \"maxFeelslikeC\": 29,\n" +
                "          \"maxFeelslikeF\": 85,\n" +
                "          \"minFeelslikeC\": 19,\n" +
                "          \"minFeelslikeF\": 67,\n" +
                "          \"avgFeelslikeC\": 24,\n" +
                "          \"avgFeelslikeF\": 76,\n" +
                "          \"feelslikeC\": 19,\n" +
                "          \"feelslikeF\": 67,\n" +
                "          \"maxDewpointC\": 15,\n" +
                "          \"maxDewpointF\": 60,\n" +
                "          \"minDewpointC\": 10,\n" +
                "          \"minDewpointF\": 50,\n" +
                "          \"avgDewpointC\": 12,\n" +
                "          \"avgDewpointF\": 54,\n" +
                "          \"dewpointC\": 15,\n" +
                "          \"dewpointF\": 60,\n" +
                "          \"maxHumidity\": 77,\n" +
                "          \"minHumidity\": 30,\n" +
                "          \"humidity\": 77,\n" +
                "          \"pop\": 0,\n" +
                "          \"precipMM\": 0,\n" +
                "          \"precipIN\": 0,\n" +
                "          \"iceaccum\": null,\n" +
                "          \"iceaccumMM\": null,\n" +
                "          \"iceaccumIN\": null,\n" +
                "          \"snowCM\": 0,\n" +
                "          \"snowIN\": 0,\n" +
                "          \"pressureMB\": 1014,\n" +
                "          \"pressureIN\": 29.95,\n" +
                "          \"windDir\": \"NW\",\n" +
                "          \"windDirDEG\": 325,\n" +
                "          \"windSpeedKTS\": 1,\n" +
                "          \"windSpeedKPH\": 2,\n" +
                "          \"windSpeedMPH\": 1,\n" +
                "          \"windGustKTS\": 16,\n" +
                "          \"windGustKPH\": 29,\n" +
                "          \"windGustMPH\": 18,\n" +
                "          \"windDirMax\": \"WNW\",\n" +
                "          \"windDirMaxDEG\": 298,\n" +
                "          \"windSpeedMaxKTS\": 7,\n" +
                "          \"windSpeedMaxKPH\": 13,\n" +
                "          \"windSpeedMaxMPH\": 8,\n" +
                "          \"windDirMin\": \"NW\",\n" +
                "          \"windDirMinDEG\": 325,\n" +
                "          \"windSpeedMinKTS\": 1,\n" +
                "          \"windSpeedMinKPH\": 2,\n" +
                "          \"windSpeedMinMPH\": 1,\n" +
                "          \"windDir80m\": \"NNW\",\n" +
                "          \"windDir80mDEG\": 347,\n" +
                "          \"windSpeed80mKTS\": 6,\n" +
                "          \"windSpeed80mKPH\": 10,\n" +
                "          \"windSpeed80mMPH\": 6,\n" +
                "          \"windGust80mKTS\": 20,\n" +
                "          \"windGust80mKPH\": 37,\n" +
                "          \"windGust80mMPH\": 23,\n" +
                "          \"windDirMax80m\": \"NW\",\n" +
                "          \"windDirMax80mDEG\": 316,\n" +
                "          \"windSpeedMax80mKTS\": 20,\n" +
                "          \"windSpeedMax80mKPH\": 37,\n" +
                "          \"windSpeedMax80mMPH\": 23,\n" +
                "          \"windDirMin80m\": \"NNW\",\n" +
                "          \"windDirMin80mDEG\": 347,\n" +
                "          \"windSpeedMin80mKTS\": 6,\n" +
                "          \"windSpeedMin80mKPH\": 10,\n" +
                "          \"windSpeedMin80mMPH\": 6,\n" +
                "          \"sky\": 30,\n" +
                "          \"cloudsCoded\": \"FW\",\n" +
                "          \"weather\": \"Mostly Sunny\",\n" +
                "          \"weatherCoded\": [],\n" +
                "          \"weatherPrimary\": \"Mostly Sunny\",\n" +
                "          \"weatherPrimaryCoded\": \"::FW\",\n" +
                "          \"icon\": \"fair.png\",\n" +
                "          \"visibilityKM\": 24.135,\n" +
                "          \"visibilityMI\": 15,\n" +
                "          \"uvi\": 6,\n" +
                "          \"solradWM2\": 5486,\n" +
                "          \"solradMinWM2\": 0,\n" +
                "          \"solradMaxWM2\": 742,\n" +
                "          \"isDay\": true,\n" +
                "          \"maxCoverage\": \"\",\n" +
                "          \"sunrise\": 1665114710,\n" +
                "          \"sunset\": 1665156831,\n" +
                "          \"sunriseISO\": \"2022-10-07T05:51:50+02:00\",\n" +
                "          \"sunsetISO\": \"2022-10-07T17:33:51+02:00\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"timestamp\": 1665205200,\n" +
                "          \"validTime\": \"2022-10-08T07:00:00+02:00\",\n" +
                "          \"dateTimeISO\": \"2022-10-08T07:00:00+02:00\",\n" +
                "          \"maxTempC\": 30,\n" +
                "          \"maxTempF\": 87,\n" +
                "          \"minTempC\": 19,\n" +
                "          \"minTempF\": 66,\n" +
                "          \"avgTempC\": 25,\n" +
                "          \"avgTempF\": 76,\n" +
                "          \"tempC\": null,\n" +
                "          \"tempF\": null,\n" +
                "          \"maxFeelslikeC\": 30,\n" +
                "          \"maxFeelslikeF\": 86,\n" +
                "          \"minFeelslikeC\": 19,\n" +
                "          \"minFeelslikeF\": 67,\n" +
                "          \"avgFeelslikeC\": 25,\n" +
                "          \"avgFeelslikeF\": 76,\n" +
                "          \"feelslikeC\": 19,\n" +
                "          \"feelslikeF\": 67,\n" +
                "          \"maxDewpointC\": 15,\n" +
                "          \"maxDewpointF\": 59,\n" +
                "          \"minDewpointC\": 11,\n" +
                "          \"minDewpointF\": 52,\n" +
                "          \"avgDewpointC\": 13,\n" +
                "          \"avgDewpointF\": 56,\n" +
                "          \"dewpointC\": 15,\n" +
                "          \"dewpointF\": 59,\n" +
                "          \"maxHumidity\": 76,\n" +
                "          \"minHumidity\": 32,\n" +
                "          \"humidity\": 76,\n" +
                "          \"pop\": 0,\n" +
                "          \"precipMM\": 0,\n" +
                "          \"precipIN\": 0,\n" +
                "          \"iceaccum\": null,\n" +
                "          \"iceaccumMM\": null,\n" +
                "          \"iceaccumIN\": null,\n" +
                "          \"snowCM\": 0,\n" +
                "          \"snowIN\": 0,\n" +
                "          \"pressureMB\": 1014,\n" +
                "          \"pressureIN\": 29.94,\n" +
                "          \"windDir\": \"NNE\",\n" +
                "          \"windDirDEG\": 21,\n" +
                "          \"windSpeedKTS\": 1,\n" +
                "          \"windSpeedKPH\": 2,\n" +
                "          \"windSpeedMPH\": 1,\n" +
                "          \"windGustKTS\": 17,\n" +
                "          \"windGustKPH\": 32,\n" +
                "          \"windGustMPH\": 20,\n" +
                "          \"windDirMax\": \"WNW\",\n" +
                "          \"windDirMaxDEG\": 301,\n" +
                "          \"windSpeedMaxKTS\": 7,\n" +
                "          \"windSpeedMaxKPH\": 13,\n" +
                "          \"windSpeedMaxMPH\": 8,\n" +
                "          \"windDirMin\": \"NNE\",\n" +
                "          \"windDirMinDEG\": 21,\n" +
                "          \"windSpeedMinKTS\": 1,\n" +
                "          \"windSpeedMinKPH\": 2,\n" +
                "          \"windSpeedMinMPH\": 1,\n" +
                "          \"windDir80m\": \"NW\",\n" +
                "          \"windDir80mDEG\": 309,\n" +
                "          \"windSpeed80mKTS\": 5,\n" +
                "          \"windSpeed80mKPH\": 9,\n" +
                "          \"windSpeed80mMPH\": 5,\n" +
                "          \"windGust80mKTS\": 17,\n" +
                "          \"windGust80mKPH\": 31,\n" +
                "          \"windGust80mMPH\": 19,\n" +
                "          \"windDirMax80m\": \"NW\",\n" +
                "          \"windDirMax80mDEG\": 322,\n" +
                "          \"windSpeedMax80mKTS\": 17,\n" +
                "          \"windSpeedMax80mKPH\": 31,\n" +
                "          \"windSpeedMax80mMPH\": 19,\n" +
                "          \"windDirMin80m\": \"NW\",\n" +
                "          \"windDirMin80mDEG\": 309,\n" +
                "          \"windSpeedMin80mKTS\": 5,\n" +
                "          \"windSpeedMin80mKPH\": 9,\n" +
                "          \"windSpeedMin80mMPH\": 5,\n" +
                "          \"sky\": 47,\n" +
                "          \"cloudsCoded\": \"SC\",\n" +
                "          \"weather\": \"Partly Cloudy\",\n" +
                "          \"weatherCoded\": [],\n" +
                "          \"weatherPrimary\": \"Partly Cloudy\",\n" +
                "          \"weatherPrimaryCoded\": \"::SC\",\n" +
                "          \"icon\": \"pcloudy.png\",\n" +
                "          \"visibilityKM\": 24.135,\n" +
                "          \"visibilityMI\": 15,\n" +
                "          \"uvi\": 7,\n" +
                "          \"solradWM2\": 4785,\n" +
                "          \"solradMinWM2\": 0,\n" +
                "          \"solradMaxWM2\": 682,\n" +
                "          \"isDay\": true,\n" +
                "          \"maxCoverage\": \"\",\n" +
                "          \"sunrise\": 1665201146,\n" +
                "          \"sunset\": 1665243161,\n" +
                "          \"sunriseISO\": \"2022-10-08T05:52:26+02:00\",\n" +
                "          \"sunsetISO\": \"2022-10-08T17:32:41+02:00\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"timestamp\": 1665291600,\n" +
                "          \"validTime\": \"2022-10-09T07:00:00+02:00\",\n" +
                "          \"dateTimeISO\": \"2022-10-09T07:00:00+02:00\",\n" +
                "          \"maxTempC\": 31,\n" +
                "          \"maxTempF\": 87,\n" +
                "          \"minTempC\": 19,\n" +
                "          \"minTempF\": 67,\n" +
                "          \"avgTempC\": 25,\n" +
                "          \"avgTempF\": 77,\n" +
                "          \"tempC\": null,\n" +
                "          \"tempF\": null,\n" +
                "          \"maxFeelslikeC\": 30,\n" +
                "          \"maxFeelslikeF\": 86,\n" +
                "          \"minFeelslikeC\": 20,\n" +
                "          \"minFeelslikeF\": 67,\n" +
                "          \"avgFeelslikeC\": 25,\n" +
                "          \"avgFeelslikeF\": 77,\n" +
                "          \"feelslikeC\": 20,\n" +
                "          \"feelslikeF\": 67,\n" +
                "          \"maxDewpointC\": 17,\n" +
                "          \"maxDewpointF\": 63,\n" +
                "          \"minDewpointC\": 11,\n" +
                "          \"minDewpointF\": 52,\n" +
                "          \"avgDewpointC\": 14,\n" +
                "          \"avgDewpointF\": 57,\n" +
                "          \"dewpointC\": 17,\n" +
                "          \"dewpointF\": 63,\n" +
                "          \"maxHumidity\": 86,\n" +
                "          \"minHumidity\": 31,\n" +
                "          \"humidity\": 86,\n" +
                "          \"pop\": 0,\n" +
                "          \"precipMM\": 0,\n" +
                "          \"precipIN\": 0,\n" +
                "          \"iceaccum\": null,\n" +
                "          \"iceaccumMM\": null,\n" +
                "          \"iceaccumIN\": null,\n" +
                "          \"snowCM\": 0,\n" +
                "          \"snowIN\": 0,\n" +
                "          \"pressureMB\": 1016,\n" +
                "          \"pressureIN\": 29.99,\n" +
                "          \"windDir\": \"N\",\n" +
                "          \"windDirDEG\": 356,\n" +
                "          \"windSpeedKTS\": 2,\n" +
                "          \"windSpeedKPH\": 4,\n" +
                "          \"windSpeedMPH\": 2,\n" +
                "          \"windGustKTS\": 19,\n" +
                "          \"windGustKPH\": 36,\n" +
                "          \"windGustMPH\": 22,\n" +
                "          \"windDirMax\": \"NNW\",\n" +
                "          \"windDirMaxDEG\": 343,\n" +
                "          \"windSpeedMaxKTS\": 8,\n" +
                "          \"windSpeedMaxKPH\": 14,\n" +
                "          \"windSpeedMaxMPH\": 9,\n" +
                "          \"windDirMin\": \"N\",\n" +
                "          \"windDirMinDEG\": 356,\n" +
                "          \"windSpeedMinKTS\": 2,\n" +
                "          \"windSpeedMinKPH\": 4,\n" +
                "          \"windSpeedMinMPH\": 2,\n" +
                "          \"windDir80m\": \"NW\",\n" +
                "          \"windDir80mDEG\": 316,\n" +
                "          \"windSpeed80mKTS\": 5,\n" +
                "          \"windSpeed80mKPH\": 9,\n" +
                "          \"windSpeed80mMPH\": 6,\n" +
                "          \"windGust80mKTS\": 20,\n" +
                "          \"windGust80mKPH\": 36,\n" +
                "          \"windGust80mMPH\": 23,\n" +
                "          \"windDirMax80m\": \"N\",\n" +
                "          \"windDirMax80mDEG\": 354,\n" +
                "          \"windSpeedMax80mKTS\": 20,\n" +
                "          \"windSpeedMax80mKPH\": 36,\n" +
                "          \"windSpeedMax80mMPH\": 23,\n" +
                "          \"windDirMin80m\": \"NW\",\n" +
                "          \"windDirMin80mDEG\": 316,\n" +
                "          \"windSpeedMin80mKTS\": 5,\n" +
                "          \"windSpeedMin80mKPH\": 9,\n" +
                "          \"windSpeedMin80mMPH\": 6,\n" +
                "          \"sky\": 47,\n" +
                "          \"cloudsCoded\": \"SC\",\n" +
                "          \"weather\": \"Partly Cloudy\",\n" +
                "          \"weatherCoded\": [],\n" +
                "          \"weatherPrimary\": \"Partly Cloudy\",\n" +
                "          \"weatherPrimaryCoded\": \"::SC\",\n" +
                "          \"icon\": \"pcloudy.png\",\n" +
                "          \"visibilityKM\": 24.135,\n" +
                "          \"visibilityMI\": 15,\n" +
                "          \"uvi\": 7,\n" +
                "          \"solradWM2\": 4768,\n" +
                "          \"solradMinWM2\": 0,\n" +
                "          \"solradMaxWM2\": 726,\n" +
                "          \"isDay\": true,\n" +
                "          \"maxCoverage\": \"\",\n" +
                "          \"sunrise\": 1665287583,\n" +
                "          \"sunset\": 1665329491,\n" +
                "          \"sunriseISO\": \"2022-10-09T05:53:03+02:00\",\n" +
                "          \"sunsetISO\": \"2022-10-09T17:31:31+02:00\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"timestamp\": 1665378000,\n" +
                "          \"validTime\": \"2022-10-10T07:00:00+02:00\",\n" +
                "          \"dateTimeISO\": \"2022-10-10T07:00:00+02:00\",\n" +
                "          \"maxTempC\": 31,\n" +
                "          \"maxTempF\": 87,\n" +
                "          \"minTempC\": 21,\n" +
                "          \"minTempF\": 70,\n" +
                "          \"avgTempC\": 26,\n" +
                "          \"avgTempF\": 78,\n" +
                "          \"tempC\": null,\n" +
                "          \"tempF\": null,\n" +
                "          \"maxFeelslikeC\": 30,\n" +
                "          \"maxFeelslikeF\": 86,\n" +
                "          \"minFeelslikeC\": 21,\n" +
                "          \"minFeelslikeF\": 69,\n" +
                "          \"avgFeelslikeC\": 25,\n" +
                "          \"avgFeelslikeF\": 78,\n" +
                "          \"feelslikeC\": 21,\n" +
                "          \"feelslikeF\": 69,\n" +
                "          \"maxDewpointC\": 16,\n" +
                "          \"maxDewpointF\": 61,\n" +
                "          \"minDewpointC\": 13,\n" +
                "          \"minDewpointF\": 55,\n" +
                "          \"avgDewpointC\": 14,\n" +
                "          \"avgDewpointF\": 58,\n" +
                "          \"dewpointC\": 16,\n" +
                "          \"dewpointF\": 61,\n" +
                "          \"maxHumidity\": 75,\n" +
                "          \"minHumidity\": 35,\n" +
                "          \"humidity\": 75,\n" +
                "          \"pop\": 0,\n" +
                "          \"precipMM\": 0,\n" +
                "          \"precipIN\": 0,\n" +
                "          \"iceaccum\": null,\n" +
                "          \"iceaccumMM\": null,\n" +
                "          \"iceaccumIN\": null,\n" +
                "          \"snowCM\": 0,\n" +
                "          \"snowIN\": 0,\n" +
                "          \"pressureMB\": 1017,\n" +
                "          \"pressureIN\": 30.03,\n" +
                "          \"windDir\": \"N\",\n" +
                "          \"windDirDEG\": 358,\n" +
                "          \"windSpeedKTS\": 2,\n" +
                "          \"windSpeedKPH\": 4,\n" +
                "          \"windSpeedMPH\": 2,\n" +
                "          \"windGustKTS\": 16,\n" +
                "          \"windGustKPH\": 30,\n" +
                "          \"windGustMPH\": 19,\n" +
                "          \"windDirMax\": \"N\",\n" +
                "          \"windDirMaxDEG\": 10,\n" +
                "          \"windSpeedMaxKTS\": 8,\n" +
                "          \"windSpeedMaxKPH\": 15,\n" +
                "          \"windSpeedMaxMPH\": 9,\n" +
                "          \"windDirMin\": \"N\",\n" +
                "          \"windDirMinDEG\": 358,\n" +
                "          \"windSpeedMinKTS\": 2,\n" +
                "          \"windSpeedMinKPH\": 4,\n" +
                "          \"windSpeedMinMPH\": 2,\n" +
                "          \"windDir80m\": \"N\",\n" +
                "          \"windDir80mDEG\": 8,\n" +
                "          \"windSpeed80mKTS\": 7,\n" +
                "          \"windSpeed80mKPH\": 13,\n" +
                "          \"windSpeed80mMPH\": 8,\n" +
                "          \"windGust80mKTS\": 19,\n" +
                "          \"windGust80mKPH\": 36,\n" +
                "          \"windGust80mMPH\": 22,\n" +
                "          \"windDirMax80m\": \"N\",\n" +
                "          \"windDirMax80mDEG\": 10,\n" +
                "          \"windSpeedMax80mKTS\": 19,\n" +
                "          \"windSpeedMax80mKPH\": 36,\n" +
                "          \"windSpeedMax80mMPH\": 22,\n" +
                "          \"windDirMin80m\": \"E\",\n" +
                "          \"windDirMin80mDEG\": 91,\n" +
                "          \"windSpeedMin80mKTS\": 7,\n" +
                "          \"windSpeedMin80mKPH\": 13,\n" +
                "          \"windSpeedMin80mMPH\": 8,\n" +
                "          \"sky\": 64,\n" +
                "          \"cloudsCoded\": \"SC\",\n" +
                "          \"weather\": \"Partly Cloudy\",\n" +
                "          \"weatherCoded\": [],\n" +
                "          \"weatherPrimary\": \"Partly Cloudy\",\n" +
                "          \"weatherPrimaryCoded\": \"::SC\",\n" +
                "          \"icon\": \"pcloudy.png\",\n" +
                "          \"visibilityKM\": 24.135,\n" +
                "          \"visibilityMI\": 15,\n" +
                "          \"uvi\": 6,\n" +
                "          \"solradWM2\": 4494,\n" +
                "          \"solradMinWM2\": 0,\n" +
                "          \"solradMaxWM2\": 597,\n" +
                "          \"isDay\": true,\n" +
                "          \"maxCoverage\": \"\",\n" +
                "          \"sunrise\": 1665374020,\n" +
                "          \"sunset\": 1665415821,\n" +
                "          \"sunriseISO\": \"2022-10-10T05:53:40+02:00\",\n" +
                "          \"sunsetISO\": \"2022-10-10T17:30:21+02:00\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"timestamp\": 1665464400,\n" +
                "          \"validTime\": \"2022-10-11T07:00:00+02:00\",\n" +
                "          \"dateTimeISO\": \"2022-10-11T07:00:00+02:00\",\n" +
                "          \"maxTempC\": 31,\n" +
                "          \"maxTempF\": 87,\n" +
                "          \"minTempC\": 21,\n" +
                "          \"minTempF\": 70,\n" +
                "          \"avgTempC\": 26,\n" +
                "          \"avgTempF\": 78,\n" +
                "          \"tempC\": null,\n" +
                "          \"tempF\": null,\n" +
                "          \"maxFeelslikeC\": 31,\n" +
                "          \"maxFeelslikeF\": 87,\n" +
                "          \"minFeelslikeC\": 22,\n" +
                "          \"minFeelslikeF\": 72,\n" +
                "          \"avgFeelslikeC\": 26,\n" +
                "          \"avgFeelslikeF\": 79,\n" +
                "          \"feelslikeC\": 22,\n" +
                "          \"feelslikeF\": 72,\n" +
                "          \"maxDewpointC\": 17,\n" +
                "          \"maxDewpointF\": 62,\n" +
                "          \"minDewpointC\": 11,\n" +
                "          \"minDewpointF\": 51,\n" +
                "          \"avgDewpointC\": 13,\n" +
                "          \"avgDewpointF\": 55,\n" +
                "          \"dewpointC\": 17,\n" +
                "          \"dewpointF\": 62,\n" +
                "          \"maxHumidity\": 71,\n" +
                "          \"minHumidity\": 30,\n" +
                "          \"humidity\": 71,\n" +
                "          \"pop\": 0,\n" +
                "          \"precipMM\": 0,\n" +
                "          \"precipIN\": 0,\n" +
                "          \"iceaccum\": null,\n" +
                "          \"iceaccumMM\": null,\n" +
                "          \"iceaccumIN\": null,\n" +
                "          \"snowCM\": 0,\n" +
                "          \"snowIN\": 0,\n" +
                "          \"pressureMB\": 1015,\n" +
                "          \"pressureIN\": 29.98,\n" +
                "          \"windDir\": \"NNE\",\n" +
                "          \"windDirDEG\": 13,\n" +
                "          \"windSpeedKTS\": 8,\n" +
                "          \"windSpeedKPH\": 15,\n" +
                "          \"windSpeedMPH\": 9,\n" +
                "          \"windGustKTS\": 15,\n" +
                "          \"windGustKPH\": 28,\n" +
                "          \"windGustMPH\": 17,\n" +
                "          \"windDirMax\": \"NNE\",\n" +
                "          \"windDirMaxDEG\": 28,\n" +
                "          \"windSpeedMaxKTS\": 15,\n" +
                "          \"windSpeedMaxKPH\": 28,\n" +
                "          \"windSpeedMaxMPH\": 18,\n" +
                "          \"windDirMin\": \"NNE\",\n" +
                "          \"windDirMinDEG\": 14,\n" +
                "          \"windSpeedMinKTS\": 7,\n" +
                "          \"windSpeedMinKPH\": 14,\n" +
                "          \"windSpeedMinMPH\": 8,\n" +
                "          \"windDir80m\": \"NNE\",\n" +
                "          \"windDir80mDEG\": 16,\n" +
                "          \"windSpeed80mKTS\": 10,\n" +
                "          \"windSpeed80mKPH\": 19,\n" +
                "          \"windSpeed80mMPH\": 12,\n" +
                "          \"windGust80mKTS\": 17,\n" +
                "          \"windGust80mKPH\": 31,\n" +
                "          \"windGust80mMPH\": 19,\n" +
                "          \"windDirMax80m\": \"NNE\",\n" +
                "          \"windDirMax80mDEG\": 28,\n" +
                "          \"windSpeedMax80mKTS\": 17,\n" +
                "          \"windSpeedMax80mKPH\": 31,\n" +
                "          \"windSpeedMax80mMPH\": 19,\n" +
                "          \"windDirMin80m\": \"NNE\",\n" +
                "          \"windDirMin80mDEG\": 13,\n" +
                "          \"windSpeedMin80mKTS\": 9,\n" +
                "          \"windSpeedMin80mKPH\": 18,\n" +
                "          \"windSpeedMin80mMPH\": 11,\n" +
                "          \"sky\": 0,\n" +
                "          \"cloudsCoded\": \"CL\",\n" +
                "          \"weather\": \"Sunny\",\n" +
                "          \"weatherCoded\": [],\n" +
                "          \"weatherPrimary\": \"Sunny\",\n" +
                "          \"weatherPrimaryCoded\": \"::CL\",\n" +
                "          \"icon\": \"sunny.png\",\n" +
                "          \"visibilityKM\": 24.135,\n" +
                "          \"visibilityMI\": 15,\n" +
                "          \"uvi\": null,\n" +
                "          \"solradWM2\": 5450,\n" +
                "          \"solradMinWM2\": 0,\n" +
                "          \"solradMaxWM2\": 758,\n" +
                "          \"isDay\": true,\n" +
                "          \"maxCoverage\": \"\",\n" +
                "          \"sunrise\": 1665460458,\n" +
                "          \"sunset\": 1665502153,\n" +
                "          \"sunriseISO\": \"2022-10-11T05:54:18+02:00\",\n" +
                "          \"sunsetISO\": \"2022-10-11T17:29:13+02:00\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"timestamp\": 1665550800,\n" +
                "          \"validTime\": \"2022-10-12T07:00:00+02:00\",\n" +
                "          \"dateTimeISO\": \"2022-10-12T07:00:00+02:00\",\n" +
                "          \"maxTempC\": 31,\n" +
                "          \"maxTempF\": 88,\n" +
                "          \"minTempC\": 21,\n" +
                "          \"minTempF\": 69,\n" +
                "          \"avgTempC\": 26,\n" +
                "          \"avgTempF\": 79,\n" +
                "          \"tempC\": null,\n" +
                "          \"tempF\": null,\n" +
                "          \"maxFeelslikeC\": 31,\n" +
                "          \"maxFeelslikeF\": 88,\n" +
                "          \"minFeelslikeC\": 22,\n" +
                "          \"minFeelslikeF\": 72,\n" +
                "          \"avgFeelslikeC\": 26,\n" +
                "          \"avgFeelslikeF\": 80,\n" +
                "          \"feelslikeC\": 22,\n" +
                "          \"feelslikeF\": 72,\n" +
                "          \"maxDewpointC\": 16,\n" +
                "          \"maxDewpointF\": 60,\n" +
                "          \"minDewpointC\": 11,\n" +
                "          \"minDewpointF\": 51,\n" +
                "          \"avgDewpointC\": 13,\n" +
                "          \"avgDewpointF\": 55,\n" +
                "          \"dewpointC\": 16,\n" +
                "          \"dewpointF\": 60,\n" +
                "          \"maxHumidity\": 68,\n" +
                "          \"minHumidity\": 29,\n" +
                "          \"humidity\": 68,\n" +
                "          \"pop\": 0,\n" +
                "          \"precipMM\": 0,\n" +
                "          \"precipIN\": 0,\n" +
                "          \"iceaccum\": null,\n" +
                "          \"iceaccumMM\": null,\n" +
                "          \"iceaccumIN\": null,\n" +
                "          \"snowCM\": 0,\n" +
                "          \"snowIN\": 0,\n" +
                "          \"pressureMB\": 1014,\n" +
                "          \"pressureIN\": 29.95,\n" +
                "          \"windDir\": \"NNE\",\n" +
                "          \"windDirDEG\": 12,\n" +
                "          \"windSpeedKTS\": 8,\n" +
                "          \"windSpeedKPH\": 15,\n" +
                "          \"windSpeedMPH\": 9,\n" +
                "          \"windGustKTS\": 15,\n" +
                "          \"windGustKPH\": 28,\n" +
                "          \"windGustMPH\": 17,\n" +
                "          \"windDirMax\": \"E\",\n" +
                "          \"windDirMaxDEG\": 96,\n" +
                "          \"windSpeedMaxKTS\": 14,\n" +
                "          \"windSpeedMaxKPH\": 26,\n" +
                "          \"windSpeedMaxMPH\": 16,\n" +
                "          \"windDirMin\": \"NNE\",\n" +
                "          \"windDirMinDEG\": 12,\n" +
                "          \"windSpeedMinKTS\": 7,\n" +
                "          \"windSpeedMinKPH\": 13,\n" +
                "          \"windSpeedMinMPH\": 8,\n" +
                "          \"windDir80m\": \"NNE\",\n" +
                "          \"windDir80mDEG\": 15,\n" +
                "          \"windSpeed80mKTS\": 10,\n" +
                "          \"windSpeed80mKPH\": 19,\n" +
                "          \"windSpeed80mMPH\": 12,\n" +
                "          \"windGust80mKTS\": 18,\n" +
                "          \"windGust80mKPH\": 33,\n" +
                "          \"windGust80mMPH\": 21,\n" +
                "          \"windDirMax80m\": \"E\",\n" +
                "          \"windDirMax80mDEG\": 96,\n" +
                "          \"windSpeedMax80mKTS\": 18,\n" +
                "          \"windSpeedMax80mKPH\": 33,\n" +
                "          \"windSpeedMax80mMPH\": 21,\n" +
                "          \"windDirMin80m\": \"NNE\",\n" +
                "          \"windDirMin80mDEG\": 15,\n" +
                "          \"windSpeedMin80mKTS\": 10,\n" +
                "          \"windSpeedMin80mKPH\": 18,\n" +
                "          \"windSpeedMin80mMPH\": 11,\n" +
                "          \"sky\": 27,\n" +
                "          \"cloudsCoded\": \"FW\",\n" +
                "          \"weather\": \"Mostly Sunny\",\n" +
                "          \"weatherCoded\": [],\n" +
                "          \"weatherPrimary\": \"Mostly Sunny\",\n" +
                "          \"weatherPrimaryCoded\": \"::FW\",\n" +
                "          \"icon\": \"fair.png\",\n" +
                "          \"visibilityKM\": 24.135,\n" +
                "          \"visibilityMI\": 15,\n" +
                "          \"uvi\": null,\n" +
                "          \"solradWM2\": 4740,\n" +
                "          \"solradMinWM2\": 0,\n" +
                "          \"solradMaxWM2\": 743,\n" +
                "          \"isDay\": true,\n" +
                "          \"maxCoverage\": \"\",\n" +
                "          \"sunrise\": 1665546895,\n" +
                "          \"sunset\": 1665588484,\n" +
                "          \"sunriseISO\": \"2022-10-12T05:54:55+02:00\",\n" +
                "          \"sunsetISO\": \"2022-10-12T17:28:04+02:00\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"profile\": {\n" +
                "        \"tz\": \"Africa/Cairo\",\n" +
                "        \"elevM\": 23,\n" +
                "        \"elevFT\": 75\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        JSONObject jsonObject = new JSONObject(str);
        String actual = XML.toString(jsonObject, null, XMLParserConfiguration.KEEP_STRINGS,2);
        String expected = "<success>true</success>\n" +
                "<response>\n" +
                "  <loc>\n" +
                "    <long>31.25</long>\n" +
                "    <lat>30.063</lat>\n" +
                "  </loc>\n" +
                "  <profile>\n" +
                "    <elevM>23</elevM>\n" +
                "    <tz>Africa/Cairo</tz>\n" +
                "    <elevFT>75</elevFT>\n" +
                "  </profile>\n" +
                "  <periods>\n" +
                "    <dateTimeISO>2022-10-06T07:00:00+02:00</dateTimeISO>\n" +
                "    <windDirMin80m>E</windDirMin80m>\n" +
                "    <windDirMin80mDEG>95</windDirMin80mDEG>\n" +
                "    <feelslikeC>21</feelslikeC>\n" +
                "    <visibilityMI>15</visibilityMI>\n" +
                "    <windSpeedMaxMPH>10</windSpeedMaxMPH>\n" +
                "    <windDirDEG>353</windDirDEG>\n" +
                "    <windDir>N</windDir>\n" +
                "    <sunriseISO>2022-10-06T05:51:14+02:00</sunriseISO>\n" +
                "    <iceaccumMM>null</iceaccumMM>\n" +
                "    <windSpeedMaxKTS>9</windSpeedMaxKTS>\n" +
                "    <iceaccumIN>null</iceaccumIN>\n" +
                "    <minTempF>66</minTempF>\n" +
                "    <snowIN>0</snowIN>\n" +
                "    <weather>Mostly Sunny</weather>\n" +
                "    <sunsetISO>2022-10-06T17:35:02+02:00</sunsetISO>\n" +
                "    <maxFeelslikeC>32</maxFeelslikeC>\n" +
                "    <humidity>77</humidity>\n" +
                "    <windDir80m>N</windDir80m>\n" +
                "    <maxFeelslikeF>89</maxFeelslikeF>\n" +
                "    <precipMM>0</precipMM>\n" +
                "    <sky>22</sky>\n" +
                "    <windGust80mMPH>25</windGust80mMPH>\n" +
                "    <windSpeedMax80mMPH>25</windSpeedMax80mMPH>\n" +
                "    <weatherPrimary>Mostly Sunny</weatherPrimary>\n" +
                "    <windGust80mKPH>41</windGust80mKPH>\n" +
                "    <avgDewpointF>58</avgDewpointF>\n" +
                "    <windSpeedMax80mKPH>41</windSpeedMax80mKPH>\n" +
                "    <windGust80mKTS>22</windGust80mKTS>\n" +
                "    <avgDewpointC>14</avgDewpointC>\n" +
                "    <precipIN>0</precipIN>\n" +
                "    <windSpeedMax80mKTS>22</windSpeedMax80mKTS>\n" +
                "    <windDirMinDEG>353</windDirMinDEG>\n" +
                "    <windSpeedMaxKPH>16</windSpeedMaxKPH>\n" +
                "    <windSpeedMin80mKTS>8</windSpeedMin80mKTS>\n" +
                "    <feelslikeF>70</feelslikeF>\n" +
                "    <validTime>2022-10-06T07:00:00+02:00</validTime>\n" +
                "    <windSpeedMin80mMPH>10</windSpeedMin80mMPH>\n" +
                "    <solradMaxWM2>778</solradMaxWM2>\n" +
                "    <avgTempC>25</avgTempC>\n" +
                "    <windSpeedMin80mKPH>15</windSpeedMin80mKPH>\n" +
                "    <weatherPrimaryCoded>::FW</weatherPrimaryCoded>\n" +
                "    <sunrise>1665028274</sunrise>\n" +
                "    <avgTempF>78</avgTempF>\n" +
                "    <windDirMin>N</windDirMin>\n" +
                "    <maxCoverage/>\n" +
                "    <icon>fair.png</icon>\n" +
                "    <minFeelslikeC>21</minFeelslikeC>\n" +
                "    <dewpointC>17</dewpointC>\n" +
                "    <cloudsCoded>FW</cloudsCoded>\n" +
                "    <minFeelslikeF>70</minFeelslikeF>\n" +
                "    <minHumidity>29</minHumidity>\n" +
                "    <dewpointF>63</dewpointF>\n" +
                "    <windSpeed80mKTS>12</windSpeed80mKTS>\n" +
                "    <pop>0</pop>\n" +
                "    <snowCM>0</snowCM>\n" +
                "    <windDirMax>NNW</windDirMax>\n" +
                "    <windSpeed80mMPH>13</windSpeed80mMPH>\n" +
                "    <windSpeed80mKPH>22</windSpeed80mKPH>\n" +
                "    <windDir80mDEG>11</windDir80mDEG>\n" +
                "    <maxTempC>32</maxTempC>\n" +
                "    <pressureMB>1015</pressureMB>\n" +
                "    <visibilityKM>24.135</visibilityKM>\n" +
                "    <timestamp>1665032400</timestamp>\n" +
                "    <maxTempF>90</maxTempF>\n" +
                "    <tempF>null</tempF>\n" +
                "    <minDewpointC>11</minDewpointC>\n" +
                "    <solradMinWM2>0</solradMinWM2>\n" +
                "    <windSpeedMinKTS>1</windSpeedMinKTS>\n" +
                "    <windDirMax80mDEG>343</windDirMax80mDEG>\n" +
                "    <windGustKTS>21</windGustKTS>\n" +
                "    <windSpeedMinKPH>2</windSpeedMinKPH>\n" +
                "    <maxDewpointF>63</maxDewpointF>\n" +
                "    <windSpeedMinMPH>1</windSpeedMinMPH>\n" +
                "    <avgFeelslikeC>26</avgFeelslikeC>\n" +
                "    <uvi>6</uvi>\n" +
                "    <windDirMax80m>NNW</windDirMax80m>\n" +
                "    <maxDewpointC>17</maxDewpointC>\n" +
                "    <pressureIN>29.97</pressureIN>\n" +
                "    <avgFeelslikeF>80</avgFeelslikeF>\n" +
                "    <iceaccum>null</iceaccum>\n" +
                "    <isDay>true</isDay>\n" +
                "    <minTempC>19</minTempC>\n" +
                "    <minDewpointF>52</minDewpointF>\n" +
                "    <windSpeedKTS>5</windSpeedKTS>\n" +
                "    <sunset>1665070502</sunset>\n" +
                "    <solradWM2>5608</solradWM2>\n" +
                "    <windSpeedKPH>9</windSpeedKPH>\n" +
                "    <windGustMPH>25</windGustMPH>\n" +
                "    <maxHumidity>77</maxHumidity>\n" +
                "    <windSpeedMPH>6</windSpeedMPH>\n" +
                "    <windGustKPH>40</windGustKPH>\n" +
                "    <windDirMaxDEG>342</windDirMaxDEG>\n" +
                "    <tempC>null</tempC>\n" +
                "  </periods>\n" +
                "  <periods>\n" +
                "    <dateTimeISO>2022-10-07T07:00:00+02:00</dateTimeISO>\n" +
                "    <windDirMin80m>NNW</windDirMin80m>\n" +
                "    <windDirMin80mDEG>347</windDirMin80mDEG>\n" +
                "    <feelslikeC>19</feelslikeC>\n" +
                "    <visibilityMI>15</visibilityMI>\n" +
                "    <windSpeedMaxMPH>8</windSpeedMaxMPH>\n" +
                "    <windDirDEG>325</windDirDEG>\n" +
                "    <windDir>NW</windDir>\n" +
                "    <sunriseISO>2022-10-07T05:51:50+02:00</sunriseISO>\n" +
                "    <iceaccumMM>null</iceaccumMM>\n" +
                "    <windSpeedMaxKTS>7</windSpeedMaxKTS>\n" +
                "    <iceaccumIN>null</iceaccumIN>\n" +
                "    <minTempF>66</minTempF>\n" +
                "    <snowIN>0</snowIN>\n" +
                "    <weather>Mostly Sunny</weather>\n" +
                "    <sunsetISO>2022-10-07T17:33:51+02:00</sunsetISO>\n" +
                "    <maxFeelslikeC>29</maxFeelslikeC>\n" +
                "    <humidity>77</humidity>\n" +
                "    <windDir80m>NNW</windDir80m>\n" +
                "    <maxFeelslikeF>85</maxFeelslikeF>\n" +
                "    <precipMM>0</precipMM>\n" +
                "    <sky>30</sky>\n" +
                "    <windGust80mMPH>23</windGust80mMPH>\n" +
                "    <windSpeedMax80mMPH>23</windSpeedMax80mMPH>\n" +
                "    <weatherPrimary>Mostly Sunny</weatherPrimary>\n" +
                "    <windGust80mKPH>37</windGust80mKPH>\n" +
                "    <avgDewpointF>54</avgDewpointF>\n" +
                "    <windSpeedMax80mKPH>37</windSpeedMax80mKPH>\n" +
                "    <windGust80mKTS>20</windGust80mKTS>\n" +
                "    <avgDewpointC>12</avgDewpointC>\n" +
                "    <precipIN>0</precipIN>\n" +
                "    <windSpeedMax80mKTS>20</windSpeedMax80mKTS>\n" +
                "    <windDirMinDEG>325</windDirMinDEG>\n" +
                "    <windSpeedMaxKPH>13</windSpeedMaxKPH>\n" +
                "    <windSpeedMin80mKTS>6</windSpeedMin80mKTS>\n" +
                "    <feelslikeF>67</feelslikeF>\n" +
                "    <validTime>2022-10-07T07:00:00+02:00</validTime>\n" +
                "    <windSpeedMin80mMPH>6</windSpeedMin80mMPH>\n" +
                "    <solradMaxWM2>742</solradMaxWM2>\n" +
                "    <avgTempC>24</avgTempC>\n" +
                "    <windSpeedMin80mKPH>10</windSpeedMin80mKPH>\n" +
                "    <weatherPrimaryCoded>::FW</weatherPrimaryCoded>\n" +
                "    <sunrise>1665114710</sunrise>\n" +
                "    <avgTempF>76</avgTempF>\n" +
                "    <windDirMin>NW</windDirMin>\n" +
                "    <maxCoverage/>\n" +
                "    <icon>fair.png</icon>\n" +
                "    <minFeelslikeC>19</minFeelslikeC>\n" +
                "    <dewpointC>15</dewpointC>\n" +
                "    <cloudsCoded>FW</cloudsCoded>\n" +
                "    <minFeelslikeF>67</minFeelslikeF>\n" +
                "    <minHumidity>30</minHumidity>\n" +
                "    <dewpointF>60</dewpointF>\n" +
                "    <windSpeed80mKTS>6</windSpeed80mKTS>\n" +
                "    <pop>0</pop>\n" +
                "    <snowCM>0</snowCM>\n" +
                "    <windDirMax>WNW</windDirMax>\n" +
                "    <windSpeed80mMPH>6</windSpeed80mMPH>\n" +
                "    <windSpeed80mKPH>10</windSpeed80mKPH>\n" +
                "    <windDir80mDEG>347</windDir80mDEG>\n" +
                "    <maxTempC>30</maxTempC>\n" +
                "    <pressureMB>1014</pressureMB>\n" +
                "    <visibilityKM>24.135</visibilityKM>\n" +
                "    <timestamp>1665118800</timestamp>\n" +
                "    <maxTempF>86</maxTempF>\n" +
                "    <tempF>null</tempF>\n" +
                "    <minDewpointC>10</minDewpointC>\n" +
                "    <solradMinWM2>0</solradMinWM2>\n" +
                "    <windSpeedMinKTS>1</windSpeedMinKTS>\n" +
                "    <windDirMax80mDEG>316</windDirMax80mDEG>\n" +
                "    <windGustKTS>16</windGustKTS>\n" +
                "    <windSpeedMinKPH>2</windSpeedMinKPH>\n" +
                "    <maxDewpointF>60</maxDewpointF>\n" +
                "    <windSpeedMinMPH>1</windSpeedMinMPH>\n" +
                "    <avgFeelslikeC>24</avgFeelslikeC>\n" +
                "    <uvi>6</uvi>\n" +
                "    <windDirMax80m>NW</windDirMax80m>\n" +
                "    <maxDewpointC>15</maxDewpointC>\n" +
                "    <pressureIN>29.95</pressureIN>\n" +
                "    <avgFeelslikeF>76</avgFeelslikeF>\n" +
                "    <iceaccum>null</iceaccum>\n" +
                "    <isDay>true</isDay>\n" +
                "    <minTempC>19</minTempC>\n" +
                "    <minDewpointF>50</minDewpointF>\n" +
                "    <windSpeedKTS>1</windSpeedKTS>\n" +
                "    <sunset>1665156831</sunset>\n" +
                "    <solradWM2>5486</solradWM2>\n" +
                "    <windSpeedKPH>2</windSpeedKPH>\n" +
                "    <windGustMPH>18</windGustMPH>\n" +
                "    <maxHumidity>77</maxHumidity>\n" +
                "    <windSpeedMPH>1</windSpeedMPH>\n" +
                "    <windGustKPH>29</windGustKPH>\n" +
                "    <windDirMaxDEG>298</windDirMaxDEG>\n" +
                "    <tempC>null</tempC>\n" +
                "  </periods>\n" +
                "  <periods>\n" +
                "    <dateTimeISO>2022-10-08T07:00:00+02:00</dateTimeISO>\n" +
                "    <windDirMin80m>NW</windDirMin80m>\n" +
                "    <windDirMin80mDEG>309</windDirMin80mDEG>\n" +
                "    <feelslikeC>19</feelslikeC>\n" +
                "    <visibilityMI>15</visibilityMI>\n" +
                "    <windSpeedMaxMPH>8</windSpeedMaxMPH>\n" +
                "    <windDirDEG>21</windDirDEG>\n" +
                "    <windDir>NNE</windDir>\n" +
                "    <sunriseISO>2022-10-08T05:52:26+02:00</sunriseISO>\n" +
                "    <iceaccumMM>null</iceaccumMM>\n" +
                "    <windSpeedMaxKTS>7</windSpeedMaxKTS>\n" +
                "    <iceaccumIN>null</iceaccumIN>\n" +
                "    <minTempF>66</minTempF>\n" +
                "    <snowIN>0</snowIN>\n" +
                "    <weather>Partly Cloudy</weather>\n" +
                "    <sunsetISO>2022-10-08T17:32:41+02:00</sunsetISO>\n" +
                "    <maxFeelslikeC>30</maxFeelslikeC>\n" +
                "    <humidity>76</humidity>\n" +
                "    <windDir80m>NW</windDir80m>\n" +
                "    <maxFeelslikeF>86</maxFeelslikeF>\n" +
                "    <precipMM>0</precipMM>\n" +
                "    <sky>47</sky>\n" +
                "    <windGust80mMPH>19</windGust80mMPH>\n" +
                "    <windSpeedMax80mMPH>19</windSpeedMax80mMPH>\n" +
                "    <weatherPrimary>Partly Cloudy</weatherPrimary>\n" +
                "    <windGust80mKPH>31</windGust80mKPH>\n" +
                "    <avgDewpointF>56</avgDewpointF>\n" +
                "    <windSpeedMax80mKPH>31</windSpeedMax80mKPH>\n" +
                "    <windGust80mKTS>17</windGust80mKTS>\n" +
                "    <avgDewpointC>13</avgDewpointC>\n" +
                "    <precipIN>0</precipIN>\n" +
                "    <windSpeedMax80mKTS>17</windSpeedMax80mKTS>\n" +
                "    <windDirMinDEG>21</windDirMinDEG>\n" +
                "    <windSpeedMaxKPH>13</windSpeedMaxKPH>\n" +
                "    <windSpeedMin80mKTS>5</windSpeedMin80mKTS>\n" +
                "    <feelslikeF>67</feelslikeF>\n" +
                "    <validTime>2022-10-08T07:00:00+02:00</validTime>\n" +
                "    <windSpeedMin80mMPH>5</windSpeedMin80mMPH>\n" +
                "    <solradMaxWM2>682</solradMaxWM2>\n" +
                "    <avgTempC>25</avgTempC>\n" +
                "    <windSpeedMin80mKPH>9</windSpeedMin80mKPH>\n" +
                "    <weatherPrimaryCoded>::SC</weatherPrimaryCoded>\n" +
                "    <sunrise>1665201146</sunrise>\n" +
                "    <avgTempF>76</avgTempF>\n" +
                "    <windDirMin>NNE</windDirMin>\n" +
                "    <maxCoverage/>\n" +
                "    <icon>pcloudy.png</icon>\n" +
                "    <minFeelslikeC>19</minFeelslikeC>\n" +
                "    <dewpointC>15</dewpointC>\n" +
                "    <cloudsCoded>SC</cloudsCoded>\n" +
                "    <minFeelslikeF>67</minFeelslikeF>\n" +
                "    <minHumidity>32</minHumidity>\n" +
                "    <dewpointF>59</dewpointF>\n" +
                "    <windSpeed80mKTS>5</windSpeed80mKTS>\n" +
                "    <pop>0</pop>\n" +
                "    <snowCM>0</snowCM>\n" +
                "    <windDirMax>WNW</windDirMax>\n" +
                "    <windSpeed80mMPH>5</windSpeed80mMPH>\n" +
                "    <windSpeed80mKPH>9</windSpeed80mKPH>\n" +
                "    <windDir80mDEG>309</windDir80mDEG>\n" +
                "    <maxTempC>30</maxTempC>\n" +
                "    <pressureMB>1014</pressureMB>\n" +
                "    <visibilityKM>24.135</visibilityKM>\n" +
                "    <timestamp>1665205200</timestamp>\n" +
                "    <maxTempF>87</maxTempF>\n" +
                "    <tempF>null</tempF>\n" +
                "    <minDewpointC>11</minDewpointC>\n" +
                "    <solradMinWM2>0</solradMinWM2>\n" +
                "    <windSpeedMinKTS>1</windSpeedMinKTS>\n" +
                "    <windDirMax80mDEG>322</windDirMax80mDEG>\n" +
                "    <windGustKTS>17</windGustKTS>\n" +
                "    <windSpeedMinKPH>2</windSpeedMinKPH>\n" +
                "    <maxDewpointF>59</maxDewpointF>\n" +
                "    <windSpeedMinMPH>1</windSpeedMinMPH>\n" +
                "    <avgFeelslikeC>25</avgFeelslikeC>\n" +
                "    <uvi>7</uvi>\n" +
                "    <windDirMax80m>NW</windDirMax80m>\n" +
                "    <maxDewpointC>15</maxDewpointC>\n" +
                "    <pressureIN>29.94</pressureIN>\n" +
                "    <avgFeelslikeF>76</avgFeelslikeF>\n" +
                "    <iceaccum>null</iceaccum>\n" +
                "    <isDay>true</isDay>\n" +
                "    <minTempC>19</minTempC>\n" +
                "    <minDewpointF>52</minDewpointF>\n" +
                "    <windSpeedKTS>1</windSpeedKTS>\n" +
                "    <sunset>1665243161</sunset>\n" +
                "    <solradWM2>4785</solradWM2>\n" +
                "    <windSpeedKPH>2</windSpeedKPH>\n" +
                "    <windGustMPH>20</windGustMPH>\n" +
                "    <maxHumidity>76</maxHumidity>\n" +
                "    <windSpeedMPH>1</windSpeedMPH>\n" +
                "    <windGustKPH>32</windGustKPH>\n" +
                "    <windDirMaxDEG>301</windDirMaxDEG>\n" +
                "    <tempC>null</tempC>\n" +
                "  </periods>\n" +
                "  <periods>\n" +
                "    <dateTimeISO>2022-10-09T07:00:00+02:00</dateTimeISO>\n" +
                "    <windDirMin80m>NW</windDirMin80m>\n" +
                "    <windDirMin80mDEG>316</windDirMin80mDEG>\n" +
                "    <feelslikeC>20</feelslikeC>\n" +
                "    <visibilityMI>15</visibilityMI>\n" +
                "    <windSpeedMaxMPH>9</windSpeedMaxMPH>\n" +
                "    <windDirDEG>356</windDirDEG>\n" +
                "    <windDir>N</windDir>\n" +
                "    <sunriseISO>2022-10-09T05:53:03+02:00</sunriseISO>\n" +
                "    <iceaccumMM>null</iceaccumMM>\n" +
                "    <windSpeedMaxKTS>8</windSpeedMaxKTS>\n" +
                "    <iceaccumIN>null</iceaccumIN>\n" +
                "    <minTempF>67</minTempF>\n" +
                "    <snowIN>0</snowIN>\n" +
                "    <weather>Partly Cloudy</weather>\n" +
                "    <sunsetISO>2022-10-09T17:31:31+02:00</sunsetISO>\n" +
                "    <maxFeelslikeC>30</maxFeelslikeC>\n" +
                "    <humidity>86</humidity>\n" +
                "    <windDir80m>NW</windDir80m>\n" +
                "    <maxFeelslikeF>86</maxFeelslikeF>\n" +
                "    <precipMM>0</precipMM>\n" +
                "    <sky>47</sky>\n" +
                "    <windGust80mMPH>23</windGust80mMPH>\n" +
                "    <windSpeedMax80mMPH>23</windSpeedMax80mMPH>\n" +
                "    <weatherPrimary>Partly Cloudy</weatherPrimary>\n" +
                "    <windGust80mKPH>36</windGust80mKPH>\n" +
                "    <avgDewpointF>57</avgDewpointF>\n" +
                "    <windSpeedMax80mKPH>36</windSpeedMax80mKPH>\n" +
                "    <windGust80mKTS>20</windGust80mKTS>\n" +
                "    <avgDewpointC>14</avgDewpointC>\n" +
                "    <precipIN>0</precipIN>\n" +
                "    <windSpeedMax80mKTS>20</windSpeedMax80mKTS>\n" +
                "    <windDirMinDEG>356</windDirMinDEG>\n" +
                "    <windSpeedMaxKPH>14</windSpeedMaxKPH>\n" +
                "    <windSpeedMin80mKTS>5</windSpeedMin80mKTS>\n" +
                "    <feelslikeF>67</feelslikeF>\n" +
                "    <validTime>2022-10-09T07:00:00+02:00</validTime>\n" +
                "    <windSpeedMin80mMPH>6</windSpeedMin80mMPH>\n" +
                "    <solradMaxWM2>726</solradMaxWM2>\n" +
                "    <avgTempC>25</avgTempC>\n" +
                "    <windSpeedMin80mKPH>9</windSpeedMin80mKPH>\n" +
                "    <weatherPrimaryCoded>::SC</weatherPrimaryCoded>\n" +
                "    <sunrise>1665287583</sunrise>\n" +
                "    <avgTempF>77</avgTempF>\n" +
                "    <windDirMin>N</windDirMin>\n" +
                "    <maxCoverage/>\n" +
                "    <icon>pcloudy.png</icon>\n" +
                "    <minFeelslikeC>20</minFeelslikeC>\n" +
                "    <dewpointC>17</dewpointC>\n" +
                "    <cloudsCoded>SC</cloudsCoded>\n" +
                "    <minFeelslikeF>67</minFeelslikeF>\n" +
                "    <minHumidity>31</minHumidity>\n" +
                "    <dewpointF>63</dewpointF>\n" +
                "    <windSpeed80mKTS>5</windSpeed80mKTS>\n" +
                "    <pop>0</pop>\n" +
                "    <snowCM>0</snowCM>\n" +
                "    <windDirMax>NNW</windDirMax>\n" +
                "    <windSpeed80mMPH>6</windSpeed80mMPH>\n" +
                "    <windSpeed80mKPH>9</windSpeed80mKPH>\n" +
                "    <windDir80mDEG>316</windDir80mDEG>\n" +
                "    <maxTempC>31</maxTempC>\n" +
                "    <pressureMB>1016</pressureMB>\n" +
                "    <visibilityKM>24.135</visibilityKM>\n" +
                "    <timestamp>1665291600</timestamp>\n" +
                "    <maxTempF>87</maxTempF>\n" +
                "    <tempF>null</tempF>\n" +
                "    <minDewpointC>11</minDewpointC>\n" +
                "    <solradMinWM2>0</solradMinWM2>\n" +
                "    <windSpeedMinKTS>2</windSpeedMinKTS>\n" +
                "    <windDirMax80mDEG>354</windDirMax80mDEG>\n" +
                "    <windGustKTS>19</windGustKTS>\n" +
                "    <windSpeedMinKPH>4</windSpeedMinKPH>\n" +
                "    <maxDewpointF>63</maxDewpointF>\n" +
                "    <windSpeedMinMPH>2</windSpeedMinMPH>\n" +
                "    <avgFeelslikeC>25</avgFeelslikeC>\n" +
                "    <uvi>7</uvi>\n" +
                "    <windDirMax80m>N</windDirMax80m>\n" +
                "    <maxDewpointC>17</maxDewpointC>\n" +
                "    <pressureIN>29.99</pressureIN>\n" +
                "    <avgFeelslikeF>77</avgFeelslikeF>\n" +
                "    <iceaccum>null</iceaccum>\n" +
                "    <isDay>true</isDay>\n" +
                "    <minTempC>19</minTempC>\n" +
                "    <minDewpointF>52</minDewpointF>\n" +
                "    <windSpeedKTS>2</windSpeedKTS>\n" +
                "    <sunset>1665329491</sunset>\n" +
                "    <solradWM2>4768</solradWM2>\n" +
                "    <windSpeedKPH>4</windSpeedKPH>\n" +
                "    <windGustMPH>22</windGustMPH>\n" +
                "    <maxHumidity>86</maxHumidity>\n" +
                "    <windSpeedMPH>2</windSpeedMPH>\n" +
                "    <windGustKPH>36</windGustKPH>\n" +
                "    <windDirMaxDEG>343</windDirMaxDEG>\n" +
                "    <tempC>null</tempC>\n" +
                "  </periods>\n" +
                "  <periods>\n" +
                "    <dateTimeISO>2022-10-10T07:00:00+02:00</dateTimeISO>\n" +
                "    <windDirMin80m>E</windDirMin80m>\n" +
                "    <windDirMin80mDEG>91</windDirMin80mDEG>\n" +
                "    <feelslikeC>21</feelslikeC>\n" +
                "    <visibilityMI>15</visibilityMI>\n" +
                "    <windSpeedMaxMPH>9</windSpeedMaxMPH>\n" +
                "    <windDirDEG>358</windDirDEG>\n" +
                "    <windDir>N</windDir>\n" +
                "    <sunriseISO>2022-10-10T05:53:40+02:00</sunriseISO>\n" +
                "    <iceaccumMM>null</iceaccumMM>\n" +
                "    <windSpeedMaxKTS>8</windSpeedMaxKTS>\n" +
                "    <iceaccumIN>null</iceaccumIN>\n" +
                "    <minTempF>70</minTempF>\n" +
                "    <snowIN>0</snowIN>\n" +
                "    <weather>Partly Cloudy</weather>\n" +
                "    <sunsetISO>2022-10-10T17:30:21+02:00</sunsetISO>\n" +
                "    <maxFeelslikeC>30</maxFeelslikeC>\n" +
                "    <humidity>75</humidity>\n" +
                "    <windDir80m>N</windDir80m>\n" +
                "    <maxFeelslikeF>86</maxFeelslikeF>\n" +
                "    <precipMM>0</precipMM>\n" +
                "    <sky>64</sky>\n" +
                "    <windGust80mMPH>22</windGust80mMPH>\n" +
                "    <windSpeedMax80mMPH>22</windSpeedMax80mMPH>\n" +
                "    <weatherPrimary>Partly Cloudy</weatherPrimary>\n" +
                "    <windGust80mKPH>36</windGust80mKPH>\n" +
                "    <avgDewpointF>58</avgDewpointF>\n" +
                "    <windSpeedMax80mKPH>36</windSpeedMax80mKPH>\n" +
                "    <windGust80mKTS>19</windGust80mKTS>\n" +
                "    <avgDewpointC>14</avgDewpointC>\n" +
                "    <precipIN>0</precipIN>\n" +
                "    <windSpeedMax80mKTS>19</windSpeedMax80mKTS>\n" +
                "    <windDirMinDEG>358</windDirMinDEG>\n" +
                "    <windSpeedMaxKPH>15</windSpeedMaxKPH>\n" +
                "    <windSpeedMin80mKTS>7</windSpeedMin80mKTS>\n" +
                "    <feelslikeF>69</feelslikeF>\n" +
                "    <validTime>2022-10-10T07:00:00+02:00</validTime>\n" +
                "    <windSpeedMin80mMPH>8</windSpeedMin80mMPH>\n" +
                "    <solradMaxWM2>597</solradMaxWM2>\n" +
                "    <avgTempC>26</avgTempC>\n" +
                "    <windSpeedMin80mKPH>13</windSpeedMin80mKPH>\n" +
                "    <weatherPrimaryCoded>::SC</weatherPrimaryCoded>\n" +
                "    <sunrise>1665374020</sunrise>\n" +
                "    <avgTempF>78</avgTempF>\n" +
                "    <windDirMin>N</windDirMin>\n" +
                "    <maxCoverage/>\n" +
                "    <icon>pcloudy.png</icon>\n" +
                "    <minFeelslikeC>21</minFeelslikeC>\n" +
                "    <dewpointC>16</dewpointC>\n" +
                "    <cloudsCoded>SC</cloudsCoded>\n" +
                "    <minFeelslikeF>69</minFeelslikeF>\n" +
                "    <minHumidity>35</minHumidity>\n" +
                "    <dewpointF>61</dewpointF>\n" +
                "    <windSpeed80mKTS>7</windSpeed80mKTS>\n" +
                "    <pop>0</pop>\n" +
                "    <snowCM>0</snowCM>\n" +
                "    <windDirMax>N</windDirMax>\n" +
                "    <windSpeed80mMPH>8</windSpeed80mMPH>\n" +
                "    <windSpeed80mKPH>13</windSpeed80mKPH>\n" +
                "    <windDir80mDEG>8</windDir80mDEG>\n" +
                "    <maxTempC>31</maxTempC>\n" +
                "    <pressureMB>1017</pressureMB>\n" +
                "    <visibilityKM>24.135</visibilityKM>\n" +
                "    <timestamp>1665378000</timestamp>\n" +
                "    <maxTempF>87</maxTempF>\n" +
                "    <tempF>null</tempF>\n" +
                "    <minDewpointC>13</minDewpointC>\n" +
                "    <solradMinWM2>0</solradMinWM2>\n" +
                "    <windSpeedMinKTS>2</windSpeedMinKTS>\n" +
                "    <windDirMax80mDEG>10</windDirMax80mDEG>\n" +
                "    <windGustKTS>16</windGustKTS>\n" +
                "    <windSpeedMinKPH>4</windSpeedMinKPH>\n" +
                "    <maxDewpointF>61</maxDewpointF>\n" +
                "    <windSpeedMinMPH>2</windSpeedMinMPH>\n" +
                "    <avgFeelslikeC>25</avgFeelslikeC>\n" +
                "    <uvi>6</uvi>\n" +
                "    <windDirMax80m>N</windDirMax80m>\n" +
                "    <maxDewpointC>16</maxDewpointC>\n" +
                "    <pressureIN>30.03</pressureIN>\n" +
                "    <avgFeelslikeF>78</avgFeelslikeF>\n" +
                "    <iceaccum>null</iceaccum>\n" +
                "    <isDay>true</isDay>\n" +
                "    <minTempC>21</minTempC>\n" +
                "    <minDewpointF>55</minDewpointF>\n" +
                "    <windSpeedKTS>2</windSpeedKTS>\n" +
                "    <sunset>1665415821</sunset>\n" +
                "    <solradWM2>4494</solradWM2>\n" +
                "    <windSpeedKPH>4</windSpeedKPH>\n" +
                "    <windGustMPH>19</windGustMPH>\n" +
                "    <maxHumidity>75</maxHumidity>\n" +
                "    <windSpeedMPH>2</windSpeedMPH>\n" +
                "    <windGustKPH>30</windGustKPH>\n" +
                "    <windDirMaxDEG>10</windDirMaxDEG>\n" +
                "    <tempC>null</tempC>\n" +
                "  </periods>\n" +
                "  <periods>\n" +
                "    <dateTimeISO>2022-10-11T07:00:00+02:00</dateTimeISO>\n" +
                "    <windDirMin80m>NNE</windDirMin80m>\n" +
                "    <windDirMin80mDEG>13</windDirMin80mDEG>\n" +
                "    <feelslikeC>22</feelslikeC>\n" +
                "    <visibilityMI>15</visibilityMI>\n" +
                "    <windSpeedMaxMPH>18</windSpeedMaxMPH>\n" +
                "    <windDirDEG>13</windDirDEG>\n" +
                "    <windDir>NNE</windDir>\n" +
                "    <sunriseISO>2022-10-11T05:54:18+02:00</sunriseISO>\n" +
                "    <iceaccumMM>null</iceaccumMM>\n" +
                "    <windSpeedMaxKTS>15</windSpeedMaxKTS>\n" +
                "    <iceaccumIN>null</iceaccumIN>\n" +
                "    <minTempF>70</minTempF>\n" +
                "    <snowIN>0</snowIN>\n" +
                "    <weather>Sunny</weather>\n" +
                "    <sunsetISO>2022-10-11T17:29:13+02:00</sunsetISO>\n" +
                "    <maxFeelslikeC>31</maxFeelslikeC>\n" +
                "    <humidity>71</humidity>\n" +
                "    <windDir80m>NNE</windDir80m>\n" +
                "    <maxFeelslikeF>87</maxFeelslikeF>\n" +
                "    <precipMM>0</precipMM>\n" +
                "    <sky>0</sky>\n" +
                "    <windGust80mMPH>19</windGust80mMPH>\n" +
                "    <windSpeedMax80mMPH>19</windSpeedMax80mMPH>\n" +
                "    <weatherPrimary>Sunny</weatherPrimary>\n" +
                "    <windGust80mKPH>31</windGust80mKPH>\n" +
                "    <avgDewpointF>55</avgDewpointF>\n" +
                "    <windSpeedMax80mKPH>31</windSpeedMax80mKPH>\n" +
                "    <windGust80mKTS>17</windGust80mKTS>\n" +
                "    <avgDewpointC>13</avgDewpointC>\n" +
                "    <precipIN>0</precipIN>\n" +
                "    <windSpeedMax80mKTS>17</windSpeedMax80mKTS>\n" +
                "    <windDirMinDEG>14</windDirMinDEG>\n" +
                "    <windSpeedMaxKPH>28</windSpeedMaxKPH>\n" +
                "    <windSpeedMin80mKTS>9</windSpeedMin80mKTS>\n" +
                "    <feelslikeF>72</feelslikeF>\n" +
                "    <validTime>2022-10-11T07:00:00+02:00</validTime>\n" +
                "    <windSpeedMin80mMPH>11</windSpeedMin80mMPH>\n" +
                "    <solradMaxWM2>758</solradMaxWM2>\n" +
                "    <avgTempC>26</avgTempC>\n" +
                "    <windSpeedMin80mKPH>18</windSpeedMin80mKPH>\n" +
                "    <weatherPrimaryCoded>::CL</weatherPrimaryCoded>\n" +
                "    <sunrise>1665460458</sunrise>\n" +
                "    <avgTempF>78</avgTempF>\n" +
                "    <windDirMin>NNE</windDirMin>\n" +
                "    <maxCoverage/>\n" +
                "    <icon>sunny.png</icon>\n" +
                "    <minFeelslikeC>22</minFeelslikeC>\n" +
                "    <dewpointC>17</dewpointC>\n" +
                "    <cloudsCoded>CL</cloudsCoded>\n" +
                "    <minFeelslikeF>72</minFeelslikeF>\n" +
                "    <minHumidity>30</minHumidity>\n" +
                "    <dewpointF>62</dewpointF>\n" +
                "    <windSpeed80mKTS>10</windSpeed80mKTS>\n" +
                "    <pop>0</pop>\n" +
                "    <snowCM>0</snowCM>\n" +
                "    <windDirMax>NNE</windDirMax>\n" +
                "    <windSpeed80mMPH>12</windSpeed80mMPH>\n" +
                "    <windSpeed80mKPH>19</windSpeed80mKPH>\n" +
                "    <windDir80mDEG>16</windDir80mDEG>\n" +
                "    <maxTempC>31</maxTempC>\n" +
                "    <pressureMB>1015</pressureMB>\n" +
                "    <visibilityKM>24.135</visibilityKM>\n" +
                "    <timestamp>1665464400</timestamp>\n" +
                "    <maxTempF>87</maxTempF>\n" +
                "    <tempF>null</tempF>\n" +
                "    <minDewpointC>11</minDewpointC>\n" +
                "    <solradMinWM2>0</solradMinWM2>\n" +
                "    <windSpeedMinKTS>7</windSpeedMinKTS>\n" +
                "    <windDirMax80mDEG>28</windDirMax80mDEG>\n" +
                "    <windGustKTS>15</windGustKTS>\n" +
                "    <windSpeedMinKPH>14</windSpeedMinKPH>\n" +
                "    <maxDewpointF>62</maxDewpointF>\n" +
                "    <windSpeedMinMPH>8</windSpeedMinMPH>\n" +
                "    <avgFeelslikeC>26</avgFeelslikeC>\n" +
                "    <uvi>null</uvi>\n" +
                "    <windDirMax80m>NNE</windDirMax80m>\n" +
                "    <maxDewpointC>17</maxDewpointC>\n" +
                "    <pressureIN>29.98</pressureIN>\n" +
                "    <avgFeelslikeF>79</avgFeelslikeF>\n" +
                "    <iceaccum>null</iceaccum>\n" +
                "    <isDay>true</isDay>\n" +
                "    <minTempC>21</minTempC>\n" +
                "    <minDewpointF>51</minDewpointF>\n" +
                "    <windSpeedKTS>8</windSpeedKTS>\n" +
                "    <sunset>1665502153</sunset>\n" +
                "    <solradWM2>5450</solradWM2>\n" +
                "    <windSpeedKPH>15</windSpeedKPH>\n" +
                "    <windGustMPH>17</windGustMPH>\n" +
                "    <maxHumidity>71</maxHumidity>\n" +
                "    <windSpeedMPH>9</windSpeedMPH>\n" +
                "    <windGustKPH>28</windGustKPH>\n" +
                "    <windDirMaxDEG>28</windDirMaxDEG>\n" +
                "    <tempC>null</tempC>\n" +
                "  </periods>\n" +
                "  <periods>\n" +
                "    <dateTimeISO>2022-10-12T07:00:00+02:00</dateTimeISO>\n" +
                "    <windDirMin80m>NNE</windDirMin80m>\n" +
                "    <windDirMin80mDEG>15</windDirMin80mDEG>\n" +
                "    <feelslikeC>22</feelslikeC>\n" +
                "    <visibilityMI>15</visibilityMI>\n" +
                "    <windSpeedMaxMPH>16</windSpeedMaxMPH>\n" +
                "    <windDirDEG>12</windDirDEG>\n" +
                "    <windDir>NNE</windDir>\n" +
                "    <sunriseISO>2022-10-12T05:54:55+02:00</sunriseISO>\n" +
                "    <iceaccumMM>null</iceaccumMM>\n" +
                "    <windSpeedMaxKTS>14</windSpeedMaxKTS>\n" +
                "    <iceaccumIN>null</iceaccumIN>\n" +
                "    <minTempF>69</minTempF>\n" +
                "    <snowIN>0</snowIN>\n" +
                "    <weather>Mostly Sunny</weather>\n" +
                "    <sunsetISO>2022-10-12T17:28:04+02:00</sunsetISO>\n" +
                "    <maxFeelslikeC>31</maxFeelslikeC>\n" +
                "    <humidity>68</humidity>\n" +
                "    <windDir80m>NNE</windDir80m>\n" +
                "    <maxFeelslikeF>88</maxFeelslikeF>\n" +
                "    <precipMM>0</precipMM>\n" +
                "    <sky>27</sky>\n" +
                "    <windGust80mMPH>21</windGust80mMPH>\n" +
                "    <windSpeedMax80mMPH>21</windSpeedMax80mMPH>\n" +
                "    <weatherPrimary>Mostly Sunny</weatherPrimary>\n" +
                "    <windGust80mKPH>33</windGust80mKPH>\n" +
                "    <avgDewpointF>55</avgDewpointF>\n" +
                "    <windSpeedMax80mKPH>33</windSpeedMax80mKPH>\n" +
                "    <windGust80mKTS>18</windGust80mKTS>\n" +
                "    <avgDewpointC>13</avgDewpointC>\n" +
                "    <precipIN>0</precipIN>\n" +
                "    <windSpeedMax80mKTS>18</windSpeedMax80mKTS>\n" +
                "    <windDirMinDEG>12</windDirMinDEG>\n" +
                "    <windSpeedMaxKPH>26</windSpeedMaxKPH>\n" +
                "    <windSpeedMin80mKTS>10</windSpeedMin80mKTS>\n" +
                "    <feelslikeF>72</feelslikeF>\n" +
                "    <validTime>2022-10-12T07:00:00+02:00</validTime>\n" +
                "    <windSpeedMin80mMPH>11</windSpeedMin80mMPH>\n" +
                "    <solradMaxWM2>743</solradMaxWM2>\n" +
                "    <avgTempC>26</avgTempC>\n" +
                "    <windSpeedMin80mKPH>18</windSpeedMin80mKPH>\n" +
                "    <weatherPrimaryCoded>::FW</weatherPrimaryCoded>\n" +
                "    <sunrise>1665546895</sunrise>\n" +
                "    <avgTempF>79</avgTempF>\n" +
                "    <windDirMin>NNE</windDirMin>\n" +
                "    <maxCoverage/>\n" +
                "    <icon>fair.png</icon>\n" +
                "    <minFeelslikeC>22</minFeelslikeC>\n" +
                "    <dewpointC>16</dewpointC>\n" +
                "    <cloudsCoded>FW</cloudsCoded>\n" +
                "    <minFeelslikeF>72</minFeelslikeF>\n" +
                "    <minHumidity>29</minHumidity>\n" +
                "    <dewpointF>60</dewpointF>\n" +
                "    <windSpeed80mKTS>10</windSpeed80mKTS>\n" +
                "    <pop>0</pop>\n" +
                "    <snowCM>0</snowCM>\n" +
                "    <windDirMax>E</windDirMax>\n" +
                "    <windSpeed80mMPH>12</windSpeed80mMPH>\n" +
                "    <windSpeed80mKPH>19</windSpeed80mKPH>\n" +
                "    <windDir80mDEG>15</windDir80mDEG>\n" +
                "    <maxTempC>31</maxTempC>\n" +
                "    <pressureMB>1014</pressureMB>\n" +
                "    <visibilityKM>24.135</visibilityKM>\n" +
                "    <timestamp>1665550800</timestamp>\n" +
                "    <maxTempF>88</maxTempF>\n" +
                "    <tempF>null</tempF>\n" +
                "    <minDewpointC>11</minDewpointC>\n" +
                "    <solradMinWM2>0</solradMinWM2>\n" +
                "    <windSpeedMinKTS>7</windSpeedMinKTS>\n" +
                "    <windDirMax80mDEG>96</windDirMax80mDEG>\n" +
                "    <windGustKTS>15</windGustKTS>\n" +
                "    <windSpeedMinKPH>13</windSpeedMinKPH>\n" +
                "    <maxDewpointF>60</maxDewpointF>\n" +
                "    <windSpeedMinMPH>8</windSpeedMinMPH>\n" +
                "    <avgFeelslikeC>26</avgFeelslikeC>\n" +
                "    <uvi>null</uvi>\n" +
                "    <windDirMax80m>E</windDirMax80m>\n" +
                "    <maxDewpointC>16</maxDewpointC>\n" +
                "    <pressureIN>29.95</pressureIN>\n" +
                "    <avgFeelslikeF>80</avgFeelslikeF>\n" +
                "    <iceaccum>null</iceaccum>\n" +
                "    <isDay>true</isDay>\n" +
                "    <minTempC>21</minTempC>\n" +
                "    <minDewpointF>51</minDewpointF>\n" +
                "    <windSpeedKTS>8</windSpeedKTS>\n" +
                "    <sunset>1665588484</sunset>\n" +
                "    <solradWM2>4740</solradWM2>\n" +
                "    <windSpeedKPH>15</windSpeedKPH>\n" +
                "    <windGustMPH>17</windGustMPH>\n" +
                "    <maxHumidity>68</maxHumidity>\n" +
                "    <windSpeedMPH>9</windSpeedMPH>\n" +
                "    <windGustKPH>28</windGustKPH>\n" +
                "    <windDirMaxDEG>96</windDirMaxDEG>\n" +
                "    <tempC>null</tempC>\n" +
                "  </periods>\n" +
                "  <interval>day</interval>\n" +
                "  <place>\n" +
                "    <country>eg</country>\n" +
                "    <name>cairo</name>\n" +
                "    <state>qh</state>\n" +
                "  </place>\n" +
                "</response>\n" +
                "<error>null</error>\n";
        assertEquals(actual, expected);
    }
}



