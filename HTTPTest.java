package org.json.junit;

import org.json.*;
import org.junit.Test;


/**
 * Tests for JSON-Java HTTP.java
 * See RFC7230
 */
public class HTTPTest {

    @Test(expected=NullPointerException.class)
    public void nullHTTPException() {
        String httpStr = null;
        HTTP.toJSONObject(httpStr);
    }

    @Test(expected=JSONException.class)
    public void notEnoughHTTPException() {
        String httpStr = "{}";
        JSONObject jsonObject = new JSONObject(httpStr);
        HTTP.toString(jsonObject);
    }

    @Test
    public void emptyStringHTTPException() {
        String httpStr = "";
        String expectedHTTPStr = "{\"Request-URI\":\"\",\"Method\":\"\",\"HTTP-Version\":\"\"}";
        JSONObject jsonObject = HTTP.toJSONObject(httpStr);
        JSONObject expectedJsonObject = new JSONObject(expectedHTTPStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    @Test
    public void simpleHTTPRequest() {
        String httpStr = "GET /hello.txt HTTP/1.1";
        String expectedHTTPStr = 
            "{\"Request-URI\":\"/hello.txt\",\"Method\":\"GET\",\"HTTP-Version\":\"HTTP/1.1\"}";
        JSONObject jsonObject = HTTP.toJSONObject(httpStr);
        JSONObject expectedJsonObject = new JSONObject(expectedHTTPStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    @Test
    public void simpleHTTPResponse() {
        String httpStr = "HTTP/1.1 200 OK";
        String expectedHTTPStr = 
            "{\"HTTP-Version\":\"HTTP/1.1\",\"Status-Code\":\"200\",\"Reason-Phrase\":\"OK\"}";
        JSONObject jsonObject = HTTP.toJSONObject(httpStr);
        JSONObject expectedJsonObject = new JSONObject(expectedHTTPStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    @Test
    public void extendedHTTPRequest() {
        String httpStr = 
            "POST /enlighten/calais.asmx HTTP/1.1\n"+
            "Host: api.opencalais.com\n"+
            "Content-Type: text/xml; charset=utf-8\n"+
            "Content-Length: 100\n"+
            "SOAPAction: \"http://clearforest.com/Enlighten\"";
        String expectedHTTPStr = 
            "{"+
            "\"Request-URI\":\"/enlighten/calais.asmx\","+
            "\"Host\":\"api.opencalais.com\","+
            "\"Method\":\"POST\","+
            "\"HTTP-Version\":\"HTTP/1.1\","+
            "\"Content-Length\":\"100\","+
            "\"Content-Type\":\"text/xml; charset=utf-8\"}";
        JSONObject jsonObject = HTTP.toJSONObject(httpStr);
        JSONObject expectedJsonObject = new JSONObject(expectedHTTPStr);
        // not too easy for JSONObject to parse a string with embedded quotes
        expectedJsonObject.put("SOAPAction","\"http://clearforest.com/Enlighten\"");
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    @Test
    public void extendedHTTPResponse() {
        String httpStr = 
            "HTTP/1.1 200 OK\n"+
            "Content-Type: text/xml; charset=utf-8\n"+
            "Content-Length: 100\n";
        String expectedHTTPStr = 
            "{\"HTTP-Version\":\"HTTP/1.1\","+
            "\"Status-Code\":\"200\","+
            "\"Content-Length\":\"100\","+
            "\"Reason-Phrase\":\"OK\","+
            "\"Content-Type\":\"text/xml; charset=utf-8\"}";
        JSONObject jsonObject = HTTP.toJSONObject(httpStr);
        JSONObject expectedJsonObject = new JSONObject(expectedHTTPStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    @Test
    public void convertHTTPRequestToString() {
        String httpStr = 
            "POST /enlighten/calais.asmx HTTP/1.1\n"+
            "Host: api.opencalais.com\n"+
            "Content-Type: text/xml; charset=utf-8\n"+
            "Content-Length: 100";
        String expectedHTTPStr = 
            "{"+
            "\"Request-URI\":\"/enlighten/calais.asmx\","+
            "\"Host\":\"api.opencalais.com\","+
            "\"Method\":\"POST\","+
            "\"HTTP-Version\":\"HTTP/1.1\","+
            "\"Content-Length\":\"100\","+
            "\"Content-Type\":\"text/xml; charset=utf-8\"}";
        JSONObject jsonObject = HTTP.toJSONObject(httpStr);
        JSONObject expectedJsonObject = new JSONObject(expectedHTTPStr);
        String httpToStr = HTTP.toString(jsonObject);
        // JSONObject objects to crlfs and any trailing chars
        // httpToStr = httpToStr.replaceAll("(\r\n\r\n)", "");
        httpToStr = httpToStr.replaceAll("("+HTTP.CRLF+HTTP.CRLF+")", "");
        httpToStr = httpToStr.replaceAll(HTTP.CRLF, "\n");
        JSONObject finalJsonObject = HTTP.toJSONObject(httpToStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }

    @Test
    public void convertHTTPResponseToString() {
        String httpStr = 
                "HTTP/1.1 200 OK\n"+
                "Content-Type: text/xml; charset=utf-8\n"+
                "Content-Length: 100\n";
            String expectedHTTPStr = 
                "{\"HTTP-Version\":\"HTTP/1.1\","+
                "\"Status-Code\":\"200\","+
                "\"Content-Length\":\"100\","+
                "\"Reason-Phrase\":\"OK\","+
                "\"Content-Type\":\"text/xml; charset=utf-8\"}";
        JSONObject jsonObject = HTTP.toJSONObject(httpStr);
        JSONObject expectedJsonObject = new JSONObject(expectedHTTPStr);
        String httpToStr = HTTP.toString(jsonObject);
        // JSONObject objects to crlfs and any trailing chars
        // httpToStr = httpToStr.replaceAll("(\r\n\r\n)", "");
        httpToStr = httpToStr.replaceAll("("+HTTP.CRLF+HTTP.CRLF+")", "");
        httpToStr = httpToStr.replaceAll(HTTP.CRLF, "\n");
        JSONObject finalJsonObject = HTTP.toJSONObject(httpToStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }
}
