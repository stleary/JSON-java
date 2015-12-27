package org.json.junit;

import static org.junit.Assert.*;

import java.util.*;

import org.json.*;
import org.junit.*;

import com.jayway.jsonpath.*;

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
        assertTrue("simple enum has no getters", jsonObject.length() == 0);

         // enum with a getters should create a non-empty object 
        MyEnumField myEnumField = MyEnumField.VAL2;
        jsonObject = new JSONObject(myEnumField);

        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider()
                .parse(jsonObject.toString());
        assertTrue("expecting 2 items in top level object",
                ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expecting val 2", "val 2".equals(JsonPath.read(doc, "$.value")));
        assertTrue("expecting 2", Integer.valueOf(2).equals(JsonPath.read(doc, "$.intVal")));

        /**
         * class which contains enum instances. Each enum should be stored
         * in its own JSONObject
         */
        MyEnumClass myEnumClass = new MyEnumClass();
        myEnumClass.setMyEnum(MyEnum.VAL1);
        myEnumClass.setMyEnumField(MyEnumField.VAL3);
        jsonObject = new JSONObject(myEnumClass);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider()
                .parse(jsonObject.toString());
        assertTrue("expecting 2 items in top level object",
                ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expecting 2 items in myEnumField object",
                ((Map<?,?>)(JsonPath.read(doc, "$.myEnumField"))).size() == 2);
        assertTrue("expecting 0 items in myEnum object",
                ((Map<?,?>)(JsonPath.read(doc, "$.myEnum"))).size() == 0);
        assertTrue("expecting 3", Integer.valueOf(3).equals(JsonPath.read(doc, "$.myEnumField.intVal")));
        assertTrue("expecting val 3", "val 3".equals(JsonPath.read(doc, "$.myEnumField.value")));
    }

    /**
     * To serialize an enum by its set of allowed values, use getNames()
     * and the the JSONObject Object with names constructor. 
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
        assertTrue("expected VAL1", "VAL1".equals(JsonPath.read(doc, "$.VAL1")));
        assertTrue("expected VAL2", "VAL2".equals(JsonPath.read(doc, "$.VAL2")));
        assertTrue("expected VAL3", "VAL3".equals(JsonPath.read(doc, "$.VAL3")));

        MyEnumField myEnumField = MyEnumField.VAL3;
        names = JSONObject.getNames(myEnumField);
        // The values will be MyEnmField fields
        jsonObject = new JSONObject(myEnumField, names);
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 3 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 3);
        assertTrue("expected VAL1", "VAL1".equals(JsonPath.read(doc, "$.VAL1")));
        assertTrue("expected VAL2", "VAL2".equals(JsonPath.read(doc, "$.VAL2")));
        assertTrue("expected VAL3", "VAL3".equals(JsonPath.read(doc, "$.VAL3")));

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
        assertTrue("expected VAL2", "VAL2".equals(JsonPath.read(doc, "$.myEnum")));
        assertTrue("expected VAL1", "VAL1".equals(JsonPath.read(doc, "$.myEnumField")));
        
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(myEnum);
        jsonArray.put(1, myEnumField);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertTrue("expected 2 top level objects", ((List<?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected VAL2", "VAL2".equals(JsonPath.read(doc, "$[0]")));
        assertTrue("expected VAL1", "VAL1".equals(JsonPath.read(doc, "$[1]")));

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
        String expectedStr3 = "\"org.json.junit.MyEnumClass@";
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
        assertTrue("expected val 2", "val 2".equals(JsonPath.read(doc, "$.value")));
        assertTrue("expected 2", Integer.valueOf(2).equals(JsonPath.read(doc, "$.intVal")));

        MyEnumClass myEnumClass = new MyEnumClass();
        myEnumClass.setMyEnum(MyEnum.VAL1);
        myEnumClass.setMyEnumField(MyEnumField.VAL3);
        jsonObject = new JSONObject(myEnumClass);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 2 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected 2 myEnumField items", ((Map<?,?>)(JsonPath.read(doc, "$.myEnumField"))).size() == 2);
        assertTrue("expected 0 myEnum items", ((Map<?,?>)(JsonPath.read(doc, "$.myEnum"))).size() == 0);
        assertTrue("expected 3", Integer.valueOf(3).equals(JsonPath.read(doc, "$.myEnumField.intVal")));
        assertTrue("expected val 3", "val 3".equals(JsonPath.read(doc, "$.myEnumField.value")));

        String [] names = JSONObject.getNames(myEnum);
        jsonObject = new JSONObject(myEnum, names);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 3 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 3);
        assertTrue("expected VAL1", "VAL1".equals(JsonPath.read(doc, "$.VAL1")));
        assertTrue("expected VAL2", "VAL2".equals(JsonPath.read(doc, "$.VAL2")));
        assertTrue("expected VAL3", "VAL3".equals(JsonPath.read(doc, "$.VAL3")));
        
        names = JSONObject.getNames(myEnumField);
        jsonObject = new JSONObject(myEnumField, names);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 3 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 3);
        assertTrue("expected VAL1", "VAL1".equals(JsonPath.read(doc, "$.VAL1")));
        assertTrue("expected VAL2", "VAL2".equals(JsonPath.read(doc, "$.VAL2")));
        assertTrue("expected VAL3", "VAL3".equals(JsonPath.read(doc, "$.VAL3")));

        expectedStr = "{\"myEnum\":\"VAL2\", \"myEnumField\":\"VAL2\"}";
        jsonObject = new JSONObject();
        jsonObject.putOpt("myEnum", myEnum);
        jsonObject.putOnce("myEnumField", myEnumField);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 2 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected VAL2", "VAL2".equals(JsonPath.read(doc, "$.myEnum")));
        assertTrue("expected VAL2", "VAL2".equals(JsonPath.read(doc, "$.myEnumField")));

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(myEnum);
        jsonArray.put(1, myEnumField);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertTrue("expected 2 top level items", ((List<?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected VAL2", "VAL2".equals(JsonPath.read(doc, "$[0]")));
        assertTrue("expected VAL2", "VAL2".equals(JsonPath.read(doc, "$[1]")));
    }

    /**
     * Wrap should handle enums exactly the same way as the JSONObject(Object)
     * constructor. 
     */
    @Test
    public void wrap() {
        MyEnum myEnum = MyEnum.VAL2;
        JSONObject jsonObject = (JSONObject)JSONObject.wrap(myEnum);
        assertTrue("simple enum has no getters", jsonObject.length() == 0);

        MyEnumField myEnumField = MyEnumField.VAL2;
        jsonObject = (JSONObject)JSONObject.wrap(myEnumField);

        // validate JSON content
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 2 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected val 2", "val 2".equals(JsonPath.read(doc, "$.value")));
        assertTrue("expected 2", Integer.valueOf(2).equals(JsonPath.read(doc, "$.intVal")));

        MyEnumClass myEnumClass = new MyEnumClass();
        myEnumClass.setMyEnum(MyEnum.VAL1);
        myEnumClass.setMyEnumField(MyEnumField.VAL3);
        jsonObject = (JSONObject)JSONObject.wrap(myEnumClass);

        // validate JSON content
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 2 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected 2 myEnumField items", ((Map<?,?>)(JsonPath.read(doc, "$.myEnumField"))).size() == 2);
        assertTrue("expected 0 myEnum items", ((Map<?,?>)(JsonPath.read(doc, "$.myEnum"))).size() == 0);
        assertTrue("expected 3", Integer.valueOf(3).equals(JsonPath.read(doc, "$.myEnumField.intVal")));
        assertTrue("expected val 3", "val 3".equals(JsonPath.read(doc, "$.myEnumField.value")));

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
        assertTrue("opt null", actualEnum == null);

        // opt with default an index that does not exist
        actualEnum = jsonObject.optEnum(MyEnumField.class, "noKey", null);
        assertTrue("opt null", actualEnum == null);

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
