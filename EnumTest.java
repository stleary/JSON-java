package org.json.junit;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.*;

/**
 * Enums are not explicitly supported in JSON-Java. But because enums act like
 * classes, all required behavior is already be present in some form. 
 * These tests explore how enum serialization works with JSON-Java.
 */
public class EnumTest {
    @Test
    public void jsonObjectFromEnum() {
        /**
         * To serialize an enum by its getters, use the JSONObject Object constructor.
         * The JSONObject ctor handles enum like any other bean. A JSONobject 
         * is created whose entries are the getter name/value pairs.
         */
        
        // If there are no getters then the object is empty.
        MyEnum myEnum = MyEnum.VAL2;
        JSONObject jsonObject = new JSONObject(myEnum);
        assertTrue("simple enum has no getters", jsonObject.length() == 0);

         // enum with a getters should create a non-empty object 
        String expectedStr = "{\"value\":\"val 2\", \"intVal\":2}";
        MyEnumField myEnumField = MyEnumField.VAL2;
        jsonObject = new JSONObject(myEnumField);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);

        /**
         * class which contains enum instances. Each enum should be stored
         * in its own JSONObject
         */
        expectedStr = "{\"myEnumField\":{\"intVal\":3,\"value\":\"val 3\"},\"myEnum\":{}}";
        MyEnumClass myEnumClass = new MyEnumClass();
        myEnumClass.setMyEnum(MyEnum.VAL1);
        myEnumClass.setMyEnumField(MyEnumField.VAL3);
        jsonObject = new JSONObject(myEnumClass);
        expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void jsonObjectFromEnumWithNames() {
        /**
         * To serialize an enum by its set of allowed values, use getNames()
         * and the the JSONObject Object with names constructor. 
         */
        String [] names;
        String expectedStr;
        JSONObject jsonObject;
        JSONObject finalJsonObject;
        JSONObject expectedJsonObject;
 
        expectedStr = "{\"VAL1\":\"VAL1\",\"VAL2\":\"VAL2\",\"VAL3\":\"VAL3\"}";
        MyEnum myEnum = MyEnum.VAL1;
        names = JSONObject.getNames(myEnum);
        // The values will be MyEnmField fields, so need to convert back to string for comparison
        jsonObject = new JSONObject(myEnum, names);
        finalJsonObject = new JSONObject(jsonObject.toString());
        expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject, expectedJsonObject);

