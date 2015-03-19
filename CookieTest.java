package org.json.junit;

import java.util.*;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;


/**
 * Tests for JSON-Java Cookie.java
 * Paraphrased from: 
 * http://www.nczonline.net/blog/2009/05/05/http-cookies-explained/
 * 
 * A web server specifies a cookie to be stored by sending an HTTP header
 * called Set-Cookie. The format of the Set-Cookie header is a string as 
 * follows (parts in square brackets are optional):
 * Set-Cookie: value[; expires=date][; domain=domain][; path=path][; secure]
 * Where value is usually, but not always, a key/value pair: name=value
 * Separators between the optional segments (e.g. expires=date) consist of a
 * semicolon followed by a space.
 * 
 * Although cookies are typically url-encoded, they don't have to be.
 * 
 * expires date example:
 * Set-Cookie: name=Nicholas; expires=Sat, 02 May 2009 23:38:25 GMT
 * 
 * domain option example: 
 * Set-Cookie: name=Nicholas; domain=nczonline.net
 * 
 * Path option example:
 * Set-Cookie: name=Nicholas; path=/blog
 * 
 * Secure option example (it is just a flag):
 * Set-Cookie: name=Nicholas; secure
 * 
 * Subcookies. There is a hard limit of size (4k) that can't be finessed.
 * But many browsers (not Chrome) have a  max cookies per domain limit
 * (usually 50). To get around this, subcookies are encoded in the initial
 * name/value pair as follows: 
 * name=a=b&c=d&e=f&g=h
 */
public class CookieTest {

    String simpleCookieStr = 
            "PH=deleted"+
            "; expires=Wed, 19-Mar-2014 17:53:53 GMT"+
            ";path=/"+
            "; domain=.yahoo.com"+
            ";secure"+
            ";not=included";

    String encodedCookieStr = 
            "PH=contains+some+chars"+
            ";expires=Wed, 19-Mar-2014 17:53:53 GMT"+
            "; path=/"+
            ";domain=.yahoo.com?some+escape+chars"+
            "; secure"+
            "; CRZY=%7B%2233748770511_20150319%22%3A%7B%22expires%22%3A142696041"+
            "3419%2C%22data%22%3A%7B%22nv%22%3A3%2C%22bn%22%3A0%2C%22collapsed%2"+
            "2%3A0%7D%7D%7D";

    @Test(expected=NullPointerException.class)
    public void shouldHandleNullCookie() {
        String cookieStr = null;
        Cookie.toJSONObject(cookieStr);
    }

    @Test(expected=JSONException.class)
    public void shouldHandleEmptyStringCookie() {
        String cookieStr = "";
        Cookie.toJSONObject(cookieStr);
    }

    @Test
    public void shouldHandleNonEncodedCookie() {
        JSONObject jsonObject = Cookie.toJSONObject(simpleCookieStr);
        Set<String> keySet = jsonObject.keySet();
        assertTrue("Keyset should have exactly 7 keys", keySet.size() == 7);
        assertTrue("name should have expected value",
                "PH".equals(jsonObject.getString("name")));
        assertTrue("Value should have expected value", 
                "deleted".equals(jsonObject.getString("value")));
        assertTrue("expires should have expected value", 
                "Wed, 19-Mar-2014 17:53:53 GMT".equals(
                        jsonObject.getString("expires")));
        assertTrue("domain should have expected value", 
                ".yahoo.com".equals(
                        jsonObject.getString("domain")));
        assertTrue("path should have expected value", 
                "/".equals(
                        jsonObject.getString("path")));
        assertTrue("not should have expected value", 
                "included".equals(
                        jsonObject.getString("not")));
        Boolean secureBool = jsonObject.getBoolean("secure");
        assertTrue("secure should be found in jsonObject", secureBool != null);
        assertTrue("secure should have expected value", 
                secureBool.equals(true));
    }

