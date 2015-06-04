package org.json.junit;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.*;

/**
 * Documents how enum is handled by JSON-Java.
 */
public class EnumTest {

    @Test
    public void simpleEnum() {
        /**
         * Nothing happens when a simple enum is parsed to JSONObject
         */
        MyEnum myEnum = MyEnum.VAL2;
        JSONObject jsonObject = new JSONObject(myEnum);
        assertTrue("simple enum is not processed by JSONObject", jsonObject.length() == 0);
        /**
         * Nothing good happens when a simple enum is parsed to JSONArray
         */
        try {
            new JSONArray(myEnum);
        } catch (JSONException e) {
            assertTrue("JSONArray throws exception when passed enum", true);
        }
    }

    @Test
    public void enumWithField() {
        /**
         * enum with a getters is handled like a bean
         */
        String expectedStr = "{\"value\":\"val 2\", \"intVal\":2}";
        MyEnumField myEnum = MyEnumField.VAL2;
        JSONObject jsonObject = new JSONObject(myEnum);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void enumInClass() {
        /**
         * class which contains enum instances.
         * The enum values in MyEnum are lost.
         * The string values in MyEnumFild are extracted and wrapped.
         */
        String expectedStr = "{\"myEnumField\":{\"intVal\":3,\"value\":\"val 3\"},\"myEnum\":{}}";
        MyEnumClass myEnumClass = new MyEnumClass();
        myEnumClass.setMyEnum(MyEnum.VAL1);
        myEnumClass.setMyEnumField(MyEnumField.VAL3);
        JSONObject jsonObject = new JSONObject(myEnumClass);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void enumValueToString() {
        String expectedStr1 = "\"VAL1\"";
        String expectedStr2 = "\"VAL1\"";
        String expectedStr3 = "\"org.json.junit.MyEnumClass@";
        MyEnum myEnum = MyEnum.VAL1;
        MyEnumField myEnumField = MyEnumField.VAL1;
        MyEnumClass myEnumClass = new MyEnumClass();
        myEnumClass.setMyEnum(MyEnum.VAL1);
        myEnumClass.setMyEnumField(MyEnumField.VAL1);
        
        String str1 = JSONObject.valueToString(myEnum);
        assertTrue("actual myEnum: "+str1+" expected: "+expectedStr1,
                str1.equals(expectedStr1));
        String str2 = JSONObject.valueToString(myEnumField);
        assertTrue("actual myEnumField: "+str2+" expected: "+expectedStr2,
                str2.equals(expectedStr2));
        String str3 = JSONObject.valueToString(myEnumClass);
        assertTrue("actual myEnumClass: "+str3+" expected: "+expectedStr3,
                str3.startsWith(expectedStr3));
    }
}
