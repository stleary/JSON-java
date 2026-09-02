package org.json;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

/**
 * Tests cyclic-reference detection during serialization. Lives in {@code org.json}
 * so tests can assert {@link JSONObject.CyclicReferenceException} by type.
 */
public class Issue1056CyclicSerializationTest {

    @Test
    public void selfReferentialJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("key", "value");
        jo.put("self", jo);
        JSONException ex = assertThrows(JSONException.class, jo::toString);
        assertTrue(ex instanceof JSONObject.CyclicReferenceException);
    }

    @Test
    public void mutualJSONObjectCycle() {
        JSONObject a = new JSONObject();
        JSONObject b = new JSONObject();
        a.put("b", b);
        b.put("a", a);
        JSONException ex = assertThrows(JSONException.class, a::toString);
        assertTrue(ex instanceof JSONObject.CyclicReferenceException);
    }

    @Test
    public void mixedJSONObjectJSONArrayCycle() {
        JSONObject jo = new JSONObject();
        JSONArray arr = new JSONArray();
        jo.put("arr", arr);
        arr.put(jo);
        JSONException ex = assertThrows(JSONException.class, jo::toString);
        assertTrue(ex instanceof JSONObject.CyclicReferenceException);
    }

    @Test
    public void selfReferentialJSONArray() {
        JSONArray arr = new JSONArray();
        arr.put("x");
        arr.put(arr);
        JSONException ex = assertThrows(JSONException.class, arr::toString);
        assertTrue(ex instanceof JSONObject.CyclicReferenceException);
    }

    @Test
    public void valueToStringOnCyclicJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("self", jo);
        JSONException ex = assertThrows(JSONException.class, () -> JSONObject.valueToString(jo));
        assertTrue(ex instanceof JSONObject.CyclicReferenceException);
    }
}
