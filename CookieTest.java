package org.json.junit;

import java.util.*;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;


/**
 * Tests for JSON-Java Cookie.java
 * See RFC6265
 * At its most basic, a cookie is a name=value pair.
 * A JSON-Java encoded cookie escapes '+', '%', '=', ';' with %hh values.
 */
public class CookieTest {

    @Test(expected=NullPointerException.class)
    public void nullCookieException() {
        String cookieStr = null;
        Cookie.toJSONObject(cookieStr);
    }

    @Test(expected=JSONException.class)
    public void malFormedCookieException() {
        String cookieStr = "thisCookieHasNoEqualsChar";
        Cookie.toJSONObject(cookieStr);
    }

    @Test(expected=JSONException.class)
    public void emptyStringCookieException() {
        /**
         * Cookie throws an exception, but CookieList does not
         */
        String cookieStr = "";
        Cookie.toJSONObject(cookieStr);
    }

    @Test
    public void simpleCookie() {
        String cookieStr = "SID=31d4d96e407aad42";
        String expectedCookieStr = "{\"name\":\"SID\",\"value\":\"31d4d96e407aad42\"}";
        JSONObject jsonObject = Cookie.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    @Test
    public void multiPartCookie() {
        String cookieStr = 
            "PH=deleted;  "+
            " expires=Wed, 19-Mar-2014 17:53:53 GMT;"+
            "path=/;   "+
            "    domain=.yahoo.com;"+
            "secure";
        String expectedCookieStr = 
            "{\"path\":\"/\","+
            "\"expires\":\"Wed, 19-Mar-2014 17:53:53 GMT\","+
            "\"domain\":\".yahoo.com\","+
            "\"name\":\"PH\","+
            "\"secure\":true,"+
            "\"value\":\"deleted\"}";
        JSONObject jsonObject = Cookie.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    @Test
    public void convertCookieToString() {
        String cookieStr = 
                "PH=deleted;  "+
                " expires=Wed, 19-Mar-2014 17:53:53 GMT;"+
                "path=/;   "+
                "    domain=.yahoo.com;"+
                "thisWont=beIncluded;"+
                "secure";
            String expectedCookieStr = 
                "{\"path\":\"/\","+
                "\"expires\":\"Wed, 19-Mar-2014 17:53:53 GMT\","+
                "\"domain\":\".yahoo.com\","+
                "\"name\":\"PH\","+
                "\"secure\":true,"+
                "\"value\":\"deleted\"}";
            /**
             * ToString() will omit the non-standard segment, 
             * but it will still be stored in the JSONObject
             */
            String expectedDirectCompareCookieStr = 
                    expectedCookieStr.replaceAll("\\{", "\\{\"thisWont\":\"beIncluded\",");
        JSONObject jsonObject = Cookie.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        JSONObject expectedDirectCompareJsonObject = 
                new JSONObject(expectedDirectCompareCookieStr);
        String cookieToStr = Cookie.toString(jsonObject);
        JSONObject finalJsonObject = Cookie.toJSONObject(cookieToStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedDirectCompareJsonObject);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }

    @Test
    public void convertEncodedCookieToString() {
        String cookieStr = 
                "PH=deleted;  "+
                " expires=Wed,+19-Mar-2014+17:53:53+GMT;"+
                "path=/%2Bthis/is%26/a/spec%3Bsegment%3D;   "+
                "    domain=.yahoo.com;"+
                "secure";
            String expectedCookieStr = 
                "{\"path\":\"/+this/is&/a/spec;segment=\","+
                "\"expires\":\"Wed, 19-Mar-2014 17:53:53 GMT\","+
                "\"domain\":\".yahoo.com\","+
                "\"name\":\"PH\","+
                "\"secure\":true,"+
                "\"value\":\"deleted\"}";
        JSONObject jsonObject = Cookie.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        String cookieToStr = Cookie.toString(jsonObject);
        JSONObject finalJsonObject = Cookie.toJSONObject(cookieToStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }
    
}
