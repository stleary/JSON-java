/*
 * File: TestJSONObject.java Author: JSON.org
 */
package org.json.tests;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.XML;

import junit.framework.TestCase;

/**
 * The Class TestJSONObject.
 */
public class TestJSONObject extends TestCase
{

    /**
     * The Class GoodJsonString.
     */
    public class GoodJsonString implements JSONString
    {

        /*
         * (non-Javadoc)
         * 
         * @see org.json.JSONString#toJSONString()
         */
        @Override
        public String toJSONString()
        {
            return "jsonstring";
        }

    }

    /**
     * The Class NullJsonString.
     */
    public class NullJsonString implements JSONString
    {

        /*
         * (non-Javadoc)
         * 
         * @see org.json.JSONString#toJSONString()
         */
        @Override
        public String toJSONString()
        {
            return null;
        }

    }

    /**
     * The Class BadJsonString.
     */
    public class BadJsonString implements JSONString
    {

        /*
         * (non-Javadoc)
         * 
         * @see org.json.JSONString#toJSONString()
         */
        @Override
        public String toJSONString()
        {
            String[] arString = new String[]
            {
                "abc"
            };
            return arString[1];
        }

    }

    /**
     * The Class ObjectWithPrimatives.
     */
    public class ObjectWithPrimatives
    {

        /** The i. */
        public int i;

        /** The null string. */
        private String nullString;

        /** The j. */
        private String j;

        /** The k. */
        private double k;

        /** The l. */
        private long l;

        /** The m. */
        public boolean m;

        /**
         * Instantiates a new object with primatives.
         */
        public ObjectWithPrimatives()
        {
            i = 3;
            j = "3";
            k = 10.03;
            l = 5748548957230984584L;
            m = true;
            nullString = null;
        }

        /**
         * Gets the i.
         * 
         * @return the i
         */
        public int getI()
        {
            return i;
        }

        /**
         * Gets the j.
         * 
         * @return the j
         */
        public String getJ()
        {
            return j;
        }

        /**
         * Gets the k.
         * 
         * @return the k
         */
        public double getK()
        {
            return k;
        }

        /**
         * Gets the l.
         * 
         * @return the l
         */
        public long getL()
        {
            return l;
        }

        /**
         * Gets the m.
         * 
         * @return the m
         */
        public boolean getM()
        {
            return m;
        }

        /**
         * Gets the m.
         * 
         * @param test
         *            the test
         * @return the m
         */
        public boolean getM(Boolean test)
        {
            return m;
        }

        /**
         * Gets the null string.
         * 
         * @return the null string
         */
        public String getNullString()
        {
            return nullString;
        }

        /**
         * Gets the zERO.
         * 
         * @return the zERO
         */
        public int getZERO()
        {
            return 0;
        }

        /**
         * Gets the one.
         * 
         * @return the one
         */
        public int getone()
        {
            return 1;
        }

        /**
         * Checks if is big.
         * 
         * @return true, if is big
         */
        public boolean isBig()
        {
            return false;
        }

        /**
         * Checks if is small.
         * 
         * @return true, if is small
         */
        @SuppressWarnings("unused")
        private boolean isSmall()
        {
            return true;
        }

    }

    /**
     * The Class ObjectWithPrimativesExtension.
     */
    public class ObjectWithPrimativesExtension extends ObjectWithPrimatives
    {
        // Same Object
    }

    /** The jsonobject. */
    JSONObject jsonobject = new JSONObject();

    /** The iterator. */
    Iterator<String> iterator;

    /** The jsonarray. */
    JSONArray jsonarray;

    /** The object. */
    Object object;

    /** The string. */
    String string;

    /** The eps. */
    double eps = 2.220446049250313e-16;

    /**
     * Tests the null method.
     * 
     * @throws Exception
     *             the exception
     */
    public void testNull() throws Exception
    {
        jsonobject = new JSONObject("{\"message\":\"null\"}");
        assertFalse(jsonobject.isNull("message"));
        assertEquals("null", jsonobject.getString("message"));

        jsonobject = new JSONObject("{\"message\":null}");
        assertTrue(jsonobject.isNull("message"));
    }

    /**
     * Tests the constructor method using duplicate key.
     */
    public void testConstructor_DuplicateKey()
    {
        try
        {
            string = "{\"koda\": true, \"koda\": true}";
            jsonobject = new JSONObject(string);
            fail("expecting JSONException here.");
        } catch (JSONException jsone)
        {
            assertEquals("Duplicate key \"koda\"", jsone.getMessage());
        }
    }

    /**
     * Tests the constructor method using null key.
     */
    public void testConstructor_NullKey()
    {
        try
        {
            jsonobject.put(null, "howard");
            fail("expecting JSONException here.");
        } catch (JSONException jsone)
        {
            assertEquals("Null key.", jsone.getMessage());
        }
    }

    /**
     * Tests the getDouble method using invalid key howard.
     */
    public void testGetDouble_InvalidKeyHoward()
    {
        try
        {
            jsonobject.getDouble("howard");
            fail("expecting JSONException here.");
        } catch (JSONException jsone)
        {
            assertEquals("JSONObject[\"howard\"] not found.",
                    jsone.getMessage());
        }
    }

    /**
     * Tests the getDouble method using invalid key stooge.
     */
    public void testGetDouble_InvalidKeyStooge()
    {
        jsonobject = new JSONObject();
        try
        {
            jsonobject.getDouble("stooge");
            fail("expecting JSONException here.");
        } catch (JSONException jsone)
        {
            assertEquals("JSONObject[\"stooge\"] not found.",
                    jsone.getMessage());
        }
    }

    /**
     * Tests the isNull method.
     */
    public void testIsNull()
    {
        try
        {
            jsonobject = new JSONObject();
            object = null;
            jsonobject.put("booga", object);
            jsonobject.put("wooga", JSONObject.NULL);
            assertEquals("{\"wooga\":null}", jsonobject.toString());
            assertTrue(jsonobject.isNull("booga"));
        } catch (JSONException e)
        {
            fail(e.toString());
        }
    }

