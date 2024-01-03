package org.json.junit;

/*
Public Domain.
*/

import static org.junit.jupiter.api.Assertions.*;

import org.json.*;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for JSON-Java HTTP.java. See RFC7230.
 */
class HTTPTest {

    /**
     * Attempt to call HTTP.toJSONObject() with a null string
     * Expects a NUllPointerException.
     */
    @Test
    void nullHTTPException() {
        assertThrows(NullPointerException.class, () -> {
            String httpStr = null;
            HTTP.toJSONObject(httpStr);
        });
    }

    /**
     * Attempt to call HTTP.toJSONObject() with a string containing
     * an empty object. Expects a JSONException.
     */
    @Test
    void notEnoughHTTPException() {
        String httpStr = "{}";
        JSONObject jsonObject = new JSONObject(httpStr);
        try {
            HTTP.toString(jsonObject);
            assertTrue(false, "Expected to throw exception");
        } catch (JSONException e) {
            assertEquals("Not enough material for an HTTP header.", e.getMessage(), "Expecting an exception message");
        }
    }

    /**
     * Calling HTTP.toJSONObject() with an empty string will result in a 
     * populated JSONObject with keys but no values for Request-URI, Method,
     * and HTTP-Version.
     */
    @Test
    void emptyStringHTTPRequest() {
        String httpStr = "";
        String expectedHTTPStr = "{\"Request-URI\":\"\",\"Method\":\"\",\"HTTP-Version\":\"\"}";
        JSONObject jsonObject = HTTP.toJSONObject(httpStr);
        JSONObject expectedJsonObject = new JSONObject(expectedHTTPStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    /**
     * Call HTTP.toJSONObject() with a Request-URI, Method,
     * and HTTP-Version.
     */
    @Test
    void simpleHTTPRequest() {
        String httpStr = "GET /hello.txt HTTP/1.1";
        String expectedHTTPStr = 
            "{\"Request-URI\":\"/hello.txt\",\"Method\":\"GET\",\"HTTP-Version\":\"HTTP/1.1\"}";
        JSONObject jsonObject = HTTP.toJSONObject(httpStr);
        JSONObject expectedJsonObject = new JSONObject(expectedHTTPStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    /**
     * Call HTTP.toJSONObject() with a response string containing a
     * HTTP-Version, Status-Code, and Reason.
     */
    @Test
    void simpleHTTPResponse() {
        String httpStr = "HTTP/1.1 200 OK";
        String expectedHTTPStr = 
            "{\"HTTP-Version\":\"HTTP/1.1\",\"Status-Code\":\"200\",\"Reason-Phrase\":\"OK\"}";
        JSONObject jsonObject = HTTP.toJSONObject(httpStr);
        JSONObject expectedJsonObject = new JSONObject(expectedHTTPStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    /**
     * Call HTTP.toJSONObject() with a full request string including
     * request headers. 
     */
    @Test
    void extendedHTTPRequest() {
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
        /**
         * Not too easy for JSONObject to parse a string with embedded quotes.
         * For the sake of the test, add it here.
         */
        expectedJsonObject.put("SOAPAction","\"http://clearforest.com/Enlighten\"");
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    /**
     * Call HTTP.toJSONObject() with a full response string including
     * response headers. 
     */
    @Test
    void extendedHTTPResponse() {
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

    /**
     * Call HTTP.toJSONObject() with a full POST request string including
     * response headers, then convert it back into an HTTP string.
     */
    @Test
    void convertHTTPRequestToString() {
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
        /**
         * JSONObject objects to crlfs and any trailing chars.
         * For the sake of the test, simplify the resulting string
         */
        httpToStr = httpToStr.replaceAll("("+HTTP.CRLF+HTTP.CRLF+")", "");
        httpToStr = httpToStr.replaceAll(HTTP.CRLF, "\n");
        JSONObject finalJsonObject = HTTP.toJSONObject(httpToStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }

    /**
     * Call HTTP.toJSONObject() with a full response string including
     * response headers, then convert it back into an HTTP string.
     */
    @Test
    void convertHTTPResponseToString() {
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
        /**
         * JSONObject objects to crlfs and any trailing chars.
         * For the sake of the test, simplify the resulting string
         */
        httpToStr = httpToStr.replaceAll("("+HTTP.CRLF+HTTP.CRLF+")", "");
        httpToStr = httpToStr.replaceAll(HTTP.CRLF, "\n");
        JSONObject finalJsonObject = HTTP.toJSONObject(httpToStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }
}
