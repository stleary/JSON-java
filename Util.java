package org.json.junit;

import static org.junit.Assert.*;

public class Util {

    /**
     * Utility method to check for a target string, then remove it from
     * the string to be searched.
     * @param jsonStr the string to be searched
     * @param expectedStr the target string to search for
     * @param assertStr the error message for the assert
     * @return new string with target substring removed
     */
    public static String checkAndRemoveString(
            String jsonStr, String expectedStr, String assertStr) {
        int idx = jsonStr.indexOf(expectedStr);
        assertTrue(assertStr, idx != -1);
        String newStr = jsonStr.substring(0, idx)+
            jsonStr.substring(idx+expectedStr.length());
        return newStr;
    }

    /**
     * Utility method to strip out selected punctuation chars and confirm
     * that jsonStr is now empty
     * @param jsonStr the string to be verified
     * @param regexStr regex string of the chars to remove
     * @param assertStr the error message for the assert
     */
    public static void verifyEmptyJsonStr(String jsonStr, String regexStr,
            String assertStr) {
        jsonStr = jsonStr.replaceAll(regexStr, "");
        assertTrue(assertStr, jsonStr.length() == 0);
    }

    /**
     * Utility method to check for a set of target strings, 
     * then remove them from the string to be searched.
     * When completed, punctuation marks are stripped out and
     * the string to be searched is confirmed as empty
     * @param jsonStr the string to be searched
     * @param expectedStrArray the target strings to search for
     * @param regexStr regex string of the chars to remove
     * @param methodStr the method name
     */
    public static void checkAndRemoveStrings(String jsonStr,
            String[] expectedStr, String regexStr, String methodStr) {
        for (int i = 0; i < expectedStr.length; ++i) {
            jsonStr = Util.checkAndRemoveString(jsonStr, expectedStr[i], 
                methodStr+expectedStr+" should be included in string output");
        }
        Util.verifyEmptyJsonStr(jsonStr, regexStr,
            methodStr+" jsonStr should be empty");
    }
}
