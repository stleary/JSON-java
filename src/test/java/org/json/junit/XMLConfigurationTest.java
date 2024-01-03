package org.json.junit;

/*
Public Domain.
*/

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.json.XMLParserConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests for JSON-Java XML.java with XMLParserConfiguration.java
 */
public class XMLConfigurationTest {
    /**
     * JUnit supports temporary files and folders that are cleaned up after the test.
     * https://garygregory.wordpress.com/2010/01/20/junit-tip-use-rules-to-manage-temporary-files-and-folders/ 
     */
    @TempDir
    public File testFolder;

    /**
     * JSONObject from a null XML string.
     * Expects a NullPointerException
     */
    @Test
    void shouldHandleNullXML() {
        assertThrows(NullPointerException.class, () -> {
            String xmlStr = null;
            JSONObject jsonObject =
                    XML.toJSONObject(xmlStr, XMLParserConfiguration.KEEP_STRINGS);
            assertTrue(jsonObject.isEmpty(), "jsonObject should be empty");
        });
    }

    /**
     * Empty JSONObject from an empty XML string.
     */
    @Test
    void shouldHandleEmptyXML() {

        String xmlStr = "";
        JSONObject jsonObject = 
                XML.toJSONObject(xmlStr, XMLParserConfiguration.KEEP_STRINGS);
        assertTrue(jsonObject.isEmpty(), "jsonObject should be empty");
    }

    /**
     * Empty JSONObject from a non-XML string.
     */
    @Test
    void shouldHandleNonXML() {
        String xmlStr = "{ \"this is\": \"not xml\"}";
        JSONObject jsonObject = 
                XML.toJSONObject(xmlStr, XMLParserConfiguration.KEEP_STRINGS);
        assertTrue(jsonObject.isEmpty(), "xml string should be empty");
    }

