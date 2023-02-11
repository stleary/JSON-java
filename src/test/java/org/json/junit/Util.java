package org.json.junit;

/*
Public Domain.
*/

import static org.junit.Assert.*;

import java.util.*;
import org.json.*;

/**
 * These are helpful utility methods that perform basic comparisons between various objects. In most
 * cases, the comparisons are not order-dependent, or else the order is known.
 */
public class Util {

    /**
     * Compares two JSONArrays for equality. The arrays need not be in the same order.
     *
     * @param jsonArray created by the code to be tested
     * @param expectedJsonArray created specifically for comparing
     */
    public static void compareActualVsExpectedJsonArrays(
            JSONArray jsonArray, JSONArray expectedJsonArray) {
        assertTrue(
                "jsonArray lengths should be equal",
                jsonArray.length() == expectedJsonArray.length());
        for (int i = 0; i < jsonArray.length(); ++i) {
            Object value = jsonArray.get(i);
            Object expectedValue = expectedJsonArray.get(i);
            compareActualVsExpectedObjects(value, expectedValue);
        }
    }

    /**
     * Compares two JSONObjects for equality. The objects need not be in the same order
     *
     * @param jsonObject created by the code to be tested
     * @param expectedJsonObject created specifically for comparing
     */
    public static void compareActualVsExpectedJsonObjects(
            JSONObject jsonObject, JSONObject expectedJsonObject) {
        assertTrue(
                "jsonObjects should have the same length",
                jsonObject.length() == expectedJsonObject.length());
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);
            Object expectedValue = expectedJsonObject.get(key);
            compareActualVsExpectedObjects(value, expectedValue);
        }
    }

    /**
     * Compare two objects for equality. Might be JSONArray, JSONObject, or something else.
     *
     * @param value created by the code to be tested
     * @param expectedValue created specifically for comparing
     */
    private static void compareActualVsExpectedObjects(Object value, Object expectedValue) {
        if (value instanceof JSONObject && expectedValue instanceof JSONObject) {
            // Compare JSONObjects
            JSONObject jsonObject = (JSONObject) value;
            JSONObject expectedJsonObject = (JSONObject) expectedValue;
            compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
        } else if (value instanceof JSONArray && expectedValue instanceof JSONArray) {
            // Compare JSONArrays
            JSONArray jsonArray = (JSONArray) value;
            JSONArray expectedJsonArray = (JSONArray) expectedValue;
            compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
        } else {
            /**
             * Compare all other types using toString(). First, the types must also be equal, unless
             * both are Number type. Certain helper classes (e.g. XML) may create Long instead of
             * Integer for small int values.
             */
            if (!(value instanceof Number && expectedValue instanceof Number)) {
                // Non-Number and non-matching types
                assertEquals(
                        "object types should be equal ",
                        expectedValue.getClass().toString(),
                        value.getClass().toString());
            }
            /** Same types or both Numbers, compare by toString() */
            assertEquals("values should be equal", expectedValue.toString(), value.toString());
        }
    }

    /**
     * Asserts that all JSONObject maps are the same as the default ctor
     *
     * @param jsonObjects list of objects to be tested
     */
    public static void checkJSONObjectsMaps(List<JSONObject> jsonObjects) {
        if (jsonObjects == null || jsonObjects.size() == 0) {
            return;
        }
        Class<? extends Map> mapType = new JSONObject().getMapType();
        for (JSONObject jsonObject : jsonObjects) {
            if (jsonObject != null) {
                assertTrue(mapType == jsonObject.getMapType());
                checkJSONObjectMaps(jsonObject, mapType);
            }
        }
    }

    /**
     * Asserts that all JSONObject maps are the same as the default ctor
     *
     * @param jsonObject the object to be tested
     */
    public static void checkJSONObjectMaps(JSONObject jsonObject) {
        if (jsonObject != null) {
            checkJSONObjectMaps(jsonObject, jsonObject.getMapType());
        }
    }

    /**
     * Asserts that all JSONObject maps are the same as mapType
     *
     * @param jsonObject object to be tested
     * @param mapType mapType to test against
     */
    public static void checkJSONObjectMaps(JSONObject jsonObject, Class<? extends Map> mapType) {
        if (mapType == null) {
            mapType = new JSONObject().getMapType();
        }
        Set<String> keys = jsonObject.keySet();
        for (String key : keys) {
            Object val = jsonObject.get(key);
            if (val instanceof JSONObject) {
                JSONObject jsonObjectVal = (JSONObject) val;
                assertTrue(mapType == ((JSONObject) val).getMapType());
                checkJSONObjectMaps(jsonObjectVal, mapType);
            } else if (val instanceof JSONArray) {
                JSONArray jsonArrayVal = (JSONArray) val;
                checkJSONArrayMaps(jsonArrayVal, mapType);
            }
        }
    }

    /**
     * Asserts that all JSONObject maps in the JSONArray object match the default map
     *
     * @param jsonArrays list of JSONArray objects to be tested
     */
    public static void checkJSONArraysMaps(List<JSONArray> jsonArrays) {
        if (jsonArrays == null || jsonArrays.size() == 0) {
            return;
        }
        Class<? extends Map> mapType = new JSONObject().getMapType();
        for (JSONArray jsonArray : jsonArrays) {
            if (jsonArray != null) {
                checkJSONArrayMaps(jsonArray, mapType);
            }
        }
    }

    /**
     * Asserts that all JSONObject maps in the JSONArray object match mapType
     *
     * @param jsonArray object to be tested
     * @param mapType map type to be tested against
     */
    public static void checkJSONArrayMaps(JSONArray jsonArray, Class<? extends Map> mapType) {
        if (jsonArray == null) {
            return;
        }
        if (mapType == null) {
            mapType = new JSONObject().getMapType();
        }
        Iterator<Object> it = jsonArray.iterator();
        while (it.hasNext()) {
            Object val = it.next();
            if (val instanceof JSONObject) {
                JSONObject jsonObjectVal = (JSONObject) val;
                checkJSONObjectMaps(jsonObjectVal, mapType);
            } else if (val instanceof JSONArray) {
                JSONArray jsonArrayVal = (JSONArray) val;
                checkJSONArrayMaps(jsonArrayVal, mapType);
            }
        }
    }

    /**
     * Asserts that all JSONObject maps nested in the JSONArray match the default mapType
     *
     * @param jsonArray the object to be tested
     */
    public static void checkJSONArrayMaps(JSONArray jsonArray) {
        if (jsonArray != null) {
            checkJSONArrayMaps(jsonArray, null);
        }
    }
}
