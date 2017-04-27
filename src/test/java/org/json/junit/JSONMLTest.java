package org.json.junit;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;

/**
 * Tests for org.json.JSONML.java
 * 
 * Certain inputs are expected to result in exceptions. These tests are
 * executed first. JSONML provides an API to:
 *     Convert an XML string into a JSONArray or a JSONObject. 
 *     Convert a JSONArray or JSONObject into an XML string.
 * Both fromstring and tostring operations operations should be symmetrical
 * within the limits of JSONML. 
 * It should be possible to perform the following operations, which should
 * result in the original string being recovered, within the limits of the
 * underlying classes:
 *  Convert a string -> JSONArray -> string -> JSONObject -> string
 *  Convert a string -> JSONObject -> string -> JSONArray -> string
 * 
 */
public class JSONMLTest {

    /**
     * Attempts to transform a null XML string to JSON.
     * Expects a NullPointerException
     */
    @Test(expected=NullPointerException.class)
    public void nullXMLException() {
        String xmlStr = null;
        JSONML.toJSONArray(xmlStr);
    }

    /**
     * Attempts to transform an empty string to JSON.
     * Expects a JSONException
     */
    @Test
    public void emptyXMLException() {
        String xmlStr = "";
        try {
            JSONML.toJSONArray(xmlStr);
            assertTrue("Expecting an exception", false);
        } catch (JSONException e) {
            assertTrue("Expecting an exception message",
                "Bad XML at 1 [character 2 line 1]".
                equals(e.getMessage()));
        }
    }

    /**
     * Attempts to call JSONML.toString() with a null JSONArray.
     * Expects a NullPointerException. 
     */
    @Test(expected=NullPointerException.class)
    public void nullJSONXMLException() {
        /**
         * Tries to convert a null JSONArray to XML.
         */
        JSONArray jsonArray= null;
        JSONML.toString(jsonArray);
    }

    /**
     * Attempts to call JSONML.toString() with a null JSONArray.
     * Expects a JSONException. 
     */
    @Test
    public void emptyJSONXMLException() {
        /**
         * Tries to convert an empty JSONArray to XML.
         */
        JSONArray jsonArray = new JSONArray();
        try {
            JSONML.toString(jsonArray);
            assertTrue("Expecting an exception", false);
        } catch (JSONException e) {
            assertTrue("Expecting an exception message",
                "JSONArray[0] not found.".
                equals(e.getMessage()));
        }
    }

    /**
     * Attempts to transform an non-XML string to JSON.
     * Expects a JSONException
     */
    @Test
    public void nonXMLException() {
        /**
         * Attempts to transform a nonXML string to JSON
         */
        String xmlStr = "{ \"this is\": \"not xml\"}";
        try {
            JSONML.toJSONArray(xmlStr);
            assertTrue("Expecting an exception", false);
        } catch (JSONException e) {
            assertTrue("Expecting an exception message",
                "Bad XML at 25 [character 26 line 1]".
                equals(e.getMessage()));
        }
    }

    /**
     * Attempts to transform a JSON document with XML content that
     * does not follow JSONML conventions (element name is not first value
     * in a nested JSONArray) to a JSONArray then back to string.
     * Expects a JSONException
     */
    @Test
    public void emptyTagException() {
        /**
         * jsonArrayStr is used to build a JSONArray which is then
         * turned into XML. For this transformation, all arrays represent
         * elements and the first array entry is the name of the element.
         * In this case, one of the arrays does not have a name
         */
        String jsonArrayStr =
            "[\"addresses\","+
                "{\"xsi:noNamespaceSchemaLocation\":\"test.xsd\","+
                    "\"xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\"},"+
                // this array has no name 
                "["+
                    "[\"name\"],"+
                    "[\"nocontent\"],"+
                    "\">\""+
                "]"+
            "]";
        JSONArray jsonArray = new JSONArray(jsonArrayStr);
        try {
            JSONML.toString(jsonArray);
            assertTrue("Expecting an exception", false);
        } catch (JSONException e) {
            assertTrue("Expecting an exception message",
                "JSONArray[0] not a string.".
                equals(e.getMessage()));
        }
    }

