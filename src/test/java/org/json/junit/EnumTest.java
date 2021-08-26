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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.junit.data.MyEnum;
import org.json.junit.data.MyEnumClass;
import org.json.junit.data.MyEnumField;
import org.junit.Test;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

/**
 * Enums are not explicitly supported in JSON-Java. But because enums act like
 * classes, all required behavior is already be present in some form. 
 * These tests explore how enum serialization works with JSON-Java.
 */
public class EnumTest {

    /**
     * To serialize an enum by its getters, use the JSONObject Object constructor.
     * The JSONObject ctor handles enum like any other bean. A JSONobject 
     * is created whose entries are the getter name/value pairs.
     */
    @Test
    public void jsonObjectFromEnum() {
        // If there are no getters then the object is empty.
        MyEnum myEnum = MyEnum.VAL2;
        JSONObject jsonObject = new JSONObject(myEnum);
        assertTrue("simple enum has no getters", jsonObject.isEmpty());

         // enum with a getters should create a non-empty object 
        MyEnumField myEnumField = MyEnumField.VAL2;
        jsonObject = new JSONObject(myEnumField);

        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider()
                .parse(jsonObject.toString());
        assertTrue("expecting 2 items in top level object", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expecting val 2", "val 2".equals(jsonObject.query("/value")));
        assertTrue("expecting 2", Integer.valueOf(2).equals(jsonObject.query("/intVal")));

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
        assertTrue("expected 2 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected 2 myEnumField items", "VAL3".equals((JsonPath.read(doc, "$.myEnumField"))));
        assertTrue("expected 0 myEnum items", "VAL1".equals((JsonPath.read(doc, "$.myEnum"))));

        assertTrue("expecting MyEnumField.VAL3", MyEnumField.VAL3.equals(jsonObject.query("/myEnumField")));
        assertTrue("expecting MyEnum.VAL1", MyEnum.VAL1.equals(jsonObject.query("/myEnum")));
    }

    /**
     * To serialize an enum by its set of allowed values, use getNames()
     * and the JSONObject Object with names constructor.
     */
    @Test
    public void jsonObjectFromEnumWithNames() {
        String [] names;
        JSONObject jsonObject;
 
        MyEnum myEnum = MyEnum.VAL1;
        names = JSONObject.getNames(myEnum);
        // The values will be MyEnum fields
        jsonObject = new JSONObject(myEnum, names);

        // validate JSON object
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 3 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 3);
        assertTrue("expected VAL1", MyEnum.VAL1.equals(jsonObject.query("/VAL1")));
        assertTrue("expected VAL2", MyEnum.VAL2.equals(jsonObject.query("/VAL2")));
        assertTrue("expected VAL3", MyEnum.VAL3.equals(jsonObject.query("/VAL3")));

        MyEnumField myEnumField = MyEnumField.VAL3;
        names = JSONObject.getNames(myEnumField);
        // The values will be MyEnmField fields
        jsonObject = new JSONObject(myEnumField, names);
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 3 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 3);
        assertTrue("expected VAL1", MyEnumField.VAL1.equals(jsonObject.query("/VAL1")));
        assertTrue("expected VAL2", MyEnumField.VAL2.equals(jsonObject.query("/VAL2")));
        assertTrue("expected VAL3", MyEnumField.VAL3.equals(jsonObject.query("/VAL3")));
    }
    
    /**
     * Verify that enums are handled consistently between JSONArray and JSONObject
     */
    @Test
    public void verifyEnumConsistency(){
        JSONObject jo = new JSONObject();
        
        jo.put("value", MyEnumField.VAL2);
        String expected="{\"value\":\"VAL2\"}";
        String actual = jo.toString();
        assertTrue("Expected "+expected+" but actual was "+actual, expected.equals(actual));

        jo.accumulate("value", MyEnumField.VAL1);
        expected="{\"value\":[\"VAL2\",\"VAL1\"]}";
        actual = jo.toString();
        assertTrue("Expected "+expected+" but actual was "+actual, expected.equals(actual));

        jo.remove("value");
        jo.append("value", MyEnumField.VAL1);
        expected="{\"value\":[\"VAL1\"]}";
        actual = jo.toString();
        assertTrue("Expected "+expected+" but actual was "+actual, expected.equals(actual));

        jo.put("value", EnumSet.of(MyEnumField.VAL2));
        expected="{\"value\":[\"VAL2\"]}";
        actual = jo.toString();
        assertTrue("Expected "+expected+" but actual was "+actual, expected.equals(actual));

        JSONArray ja = new JSONArray();
        ja.put(MyEnumField.VAL2);
        jo.put("value", ja);
        actual = jo.toString();
        assertTrue("Expected "+expected+" but actual was "+actual, expected.equals(actual));

        jo.put("value", new MyEnumField[]{MyEnumField.VAL2});
        actual = jo.toString();
        assertTrue("Expected "+expected+" but actual was "+actual, expected.equals(actual));

    }

    /**
     * To serialize by assigned value, use the put() methods. The value
     * will be stored as a enum type. 
     */
    @Test
    public void enumPut() {
        JSONObject jsonObject = new JSONObject();
        MyEnum myEnum = MyEnum.VAL2;
        jsonObject.put("myEnum", myEnum);
        MyEnumField myEnumField = MyEnumField.VAL1;
        jsonObject.putOnce("myEnumField", myEnumField);

        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 2 top level objects", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected VAL2", MyEnum.VAL2.equals(jsonObject.query("/myEnum")));
        assertTrue("expected VAL1", MyEnumField.VAL1.equals(jsonObject.query("/myEnumField")));
        
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(myEnum);
        jsonArray.put(1, myEnumField);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertTrue("expected 2 top level objects", ((List<?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected VAL2", MyEnum.VAL2.equals(jsonArray.query("/0")));
        assertTrue("expected VAL1", MyEnumField.VAL1.equals(jsonArray.query("/1")));

        /**
         * Leaving these tests because they exercise get, opt, and remove
         */
        assertTrue("expecting myEnum value", MyEnum.VAL2.equals(jsonArray.get(0)));
        assertTrue("expecting myEnumField value", MyEnumField.VAL1.equals(jsonArray.opt(1)));
        assertTrue("expecting myEnumField value", MyEnumField.VAL1.equals(jsonArray.remove(1)));
    }

    /**
     * The default action of valueToString() is to call object.toString().
     * For enums, this means the assigned value will be returned as a string.
     */
    @Test
    public void enumValueToString() {
        String expectedStr1 = "\"VAL1\"";
        String expectedStr2 = "\"VAL1\"";
        MyEnum myEnum = MyEnum.VAL1;
        MyEnumField myEnumField = MyEnumField.VAL1;
        MyEnumClass myEnumClass = new MyEnumClass();
        
        String str1 = JSONObject.valueToString(myEnum);
        assertTrue("actual myEnum: "+str1+" expected: "+expectedStr1,
                str1.equals(expectedStr1));
        String str2 = JSONObject.valueToString(myEnumField);
        assertTrue("actual myEnumField: "+str2+" expected: "+expectedStr2,
                str2.equals(expectedStr2));

        /**
         * However, an enum within another class will not be rendered
         * unless that class overrides default toString() 
         */
        String expectedStr3 = "\"org.json.junit.data.MyEnumClass@";
        myEnumClass.setMyEnum(MyEnum.VAL1);
        myEnumClass.setMyEnumField(MyEnumField.VAL1);
        String str3 = JSONObject.valueToString(myEnumClass);
        assertTrue("actual myEnumClass: "+str3+" expected: "+expectedStr3,
                str3.startsWith(expectedStr3));
    }

    /**
     * In whatever form the enum was added to the JSONObject or JSONArray,
     * json[Object|Array].toString should serialize it in a reasonable way.
     */
    @Test
    public void enumToString() {
        MyEnum myEnum = MyEnum.VAL2;
        JSONObject jsonObject = new JSONObject(myEnum);
        String expectedStr = "{}";
        assertTrue("myEnum toString() should be empty", expectedStr.equals(jsonObject.toString()));

        MyEnumField myEnumField = MyEnumField.VAL2;
        jsonObject = new JSONObject(myEnumField);

        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 2 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected val 2", "val 2".equals(jsonObject.query("/value")));
        assertTrue("expected 2", Integer.valueOf(2).equals(jsonObject.query("/intVal")));

        MyEnumClass myEnumClass = new MyEnumClass();
        myEnumClass.setMyEnum(MyEnum.VAL1);
        myEnumClass.setMyEnumField(MyEnumField.VAL3);
        jsonObject = new JSONObject(myEnumClass);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 2 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected VAL3", "VAL3".equals((JsonPath.read(doc, "$.myEnumField"))));
        assertTrue("expected VAL1", "VAL1".equals((JsonPath.read(doc, "$.myEnum"))));

        String [] names = JSONObject.getNames(myEnum);
        jsonObject = new JSONObject(myEnum, names);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 3 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 3);
        assertTrue("expected VAL1", MyEnum.VAL1.equals(jsonObject.query("/VAL1")));
        assertTrue("expected VAL2", MyEnum.VAL2.equals(jsonObject.query("/VAL2")));
        assertTrue("expected VAL3", MyEnum.VAL3.equals(jsonObject.query("/VAL3")));
        
        names = JSONObject.getNames(myEnumField);
        jsonObject = new JSONObject(myEnumField, names);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 3 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 3);
        assertTrue("expected VAL1", MyEnumField.VAL1.equals(jsonObject.query("/VAL1")));
        assertTrue("expected VAL2", MyEnumField.VAL2.equals(jsonObject.query("/VAL2")));
        assertTrue("expected VAL3", MyEnumField.VAL3.equals(jsonObject.query("/VAL3")));

        expectedStr = "{\"myEnum\":\"VAL2\", \"myEnumField\":\"VAL2\"}";
        jsonObject = new JSONObject();
        jsonObject.putOpt("myEnum", myEnum);
        jsonObject.putOnce("myEnumField", myEnumField);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 2 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected VAL2", MyEnum.VAL2.equals(jsonObject.query("/myEnum")));
        assertTrue("expected VAL2", MyEnumField.VAL2.equals(jsonObject.query("/myEnumField")));

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(myEnum);
        jsonArray.put(1, myEnumField);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertTrue("expected 2 top level items", ((List<?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected VAL2", MyEnum.VAL2.equals(jsonArray.query("/0")));
        assertTrue("expected VAL2", MyEnumField.VAL2.equals(jsonArray.query("/1")));
    }

    /**
     * Wrap should handle enums exactly as a value type like Integer, Boolean, or String.
     */
    @Test
    public void wrap() {
        assertTrue("simple enum has no getters", JSONObject.wrap(MyEnum.VAL2) instanceof MyEnum);

        MyEnumField myEnumField = MyEnumField.VAL2;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enum",myEnumField);

        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 1 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 1);
        assertTrue("expected VAL2", MyEnumField.VAL2.equals(jsonObject.query("/enum")));

        MyEnumClass myEnumClass = new MyEnumClass();
        myEnumClass.setMyEnum(MyEnum.VAL1);
        myEnumClass.setMyEnumField(MyEnumField.VAL3);
        jsonObject = (JSONObject)JSONObject.wrap(myEnumClass);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 2 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected VAL3", "VAL3".equals((JsonPath.read(doc, "$.myEnumField"))));
        assertTrue("expected VAL1", "VAL1".equals((JsonPath.read(doc, "$.myEnum"))));

        assertTrue("expecting MyEnumField.VAL3", MyEnumField.VAL3.equals(jsonObject.query("/myEnumField")));
        assertTrue("expecting MyEnum.VAL1", MyEnum.VAL1.equals(jsonObject.query("/myEnum")));
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
    public void enumAPI() {
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
        assertTrue("get myEnumField", actualEnum == MyEnumField.VAL2);

        // try to get the wrong value
        try {
            actualEnum = jsonObject.getEnum(MyEnumField.class, "strKey");
            assertTrue("should throw an exception for wrong key", false);
        } catch (Exception ignored) {}

        // get a class that contains an enum
        MyEnumClass actualEnumClass = (MyEnumClass)jsonObject.get("enumClassKey");
        assertTrue("get enum", actualEnumClass.getMyEnum() == MyEnum.VAL1);

        // opt a plain old enum
        actualEnum = jsonObject.optEnum(MyEnumField.class, "enumKey");
        assertTrue("opt myEnumField", actualEnum == MyEnumField.VAL2);

        // opt the wrong value
        actualEnum = jsonObject.optEnum(MyEnumField.class, "strKey");
        assertTrue("opt null", actualEnum == null);

        // opt a class that contains an enum
        actualEnumClass = (MyEnumClass)jsonObject.opt("enumClassKey");
        assertTrue("get enum", actualEnumClass.getMyEnum() == MyEnum.VAL1);

        // opt with default a plain old enum
        actualEnum = jsonObject.optEnum(MyEnumField.class, "enumKey", null);
        assertTrue("opt myEnumField", actualEnum == MyEnumField.VAL2);

        // opt with default the wrong value
        actualEnum = jsonObject.optEnum(MyEnumField.class, "strKey", null);
        assertNull("opt null", actualEnum);

        // opt with default the string value
        actualEnum = jsonObject.optEnum(MyEnumField.class, "strKey2", null);
        assertEquals(MyEnumField.VAL1, actualEnum);

        // opt with default an index that does not exist
        actualEnum = jsonObject.optEnum(MyEnumField.class, "noKey", null);
        assertNull("opt null", actualEnum);
        
        assertNull("Expected Null when the enum class is null",
                jsonObject.optEnum(null, "enumKey"));

        /**
         * Exercise the proposed enum API methods on JSONArray
         */
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("value");
        jsonArray.put(myEnumField);
        jsonArray.put(myEnumClass);

        // get a plain old enum
        actualEnum = jsonArray.getEnum(MyEnumField.class, 1);
        assertTrue("get myEnumField", actualEnum == MyEnumField.VAL2);

        // try to get the wrong value
        try {
            actualEnum = jsonArray.getEnum(MyEnumField.class, 0);
            assertTrue("should throw an exception for wrong index", false);
        } catch (Exception ignored) {}

        // get a class that contains an enum
        actualEnumClass = (MyEnumClass)jsonArray.get(2);
        assertTrue("get enum", actualEnumClass.getMyEnum() == MyEnum.VAL1);

        // opt a plain old enum
        actualEnum = jsonArray.optEnum(MyEnumField.class, 1);
        assertTrue("opt myEnumField", actualEnum == MyEnumField.VAL2);

        // opt the wrong value
        actualEnum = jsonArray.optEnum(MyEnumField.class, 0);
        assertTrue("opt null", actualEnum == null);

        // opt a class that contains an enum
        actualEnumClass = (MyEnumClass)jsonArray.opt(2);
        assertTrue("get enum", actualEnumClass.getMyEnum() == MyEnum.VAL1);

        // opt with default a plain old enum
        actualEnum = jsonArray.optEnum(MyEnumField.class, 1, null);
        assertTrue("opt myEnumField", actualEnum == MyEnumField.VAL2);

        // opt with default the wrong value
        actualEnum = jsonArray.optEnum(MyEnumField.class, 0, null);
        assertTrue("opt null", actualEnum == null);

        // opt with default an index that does not exist
        actualEnum = jsonArray.optEnum(MyEnumField.class, 3, null);
        assertTrue("opt null", actualEnum == null);

    }
}
