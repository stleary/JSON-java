package org.json.junit;

/*
Public Domain.
*/

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
    private static final String LINES = "Col 1, Col 2,  \tCol 3, Col 4, Col 5, Col 6, Col 7\n" +
			"val1, val2, val3, val4, val5, val6, val7\n" +
			"1, 2, 3, 4\t, 5, 6, 7\n" +
			"true, false, true, true, false, false, false\n" +
			"0.23, 57.42, 5e27, -234.879, 2.34e5, 0.0, 9e-3\n" +
			"\"va\tl1\", \"v\bal2\", \"val3\", \"val\f4\", \"val5\", \"va'l6\", val7\n";


    /**
     * CDL.toJSONArray() adds all values as strings, with no filtering or 
     * conversions. For testing, this means that the expected JSONObject 
     * values all must be quoted in the cases where the JSONObject parsing 
     * might normally convert the value into a non-string. 
     */
    private static final String EXPECTED_LINES =
            "[  " +
                "{" +
                    "\"Col 1\":\"val1\", " +
                    "\"Col 2\":\"val2\", " +
                    "\"Col 3\":\"val3\", " +
                    "\"Col 4\":\"val4\", " +
                    "\"Col 5\":\"val5\", " +
                    "\"Col 6\":\"val6\", " +
                    "\"Col 7\":\"val7\"" +
                "}, " +
			"   {" +
                    "\"Col 1\":\"1\", " +
                    "\"Col 2\":\"2\", " +
                    "\"Col 3\":\"3\", " +
                    "\"Col 4\":\"4\", " +
                    "\"Col 5\":\"5\", " +
                    "\"Col 6\":\"6\", " +
                    "\"Col 7\":\"7\"" +
                "}, " +
			"   {" +
                    "\"Col 1\":\"true\", " +
                    "\"Col 2\":\"false\", " +
                    "\"Col 3\":\"true\", " +
                    "\"Col 4\":\"true\", " +
                    "\"Col 5\":\"false\", " +
                    "\"Col 6\":\"false\", " +
                    "\"Col 7\":\"false\"" +
                "}, " +
			    "{" +
                    "\"Col 1\":\"0.23\", " +
                    "\"Col 2\":\"57.42\", " +
                    "\"Col 3\":\"5e27\", " +
                    "\"Col 4\":\"-234.879\", " +
                    "\"Col 5\":\"2.34e5\", " +
                    "\"Col 6\":\"0.0\", " +
                    "\"Col 7\":\"9e-3\"" +
                "}, " +
			    "{" +
                    "\"Col 1\":\"va\tl1\", " +
                    "\"Col 2\":\"v\bal2\", " +
                    "\"Col 3\":\"val3\", " +
                    "\"Col 4\":\"val\f4\", " +
                    "\"Col 5\":\"val5\", " +
                    "\"Col 6\":\"va'l6\", " +
                    "\"Col 7\":\"val7\"" +
                "}" +
            "]";

    /**
     * Attempts to create a JSONArray from a null string.
     * Expect a NullPointerException.
     */
    @Test(expected=NullPointerException.class)
    public void exceptionOnNullString() {
        String nullStr = null;
        CDL.toJSONArray(nullStr);
    }

    /**
     * Attempts to create a JSONArray from a string with unbalanced quotes
     * in column title line. Expects a JSONException.
     */
    @Test
    public void unbalancedQuoteInName() {
        String badLine = "Col1, \"Col2\nVal1, Val2";
        try {
            CDL.toJSONArray(badLine);
            fail("Expecting an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "Missing close quote '\"'. at 12 [character 0 line 2]",
                    e.getMessage());
        }
    }
    
    /**
     * Attempts to create a JSONArray from a string with unbalanced quotes
     * in value line. Expects a JSONException.
     */
    @Test
    public void unbalancedQuoteInValue() {
        String badLine = "Col1, Col2\n\"Val1, Val2";
        try {
            CDL.toJSONArray(badLine);
            fail("Expecting an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "Missing close quote '\"'. at 22 [character 11 line 2]",
                    e.getMessage());
            
        }
    }

    /**
     * Attempts to create a JSONArray from a string with null char
     * in column title line. Expects a JSONException.
     */
    @Test
    public void nullInName() {
        String badLine = "C\0ol1, Col2\nVal1, Val2";
        try {
            CDL.toJSONArray(badLine);
            fail("Expecting an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "Bad character 'o' (111). at 2 [character 3 line 1]",
                    e.getMessage());
            
        }
    }
    
    /**
     * Attempt to create a JSONArray with unbalanced quotes and a properly escaped doubled quote.
     * Expects a JSONException. 
     */
    @Test
    public void unbalancedEscapedQuote(){
    	   String badLine = "Col1, Col2\n\"Val1, \"\"Val2\"\"";
           try {
               CDL.toJSONArray(badLine);
               fail("Expecting an exception");
           } catch (JSONException e) {
               assertEquals("Expecting an exception message",
                       "Missing close quote '\"'. at 26 [character 15 line 2]",
                       e.getMessage());
               
           }
    }

    /**
     * Csv parsing skip last row if last field of this row is empty #943
     */
    @Test
    public void csvParsingCatchesLastRow(){
        String data = "Field 1,Field 2,Field 3\n" +
                "value11,value12,\n" +
                "value21,value22,";

        JSONArray jsonArray = CDL.toJSONArray(data);

        JSONArray expectedJsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Field 1", "value11");
        jsonObject.put("Field 2", "value12");
        jsonObject.put("Field 3", "");
        expectedJsonArray.put(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("Field 1", "value21");
        jsonObject.put("Field 2", "value22");
        jsonObject.put("Field 3", "");
        expectedJsonArray.put(jsonObject);

        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
    }

    /**
     * Assert that there is no error for a single escaped quote within a properly embedded quote.
     */
    @Test
    public void singleEscapedQuote(){
               String singleEscape = "Col1, Col2\nVal1, \"\"\"Val2\"";
               JSONArray jsonArray = CDL.toJSONArray(singleEscape);
               
               String cdlStr = CDL.toString(jsonArray);
               assertTrue(cdlStr.contains("Col1"));
               assertTrue(cdlStr.contains("Col2"));
               assertTrue(cdlStr.contains("Val1"));
               assertTrue(cdlStr.contains("\"Val2"));
    }
    
    /**
     * Assert that there is no error for a single escaped quote within a properly
     * embedded quote when not the last value.
     */
    @Test
    public void singleEscapedQuoteMiddleString(){
               String singleEscape = "Col1, Col2\nVal1, \"\"\"Val2\"\nVal 3,Val 4";
               JSONArray jsonArray = CDL.toJSONArray(singleEscape);
               
               String cdlStr = CDL.toString(jsonArray);
               assertTrue(cdlStr.contains("Col1"));
               assertTrue(cdlStr.contains("Col2"));
               assertTrue(cdlStr.contains("Val1"));
               assertTrue(cdlStr.contains("\"Val2"));
    }
    
    /**
     * Attempt to create a JSONArray with an escape quote and no enclosing quotes.
     * Expects a JSONException. 
     */
    @Test
    public void badEscapedQuote(){
    	       String badLine = "Col1, Col2\nVal1, \"\"Val2";
    	       
    	       try {
                   CDL.toJSONArray(badLine);
                   fail("Expecting an exception");
               } catch (JSONException e) {
            	   //System.out.println("Message" + e.getMessage());
                   assertEquals("Expecting an exception message",
                           "Bad character 'V' (86). at 20 [character 9 line 2]",
                           e.getMessage());
                   
               }
               
    }
    
    /**
     * call toString with a null array
     */
    @Test(expected=NullPointerException.class)
    public void nullJSONArrayToString() {
        CDL.toString((JSONArray)null);
    }

    /**
     * Create a JSONArray from an empty string
     */
    @Test
    public void emptyString() {
        String emptyStr = "";
        JSONArray jsonArray = CDL.toJSONArray(emptyStr);
		assertNull("CDL should return null when the input string is empty", jsonArray);
    }

    /**
     * Create a JSONArray with only 1 row
     */
    @Test
    public void onlyColumnNames() {
        String columnNameStr = "col1, col2, col3";
        JSONArray jsonArray = CDL.toJSONArray(columnNameStr);
        assertNull("CDL should return null when only 1 row is given",
                jsonArray);
    }

    /**
     * Create a JSONArray from string containing only whitespace and commas
     */
    @Test
    public void emptyLinesToJSONArray() {
        String str = " , , , \n , , , ";
        JSONArray jsonArray = CDL.toJSONArray(str);
        assertNull("JSONArray should be null for no content",
                jsonArray);
    }

    /**
     * call toString with a null array
     */
    @Test
    public void emptyJSONArrayToString() {
        JSONArray jsonArray = new JSONArray();
        String str = CDL.toString(jsonArray);
        assertNull("CDL should return null for toString(null)",
                str);
    }

    /**
     * call toString with a null arrays for names and values
     */
    @Test
    public void nullJSONArraysToString() {
        String str = CDL.toString(null, null);
        assertNull("CDL should return null for toString(null)",
                str);
    }

    /**
     * Given a JSONArray that was not built by CDL, some chars may be
     * found that would otherwise be filtered out by CDL.
     */
    @Test
    public void checkSpecialChars() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonArray.put(jsonObject);
        // \r will be filtered from name
        jsonObject.put("Col \r1", "V1");
        // \r will be filtered from value
        jsonObject.put("Col 2", "V2\r");
		assertEquals("expected length should be 1", 1, jsonArray.length());
        String cdlStr = CDL.toString(jsonArray);
        jsonObject = jsonArray.getJSONObject(0);
        assertTrue(cdlStr.contains("\"Col 1\""));
        assertTrue(cdlStr.contains("Col 2"));
        assertTrue(cdlStr.contains("V1"));
        assertTrue(cdlStr.contains("\"V2\""));
    }

    /**
     * Create a JSONArray from a string of lines
     */
    @Test
    public void textToJSONArray() {
        JSONArray jsonArray = CDL.toJSONArray(LINES);
        JSONArray expectedJsonArray = new JSONArray(EXPECTED_LINES);
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
    }
    @Test
    public void textToJSONArrayPipeDelimited() {
        char delimiter = '|';
        JSONArray jsonArray = CDL.toJSONArray(LINES.replaceAll(",", String.valueOf(delimiter)), delimiter);
        JSONArray expectedJsonArray = new JSONArray(EXPECTED_LINES);
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
    }

    /**
     * Create a JSONArray from a JSONArray of titles and a 
     * string of value lines
     */
    @Test
    public void jsonArrayToJSONArray() {
        String nameArrayStr = "[\"Col1\", \"Col2\"]";
        String values = "V1, V2";
        JSONArray nameJSONArray = new JSONArray(nameArrayStr);
        JSONArray jsonArray = CDL.toJSONArray(nameJSONArray, values);
        JSONArray expectedJsonArray = new JSONArray("[{\"Col1\":\"V1\",\"Col2\":\"V2\"}]");
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
    }

    /**
     * Create a JSONArray from a string of lines,
     * then convert to string and then back to JSONArray
     */
    @Test
    public void textToJSONArrayAndBackToString() {
        JSONArray jsonArray = CDL.toJSONArray(LINES);
        String jsonStr = CDL.toString(jsonArray);
        JSONArray finalJsonArray = CDL.toJSONArray(jsonStr);
        JSONArray expectedJsonArray = new JSONArray(EXPECTED_LINES);
        Util.compareActualVsExpectedJsonArrays(finalJsonArray, expectedJsonArray);
    }

    /**
     * Create a JSONArray from a string of lines,
     * then convert to string and then back to JSONArray
     * with a custom delimiter
     */
    @Test
    public void textToJSONArrayAndBackToStringCustomDelimiter() {
        JSONArray jsonArray = CDL.toJSONArray(LINES, ',');
        String jsonStr = CDL.toString(jsonArray, ';');
        JSONArray finalJsonArray = CDL.toJSONArray(jsonStr, ';');
        JSONArray expectedJsonArray = new JSONArray(EXPECTED_LINES);
        Util.compareActualVsExpectedJsonArrays(finalJsonArray, expectedJsonArray);
    }
    

}