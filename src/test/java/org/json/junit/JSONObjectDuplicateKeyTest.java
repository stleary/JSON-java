package org.json.junit;

import org.json.*;

import static org.junit.Assert.*;

import org.junit.Test;

public class JSONObjectDuplicateKeyTest {
    private static final String TEST_SOURCE = "{\"key\": \"value1\", \"key\": \"value2\", \"key\": \"value3\"}";

    @Test(expected = JSONException.class)
    public void testThrowException() {
        new JSONObject(TEST_SOURCE);
    }

    @Test
    public void testIgnore() {
        JSONObject jsonObject = new JSONObject(TEST_SOURCE, new JSONParserConfiguration(
                JSONDuplicateKeyStrategy.IGNORE
        ));

        assertEquals("duplicate key shouldn't be overwritten", "value1", jsonObject.getString("key"));
    }

    @Test
    public void testOverwrite() {
        JSONObject jsonObject = new JSONObject(TEST_SOURCE, new JSONParserConfiguration(
                JSONDuplicateKeyStrategy.OVERWRITE
        ));

        assertEquals("duplicate key should be overwritten", "value3", jsonObject.getString("key"));
    }

    @Test
    public void testMergeIntoArray() {
        JSONObject jsonObject = new JSONObject(TEST_SOURCE, new JSONParserConfiguration(
                JSONDuplicateKeyStrategy.MERGE_INTO_ARRAY
        ));

        JSONArray jsonArray;
        assertTrue("duplicate key should be merged into JSONArray", jsonObject.get("key") instanceof JSONArray
                && (jsonArray = jsonObject.getJSONArray("key")).length() == 3
                && jsonArray.getString(0).equals("value1") && jsonArray.getString(1).equals("value2")
                && jsonArray.getString(2).equals("value3"));
    }
}
