package org.json.junit;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;

/**
 * HTTP cookie specification RFC6265: http://tools.ietf.org/html/rfc6265
 * <p>
 * A cookie list is a JSONObject whose members are presumed to be cookie
 * name/value pairs. Entries are unescaped while being added, and escaped in 
 * the toString() output.
 * Unescaping means to convert %hh hex strings to the ascii equivalent
 * and converting '+' to ' '.
 * Escaping converts '+', '%', '=', ';' and ascii control chars to %hh hex strings.
 * <p>
 * CookieList should not be considered as just a list of Cookie objects:<br>
 * - CookieList stores a cookie name/value pair as a single entry; Cookie stores 
 *      it as 2 entries (key="name" and key="value").<br>
 * - CookieList requires multiple name/value pairs as input; Cookie allows the
 *      'secure' name with no associated value<br>
 * - CookieList has no special handling for attribute name/value pairs.<br>
 */
public class CookieListTest {

    /**
     * Attempts to create a CookieList from a null string.
     * Expects a NullPointerException.
     */
    @Test(expected=NullPointerException.class)
    public void nullCookieListException() {
        String cookieStr = null;
        CookieList.toJSONObject(cookieStr);
    }

    /**
     * Attempts to create a CookieList from a malformed string.
     * Expects a JSONException.
     */
    @Test
    public void malFormedCookieListException() {
        String cookieStr = "thisCookieHasNoEqualsChar";
        try {
            CookieList.toJSONObject(cookieStr);
            assertTrue("should throw an exception", false);
        } catch (JSONException e) {
            /**
             * Not sure of the missing char, but full string compare fails 
             */
            assertTrue("Expecting an exception message",
                    e.getMessage().startsWith("Expected '=' and instead saw '") &&
                            e.getMessage().endsWith("' at 27 [character 28 line 1]"));
        }
    }

    /**
     * Creates a CookieList from an empty string.
     */
    @Test
    public void emptyStringCookieList() {
        String cookieStr = "";
        String expectedCookieStr = "";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        assertTrue(jsonObject.length() == 0);
    }

    /**
     * CookieList with the simplest cookie - a name/value pair with no delimiter.
     */
    @Test
    public void simpleCookieList() {
        String cookieStr = "SID=31d4d96e407aad42";
        String expectedCookieStr = "{\"SID\":\"31d4d96e407aad42\"}";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    /**
     * CookieList with a single a cookie which has a name/value pair and delimiter.
     */
    @Test
    public void simpleCookieListWithDelimiter() {
        String cookieStr = "SID=31d4d96e407aad42;";
        String expectedCookieStr = "{\"SID\":\"31d4d96e407aad42\"}";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    /**
     * CookieList with multiple cookies consisting of name/value pairs
     * with delimiters.
     */
    @Test
    public void multiPartCookieList() {
        String cookieStr = 
            "name1=myCookieValue1;  "+
            "  name2=myCookieValue2;"+
            "name3=myCookieValue3;"+
            "  name4=myCookieValue4;  "+
            "name5=myCookieValue5;"+
            "  name6=myCookieValue6;";
        String expectedCookieStr = 
            "{"+
                "\"name1\":\"myCookieValue1\","+
                "\"name2\":\"myCookieValue2\","+
                "\"name3\":\"myCookieValue3\","+
                "\"name4\":\"myCookieValue4\","+
                "\"name5\":\"myCookieValue5\","+
                "\"name6\":\"myCookieValue6\""+
            "}";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    /**
     * CookieList from a JSONObject with valid key and null value
     */
    @Test
    public void convertCookieListWithNullValueToString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key",  JSONObject.NULL);
        String cookieToStr = CookieList.toString(jsonObject);
        assertTrue("toString() should be empty", "".equals(cookieToStr));
    }

    /**
     * CookieList with multiple entries converted to a JSON document. 
     */
    @Test
    public void convertCookieListToString() {
        String cookieStr = 
                "name1=myCookieValue1;  "+
                "  name2=myCookieValue2;"+
                "name3=myCookieValue3;"+
                "  name4=myCookieValue4;  "+
                "name5=myCookieValue5;"+
                "  name6=myCookieValue6;";
            String expectedCookieStr = 
                "{"+
                    "\"name1\":\"myCookieValue1\","+
                    "\"name2\":\"myCookieValue2\","+
                    "\"name3\":\"myCookieValue3\","+
                    "\"name4\":\"myCookieValue4\","+
                    "\"name5\":\"myCookieValue5\","+
                    "\"name6\":\"myCookieValue6\""+
                "}";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        String cookieToStr = CookieList.toString(jsonObject);
        JSONObject finalJsonObject = CookieList.toJSONObject(cookieToStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }

    /**
     * CookieList with multiple entries and some '+' chars and URL-encoded
     * values converted to a JSON document. 
     */
    @Test   
    public void convertEncodedCookieListToString() {
        String cookieStr = 
                "name1=myCookieValue1;  "+
                "  name2=my+Cookie+Value+2;"+
                "name3=my%2BCookie%26Value%3B3%3D;"+
                "  name4=my%25CookieValue4;  "+
                "name5=myCookieValue5;"+
                "  name6=myCookieValue6;";
            String expectedCookieStr = 
                "{"+
                    "\"name1\":\"myCookieValue1\","+
                    "\"name2\":\"my Cookie Value 2\","+
                    "\"name3\":\"my+Cookie&Value;3=\","+
                    "\"name4\":\"my%CookieValue4\","+
                    "\"name5\":\"myCookieValue5\","+
                    "\"name6\":\"myCookieValue6\""+
                "}";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        String cookieToStr = CookieList.toString(jsonObject);
        JSONObject finalJsonObject = CookieList.toJSONObject(cookieToStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }
    

}
