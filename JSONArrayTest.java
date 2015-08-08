package org.json.junit;

import static org.junit.Assert.*;

import java.util.*;

import org.json.*;
import org.junit.Test;


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
        new JSONArray(str);
    }

    /**
     * Attempt to create a JSONArray with an empty string.
     * Expects a JSONException.
     */
    @Test
    public void emptStr() {
        String str = "";
        try {
            new JSONArray(str);
            assertTrue("Should throw an exception", false);
        } catch (JSONException e) {
            assertTrue("Expected an exception message", 
                    "A JSONArray text must start with '[' at 1 [character 2 line 1]".
                    equals(e.getMessage()));
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
            new JSONArray((Object)str);
            assertTrue("Should throw an exception", false);
        } catch (JSONException e) {
            assertTrue("Expected an exception message", 
                    "JSONArray initial value should be a string or collection or array.".
                    equals(e.getMessage()));
        }
    }

    /**
     * Create a JSONArray doc with a variety of different elements.
     * Confirm that the values can be accessed via the get[type]() API methods
     */
    @Test
    public void getArrayValues() {
        JSONArray jsonArray = new JSONArray(arrayStr);
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
        JSONArray jsonArray = new JSONArray(arrayStr);
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
     * array braces are added to the beginning and end, and it is reconverted
     * back to a JSONArray for comparison.
     */
    @Test
    public void join() {
        String expectedStr =
            "["+
                "true,"+
                "false,"+
                "\"true\","+
                "\"false\","+
                "\"hello\","+
                "0.002345,"+
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

        JSONArray jsonArray = new JSONArray(arrayStr);
        String joinStr = jsonArray.join(",");
        JSONArray finalJsonArray = new JSONArray("["+joinStr+"]");
        JSONArray expectedJsonArray = new JSONArray(expectedStr);
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
        Util.compareActualVsExpectedJsonArrays(finalJsonArray, expectedJsonArray);
    }

    /**
     * Confirm the JSONArray.length() method
     */
    @Test 
    public void length() {
        assertTrue("expected empty JSONArray length 0",
                new JSONArray().length() == 0);
        JSONArray jsonArray = new JSONArray(arrayStr);
        assertTrue("expected JSONArray length 13", jsonArray.length() == 13);
        JSONArray nestedJsonArray = jsonArray.getJSONArray(9);
        assertTrue("expected JSONArray length 1", nestedJsonArray.length() == 1);
    }

    /**
     * Create a JSONArray doc with a variety of different elements.
     * Confirm that the values can be accessed via the opt[type](index)
     * and opt[type](index, default) API methods.
     */
    @Test 
    public void opt() {
        JSONArray jsonArray = new JSONArray(arrayStr);
        assertTrue("Array opt value true",
                Boolean.TRUE == jsonArray.opt(0));
        assertTrue("Array opt value out of range",
                null == jsonArray.opt(-1));

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
     * Exercise the JSONArray.put(value) method with various parameters
     * and confirm the resulting JSONArray.
     */
    @Test
    public void put() {
        String expectedStr =
            "["+
                "true,"+
                "false,"+
                "["+
                    "hello,"+
                    "world"+
                "],"+
                "2.5,"+
                "1,"+
                "45,"+
                "\"objectPut\","+
                "{"+
                    "\"key10\":\"val10\","+
                    "\"key20\":\"val20\","+
                    "\"key30\":\"val30\""+
                "},"+
                "{"+
                    "\"k1\":\"v1\""+
                "},"+
                "["+
                    "1,"+
                    "2"+
                "]"+
            "]";
        JSONArray jsonArray = new JSONArray();
        JSONArray expectedJsonArray = new JSONArray(expectedStr);

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
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
    }

    /**
     * Exercise the JSONArray.put(index, value) method with various parameters
     * and confirm the resulting JSONArray.
     */
    @Test
    public void putIndex() {
        String expectedStr =
            "["+
                "true,"+
                "false,"+
                "["+
                    "hello,"+
                    "world"+
                "],"+
                "2.5,"+
                "1,"+
                "45,"+
                "\"objectPut\","+
                "null,"+
                "{"+
                    "\"key10\":\"val10\","+
                    "\"key20\":\"val20\","+
                    "\"key30\":\"val30\""+
                "},"+
                "["+
                    "1,"+
                    "2"+
                "],"+
                "{"+
                    "\"k1\":\"v1\""+
                "},"+
            "]";
        JSONArray jsonArray = new JSONArray();
        JSONArray expectedJsonArray = new JSONArray(expectedStr);

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
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
    }

    /**
     * Exercise the JSONArray.remove(index) method 
     * and confirm the resulting JSONArray.
     */
    @Test
    public void remove() {
        String arrayStr = 
            "["+
                "1"+
            "]";
        JSONArray jsonArray = new JSONArray(arrayStr);
        JSONArray expectedJsonArray = new JSONArray();
        jsonArray.remove(0);
        assertTrue("array should be empty", null == jsonArray.remove(5));
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
    }

    /**
     * Exercise the JSONArray.similar() method with various parameters
     * and confirm the results when not similar.
     */
    @Test
    public void notSimilar() {
        String arrayStr = 
            "["+
                "1"+
            "]";
        JSONArray jsonArray = new JSONArray(arrayStr);
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
        String expectedStr = 
            "["+
                "1,2,3,4,5,6,7"+
            "]";
        int[] myInts = { 1, 2, 3, 4, 5, 6, 7 };
        Object myObject = myInts;
        JSONArray jsonArray = new JSONArray(myObject);
        JSONArray expectedJsonArray = new JSONArray(expectedStr);
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
    }

    /**
     * Exercise the JSONArray iterator.
     */
    @Test
    public void iterator() {
        JSONArray jsonArray = new JSONArray(arrayStr);
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
}
