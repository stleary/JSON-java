package org.json.junit;

import static org.junit.Assert.*;

import java.util.*;

import org.json.*;
import org.junit.*;


public class JSONObjectTest {


    @Test
    public void jsonObjectByNames() {
        String str = 
            "{"+
                "\"trueKey\":true,"+
                "\"falseKey\":false,"+
                "\"nullKey\":null,"+
                "\"stringKey\":\"hello world!\","+
                "\"complexStringKey\":\"h\be\tllo w\u1234orld!\","+
                "\"intKey\":42,"+
                "\"doubleKey\":-23.45e67"+
            "}";
        String[] keys = {"falseKey", "stringKey", "nullKey", "doubleKey"};
        String expectedStr = 
            "{"+
                "\"falseKey\":false,"+
                "\"nullKey\":null,"+
                "\"stringKey\":\"hello world!\","+
                "\"doubleKey\":-23.45e67"+
            "}";
        JSONObject jsonObject = new JSONObject(str);
        JSONObject copyJsonObject = new JSONObject(jsonObject, keys);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(copyJsonObject, expectedJsonObject);
    }

    @Test
    public void jsonObjectByMap() {
        String expectedStr = 
            "{"+
                "\"trueKey\":true,"+
                "\"falseKey\":false,"+
                "\"stringKey\":\"hello world!\","+
                "\"complexStringKey\":\"h\be\tllo w\u1234orld!\","+
                "\"intKey\":42,"+
                "\"doubleKey\":-23.45e67"+
            "}";
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("trueKey", new Boolean(true));
        jsonMap.put("falseKey", new Boolean(false));
        jsonMap.put("stringKey", "hello world!");
        jsonMap.put("complexStringKey", "h\be\tllo w\u1234orld!");
        jsonMap.put("intKey", new Long(42));
        jsonMap.put("doubleKey", new Double(-23.45e67));

        JSONObject jsonObject = new JSONObject(jsonMap);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void jsonObjectByBean() {
        String expectedStr = 
            "{"+
                "\"trueKey\":true,"+
                "\"falseKey\":false,"+
                "\"stringKey\":\"hello world!\","+
                "\"complexStringKey\":\"h\be\tllo w\u1234orld!\","+
                "\"intKey\":42,"+
                "\"doubleKey\":-23.45e7"+
            "}";
        MyBean myBean = new MyBean();
        JSONObject jsonObject = new JSONObject(myBean);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void jsonObjectByBeanAndNames() {
        String expectedStr = 
            "{"+
                "\"trueKey\":true,"+
                "\"complexStringKey\":\"h\be\tllo w\u1234orld!\","+
                "\"doubleKey\":-23.45e7"+
            "}";
        String[] keys = {"trueKey", "complexStringKey", "doubleKey"};
        MyBean myBean = new MyBean();
        JSONObject jsonObject = new JSONObject(myBean, keys);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void jsonObjectByResourceBundle() {
        String expectedStr = 
            "{"+
                "\"greetings\": {"+
                    "\"hello\":\"Hello, \","+
                    "\"world\":\"World!\""+
                "},"+
                "\"farewells\": {"+
                    "\"later\":\"Later, \","+
                    "\"gator\":\"Alligator!\""+
                "}"+
            "}";
        JSONObject jsonObject = new 
                JSONObject("org.json.junit.StringsResourceBundle",
                        Locale.getDefault());
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void jsonObjectAccumulate() {
        String expectedStr = 
            "{"+
                "\"myArray\": ["+
                    "true,"+
                    "false,"+
                    "\"hello world!\","+
                    "\"h\be\tllo w\u1234orld!\","+
                    "42,"+
                    "-23.45e7"+
                "]"+
            "}";
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("myArray", true);
        jsonObject.accumulate("myArray", false);
        jsonObject.accumulate("myArray", "hello world!");
        jsonObject.accumulate("myArray", "h\be\tllo w\u1234orld!");
        jsonObject.accumulate("myArray", 42);
        jsonObject.accumulate("myArray", -23.45e7);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void jsonObjectAppend() {
        String expectedStr = 
            "{"+
                "\"myArray\": ["+
                    "true,"+
                    "false,"+
                    "\"hello world!\","+
                    "\"h\be\tllo w\u1234orld!\","+
                    "42,"+
                    "-23.45e7"+
                "]"+
            "}";
        JSONObject jsonObject = new JSONObject();
        jsonObject.append("myArray", true);
        jsonObject.append("myArray", false);
        jsonObject.append("myArray", "hello world!");
        jsonObject.append("myArray", "h\be\tllo w\u1234orld!");
        jsonObject.append("myArray", 42);
        jsonObject.append("myArray", -23.45e7);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void jsonObjectDoubleToString() {
        String [] expectedStrs = {"1", "1", "-23.4", "-2.345E68" };
        Double [] doubles = { 1.0, 00001.00000, -23.4, -23.45e67 }; 
        for (int i = 0; i < expectedStrs.length; ++i) {
            String actualStr = JSONObject.doubleToString(doubles[i]);
            assertTrue("value expected ["+expectedStrs[i]+
                    "] found ["+actualStr+ "]",
                    expectedStrs[i].equals(actualStr));
        }
    }

    @Test
    public void jsonObjectValues() {
        String str = 
            "{"+
                "\"trueKey\":true,"+
                "\"falseKey\":false,"+
                "\"trueStrKey\":\"true\","+
                "\"falseStrKey\":\"false\","+
                "\"stringKey\":\"hello world!\","+
                "\"complexStringKey\":\"h\be\tllo w\u1234orld!\","+
                "\"intKey\":42,"+
                "\"intStrKey\":\"43\","+
                "\"longKey\":1234567890123456789,"+
                "\"longStrKey\":\"987654321098765432\","+
                "\"doubleKey\":-23.45e7,"+
                "\"doubleStrKey\":\"00001.000\","+
                "\"arrayKey\":[0,1,2],"+
                "\"objectKey\":{\"myKey\":\"myVal\"}"+
            "}";
        JSONObject jsonObject = new JSONObject(str);
        assertTrue("trueKey should be true", jsonObject.getBoolean("trueKey"));
        assertTrue("falseKey should be false", !jsonObject.getBoolean("falseKey"));
        assertTrue("trueStrKey should be true", jsonObject.getBoolean("trueStrKey"));
        assertTrue("falseStrKey should be false", !jsonObject.getBoolean("falseStrKey"));
        assertTrue("doubleKey should be double", 
                jsonObject.getDouble("doubleKey") == -23.45e7);
        assertTrue("doubleStrKey should be double", 
                jsonObject.getDouble("doubleStrKey") == 1);
        assertTrue("intKey should be int", 
                jsonObject.getInt("intKey") == 42);
        assertTrue("intStrKey should be int", 
                jsonObject.getInt("intStrKey") == 43);
        assertTrue("longKey should be long", 
                jsonObject.getLong("longKey") == 1234567890123456789L);
        assertTrue("longStrKey should be long", 
                jsonObject.getLong("longStrKey") == 987654321098765432L);
        JSONArray jsonArray = jsonObject.getJSONArray("arrayKey");
        assertTrue("arrayKey should be JSONArray", 
                jsonArray.getInt(0) == 0 &&
                jsonArray.getInt(1) == 1 &&
                jsonArray.getInt(2) == 2);
        JSONObject jsonObjectInner = jsonObject.getJSONObject("objectKey");
        assertTrue("objectKey should be JSONObject", 
                jsonObjectInner.get("myKey").equals("myVal"));
    }

    @Test
    public void jsonObjectNames() {
        String str = 
            "{"+
                "\"trueKey\":true,"+
                "\"falseKey\":false,"+
                "\"stringKey\":\"hello world!\","+
            "}";
        String [] expectedNames = {"trueKey", "falseKey", "stringKey"};
        JSONObject jsonObject = new JSONObject(str);
        String [] names = JSONObject.getNames(jsonObject);
        Util.compareActualVsExpectedStringArrays(names, expectedNames);
    }

}