        expectedStr = "{\"VAL1\":\"VAL1\",\"VAL2\":\"VAL2\",\"VAL3\":\"VAL3\"}";
        MyEnumField myEnumField = MyEnumField.VAL3;
        names = JSONObject.getNames(myEnumField);
        // The values will be MyEnmField fields, so need to convert back to string for comparison
        jsonObject = new JSONObject(myEnumField, names);
        finalJsonObject = new JSONObject(jsonObject.toString());
        expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject, expectedJsonObject);
    }
    @Test
    public void enumPut() {
        /**
         * To serialize by assigned value, use the put() methods. The value
         * will be stored as a enum type. 
         */
        String expectedFinalStr = "{\"myEnum\":\"VAL2\", \"myEnumField\":\"VAL1\"}";
        JSONObject jsonObject = new JSONObject();
        MyEnum myEnum = MyEnum.VAL2;
        jsonObject.put("myEnum", myEnum);
        assertTrue("expecting myEnum value", MyEnum.VAL2.equals(jsonObject.get("myEnum")));
        assertTrue("expecting myEnum value", MyEnum.VAL2.equals(jsonObject.opt("myEnum")));
        MyEnumField myEnumField = MyEnumField.VAL1;
        jsonObject.putOnce("myEnumField", myEnumField);
        assertTrue("expecting myEnumField value", MyEnumField.VAL1.equals(jsonObject.get("myEnumField")));
        assertTrue("expecting myEnumField value", MyEnumField.VAL1.equals(jsonObject.opt("myEnumField")));
        JSONObject finalJsonObject = new JSONObject(jsonObject.toString());
        JSONObject expectedFinalJsonObject = new JSONObject(expectedFinalStr);
        Util.compareActualVsExpectedJsonObjects(finalJsonObject, expectedFinalJsonObject);

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(myEnum);
        jsonArray.put(1, myEnumField);
        assertTrue("expecting myEnum value", MyEnum.VAL2.equals(jsonArray.get(0)));
        assertTrue("expecting myEnumField value", MyEnumField.VAL1.equals(jsonArray.opt(1)));
        JSONArray expectedJsonArray = new JSONArray();
        expectedJsonArray.put(MyEnum.VAL2);
        expectedJsonArray.put(MyEnumField.VAL1);
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
        assertTrue("expecting myEnumField value", MyEnumField.VAL1.equals(jsonArray.remove(1)));
    }

    @Test
    public void enumValueToString() {
        /**
         * The default action of valueToString() is to call object.toString().
         * For enums, this means the assigned value will be returned as a string.
         */
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

    @Test
    public void enumToString() {
        /**
         * In whatever form the enum was added to the JSONObject or JSONArray,
         * json[Object|Array].toString should serialize it in a reasonable way.
         */
        MyEnum myEnum = MyEnum.VAL2;
        JSONObject jsonObject = new JSONObject(myEnum);
        String expectedStr = "{}";
        assertTrue("myEnum toString() should be empty", expectedStr.equals(jsonObject.toString()));

        MyEnumField myEnumField = MyEnumField.VAL2;
        jsonObject = new JSONObject(myEnumField);
        expectedStr = "{\"value\":\"val 2\", \"intVal\":2}";
        JSONObject actualJsonObject = new JSONObject(jsonObject.toString());
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(actualJsonObject, expectedJsonObject);

        expectedStr = "{\"myEnumField\":{\"intVal\":3,\"value\":\"val 3\"},\"myEnum\":{}}";
        MyEnumClass myEnumClass = new MyEnumClass();
        myEnumClass.setMyEnum(MyEnum.VAL1);
        myEnumClass.setMyEnumField(MyEnumField.VAL3);
        jsonObject = new JSONObject(myEnumClass);
        actualJsonObject = new JSONObject(jsonObject.toString());
        expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(actualJsonObject, expectedJsonObject);

        expectedStr = "{\"VAL1\":\"VAL1\",\"VAL2\":\"VAL2\",\"VAL3\":\"VAL3\"}";
        String [] names = JSONObject.getNames(myEnum);
        jsonObject = new JSONObject(myEnum, names);
        actualJsonObject = new JSONObject(jsonObject.toString());
        expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(actualJsonObject, expectedJsonObject);

        expectedStr = "{\"VAL1\":\"VAL1\",\"VAL2\":\"VAL2\",\"VAL3\":\"VAL3\"}";
        names = JSONObject.getNames(myEnumField);
        jsonObject = new JSONObject(myEnumField, names);
        actualJsonObject = new JSONObject(jsonObject.toString());
        expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(actualJsonObject, expectedJsonObject);

        expectedStr = "{\"myEnum\":\"VAL2\", \"myEnumField\":\"VAL2\"}";
        jsonObject = new JSONObject();
        jsonObject.putOpt("myEnum", myEnum);
        jsonObject.putOnce("myEnumField", myEnumField);
        actualJsonObject = new JSONObject(jsonObject.toString());
        expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(actualJsonObject, expectedJsonObject);

        expectedStr = "[\"VAL2\", \"VAL2\"]";
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(myEnum);
        jsonArray.put(1, myEnumField);
        JSONArray actualJsonArray = new JSONArray(jsonArray.toString());
        JSONArray expectedJsonArray = new JSONArray(expectedStr);
        Util.compareActualVsExpectedJsonArrays(actualJsonArray, expectedJsonArray);
    }

    public void wrap() {
        /**
         * Wrap should handle enums exactly the same way as the JSONObject(Object)
         * constructor. 
         */
        MyEnum myEnum = MyEnum.VAL2;
        JSONObject jsonObject = (JSONObject)JSONObject.wrap(myEnum);
        assertTrue("simple enum has no getters", jsonObject.length() == 0);

        String expectedStr = "{\"value\":\"val 2\", \"intVal\":2}";
        MyEnumField myEnumField = MyEnumField.VAL2;
        jsonObject = (JSONObject)JSONObject.wrap(myEnumField);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);

        expectedStr = "{\"myEnumField\":{\"intVal\":3,\"value\":\"val 3\"},\"myEnum\":{}}";
        MyEnumClass myEnumClass = new MyEnumClass();
        myEnumClass.setMyEnum(MyEnum.VAL1);
        myEnumClass.setMyEnumField(MyEnumField.VAL3);
        jsonObject = (JSONObject)JSONObject.wrap(myEnumClass);
        expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }
}
