package org.json.junit;

import static org.junit.Assert.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
     * String of lines where the column names are in the first row,
     * and all subsequent rows are values. All keys and values should be legal.
     */
    String lines = new String(
            "Col 1, Col 2, Col 3, Col 4, Col 5, Col 6, Col 7\n" +
            "val1, val2, val3, val4, val5, val6, val7\n" +
            "1, 2, 3, 4, 5, 6, 7\n" +
            "true, false, true, true, false, false, false\n" +
            "0.23, 57.42, 5e27, -234.879, 2.34e5, 0.0, 9e-3\n" +
            "\"va\tl1\", \"val2\", \"val\\b3\", \"val4\\n\", \"va\\rl5\", val6, val7\n"
    );

    @Test(expected=NullPointerException.class)
    public void shouldThrowExceptionOnNullString() {
        String nullStr = null;
        CDL.toJSONArray(nullStr);
    }

    @Test
    /**
     * Note: This test reveals a bug in the method JavaDoc. It should
     * mention it might return null, or it should return an empty JSONArray.
     */
    public void shouldHandleOnlyColumnNames() {
        String columnNameStr = "col1, col2, col3";
        JSONArray jsonArray = CDL.toJSONArray(columnNameStr);
        assertTrue("CDL should return null when only 1 row is given",
                jsonArray == null);
    }

    @Test
    /**
     * Note: This test reveals a bug in the method JavaDoc. It should
     * mention it might return null, or it should return an empty JSONArray.
     */
    public void shouldHandleEmptyString() {
        String emptyStr = "";
        JSONArray jsonArray = CDL.toJSONArray(emptyStr);
        assertTrue("CDL should return null when the input string is empty",
                jsonArray == null);
    }

    @Test
    public void toStringShouldCheckSpecialChars() {
        /**
         * This is pretty clumsy, there should be a better way
         * to perform this test. Needs more debugging. The problem
         * may be that these chars are sanitized out by CDL when constructing
         * a JSONArray from a string.
         */
        String singleStr = "\"Col 1\"\n1";
        JSONArray jsonArray = CDL.toJSONArray(singleStr);
        JSONObject jsonObject = (JSONObject)(jsonArray.get(0));
        jsonObject.put("Col \r4", "V4");
        jsonObject.put("Col \0 a", "V5");
        boolean doNotNormalize = false;
        List<List<String>> expectedLines = 
                sortColumnsInLines("Col ,2\",Col 1,\"Col 4\",\"Col  a\"\nV2,1,V4,V5,V3",
                        doNotNormalize);
        List<List<String>> jsonArrayLines = 
                sortColumnsInLines(CDL.toString(jsonArray), doNotNormalize);
        System.out.println("expected: " +expectedLines);
        System.out.println("jsonArray: " +jsonArrayLines);
    }

    @Test
    public void shouldConvertJSONArrayToCDLString() {
        /**
         * This is the first test of normal functionality.
         * The string contains a typical variety of values
         * that might be found in a real CDL.
         */
        final boolean normalize = true;
        final boolean doNotNormalize = false;
        JSONArray jsonArray = CDL.toJSONArray(lines);
        String jsonStr = CDL.toString(jsonArray);
        // normal sorted
        List<List<String>> sortedLines = sortColumnsInLines(lines, normalize);
        // sorted, should already be normalized
        List<List<String>> sortedJsonStr = sortColumnsInLines(jsonStr, doNotNormalize);
        boolean result = sortedLines.equals(sortedJsonStr);
        if (!result) {
            System.out.println("lines: " +sortedLines);
            System.out.println("jsonStr: " +sortedJsonStr);
            assertTrue("CDL should convert JSONArray back to original string: " +
                lines.equals(jsonStr), false);
        }
    }

    @Test
    public void shouldConvertCDLToJSONArray() {
        JSONArray jsonArray = CDL.toJSONArray(lines);
        String resultStr = compareJSONArrayToString(jsonArray, lines);
        if (resultStr != null) {
            assertTrue("CDL should convert string to JSONArray: " +
                resultStr, false);
        }
    }


    /******************************************************************\
    * SUPPORT AND UTILITY
    \******************************************************************/

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
    private String compareJSONArrayToString(JSONArray jsonArray, String str) {
        int rows = jsonArray.length();
        StringReader sr = new StringReader(str);
        BufferedReader reader = new BufferedReader(sr);
        try {
            // first line contains the keys to the JSONObject array entries
            String columnNames = reader.readLine();
            columnNames = normalizeString(columnNames);
            String[] keys = columnNames.split(",");
            /**
             * Each line contains the values for the corresponding
             * JSONObject array entry
             */
            for (int i = 0; i < rows; ++i) {
                String line = reader.readLine();
                line = normalizeString(line);
                String[] values = line.split(",");
                // need a value for every key to proceed
                if (keys.length != values.length) {
                    System.out.println("keys: " + Arrays.toString(keys));
                    System.out.println("values: " + Arrays.toString(values));
                    return("row: " +i+ " key and value counts do not match");
                }
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // need a key for every JSONObject entry to proceed
                if (keys.length != jsonObject.length()) {
                    System.out.println("keys: " + Arrays.toString(keys));
                    System.out.println("jsonObject: " + jsonObject.toString());
                    return("row: " +i+ " key and jsonObject counts do not match");
                }
                // convert string entries into a natural order map.
                Map<String, String> strMap = new TreeMap<String, String>();
                for (int j = 0; j < keys.length; ++j) {
                    strMap.put(keys[j], values[j]);
                }
                // put the JSONObjet key/value pairs in natural key order
                Iterator<String> keyIt = jsonObject.keys();
                Map<String, String> jsonObjectMap = new TreeMap<String, String>();
                while (keyIt.hasNext()) {
                    String key = keyIt.next();
                    jsonObjectMap.put(key, jsonObject.get(key).toString());
                }
                if (!strMap.equals(jsonObjectMap)) {
                    System.out.println("strMap: " +strMap.toString());
                    System.out.println("jsonObjectMap: " +jsonObjectMap.toString());
                    return("row: " +i+ "string does not match jsonObject");
                }
            }
        } catch (IOException ignore) {
        } catch (JSONException ignore) {}
        return null;
    }

    /**
     * Utility to trim and remove internal quotes from comma delimited strings.
     * Need to do this because JSONObject does the same thing
     * @param line the line to be normalized
     * @return the normalized line
     */
    private String normalizeString(String line) {
        StringBuilder builder = new StringBuilder();
        boolean comma = false;
        String[] values = line.split(",");
        for (int i = 0; i < values.length; ++i) {
            if (comma) {
                builder.append(",");
            }
            comma = true;
            values[i] = values[i].trim();
            // strip optional surrounding quotes
            values[i] = values[i].replaceAll("^\"|\"$", "");
            builder.append(values[i]);
        }
        return builder.toString();
    }

    /**
     * Utility to sort the columns in a (possibly) multi-lined string.
     * The columns are column separated. Need to do this because 
     * JSONObects are not ordered
     * @param string the string to be sorted
     * @param normalize flag, true if line should be normalized
     * @return a list of sorted lines, where each line is a list sorted 
     * in natural key order
     */
    private List<List<String>> sortColumnsInLines(String string,
            boolean normalizeFlag) {
        List<List<String>> lineList = new ArrayList<List<String>>();
        StringReader sr = new StringReader(string);
        BufferedReader reader = new BufferedReader(sr);
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (normalizeFlag) {
                    line = normalizeString(line);
                }
                List<String> columnList = new ArrayList<String>();
                String[] values = line.split(",");
                for (int i = 0; i < values.length; ++i) {
                    columnList.add(values[i]);
                }
                Collections.sort(columnList);
                lineList.add(columnList);
            }
        } catch (IOException ignore) {}
        return lineList;
    }
}