/*
 * File: TestHTTP.java Author: JSON.org
 */
package org.json.tests;

import org.json.HTTP;
import org.json.JSONObject;

import junit.framework.TestCase;

/**
 * The Class TestHTTP.
 */
public class TestHTTP extends TestCase
{

    /** The jsonobject. */
    JSONObject jsonobject = new JSONObject();

    /**
     * Tests the stub method.
     */
    public void testToJsonObject_Request()
    {
        try
        {
            jsonobject = HTTP
                    .toJSONObject("GET / HTTP/1.0\nAccept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\nAccept-Language: en-us\nUser-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\nHost: www.nokko.com\nConnection: keep-alive\nAccept-encoding: gzip, deflate\n");
            assertEquals(
                    "{\n  \"Accept-Language\": \"en-us\",\n  \"Request-URI\": \"/\",\n  \"Host\": \"www.nokko.com\",\n  \"Method\": \"GET\",\n  \"Accept-encoding\": \"gzip, deflate\",\n  \"User-Agent\": \"Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\",\n  \"HTTP-Version\": \"HTTP/1.0\",\n  \"Connection\": \"keep-alive\",\n  \"Accept\": \"image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\"\n}",
                    jsonobject.toString(2));
            assertEquals(
                    "GET \"/\" HTTP/1.0\r\n"
                            + "Accept-Language: en-us\r\n"
                            + "Host: www.nokko.com\r\n"
                            + "Accept-encoding: gzip, deflate\r\n"
                            + "User-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\r\n"
                            + "Connection: keep-alive\r\n"
                            + "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\r\n\r\n",
                    HTTP.toString(jsonobject));

        } catch (Exception e)
        {
            fail(e.toString());
        }
    }

    /**
     * Tests the toJsonObject method using response.
     */
    public void testToJsonObject_Response()
    {
        try
        {
            jsonobject = HTTP
                    .toJSONObject("HTTP/1.1 200 Oki Doki\nDate: Sun, 26 May 2002 17:38:52 GMT\nServer: Apache/1.3.23 (Unix) mod_perl/1.26\nKeep-Alive: timeout=15, max=100\nConnection: Keep-Alive\nTransfer-Encoding: chunked\nContent-Type: text/html\n");
            assertEquals(
                    "{\n  \"Reason-Phrase\": \"Oki Doki\",\n  \"Status-Code\": \"200\",\n  \"Transfer-Encoding\": \"chunked\",\n  \"Date\": \"Sun, 26 May 2002 17:38:52 GMT\",\n  \"Keep-Alive\": \"timeout=15, max=100\",\n  \"HTTP-Version\": \"HTTP/1.1\",\n  \"Content-Type\": \"text/html\",\n  \"Connection\": \"Keep-Alive\",\n  \"Server\": \"Apache/1.3.23 (Unix) mod_perl/1.26\"\n}",
                    jsonobject.toString(2));
            assertEquals("HTTP/1.1 200 Oki Doki\r\n"
                    + "Transfer-Encoding: chunked\r\n"
                    + "Date: Sun, 26 May 2002 17:38:52 GMT\r\n"
                    + "Keep-Alive: timeout=15, max=100\r\n"
                    + "Content-Type: text/html\r\n"
                    + "Connection: Keep-Alive\r\n"
                    + "Server: Apache/1.3.23 (Unix) mod_perl/1.26\r\n\r\n",
                    HTTP.toString(jsonobject));
        } catch (Exception e)
        {
            fail(e.toString());
        }
    }
    
    /**
     * Tests the toString method using null key.
     */
    public void testToString_NullKey()
    {
        try
        {
            jsonobject = new JSONObject("{\n  \"Reason-Phrase\": \"Oki Doki\",\n  \"Status-Code\": \"200\",\n  \"Transfer-Encoding\": \"chunked\",\n  \"Date\": \"Sun, 26 May 2002 17:38:52 GMT\",\n  \"Keep-Alive\": \"timeout=15, max=100\",\n  \"HTTP-Version\": \"HTTP/1.1\",\n  \"Content-Type\": \"text/html\",\n  \"Connection\": \"Keep-Alive\",\n  \"Server\": \"Apache/1.3.23 (Unix) mod_perl/1.26\"\n}");
            jsonobject.put("testKey", JSONObject.NULL);
            assertEquals("HTTP/1.1 200 Oki Doki\r\nDate: Sun, 26 May 2002 17:38:52 GMT\r\nTransfer-Encoding: chunked\r\nKeep-Alive: timeout=15, max=100\r\nConnection: Keep-Alive\r\nContent-Type: text/html\r\nServer: Apache/1.3.23 (Unix) mod_perl/1.26\r\n\r\n", HTTP.toString(jsonobject));
        } catch (Exception e)
        {
            fail(e.toString());
        }
    }
    
    /**
     * Tests the toString method using status code but no reason phrase.
     */
    public void testToString_StatusCodeButNoReasonPhrase()
    {
        try
        {
            jsonobject = new JSONObject("{\n  \"Status-Code\": \"200\",\n  \"Transfer-Encoding\": \"chunked\",\n  \"Date\": \"Sun, 26 May 2002 17:38:52 GMT\",\n  \"Keep-Alive\": \"timeout=15, max=100\",\n  \"HTTP-Version\": \"HTTP/1.1\",\n  \"Content-Type\": \"text/html\",\n  \"Connection\": \"Keep-Alive\",\n  \"Server\": \"Apache/1.3.23 (Unix) mod_perl/1.26\"\n}");
            HTTP.toString(jsonobject);
            fail("Should have thrown an exception.");
        } catch (Exception e)
        {
            assertEquals("Not enough material for an HTTP header.", e.getMessage());
        }
    }
    

    
    /**
     * Tests the toString method using method but no request uri.
     */
    public void testToString_MethodButNoRequestUri()
    {
        try
        {
            jsonobject = new JSONObject("{\n  \"Accept-Language\": \"en-us\",\n  \"Host\": \"www.nokko.com\",\n  \"Method\": \"GET\",\n  \"Accept-encoding\": \"gzip, deflate\",\n  \"User-Agent\": \"Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\",\n  \"HTTP-Version\": \"HTTP/1.0\",\n  \"Connection\": \"keep-alive\",\n  \"Accept\": \"image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\"\n}");
            HTTP.toString(jsonobject);
            fail("Should have thrown an exception.");
        } catch (Exception e)
        {
            assertEquals("Not enough material for an HTTP header.", e.getMessage());
        }
    }
    
    /**
     * Tests the constructor method.
     */
    public static void testConstructor()
    {
        HTTP http = new HTTP();
        assertEquals("HTTP", http.getClass().getSimpleName());
    }
}