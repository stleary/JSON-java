package org.json.junit;

/*
Public Domain.
*/

import static org.junit.jupiter.api.Assertions.*;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.junit.data.MyEnum;
import org.json.junit.data.MyEnumClass;
import org.json.junit.data.MyEnumField;
import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

/**
 * Enums are not explicitly supported in JSON-Java. But because enums act like
 * classes, all required behavior is already be present in some form. 
 * These tests explore how enum serialization works with JSON-Java.
 */
class EnumTest {

    /**
     * To serialize an enum by its getters, use the JSONObject Object constructor.
     * The JSONObject ctor handles enum like any other bean. A JSONobject 
     * is created whose entries are the getter name/value pairs.
     */
    @Test
    void jsonObjectFromEnum() {
        // If there are no getters then the object is empty.
        MyEnum myEnum = MyEnum.VAL2;
        JSONObject jsonObject = new JSONObject(myEnum);
        assertTrue(jsonObject.isEmpty(), "simple enum has no getters");

         // enum with a getters should create a non-empty object 
        MyEnumField myEnumField = MyEnumField.VAL2;
        jsonObject = new JSONObject(myEnumField);

        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider()
                .parse(jsonObject.toString());
        assertEquals(2, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "expecting 2 items in top level object");
        assertEquals("val 2", jsonObject.query("/value"), "expecting val 2");
        assertEquals(Integer.valueOf(2), jsonObject.query("/intVal"), "expecting 2");

        /**
         * class which contains enum instances. Each enum should be stored
         * in its own JSONObject
         */
        MyEnumClass myEnumClass = new MyEnumClass();
        myEnumClass.setMyEnum(MyEnum.VAL1);
        myEnumClass.setMyEnumField(MyEnumField.VAL3);
        jsonObject = new JSONObject(myEnumClass);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertEquals(2, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "expected 2 top level items");
        assertEquals("VAL3", (JsonPath.read(doc, "$.myEnumField")), "expected 2 myEnumField items");
        assertEquals("VAL1", (JsonPath.read(doc, "$.myEnum")), "expected 0 myEnum items");

        assertEquals(MyEnumField.VAL3, jsonObject.query("/myEnumField"), "expecting MyEnumField.VAL3");
        assertEquals(MyEnum.VAL1, jsonObject.query("/myEnum"), "expecting MyEnum.VAL1");
    }

    /**
     * To serialize an enum by its set of allowed values, use getNames()
     * and the JSONObject Object with names constructor.
     */
    @Test
    void jsonObjectFromEnumWithNames() {
        String [] names;
        JSONObject jsonObject;
 
        MyEnum myEnum = MyEnum.VAL1;
        names = JSONObject.getNames(myEnum);
        // The values will be MyEnum fields
        jsonObject = new JSONObject(myEnum, names);

        // validate JSON object
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertEquals(3, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "expected 3 top level items");
        assertEquals(MyEnum.VAL1, jsonObject.query("/VAL1"), "expected VAL1");
        assertEquals(MyEnum.VAL2, jsonObject.query("/VAL2"), "expected VAL2");
        assertEquals(MyEnum.VAL3, jsonObject.query("/VAL3"), "expected VAL3");

        MyEnumField myEnumField = MyEnumField.VAL3;
        names = JSONObject.getNames(myEnumField);
        // The values will be MyEnmField fields
        jsonObject = new JSONObject(myEnumField, names);
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertEquals(3, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "expected 3 top level items");
        assertEquals(MyEnumField.VAL1, jsonObject.query("/VAL1"), "expected VAL1");
        assertEquals(MyEnumField.VAL2, jsonObject.query("/VAL2"), "expected VAL2");
        assertEquals(MyEnumField.VAL3, jsonObject.query("/VAL3"), "expected VAL3");
    }