    @Test
    public void shouldConvertNonEncodedCookieToString() {
        int idx;
        String expectedStr;
        JSONObject jsonObject = Cookie.toJSONObject(simpleCookieStr);
        String cookieStr = Cookie.toString(jsonObject);

        // check for unordered expected output
        expectedStr = "path=/";
        idx = cookieStr.indexOf(expectedStr);
        assertTrue("path should be included in string output", idx != -1);
        cookieStr = cookieStr.substring(0, idx)+
                cookieStr.substring(idx+expectedStr.length());

        expectedStr = "expires=Wed, 19-Mar-2014 17:53:53 GMT";
        idx = cookieStr.indexOf(expectedStr);
        assertTrue("expires should be included in string output", idx != -1);
        cookieStr = cookieStr.substring(0, idx)+
                cookieStr.substring(idx+expectedStr.length());
        
        expectedStr = "domain=.yahoo.com";
        idx = cookieStr.indexOf(expectedStr);
        assertTrue("domain should be included in string output", idx != -1);
        cookieStr = cookieStr.substring(0, idx)+
                cookieStr.substring(idx+expectedStr.length());

        expectedStr = "PH=deleted";
        idx = cookieStr.indexOf(expectedStr);
        assertTrue("name/value should be included in string output", idx != -1);
        cookieStr = cookieStr.substring(0, idx)+
                cookieStr.substring(idx+expectedStr.length());

        expectedStr = "secure";
        idx = cookieStr.indexOf(expectedStr);
        assertTrue("secure should be included in string output", idx != -1);
        cookieStr = cookieStr.substring(0, idx)+
                cookieStr.substring(idx+expectedStr.length());

        // after semicolons, nothing should be left
        cookieStr = cookieStr.replaceAll(";", "");
        assertTrue("nothing else should remain in cookie toString()",
              cookieStr.length() == 0);
    }

    @Test
    public void shouldHandleEncodedCookie() {
        JSONObject jsonObject = Cookie.toJSONObject(encodedCookieStr);
        Set<String> keySet = jsonObject.keySet();
        // Note: the 7th key/value is not used by Cookie.java
        assertTrue("Keyset should have exactly 7 keys", keySet.size() == 7);
        assertTrue("name should have expected value",
                "PH".equals(jsonObject.getString("name")));
        assertTrue("Value should have expected value", 
                "contains+some+chars".equals(jsonObject.getString("value")));
        assertTrue("expires should have expected value", 
                "Wed, 19-Mar-2014 17:53:53 GMT".equals(
                        jsonObject.getString("expires")));
        assertTrue("domain should have expected value", 
                ".yahoo.com?some escape chars".equals(
                        jsonObject.getString("domain")));
        assertTrue("path should have expected value", 
                "/".equals(
                        jsonObject.getString("path")));
        Boolean secureBool = jsonObject.getBoolean("secure");
        assertTrue("secure should be found in jsonObject", secureBool != null);
        assertTrue("secure should have expected value", 
                secureBool.equals(true));
        String expectedStr = "{\"33748770511_20150319\":{\"expires\":14269604134"+
                "19,\"data\":{\"nv\":3,\"bn\":0,\"collapsed\":0}}}";
        assertTrue("CRZY should have expected value", 
                expectedStr.equals(jsonObject.getString("CRZY")));
    }

    @Test
    public void shouldConvertEncodedCookieToString() {
        int idx;
        String expectedStr;
        JSONObject jsonObject = Cookie.toJSONObject(encodedCookieStr);
        String cookieStr = Cookie.toString(jsonObject);

        // check for unordered expected output
        expectedStr = "path=/";
        idx = cookieStr.indexOf(expectedStr);
        assertTrue("path should be included in string output", idx != -1);
        cookieStr = cookieStr.substring(0, idx)+
                cookieStr.substring(idx+expectedStr.length());

        expectedStr = "expires=Wed, 19-Mar-2014 17:53:53 GMT";
        idx = cookieStr.indexOf(expectedStr);
        assertTrue("expires should be included in string output", idx != -1);
        cookieStr = cookieStr.substring(0, idx)+
                cookieStr.substring(idx+expectedStr.length());
        
        expectedStr = "domain=.yahoo.com?some escape chars";
        idx = cookieStr.indexOf(expectedStr);
        assertTrue("domain should be included in string output", idx != -1);
        cookieStr = cookieStr.substring(0, idx)+
                cookieStr.substring(idx+expectedStr.length());

        expectedStr = "PH=contains%2bsome%2bchars";
        idx = cookieStr.indexOf(expectedStr);
        assertTrue("name/value should be included in string output", idx != -1);
        cookieStr = cookieStr.substring(0, idx)+
                cookieStr.substring(idx+expectedStr.length());

        expectedStr = "secure";
        idx = cookieStr.indexOf(expectedStr);
        assertTrue("secure should be included in string output", idx != -1);
        cookieStr = cookieStr.substring(0, idx)+
                cookieStr.substring(idx+expectedStr.length());

        // after semicolons, nothing should be left
        cookieStr = cookieStr.replaceAll(";", "");
        assertTrue("nothing else should remain in cookie toString()",
              cookieStr.length() == 0);
    }
    
}
