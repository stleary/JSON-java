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
 * and then converted back. Since each row is an unordered JSONObject,
 * can't use a simple string compare to check for equality.
 * @author JSON.org
 * @version 2015-03-22
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
            "\"va\tl1\", \"v\bal2\", \"val3\", \"val\f4\", \"val5\", va\'l6, val7\n"
    );

    /**
     * Something I did not expect is that CDL.toJSONArray() adds all values as
     * strings, with no filtering or conversions. I suppose this makes it
     * easier to emit it as CDL later. For testing, it means that the 
     * expected JSONObject values all must be quoted in the cases where the
     * JSONObject parsing might normally convert the value into a non-string. 
     */
    String expectedLines = new String(
            "[{Col 1:val1, Col 2:val2, Col 3:val3, Col 4:val4, Col 5:val5, Col 6:val6, Col 7:val7}, "+
            "{Col 1:1, Col 2:2, Col 3:3, Col 4:4, Col 5:5, Col 6:6, Col 7:7}, "+
            "{Col 1:true, Col 2:false, Col 3:true, Col 4:true, Col 5:false, Col 6:false, Col 7:false}, "+
            "{Col 1:\"0.23\", Col 2:\"57.42\", Col 3:\"5e27\", Col 4:\"-234.879\", Col 5:\"2.34e5\", Col 6:\"0.0\", Col 7:\"9e-3\"}, "+
            "{Col 1:\"va\tl1\", Col 2:\"v\bal2\", Col 3:val3, Col 4:\"val\f4\", Col 5:val5, Col 6:va\'l6, Col 7:val7}]");

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

    @Test(expected=JSONException.class)
    public void shouldHandleUnbalancedQuoteInName() {
        String badLine = "Col1, \"Col2\nVal1, Val2";
        CDL.toJSONArray(badLine);
    }

    @Test(expected=JSONException.class)
    public void shouldHandleUnbalancedQuoteInValue() {
        String badLine = "Col1, Col2\n\"Val1, Val2";
        CDL.toJSONArray(badLine);
    }

    @Test(expected=JSONException.class)
    public void shouldHandleNullInName() {
        String badLine = "C\0ol1, Col2\nVal1, Val2";
        CDL.toJSONArray(badLine);
    }

    
    @Test
    public void toStringShouldCheckSpecialChars() {
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
    public void shouldConvertCDLToJSONArray() {
        // this array is built by CDL
        JSONArray jsonArray = CDL.toJSONArray(lines);
        // This array is built from JSON parsing 
        JSONArray expectedJsonArray = new JSONArray(expectedLines);
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
    }

    @Test
    public void shouldCreateJSONArrayUsingJSONArray() {
        String nameArrayStr = "[Col1, Col2]";
        String values = "V1, V2";
        JSONArray nameJSONArray = new JSONArray(nameArrayStr);
        JSONArray jsonArray = CDL.toJSONArray(nameJSONArray, values);
        JSONArray expectedJsonArray = new JSONArray("[{Col1:V1,Col2:V2}]");
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
    }

    @Test
    public void shouldConvertCDLToJSONArrayAndBackToString() {
        /**
         * This is the first test of normal functionality.
         * The string contains a typical variety of values
         * that might be found in a real CDL.
         */
        JSONArray jsonArray = CDL.toJSONArray(lines);
        String jsonStr = CDL.toString(jsonArray);
        JSONArray finalJsonArray = CDL.toJSONArray(jsonStr);
        JSONArray expectedJsonArray = new JSONArray(expectedLines);
        Util.compareActualVsExpectedJsonArrays(finalJsonArray, expectedJsonArray);
    }
    

}