    /**
     * Verify that enums are handled consistently between JSONArray and JSONObject
     */
    @Test
    void verifyEnumConsistency(){
        JSONObject jo = new JSONObject();
        
        jo.put("value", MyEnumField.VAL2);
        String expected="{\"value\":\"VAL2\"}";
        String actual = jo.toString();
        assertEquals(expected, actual, "Expected " + expected + " but actual was " + actual);

        jo.accumulate("value", MyEnumField.VAL1);
        expected="{\"value\":[\"VAL2\",\"VAL1\"]}";
        actual = jo.toString();
        assertEquals(expected, actual, "Expected " + expected + " but actual was " + actual);

        jo.remove("value");
        jo.append("value", MyEnumField.VAL1);
        expected="{\"value\":[\"VAL1\"]}";
        actual = jo.toString();
        assertEquals(expected, actual, "Expected " + expected + " but actual was " + actual);

        jo.put("value", EnumSet.of(MyEnumField.VAL2));
        expected="{\"value\":[\"VAL2\"]}";
        actual = jo.toString();
        assertEquals(expected, actual, "Expected " + expected + " but actual was " + actual);

        JSONArray ja = new JSONArray();
        ja.put(MyEnumField.VAL2);
        jo.put("value", ja);
        actual = jo.toString();
        assertEquals(expected, actual, "Expected " + expected + " but actual was " + actual);

        jo.put("value", new MyEnumField[]{MyEnumField.VAL2});
        actual = jo.toString();
        assertEquals(expected, actual, "Expected " + expected + " but actual was " + actual);

    }

    /**
     * To serialize by assigned value, use the put() methods. The value
     * will be stored as a enum type. 
     */
    @Test
    void enumPut() {
        JSONObject jsonObject = new JSONObject();
        MyEnum myEnum = MyEnum.VAL2;
        jsonObject.put("myEnum", myEnum);
        MyEnumField myEnumField = MyEnumField.VAL1;
        jsonObject.putOnce("myEnumField", myEnumField);

        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertEquals(2, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "expected 2 top level objects");
        assertEquals(MyEnum.VAL2, jsonObject.query("/myEnum"), "expected VAL2");
        assertEquals(MyEnumField.VAL1, jsonObject.query("/myEnumField"), "expected VAL1");
        
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(myEnum);
        jsonArray.put(1, myEnumField);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertEquals(2, ((List<?>)(JsonPath.read(doc, "$"))).size(), "expected 2 top level objects");
        assertEquals(MyEnum.VAL2, jsonArray.query("/0"), "expected VAL2");
        assertEquals(MyEnumField.VAL1, jsonArray.query("/1"), "expected VAL1");

        /**
         * Leaving these tests because they exercise get, opt, and remove
         */
        assertEquals(MyEnum.VAL2, jsonArray.get(0), "expecting myEnum value");
        assertEquals(MyEnumField.VAL1, jsonArray.opt(1), "expecting myEnumField value");
        assertEquals(MyEnumField.VAL1, jsonArray.remove(1), "expecting myEnumField value");
    }

    /**
     * The default action of valueToString() is to call object.toString().
     * For enums, this means the assigned value will be returned as a string.
     */
    @Test
    void enumValueToString() {
        String expectedStr1 = "\"VAL1\"";
        String expectedStr2 = "\"VAL1\"";
        MyEnum myEnum = MyEnum.VAL1;
        MyEnumField myEnumField = MyEnumField.VAL1;
        MyEnumClass myEnumClass = new MyEnumClass();
        
        String str1 = JSONObject.valueToString(myEnum);
        assertEquals(str1, expectedStr1, "actual myEnum: " + str1 + " expected: " + expectedStr1);
        String str2 = JSONObject.valueToString(myEnumField);
        assertEquals(str2, expectedStr2, "actual myEnumField: " + str2 + " expected: " + expectedStr2);

        /**
         * However, an enum within another class will not be rendered
         * unless that class overrides default toString() 
         */
        String expectedStr3 = "\"org.json.junit.data.MyEnumClass@";
        myEnumClass.setMyEnum(MyEnum.VAL1);
        myEnumClass.setMyEnumField(MyEnumField.VAL1);
        String str3 = JSONObject.valueToString(myEnumClass);
        assertTrue(str3.startsWith(expectedStr3),
                "actual myEnumClass: "+str3+" expected: "+expectedStr3);
    }

