/*
 * File: TestJSONArray.java Author: JSON.org
 */
package org.json.tests;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.junit.Before;

import junit.framework.TestCase;

/**
 * The Class TestJSONArray.
 */
public class TestJSONArray extends TestCase
{

    /** The jsonarray. */
    private JSONArray jsonarray;

    /** The string. */
    private String string;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    @Before
    public void setUp()
    {
        jsonarray = new JSONArray();
        string = "";
    }

    /**
     * The Class testObject.
     */
    public class testObject
    {
        //Do Nothing
    }

    /**
     * Tests the jsonArray method using int with leading zeros.
     */
    public static void testJsonArray_IntWithLeadingZeros()
    {
        JSONArray jsonarray;
        String string;

        try
        {
            string = "[001122334455]";
            jsonarray = new JSONArray(string);
            assertEquals("[1122334455]", jsonarray.toString());
        } catch (Exception e)
        {
            fail(e.toString());
        }
    }

    /**
     * Tests the jsonArray method using scintific notation.
     */
    public static void testJsonArray_ScintificNotation()
    {
        JSONArray jsonarray;
        String string;

        try
        {
            string = "[666e666]";
            jsonarray = new JSONArray(string);
            assertEquals("[\"666e666\"]", jsonarray.toString());
        } catch (Exception e)
        {
            fail(e.toString());
        }
    }

    /**
     * Tests the jsonArray method using double with leading and trailing zeros.
     */
    public static void testJsonArray_DoubleWithLeadingAndTrailingZeros()
    {
        JSONArray jsonarray;
        String string;

        try
        {
            string = "[00.10]";
            jsonarray = new JSONArray(string);
            assertEquals("[0.1]", jsonarray.toString());
        } catch (Exception e)
        {
            fail(e.toString());
        }
    }

    /**
     * Tests the constructor method using missing value.
     */
    public void testConstructor_MissingValue()
    {
        try
        {
            jsonarray = new JSONArray("[\n\r\n\r}");
            fail("expecting JSONException here.");
        } catch (JSONException jsone)
        {
            assertEquals("Missing value at 5 [character 0 line 4]",
                    jsone.getMessage());
        }
    }

