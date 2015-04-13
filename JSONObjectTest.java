package org.json.junit;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.json.*;
import org.junit.*;

class MyJsonString implements JSONString {

    @Override
    public String toJSONString() {
        return "my string";
    }
}

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
        assertTrue("opt trueKey should be true", jsonObject.optBoolean("trueKey"));
        assertTrue("falseKey should be false", !jsonObject.getBoolean("falseKey"));
        assertTrue("trueStrKey should be true", jsonObject.getBoolean("trueStrKey"));
        assertTrue("trueStrKey should be true", jsonObject.optBoolean("trueStrKey"));
        assertTrue("falseStrKey should be false", !jsonObject.getBoolean("falseStrKey"));
        assertTrue("stringKey should be string",
            jsonObject.getString("stringKey").equals("hello world!"));
        assertTrue("doubleKey should be double", 
                jsonObject.getDouble("doubleKey") == -23.45e7);
        assertTrue("doubleStrKey should be double", 
                jsonObject.getDouble("doubleStrKey") == 1);
        assertTrue("opt doubleKey should be double", 
                jsonObject.optDouble("doubleKey") == -23.45e7);
        assertTrue("opt doubleKey with Default should be double", 
                jsonObject.optDouble("doubleStrKey", Double.NaN) == 1);
        assertTrue("intKey should be int", 
                jsonObject.optInt("intKey") == 42);
        assertTrue("opt intKey should be int", 
                jsonObject.optInt("intKey", 0) == 42);
        assertTrue("opt intKey with default should be int", 
                jsonObject.getInt("intKey") == 42);
        assertTrue("intStrKey should be int", 
                jsonObject.getInt("intStrKey") == 43);
        assertTrue("longKey should be long", 
                jsonObject.getLong("longKey") == 1234567890123456789L);
        assertTrue("opt longKey should be long", 
                jsonObject.optLong("longKey") == 1234567890123456789L);
        assertTrue("opt longKey with default should be long", 
                jsonObject.optLong("longKey", 0) == 1234567890123456789L);
        assertTrue("longStrKey should be long", 
                jsonObject.getLong("longStrKey") == 987654321098765432L);
        assertTrue("xKey should not exist",
                jsonObject.isNull("xKey"));
        assertTrue("stringKey should exist",
                jsonObject.has("stringKey"));
        assertTrue("opt stringKey should string",
                jsonObject.optString("stringKey").equals("hello world!"));
        assertTrue("opt stringKey with default should string",
                jsonObject.optString("stringKey", "not found").equals("hello world!"));
        JSONArray jsonArray = jsonObject.getJSONArray("arrayKey");
        assertTrue("arrayKey should be JSONArray", 
                jsonArray.getInt(0) == 0 &&
                jsonArray.getInt(1) == 1 &&
                jsonArray.getInt(2) == 2);
        jsonArray = jsonObject.optJSONArray("arrayKey");
        assertTrue("opt arrayKey should be JSONArray", 
                jsonArray.getInt(0) == 0 &&
                jsonArray.getInt(1) == 1 &&
                jsonArray.getInt(2) == 2);
        JSONObject jsonObjectInner = jsonObject.getJSONObject("objectKey");
        assertTrue("objectKey should be JSONObject", 
                jsonObjectInner.get("myKey").equals("myVal"));
    }

    @Test
    public void jsonObjectNonAndWrongValues() {
        String str = 
            "{"+
                "\"trueKey\":true,"+
                "\"falseKey\":false,"+
                "\"trueStrKey\":\"true\","+
                "\"falseStrKey\":\"false\","+
                "\"stringKey\":\"hello world!\","+
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
        int tryCount = 0;
        int exceptionCount = 0;
        try {
            ++tryCount;
            jsonObject.getBoolean("nonKey");
        } catch (JSONException ignore) { ++exceptionCount; }
        try {
            ++tryCount;
            jsonObject.getBoolean("stringKey");
        } catch (JSONException ignore) { ++exceptionCount; }
        try {
            ++tryCount;
            jsonObject.getString("nonKey");
        } catch (JSONException ignore) { ++exceptionCount; }
        try {
            ++tryCount;
            jsonObject.getString("trueKey");
        } catch (JSONException ignore) { ++exceptionCount; }
        try {
            ++tryCount;
            jsonObject.getDouble("nonKey");
        } catch (JSONException ignore) { ++exceptionCount; }
        try {
            ++tryCount;
            jsonObject.getDouble("stringKey");
        } catch (JSONException ignore) { ++exceptionCount; }
        try {
            ++tryCount;
            jsonObject.getInt("nonKey");
        } catch (JSONException ignore) { ++exceptionCount; }
        try {
            ++tryCount;
            jsonObject.getInt("stringKey");
        } catch (JSONException ignore) { ++exceptionCount; }
        try {
            ++tryCount;
            jsonObject.getLong("nonKey");
        } catch (JSONException ignore) { ++exceptionCount; }
        try {
            ++tryCount;
            jsonObject.getLong("stringKey");
        } catch (JSONException ignore) { ++exceptionCount; }
        try {
            ++tryCount;
            jsonObject.getJSONArray("nonKey");
        } catch (JSONException ignore) { ++exceptionCount; }
        try {
            ++tryCount;
            jsonObject.getJSONArray("stringKey");
        } catch (JSONException ignore) { ++exceptionCount; }
        try {
            ++tryCount;
            jsonObject.getJSONObject("nonKey");
        } catch (JSONException ignore) { ++exceptionCount; }
        try {
            ++tryCount;
            jsonObject.getJSONObject("stringKey");
        } catch (JSONException ignore) { ++exceptionCount; }
        assertTrue("all get calls should have failed",
                exceptionCount == tryCount);
    }

    @Test
    public void jsonObjectNames() {

        // getNames() from null JSONObject
        assertTrue("null names from null Object", 
                null == JSONObject.getNames((Object)null));

        // getNames() from object with no fields
        assertTrue("null names from Object with no fields", 
                null == JSONObject.getNames(new MyJsonString()));

        // getNames() from empty JSONObject
        String emptyStr = "{}";
        JSONObject emptyJsonObject = new JSONObject(emptyStr);
        assertTrue("empty JSONObject should have null names",
                null == JSONObject.getNames(emptyJsonObject));

        // getNames() from JSONObject
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

    @Test
    public void jsonObjectNamesToJsonAray() {
        String str = 
            "{"+
                "\"trueKey\":true,"+
                "\"falseKey\":false,"+
                "\"stringKey\":\"hello world!\","+
            "}";
        String [] expectedNames = {"trueKey", "falseKey", "stringKey" };

        JSONObject jsonObject = new JSONObject(str);
        JSONArray jsonArray = jsonObject.names();
        /**
         * Cannot really compare to an expected JSONArray because the ordering
         * of the JSONObject keys is not fixed, and JSONArray comparisons
         * presume fixed. Since this test is limited to key strings, a 
         * string comparison will have to suffice.
         */
        String namesStr = jsonArray.toString();
        // remove square brackets, commas, and spaces
        namesStr = namesStr.replaceAll("[\\]|\\[|\"]", "");
        String [] names = namesStr.split(",");

        Util.compareActualVsExpectedStringArrays(names, expectedNames);
    }

    @Test
    public void objectNames() {
        MyBean myBean = new MyBean();
        String [] expectedNames = {"intKey", "doubleKey", "stringKey", 
                "complexStringKey", "trueKey", "falseKey"};
        String [] names = JSONObject.getNames(myBean);
        Util.compareActualVsExpectedStringArrays(names, expectedNames);
    }

    @Test
    public void jsonObjectIncrement() {
        String str = 
            "{"+
                "\"keyLong\":9999999991,"+
                "\"keyDouble\":1.1,"+
             "}";
        String expectedStr = 
        "{"+
            "\"keyInt\":3,"+
            "\"keyLong\":9999999993,"+
            "\"keyDouble\":3.1,"+
        "}";
        JSONObject jsonObject = new JSONObject(str);
        jsonObject.increment("keyInt");
        jsonObject.increment("keyInt");
        jsonObject.increment("keyLong");
        jsonObject.increment("keyDouble");
        jsonObject.increment("keyInt");
        jsonObject.increment("keyLong");
        jsonObject.increment("keyDouble");
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void jsonObjectNamesToArray() {
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

    @Test
    public void jsonObjectNumberToString() {
        String str;
        Double dVal;
        Integer iVal = 1;
        str = JSONObject.numberToString(iVal);
        assertTrue("expected "+iVal+" actual "+str, iVal.toString().equals(str));
        dVal = 12.34;
        str = JSONObject.numberToString(dVal);
        assertTrue("expected "+dVal+" actual "+str, dVal.toString().equals(str));
        dVal = 12.34e27;
        str = JSONObject.numberToString(dVal);
        assertTrue("expected "+dVal+" actual "+str, dVal.toString().equals(str));
        // trailing .0 is truncated, so it doesn't quite match toString()
        dVal = 5000000.0000000;
        str = JSONObject.numberToString(dVal);
        assertTrue("expected 5000000 actual "+str, str.equals("5000000"));
    }

    @Test
    public void jsonObjectPut() {
        String expectedStr = 
            "{"+
                "\"trueKey\":true,"+
                "\"falseKey\":false,"+
                "\"arrayKey\":[0,1,2],"+
                "\"objectKey\":{"+
                    "\"myKey1\":\"myVal1\","+
                    "\"myKey2\":\"myVal2\","+
                    "\"myKey3\":\"myVal3\","+
                    "\"myKey4\":\"myVal4\""+
                "}"+
            "}";
        String expectedStrAfterRemoval = 
                "{"+
                    "\"falseKey\":false,"+
                    "\"arrayKey\":[0,1,2],"+
                    "\"objectKey\":{"+
                        "\"myKey1\":\"myVal1\","+
                        "\"myKey2\":\"myVal2\","+
                        "\"myKey3\":\"myVal3\","+
                        "\"myKey4\":\"myVal4\""+
                    "}"+
                "}";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("trueKey", true);
        jsonObject.put("falseKey", false);
        Integer [] intArray = { 0, 1, 2 };
        jsonObject.put("arrayKey", Arrays.asList(intArray));
        Map<String, Object> myMap = new HashMap<String, Object>();
        myMap.put("myKey1", "myVal1");
        myMap.put("myKey2", "myVal2");
        myMap.put("myKey3", "myVal3");
        myMap.put("myKey4", "myVal4");
        jsonObject.put("objectKey", myMap);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
        assertTrue("equal jsonObjects should be similar",
                jsonObject.similar(expectedJsonObject));

        jsonObject.remove("trueKey");
        JSONObject expectedJsonObjectAfterRemoval =
                new JSONObject(expectedStrAfterRemoval);
        Util.compareActualVsExpectedJsonObjects(jsonObject,
                expectedJsonObjectAfterRemoval);
        assertTrue("unequal jsonObjects should not be similar",
                !jsonObject.similar(expectedJsonObject));
        assertTrue("unequal Objects should not be similar",
                !jsonObject.similar(new JSONArray()));

        String aCompareValueStr = "{\"a\":\"aval\",\"b\":true}";
        String bCompareValueStr = "{\"a\":\"notAval\",\"b\":true}";
        JSONObject aCompareValueJsonObject = new JSONObject(aCompareValueStr);
        JSONObject bCompareValueJsonObject = new JSONObject(bCompareValueStr);
        assertTrue("different values should not be similar",
                !aCompareValueJsonObject.similar(bCompareValueJsonObject));

        String aCompareObjectStr = "{\"a\":\"aval\",\"b\":{}}";
        String bCompareObjectStr = "{\"a\":\"aval\",\"b\":true}";
        JSONObject aCompareObjectJsonObject = new JSONObject(aCompareObjectStr);
        JSONObject bCompareObjectJsonObject = new JSONObject(bCompareObjectStr);
        assertTrue("different nested JSONObjects should not be similar",
                !aCompareObjectJsonObject.similar(bCompareObjectJsonObject));

        String aCompareArrayStr = "{\"a\":\"aval\",\"b\":[]}";
        String bCompareArrayStr = "{\"a\":\"aval\",\"b\":true}";
        JSONObject aCompareArrayJsonObject = new JSONObject(aCompareArrayStr);
        JSONObject bCompareArrayJsonObject = new JSONObject(bCompareArrayStr);
        assertTrue("different nested JSONArrays should not be similar",
                !aCompareArrayJsonObject.similar(bCompareArrayJsonObject));
    }

    @Test
    public void jsonObjectToString() {
        String str = 
            "{"+
                "\"trueKey\":true,"+
                "\"falseKey\":false,"+
                "\"arrayKey\":[0,1,2],"+
                "\"objectKey\":{"+
                    "\"myKey1\":\"myVal1\","+
                    "\"myKey2\":\"myVal2\","+
                    "\"myKey3\":\"myVal3\","+
                    "\"myKey4\":\"myVal4\""+
                "}"+
            "}";
        JSONObject jsonObject = new JSONObject(str);
        String toStr = jsonObject.toString();
        JSONObject expectedJsonObject = new JSONObject(toStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void valueToString() {
        
        assertTrue("null valueToString() incorrect",
                "null".equals(JSONObject.valueToString(null)));
        MyJsonString jsonString = new MyJsonString();
        assertTrue("jsonstring valueToString() incorrect",
                "my string".equals(JSONObject.valueToString(jsonString)));
        assertTrue("boolean valueToString() incorrect",
                "true".equals(JSONObject.valueToString(Boolean.TRUE)));
        assertTrue("non-numeric double",
                "null".equals(JSONObject.doubleToString(Double.POSITIVE_INFINITY)));
        String jsonObjectStr = 
            "{"+
                "\"key1\":\"val1\","+
                "\"key2\":\"val2\","+
                "\"key3\":\"val3\""+
             "}";
        JSONObject jsonObject = new JSONObject(jsonObjectStr);
        assertTrue("jsonObject valueToString() incorrect",
                JSONObject.valueToString(jsonObject).equals(jsonObject.toString()));
        String jsonArrayStr = 
            "[1,2,3]";
        JSONArray jsonArray = new JSONArray(jsonArrayStr);
        assertTrue("jsonArra valueToString() incorrect",
                JSONObject.valueToString(jsonArray).equals(jsonArray.toString()));
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "val1");
        map.put("key2", "val2");
        map.put("key3", "val3");
        assertTrue("map valueToString() incorrect",
                jsonObject.toString().equals(JSONObject.valueToString(map))); 
        Collection<Integer> collection = new ArrayList<Integer>();
        collection.add(new Integer(1));
        collection.add(new Integer(2));
        collection.add(new Integer(3));
        assertTrue("collection valueToString() expected: "+
                jsonArray.toString()+ " actual: "+
                JSONObject.valueToString(collection),
                jsonArray.toString().equals(JSONObject.valueToString(collection))); 
        Integer[] array = { new Integer(1), new Integer(2), new Integer(3) };
        assertTrue("array valueToString() incorrect",
                jsonArray.toString().equals(JSONObject.valueToString(array))); 
    }

    @Test
    public void wrapObject() {
        // wrap(null) returns NULL
        assertTrue("null wrap() incorrect",
                JSONObject.NULL == JSONObject.wrap(null));

        // wrap(Integer) returns Integer
        Integer in = new Integer(1);
        assertTrue("Integer wrap() incorrect",
                in == JSONObject.wrap(in));

        // wrap JSONObject returns JSONObject
        String jsonObjectStr = 
                "{"+
                    "\"key1\":\"val1\","+
                    "\"key2\":\"val2\","+
                    "\"key3\":\"val3\""+
                 "}";
        JSONObject jsonObject = new JSONObject(jsonObjectStr);
        assertTrue("JSONObject wrap() incorrect",
                jsonObject == JSONObject.wrap(jsonObject));

        // wrap collection returns JSONArray
        Collection<Integer> collection = new ArrayList<Integer>();
        collection.add(new Integer(1));
        collection.add(new Integer(2));
        collection.add(new Integer(3));
        JSONArray jsonArray = (JSONArray)(JSONObject.wrap(collection));
        String expectedCollectionJsonArrayStr = 
                "[1,2,3]";
        JSONArray expectedCollectionJsonArray = 
                new JSONArray(expectedCollectionJsonArrayStr);
        Util.compareActualVsExpectedJsonArrays(jsonArray, 
                expectedCollectionJsonArray);

        // wrap Array returns JSONArray
        Integer[] array = { new Integer(1), new Integer(2), new Integer(3) };
        JSONArray integerArrayJsonArray = (JSONArray)(JSONObject.wrap(array));
        JSONArray expectedIntegerArrayJsonArray = new JSONArray("[1,2,3]");
        Util.compareActualVsExpectedJsonArrays(integerArrayJsonArray, 
                expectedIntegerArrayJsonArray);

        // wrap map returns JSONObject
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "val1");
        map.put("key2", "val2");
        map.put("key3", "val3");
        JSONObject mapJsonObject = (JSONObject)(JSONObject.wrap(map));
        Util.compareActualVsExpectedJsonObjects(jsonObject, mapJsonObject);

        // TODO test wrap(package)
    }

    @Test
    public void jsonObjectEquals() {
        String str = 
            "{"+
                "\"trueKey\":true,"+
                "\"falseKey\":false,"+
                "\"arrayKey\":[0,1,2],"+
                "\"objectKey\":{"+
                    "\"myKey1\":\"myVal1\","+
                    "\"myKey2\":\"myVal2\","+
                    "\"myKey3\":\"myVal3\","+
                    "\"myKey4\":\"myVal4\""+
                "}"+
            "}";
        String diffStr = 
                "{"+
                    "\"arrayKey\":[0]"+
                "}";
        JSONObject jsonObject = new JSONObject(str);
        JSONObject otherJsonObject = new JSONObject(str);
        JSONObject unequalJsonObject = new JSONObject(diffStr);
        assertTrue("equal JSONObjects should be equal",
                jsonObject.equals(otherJsonObject));
        assertTrue("unequal JSONObjects should not be equal",
                !jsonObject.equals(unequalJsonObject));

        // make sure hashcode returns some interesting value
        assertTrue("equal JSONObjects should have equal hashCode",
                jsonObject.hashCode() == otherJsonObject.hashCode());
        assertTrue("unequal JSONObjects should have unequal hashCode",
                jsonObject.hashCode() != unequalJsonObject.hashCode());
    }

    @Test
    public void jsonObjectParsingErrors() {
        int tryCount = 0;
        int exceptionCount = 0;
        try {
            // does not start with '{'
            ++tryCount;
            String str = "abc";
            new JSONObject(str);
        } catch (JSONException ignore) {++exceptionCount; }
        try {
            // does not end with '}'
            ++tryCount;
            String str = "{";
            new JSONObject(str);
        } catch (JSONException ignore) {++exceptionCount; }
        try {
            // key with no ':'
            ++tryCount;
            String str = "{\"myKey\" = true}";
            new JSONObject(str);
        } catch (JSONException ignore) {++exceptionCount; }
        try {
            // entries with no ',' separator
            ++tryCount;
            String str = "{\"myKey\":true \"myOtherKey\":false}";
            new JSONObject(str);
        } catch (JSONException ignore) {++exceptionCount; }
        try {
            // append to wrong key
            ++tryCount;
            String str = "{\"myKey\":true, \"myOtherKey\":false}";
            JSONObject jsonObject = new JSONObject(str);
            jsonObject.append("myKey", "hello");
        } catch (JSONException ignore) {++exceptionCount; }
        try {
            // increment wrong key
            ++tryCount;
            String str = "{\"myKey\":true, \"myOtherKey\":false}";
            JSONObject jsonObject = new JSONObject(str);
            jsonObject.increment("myKey");
        } catch (JSONException ignore) {++exceptionCount; }
        try {
            // invalid key
            ++tryCount;
            String str = "{\"myKey\":true, \"myOtherKey\":false}";
            JSONObject jsonObject = new JSONObject(str);
            jsonObject.get(null);
        } catch (JSONException ignore) {++exceptionCount; }
        try {
            // invalid numberToString()
            ++tryCount;
            JSONObject.numberToString((Number)null);
        } catch (JSONException ignore) {++exceptionCount; }
        try {
            // null put key 
            ++tryCount;
            JSONObject jsonObject = new JSONObject("{}");
            jsonObject.put(null, 0);
        } catch (NullPointerException ignore) {++exceptionCount; }
        try {
            // multiple putOnce key 
            ++tryCount;
            JSONObject jsonObject = new JSONObject("{}");
            jsonObject.putOnce("hello", "world");
            jsonObject.putOnce("hello", "world!");
        } catch (JSONException ignore) {++exceptionCount; }
        try {
            // test validity of invalid double 
            ++tryCount;
            JSONObject.testValidity(Double.NaN);
        } catch (JSONException ignore) {++exceptionCount; }
        try {
            // test validity of invalid float 
            ++tryCount;
            JSONObject.testValidity(Float.NEGATIVE_INFINITY);
        } catch (JSONException ignore) {++exceptionCount; }

        assertTrue("all tries should have failed",
                exceptionCount == tryCount);
    }

    @Test
    public void jsonObjectOptDefault() {

        String str = "{\"myKey\": \"myval\"}";
        JSONObject jsonObject = new JSONObject(str);

        assertTrue("optBoolean() should return default boolean",
                Boolean.TRUE == jsonObject.optBoolean("myKey", Boolean.TRUE));
        assertTrue("optInt() should return default int",
                42 == jsonObject.optInt("myKey", 42));
        assertTrue("optInt() should return default int",
                42 == jsonObject.optInt("myKey", 42));
        assertTrue("optLong() should return default long",
                42 == jsonObject.optLong("myKey", 42));
        assertTrue("optDouble() should return default double",
                42.3 == jsonObject.optDouble("myKey", 42.3));
        assertTrue("optString() should return default string",
                "hi".equals(jsonObject.optString("hiKey", "hi")));
    }

    @Test
    public void jsonObjectputNull() {

        // put null should remove the item.
        String str = "{\"myKey\": \"myval\"}";
        JSONObject jsonObjectRemove = new JSONObject(str);
        JSONObject jsonObjectPutNull = new JSONObject(str);
        jsonObjectRemove.remove("myKey");
        jsonObjectPutNull.put("myKey", (Object)null);
        Util.compareActualVsExpectedJsonObjects(jsonObjectRemove, jsonObjectPutNull);
        assertTrue("jsonObject should be empty",
                jsonObjectRemove.length() == 0 &&
                jsonObjectPutNull.length() == 0);
    }

    @Test
    public void jsonObjectQuote() {
        String str;
        str = "";
        String quotedStr;
        quotedStr = JSONObject.quote(str);
        assertTrue("quote() expected escaped quotes, found "+quotedStr,
                "\"\"".equals(quotedStr));
        str = "\"\"";
        quotedStr = JSONObject.quote(str);
        assertTrue("quote() expected escaped quotes, found "+quotedStr,
                "\"\\\"\\\"\"".equals(quotedStr));
        str = "</";
        quotedStr = JSONObject.quote(str);
        assertTrue("quote() expected escaped frontslash, found "+quotedStr,
                "\"<\\/\"".equals(quotedStr));
        str = "AB\bC";
        quotedStr = JSONObject.quote(str);
        assertTrue("quote() expected escaped backspace, found "+quotedStr,
                "\"AB\\bC\"".equals(quotedStr));
        str = "ABC\n";
        quotedStr = JSONObject.quote(str);
        assertTrue("quote() expected escaped newline, found "+quotedStr,
                "\"ABC\\n\"".equals(quotedStr));
        str = "AB\fC";
        quotedStr = JSONObject.quote(str);
        assertTrue("quote() expected escaped formfeed, found "+quotedStr,
                "\"AB\\fC\"".equals(quotedStr));
        str = "\r";
        quotedStr = JSONObject.quote(str);
        assertTrue("quote() expected escaped return, found "+quotedStr,
                "\"\\r\"".equals(quotedStr));
        str = "\u1234\u0088";
        quotedStr = JSONObject.quote(str);
        assertTrue("quote() expected escaped unicode, found "+quotedStr,
                "\"\u1234\\u0088\"".equals(quotedStr));
    }

    @Test
    public void stringToValue() {
        String str = "";
        String valueStr = (String)(JSONObject.stringToValue(str));
        assertTrue("stringToValue() expected empty String, found "+valueStr,
                "".equals(valueStr));
    }

    @Test
    public void toJSONArray() {
        assertTrue("toJSONArray() with null names should be null",
                null == new JSONObject().toJSONArray(null));
    }

    @Test
    public void write() {
        String str = "{\"key\":\"value\"}";
        String expectedStr = str;
        JSONObject jsonObject = new JSONObject(str);
        StringWriter stringWriter = new StringWriter();
        Writer writer = jsonObject.write(stringWriter);
        String actualStr = writer.toString();
        assertTrue("write() expected " +expectedStr+
                "but found " +actualStr,
                expectedStr.equals(actualStr));
    }

    @Test
    public void equals() {
        String str = "{\"key\":\"value\"}";
        JSONObject aJsonObject = new JSONObject(str);
        JSONObject bJsonObject = new JSONObject(str);
        assertTrue("Same JSONObject should be equal to itself",
                aJsonObject.equals(aJsonObject));
        assertTrue("JSONObjects with equal content should be equal",
                aJsonObject.equals(bJsonObject));
        assertTrue("JSONObjects should not be equal to non J",
                !aJsonObject.equals(new String()));
    }
}

