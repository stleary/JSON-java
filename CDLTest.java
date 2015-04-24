package org.json.junit;

import static org.junit.Assert.*;
import org.junit.Test;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.CDL;

/**
 * Tests for CDL.java.
 * CDL provides an application level API, but it is not used by the
 * reference app. To test it, strings will be converted to JSON-Java classes
 * and then converted back.
 */
public class CDLTest {

    /**
     * String of lines where the column names are in the first row,
     * and all subsequent rows are values. All keys and values should be legal.
     */
    String lines = new String(
            "Col 1, Col 2,  \tCol 3, Col 4, Col 5, Col 6, Col 7\n" +
            "val1, val2, val3, val4, val5, val6, val7\n" +
            "1, 2, 3, 4\t, 5, 6, 7\n" +
            "true, false, true, true, false, false, false\n" +
            "0.23, 57.42, 5e27, -234.879, 2.34e5, 0.0, 9e-3\n" +
            "\"va\tl1\", \"v\bal2\", \"val3\", \"val\f4\", \"val5\", va\'l6, val7\n"
    );

    /**
     * CDL.toJSONArray() adds all values asstrings, with no filtering or 
     * conversions. For testing, this means that the expected JSONObject 
     * values all must be quoted in the cases where the JSONObject parsing 
     * might normally convert the value into a non-string. 
     */
    String expectedLines = new String(
            "[{Col 1:val1, Col 2:val2, Col 3:val3, Col 4:val4, Col 5:val5, Col 6:val6, Col 7:val7}, "+
            "{Col 1:\"1\", Col 2:\"2\", Col 3:\"3\", Col 4:\"4\", Col 5:\"5\", Col 6:\"6\", Col 7:\"7\"}, "+
            "{Col 1:\"true\", Col 2:\"false\", Col 3:\"true\", Col 4:\"true\", Col 5:\"false\", Col 6:\"false\", Col 7:\"false\"}, "+
            "{Col 1:\"0.23\", Col 2:\"57.42\", Col 3:\"5e27\", Col 4:\"-234.879\", Col 5:\"2.34e5\", Col 6:\"0.0\", Col 7:\"9e-3\"}, "+
            "{Col 1:\"va\tl1\", Col 2:\"v\bal2\", Col 3:val3, Col 4:\"val\f4\", Col 5:val5, Col 6:va\'l6, Col 7:val7}]");

    @Test(expected=NullPointerException.class)
    public void exceptionOnNullString() {
        /**
         * Attempts to create a JSONArray from a null string
         */
        String nullStr = null;
        CDL.toJSONArray(nullStr);
    }

    @Test(expected=JSONException.class)
    public void unbalancedQuoteInName() {
        /**
         * Attempts to create a JSONArray from a string with unbalanced quotes
         * in column title line
         */
        String badLine = "Col1, \"Col2\nVal1, Val2";
        CDL.toJSONArray(badLine);
    }

    @Test(expected=JSONException.class)
    public void unbalancedQuoteInValue() {
        /**
         * Attempts to create a JSONArray from a string with unbalanced quotes
         * in value line
         */
        String badLine = "Col1, Col2\n\"Val1, Val2";
        CDL.toJSONArray(badLine);
    }

    @Test(expected=JSONException.class)
    public void nullInName() {
        /**
         * Attempts to create a JSONArray from a string with null char
         * in column title line
         */
        String badLine = "C\0ol1, Col2\nVal1, Val2";
        CDL.toJSONArray(badLine);
    }

    @Test(expected=NullPointerException.class)
    public void nullJSONArrayToString() {
        /**
         * call toString with a null array
         */
        CDL.toString((JSONArray)null);
    }

    @Test
    public void emptyString() {
        /**
         * Create a JSONArray from an empty string
         */
        String emptyStr = "";
        JSONArray jsonArray = CDL.toJSONArray(emptyStr);
        assertTrue("CDL should return null when the input string is empty",
                jsonArray == null);
    }

    @Test
    public void onlyColumnNames() {
        /**
         * Create a JSONArray with only 1 row
         */
        String columnNameStr = "col1, col2, col3";
        JSONArray jsonArray = CDL.toJSONArray(columnNameStr);
        assertTrue("CDL should return null when only 1 row is given",
                jsonArray == null);
    }

    @Test
    public void emptyLinesToJSONArray() {
        /**
         * Create a JSONArray from string containing only whitespace and commas
         */
        String str = " , , , \n , , , ";
        JSONArray jsonArray = CDL.toJSONArray(str);
        assertTrue("JSONArray should be null for no content",
                jsonArray == null);
    }

    @Test
    public void emptyJSONArrayToString() {
        /**
         * call toString with a null array
         */
        JSONArray jsonArray = new JSONArray();
        String str = CDL.toString(jsonArray);
        assertTrue("CDL should return null for toString(null)",
                str == null);
    }

    @Test
    public void nullJSONArraysToString() {
        /**
         * call toString with a null arrays for names and values
         */
        String str = CDL.toString(null, null);
        assertTrue("CDL should return null for toString(null)",
                str == null);
    }

    @Test
    public void checkSpecialChars() {
        /**
         * Given a JSONArray that was not built by CDL, some chars may be
         * found that would otherwise be filtered out by CDL.
         */
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonArray.put(jsonObject);
        // \r will be filtered from name
        jsonObject.put("Col \r1", "V1");
        // \r will be filtered from value
        jsonObject.put("Col 2", "V2\r");
        assertTrue("expected length should be 1",jsonArray.length() == 1);
        String cdlStr = CDL.toString(jsonArray);
        jsonObject = jsonArray.getJSONObject(0);
        assertTrue(cdlStr.contains("\"Col 1\""));
        assertTrue(cdlStr.contains("Col 2"));
        assertTrue(cdlStr.contains("V1"));
        assertTrue(cdlStr.contains("\"V2\""));
    }

    @Test
    public void textToJSONArray() {
        /**
         * Create a JSONArray from a string of lines
         */
        JSONArray jsonArray = CDL.toJSONArray(lines);
        JSONArray expectedJsonArray = new JSONArray(expectedLines);
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
    }

    @Test
    public void jsonArrayToJSONArray() {
        /**
         * Create a JSONArray from a JSONArray of titles and a 
         * string of value lines
         */
        String nameArrayStr = "[Col1, Col2]";
        String values = "V1, V2";
        JSONArray nameJSONArray = new JSONArray(nameArrayStr);
        JSONArray jsonArray = CDL.toJSONArray(nameJSONArray, values);
        JSONArray expectedJsonArray = new JSONArray("[{Col1:V1,Col2:V2}]");
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
    }

    @Test
    public void textToJSONArrayAndBackToString() {
        /**
         * Create a JSONArray from a string of lines,
         * then convert to string and then back to JSONArray
         */
        JSONArray jsonArray = CDL.toJSONArray(lines);
        String jsonStr = CDL.toString(jsonArray);
        JSONArray finalJsonArray = CDL.toJSONArray(jsonStr);
        JSONArray expectedJsonArray = new JSONArray(expectedLines);
        Util.compareActualVsExpectedJsonArrays(finalJsonArray, expectedJsonArray);
    }
    

}