package org.json.junit;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;


/**
 * Tests for JSON-Java JSONML.java
 */
public class JSONMLTest {

    @Test(expected=NullPointerException.class)
    public void nullXMLException() {

        String xmlStr = null;
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=JSONException.class)
    public void emptyXMLException() {

        String xmlStr = "";
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=JSONException.class)
    public void nonXMLException() {
        String xmlStr = "{ \"this is\": \"not xml\"}";
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=JSONException.class)
    public void emptyTagException() {
        String jsonArrayStr =
            "[\"addresses\","+
                "{\"xsi:noNamespaceSchemaLocation\":\"test.xsd\","+
                    "\"xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\"},"+
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
        String jsonArrayStr =
            "[\"addresses\","+
                "{\"xsi:noNamespaceSchemaLocation\":\"test.xsd\","+
                    "\"xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\"},"+
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
    public void unvalidSlashInTagException() {
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "    <address>\n"+
            "       <name/x>\n"+
            "       <street>abc street</street>\n"+
            "   </address>\n"+
            "</addresses>";
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=JSONException.class)
    public void invalidBangInTagException() {
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "    <address>\n"+
            "       <name/>\n"+
            "       <!>\n"+
            "   </address>\n"+
            "</addresses>";
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=JSONException.class)
    public void invalidBangNoCloseInTagException() {
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "    <address>\n"+
            "       <name/>\n"+
            "       <!\n"+
            "   </address>\n"+
            "</addresses>";
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=JSONException.class)
    public void noCloseStartTagException() {
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            "   xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
            "    <address>\n"+
            "       <name/>\n"+
            "       <abc\n"+
            "   </address>\n"+
            "</addresses>";
        JSONML.toJSONArray(xmlStr);
    }

    @Test(expected=JSONException.class)
    public void invalidCDATABangInTagException() {
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
        JSONArray jsonArray= null;
        JSONML.toString(jsonArray);
    }

    @Test(expected=JSONException.class)
    public void emptyJSONXMLException() {
        JSONArray jsonArray = new JSONArray();
        JSONML.toString(jsonArray);
    }

    @Test
    public void complexTypeXML() {
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
                 "xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
                 "<address>\n"+
                     "<name/>\n"+
                     "<nocontent/>>\n"+
                 "</address>\n"+
            "</addresses>";
        String expectedStr = 
            "[\"addresses\","+
                "{\"xsi:noNamespaceSchemaLocation\":\"test.xsd\","+
                    "\"xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\"},"+
                "[\"address\","+
                    "[\"name\"],"+
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
    public void basicXMLAsObject() {
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
                "xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
                "<address>\n"+
                    "<name>Joe Tester</name>\n"+
                    "<street>[CDATA[Baker street 5]</street>\n"+
                    "<NothingHere/>\n"+
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
                        "<value>3</value>\n"+
                        "<value>4.1</value>\n"+
                        "<value>5.2</value>\n"+
                    "</ArrayOfNum>\n"+
                "</address>\n"+
            "</addresses>";

        String expectedStr =
            "{\"xsi:noNamespaceSchemaLocation\":\"test.xsd\","+
                "\"childNodes\":["+
                    "{\"childNodes\":["+
                        "{\"childNodes\":[\"Joe Tester\"],"+
                            "\"tagName\":\"name\"},"+
                        "{\"childNodes\":[\"[CDATA[Baker street 5]\"],"+
                            "\"tagName\":\"street\"},"+
                        "{\"tagName\":\"NothingHere\"},"+
                            "{\"childNodes\":[true],"+
                        "\"tagName\":\"TrueValue\"},"+
                            "{\"childNodes\":[false],"+
                        "\"tagName\":\"FalseValue\"},"+
                            "{\"childNodes\":[null],"+
                        "\"tagName\":\"NullValue\"},"+
                            "{\"childNodes\":[42],"+
                        "\"tagName\":\"PositiveValue\"},"+
                            "{\"childNodes\":[-23],"+
                        "\"tagName\":\"NegativeValue\"},"+
                            "{\"childNodes\":[-23.45],"+
                        "\"tagName\":\"DoubleValue\"},"+
                            "{\"childNodes\":[\"-23x.45\"],"+
                        "\"tagName\":\"Nan\"},"+
                            "{\"childNodes\":["+
                                "{\"childNodes\":[1],"+
                                    "\"tagName\":\"value\"},"+
                                "{\"childNodes\":[2],"+
                                    "\"tagName\":\"value\"},"+
                                "{\"childNodes\":[3],"+
                                    "\"tagName\":\"value\"},"+
                                "{\"childNodes\":[4.1],"+
                                    "\"tagName\":\"value\"},"+
                                "{\"childNodes\":[5.2],"+
                                    "\"tagName\":\"value\"}"+
                            "],"+
                        "\"tagName\":\"ArrayOfNum\"}"+
                    "],"+
                    "\"tagName\":\"address\"}"+
                "],"+
                "\"xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\","+
                    "\"tagName\":\"addresses\"}";
        JSONObject jsonObject = JSONML.toJSONObject(xmlStr);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        String xmlToStr = JSONML.toString(jsonObject);
        JSONObject finalJsonObject = JSONML.toJSONObject(xmlToStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject, expectedJsonObject);
    }

    @Test
    public void basicXMLAsArray() {
        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<addresses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
                "xsi:noNamespaceSchemaLocation='test.xsd'>\n"+
                "<address>\n"+
                    "<name>Joe Tester</name>\n"+
                    "<street>[CDATA[Baker street 5]</street>\n"+
                    "<NothingHere/>\n"+
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
                        "<value>3</value>\n"+
                        "<value>4.1</value>\n"+
                        "<value>5.2</value>\n"+
                    "</ArrayOfNum>\n"+
                "</address>\n"+
            "</addresses>";

        String expectedStr = 
            "[\"addresses\","+
                "{\"xsi:noNamespaceSchemaLocation\":\"test.xsd\","+
                "\"xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\"},"+
                "[\"address\","+
                    "[\"name\",\"Joe Tester\"],"+
                    "[\"street\",\"[CDATA[Baker street 5]\"],"+
                    "[\"NothingHere\"],"+
                    "[\"TrueValue\",true],"+
                    "[\"FalseValue\",false],"+
                    "[\"NullValue\",null],"+
                    "[\"PositiveValue\",42],"+
                    "[\"NegativeValue\",-23],"+
                    "[\"DoubleValue\",-23.45],"+
                    "[\"Nan\",\"-23x.45\"],"+
                    "[\"ArrayOfNum\","+
                        "[\"value\",1],"+
                        "[\"value\",2],"+
                        "[\"value\",3],"+
                        "[\"value\",4.1],"+
                        "[\"value\",5.2]"+
                    "]"+
                "]"+
            "]";
        JSONArray jsonArray = JSONML.toJSONArray(xmlStr);
        JSONArray expectedJsonArray = new JSONArray(expectedStr);
        String xmlToStr = JSONML.toString(jsonArray);
        JSONArray finalJsonArray = JSONML.toJSONArray(xmlToStr);
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
        // TODO: this test fails because JSONML.toString() does not emit values
        // for true, false, null, and numbers
        // Util.compareActualVsExpectedJsonArrays(finalJsonArray, expectedJsonArray);
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
