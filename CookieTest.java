package org.json.junit;



import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;


/**
 * HTTP cookie specification: RFC6265
 * 
 * At its most basic, a cookie is a name=value pair. The value may be subdivided
 * into other cookies, but that is not tested here. The cookie may also include
 * certain named attributes, delimited by semicolons. 
 * 
 * The Cookie.toString() method emits certain attributes if present: expires,
 * domain, path, secure. All but secure are name-value pairs. Other attributes
 * are not included in the toString() output.
 *   
 * A JSON-Java encoded cookie escapes '+', '%', '=', ';' with %hh values.
 */
public class CookieTest {

    @Test(expected=NullPointerException.class)
    public void nullCookieException() {
        /**
         * Attempts to create a JSONObject from a null string
         */
        String cookieStr = null;
        Cookie.toJSONObject(cookieStr);
    }

    @Test(expected=JSONException.class)
    public void malFormedNameValueException() {
        /**
         * Attempts to create a JSONObject from a malformed cookie string
         */
        String cookieStr = "thisCookieHasNoEqualsChar";
        Cookie.toJSONObject(cookieStr);
    }

    @Test(expected=JSONException.class)
    public void malFormedAttributeException() {
        /**
         * Attempts to create a JSONObject from a malformed cookie string
         */
        String cookieStr = "this=Cookie;myAttribute";
        Cookie.toJSONObject(cookieStr);
    }

    @Test(expected=JSONException.class)
    public void emptyStringCookieException() {
        /**
         * Attempts to create a JSONObject from an empty cookie string
         * Note: Cookie throws an exception, but CookieList does not
         */
        String cookieStr = "";
        Cookie.toJSONObject(cookieStr);
    }

    @Test
    public void simpleCookie() {
        /**
         * The simplest cookie is a name/value pair with no delimiter
         */
        String cookieStr = "SID=31d4d96e407aad42";
        String expectedCookieStr = "{\"name\":\"SID\",\"value\":\"31d4d96e407aad42\"}";
        JSONObject jsonObject = Cookie.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    @Test
    public void multiPartCookie() {
        /**
         * Store a cookie with all of the supported attributes in a 
         * JSONObject. The secure attribute, which has no value, is treated
         * as a boolean.
         */
        String cookieStr = 
            "PH=deleted;  "+
            " expires=Wed, 19-Mar-2014 17:53:53 GMT;"+
            "path=/;   "+
            "    domain=.yahoo.com;"+
            "secure";
        String expectedCookieStr = 
            "{"+
                "\"name\":\"PH\","+
                "\"value\":\"deleted\","+
                "\"path\":\"/\","+
                "\"expires\":\"Wed, 19-Mar-2014 17:53:53 GMT\","+
                "\"domain\":\".yahoo.com\","+
                "\"secure\":true"+
            "}";
        JSONObject jsonObject = Cookie.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    @Test
    public void convertCookieToString() {
        /**
         * ToString() will omit the non-standard "thiswont=beIncluded"
         * attribute, but the attribute is still stored in the JSONObject.
         * This test confirms both behaviors.
         */
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
        // Add the nonstandard attribute to the expected cookie string
        String expectedDirectCompareCookieStr = 
            expectedCookieStr.replaceAll("\\{", "\\{\"thisWont\":\"beIncluded\",");
        // convert all strings into JSONObjects
        JSONObject jsonObject = Cookie.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        JSONObject expectedDirectCompareJsonObject = 
                new JSONObject(expectedDirectCompareCookieStr);
        // emit the string
        String cookieToStr = Cookie.toString(jsonObject);
        // create a final JSONObject from the string
        JSONObject finalJsonObject = Cookie.toJSONObject(cookieToStr);
        // JSONObject should contain the nonstandard string
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedDirectCompareJsonObject);
        // JSONObject -> string -> JSONObject should not contain the nonstandard string
        Util.compareActualVsExpectedJsonObjects(finalJsonObject,expectedJsonObject);
    }

    @Test
    public void convertEncodedCookieToString() {
        /**
         * A string may be URL-encoded when converting to JSONObject.
         * If found, '+' is converted to ' ', and %hh hex strings are converted
         * to their ascii char equivalents. This test confirms the decoding
         * behavior. 
         */
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
    
    @Test
    public void escapeString() {
        /**
         * A public API method performs a URL encoding for selected chars
         * in a string. Control chars, '+', '%', '=', ';' are all encoded 
         * as %hh hex strings. The string is also trimmed.
         * This test confirms that behavior. 
         */
        String str = "   +%\r\n\t\b%=;;;   ";
        String expectedStr = "%2b%25%0d%0a%09%08%25%3d%3b%3b%3b";
        String actualStr = Cookie.escape(str);
        assertTrue("expect escape() to encode correctly. Actual: " +actualStr+
                " expected: " +expectedStr, expectedStr.equals(actualStr));
    }

    @Test
    public void unescapeString() {
        /**
         * A public API method performs URL decoding for strings.
         * '+' is converted to space and %hh hex strings are converted to
         * their ascii equivalent values. The string is not trimmed.
         * This test confirms that behavior. 
         */
        String str = " +%2b%25%0d%0a%09%08%25%3d%3b%3b%3b+ ";
        String expectedStr = "  +%\r\n\t\b%=;;;  ";
        String actualStr = Cookie.unescape(str);
        assertTrue("expect unescape() to decode correctly. Actual: " +actualStr+
                " expected: " +expectedStr, expectedStr.equals(actualStr));
    }
}
