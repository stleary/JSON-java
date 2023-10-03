package org.json.junit.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertThrows;


public class JsonObjectTest {

    @Test
    public void jsonObject_map() {
        assertThrows(JSONException.class, () -> new JSONObject(Map.of("a", Double.POSITIVE_INFINITY)));
    }

    @Test
    public void jsonObject_bean() {
        assertThrows(JSONException.class, () -> new JSONObject(new MyBean()));
    }
    public static class MyBean {
        public double getA() { return Double.POSITIVE_INFINITY; }
    }
}
