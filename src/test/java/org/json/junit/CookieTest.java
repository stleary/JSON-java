package org.json.junit;

/*
Public Domain.
*/

import static org.junit.jupiter.api.Assertions.*;

import org.json.*;
import org.junit.jupiter.api.Test;


/**
 * HTTP cookie specification: RFC6265
 * <p>
 * At its most basic, a cookie is a name=value pair. The value may be subdivided
 * into other cookies, but that is not tested here. The cookie may also include
 * certain named attributes, delimited by semicolons. 
 * <p>
 * The Cookie.toString() method emits certain attributes if present: expires,
 * domain, path, secure. All but secure are name-value pairs. Other attributes
 * are not included in the toString() output.
 * <p>
 * A JSON-Java encoded cookie escapes '+', '%', '=', ';' with %hh values.
 */
class CookieTest {

    /**
     * Attempts to create a JSONObject from a null string.
     * Expects a NullPointerException.
     */
    @Test
    void nullCookieException() {
        assertThrows(NullPointerException.class, () -> {
            String cookieStr = null;
            Cookie.toJSONObject(cookieStr);
        });
    }

    /**
     * Attempts to create a JSONObject from a cookie string with
     * no '=' char.
     * Expects a JSONException.
     */
    @Test
    void malFormedNameValueException() {
        String cookieStr = "thisCookieHasNoEqualsChar";
        try {
            Cookie.toJSONObject(cookieStr);
            fail("Expecting an exception");
        } catch (JSONException e) {
            assertEquals("Expected '=' and instead saw '' at 25 [character 26 line 1]",
                    e.getMessage(),
                    "Expecting an exception message");
        }
    }

    /**
     * Attempts to create a JSONObject from a cookie string
     * with embedded ';' char.
     * Expects a JSONException.
     */
    @Test
    void booleanAttribute() {
        String cookieStr = "this=Cookie;myAttribute";
            JSONObject jo = Cookie.toJSONObject(cookieStr);
            assertTrue(jo.has("name"), "has key 'name'");
            assertTrue(jo.has("value"), "has key 'value'");
            assertTrue(jo.has("myattribute"), "has key 'myAttribute'");
    }

    /**
     * Attempts to create a JSONObject from an empty cookie string.<br>
     * Note: Cookie throws an exception, but CookieList does not.<br>
     * Expects a JSONException
     */
    @Test
    void emptyStringCookieException() {
        String cookieStr = "";
        try {
            Cookie.toJSONObject(cookieStr);
            fail("Expecting an exception");
        } catch (JSONException e) {
            assertEquals("Cookies must have a 'name'",
                    e.getMessage(),
                    "Expecting an exception message");
        }
    }

    /**
     * 
     * Attempts to create a JSONObject from an cookie string where the name is blank.<br>
     * Note: Cookie throws an exception, but CookieList does not.<br>
     * Expects a JSONException
     */
    @Test
    void emptyNameCookieException() {
        String cookieStr = " = value ";
        try {
            Cookie.toJSONObject(cookieStr);
            fail("Expecting an exception");
        } catch (JSONException e) {
            assertEquals("Cookies must have a 'name'",
                    e.getMessage(),
                    "Expecting an exception message");
        }
    }

    /**
     * Cookie from a simple name/value pair with no delimiter
     */
    @Test
    void simpleCookie() {
        String cookieStr = "SID=31d4d96e407aad42";
        String expectedCookieStr = "{\"name\":\"SID\",\"value\":\"31d4d96e407aad42\"}";
        JSONObject jsonObject = Cookie.toJSONObject(cookieStr);
        JSONObject expectedJsonObject = new JSONObject(expectedCookieStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject,expectedJsonObject);
    }

    /**
     * Store a cookie with all of the supported attributes in a 
     * JSONObject. The secure attribute, which has no value, is treated
     * as a boolean.
     */
    @Test
    void multiPartCookie() {
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

    /**
     * Cookie.toString() will emit the non-standard "thiswont=beIncluded"
     * attribute, and the attribute is still stored in the JSONObject.
     * This test confirms both behaviors.
     */
    @Test
    void convertCookieToString() {
        String cookieStr = 
            "PH=deleted;  "+
            " expires=Wed, 19-Mar-2014 17:53:53 GMT;"+
            "path=/;   "+
            "    domain=.yahoo.com;"+
            "thisWont=beIncluded;"+
            "secure";
        String expectedCookieStr = 
            "{\"thiswont\":\"beIncluded\","+
            "\"path\":\"/\","+
            "\"expires\":\"Wed, 19-Mar-2014 17:53:53 GMT\","+
            "\"domain\":\".yahoo.com\","+
            "\"name\":\"PH\","+
            "\"secure\":true,"+
            "\"value\":\"deleted\"}";
        // Add the nonstandard attribute to the expected cookie string
        String expectedDirectCompareCookieStr = expectedCookieStr;
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

    /**
     * A string may be URL-encoded when converting to JSONObject.
     * If found, '+' is converted to ' ', and %hh hex strings are converted
     * to their ascii char equivalents. This test confirms the decoding
     * behavior. 
     */
    @Test
    void convertEncodedCookieToString() {
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

    /**
     * A public API method performs a URL encoding for selected chars
     * in a string. Control chars, '+', '%', '=', ';' are all encoded 
     * as %hh hex strings. The string is also trimmed.
     * This test confirms that behavior. 
     */
    @Test
    void escapeString() {
        String str = "   +%\r\n\t\b%=;;;   ";
        String expectedStr = "%2b%25%0d%0a%09%08%25%3d%3b%3b%3b";
        String actualStr = Cookie.escape(str);
        assertEquals(expectedStr, actualStr, "expect escape() to encode correctly. Actual: " + actualStr +
                " expected: " + expectedStr);
    }

    /**
     * A public API method performs URL decoding for strings.
     * '+' is converted to space and %hh hex strings are converted to
     * their ascii equivalent values. The string is not trimmed.
     * This test confirms that behavior. 
     */
    @Test
    void unescapeString() {
        String str = " +%2b%25%0d%0a%09%08%25%3d%3b%3b%3b+ ";
        String expectedStr = "  +%\r\n\t\b%=;;;  ";
        String actualStr = Cookie.unescape(str);
        assertEquals(expectedStr, actualStr, "expect unescape() to decode correctly. Actual: " + actualStr +
                " expected: " + expectedStr);
    }
}
