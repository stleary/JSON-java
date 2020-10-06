package org.json.junit;

/*
Copyright (c) 2020 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import static org.junit.Assert.*;

import java.util.*;

import org.json.*;
import org.junit.Test;

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
            fail("should throw an exception");
        } catch (JSONException e) {
            /**
             * Not sure of the missing char, but full string compare fails 
             */
            assertEquals("Expecting an exception message",
                    "Expected '=' and instead saw '' at 25 [character 26 line 1]",
                    e.getMessage());
        }
    }

    /**
     * Creates a CookieList from an empty string.
     */
    @Test
    public void emptyStringCookieList() {
        String cookieStr = "";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        assertTrue(jsonObject.isEmpty());
    }

    /**
     * CookieList with the simplest cookie - a name/value pair with no delimiter.
     */
    @Test
    public void simpleCookieList() {
        String cookieStr = "SID=31d4d96e407aad42";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("Expected 1 top level item", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 1);
        assertTrue("expected 31d4d96e407aad42", "31d4d96e407aad42".equals(jsonObject.query("/SID")));
    }

    /**
     * CookieList with a single a cookie which has a name/value pair and delimiter.
     */
    @Test
    public void simpleCookieListWithDelimiter() {
        String cookieStr = "SID=31d4d96e407aad42;";
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("Expected 1 top level item", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 1);
        assertTrue("expected 31d4d96e407aad42", "31d4d96e407aad42".equals(jsonObject.query("/SID")));
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
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("Expected 6 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 6);
        assertTrue("expected myCookieValue1", "myCookieValue1".equals(jsonObject.query("/name1")));
        assertTrue("expected myCookieValue2", "myCookieValue2".equals(jsonObject.query("/name2")));
        assertTrue("expected myCookieValue3", "myCookieValue3".equals(jsonObject.query("/name3")));
        assertTrue("expected myCookieValue4", "myCookieValue4".equals(jsonObject.query("/name4")));
        assertTrue("expected myCookieValue5", "myCookieValue5".equals(jsonObject.query("/name5")));
        assertTrue("expected myCookieValue6", "myCookieValue6".equals(jsonObject.query("/name6")));
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
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        // exercise CookieList.toString()
        String cookieListString = CookieList.toString(jsonObject);
        // have to convert it back for validation
        jsonObject = CookieList.toJSONObject(cookieListString);

        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("Expected 6 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 6);
        assertTrue("expected myCookieValue1", "myCookieValue1".equals(jsonObject.query("/name1")));
        assertTrue("expected myCookieValue2", "myCookieValue2".equals(jsonObject.query("/name2")));
        assertTrue("expected myCookieValue3", "myCookieValue3".equals(jsonObject.query("/name3")));
        assertTrue("expected myCookieValue4", "myCookieValue4".equals(jsonObject.query("/name4")));
        assertTrue("expected myCookieValue5", "myCookieValue5".equals(jsonObject.query("/name5")));
        assertTrue("expected myCookieValue6", "myCookieValue6".equals(jsonObject.query("/name6")));
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
        JSONObject jsonObject = CookieList.toJSONObject(cookieStr);
        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("Expected 6 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 6);
        assertTrue("expected myCookieValue1", "myCookieValue1".equals(jsonObject.query("/name1")));
        assertTrue("expected my Cookie Value 2", "my Cookie Value 2".equals(jsonObject.query("/name2")));
        assertTrue("expected my+Cookie&Value;3=", "my+Cookie&Value;3=".equals(jsonObject.query("/name3")));
        assertTrue("expected my%CookieValue4", "my%CookieValue4".equals(jsonObject.query("/name4")));
        assertTrue("expected my%CookieValue5", "myCookieValue5".equals(jsonObject.query("/name5")));
        assertTrue("expected myCookieValue6", "myCookieValue6".equals(jsonObject.query("/name6")));
    }
}
