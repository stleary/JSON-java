/*
 * File:         TestJSONStringer.java
 * Author:       JSON.org
 */
package org.json.tests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONStringer;
import junit.framework.TestCase;

/**
 * The Class TestJSONString.
 */
public class TestJSONStringer extends TestCase
{
    
    /** The jsonobject. */
    JSONObject jsonobject;
    
    /** The jsonstringer. */
    JSONStringer jsonstringer;
    
    /** The string. */
    String string;

    /** The jsonarray. */
    JSONArray jsonarray;
    
	/**
	 * Tests the jsonString method.
	 */
	public void testJsonString()
	{

		try{
        Beany beanie = new Beany("A beany object", 42, true);
        jsonstringer = new JSONStringer();
        string = jsonstringer.object().key("single").value("MARIE HAA'S")
                .key("Johnny").value("MARIE HAA\\'S").key("foo").value("bar")
                .key("baz").array().object().key("quux").value("Thanks, Josh!")
                .endObject().endArray().key("obj keys")
                .value(JSONObject.getNames(beanie)).endObject().toString();
        assertEquals(
                "{\"single\":\"MARIE HAA'S\",\"Johnny\":\"MARIE HAA\\\\'S\",\"foo\":\"bar\",\"baz\":[{\"quux\":\"Thanks, Josh!\"}],\"obj keys\":[\"aString\",\"aNumber\",\"aBoolean\"]}",
                string);

        assertEquals("{\"a\":[[[\"b\"]]]}", new JSONStringer().object()
                .key("a").array().array().array().value("b").endArray()
                .endArray().endArray().endObject().toString());

        jsonstringer = new JSONStringer();
        jsonstringer.array();
        jsonstringer.value(1);
        jsonstringer.array();
        jsonstringer.value(null);
        jsonstringer.array();
        jsonstringer.object();
        jsonstringer.key("empty-array").array().endArray();
        jsonstringer.key("answer").value(42);
        jsonstringer.key("null").value(null);
        jsonstringer.key("false").value(false);
        jsonstringer.key("true").value(true);
        jsonstringer.key("big").value(123456789e+88);
        jsonstringer.key("small").value(123456789e-88);
        jsonstringer.key("empty-object").object().endObject();
        jsonstringer.key("long");
        jsonstringer.value(9223372036854775807L);
        jsonstringer.endObject();
        jsonstringer.value("two");
        jsonstringer.endArray();
        jsonstringer.value(true);
        jsonstringer.endArray();
        jsonstringer.value(98.6);
        jsonstringer.value(-100.0);
        jsonstringer.object();
        jsonstringer.endObject();
        jsonstringer.object();
        jsonstringer.key("one");
        jsonstringer.value(1.00);
        jsonstringer.endObject();
        jsonstringer.value(beanie);
        jsonstringer.endArray();
        assertEquals(
                "[1,[null,[{\"empty-array\":[],\"answer\":42,\"null\":null,\"false\":false,\"true\":true,\"big\":1.23456789E96,\"small\":1.23456789E-80,\"empty-object\":{},\"long\":9223372036854775807},\"two\"],true],98.6,-100,{},{\"one\":1},{\"A beany object\":42}]",
                jsonstringer.toString());
        assertEquals(
                "[\n    1,\n    [\n        null,\n        [\n            {\n                \"empty-array\": [],\n                \"empty-object\": {},\n                \"answer\": 42,\n                \"true\": true,\n                \"false\": false,\n                \"long\": 9223372036854775807,\n                \"big\": 1.23456789E96,\n                \"small\": 1.23456789E-80,\n                \"null\": null\n            },\n            \"two\"\n        ],\n        true\n    ],\n    98.6,\n    -100,\n    {},\n    {\"one\": 1},\n    {\"A beany object\": 42}\n]",
                new JSONArray(jsonstringer.toString()).toString(4));

        

        String sa[] =
        {
                "aString", "aNumber", "aBoolean"
        };
        jsonobject = new JSONObject(beanie, sa);
        jsonobject.put("Testing JSONString interface", beanie);
        assertEquals(
                "{\n    \"aBoolean\": true,\n    \"aNumber\": 42,\n    \"aString\": \"A beany object\",\n    \"Testing JSONString interface\": {\"A beany object\":42}\n}",
                jsonobject.toString(4));
		}catch(Exception e)
		{
			fail(e.toString());
		}
	}

