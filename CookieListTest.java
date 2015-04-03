package org.json.junit;

import java.util.*;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;


/**
 * Tests for JSON-Java CookieList.java
 * The main differences between Cookie and CookieList appears to be that 
 * CookieList does not treat the initial name/value pair different than
 * the other segments, and does not handle "secure".
 * Therefore the tests will be similar, but not identical.
 */
public class CookieListTest {
    @Test(expected=NullPointerException.class)
    public void nullCookieListException() {
        String cookieStr = null;
        CookieList.toJSONObject(cookieStr);
    }

    @Test(expected=JSONException.class)
    public void malFormedCookieListException() {
        String cookieStr = "thisCookieHasNoEqualsChar";
        CookieList.toJSONObject(cookieStr);
    }

    @Test(expected=JSONException.class)
    public void emptyStringCookieList() {
        /**
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
            "\"name\":\"PH\","+
            "\"value\":\"deleted\"}";
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