    /**
     * Attempts to transform a JSON document with XML content that
     * does not follow JSONML conventions (element tag has an embedded space)
     * to a JSONArray then back to string. Expects a JSONException
     */
    @Test
    public void spaceInTagException() {
        /**
         * jsonArrayStr is used to build a JSONArray which is then
         * turned into XML. For this transformation, all arrays represent
         * elements and the first array entry is the name of the element.
         * In this case, one of the element names has an embedded space,
         * which is not allowed.
         */
        String jsonArrayStr =
            "[\"addresses\","+
                "{\"xsi:noNamespaceSchemaLocation\":\"test.xsd\","+
                    "\"xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\"},"+
                // this array has an invalid name
                "[\"addr esses\","+
                    "[\"name\"],"+
                    "[\"nocontent\"],"+
                    "\">\""+
                "]"+
            "]";
        JSONArray jsonArray = new JSONArray(jsonArrayStr);
        try {
            JSONML.toString(jsonArray);
            assertTrue("Expecting an exception", false);
        } catch (JSONException e) {
            assertTrue("Expecting an exception message",
                "'addr esses' contains a space character.".
                equals(e.getMessage()));
        }
    }

    /**
     * Attempts to transform a malformed XML document 
     * (element tag has a frontslash) to a JSONArray.\
     * Expects a JSONException
     */
    @Test
    public void invalidSlashInTagException() {
        /**
         * xmlStr contains XML text which is transformed into a JSONArray.
         * In this case, the XML is invalid because the 'name' element
         * contains an invalid frontslash.
         */
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "    <address>\n"+
            "       <name/x>\n"+
            "       <street>abc street</street>\n"+
            "    </address>\n"+
            "</addresses>";
        try {
            JSONML.toJSONArray(xmlStr);
            assertTrue("Expecting an exception", false);
        } catch (JSONException e) {
            assertTrue("Expecting an exception message",
                "Misshaped tag at 176 [character 14 line 7]".
                equals(e.getMessage()));
        }
    }

    /**
     * Malformed XML text (invalid tagname) is transformed into a JSONArray.
     * Expects a JSONException.
     */
    @Test
    public void invalidBangInTagException() {
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "    <address>\n"+
            "       <name/>\n"+
            "       <!>\n"+
            "    </address>\n"+
            "</addresses>";
        try {
            JSONML.toJSONArray(xmlStr);
            assertTrue("Expecting an exception", false);
        } catch (JSONException e) {
            assertTrue("Expecting an exception message",
                "Misshaped meta tag at 216 [character 13 line 11]".
                equals(e.getMessage()));
        }
    }

    /**
     * Malformed XML text (invalid tagname, no close bracket) is transformed\
     * into a JSONArray. Expects a JSONException.
     */
    @Test
    public void invalidBangNoCloseInTagException() {
        /**
         * xmlStr contains XML text which is transformed into a JSONArray.
         * In this case, the XML is invalid because an element
         * starts with '!' and has no closing tag
         */
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "    <address>\n"+
            "       <name/>\n"+
            "       <!\n"+
            "    </address>\n"+
            "</addresses>";
        try {
            JSONML.toJSONArray(xmlStr);
            assertTrue("Expecting an exception", false);
        } catch (JSONException e) {
            assertTrue("Expecting an exception message",
                "Misshaped meta tag at 215 [character 13 line 11]".
                equals(e.getMessage()));
        }
    }

    /**
     * Malformed XML text (tagname with no close bracket) is transformed\
     * into a JSONArray. Expects a JSONException.
     */
    @Test
    public void noCloseStartTagException() {
        /**
         * xmlStr contains XML text which is transformed into a JSONArray.
         * In this case, the XML is invalid because an element
         * has no closing '>'.
         */
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "    <address>\n"+
            "       <name/>\n"+
            "       <abc\n"+
            "    </address>\n"+
            "</addresses>";
        try {
            JSONML.toJSONArray(xmlStr);
            assertTrue("Expecting an exception", false);
        } catch (JSONException e) {
            assertTrue("Expecting an exception message",
                "Misplaced '<' at 194 [character 5 line 10]".
                equals(e.getMessage()));
        }
    }

    /**
     * Malformed XML text (endtag with no name) is transformed\
     * into a JSONArray. Expects a JSONException.
     */
    @Test
    public void noCloseEndTagException() {
        /**
         * xmlStr contains XML text which is transformed into a JSONArray.
         * In this case, the XML is invalid because an element
         * has no name after the closing tag '</'.
         */
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "    <address>\n"+
            "       <name/>\n"+
            "       <abc/>\n"+
            "   </>\n"+
            "</addresses>";
        try {
            JSONML.toJSONArray(xmlStr);
            assertTrue("Expecting an exception", false);
        } catch (JSONException e) {
            assertTrue("Expecting an exception message",
                "Expected a closing name instead of '>'.".
                equals(e.getMessage()));
        }
    }

