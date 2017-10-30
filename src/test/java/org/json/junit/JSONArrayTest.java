package org.json.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONPointerException;
import org.junit.Test;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;


/**
 * Tests for JSON-Java JSONArray.java
 */
public class JSONArrayTest {
    String arrayStr = 
            "["+
                "true,"+
                "false,"+
                "\"true\","+
                "\"false\","+
                "\"hello\","+
                "23.45e-4,"+
                "\"23.45\","+
                "42,"+
                "\"43\","+
                "["+
                    "\"world\""+
                "],"+
                "{"+
                    "\"key1\":\"value1\","+
                    "\"key2\":\"value2\","+
                    "\"key3\":\"value3\","+
                    "\"key4\":\"value4\""+
                "},"+
                "0,"+
                "\"-1\""+
            "]";

    /**
     * Attempt to create a JSONArray with a null string.
     * Expects a NullPointerException.
     */
    @Test(expected=NullPointerException.class)
    public void nullException() {
        String str = null;
        assertNull("Should throw an exception", new JSONArray(str));
    }

    /**
     * Attempt to create a JSONArray with an empty string.
     * Expects a JSONException.
     */
    @Test
    public void emptStr() {
        String str = "";
        try {
            assertNull("Should throw an exception", new JSONArray(str));
        } catch (JSONException e) {
            assertEquals("Expected an exception message", 
                    "A JSONArray text must start with '[' at 0 [character 1 line 1]",
                    e.getMessage());
        }
    }
    
    /**
     * Attempt to create a JSONArray with an unclosed array.
     * Expects an exception
     */
    @Test
    public void unclosedArray() {
        try {
            assertNull("Should throw an exception", new JSONArray("["));
        } catch (JSONException e) {
            assertEquals("Expected an exception message", 
                    "Expected a ',' or ']' at 1 [character 2 line 1]",
                    e.getMessage());
        }
    }

    /**
     * Attempt to create a JSONArray with a string as object that is
     * not a JSON array doc.
     * Expects a JSONException.
     */
    @Test
    public void badObject() {
        String str = "abc";
        try {
            assertNull("Should throw an exception", new JSONArray((Object)str));
        } catch (JSONException e) {
            assertTrue("Expected an exception message", 
                    "JSONArray initial value should be a string or collection or array.".
                    equals(e.getMessage()));
        }
    }
    
