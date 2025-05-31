package org.json.junit;

/*
Public Domain.
*/

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONPointerException;
import org.json.JSONParserConfiguration;
import org.json.JSONString;
import org.json.JSONTokener;
import org.json.ParserConfiguration;
import org.json.XML;
import org.json.junit.data.BrokenToString;
import org.json.junit.data.ExceptionalBean;
import org.json.junit.data.Fraction;
import org.json.junit.data.GenericBean;
import org.json.junit.data.GenericBeanInt;
import org.json.junit.data.MyBean;
import org.json.junit.data.MyBeanCustomName;
import org.json.junit.data.MyBeanCustomNameSubClass;
import org.json.junit.data.MyBigNumberBean;
import org.json.junit.data.MyEnum;
import org.json.junit.data.MyEnumField;
import org.json.junit.data.MyJsonString;
import org.json.junit.data.MyNumber;
import org.json.junit.data.MyNumberContainer;
import org.json.junit.data.MyPublicClass;
import org.json.junit.data.RecursiveBean;
import org.json.junit.data.RecursiveBeanEquals;
import org.json.junit.data.Singleton;
import org.json.junit.data.SingletonEnum;
import org.json.junit.data.WeirdList;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

/**
 * JSONObject, along with JSONArray, are the central classes of the reference app.
 * All of the other classes interact with them, and JSON functionality would
 * otherwise be impossible.
 */
public class JSONObjectTest {

    /**
     *  Regular Expression Pattern that matches JSON Numbers. This is primarily used for
     *  output to guarantee that we are always writing valid JSON. 
     */
    static final Pattern NUMBER_PATTERN = Pattern.compile("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?");

    @After
    public void tearDown() {
        SingletonEnum.getInstance().setSomeInt(0);
        SingletonEnum.getInstance().setSomeString(null);
        Singleton.getInstance().setSomeInt(0);
        Singleton.getInstance().setSomeString(null);
    }

    /**
     * Tests that the similar method is working as expected.
     */
    @Test
    public void verifySimilar() {
        final String string1 = "HasSameRef";
        final String string2 = "HasDifferentRef";
        JSONObject obj1 = new JSONObject()
                .put("key1", "abc")
                .put("key2", 2)
                .put("key3", string1);
        
        JSONObject obj2 = new JSONObject()
                .put("key1", "abc")
                .put("key2", 3)
                .put("key3", string1);

        JSONObject obj3 = new JSONObject()
                .put("key1", "abc")
                .put("key2", 2)
                .put("key3", new String(string1));
        
        JSONObject obj4 = new JSONObject()
                .put("key1", "abc")
                .put("key2", 2.0)
                .put("key3", new String(string1));

        JSONObject obj5 = new JSONObject()
                .put("key1", "abc")
                .put("key2", 2.0)
                .put("key3", new String(string2));
        
        assertFalse("obj1-obj2 Should eval to false", obj1.similar(obj2));
        assertTrue("obj1-obj3 Should eval to true", obj1.similar(obj3));
        assertTrue("obj1-obj4 Should eval to true", obj1.similar(obj4));
        assertFalse("obj1-obj5 Should eval to false", obj1.similar(obj5));
        // verify that a double and big decimal are "similar"
        assertTrue("should eval to true",new JSONObject().put("a",1.1d).similar(new JSONObject("{\"a\":1.1}")));
        // Confirm #618 is fixed (compare should not exit early if similar numbers are found)
        // Note that this test may not work if the JSONObject map entry order changes
        JSONObject first = new JSONObject("{\"a\": 1, \"b\": 2, \"c\": 3}");
        JSONObject second = new JSONObject("{\"a\": 1, \"b\": 2.0, \"c\": 4}");
        assertFalse("first-second should eval to false", first.similar(second));
        List<JSONObject> jsonObjects = new ArrayList<JSONObject>(
                Arrays.asList(obj1, obj2, obj3, obj4, obj5)
        );
        Util.checkJSONObjectsMaps(jsonObjects);
    }

    @Test
    public void timeNumberParsing() {
        // test data to use
        final String[] testData = new String[] {
                null,
                "",
                "100",
                "-100",
                "abc123",
                "012345",
                "100.5e199",
                "-100.5e199",
                "DEADBEEF",
                "0xDEADBEEF",
                "1234567890.1234567890",
                "-1234567890.1234567890",
                "adloghakuidghauiehgauioehgdkjfb nsruoh aeu noerty384 nkljfgh "
                    + "395h tdfn kdz8yt3 4hkls gn.ey85 4hzfhnz.o8y5a84 onvklt "
                    + "yh389thub nkz8y49lihv al4itlaithknty8hnbl"
                // long (in length) number sequences with invalid data at the end of the
                // string offer very poor performance for the REGEX.
                ,"123467890123467890123467890123467890123467890123467890123467"
                    + "8901234678901234678901234678901234678901234678901234678"
                    + "9012346789012346789012346789012346789012346789012346789"
                    + "0a"
        };
        final int testDataLength = testData.length;
        /**
         * Changed to 1000 for faster test runs
         */
        // final int iterations = 1000000;
        final int iterations = 1000;

        // 10 million iterations 1,000,000 * 10 (currently 100,000)
        long startTime = System.nanoTime();
        for(int i = 0; i < iterations; i++) {
            for(int j = 0; j < testDataLength; j++) {
                try {
                BigDecimal v1 = new BigDecimal(testData[j]);
                v1.signum();
                } catch(Exception ignore) {
                    //do nothing
                }
            }
        }
        final long elapsedNano1 = System.nanoTime() - startTime;
        System.out.println("new BigDecimal(testData[]) : " + elapsedNano1 / 1000000 + " ms");

        startTime = System.nanoTime();
        for(int i = 0; i < iterations; i++) {
            for(int j = 0; j < testDataLength; j++) {
                try {
                boolean v2 = NUMBER_PATTERN.matcher(testData[j]).matches();
                assert v2 == !!v2;
                } catch(Exception ignore) {
                    //do nothing
                }
            }
        }
        final long elapsedNano2 = System.nanoTime() - startTime;
        System.out.println("NUMBER_PATTERN.matcher(testData[]).matches() : " + elapsedNano2 / 1000000 + " ms");
        // don't assert normally as the testing is machine dependent.
        // assertTrue("Expected Pattern matching to be faster than BigDecimal constructor",elapsedNano2<elapsedNano1);
   }

    /**
     * JSONObject built from a bean, but only using a null value.
     * Nothing good is expected to happen.
     * Expects NullPointerException
     */
    @Test(expected=NullPointerException.class)
    public void jsonObjectByNullBean() {
        JSONObject jsonObject = new JSONObject((MyBean)null);
        assertNull("Expected an exception", jsonObject);
        Util.checkJSONObjectMaps(jsonObject);
    }
    
    /**
     * The JSON parser is permissive of unambiguous unquoted keys and values.
     * Such JSON text should be allowed, even if it does not strictly conform
     * to the spec. However, after being parsed, toString() should emit strictly
     * conforming JSON text.  
     */
    @Test
    public void unquotedText() {
        String str = "{key1:value1, key2:42, 1.2 : 3.4, -7e5 : something!}";

        // Test should fail if default strictMode is true, pass if false
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration();
        if (jsonParserConfiguration.isStrictMode()) {
            try {
                JSONObject jsonObject = new JSONObject(str);
                assertEquals("Expected to throw exception due to invalid string", true, false);
            } catch (JSONException e) { }
        } else {
            JSONObject jsonObject = new JSONObject(str);
            String textStr = jsonObject.toString();
            assertTrue("expected key1", textStr.contains("\"key1\""));
            assertTrue("expected value1", textStr.contains("\"value1\""));
            assertTrue("expected key2", textStr.contains("\"key2\""));
            assertTrue("expected 42", textStr.contains("42"));
            assertTrue("expected 1.2", textStr.contains("\"1.2\""));
            assertTrue("expected 3.4", textStr.contains("3.4"));
            assertTrue("expected -7E+5", textStr.contains("\"-7E+5\""));
            assertTrue("expected something!", textStr.contains("\"something!\""));
            Util.checkJSONObjectMaps(jsonObject);
        }
    }
    
    @Test
    public void testLongFromString(){
        String str = "26315000000253009";
        JSONObject json = new JSONObject();
        json.put("key", str);
        
        final Object actualKey = json.opt("key");
        assert str.equals(actualKey) : "Incorrect key value. Got " + actualKey
                + " expected " + str;
        
        final long actualLong = json.optLong("key");
        assert actualLong != 0 : "Unable to extract long value for string " + str;
        assert 26315000000253009L == actualLong : "Incorrect key value. Got "
                + actualLong + " expected " + str;

        final Long actualLongObject = json.optLongObject("key");
        assert actualLongObject != 0L : "Unable to extract Long value for string " + str;
        assert Long.valueOf(26315000000253009L).equals(actualLongObject) : "Incorrect key value. Got "
                + actualLongObject + " expected " + str;

        final String actualString = json.optString("key");
        assert str.equals(actualString) : "Incorrect key value. Got "
                + actualString + " expected " + str;
        Util.checkJSONObjectMaps(json);
    }
    
