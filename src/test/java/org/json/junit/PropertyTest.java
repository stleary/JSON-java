package org.json.junit;

/*
Public Domain.
*/

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.*;
import org.junit.jupiter.api.Test;


/**
 * Tests for JSON-Java Property
 */
class PropertyTest {

    /**
     * JSONObject from null properties object should
     * result in an empty JSONObject.
     */
    @Test
    void shouldHandleNullProperties() {
        Properties properties = null;
        JSONObject jsonObject = Property.toJSONObject(properties);
        assertTrue(jsonObject.isEmpty(), "jsonObject should be empty");
    }

    /**
     * JSONObject from empty properties object should
     * result in an empty JSONObject.
     */
    @Test
    void shouldHandleEmptyProperties() {
        Properties properties = new Properties();
        JSONObject jsonObject = Property.toJSONObject(properties);
        assertTrue(jsonObject.isEmpty(), "jsonObject should be empty");
    }

    /**
     * JSONObject from simple properties object.
     */
    @Test
    void shouldHandleProperties() {
        Properties properties = new Properties();
        
        properties.put("Illinois", "Springfield");
        properties.put("Missouri", "Jefferson City");
        properties.put("Washington", "Olympia");
        properties.put("California", "Sacramento");
        properties.put("Indiana", "Indianapolis");

        JSONObject jsonObject = Property.toJSONObject(properties);

        assertEquals(5, jsonObject.length(), "jsonObject should contain 5 items");
        assertEquals("Springfield", jsonObject.get("Illinois"), "jsonObject should contain Illinois property");
        assertEquals("Jefferson City", jsonObject.get("Missouri"), "jsonObject should contain Missouri property");
        assertEquals("Olympia", jsonObject.get("Washington"), "jsonObject should contain Washington property");
        assertEquals("Sacramento", jsonObject.get("California"), "jsonObject should contain California property");
        assertEquals("Indianapolis", jsonObject.get("Indiana"), "jsonObject should contain Indiana property");
    }

    /**
     * Null JSONObject toProperties() should result in an empty
     * Properties object.
     */
    @Test
    void shouldHandleNullJSONProperty() {
        JSONObject jsonObject= null;
        Properties properties = Property.toProperties(jsonObject);
        assertEquals(0, properties.size(), "properties should be empty");
    }

    /**
     * Properties should convert to JSONObject, and back to
     * Properties without changing.
     */
    @Test
    void shouldHandleJSONProperty() {
        Properties properties = new Properties();
        
        properties.put("Illinois", "Springfield");
        properties.put("Missouri", "Jefferson City");
        properties.put("Washington", "Olympia");
        properties.put("California", "Sacramento");
        properties.put("Indiana", "Indianapolis");

        JSONObject jsonObject = Property.toJSONObject(properties);
        Properties jsonProperties = Property.toProperties(jsonObject);

        assertEquals(properties, jsonProperties, "property objects should match");
    }
}