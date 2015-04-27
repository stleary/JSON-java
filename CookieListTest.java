package org.json.junit;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;

/**
 * HTTP cookie specification: RFC6265
 * 
 * A cookie list is a JSONObject whose members are presumed to be cookie
 * name/value pairs. Entries are unescaped while being added, and escaped in 
 * the toString() output.
 * Unescaping means to convert %hh hex strings to the ascii equivalent
 * and converting '+' to ' '.
 * Escaping converts '+', '%', '=', ';' and ascii control chars to %hh hex strings.
 *
 * CookieList should not be considered as just a list of Cookie objects:
 * - CookieList stores a cookie name/value pair as a single entry; Cookie stores 
 *      it as 2 entries (key="name" and key="value").
 * - CookieList requires multiple name/value pairs as input; Cookie allows the
 *      'secure' name with no associated value
 * - CookieList has no special handling for attribute name/value pairs.
 */
public class CookieListTest {

    @Test(expected=NullPointerException.class)
    public void nullCookieListException() {
        /**
         * Attempts to create a CookieList from a null string
         */
        String cookieStr = null;
        CookieList.toJSONObject(cookieStr);
    }

    @Test(expected=JSONException.class)
    public void malFormedCookieListException() {
        /**
         * Attempts to create a CookieList from a malformed string
         */
        String cookieStr = "thisCookieHasNoEqualsChar";
        CookieList.toJSONObject(cookieStr);
    }

    @Test(expected=JSONException.class)
    public void emptyStringCookieList() {
        /**
         * Creates a CookieList from an empty string.
         * Cookie throws an exception, but CookieList does not
         */
        String cookieStr = "";
        String expectedCookieStr = "";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    @Test
    public void simpleCookieList() {
        /**
         * The simplest cookie is a name/value pair with no delimiter
         */
        String cookieStr = "SID=31d4d96e407aad42";
        String expectedCookieStr = "{\"SID\":\"31d4d96e407aad42\"}";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    @Test
    public void simpleCookieListWithDelimiter() {
        /**
         * The simplest cookie is a name/value pair with a delimiter
         */
        String cookieStr = "SID=31d4d96e407aad42;";
        String expectedCookieStr = "{\"SID\":\"31d4d96e407aad42\"}";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

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

    @Test
    public void convertCookieListWithNullValueToString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key",  JSONObject.NULL);
        String cookieToStr = CookieList.toString(jsonObject);
        assertTrue("toString() should be empty", "".equals(cookieToStr));
    }

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
