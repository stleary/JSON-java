/*
 * File: TestCDL.java Author: JSON.org
 */
package org.json.tests;

import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;

import junit.framework.TestCase;

/**
 * The Class TestCDL.
 */
public class TestCDL extends TestCase
{

    /** The string. */
    private String string;

    /** The jsonarray. */
    private JSONArray jsonarray;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    @Override
    public void setUp()
    {
        //@formatter:off
        string = 
            "abc,test,123\n" +
            "gg,hh,jj\n" +
            "aa,bb,cc\n";
        //@formatter:on

        try
        {
            jsonarray = new JSONArray();
            JSONObject jo = new JSONObject();
            JSONObject jo2 = new JSONObject();
            jo.put("abc", "gg");
            jo.put("test", "hh");
            jo.put("123", "jj");
            jo2.put("abc", "aa");
            jo2.put("test", "bb");
            jo2.put("123", "cc");
            jsonarray.put(jo);
            jsonarray.put(jo2);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Tests the toJsonArray method.
     */
    public void testToJsonArray()
    {

        try
        {
            assertEquals(jsonarray.toString(), CDL.toJSONArray(string)
                    .toString());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Tests the toJsonArray method using No names.
     */
    public static void testToJsonArray_NoNames()
    {

        try
        {
            assertEquals(null, CDL.toJSONArray(new JSONArray(), "abc,123"));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Tests the toJsonArray method using Null names.
     */
    public static void testToJsonArray_NullNames()
    {

        try
        {
            assertEquals(null, CDL.toJSONArray(null, "abc,123"));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Tests the toJsonArray method using No data.
     */
    public static void testToJsonArray_NoData()
    {

        try
        {
            assertEquals(null, CDL.toJSONArray("abc,123\n"));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Tests the toJsonArray method using weird data.
     */
    public void testToJsonArray_WeirdData()
    {
      //@formatter:off
        string = 
            "abc,test,123\r" +
            "gg,hh,\"jj\"\r" +
            "aa,\tbb,cc";
        //@formatter:on
        try
        {
            assertEquals(jsonarray.toString(), CDL.toJSONArray(string)
                    .toString());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Tests the toJsonArray method using no closing quote.
     */
    public void testToJsonArray_NoClosingQuote()
    {
        //@formatter:off
        string = 
            "abc,test,123\r" +
            "gg,hh,jj \r" +
            "aa,\"bb ,cc ";
        //@formatter:on
        try
        {
            assertEquals(jsonarray.toString(), CDL.toJSONArray(string)
                    .toString());
            fail("Should have thrown Exception");
        } catch (JSONException e)
        {
            assertEquals("Missing close quote '\"'. at 35 [character 12 line 5]", e.getMessage());
        }
        //@formatter:off
        string = 
            "abc,test,123\r" +
            "gg,hh,jj \r" +
            "aa,\"bb ,cc \n";
        //@formatter:on
        try
        {
            assertEquals(jsonarray.toString(), CDL.toJSONArray(string)
                    .toString());
            fail("Should have thrown Exception");
        } catch (JSONException e)
        {
            assertEquals("Missing close quote '\"'. at 35 [character 0 line 6]", e.getMessage());
        }
        //@formatter:off
        string = 
            "abc,test,123\r" +
            "gg,hh,jj \r" +
            "aa,\"bb ,cc \r";
        //@formatter:on
        try
        {
            assertEquals(jsonarray.toString(), CDL.toJSONArray(string)
                    .toString());
            fail("Should have thrown Exception");
        } catch (JSONException e)
        {
            assertEquals(
                    "Missing close quote '\"'. at 35 [character 12 line 5]",
                    e.getMessage());
        }
    }
    
    /**
     * Tests the toJsonArray method using space after string.
     */
    public void testToJsonArray_SpaceAfterString()
    {
      //@formatter:off
        string = 
            "abc,test,123\r" +
            "gg,hh,jj \r" +
            "aa,\"bb\" ,cc\r";
        //@formatter:on
        try
        {
            assertEquals(jsonarray.toString(), CDL.toJSONArray(string)
                    .toString());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Tests the toJsonArray method using bad character.
     */
    public void testToJsonArray_BadCharacter()
    {
      //@formatter:off
        string = 
            "abc,test,123\r" +
            "gg,hh,jj \r" +
            "aa,\"bb \"?,cc \r";
        //@formatter:on
        try
        {
            assertEquals(jsonarray.toString(), CDL.toJSONArray(string)
                    .toString());
            fail("Should have thrown Exception");
        } catch (JSONException e)
        {
            assertEquals("Bad character '?' (63). at 32 [character 9 line 5]", e.getMessage());
        }
    }

    /**
     * Tests the toString method.
     */
    public void testToString()
    {
        try
        {
            assertEquals(string, CDL.toString(jsonarray));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Tests the toString method using Bad json array.
     */
    public void testToString_BadJsonArray()
    {
        try
        {
            jsonarray = new JSONArray();
            assertEquals(null, CDL.toString(jsonarray));
            jsonarray.put("abc");
            assertEquals("", CDL.toString(jsonarray, jsonarray));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Tests the toString method using No names.
     */
    public void testToString_NoNames()
    {
        try
        {
            jsonarray = new JSONArray();
            JSONObject jo = new JSONObject();
            jsonarray.put(jo);
            assertEquals(null, CDL.toString(jsonarray));

            assertEquals(null, CDL.toString(new JSONArray(), jsonarray));

            JSONArray names = new JSONArray();
            names.put("");
            assertEquals("\n", CDL.toString(names, jsonarray));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Tests the toString method using Null names.
     */
    public void testToString_NullNames()
    {
        try
        {
            jsonarray = new JSONArray();
            JSONObject jo = new JSONObject();
            jsonarray.put(jo);
            assertEquals(null, CDL.toString(null, jsonarray));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Tests the toString method using Quotes.
     */
    public void testToString_Quotes()
    {

        try
        {
            jsonarray = CDL
                    .toJSONArray("Comma delimited list test, '\"Strip\"Quotes', 'quote, comma', No quotes, 'Single Quotes', \"Double Quotes\"\n1,'2',\"3\"\n,'It is \"good,\"', \"It works.\"\n\n");

            string = CDL.toString(jsonarray);
            assertEquals(
                    "\"quote, comma\",\"StripQuotes\",Comma delimited list test\n"
                            + "3,2,1\n" + "It works.,\"It is good,\",\n",
                    string);
            assertEquals(
                    "[\n {\n  \"quote, comma\": \"3\",\n  \"\\\"Strip\\\"Quotes\": \"2\",\n  \"Comma delimited list test\": \"1\"\n },\n {\n  \"quote, comma\": \"It works.\",\n  \"\\\"Strip\\\"Quotes\": \"It is \\\"good,\\\"\",\n  \"Comma delimited list test\": \"\"\n }\n]",
                    jsonarray.toString(1));
            jsonarray = CDL.toJSONArray(string);
            assertEquals(
                    "[\n {\n  \"quote, comma\": \"3\",\n  \"StripQuotes\": \"2\",\n  \"Comma delimited list test\": \"1\"\n },\n {\n  \"quote, comma\": \"It works.\",\n  \"StripQuotes\": \"It is good,\",\n  \"Comma delimited list test\": \"\"\n }\n]",
                    jsonarray.toString(1));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Tests the constructor method.
     */
    public static void testConstructor()
    {
        CDL cdl = new CDL();
        assertEquals("CDL", cdl.getClass().getSimpleName());
    }

}