    /**
     * A JSONObject can be created with no content
     */
    @Test
    public void emptyJsonObject() {
        JSONObject jsonObject = new JSONObject();
        assertTrue("jsonObject should be empty", jsonObject.isEmpty());
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * A JSONObject can be created from another JSONObject plus a list of names.
     * In this test, some of the starting JSONObject keys are not in the 
     * names list.
     */
    @Test
    public void jsonObjectByNames() {
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
        JSONObject jsonObject = new JSONObject(str);

        // validate JSON
        JSONObject jsonObjectByName = new JSONObject(jsonObject, keys);
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObjectByName.toString());
        assertTrue("expected 4 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 4);
        assertTrue("expected \"falseKey\":false", Boolean.FALSE.equals(jsonObjectByName.query("/falseKey")));
        assertTrue("expected \"nullKey\":null", JSONObject.NULL.equals(jsonObjectByName.query("/nullKey")));
        assertTrue("expected \"stringKey\":\"hello world!\"", "hello world!".equals(jsonObjectByName.query("/stringKey")));
        assertTrue("expected \"doubleKey\":-23.45e67", new BigDecimal("-23.45e67").equals(jsonObjectByName.query("/doubleKey")));
        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(jsonObject, jsonObjectByName)));
    }

    /**
     * JSONObjects can be built from a Map<String, Object>. 
     * In this test the map is null.
     * the JSONObject(JsonTokener) ctor is not tested directly since it already
     * has full coverage from other tests.
     */
    @Test
    public void jsonObjectByNullMap() {
        Map<String, Object> map = null;
        JSONObject jsonObject = new JSONObject(map);
        assertTrue("jsonObject should be empty", jsonObject.isEmpty());
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * JSONObjects can be built from a Map<String, Object>. 
     * In this test all of the map entries are valid JSON types.
     */
    @Test
    public void jsonObjectByMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("trueKey", Boolean.valueOf(true));
        map.put("falseKey", Boolean.valueOf(false));
        map.put("stringKey", "hello world!");
        map.put("escapeStringKey", "h\be\tllo w\u1234orld!");
        map.put("intKey", Long.valueOf(42));
        map.put("doubleKey", Double.valueOf(-23.45e67));
        JSONObject jsonObject = new JSONObject(map);

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 6 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 6);
        assertTrue("expected \"trueKey\":true", Boolean.TRUE.equals(jsonObject.query("/trueKey")));
        assertTrue("expected \"falseKey\":false", Boolean.FALSE.equals(jsonObject.query("/falseKey")));
        assertTrue("expected \"stringKey\":\"hello world!\"", "hello world!".equals(jsonObject.query("/stringKey")));
        assertTrue("expected \"escapeStringKey\":\"h\be\tllo w\u1234orld!\"", "h\be\tllo w\u1234orld!".equals(jsonObject.query("/escapeStringKey")));
        assertTrue("expected \"doubleKey\":-23.45e67", Double.valueOf("-23.45e67").equals(jsonObject.query("/doubleKey")));
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * Verifies that the constructor has backwards compatability with RAW types pre-java5.
     */
    @Test
    public void verifyConstructor() {
        
        final JSONObject expected = new JSONObject("{\"myKey\":10}");
        
        @SuppressWarnings("rawtypes")
        Map myRawC = Collections.singletonMap("myKey", Integer.valueOf(10));
        JSONObject jaRaw = new JSONObject(myRawC);

        Map<String, Object> myCStrObj = Collections.singletonMap("myKey",
                (Object) Integer.valueOf(10));
        JSONObject jaStrObj = new JSONObject(myCStrObj);

        Map<String, Integer> myCStrInt = Collections.singletonMap("myKey",
                Integer.valueOf(10));
        JSONObject jaStrInt = new JSONObject(myCStrInt);

        Map<?, ?> myCObjObj = Collections.singletonMap((Object) "myKey",
                (Object) Integer.valueOf(10));
        JSONObject jaObjObj = new JSONObject(myCObjObj);

        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaRaw));
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaStrObj));
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaStrInt));
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaObjObj));
        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(
                Arrays.asList(jaRaw, jaStrObj, jaStrInt, jaObjObj))
        );
    }
    
    /**
     * Tests Number serialization.
     */
    @Test
    public void verifyNumberOutput(){
        /**
         * MyNumberContainer is a POJO, so call JSONObject(bean), 
         * which builds a map of getter names/values
         * The only getter is getMyNumber (key=myNumber), 
         * whose return value is MyNumber. MyNumber extends Number, 
         * but is not recognized as such by wrap() per current
         * implementation, so wrap() returns the default new JSONObject(bean).
         * The only getter is getNumber (key=number), whose return value is
         * BigDecimal(42). 
         */
        JSONObject jsonObject0 = new JSONObject(new MyNumberContainer());
        String actual = jsonObject0.toString();
        String expected = "{\"myNumber\":{\"number\":42}}";
        assertEquals("Equal", expected , actual);
        
        /**
         * JSONObject.put() handles objects differently than the 
         * bean constructor. Where the bean ctor wraps objects before 
         * placing them in the map, put() inserts the object without wrapping.
         * In this case, a MyNumber instance is the value.
         * The MyNumber.toString() method is responsible for
         * returning a reasonable value: the string '42'.
         */
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("myNumber", new MyNumber());
        actual = jsonObject1.toString();
        expected = "{\"myNumber\":42}";
        assertEquals("Equal", expected , actual);

        /**
         * Calls the JSONObject(Map) ctor, which calls wrap() for values.
         * AtomicInteger is a Number, but is not recognized by wrap(), per
         * current implementation. However, the type is
         * 'java.util.concurrent.atomic', so due to the 'java' prefix,
         * wrap() inserts the value as a string. That is why 42 comes back
         * wrapped in quotes.
         */
        JSONObject jsonObject2 = new JSONObject(Collections.singletonMap("myNumber", new AtomicInteger(42)));
        actual = jsonObject2.toString();
        expected = "{\"myNumber\":\"42\"}";
        assertEquals("Equal", expected , actual);

        /**
         * JSONObject.put() inserts the AtomicInteger directly into the
         * map not calling wrap(). In toString()->write()->writeValue(), 
         * AtomicInteger is recognized as a Number, and converted via
         * numberToString() into the unquoted string '42'.
         */
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("myNumber", new AtomicInteger(42));
        actual = jsonObject3.toString();
        expected = "{\"myNumber\":42}";
        assertEquals("Equal", expected , actual);

        /**
         * Calls the JSONObject(Map) ctor, which calls wrap() for values.
         * Fraction is a Number, but is not recognized by wrap(), per
         * current implementation. As a POJO, Fraction is handled as a
         * bean and inserted into a contained JSONObject. It has 2 getters,
         * for numerator and denominator. 
         */
        JSONObject jsonObject4 = new JSONObject(Collections.singletonMap("myNumber", new Fraction(4,2)));
        assertEquals(1, jsonObject4.length());
        assertEquals(2, ((JSONObject)(jsonObject4.get("myNumber"))).length());
        assertEquals("Numerator", BigInteger.valueOf(4) , jsonObject4.query("/myNumber/numerator"));
        assertEquals("Denominator", BigInteger.valueOf(2) , jsonObject4.query("/myNumber/denominator"));

        /**
         * JSONObject.put() inserts the Fraction directly into the
         * map not calling wrap(). In toString()->write()->writeValue(), 
         * Fraction is recognized as a Number, and converted via
         * numberToString() into the unquoted string '4/2'. But the 
         * BigDecimal sanity check fails, so writeValue() defaults
         * to returning a safe JSON quoted string. Pretty slick!
         */
        JSONObject jsonObject5 = new JSONObject();
        jsonObject5.put("myNumber", new Fraction(4,2));
        actual = jsonObject5.toString();
        expected = "{\"myNumber\":\"4/2\"}"; // valid JSON, bug fixed
        assertEquals("Equal", expected , actual);

        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                jsonObject0, jsonObject1, jsonObject2, jsonObject3, jsonObject4, jsonObject5
        )));
    }

    /**
     * Verifies that the put Collection has backwards compatibility with RAW types pre-java5.
     */
    @Test
    public void verifyPutCollection() {
        
        final JSONObject expected = new JSONObject("{\"myCollection\":[10]}");

        @SuppressWarnings("rawtypes")
        Collection myRawC = Collections.singleton(Integer.valueOf(10));
        JSONObject jaRaw = new JSONObject();
        jaRaw.put("myCollection", myRawC);

        Collection<Object> myCObj = Collections.singleton((Object) Integer
                .valueOf(10));
        JSONObject jaObj = new JSONObject();
        jaObj.put("myCollection", myCObj);

        Collection<Integer> myCInt = Collections.singleton(Integer
                .valueOf(10));
        JSONObject jaInt = new JSONObject();
        jaInt.put("myCollection", myCInt);

        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaRaw));
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaObj));
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaInt));

        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                jaRaw, jaObj, jaInt
        )));
    }

    
    /**
     * Verifies that the put Map has backwards compatibility with RAW types pre-java5.
     */
    @Test
    public void verifyPutMap() {
        
        final JSONObject expected = new JSONObject("{\"myMap\":{\"myKey\":10}}");

        @SuppressWarnings("rawtypes")
        Map myRawC = Collections.singletonMap("myKey", Integer.valueOf(10));
        JSONObject jaRaw = new JSONObject();
        jaRaw.put("myMap", myRawC);

        Map<String, Object> myCStrObj = Collections.singletonMap("myKey",
                (Object) Integer.valueOf(10));
        JSONObject jaStrObj = new JSONObject();
        jaStrObj.put("myMap", myCStrObj);

        Map<String, Integer> myCStrInt = Collections.singletonMap("myKey",
                Integer.valueOf(10));
        JSONObject jaStrInt = new JSONObject();
        jaStrInt.put("myMap", myCStrInt);

        Map<?, ?> myCObjObj = Collections.singletonMap((Object) "myKey",
                (Object) Integer.valueOf(10));
        JSONObject jaObjObj = new JSONObject();
        jaObjObj.put("myMap", myCObjObj);

        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaRaw));
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaStrObj));
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaStrInt));
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaObjObj));

        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                jaRaw, jaStrObj, jaStrInt, jaStrObj
        )));
    }


    /**
     * JSONObjects can be built from a Map<String, Object>. 
     * In this test the map entries are not valid JSON types.
     * The actual conversion is kind of interesting.
     */
    @Test
    public void jsonObjectByMapWithUnsupportedValues() {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        // Just insert some random objects
        jsonMap.put("key1", new CDL());
        jsonMap.put("key2", new Exception());

        JSONObject jsonObject = new JSONObject(jsonMap);

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 2 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected 0 key1 items", ((Map<?,?>)(JsonPath.read(doc, "$.key1"))).size() == 0);
        assertTrue("expected \"key2\":java.lang.Exception","java.lang.Exception".equals(jsonObject.query("/key2")));
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * JSONObjects can be built from a Map<String, Object>. 
     * In this test one of the map values is null 
     */
    @Test
    public void jsonObjectByMapWithNullValue() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("trueKey", Boolean.valueOf(true));
        map.put("falseKey", Boolean.valueOf(false));
        map.put("stringKey", "hello world!");
        map.put("nullKey", null);
        map.put("escapeStringKey", "h\be\tllo w\u1234orld!");
        map.put("intKey", Long.valueOf(42));
        map.put("doubleKey", Double.valueOf(-23.45e67));
        JSONObject jsonObject = new JSONObject(map);

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 6 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 6);
        assertTrue("expected \"trueKey\":true", Boolean.TRUE.equals(jsonObject.query("/trueKey")));
        assertTrue("expected \"falseKey\":false", Boolean.FALSE.equals(jsonObject.query("/falseKey")));
        assertTrue("expected \"stringKey\":\"hello world!\"", "hello world!".equals(jsonObject.query("/stringKey")));
        assertTrue("expected \"escapeStringKey\":\"h\be\tllo w\u1234orld!\"", "h\be\tllo w\u1234orld!".equals(jsonObject.query("/escapeStringKey")));
        assertTrue("expected \"intKey\":42", Long.valueOf("42").equals(jsonObject.query("/intKey")));
        assertTrue("expected \"doubleKey\":-23.45e67", Double.valueOf("-23.45e67").equals(jsonObject.query("/doubleKey")));
        Util.checkJSONObjectMaps(jsonObject);
    }
    
    @Test
    public void jsonObjectByMapWithNullValueAndParserConfiguration() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("nullKey", null);
        
        // by default, null values are ignored
        JSONObject obj1 = new JSONObject(map);
        assertTrue("expected null value to be ignored by default", obj1.isEmpty());

        // if configured, null values are written as such into the JSONObject.
        JSONParserConfiguration parserConfiguration = new JSONParserConfiguration().withUseNativeNulls(true);
        JSONObject obj2 = new JSONObject(map, parserConfiguration);
        assertFalse("expected null value to accepted when configured", obj2.isEmpty());
        assertTrue(obj2.has("nullKey"));
        assertEquals(JSONObject.NULL, obj2.get("nullKey"));
    }
    
    @Test
    public void jsonObjectByMapWithNestedNullValueAndParserConfiguration() {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> nestedMap = new HashMap<String, Object>();
        nestedMap.put("nullKey", null);
        map.put("nestedMap", nestedMap);
        List<Map<String, Object>> nestedList = new ArrayList<Map<String,Object>>();
        nestedList.add(nestedMap);        
        map.put("nestedList", nestedList);
        
        JSONParserConfiguration parserConfiguration = new JSONParserConfiguration().withUseNativeNulls(true);
        JSONObject jsonObject = new JSONObject(map, parserConfiguration);

        JSONObject nestedObject = jsonObject.getJSONObject("nestedMap");
        assertTrue(nestedObject.has("nullKey"));
        assertEquals(JSONObject.NULL, nestedObject.get("nullKey"));
        
        JSONArray nestedArray = jsonObject.getJSONArray("nestedList");
        assertEquals(1, nestedArray.length());
        assertTrue(nestedArray.getJSONObject(0).has("nullKey"));
        assertEquals(JSONObject.NULL, nestedArray.getJSONObject(0).get("nullKey"));
    }

    /**
     * JSONObject built from a bean. In this case all but one of the 
     * bean getters return valid JSON types
     */
    @SuppressWarnings("boxing")
    @Test
    public void jsonObjectByBean1() {
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
            });

        JSONObject jsonObject = new JSONObject(myBean);

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 8 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 8);
        assertTrue("expected 0 items in stringReaderKey", ((Map<?, ?>) (JsonPath.read(doc, "$.stringReaderKey"))).size() == 0);
        assertTrue("expected true", Boolean.TRUE.equals(jsonObject.query("/trueKey")));
        assertTrue("expected false", Boolean.FALSE.equals(jsonObject.query("/falseKey")));
        assertTrue("expected hello world!","hello world!".equals(jsonObject.query("/stringKey")));
        assertTrue("expected h\be\tllo w\u1234orld!", "h\be\tllo w\u1234orld!".equals(jsonObject.query("/escapeStringKey")));
        assertTrue("expected 42", Integer.valueOf("42").equals(jsonObject.query("/intKey")));
        assertTrue("expected -23.45e7", Double.valueOf("-23.45e7").equals(jsonObject.query("/doubleKey")));
        // sorry, mockito artifact
        assertTrue("expected 2 mockitoInterceptor items", ((Map<?,?>)(JsonPath.read(doc, "$.mockitoInterceptor"))).size() == 2);
        assertTrue("expected 0 mockitoInterceptor.serializationSupport items",
                ((Map<?,?>)(JsonPath.read(doc, "$.mockitoInterceptor.serializationSupport"))).size() == 0);
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * JSONObject built from a bean that has custom field names.
     */
    @Test
    public void jsonObjectByBean2() {
        JSONObject jsonObject = new JSONObject(new MyBeanCustomName());
        assertNotNull(jsonObject);
        assertEquals("Wrong number of keys found:",
                5,
                jsonObject.keySet().size());
        assertFalse("Normal field name (someString) processing did not work",
                jsonObject.has("someString"));
        assertFalse("Normal field name (myDouble) processing did not work",
                jsonObject.has("myDouble"));
        assertFalse("Normal field name (someFloat) processing did not work",
                jsonObject.has("someFloat"));
        assertFalse("Ignored field not found!",
                jsonObject.has("ignoredInt"));
        // getSomeInt() has no user-defined annotation
        assertTrue("Normal field name (someInt) should have been found",
                jsonObject.has("someInt"));
        // the user-defined annotation does not replace any value, so someLong should be found
        assertTrue("Normal field name (someLong) should have been found",
                jsonObject.has("someLong"));
        // myStringField replaces someString property name via user-defined annotation
        assertTrue("Overridden String field name (myStringField) should have been found",
                jsonObject.has("myStringField"));
        // weird name replaces myDouble property name via user-defined annotation
        assertTrue("Overridden String field name (Some Weird NAme that Normally Wouldn't be possible!) should have been found",
                jsonObject.has("Some Weird NAme that Normally Wouldn't be possible!"));
        // InterfaceField replaces someFloat property name via user-defined annotation
        assertTrue("Overridden String field name (InterfaceField) should have been found",
                jsonObject.has("InterfaceField"));
        Util.checkJSONObjectMaps(jsonObject);
    }
    
    /**
     * JSONObject built from a bean that has custom field names inherited from a parent class.
     */
    @Test
    public void jsonObjectByBean3() {
        JSONObject jsonObject = new JSONObject(new MyBeanCustomNameSubClass());
        assertNotNull(jsonObject);
        assertEquals("Wrong number of keys found:",
                7,
                jsonObject.keySet().size());
        assertFalse("Normal int field name (someInt) found, but was overridden",
                jsonObject.has("someInt"));
        assertFalse("Normal field name (myDouble) processing did not work",
                jsonObject.has("myDouble"));
        // myDouble was replaced by weird name, and then replaced again by AMoreNormalName via user-defined annotation
        assertFalse("Overridden String field name (Some Weird NAme that Normally Wouldn't be possible!) should not be FOUND!",
                jsonObject.has("Some Weird NAme that Normally Wouldn't be possible!"));
        assertFalse("Normal field name (someFloat) found, but was overridden",
                jsonObject.has("someFloat"));
        assertFalse("Ignored field found! but was overridden",
                jsonObject.has("ignoredInt"));
        // shouldNotBeJSON property name was first ignored, then replaced by ShouldBeIgnored via user-defined annotations
        assertFalse("Ignored field at the same level as forced name should not have been found",
                jsonObject.has("ShouldBeIgnored"));
        // able property name was replaced by Getable via user-defined annotation
        assertFalse("Normally ignored field (able) with explicit property name should not have been found",
                jsonObject.has("able"));
        // property name someInt was replaced by newIntFieldName via user-defined annotation
        assertTrue("Overridden int field name (newIntFieldName) should have been found",
                jsonObject.has("newIntFieldName"));
        // property name someLong was not replaced via user-defined annotation
        assertTrue("Normal field name (someLong) should have been found",
                jsonObject.has("someLong"));
        // property name someString was replaced by myStringField via user-defined annotation
        assertTrue("Overridden String field name (myStringField) should have been found",
                jsonObject.has("myStringField"));
        // property name myDouble was replaced by a weird name, followed by AMoreNormalName via user-defined annotations
        assertTrue("Overridden double field name (AMoreNormalName) should have been found",
                jsonObject.has("AMoreNormalName"));
        // property name someFloat was replaced by InterfaceField via user-defined annotation
        assertTrue("Overridden String field name (InterfaceField) should have been found",
                jsonObject.has("InterfaceField"));
        // property name ignoredInt was replaced by none, followed by forcedInt via user-defined annotations
        assertTrue("Forced field should have been found!",
                jsonObject.has("forcedInt"));
        // property name able was replaced by Getable via user-defined annotation
        assertTrue("Overridden boolean field name (Getable) should have been found",
                jsonObject.has("Getable"));
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * A bean is also an object. But in order to test the JSONObject
     * ctor that takes an object and a list of names, 
     * this particular bean needs some public
     * data members, which have been added to the class.
     */
    @Test
    public void jsonObjectByObjectAndNames() {
        String[] keys = {"publicString", "publicInt"};
        // just need a class that has public data members
        MyPublicClass myPublicClass = new MyPublicClass();
        JSONObject jsonObject = new JSONObject(myPublicClass, keys);

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 2 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected \"publicString\":\"abc\"", "abc".equals(jsonObject.query("/publicString")));
        assertTrue("expected \"publicInt\":42", Integer.valueOf(42).equals(jsonObject.query("/publicInt")));
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * Exercise the JSONObject from resource bundle functionality.
     * The test resource bundle is uncomplicated, but provides adequate test coverage.
     */
    @Test
    public void jsonObjectByResourceBundle() {
        JSONObject jsonObject = new
                JSONObject("org.json.junit.data.StringsResourceBundle",
                        Locale.getDefault());

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 2 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 2);
        assertTrue("expected 2 greetings items", ((Map<?,?>)(JsonPath.read(doc, "$.greetings"))).size() == 2);
        assertTrue("expected \"hello\":\"Hello, \"", "Hello, ".equals(jsonObject.query("/greetings/hello")));
        assertTrue("expected \"world\":\"World!\"", "World!".equals(jsonObject.query("/greetings/world")));
        assertTrue("expected 2 farewells items", ((Map<?,?>)(JsonPath.read(doc, "$.farewells"))).size() == 2);
        assertTrue("expected \"later\":\"Later, \"", "Later, ".equals(jsonObject.query("/farewells/later")));
        assertTrue("expected \"world\":\"World!\"", "Alligator!".equals(jsonObject.query("/farewells/gator")));
        Util.checkJSONObjectMaps(jsonObject);
    }
    
    /**
     * Exercise the JSONObject.accumulate() method
     */
    @SuppressWarnings("boxing")
    @Test
    public void jsonObjectAccumulate() {

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("myArray", true);
        jsonObject.accumulate("myArray", false);
        jsonObject.accumulate("myArray", "hello world!");
        jsonObject.accumulate("myArray", "h\be\tllo w\u1234orld!");
        jsonObject.accumulate("myArray", 42);
        jsonObject.accumulate("myArray", -23.45e7);
        // include an unsupported object for coverage
        try {
            jsonObject.accumulate("myArray", Double.NaN);
            fail("Expected exception");
        } catch (JSONException ignored) {}

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 1 top level item", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 1);
        assertTrue("expected 6 myArray items", ((List<?>)(JsonPath.read(doc, "$.myArray"))).size() == 6);
        assertTrue("expected true", Boolean.TRUE.equals(jsonObject.query("/myArray/0")));
        assertTrue("expected false", Boolean.FALSE.equals(jsonObject.query("/myArray/1")));
        assertTrue("expected hello world!", "hello world!".equals(jsonObject.query("/myArray/2")));
        assertTrue("expected h\be\tllo w\u1234orld!", "h\be\tllo w\u1234orld!".equals(jsonObject.query("/myArray/3")));
        assertTrue("expected 42", Integer.valueOf(42).equals(jsonObject.query("/myArray/4")));
        assertTrue("expected -23.45e7", Double.valueOf(-23.45e7).equals(jsonObject.query("/myArray/5")));
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * Exercise the JSONObject append() functionality
     */
    @SuppressWarnings("boxing")
    @Test
    public void jsonObjectAppend() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.append("myArray", true);
        jsonObject.append("myArray", false);
        jsonObject.append("myArray", "hello world!");
        jsonObject.append("myArray", "h\be\tllo w\u1234orld!");
        jsonObject.append("myArray", 42);
        jsonObject.append("myArray", -23.45e7);
        // include an unsupported object for coverage
        try {
            jsonObject.append("myArray", Double.NaN);
            fail("Expected exception");
        } catch (JSONException ignored) {}

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 1 top level item", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 1);
        assertTrue("expected 6 myArray items", ((List<?>)(JsonPath.read(doc, "$.myArray"))).size() == 6);
        assertTrue("expected true", Boolean.TRUE.equals(jsonObject.query("/myArray/0")));
        assertTrue("expected false", Boolean.FALSE.equals(jsonObject.query("/myArray/1")));
        assertTrue("expected hello world!", "hello world!".equals(jsonObject.query("/myArray/2")));
        assertTrue("expected h\be\tllo w\u1234orld!", "h\be\tllo w\u1234orld!".equals(jsonObject.query("/myArray/3")));
        assertTrue("expected 42", Integer.valueOf(42).equals(jsonObject.query("/myArray/4")));
        assertTrue("expected -23.45e7", Double.valueOf(-23.45e7).equals(jsonObject.query("/myArray/5")));
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * Exercise the JSONObject doubleToString() method
     */
    @SuppressWarnings("boxing")
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

    /**
     * Exercise some JSONObject get[type] and opt[type] methods
     */
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
                "\"BigDecimalStrKey\":\"19007199254740993.35481234487103587486413587843213584\","+
                "\"negZeroKey\":-0.0,"+
                "\"negZeroStrKey\":\"-0.0\","+
                "\"arrayKey\":[0,1,2],"+
                "\"objectKey\":{\"myKey\":\"myVal\"}"+
            "}";
        JSONObject jsonObject = new JSONObject(str);
        assertTrue("trueKey should be true", jsonObject.getBoolean("trueKey"));
        assertTrue("opt trueKey should be true", jsonObject.optBoolean("trueKey"));
        assertTrue("opt trueKey should be true", jsonObject.optBooleanObject("trueKey"));
        assertTrue("falseKey should be false", !jsonObject.getBoolean("falseKey"));
        assertTrue("trueStrKey should be true", jsonObject.getBoolean("trueStrKey"));
        assertTrue("trueStrKey should be true", jsonObject.optBoolean("trueStrKey"));
        assertTrue("trueStrKey should be true", jsonObject.optBooleanObject("trueStrKey"));
        assertTrue("falseStrKey should be false", !jsonObject.getBoolean("falseStrKey"));
        assertTrue("stringKey should be string",
            jsonObject.getString("stringKey").equals("hello world!"));
        assertTrue("doubleKey should be double", 
                jsonObject.getDouble("doubleKey") == -23.45e7);
        assertTrue("doubleStrKey should be double", 
                jsonObject.getDouble("doubleStrKey") == 1);
        assertTrue("doubleKey can be float", 
                jsonObject.getFloat("doubleKey") == -23.45e7f);
        assertTrue("doubleStrKey can be float", 
                jsonObject.getFloat("doubleStrKey") == 1f);
        assertTrue("opt doubleKey should be double", 
                jsonObject.optDouble("doubleKey") == -23.45e7);
        assertTrue("opt doubleKey with Default should be double", 
                jsonObject.optDouble("doubleStrKey", Double.NaN) == 1);
        assertTrue("opt doubleKey should be Double",
                Double.valueOf(-23.45e7).equals(jsonObject.optDoubleObject("doubleKey")));
        assertTrue("opt doubleKey with Default should be Double",
                Double.valueOf(1).equals(jsonObject.optDoubleObject("doubleStrKey", Double.NaN)));
        assertTrue("opt negZeroKey should be a Double", 
                jsonObject.opt("negZeroKey") instanceof Double);
        assertTrue("get negZeroKey should be a Double", 
                jsonObject.get("negZeroKey") instanceof Double);
        assertTrue("optNumber negZeroKey should return Double",
                jsonObject.optNumber("negZeroKey") instanceof Double);
        assertTrue("optNumber negZeroStrKey should return Double",
                jsonObject.optNumber("negZeroStrKey") instanceof Double);
        assertTrue("opt negZeroKey should be double", 
                Double.compare(jsonObject.optDouble("negZeroKey"), -0.0d) == 0);
        assertTrue("opt negZeroStrKey with Default should be double", 
                Double.compare(jsonObject.optDouble("negZeroStrKey"), -0.0d) == 0);
        assertTrue("opt negZeroKey should be Double",
                Double.valueOf(-0.0d).equals(jsonObject.optDoubleObject("negZeroKey")));
        assertTrue("opt negZeroStrKey with Default should be Double",
                Double.valueOf(-0.0d).equals(jsonObject.optDoubleObject("negZeroStrKey")));
        assertTrue("optNumber negZeroKey should be -0.0", 
                Double.compare(jsonObject.optNumber("negZeroKey").doubleValue(), -0.0d) == 0);
        assertTrue("optNumber negZeroStrKey should be -0.0", 
                Double.compare(jsonObject.optNumber("negZeroStrKey").doubleValue(), -0.0d) == 0);
        assertTrue("optFloat doubleKey should be float", 
                jsonObject.optFloat("doubleKey") == -23.45e7f);
        assertTrue("optFloat doubleKey with Default should be float", 
                jsonObject.optFloat("doubleStrKey", Float.NaN) == 1f);
        assertTrue("optFloat doubleKey should be Float",
                Float.valueOf(-23.45e7f).equals(jsonObject.optFloatObject("doubleKey")));
        assertTrue("optFloat doubleKey with Default should be Float",
                Float.valueOf(1f).equals(jsonObject.optFloatObject("doubleStrKey", Float.NaN)));
        assertTrue("intKey should be int", 
                jsonObject.optInt("intKey") == 42);
        assertTrue("opt intKey should be int", 
                jsonObject.optInt("intKey", 0) == 42);
        assertTrue("intKey should be Integer",
                Integer.valueOf(42).equals(jsonObject.optIntegerObject("intKey")));
        assertTrue("opt intKey should be Integer",
                Integer.valueOf(42).equals(jsonObject.optIntegerObject("intKey", 0)));
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
        assertTrue("opt longKey should be Long",
                Long.valueOf(1234567890123456789L).equals(jsonObject.optLongObject("longKey")));
        assertTrue("opt longKey with default should be Long",
                Long.valueOf(1234567890123456789L).equals(jsonObject.optLongObject("longKey", 0L)));
        assertTrue("longStrKey should be long", 
                jsonObject.getLong("longStrKey") == 987654321098765432L);
        assertTrue("optNumber int should return Integer",
                jsonObject.optNumber("intKey") instanceof Integer);
        assertTrue("optNumber long should return Long",
                jsonObject.optNumber("longKey") instanceof Long);
        assertTrue("optNumber double should return BigDecimal",
                jsonObject.optNumber("doubleKey") instanceof BigDecimal);
        assertTrue("optNumber Str int should return Integer",
                jsonObject.optNumber("intStrKey") instanceof Integer);
        assertTrue("optNumber Str long should return Long",
                jsonObject.optNumber("longStrKey") instanceof Long);
        assertTrue("optNumber Str double should return BigDecimal",
                jsonObject.optNumber("doubleStrKey") instanceof BigDecimal);
        assertTrue("optNumber BigDecimalStrKey should return BigDecimal",
                jsonObject.optNumber("BigDecimalStrKey") instanceof BigDecimal);
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
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * Check whether JSONObject handles large or high precision numbers correctly
     */
    @Test
    public void stringToValueNumbersTest() {
        assertTrue("-0 Should be a Double!",JSONObject.stringToValue("-0")  instanceof Double);
        assertTrue("-0.0 Should be a Double!",JSONObject.stringToValue("-0.0") instanceof Double);
        assertTrue("'-' Should be a String!",JSONObject.stringToValue("-") instanceof String);
        assertTrue( "0.2 should be a BigDecimal!",
                JSONObject.stringToValue( "0.2" ) instanceof BigDecimal );
        assertTrue( "Doubles should be BigDecimal, even when incorrectly converting floats!",
                JSONObject.stringToValue( Double.valueOf( "0.2f" ).toString() ) instanceof BigDecimal );
        /**
         * This test documents a need for BigDecimal conversion.
         */
        Object obj = JSONObject.stringToValue( "299792.457999999984" );
        assertTrue( "does not evaluate to 299792.457999999984 BigDecimal!",
                 obj.equals(new BigDecimal("299792.457999999984")) );
        assertTrue( "1 should be an Integer!",
                JSONObject.stringToValue( "1" ) instanceof Integer );
        assertTrue( "Integer.MAX_VALUE should still be an Integer!",
                JSONObject.stringToValue( Integer.valueOf( Integer.MAX_VALUE ).toString() ) instanceof Integer );
        assertTrue( "Large integers should be a Long!",
                JSONObject.stringToValue( Long.valueOf(((long)Integer.MAX_VALUE) + 1 ) .toString() ) instanceof Long );
        assertTrue( "Long.MAX_VALUE should still be an Integer!",
                JSONObject.stringToValue( Long.valueOf( Long.MAX_VALUE ).toString() ) instanceof Long );

        String str = new BigInteger( Long.valueOf( Long.MAX_VALUE ).toString() ).add( BigInteger.ONE ).toString();
        assertTrue( "Really large integers currently evaluate to BigInteger",
                JSONObject.stringToValue(str).equals(new BigInteger("9223372036854775808")));
    }

    /**
     * This test documents numeric values which could be numerically
     * handled as BigDecimal or BigInteger. It helps determine what outputs
     * will change if those types are supported.
     */
    @Test
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
                jsonObject.get( "numberWithDecimals" ).equals( new BigDecimal( "299792.457999999984" ) ) );
        Object obj = jsonObject.get( "largeNumber" );
        assertTrue("largeNumber currently evaluates to BigInteger",
                new BigInteger("12345678901234567890").equals(obj));
        // comes back as a double but loses precision
        assertEquals( "preciseNumber currently evaluates to double 0.2",
                0.2, jsonObject.getDouble( "preciseNumber" ), 0.0);
        obj = jsonObject.get( "largeExponent" );
        assertTrue("largeExponent should evaluate as a BigDecimal",
                new BigDecimal("-23.45e2327").equals(obj));
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * This test documents how JSON-Java handles invalid numeric input.
     */
    @Test
    public void jsonInvalidNumberValues() {
        // Number-notations supported by Java and invalid as JSON
        String str =
                "{" +
                        "\"hexNumber\":-0x123," +
                        "\"tooManyZeros\":00," +
                        "\"negativeInfinite\":-Infinity," +
                        "\"negativeNaN\":-NaN," +
                        "\"negativeFraction\":-.01," +
                        "\"tooManyZerosFraction\":00.001," +
                        "\"negativeHexFloat\":-0x1.fffp1," +
                        "\"hexFloat\":0x1.0P-1074," +
                        "\"floatIdentifier\":0.1f," +
                        "\"doubleIdentifier\":0.1d" +
                        "}";

        // Test should fail if default strictMode is true, pass if false
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration();
        if (jsonParserConfiguration.isStrictMode()) {
            try {
                JSONObject jsonObject = new JSONObject(str);
                assertEquals("Expected to throw exception due to invalid string", true, false);
            } catch (JSONException e) { }
        } else {
            JSONObject jsonObject = new JSONObject(str);
            Object obj;
            obj = jsonObject.get("hexNumber");
            assertFalse("hexNumber must not be a number (should throw exception!?)",
                    obj instanceof Number);
            assertTrue("hexNumber currently evaluates to string",
                    obj.equals("-0x123"));
            assertTrue("tooManyZeros currently evaluates to string",
                    jsonObject.get("tooManyZeros").equals("00"));
            obj = jsonObject.get("negativeInfinite");
            assertTrue("negativeInfinite currently evaluates to string",
                    obj.equals("-Infinity"));
            obj = jsonObject.get("negativeNaN");
            assertTrue("negativeNaN currently evaluates to string",
                    obj.equals("-NaN"));
            assertTrue("negativeFraction currently evaluates to double -0.01",
                    jsonObject.get("negativeFraction").equals(BigDecimal.valueOf(-0.01)));
            assertTrue("tooManyZerosFraction currently evaluates to double 0.001",
                    jsonObject.optLong("tooManyZerosFraction") == 0);
            assertTrue("negativeHexFloat currently evaluates to double -3.99951171875",
                    jsonObject.get("negativeHexFloat").equals(Double.valueOf(-3.99951171875)));
            assertTrue("hexFloat currently evaluates to double 4.9E-324",
                    jsonObject.get("hexFloat").equals(Double.valueOf(4.9E-324)));
            assertTrue("floatIdentifier currently evaluates to double 0.1",
                    jsonObject.get("floatIdentifier").equals(Double.valueOf(0.1)));
            assertTrue("doubleIdentifier currently evaluates to double 0.1",
                    jsonObject.get("doubleIdentifier").equals(Double.valueOf(0.1)));
            Util.checkJSONObjectMaps(jsonObject);
        }
    }

    /**
     * Tests how JSONObject get[type] handles incorrect types
     */
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
        try {
            jsonObject.getBoolean("nonKey");
            fail("Expected an exception");
        } catch (JSONException e) { 
            assertEquals("expecting an exception message", 
                    "JSONObject[\"nonKey\"] not found.", e.getMessage());
        }
        try {
            jsonObject.getBoolean("stringKey");
            fail("Expected an exception");
        } catch (JSONException e) { 
            assertEquals("Expecting an exception message", 
                    "JSONObject[\"stringKey\"] is not a Boolean (class java.lang.String : hello world!).",
                    e.getMessage());
        }
        try {
            jsonObject.getString("nonKey");
            fail("Expected an exception");
        } catch (JSONException e) { 
            assertEquals("Expecting an exception message", 
                    "JSONObject[\"nonKey\"] not found.",
                    e.getMessage());
        }
        try {
            jsonObject.getString("trueKey");
            fail("Expected an exception");
        } catch (JSONException e) { 
            assertEquals("Expecting an exception message", 
                    "JSONObject[\"trueKey\"] is not a string (class java.lang.Boolean : true).",
                    e.getMessage());
        }
        try {
            jsonObject.getDouble("nonKey");
            fail("Expected an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "JSONObject[\"nonKey\"] not found.",
                    e.getMessage());
        }
        try {
            jsonObject.getDouble("stringKey");
            fail("Expected an exception");
        } catch (JSONException e) { 
            assertEquals("Expecting an exception message",
                    "JSONObject[\"stringKey\"] is not a double (class java.lang.String : hello world!).",
                    e.getMessage());
        }
        try {
            jsonObject.getFloat("nonKey");
            fail("Expected an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "JSONObject[\"nonKey\"] not found.",
                    e.getMessage());
        }
        try {
            jsonObject.getFloat("stringKey");
            fail("Expected an exception");
        } catch (JSONException e) { 
            assertEquals("Expecting an exception message",
                    "JSONObject[\"stringKey\"] is not a float (class java.lang.String : hello world!).",
                    e.getMessage());
        }
        try {
            jsonObject.getInt("nonKey");
            fail("Expected an exception");
        } catch (JSONException e) { 
            assertEquals("Expecting an exception message",
                    "JSONObject[\"nonKey\"] not found.",
                    e.getMessage());
        }
        try {
            jsonObject.getInt("stringKey");
            fail("Expected an exception");
        } catch (JSONException e) { 
            assertEquals("Expecting an exception message", 
                    "JSONObject[\"stringKey\"] is not a int (class java.lang.String : hello world!).",
                    e.getMessage());
        }
        try {
            jsonObject.getLong("nonKey");
            fail("Expected an exception");
        } catch (JSONException e) { 
            assertEquals("Expecting an exception message", 
                    "JSONObject[\"nonKey\"] not found.",
                    e.getMessage());
        }
        try {
            jsonObject.getLong("stringKey");
            fail("Expected an exception");
        } catch (JSONException e) { 
            assertEquals("Expecting an exception message", 
                    "JSONObject[\"stringKey\"] is not a long (class java.lang.String : hello world!).",
                    e.getMessage());
        }
        try {
            jsonObject.getJSONArray("nonKey");
            fail("Expected an exception");
        } catch (JSONException e) { 
            assertEquals("Expecting an exception message", 
                    "JSONObject[\"nonKey\"] not found.",
                    e.getMessage());
        }
        try {
            jsonObject.getJSONArray("stringKey");
            fail("Expected an exception");
        } catch (JSONException e) { 
            assertEquals("Expecting an exception message", 
                    "JSONObject[\"stringKey\"] is not a JSONArray (class java.lang.String : hello world!).",
                    e.getMessage());
        }
        try {
            jsonObject.getJSONObject("nonKey");
            fail("Expected an exception");
        } catch (JSONException e) { 
            assertEquals("Expecting an exception message", 
                    "JSONObject[\"nonKey\"] not found.",
                    e.getMessage());
        }
        try {
            jsonObject.getJSONObject("stringKey");
            fail("Expected an exception");
        } catch (JSONException e) { 
            assertEquals("Expecting an exception message", 
                    "JSONObject[\"stringKey\"] is not a JSONObject (class java.lang.String : hello world!).",
                    e.getMessage());
        }
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * This test documents an unexpected numeric behavior.
     * A double that ends with .0 is parsed, serialized, then
     * parsed again. On the second parse, it has become an int.
     */
    @Test
    public void unexpectedDoubleToIntConversion() {
        String key30 = "key30";
        String key31 = "key31";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key30, Double.valueOf(3.0));
        jsonObject.put(key31, Double.valueOf(3.1));

        assertTrue("3.0 should remain a double",
                jsonObject.getDouble(key30) == 3); 
        assertTrue("3.1 should remain a double",
                jsonObject.getDouble(key31) == 3.1); 
 
        // turns 3.0 into 3.
        String serializedString = jsonObject.toString();
        JSONObject deserialized = new JSONObject(serializedString);
        assertTrue("3.0 is now an int", deserialized.get(key30) instanceof Integer);
        assertTrue("3.0 can still be interpreted as a double",
                deserialized.getDouble(key30) == 3.0);
        assertTrue("3.1 remains a double", deserialized.getDouble(key31) == 3.1);
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * Document behaviors of big numbers. Includes both JSONObject
     * and JSONArray tests
     */
    @SuppressWarnings("boxing")
    @Test
    public void bigNumberOperations() {
        /**
         * JSONObject tries to parse BigInteger as a bean, but it only has
         * one getter, getLowestBitSet(). The value is lost and an unhelpful
         * value is stored. This should be fixed.
         */
        BigInteger bigInteger = new BigInteger("123456789012345678901234567890");
        JSONObject jsonObject0 = new JSONObject(bigInteger);
        Object obj = jsonObject0.get("lowestSetBit");
        assertTrue("JSONObject only has 1 value", jsonObject0.length() == 1);
        assertTrue("JSONObject parses BigInteger as the Integer lowestBitSet",
                obj instanceof Integer);
        assertTrue("this bigInteger lowestBitSet happens to be 1",
                obj.equals(1));

        /**
         * JSONObject tries to parse BigDecimal as a bean, but it has
         * no getters, The value is lost and no value is stored.
         * This should be fixed.
         */
        BigDecimal bigDecimal = new BigDecimal(
                "123456789012345678901234567890.12345678901234567890123456789");
        JSONObject jsonObject1 = new JSONObject(bigDecimal);
        assertTrue("large bigDecimal is not stored", jsonObject1.isEmpty());

        /**
         * JSONObject put(String, Object) method stores and serializes
         * bigInt and bigDec correctly. Nothing needs to change. 
         */
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("bigInt", bigInteger);
        assertTrue("jsonObject.put() handles bigInt correctly",
                jsonObject2.get("bigInt").equals(bigInteger));
        assertTrue("jsonObject.getBigInteger() handles bigInt correctly",
                jsonObject2.getBigInteger("bigInt").equals(bigInteger));
        assertTrue("jsonObject.optBigInteger() handles bigInt correctly",
                jsonObject2.optBigInteger("bigInt", BigInteger.ONE).equals(bigInteger));
        assertTrue("jsonObject serializes bigInt correctly",
                jsonObject2.toString().equals("{\"bigInt\":123456789012345678901234567890}"));
        assertTrue("BigInteger as BigDecimal",
                jsonObject2.getBigDecimal("bigInt").equals(new BigDecimal(bigInteger)));

        
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("bigDec", bigDecimal);
        assertTrue("jsonObject.put() handles bigDec correctly",
                jsonObject3.get("bigDec").equals(bigDecimal));
        assertTrue("jsonObject.getBigDecimal() handles bigDec correctly",
                jsonObject3.getBigDecimal("bigDec").equals(bigDecimal));
        assertTrue("jsonObject.optBigDecimal() handles bigDec correctly",
                jsonObject3.optBigDecimal("bigDec", BigDecimal.ONE).equals(bigDecimal));
        assertTrue("jsonObject serializes bigDec correctly",
                jsonObject3.toString().equals(
                "{\"bigDec\":123456789012345678901234567890.12345678901234567890123456789}"));

        assertTrue("BigDecimal as BigInteger",
                jsonObject3.getBigInteger("bigDec").equals(bigDecimal.toBigInteger()));
        /**
         * exercise some exceptions
         */
        try {
            // bigInt key does not exist
            jsonObject3.getBigDecimal("bigInt");
            fail("expected an exeption");
        } catch (JSONException ignored) {}
        obj = jsonObject3.optBigDecimal("bigInt", BigDecimal.ONE);
        assertTrue("expected BigDecimal", obj.equals(BigDecimal.ONE));
        jsonObject3.put("stringKey",  "abc");
        try {
            jsonObject3.getBigDecimal("stringKey");
            fail("expected an exeption");
        } catch (JSONException ignored) {}
        obj = jsonObject3.optBigInteger("bigDec", BigInteger.ONE);
        assertTrue("expected BigInteger", obj instanceof BigInteger);
        assertEquals(bigDecimal.toBigInteger(), obj);

        /**
         * JSONObject.numberToString() works correctly, nothing to change.
         */
        String str = JSONObject.numberToString(bigInteger);
        assertTrue("numberToString() handles bigInteger correctly",
                str.equals("123456789012345678901234567890"));
        str = JSONObject.numberToString(bigDecimal);
        assertTrue("numberToString() handles bigDecimal correctly",
                str.equals("123456789012345678901234567890.12345678901234567890123456789"));

        /**
         * JSONObject.stringToValue() turns bigInt into an accurate string,
         * and rounds bigDec. This incorrect, but users may have come to 
         * expect this behavior. Change would be marginally better, but 
         * might inconvenience users.
         */
        obj = JSONObject.stringToValue(bigInteger.toString());
        assertTrue("stringToValue() turns bigInteger string into Number",
                obj instanceof Number);
        obj = JSONObject.stringToValue(bigDecimal.toString());
        assertTrue("stringToValue() changes bigDecimal Number",
                obj instanceof Number);

        /**
         * wrap() vs put() big number behavior is now the same.
         */
        // bigInt map ctor 
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("bigInt", bigInteger);
        JSONObject jsonObject4 = new JSONObject(map);
        String actualFromMapStr = jsonObject4.toString();
        assertTrue("bigInt in map (or array or bean) is a string",
                actualFromMapStr.equals(
                "{\"bigInt\":123456789012345678901234567890}"));
        // bigInt put
        JSONObject jsonObject5 = new JSONObject();
        jsonObject5.put("bigInt", bigInteger);
        String actualFromPutStr = jsonObject5.toString();
        assertTrue("bigInt from put is a number",
                actualFromPutStr.equals(
                "{\"bigInt\":123456789012345678901234567890}"));
        // bigDec map ctor
        map = new HashMap<String, Object>();
        map.put("bigDec", bigDecimal);
        JSONObject jsonObject6 = new JSONObject(map);
        actualFromMapStr = jsonObject6.toString();
        assertTrue("bigDec in map (or array or bean) is a bigDec",
                actualFromMapStr.equals(
                "{\"bigDec\":123456789012345678901234567890.12345678901234567890123456789}"));
        // bigDec put
        JSONObject jsonObject7 = new JSONObject();
        jsonObject7.put("bigDec", bigDecimal);
        actualFromPutStr = jsonObject7.toString();
        assertTrue("bigDec from put is a number",
                actualFromPutStr.equals(
                "{\"bigDec\":123456789012345678901234567890.12345678901234567890123456789}"));
        // bigInt,bigDec put 
        JSONArray jsonArray0 = new JSONArray();
        jsonArray0.put(bigInteger);
        jsonArray0.put(bigDecimal);
        actualFromPutStr = jsonArray0.toString();
        assertTrue("bigInt, bigDec from put is a number",
                actualFromPutStr.equals(
                "[123456789012345678901234567890,123456789012345678901234567890.12345678901234567890123456789]"));
        assertTrue("getBigInt is bigInt", jsonArray0.getBigInteger(0).equals(bigInteger));
        assertTrue("getBigDec is bigDec", jsonArray0.getBigDecimal(1).equals(bigDecimal));
        assertTrue("optBigInt is bigInt", jsonArray0.optBigInteger(0, BigInteger.ONE).equals(bigInteger));
        assertTrue("optBigDec is bigDec", jsonArray0.optBigDecimal(1, BigDecimal.ONE).equals(bigDecimal));
        jsonArray0.put(Boolean.TRUE);
        try {
            jsonArray0.getBigInteger(2);
            fail("should not be able to get big int");
        } catch (Exception ignored) {}
        try {
            jsonArray0.getBigDecimal(2);
            fail("should not be able to get big dec");
        } catch (Exception ignored) {}
        assertTrue("optBigInt is default", jsonArray0.optBigInteger(2, BigInteger.ONE).equals(BigInteger.ONE));
        assertTrue("optBigDec is default", jsonArray0.optBigDecimal(2, BigDecimal.ONE).equals(BigDecimal.ONE));

        // bigInt,bigDec list ctor
        List<Object> list = new ArrayList<Object>();
        list.add(bigInteger);
        list.add(bigDecimal);
        JSONArray jsonArray1 = new JSONArray(list);
        String actualFromListStr = jsonArray1.toString();
        assertTrue("bigInt, bigDec in list is a bigInt, bigDec",
                actualFromListStr.equals(
                "[123456789012345678901234567890,123456789012345678901234567890.12345678901234567890123456789]"));
        // bigInt bean ctor
        MyBigNumberBean myBigNumberBean = mock(MyBigNumberBean.class);
        when(myBigNumberBean.getBigInteger()).thenReturn(new BigInteger("123456789012345678901234567890"));
        JSONObject jsonObject8 = new JSONObject(myBigNumberBean);
        String actualFromBeanStr = jsonObject8.toString();
        // can't do a full string compare because mockery adds an extra key/value
        assertTrue("bigInt from bean ctor is a bigInt",
                actualFromBeanStr.contains("123456789012345678901234567890"));
        // bigDec bean ctor
        myBigNumberBean = mock(MyBigNumberBean.class);
        when(myBigNumberBean.getBigDecimal()).thenReturn(new BigDecimal("123456789012345678901234567890.12345678901234567890123456789"));
        jsonObject8 = new JSONObject(myBigNumberBean);
        actualFromBeanStr = jsonObject8.toString();
        // can't do a full string compare because mockery adds an extra key/value
        assertTrue("bigDec from bean ctor is a bigDec",
                actualFromBeanStr.contains("123456789012345678901234567890.12345678901234567890123456789"));
        // bigInt,bigDec wrap()
        obj = JSONObject.wrap(bigInteger);
        assertTrue("wrap() returns big num",obj.equals(bigInteger));
        obj = JSONObject.wrap(bigDecimal);
        assertTrue("wrap() returns string",obj.equals(bigDecimal));
        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                jsonObject0, jsonObject1, jsonObject2, jsonObject3, jsonObject4,
                jsonObject5, jsonObject6, jsonObject7, jsonObject8
        )));
        Util.checkJSONArrayMaps(jsonArray0, jsonObject0.getMapType());
        Util.checkJSONArrayMaps(jsonArray1, jsonObject0.getMapType());
    }

    /**
     * The purpose for the static method getNames() methods are not clear.
     * This method is not called from within JSON-Java. Most likely
     * uses are to prep names arrays for:  
     * JSONObject(JSONObject jo, String[] names)
     * JSONObject(Object object, String names[]),
     */
    @Test
    public void jsonObjectNames() {

        // getNames() from null JSONObject
        assertTrue("null names from null Object", 
                null == JSONObject.getNames((Object)null));

        // getNames() from object with no fields
        assertTrue("null names from Object with no fields", 
                null == JSONObject.getNames(new MyJsonString()));

        // getNames from new JSONOjbect
        JSONObject jsonObject0 = new JSONObject();
        String [] names = JSONObject.getNames(jsonObject0);
        assertTrue("names should be null", names == null);

        
        // getNames() from empty JSONObject
        String emptyStr = "{}";
        JSONObject jsonObject1 = new JSONObject(emptyStr);
        assertTrue("empty JSONObject should have null names",
                null == JSONObject.getNames(jsonObject1));

        // getNames() from JSONObject
        String str = 
            "{"+
                "\"trueKey\":true,"+
                "\"falseKey\":false,"+
                "\"stringKey\":\"hello world!\""+
            "}";
        JSONObject jsonObject2 = new JSONObject(str);
        names = JSONObject.getNames(jsonObject2);
        JSONArray jsonArray0 = new JSONArray(names);

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider()
                .parse(jsonArray0.toString());
        List<?> docList = JsonPath.read(doc, "$");
        assertTrue("expected 3 items", docList.size() == 3);
        assertTrue(
                "expected to find trueKey",
                ((List<?>) JsonPath.read(doc, "$[?(@=='trueKey')]")).size() == 1);
        assertTrue(
                "expected to find falseKey",
                ((List<?>) JsonPath.read(doc, "$[?(@=='falseKey')]")).size() == 1);
        assertTrue(
                "expected to find stringKey",
                ((List<?>) JsonPath.read(doc, "$[?(@=='stringKey')]")).size() == 1);

        /**
         * getNames() from an enum with properties has an interesting result.
         * It returns the enum values, not the selected enum properties
         */
        MyEnumField myEnumField = MyEnumField.VAL1;
        names = JSONObject.getNames(myEnumField);

        // validate JSON
        JSONArray jsonArray1 = new JSONArray(names);
        doc = Configuration.defaultConfiguration().jsonProvider()
                .parse(jsonArray1.toString());
        docList = JsonPath.read(doc, "$");
        assertTrue("expected 3 items", docList.size() == 3);
        assertTrue(
                "expected to find VAL1",
                ((List<?>) JsonPath.read(doc, "$[?(@=='VAL1')]")).size() == 1);
        assertTrue(
                "expected to find VAL2",
                ((List<?>) JsonPath.read(doc, "$[?(@=='VAL2')]")).size() == 1);
        assertTrue(
                "expected to find VAL3",
                ((List<?>) JsonPath.read(doc, "$[?(@=='VAL3')]")).size() == 1);

        /**
         * A bean is also an object. But in order to test the static
         * method getNames(), this particular bean needs some public
         * data members.
         */
        MyPublicClass myPublicClass = new MyPublicClass();
        names = JSONObject.getNames(myPublicClass);

        // validate JSON
        JSONArray jsonArray2 = new JSONArray(names);
        doc = Configuration.defaultConfiguration().jsonProvider()
                .parse(jsonArray2.toString());
        docList = JsonPath.read(doc, "$");
        assertTrue("expected 2 items", docList.size() == 2);
        assertTrue(
                "expected to find publicString",
                ((List<?>) JsonPath.read(doc, "$[?(@=='publicString')]")).size() == 1);
        assertTrue(
                "expected to find publicInt",
                ((List<?>) JsonPath.read(doc, "$[?(@=='publicInt')]")).size() == 1);
        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                jsonObject0, jsonObject1, jsonObject2
        )));
        Util.checkJSONArrayMaps(jsonArray0, jsonObject0.getMapType());
        Util.checkJSONArrayMaps(jsonArray1, jsonObject0.getMapType());
        Util.checkJSONArrayMaps(jsonArray2, jsonObject0.getMapType());
    }

    /**
     * Populate a JSONArray from an empty JSONObject names() method.
     * It should be empty.
     */
    @Test
    public void emptyJsonObjectNamesToJsonAray() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = jsonObject.names();
        assertTrue("jsonArray should be null", jsonArray == null);
        Util.checkJSONObjectMaps(jsonObject);
        Util.checkJSONArrayMaps(jsonArray, jsonObject.getMapType());
    }

    /**
     * Populate a JSONArray from a JSONObject names() method.
     * Confirm that it contains the expected names.
     */
    @Test
    public void jsonObjectNamesToJsonAray() {
        String str = 
            "{"+
                "\"trueKey\":true,"+
                "\"falseKey\":false,"+
                "\"stringKey\":\"hello world!\""+
            "}";

        JSONObject jsonObject = new JSONObject(str);
        JSONArray jsonArray = jsonObject.names();

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertTrue("expected 3 top level items", ((List<?>)(JsonPath.read(doc, "$"))).size() == 3);
        assertTrue("expected to find trueKey", ((List<?>) JsonPath.read(doc, "$[?(@=='trueKey')]")).size() == 1);
        assertTrue("expected to find falseKey", ((List<?>) JsonPath.read(doc, "$[?(@=='falseKey')]")).size() == 1);
        assertTrue("expected to find stringKey", ((List<?>) JsonPath.read(doc, "$[?(@=='stringKey')]")).size() == 1);
        Util.checkJSONObjectMaps(jsonObject);
        Util.checkJSONArrayMaps(jsonArray, jsonObject.getMapType());
    }

    /**
     * Exercise the JSONObject increment() method.
     */
    @SuppressWarnings("cast")
    @Test
    public void jsonObjectIncrement() {
        String str = 
            "{"+
                "\"keyLong\":9999999991,"+
                "\"keyDouble\":1.1"+
             "}";
        JSONObject jsonObject = new JSONObject(str);
        jsonObject.increment("keyInt");
        jsonObject.increment("keyInt");
        jsonObject.increment("keyLong");
        jsonObject.increment("keyDouble");
        jsonObject.increment("keyInt");
        jsonObject.increment("keyLong");
        jsonObject.increment("keyDouble");
        /**
         * JSONObject constructor won't handle these types correctly, but
         * adding them via put works.
         */
        jsonObject.put("keyFloat", 1.1f);
        jsonObject.put("keyBigInt", new BigInteger("123456789123456789123456789123456780"));
        jsonObject.put("keyBigDec", new BigDecimal("123456789123456789123456789123456780.1"));
        jsonObject.increment("keyFloat");
        jsonObject.increment("keyFloat");
        jsonObject.increment("keyBigInt");
        jsonObject.increment("keyBigDec");

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 6 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 6);
        assertTrue("expected 3", Integer.valueOf(3).equals(jsonObject.query("/keyInt")));
        assertTrue("expected 9999999993", Long.valueOf(9999999993L).equals(jsonObject.query("/keyLong")));
        assertTrue("expected 3.1", BigDecimal.valueOf(3.1).equals(jsonObject.query("/keyDouble")));
        assertTrue("expected 123456789123456789123456789123456781", new BigInteger("123456789123456789123456789123456781").equals(jsonObject.query("/keyBigInt")));
        assertTrue("expected 123456789123456789123456789123456781.1", new BigDecimal("123456789123456789123456789123456781.1").equals(jsonObject.query("/keyBigDec")));

        /**
         * Should work the same way on any platform! @see https://docs.oracle
         * .com/javase/specs/jls/se7/html/jls-4.html#jls-4.2.3 This is the
         * effect of a float to double conversion and is inherent to the
         * shortcomings of the IEEE 754 format, when converting 32-bit into
         * double-precision 64-bit. Java type-casts float to double. A 32 bit
         * float is type-casted to 64 bit double by simply appending zero-bits
         * to the mantissa (and extended the signed exponent by 3 bits.) and
         * there is no way to obtain more information than it is stored in the
         * 32-bits float.
         * 
         * Like 1/3 cannot be represented as base10 number because it is
         * periodically, 1/5 (for example) cannot be represented as base2 number
         * since it is periodically in base2 (take a look at
         * http://www.h-schmidt.net/FloatConverter/) The same happens to 3.1,
         * that decimal number (base10 representation) is periodic in base2
         * representation, therefore appending zero-bits is inaccurate. Only
         * repeating the periodically occurring bits (0110) would be a proper
         * conversion. However one cannot detect from a 32 bit IEE754
         * representation which bits would "repeat infinitely", since the
         * missing bits would not fit into the 32 bit float, i.e. the
         * information needed simply is not there!
         */
        assertEquals(Float.valueOf(3.1f), jsonObject.query("/keyFloat"));

        /**
         * float f = 3.1f; double df = (double) f; double d = 3.1d;
         * System.out.println
         * (Integer.toBinaryString(Float.floatToRawIntBits(f)));
         * System.out.println
         * (Long.toBinaryString(Double.doubleToRawLongBits(df)));
         * System.out.println
         * (Long.toBinaryString(Double.doubleToRawLongBits(d)));
         * 
         * - Float:
         * seeeeeeeemmmmmmmmmmmmmmmmmmmmmmm
         * 1000000010001100110011001100110
         * - Double
         * seeeeeeeeeeemmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
         * 10000000   10001100110011001100110
         * 100000000001000110011001100110011000000000000000000000000000000
         * 100000000001000110011001100110011001100110011001100110011001101
         */

        /**
        * Examples of well documented but probably unexpected behavior in 
        * java / with 32-bit float to 64-bit float conversion.
        */
        assertFalse("Document unexpected behaviour with explicit type-casting float as double!", (double)0.2f == 0.2d );
        assertFalse("Document unexpected behaviour with implicit type-cast!", 0.2f == 0.2d );
        Double d1 = Double.valueOf( 1.1f );
        Double d2 = Double.valueOf( "1.1f" );
        assertFalse( "Document implicit type cast from float to double before calling Double(double d) constructor", d1.equals( d2 ) );

        assertTrue( "Correctly converting float to double via base10 (string) representation!", Double.valueOf( 3.1d ).equals(  Double.valueOf( Float.valueOf( 3.1f ).toString() ) ) );

        // Pinpointing the not so obvious "buggy" conversion from float to double in JSONObject
        JSONObject jo = new JSONObject();
        jo.put( "bug", 3.1f ); // will call put( String key, double value ) with implicit and "buggy" type-cast from float to double
        assertFalse( "The java-compiler did add some zero bits for you to the mantissa (unexpected, but well documented)", jo.get( "bug" ).equals(  Double.valueOf( 3.1d ) ) );

        JSONObject inc = new JSONObject();
        inc.put( "bug", Float.valueOf( 3.1f ) ); // This will put in instance of Float into JSONObject, i.e. call put( String key, Object value )
        assertTrue( "Everything is ok here!", inc.get( "bug" ) instanceof Float );
        inc.increment( "bug" ); // after adding 1, increment will call put( String key, double value ) with implicit and "buggy" type-cast from float to double!
        // this.put(key, (Float) value + 1);
        // 1.        The (Object)value will be typecasted to (Float)value since it is an instanceof Float actually nothing is done. 
        // 2.        Float instance will be autoboxed into float because the + operator will work on primitives not Objects!
        // 3.        A float+float operation will be performed and results into a float primitive.
        // 4.        There is no method that matches the signature put( String key, float value), java-compiler will choose the method
        //                put( String key, double value) and does an implicit type-cast(!) by appending zero-bits to the mantissa
        assertTrue( "JSONObject increment converts Float to Double", jo.get( "bug" ) instanceof Float );
        // correct implementation (with change of behavior) would be:
        // this.put(key, new Float((Float) value + 1)); 
        // Probably it would be better to deprecate the method and remove some day, while convenient processing the "payload" is not
        // really in the scope of a JSON-library (IMHO.)
        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                jsonObject, inc
        )));
    }

    /**
     * Exercise JSONObject numberToString() method
     */
    @SuppressWarnings("boxing")
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

    /**
     * Exercise JSONObject put() and similar() methods
     */
    @SuppressWarnings("boxing")
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

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 4 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 4);
        assertTrue("expected true", Boolean.TRUE.equals(jsonObject.query("/trueKey")));
        assertTrue("expected false", Boolean.FALSE.equals(jsonObject.query("/falseKey")));
        assertTrue("expected 3 arrayKey items", ((List<?>)(JsonPath.read(doc, "$.arrayKey"))).size() == 3);
        assertTrue("expected 0", Integer.valueOf(0).equals(jsonObject.query("/arrayKey/0")));
        assertTrue("expected 1", Integer.valueOf(1).equals(jsonObject.query("/arrayKey/1")));
        assertTrue("expected 2", Integer.valueOf(2).equals(jsonObject.query("/arrayKey/2")));
        assertTrue("expected 4 objectKey items", ((Map<?,?>)(JsonPath.read(doc, "$.objectKey"))).size() == 4);
        assertTrue("expected myVal1", "myVal1".equals(jsonObject.query("/objectKey/myKey1")));
        assertTrue("expected myVal2", "myVal2".equals(jsonObject.query("/objectKey/myKey2")));
        assertTrue("expected myVal3", "myVal3".equals(jsonObject.query("/objectKey/myKey3")));
        assertTrue("expected myVal4", "myVal4".equals(jsonObject.query("/objectKey/myKey4")));

        jsonObject.remove("trueKey");
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        assertTrue("unequal jsonObjects should not be similar",
                !jsonObject.similar(expectedJsonObject));
        assertTrue("jsonObject should not be similar to jsonArray",
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
        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                jsonObject, expectedJsonObject, aCompareValueJsonObject,
                aCompareArrayJsonObject, aCompareObjectJsonObject, aCompareArrayJsonObject,
                bCompareValueJsonObject, bCompareArrayJsonObject, bCompareObjectJsonObject,
                bCompareArrayJsonObject
        )));
    }

    /**
     * Exercise JSONObject toString() method
     */
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

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 4 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 4);
        assertTrue("expected true", Boolean.TRUE.equals(jsonObject.query("/trueKey")));
        assertTrue("expected false", Boolean.FALSE.equals(jsonObject.query("/falseKey")));
        assertTrue("expected 3 arrayKey items", ((List<?>)(JsonPath.read(doc, "$.arrayKey"))).size() == 3);
        assertTrue("expected 0", Integer.valueOf(0).equals(jsonObject.query("/arrayKey/0")));
        assertTrue("expected 1", Integer.valueOf(1).equals(jsonObject.query("/arrayKey/1")));
        assertTrue("expected 2", Integer.valueOf(2).equals(jsonObject.query("/arrayKey/2")));
        assertTrue("expected 4 objectKey items", ((Map<?,?>)(JsonPath.read(doc, "$.objectKey"))).size() == 4);
        assertTrue("expected myVal1", "myVal1".equals(jsonObject.query("/objectKey/myKey1")));
        assertTrue("expected myVal2", "myVal2".equals(jsonObject.query("/objectKey/myKey2")));
        assertTrue("expected myVal3", "myVal3".equals(jsonObject.query("/objectKey/myKey3")));
        assertTrue("expected myVal4", "myVal4".equals(jsonObject.query("/objectKey/myKey4")));
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * Exercise JSONObject toString() method with various indent levels.
     */
    @Test
    public void jsonObjectToStringIndent() {
        String jsonObject0Str =
                "{"+
                        "\"key1\":" +
                                "[1,2," +
                                        "{\"key3\":true}" +
                                "],"+
                        "\"key2\":" +
                                "{\"key1\":\"val1\",\"key2\":" +
                                        "{\"key2\":\"val2\"}" +
                                "},"+
                        "\"key3\":" +
                                "[" +
                                        "[1,2.1]" +
                                "," +
                                        "[null]" +
                                "]"+
                        "}";

        String jsonObject1Str =
                "{\n" +
                " \"key1\": [\n" +
                "  1,\n" +
                "  2,\n" +
                "  {\"key3\": true}\n" +
                " ],\n" +
                " \"key2\": {\n" +
                "  \"key1\": \"val1\",\n" +
                "  \"key2\": {\"key2\": \"val2\"}\n" +
                " },\n" +
                " \"key3\": [\n" +
                "  [\n" +
                "   1,\n" +
                "   2.1\n" +
                "  ],\n" +
                "  [null]\n" +
                " ]\n" +
                "}";
        String jsonObject4Str =
                "{\n" +
                "    \"key1\": [\n" +
                "        1,\n" +
                "        2,\n" +
                "        {\"key3\": true}\n" +
                "    ],\n" +
                "    \"key2\": {\n" +
                "        \"key1\": \"val1\",\n" +
                "        \"key2\": {\"key2\": \"val2\"}\n" +
                "    },\n" +
                "    \"key3\": [\n" +
                "        [\n" +
                "            1,\n" +
                "            2.1\n" +
                "        ],\n" +
                "        [null]\n" +
                "    ]\n" +
                "}";
        JSONObject jsonObject = new JSONObject(jsonObject0Str);
        // contents are tested in other methods, in this case just validate the spacing by
        // checking length
        assertEquals("toString() length",jsonObject0Str.length(), jsonObject.toString().length());
        assertEquals("toString(0) length",jsonObject0Str.length(), jsonObject.toString(0).length());
        assertEquals("toString(1) length",jsonObject1Str.length(), jsonObject.toString(1).length());
        assertEquals("toString(4) length",jsonObject4Str.length(), jsonObject.toString(4).length());
        
        JSONObject jo = new JSONObject().put("TABLE", new JSONObject().put("yhoo", new JSONObject()));
        assertEquals("toString(2)","{\"TABLE\": {\"yhoo\": {}}}", jo.toString(2));
        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                jsonObject, jo
        )));
    }

    /**
     * Explores how JSONObject handles maps. Insert a string/string map
     * as a value in a JSONObject. It will remain a map. Convert the 
     * JSONObject to string, then create a new JSONObject from the string. 
     * In the new JSONObject, the value will be stored as a nested JSONObject.
     * Confirm that map and nested JSONObject have the same contents.
     */
    @Test
    public void jsonObjectToStringSuppressWarningOnCastToMap() {
        JSONObject jsonObject = new JSONObject();
        Map<String, String> map = new HashMap<>();
        map.put("abc", "def");
        jsonObject.put("key", map);

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 1 top level item", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 1);
        assertTrue("expected 1 key item", ((Map<?,?>)(JsonPath.read(doc, "$.key"))).size() == 1);
        assertTrue("expected def", "def".equals(jsonObject.query("/key/abc")));
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * Explores how JSONObject handles collections. Insert a string collection
     * as a value in a JSONObject. It will remain a collection. Convert the 
     * JSONObject to string, then create a new JSONObject from the string. 
     * In the new JSONObject, the value will be stored as a nested JSONArray.
     * Confirm that collection and nested JSONArray have the same contents.
     */
    @Test
    public void jsonObjectToStringSuppressWarningOnCastToCollection() {
        JSONObject jsonObject = new JSONObject();
        Collection<String> collection = new ArrayList<String>();
        collection.add("abc");
        // ArrayList will be added as an object
        jsonObject.put("key", collection);

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonObject.toString());
        assertTrue("expected 1 top level item", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 1);
        assertTrue("expected 1 key item", ((List<?>)(JsonPath.read(doc, "$.key"))).size() == 1);
        assertTrue("expected abc", "abc".equals(jsonObject.query("/key/0")));
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * Exercises the JSONObject.valueToString() method for various types
     */
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
            new JSONObject(JSONObject.valueToString(jsonObject))
                .similar(new JSONObject(jsonObject.toString()))
            );
        String jsonArrayStr = 
            "[1,2,3]";
        JSONArray jsonArray = new JSONArray(jsonArrayStr);
        assertTrue("jsonArray valueToString() incorrect",
                JSONObject.valueToString(jsonArray).equals(jsonArray.toString()));
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "val1");
        map.put("key2", "val2");
        map.put("key3", "val3");
        assertTrue("map valueToString() incorrect",
         new JSONObject(jsonObject.toString())
         .similar(new JSONObject(JSONObject.valueToString(map))));
        Collection<Integer> collection = new ArrayList<Integer>();
        collection.add(Integer.valueOf(1));
        collection.add(Integer.valueOf(2));
        collection.add(Integer.valueOf(3));
        assertTrue("collection valueToString() expected: "+
                jsonArray.toString()+ " actual: "+
                JSONObject.valueToString(collection),
                jsonArray.toString().equals(JSONObject.valueToString(collection))); 
        Integer[] array = { Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3) };
        assertTrue("array valueToString() incorrect",
                jsonArray.toString().equals(JSONObject.valueToString(array)));
        Util.checkJSONObjectMaps(jsonObject);
        Util.checkJSONArrayMaps(jsonArray, jsonObject.getMapType());
    }

    /**
     * Confirm that https://github.com/douglascrockford/JSON-java/issues/167 is fixed.
     * The following code was throwing a ClassCastException in the 
     * JSONObject(Map<String, Object>) constructor
     */
    @SuppressWarnings("boxing")
    @Test
    public void valueToStringConfirmException() {
        Map<Integer, String> myMap = new HashMap<Integer, String>();
        myMap.put(1,  "myValue");
        // this is the test, it should not throw an exception
        String str = JSONObject.valueToString(myMap);
        // confirm result, just in case
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(str);
        assertTrue("expected 1 top level item", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 1);
        assertTrue("expected myValue", "myValue".equals(JsonPath.read(doc, "$.1")));
    }

    /**
     * Exercise the JSONObject wrap() method. Sometimes wrap() will change
     * the object being wrapped, other times not. The purpose of wrap() is
     * to ensure the value is packaged in a way that is compatible with how
     * a JSONObject value or JSONArray value is supposed to be stored.
     */
    @Test
    public void wrapObject() {
        // wrap(null) returns NULL
        assertTrue("null wrap() incorrect",
                JSONObject.NULL == JSONObject.wrap(null));

        // wrap(Integer) returns Integer
        Integer in = Integer.valueOf(1);
        assertTrue("Integer wrap() incorrect",
                in == JSONObject.wrap(in));

        /**
         * This test is to document the preferred behavior if BigDecimal is
         * supported. Previously bd returned as a string, since it
         * is recognized as being a Java package class. Now with explicit
         * support for big numbers, it remains a BigDecimal 
         */
        Object bdWrap = JSONObject.wrap(BigDecimal.ONE);
        assertTrue("BigDecimal.ONE evaluates to ONE",
                bdWrap.equals(BigDecimal.ONE));

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
        collection.add(Integer.valueOf(1));
        collection.add(Integer.valueOf(2));
        collection.add(Integer.valueOf(3));
        JSONArray jsonArray = (JSONArray) (JSONObject.wrap(collection));

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertTrue("expected 3 top level items", ((List<?>)(JsonPath.read(doc, "$"))).size() == 3);
        assertTrue("expected 1", Integer.valueOf(1).equals(jsonArray.query("/0")));
        assertTrue("expected 2", Integer.valueOf(2).equals(jsonArray.query("/1")));
        assertTrue("expected 3", Integer.valueOf(3).equals(jsonArray.query("/2")));

        // wrap Array returns JSONArray
        Integer[] array = { Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3) };
        JSONArray integerArrayJsonArray = (JSONArray)(JSONObject.wrap(array));

        // validate JSON
        doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertTrue("expected 3 top level items", ((List<?>)(JsonPath.read(doc, "$"))).size() == 3);
        assertTrue("expected 1", Integer.valueOf(1).equals(jsonArray.query("/0")));
        assertTrue("expected 2", Integer.valueOf(2).equals(jsonArray.query("/1")));
        assertTrue("expected 3", Integer.valueOf(3).equals(jsonArray.query("/2")));

        // validate JSON
        doc = Configuration.defaultConfiguration().jsonProvider().parse(integerArrayJsonArray.toString());
        assertTrue("expected 3 top level items", ((List<?>)(JsonPath.read(doc, "$"))).size() == 3);
        assertTrue("expected 1", Integer.valueOf(1).equals(jsonArray.query("/0")));
        assertTrue("expected 2", Integer.valueOf(2).equals(jsonArray.query("/1")));
        assertTrue("expected 3", Integer.valueOf(3).equals(jsonArray.query("/2")));

        // wrap map returns JSONObject
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "val1");
        map.put("key2", "val2");
        map.put("key3", "val3");
        JSONObject mapJsonObject = (JSONObject) (JSONObject.wrap(map));

        // validate JSON
        doc = Configuration.defaultConfiguration().jsonProvider().parse(mapJsonObject.toString());
        assertTrue("expected 3 top level items", ((Map<?,?>)(JsonPath.read(doc, "$"))).size() == 3);
        assertTrue("expected val1", "val1".equals(mapJsonObject.query("/key1")));
        assertTrue("expected val2", "val2".equals(mapJsonObject.query("/key2")));
        assertTrue("expected val3", "val3".equals(mapJsonObject.query("/key3")));
        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                jsonObject, mapJsonObject
        )));
        Util.checkJSONArrayMaps(jsonArray, jsonObject.getMapType());
        Util.checkJSONArrayMaps(integerArrayJsonArray, jsonObject.getMapType());
    }

    
    /**
     * RFC 7159 defines control characters to be U+0000 through U+001F. This test verifies that the parser is checking for these in expected ways.
     */
    @Test
    public void jsonObjectParseControlCharacters(){
        for(int i = 0;i<=0x001f;i++){
            final String charString = String.valueOf((char)i);
            final String source = "{\"key\":\""+charString+"\"}";
            try {
                JSONObject jo = new JSONObject(source);
                assertTrue("Expected "+charString+"("+i+") in the JSON Object but did not find it.",charString.equals(jo.getString("key")));
                Util.checkJSONObjectMaps(jo);
            } catch (JSONException ex) {
                assertTrue("Only \\0 (U+0000), \\n (U+000A), and \\r (U+000D) should cause an error. Instead "+charString+"("+i+") caused an error",
                        i=='\0' || i=='\n' || i=='\r'
                );
            }
        }
    }

    @Test
    public void jsonObjectParseControlCharacterEOFAssertExceptionMessage(){
        char c = '\0';
        final String source = "{\"key\":\"" + c + "\"}";
        try {
            JSONObject jo = new JSONObject(source);
            fail("JSONException should be thrown");
        } catch (JSONException ex) {
            assertEquals("Unterminated string. " + "Character with int code 0" +
                    " is not allowed within a quoted string. at 8 [character 9 line 1]", ex.getMessage());
        }
    }

    @Test
    public void jsonObjectParseControlCharacterNewLineAssertExceptionMessage(){
        char[] chars = {'\n', '\r'};
        for( char c : chars) {
            final String source = "{\"key\":\"" + c + "\"}";
            try {
                JSONObject jo = new JSONObject(source);
                fail("JSONException should be thrown");
            } catch (JSONException ex) {
                assertEquals("Unterminated string. " + "Character with int code " + (int) c +
                        " is not allowed within a quoted string. at 9 [character 0 line 2]", ex.getMessage());
            }
        }
    }

    @Test
    public void jsonObjectParseUTF8EncodingAssertExceptionMessage(){
        String c = "\\u123x";
        final String source = "{\"key\":\"" + c + "\"}";
        try {
            JSONObject jo = new JSONObject(source);
            fail("JSONException should be thrown");
        } catch (JSONException ex) {
            assertEquals("Illegal escape. \\u must be followed by a 4 digit hexadecimal number. " +
                    "\\123x is not valid. at 14 [character 15 line 1]", ex.getMessage());
        }
    }

    @Test
    public void jsonObjectParseIllegalEscapeAssertExceptionMessage(){
        String c = "\\x";
        final String source = "{\"key\":\"" + c + "\"}";
        try {
            JSONObject jo = new JSONObject(source);
            fail("JSONException should be thrown");
        } catch (JSONException ex) {
            assertEquals("Illegal escape. Escape sequence  " + c + " is not valid." +
                    " at 10 [character 11 line 1]", ex.getMessage());
        }
    }

    @Test
    public void parsingErrorTrailingCurlyBrace () {
        try {
            // does not end with '}'
            String str = "{";
            assertNull("Expected an exception", new JSONObject(str));
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "A JSONObject text must end with '}' at 1 [character 2 line 1]",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorInitialCurlyBrace() {
        try {
            // does not start with '{'
            String str = "abc";
            assertNull("Expected an exception", new JSONObject(str));
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "A JSONObject text must begin with '{' at 1 [character 2 line 1]",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorNoColon() {
        try {
            // key with no ':'
            String str = "{\"myKey\" = true}";
            assertNull("Expected an exception", new JSONObject(str));
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "Expected a ':' after a key at 10 [character 11 line 1]",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorNoCommaSeparator() {
        try {
            // entries with no ',' separator
            String str = "{\"myKey\":true \"myOtherKey\":false}";
            assertNull("Expected an exception", new JSONObject(str));
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "Expected a ',' or '}' at 15 [character 16 line 1]",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorKeyIsNestedMap() {
        try {
            // key is a nested map
            String str = "{{\"foo\": \"bar\"}: \"baz\"}";
            assertNull("Expected an exception", new JSONObject(str));
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "Missing value at 1 [character 2 line 1]",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorKeyIsNestedArrayWithMap() {
        try {
            // key is a nested array containing a map
            String str = "{\"a\": 1, [{\"foo\": \"bar\"}]: \"baz\"}";
            assertNull("Expected an exception", new JSONObject(str));
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "Missing value at 9 [character 10 line 1]",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorKeyContainsCurlyBrace() {
        try {
            // key contains }
            String str = "{foo}: 2}";
            assertNull("Expected an exception", new JSONObject(str));
        } catch (JSONException e) {
//            assertEquals("Expecting an exception message",
//                    "Expected a ':' after a key at 5 [character 6 line 1]",
//                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorKeyContainsSquareBrace() {
        try {
            // key contains ]
            String str = "{foo]: 2}";
            assertNull("Expected an exception", new JSONObject(str));
        } catch (JSONException e) {
//            assertEquals("Expecting an exception message",
//                    "Expected a ':' after a key at 5 [character 6 line 1]",
//                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorKeyContainsBinaryZero() {
        try {
            // \0 after ,
            String str = "{\"myKey\":true, \0\"myOtherKey\":false}";
            assertNull("Expected an exception", new JSONObject(str));
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "A JSONObject text must end with '}' at 15 [character 16 line 1]",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorAppendToWrongValue() {
        try {
            // append to wrong value
            String str = "{\"myKey\":true, \"myOtherKey\":false}";
            JSONObject jsonObject = new JSONObject(str);
            jsonObject.append("myKey", "hello");
            fail("Expected an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "JSONObject[\"myKey\"] is not a JSONArray (null).",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorIncrementWrongValue() {
        try {
            // increment wrong value
            String str = "{\"myKey\":true, \"myOtherKey\":false}";
            JSONObject jsonObject = new JSONObject(str);
            jsonObject.increment("myKey");
            fail("Expected an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "Unable to increment [\"myKey\"].",
                    e.getMessage());
        }
    }
    @Test
    public void parsingErrorInvalidKey() {
        try {
            // invalid key
            String str = "{\"myKey\":true, \"myOtherKey\":false}";
            JSONObject jsonObject = new JSONObject(str);
            jsonObject.get(null);
            fail("Expected an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "Null key.",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorNumberToString() {
        try {
            // invalid numberToString()
            JSONObject.numberToString((Number) null);
            fail("Expected an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an exception message",
                    "Null pointer",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorPutOnceDuplicateKey() {
        try {
            // multiple putOnce key
            JSONObject jsonObject = new JSONObject("{}");
            jsonObject.putOnce("hello", "world");
            jsonObject.putOnce("hello", "world!");
            fail("Expected an exception");
        } catch (JSONException e) {
            assertTrue("", true);
        }
    }

    @Test
    public void parsingErrorInvalidDouble() {
        try {
            // test validity of invalid double
            JSONObject.testValidity(Double.NaN);
            fail("Expected an exception");
        } catch (JSONException e) {
            assertTrue("", true);
        }
    }

    @Test
    public void parsingErrorInvalidFloat() {
        try {
            // test validity of invalid float
            JSONObject.testValidity(Float.NEGATIVE_INFINITY);
            fail("Expected an exception");
        } catch (JSONException e) {
            assertTrue("", true);
        }
    }

    @Test
    public void parsingErrorDuplicateKeyException() {
        try {
            // test exception message when including a duplicate key (level 0)
            String str = "{\n"
                    + "    \"attr01\":\"value-01\",\n"
                    + "    \"attr02\":\"value-02\",\n"
                    + "    \"attr03\":\"value-03\",\n"
                    + "    \"attr03\":\"value-04\"\n"
                    + "}";
            new JSONObject(str);
            fail("Expected an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an expection message",
                    "Duplicate key \"attr03\" at 90 [character 13 line 5]",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorNestedDuplicateKeyException() {
        try {
            // test exception message when including a duplicate key (level 0) holding an object
            String str = "{\n"
                    + "    \"attr01\":\"value-01\",\n"
                    + "    \"attr02\":\"value-02\",\n"
                    + "    \"attr03\":\"value-03\",\n"
                    + "    \"attr03\": {"
                    + "        \"attr04-01\":\"value-04-01\",n"
                    + "        \"attr04-02\":\"value-04-02\",n"
                    + "        \"attr04-03\":\"value-04-03\"n"
                    + "    }\n"
                    + "}";
            new JSONObject(str);
            fail("Expected an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an expection message",
                    "Duplicate key \"attr03\" at 90 [character 13 line 5]",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorNestedDuplicateKeyWithArrayException() {
        try {
            // test exception message when including a duplicate key (level 0) holding an array
            String str = "{\n"
                    + "    \"attr01\":\"value-01\",\n"
                    + "    \"attr02\":\"value-02\",\n"
                    + "    \"attr03\":\"value-03\",\n"
                    + "    \"attr03\": [\n"
                    + "        {"
                    + "            \"attr04-01\":\"value-04-01\",n"
                    + "            \"attr04-02\":\"value-04-02\",n"
                    + "            \"attr04-03\":\"value-04-03\"n"
                    + "        }\n"
                    + "    ]\n"
                    + "}";
            new JSONObject(str);
            fail("Expected an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an expection message",
                    "Duplicate key \"attr03\" at 90 [character 13 line 5]",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorDuplicateKeyWithinNestedDictExceptionMessage() {
        try {
            // test exception message when including a duplicate key (level 1)
            String str = "{\n"
                    + "    \"attr01\":\"value-01\",\n"
                    + "    \"attr02\":\"value-02\",\n"
                    + "    \"attr03\":\"value-03\",\n"
                    + "    \"attr04\": {\n"
                    + "        \"attr04-01\":\"value04-01\",\n"
                    + "        \"attr04-02\":\"value04-02\",\n"
                    + "        \"attr04-03\":\"value04-03\",\n"
                    + "        \"attr04-03\":\"value04-04\"\n"
                    + "    }\n"
                    + "}";
            new JSONObject(str);
            fail("Expected an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an expection message",
                    "Duplicate key \"attr04-03\" at 215 [character 20 line 9]",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorDuplicateKeyDoubleNestedDictExceptionMessage() {
        try {
            // test exception message when including a duplicate key (level 1) holding an
            // object
            String str = "{\n"
                    + "    \"attr01\":\"value-01\",\n"
                    + "    \"attr02\":\"value-02\",\n"
                    + "    \"attr03\":\"value-03\",\n"
                    + "    \"attr04\": {\n"
                    + "        \"attr04-01\":\"value04-01\",\n"
                    + "        \"attr04-02\":\"value04-02\",\n"
                    + "        \"attr04-03\":\"value04-03\",\n"
                    + "        \"attr04-03\": {\n"
                    + "            \"attr04-04-01\":\"value04-04-01\",\n"
                    + "            \"attr04-04-02\":\"value04-04-02\",\n"
                    + "            \"attr04-04-03\":\"value04-04-03\",\n"
                    + "        }\n"
                    + "    }\n"
                    + "}";
            new JSONObject(str);
            fail("Expected an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an expection message",
                    "Duplicate key \"attr04-03\" at 215 [character 20 line 9]",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorDuplicateKeyNestedWithArrayExceptionMessage() {
        try {
            // test exception message when including a duplicate key (level 1) holding an
            // array
            String str = "{\n"
                    + "    \"attr01\":\"value-01\",\n"
                    + "    \"attr02\":\"value-02\",\n"
                    + "    \"attr03\":\"value-03\",\n"
                    + "    \"attr04\": {\n"
                    + "        \"attr04-01\":\"value04-01\",\n"
                    + "        \"attr04-02\":\"value04-02\",\n"
                    + "        \"attr04-03\":\"value04-03\",\n"
                    + "        \"attr04-03\": [\n"
                    + "            {\n"
                    + "                \"attr04-04-01\":\"value04-04-01\",\n"
                    + "                \"attr04-04-02\":\"value04-04-02\",\n"
                    + "                \"attr04-04-03\":\"value04-04-03\",\n"
                    + "            }\n"
                    + "        ]\n"
                    + "    }\n"
                    + "}";
            new JSONObject(str);
            fail("Expected an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an expection message",
                    "Duplicate key \"attr04-03\" at 215 [character 20 line 9]",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorDuplicateKeyWithinArrayExceptionMessage() {
        try {
            // test exception message when including a duplicate key in object (level 0)
            // within an array
            String str = "[\n"
                    + "    {\n"
                    + "        \"attr01\":\"value-01\",\n"
                    + "        \"attr02\":\"value-02\"\n"
                    + "    },\n"
                    + "    {\n"
                    + "        \"attr01\":\"value-01\",\n"
                    + "        \"attr01\":\"value-02\"\n"
                    + "    }\n"
                    + "]";
            new JSONArray(str);
            fail("Expected an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an expection message",
                    "Duplicate key \"attr01\" at 124 [character 17 line 8]",
                    e.getMessage());
        }
    }

    @Test
    public void parsingErrorDuplicateKeyDoubleNestedWithinArrayExceptionMessage() {
        try {
            // test exception message when including a duplicate key in object (level 1)
            // within an array
            String str = "[\n"
                    + "    {\n"
                    + "        \"attr01\":\"value-01\",\n"
                    + "        \"attr02\": {\n"
                    + "            \"attr02-01\":\"value-02-01\",\n"
                    + "            \"attr02-02\":\"value-02-02\"\n"
                    + "        }\n"
                    + "    },\n"
                    + "    {\n"
                    + "        \"attr01\":\"value-01\",\n"
                    + "        \"attr02\": {\n"
                    + "            \"attr02-01\":\"value-02-01\",\n"
                    + "            \"attr02-01\":\"value-02-02\"\n"
                    + "        }\n"
                    + "    }\n"
                    + "]";
            new JSONArray(str);
            fail("Expected an exception");
        } catch (JSONException e) {
            assertEquals("Expecting an expection message",
                    "Duplicate key \"attr02-01\" at 269 [character 24 line 13]",
                    e.getMessage());
        }
    }

    /**
     * Confirm behavior when putOnce() is called with null parameters
     */
    @Test
    public void jsonObjectPutOnceNull() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce(null, null);
        assertTrue("jsonObject should be empty", jsonObject.isEmpty());
        jsonObject.putOnce("", null);
        assertTrue("jsonObject should be empty", jsonObject.isEmpty());
        jsonObject.putOnce(null, "");
        assertTrue("jsonObject should be empty", jsonObject.isEmpty());
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * Exercise JSONObject opt(key, default) method.
     */
    @Test
    public void jsonObjectOptDefault() {

        String str = "{\"myKey\": \"myval\", \"hiKey\": null}";
        JSONObject jsonObject = new JSONObject(str);

        assertTrue("optBigDecimal() should return default BigDecimal",
                BigDecimal.TEN.compareTo(jsonObject.optBigDecimal("myKey", BigDecimal.TEN))==0);
        assertTrue("optBigInteger() should return default BigInteger",
                BigInteger.TEN.compareTo(jsonObject.optBigInteger("myKey",BigInteger.TEN ))==0);
        assertTrue("optBoolean() should return default boolean",
                 jsonObject.optBoolean("myKey", true));
        assertTrue("optBooleanObject() should return default Boolean",
                 jsonObject.optBooleanObject("myKey", true));
        assertTrue("optInt() should return default int",
                42 == jsonObject.optInt("myKey", 42));
        assertTrue("optIntegerObject() should return default Integer",
                Integer.valueOf(42).equals(jsonObject.optIntegerObject("myKey", 42)));
        assertTrue("optEnum() should return default Enum",
                MyEnum.VAL1.equals(jsonObject.optEnum(MyEnum.class, "myKey", MyEnum.VAL1)));
        assertTrue("optJSONArray() should return null ",
                null==jsonObject.optJSONArray("myKey"));
        assertTrue("optJSONArray() should return default JSONArray",
                "value".equals(jsonObject.optJSONArray("myKey", new JSONArray("[\"value\"]")).getString(0)));
        assertTrue("optJSONObject() should return default JSONObject ",
                jsonObject.optJSONObject("myKey", new JSONObject("{\"testKey\":\"testValue\"}")).getString("testKey").equals("testValue"));
        assertTrue("optLong() should return default long",
                42l == jsonObject.optLong("myKey", 42l));
        assertTrue("optLongObject() should return default Long",
                Long.valueOf(42l).equals(jsonObject.optLongObject("myKey", 42l)));
        assertTrue("optDouble() should return default double",
                42.3d == jsonObject.optDouble("myKey", 42.3d));
        assertTrue("optDoubleObject() should return default Double",
                Double.valueOf(42.3d).equals(jsonObject.optDoubleObject("myKey", 42.3d)));
        assertTrue("optFloat() should return default float",
                42.3f == jsonObject.optFloat("myKey", 42.3f));
        assertTrue("optFloatObject() should return default Float",
                Float.valueOf(42.3f).equals(jsonObject.optFloatObject("myKey", 42.3f)));
        assertTrue("optNumber() should return default Number",
                42l == jsonObject.optNumber("myKey", Long.valueOf(42)).longValue());
        assertTrue("optString() should return default string",
                "hi".equals(jsonObject.optString("hiKey", "hi")));
        Util.checkJSONObjectMaps(jsonObject);
    }
    
    /**
     * Exercise JSONObject opt(key, default) method when the key doesn't exist.
     */
    @Test
    public void jsonObjectOptNoKey() {

         JSONObject jsonObject = new JSONObject();
         
         assertNull(jsonObject.opt(null));

         assertTrue("optBigDecimal() should return default BigDecimal",
                 BigDecimal.TEN.compareTo(jsonObject.optBigDecimal("myKey", BigDecimal.TEN))==0);
         assertTrue("optBigInteger() should return default BigInteger",
                 BigInteger.TEN.compareTo(jsonObject.optBigInteger("myKey",BigInteger.TEN ))==0);
         assertTrue("optBoolean() should return default boolean",
                  jsonObject.optBoolean("myKey", true));
         assertTrue("optBooleanObject() should return default Boolean",
                  jsonObject.optBooleanObject("myKey", true));
         assertTrue("optInt() should return default int",
                 42 == jsonObject.optInt("myKey", 42));
         assertTrue("optIntegerObject() should return default Integer",
                 Integer.valueOf(42).equals(jsonObject.optIntegerObject("myKey", 42)));
         assertTrue("optEnum() should return default Enum",
                 MyEnum.VAL1.equals(jsonObject.optEnum(MyEnum.class, "myKey", MyEnum.VAL1)));
         assertTrue("optJSONArray() should return default JSONArray",
                 "value".equals(jsonObject.optJSONArray("myKey", new JSONArray("[\"value\"]")).getString(0)));
         assertTrue("optJSONArray() should return null ",
                 null==jsonObject.optJSONArray("myKey"));
         assertTrue("optJSONObject() should return default JSONObject ",
                jsonObject.optJSONObject("myKey", new JSONObject("{\"testKey\":\"testValue\"}")).getString("testKey").equals("testValue"));
         assertTrue("optLong() should return default long",
                 42l == jsonObject.optLong("myKey", 42l));
         assertTrue("optLongObject() should return default Long",
                 Long.valueOf(42l).equals(jsonObject.optLongObject("myKey", 42l)));
         assertTrue("optDouble() should return default double",
                 42.3d == jsonObject.optDouble("myKey", 42.3d));
         assertTrue("optDoubleObject() should return default Double",
                 Double.valueOf(42.3d).equals(jsonObject.optDoubleObject("myKey", 42.3d)));
         assertTrue("optFloat() should return default float",
                 42.3f == jsonObject.optFloat("myKey", 42.3f));
         assertTrue("optFloatObject() should return default Float",
                 Float.valueOf(42.3f).equals(jsonObject.optFloatObject("myKey", 42.3f)));
         assertTrue("optNumber() should return default Number",
                 42l == jsonObject.optNumber("myKey", Long.valueOf(42)).longValue());
         assertTrue("optString() should return default string",
                 "hi".equals(jsonObject.optString("hiKey", "hi")));
         Util.checkJSONObjectMaps(jsonObject);
    }
    
    /**
     * Verifies that the opt methods properly convert string values.
     */
    @Test
    public void jsonObjectOptStringConversion() {
        JSONObject jo = new JSONObject("{\"int\":\"123\",\"true\":\"true\",\"false\":\"false\"}");
        assertTrue("unexpected optBoolean value",jo.optBoolean("true",false)==true);
        assertTrue("unexpected optBooleanObject value",Boolean.valueOf(true).equals(jo.optBooleanObject("true",false)));
        assertTrue("unexpected optBoolean value",jo.optBoolean("false",true)==false);
        assertTrue("unexpected optBooleanObject value",Boolean.valueOf(false).equals(jo.optBooleanObject("false",true)));
        assertTrue("unexpected optInt value",jo.optInt("int",0)==123);
        assertTrue("unexpected optIntegerObject value",Integer.valueOf(123).equals(jo.optIntegerObject("int",0)));
        assertTrue("unexpected optLong value",jo.optLong("int",0)==123l);
        assertTrue("unexpected optLongObject value",Long.valueOf(123l).equals(jo.optLongObject("int",0L)));
        assertTrue("unexpected optDouble value",jo.optDouble("int",0.0d)==123.0d);
        assertTrue("unexpected optDoubleObject value",Double.valueOf(123.0d).equals(jo.optDoubleObject("int",0.0d)));
        assertTrue("unexpected optFloat value",jo.optFloat("int",0.0f)==123.0f);
        assertTrue("unexpected optFloatObject value",Float.valueOf(123.0f).equals(jo.optFloatObject("int",0.0f)));
        assertTrue("unexpected optBigInteger value",jo.optBigInteger("int",BigInteger.ZERO).compareTo(new BigInteger("123"))==0);
        assertTrue("unexpected optBigDecimal value",jo.optBigDecimal("int",BigDecimal.ZERO).compareTo(new BigDecimal("123"))==0);
        assertTrue("unexpected optBigDecimal value",jo.optBigDecimal("int",BigDecimal.ZERO).compareTo(new BigDecimal("123"))==0);
        assertTrue("unexpected optNumber value",jo.optNumber("int",BigInteger.ZERO).longValue()==123l);
        Util.checkJSONObjectMaps(jo);
    }
    
    /**
     * Verifies that the opt methods properly convert string values to numbers and coerce them consistently.
     */
    @Test
    public void jsonObjectOptCoercion() {
        JSONObject jo = new JSONObject("{\"largeNumberStr\":\"19007199254740993.35481234487103587486413587843213584\"}");
        // currently the parser doesn't recognize BigDecimal, to we have to put it manually
        jo.put("largeNumber", new BigDecimal("19007199254740993.35481234487103587486413587843213584"));
        
        // Test type coercion from larger to smaller
        assertEquals(new BigDecimal("19007199254740993.35481234487103587486413587843213584"), jo.optBigDecimal("largeNumber",null));
        assertEquals(new BigInteger("19007199254740993"), jo.optBigInteger("largeNumber",null));
        assertEquals(1.9007199254740992E16, jo.optDouble("largeNumber"),0.0);
        assertEquals(1.9007199254740992E16, jo.optDoubleObject("largeNumber"),0.0);
        assertEquals(1.90071995E16f, jo.optFloat("largeNumber"),0.0f);
        assertEquals(1.90071995E16f, jo.optFloatObject("largeNumber"),0.0f);
        assertEquals(19007199254740993l, jo.optLong("largeNumber"));
        assertEquals(Long.valueOf(19007199254740993l), jo.optLongObject("largeNumber"));
        assertEquals(1874919425, jo.optInt("largeNumber"));
        assertEquals(Integer.valueOf(1874919425), jo.optIntegerObject("largeNumber"));

        // conversion from a string
        assertEquals(new BigDecimal("19007199254740993.35481234487103587486413587843213584"), jo.optBigDecimal("largeNumberStr",null));
        assertEquals(new BigInteger("19007199254740993"), jo.optBigInteger("largeNumberStr",null));
        assertEquals(1.9007199254740992E16, jo.optDouble("largeNumberStr"),0.0);
        assertEquals(1.9007199254740992E16, jo.optDoubleObject("largeNumberStr"),0.0);
        assertEquals(1.90071995E16f, jo.optFloat("largeNumberStr"),0.0f);
        assertEquals(1.90071995E16f, jo.optFloatObject("largeNumberStr"),0.0f);
        assertEquals(19007199254740993l, jo.optLong("largeNumberStr"));
        assertEquals(Long.valueOf(19007199254740993l), jo.optLongObject("largeNumberStr"));
        assertEquals(1874919425, jo.optInt("largeNumberStr"));
        assertEquals(Integer.valueOf(1874919425), jo.optIntegerObject("largeNumberStr"));

        // the integer portion of the actual value is larger than a double can hold.
        assertNotEquals((long)Double.parseDouble("19007199254740993.35481234487103587486413587843213584"), jo.optLong("largeNumber"));
        assertNotEquals(Long.valueOf((long)Double.parseDouble("19007199254740993.35481234487103587486413587843213584")), jo.optLongObject("largeNumber"));
        assertNotEquals((int)Double.parseDouble("19007199254740993.35481234487103587486413587843213584"), jo.optInt("largeNumber"));
        assertNotEquals(Integer.valueOf((int)Double.parseDouble("19007199254740993.35481234487103587486413587843213584")), jo.optIntegerObject("largeNumber"));
        assertNotEquals((long)Double.parseDouble("19007199254740993.35481234487103587486413587843213584"), jo.optLong("largeNumberStr"));
        assertNotEquals(Long.valueOf((long)Double.parseDouble("19007199254740993.35481234487103587486413587843213584")), jo.optLongObject("largeNumberStr"));
        assertNotEquals((int)Double.parseDouble("19007199254740993.35481234487103587486413587843213584"), jo.optInt("largeNumberStr"));
        assertNotEquals(Integer.valueOf((int)Double.parseDouble("19007199254740993.35481234487103587486413587843213584")), jo.optIntegerObject("largeNumberStr"));
        assertEquals(19007199254740992l, (long)Double.parseDouble("19007199254740993.35481234487103587486413587843213584"));
        assertEquals(2147483647, (int)Double.parseDouble("19007199254740993.35481234487103587486413587843213584"));
        Util.checkJSONObjectMaps(jo);
    }
    
    /**
     * Verifies that the optBigDecimal method properly converts values to BigDecimal and coerce them consistently.
     */
    @Test
    public void jsonObjectOptBigDecimal() {
        JSONObject jo = new JSONObject().put("int", 123).put("long", 654L)
                .put("float", 1.234f).put("double", 2.345d)
                .put("bigInteger", new BigInteger("1234"))
                .put("bigDecimal", new BigDecimal("1234.56789"))
                .put("nullVal", JSONObject.NULL);
        
        assertEquals(new BigDecimal("123"),jo.optBigDecimal("int", null));
        assertEquals(new BigDecimal("654"),jo.optBigDecimal("long", null));
        assertEquals(new BigDecimal(1.234f),jo.optBigDecimal("float", null));
        assertEquals(new BigDecimal(2.345d),jo.optBigDecimal("double", null));
        assertEquals(new BigDecimal("1234"),jo.optBigDecimal("bigInteger", null));
        assertEquals(new BigDecimal("1234.56789"),jo.optBigDecimal("bigDecimal", null));
        assertNull(jo.optBigDecimal("nullVal", null));
        assertEquals(jo.optBigDecimal("float", null),jo.getBigDecimal("float"));
        assertEquals(jo.optBigDecimal("double", null),jo.getBigDecimal("double"));
        Util.checkJSONObjectMaps(jo);
    }
    
    /**
     * Verifies that the optBigDecimal method properly converts values to BigDecimal and coerce them consistently.
     */
    @Test
    public void jsonObjectOptBigInteger() {
        JSONObject jo = new JSONObject().put("int", 123).put("long", 654L)
                .put("float", 1.234f).put("double", 2.345d)
                .put("bigInteger", new BigInteger("1234"))
                .put("bigDecimal", new BigDecimal("1234.56789"))
                .put("nullVal", JSONObject.NULL);
        
        assertEquals(new BigInteger("123"),jo.optBigInteger("int", null));
        assertEquals(new BigInteger("654"),jo.optBigInteger("long", null));
        assertEquals(new BigInteger("1"),jo.optBigInteger("float", null));
        assertEquals(new BigInteger("2"),jo.optBigInteger("double", null));
        assertEquals(new BigInteger("1234"),jo.optBigInteger("bigInteger", null));
        assertEquals(new BigInteger("1234"),jo.optBigInteger("bigDecimal", null));
        assertNull(jo.optBigDecimal("nullVal", null));
        Util.checkJSONObjectMaps(jo);
    }

    /**
     * Confirm behavior when JSONObject put(key, null object) is called
     */
    @Test
    public void jsonObjectputNull() {

        // put null should remove the item.
        String str = "{\"myKey\": \"myval\"}";
        JSONObject jsonObjectRemove = new JSONObject(str);
        jsonObjectRemove.remove("myKey");
        assertTrue("jsonObject should be empty", jsonObjectRemove.isEmpty());

        JSONObject jsonObjectPutNull = new JSONObject(str);
        jsonObjectPutNull.put("myKey", (Object) null);
        assertTrue("jsonObject should be empty", jsonObjectPutNull.isEmpty());
        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                jsonObjectRemove, jsonObjectPutNull
        )));
    }

    /**
     * Exercise JSONObject quote() method
     * This purpose of quote() is to ensure that for strings with embedded
     * quotes, the quotes are properly escaped.
     */
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

    /**
     * Confirm behavior when JSONObject stringToValue() is called for an
     * empty string
     */
    @Test
    public void stringToValue() {
        String str = "";
        String valueStr = (String)(JSONObject.stringToValue(str));
        assertTrue("stringToValue() expected empty String, found "+valueStr,
                "".equals(valueStr));
    }

    /**
     * Confirm behavior when toJSONArray is called with a null value
     */
    @Test
    public void toJSONArray() {
        assertTrue("toJSONArray() with null names should be null",
                null == new JSONObject().toJSONArray(null));
    }

    /**
     * Exercise the JSONObject write() method
     */
    @Test
    public void write() throws IOException {
        String str = "{\"key1\":\"value1\",\"key2\":[1,2,3]}";
        String expectedStr = str;
        JSONObject jsonObject = new JSONObject(str);
        StringWriter stringWriter = new StringWriter();
        try {
            String actualStr = jsonObject.write(stringWriter).toString();
            // key order may change. verify length and individual key content
            assertEquals("length", expectedStr.length(), actualStr.length());
            assertTrue("key1", actualStr.contains("\"key1\":\"value1\""));
            assertTrue("key2", actualStr.contains("\"key2\":[1,2,3]"));
        } finally {
            stringWriter.close();
        }
        Util.checkJSONObjectMaps(jsonObject);
    }
    
    /**
     * Confirms that exceptions thrown when writing values are wrapped properly.
     */
    @Test
    public void testJSONWriterException() {
        final JSONObject jsonObject = new JSONObject();

        jsonObject.put("someKey",new BrokenToString());

        // test single element JSONObject
        StringWriter writer = new StringWriter();
        try {
            jsonObject.write(writer).toString();
            fail("Expected an exception, got a String value");
        } catch (JSONException e) {
            assertEquals("Unable to write JSONObject value for key: someKey", e.getMessage());
        } catch(Exception e) {
            fail("Expected JSONException");
        } finally {
            try {
                writer.close();
            } catch (Exception e) {}
        }

        //test multiElement
        jsonObject.put("somethingElse", "a value");
        
        writer = new StringWriter();
        try {
            jsonObject.write(writer).toString();
            fail("Expected an exception, got a String value");
        } catch (JSONException e) {
            assertEquals("Unable to write JSONObject value for key: someKey", e.getMessage());
        } catch(Exception e) {
            fail("Expected JSONException");
        } finally {
            try {
                writer.close();
            } catch (Exception e) {}
        }
        
        // test a more complex object
        writer = new StringWriter();
        try {
            new JSONObject()
                .put("somethingElse", "a value")
                .put("someKey", new JSONArray()
                        .put(new JSONObject().put("key1", new BrokenToString())))
                .write(writer).toString();
            fail("Expected an exception, got a String value");
        } catch (JSONException e) {
            assertEquals("Unable to write JSONObject value for key: someKey", e.getMessage());
        } catch(Exception e) {
            fail("Expected JSONException");
        } finally {
            try {
                writer.close();
            } catch (Exception e) {}
        }
       
        // test a more slightly complex object
        writer = new StringWriter();
        try {
            new JSONObject()
                .put("somethingElse", "a value")
                .put("someKey", new JSONArray()
                        .put(new JSONObject().put("key1", new BrokenToString()))
                        .put(12345)
                 )
                .write(writer).toString();
            fail("Expected an exception, got a String value");
        } catch (JSONException e) {
            assertEquals("Unable to write JSONObject value for key: someKey", e.getMessage());
        } catch(Exception e) {
            fail("Expected JSONException");
        } finally {
            try {
                writer.close();
            } catch (Exception e) {}
        }
        Util.checkJSONObjectMaps(jsonObject);
    }


    /**
     * Exercise the JSONObject write() method
     */
/*
    @Test
    public void writeAppendable() {
        String str = "{\"key1\":\"value1\",\"key2\":[1,2,3]}";
        String expectedStr = str;
        JSONObject jsonObject = new JSONObject(str);
        StringBuilder stringBuilder = new StringBuilder();
        Appendable appendable = jsonObject.write(stringBuilder);
        String actualStr = appendable.toString();
        assertTrue("write() expected " +expectedStr+
                        " but found " +actualStr,
                expectedStr.equals(actualStr));
    }
*/

    /**
     * Exercise the JSONObject write(Writer, int, int) method
     */
    @Test
    public void write3Param() throws IOException {
        String str0 = "{\"key1\":\"value1\",\"key2\":[1,false,3.14]}";
        String str2 =
                "{\n" +
                "   \"key1\": \"value1\",\n" +
                "   \"key2\": [\n" +
                "     1,\n" +
                "     false,\n" +
                "     3.14\n" +
                "   ]\n" +
                " }";
        JSONObject jsonObject = new JSONObject(str0);
        StringWriter stringWriter = new StringWriter();
        try {
            String actualStr = jsonObject.write(stringWriter,0,0).toString();
            
            assertEquals("length", str0.length(), actualStr.length());
            assertTrue("key1", actualStr.contains("\"key1\":\"value1\""));
            assertTrue("key2", actualStr.contains("\"key2\":[1,false,3.14]"));
        } finally {
            try {
                stringWriter.close();
            } catch (Exception e) {}
        }
        
        stringWriter = new StringWriter();
        try {
            String actualStr = jsonObject.write(stringWriter,2,1).toString();

            assertEquals("length", str2.length(), actualStr.length());
            assertTrue("key1", actualStr.contains("   \"key1\": \"value1\""));
            assertTrue("key2", actualStr.contains("   \"key2\": [\n" +
                            "     1,\n" +
                            "     false,\n" +
                            "     3.14\n" +
                            "   ]")
            );
        } finally {
            try {
                stringWriter.close();
            } catch (Exception e) {}
        }
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * Exercise the JSONObject write(Appendable, int, int) method
     */
/*
    @Test
    public void write3ParamAppendable() {
        String str0 = "{\"key1\":\"value1\",\"key2\":[1,false,3.14]}";
        String str2 =
                "{\n" +
                        "   \"key1\": \"value1\",\n" +
                        "   \"key2\": [\n" +
                        "     1,\n" +
                        "     false,\n" +
                        "     3.14\n" +
                        "   ]\n" +
                        " }";
        JSONObject jsonObject = new JSONObject(str0);
        String expectedStr = str0;
        StringBuilder stringBuilder = new StringBuilder();
        Appendable appendable = jsonObject.write(stringBuilder,0,0);
        String actualStr = appendable.toString();
        assertEquals(expectedStr, actualStr);

        expectedStr = str2;
        stringBuilder = new StringBuilder();
        appendable = jsonObject.write(stringBuilder,2,1);
        actualStr = appendable.toString();
        assertEquals(expectedStr, actualStr);
    }
*/

    /**
     * Exercise the JSONObject equals() method
     */
    @Test
    public void equals() {
        String str = "{\"key\":\"value\"}";
        JSONObject aJsonObject = new JSONObject(str);
        assertTrue("Same JSONObject should be equal to itself",
                aJsonObject.equals(aJsonObject));
        Util.checkJSONObjectMaps(aJsonObject);
    }

    /**
     * JSON null is not the same as Java null. This test examines the differences
     * in how they are handled by JSON-java.
     */
    @Test
    public void jsonObjectNullOperations() {
        /**
         * The Javadoc for JSONObject.NULL states:
         *      "JSONObject.NULL is equivalent to the value that JavaScript calls null,
         *      whilst Java's null is equivalent to the value that JavaScript calls
         *      undefined."
         * 
         * Standard ECMA-262 6th Edition / June 2015 (included to help explain the javadoc):
         *      undefined value: primitive value used when a variable has not been assigned a value
         *      Undefined type:  type whose sole value is the undefined value
         *      null value:      primitive value that represents the intentional absence of any object value
         *      Null type:       type whose sole value is the null value
         * Java SE8 language spec (included to help explain the javadoc):
         *      The Kinds of Types and Values ...
         *      There is also a special null type, the type of the expression null, which has no name.
         *      Because the null type has no name, it is impossible to declare a variable of the null 
         *      type or to cast to the null type. The null reference is the only possible value of an 
         *      expression of null type. The null reference can always be assigned or cast to any reference type.
         *      In practice, the programmer can ignore the null type and just pretend that null is merely 
         *      a special literal that can be of any reference type.
         * Extensible Markup Language (XML) 1.0 Fifth Edition / 26 November 2008
         *      No mention of null
         * ECMA-404 1st Edition / October 2013:
         *      JSON Text  ...
         *      These are three literal name tokens: ...
         *      null 
         * 
         * There seems to be no best practice to follow, it's all about what we
         * want the code to do.
         */

        // add JSONObject.NULL then convert to string in the manner of XML.toString() 
        JSONObject jsonObjectJONull = new JSONObject();
        Object obj = JSONObject.NULL;
        jsonObjectJONull.put("key", obj);
        Object value = jsonObjectJONull.opt("key");
        assertTrue("opt() JSONObject.NULL should find JSONObject.NULL",
                obj.equals(value));
        value = jsonObjectJONull.get("key");
        assertTrue("get() JSONObject.NULL should find JSONObject.NULL",
                obj.equals(value));
        if (value == null) {
            value = "";
        }
        String string = value instanceof String ? (String)value : null;
        assertTrue("XML toString() should convert JSONObject.NULL to null",
                string == null);

        // now try it with null
        JSONObject jsonObjectNull = new JSONObject();
        obj = null;
        jsonObjectNull.put("key", obj);
        value = jsonObjectNull.opt("key");
        assertNull("opt() null should find null", value);
        // what is this trying to do? It appears to test absolutely nothing...
//        if (value == null) {
//            value = "";
//        }
//        string = value instanceof String ? (String)value : null;
//        assertTrue("should convert null to empty string", "".equals(string));
        try {
            value = jsonObjectNull.get("key");
            fail("get() null should throw exception");
        } catch (Exception ignored) {}

        /**
         * XML.toString() then goes on to do something with the value
         * if the key val is "content", then value.toString() will be 
         * called. This will evaluate to "null" for JSONObject.NULL,
         * and the empty string for null.
         * But if the key is anything else, then JSONObject.NULL will be emitted
         * as <key>null</key> and null will be emitted as ""
         */
        String sJONull = XML.toString(jsonObjectJONull);
        assertTrue("JSONObject.NULL should emit a null value", 
                "<key>null</key>".equals(sJONull));
        String sNull = XML.toString(jsonObjectNull);
        assertTrue("null should emit an empty string", "".equals(sNull));
        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                jsonObjectJONull, jsonObjectNull
        )));
    }
    
    @Test(expected = JSONPointerException.class)
    public void queryWithNoResult() {
        new JSONObject().query("/a/b");
    }
    
    @Test
    public void optQueryWithNoResult() {
        assertNull(new JSONObject().optQuery("/a/b"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void optQueryWithSyntaxError() {
        new JSONObject().optQuery("invalid");
    }

    @Test(expected = JSONException.class)
    public void invalidEscapeSequence() {
      String json = "{ \"\\url\": \"value\" }";
      assertNull("Expected an exception",new JSONObject(json));
    }

    /**
     * Exercise JSONObject toMap() method.
     */
    @Test
    public void toMap() {
        String jsonObjectStr =
                "{" +
                "\"key1\":" +
                    "[1,2," +
                        "{\"key3\":true}" +
                    "]," +
                "\"key2\":" +
                    "{\"key1\":\"val1\",\"key2\":" +
                        "{\"key2\":null}," +
                    "\"key3\":42" +
                    "}," +
                "\"key3\":" +
                    "[" +
                        "[\"value1\",2.1]" +
                    "," +
                        "[null]" +
                    "]" +
                "}";

        JSONObject jsonObject = new JSONObject(jsonObjectStr);
        Map<?,?> map = jsonObject.toMap();

        assertTrue("Map should not be null", map != null);
        assertTrue("Map should have 3 elements", map.size() == 3);

        List<?> key1List = (List<?>)map.get("key1");
        assertTrue("key1 should not be null", key1List != null);
        assertTrue("key1 list should have 3 elements", key1List.size() == 3);
        assertTrue("key1 value 1 should be 1", key1List.get(0).equals(Integer.valueOf(1)));
        assertTrue("key1 value 2 should be 2", key1List.get(1).equals(Integer.valueOf(2)));

        Map<?,?> key1Value3Map = (Map<?,?>)key1List.get(2);
        assertTrue("Map should not be null", key1Value3Map != null);
        assertTrue("Map should have 1 element", key1Value3Map.size() == 1);
        assertTrue("Map key3 should be true", key1Value3Map.get("key3").equals(Boolean.TRUE));

        Map<?,?> key2Map = (Map<?,?>)map.get("key2");
        assertTrue("key2 should not be null", key2Map != null);
        assertTrue("key2 map should have 3 elements", key2Map.size() == 3);
        assertTrue("key2 map key 1 should be val1", key2Map.get("key1").equals("val1"));
        assertTrue("key2 map key 3 should be 42", key2Map.get("key3").equals(Integer.valueOf(42)));

        Map<?,?> key2Val2Map = (Map<?,?>)key2Map.get("key2");
        assertTrue("key2 map key 2 should not be null", key2Val2Map != null);
        assertTrue("key2 map key 2 should have an entry", key2Val2Map.containsKey("key2"));
        assertTrue("key2 map key 2 value should be null", key2Val2Map.get("key2") == null);

        List<?> key3List = (List<?>)map.get("key3");
        assertTrue("key3 should not be null", key3List != null);
        assertTrue("key3 list should have 3 elements", key3List.size() == 2);

        List<?> key3Val1List = (List<?>)key3List.get(0);
        assertTrue("key3 list val 1 should not be null", key3Val1List != null);
        assertTrue("key3 list val 1 should have 2 elements", key3Val1List.size() == 2);
        assertTrue("key3 list val 1 list element 1 should be value1", key3Val1List.get(0).equals("value1"));
        assertTrue("key3 list val 1 list element 2 should be 2.1", key3Val1List.get(1).equals(new BigDecimal("2.1")));

        List<?> key3Val2List = (List<?>)key3List.get(1);
        assertTrue("key3 list val 2 should not be null", key3Val2List != null);
        assertTrue("key3 list val 2 should have 1 element", key3Val2List.size() == 1);
        assertTrue("key3 list val 2 list element 1 should be null", key3Val2List.get(0) == null);

        // Assert that toMap() is a deep copy
        jsonObject.getJSONArray("key3").getJSONArray(0).put(0, "still value 1");
        assertTrue("key3 list val 1 list element 1 should be value1", key3Val1List.get(0).equals("value1"));

        // assert that the new map is mutable
        assertTrue("Removing a key should succeed", map.remove("key3") != null);
        assertTrue("Map should have 2 elements", map.size() == 2);
        Util.checkJSONObjectMaps(jsonObject);
    }
    
    /**
     * test that validates a singleton can be serialized as a bean.
     */
    @SuppressWarnings("boxing")
    @Test
    public void testSingletonBean() {
        final JSONObject jo = new JSONObject(Singleton.getInstance());
        assertEquals(jo.keySet().toString(), 1, jo.length());
        assertEquals(0, jo.get("someInt"));
        assertEquals(null, jo.opt("someString"));
        
        // Update the singleton values
        Singleton.getInstance().setSomeInt(42);
        Singleton.getInstance().setSomeString("Something");
        final JSONObject jo2 = new JSONObject(Singleton.getInstance());
        assertEquals(2, jo2.length());
        assertEquals(42, jo2.get("someInt"));
        assertEquals("Something", jo2.get("someString"));

        // ensure our original jo hasn't changed.
        assertEquals(0, jo.get("someInt"));
        assertEquals(null, jo.opt("someString"));
        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                jo, jo2
        )));
    }

    /**
     * test that validates a singleton can be serialized as a bean.
     */
    @SuppressWarnings("boxing")
    @Test
    public void testSingletonEnumBean() {
        final JSONObject jo = new JSONObject(SingletonEnum.getInstance());
        assertEquals(jo.keySet().toString(), 1, jo.length());
        assertEquals(0, jo.get("someInt"));
        assertEquals(null, jo.opt("someString"));
        
        // Update the singleton values
        SingletonEnum.getInstance().setSomeInt(42);
        SingletonEnum.getInstance().setSomeString("Something");
        final JSONObject jo2 = new JSONObject(SingletonEnum.getInstance());
        assertEquals(2, jo2.length());
        assertEquals(42, jo2.get("someInt"));
        assertEquals("Something", jo2.get("someString"));

        // ensure our original jo hasn't changed.
        assertEquals(0, jo.get("someInt"));
        assertEquals(null, jo.opt("someString"));
        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                jo, jo2
        )));
    }
    
    /**
     * Test to validate that a generic class can be serialized as a bean.
     */
    @SuppressWarnings("boxing")
    @Test
    public void testGenericBean() {
        GenericBean<Integer> bean = new GenericBean<>(42);
        final JSONObject jo = new JSONObject(bean);
        assertEquals(jo.keySet().toString(), 8, jo.length());
        assertEquals(42, jo.get("genericValue"));
        assertEquals("Expected the getter to only be called once",
                1, bean.genericGetCounter);
        assertEquals(0, bean.genericSetCounter);
        Util.checkJSONObjectMaps(jo);
    }
    
    /**
     * Test to validate that a generic class can be serialized as a bean.
     */
    @SuppressWarnings("boxing")
    @Test
    public void testGenericIntBean() {
        GenericBeanInt bean = new GenericBeanInt(42);
        final JSONObject jo = new JSONObject(bean);
        assertEquals(jo.keySet().toString(), 10, jo.length());
        assertEquals(42, jo.get("genericValue"));
        assertEquals("Expected the getter to only be called once",
                1, bean.genericGetCounter);
        assertEquals(0, bean.genericSetCounter);
        Util.checkJSONObjectMaps(jo);
    }
    
    /**
     * Test to verify <code>key</code> limitations in the JSONObject bean serializer.
     */
    @Test
    public void testWierdListBean() {
        @SuppressWarnings("boxing")
        WeirdList bean = new WeirdList(42, 43, 44);
        final JSONObject jo = new JSONObject(bean);
        // get() should have a key of 0 length
        // get(int) should be ignored base on parameter count
        // getInt(int) should also be ignored based on parameter count
        // add(Integer) should be ignore as it doesn't start with get/is and also has a parameter
        // getALL should be mapped
        assertEquals("Expected 1 key to be mapped. Instead found: "+jo.keySet().toString(),
                1, jo.length());
        assertNotNull(jo.get("ALL"));
        Util.checkJSONObjectMaps(jo);
    }
    
    /**
     * Sample test case from https://github.com/stleary/JSON-java/issues/531
     * which verifies that no regression in double/BigDecimal support is present.
     */
    @Test
    public void testObjectToBigDecimal() {  
        double value = 1412078745.01074;  
        Reader reader = new StringReader("[{\"value\": " + value + "}]");
        JSONTokener tokener = new JSONTokener(reader);
        JSONArray array = new JSONArray(tokener);
        JSONObject jsonObject = array.getJSONObject(0);

        BigDecimal current = jsonObject.getBigDecimal("value");
        BigDecimal wantedValue = BigDecimal.valueOf(value);

        assertEquals(current, wantedValue);
        Util.checkJSONObjectMaps(jsonObject);
        Util.checkJSONArrayMaps(array, jsonObject.getMapType());
     }
    
    /**
     * Tests the exception portions of populateMap.
     */
    @Test
    public void testExceptionalBean() {
        ExceptionalBean bean = new ExceptionalBean();
        final JSONObject jo = new JSONObject(bean);
        assertEquals("Expected 1 key to be mapped. Instead found: "+jo.keySet().toString(),
                1, jo.length());
        assertTrue(jo.get("closeable") instanceof JSONObject);
        assertTrue(jo.getJSONObject("closeable").has("string"));
        Util.checkJSONObjectMaps(jo);
    }
    
    @Test(expected=NullPointerException.class)
    public void testPutNullBoolean() {
        // null put key 
        JSONObject jsonObject = new JSONObject("{}");
        jsonObject.put(null, false);
        fail("Expected an exception");
    }
    @Test(expected=NullPointerException.class)
    public void testPutNullCollection() {
        // null put key 
        JSONObject jsonObject = new JSONObject("{}");
        jsonObject.put(null, Collections.emptySet());
        fail("Expected an exception");
    }
    @Test(expected=NullPointerException.class)
    public void testPutNullDouble() {
        // null put key 
        JSONObject jsonObject = new JSONObject("{}");
        jsonObject.put(null, 0.0d);
        fail("Expected an exception");
    }
    @Test(expected=NullPointerException.class)
    public void testPutNullFloat() {
        // null put key 
        JSONObject jsonObject = new JSONObject("{}");
        jsonObject.put(null, 0.0f);
        fail("Expected an exception");
    }
    @Test(expected=NullPointerException.class)
    public void testPutNullInt() {
        // null put key 
        JSONObject jsonObject = new JSONObject("{}");
        jsonObject.put(null, 0);
        fail("Expected an exception");
    }
    @Test(expected=NullPointerException.class)
    public void testPutNullLong() {
        // null put key 
        JSONObject jsonObject = new JSONObject("{}");
        jsonObject.put(null, 0L);
        fail("Expected an exception");
    }
    @Test(expected=NullPointerException.class)
    public void testPutNullMap() {
        // null put key 
        JSONObject jsonObject = new JSONObject("{}");
        jsonObject.put(null, Collections.emptyMap());
        fail("Expected an exception");
    }
    @Test(expected=NullPointerException.class)
    public void testPutNullObject() {
        // null put key 
        JSONObject jsonObject = new JSONObject("{}");
        jsonObject.put(null, new Object());
        fail("Expected an exception");
    }
    @Test(expected=JSONException.class)
    public void testSelfRecursiveObject() {
        // A -> A ...
        RecursiveBean ObjA = new RecursiveBean("ObjA");
        ObjA.setRef(ObjA);
        new JSONObject(ObjA);
        fail("Expected an exception");
    }
    @Test(expected=JSONException.class)
    public void testLongSelfRecursiveObject() {
        // B -> A -> A ...
        RecursiveBean ObjA = new RecursiveBean("ObjA");
        RecursiveBean ObjB = new RecursiveBean("ObjB");
        ObjB.setRef(ObjA);
        ObjA.setRef(ObjA);
        new JSONObject(ObjB);
        fail("Expected an exception");
    }
    @Test(expected=JSONException.class)
    public void testSimpleRecursiveObject() {
        // B -> A -> B ...
        RecursiveBean ObjA = new RecursiveBean("ObjA");
        RecursiveBean ObjB = new RecursiveBean("ObjB");
        ObjB.setRef(ObjA);
        ObjA.setRef(ObjB);
        new JSONObject(ObjA);
        fail("Expected an exception");
    }
    @Test(expected=JSONException.class)
    public void testLongRecursiveObject() {
        // D -> C -> B -> A -> D ...
        RecursiveBean ObjA = new RecursiveBean("ObjA");
        RecursiveBean ObjB = new RecursiveBean("ObjB");
        RecursiveBean ObjC = new RecursiveBean("ObjC");
        RecursiveBean ObjD = new RecursiveBean("ObjD");
        ObjC.setRef(ObjB);
        ObjB.setRef(ObjA);
        ObjD.setRef(ObjC);
        ObjA.setRef(ObjD);
        new JSONObject(ObjB);
        fail("Expected an exception");
    }
    @Test(expected=JSONException.class)
    public void testRepeatObjectRecursive() {
        // C -> B -> A -> D -> C ...
        //        -> D -> C ...
        RecursiveBean ObjA = new RecursiveBean("ObjA");
        RecursiveBean ObjB = new RecursiveBean("ObjB");
        RecursiveBean ObjC = new RecursiveBean("ObjC");
        RecursiveBean ObjD = new RecursiveBean("ObjD");
        ObjC.setRef(ObjB);
        ObjB.setRef(ObjA);
        ObjB.setRef2(ObjD);
        ObjA.setRef(ObjD);
        ObjD.setRef(ObjC);
        new JSONObject(ObjC);
        fail("Expected an exception");
    }
    @Test
    public void testRepeatObjectNotRecursive() {
        // C -> B -> A
        //        -> A
        RecursiveBean ObjA = new RecursiveBean("ObjA");
        RecursiveBean ObjB = new RecursiveBean("ObjB");
        RecursiveBean ObjC = new RecursiveBean("ObjC");
        ObjC.setRef(ObjA);
        ObjB.setRef(ObjA);
        ObjB.setRef2(ObjA);
        JSONObject j0 = new JSONObject(ObjC);
        JSONObject j1 = new JSONObject(ObjB);
        JSONObject j2 = new JSONObject(ObjA);
        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                j0, j1, j2
        )));
    }
    @Test
    public void testLongRepeatObjectNotRecursive() {
        // C -> B -> A -> D -> E
        //        -> D -> E
        RecursiveBean ObjA = new RecursiveBean("ObjA");
        RecursiveBean ObjB = new RecursiveBean("ObjB");
        RecursiveBean ObjC = new RecursiveBean("ObjC");
        RecursiveBean ObjD = new RecursiveBean("ObjD");
        RecursiveBean ObjE = new RecursiveBean("ObjE");
        ObjC.setRef(ObjB);
        ObjB.setRef(ObjA);
        ObjB.setRef2(ObjD);
        ObjA.setRef(ObjD);
        ObjD.setRef(ObjE);
        JSONObject j0 = new JSONObject(ObjC);
        JSONObject j1 = new JSONObject(ObjB);
        JSONObject j2 = new JSONObject(ObjA);
        JSONObject j3 = new JSONObject(ObjD);
        JSONObject j4 = new JSONObject(ObjE);
        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                j0, j1, j2, j3, j4
        )));
    }
    @Test(expected=JSONException.class)
    public void testRecursiveEquals() {
        RecursiveBeanEquals a = new RecursiveBeanEquals("same");
        a.setRef(a);
        JSONObject j0 = new JSONObject(a);
        Util.checkJSONObjectMaps(j0);
    }
    @Test
    public void testNotRecursiveEquals() {
        RecursiveBeanEquals a = new RecursiveBeanEquals("same");
        RecursiveBeanEquals b = new RecursiveBeanEquals("same");
        RecursiveBeanEquals c = new RecursiveBeanEquals("same");
        a.setRef(b);
        b.setRef(c);
        JSONObject j0 = new JSONObject(a);
        Util.checkJSONObjectMaps(j0);
    }


    @Test
    public void testIssue548ObjectWithEmptyJsonArray() {
        JSONObject jsonObject = new JSONObject("{\"empty_json_array\": []}");
        assertTrue("missing expected key 'empty_json_array'", jsonObject.has("empty_json_array"));
        assertNotNull("'empty_json_array' should be an array", jsonObject.getJSONArray("empty_json_array"));
        assertEquals("'empty_json_array' should have a length of 0", 0, jsonObject.getJSONArray("empty_json_array").length());
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
    * Tests if calling JSONObject clear() method actually makes the JSONObject empty
    */
    @Test(expected = JSONException.class)
    public void jsonObjectClearMethodTest() {
        //Adds random stuff to the JSONObject
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key1", 123);
        jsonObject.put("key2", "456");
        jsonObject.put("key3", new JSONObject());
        jsonObject.clear(); //Clears the JSONObject
        assertTrue("expected jsonObject.length() == 0", jsonObject.length() == 0); //Check if its length is 0
        jsonObject.getInt("key1"); //Should throws org.json.JSONException: JSONObject["asd"] not found
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
    * Tests for stack overflow. See https://github.com/stleary/JSON-java/issues/654
    */
    @Test(expected = JSONException.class)
    public void issue654StackOverflowInput() {
        //String base64Bytes ="eyJHWiI6Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMC0wLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7ewl7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMCkwLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMC0wLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7CXt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7ewl7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMCkwLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMC0wLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7CXt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMC0wLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMC0wLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3sJe3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMC0wLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTApMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7ewl7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMC0wLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3sJe3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMC0wLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTApMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMCkwLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMC0wLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7CXt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7ewl7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMCkwLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMC0wLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7CXt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMC0wLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMC0wLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3sJe3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMC0wLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTApMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7ewl7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMC0wLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3sJe3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMC0wLHt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTApMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7ewl7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7c3t7e3t7e3vPAAAAAAAAAHt7e3t7e3t7e3t7e3t7e3t7e3t7e1ste3t7e3t7e3t7e3t7e3t7e3t7e3t7CXt7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3tbLTAtMCx7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e1stMC0wLHt7e3t7e3t7e3t7e3t7e3t7e88AAAAAAAAAe3t7e3t7e3t7e3t7e3t7e3t7e3t7Wy0wLTAse3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7e3t7f3syMv//e3t7e3t7e3t7e3t7e3sx//////8=";
        //String input = new String(java.util.Base64.getDecoder().decode(base64Bytes));
        String input = "{\"GZ\":[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{  {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0)0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{   {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{    {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0)0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{   {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{  {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0)0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{   {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{    {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0)0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0)0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{   {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{    {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0)0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{   {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{  {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0)0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{   {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{    {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0)0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{   {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{{{{{{{{{{{{{{{{{{{{[-0-0,{{{{{{{{{{s{{{{{{{";
        JSONObject json_input = new JSONObject(input);
        assertNotNull(json_input);
        fail("Excepected Exception.");
        Util.checkJSONObjectMaps(json_input);
    }

    /**
    * Tests for incorrect object/array nesting. See https://github.com/stleary/JSON-java/issues/654
    */
    @Test(expected = JSONException.class)
    public void issue654IncorrectNestingNoKey1() {
        JSONObject json_input = new JSONObject("{{\"a\":0}}");
        assertNotNull(json_input);
        fail("Expected Exception.");
    }

    /**
    * Tests for incorrect object/array nesting. See https://github.com/stleary/JSON-java/issues/654
    */
    @Test(expected = JSONException.class)
    public void issue654IncorrectNestingNoKey2() {
        JSONObject json_input = new JSONObject("{[\"a\"]}");
        assertNotNull(json_input);
        fail("Excepected Exception.");
    }
    
    /**
    * Tests for stack overflow. See https://github.com/stleary/JSON-java/issues/654
    */
    @Ignore("This test relies on system constraints and may not always pass. See: https://github.com/stleary/JSON-java/issues/821")
    @Test(expected = JSONException.class)
    public void issue654StackOverflowInputWellFormed() {
        //String input = new String(java.util.Base64.getDecoder().decode(base64Bytes));
        final InputStream resourceAsStream = JSONObjectTest.class.getClassLoader().getResourceAsStream("Issue654WellFormedObject.json");
        JSONTokener tokener = new JSONTokener(resourceAsStream);
        JSONObject json_input = new JSONObject(tokener);
        assertNotNull(json_input);
        fail("Excepected Exception due to stack overflow.");
    }

    @Test
    public void testIssue682SimilarityOfJSONString() {
        JSONObject jo1 = new JSONObject()
                .put("a", new MyJsonString())
                .put("b", 2);
        JSONObject jo2 = new JSONObject()
                .put("a", new MyJsonString())
                .put("b", 2);
        assertTrue(jo1.similar(jo2));

        JSONObject jo3 = new JSONObject()
                .put("a", new JSONString() {
                    @Override
                    public String toJSONString() {
                        return "\"different value\"";
                    }
                })
                .put("b", 2);
        assertFalse(jo1.similar(jo3));
    }

    private static final Number[] NON_FINITE_NUMBERS = { Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN,
            Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NaN };

    @Test
    public void issue713MapConstructorWithNonFiniteNumbers() {
        for (Number nonFinite : NON_FINITE_NUMBERS) {
            Map<String, Number> map = new HashMap<>();
            map.put("a", nonFinite);

            assertThrows(JSONException.class, () -> new JSONObject(map));
        }
    }

    @Test
    public void issue713BeanConstructorWithNonFiniteNumbers() {
        for (Number nonFinite : NON_FINITE_NUMBERS) {
            GenericBean<Number> bean = new GenericBean<>(nonFinite);
            assertThrows(JSONException.class, () -> new JSONObject(bean));
        }
    }

    @Test(expected = JSONException.class)
    public void issue743SerializationMap() {
      HashMap<String, Object> map = new HashMap<>();
      map.put("t", map);
      JSONObject object = new JSONObject(map);
      String jsonString = object.toString();
    }

    @Test(expected = JSONException.class)
    public void testCircularReferenceMultipleLevel() {
      HashMap<String, Object> inside = new HashMap<>();
      HashMap<String, Object> jsonObject = new HashMap<>();
      inside.put("inside", jsonObject);
      jsonObject.put("test", inside);
      new JSONObject(jsonObject);
    }

    @Test
    public void issue743SerializationMapWith512Objects() {
        HashMap<String, Object> map  = buildNestedMap(ParserConfiguration.DEFAULT_MAXIMUM_NESTING_DEPTH);
        JSONObject object = new JSONObject(map);
        String jsonString = object.toString();
    }

    @Test
    public void issue743SerializationMapWith1000Objects() {
      HashMap<String, Object> map  = buildNestedMap(1000);
      JSONParserConfiguration parserConfiguration = new JSONParserConfiguration().withMaxNestingDepth(1000);
      JSONObject object = new JSONObject(map, parserConfiguration);
      String jsonString = object.toString();
    }

    @Test(expected = JSONException.class)
    public void issue743SerializationMapWith1001Objects() {
        HashMap<String, Object> map  = buildNestedMap(1001);
        JSONObject object = new JSONObject(map);
        String jsonString = object.toString();
    }

    @Test(expected = JSONException.class)
    public void testCircleReferenceFirstLevel() {
        Map<Object, Object> jsonObject = new HashMap<>();

        jsonObject.put("test", jsonObject);

        new JSONObject(jsonObject, new JSONParserConfiguration());
    }

    @Test(expected = StackOverflowError.class)
    public void testCircleReferenceMultiplyLevel_notConfigured_expectedStackOverflow() {
        Map<Object, Object> inside = new HashMap<>();

        Map<Object, Object> jsonObject = new HashMap<>();
        inside.put("test", jsonObject);
        jsonObject.put("test", inside);

        new JSONObject(jsonObject, new JSONParserConfiguration().withMaxNestingDepth(99999));
    }

    @Test(expected = JSONException.class)
    public void testCircleReferenceMultiplyLevel_configured_expectedJSONException() {
        Map<Object, Object> inside = new HashMap<>();

        Map<Object, Object> jsonObject = new HashMap<>();
        inside.put("test", jsonObject);
        jsonObject.put("test", inside);

        new JSONObject(jsonObject, new JSONParserConfiguration());
    }

    @Test
    public void testDifferentKeySameInstanceNotACircleReference() {
        Map<Object, Object> map1 = new HashMap<>();
        Map<Object, Object> map2 = new HashMap<>();

        map1.put("test1", map2);
        map1.put("test2", map2);

        new JSONObject(map1);
    }

    @Test
    public void clarifyCurrentBehavior() {
        // Behavior documented in #653 optLong vs getLong inconsistencies
        // This problem still exists.
        // Internally, both number_1 and number_2 are stored as strings. This is reasonable since they are parsed as strings.
        // However, getLong and optLong should return similar results
        JSONObject json = new JSONObject("{\"number_1\":\"01234\", \"number_2\": \"332211\"}");
        assertEquals(json.getLong("number_1"), 1234L);
        assertEquals(json.optLong("number_1"), 0); //THIS VALUE IS NOT RETURNED AS A NUMBER
        assertEquals(json.getLong("number_2"), 332211L);
        assertEquals(json.optLong("number_2"), 332211L);

        // Behavior documented in #826 JSONObject parsing 0-led numeric strings as ints
        // After reverting the code, personId is stored as a string, and the behavior is as expected
        String personId = "\"0123\"";
        JSONObject j1 = new JSONObject("{\"personId\": " + personId + "}");
        assertEquals(j1.getString("personId"), "0123");

        // Also #826. Here is input with missing quotes. Because of the leading zero, it should not be parsed as a number.
        // This example was mentioned in the same ticket
        // After reverting the code, personId is stored as a string, and the behavior is as expected
        JSONObject j2 = new JSONObject("{\"personId\":\"0123\"}");
        assertEquals(j2.getString("personId"), "0123");

        // Behavior uncovered while working on the code
        // All of the values are stored as strings except for hex4, which is stored as a number. This is probably incorrect
        JSONObject j3 = new JSONObject("{ " +
                "\"hex1\": \"010e4\", \"hex2\": \"00f0\", \"hex3\": \"0011\", " +
                "\"hex4\": 00e0, \"hex5\": \"00f0\", \"hex6\": \"0011\" }");
        assertEquals(j3.getString("hex1"), "010e4");
        assertEquals(j3.getString("hex2"), "00f0");
        assertEquals(j3.getString("hex3"), "0011");
        assertEquals(j3.getLong("hex4"), 0, .1);
        assertEquals(j3.getString("hex5"), "00f0");
        assertEquals(j3.getString("hex6"), "0011");
    }


    @Test
    public void testStrictModeJSONTokener_expectException(){
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration().withStrictMode();
        JSONTokener tokener = new JSONTokener("{\"key\":\"value\"}invalidCharacters", jsonParserConfiguration);

        assertThrows(JSONException.class, () -> { new JSONObject(tokener); });
    }

    /**
     * Method to build nested map of max maxDepth
     *
     * @param maxDepth
     * @return
     */
    public static HashMap<String, Object> buildNestedMap(int maxDepth) {
        if (maxDepth <= 0) {
            return new HashMap<>();
        }
        HashMap<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("t", buildNestedMap(maxDepth - 1));
        return nestedMap;
    }
    

    /**
     * Tests the behavior of the {@link JSONObject} when parsing a bean with null fields
     * using a custom {@link JSONParserConfiguration} that enables the use of native nulls.
     * 
     * <p>This test ensures that uninitialized fields in the bean are serialized correctly
     * into the resulting JSON object, and their keys are present in the JSON string output.</p>
     */
    @Test
    public void jsonObjectParseNullFieldsWithParserConfiguration() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration();
        RecursiveBean bean = new RecursiveBean(null);
        JSONObject jsonObject = new JSONObject(bean, jsonParserConfiguration.withUseNativeNulls(true));
        assertTrue("name key should be present", jsonObject.has("name"));
        assertTrue("ref key should be present", jsonObject.has("ref"));
        assertTrue("ref2 key should be present", jsonObject.has("ref2"));
    }

    /**
     * Tests the behavior of the {@link JSONObject} when parsing a bean with null fields
     * without using a custom {@link JSONParserConfiguration}.
     * 
     * <p>This test ensures that uninitialized fields in the bean are not serialized
     * into the resulting JSON object, and the object remains empty.</p>
     */
    @Test
    public void jsonObjectParseNullFieldsWithoutParserConfiguration() {
        RecursiveBean bean = new RecursiveBean(null);
        JSONObject jsonObject = new JSONObject(bean);
        assertTrue("JSONObject should be empty", jsonObject.isEmpty());
    }

}
