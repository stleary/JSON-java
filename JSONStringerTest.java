package org.json.junit;

import static org.junit.Assert.*;

import java.util.*;

import org.json.*;
import org.junit.Test;

import com.jayway.jsonpath.*;


/**
 * Tests for JSON-Java JSONStringer and JSONWriter.
 */
public class JSONStringerTest {

    /**
     * Object with a null key.
     * Expects a JSONException.
     */
    @Test
    public void nullKeyException() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object();
        try {
            jsonStringer.key(null);
            assertTrue("Expected an exception", false);
        } catch (JSONException e) {
            assertTrue("Expected an exception message", 
                    "Null key.".
                    equals(e.getMessage()));
        }
    }

    /**
     * Add a key with no object.
     * Expects a JSONException.
     */
    @Test
    public void outOfSequenceException() {
        JSONStringer jsonStringer = new JSONStringer();
        try {
            jsonStringer.key("hi");
            assertTrue("Expected an exception", false);
        } catch (JSONException e) {
            assertTrue("Expected an exception message", 
                    "Misplaced key.".
                    equals(e.getMessage()));
        }
    }

    /**
     * Missplace an array.
     * Expects a JSONException
     */
    @Test
    public void missplacedArrayException() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object().endObject();
        try {
            jsonStringer.array();
        } catch (JSONException e) {
            assertTrue("Expected an exception message", 
                    "Misplaced array.".
                    equals(e.getMessage()));
        }
    }

    /**
     * Missplace an endErray.
     * Expects a JSONException
     */
    @Test
    public void missplacedEndArrayException() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object();
        try {
            jsonStringer.endArray();
        } catch (JSONException e) {
            assertTrue("Expected an exception message", 
                    "Misplaced endArray.".
                    equals(e.getMessage()));
        }
    }

    /**
     * Missplace an endObject.
     * Expects a JSONException
     */
    @Test
    public void missplacedEndObjectException() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.array();
        try {
            jsonStringer.endObject();
        } catch (JSONException e) {
            assertTrue("Expected an exception message", 
                    "Misplaced endObject.".
                    equals(e.getMessage()));
        }
    }

    /**
     * Missplace an object.
     * Expects a JSONException.
     */
    @Test
    public void missplacedObjectException() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object().endObject();
        try {
            jsonStringer.object();
        } catch (JSONException e) {
            assertTrue("Expected an exception message", 
                    "Misplaced object.".
                    equals(e.getMessage()));
        }
    }

    /**
     * Exceeds implementation max nesting depth.
     * Expects a JSONException
     */
    @Test
    public void exceedNestDepthException() {
        try {
            new JSONStringer().object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
            key("k").object().key("k").object().key("k").object().key("k").object().key("k").object();
        } catch (JSONException e) {
            assertTrue("Expected an exception message", 
                    "".
                    equals(e.getMessage()));
        }
    }

    /**
     * Build a JSON doc using JSONString API calls,
     * then convert to JSONObject
     */
    @Test
    public void simpleObjectString() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object();
        jsonStringer.key("trueValue").value(true);
        jsonStringer.key("falseValue").value(false);
        jsonStringer.key("nullValue").value(null);
        jsonStringer.key("stringValue").value("hello world!");
        jsonStringer.key("complexStringValue").value("h\be\tllo w\u1234orld!");
        jsonStringer.key("intValue").value(42);
        jsonStringer.key("doubleValue").value(-23.45e67);
        jsonStringer.endObject();
        String str = jsonStringer.toString();
        JSONObject jsonObject = new JSONObject(str);

        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 7 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 7);
        assertTrue("expected true", Boolean.TRUE.equals(JsonPath.read(doc, "$.trueValue")));
        assertTrue("expected false", Boolean.FALSE.equals(JsonPath.read(doc, "$.falseValue")));
        assertTrue("expected null", null == JsonPath.read(doc, "$.nullValue"));
        assertTrue("expected hello world!", "hello world!".equals(JsonPath.read(doc, "$.stringValue")));
        assertTrue("expected h\be\tllo w\u1234orld!", "h\be\tllo w\u1234orld!".equals(JsonPath.read(doc, "$.complexStringValue")));
        assertTrue("expected 42", Integer.valueOf(42).equals(JsonPath.read(doc, "$.intValue")));
        assertTrue("expected -23.45e67", Double.valueOf(-23.45e67).equals(JsonPath.read(doc, "$.doubleValue")));
    }

    /**
     * Build a JSON doc using JSONString API calls,
     * then convert to JSONArray
     */
    @Test
    public void simpleArrayString() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.array();
        jsonStringer.value(true);
        jsonStringer.value(false);
        jsonStringer.value(null);
        jsonStringer.value("hello world!");
        jsonStringer.value(42);
        jsonStringer.value(-23.45e67);
        jsonStringer.endArray();
        String str = jsonStringer.toString();
        JSONArray jsonArray = new JSONArray(str);

        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertTrue("expected 6 top level items", ((List<?>)(JsonPath.read(doc, "$"))).size() == 6);
        assertTrue("expected true", Boolean.TRUE.equals(JsonPath.read(doc, "$[0]")));
        assertTrue("expected false", Boolean.FALSE.equals(JsonPath.read(doc, "$[1]")));
        assertTrue("expected null", null == JsonPath.read(doc, "$[2]"));
        assertTrue("expected hello world!", "hello world!".equals(JsonPath.read(doc, "$[3]")));
        assertTrue("expected 42", Integer.valueOf(42).equals(JsonPath.read(doc, "$[4]")));
        assertTrue("expected -23.45e67", Double.valueOf(-23.45e67).equals(JsonPath.read(doc, "$[5]")));
    }

    /**
     * Build a nested JSON doc using JSONString API calls, then convert to
     * JSONObject. Will create a long cascade of output by reusing the
     * returned values..
     */
    @Test
    public void complexObjectString() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object().
            key("trueValue").value(true).
            key("falseValue").value(false).
            key("nullValue").value(null).
            key("stringValue").value("hello world!").
            key("object2").object().
            key("k1").value("v1").
            key("k2").value("v2").
            key("k3").value("v3").
            key("array1").array().
            value(1).
            value(2).
            object().
            key("k4").value("v4").
            key("k5").value("v5").
            key("k6").value("v6").
            key("array2").array().
            value(5).
            value(6).
            value(7).
            value(8).
            endArray().
            endObject().
            value(3).
            value(4).
            endArray().
            endObject().
            key("complexStringValue").value("h\be\tllo w\u1234orld!").
            key("intValue").value(42).
            key("doubleValue").value(-23.45e67).
            endObject();
        String str = jsonStringer.toString();
        JSONObject jsonObject = new JSONObject(str);

        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 8 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 8);
        assertTrue("expected 4 object2 items", ((Map<?,?>)(JsonPath.read(doc, "$.object2"))).size() == 4);
        assertTrue("expected 5 array1 items", ((List<?>)(JsonPath.read(doc, "$.object2.array1"))).size() == 5);
        assertTrue("expected 4 array[2] items", ((Map<?,?>)(JsonPath.read(doc, "$.object2.array1[2]"))).size() == 4);
        assertTrue("expected 4 array1[2].array2 items", ((List<?>)(JsonPath.read(doc, "$.object2.array1[2].array2"))).size() == 4);
        assertTrue("expected true", Boolean.TRUE.equals(JsonPath.read(doc, "$.trueValue")));
        assertTrue("expected false", Boolean.FALSE.equals(JsonPath.read(doc, "$.falseValue")));
        assertTrue("expected null", null == JsonPath.read(doc, "$.nullValue"));
        assertTrue("expected hello world!", "hello world!".equals(JsonPath.read(doc, "$.stringValue")));
        assertTrue("expected 42", Integer.valueOf(42).equals(JsonPath.read(doc, "$.intValue")));
        assertTrue("expected -23.45e67", Double.valueOf(-23.45e67).equals(JsonPath.read(doc, "$.doubleValue")));
        assertTrue("expected h\be\tllo w\u1234orld!", "h\be\tllo w\u1234orld!".equals(JsonPath.read(doc, "$.complexStringValue")));
        assertTrue("expected v1", "v1".equals(JsonPath.read(doc, "$.object2.k1")));
        assertTrue("expected v2", "v2".equals(JsonPath.read(doc, "$.object2.k2")));
        assertTrue("expected v3", "v3".equals(JsonPath.read(doc, "$.object2.k3")));
        assertTrue("expected 1", Integer.valueOf(1).equals(JsonPath.read(doc, "$.object2.array1[0]")));
        assertTrue("expected 2", Integer.valueOf(2).equals(JsonPath.read(doc, "$.object2.array1[1]")));
        assertTrue("expected v4", "v4".equals(JsonPath.read(doc, "$.object2.array1[2].k4")));
        assertTrue("expected v5", "v5".equals(JsonPath.read(doc, "$.object2.array1[2].k5")));
        assertTrue("expected v6", "v6".equals(JsonPath.read(doc, "$.object2.array1[2].k6")));
        assertTrue("expected 5", Integer.valueOf(5).equals(JsonPath.read(doc, "$.object2.array1[2].array2[0]")));
        assertTrue("expected 6", Integer.valueOf(6).equals(JsonPath.read(doc, "$.object2.array1[2].array2[1]")));
        assertTrue("expected 7", Integer.valueOf(7).equals(JsonPath.read(doc, "$.object2.array1[2].array2[2]")));
        assertTrue("expected 8", Integer.valueOf(8).equals(JsonPath.read(doc, "$.object2.array1[2].array2[3]")));
        assertTrue("expected 3", Integer.valueOf(3).equals(JsonPath.read(doc, "$.object2.array1[3]")));
        assertTrue("expected 4", Integer.valueOf(4).equals(JsonPath.read(doc, "$.object2.array1[4]")));
    }

}
