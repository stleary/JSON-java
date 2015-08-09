package org.json.junit;

import static org.junit.Assert.*;

import java.util.*;

import org.json.*;

/**
 * These are helpful utility methods that perform basic comparisons
 * between various objects. In most cases, the comparisons are not
 * order-dependent, or else the order is known.
 */
public class Util {

    /**
     * Compares two JSONArrays for equality.
     * The arrays need not be in the same order.
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
     * Compares two JSONObjects for equality. The objects need not be
     * in the same order 
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
            /**
             * Certain helper classes (e.g. XML) may create Long instead of
             * Integer for small int values. As long as both are Numbers,
             * just compare the toString() values.
             * TODO: this may not work in the case where the underlying types
             * do not have the same precision.
             */
            if (!(value instanceof Number && expectedValue instanceof Number)) {
                assertTrue("object types should be equal for actual: "+
                    value.toString()+" ("+
                    value.getClass().toString()+") expected: "+
                    expectedValue.toString()+" ("+
                    expectedValue.getClass().toString()+")",
                    value.getClass().toString().equals(
                            expectedValue.getClass().toString()));
            }
            /**
             * When in doubt, compare by string
             * TODO: should not this be an else to the previous condition?
             */
            assertTrue("string values should be equal for actual: "+
                value.toString()+" expected: "+expectedValue.toString(),
                value.toString().equals(expectedValue.toString()));
        }
    }

    /**
     * Sometimes test completion requires comparison of JSONArray objects that
     * were produced from a JSONObject, and so unordered. This method is 
     * imperfect since it only compares the array elements and won't catch
     * JSON syntax errors but at least it does not rely on ordering
     * <p>
     * It is expected that the arrays to be compared come from JSONArray
     * instances which have been rendered by toString(), and whose syntax
     * chars have been removed.
     * <p>
     * TODO: why are we not simply comparing the JSONArrays?
     * <p>
     * @param names an array of strings for comparison
     * @param expectedNames the other array of strings for comparison
     */
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

    /**
     * This is stopgap test utility. It is meant to compare strings
     * of XML, but it does not take ordering into account and should
     * not be expected to work correctly with complex XML.
     * @param aXmlStr an XML doc to be compared
     * @param bXmlStr the other XML doc to be compared
     */
    public static void compareXML(String aXmlStr, String bXmlStr) {
        // TODO For simple tests this may be adequate, but it won't work for
        // elements with multiple attributes and possibly other cases as well.
        // Should use XMLUnit or similar.
        assertTrue("expected equal XML strings \naXmlStr: "+
                aXmlStr+ "\nbXmlStr: " +bXmlStr, aXmlStr.equals(bXmlStr));
        
    }

}
