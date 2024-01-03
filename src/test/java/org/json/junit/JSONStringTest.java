package org.json.junit;

/*
Public Domain.
*/

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringWriter;
import java.util.*;

import org.json.*;
import org.junit.jupiter.api.Test;

/**
 * Tests for JSONString implementations, and the difference between
 * {@link JSONObject#valueToString} and {@link JSONObject#writeValue}.
 */
class JSONStringTest {

    /**
     * This tests the JSONObject.writeValue() method. We can't test directly
     * due to it being a package-protected method. Instead, we can call
     * JSONArray.write(), which delegates the writing of each entry to
     * writeValue().
     */
    @Test
    void writeValues() throws Exception {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put((Object)null);

        StringWriter writer = new StringWriter();
        try {
            String output = jsonArray.write(writer).toString();
            assertEquals("[null]", output, "String values should be equal");
    
            jsonArray = new JSONArray();
            jsonArray.put(JSONObject.NULL);
        } finally {
            writer.close();
        }

        writer = new StringWriter();
        try {
            String output = jsonArray.write(writer).toString();
            assertEquals("[null]", output, "String values should be equal");
    
            jsonArray = new JSONArray();
            jsonArray.put(new JSONObject());
        } finally {
            writer.close();
        }
        
        writer = new StringWriter();
        try  {
            String output = jsonArray.write(writer).toString();
            assertEquals("[{}]", output, "String values should be equal");
    
            jsonArray = new JSONArray();
            jsonArray.put(new JSONArray());
        } finally {
            writer.close();
        }

        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertEquals("[[]]", output, "String values should be equal");
    
            jsonArray = new JSONArray();
            Map<?,?> singleMap = Collections.singletonMap("key1", "value1");
            jsonArray.put((Object)singleMap);
        } finally {
            writer.close();
        }
        
        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertEquals("[{\"key1\":\"value1\"}]", output, "String values should be equal");
    