    /**
     * Tests the constructor method using nan.
     */
    public void testConstructor_Nan()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(Double.NaN);
            jsonarray.toString();
            fail("expecting JSONException here.");
        } catch (JSONException jsone)
        {
            assertEquals("JSON does not allow non-finite numbers.",
                    jsone.getMessage());
        }
    }

    /**
     * Tests the constructor method using negative infinity.
     */
    public void testConstructor_NegativeInfinity()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(Double.NEGATIVE_INFINITY);
            fail("expecting JSONException here.");
        } catch (JSONException jsone)
        {
            assertEquals("JSON does not allow non-finite numbers.",
                    jsone.getMessage());
        }
    }

    /**
     * Tests the constructor method using positive infinity.
     */
    public void testConstructor_PositiveInfinity()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(Double.POSITIVE_INFINITY);
            fail("expecting JSONException here.");
        } catch (JSONException jsone)
        {
            assertEquals("JSON does not allow non-finite numbers.",
                    jsone.getMessage());
        }
    }

    /**
     * Tests the put method using positive infinity.
     */
    public void testPut_PositiveInfinity()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(Double.POSITIVE_INFINITY);
            fail("expecting JSONException here.");
        } catch (JSONException jsone)
        {
            assertEquals("JSON does not allow non-finite numbers.",
                    jsone.getMessage());
        }
    }

    /**
     * Tests the getDouble method using empty array.
     */
    public void testGetDouble_EmptyArray()
    {

        try
        {
            jsonarray.getDouble(0);
            fail("expecting JSONException here.");
        } catch (JSONException jsone)
        {
            assertEquals("JSONArray[0] not found.", jsone.getMessage());
        }
    }

    /**
     * Tests the get method using negative index.
     */
    public void testGet_NegativeIndex()
    {

        try
        {
            jsonarray.get(-1);
            fail("expecting JSONException here.");
        } catch (JSONException jsone)
        {
            assertEquals("JSONArray[-1] not found.", jsone.getMessage());
        }
    }

    /**
     * Tests the put method using nan.
     */
    public void testPut_Nan()
    {
        try
        {
            jsonarray.put(Double.NaN);
            fail("expecting JSONException here.");
        } catch (JSONException jsone)
        {
            assertEquals("JSON does not allow non-finite numbers.",
                    jsone.getMessage());
        }
    }

    /**
     * Tests the constructor method using object.
     */
    public void testConstructor_Object()
    {
        try
        {
            jsonarray = new JSONArray(new Object());
            fail("expecting JSONException here.");
        } catch (JSONException jsone)
        {
            assertEquals(
                    "JSONArray initial value should be a string or collection or array.",
                    jsone.getMessage());
        }
    }

    /**
     * Tests the constructor method using bad json.
     */
    public void testConstructor_BadJson()
    {

        try
        {
            string = "[)";
            jsonarray = new JSONArray(string);
            fail("expecting JSONException here.");
        } catch (JSONException jsone)
        {
            assertEquals("Expected a ',' or ']' at 3 [character 4 line 1]",
                    jsone.getMessage());
        }
    }

    /**
     * Tests the toString method using locations.
     */
    public void testToString_Locations()
    {
        try
        {
            string = " [\"San Francisco\", \"New York\", \"Seoul\", \"London\", \"Seattle\", \"Shanghai\"]";
            jsonarray = new JSONArray(string);
            assertEquals(
                    "[\"San Francisco\",\"New York\",\"Seoul\",\"London\",\"Seattle\",\"Shanghai\"]",
                    jsonarray.toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the constructor method using collection.
     */
    public void testConstructor_Collection()
    {
        Collection<String> stringCol = new Stack<String>();
        stringCol.add("string1");
        stringCol.add("string2");
        stringCol.add("string3");
        stringCol.add("string4");
        jsonarray = new JSONArray(stringCol);
        assertEquals("[\"string1\",\"string2\",\"string3\",\"string4\"]",
                jsonarray.toString());
    }

    /**
     * Tests the constructor method using null collection.
     */
    public void testConstructor_NullCollection()
    {
        Collection<String> stringCol = null;
        jsonarray = new JSONArray(stringCol);
        assertEquals("[]", jsonarray.toString());
    }

    /**
     * Tests the constructor method using string array.
     */
    public void testConstructor_StringArray()
    {
        try
        {
            jsonarray = new JSONArray(new String[]
            {
                    "string1", "string2"
            });
            assertEquals("[\"string1\",\"string2\"]", jsonarray.toString());

        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the opt method.
     */
    public void testOpt()
    {
        try
        {
            jsonarray = new JSONArray(new String[]
            {
                    "string1", "string2"
            });
            assertEquals("string1", jsonarray.opt(0));
            assertEquals("string2", jsonarray.opt(1));

        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the toString method using exception.
     */
    public void testToString_Exception()
    {
        class BadJsonString implements JSONString
        {

            /*
             * (non-Javadoc)
             * 
             * @see org.json.JSONString#toJSONString()
             */
            @Override
            public String toJSONString()
            {
                String[] arString = new String[]
                {
                    "abc"
                };
                return arString[1];
            }

        }

        jsonarray = new JSONArray();
        jsonarray.put(new BadJsonString());
        assertEquals(null, jsonarray.toString());
    }

    /**
     * Tests the toString method using indents.
     */
    public void testToString_Indents()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put("123");
            jsonarray.put(new JSONObject().put("abc", "123"));
            jsonarray.put("abc");
            jsonarray.put(new JSONArray().put(new JSONArray()).put(
                    new JSONArray().put("123").put("abc")));
            assertEquals(
                    "[\n    \"123\",\n    {\"abc\": \"123\"},\n    \"abc\",\n    [\n        [],\n        [\n            \"123\",\n            \"abc\"\n        ]\n    ]\n]",
                    jsonarray.toString(4));
            assertEquals("[\"123\"]", new JSONArray().put("123").toString(4));
            assertEquals("[]", new JSONArray().toString(4));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the get method using invalid index.
     */
    public void testGet_InvalidIndex()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put("123");
            jsonarray.get(1);
        } catch (JSONException e)
        {
            assertEquals("JSONArray[1] not found.", e.getMessage());
        }
    }

    /**
     * Tests the get method using valid index.
     */
    public void testGet_ValidIndex()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put("123");
            assertEquals("123", jsonarray.get(0));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getBoolean method.
     */
    public void testGetBoolean()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put("true");
            jsonarray.put("false");
            jsonarray.put(true);
            jsonarray.put(false);
            jsonarray.put("TRUE");
            jsonarray.put("FALSE");
            assertTrue(jsonarray.getBoolean(0));
            assertFalse(jsonarray.getBoolean(1));
            assertTrue(jsonarray.getBoolean(2));
            assertFalse(jsonarray.getBoolean(3));
            assertTrue(jsonarray.getBoolean(4));
            assertFalse(jsonarray.getBoolean(5));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getBoolean method using non boolean.
     */
    public void testGetBoolean_NonBoolean()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put("123");
            jsonarray.getBoolean(0);
        } catch (JSONException e)
        {
            assertEquals("JSONArray[0] is not a boolean.", e.getMessage());
        }
    }

    /**
     * Tests the optBoolean method.
     */
    public void testOptBoolean()
    {
        jsonarray = new JSONArray();
        jsonarray.put("true");
        jsonarray.put("false");
        jsonarray.put(true);
        jsonarray.put(false);
        jsonarray.put("TRUE");
        jsonarray.put("FALSE");
        jsonarray.put("grass");
        assertTrue(jsonarray.optBoolean(0));
        assertFalse(jsonarray.optBoolean(1));
        assertTrue(jsonarray.optBoolean(2));
        assertFalse(jsonarray.optBoolean(3));
        assertTrue(jsonarray.optBoolean(4));
        assertFalse(jsonarray.optBoolean(5));
        assertFalse(jsonarray.optBoolean(6));
    }

    /**
     * Tests the getInt method.
     */
    public void testGetInt()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put("123");
            jsonarray.put("-12");
            jsonarray.put(45);
            jsonarray.put(-98);
            assertEquals(123, jsonarray.getInt(0));
            assertEquals(-12, jsonarray.getInt(1));
            assertEquals(45, jsonarray.getInt(2));
            assertEquals(-98, jsonarray.getInt(3));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getInt method using non integer.
     */
    public void testGetInt_NonInteger()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put("abc");
            jsonarray.getInt(0);
        } catch (JSONException e)
        {
            assertEquals("JSONArray[0] is not a number.", e.getMessage());
        }
    }

    /**
     * Tests the optInt method.
     */
    public void testOptInt()
    {
        jsonarray = new JSONArray();
        jsonarray.put("123");
        jsonarray.put("-12");
        jsonarray.put(45);
        jsonarray.put(-98);
        jsonarray.put("abc");
        assertEquals(123, jsonarray.optInt(0));
        assertEquals(-12, jsonarray.optInt(1));
        assertEquals(45, jsonarray.optInt(2));
        assertEquals(-98, jsonarray.optInt(3));
        assertEquals(0, jsonarray.optInt(4));
    }

    /**
     * Tests the getDouble method.
     */
    public void testGetDouble()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put("123");
            jsonarray.put("-12");
            jsonarray.put(45);
            jsonarray.put(-98);
            jsonarray.put("123.5");
            jsonarray.put("-12.87");
            jsonarray.put(45.22);
            jsonarray.put(-98.18);
            assertEquals(123.0, jsonarray.getDouble(0));
            assertEquals(-12.0, jsonarray.getDouble(1));
            assertEquals(45.0, jsonarray.getDouble(2));
            assertEquals(-98.0, jsonarray.getDouble(3));
            assertEquals(123.5, jsonarray.getDouble(4));
            assertEquals(-12.87, jsonarray.getDouble(5));
            assertEquals(45.22, jsonarray.getDouble(6));
            assertEquals(-98.18, jsonarray.getDouble(7));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getDouble method using non double.
     */
    public void testGetDouble_NonDouble()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put("abc");
            jsonarray.getDouble(0);
        } catch (JSONException e)
        {
            assertEquals("JSONArray[0] is not a number.", e.getMessage());
        }
    }

    /**
     * Tests the optDouble method.
     */
    public void testOptDouble()
    {
        jsonarray = new JSONArray();
        jsonarray.put("123");
        jsonarray.put("-12");
        jsonarray.put(45);
        jsonarray.put(-98);
        jsonarray.put("123.5");
        jsonarray.put("-12.87");
        try
        {
            jsonarray.put(45.22);
            jsonarray.put(-98.18);
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
        assertEquals(123.0, jsonarray.optDouble(0));
        assertEquals(-12.0, jsonarray.optDouble(1));
        assertEquals(45.0, jsonarray.optDouble(2));
        assertEquals(-98.0, jsonarray.optDouble(3));
        assertEquals(123.5, jsonarray.optDouble(4));
        assertEquals(-12.87, jsonarray.optDouble(5));
        assertEquals(45.22, jsonarray.optDouble(6));
        assertEquals(-98.18, jsonarray.optDouble(7));
        assertEquals(Double.NaN, jsonarray.optDouble(8));
    }

    /**
     * Tests the getLong method.
     */
    public void testGetLong()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put("123");
            jsonarray.put("-12");
            jsonarray.put(45L);
            jsonarray.put(-98L);
            assertEquals(123, jsonarray.getLong(0));
            assertEquals(-12, jsonarray.getLong(1));
            assertEquals(45, jsonarray.getLong(2));
            assertEquals(-98, jsonarray.getLong(3));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getLong method using non long.
     */
    public void testGetLong_NonLong()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put("abc");
            jsonarray.getLong(0);
        } catch (JSONException e)
        {
            assertEquals("JSONArray[0] is not a number.", e.getMessage());
        }
    }

    /**
     * Tests the optLong method.
     */
    public void testOptLong()
    {
        jsonarray = new JSONArray();
        jsonarray.put("123");
        jsonarray.put("-12");
        jsonarray.put(45L);
        jsonarray.put(-98L);
        assertEquals(123, jsonarray.optLong(0));
        assertEquals(-12, jsonarray.optLong(1));
        assertEquals(45, jsonarray.optLong(2));
        assertEquals(-98, jsonarray.optLong(3));
        assertEquals(0, jsonarray.optLong(8));
    }

    /**
     * Tests the getString method.
     */
    public void testGetString()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put("123");
            jsonarray.put("-12");
            jsonarray.put("abc");
            jsonarray.put("123");
            assertEquals("123", jsonarray.getString(0));
            assertEquals("-12", jsonarray.getString(1));
            assertEquals("abc", jsonarray.getString(2));
            assertEquals("123", jsonarray.getString(3));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getString method using non string.
     */
    public void testGetString_NonString()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(123);
            jsonarray.getString(0);
        } catch (JSONException e)
        {
            assertEquals("JSONArray[0] not a string.", e.getMessage());
        }
    }

    /**
     * Tests the optString method.
     */
    public void testOptString()
    {
        jsonarray = new JSONArray();
        jsonarray.put("123");
        jsonarray.put("-12");
        jsonarray.put("abc");
        jsonarray.put("123");
        assertEquals("123", jsonarray.optString(0));
        assertEquals("-12", jsonarray.optString(1));
        assertEquals("abc", jsonarray.optString(2));
        assertEquals("123", jsonarray.optString(3));
        assertEquals("", jsonarray.optString(4));
    }

    /**
     * Tests the optJSONObject method.
     */
    public void testOptJSONObject()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(new JSONObject().put("abc", "123"));
            assertEquals("{\"abc\":\"123\"}", jsonarray.optJSONObject(0).toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the optJSONObject method using non json object.
     */
    public void testOptJSONObject_NonJsonObject()
    {
        jsonarray = new JSONArray();
        jsonarray.put("123");
        assertEquals(null, jsonarray.optJSONObject(0));
    }

    /**
     * Tests the optJSONArray method.
     */
    public void testOptJSONArray()
    {
        jsonarray = new JSONArray();
        jsonarray.put(new JSONArray().put("abc"));
        assertEquals("[\"abc\"]", jsonarray.optJSONArray(0).toString());
    }

    /**
     * Tests the optJSONArray method using non json array.
     */
    public void testOptJSONArray_NonJsonArray()
    {
        jsonarray = new JSONArray();
        jsonarray.put("123");
        assertEquals(null, jsonarray.optJSONArray(0));
    }

    /**
     * Tests the isNull method.
     */
    public void testIsNull()
    {
        jsonarray = new JSONArray();
        jsonarray.put(JSONObject.NULL);
        jsonarray.put("null");
        assertTrue(jsonarray.isNull(0));
        assertFalse(jsonarray.isNull(1));
    }

    /**
     * Tests the writer method.
     */
    public void testWriter()
    {
        try
        {
            StringWriter sw = new StringWriter();
            jsonarray = new JSONArray();
            jsonarray.put("123");
            jsonarray.put("-12");
            jsonarray.put(45);
            jsonarray.put(-98);
            jsonarray.put(new JSONArray().put("abc"));
            jsonarray.put("-12");
            jsonarray.put("abc");
            jsonarray.put("123");
            jsonarray.put("123");
            jsonarray.put(new JSONObject().put("abc", "123"));
            jsonarray.put("abc");
            jsonarray.put("123");
            jsonarray.write(sw);
            assertEquals(
                    "[\"123\",\"-12\",45,-98,[\"abc\"],\"-12\",\"abc\",\"123\",\"123\",{\"abc\":\"123\"},\"abc\",\"123\"]",
                    sw.toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the writer method using bad writer.
     */
    public void testWriter_BadWriter()
    {
        class BadWriter extends Writer
        {

            /*
             * (non-Javadoc)
             * 
             * @see java.io.Writer#write(char[], int, int)
             */
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException
            {
                throw new IOException("Test Message From Bad Writer");
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.io.Writer#flush()
             */
            @Override
            public void flush() throws IOException
            {
                //Do Nothing
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.io.Writer#close()
             */
            @Override
            public void close() throws IOException
            {
                // Do nothing
            }

        }
        try
        {
            BadWriter sw = new BadWriter();
            jsonarray = new JSONArray();
            jsonarray.put("123");
            jsonarray.put("-12");
            jsonarray.put(45);
            jsonarray.put(-98);
            jsonarray.put(new JSONArray().put("abc"));
            jsonarray.put("-12");
            jsonarray.put("abc");
            jsonarray.put("123");
            jsonarray.put("123");
            jsonarray.put(new JSONObject().put("abc", "123"));
            jsonarray.put("abc");
            jsonarray.put("123");
            jsonarray.write(sw);
        } catch (JSONException e)
        {
            assertEquals("Test Message From Bad Writer", e.getMessage());
        }
    }

    /**
     * Tests the put method using object and specific index.
     */
    public void testPut_ObjectAndSpecificIndex()
    {
        try
        {
            testObject a = new testObject();
            testObject b = new testObject();
            testObject c = new testObject();
            testObject d = new testObject();
            jsonarray = new JSONArray();
            jsonarray.put(0, a);
            jsonarray.put(1, b);
            assertEquals(a, jsonarray.get(0));
            assertEquals(b, jsonarray.get(1));
            jsonarray.put(0, c);
            assertEquals(c, jsonarray.get(0));
            assertEquals(b, jsonarray.get(1));
            jsonarray.put(8, d);
            assertEquals(d, jsonarray.get(8));
            assertEquals(JSONObject.NULL, jsonarray.get(2));
            assertEquals(JSONObject.NULL, jsonarray.get(3));
            assertEquals(JSONObject.NULL, jsonarray.get(4));
            assertEquals(JSONObject.NULL, jsonarray.get(5));
            assertEquals(JSONObject.NULL, jsonarray.get(6));
            assertEquals(JSONObject.NULL, jsonarray.get(7));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the put method using object and negative index.
     */
    public void testPut_ObjectAndNegativeIndex()
    {
        try
        {
            testObject a = new testObject();
            jsonarray = new JSONArray();
            jsonarray.put(-1, a);
            fail("Should have thrown exception");
        } catch (JSONException e)
        {
            assertEquals("JSONArray[-1] not found.", e.getMessage());
        }
    }

    /**
     * Tests the toJSONObject method.
     */
    public void testToJSONObject()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put("123");
            jsonarray.put("-12");
            jsonarray.put(45);
            jsonarray.put(-98);
            jsonarray.put(new JSONArray().put("abc"));
            jsonarray.put(new JSONObject().put("abc", "123"));
            JSONArray names = new JSONArray(new String[]
            {
                    "bdd", "fdsa", "fds", "ewre", "rer", "gfs"
            });
            assertEquals(
                    "{\"gfs\":{\"abc\":\"123\"},\"fdsa\":\"-12\",\"bdd\":\"123\",\"ewre\":-98,\"rer\":[\"abc\"],\"fds\":45}",
                    jsonarray.toJSONObject(names).toString());
            assertEquals(null, jsonarray.toJSONObject(new JSONArray()));
            assertEquals(null, jsonarray.toJSONObject(null));
            assertEquals(null, new JSONArray().toJSONObject(names));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getJSONObject method.
     */
    public void testGetJSONObject()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(new JSONObject().put("abc", "123"));
            assertEquals("{\"abc\":\"123\"}", jsonarray.getJSONObject(0).toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getJSONObject method using non json object.
     */
    public void testGetJSONObject_NonJsonObject()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put("123");
            jsonarray.getJSONObject(0);
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("JSONArray[0] is not a JSONObject.", e.getMessage());
        }
    }

    /**
     * Tests the getJSONArray method.
     */
    public void testGetJSONArray()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(new JSONArray().put("abc"));
            assertEquals("[\"abc\"]", jsonarray.getJSONArray(0).toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getJSONArray method using non json array.
     */
    public void testGetJSONArray_NonJsonArray()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put("123");
            jsonarray.getJSONArray(0);
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("JSONArray[0] is not a JSONArray.", e.getMessage());
        }
    }

    /**
     * Tests the put method using map.
     */
    public void testPut_Map()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("abc", "123");
        jsonarray = new JSONArray();
        jsonarray.put(map);
        assertEquals("[{\"abc\":\"123\"}]", jsonarray.toString());
    }

    /**
     * Tests the constructor method using bad json array.
     */
    public void testConstructor_BadJsonArray()
    {
        try
        {
            jsonarray = new JSONArray("abc");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("A JSONArray text must start with '[' at 1 [character 2 line 1]", e.getMessage());
        }
    }

    /**
     * Tests the constructor method.
     */
    public void testConstructor()
    {
        try
        {
            jsonarray = new JSONArray("[]");
            assertEquals("[]", jsonarray.toString());
            jsonarray = new JSONArray("[\"abc\"]");
            assertEquals("[\"abc\"]", jsonarray.toString());
            jsonarray = new JSONArray("[\"abc\",\"123\"]");
            assertEquals("[\"abc\",\"123\"]", jsonarray.toString());
            jsonarray = new JSONArray("[123,{}]");
            assertEquals("[123,{}]", jsonarray.toString());
            jsonarray = new JSONArray("[123,,{}]");
            assertEquals("[123,null,{}]", jsonarray.toString());
            jsonarray = new JSONArray("[123,,{},]");
            assertEquals("[123,null,{}]", jsonarray.toString());
            jsonarray = new JSONArray("[123,,{};]");
            assertEquals("[123,null,{}]", jsonarray.toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the put method using collection.
     */
    public void testPut_Collection()
    {
        Collection<Object> stringCol = new Stack<Object>();
        stringCol.add("string1");
        stringCol.add("string2");
        stringCol.add("string3");
        stringCol.add("string4");
        jsonarray = new JSONArray();
        jsonarray.put(stringCol);
        assertEquals("[[\"string1\",\"string2\",\"string3\",\"string4\"]]",
                jsonarray.toString());
    }

    /**
     * Tests the put method using boolean and specific index.
     */
    public void testPut_BooleanAndSpecificIndex()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(0, true);
            jsonarray.put(1, true);
            assertEquals(true, jsonarray.get(0));
            assertEquals(true, jsonarray.get(1));
            jsonarray.put(0, false);
            assertEquals(false, jsonarray.get(0));
            assertEquals(true, jsonarray.get(1));
            jsonarray.put(8, false);
            assertEquals(false, jsonarray.get(8));
            assertEquals(JSONObject.NULL, jsonarray.get(2));
            assertEquals(JSONObject.NULL, jsonarray.get(3));
            assertEquals(JSONObject.NULL, jsonarray.get(4));
            assertEquals(JSONObject.NULL, jsonarray.get(5));
            assertEquals(JSONObject.NULL, jsonarray.get(6));
            assertEquals(JSONObject.NULL, jsonarray.get(7));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the put method using boolean and negative index.
     */
    public void testPut_BooleanAndNegativeIndex()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(-1, true);
            fail("Should have thrown exception");
        } catch (JSONException e)
        {
            assertEquals("JSONArray[-1] not found.", e.getMessage());
        }
    }

    /**
     * Tests the put method using collection and specific index.
     */
    public void testPut_CollectionAndSpecificIndex()
    {
        try
        {
            Collection<Object> a = new Stack<Object>();
            a.add("string1");
            a.add("string4");
            Collection<Object> b = new Stack<Object>();
            b.add("string2");
            b.add("string3");
            Collection<Object> c = new Stack<Object>();
            c.add("string3");
            c.add("string4");
            Collection<Object> d = new Stack<Object>();
            d.add("string1");
            d.add("string2");
            jsonarray = new JSONArray();
            jsonarray.put(0, a);
            jsonarray.put(1, b);
            assertEquals(new JSONArray(a).toString(), jsonarray.get(0).toString());
            assertEquals(new JSONArray(b).toString(), jsonarray.get(1).toString());
            jsonarray.put(0, c);
            assertEquals(new JSONArray(c).toString(), jsonarray.get(0).toString());
            assertEquals(new JSONArray(b).toString(), jsonarray.get(1).toString());
            jsonarray.put(8, d);
            assertEquals(new JSONArray(d).toString(), jsonarray.get(8).toString());
            assertEquals(JSONObject.NULL, jsonarray.get(2));
            assertEquals(JSONObject.NULL, jsonarray.get(3));
            assertEquals(JSONObject.NULL, jsonarray.get(4));
            assertEquals(JSONObject.NULL, jsonarray.get(5));
            assertEquals(JSONObject.NULL, jsonarray.get(6));
            assertEquals(JSONObject.NULL, jsonarray.get(7));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the put method using collection and negative index.
     */
    public void testPut_CollectionAndNegativeIndex()
    {
        try
        {
            Collection<Object> a = new Stack<Object>();
            a.add("string1");
            a.add("string4");
            jsonarray = new JSONArray();
            jsonarray.put(-1, a);
            fail("Should have thrown exception");
        } catch (JSONException e)
        {
            assertEquals("JSONArray[-1] not found.", e.getMessage());
        }
    }

    /**
     * Tests the put method using double and specific index.
     */
    public void testPut_DoubleAndSpecificIndex()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(0, 10.0);
            jsonarray.put(1, 30.2);
            assertEquals(10.0, jsonarray.get(0));
            assertEquals(30.2, jsonarray.get(1));
            jsonarray.put(0, 52.64);
            assertEquals(52.64, jsonarray.get(0));
            assertEquals(30.2, jsonarray.get(1));
            jsonarray.put(8, 14.23);
            assertEquals(14.23, jsonarray.get(8));
            assertEquals(JSONObject.NULL, jsonarray.get(2));
            assertEquals(JSONObject.NULL, jsonarray.get(3));
            assertEquals(JSONObject.NULL, jsonarray.get(4));
            assertEquals(JSONObject.NULL, jsonarray.get(5));
            assertEquals(JSONObject.NULL, jsonarray.get(6));
            assertEquals(JSONObject.NULL, jsonarray.get(7));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the put method using double and negative index.
     */
    public void testPut_DoubleAndNegativeIndex()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(-1, 30.65);
            fail("Should have thrown exception");
        } catch (JSONException e)
        {
            assertEquals("JSONArray[-1] not found.", e.getMessage());
        }
    }

    /**
     * Tests the put method using int and specific index.
     */
    public void testPut_IntAndSpecificIndex()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(0, 54);
            jsonarray.put(1, 82);
            assertEquals(54, jsonarray.get(0));
            assertEquals(82, jsonarray.get(1));
            jsonarray.put(0, 36);
            assertEquals(36, jsonarray.get(0));
            assertEquals(82, jsonarray.get(1));
            jsonarray.put(8, 67);
            assertEquals(67, jsonarray.get(8));
            assertEquals(JSONObject.NULL, jsonarray.get(2));
            assertEquals(JSONObject.NULL, jsonarray.get(3));
            assertEquals(JSONObject.NULL, jsonarray.get(4));
            assertEquals(JSONObject.NULL, jsonarray.get(5));
            assertEquals(JSONObject.NULL, jsonarray.get(6));
            assertEquals(JSONObject.NULL, jsonarray.get(7));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the put method using int and negative index.
     */
    public void testPut_IntAndNegativeIndex()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(-1, 3);
            fail("Should have thrown exception");
        } catch (JSONException e)
        {
            assertEquals("JSONArray[-1] not found.", e.getMessage());
        }
    }

    /**
     * Tests the put method using long and specific index.
     */
    public void testPut_LongAndSpecificIndex()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(0, 54L);
            jsonarray.put(1, 456789123L);
            assertEquals(54L, jsonarray.get(0));
            assertEquals(456789123L, jsonarray.get(1));
            jsonarray.put(0, 72887L);
            assertEquals(72887L, jsonarray.get(0));
            assertEquals(456789123L, jsonarray.get(1));
            jsonarray.put(8, 39397L);
            assertEquals(39397L, jsonarray.get(8));
            assertEquals(JSONObject.NULL, jsonarray.get(2));
            assertEquals(JSONObject.NULL, jsonarray.get(3));
            assertEquals(JSONObject.NULL, jsonarray.get(4));
            assertEquals(JSONObject.NULL, jsonarray.get(5));
            assertEquals(JSONObject.NULL, jsonarray.get(6));
            assertEquals(JSONObject.NULL, jsonarray.get(7));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the put method using long and negative index.
     */
    public void testPut_LongAndNegativeIndex()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(-1, 456486794L);
            fail("Should have thrown exception");
        } catch (JSONException e)
        {
            assertEquals("JSONArray[-1] not found.", e.getMessage());
        }
    }

    /**
     * Tests the put method using map and specific index.
     */
    public void testPut_MapAndSpecificIndex()
    {
        try
        {
            Map<String, Object> a = new HashMap<String, Object>();
            a.put("abc", "123");
            Map<String, Object> b = new HashMap<String, Object>();
            b.put("abffc", "1253");
            Map<String, Object> c = new HashMap<String, Object>();
            c.put("addbc", "145623");
            Map<String, Object> d = new HashMap<String, Object>();
            d.put("abffdc", "122623");            
            jsonarray = new JSONArray();
            jsonarray.put(0, a);
            jsonarray.put(1, b);
            assertEquals(new JSONObject(a).toString(), jsonarray.get(0).toString());
            assertEquals(new JSONObject(b).toString(), jsonarray.get(1).toString());
            jsonarray.put(0, c);
            assertEquals(new JSONObject(c).toString(), jsonarray.get(0).toString());
            assertEquals(new JSONObject(b).toString(), jsonarray.get(1).toString());
            jsonarray.put(8, d);
            assertEquals(new JSONObject(d).toString(), jsonarray.get(8).toString());
            assertEquals(JSONObject.NULL, jsonarray.get(2));
            assertEquals(JSONObject.NULL, jsonarray.get(3));
            assertEquals(JSONObject.NULL, jsonarray.get(4));
            assertEquals(JSONObject.NULL, jsonarray.get(5));
            assertEquals(JSONObject.NULL, jsonarray.get(6));
            assertEquals(JSONObject.NULL, jsonarray.get(7));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the put method using map and negative index.
     */
    public void testPut_MapAndNegativeIndex()
    {
        try
        {
            Map<String, Object> a = new HashMap<String, Object>();
            a.put("abc", "123");
            jsonarray = new JSONArray();
            jsonarray.put(-1, a);
            fail("Should have thrown exception");
        } catch (JSONException e)
        {
            assertEquals("JSONArray[-1] not found.", e.getMessage());
        }
    }

    /**
     * Tests the remove method.
     */
    public void testRemove()
    {
        try
        {
            jsonarray = new JSONArray();
            jsonarray.put(0, 54);
            jsonarray.put(1, 456789123);
            jsonarray.put(2, 72887);
            jsonarray.remove(1);
            assertEquals(54, jsonarray.get(0));
            assertEquals(72887, jsonarray.get(1));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
}