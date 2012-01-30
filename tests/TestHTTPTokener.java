/*
 * File: TestHTTPTokener.java Author: JSON.org
 */
package org.json.tests;

import org.json.HTTPTokener;
import org.json.JSONException;

import junit.framework.TestCase;

/**
 * The Class TestHTTPTokener.
 */
public class TestHTTPTokener extends TestCase
{

    private HTTPTokener httptokener;

    /**
     * Tests the toString method.
     */
    public void testNextToken_SimpleString()
    {
        try
        {
            httptokener = new HTTPTokener(
                    "{\n  \"Accept-Language\": 'en-us' ," +
                    "\n  \"Host\": 23");
            assertEquals("{", httptokener.nextToken());
            assertEquals("Accept-Language", httptokener.nextToken());
            assertEquals(":", httptokener.nextToken());
            assertEquals("en-us", httptokener.nextToken());
            assertEquals(",", httptokener.nextToken());
            assertEquals("Host", httptokener.nextToken());
            assertEquals(":", httptokener.nextToken());
            assertEquals("23", httptokener.nextToken());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the nextToken method using unterminated string.
     */
    public void testNextToken_UnterminatedString()
    {
        try
        {
            httptokener = new HTTPTokener(
                    "'en-us");
            httptokener.nextToken();
            fail("Should have thrown exception");
        } catch (JSONException e)
        {
            assertEquals("Unterminated string. at 7 [character 8 line 1]", e.getMessage());
        }
    }
    
}