    /**
     * In whatever form the enum was added to the JSONObject or JSONArray,
     * json[Object|Array].toString should serialize it in a reasonable way.
     */
    @Test
    void enumToString() {
        MyEnum myEnum = MyEnum.VAL2;
        JSONObject jsonObject = new JSONObject(myEnum);
        String expectedStr = "{}";
        assertEquals(expectedStr, jsonObject.toString(), "myEnum toString() should be empty");

        MyEnumField myEnumField = MyEnumField.VAL2;
        jsonObject = new JSONObject(myEnumField);

        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertEquals(2, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "expected 2 top level items");
        assertEquals("val 2", jsonObject.query("/value"), "expected val 2");
        assertEquals(Integer.valueOf(2), jsonObject.query("/intVal"), "expected 2");

        MyEnumClass myEnumClass = new MyEnumClass();
        myEnumClass.setMyEnum(MyEnum.VAL1);
        myEnumClass.setMyEnumField(MyEnumField.VAL3);
        jsonObject = new JSONObject(myEnumClass);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertEquals(2, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "expected 2 top level items");
        assertEquals("VAL3", (JsonPath.read(doc, "$.myEnumField")), "expected VAL3");
        assertEquals("VAL1", (JsonPath.read(doc, "$.myEnum")), "expected VAL1");

        String [] names = JSONObject.getNames(myEnum);
        jsonObject = new JSONObject(myEnum, names);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertEquals(3, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "expected 3 top level items");
        assertEquals(MyEnum.VAL1, jsonObject.query("/VAL1"), "expected VAL1");
        assertEquals(MyEnum.VAL2, jsonObject.query("/VAL2"), "expected VAL2");
        assertEquals(MyEnum.VAL3, jsonObject.query("/VAL3"), "expected VAL3");
        
        names = JSONObject.getNames(myEnumField);
        jsonObject = new JSONObject(myEnumField, names);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertEquals(3, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "expected 3 top level items");
        assertEquals(MyEnumField.VAL1, jsonObject.query("/VAL1"), "expected VAL1");
        assertEquals(MyEnumField.VAL2, jsonObject.query("/VAL2"), "expected VAL2");
        assertEquals(MyEnumField.VAL3, jsonObject.query("/VAL3"), "expected VAL3");

        expectedStr = "{\"myEnum\":\"VAL2\", \"myEnumField\":\"VAL2\"}";
        jsonObject = new JSONObject();
        jsonObject.putOpt("myEnum", myEnum);
        jsonObject.putOnce("myEnumField", myEnumField);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertEquals(2, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "expected 2 top level items");
        assertEquals(MyEnum.VAL2, jsonObject.query("/myEnum"), "expected VAL2");
        assertEquals(MyEnumField.VAL2, jsonObject.query("/myEnumField"), "expected VAL2");

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(myEnum);
        jsonArray.put(1, myEnumField);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertEquals(2, ((List<?>)(JsonPath.read(doc, "$"))).size(), "expected 2 top level items");
        assertEquals(MyEnum.VAL2, jsonArray.query("/0"), "expected VAL2");
        assertEquals(MyEnumField.VAL2, jsonArray.query("/1"), "expected VAL2");
    }

    /**
     * Wrap should handle enums exactly as a value type like Integer, Boolean, or String.
     */
    @Test
    void wrap() {
        assertTrue(JSONObject.wrap(MyEnum.VAL2) instanceof MyEnum, "simple enum has no getters");

        MyEnumField myEnumField = MyEnumField.VAL2;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enum",myEnumField);

        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertEquals(1, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "expected 1 top level items");
        assertEquals(MyEnumField.VAL2, jsonObject.query("/enum"), "expected VAL2");

        MyEnumClass myEnumClass = new MyEnumClass();
        myEnumClass.setMyEnum(MyEnum.VAL1);
        myEnumClass.setMyEnumField(MyEnumField.VAL3);
        jsonObject = (JSONObject)JSONObject.wrap(myEnumClass);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertEquals(2, ((Map<?, ?>)(JsonPath.read(doc, "$"))).size(), "expected 2 top level items");
        assertEquals("VAL3", (JsonPath.read(doc, "$.myEnumField")), "expected VAL3");
        assertEquals("VAL1", (JsonPath.read(doc, "$.myEnum")), "expected VAL1");

        assertEquals(MyEnumField.VAL3, jsonObject.query("/myEnumField"), "expecting MyEnumField.VAL3");
        assertEquals(MyEnum.VAL1, jsonObject.query("/myEnum"), "expecting MyEnum.VAL1");
    }

