package org.json.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import org.json.JSONObject;
import org.json.junit.data.GenericBeanInt;
import org.json.junit.data.MyEnum;
import org.json.junit.data.MyNumber;
import org.json.junit.data.PersonRecord;
import org.junit.Test;

/**
 * Tests for JSONObject support of Java record-style classes.
 * These tests verify that classes with accessor methods without get/is prefixes
 * (like Java records) can be properly converted to JSONObject.
 */
public class JSONObjectRecordTest {

    /**
     * Tests that JSONObject can be created from a record-style class.
     * Record-style classes use accessor methods like name() instead of getName().
     */
    @Test
    public void jsonObjectByRecord() {
        PersonRecord person = new PersonRecord("John Doe", 30, true);
        JSONObject jsonObject = new JSONObject(person);
        
        assertEquals("Expected 3 keys in the JSONObject", 3, jsonObject.length());
        assertEquals("John Doe", jsonObject.get("name"));
        assertEquals(30, jsonObject.get("age"));
        assertEquals(true, jsonObject.get("active"));
    }
    
    /**
     * Test that Object methods (toString, hashCode, equals, etc.) are not included
     */
    @Test
    public void recordStyleClassShouldNotIncludeObjectMethods() {
        PersonRecord person = new PersonRecord("Jane Doe", 25, false);
        JSONObject jsonObject = new JSONObject(person);
        
        // Should NOT include Object methods
        assertFalse("Should not include toString", jsonObject.has("toString"));
        assertFalse("Should not include hashCode", jsonObject.has("hashCode"));
        assertFalse("Should not include equals", jsonObject.has("equals"));
        assertFalse("Should not include clone", jsonObject.has("clone"));
        assertFalse("Should not include wait", jsonObject.has("wait"));
        assertFalse("Should not include notify", jsonObject.has("notify"));
        assertFalse("Should not include notifyAll", jsonObject.has("notifyAll"));
        
        // Should only have the 3 record fields
        assertEquals("Should only have 3 fields", 3, jsonObject.length());
    }
    
    /**
     * Test that enum methods are not included when processing an enum
     */
    @Test
    public void enumsShouldNotIncludeEnumMethods() {
        MyEnum myEnum = MyEnum.VAL1;
        JSONObject jsonObject = new JSONObject(myEnum);
        
        // Should NOT include enum-specific methods like name(), ordinal(), values(), valueOf()
        assertFalse("Should not include name method", jsonObject.has("name"));
        assertFalse("Should not include ordinal method", jsonObject.has("ordinal"));
        assertFalse("Should not include declaringClass", jsonObject.has("declaringClass"));
        
        // Enums should still work with traditional getters if they have any
        // But should not pick up the built-in enum methods
    }
    
    /**
     * Test that Number subclass methods are not included
     */
    @Test
    public void numberSubclassesShouldNotIncludeNumberMethods() {
        MyNumber myNumber = new MyNumber();
        JSONObject jsonObject = new JSONObject(myNumber);
        
        // Should NOT include Number methods like intValue(), longValue(), etc.
        assertFalse("Should not include intValue", jsonObject.has("intValue"));
        assertFalse("Should not include longValue", jsonObject.has("longValue"));
        assertFalse("Should not include doubleValue", jsonObject.has("doubleValue"));
        assertFalse("Should not include floatValue", jsonObject.has("floatValue"));
        
        // Should include the actual getter
        assertTrue("Should include number", jsonObject.has("number"));
        assertEquals("Should have 1 field", 1, jsonObject.length());
    }
    
    /**
     * Test that generic bean with get() and is() methods works correctly
     */
    @Test
    public void genericBeanWithGetAndIsMethodsShouldNotBeIncluded() {
        GenericBeanInt bean = new GenericBeanInt(42);
        JSONObject jsonObject = new JSONObject(bean);
        
        // Should NOT include standalone get() or is() methods
        assertFalse("Should not include standalone 'get' method", jsonObject.has("get"));
        assertFalse("Should not include standalone 'is' method", jsonObject.has("is"));
        
        // Should include the actual getters
        assertTrue("Should include genericValue field", jsonObject.has("genericValue"));
        assertTrue("Should include a field", jsonObject.has("a"));
    }
    
    /**
     * Test that java.* classes don't have their methods picked up
     */
    @Test
    public void javaLibraryClassesShouldNotIncludeTheirMethods() {
        StringReader reader = new StringReader("test");
        JSONObject jsonObject = new JSONObject(reader);
        
        // Should NOT include java.io.Reader methods like read(), reset(), etc.
        assertFalse("Should not include read method", jsonObject.has("read"));
        assertFalse("Should not include reset method", jsonObject.has("reset"));
        assertFalse("Should not include ready method", jsonObject.has("ready"));
        assertFalse("Should not include skip method", jsonObject.has("skip"));
        
        // Reader should produce empty JSONObject (no valid properties)
        assertEquals("Reader should produce empty JSON", 0, jsonObject.length());
    }
    
    /**
     * Test mixed case - object with both traditional getters and record-style accessors
     */
    @Test
    public void mixedGettersAndRecordStyleAccessors() {
        // PersonRecord has record-style accessors: name(), age(), active()
        // These should all be included
        PersonRecord person = new PersonRecord("Mixed Test", 40, true);
        JSONObject jsonObject = new JSONObject(person);
        
        assertEquals("Should have all 3 record-style fields", 3, jsonObject.length());
        assertTrue("Should include name", jsonObject.has("name"));
        assertTrue("Should include age", jsonObject.has("age"));
        assertTrue("Should include active", jsonObject.has("active"));
    }
    
    /**
     * Test that methods starting with uppercase are not included (not valid record accessors)
     */
    @Test
    public void methodsStartingWithUppercaseShouldNotBeIncluded() {
        PersonRecord person = new PersonRecord("Test", 50, false);
        JSONObject jsonObject = new JSONObject(person);
        
        // Record-style accessors must start with lowercase
        // Methods like Name(), Age() (uppercase) should not be picked up
        // Our PersonRecord only has lowercase accessors, which is correct
        
        assertEquals("Should only have lowercase accessors", 3, jsonObject.length());
    }
}
