package org.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.StringWriter;
import junit.framework.TestCase;

/*
Copyright (c) 2002 JSON.org

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

/**
 * Test class. This file is not formally a member of the org.json library.
 * It is just a test tool.
 *
 * Issue: JSONObject does not specify the ordering of keys, so simple-minded
 * comparisons of .toString to a string literal are likely to fail.
 *
 * @author JSON.org
 * @version 2011-10-25
 */
public class Test extends TestCase {
    public Test(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testXML() throws Exception {
        JSONObject jsonobject;
        String string;

        jsonobject = XML.toJSONObject("<![CDATA[This is a collection of test patterns and examples for org.json.]]>  Ignore the stuff past the end.  ");
        assertEquals("{\"content\":\"This is a collection of test patterns and examples for org.json.\"}", jsonobject.toString());
        assertEquals("This is a collection of test patterns and examples for org.json.", jsonobject.getString("content"));

        string = "<test><blank></blank><empty/></test>";
        jsonobject = XML.toJSONObject(string);
        assertEquals("{\"test\": {\n  \"blank\": \"\",\n  \"empty\": \"\"\n}}", jsonobject.toString(2));
        assertEquals("<test><blank/><empty/></test>", XML.toString(jsonobject));

        string = "<subsonic-response><playlists><playlist id=\"476c65652e6d3375\" int=\"12345678901234567890123456789012345678901234567890213991133777039355058536718668104339937\"/><playlist id=\"50617274792e78737066\"/></playlists></subsonic-response>";
        jsonobject = XML.toJSONObject(string);
        assertEquals("{\"subsonic-response\":{\"playlists\":{\"playlist\":[{\"id\":\"476c65652e6d3375\",\"int\":\"12345678901234567890123456789012345678901234567890213991133777039355058536718668104339937\"},{\"id\":\"50617274792e78737066\"}]}}}", jsonobject.toString());
    }

    public void testNull() throws Exception {
        JSONObject jsonobject;

        jsonobject = new JSONObject("{\"message\":\"null\"}");
        assertFalse(jsonobject.isNull("message"));
        assertEquals("null", jsonobject.getString("message"));

        jsonobject = new JSONObject("{\"message\":null}");
        assertTrue(jsonobject.isNull("message"));
    }

    public void testJSON() throws Exception {
        double       eps = 2.220446049250313e-16;
        Iterator     iterator;
        JSONArray    jsonarray;
        JSONObject   jsonobject;
        JSONStringer jsonstringer;
        Object       object;
        String       string;

        Beany beanie = new Beany("A beany object", 42, true);

        string = "[001122334455]";
        jsonarray = new JSONArray(string);
        assertEquals("[1122334455]", jsonarray.toString());

        string = "[666e666]";
        jsonarray = new JSONArray(string);
        assertEquals("[\"666e666\"]", jsonarray.toString());

        string = "[00.10]";
        jsonarray = new JSONArray(string);
        assertEquals("[0.1]", jsonarray.toString());

        jsonobject = new JSONObject();
        object = null;
        jsonobject.put("booga", object);
        jsonobject.put("wooga", JSONObject.NULL);
        assertEquals("{\"wooga\":null}", jsonobject.toString());
        assertTrue(jsonobject.isNull("booga"));

        jsonobject = new JSONObject();
        jsonobject.increment("two");
        jsonobject.increment("two");
        assertEquals("{\"two\":2}", jsonobject.toString());
        assertEquals(2, jsonobject.getInt("two"));

        string = "{     \"list of lists\" : [         [1, 2, 3],         [4, 5, 6],     ] }";
        jsonobject = new JSONObject(string);
        assertEquals("{\"list of lists\": [\n" +
                "    [\n" +
                "        1,\n" +
                "        2,\n" +
                "        3\n" +
                "    ],\n" +
                "    [\n" +
                "        4,\n" +
                "        5,\n" +
                "        6\n" +
                "    ]\n" +
                "]}", jsonobject.toString(4));
        assertEquals("<list of lists><array>1</array><array>2</array><array>3</array></list of lists><list of lists><array>4</array><array>5</array><array>6</array></list of lists>",
                XML.toString(jsonobject));

        string = "<recipe name=\"bread\" prep_time=\"5 mins\" cook_time=\"3 hours\"> <title>Basic bread</title> <ingredient amount=\"8\" unit=\"dL\">Flour</ingredient> <ingredient amount=\"10\" unit=\"grams\">Yeast</ingredient> <ingredient amount=\"4\" unit=\"dL\" state=\"warm\">Water</ingredient> <ingredient amount=\"1\" unit=\"teaspoon\">Salt</ingredient> <instructions> <step>Mix all ingredients together.</step> <step>Knead thoroughly.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Knead again.</step> <step>Place in a bread baking tin.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Bake in the oven at 180(degrees)C for 30 minutes.</step> </instructions> </recipe> ";
        jsonobject = XML.toJSONObject(string);
        assertEquals("{\"recipe\": {\n    \"title\": \"Basic bread\",\n    \"cook_time\": \"3 hours\",\n    \"instructions\": {\"step\": [\n        \"Mix all ingredients together.\",\n        \"Knead thoroughly.\",\n        \"Cover with a cloth, and leave for one hour in warm room.\",\n        \"Knead again.\",\n        \"Place in a bread baking tin.\",\n        \"Cover with a cloth, and leave for one hour in warm room.\",\n        \"Bake in the oven at 180(degrees)C for 30 minutes.\"\n    ]},\n    \"name\": \"bread\",\n    \"ingredient\": [\n        {\n            \"content\": \"Flour\",\n            \"amount\": 8,\n            \"unit\": \"dL\"\n        },\n        {\n            \"content\": \"Yeast\",\n            \"amount\": 10,\n            \"unit\": \"grams\"\n        },\n        {\n            \"content\": \"Water\",\n            \"amount\": 4,\n            \"unit\": \"dL\",\n            \"state\": \"warm\"\n        },\n        {\n            \"content\": \"Salt\",\n            \"amount\": 1,\n            \"unit\": \"teaspoon\"\n        }\n    ],\n    \"prep_time\": \"5 mins\"\n}}",
                jsonobject.toString(4));

        jsonobject = JSONML.toJSONObject(string);
        assertEquals("{\"cook_time\":\"3 hours\",\"name\":\"bread\",\"tagName\":\"recipe\",\"childNodes\":[{\"tagName\":\"title\",\"childNodes\":[\"Basic bread\"]},{\"amount\":8,\"unit\":\"dL\",\"tagName\":\"ingredient\",\"childNodes\":[\"Flour\"]},{\"amount\":10,\"unit\":\"grams\",\"tagName\":\"ingredient\",\"childNodes\":[\"Yeast\"]},{\"amount\":4,\"unit\":\"dL\",\"tagName\":\"ingredient\",\"state\":\"warm\",\"childNodes\":[\"Water\"]},{\"amount\":1,\"unit\":\"teaspoon\",\"tagName\":\"ingredient\",\"childNodes\":[\"Salt\"]},{\"tagName\":\"instructions\",\"childNodes\":[{\"tagName\":\"step\",\"childNodes\":[\"Mix all ingredients together.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Knead thoroughly.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Cover with a cloth, and leave for one hour in warm room.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Knead again.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Place in a bread baking tin.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Cover with a cloth, and leave for one hour in warm room.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Bake in the oven at 180(degrees)C for 30 minutes.\"]}]}],\"prep_time\":\"5 mins\"}",
                jsonobject.toString());
        assertEquals("<recipe cook_time=\"3 hours\" name=\"bread\" prep_time=\"5 mins\"><title>Basic bread</title><ingredient amount=\"8\" unit=\"dL\">Flour</ingredient><ingredient amount=\"10\" unit=\"grams\">Yeast</ingredient><ingredient amount=\"4\" unit=\"dL\" state=\"warm\">Water</ingredient><ingredient amount=\"1\" unit=\"teaspoon\">Salt</ingredient><instructions><step>Mix all ingredients together.</step><step>Knead thoroughly.</step><step>Cover with a cloth, and leave for one hour in warm room.</step><step>Knead again.</step><step>Place in a bread baking tin.</step><step>Cover with a cloth, and leave for one hour in warm room.</step><step>Bake in the oven at 180(degrees)C for 30 minutes.</step></instructions></recipe>",
                JSONML.toString(jsonobject));

        jsonarray = JSONML.toJSONArray(string);
        assertEquals("[\n    \"recipe\",\n    {\n        \"cook_time\": \"3 hours\",\n        \"name\": \"bread\",\n        \"prep_time\": \"5 mins\"\n    },\n    [\n        \"title\",\n        \"Basic bread\"\n    ],\n    [\n        \"ingredient\",\n        {\n            \"amount\": 8,\n            \"unit\": \"dL\"\n        },\n        \"Flour\"\n    ],\n    [\n        \"ingredient\",\n        {\n            \"amount\": 10,\n            \"unit\": \"grams\"\n        },\n        \"Yeast\"\n    ],\n    [\n        \"ingredient\",\n        {\n            \"amount\": 4,\n            \"unit\": \"dL\",\n            \"state\": \"warm\"\n        },\n        \"Water\"\n    ],\n    [\n        \"ingredient\",\n        {\n            \"amount\": 1,\n            \"unit\": \"teaspoon\"\n        },\n        \"Salt\"\n    ],\n    [\n        \"instructions\",\n        [\n            \"step\",\n            \"Mix all ingredients together.\"\n        ],\n        [\n            \"step\",\n            \"Knead thoroughly.\"\n        ],\n        [\n            \"step\",\n            \"Cover with a cloth, and leave for one hour in warm room.\"\n        ],\n        [\n            \"step\",\n            \"Knead again.\"\n        ],\n        [\n            \"step\",\n            \"Place in a bread baking tin.\"\n        ],\n        [\n            \"step\",\n            \"Cover with a cloth, and leave for one hour in warm room.\"\n        ],\n        [\n            \"step\",\n            \"Bake in the oven at 180(degrees)C for 30 minutes.\"\n        ]\n    ]\n]",
                jsonarray.toString(4));
        assertEquals("<recipe cook_time=\"3 hours\" name=\"bread\" prep_time=\"5 mins\"><title>Basic bread</title><ingredient amount=\"8\" unit=\"dL\">Flour</ingredient><ingredient amount=\"10\" unit=\"grams\">Yeast</ingredient><ingredient amount=\"4\" unit=\"dL\" state=\"warm\">Water</ingredient><ingredient amount=\"1\" unit=\"teaspoon\">Salt</ingredient><instructions><step>Mix all ingredients together.</step><step>Knead thoroughly.</step><step>Cover with a cloth, and leave for one hour in warm room.</step><step>Knead again.</step><step>Place in a bread baking tin.</step><step>Cover with a cloth, and leave for one hour in warm room.</step><step>Bake in the oven at 180(degrees)C for 30 minutes.</step></instructions></recipe>",
                JSONML.toString(jsonarray));

        string = "<div id=\"demo\" class=\"JSONML\"><p>JSONML is a transformation between <b>JSON</b> and <b>XML</b> that preserves ordering of document features.</p><p>JSONML can work with JSON arrays or JSON objects.</p><p>Three<br/>little<br/>words</p></div>";
        jsonobject = JSONML.toJSONObject(string);
        assertEquals("{\n    \"id\": \"demo\",\n    \"tagName\": \"div\",\n    \"class\": \"JSONML\",\n    \"childNodes\": [\n        {\n            \"tagName\": \"p\",\n            \"childNodes\": [\n                \"JSONML is a transformation between\",\n                {\n                    \"tagName\": \"b\",\n                    \"childNodes\": [\"JSON\"]\n                },\n                \"and\",\n                {\n                    \"tagName\": \"b\",\n                    \"childNodes\": [\"XML\"]\n                },\n                \"that preserves ordering of document features.\"\n            ]\n        },\n        {\n            \"tagName\": \"p\",\n            \"childNodes\": [\"JSONML can work with JSON arrays or JSON objects.\"]\n        },\n        {\n            \"tagName\": \"p\",\n            \"childNodes\": [\n                \"Three\",\n                {\"tagName\": \"br\"},\n                \"little\",\n                {\"tagName\": \"br\"},\n                \"words\"\n            ]\n        }\n    ]\n}",
                jsonobject.toString(4));
        assertEquals("<div id=\"demo\" class=\"JSONML\"><p>JSONML is a transformation between<b>JSON</b>and<b>XML</b>that preserves ordering of document features.</p><p>JSONML can work with JSON arrays or JSON objects.</p><p>Three<br/>little<br/>words</p></div>",
                JSONML.toString(jsonobject));

        jsonarray = JSONML.toJSONArray(string);
        assertEquals("[\n    \"div\",\n    {\n        \"id\": \"demo\",\n        \"class\": \"JSONML\"\n    },\n    [\n        \"p\",\n        \"JSONML is a transformation between\",\n        [\n            \"b\",\n            \"JSON\"\n        ],\n        \"and\",\n        [\n            \"b\",\n            \"XML\"\n        ],\n        \"that preserves ordering of document features.\"\n    ],\n    [\n        \"p\",\n        \"JSONML can work with JSON arrays or JSON objects.\"\n    ],\n    [\n        \"p\",\n        \"Three\",\n        [\"br\"],\n        \"little\",\n        [\"br\"],\n        \"words\"\n    ]\n]",
                jsonarray.toString(4));
        assertEquals("<div id=\"demo\" class=\"JSONML\"><p>JSONML is a transformation between<b>JSON</b>and<b>XML</b>that preserves ordering of document features.</p><p>JSONML can work with JSON arrays or JSON objects.</p><p>Three<br/>little<br/>words</p></div>",
                JSONML.toString(jsonarray));

        string = "{\"xmlns:soap\":\"http://www.w3.org/2003/05/soap-envelope\",\"tagName\":\"soap:Envelope\",\"childNodes\":[{\"tagName\":\"soap:Header\"},{\"tagName\":\"soap:Body\",\"childNodes\":[{\"tagName\":\"ws:listProducts\",\"childNodes\":[{\"tagName\":\"ws:delay\",\"childNodes\":[1]}]}]}],\"xmlns:ws\":\"http://warehouse.acme.com/ws\"}";
        jsonobject = new JSONObject(string);
        assertEquals("<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ws=\"http://warehouse.acme.com/ws\"><soap:Header/><soap:Body><ws:listProducts><ws:delay>1</ws:delay></ws:listProducts></soap:Body></soap:Envelope>",
            JSONML.toString(jsonobject));

        string = "<person created=\"2006-11-11T19:23\" modified=\"2006-12-31T23:59\">\n <firstName>Robert</firstName>\n <lastName>Smith</lastName>\n <address type=\"home\">\n <street>12345 Sixth Ave</street>\n <city>Anytown</city>\n <state>CA</state>\n <postalCode>98765-4321</postalCode>\n </address>\n </person>";
        jsonobject = XML.toJSONObject(string);
        assertEquals("{\"person\": {\n    \"lastName\": \"Smith\",\n    \"address\": {\n        \"postalCode\": \"98765-4321\",\n        \"street\": \"12345 Sixth Ave\",\n        \"state\": \"CA\",\n        \"type\": \"home\",\n        \"city\": \"Anytown\"\n    },\n    \"created\": \"2006-11-11T19:23\",\n    \"firstName\": \"Robert\",\n    \"modified\": \"2006-12-31T23:59\"\n}}",
                jsonobject.toString(4));

        string = "{ \"entity\": { \"imageURL\": \"\", \"name\": \"IXXXXXXXXXXXXX\", \"id\": 12336, \"ratingCount\": null, \"averageRating\": null } }";
        jsonobject = new JSONObject(string);
        assertEquals("{\"entity\": {\n  \"id\": 12336,\n  \"averageRating\": null,\n  \"ratingCount\": null,\n  \"name\": \"IXXXXXXXXXXXXX\",\n  \"imageURL\": \"\"\n}}",
                jsonobject.toString(2));

        jsonstringer = new JSONStringer();
        string = jsonstringer
                .object()
                .key("single")
                .value("MARIE HAA'S")
                .key("Johnny")
                .value("MARIE HAA\\'S")
                .key("foo")
                .value("bar")
                .key("baz")
                .array()
                .object()
                .key("quux")
                .value("Thanks, Josh!")
                .endObject()
                .endArray()
                .key("obj keys")
                .value(JSONObject.getNames(beanie))
                .endObject()
                .toString();
        assertEquals("{\"single\":\"MARIE HAA'S\",\"Johnny\":\"MARIE HAA\\\\'S\",\"foo\":\"bar\",\"baz\":[{\"quux\":\"Thanks, Josh!\"}],\"obj keys\":[\"aString\",\"aNumber\",\"aBoolean\"]}"
                , string);

        assertEquals("{\"a\":[[[\"b\"]]]}"
                , new JSONStringer()
                .object()
                .key("a")
                .array()
                .array()
                .array()
                .value("b")
                .endArray()
                .endArray()
                .endArray()
                .endObject()
                .toString());

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
        assertEquals("[1,[null,[{\"empty-array\":[],\"answer\":42,\"null\":null,\"false\":false,\"true\":true,\"big\":1.23456789E96,\"small\":1.23456789E-80,\"empty-object\":{},\"long\":9223372036854775807},\"two\"],true],98.6,-100,{},{\"one\":1},{\"A beany object\":42}]",
                jsonstringer.toString());
        assertEquals("[\n    1,\n    [\n        null,\n        [\n            {\n                \"empty-array\": [],\n                \"empty-object\": {},\n                \"answer\": 42,\n                \"true\": true,\n                \"false\": false,\n                \"long\": 9223372036854775807,\n                \"big\": 1.23456789E96,\n                \"small\": 1.23456789E-80,\n                \"null\": null\n            },\n            \"two\"\n        ],\n        true\n    ],\n    98.6,\n    -100,\n    {},\n    {\"one\": 1},\n    {\"A beany object\": 42}\n]",
                new JSONArray(jsonstringer.toString()).toString(4));

        int ar[] = {1, 2, 3};
        JSONArray ja = new JSONArray(ar);
        assertEquals("[1,2,3]", ja.toString());
        assertEquals("<array>1</array><array>2</array><array>3</array>", XML.toString(ar));

        String sa[] = {"aString", "aNumber", "aBoolean"};
        jsonobject = new JSONObject(beanie, sa);
        jsonobject.put("Testing JSONString interface", beanie);
        assertEquals("{\n    \"aBoolean\": true,\n    \"aNumber\": 42,\n    \"aString\": \"A beany object\",\n    \"Testing JSONString interface\": {\"A beany object\":42}\n}",
            jsonobject.toString(4));

        jsonobject = new JSONObject("{slashes: '///', closetag: '</script>', backslash:'\\\\', ei: {quotes: '\"\\''},eo: {a: '\"quoted\"', b:\"don't\"}, quotes: [\"'\", '\"']}");
        assertEquals("{\n  \"quotes\": [\n    \"'\",\n    \"\\\"\"\n  ],\n  \"slashes\": \"///\",\n  \"ei\": {\"quotes\": \"\\\"'\"},\n  \"eo\": {\n    \"b\": \"don't\",\n    \"a\": \"\\\"quoted\\\"\"\n  },\n  \"closetag\": \"<\\/script>\",\n  \"backslash\": \"\\\\\"\n}",
                jsonobject.toString(2));
        assertEquals("<quotes>&apos;</quotes><quotes>&quot;</quotes><slashes>///</slashes><ei><quotes>&quot;&apos;</quotes></ei><eo><b>don&apos;t</b><a>&quot;quoted&quot;</a></eo><closetag>&lt;/script&gt;</closetag><backslash>\\</backslash>",
                XML.toString(jsonobject));

        jsonobject = new JSONObject(
                "{foo: [true, false,9876543210,    0.0, 1.00000001,  1.000000000001, 1.00000000000000001," +
                        " .00000000000000001, 2.00, 0.1, 2e100, -32,[],{}, \"string\"], " +
                        "  to   : null, op : 'Good'," +
                        "ten:10} postfix comment");
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
        assertEquals("{\n    \"to\": null,\n    \"ten\": 10,\n    \"JSONObject\": {},\n    \"JSONArray\": [],\n    \"op\": \"Good\",\n    \"keys\": [\n        \"to\",\n        \"ten\",\n        \"JSONObject\",\n        \"JSONArray\",\n        \"op\",\n        \"int\",\n        \"true\",\n        \"foo\",\n        \"zero\",\n        \"double\",\n        \"String\",\n        \"false\",\n        \"bool\",\n        \"\\\\u2028\",\n        \"\\\\u2029\",\n        \"null\"\n    ],\n    \"int\": 57,\n    \"true\": true,\n    \"foo\": [\n        true,\n        false,\n        9876543210,\n        0,\n        1.00000001,\n        1.000000000001,\n        1,\n        1.0E-17,\n        2,\n        0.1,\n        2.0E100,\n        -32,\n        [],\n        {},\n        \"string\",\n        666,\n        2001.99,\n        \"so \\\"fine\\\".\",\n        \"so <fine>.\",\n        true,\n        false,\n        [],\n        {}\n    ],\n    \"zero\": -0,\n    \"double\": 1.2345678901234568E29,\n    \"String\": \"98.6\",\n    \"false\": false,\n    \"bool\": \"true\",\n    \"\\\\u2028\": \"\\u2028\",\n    \"\\\\u2029\": \"\\u2029\",\n    \"null\": null\n}",
                jsonobject.toString(4));
        assertEquals("<to>null</to><ten>10</ten><JSONObject></JSONObject><op>Good</op><keys>to</keys><keys>ten</keys><keys>JSONObject</keys><keys>JSONArray</keys><keys>op</keys><keys>int</keys><keys>true</keys><keys>foo</keys><keys>zero</keys><keys>double</keys><keys>String</keys><keys>false</keys><keys>bool</keys><keys>\\u2028</keys><keys>\\u2029</keys><keys>null</keys><int>57</int><true>true</true><foo>true</foo><foo>false</foo><foo>9876543210</foo><foo>0.0</foo><foo>1.00000001</foo><foo>1.000000000001</foo><foo>1.0</foo><foo>1.0E-17</foo><foo>2.0</foo><foo>0.1</foo><foo>2.0E100</foo><foo>-32</foo><foo></foo><foo></foo><foo>string</foo><foo>666</foo><foo>2001.99</foo><foo>so &quot;fine&quot;.</foo><foo>so &lt;fine&gt;.</foo><foo>true</foo><foo>false</foo><foo></foo><foo></foo><zero>-0.0</zero><double>1.2345678901234568E29</double><String>98.6</String><false>false</false><bool>true</bool><\\u2028>\u2028</\\u2028><\\u2029>\u2029</\\u2029><null>null</null>",
                 XML.toString(jsonobject));
        assertEquals(98.6d, jsonobject.getDouble("String"), eps);
        assertTrue(jsonobject.getBoolean("bool"));
        assertEquals("[true,false,9876543210,0,1.00000001,1.000000000001,1,1.0E-17,2,0.1,2.0E100,-32,[],{},\"string\",666,2001.99,\"so \\\"fine\\\".\",\"so <fine>.\",true,false,[],{}]",
                jsonobject.getJSONArray("foo").toString());
        assertEquals("Good", jsonobject.getString("op"));
        assertEquals(10, jsonobject.getInt("ten"));
        assertFalse(jsonobject.optBoolean("oops"));

        string = "<xml one = 1 two=' \"2\" '><five></five>First \u0009&lt;content&gt;<five></five> This is \"content\". <three>  3  </three>JSON does not preserve the sequencing of elements and contents.<three>  III  </three>  <three>  T H R E E</three><four/>Content text is an implied structure in XML. <six content=\"6\"/>JSON does not have implied structure:<seven>7</seven>everything is explicit.<![CDATA[CDATA blocks<are><supported>!]]></xml>";
        jsonobject = XML.toJSONObject(string);
        assertEquals("{\"xml\": {\n  \"content\": [\n    \"First \\t<content>\",\n    \"This is \\\"content\\\".\",\n    \"JSON does not preserve the sequencing of elements and contents.\",\n    \"Content text is an implied structure in XML.\",\n    \"JSON does not have implied structure:\",\n    \"everything is explicit.\",\n    \"CDATA blocks<are><supported>!\"\n  ],\n  \"two\": \" \\\"2\\\" \",\n  \"seven\": 7,\n  \"five\": [\n    \"\",\n    \"\"\n  ],\n  \"one\": 1,\n  \"three\": [\n    3,\n    \"III\",\n    \"T H R E E\"\n  ],\n  \"four\": \"\",\n  \"six\": {\"content\": 6}\n}}",
                jsonobject.toString(2));
        assertEquals("<xml>First \t&lt;content&gt;\n" +
                "This is &quot;content&quot;.\n" +
                "JSON does not preserve the sequencing of elements and contents.\n" +
                "Content text is an implied structure in XML.\n" +
                "JSON does not have implied structure:\n" +
                "everything is explicit.\n" +
                "CDATA blocks&lt;are&gt;&lt;supported&gt;!<two> &quot;2&quot; </two><seven>7</seven><five/><five/><one>1</one><three>3</three><three>III</three><three>T H R E E</three><four/><six>6</six></xml>",
                XML.toString(jsonobject));

        ja = JSONML.toJSONArray(string);
        assertEquals("[\n    \"xml\",\n    {\n        \"two\": \" \\\"2\\\" \",\n        \"one\": 1\n    },\n    [\"five\"],\n    \"First \\t<content>\",\n    [\"five\"],\n    \"This is \\\"content\\\".\",\n    [\n        \"three\",\n        3\n    ],\n    \"JSON does not preserve the sequencing of elements and contents.\",\n    [\n        \"three\",\n        \"III\"\n    ],\n    [\n        \"three\",\n        \"T H R E E\"\n    ],\n    [\"four\"],\n    \"Content text is an implied structure in XML.\",\n    [\n        \"six\",\n        {\"content\": 6}\n    ],\n    \"JSON does not have implied structure:\",\n    [\n        \"seven\",\n        7\n    ],\n    \"everything is explicit.\",\n    \"CDATA blocks<are><supported>!\"\n]",
                ja.toString(4));
        assertEquals("<xml two=\" &quot;2&quot; \" one=\"1\"><five/>First \t&lt;content&gt;<five/>This is &quot;content&quot;.<three></three>JSON does not preserve the sequencing of elements and contents.<three>III</three><three>T H R E E</three><four/>Content text is an implied structure in XML.<six content=\"6\"/>JSON does not have implied structure:<seven></seven>everything is explicit.CDATA blocks&lt;are&gt;&lt;supported&gt;!</xml>",
                JSONML.toString(ja));

        string = "<xml do='0'>uno<a re='1' mi='2'>dos<b fa='3'/>tres<c>true</c>quatro</a>cinqo<d>seis<e/></d></xml>";
        ja = JSONML.toJSONArray(string);
        assertEquals("[\n    \"xml\",\n    {\"do\": 0},\n    \"uno\",\n    [\n        \"a\",\n        {\n            \"re\": 1,\n            \"mi\": 2\n        },\n        \"dos\",\n        [\n            \"b\",\n            {\"fa\": 3}\n        ],\n        \"tres\",\n        [\n            \"c\",\n            true\n        ],\n        \"quatro\"\n    ],\n    \"cinqo\",\n    [\n        \"d\",\n        \"seis\",\n        [\"e\"]\n    ]\n]",
                ja.toString(4));
        assertEquals("<xml do=\"0\">uno<a re=\"1\" mi=\"2\">dos<b fa=\"3\"/>tres<c></c>quatro</a>cinqo<d>seis<e/></d></xml>",
                JSONML.toString(ja));

        string = "<mapping><empty/>   <class name = \"Customer\">      <field name = \"ID\" type = \"string\">         <bind-xml name=\"ID\" node=\"attribute\"/>      </field>      <field name = \"FirstName\" type = \"FirstName\"/>      <field name = \"MI\" type = \"MI\"/>      <field name = \"LastName\" type = \"LastName\"/>   </class>   <class name = \"FirstName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"MI\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"LastName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class></mapping>";
        jsonobject = XML.toJSONObject(string);

        assertEquals("{\"mapping\": {\n  \"empty\": \"\",\n  \"class\": [\n    {\n      \"field\": [\n        {\n          \"bind-xml\": {\n            \"node\": \"attribute\",\n            \"name\": \"ID\"\n          },\n          \"name\": \"ID\",\n          \"type\": \"string\"\n        },\n        {\n          \"name\": \"FirstName\",\n          \"type\": \"FirstName\"\n        },\n        {\n          \"name\": \"MI\",\n          \"type\": \"MI\"\n        },\n        {\n          \"name\": \"LastName\",\n          \"type\": \"LastName\"\n        }\n      ],\n      \"name\": \"Customer\"\n    },\n    {\n      \"field\": {\n        \"bind-xml\": {\n          \"node\": \"text\",\n          \"name\": \"text\"\n        },\n        \"name\": \"text\"\n      },\n      \"name\": \"FirstName\"\n    },\n    {\n      \"field\": {\n        \"bind-xml\": {\n          \"node\": \"text\",\n          \"name\": \"text\"\n        },\n        \"name\": \"text\"\n      },\n      \"name\": \"MI\"\n    },\n    {\n      \"field\": {\n        \"bind-xml\": {\n          \"node\": \"text\",\n          \"name\": \"text\"\n        },\n        \"name\": \"text\"\n      },\n      \"name\": \"LastName\"\n    }\n  ]\n}}",
                jsonobject.toString(2));
        assertEquals("<mapping><empty/><class><field><bind-xml><node>attribute</node><name>ID</name></bind-xml><name>ID</name><type>string</type></field><field><name>FirstName</name><type>FirstName</type></field><field><name>MI</name><type>MI</type></field><field><name>LastName</name><type>LastName</type></field><name>Customer</name></class><class><field><bind-xml><node>text</node><name>text</name></bind-xml><name>text</name></field><name>FirstName</name></class><class><field><bind-xml><node>text</node><name>text</name></bind-xml><name>text</name></field><name>MI</name></class><class><field><bind-xml><node>text</node><name>text</name></bind-xml><name>text</name></field><name>LastName</name></class></mapping>",
                XML.toString(jsonobject));
        ja = JSONML.toJSONArray(string);
        assertEquals("[\n    \"mapping\",\n    [\"empty\"],\n    [\n        \"class\",\n        {\"name\": \"Customer\"},\n        [\n            \"field\",\n            {\n                \"name\": \"ID\",\n                \"type\": \"string\"\n            },\n            [\n                \"bind-xml\",\n                {\n                    \"node\": \"attribute\",\n                    \"name\": \"ID\"\n                }\n            ]\n        ],\n        [\n            \"field\",\n            {\n                \"name\": \"FirstName\",\n                \"type\": \"FirstName\"\n            }\n        ],\n        [\n            \"field\",\n            {\n                \"name\": \"MI\",\n                \"type\": \"MI\"\n            }\n        ],\n        [\n            \"field\",\n            {\n                \"name\": \"LastName\",\n                \"type\": \"LastName\"\n            }\n        ]\n    ],\n    [\n        \"class\",\n        {\"name\": \"FirstName\"},\n        [\n            \"field\",\n            {\"name\": \"text\"},\n            [\n                \"bind-xml\",\n                {\n                    \"node\": \"text\",\n                    \"name\": \"text\"\n                }\n            ]\n        ]\n    ],\n    [\n        \"class\",\n        {\"name\": \"MI\"},\n        [\n            \"field\",\n            {\"name\": \"text\"},\n            [\n                \"bind-xml\",\n                {\n                    \"node\": \"text\",\n                    \"name\": \"text\"\n                }\n            ]\n        ]\n    ],\n    [\n        \"class\",\n        {\"name\": \"LastName\"},\n        [\n            \"field\",\n            {\"name\": \"text\"},\n            [\n                \"bind-xml\",\n                {\n                    \"node\": \"text\",\n                    \"name\": \"text\"\n                }\n            ]\n        ]\n    ]\n]",
                ja.toString(4));
        assertEquals("<mapping><empty/><class name=\"Customer\"><field name=\"ID\" type=\"string\"><bind-xml node=\"attribute\" name=\"ID\"/></field><field name=\"FirstName\" type=\"FirstName\"/><field name=\"MI\" type=\"MI\"/><field name=\"LastName\" type=\"LastName\"/></class><class name=\"FirstName\"><field name=\"text\"><bind-xml node=\"text\" name=\"text\"/></field></class><class name=\"MI\"><field name=\"text\"><bind-xml node=\"text\" name=\"text\"/></field></class><class name=\"LastName\"><field name=\"text\"><bind-xml node=\"text\" name=\"text\"/></field></class></mapping>",
                JSONML.toString(ja));

        jsonobject = XML.toJSONObject("<?xml version=\"1.0\" ?><Book Author=\"Anonymous\"><Title>Sample Book</Title><Chapter id=\"1\">This is chapter 1. It is not very long or interesting.</Chapter><Chapter id=\"2\">This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.</Chapter></Book>");
        assertEquals("{\"Book\": {\n  \"Chapter\": [\n    {\n      \"content\": \"This is chapter 1. It is not very long or interesting.\",\n      \"id\": 1\n    },\n    {\n      \"content\": \"This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.\",\n      \"id\": 2\n    }\n  ],\n  \"Author\": \"Anonymous\",\n  \"Title\": \"Sample Book\"\n}}",
                jsonobject.toString(2));
        assertEquals("<Book><Chapter>This is chapter 1. It is not very long or interesting.<id>1</id></Chapter><Chapter>This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.<id>2</id></Chapter><Author>Anonymous</Author><Title>Sample Book</Title></Book>",
                XML.toString(jsonobject));

        jsonobject = XML.toJSONObject("<!DOCTYPE bCard 'http://www.cs.caltech.edu/~adam/schemas/bCard'><bCard><?xml default bCard        firstname = ''        lastname  = '' company   = '' email = '' homepage  = ''?><bCard        firstname = 'Rohit'        lastname  = 'Khare'        company   = 'MCI'        email     = 'khare@mci.net'        homepage  = 'http://pest.w3.org/'/><bCard        firstname = 'Adam'        lastname  = 'Rifkin'        company   = 'Caltech Infospheres Project'        email     = 'adam@cs.caltech.edu'        homepage  = 'http://www.cs.caltech.edu/~adam/'/></bCard>");
        assertEquals("{\"bCard\": {\"bCard\": [\n  {\n    \"email\": \"khare@mci.net\",\n    \"company\": \"MCI\",\n    \"lastname\": \"Khare\",\n    \"firstname\": \"Rohit\",\n    \"homepage\": \"http://pest.w3.org/\"\n  },\n  {\n    \"email\": \"adam@cs.caltech.edu\",\n    \"company\": \"Caltech Infospheres Project\",\n    \"lastname\": \"Rifkin\",\n    \"firstname\": \"Adam\",\n    \"homepage\": \"http://www.cs.caltech.edu/~adam/\"\n  }\n]}}",
                jsonobject.toString(2));
        assertEquals("<bCard><bCard><email>khare@mci.net</email><company>MCI</company><lastname>Khare</lastname><firstname>Rohit</firstname><homepage>http://pest.w3.org/</homepage></bCard><bCard><email>adam@cs.caltech.edu</email><company>Caltech Infospheres Project</company><lastname>Rifkin</lastname><firstname>Adam</firstname><homepage>http://www.cs.caltech.edu/~adam/</homepage></bCard></bCard>",
                XML.toString(jsonobject));

        jsonobject = XML.toJSONObject("<?xml version=\"1.0\"?><customer>    <firstName>        <text>Fred</text>    </firstName>    <ID>fbs0001</ID>    <lastName> <text>Scerbo</text>    </lastName>    <MI>        <text>B</text>    </MI></customer>");
        assertEquals("{\"customer\": {\n  \"lastName\": {\"text\": \"Scerbo\"},\n  \"MI\": {\"text\": \"B\"},\n  \"ID\": \"fbs0001\",\n  \"firstName\": {\"text\": \"Fred\"}\n}}",
                jsonobject.toString(2));
        assertEquals("<customer><lastName><text>Scerbo</text></lastName><MI><text>B</text></MI><ID>fbs0001</ID><firstName><text>Fred</text></firstName></customer>",
                XML.toString(jsonobject));

        jsonobject = XML.toJSONObject("<!ENTITY tp-address PUBLIC '-//ABC University::Special Collections Library//TEXT (titlepage: name and address)//EN' 'tpspcoll.sgm'><list type='simple'><head>Repository Address </head><item>Special Collections Library</item><item>ABC University</item><item>Main Library, 40 Circle Drive</item><item>Ourtown, Pennsylvania</item><item>17654 USA</item></list>");
        assertEquals("{\"list\":{\"item\":[\"Special Collections Library\",\"ABC University\",\"Main Library, 40 Circle Drive\",\"Ourtown, Pennsylvania\",\"17654 USA\"],\"head\":\"Repository Address\",\"type\":\"simple\"}}",
                jsonobject.toString());
        assertEquals("<list><item>Special Collections Library</item><item>ABC University</item><item>Main Library, 40 Circle Drive</item><item>Ourtown, Pennsylvania</item><item>17654 USA</item><head>Repository Address</head><type>simple</type></list>",
                XML.toString(jsonobject));

        jsonobject = XML.toJSONObject("<test intertag zero=0 status=ok><empty/>deluxe<blip sweet=true>&amp;&quot;toot&quot;&toot;&#x41;</blip><x>eks</x><w>bonus</w><w>bonus2</w></test>");
        assertEquals("{\"test\": {\n  \"w\": [\n    \"bonus\",\n    \"bonus2\"\n  ],\n  \"content\": \"deluxe\",\n  \"intertag\": \"\",\n  \"status\": \"ok\",\n  \"blip\": {\n    \"content\": \"&\\\"toot\\\"&toot;&#x41;\",\n    \"sweet\": true\n  },\n  \"empty\": \"\",\n  \"zero\": 0,\n  \"x\": \"eks\"\n}}",
                jsonobject.toString(2));
        assertEquals("<test><w>bonus</w><w>bonus2</w>deluxe<intertag/><status>ok</status><blip>&amp;&quot;toot&quot;&amp;toot;&amp;#x41;<sweet>true</sweet></blip><empty/><zero>0</zero><x>eks</x></test>",
                XML.toString(jsonobject));

        jsonobject = HTTP.toJSONObject("GET / HTTP/1.0\nAccept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\nAccept-Language: en-us\nUser-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\nHost: www.nokko.com\nConnection: keep-alive\nAccept-encoding: gzip, deflate\n");
        assertEquals("{\n  \"Accept-Language\": \"en-us\",\n  \"Request-URI\": \"/\",\n  \"Host\": \"www.nokko.com\",\n  \"Method\": \"GET\",\n  \"Accept-encoding\": \"gzip, deflate\",\n  \"User-Agent\": \"Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\",\n  \"HTTP-Version\": \"HTTP/1.0\",\n  \"Connection\": \"keep-alive\",\n  \"Accept\": \"image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\"\n}",
                jsonobject.toString(2));
        assertEquals("GET \"/\" HTTP/1.0\r\n" +
                "Accept-Language: en-us\r\n" +
                "Host: www.nokko.com\r\n" +
                "Accept-encoding: gzip, deflate\r\n" +
                "User-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\r\n" +
                "Connection: keep-alive\r\n" +
                "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\r\n\r\n",
                HTTP.toString(jsonobject));

        jsonobject = HTTP.toJSONObject("HTTP/1.1 200 Oki Doki\nDate: Sun, 26 May 2002 17:38:52 GMT\nServer: Apache/1.3.23 (Unix) mod_perl/1.26\nKeep-Alive: timeout=15, max=100\nConnection: Keep-Alive\nTransfer-Encoding: chunked\nContent-Type: text/html\n");
        assertEquals("{\n  \"Reason-Phrase\": \"Oki Doki\",\n  \"Status-Code\": \"200\",\n  \"Transfer-Encoding\": \"chunked\",\n  \"Date\": \"Sun, 26 May 2002 17:38:52 GMT\",\n  \"Keep-Alive\": \"timeout=15, max=100\",\n  \"HTTP-Version\": \"HTTP/1.1\",\n  \"Content-Type\": \"text/html\",\n  \"Connection\": \"Keep-Alive\",\n  \"Server\": \"Apache/1.3.23 (Unix) mod_perl/1.26\"\n}",
                jsonobject.toString(2));
        assertEquals("HTTP/1.1 200 Oki Doki\r\n" +
                "Transfer-Encoding: chunked\r\n" +
                "Date: Sun, 26 May 2002 17:38:52 GMT\r\n" +
                "Keep-Alive: timeout=15, max=100\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: Keep-Alive\r\n" +
                "Server: Apache/1.3.23 (Unix) mod_perl/1.26\r\n\r\n",
                HTTP.toString(jsonobject));

        jsonobject = new JSONObject("{nix: null, nux: false, null: 'null', 'Request-URI': '/', Method: 'GET', 'HTTP-Version': 'HTTP/1.0'}");
        assertEquals("{\n  \"Request-URI\": \"/\",\n  \"nix\": null,\n  \"nux\": false,\n  \"Method\": \"GET\",\n  \"HTTP-Version\": \"HTTP/1.0\",\n  \"null\": \"null\"\n}",
                jsonobject.toString(2));
        assertTrue(jsonobject.isNull("nix"));
        assertTrue(jsonobject.has("nix"));
        assertEquals("<Request-URI>/</Request-URI><nix>null</nix><nux>false</nux><Method>GET</Method><HTTP-Version>HTTP/1.0</HTTP-Version><null>null</null>",
                XML.toString(jsonobject));

        jsonobject = XML.toJSONObject("<?xml version='1.0' encoding='UTF-8'?>" + "\n\n" + "<SOAP-ENV:Envelope" +
                " xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
                " xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\"" +
                " xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">" +
                "<SOAP-ENV:Body><ns1:doGoogleSearch" +
                " xmlns:ns1=\"urn:GoogleSearch\"" +
                " SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<key xsi:type=\"xsd:string\">GOOGLEKEY</key> <q" +
                " xsi:type=\"xsd:string\">'+search+'</q> <start" +
                " xsi:type=\"xsd:int\">0</start> <maxResults" +
                " xsi:type=\"xsd:int\">10</maxResults> <filter" +
                " xsi:type=\"xsd:boolean\">true</filter> <restrict" +
                " xsi:type=\"xsd:string\"></restrict> <safeSearch" +
                " xsi:type=\"xsd:boolean\">false</safeSearch> <lr" +
                " xsi:type=\"xsd:string\"></lr> <ie" +
                " xsi:type=\"xsd:string\">latin1</ie> <oe" +
                " xsi:type=\"xsd:string\">latin1</oe>" +
                "</ns1:doGoogleSearch>" +
                "</SOAP-ENV:Body></SOAP-ENV:Envelope>");

        assertEquals("{\"SOAP-ENV:Envelope\": {\n  \"SOAP-ENV:Body\": {\"ns1:doGoogleSearch\": {\n    \"oe\": {\n      \"content\": \"latin1\",\n      \"xsi:type\": \"xsd:string\"\n    },\n    \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\",\n    \"lr\": {\"xsi:type\": \"xsd:string\"},\n    \"start\": {\n      \"content\": 0,\n      \"xsi:type\": \"xsd:int\"\n    },\n    \"q\": {\n      \"content\": \"'+search+'\",\n      \"xsi:type\": \"xsd:string\"\n    },\n    \"ie\": {\n      \"content\": \"latin1\",\n      \"xsi:type\": \"xsd:string\"\n    },\n    \"safeSearch\": {\n      \"content\": false,\n      \"xsi:type\": \"xsd:boolean\"\n    },\n    \"xmlns:ns1\": \"urn:GoogleSearch\",\n    \"restrict\": {\"xsi:type\": \"xsd:string\"},\n    \"filter\": {\n      \"content\": true,\n      \"xsi:type\": \"xsd:boolean\"\n    },\n    \"maxResults\": {\n      \"content\": 10,\n      \"xsi:type\": \"xsd:int\"\n    },\n    \"key\": {\n      \"content\": \"GOOGLEKEY\",\n      \"xsi:type\": \"xsd:string\"\n    }\n  }},\n  \"xmlns:xsd\": \"http://www.w3.org/1999/XMLSchema\",\n  \"xmlns:xsi\": \"http://www.w3.org/1999/XMLSchema-instance\",\n  \"xmlns:SOAP-ENV\": \"http://schemas.xmlsoap.org/soap/envelope/\"\n}}",
                jsonobject.toString(2));

        assertEquals("<SOAP-ENV:Envelope><SOAP-ENV:Body><ns1:doGoogleSearch><oe>latin1<xsi:type>xsd:string</xsi:type></oe><SOAP-ENV:encodingStyle>http://schemas.xmlsoap.org/soap/encoding/</SOAP-ENV:encodingStyle><lr><xsi:type>xsd:string</xsi:type></lr><start>0<xsi:type>xsd:int</xsi:type></start><q>&apos;+search+&apos;<xsi:type>xsd:string</xsi:type></q><ie>latin1<xsi:type>xsd:string</xsi:type></ie><safeSearch>false<xsi:type>xsd:boolean</xsi:type></safeSearch><xmlns:ns1>urn:GoogleSearch</xmlns:ns1><restrict><xsi:type>xsd:string</xsi:type></restrict><filter>true<xsi:type>xsd:boolean</xsi:type></filter><maxResults>10<xsi:type>xsd:int</xsi:type></maxResults><key>GOOGLEKEY<xsi:type>xsd:string</xsi:type></key></ns1:doGoogleSearch></SOAP-ENV:Body><xmlns:xsd>http://www.w3.org/1999/XMLSchema</xmlns:xsd><xmlns:xsi>http://www.w3.org/1999/XMLSchema-instance</xmlns:xsi><xmlns:SOAP-ENV>http://schemas.xmlsoap.org/soap/envelope/</xmlns:SOAP-ENV></SOAP-ENV:Envelope>",
                XML.toString(jsonobject));

        jsonobject = new JSONObject("{Envelope: {Body: {\"ns1:doGoogleSearch\": {oe: \"latin1\", filter: true, q: \"'+search+'\", key: \"GOOGLEKEY\", maxResults: 10, \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\", start: 0, ie: \"latin1\", safeSearch:false, \"xmlns:ns1\": \"urn:GoogleSearch\"}}}}");
        assertEquals("{\"Envelope\": {\"Body\": {\"ns1:doGoogleSearch\": {\n  \"oe\": \"latin1\",\n  \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\",\n  \"start\": 0,\n  \"q\": \"'+search+'\",\n  \"ie\": \"latin1\",\n  \"safeSearch\": false,\n  \"xmlns:ns1\": \"urn:GoogleSearch\",\n  \"maxResults\": 10,\n  \"key\": \"GOOGLEKEY\",\n  \"filter\": true\n}}}}",
                jsonobject.toString(2));
        assertEquals("<Envelope><Body><ns1:doGoogleSearch><oe>latin1</oe><SOAP-ENV:encodingStyle>http://schemas.xmlsoap.org/soap/encoding/</SOAP-ENV:encodingStyle><start>0</start><q>&apos;+search+&apos;</q><ie>latin1</ie><safeSearch>false</safeSearch><xmlns:ns1>urn:GoogleSearch</xmlns:ns1><maxResults>10</maxResults><key>GOOGLEKEY</key><filter>true</filter></ns1:doGoogleSearch></Body></Envelope>",
                XML.toString(jsonobject));

        jsonobject = CookieList.toJSONObject("  f%oo = b+l=ah  ; o;n%40e = t.wo ");
        assertEquals("{\n  \"o;n@e\": \"t.wo\",\n  \"f%oo\": \"b l=ah\"\n}",
                jsonobject.toString(2));
        assertEquals("o%3bn@e=t.wo;f%25oo=b l%3dah",
                CookieList.toString(jsonobject));

        jsonobject = Cookie.toJSONObject("f%oo=blah; secure ;expires = April 24, 2002");
        assertEquals("{\n" +
                "  \"expires\": \"April 24, 2002\",\n" +
                "  \"name\": \"f%oo\",\n" +
                "  \"secure\": true,\n" +
                "  \"value\": \"blah\"\n" +
                "}", jsonobject.toString(2));
        assertEquals("f%25oo=blah;expires=April 24, 2002;secure",
                Cookie.toString(jsonobject));

        jsonobject = new JSONObject("{script: 'It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers</script>so we insert a backslash before the /'}");
        assertEquals("{\"script\":\"It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers<\\/script>so we insert a backslash before the /\"}",
                jsonobject.toString());

        JSONTokener jsontokener = new JSONTokener("{op:'test', to:'session', pre:1}{op:'test', to:'session', pre:2}");
        jsonobject = new JSONObject(jsontokener);
        assertEquals("{\"to\":\"session\",\"op\":\"test\",\"pre\":1}",
                jsonobject.toString());
        assertEquals(1, jsonobject.optInt("pre"));
        int i = jsontokener.skipTo('{');
        assertEquals(123, i);
        jsonobject = new JSONObject(jsontokener);
        assertEquals("{\"to\":\"session\",\"op\":\"test\",\"pre\":2}",
                jsonobject.toString());

        jsonarray = CDL.toJSONArray("Comma delimited list test, '\"Strip\"Quotes', 'quote, comma', No quotes, 'Single Quotes', \"Double Quotes\"\n1,'2',\"3\"\n,'It is \"good,\"', \"It works.\"\n\n");

        string = CDL.toString(jsonarray);
        assertEquals("\"quote, comma\",\"StripQuotes\",Comma delimited list test\n" +
                "3,2,1\n" +
                "It works.,\"It is good,\",\n",
                string);
        assertEquals("[\n {\n  \"quote, comma\": \"3\",\n  \"\\\"Strip\\\"Quotes\": \"2\",\n  \"Comma delimited list test\": \"1\"\n },\n {\n  \"quote, comma\": \"It works.\",\n  \"\\\"Strip\\\"Quotes\": \"It is \\\"good,\\\"\",\n  \"Comma delimited list test\": \"\"\n }\n]",
                jsonarray.toString(1));
        jsonarray = CDL.toJSONArray(string);
        assertEquals("[\n {\n  \"quote, comma\": \"3\",\n  \"StripQuotes\": \"2\",\n  \"Comma delimited list test\": \"1\"\n },\n {\n  \"quote, comma\": \"It works.\",\n  \"StripQuotes\": \"It is good,\",\n  \"Comma delimited list test\": \"\"\n }\n]",
                jsonarray.toString(1));

        jsonarray = new JSONArray(" [\"<escape>\", next is an implied null , , ok,] ");
        assertEquals("[\"<escape>\",\"next is an implied null\",null,\"ok\"]",
                jsonarray.toString());
        assertEquals("<array>&lt;escape&gt;</array><array>next is an implied null</array><array>null</array><array>ok</array>",
                XML.toString(jsonarray));

        jsonobject = new JSONObject("{ fun => with non-standard forms ; forgiving => This package can be used to parse formats that are similar to but not stricting conforming to JSON; why=To make it easier to migrate existing data to JSON,one = [[1.00]]; uno=[[{1=>1}]];'+':+6e66 ;pluses=+++;empty = '' , 'double':0.666,true: TRUE, false: FALSE, null=NULL;[true] = [[!,@;*]]; string=>  o. k. ; \r oct=0666; hex=0x666; dec=666; o=0999; noh=0x0x}");
        assertEquals("{\n \"noh\": \"0x0x\",\n \"one\": [[1]],\n \"o\": 999,\n \"+\": 6.0E66,\n \"true\": true,\n \"forgiving\": \"This package can be used to parse formats that are similar to but not stricting conforming to JSON\",\n \"fun\": \"with non-standard forms\",\n \"double\": 0.666,\n \"uno\": [[{\"1\": 1}]],\n \"dec\": 666,\n \"oct\": 666,\n \"hex\": \"0x666\",\n \"string\": \"o. k.\",\n \"empty\": \"\",\n \"false\": false,\n \"[true]\": [[\n  \"!\",\n  \"@\",\n  \"*\"\n ]],\n \"pluses\": \"+++\",\n \"why\": \"To make it easier to migrate existing data to JSON\",\n \"null\": null\n}", jsonobject.toString(1));
        assertTrue(jsonobject.getBoolean("true"));
        assertFalse(jsonobject.getBoolean("false"));

        jsonobject = new JSONObject(jsonobject, new String[]{"dec", "oct", "hex", "missing"});
        assertEquals("{\n \"oct\": 666,\n \"dec\": 666,\n \"hex\": \"0x666\"\n}", jsonobject.toString(1));

        assertEquals("[[\"<escape>\",\"next is an implied null\",null,\"ok\"],{\"oct\":666,\"dec\":666,\"hex\":\"0x666\"}]",
                new JSONStringer().array().value(jsonarray).value(jsonobject).endArray().toString());

        jsonobject = new JSONObject("{string: \"98.6\", long: 2147483648, int: 2147483647, longer: 9223372036854775807, double: 9223372036854775808}");
        assertEquals("{\n \"int\": 2147483647,\n \"string\": \"98.6\",\n \"longer\": 9223372036854775807,\n \"double\": \"9223372036854775808\",\n \"long\": 2147483648\n}",
                jsonobject.toString(1));

        // getInt
        assertEquals(2147483647, jsonobject.getInt("int"));
        assertEquals(-2147483648, jsonobject.getInt("long"));
        assertEquals(-1, jsonobject.getInt("longer"));
        try {
            jsonobject.getInt("double");
            fail("should fail with - JSONObject[\"double\"] is not an int.");
        } catch (JSONException expected) {
        }
        try {
            jsonobject.getInt("string");
            fail("should fail with - JSONObject[\"string\"] is not an int.");
        } catch (JSONException expected) {
        }

        // getLong
        assertEquals(2147483647, jsonobject.getLong("int"));
        assertEquals(2147483648l, jsonobject.getLong("long"));
        assertEquals(9223372036854775807l, jsonobject.getLong("longer"));
        try {
            jsonobject.getLong("double");
            fail("should fail with - JSONObject[\"double\"] is not a long.");
        } catch (JSONException expected) {
        }
        try {
            jsonobject.getLong("string");
            fail("should fail with - JSONObject[\"string\"] is not a long.");
        } catch (JSONException expected) {
        }

        // getDouble
        assertEquals(2.147483647E9, jsonobject.getDouble("int"), eps);
        assertEquals(2.147483648E9, jsonobject.getDouble("long"), eps);
        assertEquals(9.223372036854776E18, jsonobject.getDouble("longer"), eps);
        assertEquals(9223372036854775808d, jsonobject.getDouble("double"), eps);
        assertEquals(98.6, jsonobject.getDouble("string"), eps);

        jsonobject.put("good sized", 9223372036854775807L);
        assertEquals("{\n \"int\": 2147483647,\n \"string\": \"98.6\",\n \"longer\": 9223372036854775807,\n \"good sized\": 9223372036854775807,\n \"double\": \"9223372036854775808\",\n \"long\": 2147483648\n}",
                jsonobject.toString(1));

        jsonarray = new JSONArray("[2147483647, 2147483648, 9223372036854775807, 9223372036854775808]");
        assertEquals("[\n 2147483647,\n 2147483648,\n 9223372036854775807,\n \"9223372036854775808\"\n]",
                jsonarray.toString(1));

        List expectedKeys = new ArrayList(6);
        expectedKeys.add("int");
        expectedKeys.add("string");
        expectedKeys.add("longer");
        expectedKeys.add("good sized");
        expectedKeys.add("double");
        expectedKeys.add("long");

        iterator = jsonobject.keys();
        while (iterator.hasNext()) {
            string = (String) iterator.next();
            assertTrue(expectedKeys.remove(string));
        }
        assertEquals(0, expectedKeys.size());


        // accumulate
        jsonobject = new JSONObject();
        jsonobject.accumulate("stooge", "Curly");
        jsonobject.accumulate("stooge", "Larry");
        jsonobject.accumulate("stooge", "Moe");
        jsonarray = jsonobject.getJSONArray("stooge");
        jsonarray.put(5, "Shemp");
        assertEquals("{\"stooge\": [\n" +
                "    \"Curly\",\n" +
                "    \"Larry\",\n" +
                "    \"Moe\",\n" +
                "    null,\n" +
                "    null,\n" +
                "    \"Shemp\"\n" +
                "]}", jsonobject.toString(4));

        // write
        assertEquals("{\"stooge\":[\"Curly\",\"Larry\",\"Moe\",null,null,\"Shemp\"]}",
                jsonobject.write(new StringWriter()).toString());

        string = "<xml empty><a></a><a>1</a><a>22</a><a>333</a></xml>";
        jsonobject = XML.toJSONObject(string);
        assertEquals("{\"xml\": {\n" +
                "    \"a\": [\n" +
                "        \"\",\n" +
                "        1,\n" +
                "        22,\n" +
                "        333\n" +
                "    ],\n" +
                "    \"empty\": \"\"\n" +
                "}}", jsonobject.toString(4));
        assertEquals("<xml><a/><a>1</a><a>22</a><a>333</a><empty/></xml>",
                XML.toString(jsonobject));

        string = "<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter      <chapter>Content of the first subchapter</chapter>      <chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>";
        jsonobject = XML.toJSONObject(string);
        assertEquals("{\"book\": {\"chapter\": [\n \"Content of the first chapter\",\n {\n  \"content\": \"Content of the second chapter\",\n  \"chapter\": [\n   \"Content of the first subchapter\",\n   \"Content of the second subchapter\"\n  ]\n },\n \"Third Chapter\"\n]}}", jsonobject.toString(1));
        assertEquals("<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter<chapter>Content of the first subchapter</chapter><chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>",
                XML.toString(jsonobject));

        jsonarray = JSONML.toJSONArray(string);
        assertEquals("[\n" +
                "    \"book\",\n" +
                "    [\n" +
                "        \"chapter\",\n" +
                "        \"Content of the first chapter\"\n" +
                "    ],\n" +
                "    [\n" +
                "        \"chapter\",\n" +
                "        \"Content of the second chapter\",\n" +
                "        [\n" +
                "            \"chapter\",\n" +
                "            \"Content of the first subchapter\"\n" +
                "        ],\n" +
                "        [\n" +
                "            \"chapter\",\n" +
                "            \"Content of the second subchapter\"\n" +
                "        ]\n" +
                "    ],\n" +
                "    [\n" +
                "        \"chapter\",\n" +
                "        \"Third Chapter\"\n" +
                "    ]\n" +
                "]", jsonarray.toString(4));
        assertEquals("<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter<chapter>Content of the first subchapter</chapter><chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>",
                JSONML.toString(jsonarray));

        Collection collection = null;
        Map map = null;

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
        assertEquals("{\"stooge\":[\"Joe DeRita\",\"Shemp\"],\"map\":{},\"stooges\":[\"Curly\",\"Larry\",\"Moe\"],\"collection\":[],\"stoogearray\":[[\"Curly\",\"Larry\",\"Moe\"]],\"array\":[{},[]]}", jsonobject.toString());

        string = "{plist=Apple; AnimalSmells = { pig = piggish; lamb = lambish; worm = wormy; }; AnimalSounds = { pig = oink; lamb = baa; worm = baa;  Lisa = \"Why is the worm talking like a lamb?\" } ; AnimalColors = { pig = pink; lamb = black; worm = pink; } } ";
        jsonobject = new JSONObject(string);
        assertEquals("{\"AnimalColors\":{\"worm\":\"pink\",\"lamb\":\"black\",\"pig\":\"pink\"},\"plist\":\"Apple\",\"AnimalSounds\":{\"worm\":\"baa\",\"Lisa\":\"Why is the worm talking like a lamb?\",\"lamb\":\"baa\",\"pig\":\"oink\"},\"AnimalSmells\":{\"worm\":\"wormy\",\"lamb\":\"lambish\",\"pig\":\"piggish\"}}",
                jsonobject.toString());

        string = " [\"San Francisco\", \"New York\", \"Seoul\", \"London\", \"Seattle\", \"Shanghai\"]";
        jsonarray = new JSONArray(string);
        assertEquals("[\"San Francisco\",\"New York\",\"Seoul\",\"London\",\"Seattle\",\"Shanghai\"]",
                jsonarray.toString());

        string = "<a ichi='1' ni='2'><b>The content of b</b> and <c san='3'>The content of c</c><d>do</d><e></e><d>re</d><f/><d>mi</d></a>";
        jsonobject = XML.toJSONObject(string);
        assertEquals("{\"a\":{\"f\":\"\",\"content\":\"and\",\"d\":[\"do\",\"re\",\"mi\"],\"ichi\":1,\"e\":\"\",\"b\":\"The content of b\",\"c\":{\"content\":\"The content of c\",\"san\":3},\"ni\":2}}",
                jsonobject.toString());
        assertEquals("<a><f/>and<d>do</d><d>re</d><d>mi</d><ichi>1</ichi><e/><b>The content of b</b><c>The content of c<san>3</san></c><ni>2</ni></a>",
                XML.toString(jsonobject));
        ja = JSONML.toJSONArray(string);
        assertEquals("[\n" +
                "    \"a\",\n" +
                "    {\n" +
                "        \"ichi\": 1,\n" +
                "        \"ni\": 2\n" +
                "    },\n" +
                "    [\n" +
                "        \"b\",\n" +
                "        \"The content of b\"\n" +
                "    ],\n" +
                "    \"and\",\n" +
                "    [\n" +
                "        \"c\",\n" +
                "        {\"san\": 3},\n" +
                "        \"The content of c\"\n" +
                "    ],\n" +
                "    [\n" +
                "        \"d\",\n" +
                "        \"do\"\n" +
                "    ],\n" +
                "    [\"e\"],\n" +
                "    [\n" +
                "        \"d\",\n" +
                "        \"re\"\n" +
                "    ],\n" +
                "    [\"f\"],\n" +
                "    [\n" +
                "        \"d\",\n" +
                "        \"mi\"\n" +
                "    ]\n" +
                "]", ja.toString(4));
        assertEquals("<a ichi=\"1\" ni=\"2\"><b>The content of b</b>and<c san=\"3\">The content of c</c><d>do</d><e/><d>re</d><f/><d>mi</d></a>",
                JSONML.toString(ja));

        string = "<Root><MsgType type=\"node\"><BatchType type=\"string\">111111111111111</BatchType></MsgType></Root>";
        jsonobject = JSONML.toJSONObject(string);
        assertEquals("{\"tagName\":\"Root\",\"childNodes\":[{\"tagName\":\"MsgType\",\"childNodes\":[{\"tagName\":\"BatchType\",\"childNodes\":[111111111111111],\"type\":\"string\"}],\"type\":\"node\"}]}",
                jsonobject.toString());
        ja = JSONML.toJSONArray(string);
        assertEquals("[\"Root\",[\"MsgType\",{\"type\":\"node\"},[\"BatchType\",{\"type\":\"string\"},111111111111111]]]",
                ja.toString());
    }

    public void testExceptions() throws Exception {
        JSONArray jsonarray = null;
        JSONObject jsonobject;
        String string;

        try {
            jsonarray = new JSONArray("[\n\r\n\r}");
            System.out.println(jsonarray.toString());
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Missing value at 5 [character 0 line 4]", jsone.getMessage());
        }

        try {
            jsonarray = new JSONArray("<\n\r\n\r      ");
            System.out.println(jsonarray.toString());
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("A JSONArray text must start with '[' at 1 [character 2 line 1]", jsone.getMessage());
        }

        try {
            jsonarray = new JSONArray();
            jsonarray.put(Double.NEGATIVE_INFINITY);
            jsonarray.put(Double.NaN);
            System.out.println(jsonarray.toString());
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("JSON does not allow non-finite numbers.", jsone.getMessage());
        }

        jsonobject = new JSONObject();
        try {
            System.out.println(jsonobject.getDouble("stooge"));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("JSONObject[\"stooge\"] not found.", jsone.getMessage());
        }

        try {
            System.out.println(jsonobject.getDouble("howard"));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("JSONObject[\"howard\"] not found.", jsone.getMessage());
        }

        try {
            System.out.println(jsonobject.put(null, "howard"));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Null key.", jsone.getMessage());
        }

        try {
            System.out.println(jsonarray.getDouble(0));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("JSONArray[0] not found.", jsone.getMessage());
        }

        try {
            System.out.println(jsonarray.get(-1));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("JSONArray[-1] not found.", jsone.getMessage());
        }

        try {
            System.out.println(jsonarray.put(Double.NaN));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("JSON does not allow non-finite numbers.", jsone.getMessage());
        }

        try {
            jsonobject = XML.toJSONObject("<a><b>    ");
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Unclosed tag b at 11 [character 12 line 1]", jsone.getMessage());
        }

        try {
            jsonobject = XML.toJSONObject("<a></b>    ");
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Mismatched a and b at 6 [character 7 line 1]", jsone.getMessage());
        }

        try {
            jsonobject = XML.toJSONObject("<a></a    ");
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Misshaped element at 11 [character 12 line 1]", jsone.getMessage());
        }

        try {
            jsonarray = new JSONArray(new Object());
            System.out.println(jsonarray.toString());
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("JSONArray initial value should be a string or collection or array.", jsone.getMessage());
        }

        try {
            string = "[)";
            jsonarray = new JSONArray(string);
            System.out.println(jsonarray.toString());
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Expected a ',' or ']' at 3 [character 4 line 1]", jsone.getMessage());
        }

        try {
            string = "<xml";
            jsonarray = JSONML.toJSONArray(string);
            System.out.println(jsonarray.toString(4));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Misshaped element at 6 [character 7 line 1]", jsone.getMessage());
        }

        try {
            string = "<right></wrong>";
            jsonarray = JSONML.toJSONArray(string);
            System.out.println(jsonarray.toString(4));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Mismatched 'right' and 'wrong' at 15 [character 16 line 1]", jsone.getMessage());
        }

        try {
            string = "This ain't XML.";
            jsonarray = JSONML.toJSONArray(string);
            System.out.println(jsonarray.toString(4));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Bad XML at 17 [character 18 line 1]", jsone.getMessage());
        }

        try {
            string = "{\"koda\": true, \"koda\": true}";
            jsonobject = new JSONObject(string);
            System.out.println(jsonobject.toString(4));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Duplicate key \"koda\"", jsone.getMessage());
        }

        try {
            JSONStringer jj = new JSONStringer();
            string = jj
                    .object()
                    .key("bosanda")
                    .value("MARIE HAA'S")
                    .key("bosanda")
                    .value("MARIE HAA\\'S")
                    .endObject()
                    .toString();
            System.out.println(jsonobject.toString(4));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Duplicate key \"bosanda\"", jsone.getMessage());
        }
    }

    /**
     * Beany is a typical class that implements JSONString. It also
     * provides some beany methods that can be used to
     * construct a JSONObject. It also demonstrates constructing
     * a JSONObject with an array of names.
     */
    class Beany implements JSONString {
        public String aString;
        public double aNumber;
        public boolean aBoolean;

        public Beany(String string, double d, boolean b) {
            this.aString = string;
            this.aNumber = d;
            this.aBoolean = b;
        }

        public double getNumber() {
            return this.aNumber;
        }

        public String getString() {
            return this.aString;
        }

        public boolean isBoolean() {
            return this.aBoolean;
        }

        public String getBENT() {
            return "All uppercase key";
        }

        public String getX() {
            return "x";
        }

        public String toJSONString() {
            return "{" + JSONObject.quote(this.aString) + ":" +
                    JSONObject.doubleToString(this.aNumber) + "}";
        }

        public String toString() {
            return this.getString() + " " + this.getNumber() + " " +
                    this.isBoolean() + "." + this.getBENT() + " " + this.getX();
        }
    }
}