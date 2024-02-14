package org.json.junit;

import org.json.*;

import static org.junit.Assert.*;

import org.junit.Test;

public class JSONObjectDuplicateKeyTest {
    private static final String TEST_SOURCE = "{\"key\": \"value1\", \"key\": \"value2\"}";

    @Test(expected = JSONException.class)
    public void testThrowException() {
        new JSONObject(TEST_SOURCE);
    }

    @Test
    public void testOverwrite() {
        JSONObject jsonObject = new JSONObject(TEST_SOURCE, new JSONParserConfiguration(true));

        assertEquals("duplicate key should be overwritten", "value2", jsonObject.getString("key"));
    }
}
