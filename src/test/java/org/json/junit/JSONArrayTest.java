package org.json.junit;

/*
Public Domain.
*/

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONParserConfiguration;
import org.json.JSONPointerException;
import org.json.JSONString;
import org.json.JSONTokener;
import org.json.ParserConfiguration;
import org.json.junit.data.MyJsonString;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;


/**
 * Tests for JSON-Java JSONArray.java
 */
public class JSONArrayTest {
    private final String arrayStr = 
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
     * Tests that the similar method is working as expected.
     */
    @Test
    void verifySimilar() {
        final String string1 = "HasSameRef";
        final String string2 = "HasDifferentRef";
        JSONArray obj1 = new JSONArray()
                .put("abc")
                .put(string1)
                .put(2);
        
        JSONArray obj2 = new JSONArray()
                .put("abc")
                .put(string1)
                .put(3);

        JSONArray obj3 = new JSONArray()
                .put("abc")
                .put(new String(string1))
                .put(2);

        JSONArray obj4 = new JSONArray()
                .put("abc")
                .put(2.0)
        		.put(new String(string1));

        JSONArray obj5 = new JSONArray()
                .put("abc")
                .put(2.0)
        		.put(new String(string2));
        
        assertFalse(obj1.similar(obj2), "obj1-obj2 Should eval to false");
        assertTrue(obj1.similar(obj3), "obj1-obj3 Should eval to true");
        assertFalse(obj4.similar(obj5), "obj4-obj5 Should eval to false");
    }

    /**
     * Attempt to create a JSONArray with a null string.
     * Expects a NullPointerException.
     */
    @Test
    void nullException() {
        assertThrows(NullPointerException.class, () -> {
            String str = null;
            assertNull(new JSONArray(str), "Should throw an exception");
        });
    }

    /**
     * Attempt to create a JSONArray with an empty string.
     * Expects a JSONException.
     */
    @Test
    void emptyStr() {
        String str = "";
        try {
            assertNull(new JSONArray(str), "Should throw an exception");
        } catch (JSONException e) {
            assertEquals("A JSONArray text must start with '[' at 0 [character 1 line 1]",
                    e.getMessage(),
                    "Expected an exception message");
        }
    }

    /**
     * Attempt to create a JSONArray with an unclosed array.
     * Expects an exception
     */
    @Test
    void unclosedArray() {
        try {
            assertNull(new JSONArray("["), "Should throw an exception");
        } catch (JSONException e) {
            assertEquals("Expected a ',' or ']' at 1 [character 2 line 1]",
                    e.getMessage(),
                    "Expected an exception message");
        }
    }

    /**
     * Attempt to create a JSONArray with an unclosed array.
     * Expects an exception
     */
    @Test
    void unclosedArray2() {
        try {
            assertNull(new JSONArray("[\"test\""), "Should throw an exception");
        } catch (JSONException e) {
            assertEquals("Expected a ',' or ']' at 7 [character 8 line 1]",
                    e.getMessage(),
                    "Expected an exception message");
        }
    }

    /**
     * Attempt to create a JSONArray with an unclosed array.
     * Expects an exception
     */
    @Test
    void unclosedArray3() {
        try {
            assertNull(new JSONArray("[\"test\","), "Should throw an exception");
        } catch (JSONException e) {
            assertEquals("Expected a ',' or ']' at 8 [character 9 line 1]",
                    e.getMessage(),
                    "Expected an exception message");
        }
    }

    /**
     * Attempt to create a JSONArray with a string as object that is
     * not a JSON array doc.
     * Expects a JSONException.
     */
    @Test
    void badObject() {
        String str = "abc";
        try {
            assertNull(new JSONArray((Object)str), "Should throw an exception");
        } catch (JSONException e) {
            assertEquals("JSONArray initial value should be a string or collection or array.", e.getMessage(), "Expected an exception message");
        }
    }

