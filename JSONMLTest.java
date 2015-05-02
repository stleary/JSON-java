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

    @Test(expected=NullPointerException.class)
    public void nullXMLException() {
        /**
         * Attempts to transform a null XML string to JSON
         */
        String xmlStr = null;
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=JSONException.class)
    public void emptyXMLException() {
        /**
         * Attempts to transform an empty XML string to JSON
         */
        String xmlStr = "";
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=JSONException.class)
    public void nonXMLException() {
        /**
         * Attempts to transform a nonXML string to JSON
         */
        String xmlStr = "{ \"this is\": \"not xml\"}";
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=JSONException.class)
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
        JSONML.toString(jsonArray);
    }

    @Test(expected=JSONException.class)
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
        JSONML.toString(jsonArray);
    }

    @Test(expected=JSONException.class)
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
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=JSONException.class)
    public void invalidBangInTagException() {
        /**
         * xmlStr contains XML text which is transformed into a JSONArray.
         * In this case, the XML is invalid because an element
         * has the invalid name '!'.
         */
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "    <address>\n"+
            "       <name/>\n"+
            "       <!>\n"+
            "    </address>\n"+
            "</addresses>";
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=JSONException.class)
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
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=JSONException.class)
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
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=JSONException.class)
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
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=JSONException.class)
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
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=JSONException.class)
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
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=NullPointerException.class)
    public void nullJSONXMLException() {
        /**
         * Tries to convert a null JSONArray to XML.
         */
        JSONArray jsonArray= null;
        JSONML.toString(jsonArray);
    }

    @Test(expected=JSONException.class)
    public void emptyJSONXMLException() {
        /**
         * Tries to convert an empty JSONArray to XML.
         */
        JSONArray jsonArray = new JSONArray();
        JSONML.toString(jsonArray);
    }

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
        Util.compareXML(jsonObjectXmlToStr, jsonArrayXmlToStr);
    }


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

}