            jsonArray = new JSONArray();
            List<?> singleList = Collections.singletonList("entry1");
            jsonArray.put((Object)singleList);
        } finally {
            writer.close();
        }

        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertEquals("[[\"entry1\"]]", output, "String values should be equal");
    
            jsonArray = new JSONArray();
            int[] intArray = new int[] { 1, 2, 3 };
            jsonArray.put(intArray);
        } finally {
            writer.close();
        }

        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertEquals("[[1,2,3]]", output, "String values should be equal");
    
            jsonArray = new JSONArray();
            jsonArray.put(24);
        } finally {
            writer.close();
        }
        
        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertEquals("[24]", output, "String values should be equal");
    
            jsonArray = new JSONArray();
            jsonArray.put("string value");
        } finally {
            writer.close();
        }

        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertEquals("[\"string value\"]", output, "String values should be equal");
    
            jsonArray = new JSONArray();
            jsonArray.put(true);
        } finally {
            writer.close();
        }

        writer = new StringWriter();
        try {
            String output = jsonArray.write(writer).toString();
            assertEquals("[true]", output, "String values should be equal");
        } finally {
            writer.close();
        }

    }

    /**
     * This tests the JSONObject.valueToString() method. These should be
     * identical to the values above, except for the enclosing [ and ].
     */
    @SuppressWarnings("boxing")
    @Test
    void valuesToString() throws Exception {

        String output = JSONObject.valueToString(null);
        assertEquals("null", output, "String values should be equal");

        output = JSONObject.valueToString(JSONObject.NULL);
        assertEquals("null", output, "String values should be equal");

        output = JSONObject.valueToString(new JSONObject());
        assertEquals("{}", output, "String values should be equal");

        output = JSONObject.valueToString(new JSONArray());
        assertEquals("[]", output, "String values should be equal");

        Map<?,?> singleMap = Collections.singletonMap("key1", "value1");
        output = JSONObject.valueToString(singleMap);
        assertEquals("{\"key1\":\"value1\"}", output, "String values should be equal");

        List<?> singleList = Collections.singletonList("entry1");
        output = JSONObject.valueToString(singleList);
        assertEquals("[\"entry1\"]", output, "String values should be equal");

        int[] intArray = new int[] { 1, 2, 3 };
        output = JSONObject.valueToString(intArray);
        assertEquals("[1,2,3]", output, "String values should be equal");

        output = JSONObject.valueToString(24);
        assertEquals("24", output, "String values should be equal");

        output = JSONObject.valueToString("string value");
        assertEquals("\"string value\"", output, "String values should be equal");

        output = JSONObject.valueToString(true);
        assertEquals("true", output, "String values should be equal");

    }

    /**
     * Test what happens when toJSONString() returns a well-formed JSON value.
     * This is the usual case.
     */
    @Test
    void jSONStringValue() throws Exception {
        JSONStringValue jsonString = new JSONStringValue();
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(jsonString);

        StringWriter writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertEquals("[\"the JSON string value\"]", output, "String values should be equal");
    
            output = JSONObject.valueToString(jsonString);
            assertEquals("\"the JSON string value\"", output, "String values should be equal");
        } finally {
            writer.close();
        }
    }

    /**
     * Test what happens when toJSONString() returns null. In one case,
     * use the object's toString() method. In the other, throw a JSONException.
     */
    @Test
    void jSONNullStringValue() throws Exception {
        JSONNullStringValue jsonString = new JSONNullStringValue();
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(jsonString);

        StringWriter writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertEquals("[\"the toString value\"]", output, "String values should be equal");
    
            // The only different between writeValue() and valueToString():
            // in this case, valueToString throws a JSONException
            try {
                output = JSONObject.valueToString(jsonString);
                fail("Expected an exception, got a String value");
            } catch (Exception e) {
                assertTrue(e instanceof JSONException, "Expected JSONException");
                assertEquals("Bad value from toJSONString: null", e.getMessage(), "Exception message does not match");
            }
        } finally {
            writer.close();
        }
    }

    /**
     * Test what happens when toJSONString() returns an exception. In both
     * cases, a JSONException is thrown, with the cause and message set from
     * the original exception.
     */
    @Test
    void jSONStringExceptionValue() {
        JSONStringExceptionValue jsonString = new JSONStringExceptionValue();
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(jsonString);

        StringWriter writer = new StringWriter(); 
        try {
            jsonArray.write(writer).toString();
            fail("Expected an exception, got a String value");
        } catch (JSONException e) {
            assertEquals("Unable to write JSONArray value at index: 0", e.getMessage());
        } catch(Exception e) {
            fail("Expected JSONException");
        } finally {
            try {
                writer.close();
            } catch (Exception e){}
        }

        try {
            JSONObject.valueToString(jsonString);
            fail("Expected an exception, got a String value");
        } catch (JSONException e) {
            assertEquals("the exception value", e.getMessage(), "Exception message does not match");
        } catch(Exception e) {
            fail("Expected JSONException");
        }
    }

    /**
     * Test what happens when a Java object's toString() returns a String value.
     * This is the usual case.
     */
    @Test
    void stringValue() throws Exception {
        StringValue nonJsonString = new StringValue();
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(nonJsonString);

        StringWriter writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertEquals("[\"the toString value for StringValue\"]", output, "String values should be equal");
    
            output = JSONObject.valueToString(nonJsonString);
            assertEquals("\"the toString value for StringValue\"", output, "String values should be equal");
        } finally {
            writer.close();
        }
    }

    /**
     * Test what happens when a Java object's toString() returns null.
     * Defaults to empty string.
     */
    @Test
    void nullStringValue() throws Exception {
        NullStringValue nonJsonString = new NullStringValue();
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(nonJsonString);

        StringWriter writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertEquals("[\"\"]", output, "String values should be equal");
    
            output = JSONObject.valueToString(nonJsonString);
            assertEquals("\"\"", output, "String values should be equal");
        } finally {
            writer.close();
        }
    }

    /**
     * A JSONString that returns a valid JSON string value.
     */
    private static final class JSONStringValue implements JSONString {

        @Override
        public String toJSONString() {
            return "\"the JSON string value\"";
        }

        @Override
        public String toString() {
            return "the toString value for JSONStringValue";
        }
    }

    /**
     * A JSONString that returns null when calling toJSONString().
     */
    private static final class JSONNullStringValue implements JSONString {

        @Override
        public String toJSONString() {
            return null;
        }

        @Override
        public String toString() {
            return "the toString value";
        }
    }

    /**
     * A JSONString that throw an exception when calling toJSONString().
     */
    private static final class JSONStringExceptionValue implements JSONString {

        @Override
        public String toJSONString() {
            throw new IllegalStateException("the exception value");
        }

        @Override
        public String toString() {
            return "the toString value for JSONStringExceptionValue";
        }
    }

    public static final class StringValue {

        @Override
        public String toString() {
            return "the toString value for StringValue";
        }
    }

    public static final class NullStringValue {

        @Override
        public String toString() {
            return null;
        }
    }
}