    /**
     * Verifies that the constructor has backwards compatibility with RAW types pre-java5.
     */
    @Test
    void verifyConstructor() {
        
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
                expected.similar(jaRaw),
                "The RAW Collection should give me the same as the Typed Collection");
        assertTrue(
                expected.similar(jaInt),
                "The RAW Collection should give me the same as the Typed Collection");
        assertTrue(
                expected.similar(jaObj),
                "The RAW Collection should give me the same as the Typed Collection");
        Util.checkJSONArrayMaps(expected);
        Util.checkJSONArrayMaps(jaObj);
        Util.checkJSONArrayMaps(jaRaw);
        Util.checkJSONArrayMaps(jaInt);
    }

    /**
     * Tests consecutive calls to putAll with array and collection.
     */
    @Test
    void verifyPutAll() {
        final JSONArray jsonArray = new JSONArray();

        // array
        int[] myInts = { 1, 2, 3, 4, 5 };
        jsonArray.putAll(myInts);

        assertEquals(jsonArray.length(),
                     myInts.length,
                     "int arrays lengths should be equal");

        for (int i = 0; i < myInts.length; i++) {
            assertEquals(myInts[i],
                         jsonArray.getInt(i),
                         "int arrays elements should be equal");
        }

        // collection
        List<String> myList = Arrays.asList("one", "two", "three", "four", "five");
        jsonArray.putAll(myList);

        int len = myInts.length + myList.size();

        assertEquals(jsonArray.length(),
                     len,
                     "arrays lengths should be equal");

        for (int i = 0; i < myList.size(); i++) {
            assertEquals(myList.get(i),
                         jsonArray.getString(myInts.length + i),
                         "collection elements should be equal");
        }
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Verifies that the put Collection has backwards compatibility with RAW types pre-java5.
     */
    @Test
    void verifyPutCollection() {
        
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
                expected.similar(jaRaw),
                "The RAW Collection should give me the same as the Typed Collection");
        assertTrue(
                expected.similar(jaObj),
                "The RAW Collection should give me the same as the Typed Collection");
        assertTrue(
                expected.similar(jaInt),
                "The RAW Collection should give me the same as the Typed Collection");
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                jaRaw, jaObj, jaInt
        )));
    }


    /**
     * Verifies that the put Map has backwards compatibility with RAW types pre-java5.
     */
    @Test
    void verifyPutMap() {
        
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
                expected.similar(jaRaw),
                "The RAW Collection should give me the same as the Typed Collection");
        assertTrue(
                expected.similar(jaStrObj),
                "The RAW Collection should give me the same as the Typed Collection");
        assertTrue(
                expected.similar(jaStrInt),
                "The RAW Collection should give me the same as the Typed Collection");
        assertTrue(
                expected.similar(jaObjObj),
                "The RAW Collection should give me the same as the Typed Collection");
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                expected, jaRaw, jaStrObj, jaStrInt, jaObjObj
        )));
    }

    /**
     * Create a JSONArray doc with a variety of different elements.
     * Confirm that the values can be accessed via the get[type]() API methods
     */
    @SuppressWarnings("boxing")
    @Test
    void getArrayValues() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        // booleans
        assertTrue(jsonArray.getBoolean(0), "Array true");
        assertFalse(jsonArray.getBoolean(1), "Array false");
        assertTrue(jsonArray.getBoolean(2), "Array string true");
        assertFalse(jsonArray.getBoolean(3), "Array string false");
        // strings
        assertEquals("hello", jsonArray.getString(4), "Array value string");
        // doubles
        assertEquals(Double.valueOf(23.45e-4), jsonArray.getDouble(5), "Array double");
        assertEquals(Double.valueOf(23.45), jsonArray.getDouble(6), "Array string double");
        assertEquals(Float.valueOf(23.45e-4f), jsonArray.getFloat(5), "Array double can be float");
        // ints
        assertEquals(Integer.valueOf(42), jsonArray.getInt(7), "Array value int");
        assertEquals(Integer.valueOf(43), jsonArray.getInt(8), "Array value string int");
        // nested objects
        JSONArray nestedJsonArray = jsonArray.getJSONArray(9);
        assertTrue(nestedJsonArray != null, "Array value JSONArray");
        JSONObject nestedJsonObject = jsonArray.getJSONObject(10);
        assertTrue(nestedJsonObject != null, "Array value JSONObject");
        // longs
        assertEquals(Long.valueOf(0), jsonArray.getLong(11), "Array value long");
        assertEquals(Long.valueOf(-1), jsonArray.getLong(12), "Array value string long");

        assertTrue(jsonArray.isNull(-1), "Array value null");
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Create a JSONArray doc with a variety of different elements.
     * Confirm that attempting to get the wrong types via the get[type]()
     * API methods result in JSONExceptions
     */
    @Test
    void failedGetArrayValues() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        try {
            jsonArray.getBoolean(4);
            assertTrue(false, "expected getBoolean to fail");
        } catch (JSONException e) {
            assertEquals("JSONArray[4] is not a boolean (class java.lang.String : hello).",e.getMessage(),"Expected an exception message");
        }
        try {
            jsonArray.get(-1);
            assertTrue(false, "expected get to fail");
        } catch (JSONException e) {
            assertEquals("JSONArray[-1] not found.",e.getMessage(),"Expected an exception message");
        }
        try {
            jsonArray.getDouble(4);
            assertTrue(false, "expected getDouble to fail");
        } catch (JSONException e) {
            assertEquals("JSONArray[4] is not a double (class java.lang.String : hello).",e.getMessage(),"Expected an exception message");
        }
        try {
            jsonArray.getInt(4);
            assertTrue(false, "expected getInt to fail");
        } catch (JSONException e) {
            assertEquals("JSONArray[4] is not a int (class java.lang.String : hello).",e.getMessage(),"Expected an exception message");
        }
        try {
            jsonArray.getJSONArray(4);
            assertTrue(false, "expected getJSONArray to fail");
        } catch (JSONException e) {
            assertEquals("JSONArray[4] is not a JSONArray (class java.lang.String : hello).",e.getMessage(),"Expected an exception message");
        }
        try {
            jsonArray.getJSONObject(4);
            assertTrue(false, "expected getJSONObject to fail");
        } catch (JSONException e) {
            assertEquals("JSONArray[4] is not a JSONObject (class java.lang.String : hello).",e.getMessage(),"Expected an exception message");
        }
        try {
            jsonArray.getLong(4);
            assertTrue(false, "expected getLong to fail");
        } catch (JSONException e) {
            assertEquals("JSONArray[4] is not a long (class java.lang.String : hello).",e.getMessage(),"Expected an exception message");
        }
        try {
            jsonArray.getString(5);
            assertTrue(false, "expected getString to fail");
        } catch (JSONException e) {
            assertEquals("JSONArray[5] is not a String (class java.math.BigDecimal : 0.002345).",e.getMessage(),"Expected an exception message");
        }
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * The JSON parser is permissive of unambiguous unquoted keys and values.
     * Such JSON text should be allowed, even if it does not strictly conform
     * to the spec. However, after being parsed, toString() should emit strictly
     * conforming JSON text.  
     */
    @Test
    void unquotedText() {
        String str = "[value1, something!, (parens), foo@bar.com, 23, 23+45]";
        JSONArray jsonArray = new JSONArray(str);
        List<Object> expected = Arrays.asList("value1", "something!", "(parens)", "foo@bar.com", 23, "23+45");
        assertEquals(expected, jsonArray.toList());
    }

    /**
     * Exercise JSONArray.join() by converting a JSONArray into a 
     * comma-separated string. Since this is very nearly a JSON document,
     * array braces are added to the beginning and end prior to validation.
     */
    @Test
    void join() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        String joinStr = jsonArray.join(",");

        // validate JSON
        /**
         * Don't need to remake the JSONArray to perform the parsing
         */
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse("["+joinStr+"]");
        assertEquals(13, ((List<?>)(JsonPath.read(doc, "$"))).size(), "expected 13 items in top level object");
        assertEquals(Boolean.TRUE, jsonArray.query("/0"), "expected true");
        assertEquals(Boolean.FALSE, jsonArray.query("/1"), "expected false");
        assertEquals("true", jsonArray.query("/2"), "expected \"true\"");
        assertEquals("false", jsonArray.query("/3"), "expected \"false\"");
        assertEquals("hello", jsonArray.query("/4"), "expected hello");
        assertEquals(BigDecimal.valueOf(0.002345), jsonArray.query("/5"), "expected 0.002345");
        assertEquals("23.45", jsonArray.query("/6"), "expected \"23.45\"");
        assertEquals(Integer.valueOf(42), jsonArray.query("/7"), "expected 42");
        assertEquals("43", jsonArray.query("/8"), "expected \"43\"");
        assertEquals(1, ((List<?>)(JsonPath.read(doc, "$[9]"))).size(), "expected 1 item in [9]");
        assertEquals("world", jsonArray.query("/9/0"), "expected world");
        assertEquals(4, ((Map<?, ?>)(JsonPath.read(doc, "$[10]"))).size(), "expected 4 items in [10]");
        assertEquals("value1", jsonArray.query("/10/key1"), "expected value1");
        assertEquals("value2", jsonArray.query("/10/key2"), "expected value2");
        assertEquals("value3", jsonArray.query("/10/key3"), "expected value3");
        assertEquals("value4", jsonArray.query("/10/key4"), "expected value4");
        assertEquals(Integer.valueOf(0), jsonArray.query("/11"), "expected 0");
        assertEquals("-1", jsonArray.query("/12"), "expected \"-1\"");
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Confirm the JSONArray.length() method
     */
    @Test
    void length() {
        assertEquals(0, new JSONArray().length(), "expected empty JSONArray length 0");
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        assertEquals(13, jsonArray.length(), "expected JSONArray length 13. instead found " + jsonArray.length());
        JSONArray nestedJsonArray = jsonArray.getJSONArray(9);
        assertEquals(1, nestedJsonArray.length(), "expected JSONArray length 1");
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                jsonArray, nestedJsonArray
        )));
    }

    /**
     * Create a JSONArray doc with a variety of different elements.
     * Confirm that the values can be accessed via the opt[type](index)
     * and opt[type](index, default) API methods.
     */
    @SuppressWarnings("boxing")
    @Test
    void opt() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        assertTrue(Boolean.TRUE == jsonArray.opt(0),
                "Array opt value true");
        assertTrue(null == jsonArray.opt(-1),
                "Array opt value out of range");

        assertTrue(null == jsonArray.opt(jsonArray.length()),
                "Array opt value out of range");

         assertTrue(Boolean.TRUE == jsonArray.optBoolean(0),
                "Array opt boolean");
        assertTrue(Boolean.FALSE == jsonArray.optBoolean(-1, Boolean.FALSE),
                "Array opt boolean default");
        assertTrue(Boolean.FALSE == jsonArray.optBoolean(-1),
                "Array opt boolean implicit default");

        assertEquals(Boolean.TRUE, jsonArray.optBooleanObject(0), "Array opt boolean object");
        assertEquals(Boolean.FALSE, jsonArray.optBooleanObject(-1, Boolean.FALSE), "Array opt boolean object default");
        assertEquals(Boolean.FALSE, jsonArray.optBooleanObject(-1), "Array opt boolean object implicit default");

        assertEquals(Double.valueOf(23.45e-4), jsonArray.optDouble(5), "Array opt double");
        assertEquals(Double.valueOf(1), jsonArray.optDouble(0, 1), "Array opt double default");
        assertTrue(Double.valueOf(jsonArray.optDouble(99)).isNaN(),
           "Array opt double default implicit");

        assertEquals(Double.valueOf(23.45e-4), jsonArray.optDoubleObject(5), "Array opt double object");
        assertEquals(Double.valueOf(1), jsonArray.optDoubleObject(0, 1D), "Array opt double object default");
        assertTrue(jsonArray.optDoubleObject(99).isNaN(),
                "Array opt double object default implicit");

        assertEquals(Float.valueOf(Double.valueOf(23.45e-4).floatValue()), jsonArray.optFloat(5), "Array opt float");
        assertEquals(Float.valueOf(1), jsonArray.optFloat(0, 1), "Array opt float default");
        assertTrue(Float.valueOf(jsonArray.optFloat(99)).isNaN(),
           "Array opt float default implicit");

        assertEquals(Float.valueOf(23.45e-4F), jsonArray.optFloatObject(5), "Array opt float object");
        assertEquals(Float.valueOf(1), jsonArray.optFloatObject(0, 1F), "Array opt float object default");
        assertTrue(jsonArray.optFloatObject(99).isNaN(),
                "Array opt float object default implicit");

        assertEquals(BigDecimal.valueOf(23.45e-4), jsonArray.optNumber(5), "Array opt Number");
        assertEquals(Double.valueOf(1), jsonArray.optNumber(0, 1d), "Array opt Number default");
        assertTrue(Double.valueOf(jsonArray.optNumber(99,Double.NaN).doubleValue()).isNaN(),
           "Array opt Number default implicit");

        assertEquals(Integer.valueOf(42), jsonArray.optInt(7), "Array opt int");
        assertEquals(Integer.valueOf(-1), jsonArray.optInt(0, -1), "Array opt int default");
        assertEquals(0, jsonArray.optInt(0), "Array opt int default implicit");

        assertEquals(Integer.valueOf(42), jsonArray.optIntegerObject(7), "Array opt int object");
        assertEquals(Integer.valueOf(-1), jsonArray.optIntegerObject(0, -1), "Array opt int object default");
        assertEquals(Integer.valueOf(0), jsonArray.optIntegerObject(0), "Array opt int object default implicit");

        JSONArray nestedJsonArray = jsonArray.optJSONArray(9);
        assertTrue(nestedJsonArray != null, "Array opt JSONArray");
        assertTrue(null == jsonArray.optJSONArray(99),
                "Array opt JSONArray null");
        assertEquals("value", jsonArray.optJSONArray(99, new JSONArray("[\"value\"]")).getString(0), "Array opt JSONArray default");

        JSONObject nestedJsonObject = jsonArray.optJSONObject(10);
        assertTrue(nestedJsonObject != null, "Array opt JSONObject");
        assertTrue(null == jsonArray.optJSONObject(99),
                "Array opt JSONObject null");
        assertEquals("value", jsonArray.optJSONObject(99, new JSONObject("{\"key\":\"value\"}")).getString("key"), "Array opt JSONObject default");

        assertEquals(0, jsonArray.optLong(11), "Array opt long");
        assertEquals(-2, jsonArray.optLong(-1, -2), "Array opt long default");
        assertEquals(0, jsonArray.optLong(-1), "Array opt long default implicit");

        assertEquals(Long.valueOf(0), jsonArray.optLongObject(11), "Array opt long object");
        assertEquals(Long.valueOf(-2), jsonArray.optLongObject(-1, -2L), "Array opt long object default");
        assertEquals(Long.valueOf(0), jsonArray.optLongObject(-1), "Array opt long object default implicit");

        assertEquals("hello", jsonArray.optString(4), "Array opt string");
        assertEquals("", jsonArray.optString(-1), "Array opt string default implicit");
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                jsonArray, nestedJsonArray
        )));
        Util.checkJSONObjectMaps(nestedJsonObject);
    }

    /**
     * Verifies that the opt methods properly convert string values.
     */
    @Test
    void optStringConversion(){
        JSONArray ja = new JSONArray("[\"123\",\"true\",\"false\"]");
        assertTrue(ja.optBoolean(1, false), "unexpected optBoolean value");
        assertEquals(Boolean.valueOf(true), ja.optBooleanObject(1, false), "unexpected optBooleanObject value");
        assertFalse(ja.optBoolean(2, true), "unexpected optBoolean value");
        assertEquals(Boolean.valueOf(false), ja.optBooleanObject(2, true), "unexpected optBooleanObject value");
        assertEquals(123, ja.optInt(0, 0), "unexpected optInt value");
        assertEquals(Integer.valueOf(123), ja.optIntegerObject(0, 0), "unexpected optIntegerObject value");
        assertEquals(123, ja.optLong(0, 0), "unexpected optLong value");
        assertEquals(Long.valueOf(123), ja.optLongObject(0, 0L), "unexpected optLongObject value");
        assertEquals(123.0, ja.optDouble(0, 0.0), "unexpected optDouble value");
        assertEquals(Double.valueOf(123.0), ja.optDoubleObject(0, 0.0), "unexpected optDoubleObject value");
        assertEquals(0, ja.optBigInteger(0, BigInteger.ZERO).compareTo(new BigInteger("123")), "unexpected optBigInteger value");
        assertEquals(0, ja.optBigDecimal(0, BigDecimal.ZERO).compareTo(new BigDecimal("123")), "unexpected optBigDecimal value");
        Util.checkJSONArrayMaps(ja);
    }

    /**
     * Exercise the JSONArray.put(value) method with various parameters
     * and confirm the resulting JSONArray.
     */
    @SuppressWarnings("boxing")
    @Test
    void put() {
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
        assertEquals(10, ((List<?>)(JsonPath.read(doc, "$"))).size(), "expected 10 top level items");
        assertEquals(Boolean.TRUE, jsonArray.query("/0"), "expected true");
        assertEquals(Boolean.FALSE, jsonArray.query("/1"), "expected false");
        assertEquals(2, ((List<?>)(JsonPath.read(doc, "$[2]"))).size(), "expected 2 items in [2]");
        assertEquals("hello", jsonArray.query("/2/0"), "expected hello");
        assertEquals("world", jsonArray.query("/2/1"), "expected world");
        assertEquals(Double.valueOf(2.5), jsonArray.query("/3"), "expected 2.5");
        assertEquals(Integer.valueOf(1), jsonArray.query("/4"), "expected 1");
        assertEquals(Long.valueOf(45), jsonArray.query("/5"), "expected 45");
        assertEquals("objectPut", jsonArray.query("/6"), "expected objectPut");
        assertEquals(3, ((Map<?, ?>)(JsonPath.read(doc, "$[7]"))).size(), "expected 3 items in [7]");
        assertEquals("val10", jsonArray.query("/7/key10"), "expected val10");
        assertEquals("val20", jsonArray.query("/7/key20"), "expected val20");
        assertEquals("val30", jsonArray.query("/7/key30"), "expected val30");
        assertEquals(1, ((Map<?, ?>)(JsonPath.read(doc, "$[8]"))).size(), "expected 1 item in [8]");
        assertEquals("v1", jsonArray.query("/8/k1"), "expected v1");
        assertEquals(2, ((List<?>)(JsonPath.read(doc, "$[9]"))).size(), "expected 2 items in [9]");
        assertEquals(Integer.valueOf(1), jsonArray.query("/9/0"), "expected 1");
        assertEquals(Integer.valueOf(2), jsonArray.query("/9/1"), "expected 2");
        Util.checkJSONArrayMaps(jsonArray);
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * Exercise the JSONArray.put(index, value) method with various parameters
     * and confirm the resulting JSONArray.
     */
    @SuppressWarnings("boxing")
    @Test
    void putIndex() {
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
            assertTrue(false, "put index < 0 should have thrown exception");
        } catch(Exception ignored) {}

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertEquals(11, ((List<?>)(JsonPath.read(doc, "$"))).size(), "expected 11 top level items");
        assertEquals(Boolean.TRUE, jsonArray.query("/0"), "expected true");
        assertEquals(Boolean.FALSE, jsonArray.query("/1"), "expected false");
        assertEquals(2, ((List<?>)(JsonPath.read(doc, "$[2]"))).size(), "expected 2 items in [2]");
        assertEquals("hello", jsonArray.query("/2/0"), "expected hello");
        assertEquals("world", jsonArray.query("/2/1"), "expected world");
        assertEquals(Double.valueOf(2.5), jsonArray.query("/3"), "expected 2.5");
        assertEquals(Integer.valueOf(1), jsonArray.query("/4"), "expected 1");
        assertEquals(Long.valueOf(45), jsonArray.query("/5"), "expected 45");
        assertEquals("objectPut", jsonArray.query("/6"), "expected objectPut");
        assertEquals(JSONObject.NULL, jsonArray.query("/7"), "expected null");
        assertEquals(3, ((Map<?, ?>)(JsonPath.read(doc, "$[8]"))).size(), "expected 3 items in [8]");
        assertEquals("val10", jsonArray.query("/8/key10"), "expected val10");
        assertEquals("val20", jsonArray.query("/8/key20"), "expected val20");
        assertEquals("val30", jsonArray.query("/8/key30"), "expected val30");
        assertEquals(2, ((List<?>)(JsonPath.read(doc, "$[9]"))).size(), "expected 2 items in [9]");
        assertEquals(Integer.valueOf(1), jsonArray.query("/9/0"), "expected 1");
        assertEquals(Integer.valueOf(2), jsonArray.query("/9/1"), "expected 2");
        assertEquals(1, ((Map<?, ?>)(JsonPath.read(doc, "$[10]"))).size(), "expected 1 item in [10]");
        assertEquals("v1", jsonArray.query("/10/k1"), "expected v1");
        Util.checkJSONObjectMaps(jsonObject);
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Exercise the JSONArray.remove(index) method 
     * and confirm the resulting JSONArray.
     */
    @Test
    void remove() {
        String arrayStr1 = 
            "["+
                "1"+
            "]";
        JSONArray jsonArray = new JSONArray(arrayStr1);
        jsonArray.remove(0);
        assertTrue(null == jsonArray.remove(5), "array should be empty");
        assertTrue(jsonArray.isEmpty(), "jsonArray should be empty");
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Exercise the JSONArray.similar() method with various parameters
     * and confirm the results when not similar.
     */
    @Test
    void notSimilar() {
        String arrayStr1 = 
            "["+
                "1"+
            "]";
        JSONArray jsonArray = new JSONArray(arrayStr1);
        JSONArray otherJsonArray = new JSONArray();
        assertFalse(jsonArray.similar(otherJsonArray), "arrays lengths differ");

        JSONObject jsonObject = new JSONObject("{\"k1\":\"v1\"}");
        JSONObject otherJsonObject = new JSONObject();
        jsonArray = new JSONArray();
        jsonArray.put(jsonObject);
        otherJsonArray = new JSONArray();
        otherJsonArray.put(otherJsonObject);
        assertFalse(jsonArray.similar(otherJsonArray), "arrays JSONObjects differ");

        JSONArray nestedJsonArray = new JSONArray("[1, 2]");
        JSONArray otherNestedJsonArray = new JSONArray();
        jsonArray = new JSONArray();
        jsonArray.put(nestedJsonArray);
        otherJsonArray = new JSONArray();
        otherJsonArray.put(otherNestedJsonArray);
        assertFalse(jsonArray.similar(otherJsonArray), "arrays nested JSONArrays differ");

        jsonArray = new JSONArray();
        jsonArray.put("hello");
        otherJsonArray = new JSONArray();
        otherJsonArray.put("world");
        assertFalse(jsonArray.similar(otherJsonArray), "arrays values differ");
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                jsonArray, otherJsonArray
        )));
        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                jsonObject, otherJsonObject
        )));
    }

    /**
     * Exercise JSONArray toString() method with various indent levels.
     */
    @Test
    void jsonArrayToStringIndent() {
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

        String jsonArray1Strs [] = 
            {
                "[",
                " [",
                "  1,",
                "  2,",
                "  {\"key3\": true}",
                " ],",
                " {",
                "  \"key1\": \"val1\",",
                "  \"key2\": {\"key2\": \"val2\"}",
                " },",
                " [",
                "  [",
                "   1,",
                "   2.1",
                "  ],",
                "  [null]",
                " ]",
                "]"
            };
        String jsonArray4Strs [] =
            {
                "[",
                "    [",
                "        1,",
                "        2,",
                "        {\"key3\": true}",
                "    ],",
                "    {",
                "        \"key1\": \"val1\",",
                "        \"key2\": {\"key2\": \"val2\"}",
                "    },",
                "    [",
                "        [",
                "            1,",
                "            2.1",
                "        ],",
                "        [null]",
                "    ]",
                "]"
            };
        JSONArray jsonArray = new JSONArray(jsonArray0Str);
        String [] actualStrArray = jsonArray.toString().split("\\r?\\n");
        assertEquals(1, actualStrArray.length, "Expected 1 line");
        actualStrArray = jsonArray.toString(0).split("\\r?\\n");
        assertEquals(1, actualStrArray.length, "Expected 1 line");

        actualStrArray = jsonArray.toString(1).split("\\r?\\n");
        assertEquals(jsonArray1Strs.length, actualStrArray.length, "Expected lines");
        List<String> list = Arrays.asList(actualStrArray);
        for (String s : jsonArray1Strs) {
            list.contains(s);
        }
        
        actualStrArray = jsonArray.toString(4).split("\\r?\\n");
        assertEquals(jsonArray1Strs.length, actualStrArray.length, "Expected lines");
        list = Arrays.asList(actualStrArray);
        for (String s : jsonArray4Strs) {
            list.contains(s);
        }
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Convert an empty JSONArray to JSONObject
     */
    @Test
    void toJSONObject() {
        JSONArray names = new JSONArray();
        JSONArray jsonArray = new JSONArray();
        assertTrue(null == jsonArray.toJSONObject(names),
                "toJSONObject should return null");
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                names, jsonArray
        )));
    }

    /**
     * Confirm the creation of a JSONArray from an array of ints
     */
    @Test
    void objectArrayVsIsArray() {
        int[] myInts = { 1, 2, 3, 4, 5, 6, 7 };
        Object myObject = myInts;
        JSONArray jsonArray = new JSONArray(myObject);

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertEquals(7, ((List<?>)(JsonPath.read(doc, "$"))).size(), "expected 7 top level items");
        assertEquals(Integer.valueOf(1), jsonArray.query("/0"), "expected 1");
        assertEquals(Integer.valueOf(2), jsonArray.query("/1"), "expected 2");
        assertEquals(Integer.valueOf(3), jsonArray.query("/2"), "expected 3");
        assertEquals(Integer.valueOf(4), jsonArray.query("/3"), "expected 4");
        assertEquals(Integer.valueOf(5), jsonArray.query("/4"), "expected 5");
        assertEquals(Integer.valueOf(6), jsonArray.query("/5"), "expected 6");
        assertEquals(Integer.valueOf(7), jsonArray.query("/6"), "expected 7");
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Exercise the JSONArray iterator.
     */
    @SuppressWarnings("boxing")
    @Test
    void iteratorTest() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        Iterator<Object> it = jsonArray.iterator();
        assertEquals(Boolean.TRUE, it.next(), "Array true");
        assertEquals(Boolean.FALSE, it.next(), "Array false");
        assertEquals("true", it.next(), "Array string true");
        assertEquals("false", it.next(), "Array string false");
        assertEquals("hello", it.next(), "Array string");

        assertEquals(new BigDecimal("0.002345"), it.next(), "Array double [23.45e-4]");
        assertEquals(Double.valueOf(23.45), Double.parseDouble((String)it.next()), "Array string double");

        assertEquals(Integer.valueOf(42), it.next(), "Array value int");
        assertEquals(Integer.valueOf(43), Integer.parseInt((String)it.next()), "Array value string int");

        JSONArray nestedJsonArray = (JSONArray)it.next();
        assertTrue(nestedJsonArray != null, "Array value JSONArray");

        JSONObject nestedJsonObject = (JSONObject)it.next();
        assertTrue(nestedJsonObject != null, "Array value JSONObject");

        assertEquals(Long.valueOf(0), ((Number)it.next()).longValue(), "Array value long");
        assertEquals(Long.valueOf(-1), Long.parseLong((String)it.next()), "Array value string long");
        assertFalse(it.hasNext(), "should be at end of array");
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                jsonArray, nestedJsonArray
        )));
        Util.checkJSONObjectMaps(nestedJsonObject);
    }

    @Test
    void queryWithNoResult() {
        assertThrows(JSONPointerException.class, () -> {
            new JSONArray().query("/a/b");
        });
    }

    @Test
    void optQueryWithNoResult() {
        assertNull(new JSONArray().optQuery("/a/b"));
    }

    @Test
    void optQueryWithSyntaxError() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JSONArray().optQuery("invalid");
        });
    }


    /**
     * Exercise the JSONArray write() method
     */
    @Test
    void write() throws IOException {
        String str = "[\"value1\",\"value2\",{\"key1\":1,\"key2\":2,\"key3\":3}]";
        JSONArray jsonArray = new JSONArray(str);
        String expectedStr = str;
        StringWriter stringWriter = new StringWriter();
        try {
            jsonArray.write(stringWriter);
            String actualStr = stringWriter.toString();
            JSONArray finalArray = new JSONArray(actualStr);
            Util.compareActualVsExpectedJsonArrays(jsonArray, finalArray);
            assertTrue(actualStr.startsWith("[\"value1\",\"value2\",{")
                    && actualStr.contains("\"key1\":1")
                    && actualStr.contains("\"key2\":2")
                    && actualStr.contains("\"key3\":3"),
                    "write() expected " + expectedStr +
                    " but found " + actualStr
                    );
        } finally {
            stringWriter.close();
        }
        Util.checkJSONArrayMaps(jsonArray);
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
    void write3Param() throws IOException {
        String str0 = "[\"value1\",\"value2\",{\"key1\":1,\"key2\":false,\"key3\":3.14}]";
        JSONArray jsonArray = new JSONArray(str0);
        String expectedStr = str0;
        StringWriter stringWriter = new StringWriter();
        try {
            String actualStr = jsonArray.write(stringWriter, 0, 0).toString();
            JSONArray finalArray = new JSONArray(actualStr);
            Util.compareActualVsExpectedJsonArrays(jsonArray, finalArray);
            assertTrue(actualStr.startsWith("[\"value1\",\"value2\",{")
                && actualStr.contains("\"key1\":1")
                && actualStr.contains("\"key2\":false")
                && actualStr.contains("\"key3\":3.14"),
                "write() expected " + expectedStr +
                " but found " + actualStr
            );
        } finally {
            stringWriter.close();
        }
        
        stringWriter = new StringWriter();
        try {
            String actualStr = jsonArray.write(stringWriter, 2, 1).toString();
            JSONArray finalArray = new JSONArray(actualStr);
            Util.compareActualVsExpectedJsonArrays(jsonArray, finalArray);
            assertTrue(actualStr.startsWith("[\n" + 
                        "   \"value1\",\n" + 
                        "   \"value2\",\n" + 
                        "   {")
                && actualStr.contains("\"key1\": 1")
                && actualStr.contains("\"key2\": false")
                && actualStr.contains("\"key3\": 3.14"),
                "write() expected " + expectedStr +
                " but found " + actualStr
            );
            Util.checkJSONArrayMaps(finalArray);
        } finally {
            stringWriter.close();
        }
        Util.checkJSONArrayMaps(jsonArray);
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
    void toList() {
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

        assertTrue(list != null, "List should not be null");
        assertEquals(3, list.size(), "List should have 3 elements");

        List<?> val1List = (List<?>) list.get(0);
        assertTrue(val1List != null, "val1 should not be null");
        assertEquals(3, val1List.size(), "val1 should have 3 elements");

        assertEquals(val1List.get(0), Integer.valueOf(1), "val1 value 1 should be 1");
        assertEquals(val1List.get(1), Integer.valueOf(2), "val1 value 2 should be 2");

        Map<?,?> key1Value3Map = (Map<?,?>)val1List.get(2);
        assertTrue(key1Value3Map != null, "Map should not be null");
        assertEquals(1, key1Value3Map.size(), "Map should have 1 element");
        assertEquals(Boolean.TRUE, key1Value3Map.get("key3"), "Map key3 should be true");

        Map<?,?> val2Map = (Map<?,?>) list.get(1);
        assertTrue(val2Map != null, "val2 should not be null");
        assertEquals(4, val2Map.size(), "val2 should have 4 elements");
        assertEquals("val1", val2Map.get("key1"), "val2 map key 1 should be val1");
        assertEquals(val2Map.get("key3"), Integer.valueOf(42), "val2 map key 3 should be 42");

        Map<?,?> val2Key2Map = (Map<?,?>)val2Map.get("key2");
        assertTrue(val2Key2Map != null, "val2 map key 2 should not be null");
        assertTrue(val2Key2Map.containsKey("key2"), "val2 map key 2 should have an entry");
        assertTrue(val2Key2Map.get("key2") == null, "val2 map key 2 value should be null");

        List<?> val2Key4List = (List<?>)val2Map.get("key4");
        assertTrue(val2Key4List != null, "val2 map key 4 should not be null");
        assertTrue(val2Key4List.isEmpty(), "val2 map key 4 should be empty");

        List<?> val3List = (List<?>) list.get(2);
        assertTrue(val3List != null, "val3 should not be null");
        assertEquals(2, val3List.size(), "val3 should have 2 elements");

        List<?> val3Val1List = (List<?>)val3List.get(0);
        assertTrue(val3Val1List != null, "val3 list val 1 should not be null");
        assertEquals(2, val3Val1List.size(), "val3 list val 1 should have 2 elements");
        assertEquals("value1", val3Val1List.get(0), "val3 list val 1 list element 1 should be value1");
        assertEquals(val3Val1List.get(1), new BigDecimal("2.1"), "val3 list val 1 list element 2 should be 2.1");

        List<?> val3Val2List = (List<?>)val3List.get(1);
        assertTrue(val3Val2List != null, "val3 list val 2 should not be null");
        assertEquals(1, val3Val2List.size(), "val3 list val 2 should have 1 element");
        assertTrue(val3Val2List.get(0) == null, "val3 list val 2 list element 1 should be null");

        // assert that toList() is a deep copy
        jsonArray.getJSONObject(1).put("key1", "still val1");
        assertEquals("val1", val2Map.get("key1"), "val2 map key 1 should be val1");

        // assert that the new list is mutable
        assertTrue(list.remove(2) != null, "Removing an entry should succeed");
        assertEquals(2, list.size(), "List should have 2 elements");
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Create a JSONArray with specified initial capacity.
     * Expects an exception if the initial capacity is specified as a negative integer 
     */
    @Test
    void jSONArrayInt() {
        assertNotNull(new JSONArray(0));
        assertNotNull(new JSONArray(5));
        // Check Size -> Even though the capacity of the JSONArray can be specified using a positive
        // integer but the length of JSONArray always reflects upon the items added into it.
        // assertEquals(0l, new JSONArray(10).length());
        try {
            assertNotNull(new JSONArray(-1), "Should throw an exception");
        } catch (JSONException e) {
            assertEquals("JSONArray initial capacity cannot be negative.",
                    e.getMessage(),
                    "Expected an exception message");
        }
    }

    /**
     * Verifies that the object constructor can properly handle any supported collection object.
     */
    @Test
    @SuppressWarnings({"unchecked", "boxing"})
    void objectConstructor() {
        // should copy the array
        Object o = new Object[] {2, "test2", true};
        JSONArray a = new JSONArray(o);
        assertNotNull(a, "Should not error");
        assertEquals(3, a.length(), "length");
        
        // should NOT copy the collection
        // this is required for backwards compatibility
        o = new ArrayList<Object>();
        ((Collection<Object>)o).add(1);
        ((Collection<Object>)o).add("test");
        ((Collection<Object>)o).add(false);
        try {
            JSONArray a0 = new JSONArray(o);
            assertNull(a0, "Should error");
        } catch (JSONException ex) {
        }

        // should NOT copy the JSONArray
        // this is required for backwards compatibility
        o = a;
        try {
            JSONArray a1 = new JSONArray(o);
            assertNull(a1, "Should error");
        } catch (JSONException ex) {
        }
        Util.checkJSONArrayMaps(a);
    }

    /**
     * Verifies that the JSONArray constructor properly copies the original.
     */
    @Test
    void jSONArrayConstructor() {
        // should copy the array
        JSONArray a1 = new JSONArray("[2, \"test2\", true]");
        JSONArray a2 = new JSONArray(a1);
        assertNotNull(a2, "Should not error");
        assertEquals(a1.length(), a2.length(), "length");
        
        for(int i = 0; i < a1.length(); i++) {
            assertEquals(a1.get(i), a2.get(i), "index " + i + " are equal");
        }
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                a1, a2
        )));
    }

    /**
     * Verifies that the object constructor can properly handle any supported collection object.
     */
    @Test
    void jSONArrayPutAll() {
        // should copy the array
        JSONArray a1 = new JSONArray("[2, \"test2\", true]");
        JSONArray a2 = new JSONArray();
        a2.putAll(a1);
        assertNotNull(a2, "Should not error");
        assertEquals(a1.length(), a2.length(), "length");
        
        for(int i = 0; i < a1.length(); i++) {
            assertEquals(a1.get(i), a2.get(i), "index " + i + " are equal");
        }
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                a1, a2
        )));
    }

    /**
     * Tests if calling JSONArray clear() method actually makes the JSONArray empty
     */
    @Test
    void jsonArrayClearMethodTest() {
        assertThrows(JSONException.class, () -> {
            //Adds random stuff to the JSONArray
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(123);
            jsonArray.put("456");
            jsonArray.put(new JSONArray());
            jsonArray.clear(); //Clears the JSONArray
            assertEquals(0, jsonArray.length(), "expected jsonArray.length() == 0"); //Check if its length is 0
            jsonArray.getInt(0); //Should throws org.json.JSONException: JSONArray[0] not found
            Util.checkJSONArrayMaps(jsonArray);
        });
    }

    /**
    * Tests for stack overflow. See https://github.com/stleary/JSON-java/issues/654
    */
    @Disabled("This test relies on system constraints and may not always pass. See: https://github.com/stleary/JSON-java/issues/821")
    @Test
    void issue654StackOverflowInputWellFormed() {
        assertThrows(JSONException.class, () -> {
            //String input = new String(java.util.Base64.getDecoder().decode(base64Bytes));
            final InputStream resourceAsStream = JSONArrayTest.class.getClassLoader().getResourceAsStream("Issue654WellFormedArray.json");
            JSONTokener tokener = new JSONTokener(resourceAsStream);
            JSONArray json_input = new JSONArray(tokener);
            assertNotNull(json_input);
            fail("Excepected Exception due to stack overflow.");
            Util.checkJSONArrayMaps(json_input);
        });
    }

    @Test
    void issue682SimilarityOfJSONString() {
        JSONArray ja1 = new JSONArray()
                .put(new MyJsonString())
                .put(2);
        JSONArray ja2 = new JSONArray()
                .put(new MyJsonString())
                .put(2);
        assertTrue(ja1.similar(ja2));

        JSONArray ja3 = new JSONArray()
                .put(new JSONString() {
                    @Override
                    public String toJSONString() {
                        return "\"different value\"";
                    }
                })
                .put(2);
        assertFalse(ja1.similar(ja3));
    }

    @Test
    void recursiveDepth() {
        assertThrows(JSONException.class, () -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("t", map);
            new JSONArray().put(map);
        });
    }

    @Test
    void recursiveDepthAtPosition() {
        assertThrows(JSONException.class, () -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("t", map);
            new JSONArray().put(0, map);
        });
    }

    @Test
    void recursiveDepthArray() {
        assertThrows(JSONException.class, () -> {
            ArrayList<Object> array = new ArrayList<>();
            array.add(array);
            new JSONArray(array);
        });
    }

    @Test
    void recursiveDepthAtPositionDefaultObject() {
        HashMap<String, Object> map = JSONObjectTest.buildNestedMap(ParserConfiguration.DEFAULT_MAXIMUM_NESTING_DEPTH);
        new JSONArray().put(0, map);
    }

    @Test
    void recursiveDepthAtPosition1000Object() {
        HashMap<String, Object> map = JSONObjectTest.buildNestedMap(1000);
        new JSONArray().put(0, map, new JSONParserConfiguration().withMaxNestingDepth(1000));
    }

    @Test
    void recursiveDepthAtPosition1001Object() {
        assertThrows(JSONException.class, () -> {
            HashMap<String, Object> map = JSONObjectTest.buildNestedMap(1001);
            new JSONArray().put(0, map);
        });
    }

    @Test
    void recursiveDepthArrayLimitedMaps() {
        assertThrows(JSONException.class, () -> {
            ArrayList<Object> array = new ArrayList<>();
            array.add(array);
            new JSONArray(array);
        });
    }

    @Test
    void recursiveDepthArrayForDefaultLevels() {
        ArrayList<Object> array = buildNestedArray(ParserConfiguration.DEFAULT_MAXIMUM_NESTING_DEPTH);
        new JSONArray(array, new JSONParserConfiguration());
    }

    @Test
    void recursiveDepthArrayFor1000Levels() {
        ArrayList<Object> array = buildNestedArray(1000);
        JSONParserConfiguration parserConfiguration = new JSONParserConfiguration().withMaxNestingDepth(1000);
        new JSONArray(array, parserConfiguration);
    }

    @Test
    void recursiveDepthArrayFor1001Levels() {
        assertThrows(JSONException.class, () -> {
            ArrayList<Object> array = buildNestedArray(1001);
            new JSONArray(array);
        });
    }

    public static ArrayList<Object> buildNestedArray(int maxDepth) {
        if (maxDepth <= 0) {
            return new ArrayList<>();
        }
        ArrayList<Object> nestedArray = new ArrayList<>();
        nestedArray.add(buildNestedArray(maxDepth - 1));
        return nestedArray;
    }
}
