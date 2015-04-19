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
            Object value = jsonArray.get(i);
            Object expectedValue = expectedJsonArray.get(i);
            compareActualVsExpectedObjects(value, expectedValue);
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
            Object value = jsonObject.get(key);
            Object expectedValue = expectedJsonObject.get(key);
            compareActualVsExpectedObjects(value, expectedValue);
        }
    }

    /**
     * Compare two objects for equality. Might be JSONArray, JSONObject,
     * or something else.
     * @param value created by the code to be tested
     * @param expectedValue created specifically for comparing
     * @param key key to the jsonObject entry to be compared
     */
    private static void compareActualVsExpectedObjects(Object value,
            Object expectedValue) {
        if (value instanceof JSONObject && expectedValue instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject)value;
            JSONObject expectedJsonObject = (JSONObject)expectedValue;
            compareActualVsExpectedJsonObjects(
                    jsonObject, expectedJsonObject);
        } else if (value instanceof JSONArray && expectedValue instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray)value;
            JSONArray expectedJsonArray = (JSONArray)expectedValue;
            compareActualVsExpectedJsonArrays(
                    jsonArray, expectedJsonArray);
        } else {
            assertTrue("string values should be equal for actual: "+
                value.toString()+" expected: "+expectedValue.toString(),
                value.toString().equals(expectedValue.toString()));
        }
    }

    public static void compareActualVsExpectedStringArrays(String[] names,
            String [] expectedNames) {
        assertTrue("Array lengths should be equal",
                names.length == expectedNames.length);
        List<String> lNames = new ArrayList<String>(Arrays.asList(names));
        for (int i = 0; i < expectedNames.length; ++i) {
            String expectedName = expectedNames[i];
            assertTrue("expected to find "+expectedName, 
                    lNames.contains(expectedName));
            lNames.remove(expectedName);
        }
    }

    public static void compareXML(String aXmlStr, String bXmlStr) {
        // TODO For simple tests this may be adequate, but it won't work for
        // elements with multiple attributes and possibly other cases as well.
        // Should use XMLUnit or similar.
        assertTrue("expected equal XML strings \naXmlStr: "+
                aXmlStr+ "\nbXmlStr: " +bXmlStr, aXmlStr.equals(bXmlStr));
        
    }

}