    /**
     * It was determined that some API methods should be added to 
     * support enums:<br>
     * JSONObject.getEnum(class, key)<br>
     * JSONObject.optEnum(class, key)<br>
     * JSONObject.optEnum(class, key, default)<br>
     * JSONArray.getEnum(class, index)<br>
     * JSONArray.optEnum(class, index)<br>
     * JSONArray.optEnum(class, index, default)<br>
     * <p>
     * Exercise these enum API methods on JSONObject and JSONArray
     */
    @Test
    void enumAPI() {
        MyEnumClass myEnumClass = new MyEnumClass();
        myEnumClass.setMyEnum(MyEnum.VAL1);
        MyEnumField myEnumField = MyEnumField.VAL2;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("strKey", "value");
        jsonObject.put("strKey2", "VAL1");
        jsonObject.put("enumKey", myEnumField);
        jsonObject.put("enumClassKey", myEnumClass);

        // get a plain old enum
        MyEnumField actualEnum = jsonObject.getEnum(MyEnumField.class, "enumKey");
        assertTrue(actualEnum == MyEnumField.VAL2, "get myEnumField");

        // try to get the wrong value
        try {
            actualEnum = jsonObject.getEnum(MyEnumField.class, "strKey");
            assertTrue(false, "should throw an exception for wrong key");
        } catch (Exception ignored) {}

        // get a class that contains an enum
        MyEnumClass actualEnumClass = (MyEnumClass)jsonObject.get("enumClassKey");
        assertTrue(actualEnumClass.getMyEnum() == MyEnum.VAL1, "get enum");

        // opt a plain old enum
        actualEnum = jsonObject.optEnum(MyEnumField.class, "enumKey");
        assertTrue(actualEnum == MyEnumField.VAL2, "opt myEnumField");

        // opt the wrong value
        actualEnum = jsonObject.optEnum(MyEnumField.class, "strKey");
        assertTrue(actualEnum == null, "opt null");

        // opt a class that contains an enum
        actualEnumClass = (MyEnumClass)jsonObject.opt("enumClassKey");
        assertTrue(actualEnumClass.getMyEnum() == MyEnum.VAL1, "get enum");

        // opt with default a plain old enum
        actualEnum = jsonObject.optEnum(MyEnumField.class, "enumKey", null);
        assertTrue(actualEnum == MyEnumField.VAL2, "opt myEnumField");

        // opt with default the wrong value
        actualEnum = jsonObject.optEnum(MyEnumField.class, "strKey", null);
        assertNull(actualEnum, "opt null");

        // opt with default the string value
        actualEnum = jsonObject.optEnum(MyEnumField.class, "strKey2", null);
        assertEquals(MyEnumField.VAL1, actualEnum);

        // opt with default an index that does not exist
        actualEnum = jsonObject.optEnum(MyEnumField.class, "noKey", null);
        assertNull(actualEnum, "opt null");
        
        assertNull(jsonObject.optEnum(null, "enumKey"),
                "Expected Null when the enum class is null");

        /**
         * Exercise the proposed enum API methods on JSONArray
         */
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("value");
        jsonArray.put(myEnumField);
        jsonArray.put(myEnumClass);

        // get a plain old enum
        actualEnum = jsonArray.getEnum(MyEnumField.class, 1);
        assertTrue(actualEnum == MyEnumField.VAL2, "get myEnumField");

        // try to get the wrong value
        try {
            actualEnum = jsonArray.getEnum(MyEnumField.class, 0);
            assertTrue(false, "should throw an exception for wrong index");
        } catch (Exception ignored) {}

        // get a class that contains an enum
        actualEnumClass = (MyEnumClass)jsonArray.get(2);
        assertTrue(actualEnumClass.getMyEnum() == MyEnum.VAL1, "get enum");

        // opt a plain old enum
        actualEnum = jsonArray.optEnum(MyEnumField.class, 1);
        assertTrue(actualEnum == MyEnumField.VAL2, "opt myEnumField");

        // opt the wrong value
        actualEnum = jsonArray.optEnum(MyEnumField.class, 0);
        assertTrue(actualEnum == null, "opt null");

        // opt a class that contains an enum
        actualEnumClass = (MyEnumClass)jsonArray.opt(2);
        assertTrue(actualEnumClass.getMyEnum() == MyEnum.VAL1, "get enum");

        // opt with default a plain old enum
        actualEnum = jsonArray.optEnum(MyEnumField.class, 1, null);
        assertTrue(actualEnum == MyEnumField.VAL2, "opt myEnumField");

        // opt with default the wrong value
        actualEnum = jsonArray.optEnum(MyEnumField.class, 0, null);
        assertTrue(actualEnum == null, "opt null");

        // opt with default an index that does not exist
        actualEnum = jsonArray.optEnum(MyEnumField.class, 3, null);
        assertTrue(actualEnum == null, "opt null");

    }
}
