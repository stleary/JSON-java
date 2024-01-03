package org.json.junit;

/*
Public Domain.
*/

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.json.*;
import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.*;

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
class CookieListTest {

    /**
     * Attempts to create a CookieList from a null string.
     * Expects a NullPointerException.
     */
    @Test
    void nullCookieListException() {
        assertThrows(NullPointerException.class, () -> {
            String cookieStr = null;
            CookieList.toJSONObject(cookieStr);
        });
    }

    /**
     * Attempts to create a CookieList from a malformed string.
     * Expects a JSONException.
     */
    @Test
    void malFormedCookieListException() {
        String cookieStr = "thisCookieHasNoEqualsChar";
        try {
            CookieList.toJSONObject(cookieStr);
            fail("should throw an exception");
        } catch (JSONException e) {
            /**
             * Not sure of the missing char, but full string compare fails 
             */
            assertEquals("Expected '=' and instead saw '' at 25 [character 26 line 1]",
                    e.getMessage(),
                    "Expecting an exception message");
        }
    }

    /**
     * Creates a CookieList from an empty string.
     */
    @Test
    void emptyStringCookieList() {
        String cookieStr = "";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        assertTrue(jsonObject.isEmpty());
    }

    /**
     * CookieList with the simplest cookie - a name/value pair with no delimiter.
     */
    @Test
    void simpleCookieList() {
        String cookieStr = "SID=31d4d96e407aad42";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertEquals(1, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "Expected 1 top level item");
        assertEquals("31d4d96e407aad42", jsonObject.query("/SID"), "expected 31d4d96e407aad42");
    }

    /**
     * CookieList with a single a cookie which has a name/value pair and delimiter.
     */
    @Test
    void simpleCookieListWithDelimiter() {
        String cookieStr = "SID=31d4d96e407aad42;";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertEquals(1, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "Expected 1 top level item");
        assertEquals("31d4d96e407aad42", jsonObject.query("/SID"), "expected 31d4d96e407aad42");
    }

    /**
     * CookieList with multiple cookies consisting of name/value pairs
     * with delimiters.
     */
    @Test
    void multiPartCookieList() {
        String cookieStr = 
            "name1=myCookieValue1;  "+
            "  name2=myCookieValue2;"+
            "name3=myCookieValue3;"+
            "  name4=myCookieValue4;  "+
            "name5=myCookieValue5;"+
            "  name6=myCookieValue6;";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertEquals(6, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "Expected 6 top level items");
        assertEquals("myCookieValue1", jsonObject.query("/name1"), "expected myCookieValue1");
        assertEquals("myCookieValue2", jsonObject.query("/name2"), "expected myCookieValue2");
        assertEquals("myCookieValue3", jsonObject.query("/name3"), "expected myCookieValue3");
        assertEquals("myCookieValue4", jsonObject.query("/name4"), "expected myCookieValue4");
        assertEquals("myCookieValue5", jsonObject.query("/name5"), "expected myCookieValue5");
        assertEquals("myCookieValue6", jsonObject.query("/name6"), "expected myCookieValue6");
    }

    /**
     * CookieList from a JSONObject with valid key and null value
     */
    @Test
    void convertCookieListWithNullValueToString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key",  JSONObject.NULL);
        String cookieToStr = CookieList.toString(jsonObject);
        assertEquals("", cookieToStr, "toString() should be empty");
    }

    /**
     * CookieList with multiple entries converted to a JSON document. 
     */
    @Test
    void convertCookieListToString() {
        String cookieStr = 
                "name1=myCookieValue1;  "+
                "  name2=myCookieValue2;"+
                "name3=myCookieValue3;"+
                "  name4=myCookieValue4;  "+
                "name5=myCookieValue5;"+
                "  name6=myCookieValue6;";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        // exercise CookieList.toString()
        String cookieListString = CookieList.toString(jsonObject);
        // have to convert it back for validation
        jsonObject = CookieList.toJSONObject(cookieListString);

        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertEquals(6, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "Expected 6 top level items");
        assertEquals("myCookieValue1", jsonObject.query("/name1"), "expected myCookieValue1");
        assertEquals("myCookieValue2", jsonObject.query("/name2"), "expected myCookieValue2");
        assertEquals("myCookieValue3", jsonObject.query("/name3"), "expected myCookieValue3");
        assertEquals("myCookieValue4", jsonObject.query("/name4"), "expected myCookieValue4");
        assertEquals("myCookieValue5", jsonObject.query("/name5"), "expected myCookieValue5");
        assertEquals("myCookieValue6", jsonObject.query("/name6"), "expected myCookieValue6");
    }

    /**
     * CookieList with multiple entries and some '+' chars and URL-encoded
     * values converted to a JSON document. 
     */
    @Test
    void convertEncodedCookieListToString() {
        String cookieStr = 
                "name1=myCookieValue1;  "+
                "  name2=my+Cookie+Value+2;"+
                "name3=my%2BCookie%26Value%3B3%3D;"+
                "  name4=my%25CookieValue4;  "+
                "name5=myCookieValue5;"+
                "  name6=myCookieValue6;";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertEquals(6, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "Expected 6 top level items");
        assertEquals("myCookieValue1", jsonObject.query("/name1"), "expected myCookieValue1");
        assertEquals("my Cookie Value 2", jsonObject.query("/name2"), "expected my Cookie Value 2");
        assertEquals("my+Cookie&Value;3=", jsonObject.query("/name3"), "expected my+Cookie&Value;3=");
        assertEquals("my%CookieValue4", jsonObject.query("/name4"), "expected my%CookieValue4");
        assertEquals("myCookieValue5", jsonObject.query("/name5"), "expected my%CookieValue5");
        assertEquals("myCookieValue6", jsonObject.query("/name6"), "expected myCookieValue6");
    }
}
