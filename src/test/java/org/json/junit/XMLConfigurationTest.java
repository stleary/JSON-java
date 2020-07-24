package org.json.junit;

/*
Copyright (c) 2020 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.json.XMLParserConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for JSON-Java XML.java with XMLParserConfiguration.java
 */
public class XMLConfigurationTest {
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
        JSONObject jsonObject = 
                XML.toJSONObject(xmlStr, XMLParserConfiguration.KEEP_STRINGS);
        assertTrue("jsonObject should be empty", jsonObject.isEmpty());
    }

    /**
     * Empty JSONObject from an empty XML string.
     */
    @Test
    public void shouldHandleEmptyXML() {

        String xmlStr = "";
        JSONObject jsonObject = 
                XML.toJSONObject(xmlStr, XMLParserConfiguration.KEEP_STRINGS);
        assertTrue("jsonObject should be empty", jsonObject.isEmpty());
    }

    /**
     * Empty JSONObject from a non-XML string.
     */
    @Test
    public void shouldHandleNonXML() {
        String xmlStr = "{ \"this is\": \"not xml\"}";
        JSONObject jsonObject = 
                XML.toJSONObject(xmlStr, XMLParserConfiguration.KEEP_STRINGS);
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
            XML.toJSONObject(xmlStr, XMLParserConfiguration.KEEP_STRINGS);
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
            XML.toJSONObject(xmlStr, XMLParserConfiguration.KEEP_STRINGS);
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
            XML.toJSONObject(xmlStr, XMLParserConfiguration.KEEP_STRINGS);
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
            XML.toJSONObject(xmlStr, XMLParserConfiguration.KEEP_STRINGS);
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
            XMLParserConfiguration config = 
                    new XMLParserConfiguration().withcDataTagName("altContent");
            XML.toJSONObject(xmlStr, config);
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
        String actualXml = XML.toString(jsonObject, null,
                XMLParserConfiguration.KEEP_STRINGS);
        assertEquals("generated XML does not equal expected XML","\"null\"",actualXml);
    }

    /**
     * Empty JSONObject in XML.toString()
     */
    @Test
    public void shouldHandleEmptyJSONXML() {
        JSONObject jsonObject= new JSONObject();
        String xmlStr = XML.toString(jsonObject, null,
                XMLParserConfiguration.KEEP_STRINGS);
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
        JSONObject jsonObject = XML.toJSONObject(xmlStr,
                XMLParserConfiguration.KEEP_STRINGS);
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
    public void shouldHandleContentNoArraytoString() {
        String expectedStr = 
            "{\"addresses\":{\"altContent\":\">\"}}";
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        XMLParserConfiguration config = new XMLParserConfiguration().withcDataTagName("altContent");
        String finalStr = XML.toString(expectedJsonObject, null, config);
        String expectedFinalStr = "<addresses>&gt;</addresses>";
        assertTrue("Should handle expectedFinal: ["+expectedStr+"] final: ["+
                finalStr+"]", expectedFinalStr.equals(finalStr));
    }

    /**
     * Converting a JSON doc containing a 'content' array to JSONObject, then
     * XML.toString() should result in valid XML.
     * TODO: This is probably an error in how the 'content' keyword is used.
     */
    @Test
    public void shouldHandleContentArraytoString() {
        String expectedStr = 
            "{\"addresses\":{\"altContent\":[1, 2, 3]}}";
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        XMLParserConfiguration config = new XMLParserConfiguration().withcDataTagName("altContent");
        String finalStr = XML.toString(expectedJsonObject, null, config);
        String expectedFinalStr = "<addresses>"+
                "1\n2\n3"+
                "</addresses>";
        assertTrue("Should handle expectedFinal: ["+expectedStr+"] final: ["+
                finalStr+"]", expectedFinalStr.equals(finalStr));
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
        String finalStr = XML.toString(expectedJsonObject, null,
                XMLParserConfiguration.KEEP_STRINGS);
        String expectedFinalStr = "<addresses>"+
                "<something>1</something><something>2</something><something>3</something>"+
                "</addresses>";
        assertTrue("Should handle expectedFinal: ["+expectedStr+"] final: ["+
                finalStr+"]", expectedFinalStr.equals(finalStr));
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
        String output1 = XML.toString(jo1, "jo",
                XMLParserConfiguration.KEEP_STRINGS);
        assertEquals("Expected an empty root tag", expected, output1);
        String output2 = XML.toString(jo2, "jo",
                XMLParserConfiguration.KEEP_STRINGS);
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
        String output1 = XML.toString(jo1, "jo",
                XMLParserConfiguration.KEEP_STRINGS);
        assertEquals("Expected a matching array", expected, output1);
        String output2 = XML.toString(jo2, "jo",
                XMLParserConfiguration.KEEP_STRINGS);

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
        String output1 = XML.toString(jo1, "jo",
                XMLParserConfiguration.KEEP_STRINGS);
        assertEquals("Expected a non empty root tag", expected, output1);
        String output2 = XML.toString(jo2, "jo",
                XMLParserConfiguration.KEEP_STRINGS);
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
        String output1 = XML.toString(jo1, "jo",
                XMLParserConfiguration.KEEP_STRINGS);
        assertEquals("Expected a matching array", expected, output1);
        String output2 = XML.toString(jo2, "jo",
                XMLParserConfiguration.KEEP_STRINGS);
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
    public void shouldHandleIllegalJSONNodeNames()
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

        assertEquals("Length", expected.length(), result.length());
        assertTrue("123IllegalNode", result.contains("<123IllegalNode>someValue1</123IllegalNode>"));
        assertTrue("Illegal@node", result.contains("<Illegal@node>someValue2</Illegal@node>"));
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
        String resultXML = XML.toString(inputJSON, null,
                XMLParserConfiguration.KEEP_STRINGS);
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
        JSONObject jsonObject = XML.toJSONObject(xmlStr, 
                XMLParserConfiguration.KEEP_STRINGS);
        assertTrue("1. 3 items", 3 == jsonObject.length());
        assertTrue("1. empty tag1", "".equals(jsonObject.get("tag1")));
        assertTrue("1. empty tag2", "".equals(jsonObject.get("tag2")));
        assertTrue("1. content found", "if (a < b && a > 0) then return".equals(jsonObject.get("content")));

        // multiple consecutive standalone cdatas are accumulated into an array
        xmlStr = "<tag1></tag1><![CDATA[if (a < b && a > 0) then return]]><tag2></tag2><![CDATA[here is another cdata]]>";
        jsonObject = XML.toJSONObject(xmlStr,
                new XMLParserConfiguration()
                        .withKeepStrings(true)
                        .withcDataTagName("altContent"));
        assertTrue("2. 3 items", 3 == jsonObject.length());
        assertTrue("2. empty tag1", "".equals(jsonObject.get("tag1")));
        assertTrue("2. empty tag2", "".equals(jsonObject.get("tag2")));
        assertTrue("2. content array found", jsonObject.get("altContent") instanceof JSONArray);
        JSONArray jsonArray = jsonObject.getJSONArray("altContent");
        assertTrue("2. array size", jsonArray.length() == 2);
        assertTrue("2. content array entry 0", "if (a < b && a > 0) then return".equals(jsonArray.get(0)));
        assertTrue("2. content array entry 1", "here is another cdata".equals(jsonArray.get(1)));

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
        assertTrue("3. 2 items", 1 == jsonObject.length());
        assertTrue("3. value tag1", "value 1".equals(jsonObject.get("tag1")));

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
        jsonObject = XML.toJSONObject(xmlStr,
                new XMLParserConfiguration()
                        .withKeepStrings(true)
                        .withcDataTagName("altContent"));
        assertTrue("5. 1 item", 1 == jsonObject.length());
        assertTrue("5. jsonObject found", jsonObject.get("tag1") 
                instanceof JSONObject);
        jsonObject = jsonObject.getJSONObject("tag1");
        assertTrue("5. 2 contained items", 2 == jsonObject.length());
        assertTrue("5. contained tag", "".equals(jsonObject.get("tag2")));
        assertTrue("5. contained content jsonArray found",
                jsonObject.get("altContent") instanceof JSONArray);
        jsonArray = jsonObject.getJSONArray("altContent");
        assertTrue("5. array size", jsonArray.length() == 2);
        assertTrue("5. content array entry 0", "val1".equals(jsonArray.get(0)));
        assertTrue("5. content array entry 1", "val2".equals(jsonArray.get(1)));

        /*
         * If there is only 1 complex text content, then it is accumulated in a 
         * "content" field as a string.
         */
        xmlStr =  "<tag1>val1<tag2/></tag1>";
        jsonObject = XML.toJSONObject(xmlStr,
                new XMLParserConfiguration()
                        .withKeepStrings(true)
                        .withcDataTagName("altContent"));
        assertTrue("6. 1 item", 1 == jsonObject.length());
        assertTrue("6. jsonObject found", jsonObject.get("tag1") instanceof JSONObject);
        jsonObject = jsonObject.getJSONObject("tag1");
        assertTrue("6. contained content found", 
                "val1".equals(jsonObject.get("altContent")));
        assertTrue("6. contained tag2", "".equals(jsonObject.get("tag2")));

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
        assertTrue("7. 1 item", 1 == jsonObject.length());
        assertTrue("7. jsonArray found", 
                jsonObject.get("tag1") instanceof JSONArray);
        jsonArray = jsonObject.getJSONArray("tag1");
        assertTrue("array size 1", jsonArray.length() == 1);
        assertTrue("7. contained array found", jsonArray.get(0) 
                instanceof JSONArray);
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
        assertTrue("nothing to test here, see comment on created XML, above", true);
    }

    /**
     * JSON string lost leading zero and converted "True" to true.
     */
    @Test
    public void testToJSONArray_jsonOutput() {
        final String originalXml = "<root><id>01</id><id>1</id><id>00</id><id>0</id><item id=\"01\"/><title>True</title></root>";
        final JSONObject expected = new JSONObject("{\"root\":{\"item\":{\"id\":\"01\"},\"id\":[\"01\",1,\"00\",0],\"title\":true}}");
        final JSONObject actualJsonOutput = XML.toJSONObject(originalXml, 
                new XMLParserConfiguration().withKeepStrings(false));
        Util.compareActualVsExpectedJsonObjects(actualJsonOutput,expected);
    }

    /**
     * JSON string cannot be reverted to original xml.
     */
    @Test
    public void testToJSONArray_reversibility() {
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
    public void testToJsonXML() {
        final String originalXml = "<root><id>01</id><id>1</id><id>00</id><id>0</id><item id=\"01\"/><title>True</title></root>";
        final JSONObject expected = new JSONObject("{\"root\":{\"item\":{\"id\":\"01\"},\"id\":[\"01\",\"1\",\"00\",\"0\"],\"title\":\"True\"}}");

        final JSONObject json = XML.toJSONObject(originalXml,
                new XMLParserConfiguration().withKeepStrings(true));
        Util.compareActualVsExpectedJsonObjects(json, expected);
        
        final String reverseXml = XML.toString(json);
        // this reversal isn't exactly the same. use JSONML for an exact reversal
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
    public void testConfig() {
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
        assertEquals(jsonObject.getJSONObject("addresses").
                getJSONObject("address").getString("num"), "1");
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
        assertEquals(jsonObject.getJSONObject("addresses").
                getJSONObject("address").getString("num"), "1");
        // complex content is collected in an 'content' array
        jsonArray = jsonObject.getJSONObject("addresses").
                getJSONObject("address").getJSONArray("content");
        expectedJsonArray = new JSONArray(expectedStr);
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
        
        // use alternate content name
        config = new XMLParserConfiguration().withcDataTagName("altContent");
        jsonObject = XML.toJSONObject(xmlStr, config);
        // num is parsed as a number
        assertEquals(jsonObject.getJSONObject("addresses").
                getJSONObject("address").getInt("num"), 1);
        // complex content is collected in an 'altContent' array
        jsonArray = jsonObject.getJSONObject("addresses").
                getJSONObject("address").getJSONArray("altContent");
        expectedJsonArray = new JSONArray(expectedStr);
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);

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
            assertTrue("Reader error: " +e.getMessage(), false);
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
            assertTrue("Error: " +e.getMessage(), false);
        }
    }
}