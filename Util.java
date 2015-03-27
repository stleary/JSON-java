package org.json.junit;

import static org.junit.Assert.*;

import java.util.*;

import org.json.*;

public class Util {

    
    
    /**
     * Compares two json arrays for equality
     * @param jsonArray created by the code to be tested
     * @param expectedJsonArray created specifically for comparing
     */
    public static void compareActualVsExpectedJsonArrays(JSONArray jsonArray,
            JSONArray expectedJsonArray) {
        assertTrue("jsonArray lengths should be equal",
                jsonArray.length() == expectedJsonArray.length());
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject expectedJsonObject = expectedJsonArray.getJSONObject(i);
            assertTrue("jsonObjects should have the same length",
                    jsonObject.length() == expectedJsonObject.length());
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                compareJsonObjectEntries(jsonObject, expectedJsonObject, key);
            }
        }
    }

    /**
     * Compares two json objects for equality
     * @param jsonObject created by the code to be tested
     * @param expectedJsonObject created specifically for comparing
     */
    public static void compareActualVsExpectedJsonObjects(
            JSONObject jsonObject, JSONObject expectedJsonObject) {
        assertTrue("jsonObjects should have the same length",
                jsonObject.length() == expectedJsonObject.length());
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            compareJsonObjectEntries(jsonObject, expectedJsonObject, key);
        }
    }

    /**
     * Compare two jsonObject entries
     * @param jsonObject created by the code to be tested
     * @param expectedJsonObject created specifically for comparing
     * @param key key to the jsonObject entry to be compared
     */
    private static void compareJsonObjectEntries(JSONObject jsonObject,
            JSONObject expectedJsonObject, String key) {
        Object value = jsonObject.get(key);
        Object expectedValue = expectedJsonObject.get(key);
        if (value instanceof JSONObject) {
            JSONObject childJsonObject = jsonObject.getJSONObject(key);
            JSONObject expectedChildJsonObject =
                    expectedJsonObject.getJSONObject(key);
            compareActualVsExpectedJsonObjects(
                    childJsonObject, expectedChildJsonObject);
        } else if (value instanceof JSONArray) {
            JSONArray childJsonArray = jsonObject.getJSONArray(key);
            JSONArray expectedChildJsonArray =
                    expectedJsonObject.getJSONArray(key);
            compareActualVsExpectedJsonArrays(
                    childJsonArray, expectedChildJsonArray);
        } else if (!(value instanceof String) && !(expectedValue instanceof String)) {
            assertTrue("string values should be equal for actual: "+
                    value.toString()+" expected: "+expectedValue.toString(),
                    value.toString().equals(expectedValue.toString()));
        } else {
            String testStr = "key: "+key+" val: "+value.toString();
            String actualStr = expectedValue.toString();
            assertTrue("string values should be equal for actual: "+
                testStr+" expected: "+actualStr,
                value.equals(expectedValue.toString()));
        }
    }
}
