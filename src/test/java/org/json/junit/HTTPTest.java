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

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;


/**
 * Unit tests for JSON-Java HTTP.java. See RFC7230.
 */
public class HTTPTest {

    /**
     * Attempt to call HTTP.toJSONObject() with a null string
     * Expects a NUllPointerException.
     */
    @Test(expected=NullPointerException.class)
    public void nullHTTPException() {
        String httpStr = null;
        HTTP.toJSONObject(httpStr);
    }

    /**
     * Attempt to call HTTP.toJSONObject() with a string containing
     * an empty object. Expects a JSONException.
     */
    @Test
    public void notEnoughHTTPException() {
        String httpStr = "{}";
        JSONObject jsonObject = new JSONObject(httpStr);
        try {
            HTTP.toString(jsonObject);
            assertTrue("Expected to throw exception", false);
        } catch (JSONException e) {
            assertTrue("Expecting an exception message",
                    "Not enough material for an HTTP header.".equals(e.getMessage()));
        }
    }

    /**
     * Calling HTTP.toJSONObject() with an empty string will result in a 
     * populated JSONObject with keys but no values for Request-URI, Method,
     * and HTTP-Version.
     */
    @Test
    public void emptyStringHTTPRequest() {
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
    public void simpleHTTPRequest() {
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
    public void simpleHTTPResponse() {
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

    /**
     * Call HTTP.toJSONObject() with a full POST request string including
     * response headers, then convert it back into an HTTP string.
     */
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
