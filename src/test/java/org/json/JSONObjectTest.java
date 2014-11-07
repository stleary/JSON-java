package org.json;

import org.junit.Test;

import static org.junit.Assert.*;

public class JSONObjectTest {


    @Test
    public void testParse() throws Exception {
        String sampleJson = "{" +
                "'a':['a', 'r', 'r', 'a', 'y']," +
                "'b' : true," +
                "'c':-25," +
                "'d':12.252525," +
                "'e':null" +
                "}";
        JSONObject jObject = new JSONObject(sampleJson);
        assertNotNull(jObject);
        assertTrue(jObject.getBoolean("b"));
        assertEquals(jObject.getInt("c"), -25);
        assertEquals(jObject.getDouble("d"), 12.252525, 0.000000);
        assertEquals(jObject.get("e"), null);
        JSONArray jArray = jObject.getJSONArray("a");
        assertEquals(jArray.length(), 5);
        assertEquals(jArray.get(0), "a");
        assertEquals(jArray.get(1), "r");
        assertEquals(jArray.get(2), "r");
        assertEquals(jArray.get(3), "a");
        assertEquals(jArray.get(4), "y");
    }
}