    /**
     * Tests the increment method.
     */
    public void testIncrement_NewKey()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.increment("two");
            jsonobject.increment("two");
            assertEquals("{\"two\":2}", jsonobject.toString());
            assertEquals(2, jsonobject.getInt("two"));
        } catch (JSONException e)
        {
            fail(e.toString());
        }
    }

    /**
     * Tests the toString method using listof lists.
     */
    public void testToString_ListofLists()
    {
        try
        {
            string = "{     \"list of lists\" : [         [1, 2, 3],         [4, 5, 6],     ] }";
            jsonobject = new JSONObject(string);
            assertEquals("{\"list of lists\": [\n" + "    [\n" + "        1,\n"
                    + "        2,\n" + "        3\n" + "    ],\n" + "    [\n"
                    + "        4,\n" + "        5,\n" + "        6\n"
                    + "    ]\n" + "]}", jsonobject.toString(4));
        } catch (JSONException e)
        {
            fail(e.toString());
        }
    }

    /**
     * Tests the toString method using indentation.
     */
    public void testToString_Indentation()
    {
        try
        {
            string = "{ \"entity\": { \"imageURL\": \"\", \"name\": \"IXXXXXXXXXXXXX\", \"id\": 12336, \"ratingCount\": null, \"averageRating\": null } }";
            jsonobject = new JSONObject(string);
            assertEquals(
                    "{\"entity\": {\n  \"id\": 12336,\n  \"averageRating\": null,\n  \"ratingCount\": null,\n  \"name\": \"IXXXXXXXXXXXXX\",\n  \"imageURL\": \"\"\n}}",
                    jsonobject.toString(2));
        } catch (JSONException e)
        {
            fail(e.toString());
        }
    }

    /**
     * Tests the multipleThings method.
     */
    public void testMultipleThings()
    {
        try
        {
            jsonobject = new JSONObject(
                    "{foo: [true, false,9876543210,    0.0, 1.00000001,  1.000000000001, 1.00000000000000001,"
                            + " .00000000000000001, 2.00, 0.1, 2e100, -32,[],{}, \"string\"], "
                            + "  to   : null, op : 'Good',"
                            + "ten:10} postfix comment");
            jsonobject.put("String", "98.6");
            jsonobject.put("JSONObject", new JSONObject());
            jsonobject.put("JSONArray", new JSONArray());
            jsonobject.put("int", 57);
            jsonobject.put("double", 123456789012345678901234567890.);
            jsonobject.put("true", true);
            jsonobject.put("false", false);
            jsonobject.put("null", JSONObject.NULL);
            jsonobject.put("bool", "true");
            jsonobject.put("zero", -0.0);
            jsonobject.put("\\u2028", "\u2028");
            jsonobject.put("\\u2029", "\u2029");
            jsonarray = jsonobject.getJSONArray("foo");
            jsonarray.put(666);
            jsonarray.put(2001.99);
            jsonarray.put("so \"fine\".");
            jsonarray.put("so <fine>.");
            jsonarray.put(true);
            jsonarray.put(false);
            jsonarray.put(new JSONArray());
            jsonarray.put(new JSONObject());
            jsonobject.put("keys", JSONObject.getNames(jsonobject));
            assertEquals(
                    "{\n    \"to\": null,\n    \"ten\": 10,\n    \"JSONObject\": {},\n    \"JSONArray\": [],\n    \"op\": \"Good\",\n    \"keys\": [\n        \"to\",\n        \"ten\",\n        \"JSONObject\",\n        \"JSONArray\",\n        \"op\",\n        \"int\",\n        \"true\",\n        \"foo\",\n        \"zero\",\n        \"double\",\n        \"String\",\n        \"false\",\n        \"bool\",\n        \"\\\\u2028\",\n        \"\\\\u2029\",\n        \"null\"\n    ],\n    \"int\": 57,\n    \"true\": true,\n    \"foo\": [\n        true,\n        false,\n        9876543210,\n        0,\n        1.00000001,\n        1.000000000001,\n        1,\n        1.0E-17,\n        2,\n        0.1,\n        2.0E100,\n        -32,\n        [],\n        {},\n        \"string\",\n        666,\n        2001.99,\n        \"so \\\"fine\\\".\",\n        \"so <fine>.\",\n        true,\n        false,\n        [],\n        {}\n    ],\n    \"zero\": -0,\n    \"double\": 1.2345678901234568E29,\n    \"String\": \"98.6\",\n    \"false\": false,\n    \"bool\": \"true\",\n    \"\\\\u2028\": \"\\u2028\",\n    \"\\\\u2029\": \"\\u2029\",\n    \"null\": null\n}",
                    jsonobject.toString(4));
            assertEquals(
                    "<to>null</to><ten>10</ten><JSONObject></JSONObject><op>Good</op><keys>to</keys><keys>ten</keys><keys>JSONObject</keys><keys>JSONArray</keys><keys>op</keys><keys>int</keys><keys>true</keys><keys>foo</keys><keys>zero</keys><keys>double</keys><keys>String</keys><keys>false</keys><keys>bool</keys><keys>\\u2028</keys><keys>\\u2029</keys><keys>null</keys><int>57</int><true>true</true><foo>true</foo><foo>false</foo><foo>9876543210</foo><foo>0.0</foo><foo>1.00000001</foo><foo>1.000000000001</foo><foo>1.0</foo><foo>1.0E-17</foo><foo>2.0</foo><foo>0.1</foo><foo>2.0E100</foo><foo>-32</foo><foo></foo><foo></foo><foo>string</foo><foo>666</foo><foo>2001.99</foo><foo>so &quot;fine&quot;.</foo><foo>so &lt;fine&gt;.</foo><foo>true</foo><foo>false</foo><foo></foo><foo></foo><zero>-0.0</zero><double>1.2345678901234568E29</double><String>98.6</String><false>false</false><bool>true</bool><\\u2028>\u2028</\\u2028><\\u2029>\u2029</\\u2029><null>null</null>",
                    XML.toString(jsonobject));
            assertEquals(98.6d, jsonobject.getDouble("String"), eps);
            assertTrue(jsonobject.getBoolean("bool"));
            assertEquals(
                    "[true,false,9876543210,0,1.00000001,1.000000000001,1,1.0E-17,2,0.1,2.0E100,-32,[],{},\"string\",666,2001.99,\"so \\\"fine\\\".\",\"so <fine>.\",true,false,[],{}]",
                    jsonobject.getJSONArray("foo").toString());
            assertEquals("Good", jsonobject.getString("op"));
            assertEquals(10, jsonobject.getInt("ten"));
            assertFalse(jsonobject.optBoolean("oops"));
        } catch (JSONException e)
        {
            fail(e.toString());
        }
    }

    /**
     * Tests the multipleThings2 method.
     */
    public void testMultipleThings2()
    {
        try
        {
            jsonobject = new JSONObject(
                    "{string: \"98.6\", long: 2147483648, int: 2147483647, longer: 9223372036854775807, double: 9223372036854775808}");
            assertEquals(
                    "{\n \"int\": 2147483647,\n \"string\": \"98.6\",\n \"longer\": 9223372036854775807,\n \"double\": \"9223372036854775808\",\n \"long\": 2147483648\n}",
                    jsonobject.toString(1));

            // getInt
            assertEquals(2147483647, jsonobject.getInt("int"));
            assertEquals(-2147483648, jsonobject.getInt("long"));
            assertEquals(-1, jsonobject.getInt("longer"));
            try
            {
                jsonobject.getInt("double");
                fail("should fail with - JSONObject[\"double\"] is not an int.");
            } catch (JSONException expected)
            {
                assertEquals("JSONObject[\"double\"] is not an int.",
                        expected.getMessage());
            }
            try
            {
                jsonobject.getInt("string");
                fail("should fail with - JSONObject[\"string\"] is not an int.");
            } catch (JSONException expected)
            {
                assertEquals("JSONObject[\"string\"] is not an int.",
                        expected.getMessage());
            }

            // getLong
            assertEquals(2147483647, jsonobject.getLong("int"));
            assertEquals(2147483648l, jsonobject.getLong("long"));
            assertEquals(9223372036854775807l, jsonobject.getLong("longer"));
            try
            {
                jsonobject.getLong("double");
                fail("should fail with - JSONObject[\"double\"] is not a long.");
            } catch (JSONException expected)
            {
                assertEquals("JSONObject[\"double\"] is not a long.",
                        expected.getMessage());
            }
            try
            {
                jsonobject.getLong("string");
                fail("should fail with - JSONObject[\"string\"] is not a long.");
            } catch (JSONException expected)
            {
                assertEquals("JSONObject[\"string\"] is not a long.",
                        expected.getMessage());
            }

            // getDouble
            assertEquals(2.147483647E9, jsonobject.getDouble("int"), eps);
            assertEquals(2.147483648E9, jsonobject.getDouble("long"), eps);
            assertEquals(9.223372036854776E18, jsonobject.getDouble("longer"),
                    eps);
            assertEquals(9223372036854775808d, jsonobject.getDouble("double"),
                    eps);
            assertEquals(98.6, jsonobject.getDouble("string"), eps);

            jsonobject.put("good sized", 9223372036854775807L);
            assertEquals(
                    "{\n \"int\": 2147483647,\n \"string\": \"98.6\",\n \"longer\": 9223372036854775807,\n \"good sized\": 9223372036854775807,\n \"double\": \"9223372036854775808\",\n \"long\": 2147483648\n}",
                    jsonobject.toString(1));

            jsonarray = new JSONArray(
                    "[2147483647, 2147483648, 9223372036854775807, 9223372036854775808]");
            assertEquals(
                    "[\n 2147483647,\n 2147483648,\n 9223372036854775807,\n \"9223372036854775808\"\n]",
                    jsonarray.toString(1));

            List<String> expectedKeys = new ArrayList<String>(6);
            expectedKeys.add("int");
            expectedKeys.add("string");
            expectedKeys.add("longer");
            expectedKeys.add("good sized");
            expectedKeys.add("double");
            expectedKeys.add("long");

            iterator = jsonobject.keys();
            while (iterator.hasNext())
            {
                string = iterator.next();
                assertTrue(expectedKeys.remove(string));
            }
            assertEquals(0, expectedKeys.size());
        } catch (JSONException e)
        {
            fail(e.toString());
        }
    }

    /**
     * Tests the put method using collection and map.
     */
    public void testPut_CollectionAndMap()
    {
        try
        {
            string = "{plist=Apple; AnimalSmells = { pig = piggish; lamb = lambish; worm = wormy; }; AnimalSounds = { pig = oink; lamb = baa; worm = baa;  Lisa = \"Why is the worm talking like a lamb?\" } ; AnimalColors = { pig = pink; lamb = black; worm = pink; } } ";
            jsonobject = new JSONObject(string);
            assertEquals(
                    "{\"AnimalColors\":{\"worm\":\"pink\",\"lamb\":\"black\",\"pig\":\"pink\"},\"plist\":\"Apple\",\"AnimalSounds\":{\"worm\":\"baa\",\"Lisa\":\"Why is the worm talking like a lamb?\",\"lamb\":\"baa\",\"pig\":\"oink\"},\"AnimalSmells\":{\"worm\":\"wormy\",\"lamb\":\"lambish\",\"pig\":\"piggish\"}}",
                    jsonobject.toString());

            Collection<Object> collection = null;
            Map<String, Object> map = null;

            jsonobject = new JSONObject(map);
            jsonarray = new JSONArray(collection);
            jsonobject.append("stooge", "Joe DeRita");
            jsonobject.append("stooge", "Shemp");
            jsonobject.accumulate("stooges", "Curly");
            jsonobject.accumulate("stooges", "Larry");
            jsonobject.accumulate("stooges", "Moe");
            jsonobject.accumulate("stoogearray", jsonobject.get("stooges"));
            jsonobject.put("map", map);
            jsonobject.put("collection", collection);
            jsonobject.put("array", jsonarray);
            jsonarray.put(map);
            jsonarray.put(collection);
            assertEquals(
                    "{\"stooge\":[\"Joe DeRita\",\"Shemp\"],\"map\":{},\"stooges\":[\"Curly\",\"Larry\",\"Moe\"],\"collection\":[],\"stoogearray\":[[\"Curly\",\"Larry\",\"Moe\"]],\"array\":[{},[]]}",
                    jsonobject.toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the accumulate method.
     */
    public void testAccumulate()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.accumulate("stooge", "Curly");
            jsonobject.accumulate("stooge", "Larry");
            jsonobject.accumulate("stooge", "Moe");
            jsonarray = jsonobject.getJSONArray("stooge");
            jsonarray.put(5, "Shemp");
            assertEquals("{\"stooge\": [\n" + "    \"Curly\",\n"
                    + "    \"Larry\",\n" + "    \"Moe\",\n" + "    null,\n"
                    + "    null,\n" + "    \"Shemp\"\n" + "]}",
                    jsonobject.toString(4));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the write method.
     */
    public void testWrite()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.accumulate("stooge", "Curly");
            jsonobject.accumulate("stooge", "Larry");
            jsonobject.accumulate("stooge", "Moe");
            jsonarray = jsonobject.getJSONArray("stooge");
            jsonarray.put(5, "Shemp");
            assertEquals(
                    "{\"stooge\":[\"Curly\",\"Larry\",\"Moe\",null,null,\"Shemp\"]}",
                    jsonobject.write(new StringWriter()).toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the toString method using html.
     */
    public void testToString_Html()
    {
        try
        {
            jsonobject = new JSONObject(
                    "{script: 'It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers</script>so we insert a backslash before the /'}");
            assertEquals(
                    "{\"script\":\"It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers<\\/script>so we insert a backslash before the /\"}",
                    jsonobject.toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the toString method using multiple test cases.
     */
    public void testToString_MultipleTestCases()
    {
        try
        {
            jsonobject = new JSONObject(
                    "{ fun => with non-standard forms ; forgiving => This package can be used to parse formats that are similar to but not stricting conforming to JSON; why=To make it easier to migrate existing data to JSON,one = [[1.00]]; uno=[[{1=>1}]];'+':+6e66 ;pluses=+++;empty = '' , 'double':0.666,true: TRUE, false: FALSE, null=NULL;[true] = [[!,@;*]]; string=>  o. k. ; \r oct=0666; hex=0x666; dec=666; o=0999; noh=0x0x}");
            assertEquals(
                    "{\n \"noh\": \"0x0x\",\n \"one\": [[1]],\n \"o\": 999,\n \"+\": 6.0E66,\n \"true\": true,\n \"forgiving\": \"This package can be used to parse formats that are similar to but not stricting conforming to JSON\",\n \"fun\": \"with non-standard forms\",\n \"double\": 0.666,\n \"uno\": [[{\"1\": 1}]],\n \"dec\": 666,\n \"oct\": 666,\n \"hex\": \"0x666\",\n \"string\": \"o. k.\",\n \"empty\": \"\",\n \"false\": false,\n \"[true]\": [[\n  \"!\",\n  \"@\",\n  \"*\"\n ]],\n \"pluses\": \"+++\",\n \"why\": \"To make it easier to migrate existing data to JSON\",\n \"null\": null\n}",
                    jsonobject.toString(1));
            assertTrue(jsonobject.getBoolean("true"));
            assertFalse(jsonobject.getBoolean("false"));

        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the populateMap method using priative class.
     */
    public void testConstructor_PriativeClass()
    {
        jsonobject = new JSONObject(new ObjectWithPrimatives());
        assertEquals(
                "{\"l\":5748548957230984584,\"m\":true,\"big\":false,\"j\":\"3\",\"k\":10.03,\"ZERO\":0,\"i\":3}",
                jsonobject.toString());
    }

    /**
     * Tests the populateMap method using sub class.
     */
    public void testConstructor_SubClass()
    {
        ObjectWithPrimatives ob = new ObjectWithPrimativesExtension();
        jsonobject = new JSONObject(ob);
        assertEquals(
                "{\"l\":5748548957230984584,\"m\":true,\"big\":false,\"j\":\"3\",\"k\":10.03,\"ZERO\":0,\"i\":3}",
                jsonobject.toString());
    }

    /**
     * Tests the populateMap method using private class.
     */
    public void testConstructor_PrivateClass()
    {
        class PrivateObject
        {
            private int i;

            public PrivateObject()
            {
                i = 3;
            }

            @SuppressWarnings("unused")
            public int getI()
            {
                return i;
            }
        }

        jsonobject = new JSONObject(new PrivateObject());
        assertEquals("{}", jsonobject.toString());
    }

    /**
     * Tests the populateMap method using array list.
     */
    public void testConstructor_ArrayList()
    {
        List<String> ar = new ArrayList<String>();
        ar.add("test1");
        ar.add("test2");

        jsonobject = new JSONObject(ar);
        assertEquals("{\"empty\":false}", jsonobject.toString());
    }

    /**
     * Tests the populateMap method using class class.
     */
    public void testConstructor_ClassClass()
    {
        try
        {
            jsonobject = new JSONObject(this.getClass());
            assertEquals("class junit.framework.TestCase",
                    jsonobject.get("genericSuperclass").toString());
        } catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the constructor method using french resource bundle.
     */
    public static void testConstructor_FrenchResourceBundle()
    {
        try
        {
            Locale currentLocale = new Locale("fr", "CA", "UNIX");

            assertEquals(
                    "{\"ASCII\":\"Number that represent chraracters\",\"JSON\":\"What are we testing?\",\"JAVA\":\"The language you are running to see this\"}",
                    new JSONObject("org.json.tests.SampleResourceBundle",
                            currentLocale).toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the constructor method using us resource bundle.
     */
    public static void testConstructor_UsResourceBundle()
    {
        try
        {
            Locale currentLocale = new Locale("en");
            assertEquals(
                    "{\"ASCII\":\"American Standard Code for Information Interchange\",\"JSON\":\"JavaScript Object Notation\",\"JAVA\":{\"desc\":\"Just Another Vague Acronym\",\"data\":\"Sweet language\"}}",
                    new JSONObject("org.json.tests.SampleResourceBundle",
                            currentLocale).toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the constructor method using object with string array.
     */
    public void testConstructor_ObjectWithStringArray()
    {
        assertEquals("{\"m\":true,\"i\":3}", new JSONObject(
                new ObjectWithPrimatives(), new String[]
                {
                        "i", "m", "k"
                }).toString());
    }

    /**
     * Tests the opt method.
     */
    public void testOpt()
    {
        try
        {
            jsonobject = new JSONObject("{\"a\":2}");
            assertEquals(2, jsonobject.opt("a"));
            assertEquals(null, jsonobject.opt("b"));
            assertEquals(null, jsonobject.opt(null));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the stringToValue method.
     */
    public static void testStringToValue()
    {
        assertEquals("", JSONObject.stringToValue(""));
        assertEquals(true, JSONObject.stringToValue("true"));
        assertEquals(false, JSONObject.stringToValue("false"));
        assertEquals(JSONObject.NULL, JSONObject.stringToValue("null"));
        assertEquals(10, JSONObject.stringToValue("10"));
        assertEquals(10000.0, JSONObject.stringToValue("10e3"));
        assertEquals(10000.0, JSONObject.stringToValue("10E3"));
        assertEquals("10E3000000000", JSONObject.stringToValue("10E3000000000"));
    }

    /**
     * Tests the quote method.
     */
    public static void testQuote()
    {
        assertEquals("\"\"", JSONObject.quote(""));
        assertEquals("\"\"", JSONObject.quote(null));
        assertEquals("\"true\"", JSONObject.quote("true"));
        assertEquals("\"10\"", JSONObject.quote("10"));
        assertEquals("\"\\b\\t\\n\\f\\r\"", JSONObject.quote("\b\t\n\f\r"));
        assertEquals("\"\\u0012\\u0085\\u2086\u2286\"",
                JSONObject.quote("\u0012\u0085\u2086\u2286"));
    }

    /**
     * Tests the getNames method.
     */
    public void testGetNames()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("a", "123");

            jsonobject.put("b", "123");
            jsonobject.put("c", "123");
            String[] names = JSONObject.getNames(jsonobject);
            assertEquals(3, names.length);
            assertEquals("b", names[0]);
            assertEquals("c", names[1]);
            assertEquals("a", names[2]);
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getNames method using empty json object.
     */
    public void testGetNames_EmptyJsonObject()
    {
        jsonobject = new JSONObject();
        assertEquals(null, JSONObject.getNames(jsonobject));
    }

    /**
     * Tests the getNames method using object with primatives.
     */
    public void testGetNames_ObjectWithPrimatives()
    {
        String[] names = JSONObject.getNames(new ObjectWithPrimatives());
        assertEquals(2, names.length);
        assertEquals("i", names[0]);
        assertEquals("m", names[1]);
    }

    /**
     * Tests the getNames method using empty object.
     */
    public static void testGetNames_EmptyObject()
    {
        class EmptyObject
        {
            // Empty Object
        }
        assertEquals(null, JSONObject.getNames(new EmptyObject()));
    }

    /**
     * Tests the getNames method using null.
     */
    public static void testGetNames_Null()
    {
        ObjectWithPrimatives owp = null;
        assertEquals(null, JSONObject.getNames(owp));
    }

    /**
     * Tests the getLong method.
     */
    public void testGetLong_Long()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", "98765432");
            jsonobject.put("123", 98765432);
            assertEquals(98765432, jsonobject.getLong("abc"));
            assertEquals(98765432, jsonobject.getLong("123"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getJsonObject method using json object.
     */
    public void testGetJsonObject_JsonObject()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", new JSONObject());
            assertEquals("{}", jsonobject.getJSONObject("abc").toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getJsonObject method using int.
     */
    public void testGetJsonObject_Int()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", 45);
            jsonobject.getJSONObject("abc");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"abc\"] is not a JSONObject.",
                    e.getMessage());
        }
    }

    /**
     * Tests the getJsonObject method using invalid key.
     */
    public void testGetJsonObject_InvalidKey()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.getJSONObject("abc");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"abc\"] not found.", e.getMessage());
        }
    }

    /**
     * Tests the getJsonArray method using json array.
     */
    public void testGetJsonArray_JsonArray()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", new JSONArray());
            assertEquals("[]", jsonobject.getJSONArray("abc").toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getJsonArray method using int.
     */
    public void testGetJsonArray_Int()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", 45);
            jsonobject.getJSONArray("abc");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"abc\"] is not a JSONArray.",
                    e.getMessage());
        }
    }

    /**
     * Tests the getJsonArray method using invalid key.
     */
    public void testGetJsonArray_InvalidKey()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.getJSONArray("abc");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"abc\"] not found.", e.getMessage());
        }
    }

    /**
     * Tests the getInt method using int.
     */
    public void testGetInt_Int()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", 45);
            assertEquals(45, jsonobject.getInt("abc"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getInt method using int string.
     */
    public void testGetInt_IntString()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", "45");
            assertEquals(45, jsonobject.getInt("abc"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getInt method using letter string.
     */
    public void testGetInt_LetterString()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", "def");
            jsonobject.getInt("abc");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"abc\"] is not an int.", e.getMessage());
        }
    }

    /**
     * Tests the getInt method using invalid key.
     */
    public void testGetInt_InvalidKey()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.getInt("abc");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"abc\"] not found.", e.getMessage());
        }
    }

    /**
     * Tests the getDouble method using double.
     */
    public void testGetDouble_Double()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", 45.10);
            assertEquals(45.10, jsonobject.getDouble("abc"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getDouble method using double string.
     */
    public void testGetDouble_DoubleString()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", "45.20");
            assertEquals(45.20, jsonobject.getDouble("abc"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getDouble method using letter string.
     */
    public void testGetDouble_LetterString()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", "def");
            jsonobject.getDouble("abc");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"abc\"] is not a number.", e.getMessage());
        }
    }

    /**
     * Tests the getDouble method using invalid key.
     */
    public void testGetDouble_InvalidKey()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.getDouble("abc");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"abc\"] not found.", e.getMessage());
        }
    }

    /**
     * Tests the getBoolean method using boolean.
     */
    public void testGetBoolean_Boolean()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", true);
            jsonobject.put("123", false);
            assertTrue(jsonobject.getBoolean("abc"));
            assertFalse(jsonobject.getBoolean("123"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getBoolean method using boolean string.
     */
    public void testGetBoolean_BooleanString()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", "true");
            jsonobject.put("123", "false");
            jsonobject.put("ABC", "TRUE");
            jsonobject.put("456", "FALSE");
            assertTrue(jsonobject.getBoolean("abc"));
            assertFalse(jsonobject.getBoolean("123"));
            assertTrue(jsonobject.getBoolean("ABC"));
            assertFalse(jsonobject.getBoolean("456"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getBoolean method using letter string.
     */
    public void testGetBoolean_LetterString()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", "def");
            jsonobject.getBoolean("abc");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"abc\"] is not a Boolean.",
                    e.getMessage());
        }
    }

    /**
     * Tests the getBoolean method using int.
     */
    public void testGetBoolean_Int()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", 45);
            jsonobject.getBoolean("abc");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"abc\"] is not a Boolean.",
                    e.getMessage());
        }
    }

    /**
     * Tests the getBoolean method using invalid key.
     */
    public void testGetBoolean_InvalidKey()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.getBoolean("abc");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"abc\"] not found.", e.getMessage());
        }
    }

    /**
     * Tests the get method using null key.
     */
    public void testGet_NullKey()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.get(null);
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Null key.", e.getMessage());
        }
    }

    /**
     * Tests the toString method using indents.
     */
    public void testToString_Indents()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", "123");
            jsonobject.put("1235", new JSONObject().put("abc", "123"));
            jsonobject.put("1234", "abc");
            jsonobject.put(
                    "1239",
                    new JSONObject().put("1fd23", new JSONObject()).put(
                            "12gfdgfg3",
                            new JSONObject().put("f123", "123").put("12fgfg3",
                                    "abc")));
            assertEquals(
                    "{\n    \"1234\": \"abc\",\n    \"1235\": {\"abc\": \"123\"},\n    \"123\": \"123\",\n    \"1239\": {\n        \"1fd23\": {},\n        \"12gfdgfg3\": {\n            \"f123\": \"123\",\n            \"12fgfg3\": \"abc\"\n        }\n    }\n}",
                    jsonobject.toString(4));
            assertEquals("{\"1gg23\": \"123\"}",
                    new JSONObject().put("1gg23", "123").toString(4));
            assertEquals("{}", new JSONObject().toString(4));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the toString method using exception.
     */
    public void testToString_Exception()
    {

        try
        {
            jsonobject.put("abc", new BadJsonString());
            assertEquals(null, jsonobject.toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the numberToString method.
     */
    public static void testNumberToString()
    {
        try
        {
            assertEquals("30.7", JSONObject.numberToString(30.70));
            assertEquals("3.0E71", JSONObject.numberToString(30e70));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the numberToString method using null.
     */
    public static void testNumberToString_Null()
    {
        try
        {
            JSONObject.numberToString(null);
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Null pointer", e.getMessage());
        }
    }

    /**
     * Tests the wrap method.
     */
    public void testWrap()
    {
        try
        {
            assertEquals(true, JSONObject.wrap(true));
            assertEquals(35, JSONObject.wrap(35));
            assertEquals(35.5, JSONObject.wrap(35.50));
            assertEquals(56456456L, JSONObject.wrap(56456456L));
            assertEquals(JSONObject.NULL, JSONObject.wrap(null));
            assertEquals(JSONObject.NULL, JSONObject.wrap(JSONObject.NULL));
            BadJsonString a = new BadJsonString();
            assertEquals(a, JSONObject.wrap(a));
            NullJsonString q = new NullJsonString();
            assertEquals(q, JSONObject.wrap(q));
            assertEquals((short) 12, JSONObject.wrap((short) 12));
            assertEquals('a', JSONObject.wrap('a'));
            assertEquals((byte) 15, JSONObject.wrap((byte) 15));
            assertEquals((float) 15.2, JSONObject.wrap((float) 15.2));
            JSONObject b;
            b = new JSONObject().put("abc", "123");
            assertEquals(b, JSONObject.wrap(b));
            JSONArray c = new JSONArray().put("abc");
            assertEquals(c, JSONObject.wrap(c));
            Collection<String> stringCol = new Stack<String>();
            stringCol.add("string1");
            assertEquals("[\"string1\"]", JSONObject.wrap(stringCol).toString());
            assertEquals("[\"abc\",\"123\"]", JSONObject.wrap(new String[]
            {
                    "abc", "123"
            }).toString());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("abc", "123");
            assertEquals("{\"abc\":\"123\"}", JSONObject.wrap(map).toString());
            assertEquals("javax.print.PrintException",
                    JSONObject.wrap(new javax.print.PrintException()));
            Class<?> d = this.getClass();
            assertEquals("class org.json.tests.TestJSONObject",
                    JSONObject.wrap(d));
            class testClass
            {
                // Empty Class
            }
            assertEquals("{}", JSONObject.wrap(new testClass()).toString());
            class BadCollection<E> implements Collection<E>
            {

                /*
                 * (non-Javadoc)
                 * 
                 * @see java.util.Collection#size()
                 */
                @Override
                public int size()
                {
                    return 0;
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see java.util.Collection#isEmpty()
                 */
                @Override
                public boolean isEmpty()
                {
                    return false;
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see java.util.Collection#contains(java.lang.Object)
                 */
                @Override
                public boolean contains(Object o)
                {
                    return false;
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see java.util.Collection#iterator()
                 */
                @Override
                public Iterator<E> iterator()
                {
                    @SuppressWarnings("unchecked")
                    Iterator<E>[] i = new Iterator[0];
                    return i[1];
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see java.util.Collection#toArray()
                 */
                @Override
                public Object[] toArray()
                {
                    return null;
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see java.util.Collection#toArray(T[])
                 */
                @SuppressWarnings("unchecked")
                @Override
                public Object[] toArray(Object[] Type)
                {
                    return null;
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see java.util.Collection#add(java.lang.Object)
                 */
                @Override
                public boolean add(Object e)
                {
                    return false;
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see java.util.Collection#remove(java.lang.Object)
                 */
                @Override
                public boolean remove(Object o)
                {
                    return false;
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see java.util.Collection#containsAll(java.util.Collection)
                 */
                @Override
                public boolean containsAll(Collection<?> collection)
                {
                    return false;
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see java.util.Collection#addAll(java.util.Collection)
                 */
                @Override
                public boolean addAll(Collection<? extends E> collection)
                {
                    return false;
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see java.util.Collection#removeAll(java.util.Collection)
                 */
                @Override
                public boolean removeAll(Collection<?> collection)
                {
                    return false;
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see java.util.Collection#retainAll(java.util.Collection)
                 */
                @Override
                public boolean retainAll(Collection<?> collection)
                {
                    return false;
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see java.util.Collection#clear()
                 */
                @Override
                public void clear()
                {
                    //Do Nothing
                }

            }
            try
            {
                JSONObject.wrap(new BadCollection<String>()).toString();
                fail("Should have thrown exception.");
            } catch (Exception e)
            {
                assertEquals(null, e.getMessage());
            }
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the writer method.
     */
    public void testWriter()
    {
        try
        {
            StringWriter sw = new StringWriter();
            jsonobject = new JSONObject();
            jsonobject.put("1ghr23", "123");
            jsonobject.put("1ss23", "-12");
            jsonobject.put("1tr23", 45);
            jsonobject.put("1trt23", -98);
            jsonobject.put("1hh23", new JSONObject().put("123", "abc"));
            jsonobject.put("1er23", "-12");
            jsonobject.put("1re23", "abc");
            jsonobject.put("1fde23", "123");
            jsonobject.put("1fd23", "123");
            jsonobject.put("1ffdsf23", new JSONObject().put("abc", "123"));
            jsonobject.put("fd123", "abc");
            jsonobject.put("12fdsf3", "123");
            jsonobject.write(sw);
            assertEquals(
                    "{\"1tr23\":45,\"1ss23\":\"-12\",\"1fd23\":\"123\",\"1trt23\":-98,\"1ffdsf23\":{\"abc\":\"123\"},\"1ghr23\":\"123\",\"1fde23\":\"123\",\"fd123\":\"abc\",\"12fdsf3\":\"123\",\"1hh23\":{\"123\":\"abc\"},\"1re23\":\"abc\",\"1er23\":\"-12\"}",
                    sw.toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the writer method using bad writer.
     */
    public void testWriter_BadWriter()
    {
        class BadWriter extends Writer
        {

            /*
             * (non-Javadoc)
             * 
             * @see java.io.Writer#write(char[], int, int)
             */
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException
            {
                throw new IOException("Test Message From Bad Writer");
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.io.Writer#flush()
             */
            @Override
            public void flush() throws IOException
            {
                // Do Nothing
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.io.Writer#close()
             */
            @Override
            public void close() throws IOException
            {
                // Do nothing
            }

        }
        try
        {
            BadWriter sw = new BadWriter();
            jsonobject = new JSONObject();
            jsonobject.put("1ghr23", "123");
            jsonobject.put("1ss23", "-12");
            jsonobject.put("1tr23", 45);
            jsonobject.put("1trt23", -98);
            jsonobject.put("1hh23", new JSONObject().put("123", "abc"));
            jsonobject.put("1er23", "-12");
            jsonobject.put("1re23", "abc");
            jsonobject.put("1fde23", "123");
            jsonobject.put("1fd23", "123");
            jsonobject.put("1ffdsf23", new JSONObject().put("abc", "123"));
            jsonobject.put("fd123", "abc");
            jsonobject.put("12fdsf3", "123");
            jsonobject.write(sw);
        } catch (JSONException e)
        {
            assertEquals("Test Message From Bad Writer", e.getMessage());
        }
    }

    /**
     * Tests the increment method.
     */
    public void testIncrement()
    {
        try
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("1ghr23", 30.56);
            map.put("1ss23", -12.22);
            map.put("1tr23", 45);
            map.put("1trt23", -98);
            map.put("1hh23", (float) 12.6);
            map.put("1er23", (float) -456.255);
            map.put("1re23", 5543L);
            map.put("1fde23", -46546546L);
            jsonobject = new JSONObject(map);
            jsonobject.increment("1ghr23");
            jsonobject.increment("1ss23");
            jsonobject.increment("1tr23");
            jsonobject.increment("1trt23");
            jsonobject.increment("1hh23");
            jsonobject.increment("1er23");
            jsonobject.increment("1re23");
            jsonobject.increment("1fde23");
            assertEquals(31.56, jsonobject.get("1ghr23"));
            assertEquals(-11.22, jsonobject.get("1ss23"));
            assertEquals(46, jsonobject.get("1tr23"));
            assertEquals(-97, jsonobject.get("1trt23"));
            assertEquals(13.6f, (Double) jsonobject.get("1hh23"), eps);
            assertEquals(-455.255f, (Double) jsonobject.get("1er23"), eps);
            assertEquals(5544L, jsonobject.get("1re23"));
            assertEquals(-46546545L, jsonobject.get("1fde23"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the increment method using string.
     */
    public void testIncrement_String()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", "abc");
            jsonobject.increment("123");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Unable to increment [\"123\"].", e.getMessage());
        }
    }

    /**
     * Tests the get method using invalid index.
     */
    public void testGet_InvalidIndex()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("554", "123");
            jsonobject.get("abc");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"abc\"] not found.", e.getMessage());
        }
    }

    /**
     * Tests the get method using valid index.
     */
    public void testGet_ValidIndex()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", "123");
            assertEquals("123", jsonobject.get("abc"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getBoolean method.
     */
    public void testGetBoolean()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("1gg23", "true");
            jsonobject.put("1dd23", "false");
            jsonobject.put("1ff23", true);
            jsonobject.put("1rr23", false);
            jsonobject.put("1hh23", "TRUE");
            jsonobject.put("1hhhgf23", "FALSE");
            assertTrue(jsonobject.getBoolean("1gg23"));
            assertFalse(jsonobject.getBoolean("1dd23"));
            assertTrue(jsonobject.getBoolean("1ff23"));
            assertFalse(jsonobject.getBoolean("1rr23"));
            assertTrue(jsonobject.getBoolean("1hh23"));
            assertFalse(jsonobject.getBoolean("1hhhgf23"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getBoolean method using non boolean.
     */
    public void testGetBoolean_NonBoolean()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", "123");
            jsonobject.getBoolean("123");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"123\"] is not a Boolean.",
                    e.getMessage());
        }
    }

    /**
     * Tests the optBoolean method.
     */
    public void testOptBoolean()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("1gg23", "true");
            jsonobject.put("1dd23", "false");
            jsonobject.put("1ff23", true);
            jsonobject.put("1rr23", false);
            jsonobject.put("1hh23", "TRUE");
            jsonobject.put("1hhhgf23", "FALSE");
            assertTrue(jsonobject.optBoolean("1gg23"));
            assertFalse(jsonobject.optBoolean("1dd23"));
            assertTrue(jsonobject.optBoolean("1ff23"));
            assertFalse(jsonobject.optBoolean("1rr23"));
            assertTrue(jsonobject.optBoolean("1hh23"));
            assertFalse(jsonobject.optBoolean("1hhhgf23"));
            assertFalse(jsonobject.optBoolean("chicken"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getInt method.
     */
    public void testGetInt()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("1gg23", "123");
            jsonobject.put("1g23", "-12");
            jsonobject.put("123", 45);
            jsonobject.put("1rr23", -98);
            assertEquals(123, jsonobject.getInt("1gg23"));
            assertEquals(-12, jsonobject.getInt("1g23"));
            assertEquals(45, jsonobject.getInt("123"));
            assertEquals(-98, jsonobject.getInt("1rr23"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getInt method using non integer.
     */
    public void testGetInt_NonInteger()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", "abc");
            jsonobject.getInt("123");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"123\"] is not an int.", e.getMessage());
        }
    }

    /**
     * Tests the optInt method.
     */
    public void testOptInt()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("1gg23", "123");
            jsonobject.put("1g23", "-12");
            jsonobject.put("123", 45);
            jsonobject.put("1rr23", -98);
            assertEquals(123, jsonobject.optInt("1gg23"));
            assertEquals(-12, jsonobject.optInt("1g23"));
            assertEquals(45, jsonobject.optInt("123"));
            assertEquals(-98, jsonobject.optInt("1rr23"));
            assertEquals(0, jsonobject.optInt("catfish"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getDouble method.
     */
    public void testGetDouble()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", "123");
            jsonobject.put("1fd23", "-12");
            jsonobject.put("1gfd23", 45);
            jsonobject.put("1gg23", -98);
            jsonobject.put("1f23", "123.5");
            jsonobject.put("1ss23", "-12.87");
            jsonobject.put("1ew23", 45.22);
            jsonobject.put("1tt23", -98.18);
            assertEquals(123.0, jsonobject.getDouble("123"));
            assertEquals(-12.0, jsonobject.getDouble("1fd23"));
            assertEquals(45.0, jsonobject.getDouble("1gfd23"));
            assertEquals(-98.0, jsonobject.getDouble("1gg23"));
            assertEquals(123.5, jsonobject.getDouble("1f23"));
            assertEquals(-12.87, jsonobject.getDouble("1ss23"));
            assertEquals(45.22, jsonobject.getDouble("1ew23"));
            assertEquals(-98.18, jsonobject.getDouble("1tt23"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getDouble method using non double.
     */
    public void testGetDouble_NonDouble()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", "abc");
            jsonobject.getDouble("123");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"123\"] is not a number.", e.getMessage());
        }
    }

    /**
     * Tests the optDouble method.
     */
    public void testOptDouble()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", "123");
            jsonobject.put("1fd23", "-12");
            jsonobject.put("1gfd23", 45);
            jsonobject.put("1gg23", -98);
            jsonobject.put("1f23", "123.5");
            jsonobject.put("1ss23", "-12.87");
            jsonobject.put("1ew23", 45.22);
            jsonobject.put("1tt23", -98.18);
            assertEquals(123.0, jsonobject.optDouble("123"));
            assertEquals(-12.0, jsonobject.optDouble("1fd23"));
            assertEquals(45.0, jsonobject.optDouble("1gfd23"));
            assertEquals(-98.0, jsonobject.optDouble("1gg23"));
            assertEquals(123.5, jsonobject.optDouble("1f23"));
            assertEquals(-12.87, jsonobject.optDouble("1ss23"));
            assertEquals(45.22, jsonobject.optDouble("1ew23"));
            assertEquals(-98.18, jsonobject.optDouble("1tt23"));
            assertEquals(Double.NaN, jsonobject.optDouble("rabbit"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getLong method.
     */
    public void testGetLong()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", "123");
            jsonobject.put("12gh3", "-12");
            jsonobject.put("1f23", 45L);
            jsonobject.put("1re23", -98L);
            assertEquals(123, jsonobject.getLong("123"));
            assertEquals(-12, jsonobject.getLong("12gh3"));
            assertEquals(45, jsonobject.getLong("1f23"));
            assertEquals(-98, jsonobject.getLong("1re23"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getLong method using non long.
     */
    public void testGetLong_NonLong()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", "abc");
            jsonobject.getLong("123");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"123\"] is not a long.", e.getMessage());
        }
    }

    /**
     * Tests the optLong method.
     */
    public void testOptLong()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", "123");
            jsonobject.put("12gh3", "-12");
            jsonobject.put("1f23", 45L);
            jsonobject.put("1re23", -98L);
            assertEquals(123, jsonobject.optLong("123"));
            assertEquals(-12, jsonobject.optLong("12gh3"));
            assertEquals(45, jsonobject.optLong("1f23"));
            assertEquals(-98, jsonobject.optLong("1re23"));
            assertEquals(0, jsonobject.optLong("randomString"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getString method.
     */
    public void testGetString()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", "123");
            jsonobject.put("12gf3", "-12");
            jsonobject.put("1fg23", "abc");
            jsonobject.put("1d23", "123");
            assertEquals("123", jsonobject.getString("123"));
            assertEquals("-12", jsonobject.getString("12gf3"));
            assertEquals("abc", jsonobject.getString("1fg23"));
            assertEquals("123", jsonobject.getString("1d23"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the getString method using non string.
     */
    public void testGetString_NonString()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", 123);
            jsonobject.getString("123");
        } catch (JSONException e)
        {
            assertEquals("JSONObject[\"123\"] not a string.", e.getMessage());
        }
    }

    /**
     * Tests the optString method.
     */
    public void testOptString()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", "123");
            jsonobject.put("12gf3", "-12");
            jsonobject.put("1fg23", "abc");
            jsonobject.put("1d23", "123");
            assertEquals("123", jsonobject.optString("123"));
            assertEquals("-12", jsonobject.optString("12gf3"));
            assertEquals("abc", jsonobject.optString("1fg23"));
            assertEquals("123", jsonobject.optString("1d23"));
            assertEquals("", jsonobject.optString("pandora"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the optJSONObject method using simple object.
     */
    public void testOptJSONObject_SimpleObject()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", new JSONObject().put("abc", "123"));
            assertEquals("{\"abc\":\"123\"}", jsonobject.optJSONObject("123")
                    .toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the optJSONObject method using non json object.
     */
    public void testOptJSONObject_NonJsonObject()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", "123");
            assertEquals(null, jsonobject.optJSONObject("123"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the optJSONArray method.
     */
    public void testOptJSONArray()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", new JSONArray().put("abc"));
            assertEquals("[\"abc\"]", jsonobject.optJSONArray("123").toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the optJSONArray method using non json array.
     */
    public void testOptJSONArray_NonJsonArray()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", "123");
            assertEquals(null, jsonobject.optJSONArray("123"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the has method.
     */
    public void testHas()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("123", "123");
            assertTrue(jsonobject.has("123"));
            assertFalse(jsonobject.has("124"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the append method.
     */
    public void testAppend()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("string", "123");
            jsonobject.put("jsonarray", new JSONArray().put("abc"));
            jsonobject.append("jsonarray", "123");
            jsonobject.append("george", "def");
            try
            {
                jsonobject.append("string", "abc");
                fail("Should have thrown exception.");
            } catch (JSONException e)
            {
                assertEquals("JSONObject[string] is not a JSONArray.",
                        e.getMessage());
            }
            assertEquals(
                    "{\"george\":[\"def\"],\"string\":\"123\",\"jsonarray\":[\"abc\",\"123\"]}",
                    jsonobject.toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the constuctor method using strings.
     */
    public void testConstuctor_Strings()
    {
        try
        {
            jsonobject = new JSONObject("123");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals(
                    "A JSONObject text must begin with '{' at 1 [character 2 line 1]",
                    e.getMessage());
        }
        try
        {
            jsonobject = new JSONObject("{\"123\":34");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Expected a ',' or '}' at 10 [character 11 line 1]",
                    e.getMessage());
        }
        try
        {
            jsonobject = new JSONObject("{\"123\",34}");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals(
                    "Expected a ':' after a key at 7 [character 8 line 1]",
                    e.getMessage());
        }
        try
        {
            jsonobject = new JSONObject("{");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals(
                    "A JSONObject text must end with '}' at 2 [character 3 line 1]",
                    e.getMessage());
        }
    }

    /**
     * Tests the testValidity method.
     */
    public static void testTestValidity()
    {
        try
        {
            JSONObject.testValidity(null);
            JSONObject.testValidity(50.4);
            JSONObject.testValidity(70.8);
            JSONObject.testValidity(50.4f);
            JSONObject.testValidity(70.8f);
            try
            {
                JSONObject.testValidity(Double.NaN);
                fail("Should have throw exception.");
            } catch (JSONException e)
            {
                assertEquals("JSON does not allow non-finite numbers.",
                        e.getMessage());
            }
            try
            {
                JSONObject.testValidity(Double.NEGATIVE_INFINITY);
                fail("Should have throw exception.");
            } catch (JSONException e)
            {
                assertEquals("JSON does not allow non-finite numbers.",
                        e.getMessage());
            }
            try
            {
                JSONObject.testValidity(Double.POSITIVE_INFINITY);
                fail("Should have throw exception.");
            } catch (JSONException e)
            {
                assertEquals("JSON does not allow non-finite numbers.",
                        e.getMessage());
            }
            try
            {
                JSONObject.testValidity(Float.NaN);
                fail("Should have throw exception.");
            } catch (JSONException e)
            {
                assertEquals("JSON does not allow non-finite numbers.",
                        e.getMessage());
            }
            try
            {
                JSONObject.testValidity(Float.NEGATIVE_INFINITY);
                fail("Should have throw exception.");
            } catch (JSONException e)
            {
                assertEquals("JSON does not allow non-finite numbers.",
                        e.getMessage());
            }
            try
            {
                JSONObject.testValidity(Float.POSITIVE_INFINITY);
                fail("Should have throw exception.");
            } catch (JSONException e)
            {
                assertEquals("JSON does not allow non-finite numbers.",
                        e.getMessage());
            }
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the names method.
     */
    public void testNames()
    {
        try
        {
            jsonobject = new JSONObject();
            assertEquals(null, jsonobject.names());
            jsonobject.put("abc", "123");
            assertEquals("[\"abc\"]", jsonobject.names().toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the constructor method using copy subset.
     */
    public void testConstructor_CopySubset()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", "123");
            jsonobject.put("abcd", "1234");
            jsonobject.put("abcde", "12345");
            jsonobject.put("abcdef", "123456");
            assertEquals("{\"abc\":\"123\",\"abcde\":\"12345\"}",
                    new JSONObject(jsonobject, new String[]
                    {
                            "abc", "abc", "abcde"
                    }).toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the putOnce method.
     */
    public void testPutOnce()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.putOnce("abc", "123").putOnce("abcd", "1234")
                    .putOnce(null, "12345").putOnce("abcdef", null);
            assertEquals("{\"abc\":\"123\",\"abcd\":\"1234\"}",
                    jsonobject.toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the toJsonArray method.
     */
    public void testToJsonArray()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", "123");
            jsonobject.put("abcd", "1234");
            jsonobject.put("abcde", "12345");
            jsonobject.put("abcdef", "123456");
            assertEquals("[\"123\",\"123\",\"12345\"]",
                    jsonobject.toJSONArray(new JSONArray(new String[]
                    {
                            "abc", "abc", "abcde"
                    })).toString());
            assertEquals(null, jsonobject.toJSONArray(new JSONArray()));
            assertEquals(null, jsonobject.toJSONArray(null));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the valueToString method.
     */
    public void testValueToString()
    {
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("abc", "123");
            jsonobject.put("abcd", "1234");
            BadJsonString bjs = new BadJsonString();
            jsonobject.put("cd", bjs);
            jsonobject.put("acd", new GoodJsonString());
            NullJsonString q = new NullJsonString();
            jsonobject.put("zzz", q);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("abc", "123");
            Collection<String> stringCol = new Stack<String>();
            stringCol.add("string1");
            jsonobject.put("de", map);
            jsonobject.put("e", stringCol);
            jsonobject.put("abcde", new JSONArray().put("123"));
            jsonobject.put("abcdef", new JSONObject().put("123", "123456"));
            JSONObject nulljo = null;
            JSONArray nullja = null;
            jsonobject.put("bcde", nulljo);
            jsonobject.put("bcdef", nullja);
            assertEquals(
                    "{\n    \"de\": {\"abc\": \"123\"},\n    \"abc\": \"123\",\n    \"e\": [\"string1\"],\n    \"zzz\": \""
                            + q.toString()
                            + "\",\n    \"abcdef\": {\"123\": \"123456\"},\n    \"abcde\": [\"123\"],\n    \"acd\": jsonstring,\n    \"abcd\": \"1234\",\n    \"cd\": \""
                            + bjs.toString() + "\"\n}", jsonobject.toString(4));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the valueToString method using object.
     */
    public void testValueToString_Object()
    {
        try
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("abcd", "1234");
            Collection<String> stringCol = new Stack<String>();
            stringCol.add("string1");
            jsonobject = new JSONObject();
            jsonobject.put("abc", "123");
            assertEquals("{\"abc\":\"123\"}",
                    JSONObject.valueToString(jsonobject));
            assertEquals("{\"abcd\":\"1234\"}", JSONObject.valueToString(map));
            assertEquals("[\"string1\"]", JSONObject.valueToString(stringCol));
            try
            {
                JSONObject.valueToString(new BadJsonString());
                fail("Should have thrown exception.");
            } catch (JSONException e)
            {
                assertEquals("1", e.getMessage());
            }
            try
            {
                JSONObject.valueToString(new NullJsonString());
                fail("Should have thrown exception.");
            } catch (JSONException e)
            {
                assertEquals("Bad value from toJSONString: null",
                        e.getMessage());
            }
            assertEquals("jsonstring",
                    JSONObject.valueToString(new GoodJsonString()));
            assertEquals("null", JSONObject.valueToString(null));
            assertEquals("null", JSONObject.valueToString(JSONObject.NULL));
            assertEquals("[\"abc\",\"123\"]",
                    JSONObject.valueToString(new String[]
                    {
                            "abc", "123"
                    }));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the doubleToString method.
     */
    public static void testDoubleToString()
    {
        assertEquals("10.66", JSONObject.doubleToString(10.66));
        assertEquals("10", JSONObject.doubleToString(10));
        assertEquals("null", JSONObject.doubleToString(Double.NaN));
        assertEquals("null",
                JSONObject.doubleToString(Double.NEGATIVE_INFINITY));
        assertEquals("null",
                JSONObject.doubleToString(Double.POSITIVE_INFINITY));
        assertEquals("1.0E89", JSONObject.doubleToString(10e88));
        assertEquals("1.0E89", JSONObject.doubleToString(10E88));
    }
}