    /**
     * Malformed XML text (endtag with no close bracket) is transformed\
     * into a JSONArray. Expects a JSONException.
     */
    @Test
    public void noCloseEndBraceException() {
        /**
         * xmlStr contains XML text which is transformed into a JSONArray.
         * In this case, the XML is invalid because an element
         * has '>' after the closing tag '</' and name.
         */
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation=\"test.xsd\">\n"+
            "    <address>\n"+
            "       <name/>\n"+
            "       <abc/>\n"+
            "    </address\n"+
            "</addresses>";
        try {
            JSONML.toJSONArray(xmlStr);
            assertTrue("Expecting an exception", false);
        } catch (JSONException e) {
            assertTrue("Expecting an exception message",
                "Misplaced '<' at 206 [character 1 line 12]".
                equals(e.getMessage()));
        }
    }

    /**
     * Malformed XML text (incomplete CDATA string) is transformed\
     * into a JSONArray. Expects a JSONException.
     */
    @Test
    public void invalidCDATABangInTagException() {
        /**
         * xmlStr contains XML text which is transformed into a JSONArray.
         * In this case, the XML is invalid because an element
         * does not have a complete CDATA string. 
         */
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
            JSONML.toJSONArray(xmlStr);
            assertTrue("Expecting an exception", false);
        } catch (JSONException e) {
            assertTrue("Expecting an exception message",
                "Expected 'CDATA[' at 204 [character 11 line 9]".
                equals(e.getMessage()));
        }
    }

    /**
     * Convert an XML document into a JSONArray, then use JSONML.toString()
     * to convert it into a string. This string is then converted back into
     * a JSONArray. Both JSONArrays are compared against a control to 
     * confirm the contents.
     */
    @Test
    public void toJSONArray() {
        /**
         * xmlStr contains XML text which is transformed into a JSONArray.
         * Each element becomes a JSONArray:
         * 1st entry = elementname
         * 2nd entry = attributes object (if present)
         * 3rd entry = content (if present)
         * 4th entry = child element JSONArrays (if present)
         * The result is compared against an expected JSONArray.
         * The transformed JSONArray is then transformed back into a string
         * which is used to create a final JSONArray, which is also compared
         * against the expected JSONArray.
         */
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
                 "xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
                 "<address attr1=\"attrValue1\" attr2=\"attrValue2\" attr3=\"attrValue3\">\n"+
                     "<name nameType=\"mine\">myName</name>\n"+
                     "<nocontent/>>\n"+
                 "</address>\n"+
            "</addresses>";
        String expectedStr = 
            "[\"addresses\","+
                "{\"xsi:noNamespaceSchemaLocation\":\"test.xsd\","+
                    "\"xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\"},"+
                "[\"address\","+
                    "{\"attr1\":\"attrValue1\",\"attr2\":\"attrValue2\",\"attr3\":\"attrValue3\"},"+
                    "[\"name\", {\"nameType\":\"mine\"},\"myName\"],"+
                    "[\"nocontent\"],"+
                    "\">\""+
                "]"+
            "]";
        JSONArray jsonArray = JSONML.toJSONArray(xmlStr);
        JSONArray expectedJsonArray = new JSONArray(expectedStr);
        String xmlToStr = JSONML.toString(jsonArray);
        JSONArray finalJsonArray = JSONML.toJSONArray(xmlToStr);
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
        Util.compareActualVsExpectedJsonArrays(finalJsonArray, expectedJsonArray);
    }

    /**
     * Convert an XML document into a JSONObject. Use JSONML.toString() to 
     * convert it back into a string, and then re-convert it into a JSONObject.
     * Both JSONObjects are compared against a control JSONObject to confirm
     * the contents.
     * <p>
     * Next convert the XML document into a JSONArray. Use JSONML.toString() to 
     * convert it back into a string, and then re-convert it into a JSONArray.
     * Both JSONArrays are compared against a control JSONArray to confirm
     * the contents.
     * <p>
     * This test gives a comprehensive example of how the JSONML
     * transformations work.
     */
    @Test
    public void toJSONObjectToJSONArray() {
        /**
         * xmlStr contains XML text which is transformed into a JSONObject,
         * restored to XML, transformed into a JSONArray, and then restored
         * to XML again. Both JSONObject and JSONArray should contain the same 
         * information and should produce the same XML, allowing for non-ordered
         * attributes.
         * 
         * Transformation to JSONObject:
         *      The elementName is stored as a string where key="tagName"
         *      Attributes are simply stored as key/value pairs
         *      If the element has either content or child elements, they are stored
         *      in a jsonArray with key="childNodes".
         * 
         * Transformation to JSONArray:
         *      1st entry = elementname
         *      2nd entry = attributes object (if present)
         *      3rd entry = content (if present)
         *      4th entry = child element JSONArrays (if present)
         */
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
                "xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
                "<address addrType=\"my address\">\n"+
                    "<name nameType=\"my name\">Joe Tester</name>\n"+
                    "<street><![CDATA[Baker street 5]]></street>\n"+
                    "<NothingHere except=\"an attribute\"/>\n"+
                    "<TrueValue>true</TrueValue>\n"+
                    "<FalseValue>false</FalseValue>\n"+
                    "<NullValue>null</NullValue>\n"+
                    "<PositiveValue>42</PositiveValue>\n"+
                    "<NegativeValue>-23</NegativeValue>\n"+
                    "<DoubleValue>-23.45</DoubleValue>\n"+
                    "<Nan>-23x.45</Nan>\n"+
                    "<ArrayOfNum>\n"+
                        "<value>1</value>\n"+
                        "<value>2</value>\n"+
                        "<value><subValue svAttr=\"svValue\">abc</subValue></value>\n"+
                        "<value>3</value>\n"+
                        "<value>4.1</value>\n"+
                        "<value>5.2</value>\n"+
                    "</ArrayOfNum>\n"+
                "</address>\n"+
            "</addresses>";

        String expectedJSONObjectStr =
            "{"+
                "\"xsi:noNamespaceSchemaLocation\":\"test.xsd\","+
                "\"childNodes\":["+
                    "{"+
                        "\"childNodes\":["+
                            "{"+
                                "\"childNodes\":[\"Joe Tester\"],"+
                                "\"nameType\":\"my name\","+
                                "\"tagName\":\"name\""+
                            "},"+
                            "{"+
                                "\"childNodes\":[\"Baker street 5\"],"+
                                "\"tagName\":\"street\""+
                            "},"+
                            "{"+
                                "\"tagName\":\"NothingHere\","+
                                "\"except\":\"an attribute\""+
                            "},"+
                            "{"+
                                "\"childNodes\":[true],"+
                                "\"tagName\":\"TrueValue\""+
                            "},"+
                            "{"+
                                "\"childNodes\":[false],"+
                                "\"tagName\":\"FalseValue\""+
                            "},"+
                            "{"+
                                "\"childNodes\":[null],"+
                                "\"tagName\":\"NullValue\""+
                            "},"+
                            "{"+
                                "\"childNodes\":[42],"+
                                "\"tagName\":\"PositiveValue\""+
                            "},"+
                            "{"+
                                "\"childNodes\":[-23],"+
                                "\"tagName\":\"NegativeValue\""+
                            "},"+
                            "{"+
                                "\"childNodes\":[-23.45],"+
                                "\"tagName\":\"DoubleValue\""+
                            "},"+
                            "{"+
                                "\"childNodes\":[\"-23x.45\"],"+
                                "\"tagName\":\"Nan\""+
                            "},"+
                            "{"+
                                "\"childNodes\":["+
                                    "{"+
                                        "\"childNodes\":[1],"+
                                        "\"tagName\":\"value\""+
                                    "},"+
                                    "{"+
                                        "\"childNodes\":[2],"+
                                        "\"tagName\":\"value\""+
                                    "},"+
                                    "{"+
                                        "\"childNodes\":["+
                                            "{"+
                                                "\"childNodes\":[\"abc\"],"+
                                                "\"svAttr\":\"svValue\","+
                                                "\"tagName\":\"subValue\""+
                                            "}"+
                                        "],"+
                                        "\"tagName\":\"value\""+
                                    "},"+
                                    "{"+
                                        "\"childNodes\":[3],"+
                                        "\"tagName\":\"value\""+
                                    "},"+
                                    "{"+
                                        "\"childNodes\":[4.1],"+
                                        "\"tagName\":\"value\""+
                                    "},"+
                                    "{"+
                                        "\"childNodes\":[5.2],"+
                                        "\"tagName\":\"value\""+
                                    "}"+
                                "],"+
                                "\"tagName\":\"ArrayOfNum\""+
                            "}"+
                        "],"+
                        "\"addrType\":\"my address\","+
                        "\"tagName\":\"address\""+
                    "}"+
                "],"+
                "\"xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\","+
                "\"tagName\":\"addresses\""+
            "}";

        String expectedJSONArrayStr = 
            "["+
                "\"addresses\","+
                "{"+
                    "\"xsi:noNamespaceSchemaLocation\":\"test.xsd\","+
                    "\"xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\""+
                "},"+
                "["+
                    "\"address\","+
                    "{"+
                        "\"addrType\":\"my address\""+
                    "},"+
                    "["+
                        "\"name\","+
                        "{"+
                            "\"nameType\":\"my name\""+
                        "},"+
                        "\"Joe Tester\""+
                    "],"+
                    "[\"street\",\"Baker street 5\"],"+
                    "["+
                        "\"NothingHere\","+
                        "{\"except\":\"an attribute\"}"+
                    "],"+
                    "[\"TrueValue\",true],"+
                    "[\"FalseValue\",false],"+
                    "[\"NullValue\",null],"+
                    "[\"PositiveValue\",42],"+
                    "[\"NegativeValue\",-23],"+
                    "[\"DoubleValue\",-23.45],"+
                    "[\"Nan\",\"-23x.45\"],"+
                    "["+
                        "\"ArrayOfNum\","+
                        "[\"value\",1],"+
                        "[\"value\",2],"+
                        "[\"value\","+
                            "["+
                                "\"subValue\","+
                                "{\"svAttr\":\"svValue\"},"+
                                "\"abc\""+
                            "],"+
                        "],"+
                        "[\"value\",3],"+
                        "[\"value\",4.1],"+
                        "[\"value\",5.2]"+
                    "]"+
                "]"+
            "]";

        // make a JSONObject and make sure it looks as expected
        JSONObject jsonObject = JSONML.toJSONObject(xmlStr);
        JSONObject expectedJsonObject = new JSONObject(expectedJSONObjectStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);

        // restore the XML, then make another JSONObject and make sure it
        // looks as expected
        String jsonObjectXmlToStr = JSONML.toString(jsonObject);
        JSONObject finalJsonObject = JSONML.toJSONObject(jsonObjectXmlToStr);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject, expectedJsonObject);

        // create a JSON array from the original string and make sure it 
        // looks as expected
        JSONArray jsonArray = JSONML.toJSONArray(xmlStr);
        JSONArray expectedJsonArray = new JSONArray(expectedJSONArrayStr);
        Util.compareActualVsExpectedJsonArrays(jsonArray,expectedJsonArray);
    
        // restore the XML, then make another JSONArray and make sure it
        // looks as expected
        String jsonArrayXmlToStr = JSONML.toString(jsonArray);
        JSONArray finalJsonArray = JSONML.toJSONArray(jsonArrayXmlToStr);
        Util.compareActualVsExpectedJsonArrays(finalJsonArray, expectedJsonArray);

        // lastly, confirm the restored JSONObject XML and JSONArray XML look
        // reasonably similar
        JSONObject jsonObjectFromObject = JSONML.toJSONObject(jsonObjectXmlToStr);
        JSONObject jsonObjectFromArray = JSONML.toJSONObject(jsonArrayXmlToStr);
        Util.compareActualVsExpectedJsonObjects(jsonObjectFromObject, jsonObjectFromArray);
    }

    /**
     * Convert an XML document which contains embedded comments into
     * a JSONArray. Use JSONML.toString() to turn it into a string, then
     * reconvert it into a JSONArray. Compare both JSONArrays to a control
     * JSONArray to confirm the contents. 
     * <p>
     * This test shows how XML comments are handled.
     */
    @Test
    public void commentsInXML() {

        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<!-- this is a comment -->\n"+
            "<addresses>\n"+
                "<address>\n"+
                    "<!-- <!--[CDATA[ this is -- <another> comment ]] -->\n"+
                    "<name>Joe Tester</name>\n"+
                    "<!-- this is a - multi line \n"+
                    "comment -->\n"+
                    "<street>Baker street 5</street>\n"+
                "</address>\n"+
            "</addresses>";
        String expectedStr =
            "[\"addresses\","+
                "[\"address\","+
                    "[\"name\",\"Joe Tester\"],"+
                    "[\"street\",\"Baker street 5\"]"+
                "]"+
            "]";
        JSONArray jsonArray = JSONML.toJSONArray(xmlStr);
        JSONArray expectedJsonArray = new JSONArray(expectedStr);
        String xmlToStr = JSONML.toString(jsonArray);
        JSONArray finalJsonArray = JSONML.toJSONArray(xmlToStr);
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
        Util.compareActualVsExpectedJsonArrays(finalJsonArray, expectedJsonArray);
    }

    /**
     * JSON string with lost leading zero and converted "True" to true. See test
     * result in comment below.
     */
    @Test
    public void testToJSONArray_jsonOutput() {
        final String originalXml = "<root><id>01</id><id>1</id><id>00</id><id>0</id><item id=\"01\"/><title>True</title></root>";
        final String expectedJsonString = "[\"root\",[\"id\",\"01\"],[\"id\",1],[\"id\",\"00\"],[\"id\",0],[\"item\",{\"id\":\"01\"}],[\"title\",true]]";
        final JSONArray actualJsonOutput = JSONML.toJSONArray(originalXml, false);
        assertEquals(expectedJsonString, actualJsonOutput.toString());
    }

    /**
     * JSON string cannot be reverted to original xml when type guessing is used.
     */
    @Test
    public void testToJSONArray_reversibility() {
        final String originalXml = "<root><id>01</id><id>1</id><id>00</id><id>0</id><item id=\"01\"/><title>True</title></root>";
        final String revertedXml = JSONML.toString(JSONML.toJSONArray(originalXml, false));
        assertNotEquals(revertedXml, originalXml);
    }

    /**
     * JSON string cannot be reverted to original xml when type guessing is used.
     * When we force all the values as string, the original text comes back.
     */
    @Test
    public void testToJSONArray_reversibility2() {
        final String originalXml = "<root><id>01</id><id>1</id><id>00</id><id>0</id><item id=\"01\"/><title>True</title></root>";
        final String expectedJsonString = "[\"root\",[\"id\",\"01\"],[\"id\",\"1\"],[\"id\",\"00\"],[\"id\",\"0\"],[\"item\",{\"id\":\"01\"}],[\"title\",\"True\"]]";
        final JSONArray json = JSONML.toJSONArray(originalXml,true);
        assertEquals(expectedJsonString, json.toString());
        
        final String reverseXml = JSONML.toString(json);
        assertEquals(originalXml, reverseXml);
    }

    /**
     * JSON can be reverted to original xml.
     */
    @Test
    public void testToJSONArray_reversibility3() {
        final String originalXml = "<readResult><errors someAttr=\"arrtValue\"><code>400</code></errors><errors><code>402</code></errors></readResult>";
        final JSONArray jsonArray = JSONML.toJSONArray(originalXml, false);
        final String revertedXml = JSONML.toString(jsonArray);
        assertEquals(revertedXml, originalXml);
    }
    
    /**
     * JSON string cannot be reverted to original xml. See test result in
     * comment below.
     */
    @Test
    public void testToJSONObject_reversibility() {
        final String originalXml = "<readResult><errors someAttr=\"arrtValue\"><code>400</code></errors><errors><code>402</code></errors></readResult>";
        final JSONObject originalObject=JSONML.toJSONObject(originalXml,false);
        final String originalJson = originalObject.toString();
        final String xml = JSONML.toString(originalObject);
        final JSONObject revertedObject = JSONML.toJSONObject(xml, false);
        final String newJson = revertedObject.toString();
        assertTrue("JSON Objects are not similar",originalObject.similar(revertedObject));
        assertEquals("original JSON does not equal the new JSON",originalJson, newJson);
    }

    /**
     * Test texts taken from jsonml.org. Currently our implementation FAILS this conversion but shouldn't.
     * Technically JsonML should be able to transform any valid xhtml document, but ours only supports
     * standard XML entities, not HTML entities.
     */
    @Test
    public void testAttributeConversionReversabilityHTML() {
        final String originalXml = "<table class=\"MyTable\" style=\"background-color:yellow\"><tr><td class=\"MyTD\" style=\"border:1px solid black\">#5D28D1</td><td class=\"MyTD\" style=\"background-color:red\">Example text here</td></tr><tr><td class=\"MyTD\" style=\"border:1px solid black\">#AF44EF</td><td class=\"MyTD\" style=\"background-color:green\">127310656</td></tr><tr><td class=\"MyTD\" style=\"border:1px solid black\">#AAD034</td><td class=\"MyTD\" style=\"background-color:blue\">&nbsp;<span style=\"background-color:maroon\">&copy;</span>&nbsp;</td></tr></table>";
        final String expectedJsonString = "[\"table\",{\"class\" : \"MyTable\",\"style\" : \"background-color:yellow\"},[\"tr\",[\"td\",{\"class\" : \"MyTD\",\"style\" : \"border:1px solid black\"},\"#550758\"],[\"td\",{\"class\" : \"MyTD\",\"style\" : \"background-color:red\"},\"Example text here\"]],[\"tr\",[\"td\",{\"class\" : \"MyTD\",\"style\" : \"border:1px solid black\"},\"#993101\"],[\"td\",{\"class\" : \"MyTD\",\"style\" : \"background-color:green\"},\"127624015\"]],[\"tr\",[\"td\",{\"class\" : \"MyTD\",\"style\" : \"border:1px solid black\"},\"#E33D87\"],[\"td\",{\"class\" : \"MyTD\",\"style\" : \"background-color:blue\"},\"\u00A0\",[\"span\",{ \"style\" : \"background-color:maroon\" },\"\u00A9\"],\"\u00A0\"]]]";
        final JSONArray json = JSONML.toJSONArray(originalXml,true);
        final String actualJsonString = json.toString();
        
        final String reverseXml = JSONML.toString(json);
        assertNotEquals(originalXml, reverseXml);

        assertNotEquals(expectedJsonString, actualJsonString);
    }
    