    /**
     * Invalid XML string (tag contains a frontslash).
     * Expects a JSONException
     */
    @Test
    void shouldHandleInvalidSlashInTag() {
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
            XML.toJSONObject(xmlStr, XMLParserConfiguration.KEEP_STRINGS);
            fail("Expecting a JSONException");
        } catch (JSONException e) {
            assertEquals("Misshaped tag at 176 [character 14 line 4]",
                    e.getMessage(),
                    "Expecting an exception message");
        }
    }

    /**
     * Invalid XML string ('!' char in tag)
     * Expects a JSONException
     */
    @Test
    void shouldHandleInvalidBangInTag() {
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
            XML.toJSONObject(xmlStr, XMLParserConfiguration.KEEP_STRINGS);
            fail("Expecting a JSONException");
        } catch (JSONException e) {
            assertEquals("Misshaped meta tag at 214 [character 12 line 7]",
                    e.getMessage(),
                    "Expecting an exception message");
        }
    }

    /**
     * Invalid XML string ('!' char and no closing tag brace)
     * Expects a JSONException
     */
    @Test
    void shouldHandleInvalidBangNoCloseInTag() {
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
            XML.toJSONObject(xmlStr, XMLParserConfiguration.KEEP_STRINGS);
            fail("Expecting a JSONException");
        } catch (JSONException e) {
            assertEquals("Misshaped meta tag at 213 [character 12 line 7]",
                    e.getMessage(),
                    "Expecting an exception message");
        }
    }

    /**
     * Invalid XML string (no end brace for tag)
     * Expects JSONException
     */
    @Test
    void shouldHandleNoCloseStartTag() {
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
            XML.toJSONObject(xmlStr, XMLParserConfiguration.KEEP_STRINGS);
            fail("Expecting a JSONException");
        } catch (JSONException e) {
            assertEquals("Misplaced '<' at 193 [character 4 line 6]",
                    e.getMessage(),
                    "Expecting an exception message");
        }
    }

    /**
     * Invalid XML string (partial CDATA chars in tag name)
     * Expects JSONException
     */
    @Test
    void shouldHandleInvalidCDATABangInTag() {
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
            XMLParserConfiguration config = 
                    new XMLParserConfiguration().withcDataTagName("altContent");
            XML.toJSONObject(xmlStr, config);
            fail("Expecting a JSONException");
        } catch (JSONException e) {
            assertEquals("Expected 'CDATA[' at 204 [character 11 line 5]",
                    e.getMessage(),
                    "Expecting an exception message");
        }
    }

    /**
     * Null JSONObject in XML.toString()
     */
    @Test
    void shouldHandleNullJSONXML() {
        JSONObject jsonObject= null;
        String actualXml = XML.toString(jsonObject, null,
                XMLParserConfiguration.KEEP_STRINGS);
        assertEquals("\"null\"",actualXml,"generated XML does not equal expected XML");
    }

    /**
     * Empty JSONObject in XML.toString()
     */
    @Test
    void shouldHandleEmptyJSONXML() {
        JSONObject jsonObject= new JSONObject();
        String xmlStr = XML.toString(jsonObject, null,
                XMLParserConfiguration.KEEP_STRINGS);
        assertTrue(xmlStr.isEmpty(), "xml string should be empty");
    }

    /**
     * No SML start tag. The ending tag ends up being treated as content.
     */
    @Test
    void shouldHandleNoStartTag() {
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
        JSONObject jsonObject = XML.toJSONObject(xmlStr,
                XMLParserConfiguration.KEEP_STRINGS);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    /**
     * Valid XML to JSONObject
     */
    @Test
    void shouldHandleSimpleXML() {
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

        XMLParserConfiguration config =
                new XMLParserConfiguration().withcDataTagName("altContent");
        compareStringToJSONObject(xmlStr, expectedStr, config);
        compareReaderToJSONObject(xmlStr, expectedStr, config);
        compareFileToJSONObject(xmlStr, expectedStr);
    }

    /**
     * Valid XML with comments to JSONObject
     */
    @Test
    void shouldHandleCommentsInXML() {

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
        XMLParserConfiguration config =
                new XMLParserConfiguration().withcDataTagName("altContent");
        JSONObject jsonObject = XML.toJSONObject(xmlStr, config);
        String expectedStr = "{\"addresses\":{\"address\":{\"street\":\"Baker "+
                "street 5\",\"name\":\"Joe Tester\",\"altContent\":\" this is -- "+
                "<another> comment \"}}}";
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    /**
     * Valid XML to XML.toString()
     */
    @Test
    void shouldHandleToString() {
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
        
        JSONObject jsonObject = XML.toJSONObject(xmlStr,
                XMLParserConfiguration.KEEP_STRINGS);
        String xmlToStr = XML.toString(jsonObject, null,
                XMLParserConfiguration.KEEP_STRINGS);
        JSONObject finalJsonObject = XML.toJSONObject(xmlToStr,
                XMLParserConfiguration.KEEP_STRINGS);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }

    /**
     * Converting a JSON doc containing '>' content to JSONObject, then
     * XML.toString() should result in valid XML.
     */
    @Test
    void shouldHandleContentNoArraytoString() {
        String expectedStr = 
            "{\"addresses\":{\"altContent\":\">\"}}";
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        XMLParserConfiguration config = new XMLParserConfiguration().withcDataTagName("altContent");
        String finalStr = XML.toString(expectedJsonObject, null, config);
        String expectedFinalStr = "<addresses>&gt;</addresses>";
        assertEquals(expectedFinalStr, finalStr, "Should handle expectedFinal: [" + expectedStr + "] final: [" +
                finalStr + "]");
    }

    /**
     * Converting a JSON doc containing a 'content' array to JSONObject, then
     * XML.toString() should result in valid XML.
     * TODO: This is probably an error in how the 'content' keyword is used.
     */
    @Test
    void shouldHandleContentArraytoString() {
        String expectedStr = 
            "{\"addresses\":{\"altContent\":[1, 2, 3]}}";
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        XMLParserConfiguration config = new XMLParserConfiguration().withcDataTagName("altContent");
        String finalStr = XML.toString(expectedJsonObject, null, config);
        String expectedFinalStr = "<addresses>"+
                "1\n2\n3"+
                "</addresses>";
        assertEquals(expectedFinalStr, finalStr, "Should handle expectedFinal: [" + expectedStr + "] final: [" +
                finalStr + "]");
    }

    /**
     * Converting a JSON doc containing a named array to JSONObject, then
     * XML.toString() should result in valid XML.
     */
    @Test
    void shouldHandleArraytoString() {
        String expectedStr = 
                "{\"addresses\":{"+
                        "\"something\":[1, 2, 3]}}";
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        String finalStr = XML.toString(expectedJsonObject, null,
                XMLParserConfiguration.KEEP_STRINGS);
        String expectedFinalStr = "<addresses>"+
                "<something>1</something><something>2</something><something>3</something>"+
                "</addresses>";
        assertEquals(expectedFinalStr, finalStr, "Should handle expectedFinal: [" + expectedStr + "] final: [" +
                finalStr + "]");
    }

    /**
     * Tests that the XML output for empty arrays is consistent.
     */
    @Test
    void shouldHandleEmptyArray(){
        final JSONObject jo1 = new JSONObject();
        jo1.put("array",new Object[]{});
        final JSONObject jo2 = new JSONObject();
        jo2.put("array",new JSONArray());

        final String expected = "<jo></jo>";
        String output1 = XML.toString(jo1, "jo",
                XMLParserConfiguration.KEEP_STRINGS);
        assertEquals(expected, output1, "Expected an empty root tag");
        String output2 = XML.toString(jo2, "jo",
                XMLParserConfiguration.KEEP_STRINGS);
        assertEquals(expected, output2, "Expected an empty root tag");
    }

    /**
     * Tests that the XML output for arrays is consistent when an internal array is empty.
     */
    @Test
    void shouldHandleEmptyMultiArray(){
        final JSONObject jo1 = new JSONObject();
        jo1.put("arr",new Object[]{"One", new String[]{}, "Four"});
        final JSONObject jo2 = new JSONObject();
        jo2.put("arr",new JSONArray(new Object[]{"One", new JSONArray(new String[]{}), "Four"}));

        final String expected = "<jo><arr>One</arr><arr></arr><arr>Four</arr></jo>";
        String output1 = XML.toString(jo1, "jo",
                XMLParserConfiguration.KEEP_STRINGS);
        assertEquals(expected, output1, "Expected a matching array");
        String output2 = XML.toString(jo2, "jo",
                XMLParserConfiguration.KEEP_STRINGS);

        assertEquals(expected, output2, "Expected a matching array");
    }

    /**
     * Tests that the XML output for arrays is consistent when arrays are not empty.
     */
    @Test
    void shouldHandleNonEmptyArray(){
        final JSONObject jo1 = new JSONObject();
        jo1.put("arr",new String[]{"One", "Two", "Three"});
        final JSONObject jo2 = new JSONObject();
        jo2.put("arr",new JSONArray(new String[]{"One", "Two", "Three"}));

        final String expected = "<jo><arr>One</arr><arr>Two</arr><arr>Three</arr></jo>";
        String output1 = XML.toString(jo1, "jo",
                XMLParserConfiguration.KEEP_STRINGS);
        assertEquals(expected, output1, "Expected a non empty root tag");
        String output2 = XML.toString(jo2, "jo",
                XMLParserConfiguration.KEEP_STRINGS);
        assertEquals(expected, output2, "Expected a non empty root tag");
    }

    /**
     * Tests that the XML output for arrays is consistent when arrays are not empty and contain internal arrays.
     */
    @Test
    void shouldHandleMultiArray(){
        final JSONObject jo1 = new JSONObject();
        jo1.put("arr",new Object[]{"One", new String[]{"Two", "Three"}, "Four"});
        final JSONObject jo2 = new JSONObject();
        jo2.put("arr",new JSONArray(new Object[]{"One", new JSONArray(new String[]{"Two", "Three"}), "Four"}));

        final String expected = "<jo><arr>One</arr><arr><array>Two</array><array>Three</array></arr><arr>Four</arr></jo>";
        String output1 = XML.toString(jo1, "jo",
                XMLParserConfiguration.KEEP_STRINGS);
        assertEquals(expected, output1, "Expected a matching array");
        String output2 = XML.toString(jo2, "jo",
                XMLParserConfiguration.KEEP_STRINGS);
        assertEquals(expected, output2, "Expected a matching array");
    }

    /**
     * Converting a JSON doc containing a named array of nested arrays to
     * JSONObject, then XML.toString() should result in valid XML.
     */
    @Test
    void shouldHandleNestedArraytoString() {
        String xmlStr = 
            "{\"addresses\":{\"address\":{\"name\":\"\",\"nocontent\":\"\","+
            "\"outer\":[[1], [2], [3]]},\"xsi:noNamespaceSchemaLocation\":\"test.xsd\",\""+
            "xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\"}}";
        JSONObject jsonObject = new JSONObject(xmlStr);
        String finalStr = XML.toString(jsonObject, null,
                XMLParserConfiguration.ORIGINAL);
        JSONObject finalJsonObject = XML.toJSONObject(finalStr);
        String expectedStr = "<addresses><address><name/><nocontent/>"+
                "<outer><array>1</array></outer><outer><array>2</array>"+
                "</outer><outer><array>3</array></outer>"+
                "</address><xsi:noNamespaceSchemaLocation>test.xsd</xsi:noName"+
                "spaceSchemaLocation><xmlns:xsi>http://www.w3.org/2001/XMLSche"+
                "ma-instance</xmlns:xsi></addresses>";
        JSONObject expectedJsonObject = XML.toJSONObject(expectedStr, 
                XMLParserConfiguration.ORIGINAL);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }


    /**
     * Possible bug: 
     * Illegal node-names must be converted to legal XML-node-names.
     * The given example shows 2 nodes which are valid for JSON, but not for XML.
     * Therefore illegal arguments should be converted to e.g. an underscore (_).
     */
    @Test
    void shouldHandleIllegalJSONNodeNames()
    {
        JSONObject inputJSON = new JSONObject();
        inputJSON.append("123IllegalNode", "someValue1");
        inputJSON.append("Illegal@node", "someValue2");

        String result = XML.toString(inputJSON, null,
                XMLParserConfiguration.KEEP_STRINGS);

        /*
         * This is invalid XML. Names should not begin with digits or contain
         * certain values, including '@'. One possible solution is to replace
         * illegal chars with '_', in which case the expected output would be:
         * <___IllegalNode>someValue1</___IllegalNode><Illegal_node>someValue2</Illegal_node>
         */
        String expected = "<123IllegalNode>someValue1</123IllegalNode><Illegal@node>someValue2</Illegal@node>";

        assertEquals(expected.length(), result.length(), "Length");
        assertTrue(result.contains("<123IllegalNode>someValue1</123IllegalNode>"), "123IllegalNode");
        assertTrue(result.contains("<Illegal@node>someValue2</Illegal@node>"), "Illegal@node");
    }

    /**
     * JSONObject with NULL value, to XML.toString()
     */
    @Test
    void shouldHandleNullNodeValue()
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
        String resultXML = XML.toString(inputJSON, null,
                XMLParserConfiguration.KEEP_STRINGS);
        assertEquals(actualXML, resultXML);
    }

    @Test
    void shouldHandleEmptyNodeValue()
    {
        JSONObject inputJSON = new JSONObject();
        inputJSON.put("Emptyness", "");
        String expectedXmlWithoutExplicitEndTag = "<Emptyness/>";
        String expectedXmlWithExplicitEndTag = "<Emptyness></Emptyness>";
        assertEquals(expectedXmlWithoutExplicitEndTag, XML.toString(inputJSON, null,
                new XMLParserConfiguration().withCloseEmptyTag(false)));
        assertEquals(expectedXmlWithExplicitEndTag, XML.toString(inputJSON, null,
                new XMLParserConfiguration().withCloseEmptyTag(true)));
    }

    @Test
    void shouldKeepConfigurationIntactAndUpdateCloseEmptyTagChoice()
    {
        XMLParserConfiguration keepStrings = XMLParserConfiguration.KEEP_STRINGS;
        XMLParserConfiguration keepStringsAndCloseEmptyTag = keepStrings.withCloseEmptyTag(true);
        XMLParserConfiguration keepDigits = keepStringsAndCloseEmptyTag.withKeepStrings(false);
        XMLParserConfiguration keepDigitsAndNoCloseEmptyTag = keepDigits.withCloseEmptyTag(false);
        assertTrue(keepStrings.isKeepStrings());
        assertFalse(keepStrings.isCloseEmptyTag());
        assertTrue(keepStringsAndCloseEmptyTag.isKeepStrings());
        assertTrue(keepStringsAndCloseEmptyTag.isCloseEmptyTag());
        assertFalse(keepDigits.isKeepStrings());
        assertTrue(keepDigits.isCloseEmptyTag());
        assertFalse(keepDigitsAndNoCloseEmptyTag.isKeepStrings());
        assertFalse(keepDigitsAndNoCloseEmptyTag.isCloseEmptyTag());

    }

    /**
     * Investigate exactly how the "content" keyword works
     */
    @Test
    void contentOperations() {
        /*
         * When a standalone <!CDATA[...]] structure is found while parsing XML into a
         * JSONObject, the contents are placed in a string value with key="content".
         */
        String xmlStr = "<tag1></tag1><![CDATA[if (a < b && a > 0) then return]]><tag2></tag2>";
        JSONObject jsonObject = XML.toJSONObject(xmlStr, 
                XMLParserConfiguration.KEEP_STRINGS);
        assertEquals(3, jsonObject.length(), "1. 3 items");
        assertEquals("", jsonObject.get("tag1"), "1. empty tag1");
        assertEquals("", jsonObject.get("tag2"), "1. empty tag2");
        assertEquals("if (a < b && a > 0) then return", jsonObject.get("content"), "1. content found");

        // multiple consecutive standalone cdatas are accumulated into an array
        xmlStr = "<tag1></tag1><![CDATA[if (a < b && a > 0) then return]]><tag2></tag2><![CDATA[here is another cdata]]>";
        jsonObject = XML.toJSONObject(xmlStr,
                new XMLParserConfiguration()
                        .withKeepStrings(true)
                        .withcDataTagName("altContent"));
        assertEquals(3, jsonObject.length(), "2. 3 items");
        assertEquals("", jsonObject.get("tag1"), "2. empty tag1");
        assertEquals("", jsonObject.get("tag2"), "2. empty tag2");
        assertTrue(jsonObject.get("altContent") instanceof JSONArray, "2. content array found");
        JSONArray jsonArray = jsonObject.getJSONArray("altContent");
        assertEquals(2, jsonArray.length(), "2. array size");
        assertEquals("if (a < b && a > 0) then return", jsonArray.get(0), "2. content array entry 0");
        assertEquals("here is another cdata", jsonArray.get(1), "2. content array entry 1");

        /*
         * text content is accumulated in a "content" inside a local JSONObject.
         * If there is only one instance, it is saved in the context (a different JSONObject 
         * from the calling code. and the content element is discarded. 
         */
        xmlStr =  "<tag1>value 1</tag1>";
        jsonObject = XML.toJSONObject(xmlStr,
                new XMLParserConfiguration()
                        .withKeepStrings(true)
                        .withcDataTagName("altContent"));
        assertEquals(1, jsonObject.length(), "3. 2 items");
        assertEquals("value 1", jsonObject.get("tag1"), "3. value tag1");

        /*
         * array-style text content (multiple tags with the same name) is 
         * accumulated in a local JSONObject with key="content" and value=JSONArray,
         * saved in the context, and then the local JSONObject is discarded.
         */
        xmlStr =  "<tag1>value 1</tag1><tag1>2</tag1><tag1>true</tag1>";
        jsonObject = XML.toJSONObject(xmlStr,
                new XMLParserConfiguration()
                        .withKeepStrings(true)
                        .withcDataTagName("altContent"));
        assertEquals(1, jsonObject.length(), "4. 1 item");
        assertTrue(jsonObject.get("tag1") instanceof JSONArray, "4. content array found");
        jsonArray = jsonObject.getJSONArray("tag1");
        assertEquals(3, jsonArray.length(), "4. array size");
        assertEquals("value 1", jsonArray.get(0), "4. content array entry 0");
        assertEquals(2, jsonArray.getInt(1), "4. content array entry 1");
        assertTrue(jsonArray.getBoolean(2), "4. content array entry 2");

        /*
         * Complex content is accumulated in a "content" field. For example, an element
         * may contain a mix of child elements and text. Each text segment is 
         * accumulated to content. 
         */
        xmlStr =  "<tag1>val1<tag2/>val2</tag1>";
        jsonObject = XML.toJSONObject(xmlStr,
                new XMLParserConfiguration()
                        .withKeepStrings(true)
                        .withcDataTagName("altContent"));
        assertEquals(1, jsonObject.length(), "5. 1 item");
        assertTrue(jsonObject.get("tag1") 
                instanceof JSONObject, "5. jsonObject found");
        jsonObject = jsonObject.getJSONObject("tag1");
        assertEquals(2, jsonObject.length(), "5. 2 contained items");
        assertEquals("", jsonObject.get("tag2"), "5. contained tag");
        assertTrue(jsonObject.get("altContent") instanceof JSONArray,
                "5. contained content jsonArray found");
        jsonArray = jsonObject.getJSONArray("altContent");
        assertEquals(2, jsonArray.length(), "5. array size");
        assertEquals("val1", jsonArray.get(0), "5. content array entry 0");
        assertEquals("val2", jsonArray.get(1), "5. content array entry 1");

        /*
         * If there is only 1 complex text content, then it is accumulated in a 
         * "content" field as a string.
         */
        xmlStr =  "<tag1>val1<tag2/></tag1>";
        jsonObject = XML.toJSONObject(xmlStr,
                new XMLParserConfiguration()
                        .withKeepStrings(true)
                        .withcDataTagName("altContent"));
        assertEquals(1, jsonObject.length(), "6. 1 item");
        assertTrue(jsonObject.get("tag1") instanceof JSONObject, "6. jsonObject found");
        jsonObject = jsonObject.getJSONObject("tag1");
        assertEquals("val1", jsonObject.get("altContent"), "6. contained content found");
        assertEquals("", jsonObject.get("tag2"), "6. contained tag2");

        /*
         * In this corner case, the content sibling happens to have key=content
         * We end up with an array within an array, and no content element.
         * This is probably a bug. 
         */
        xmlStr =  "<tag1>val1<altContent/></tag1>";
        jsonObject = XML.toJSONObject(xmlStr,
                new XMLParserConfiguration()
                        .withKeepStrings(true)
                        .withcDataTagName("altContent"));
        assertEquals(1, jsonObject.length(), "7. 1 item");
        assertTrue(jsonObject.get("tag1") instanceof JSONArray, 
                "7. jsonArray found");
        jsonArray = jsonObject.getJSONArray("tag1");
        assertEquals(1, jsonArray.length(), "array size 1");
        assertTrue(jsonArray.get(0) 
                instanceof JSONArray, "7. contained array found");
        jsonArray = jsonArray.getJSONArray(0);
        assertEquals(2, jsonArray.length(), "7. inner array size 2");
        assertEquals("val1", jsonArray.get(0), "7. inner array item 0");
        assertEquals("", jsonArray.get(1), "7. inner array item 1");

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
                                        "\"altContent\": {"+
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
        xmlStr = XML.toString(jsonObject, null,
                new XMLParserConfiguration()
                        .withKeepStrings(true)
                        .withcDataTagName("altContent"));
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
        assertTrue(true, "nothing to test here, see comment on created XML, above");
    }

    /**
     * JSON string lost leading zero and converted "True" to true.
     */
    @Test
    void toJSONArray_jsonOutput() {
        final String originalXml = "<root><id>01</id><id>1</id><id>00</id><id>0</id><item id=\"01\"/><title>True</title></root>";
        final JSONObject expected = new JSONObject("{\"root\":{\"item\":{\"id\":1},\"id\":[1,1,0,0],\"title\":true}}");
        final JSONObject actualJsonOutput = XML.toJSONObject(originalXml, 
                new XMLParserConfiguration().withKeepStrings(false));
        Util.compareActualVsExpectedJsonObjects(actualJsonOutput,expected);
    }

    /**
     * JSON string cannot be reverted to original xml.
     */
    @Test
    void toJSONArray_reversibility() {
        final String originalXml = "<root><id>01</id><id>1</id><id>00</id><id>0</id><item id=\"01\"/><title>True</title></root>";
        XMLParserConfiguration config = new XMLParserConfiguration().withKeepStrings(false);
        final String revertedXml = 
                XML.toString(XML.toJSONObject(originalXml, config), 
                        null, config);
        assertNotEquals(revertedXml, originalXml);
    }

    /**
     * test passes when using the new method toJsonArray.
     */
    @Test
    void toJsonXML() {
        final String originalXml = "<root><id>01</id><id>1</id><id>00</id><id>0</id><item id=\"01\"/><title>True</title></root>";
        final JSONObject expected = new JSONObject("{\"root\":{\"item\":{\"id\":\"01\"},\"id\":[\"01\",\"1\",\"00\",\"0\"],\"title\":\"True\"}}");

        final JSONObject json = XML.toJSONObject(originalXml,
                new XMLParserConfiguration().withKeepStrings(true));
        Util.compareActualVsExpectedJsonObjects(json, expected);
        
        final String reverseXml = XML.toString(json);
        // this reversal isn't exactly the same. use JSONML for an exact reversal
        final String expectedReverseXml = "<root><item><id>01</id></item><id>01</id><id>1</id><id>00</id><id>0</id><title>True</title></root>";

        assertEquals(expectedReverseXml.length(), reverseXml.length(), "length");
        assertTrue(reverseXml.contains("<id>01</id><id>1</id><id>00</id><id>0</id>"), "array contents");
        assertTrue(reverseXml.contains("<item><id>01</id></item>"), "item contents");
        assertTrue(reverseXml.contains("<title>True</title>"), "title contents");
    }

    /**
     * test to validate certain conditions of XML unescaping.
     */
    @Test
    void unescape() {
        assertEquals("{\"xml\":\"Can cope <;\"}",
                XML.toJSONObject("<xml>Can cope &lt;; </xml>",
                        XMLParserConfiguration.KEEP_STRINGS).toString());
        assertEquals("Can cope <; ", XML.unescape("Can cope &lt;; "));

        assertEquals("{\"xml\":\"Can cope & ;\"}",
                XML.toJSONObject("<xml>Can cope &amp; ; </xml>",
                        XMLParserConfiguration.KEEP_STRINGS).toString());
        assertEquals("Can cope & ; ", XML.unescape("Can cope &amp; ; "));

        assertEquals("{\"xml\":\"Can cope &;\"}",
                XML.toJSONObject("<xml>Can cope &amp;; </xml>",
                        XMLParserConfiguration.KEEP_STRINGS).toString());
        assertEquals("Can cope &; ", XML.unescape("Can cope &amp;; "));

        // unicode entity
        assertEquals("{\"xml\":\"Can cope 4;\"}",
                XML.toJSONObject("<xml>Can cope &#x34;; </xml>",
                        XMLParserConfiguration.KEEP_STRINGS).toString());
        assertEquals("Can cope 4; ", XML.unescape("Can cope &#x34;; "));

        // double escaped
        assertEquals("{\"xml\":\"Can cope &lt;\"}",
                XML.toJSONObject("<xml>Can cope &amp;lt; </xml>",
                        XMLParserConfiguration.KEEP_STRINGS).toString());
        assertEquals("Can cope &lt; ", XML.unescape("Can cope &amp;lt; "));
        
        assertEquals("{\"xml\":\"Can cope &#x34;\"}",
                XML.toJSONObject("<xml>Can cope &amp;#x34; </xml>",
                        XMLParserConfiguration.KEEP_STRINGS).toString());
        assertEquals("Can cope &#x34; ", XML.unescape("Can cope &amp;#x34; "));

   }

    /**
     * Confirm XMLParserConfiguration functionality
     */
    @Test
    void config() {
        /**
         * 1st param is whether to keep the raw string, or call
         * XML.stringToValue(), which may convert the token to
         * boolean, null, or number.
         * 2nd param is what JSON name to use for strings that are
         * evaluated as xml content data in complex objects, e.g. 
         * <parent>
         *      <child>value</child>
         *      content data
         * </tag>
         */

        String xmlStr = 
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
                "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
                "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
                "   <address>\n"+
                "       content 1\n"+
                "       <name>Sherlock Holmes</name>\n"+
                "       content 2\n"+
                "       <street>Baker street 5</street>\n"+
                "       content 3\n"+
                "       <num>1</num>\n"+
                "   </address>\n"+
                "</addresses>";

        // keep strings, use the altContent tag
        XMLParserConfiguration config = 
                new XMLParserConfiguration()
                        .withKeepStrings(true)
                        .withcDataTagName("altContent");
        JSONObject jsonObject = XML.toJSONObject(xmlStr, config);
        // num is parsed as a string
        assertEquals("1", jsonObject.getJSONObject("addresses").
                getJSONObject("address").getString("num"));
        // complex content is collected in an 'altContent' array
        JSONArray jsonArray = jsonObject.getJSONObject("addresses").
                getJSONObject("address").getJSONArray("altContent");
        String expectedStr = "[\"content 1\", \"content 2\", \"content 3\"]";
        JSONArray expectedJsonArray = new JSONArray(expectedStr);
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);

        // keepstrings only
        jsonObject = XML.toJSONObject(xmlStr, 
                XMLParserConfiguration.KEEP_STRINGS);
        // num is parsed as a string
        assertEquals("1", jsonObject.getJSONObject("addresses").
                getJSONObject("address").getString("num"));
        // complex content is collected in an 'content' array
        jsonArray = jsonObject.getJSONObject("addresses").
                getJSONObject("address").getJSONArray("content");
        expectedJsonArray = new JSONArray(expectedStr);
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
        
        // use alternate content name
        config = new XMLParserConfiguration().withcDataTagName("altContent");
        jsonObject = XML.toJSONObject(xmlStr, config);
        // num is parsed as a number
        assertEquals(1, jsonObject.getJSONObject("addresses").
                getJSONObject("address").getInt("num"));
        // complex content is collected in an 'altContent' array
        jsonArray = jsonObject.getJSONObject("addresses").
                getJSONObject("address").getJSONArray("altContent");
        expectedJsonArray = new JSONArray(expectedStr);
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);

    }

    /**
     * Test forceList parameter
     */
    @Test
    void simpleForceList() {
        String xmlStr = 
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
                "<addresses>\n"+
                "   <address>\n"+
                "      <name>Sherlock Holmes</name>\n"+
                "   </address>\n"+
                "</addresses>";

        String expectedStr = 
                "{\"addresses\":[{\"address\":{\"name\":\"Sherlock Holmes\"}}]}";
        
        Set<String> forceList = new HashSet<String>();
        forceList.add("addresses");

        XMLParserConfiguration config = 
                new XMLParserConfiguration()
                        .withForceList(forceList);
        JSONObject jsonObject = XML.toJSONObject(xmlStr, config);
        JSONObject expetedJsonObject = new JSONObject(expectedStr);

        Util.compareActualVsExpectedJsonObjects(jsonObject, expetedJsonObject);
    }

    @Test
    void longForceList() {
        String xmlStr = 
                "<servers>"+
                    "<server>"+
                        "<name>host1</name>"+
                        "<os>Linux</os>"+
                        "<interfaces>"+
                            "<interface>"+
                                "<name>em0</name>"+
                                "<ip_address>10.0.0.1</ip_address>"+
                            "</interface>"+
                        "</interfaces>"+
                    "</server>"+
                "</servers>";

        String expectedStr = 
                "{"+
                    "\"servers\": ["+
                        "{"+
                            "\"server\": {"+
                            "\"name\": \"host1\","+
                            "\"os\": \"Linux\","+
                            "\"interfaces\": ["+
                                "{"+
                                    "\"interface\": {"+
                                    "\"name\": \"em0\","+
                                    "\"ip_address\": \"10.0.0.1\""+
                                    "}}]}}]}";
        
        Set<String> forceList = new HashSet<String>();
        forceList.add("servers");
        forceList.add("interfaces");

        XMLParserConfiguration config = 
                new XMLParserConfiguration()
                        .withForceList(forceList);
        JSONObject jsonObject = XML.toJSONObject(xmlStr, config);
        JSONObject expetedJsonObject = new JSONObject(expectedStr);

        Util.compareActualVsExpectedJsonObjects(jsonObject, expetedJsonObject);
    }

    @Test
    void multipleTagForceList() {
        String xmlStr = 
                "<addresses>\n"+
                "   <address>\n"+
                "      <name>Sherlock Holmes</name>\n"+
                "      <name>John H. Watson</name>\n"+
                "   </address>\n"+
                "</addresses>";

        String expectedStr = 
                "{"+
                    "\"addresses\":["+
                    "{"+
                        "\"address\":["+
                            "{"+
                                "\"name\":["+
                                "\"Sherlock Holmes\","+
                                "\"John H. Watson\""+
                                "]"+
                            "}"+
                        "]"+
                    "}"+
                    "]"+
                "}";
        
        Set<String> forceList = new HashSet<String>();
        forceList.add("addresses");
        forceList.add("address");
        forceList.add("name");

        XMLParserConfiguration config = 
                new XMLParserConfiguration()
                        .withForceList(forceList);
        JSONObject jsonObject = XML.toJSONObject(xmlStr, config);
        JSONObject expetedJsonObject = new JSONObject(expectedStr);

        Util.compareActualVsExpectedJsonObjects(jsonObject, expetedJsonObject);
    }

    @Test
    void emptyForceList() {
        String xmlStr = 
                "<addresses></addresses>";

        String expectedStr = 
                "{\"addresses\":[]}";
        
        Set<String> forceList = new HashSet<String>();
        forceList.add("addresses");

        XMLParserConfiguration config = 
                new XMLParserConfiguration()
                        .withForceList(forceList);
        JSONObject jsonObject = XML.toJSONObject(xmlStr, config);
        JSONObject expetedJsonObject = new JSONObject(expectedStr);

        Util.compareActualVsExpectedJsonObjects(jsonObject, expetedJsonObject);
    }

    @Test
    void contentForceList() {
        String xmlStr = 
                "<addresses>Baker Street</addresses>";

        String expectedStr = 
                "{\"addresses\":[\"Baker Street\"]}";
        
        Set<String> forceList = new HashSet<String>();
        forceList.add("addresses");

        XMLParserConfiguration config = 
                new XMLParserConfiguration()
                        .withForceList(forceList);
        JSONObject jsonObject = XML.toJSONObject(xmlStr, config);
        JSONObject expetedJsonObject = new JSONObject(expectedStr);

        Util.compareActualVsExpectedJsonObjects(jsonObject, expetedJsonObject);
    }

    @Test
    void emptyTagForceList() {
        String xmlStr = 
                "<addresses />";

        String expectedStr = 
                "{\"addresses\":[]}";
        
        Set<String> forceList = new HashSet<String>();
        forceList.add("addresses");

        XMLParserConfiguration config = 
                new XMLParserConfiguration()
                        .withForceList(forceList);
        JSONObject jsonObject = XML.toJSONObject(xmlStr, config);
        JSONObject expetedJsonObject = new JSONObject(expectedStr);

        Util.compareActualVsExpectedJsonObjects(jsonObject, expetedJsonObject);
    }

    @Test
    void maxNestingDepthIsSet() {
        XMLParserConfiguration xmlParserConfiguration = XMLParserConfiguration.ORIGINAL;

        assertEquals(XMLParserConfiguration.DEFAULT_MAXIMUM_NESTING_DEPTH, xmlParserConfiguration.getMaxNestingDepth());

        xmlParserConfiguration = xmlParserConfiguration.withMaxNestingDepth(42);

        assertEquals(42, xmlParserConfiguration.getMaxNestingDepth());

        xmlParserConfiguration = xmlParserConfiguration.withMaxNestingDepth(0);

        assertEquals(0, xmlParserConfiguration.getMaxNestingDepth());

        xmlParserConfiguration = xmlParserConfiguration.withMaxNestingDepth(-31415926);

        assertEquals(XMLParserConfiguration.UNDEFINED_MAXIMUM_NESTING_DEPTH, xmlParserConfiguration.getMaxNestingDepth());

        xmlParserConfiguration = xmlParserConfiguration.withMaxNestingDepth(Integer.MIN_VALUE);

        assertEquals(XMLParserConfiguration.UNDEFINED_MAXIMUM_NESTING_DEPTH, xmlParserConfiguration.getMaxNestingDepth());
    }
    
    /**
     * Convenience method, given an input string and expected result,
     * convert to JSONObject and compare actual to expected result.
     * @param xmlStr the string to parse
     * @param expectedStr the expected JSON string
     * @param config provides more flexible XML parsing
     * flexible XML parsing.
     */
    private void compareStringToJSONObject(String xmlStr, String expectedStr,
            XMLParserConfiguration config) {
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        JSONObject jsonObject = XML.toJSONObject(xmlStr, config);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    /**
     * Convenience method, given an input string and expected result,
     * convert to JSONObject via reader and compare actual to expected result.
     * @param xmlStr the string to parse
     * @param expectedStr the expected JSON string
     * @param config provides more flexible XML parsing
     */
    private void compareReaderToJSONObject(String xmlStr, String expectedStr,
            XMLParserConfiguration config) {
        /*
         * Commenting out this method until the JSON-java code is updated
         * to support XML.toJSONObject(reader)
         */
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Reader reader = new StringReader(xmlStr);
        try {
            JSONObject jsonObject = XML.toJSONObject(reader, config);
            Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
        } catch (Exception e) {
            assertTrue(false, "Reader error: " +e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (Exception e) {}
        }
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
        /*
         * Commenting out this method until the JSON-java code is updated
         * to support XML.toJSONObject(reader)
         */
        try {
            JSONObject expectedJsonObject = new JSONObject(expectedStr);
            File tempFile = File.createTempFile("fileToJSONObject.xml", null, this.testFolder);
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
            assertTrue(false, "Error: " +e.getMessage());
        }
    }
}