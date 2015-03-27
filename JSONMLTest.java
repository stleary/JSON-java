package org.json.junit;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;


/**
 * Tests for JSON-Java JSONML.java
 */
public class JSONMLTest {

    @Test(expected=NullPointerException.class)
    public void shouldHandleNullXML() {

        String xmlStr = null;
        JSONObject jsonObject = JSONML.toJSONObject(xmlStr);
        assertTrue("jsonObject should be empty", jsonObject.length() == 0);
    }

    @Test(expected=JSONException.class)
    public void shouldHandleEmptyXML() {

        String xmlStr = "";
        JSONObject jsonObject = JSONML.toJSONObject(xmlStr);
        assertTrue("jsonObject should be empty", jsonObject.length() == 0);
    }

    @Test
    public void shouldHandleNonXML() {
        String xmlStr = "{ \"this is\": \"not xml\"}";
        JSONObject jsonObject = JSONML.toJSONObject(xmlStr);
        assertTrue("xml string should be empty", jsonObject.length() == 0);
    }

    @Test(expected=JSONException.class)
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
        JSONML.toJSONObject(xmlStr);
    }

    @Test(expected=JSONException.class)
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
        JSONML.toJSONObject(xmlStr);
    }

    @Test(expected=JSONException.class)
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
        JSONML.toJSONObject(xmlStr);
    }

    @Test(expected=JSONException.class)
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
        JSONML.toJSONObject(xmlStr);
    }

    @Test(expected=JSONException.class)
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
        JSONML.toJSONObject(xmlStr);
    }

    @Test(expected=NullPointerException.class)
    public void shouldHandleNullJSONXML() {
        JSONObject jsonObject= null;
        JSONML.toString(jsonObject);
    }

    @Test
    public void shouldHandleEmptyJSONXML() {
        JSONObject jsonObject= new JSONObject();
        String xmlStr = JSONML.toString(jsonObject);
        assertTrue("xml string should be empty", xmlStr.length() == 0);
    }

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
        JSONObject jsonObject = JSONML.toJSONObject(xmlStr);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

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
        
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        JSONObject jsonObject = JSONML.toJSONObject(xmlStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

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
        JSONObject jsonObject = JSONML.toJSONObject(xmlStr);
        String expectedStr = "{\"addresses\":{\"address\":{\"street\":\"Baker "+
                "street 5\",\"name\":\"Joe Tester\",\"content\":\" this is -- "+
                "<another> comment \"}}}";
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

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
        
        JSONObject jsonObject = JSONML.toJSONObject(xmlStr);
        String xmlToStr = JSONML.toString(jsonObject);
        JSONObject finalJsonObject = JSONML.toJSONObject(xmlToStr);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }

    @Test
    public void shouldHandleContentNoArraytoString() {
        String expectedStr = 
            "{\"addresses\":{\"address\":{\"name\":\"\",\"nocontent\":\"\",\""+
            "content\":\">\"},\"xsi:noNamespaceSchemaLocation\":\"test.xsd\",\""+
            "xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\"}}";
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        String finalStr = JSONML.toString(expectedJsonObject);
        String expectedFinalStr = "<addresses><address><name/><nocontent/>&gt;"+
                "</address><xsi:noNamespaceSchemaLocation>test.xsd</xsi:noName"+
                "spaceSchemaLocation><xmlns:xsi>http://www.w3.org/2001/XMLSche"+
                "ma-instance</xmlns:xsi></addresses>";
        assertTrue("Should handle expectedFinal: ["+expectedStr+"] final: ["+
                finalStr+"]", expectedFinalStr.equals(finalStr));
    }

    @Test
    public void shouldHandleContentArraytoString() {
        String expectedStr = 
            "{\"addresses\":{\"address\":{\"name\":\"\",\"nocontent\":\"\",\""+
            "content\":[1, 2, 3]},\"xsi:noNamespaceSchemaLocation\":\"test.xsd\",\""+
            "xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\"}}";
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        String finalStr = JSONML.toString(expectedJsonObject);
        String expectedFinalStr = "<addresses><address><name/><nocontent/>"+
                "1\n2\n3"+
                "</address><xsi:noNamespaceSchemaLocation>test.xsd</xsi:noName"+
                "spaceSchemaLocation><xmlns:xsi>http://www.w3.org/2001/XMLSche"+
                "ma-instance</xmlns:xsi></addresses>";
        assertTrue("Should handle expectedFinal: ["+expectedStr+"] final: ["+
                finalStr+"]", expectedFinalStr.equals(finalStr));
    }

    @Test
    public void shouldHandleArraytoString() {
        String expectedStr = 
            "{\"addresses\":{\"address\":{\"name\":\"\",\"nocontent\":\"\","+
            "\"something\":[1, 2, 3]},\"xsi:noNamespaceSchemaLocation\":\"test.xsd\",\""+
            "xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\"}}";
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        String finalStr = JSONML.toString(expectedJsonObject);
        String expectedFinalStr = "<addresses><address><name/><nocontent/>"+
                "<something>1</something><something>2</something><something>3</something>"+
                "</address><xsi:noNamespaceSchemaLocation>test.xsd</xsi:noName"+
                "spaceSchemaLocation><xmlns:xsi>http://www.w3.org/2001/XMLSche"+
                "ma-instance</xmlns:xsi></addresses>";
        assertTrue("Should handle expectedFinal: ["+expectedStr+"] final: ["+
                finalStr+"]", expectedFinalStr.equals(finalStr));
    }

    @Test
    public void shouldHandleNestedArraytoString() {
        String xmlStr = 
            "{\"addresses\":{\"address\":{\"name\":\"\",\"nocontent\":\"\","+
            "\"outer\":[[1], [2], [3]]},\"xsi:noNamespaceSchemaLocation\":\"test.xsd\",\""+
            "xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\"}}";
        JSONObject jsonObject = new JSONObject(xmlStr);
        String finalStr = JSONML.toString(jsonObject);
        JSONObject finalJsonObject = JSONML.toJSONObject(finalStr);
        String expectedStr = "<addresses><address><name/><nocontent/>"+
                "<outer><array>1</array></outer><outer><array>2</array>"+
                "</outer><outer><array>3</array></outer>"+
                "</address><xsi:noNamespaceSchemaLocation>test.xsd</xsi:noName"+
                "spaceSchemaLocation><xmlns:xsi>http://www.w3.org/2001/XMLSche"+
                "ma-instance</xmlns:xsi></addresses>";
        JSONObject expectedJsonObject = JSONML.toJSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }

}
