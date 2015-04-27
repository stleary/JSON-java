package org.json.junit;

import java.util.*;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;

/**
 * HTTP cookie specification: RFC6265
 * 
 * A cookie list is a JSONObject whose members are cookie name/value pairs.
 * Entries are unescaped while being added, and escaped in the toString()
 * method. Unescaping means to convert %hh hex strings to the ascii equivalent
 * and converting '+' to ' '. Escaping converts '+', '%', '=', ';',
 * and ascii control chars to %hh hex strings.
 *
 * CookieList should not be considered as just a list of Cookie objects:
 * - CookieList stores a cookie name/value pair as a single entry; Cookie stores 
 *      it as 2 entries.
 * - CookieList expects multiple name/value pairs as input; Cookie allows the
 *      'secure' name with no associated value
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
    public void multiPartCookieList() {
        String cookieStr = 
            "PH=deleted;  "+
            " expires=Wed, 19-Mar-2014 17:53:53 GMT;"+
            "path=/;   "+
            "    domain=.yahoo.com;";
        String expectedCookieStr = 
            "{\"path\":\"/\","+
            "\"expires\":\"Wed, 19-Mar-2014 17:53:53 GMT\","+
            "\"domain\":\".yahoo.com\","+
            "\"PH\":\"deleted\"}";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    @Test
    public void convertCookieListToString() {
        String cookieStr = 
                "PH=deleted;  "+
                " expires=Wed, 19-Mar-2014 17:53:53 GMT;"+
                "path=/;   "+
                "    domain=.yahoo.com;"+
                "thisWont=beIncluded;";
            String expectedCookieStr = 
                "{\"path\":\"/\","+
                "\"expires\":\"Wed, 19-Mar-2014 17:53:53 GMT\","+
                "\"domain\":\".yahoo.com\","+
                "\"thisWont\":\"beIncluded\","+
                "\"PH\":\"deleted\"}";
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
                "PH=deleted;  "+
                " expires=Wed,+19-Mar-2014+17:53:53+GMT;"+
                "path=/%2Bthis/is%26/a/spec%3Bsegment%3D;   "+
                "    domain=.yahoo.com;";
            String expectedCookieStr = 
                "{\"path\":\"/+this/is&/a/spec;segment=\","+
                "\"expires\":\"Wed, 19-Mar-2014 17:53:53 GMT\","+
                "\"domain\":\".yahoo.com\","+
                "\"PH\":\"deleted\"}";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        String cookieToStr = CookieList.toString(jsonObject);
        JSONObject finalJsonObject = CookieList.toJSONObject(cookieToStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }
    

}