    /**
     * Verifies that the constructor has backwards compatibility with RAW types pre-java5.
     */
    @Test
    public void verifyConstructor() {
        
        final JSONArray expected = new JSONArray("[10]");
        
        @SuppressWarnings("rawtypes")
        Collection myRawC = Collections.singleton(Integer.valueOf(10));
        JSONArray jaRaw = new JSONArray(myRawC);

        Collection<Integer> myCInt = Collections.singleton(Integer.valueOf(10));
        JSONArray jaInt = new JSONArray(myCInt);

        Collection<Object> myCObj = Collections.singleton((Object) Integer
                .valueOf(10));
        JSONArray jaObj = new JSONArray(myCObj);

        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaRaw));
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaInt));
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaObj));
    }

    /**
     * Verifies that the put Collection has backwards compatibility with RAW types pre-java5.
     */
    @Test
    public void verifyPutCollection() {
        
        final JSONArray expected = new JSONArray("[[10]]");

        @SuppressWarnings("rawtypes")
        Collection myRawC = Collections.singleton(Integer.valueOf(10));
        JSONArray jaRaw = new JSONArray();
        jaRaw.put(myRawC);

        Collection<Object> myCObj = Collections.singleton((Object) Integer
                .valueOf(10));
        JSONArray jaObj = new JSONArray();
        jaObj.put(myCObj);

        Collection<Integer> myCInt = Collections.singleton(Integer.valueOf(10));
        JSONArray jaInt = new JSONArray();
        jaInt.put(myCInt);

        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaRaw));
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaObj));
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaInt));
    }

    
    /**
     * Verifies that the put Map has backwards compatibility with RAW types pre-java5.
     */
    @Test
    public void verifyPutMap() {
        
        final JSONArray expected = new JSONArray("[{\"myKey\":10}]");

        @SuppressWarnings("rawtypes")
        Map myRawC = Collections.singletonMap("myKey", Integer.valueOf(10));
        JSONArray jaRaw = new JSONArray();
        jaRaw.put(myRawC);

        Map<String, Object> myCStrObj = Collections.singletonMap("myKey",
                (Object) Integer.valueOf(10));
        JSONArray jaStrObj = new JSONArray();
        jaStrObj.put(myCStrObj);

        Map<String, Integer> myCStrInt = Collections.singletonMap("myKey",
                Integer.valueOf(10));
        JSONArray jaStrInt = new JSONArray();
        jaStrInt.put(myCStrInt);

        Map<?, ?> myCObjObj = Collections.singletonMap((Object) "myKey",
                (Object) Integer.valueOf(10));
        JSONArray jaObjObj = new JSONArray();
        jaObjObj.put(myCObjObj);

        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaRaw));
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaStrObj));
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaStrInt));
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaObjObj));
    }

    /**
     * Create a JSONArray doc with a variety of different elements.
     * Confirm that the values can be accessed via the get[type]() API methods
     */
    @SuppressWarnings("boxing")
    @Test
    public void getArrayValues() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        // booleans
        assertTrue("Array true",
                true == jsonArray.getBoolean(0));
        assertTrue("Array false",
                false == jsonArray.getBoolean(1));
        assertTrue("Array string true",
                true == jsonArray.getBoolean(2));
        assertTrue("Array string false",
                false == jsonArray.getBoolean(3));
        // strings
        assertTrue("Array value string",
                "hello".equals(jsonArray.getString(4)));
        // doubles
        assertTrue("Array double",
                new Double(23.45e-4).equals(jsonArray.getDouble(5)));
        assertTrue("Array string double",
                new Double(23.45).equals(jsonArray.getDouble(6)));
        // ints
        assertTrue("Array value int",
                new Integer(42).equals(jsonArray.getInt(7)));
        assertTrue("Array value string int",
                new Integer(43).equals(jsonArray.getInt(8)));
        // nested objects
        JSONArray nestedJsonArray = jsonArray.getJSONArray(9);
        assertTrue("Array value JSONArray", nestedJsonArray != null);
        JSONObject nestedJsonObject = jsonArray.getJSONObject(10);
        assertTrue("Array value JSONObject", nestedJsonObject != null);
        // longs
        assertTrue("Array value long",
                new Long(0).equals(jsonArray.getLong(11)));
        assertTrue("Array value string long",
                new Long(-1).equals(jsonArray.getLong(12)));

        assertTrue("Array value null", jsonArray.isNull(-1));
    }

    /**
     * Create a JSONArray doc with a variety of different elements.
     * Confirm that attempting to get the wrong types via the get[type]()
     * API methods result in JSONExceptions
     */
    @Test
    public void failedGetArrayValues() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        try {
            jsonArray.getBoolean(4);
            assertTrue("expected getBoolean to fail", false);
        } catch (JSONException e) {
            assertTrue("Expected an exception message",
                    "JSONArray[4] is not a boolean.".equals(e.getMessage()));
        }
        try {
            jsonArray.get(-1);
            assertTrue("expected get to fail", false);
        } catch (JSONException e) {
            assertTrue("Expected an exception message",
                    "JSONArray[-1] not found.".equals(e.getMessage()));
        }
        try {
            jsonArray.getDouble(4);
            assertTrue("expected getDouble to fail", false);
        } catch (JSONException e) {
            assertTrue("Expected an exception message",
                    "JSONArray[4] is not a number.".equals(e.getMessage()));
        }
        try {
            jsonArray.getInt(4);
            assertTrue("expected getInt to fail", false);
        } catch (JSONException e) {
            assertTrue("Expected an exception message",
                    "JSONArray[4] is not a number.".equals(e.getMessage()));
        }
        try {
            jsonArray.getJSONArray(4);
            assertTrue("expected getJSONArray to fail", false);
        } catch (JSONException e) {
            assertTrue("Expected an exception message",
                    "JSONArray[4] is not a JSONArray.".equals(e.getMessage()));
        }
        try {
            jsonArray.getJSONObject(4);
            assertTrue("expected getJSONObject to fail", false);
        } catch (JSONException e) {
            assertTrue("Expected an exception message",
                    "JSONArray[4] is not a JSONObject.".equals(e.getMessage()));
        }
        try {
            jsonArray.getLong(4);
            assertTrue("expected getLong to fail", false);
        } catch (JSONException e) {
            assertTrue("Expected an exception message",
                    "JSONArray[4] is not a number.".equals(e.getMessage()));
        }
        try {
            jsonArray.getString(5);
            assertTrue("expected getString to fail", false);
        } catch (JSONException e) {
            assertTrue("Expected an exception message",
                    "JSONArray[5] not a string.".equals(e.getMessage()));
        }
    }

    /**
     * Exercise JSONArray.join() by converting a JSONArray into a 
     * comma-separated string. Since this is very nearly a JSON document,
     * array braces are added to the beginning and end prior to validation.
     */
    @Test
    public void join() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        String joinStr = jsonArray.join(",");

        // validate JSON
        /**
         * Don't need to remake the JSONArray to perform the parsing
         */
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse("["+joinStr+"]");
        assertTrue("expected 13 items in top level object", ((List<?>)(JsonPath.read(doc, "$"))).size() == 13);
        assertTrue("expected true", Boolean.TRUE.equals(jsonArray.query("/0")));
        assertTrue("expected false", Boolean.FALSE.equals(jsonArray.query("/1")));
        assertTrue("expected \"true\"", "true".equals(jsonArray.query("/2")));
        assertTrue("expected \"false\"", "false".equals(jsonArray.query("/3")));
        assertTrue("expected hello", "hello".equals(jsonArray.query("/4")));
        assertTrue("expected 0.002345", Double.valueOf(0.002345).equals(jsonArray.query("/5")));
        assertTrue("expected \"23.45\"", "23.45".equals(jsonArray.query("/6")));
        assertTrue("expected 42", Integer.valueOf(42).equals(jsonArray.query("/7")));
        assertTrue("expected \"43\"", "43".equals(jsonArray.query("/8")));
        assertTrue("expected 1 item in [9]", ((List<?>)(JsonPath.read(doc, "$[9]"))).size() == 1);
        assertTrue("expected world", "world".equals(jsonArray.query("/9/0")));
        assertTrue("expected 4 items in [10]", ((Map<?,?>)(JsonPath.read(doc, "$[10]"))).size() == 4);
        assertTrue("expected value1", "value1".equals(jsonArray.query("/10/key1")));
        assertTrue("expected value2", "value2".equals(jsonArray.query("/10/key2")));
        assertTrue("expected value3", "value3".equals(jsonArray.query("/10/key3")));
        assertTrue("expected value4", "value4".equals(jsonArray.query("/10/key4")));
        assertTrue("expected 0", Integer.valueOf(0).equals(jsonArray.query("/11")));
        assertTrue("expected \"-1\"", "-1".equals(jsonArray.query("/12")));
    }

    /**
     * Confirm the JSONArray.length() method
     */
    @Test 
    public void length() {
        assertTrue("expected empty JSONArray length 0",
                new JSONArray().length() == 0);
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        assertTrue("expected JSONArray length 13", jsonArray.length() == 13);
        JSONArray nestedJsonArray = jsonArray.getJSONArray(9);
        assertTrue("expected JSONArray length 1", nestedJsonArray.length() == 1);
    }

    /**
     * Create a JSONArray doc with a variety of different elements.
     * Confirm that the values can be accessed via the opt[type](index)
     * and opt[type](index, default) API methods.
     */
    @SuppressWarnings("boxing")
    @Test 
    public void opt() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        assertTrue("Array opt value true",
                Boolean.TRUE == jsonArray.opt(0));
        assertTrue("Array opt value out of range",
                null == jsonArray.opt(-1));

        assertTrue("Array opt value out of range",
                null == jsonArray.opt(jsonArray.length()));

         assertTrue("Array opt boolean",
                Boolean.TRUE == jsonArray.optBoolean(0));
        assertTrue("Array opt boolean default",
                Boolean.FALSE == jsonArray.optBoolean(-1, Boolean.FALSE));
        assertTrue("Array opt boolean implicit default",
                Boolean.FALSE == jsonArray.optBoolean(-1));

        assertTrue("Array opt double",
                new Double(23.45e-4).equals(jsonArray.optDouble(5)));
        assertTrue("Array opt double default",
                new Double(1).equals(jsonArray.optDouble(0, 1)));
        assertTrue("Array opt double default implicit",
           new Double(jsonArray.optDouble(99)).isNaN());

        assertTrue("Array opt float",
                new Float(23.45e-4).equals(jsonArray.optFloat(5)));
        assertTrue("Array opt float default",
                new Float(1).equals(jsonArray.optFloat(0, 1)));
        assertTrue("Array opt float default implicit",
           new Float(jsonArray.optFloat(99)).isNaN());

        assertTrue("Array opt Number",
                new Double(23.45e-4).equals(jsonArray.optNumber(5)));
        assertTrue("Array opt Number default",
                new Double(1).equals(jsonArray.optNumber(0, 1d)));
        assertTrue("Array opt Number default implicit",
           new Double(jsonArray.optNumber(99,Double.NaN).doubleValue()).isNaN());

        assertTrue("Array opt int",
                new Integer(42).equals(jsonArray.optInt(7)));
        assertTrue("Array opt int default",
                new Integer(-1).equals(jsonArray.optInt(0, -1)));
        assertTrue("Array opt int default implicit",
                0 == jsonArray.optInt(0));

        JSONArray nestedJsonArray = jsonArray.optJSONArray(9);
        assertTrue("Array opt JSONArray", nestedJsonArray != null);
        assertTrue("Array opt JSONArray default", 
                null == jsonArray.optJSONArray(99));

        JSONObject nestedJsonObject = jsonArray.optJSONObject(10);
        assertTrue("Array opt JSONObject", nestedJsonObject != null);
        assertTrue("Array opt JSONObject default", 
                null == jsonArray.optJSONObject(99));

        assertTrue("Array opt long",
                0 == jsonArray.optLong(11));
        assertTrue("Array opt long default",
                -2 == jsonArray.optLong(-1, -2));
        assertTrue("Array opt long default implicit",
                0 == jsonArray.optLong(-1));

        assertTrue("Array opt string",
                "hello".equals(jsonArray.optString(4)));
        assertTrue("Array opt string default implicit",
                "".equals(jsonArray.optString(-1)));
    }
    
    /**
     * Verifies that the opt methods properly convert string values.
     */
    @Test
    public void optStringConversion(){
        JSONArray ja = new JSONArray("[\"123\",\"true\",\"false\"]");
        assertTrue("unexpected optBoolean value",ja.optBoolean(1,false)==true);
        assertTrue("unexpected optBoolean value",ja.optBoolean(2,true)==false);
        assertTrue("unexpected optInt value",ja.optInt(0,0)==123);
        assertTrue("unexpected optLong value",ja.optLong(0,0)==123);
        assertTrue("unexpected optDouble value",ja.optDouble(0,0.0)==123.0);
        assertTrue("unexpected optBigInteger value",ja.optBigInteger(0,BigInteger.ZERO).compareTo(new BigInteger("123"))==0);
        assertTrue("unexpected optBigDecimal value",ja.optBigDecimal(0,BigDecimal.ZERO).compareTo(new BigDecimal("123"))==0);    }

    /**
     * Exercise the JSONArray.put(value) method with various parameters
     * and confirm the resulting JSONArray.
     */
    @SuppressWarnings("boxing")
    @Test
    public void put() {
        JSONArray jsonArray = new JSONArray();

        // index 0
        jsonArray.put(true);
        // 1
        jsonArray.put(false);

        String jsonArrayStr =
            "["+
                "hello,"+
                "world"+
            "]";
        // 2
        jsonArray.put(new JSONArray(jsonArrayStr));

        // 3
        jsonArray.put(2.5);
        // 4
        jsonArray.put(1);
        // 5
        jsonArray.put(45L);

        // 6
        jsonArray.put("objectPut");

        String jsonObjectStr = 
            "{"+
                "\"key10\":\"val10\","+
                "\"key20\":\"val20\","+
                "\"key30\":\"val30\""+
            "}";
        JSONObject jsonObject = new JSONObject(jsonObjectStr);
        // 7
        jsonArray.put(jsonObject);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("k1", "v1");
        // 8
        jsonArray.put(map);

        Collection<Object> collection = new ArrayList<Object>();
        collection.add(1);
        collection.add(2);
        // 9
        jsonArray.put(collection);

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertTrue("expected 10 top level items", ((List<?>)(JsonPath.read(doc, "$"))).size() == 10);
        assertTrue("expected true", Boolean.TRUE.equals(jsonArray.query("/0")));
        assertTrue("expected false", Boolean.FALSE.equals(jsonArray.query("/1")));
        assertTrue("expected 2 items in [2]", ((List<?>)(JsonPath.read(doc, "$[2]"))).size() == 2);
        assertTrue("expected hello", "hello".equals(jsonArray.query("/2/0")));
        assertTrue("expected world", "world".equals(jsonArray.query("/2/1")));
        assertTrue("expected 2.5", Double.valueOf(2.5).equals(jsonArray.query("/3")));
        assertTrue("expected 1", Integer.valueOf(1).equals(jsonArray.query("/4")));
        assertTrue("expected 45", Long.valueOf(45).equals(jsonArray.query("/5")));
        assertTrue("expected objectPut", "objectPut".equals(jsonArray.query("/6")));
        assertTrue("expected 3 items in [7]", ((Map<?,?>)(JsonPath.read(doc, "$[7]"))).size() == 3);
        assertTrue("expected val10", "val10".equals(jsonArray.query("/7/key10")));
        assertTrue("expected val20", "val20".equals(jsonArray.query("/7/key20")));
        assertTrue("expected val30", "val30".equals(jsonArray.query("/7/key30")));
        assertTrue("expected 1 item in [8]", ((Map<?,?>)(JsonPath.read(doc, "$[8]"))).size() == 1);
        assertTrue("expected v1", "v1".equals(jsonArray.query("/8/k1")));
        assertTrue("expected 2 items in [9]", ((List<?>)(JsonPath.read(doc, "$[9]"))).size() == 2);
        assertTrue("expected 1", Integer.valueOf(1).equals(jsonArray.query("/9/0")));
        assertTrue("expected 2", Integer.valueOf(2).equals(jsonArray.query("/9/1")));
    }

    /**
     * Exercise the JSONArray.put(index, value) method with various parameters
     * and confirm the resulting JSONArray.
     */
    @SuppressWarnings("boxing")
    @Test
    public void putIndex() {
        JSONArray jsonArray = new JSONArray();

        // 1
        jsonArray.put(1, false);
        // index 0
        jsonArray.put(0, true);

        String jsonArrayStr =
            "["+
                "hello,"+
                "world"+
            "]";
        // 2
        jsonArray.put(2, new JSONArray(jsonArrayStr));

        // 5
        jsonArray.put(5, 45L);
        // 4
        jsonArray.put(4, 1);
        // 3
        jsonArray.put(3, 2.5);

        // 6
        jsonArray.put(6, "objectPut");

        // 7 will be null

        String jsonObjectStr = 
            "{"+
                "\"key10\":\"val10\","+
                "\"key20\":\"val20\","+
                "\"key30\":\"val30\""+
            "}";
        JSONObject jsonObject = new JSONObject(jsonObjectStr);
        jsonArray.put(8, jsonObject);
        Collection<Object> collection = new ArrayList<Object>();
        collection.add(1);
        collection.add(2);
        jsonArray.put(9,collection);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("k1", "v1");
        jsonArray.put(10, map);
        try {
            jsonArray.put(-1, "abc");
            assertTrue("put index < 0 should have thrown exception", false);
        } catch(Exception ignored) {}

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertTrue("expected 11 top level items", ((List<?>)(JsonPath.read(doc, "$"))).size() == 11);
        assertTrue("expected true", Boolean.TRUE.equals(jsonArray.query("/0")));
        assertTrue("expected false", Boolean.FALSE.equals(jsonArray.query("/1")));
        assertTrue("expected 2 items in [2]", ((List<?>)(JsonPath.read(doc, "$[2]"))).size() == 2);
        assertTrue("expected hello", "hello".equals(jsonArray.query("/2/0")));
        assertTrue("expected world", "world".equals(jsonArray.query("/2/1")));
        assertTrue("expected 2.5", Double.valueOf(2.5).equals(jsonArray.query("/3")));
        assertTrue("expected 1", Integer.valueOf(1).equals(jsonArray.query("/4")));
        assertTrue("expected 45", Long.valueOf(45).equals(jsonArray.query("/5")));
        assertTrue("expected objectPut", "objectPut".equals(jsonArray.query("/6")));
        assertTrue("expected null", JSONObject.NULL.equals(jsonArray.query("/7")));
        assertTrue("expected 3 items in [8]", ((Map<?,?>)(JsonPath.read(doc, "$[8]"))).size() == 3);
        assertTrue("expected val10", "val10".equals(jsonArray.query("/8/key10")));
        assertTrue("expected val20", "val20".equals(jsonArray.query("/8/key20")));
        assertTrue("expected val30", "val30".equals(jsonArray.query("/8/key30")));
        assertTrue("expected 2 items in [9]", ((List<?>)(JsonPath.read(doc, "$[9]"))).size() == 2);
        assertTrue("expected 1", Integer.valueOf(1).equals(jsonArray.query("/9/0")));
        assertTrue("expected 2", Integer.valueOf(2).equals(jsonArray.query("/9/1")));
        assertTrue("expected 1 item in [10]", ((Map<?,?>)(JsonPath.read(doc, "$[10]"))).size() == 1);
        assertTrue("expected v1", "v1".equals(jsonArray.query("/10/k1")));
    }

    /**
     * Exercise the JSONArray.remove(index) method 
     * and confirm the resulting JSONArray.
     */
    @Test
    public void remove() {
        String arrayStr1 = 
            "["+
                "1"+
            "]";
        JSONArray jsonArray = new JSONArray(arrayStr1);
        jsonArray.remove(0);
        assertTrue("array should be empty", null == jsonArray.remove(5));
        assertTrue("jsonArray should be empty", jsonArray.length() == 0);
    }

    /**
     * Exercise the JSONArray.similar() method with various parameters
     * and confirm the results when not similar.
     */
    @Test
    public void notSimilar() {
        String arrayStr1 = 
            "["+
                "1"+
            "]";
        JSONArray jsonArray = new JSONArray(arrayStr1);
        JSONArray otherJsonArray = new JSONArray();
        assertTrue("arrays lengths differ", !jsonArray.similar(otherJsonArray));

        JSONObject jsonObject = new JSONObject("{\"k1\":\"v1\"}");
        JSONObject otherJsonObject = new JSONObject();
        jsonArray = new JSONArray();
        jsonArray.put(jsonObject);
        otherJsonArray = new JSONArray();
        otherJsonArray.put(otherJsonObject);
        assertTrue("arrays JSONObjects differ", !jsonArray.similar(otherJsonArray));

        JSONArray nestedJsonArray = new JSONArray("[1, 2]");
        JSONArray otherNestedJsonArray = new JSONArray();
        jsonArray = new JSONArray();
        jsonArray.put(nestedJsonArray);
        otherJsonArray = new JSONArray();
        otherJsonArray.put(otherNestedJsonArray);
        assertTrue("arrays nested JSONArrays differ",
                !jsonArray.similar(otherJsonArray));

        jsonArray = new JSONArray();
        jsonArray.put("hello");
        otherJsonArray = new JSONArray();
        otherJsonArray.put("world");
        assertTrue("arrays values differ",
                !jsonArray.similar(otherJsonArray));
    }

    /**
     * Exercise JSONArray toString() method with various indent levels.
     */
    @Test
    public void jsonArrayToStringIndent() {
        String jsonArray0Str =
                "[" +
                    "[1,2," +
                        "{\"key3\":true}" +
                    "]," +
                    "{\"key1\":\"val1\",\"key2\":" +
                        "{\"key2\":\"val2\"}" +
                    "}," +
                    "[" +
                        "[1,2.1]" +
                    "," +
                        "[null]" +
                    "]" +
                "]";

        String jsonArray1Str =
                "[\n" +
                " [\n" +
                "  1,\n" +
                "  2,\n" +
                "  {\"key3\": true}\n" +
                " ],\n" +
                " {\n" +
                "  \"key1\": \"val1\",\n" +
                "  \"key2\": {\"key2\": \"val2\"}\n" +
                " },\n" +
                " [\n" +
                "  [\n" +
                "   1,\n" +
                "   2.1\n" +
                "  ],\n" +
                "  [null]\n" +
                " ]\n" +
                "]";
        String jsonArray4Str =
                "[\n" +
                "    [\n" +
                "        1,\n" +
                "        2,\n" +
                "        {\"key3\": true}\n" +
                "    ],\n" +
                "    {\n" +
                "        \"key1\": \"val1\",\n" +
                "        \"key2\": {\"key2\": \"val2\"}\n" +
                "    },\n" +
                "    [\n" +
                "        [\n" +
                "            1,\n" +
                "            2.1\n" +
                "        ],\n" +
                "        [null]\n" +
                "    ]\n" +
                "]";
        JSONArray jsonArray = new JSONArray(jsonArray0Str);
        assertEquals(jsonArray0Str, jsonArray.toString());
        assertEquals(jsonArray0Str, jsonArray.toString(0));
        assertEquals(jsonArray1Str, jsonArray.toString(1));
        assertEquals(jsonArray4Str, jsonArray.toString(4));
    }

    /**
     * Convert an empty JSONArray to JSONObject
     */
    @Test
    public void toJSONObject() {
        JSONArray names = new JSONArray();
        JSONArray jsonArray = new JSONArray();
        assertTrue("toJSONObject should return null",
                null == jsonArray.toJSONObject(names));
    }

    /**
     * Confirm the creation of a JSONArray from an array of ints
     */
    @Test
    public void objectArrayVsIsArray() {
        int[] myInts = { 1, 2, 3, 4, 5, 6, 7 };
        Object myObject = myInts;
        JSONArray jsonArray = new JSONArray(myObject);

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertTrue("expected 7 top level items", ((List<?>)(JsonPath.read(doc, "$"))).size() == 7);
        assertTrue("expected 1", Integer.valueOf(1).equals(jsonArray.query("/0")));
        assertTrue("expected 2", Integer.valueOf(2).equals(jsonArray.query("/1")));
        assertTrue("expected 3", Integer.valueOf(3).equals(jsonArray.query("/2")));
        assertTrue("expected 4", Integer.valueOf(4).equals(jsonArray.query("/3")));
        assertTrue("expected 5", Integer.valueOf(5).equals(jsonArray.query("/4")));
        assertTrue("expected 6", Integer.valueOf(6).equals(jsonArray.query("/5")));
        assertTrue("expected 7", Integer.valueOf(7).equals(jsonArray.query("/6")));
    }

    /**
     * Exercise the JSONArray iterator.
     */
    @SuppressWarnings("boxing")
    @Test
    public void iterator() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        Iterator<Object> it = jsonArray.iterator();
        assertTrue("Array true",
                Boolean.TRUE.equals(it.next()));
        assertTrue("Array false",
                Boolean.FALSE.equals(it.next()));
        assertTrue("Array string true",
                "true".equals(it.next()));
        assertTrue("Array string false",
                "false".equals(it.next()));
        assertTrue("Array string",
                "hello".equals(it.next()));

        assertTrue("Array double",
                new Double(23.45e-4).equals(it.next()));
        assertTrue("Array string double",
                new Double(23.45).equals(Double.parseDouble((String)it.next())));

        assertTrue("Array value int",
                new Integer(42).equals(it.next()));
        assertTrue("Array value string int",
                new Integer(43).equals(Integer.parseInt((String)it.next())));

        JSONArray nestedJsonArray = (JSONArray)it.next();
        assertTrue("Array value JSONArray", nestedJsonArray != null);

        JSONObject nestedJsonObject = (JSONObject)it.next();
        assertTrue("Array value JSONObject", nestedJsonObject != null);

        assertTrue("Array value long",
                new Long(0).equals(((Number) it.next()).longValue()));
        assertTrue("Array value string long",
                new Long(-1).equals(Long.parseLong((String) it.next())));
        assertTrue("should be at end of array", !it.hasNext());
    }
    
    @Test(expected = JSONPointerException.class)
    public void queryWithNoResult() {
        new JSONArray().query("/a/b");
    }
    
    @Test
    public void optQueryWithNoResult() {
        assertNull(new JSONArray().optQuery("/a/b"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void optQueryWithSyntaxError() {
        new JSONArray().optQuery("invalid");
    }


    /**
     * Exercise the JSONArray write() method
     */
    @Test
    public void write() throws IOException {
        String str = "[\"value1\",\"value2\",{\"key1\":1,\"key2\":2,\"key3\":3}]";
        JSONArray jsonArray = new JSONArray(str);
        String expectedStr = str;
        StringWriter stringWriter = new StringWriter();
        try {
            jsonArray.write(stringWriter);
            String actualStr = stringWriter.toString();
            assertTrue("write() expected " + expectedStr +
                            " but found " + actualStr,
                    expectedStr.equals(actualStr));
        } finally {
            stringWriter.close();
        }
    }

    /**
     * Exercise the JSONArray write() method using Appendable.
     */
/*
    @Test
    public void writeAppendable() {
        String str = "[\"value1\",\"value2\",{\"key1\":1,\"key2\":2,\"key3\":3}]";
        JSONArray jsonArray = new JSONArray(str);
        String expectedStr = str;
        StringBuilder stringBuilder = new StringBuilder();
        Appendable appendable = jsonArray.write(stringBuilder);
        String actualStr = appendable.toString();
        assertTrue("write() expected " + expectedStr +
                        " but found " + actualStr,
                expectedStr.equals(actualStr));
    }
*/

    /**
     * Exercise the JSONArray write(Writer, int, int) method
     */
    @Test
    public void write3Param() throws IOException {
        String str0 = "[\"value1\",\"value2\",{\"key1\":1,\"key2\":false,\"key3\":3.14}]";
        String str2 =
                "[\n" +
                "   \"value1\",\n" +
                "   \"value2\",\n" +
                "   {\n" +
                "     \"key1\": 1,\n" +
                "     \"key2\": false,\n" +
                "     \"key3\": 3.14\n" +
                "   }\n" +
                " ]";
        JSONArray jsonArray = new JSONArray(str0);
        String expectedStr = str0;
        StringWriter stringWriter = new StringWriter();
        try {
            String actualStr = jsonArray.write(stringWriter, 0, 0).toString();
            assertEquals(expectedStr, actualStr);
        } finally {
            stringWriter.close();
        }
        stringWriter = new StringWriter();
        try {
            expectedStr = str2;
            String actualStr = jsonArray.write(stringWriter, 2, 1).toString();
            assertEquals(expectedStr, actualStr);
        } finally {
            stringWriter.close();
        }
    }

    /**
     * Exercise the JSONArray write(Appendable, int, int) method
     */
/*
    @Test
    public void write3ParamAppendable() {
        String str0 = "[\"value1\",\"value2\",{\"key1\":1,\"key2\":false,\"key3\":3.14}]";
        String str2 =
                "[\n" +
                        "   \"value1\",\n" +
                        "   \"value2\",\n" +
                        "   {\n" +
                        "     \"key1\": 1,\n" +
                        "     \"key2\": false,\n" +
                        "     \"key3\": 3.14\n" +
                        "   }\n" +
                        " ]";
        JSONArray jsonArray = new JSONArray(str0);
        String expectedStr = str0;
        StringBuilder stringBuilder = new StringBuilder();
        Appendable appendable = jsonArray.write(stringBuilder, 0, 0);
        String actualStr = appendable.toString();
        assertEquals(expectedStr, actualStr);

        expectedStr = str2;
        stringBuilder = new StringBuilder();
        appendable = jsonArray.write(stringBuilder, 2, 1);
        actualStr = appendable.toString();
        assertEquals(expectedStr, actualStr);
    }
*/

    /**
     * Exercise JSONArray toString() method with various indent levels.
     */
    @Test
    public void toList() {
        String jsonArrayStr =
                "[" +
                    "[1,2," +
                        "{\"key3\":true}" +
                    "]," +
                    "{\"key1\":\"val1\",\"key2\":" +
                        "{\"key2\":null}," +
                    "\"key3\":42,\"key4\":[]" +
                    "}," +
                    "[" +
                        "[\"value1\",2.1]" +
                    "," +
                        "[null]" +
                    "]" +
                "]";

        JSONArray jsonArray = new JSONArray(jsonArrayStr);
        List<?> list = jsonArray.toList();

        assertTrue("List should not be null", list != null);
        assertTrue("List should have 3 elements", list.size() == 3);

        List<?> val1List = (List<?>) list.get(0);
        assertTrue("val1 should not be null", val1List != null);
        assertTrue("val1 should have 3 elements", val1List.size() == 3);

        assertTrue("val1 value 1 should be 1", val1List.get(0).equals(Integer.valueOf(1)));
        assertTrue("val1 value 2 should be 2", val1List.get(1).equals(Integer.valueOf(2)));

        Map<?,?> key1Value3Map = (Map<?,?>)val1List.get(2);
        assertTrue("Map should not be null", key1Value3Map != null);
        assertTrue("Map should have 1 element", key1Value3Map.size() == 1);
        assertTrue("Map key3 should be true", key1Value3Map.get("key3").equals(Boolean.TRUE));

        Map<?,?> val2Map = (Map<?,?>) list.get(1);
        assertTrue("val2 should not be null", val2Map != null);
        assertTrue("val2 should have 4 elements", val2Map.size() == 4);
        assertTrue("val2 map key 1 should be val1", val2Map.get("key1").equals("val1"));
        assertTrue("val2 map key 3 should be 42", val2Map.get("key3").equals(Integer.valueOf(42)));

        Map<?,?> val2Key2Map = (Map<?,?>)val2Map.get("key2");
        assertTrue("val2 map key 2 should not be null", val2Key2Map != null);
        assertTrue("val2 map key 2 should have an entry", val2Key2Map.containsKey("key2"));
        assertTrue("val2 map key 2 value should be null", val2Key2Map.get("key2") == null);

        List<?> val2Key4List = (List<?>)val2Map.get("key4");
        assertTrue("val2 map key 4 should not be null", val2Key4List != null);
        assertTrue("val2 map key 4 should be empty", val2Key4List.isEmpty());

        List<?> val3List = (List<?>) list.get(2);
        assertTrue("val3 should not be null", val3List != null);
        assertTrue("val3 should have 2 elements", val3List.size() == 2);

        List<?> val3Val1List = (List<?>)val3List.get(0);
        assertTrue("val3 list val 1 should not be null", val3Val1List != null);
        assertTrue("val3 list val 1 should have 2 elements", val3Val1List.size() == 2);
        assertTrue("val3 list val 1 list element 1 should be value1", val3Val1List.get(0).equals("value1"));
        assertTrue("val3 list val 1 list element 2 should be 2.1", val3Val1List.get(1).equals(Double.valueOf("2.1")));

        List<?> val3Val2List = (List<?>)val3List.get(1);
        assertTrue("val3 list val 2 should not be null", val3Val2List != null);
        assertTrue("val3 list val 2 should have 1 element", val3Val2List.size() == 1);
        assertTrue("val3 list val 2 list element 1 should be null", val3Val2List.get(0) == null);

        // assert that toList() is a deep copy
        jsonArray.getJSONObject(1).put("key1", "still val1");
        assertTrue("val2 map key 1 should be val1", val2Map.get("key1").equals("val1"));

        // assert that the new list is mutable
        assertTrue("Removing an entry should succeed", list.remove(2) != null);
        assertTrue("List should have 2 elements", list.size() == 2);
    }
}
