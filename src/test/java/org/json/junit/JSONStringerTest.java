package org.json.junit;

/*
Public Domain.
*/

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.*;

import org.json.*;
import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.*;


/**
 * Tests for JSON-Java JSONStringer and JSONWriter.
 */
class JSONStringerTest {

    /**
     * Object with a null key.
     * Expects a JSONException.
     */
    @Test
    void nullKeyException() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object();
        try {
            jsonStringer.key(null);
            assertTrue(false, "Expected an exception");
        } catch (JSONException e) {
            assertEquals("Null key.", e.getMessage(), "Expected an exception message");
        }
    }

    /**
     * Add a key with no object.
     * Expects a JSONException.
     */
    @Test
    void outOfSequenceException() {
        JSONStringer jsonStringer = new JSONStringer();
        try {
            jsonStringer.key("hi");
            assertTrue(false, "Expected an exception");
        } catch (JSONException e) {
            assertEquals("Misplaced key.", e.getMessage(), "Expected an exception message");
        }
    }

    /**
     * Missplace an array.
     * Expects a JSONException
     */
    @Test
    void missplacedArrayException() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object().endObject();
        try {
            jsonStringer.array();
            assertTrue(false, "Expected an exception");
        } catch (JSONException e) {
            assertEquals("Misplaced array.", e.getMessage(), "Expected an exception message");
        }
    }

    /**
     * Missplace an endErray.
     * Expects a JSONException
     */
    @Test
    void missplacedEndArrayException() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object();
        try {
            jsonStringer.endArray();
            assertTrue(false, "Expected an exception");
        } catch (JSONException e) {
            assertEquals("Misplaced endArray.", e.getMessage(), "Expected an exception message");
        }
    }

    /**
     * Missplace an endObject.
     * Expects a JSONException
     */
    @Test
    void missplacedEndObjectException() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.array();
        try {
            jsonStringer.endObject();
            assertTrue(false, "Expected an exception");
        } catch (JSONException e) {
            assertEquals("Misplaced endObject.", e.getMessage(), "Expected an exception message");
        }
    }

    /**
     * Missplace an object.
     * Expects a JSONException.
     */
    @Test
    void missplacedObjectException() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object().endObject();
        try {
            jsonStringer.object();
            assertTrue(false, "Expected an exception");
        } catch (JSONException e) {
            assertEquals("Misplaced object.", e.getMessage(), "Expected an exception message");
        }
    }

    /**
     * Exceeds implementation max nesting depth.
     * Expects a JSONException
     */
    @Test
    void exceedNestDepthException() {
        try {
            JSONStringer s = new JSONStringer();
            s.object().
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
            s.key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
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
            fail("Expected an exception message");
        } catch (JSONException e) {
            assertEquals("Nesting too deep.", e.getMessage(), "Expected an exception message");
        }
    }

    /**
     * Build a JSON doc using JSONString API calls,
     * then convert to JSONObject
     */
    @Test
    void simpleObjectString() {
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
        assertEquals(7, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "expected 7 top level items");
        assertEquals(Boolean.TRUE, jsonObject.query("/trueValue"), "expected true");
        assertEquals(Boolean.FALSE, jsonObject.query("/falseValue"), "expected false");
        assertEquals(JSONObject.NULL, jsonObject.query("/nullValue"), "expected null");
        assertEquals("hello world!", jsonObject.query("/stringValue"), "expected hello world!");
        assertEquals("h\be\tllo w\u1234orld!", jsonObject.query("/complexStringValue"), "expected h\be\tllo w\u1234orld!");
        assertEquals(Integer.valueOf(42), jsonObject.query("/intValue"), "expected 42");
        assertEquals(BigDecimal.valueOf(-23.45e67), jsonObject.query("/doubleValue"), "expected -23.45e67");
    }

    /**
     * Build a JSON doc using JSONString API calls,
     * then convert to JSONArray
     */
    @Test
    void simpleArrayString() {
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
        assertEquals(6, ((List<?>)(JsonPath.read(doc, "$"))).size(), "expected 6 top level items");
        assertEquals(Boolean.TRUE, jsonArray.query("/0"), "expected true");
        assertEquals(Boolean.FALSE, jsonArray.query("/1"), "expected false");
        assertEquals(JSONObject.NULL, jsonArray.query("/2"), "expected null");
        assertEquals("hello world!", jsonArray.query("/3"), "expected hello world!");
        assertEquals(Integer.valueOf(42), jsonArray.query("/4"), "expected 42");
        assertEquals(BigDecimal.valueOf(-23.45e67), jsonArray.query("/5"), "expected -23.45e67");
    }

    /**
     * Build a nested JSON doc using JSONString API calls, then convert to
     * JSONObject. Will create a long cascade of output by reusing the
     * returned values..
     */
    @Test
    void complexObjectString() {
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
        assertEquals(8, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "expected 8 top level items");
        assertEquals(4, ((Map<?, ?>)(JsonPath.read(doc, "$.object2"))).size(), "expected 4 object2 items");
        assertEquals(5, ((List<?>)(JsonPath.read(doc, "$.object2.array1"))).size(), "expected 5 array1 items");
        assertEquals(4, ((Map<?, ?>)(JsonPath.read(doc, "$.object2.array1[2]"))).size(), "expected 4 array[2] items");
        assertEquals(4, ((List<?>)(JsonPath.read(doc, "$.object2.array1[2].array2"))).size(), "expected 4 array1[2].array2 items");
        assertEquals(Boolean.TRUE, jsonObject.query("/trueValue"), "expected true");
        assertEquals(Boolean.FALSE, jsonObject.query("/falseValue"), "expected false");
        assertEquals(JSONObject.NULL, jsonObject.query("/nullValue"), "expected null");
        assertEquals("hello world!", jsonObject.query("/stringValue"), "expected hello world!");
        assertEquals(Integer.valueOf(42), jsonObject.query("/intValue"), "expected 42");
        assertEquals(BigDecimal.valueOf(-23.45e67), jsonObject.query("/doubleValue"), "expected -23.45e67");
        assertEquals("h\be\tllo w\u1234orld!", jsonObject.query("/complexStringValue"), "expected h\be\tllo w\u1234orld!");
        assertEquals("v1", jsonObject.query("/object2/k1"), "expected v1");
        assertEquals("v2", jsonObject.query("/object2/k2"), "expected v2");
        assertEquals("v3", jsonObject.query("/object2/k3"), "expected v3");
        assertEquals(Integer.valueOf(1), jsonObject.query("/object2/array1/0"), "expected 1");
        assertEquals(Integer.valueOf(2), jsonObject.query("/object2/array1/1"), "expected 2");
        assertEquals("v4", jsonObject.query("/object2/array1/2/k4"), "expected v4");
        assertEquals("v5", jsonObject.query("/object2/array1/2/k5"), "expected v5");
        assertEquals("v6", jsonObject.query("/object2/array1/2/k6"), "expected v6");
        assertEquals(Integer.valueOf(5), jsonObject.query("/object2/array1/2/array2/0"), "expected 5");
        assertEquals(Integer.valueOf(6), jsonObject.query("/object2/array1/2/array2/1"), "expected 6");
        assertEquals(Integer.valueOf(7), jsonObject.query("/object2/array1/2/array2/2"), "expected 7");
        assertEquals(Integer.valueOf(8), jsonObject.query("/object2/array1/2/array2/3"), "expected 8");
        assertEquals(Integer.valueOf(3), jsonObject.query("/object2/array1/3"), "expected 3");
        assertEquals(Integer.valueOf(4), jsonObject.query("/object2/array1/4"), "expected 4");
    }

}