    /**
     * Beany is a typical class that implements JSONString. It also provides
     * some beany methods that can be used to construct a JSONObject. It also
     * demonstrates constructing a JSONObject with an array of names.
     */
    public class Beany implements JSONString
    {

        /** The a string. */
        public String aString;

        /** The a number. */
        public double aNumber;

        /** The a boolean. */
        public boolean aBoolean;

        /**
         * Instantiates a new beany.
         * 
         * @param String
         *            the string
         * @param d
         *            the d
         * @param b
         *            the b
         */
        public Beany(String String, double d, boolean b)
        {
            aString = String;
            aNumber = d;
            aBoolean = b;
        }

        /**
         * Gets the number.
         * 
         * @return the number
         */
        public double getNumber()
        {
            return aNumber;
        }

        /**
         * Gets the string.
         * 
         * @return the string
         */
        public String getString()
        {
            return aString;
        }

        /**
         * Checks if is boolean.
         * 
         * @return true, if is boolean
         */
        public boolean isBoolean()
        {
            return aBoolean;
        }

        /**
         * Gets the bENT.
         * 
         * @return the bENT
         */
        public String getBENT()
        {
            return "All uppercase key";
        }

        /**
         * Gets the x.
         * 
         * @return the x
         */
        public String getX()
        {
            return "x";
        }

        /*
         * (non-Javadoc)
         * 
         * @see json.JSONString#toJSONString()
         */
        @Override
        public String toJSONString()
        {
            return "{" + JSONObject.quote(aString) + ":"
                    + JSONObject.doubleToString(aNumber) + "}";
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return getString() + " " + getNumber() + " " + isBoolean() + "."
                    + getBENT() + " " + getX();
        }
        
    }
    

    /**
     * Tests the toString method using duplicate keys.
     */
    public void testToString_DuplicateKeys()
    {
        try
        {
            JSONStringer jj = new JSONStringer();
            string = jj.object().key("bosanda").value("MARIE HAA'S")
                    .key("bosanda").value("MARIE HAA\\'S").endObject()
                    .toString();
            fail("expecting JSONException here.");
        } catch (JSONException jsone)
        {
            assertEquals("Duplicate key \"bosanda\"", jsone.getMessage());
        }
    }
    
    /**
     * Tests the array method using object and array.
     */
    public void testArray_ObjectAndArray()
    {
        try
        {
            jsonobject = new JSONObject(
                "{ fun => with non-standard forms ; forgiving => This package can be used to parse formats that are similar to but not stricting conforming to JSON; why=To make it easier to migrate existing data to JSON,one = [[1.00]]; uno=[[{1=>1}]];'+':+6e66 ;pluses=+++;empty = '' , 'double':0.666,true: TRUE, false: FALSE, null=NULL;[true] = [[!,@;*]]; string=>  o. k. ; \r oct=0666; hex=0x666; dec=666; o=0999; noh=0x0x}");
        
            jsonarray = new JSONArray(
                    " [\"<escape>\", next is an implied null , , ok,] ");
            jsonobject = new JSONObject(jsonobject, new String[]
            { "dec", "oct", "hex", "missing" });
            assertEquals(
                    "{\n \"oct\": 666,\n \"dec\": 666,\n \"hex\": \"0x666\"\n}",
                    jsonobject.toString(1));

            assertEquals(
                    "[[\"<escape>\",\"next is an implied null\",null,\"ok\"],{\"oct\":666,\"dec\":666,\"hex\":\"0x666\"}]",
                    new JSONStringer().array().value(jsonarray)
                            .value(jsonobject).endArray().toString());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the toString method.
     */
    public static void testToString_EmptyStringer()
    {
        assertEquals(null, new JSONStringer().toString());
    }
    
}