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
        JSONML.toJSONObject(xmlStr);
    }

    @Test(expected=JSONException.class)
    public void emptyXMLException() {

        String xmlStr = "";
        JSONML.toJSONObject(xmlStr);
    }

    @Test(expected=JSONException.class)
    public void nonXMLException() {
        String xmlStr = "{ \"this is\": \"not xml\"}";
        JSONML.toJSONObject(xmlStr);
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
        JSONML.toJSONObject(xmlStr);
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
        JSONML.toJSONObject(xmlStr);
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
        JSONML.toJSONObject(xmlStr);
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
        JSONML.toJSONObject(xmlStr);
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
        JSONML.toJSONObject(xmlStr);
    }

    @Test(expected=NullPointerException.class)
    public void nullJSONXMLException() {
        JSONObject jsonObject= null;
        JSONML.toString(jsonObject);
    }

    @Test(expected=JSONException.class)
    public void emptyJSONXMLException() {
        JSONObject jsonObject= new JSONObject();
        JSONML.toString(jsonObject);
    }

    @Test
    public void noStartTag() {
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
            "{\"xsi:noNamespaceSchemaLocation\":\"test.xsd\","+
                "\"childNodes\":[{"+
                    "\"childNodes\":"+
                        "[{\"tagName\":\"name\"},"+
                        "{\"tagName\":\"nocontent\"},"+
                        "\">\"],"+
                        "\"tagName\":\"address\"}],"+
                "\"xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\","+
                "\"tagName\":\"addresses\"}";
        JSONObject jsonObject = JSONML.toJSONObject(xmlStr);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    @Test
    public void simpleXML() {
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
                    "<ArrayOfNum>1, 2, 3, 4.1, 5.2</ArrayOfNum>\n"+
                "</address>\n"+
            "</addresses>";

        String expectedStr = 
            "{\"xsi:noNamespaceSchemaLocation\":\"test.xsd\","+
            "\"childNodes\":[{"+
                "\"childNodes\":["+
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
                    "{\"childNodes\":[\"1, 2, 3, 4.1, 5.2\"],"+
                        "\"tagName\":\"ArrayOfNum\"}],"+
                "\"tagName\":\"address\"}],"+
            "\"xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\","+
            "\"tagName\":\"addresses\"}";
        JSONObject jsonObject = JSONML.toJSONObject(xmlStr);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    @Test
    public void commentsInXML() {

        String xmlStr = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<!-- this is a comment -->\n"+
            "<addresses>\n"+
                "<address>\n"+
                    "<![CDATA[ this is -- <another> comment ]]>\n"+
                    "<name>Joe Tester</name>\n"+
                    "<!-- this is a - multi line \n"+
                    "comment -->\n"+
                    "<street>Baker street 5</street>\n"+
                "</address>\n"+
            "</addresses>";
        String expectedStr = 
            "{\"childNodes\":["+
                "{\"childNodes\":["+
                    "\" this is -- <another> comment \","+
                    "{\"childNodes\":[\"Joe Tester\"],"+
                        "\"tagName\":\"name\"},"+
                    "{\"childNodes\":[\"Baker street 5\"],"+
                        "\"tagName\":\"street\"}],"+
                    "\"tagName\":\"address\"}],"+
                "\"tagName\":\"addresses\"}";
        JSONObject jsonObject = JSONML.toJSONObject(xmlStr);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    @Test
    public void jsonObjectToString() {
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
            "{\"xsi:noNamespaceSchemaLocation\":\"test.xsd\","+
                "\"childNodes\":["+
                    "{\"childNodes\":["+
                        "{\"childNodes\":[\"[CDATA[Joe & T > e < s \\\" t ' er]]\"],"+
                            "\"tagName\":\"name\"},"+
                        "{\"childNodes\":[\"Baker street 5\"],"+
                            "\"tagName\":\"street\"},"+
                        "{\"childNodes\":[\"1, 2, 3, 4.1, 5.2\"],"+
                            "\"tagName\":\"ArrayOfNum\"}],"+
                        "\"tagName\":\"address\"}],"+
                        "\"xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\","+
                        "\"tagName\":\"addresses\"}";
        
        JSONObject jsonObject = JSONML.toJSONObject(xmlStr);
        String xmlToStr = JSONML.toString(jsonObject);
        JSONObject finalJsonObject = JSONML.toJSONObject(xmlToStr);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }

    @Test
    public void jsonArrayToString() {
        String xmlStr =
            "<tag0>"+
                "<tag1>"+
                    "<tag2>5</tag2>"+
                    "<tag2>10</tag2>"+
                    "<tag2>15</tag2>"+
                "</tag1>"+
                "<tag2>val2</tag2>"+
                "<tag3>val3</tag3>"+
                "<tag4>"+
                    "<tag5>-6</tag5>"+
                    "<tag5>true</tag5>"+
                "</tag4>"+
                "<tag6>false</tag6>"+
                "<tag7>null</tag7>"+
                "<tag1>"+
                    "<tag8>10</tag8>"+
                    "<tag8>20</tag8>"+
                    "<tag8>33.33</tag8>"+
                    "<tag8>5220</tag8>"+
                "</tag1>"+
            "</tag0>";
        String expectedStr =
            "[\"tag0\","+
                "[\"tag1\","+
                    "[\"tag2\",5],"+
                    "[\"tag2\",10],"+
                    "[\"tag2\",15]"+
                "],"+
                "[\"tag2\",\"val2\"],"+
                "[\"tag3\",\"val3\"],"+
                "[\"tag4\","+
                    "[\"tag5\",-6],"+
                    "[\"tag5\",true]"+
                "],"+
                "[\"tag6\",false],"+
                "[\"tag7\",null],"+
                "[\"tag1\","+
                    "[\"tag8\",10],"+
                    "[\"tag8\",20],"+
                    "[\"tag8\",33.33],"+
                    "[\"tag8\",5220]"+
                "]"+
            "]";
        JSONArray jsonArray = JSONML.toJSONArray(xmlStr);
        String xmlToStr = JSONML.toString(jsonArray);
        JSONArray finalJsonArray = JSONML.toJSONArray(xmlToStr);
        JSONArray expectedJsonArray= new JSONArray(expectedStr);
        Util.compareActualVsExpectedJsonArrays(jsonArray,expectedJsonArray);
        Util.compareActualVsExpectedJsonArrays(finalJsonArray,expectedJsonArray);
    }

}
