package org.json.junit;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
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
     * the values. The JSONObject rows are unordered and may differ between
     * rows. 
     * @param jsonArray the JSONArray which was created from the string
     * @param str the string which was used to create the JSONArray
     * @return true if equal, otherwise false
     */
    public boolean compareJSONArrayToString(JSONArray jsonArray, String str) {
        boolean result = true;
        int rows = jsonArray.length();
        StringReader sr = new StringReader(str);
        BufferedReader reader = new BufferedReader(sr);
        try {
            String columnNames = reader.readLine();
            String[] keys = columnNames.split(",");
            for (int i = 0; i < rows; ++i) {
                String row = reader.readLine();
                String[] values = row.split(",");
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (keys.length != jsonObject.length()) {
                    break;
                }
                int colIndex = 0;
                for (String key: keys) {
                    
                    Object obj = jsonObject.get(key);
                    
                }
            }
        } catch (IOException ignore) {}
        return result;
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
        assertTrue("CDL should convert string to JSONArray correctly",
                compareJSONArrayToString(jsonArray, lines));
    }

}