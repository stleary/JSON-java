package org.json.junit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import org.json.*;
import org.junit.*;

/**
 * These classes will be used for testing
 */
class MyJsonString implements JSONString {

    @Override
    public String toJSONString() {
        return "my string";
    }
};

interface MyBean {
    public Integer getIntKey();
    public Double getDoubleKey();
    public String getStringKey();
    public String getEscapeStringKey();
    public Boolean isTrueKey();
    public Boolean isFalseKey();
    public StringReader getStringReaderKey();
};

/**
 * JSONObject, along with JSONArray, are the central classes of the reference app.
 * All of the other classes interact with it and JSON functionality would be
 * impossible without it.
 */
public class JSONObjectTest {
    /**
     * Need a class with some public data members for testing, so
     * JSONObjectTest is chosen.
     */
    public Integer publicInt = 42;
    public String publicString = "abc";


    @Test
    public void emptyJsonObject() {
        /**
         * A JSONObject can be created with no content
         */
        JSONObject jsonObject = new JSONObject();
        assertTrue("jsonObject should be empty", jsonObject.length() == 0);
    }

    @Test
    public void jsonObjectByNames() {
        /**
         * A JSONObject can be created from another JSONObject plus a list of names.
         * In this test, some of the starting JSONObject keys are not in the 
         * names list.
         */
        String str = 
            "{"+
                "\"trueKey\":true,"+
                "\"falseKey\":false,"+
                "\"nullKey\":null,"+
                "\"stringKey\":\"hello world!\","+
                "\"escapeStringKey\":\"h\be\tllo w\u1234orld!\","+
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

    /**
     * the JSONObject(JsonTokener) ctor is not tested directly since it already
     * has full coverage from other tests.
     */

    @Test
    public void jsonObjectByNullMap() {
        /**
         * JSONObjects can be built from a Map<String, Object>. 
         * In this test the map is null.
         */
        Map<String, Object> map = null;
        JSONObject jsonObject = new JSONObject(map);
        JSONObject expectedJsonObject = new JSONObject();
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void jsonObjectByMap() {
        /**
         * JSONObjects can be built from a Map<String, Object>. 
         * In this test all of the map entries are valid JSON types.
         */
        String expectedStr = 
            "{"+
                "\"trueKey\":true,"+
                "\"falseKey\":false,"+
                "\"stringKey\":\"hello world!\","+
                "\"escapeStringKey\":\"h\be\tllo w\u1234orld!\","+
                "\"intKey\":42,"+
                "\"doubleKey\":-23.45e67"+
            "}";
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("trueKey", new Boolean(true));
        jsonMap.put("falseKey", new Boolean(false));
        jsonMap.put("stringKey", "hello world!");
        jsonMap.put("escapeStringKey", "h\be\tllo w\u1234orld!");
        jsonMap.put("intKey", new Long(42));
        jsonMap.put("doubleKey", new Double(-23.45e67));

        JSONObject jsonObject = new JSONObject(jsonMap);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void jsonObjectByMapWithUnsupportedValues() {
        /**
         * JSONObjects can be built from a Map<String, Object>. 
         * In this test the map entries are not valid JSON types.
         * The actual conversion is kind of interesting.
         */
        String expectedStr = 
            "{"+
                "\"key1\":{},"+
                "\"key2\":\"java.lang.Exception\""+
            "}";
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        // Just insert some random objects
        jsonMap.put("key1", new CDL());
        jsonMap.put("key2", new Exception());

        JSONObject jsonObject = new JSONObject(jsonMap);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void jsonObjectByMapWithNullValue() {
        /**
         * JSONObjects can be built from a Map<String, Object>. 
         * In this test one of the map values is null 
         */
        String expectedStr = 
            "{"+
                "\"trueKey\":true,"+
                "\"falseKey\":false,"+
                "\"stringKey\":\"hello world!\","+
                "\"escapeStringKey\":\"h\be\tllo w\u1234orld!\","+
                "\"intKey\":42,"+
                "\"doubleKey\":-23.45e67"+
            "}";
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("trueKey", new Boolean(true));
        jsonMap.put("falseKey", new Boolean(false));
        jsonMap.put("stringKey", "hello world!");
        jsonMap.put("nullKey",  null);
        jsonMap.put("escapeStringKey", "h\be\tllo w\u1234orld!");
        jsonMap.put("intKey", new Long(42));
        jsonMap.put("doubleKey", new Double(-23.45e67));

        JSONObject jsonObject = new JSONObject(jsonMap);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test(expected=NullPointerException.class)
    public void jsonObjectByNullBean() {
        /**
         * JSONObject built from a bean, but only using a null value.
         * Nothing good is expected to happen.
         */
        MyBean myBean = null;
        new JSONObject(myBean);
    }

    @Test
    public void jsonObjectByBean() {
        /**
         * JSONObject built from a bean. In this case all but one of the 
         * bean getters return valid JSON types
         */
        String expectedStr = 
            "{"+
                "\"trueKey\":true,"+
                "\"falseKey\":false,"+
                "\"stringKey\":\"hello world!\","+
                "\"escapeStringKey\":\"h\be\tllo w\u1234orld!\","+
                "\"intKey\":42,"+
                "\"doubleKey\":-23.45e7,"+
                "\"stringReaderKey\":{},"+
                "\"callbacks\":[{\"handler\":{}},{}]"+ // sorry, mockito artifact
            "}";

        /**
         * Default access classes have to be mocked since JSONObject, which is
         * not in the same package, cannot call MyBean methods by reflection.
         */
        MyBean myBean = mock(MyBean.class);
        when(myBean.getDoubleKey()).thenReturn(-23.45e7);
        when(myBean.getIntKey()).thenReturn(42);
        when(myBean.getStringKey()).thenReturn("hello world!");
        when(myBean.getEscapeStringKey()).thenReturn("h\be\tllo w\u1234orld!");
        when(myBean.isTrueKey()).thenReturn(true);
        when(myBean.isFalseKey()).thenReturn(false);
        when(myBean.getStringReaderKey()).thenReturn(
            new StringReader("") {
                /**
                 * TODO: Need to understand why returning a string
                 * turns "this" into an empty JSONObject,
                 * but not overriding turns "this" into a string.
                 */
                @Override
                public String toString(){
                    return "Whatever";
                }
            });

        JSONObject jsonObject = new JSONObject(myBean);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void jsonObjectByObjectAndNames() {
        /**
         * A bean is also an object. But in order to test the JSONObject
         * ctor that takes an object and a list of names, 
         * this particular bean needs some public
         * data members, which have been added to the class.
         */
        String expectedStr = 
            "{"+
                "\"publicString\":\"abc\","+
                "\"publicInt\":42"+
            "}";
        String[] keys = {"publicString", "publicInt"};
        // just need a class that has public data members
        JSONObjectTest jsonObjectTest = new JSONObjectTest();
        JSONObject jsonObject = new JSONObject(jsonObjectTest, keys);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void jsonObjectByResourceBundle() {
        // TODO: how to improve resource bundle testing?
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
        // TODO: should include an unsupported object
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
        // TODO: should include an unsupported object
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
        String [] expectedStrs = {"1", "1", "-23.4", "-2.345E68", "null", "null" };
        Double [] doubles = { 1.0, 00001.00000, -23.4, -23.45e67, 
                Double.NaN, Double.NEGATIVE_INFINITY }; 
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

    // improving unit tests left off here

    @Test

    public void stringToValueNumbersTest() {
        // Check if library handles large or high precision numbers correctly
        assertTrue( "0.2 should be a Double!",
                JSONObject.stringToValue( "0.2" ) instanceof Double );
        assertTrue( "Doubles should be Doubles, even when incorrectly converting floats!",
                JSONObject.stringToValue( new Double( "0.2f" ).toString() ) instanceof Double );
        /**
         * This test documents a need for BigDecimal conversion.
         */
        Object obj = JSONObject.stringToValue( "299792.457999999984" );
        assertTrue( "evaluates to 299792.458 doubld instead of 299792.457999999984 BigDecimal!",
                 obj.equals(new Double(299792.458)) );
        assertTrue( "1 should be an Integer!",
                JSONObject.stringToValue( "1" ) instanceof Integer );
        assertTrue( "Integer.MAX_VALUE should still be an Integer!",
                JSONObject.stringToValue( new Integer( Integer.MAX_VALUE ).toString() ) instanceof Integer );
        assertTrue( "Large integers should be a Long!",
                JSONObject.stringToValue( new Long( Long.sum( Integer.MAX_VALUE, 1 ) ).toString() ) instanceof Long );
        assertTrue( "Long.MAX_VALUE should still be an Integer!",
                JSONObject.stringToValue( new Long( Long.MAX_VALUE ).toString() ) instanceof Long );

        String str = new BigInteger( new Long( Long.MAX_VALUE ).toString() ).add( BigInteger.ONE ).toString();
        assertTrue( "Really large integers currently evaluate to string",
                JSONObject.stringToValue(str).equals("9223372036854775808"));
    }

    @Test
    /**
     * This test documents numeric values which could be numerically
     * handled as BigDecimal or BigInteger. It helps determine what outputs
     * will change if those types are supported.
     */
    public void jsonValidNumberValuesNeitherLongNorIEEE754Compatible() {
        // Valid JSON Numbers, probably should return BigDecimal or BigInteger objects
        String str = 
            "{"+
                "\"numberWithDecimals\":299792.457999999984,"+
                "\"largeNumber\":12345678901234567890,"+
                "\"preciseNumber\":0.2000000000000000111,"+
                "\"largeExponent\":-23.45e2327"+
            "}";
        JSONObject jsonObject = new JSONObject(str);
        // Comes back as a double, but loses precision
        assertTrue( "numberWithDecimals currently evaluates to double 299792.458",
                jsonObject.get( "numberWithDecimals" ).equals( new Double( "299792.458" ) ) );
        Object obj = jsonObject.get( "largeNumber" );
        assertTrue("largeNumber currently evaluates to string",
                "12345678901234567890".equals(obj));
        // comes back as a double but loses precision
        assertTrue( "preciseNumber currently evaluates to double 0.2",
                jsonObject.get( "preciseNumber" ).equals(new Double(0.2)));
        obj = jsonObject.get( "largeExponent" );
        assertTrue("largeExponent should currently evaluates as a string",
                "-23.45e2327".equals(obj));
    }

    /**
     * This test documents how JSON-Java handles invalid numeric input.
     */
    @Test
    public void jsonInvalidNumberValues() {
    	// Number-notations supported by Java and invalid as JSON
        String str = 
            "{"+
                "\"hexNumber\":-0x123,"+
                "\"tooManyZeros\":00,"+
                "\"negativeInfinite\":-Infinity,"+
                "\"negativeNaN\":-NaN,"+
                "\"negativeFraction\":-.01,"+
                "\"tooManyZerosFraction\":00.001,"+
                "\"negativeHexFloat\":-0x1.fffp1,"+
                "\"hexFloat\":0x1.0P-1074,"+
                "\"floatIdentifier\":0.1f,"+
                "\"doubleIdentifier\":0.1d"+
            "}";
        JSONObject jsonObject = new JSONObject(str);
        Object obj;
        obj = jsonObject.get( "hexNumber" );
        assertFalse( "hexNumber must not be a number (should throw exception!?)",
                 obj instanceof Number );
        assertTrue("hexNumber currently evaluates to string",
                obj.equals("-0x123"));
        assertTrue( "tooManyZeros currently evaluates to string",
                jsonObject.get( "tooManyZeros" ).equals("00"));
        obj = jsonObject.get("negativeInfinite");
        assertTrue( "negativeInfinite currently evaluates to string",
                obj.equals("-Infinity"));
        obj = jsonObject.get("negativeNaN");
        assertTrue( "negativeNaN currently evaluates to string",
                obj.equals("-NaN"));
        assertTrue( "negativeFraction currently evaluates to double -0.01",
                jsonObject.get( "negativeFraction" ).equals(new Double(-0.01)));
        assertTrue( "tooManyZerosFraction currently evaluates to double 0.001",
                jsonObject.get( "tooManyZerosFraction" ).equals(new Double(0.001)));
        assertTrue( "negativeHexFloat currently evaluates to double -3.99951171875",
                jsonObject.get( "negativeHexFloat" ).equals(new Double(-3.99951171875)));
        assertTrue("hexFloat currently evaluates to double 4.9E-324",
                jsonObject.get("hexFloat").equals(new Double(4.9E-324)));
        assertTrue("floatIdentifier currently evaluates to double 0.1",
                jsonObject.get("floatIdentifier").equals(new Double(0.1)));
        assertTrue("doubleIdentifier currently evaluates to double 0.1",
                jsonObject.get("doubleIdentifier").equals(new Double(0.1)));
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
    public void emptyJsonObjectNamesToJsonAray() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = jsonObject.names();
        assertTrue("jsonArray should be null", jsonArray == null);
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
        /**
         * A bean is also an object. But in order to test the static
         * method getNames(), this particular bean needs some public
         * data members, which have been added to the class.
         */
        JSONObjectTest jsonObjectTest = new JSONObjectTest();
        String [] expectedNames = {"publicString", "publicInt"};
        String [] names = JSONObject.getNames(jsonObjectTest);
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
            // TODO: not sure if this will work on other platforms

            // Should work the same way on any platform! @see https://docs.oracle.com/javase/specs/jls/se7/html/jls-4.html#jls-4.2.3
            // This is the effect of a float to double conversion and is inherent to the shortcomings of the IEEE 754 format, when
            // converting 32-bit into double-precision 64-bit.
            // Java type-casts float to double. A 32 bit float is type-casted to 64 bit double by simply appending zero-bits to the
            // mantissa (and extended the signed exponent by 3 bits.) and there is no way to obtain more information than it is
            // stored in the 32-bits float.

            // Like 1/3 cannot be represented as base10 number because it is periodically, 1/5 (for example) cannot be represented
            // as base2 number since it is periodically in base2 (take a look at http://www.h-schmidt.net/FloatConverter/)
            // The same happens to 3.1, that decimal number (base10 representation) is periodic in base2 representation, therefore
            // appending zero-bits is inaccurate. Only repeating the periodically occuring bits (0110) would be a proper conversion.
            // However one cannot detect from a 32 bit IEE754 representation which bits would "repeat infinitely", since the missing
            // bits would not fit into the 32 bit float, i.e. the information needed simply is not there!
            "\"keyFloat\":3.0999999046325684,"+
        "}";
        JSONObject jsonObject = new JSONObject(str);
        jsonObject.increment("keyInt");
        jsonObject.increment("keyInt");
        jsonObject.increment("keyLong");
        jsonObject.increment("keyDouble");
        jsonObject.increment("keyInt");
        jsonObject.increment("keyLong");
        jsonObject.increment("keyDouble");
        jsonObject.put("keyFloat", new Float(1.1));
        jsonObject.increment("keyFloat");
        jsonObject.increment("keyFloat");
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);

		/*
        float f = 3.1f;
        double df = (double) f;
        double d = 3.1d;
        System.out.println(Integer.toBinaryString(Float.floatToRawIntBits(f)));
        System.out.println(Long.toBinaryString(Double.doubleToRawLongBits(df)));
        System.out.println(Long.toBinaryString(Double.doubleToRawLongBits(d)));

		- Float:
		seeeeeeeemmmmmmmmmmmmmmmmmmmmmmm
		 1000000010001100110011001100110
		- Double
		seeeeeeeeeeemmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
		 10000000   10001100110011001100110
		 100000000001000110011001100110011000000000000000000000000000000
		 100000000001000110011001100110011001100110011001100110011001101
		*/
        // Examples of well documented but probably unexpected behavior in java / with 32-bit float to 64-bit float conversion.
        assertFalse("Document unexpected behaviour with explicit type-casting float as double!", (double)0.2f == 0.2d );
        assertFalse("Document unexpected behaviour with implicit type-cast!", 0.2f == 0.2d );
        Double d1 = new Double( 1.1f );
        Double d2 = new Double( "1.1f" );
        assertFalse( "Document implicit type cast from float to double before calling Double(double d) constructor", d1.equals( d2 ) );

        assertTrue( "Correctly converting float to double via base10 (string) representation!", new Double( 3.1d ).equals(  new Double( new Float( 3.1f ).toString() ) ) );

        // Pinpointing the not so obvious "buggy" conversion from float to double in JSONObject
        JSONObject jo = new JSONObject();
        jo.put( "bug", 3.1f ); // will call put( String key, double value ) with implicit and "buggy" type-cast from float to double
        assertFalse( "The java-compiler did add some zero bits for you to the mantissa (unexpected, but well documented)", jo.get( "bug" ).equals(  new Double( 3.1d ) ) );

        JSONObject inc = new JSONObject();
        inc.put( "bug", new Float( 3.1f ) ); // This will put in instance of Float into JSONObject, i.e. call put( String key, Object value ) 
        assertTrue( "Everything is ok here!", inc.get( "bug" ) instanceof Float );
        inc.increment( "bug" ); // after adding 1, increment will call put( String key, double value ) with implicit and "buggy" type-cast from float to double!
        // this.put(key, (Float) value + 1);
        // 1.	The (Object)value will be typecasted to (Float)value since it is an instanceof Float actually nothing is done. 
        // 2.	Float instance will be autoboxed into float because the + operator will work on primitives not Objects!
        // 3.	A float+float operation will be performed and results into a float primitive.
        // 4.	There is no method that matches the signature put( String key, float value), java-compiler will choose the method
        //		put( String key, double value) and does an implicit type-cast(!) by appending zero-bits to the mantissa
        assertTrue( "JSONObject increment unexpected behavior, Float will not stay Float!", jo.get( "bug" ) instanceof Float );
        // correct implementation (with change of behavior) would be:
        // this.put(key, new Float((Float) value + 1)); 
        // Probably it would be better to deprecate the method and remove some day, while convenient processing the "payload" is not
        // really in the the scope of a JSON-library (IMHO.)

    }

    @Test
    public void emptyJsonObjectNamesToArray() {
        JSONObject jsonObject = new JSONObject();
        String [] names = JSONObject.getNames(jsonObject);
        assertTrue("names should be null", names == null);
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
    public void jsonObjectToStringSuppressWarningOnCastToMap() {
        JSONObject jsonObject = new JSONObject();
        Map<String, String> map = new HashMap<String, String>();
        map.put("abc", "def");
        jsonObject.put("key", map);
        String toStr = jsonObject.toString();
        JSONObject expectedJsonObject = new JSONObject(toStr);
        assertTrue("keys should be equal",
                jsonObject.keySet().iterator().next().equals(
                expectedJsonObject.keySet().iterator().next()));
        /**
         * Can't do a Util compare because although they look the same
         * in the debugger, one is a map and the other is a JSONObject.  
         */
        // TODO: fix warnings
        map = (Map)jsonObject.get("key");
        JSONObject mapJsonObject = expectedJsonObject.getJSONObject("key");
        assertTrue("value size should be equal",
                map.size() == mapJsonObject.length() && map.size() == 1);
        assertTrue("keys should be equal for key: "+map.keySet().iterator().next(),
                mapJsonObject.keys().next().equals(map.keySet().iterator().next()));
        assertTrue("values should be equal for key: "+map.keySet().iterator().next(),
                mapJsonObject.get(mapJsonObject.keys().next()).toString().equals(
                        map.get(map.keySet().iterator().next())));
    }

    @Test
    public void jsonObjectToStringSuppressWarningOnCastToCollection() {
        JSONObject jsonObject = new JSONObject();
        Collection<String> collection = new ArrayList<String>();
        collection.add("abc");
        // ArrayList will be added as an object
        jsonObject.put("key", collection);
        String toStr = jsonObject.toString();
        // [abc] will be added as a JSONArray
        JSONObject expectedJsonObject = new JSONObject(toStr);
        /**
         * Can't do a Util compare because although they look the same
         * in the debugger, one is a collection and the other is a JSONArray.  
         */
        assertTrue("keys should be equal",
                jsonObject.keySet().iterator().next().equals(
                expectedJsonObject.keySet().iterator().next()));
        // TODO: fix warnings
        collection = (Collection)jsonObject.get("key");
        JSONArray jsonArray = expectedJsonObject.getJSONArray("key");
        assertTrue("value size should be equal",
                collection.size() == jsonArray.length());
        Iterator it = collection.iterator();
        for (int i = 0; i < collection.size(); ++i) {
            assertTrue("items should be equal for index: "+i,
                    jsonArray.get(i).toString().equals(it.next().toString()));
        }
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

        /**
         * This test is to document the preferred behavior if BigDecimal is
         * supported. At the present time, bd returns as a string, since it
         * is recognized as being a Java package class. 
         */
        Object bdWrap = JSONObject.wrap(BigDecimal.ONE);
        assertTrue("BigDecimal.ONE currently evaluates to string",
                bdWrap.equals("1"));

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
    public void jsonObjectPutOnceNull() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce(null, null);
        assertTrue("jsonObject should be empty", jsonObject.length() == 0);
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
        assertTrue("Same JSONObject should be equal to itself",
                aJsonObject.equals(aJsonObject));
    }
}