// this test does not pass for the following reasons:
// 1. Our XML parser does not handle generic HTML entities, only valid XML entities. Hence &nbsp;
//    or other HTML specific entites would fail on reversability
// 2. Our JSON implementation for storing the XML attributes uses the standard unordered map.
//    This means that <tag attr1="v1" attr2="v2" /> can not be reversed reliably.  
//    /**
//     * Test texts taken from jsonml.org but modified to have XML entities only.
//     */
//    @Test
//    public void testAttributeConversionReversabilityXML() {
//        final String originalXml = "<table class=\"MyTable\" style=\"background-color:yellow\"><tr><td class=\"MyTD\" style=\"border:1px solid black\">#5D28D1</td><td class=\"MyTD\" style=\"background-color:red\">Example text here</td></tr><tr><td class=\"MyTD\" style=\"border:1px solid black\">#AF44EF</td><td class=\"MyTD\" style=\"background-color:green\">127310656</td></tr><tr><td class=\"MyTD\" style=\"border:1px solid black\">#AAD034</td><td class=\"MyTD\" style=\"background-color:blue\">&amp;<span style=\"background-color:maroon\">&gt;</span>&lt;</td></tr></table>";
//        final String expectedJsonString = "[\"table\",{\"class\" : \"MyTable\",\"style\" : \"background-color:yellow\"},[\"tr\",[\"td\",{\"class\" : \"MyTD\",\"style\" : \"border:1px solid black\"},\"#550758\"],[\"td\",{\"class\" : \"MyTD\",\"style\" : \"background-color:red\"},\"Example text here\"]],[\"tr\",[\"td\",{\"class\" : \"MyTD\",\"style\" : \"border:1px solid black\"},\"#993101\"],[\"td\",{\"class\" : \"MyTD\",\"style\" : \"background-color:green\"},\"127624015\"]],[\"tr\",[\"td\",{\"class\" : \"MyTD\",\"style\" : \"border:1px solid black\"},\"#E33D87\"],[\"td\",{\"class\" : \"MyTD\",\"style\" : \"background-color:blue\"},\"&\",[\"span\",{ \"style\" : \"background-color:maroon\" },\">\"],\"<\"]]]";
//        final JSONArray jsonML = JSONML.toJSONArray(originalXml,true);
//        final String actualJsonString = jsonML.toString();
//        
//        final String reverseXml = JSONML.toString(jsonML);
//        // currently not equal because the hashing of the attribute objects makes the attribute
//        // order not happen the same way twice 
//        assertEquals(originalXml, reverseXml);
//
//        assertEquals(expectedJsonString, actualJsonString);
//    }
}
