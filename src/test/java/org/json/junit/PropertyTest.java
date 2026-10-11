package org.json.junit;

/*
Public Domain.
*/

import java.util.*;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;


/**
 * Tests for JSON-Java Property
 */
public class PropertyTest {

    @Test
    public void shouldIncludeInheritedPropertiesWithoutLocalEntries() {
        Properties defaults = new Properties();
        defaults.setProperty("timeout", "30");
        Properties properties = new Properties(new Properties(defaults));

        JSONObject jsonObject = Property.toJSONObject(properties);

        assertEquals(1, jsonObject.length());
        assertEquals("30", jsonObject.getString("timeout"));
    }

    @Test
    public void shouldPreferLocalPropertiesOverDefaults() {
        Properties defaults = new Properties();
        defaults.setProperty("timeout", "30");
        defaults.setProperty("host", "localhost");
        Properties properties = new Properties(defaults);
        properties.setProperty("timeout", "60");

        JSONObject jsonObject = Property.toJSONObject(properties);

        assertEquals(2, jsonObject.length());
        assertEquals("60", jsonObject.getString("timeout"));
        assertEquals("localhost", jsonObject.getString("host"));
    }

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
