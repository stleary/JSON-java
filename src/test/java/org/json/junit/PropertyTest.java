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

import java.util.*;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;


/**
 * Tests for JSON-Java Property
 */
public class PropertyTest {

    /**
     * JSONObject from null properties object should
     * result in an empty JSONObject.
     */
    @Test
    public void shouldHandleNullProperties() {
        Properties properties = null;
        JSONObject jsonObject = Property.toJSONObject(properties);
        assertTrue("jsonObject should be empty", jsonObject.isEmpty());
    }

    /**
     * JSONObject from empty properties object should
     * result in an empty JSONObject.
     */
    @Test
    public void shouldHandleEmptyProperties() {
        Properties properties = new Properties();
        JSONObject jsonObject = Property.toJSONObject(properties);
        assertTrue("jsonObject should be empty", jsonObject.isEmpty());
    }

    /**
     * JSONObject from simple properties object.
     */
    @Test
    public void shouldHandleProperties() {
        Properties properties = new Properties();
        
        properties.put("Illinois", "Springfield");
        properties.put("Missouri", "Jefferson City");
        properties.put("Washington", "Olympia");
        properties.put("California", "Sacramento");
        properties.put("Indiana", "Indianapolis");

        JSONObject jsonObject = Property.toJSONObject(properties);

        assertTrue("jsonObject should contain 5 items", jsonObject.length() == 5);
        assertTrue("jsonObject should contain Illinois property", 
                "Springfield".equals(jsonObject.get("Illinois")));
        assertTrue("jsonObject should contain Missouri property", 
                "Jefferson City".equals(jsonObject.get("Missouri")));
        assertTrue("jsonObject should contain Washington property", 
                "Olympia".equals(jsonObject.get("Washington")));
        assertTrue("jsonObject should contain California property", 
                "Sacramento".equals(jsonObject.get("California")));
        assertTrue("jsonObject should contain Indiana property", 
                "Indianapolis".equals(jsonObject.get("Indiana")));
    }

    /**
     * Null JSONObject toProperties() should result in an empty
     * Properties object.
     */
    @Test
    public void shouldHandleNullJSONProperty() {
        JSONObject jsonObject= null;
        Properties properties = Property.toProperties(jsonObject);
        assertTrue("properties should be empty", 
                properties.size() == 0);
    }

    /**
     * Properties should convert to JSONObject, and back to
     * Properties without changing.
     */
    @Test
    public void shouldHandleJSONProperty() {
        Properties properties = new Properties();
        
        properties.put("Illinois", "Springfield");
        properties.put("Missouri", "Jefferson City");
        properties.put("Washington", "Olympia");
        properties.put("California", "Sacramento");
        properties.put("Indiana", "Indianapolis");

        JSONObject jsonObject = Property.toJSONObject(properties);
        Properties jsonProperties = Property.toProperties(jsonObject);

        assertTrue("property objects should match", 
                properties.equals(jsonProperties));
    }
}