/*
 * File:         TestCookie.java
 * Author:       JSON.org
 */
package org.json.tests;

import org.json.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

import junit.framework.TestCase;

/**
 * The Class TestCookie.
 */
public class TestCookie extends TestCase
{
    
    JSONObject jsonobject;
    
    /**
     * Tests the toJsonObject method using random cookie data.
     */
    public static void testToJsonObject_RandomCookieData()
    {
        try
        {
            JSONObject jsonobject = new JSONObject();
            jsonobject = Cookie
                    .toJSONObject("f%oo=blah; secure ;expires = April 24, 2002");
            assertEquals("{\n" + "  \"expires\": \"April 24, 2002\",\n"
                    + "  \"name\": \"f%oo\",\n" + "  \"secure\": true,\n"
                    + "  \"value\": \"blah\"\n" + "}", jsonobject.toString(2));
            assertEquals("f%25oo=blah;expires=April 24, 2002;secure",
                    Cookie.toString(jsonobject));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the escape and unescape method.
     */
    public static void testEscape()
    {
        StringBuilder testString = new StringBuilder();
        testString.append('h');
        for(int i = 0; i < ' '; i++)
            testString.append((char)i);
        testString.append('\n');
        testString.append('\t');
        testString.append('\b');
        testString.append('%');
        testString.append('+');
        testString.append('=');
        testString.append(';');
        String result = "h%00%01%02%03%04%05%06%07%08%09%0a%0b%0c%0d%0e%0f%10%11%12%13%14%15%16%17%18%19%1a%1b%1c%1d%1e%1f%0a%09%08%25%2b%3d%3b";
        assertEquals(result, Cookie.escape(testString.toString()));
    }
    
    /**
     * Tests the unescape method.
     */
    public static void testUnescape()
    {
        StringBuilder testString = new StringBuilder();
        testString.append('h');
        for(int i = 0; i < ' '; i++)
            testString.append((char)i);
        testString.append('\n');
        testString.append('\t');
        testString.append('\b');
        testString.append('%');
        testString.append('+');
        testString.append('%');
        testString.append('0');
        testString.append('\r');
        testString.append(' ');
        testString.append(' ');
        testString.append('%');
        testString.append('\n');
        testString.append('z');
        testString.append('z');
        testString.append('=');
        testString.append(';');
        testString.append('%');
        String result = "h%00%01%02%03%04%05%06%07%08%09%0a%0b%0c%0d%0e%0f%10%11%12%13%14%15%16%17%18%19%1a%1b%1c%1d%1e%1f%0a%09%08%25%2b%0\r +%\nzz%3d%3b%";
        assertEquals(testString.toString(), Cookie.unescape(result));
    }
    
    /**
     * Tests the toJsonObject method using value without equals.
     */
    public void testToJsonObject_ValueWithoutEquals()
    {
        try
        {
            jsonobject = Cookie
                    .toJSONObject("f%oo=blah; notsecure ;expires = April 24, 2002");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Missing '=' in cookie parameter. at 22 [character 23 line 1]", e.getMessage());
        }
    }
    
    /**
     * Tests the toString method.
     */
    public static void testToString()
    {
        try
        {
            JSONObject jsonobject = new JSONObject();
            jsonobject.put("secure", true);
            jsonobject.put("expires", "string1");
            jsonobject.put("domain", "string2");
            jsonobject.put("path", "string3");
            jsonobject.put("name", "foo");
            jsonobject.put("value", "bar");
            assertEquals("foo=bar;expires=string1;domain=string2;path=string3;secure", Cookie.toString(jsonobject));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the constructor method.
     */
    public static void testConstructor()
    {
        Cookie cookie = new Cookie();
        assertEquals("Cookie", cookie.getClass().getSimpleName());
    }
    
}