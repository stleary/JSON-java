package org.json.junit;

/*
Public Domain.
*/

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import org.junit.Ignore;
import org.junit.Test;

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
    public void verifySimilar() {
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
        
        assertFalse("obj1-obj2 Should eval to false", obj1.similar(obj2));
        assertTrue("obj1-obj3 Should eval to true", obj1.similar(obj3));
        assertFalse("obj4-obj5 Should eval to false", obj4.similar(obj5));
    }
        
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
    public void emptyStr() {
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
     * Attempt to create a JSONArray with an unclosed array.
     * Expects an exception
     */
    @Test
    public void unclosedArray2() {
        try {
            assertNull("Should throw an exception", new JSONArray("[\"test\""));
        } catch (JSONException e) {
            assertEquals("Expected an exception message", 
                    "Expected a ',' or ']' at 7 [character 8 line 1]",
                    e.getMessage());
        }
    }
    
    /**
     * Attempt to create a JSONArray with an unclosed array.
     * Expects an exception
     */
    @Test
    public void unclosedArray3() {
        try {
            assertNull("Should throw an exception", new JSONArray("[\"test\","));
        } catch (JSONException e) {
            assertEquals("Expected an exception message", 
                    "Expected a ',' or ']' at 8 [character 9 line 1]",
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
        Util.checkJSONArrayMaps(expected);
        Util.checkJSONArrayMaps(jaObj);
        Util.checkJSONArrayMaps(jaRaw);
        Util.checkJSONArrayMaps(jaInt);
    }

    /**
     * Tests consecutive calls to putAll with array and collection.
     */
    @Test
    public void verifyPutAll() {
        final JSONArray jsonArray = new JSONArray();

        // array
        int[] myInts = { 1, 2, 3, 4, 5 };
        jsonArray.putAll(myInts);

        assertEquals("int arrays lengths should be equal",
                     jsonArray.length(),
                     myInts.length);

        for (int i = 0; i < myInts.length; i++) {
            assertEquals("int arrays elements should be equal",
                         myInts[i],
                         jsonArray.getInt(i));
        }

        // collection
        List<String> myList = Arrays.asList("one", "two", "three", "four", "five");
        jsonArray.putAll(myList);

        int len = myInts.length + myList.size();

        assertEquals("arrays lengths should be equal",
                     jsonArray.length(),
                     len);

        for (int i = 0; i < myList.size(); i++) {
            assertEquals("collection elements should be equal",
                         myList.get(i),
                         jsonArray.getString(myInts.length + i));
        }
        Util.checkJSONArrayMaps(jsonArray);
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
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                jaRaw, jaObj, jaInt
        )));
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
                Double.valueOf(23.45e-4).equals(jsonArray.getDouble(5)));
        assertTrue("Array string double",
                Double.valueOf(23.45).equals(jsonArray.getDouble(6)));
        assertTrue("Array double can be float",
                Float.valueOf(23.45e-4f).equals(jsonArray.getFloat(5)));
        // ints
        assertTrue("Array value int",
                Integer.valueOf(42).equals(jsonArray.getInt(7)));
        assertTrue("Array value string int",
                Integer.valueOf(43).equals(jsonArray.getInt(8)));
        // nested objects
        JSONArray nestedJsonArray = jsonArray.getJSONArray(9);
        assertTrue("Array value JSONArray", nestedJsonArray != null);
        JSONObject nestedJsonObject = jsonArray.getJSONObject(10);
        assertTrue("Array value JSONObject", nestedJsonObject != null);
        // longs
        assertTrue("Array value long",
                Long.valueOf(0).equals(jsonArray.getLong(11)));
        assertTrue("Array value string long",
                Long.valueOf(-1).equals(jsonArray.getLong(12)));

        assertTrue("Array value null", jsonArray.isNull(-1));
        Util.checkJSONArrayMaps(jsonArray);
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
            assertEquals("Expected an exception message",
                    "JSONArray[4] is not a boolean (class java.lang.String : hello).",e.getMessage());
        }
        try {
            jsonArray.get(-1);
            assertTrue("expected get to fail", false);
        } catch (JSONException e) {
            assertEquals("Expected an exception message",
                    "JSONArray[-1] not found.",e.getMessage());
        }
        try {
            jsonArray.getDouble(4);
            assertTrue("expected getDouble to fail", false);
        } catch (JSONException e) {
            assertEquals("Expected an exception message",
                    "JSONArray[4] is not a double (class java.lang.String : hello).",e.getMessage());
        }
        try {
            jsonArray.getInt(4);
            assertTrue("expected getInt to fail", false);
        } catch (JSONException e) {
            assertEquals("Expected an exception message",
                    "JSONArray[4] is not a int (class java.lang.String : hello).",e.getMessage());
        }
        try {
            jsonArray.getJSONArray(4);
            assertTrue("expected getJSONArray to fail", false);
        } catch (JSONException e) {
            assertEquals("Expected an exception message",
                    "JSONArray[4] is not a JSONArray (class java.lang.String : hello).",e.getMessage());
        }
        try {
            jsonArray.getJSONObject(4);
            assertTrue("expected getJSONObject to fail", false);
        } catch (JSONException e) {
            assertEquals("Expected an exception message",
                    "JSONArray[4] is not a JSONObject (class java.lang.String : hello).",e.getMessage());
        }
        try {
            jsonArray.getLong(4);
            assertTrue("expected getLong to fail", false);
        } catch (JSONException e) {
            assertEquals("Expected an exception message",
                    "JSONArray[4] is not a long (class java.lang.String : hello).",e.getMessage());
        }
        try {
            jsonArray.getString(5);
            assertTrue("expected getString to fail", false);
        } catch (JSONException e) {
            assertEquals("Expected an exception message",
                    "JSONArray[5] is not a String (class java.math.BigDecimal : 0.002345).",e.getMessage());
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
    public void unquotedText() {
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
        assertTrue("expected 0.002345", BigDecimal.valueOf(0.002345).equals(jsonArray.query("/5")));
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
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Confirm the JSONArray.length() method
     */
    @Test 
    public void length() {
        assertTrue("expected empty JSONArray length 0",
                new JSONArray().length() == 0);
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        assertTrue("expected JSONArray length 13. instead found "+jsonArray.length(), jsonArray.length() == 13);
        JSONArray nestedJsonArray = jsonArray.getJSONArray(9);
        assertTrue("expected JSONArray length 1", nestedJsonArray.length() == 1);
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

         assertTrue("Array opt boolean object",
                Boolean.TRUE.equals(jsonArray.optBooleanObject(0)));
        assertTrue("Array opt boolean object default",
                Boolean.FALSE.equals(jsonArray.optBooleanObject(-1, Boolean.FALSE)));
        assertTrue("Array opt boolean object implicit default",
                Boolean.FALSE.equals(jsonArray.optBooleanObject(-1)));

        assertTrue("Array opt double",
                Double.valueOf(23.45e-4).equals(jsonArray.optDouble(5)));
        assertTrue("Array opt double default",
                Double.valueOf(1).equals(jsonArray.optDouble(0, 1)));
        assertTrue("Array opt double default implicit",
           Double.valueOf(jsonArray.optDouble(99)).isNaN());

        assertTrue("Array opt double object",
                Double.valueOf(23.45e-4).equals(jsonArray.optDoubleObject(5)));
        assertTrue("Array opt double object default",
                Double.valueOf(1).equals(jsonArray.optDoubleObject(0, 1D)));
        assertTrue("Array opt double object default implicit",
                jsonArray.optDoubleObject(99).isNaN());

        assertTrue("Array opt float",
                Float.valueOf(Double.valueOf(23.45e-4).floatValue()).equals(jsonArray.optFloat(5)));
        assertTrue("Array opt float default",
                Float.valueOf(1).equals(jsonArray.optFloat(0, 1)));
        assertTrue("Array opt float default implicit",
           Float.valueOf(jsonArray.optFloat(99)).isNaN());

        assertTrue("Array opt float object",
                Float.valueOf(23.45e-4F).equals(jsonArray.optFloatObject(5)));
        assertTrue("Array opt float object default",
                Float.valueOf(1).equals(jsonArray.optFloatObject(0, 1F)));
        assertTrue("Array opt float object default implicit",
                jsonArray.optFloatObject(99).isNaN());

        assertTrue("Array opt Number",
                BigDecimal.valueOf(23.45e-4).equals(jsonArray.optNumber(5)));
        assertTrue("Array opt Number default",
                Double.valueOf(1).equals(jsonArray.optNumber(0, 1d)));
        assertTrue("Array opt Number default implicit",
           Double.valueOf(jsonArray.optNumber(99,Double.NaN).doubleValue()).isNaN());

        assertTrue("Array opt int",
                Integer.valueOf(42).equals(jsonArray.optInt(7)));
        assertTrue("Array opt int default",
                Integer.valueOf(-1).equals(jsonArray.optInt(0, -1)));
        assertTrue("Array opt int default implicit",
                0 == jsonArray.optInt(0));

        assertTrue("Array opt int object",
                Integer.valueOf(42).equals(jsonArray.optIntegerObject(7)));
        assertTrue("Array opt int object default",
                Integer.valueOf(-1).equals(jsonArray.optIntegerObject(0, -1)));
        assertTrue("Array opt int object default implicit",
                Integer.valueOf(0).equals(jsonArray.optIntegerObject(0)));

        JSONArray nestedJsonArray = jsonArray.optJSONArray(9);
        assertTrue("Array opt JSONArray", nestedJsonArray != null);
        assertTrue("Array opt JSONArray null",
                null == jsonArray.optJSONArray(99));
        assertTrue("Array opt JSONArray default",
                "value".equals(jsonArray.optJSONArray(99, new JSONArray("[\"value\"]")).getString(0)));

        JSONObject nestedJsonObject = jsonArray.optJSONObject(10);
        assertTrue("Array opt JSONObject", nestedJsonObject != null);
        assertTrue("Array opt JSONObject null",
                null == jsonArray.optJSONObject(99));
        assertTrue("Array opt JSONObject default",
                "value".equals(jsonArray.optJSONObject(99, new JSONObject("{\"key\":\"value\"}")).getString("key")));

        assertTrue("Array opt long",
                0 == jsonArray.optLong(11));
        assertTrue("Array opt long default",
                -2 == jsonArray.optLong(-1, -2));
        assertTrue("Array opt long default implicit",
                0 == jsonArray.optLong(-1));

        assertTrue("Array opt long object",
                Long.valueOf(0).equals(jsonArray.optLongObject(11)));
        assertTrue("Array opt long object default",
                Long.valueOf(-2).equals(jsonArray.optLongObject(-1, -2L)));
        assertTrue("Array opt long object default implicit",
                Long.valueOf(0).equals(jsonArray.optLongObject(-1)));

        assertTrue("Array opt string",
                "hello".equals(jsonArray.optString(4)));
        assertTrue("Array opt string default implicit",
                "".equals(jsonArray.optString(-1)));
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                jsonArray, nestedJsonArray
        )));
        Util.checkJSONObjectMaps(nestedJsonObject);
    }
    
    /**
     * Verifies that the opt methods properly convert string values.
     */
    @Test
    public void optStringConversion(){
        JSONArray ja = new JSONArray("[\"123\",\"true\",\"false\"]");
        assertTrue("unexpected optBoolean value",ja.optBoolean(1,false)==true);
        assertTrue("unexpected optBooleanObject value",Boolean.valueOf(true).equals(ja.optBooleanObject(1,false)));
        assertTrue("unexpected optBoolean value",ja.optBoolean(2,true)==false);
        assertTrue("unexpected optBooleanObject value",Boolean.valueOf(false).equals(ja.optBooleanObject(2,true)));
        assertTrue("unexpected optInt value",ja.optInt(0,0)==123);
        assertTrue("unexpected optIntegerObject value",Integer.valueOf(123).equals(ja.optIntegerObject(0,0)));
        assertTrue("unexpected optLong value",ja.optLong(0,0)==123);
        assertTrue("unexpected optLongObject value",Long.valueOf(123).equals(ja.optLongObject(0,0L)));
        assertTrue("unexpected optDouble value",ja.optDouble(0,0.0)==123.0);
        assertTrue("unexpected optDoubleObject value",Double.valueOf(123.0).equals(ja.optDoubleObject(0,0.0)));
        assertTrue("unexpected optBigInteger value",ja.optBigInteger(0,BigInteger.ZERO).compareTo(new BigInteger("123"))==0);
        assertTrue("unexpected optBigDecimal value",ja.optBigDecimal(0,BigDecimal.ZERO).compareTo(new BigDecimal("123"))==0);
        Util.checkJSONArrayMaps(ja);
    }

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
        Util.checkJSONArrayMaps(jsonArray);
        Util.checkJSONObjectMaps(jsonObject);
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
        Util.checkJSONObjectMaps(jsonObject);
        Util.checkJSONArrayMaps(jsonArray);
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
        assertTrue("jsonArray should be empty", jsonArray.isEmpty());
        Util.checkJSONArrayMaps(jsonArray);
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
        assertEquals("Expected 1 line", 1, actualStrArray.length);
        actualStrArray = jsonArray.toString(0).split("\\r?\\n");
        assertEquals("Expected 1 line", 1, actualStrArray.length);

        actualStrArray = jsonArray.toString(1).split("\\r?\\n");
        assertEquals("Expected lines", jsonArray1Strs.length, actualStrArray.length);
        List<String> list = Arrays.asList(actualStrArray);
        for (String s : jsonArray1Strs) {
            list.contains(s);
        }
        
        actualStrArray = jsonArray.toString(4).split("\\r?\\n");
        assertEquals("Expected lines", jsonArray1Strs.length, actualStrArray.length);
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
    public void toJSONObject() {
        JSONArray names = new JSONArray();
        JSONArray jsonArray = new JSONArray();
        assertTrue("toJSONObject should return null",
                null == jsonArray.toJSONObject(names));
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                names, jsonArray
        )));
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
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Exercise the JSONArray iterator.
     */
    @SuppressWarnings("boxing")
    @Test
    public void iteratorTest() {
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

        assertTrue("Array double [23.45e-4]",
                new BigDecimal("0.002345").equals(it.next()));
        assertTrue("Array string double",
                Double.valueOf(23.45).equals(Double.parseDouble((String)it.next())));

        assertTrue("Array value int",
                Integer.valueOf(42).equals(it.next()));
        assertTrue("Array value string int",
                Integer.valueOf(43).equals(Integer.parseInt((String)it.next())));

        JSONArray nestedJsonArray = (JSONArray)it.next();
        assertTrue("Array value JSONArray", nestedJsonArray != null);

        JSONObject nestedJsonObject = (JSONObject)it.next();
        assertTrue("Array value JSONObject", nestedJsonObject != null);

        assertTrue("Array value long",
                Long.valueOf(0).equals(((Number) it.next()).longValue()));
        assertTrue("Array value string long",
                Long.valueOf(-1).equals(Long.parseLong((String) it.next())));
        assertTrue("should be at end of array", !it.hasNext());
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                jsonArray, nestedJsonArray
        )));
        Util.checkJSONObjectMaps(nestedJsonObject);
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
            JSONArray finalArray = new JSONArray(actualStr);
            Util.compareActualVsExpectedJsonArrays(jsonArray, finalArray);
            assertTrue("write() expected " + expectedStr +
                    " but found " + actualStr,
                    actualStr.startsWith("[\"value1\",\"value2\",{")
                    && actualStr.contains("\"key1\":1")
                    && actualStr.contains("\"key2\":2")
                    && actualStr.contains("\"key3\":3")
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
    public void write3Param() throws IOException {
        String str0 = "[\"value1\",\"value2\",{\"key1\":1,\"key2\":false,\"key3\":3.14}]";
        JSONArray jsonArray = new JSONArray(str0);
        String expectedStr = str0;
        StringWriter stringWriter = new StringWriter();
        try {
            String actualStr = jsonArray.write(stringWriter, 0, 0).toString();
            JSONArray finalArray = new JSONArray(actualStr);
            Util.compareActualVsExpectedJsonArrays(jsonArray, finalArray);
            assertTrue("write() expected " + expectedStr +
                " but found " + actualStr,
                actualStr.startsWith("[\"value1\",\"value2\",{")
                && actualStr.contains("\"key1\":1")
                && actualStr.contains("\"key2\":false")
                && actualStr.contains("\"key3\":3.14")
            );
        } finally {
            stringWriter.close();
        }
        
        stringWriter = new StringWriter();
        try {
            String actualStr = jsonArray.write(stringWriter, 2, 1).toString();
            JSONArray finalArray = new JSONArray(actualStr);
            Util.compareActualVsExpectedJsonArrays(jsonArray, finalArray);
            assertTrue("write() expected " + expectedStr +
                " but found " + actualStr,
                actualStr.startsWith("[\n" + 
                        "   \"value1\",\n" + 
                        "   \"value2\",\n" + 
                        "   {")
                && actualStr.contains("\"key1\": 1")
                && actualStr.contains("\"key2\": false")
                && actualStr.contains("\"key3\": 3.14")
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
        assertTrue("val3 list val 1 list element 2 should be 2.1", val3Val1List.get(1).equals(new BigDecimal("2.1")));

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
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Create a JSONArray with specified initial capacity.
     * Expects an exception if the initial capacity is specified as a negative integer 
     */
    @Test
    public void testJSONArrayInt() {
        assertNotNull(new JSONArray(0));
        assertNotNull(new JSONArray(5));
        // Check Size -> Even though the capacity of the JSONArray can be specified using a positive
        // integer but the length of JSONArray always reflects upon the items added into it.
        // assertEquals(0l, new JSONArray(10).length());
        try {
            assertNotNull("Should throw an exception", new JSONArray(-1));
        } catch (JSONException e) {
            assertEquals("Expected an exception message", 
                    "JSONArray initial capacity cannot be negative.",
                    e.getMessage());
        }
    }
    
    /**
     * Verifies that the object constructor can properly handle any supported collection object.
     */
    @Test
    @SuppressWarnings({ "unchecked", "boxing" })
    public void testObjectConstructor() {
        // should copy the array
        Object o = new Object[] {2, "test2", true};
        JSONArray a = new JSONArray(o);
        assertNotNull("Should not error", a);
        assertEquals("length", 3, a.length());
        
        // should NOT copy the collection
        // this is required for backwards compatibility
        o = new ArrayList<Object>();
        ((Collection<Object>)o).add(1);
        ((Collection<Object>)o).add("test");
        ((Collection<Object>)o).add(false);
        try {
            JSONArray a0 = new JSONArray(o);
            assertNull("Should error", a0);
        } catch (JSONException ex) {
        }

        // should NOT copy the JSONArray
        // this is required for backwards compatibility
        o = a;
        try {
            JSONArray a1 = new JSONArray(o);
            assertNull("Should error", a1);
        } catch (JSONException ex) {
        }
        Util.checkJSONArrayMaps(a);
    }
    
    /**
     * Verifies that the JSONArray constructor properly copies the original.
     */
    @Test
    public void testJSONArrayConstructor() {
        // should copy the array
        JSONArray a1 = new JSONArray("[2, \"test2\", true]");
        JSONArray a2 = new JSONArray(a1);
        assertNotNull("Should not error", a2);
        assertEquals("length", a1.length(), a2.length());
        
        for(int i = 0; i < a1.length(); i++) {
            assertEquals("index " + i + " are equal", a1.get(i), a2.get(i));
        }
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                a1, a2
        )));
    }
    
    /**
     * Verifies that the object constructor can properly handle any supported collection object.
     */
    @Test
    public void testJSONArrayPutAll() {
        // should copy the array
        JSONArray a1 = new JSONArray("[2, \"test2\", true]");
        JSONArray a2 = new JSONArray();
        a2.putAll(a1);
        assertNotNull("Should not error", a2);
        assertEquals("length", a1.length(), a2.length());
        
        for(int i = 0; i < a1.length(); i++) {
            assertEquals("index " + i + " are equal", a1.get(i), a2.get(i));
        }
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                a1, a2
        )));
    }

    /**
	 * Tests if calling JSONArray clear() method actually makes the JSONArray empty
	 */
	@Test(expected = JSONException.class)
	public void jsonArrayClearMethodTest() {
		//Adds random stuff to the JSONArray
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(123);
		jsonArray.put("456");
		jsonArray.put(new JSONArray());
		jsonArray.clear(); //Clears the JSONArray
		assertTrue("expected jsonArray.length() == 0", jsonArray.length() == 0); //Check if its length is 0
		jsonArray.getInt(0); //Should throws org.json.JSONException: JSONArray[0] not found
        Util.checkJSONArrayMaps(jsonArray);
	}

    /**
    * Tests for stack overflow. See https://github.com/stleary/JSON-java/issues/654
    */
    @Ignore("This test relies on system constraints and may not always pass. See: https://github.com/stleary/JSON-java/issues/821")
    @Test(expected = JSONException.class)
    public void issue654StackOverflowInputWellFormed() {
        //String input = new String(java.util.Base64.getDecoder().decode(base64Bytes));
        final InputStream resourceAsStream = JSONArrayTest.class.getClassLoader().getResourceAsStream("Issue654WellFormedArray.json");
        JSONTokener tokener = new JSONTokener(resourceAsStream);
        JSONArray json_input = new JSONArray(tokener);
        assertNotNull(json_input);
        fail("Excepected Exception due to stack overflow.");
        Util.checkJSONArrayMaps(json_input);
    }

    @Test
    public void testIssue682SimilarityOfJSONString() {
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

    @Test(expected = JSONException.class)
    public void testRecursiveDepth() {
      HashMap<String, Object> map = new HashMap<>();
      map.put("t", map);
      new JSONArray().put(map);
    }

    @Test(expected = JSONException.class)
    public void testRecursiveDepthAtPosition() {
      HashMap<String, Object> map = new HashMap<>();
      map.put("t", map);
      new JSONArray().put(0, map);
    }

    @Test(expected = JSONException.class)
    public void testRecursiveDepthArray() {
      ArrayList<Object> array = new ArrayList<>();
      array.add(array);
      new JSONArray(array);
    }

    @Test
    public void testRecursiveDepthAtPositionDefaultObject() {
        HashMap<String, Object> map = JSONObjectTest.buildNestedMap(ParserConfiguration.DEFAULT_MAXIMUM_NESTING_DEPTH);
        new JSONArray().put(0, map);
    }

    @Test
    public void testRecursiveDepthAtPosition1000Object() {
        HashMap<String, Object> map = JSONObjectTest.buildNestedMap(1000);
        new JSONArray().put(0, map, new JSONParserConfiguration().withMaxNestingDepth(1000));
    }

    @Test(expected = JSONException.class)
    public void testRecursiveDepthAtPosition1001Object() {
        HashMap<String, Object> map = JSONObjectTest.buildNestedMap(1001);
        new JSONArray().put(0, map);
    }

    @Test(expected = JSONException.class)
    public void testRecursiveDepthArrayLimitedMaps() {
        ArrayList<Object> array = new ArrayList<>();
        array.add(array);
        new JSONArray(array);
    }

    @Test
    public void testRecursiveDepthArrayForDefaultLevels() {
        ArrayList<Object> array = buildNestedArray(ParserConfiguration.DEFAULT_MAXIMUM_NESTING_DEPTH);
        new JSONArray(array, new JSONParserConfiguration());
    }

    @Test
    public void testRecursiveDepthArrayFor1000Levels() {
        ArrayList<Object> array = buildNestedArray(1000);
        JSONParserConfiguration parserConfiguration = new JSONParserConfiguration().withMaxNestingDepth(1000);
        new JSONArray(array, parserConfiguration);
    }

    @Test(expected = JSONException.class)
    public void testRecursiveDepthArrayFor1001Levels() {
        ArrayList<Object> array = buildNestedArray(1001);
        new JSONArray(array);
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
