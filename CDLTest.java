package org.json.junit;

import static org.junit.Assert.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.CDL;


/**
 * Tests for {@link CDL}.
 * CDL provides an application level API, it is not actually used by the
 * reference app. To test it, strings will be converted to JSON-Java classes
 * and then converted back. But each row will be an unordered JSONObject,
 * so can't use a simple string compare.
 * @author JSON.org
 * @version 2015-03-16
 * 
 */
public class CDLTest {

    /**
     * Compares a JSON array to the original string. The top row of the
     * string contains the JSONObject keys and the remaining rows contain
     * the values. The JSONObject in each JSONArray row is expected to have
     * an entry corresponding to each key/value pair in the string. 
     * Each JSONObject row is unordered in its own way.
     * @param jsonArray the JSONArray which was created from the string
     * @param str the string which was used to create the JSONArray
     * @return null if equal, otherwise error description
     */
    public String compareJSONArrayToString(JSONArray jsonArray, String str) {
        int rows = jsonArray.length();
        StringReader sr = new StringReader(str);
        BufferedReader reader = new BufferedReader(sr);
        try {
            // first line contains the keys to the JSONObject array entries
            String columnNames = reader.readLine();
            String[] keys = columnNames.split(",");
            /**
             * Each line contains the values for the corresponding
             * JSONObject array entry
             */
            for (int i = 0; i < rows; ++i) {
                String row = reader.readLine();
                String[] values = row.split(",");
                // need a value for every key to proceed
                if (keys.length != values.length) {
                    return("row: " +i+ " key and value counts do not match");
                }
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // need a key for every JSONObject entry to proceed
                if (keys.length != jsonObject.length()) {
                    return("row: " +i+ " key and jsonObject counts do not match");
                }
                /**
                 * convert string entries into a natural order map. Trim the
                 * keys and values for tokener compatibility
                 */
                Map<String, String> strMap = new TreeMap<String, String>();
                for (int j = 0; j < keys.length; ++j) {
                    strMap.put(keys[j].trim(), values[j].trim());
                }
                // put the JSONObjet key/value pairs in natural key order
                Iterator<String> keyIt = jsonObject.keys();
                Map<String, String> jsonObjectMap = new TreeMap<String, String>();
                while (keyIt.hasNext()) {
                    String key = keyIt.next();
                    jsonObjectMap.put(key, jsonObject.get(key).toString());
                }
                if (!strMap.equals(jsonObjectMap)) {
                    return("row: " +i+ "string does not match jsonObject");
                }
            }
        } catch (IOException ignore) {
        } catch (JSONException ignore) {}
        return null;
    }
    
    @Test
    public void shouldConvertCDLToJSONArray() {
        /**
         * simple array where the first row of the string consists of the
         * column names and there are 2 value rows
         */
        String lines = new String(
                "Col 1, Col 2, Col 3, Col 4, Col 5, Col 6, Col 7\n" +
                "val1, val2, val3, val4, val5, val6, val7\n" +
                "1, 2, 3, 4, 5, 6, 7\n");
        JSONArray jsonArray = CDL.toJSONArray(lines);
        String resultStr = compareJSONArrayToString(jsonArray, lines);
        if (resultStr != null) {
            assertTrue("CDL should convert string to JSONArray correctly: " +
                resultStr, false);
        }
    }

}