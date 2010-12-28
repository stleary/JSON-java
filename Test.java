package org.json;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.StringWriter;

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
 * It is just a casual test tool.
 *
 * @author douglas at crockford.com
 * @author yusuke at mac.com
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
        JSONObject j;
        String s;

        j = XML.toJSONObject("<![CDATA[This is a collection of test patterns and examples for org.json.]]>  Ignore the stuff past the end.  ");
        assertEquals("{\"content\":\"This is a collection of test patterns and examples for org.json.\"}", j.toString());
        assertEquals("This is a collection of test patterns and examples for org.json.", j.getString("content"));

        s = "<test><blank></blank><empty/></test>";
        j = XML.toJSONObject(s);
        assertEquals("{\"test\": {\n  \"blank\": \"\",\n  \"empty\": \"\"\n}}", j.toString(2));
        assertEquals("<test><blank/><empty/></test>", XML.toString(j));
    }

    public void testNull() throws Exception {
        JSONObject j;

        j = new JSONObject("{\"message\":\"null\"}");
        assertFalse(j.isNull("message"));
        assertEquals("null", j.getString("message"));

        j = new JSONObject("{\"message\":null}");
        assertTrue(j.isNull("message"));
        assertEquals(null, j.getString("message"));
    }

    public void testJSON() throws Exception {
        Iterator it;
        JSONArray a;
        JSONObject j;
        JSONStringer jj;
        Object o;
        String s;

        Obj obj = new Obj("A beany object", 42, true);

        s = "[0.1]";
        a = new JSONArray(s);
        assertEquals("[0.1]", a.toString());


        j = new JSONObject();
        o = null;
        j.put("booga", o);
        j.put("wooga", JSONObject.NULL);
        assertEquals("{\"wooga\":null}", j.toString());
        assertTrue(j.isNull("booga"));

        j = new JSONObject();
        j.increment("two");
        j.increment("two");
        assertEquals("{\"two\":2}", j.toString());
        assertEquals(2, j.getInt("two"));

        s = "{     \"list of lists\" : [         [1, 2, 3],         [4, 5, 6],     ] }";
        j = new JSONObject(s);
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
                "]}", j.toString(4));
        assertEquals("<list of lists><array>1</array><array>2</array><array>3</array></list of lists><list of lists><array>4</array><array>5</array><array>6</array></list of lists>",
                XML.toString(j));

        s = "<recipe name=\"bread\" prep_time=\"5 mins\" cook_time=\"3 hours\"> <title>Basic bread</title> <ingredient amount=\"8\" unit=\"dL\">Flour</ingredient> <ingredient amount=\"10\" unit=\"grams\">Yeast</ingredient> <ingredient amount=\"4\" unit=\"dL\" state=\"warm\">Water</ingredient> <ingredient amount=\"1\" unit=\"teaspoon\">Salt</ingredient> <instructions> <step>Mix all ingredients together.</step> <step>Knead thoroughly.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Knead again.</step> <step>Place in a bread baking tin.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Bake in the oven at 180(degrees)C for 30 minutes.</step> </instructions> </recipe> ";
        j = XML.toJSONObject(s);
        assertEquals("{\"recipe\": {\n" +
                "    \"cook_time\": \"3 hours\",\n" +
                "    \"ingredient\": [\n" +
                "        {\n" +
                "            \"amount\": 8,\n" +
                "            \"content\": \"Flour\",\n" +
                "            \"unit\": \"dL\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"amount\": 10,\n" +
                "            \"content\": \"Yeast\",\n" +
                "            \"unit\": \"grams\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"amount\": 4,\n" +
                "            \"content\": \"Water\",\n" +
                "            \"state\": \"warm\",\n" +
                "            \"unit\": \"dL\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"amount\": 1,\n" +
                "            \"content\": \"Salt\",\n" +
                "            \"unit\": \"teaspoon\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"instructions\": {\"step\": [\n" +
                "        \"Mix all ingredients together.\",\n" +
                "        \"Knead thoroughly.\",\n" +
                "        \"Cover with a cloth, and leave for one hour in warm room.\",\n" +
                "        \"Knead again.\",\n" +
                "        \"Place in a bread baking tin.\",\n" +
                "        \"Cover with a cloth, and leave for one hour in warm room.\",\n" +
                "        \"Bake in the oven at 180(degrees)C for 30 minutes.\"\n" +
                "    ]},\n" +
                "    \"name\": \"bread\",\n" +
                "    \"prep_time\": \"5 mins\",\n" +
                "    \"title\": \"Basic bread\"\n" +
                "}}", j.toString(4));

        j = JSONML.toJSONObject(s);
        assertEquals("{\"cook_time\":\"3 hours\",\"name\":\"bread\",\"tagName\":\"recipe\",\"childNodes\":[{\"tagName\":\"title\",\"childNodes\":[\"Basic bread\"]},{\"amount\":8,\"unit\":\"dL\",\"tagName\":\"ingredient\",\"childNodes\":[\"Flour\"]},{\"amount\":10,\"unit\":\"grams\",\"tagName\":\"ingredient\",\"childNodes\":[\"Yeast\"]},{\"amount\":4,\"unit\":\"dL\",\"tagName\":\"ingredient\",\"state\":\"warm\",\"childNodes\":[\"Water\"]},{\"amount\":1,\"unit\":\"teaspoon\",\"tagName\":\"ingredient\",\"childNodes\":[\"Salt\"]},{\"tagName\":\"instructions\",\"childNodes\":[{\"tagName\":\"step\",\"childNodes\":[\"Mix all ingredients together.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Knead thoroughly.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Cover with a cloth, and leave for one hour in warm room.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Knead again.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Place in a bread baking tin.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Cover with a cloth, and leave for one hour in warm room.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Bake in the oven at 180(degrees)C for 30 minutes.\"]}]}],\"prep_time\":\"5 mins\"}",
                j.toString());
        assertEquals("<recipe cook_time=\"3 hours\" name=\"bread\" prep_time=\"5 mins\"><title>Basic bread</title><ingredient amount=\"8\" unit=\"dL\">Flour</ingredient><ingredient amount=\"10\" unit=\"grams\">Yeast</ingredient><ingredient amount=\"4\" unit=\"dL\" state=\"warm\">Water</ingredient><ingredient amount=\"1\" unit=\"teaspoon\">Salt</ingredient><instructions><step>Mix all ingredients together.</step><step>Knead thoroughly.</step><step>Cover with a cloth, and leave for one hour in warm room.</step><step>Knead again.</step><step>Place in a bread baking tin.</step><step>Cover with a cloth, and leave for one hour in warm room.</step><step>Bake in the oven at 180(degrees)C for 30 minutes.</step></instructions></recipe>",
                JSONML.toString(j));

        a = JSONML.toJSONArray(s);
        assertEquals("[\n" +
                "    \"recipe\",\n" +
                "    {\n" +
                "        \"cook_time\": \"3 hours\",\n" +
                "        \"name\": \"bread\",\n" +
                "        \"prep_time\": \"5 mins\"\n" +
                "    },\n" +
                "    [\n" +
                "        \"title\",\n" +
                "        \"Basic bread\"\n" +
                "    ],\n" +
                "    [\n" +
                "        \"ingredient\",\n" +
                "        {\n" +
                "            \"amount\": 8,\n" +
                "            \"unit\": \"dL\"\n" +
                "        },\n" +
                "        \"Flour\"\n" +
                "    ],\n" +
                "    [\n" +
                "        \"ingredient\",\n" +
                "        {\n" +
                "            \"amount\": 10,\n" +
                "            \"unit\": \"grams\"\n" +
                "        },\n" +
                "        \"Yeast\"\n" +
                "    ],\n" +
                "    [\n" +
                "        \"ingredient\",\n" +
                "        {\n" +
                "            \"amount\": 4,\n" +
                "            \"state\": \"warm\",\n" +
                "            \"unit\": \"dL\"\n" +
                "        },\n" +
                "        \"Water\"\n" +
                "    ],\n" +
                "    [\n" +
                "        \"ingredient\",\n" +
                "        {\n" +
                "            \"amount\": 1,\n" +
                "            \"unit\": \"teaspoon\"\n" +
                "        },\n" +
                "        \"Salt\"\n" +
                "    ],\n" +
                "    [\n" +
                "        \"instructions\",\n" +
                "        [\n" +
                "            \"step\",\n" +
                "            \"Mix all ingredients together.\"\n" +
                "        ],\n" +
                "        [\n" +
                "            \"step\",\n" +
                "            \"Knead thoroughly.\"\n" +
                "        ],\n" +
                "        [\n" +
                "            \"step\",\n" +
                "            \"Cover with a cloth, and leave for one hour in warm room.\"\n" +
                "        ],\n" +
                "        [\n" +
                "            \"step\",\n" +
                "            \"Knead again.\"\n" +
                "        ],\n" +
                "        [\n" +
                "            \"step\",\n" +
                "            \"Place in a bread baking tin.\"\n" +
                "        ],\n" +
                "        [\n" +
                "            \"step\",\n" +
                "            \"Cover with a cloth, and leave for one hour in warm room.\"\n" +
                "        ],\n" +
                "        [\n" +
                "            \"step\",\n" +
                "            \"Bake in the oven at 180(degrees)C for 30 minutes.\"\n" +
                "        ]\n" +
                "    ]\n" +
                "]", a.toString(4));
        assertEquals("<recipe cook_time=\"3 hours\" name=\"bread\" prep_time=\"5 mins\"><title>Basic bread</title><ingredient amount=\"8\" unit=\"dL\">Flour</ingredient><ingredient amount=\"10\" unit=\"grams\">Yeast</ingredient><ingredient amount=\"4\" unit=\"dL\" state=\"warm\">Water</ingredient><ingredient amount=\"1\" unit=\"teaspoon\">Salt</ingredient><instructions><step>Mix all ingredients together.</step><step>Knead thoroughly.</step><step>Cover with a cloth, and leave for one hour in warm room.</step><step>Knead again.</step><step>Place in a bread baking tin.</step><step>Cover with a cloth, and leave for one hour in warm room.</step><step>Bake in the oven at 180(degrees)C for 30 minutes.</step></instructions></recipe>",
                JSONML.toString(a));

        s = "<div id=\"demo\" class=\"JSONML\"><p>JSONML is a transformation between <b>JSON</b> and <b>XML</b> that preserves ordering of document features.</p><p>JSONML can work with JSON arrays or JSON objects.</p><p>Three<br/>little<br/>words</p></div>";
        j = JSONML.toJSONObject(s);
        assertEquals("{\n" +
                "    \"childNodes\": [\n" +
                "        {\n" +
                "            \"childNodes\": [\n" +
                "                \"JSONML is a transformation between\",\n" +
                "                {\n" +
                "                    \"childNodes\": [\"JSON\"],\n" +
                "                    \"tagName\": \"b\"\n" +
                "                },\n" +
                "                \"and\",\n" +
                "                {\n" +
                "                    \"childNodes\": [\"XML\"],\n" +
                "                    \"tagName\": \"b\"\n" +
                "                },\n" +
                "                \"that preserves ordering of document features.\"\n" +
                "            ],\n" +
                "            \"tagName\": \"p\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"childNodes\": [\"JSONML can work with JSON arrays or JSON objects.\"],\n" +
                "            \"tagName\": \"p\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"childNodes\": [\n" +
                "                \"Three\",\n" +
                "                {\"tagName\": \"br\"},\n" +
                "                \"little\",\n" +
                "                {\"tagName\": \"br\"},\n" +
                "                \"words\"\n" +
                "            ],\n" +
                "            \"tagName\": \"p\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"class\": \"JSONML\",\n" +
                "    \"id\": \"demo\",\n" +
                "    \"tagName\": \"div\"\n" +
                "}", j.toString(4));
        assertEquals("<div id=\"demo\" class=\"JSONML\"><p>JSONML is a transformation between<b>JSON</b>and<b>XML</b>that preserves ordering of document features.</p><p>JSONML can work with JSON arrays or JSON objects.</p><p>Three<br/>little<br/>words</p></div>",
                JSONML.toString(j));

        a = JSONML.toJSONArray(s);
        assertEquals("[\n" +
                "    \"div\",\n" +
                "    {\n" +
                "        \"class\": \"JSONML\",\n" +
                "        \"id\": \"demo\"\n" +
                "    },\n" +
                "    [\n" +
                "        \"p\",\n" +
                "        \"JSONML is a transformation between\",\n" +
                "        [\n" +
                "            \"b\",\n" +
                "            \"JSON\"\n" +
                "        ],\n" +
                "        \"and\",\n" +
                "        [\n" +
                "            \"b\",\n" +
                "            \"XML\"\n" +
                "        ],\n" +
                "        \"that preserves ordering of document features.\"\n" +
                "    ],\n" +
                "    [\n" +
                "        \"p\",\n" +
                "        \"JSONML can work with JSON arrays or JSON objects.\"\n" +
                "    ],\n" +
                "    [\n" +
                "        \"p\",\n" +
                "        \"Three\",\n" +
                "        [\"br\"],\n" +
                "        \"little\",\n" +
                "        [\"br\"],\n" +
                "        \"words\"\n" +
                "    ]\n" +
                "]", a.toString(4));
        assertEquals("<div id=\"demo\" class=\"JSONML\"><p>JSONML is a transformation between<b>JSON</b>and<b>XML</b>that preserves ordering of document features.</p><p>JSONML can work with JSON arrays or JSON objects.</p><p>Three<br/>little<br/>words</p></div>",
                JSONML.toString(a));

        s = "<person created=\"2006-11-11T19:23\" modified=\"2006-12-31T23:59\">\n <firstName>Robert</firstName>\n <lastName>Smith</lastName>\n <address type=\"home\">\n <street>12345 Sixth Ave</street>\n <city>Anytown</city>\n <state>CA</state>\n <postalCode>98765-4321</postalCode>\n </address>\n </person>";
        j = XML.toJSONObject(s);
        assertEquals("{\"person\": {\n" +
                "    \"address\": {\n" +
                "        \"city\": \"Anytown\",\n" +
                "        \"postalCode\": \"98765-4321\",\n" +
                "        \"state\": \"CA\",\n" +
                "        \"street\": \"12345 Sixth Ave\",\n" +
                "        \"type\": \"home\"\n" +
                "    },\n" +
                "    \"created\": \"2006-11-11T19:23\",\n" +
                "    \"firstName\": \"Robert\",\n" +
                "    \"lastName\": \"Smith\",\n" +
                "    \"modified\": \"2006-12-31T23:59\"\n" +
                "}}", j.toString(4));

        j = new JSONObject(obj);
        assertEquals("{\"string\":\"A beany object\",\"BENT\":\"All uppercase key\",\"boolean\":true,\"number\":42,\"x\":\"x\"}"
                , j.toString());

        s = "{ \"entity\": { \"imageURL\": \"\", \"name\": \"IXXXXXXXXXXXXX\", \"id\": 12336, \"ratingCount\": null, \"averageRating\": null } }";
        j = new JSONObject(s);
        assertEquals("{\"entity\": {\n" +
                "  \"averageRating\": null,\n" +
                "  \"id\": 12336,\n" +
                "  \"imageURL\": \"\",\n" +
                "  \"name\": \"IXXXXXXXXXXXXX\",\n" +
                "  \"ratingCount\": null\n" +
                "}}",
                j.toString(2));

        jj = new JSONStringer();
        s = jj
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
                .value(JSONObject.getNames(obj))
                .endObject()
                .toString();
        assertEquals("{\"single\":\"MARIE HAA'S\",\"Johnny\":\"MARIE HAA\\\\'S\",\"foo\":\"bar\",\"baz\":[{\"quux\":\"Thanks, Josh!\"}],\"obj keys\":[\"aString\",\"aNumber\",\"aBoolean\"]}"
                , s);

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

        jj = new JSONStringer();
        jj.array();
        jj.value(1);
        jj.array();
        jj.value(null);
        jj.array();
        jj.object();
        jj.key("empty-array").array().endArray();
        jj.key("answer").value(42);
        jj.key("null").value(null);
        jj.key("false").value(false);
        jj.key("true").value(true);
        jj.key("big").value(123456789e+88);
        jj.key("small").value(123456789e-88);
        jj.key("empty-object").object().endObject();
        jj.key("long");
        jj.value(9223372036854775807L);
        jj.endObject();
        jj.value("two");
        jj.endArray();
        jj.value(true);
        jj.endArray();
        jj.value(98.6);
        jj.value(-100.0);
        jj.object();
        jj.endObject();
        jj.object();
        jj.key("one");
        jj.value(1.00);
        jj.endObject();
        jj.value(obj);
        jj.endArray();
        assertEquals("[1,[null,[{\"empty-array\":[],\"answer\":42,\"null\":null,\"false\":false,\"true\":true,\"big\":1.23456789E96,\"small\":1.23456789E-80,\"empty-object\":{},\"long\":9223372036854775807},\"two\"],true],98.6,-100,{},{\"one\":1},{\"A beany object\":42}]",
                jj.toString());
        assertEquals("[\n" +
                "    1,\n" +
                "    [\n" +
                "        null,\n" +
                "        [\n" +
                "            {\n" +
                "                \"answer\": 42,\n" +
                "                \"big\": 1.23456789E96,\n" +
                "                \"empty-array\": [],\n" +
                "                \"empty-object\": {},\n" +
                "                \"false\": false,\n" +
                "                \"long\": 9223372036854775807,\n" +
                "                \"null\": null,\n" +
                "                \"small\": 1.23456789E-80,\n" +
                "                \"true\": true\n" +
                "            },\n" +
                "            \"two\"\n" +
                "        ],\n" +
                "        true\n" +
                "    ],\n" +
                "    98.6,\n" +
                "    -100,\n" +
                "    {},\n" +
                "    {\"one\": 1},\n" +
                "    {\"A beany object\": 42}\n" +
                "]", new JSONArray(jj.toString()).toString(4));

        int ar[] = {1, 2, 3};
        JSONArray ja = new JSONArray(ar);
        assertEquals("[1,2,3]", ja.toString());

        String sa[] = {"aString", "aNumber", "aBoolean"};
        j = new JSONObject(obj, sa);
        j.put("Testing JSONString interface", obj);
        assertEquals("{\n" +
                "    \"Testing JSONString interface\": {\"A beany object\":42},\n" +
                "    \"aBoolean\": true,\n" +
                "    \"aNumber\": 42,\n" +
                "    \"aString\": \"A beany object\"\n" +
                "}", j.toString(4));

        j = new JSONObject("{slashes: '///', closetag: '</script>', backslash:'\\\\', ei: {quotes: '\"\\''},eo: {a: '\"quoted\"', b:\"don't\"}, quotes: [\"'\", '\"']}");
        assertEquals("{\n" +
                "  \"backslash\": \"\\\\\",\n" +
                "  \"closetag\": \"<\\/script>\",\n" +
                "  \"ei\": {\"quotes\": \"\\\"'\"},\n" +
                "  \"eo\": {\n" +
                "    \"a\": \"\\\"quoted\\\"\",\n" +
                "    \"b\": \"don't\"\n" +
                "  },\n" +
                "  \"quotes\": [\n" +
                "    \"'\",\n" +
                "    \"\\\"\"\n" +
                "  ],\n" +
                "  \"slashes\": \"///\"\n" +
                "}", j.toString(2));
        assertEquals("<quotes>'</quotes><quotes>&quot;</quotes><slashes>///</slashes><ei><quotes>&quot;'</quotes></ei><eo><b>don't</b><a>&quot;quoted&quot;</a></eo><closetag>&lt;/script&gt;</closetag><backslash>\\</backslash>",
                XML.toString(j));

        j = new JSONObject(
                "{foo: [true, false,9876543210,    0.0, 1.00000001,  1.000000000001, 1.00000000000000001," +
                        " .00000000000000001, 2.00, 0.1, 2e100, -32,[],{}, \"string\"], " +
                        "  to   : null, op : 'Good'," +
                        "ten:10} postfix comment");
        j.put("String", "98.6");
        j.put("JSONObject", new JSONObject());
        j.put("JSONArray", new JSONArray());
        j.put("int", 57);
        j.put("double", 123456789012345678901234567890.);
        j.put("true", true);
        j.put("false", false);
        j.put("null", JSONObject.NULL);
        j.put("bool", "true");
        j.put("zero", -0.0);
        j.put("\\u2028", "\u2028");
        j.put("\\u2029", "\u2029");
        a = j.getJSONArray("foo");
        a.put(666);
        a.put(2001.99);
        a.put("so \"fine\".");
        a.put("so <fine>.");
        a.put(true);
        a.put(false);
        a.put(new JSONArray());
        a.put(new JSONObject());
        j.put("keys", JSONObject.getNames(j));
        assertEquals("{\n" +
                "    \"JSONArray\": [],\n" +
                "    \"JSONObject\": {},\n" +
                "    \"String\": \"98.6\",\n" +
                "    \"\\\\u2028\": \"\\u2028\",\n" +
                "    \"\\\\u2029\": \"\\u2029\",\n" +
                "    \"bool\": \"true\",\n" +
                "    \"double\": 1.2345678901234568E29,\n" +
                "    \"false\": false,\n" +
                "    \"foo\": [\n" +
                "        true,\n" +
                "        false,\n" +
                "        9876543210,\n" +
                "        0,\n" +
                "        1.00000001,\n" +
                "        1.000000000001,\n" +
                "        1,\n" +
                "        1.0E-17,\n" +
                "        2,\n" +
                "        0.1,\n" +
                "        2.0E100,\n" +
                "        -32,\n" +
                "        [],\n" +
                "        {},\n" +
                "        \"string\",\n" +
                "        666,\n" +
                "        2001.99,\n" +
                "        \"so \\\"fine\\\".\",\n" +
                "        \"so <fine>.\",\n" +
                "        true,\n" +
                "        false,\n" +
                "        [],\n" +
                "        {}\n" +
                "    ],\n" +
                "    \"int\": 57,\n" +
                "    \"keys\": [\n" +
                "        \"to\",\n" +
                "        \"ten\",\n" +
                "        \"JSONObject\",\n" +
                "        \"JSONArray\",\n" +
                "        \"op\",\n" +
                "        \"int\",\n" +
                "        \"true\",\n" +
                "        \"foo\",\n" +
                "        \"zero\",\n" +
                "        \"double\",\n" +
                "        \"String\",\n" +
                "        \"false\",\n" +
                "        \"bool\",\n" +
                "        \"\\\\u2028\",\n" +
                "        \"\\\\u2029\",\n" +
                "        \"null\"\n" +
                "    ],\n" +
                "    \"null\": null,\n" +
                "    \"op\": \"Good\",\n" +
                "    \"ten\": 10,\n" +
                "    \"to\": null,\n" +
                "    \"true\": true,\n" +
                "    \"zero\": -0\n" +
                "}", j.toString(4));
//        assertEquals("<to>null</to><ten>10</ten><JSONObject></JSONObject><op>Good</op><keys>[Ljava.lang.String;@4d125127</keys><int>57</int><true>true</true><foo>true</foo><foo>false</foo><foo>9876543210</foo><foo>0.0</foo><foo>1.00000001</foo><foo>1.000000000001</foo><foo>1.0</foo><foo>1.0E-17</foo><foo>2.0</foo><foo>0.1</foo><foo>2.0E100</foo><foo>-32</foo><foo></foo><foo></foo><foo>string</foo><foo>666</foo><foo>2001.99</foo><foo>so &quot;fine&quot;.</foo><foo>so &lt;fine&gt;.</foo><foo>true</foo><foo>false</foo><foo></foo><foo></foo><zero>-0.0</zero><double>1.2345678901234568E29</double><String>98.6</String><false>false</false><bool>true</bool><\\u2028> </\\u2028><\\u2029> </\\u2029><null>null</null>",
//                 XML.toString(j));
        assertEquals(98.6d, j.getDouble("String"));
        assertTrue(j.getBoolean("bool"));
        assertEquals("null", j.getString("to"));
        assertEquals("true", j.getString("true"));
        assertEquals("[true,false,9876543210,0,1.00000001,1.000000000001,1,1.0E-17,2,0.1,2.0E100,-32,[],{},\"string\",666,2001.99,\"so \\\"fine\\\".\",\"so <fine>.\",true,false,[],{}]",
                j.getJSONArray("foo").toString());
        assertEquals("Good", j.getString("op"));
        assertEquals(10, j.getInt("ten"));
        assertFalse(j.optBoolean("oops"));

        s = "<xml one = 1 two=' \"2\" '><five></five>First \u0009&lt;content&gt;<five></five> This is \"content\". <three>  3  </three>JSON does not preserve the sequencing of elements and contents.<three>  III  </three>  <three>  T H R E E</three><four/>Content text is an implied structure in XML. <six content=\"6\"/>JSON does not have implied structure:<seven>7</seven>everything is explicit.<![CDATA[CDATA blocks<are><supported>!]]></xml>";
        j = XML.toJSONObject(s);
        assertEquals("{\"xml\": {\n" +
                "  \"content\": [\n" +
                "    \"First \\t<content>\",\n" +
                "    \"This is \\\"content\\\".\",\n" +
                "    \"JSON does not preserve the sequencing of elements and contents.\",\n" +
                "    \"Content text is an implied structure in XML.\",\n" +
                "    \"JSON does not have implied structure:\",\n" +
                "    \"everything is explicit.\",\n" +
                "    \"CDATA blocks<are><supported>!\"\n" +
                "  ],\n" +
                "  \"five\": [\n" +
                "    \"\",\n" +
                "    \"\"\n" +
                "  ],\n" +
                "  \"four\": \"\",\n" +
                "  \"one\": 1,\n" +
                "  \"seven\": 7,\n" +
                "  \"six\": {\"content\": 6},\n" +
                "  \"three\": [\n" +
                "    3,\n" +
                "    \"III\",\n" +
                "    \"T H R E E\"\n" +
                "  ],\n" +
                "  \"two\": \" \\\"2\\\" \"\n" +
                "}}", j.toString(2));
        assertEquals("<xml>First \t&lt;content&gt;\n" +
                "This is &quot;content&quot;.\n" +
                "JSON does not preserve the sequencing of elements and contents.\n" +
                "Content text is an implied structure in XML.\n" +
                "JSON does not have implied structure:\n" +
                "everything is explicit.\n" +
                "CDATA blocks&lt;are&gt;&lt;supported&gt;!<two> &quot;2&quot; </two><seven>7</seven><five/><five/><one>1</one><three>3</three><three>III</three><three>T H R E E</three><four/><six>6</six></xml>",
                XML.toString(j));

        ja = JSONML.toJSONArray(s);
        assertEquals("[\n" +
                "    \"xml\",\n" +
                "    {\n" +
                "        \"one\": 1,\n" +
                "        \"two\": \" \\\"2\\\" \"\n" +
                "    },\n" +
                "    [\"five\"],\n" +
                "    \"First \\t<content>\",\n" +
                "    [\"five\"],\n" +
                "    \"This is \\\"content\\\".\",\n" +
                "    [\n" +
                "        \"three\",\n" +
                "        3\n" +
                "    ],\n" +
                "    \"JSON does not preserve the sequencing of elements and contents.\",\n" +
                "    [\n" +
                "        \"three\",\n" +
                "        \"III\"\n" +
                "    ],\n" +
                "    [\n" +
                "        \"three\",\n" +
                "        \"T H R E E\"\n" +
                "    ],\n" +
                "    [\"four\"],\n" +
                "    \"Content text is an implied structure in XML.\",\n" +
                "    [\n" +
                "        \"six\",\n" +
                "        {\"content\": 6}\n" +
                "    ],\n" +
                "    \"JSON does not have implied structure:\",\n" +
                "    [\n" +
                "        \"seven\",\n" +
                "        7\n" +
                "    ],\n" +
                "    \"everything is explicit.\",\n" +
                "    \"CDATA blocks<are><supported>!\"\n" +
                "]", ja.toString(4));
        assertEquals("<xml two=\" &quot;2&quot; \" one=\"1\"><five/>First \t&lt;content&gt;<five/>This is &quot;content&quot;.<three></three>JSON does not preserve the sequencing of elements and contents.<three>III</three><three>T H R E E</three><four/>Content text is an implied structure in XML.<six content=\"6\"/>JSON does not have implied structure:<seven></seven>everything is explicit.CDATA blocks&lt;are&gt;&lt;supported&gt;!</xml>",
                JSONML.toString(ja));

        s = "<xml do='0'>uno<a re='1' mi='2'>dos<b fa='3'/>tres<c>true</c>quatro</a>cinqo<d>seis<e/></d></xml>";
        ja = JSONML.toJSONArray(s);
        assertEquals("[\n" +
                "    \"xml\",\n" +
                "    {\"do\": \"0\"},\n" +
                "    \"uno\",\n" +
                "    [\n" +
                "        \"a\",\n" +
                "        {\n" +
                "            \"mi\": 2,\n" +
                "            \"re\": 1\n" +
                "        },\n" +
                "        \"dos\",\n" +
                "        [\n" +
                "            \"b\",\n" +
                "            {\"fa\": 3}\n" +
                "        ],\n" +
                "        \"tres\",\n" +
                "        [\n" +
                "            \"c\",\n" +
                "            true\n" +
                "        ],\n" +
                "        \"quatro\"\n" +
                "    ],\n" +
                "    \"cinqo\",\n" +
                "    [\n" +
                "        \"d\",\n" +
                "        \"seis\",\n" +
                "        [\"e\"]\n" +
                "    ]\n" +
                "]", ja.toString(4));
        assertEquals("<xml do=\"0\">uno<a re=\"1\" mi=\"2\">dos<b fa=\"3\"/>tres<c></c>quatro</a>cinqo<d>seis<e/></d></xml>",
                JSONML.toString(ja));

        s = "<mapping><empty/>   <class name = \"Customer\">      <field name = \"ID\" type = \"string\">         <bind-xml name=\"ID\" node=\"attribute\"/>      </field>      <field name = \"FirstName\" type = \"FirstName\"/>      <field name = \"MI\" type = \"MI\"/>      <field name = \"LastName\" type = \"LastName\"/>   </class>   <class name = \"FirstName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"MI\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"LastName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class></mapping>";
        j = XML.toJSONObject(s);

        assertEquals("{\"mapping\": {\n" +
                "  \"class\": [\n" +
                "    {\n" +
                "      \"field\": [\n" +
                "        {\n" +
                "          \"bind-xml\": {\n" +
                "            \"name\": \"ID\",\n" +
                "            \"node\": \"attribute\"\n" +
                "          },\n" +
                "          \"name\": \"ID\",\n" +
                "          \"type\": \"string\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"FirstName\",\n" +
                "          \"type\": \"FirstName\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"MI\",\n" +
                "          \"type\": \"MI\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"LastName\",\n" +
                "          \"type\": \"LastName\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"name\": \"Customer\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"field\": {\n" +
                "        \"bind-xml\": {\n" +
                "          \"name\": \"text\",\n" +
                "          \"node\": \"text\"\n" +
                "        },\n" +
                "        \"name\": \"text\"\n" +
                "      },\n" +
                "      \"name\": \"FirstName\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"field\": {\n" +
                "        \"bind-xml\": {\n" +
                "          \"name\": \"text\",\n" +
                "          \"node\": \"text\"\n" +
                "        },\n" +
                "        \"name\": \"text\"\n" +
                "      },\n" +
                "      \"name\": \"MI\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"field\": {\n" +
                "        \"bind-xml\": {\n" +
                "          \"name\": \"text\",\n" +
                "          \"node\": \"text\"\n" +
                "        },\n" +
                "        \"name\": \"text\"\n" +
                "      },\n" +
                "      \"name\": \"LastName\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"empty\": \"\"\n" +
                "}}", j.toString(2));
        assertEquals("<mapping><empty/><class><field><bind-xml><node>attribute</node><name>ID</name></bind-xml><name>ID</name><type>string</type></field><field><name>FirstName</name><type>FirstName</type></field><field><name>MI</name><type>MI</type></field><field><name>LastName</name><type>LastName</type></field><name>Customer</name></class><class><field><bind-xml><node>text</node><name>text</name></bind-xml><name>text</name></field><name>FirstName</name></class><class><field><bind-xml><node>text</node><name>text</name></bind-xml><name>text</name></field><name>MI</name></class><class><field><bind-xml><node>text</node><name>text</name></bind-xml><name>text</name></field><name>LastName</name></class></mapping>",
                XML.toString(j));
        ja = JSONML.toJSONArray(s);
        assertEquals("[\n" +
                "    \"mapping\",\n" +
                "    [\"empty\"],\n" +
                "    [\n" +
                "        \"class\",\n" +
                "        {\"name\": \"Customer\"},\n" +
                "        [\n" +
                "            \"field\",\n" +
                "            {\n" +
                "                \"name\": \"ID\",\n" +
                "                \"type\": \"string\"\n" +
                "            },\n" +
                "            [\n" +
                "                \"bind-xml\",\n" +
                "                {\n" +
                "                    \"name\": \"ID\",\n" +
                "                    \"node\": \"attribute\"\n" +
                "                }\n" +
                "            ]\n" +
                "        ],\n" +
                "        [\n" +
                "            \"field\",\n" +
                "            {\n" +
                "                \"name\": \"FirstName\",\n" +
                "                \"type\": \"FirstName\"\n" +
                "            }\n" +
                "        ],\n" +
                "        [\n" +
                "            \"field\",\n" +
                "            {\n" +
                "                \"name\": \"MI\",\n" +
                "                \"type\": \"MI\"\n" +
                "            }\n" +
                "        ],\n" +
                "        [\n" +
                "            \"field\",\n" +
                "            {\n" +
                "                \"name\": \"LastName\",\n" +
                "                \"type\": \"LastName\"\n" +
                "            }\n" +
                "        ]\n" +
                "    ],\n" +
                "    [\n" +
                "        \"class\",\n" +
                "        {\"name\": \"FirstName\"},\n" +
                "        [\n" +
                "            \"field\",\n" +
                "            {\"name\": \"text\"},\n" +
                "            [\n" +
                "                \"bind-xml\",\n" +
                "                {\n" +
                "                    \"name\": \"text\",\n" +
                "                    \"node\": \"text\"\n" +
                "                }\n" +
                "            ]\n" +
                "        ]\n" +
                "    ],\n" +
                "    [\n" +
                "        \"class\",\n" +
                "        {\"name\": \"MI\"},\n" +
                "        [\n" +
                "            \"field\",\n" +
                "            {\"name\": \"text\"},\n" +
                "            [\n" +
                "                \"bind-xml\",\n" +
                "                {\n" +
                "                    \"name\": \"text\",\n" +
                "                    \"node\": \"text\"\n" +
                "                }\n" +
                "            ]\n" +
                "        ]\n" +
                "    ],\n" +
                "    [\n" +
                "        \"class\",\n" +
                "        {\"name\": \"LastName\"},\n" +
                "        [\n" +
                "            \"field\",\n" +
                "            {\"name\": \"text\"},\n" +
                "            [\n" +
                "                \"bind-xml\",\n" +
                "                {\n" +
                "                    \"name\": \"text\",\n" +
                "                    \"node\": \"text\"\n" +
                "                }\n" +
                "            ]\n" +
                "        ]\n" +
                "    ]\n" +
                "]", ja.toString(4));
        assertEquals("<mapping><empty/><class name=\"Customer\"><field name=\"ID\" type=\"string\"><bind-xml node=\"attribute\" name=\"ID\"/></field><field name=\"FirstName\" type=\"FirstName\"/><field name=\"MI\" type=\"MI\"/><field name=\"LastName\" type=\"LastName\"/></class><class name=\"FirstName\"><field name=\"text\"><bind-xml node=\"text\" name=\"text\"/></field></class><class name=\"MI\"><field name=\"text\"><bind-xml node=\"text\" name=\"text\"/></field></class><class name=\"LastName\"><field name=\"text\"><bind-xml node=\"text\" name=\"text\"/></field></class></mapping>",
                JSONML.toString(ja));

        j = XML.toJSONObject("<?xml version=\"1.0\" ?><Book Author=\"Anonymous\"><Title>Sample Book</Title><Chapter id=\"1\">This is chapter 1. It is not very long or interesting.</Chapter><Chapter id=\"2\">This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.</Chapter></Book>");
        assertEquals("{\"Book\": {\n" +
                "  \"Author\": \"Anonymous\",\n" +
                "  \"Chapter\": [\n" +
                "    {\n" +
                "      \"content\": \"This is chapter 1. It is not very long or interesting.\",\n" +
                "      \"id\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"content\": \"This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.\",\n" +
                "      \"id\": 2\n" +
                "    }\n" +
                "  ],\n" +
                "  \"Title\": \"Sample Book\"\n" +
                "}}", j.toString(2));
        assertEquals("<Book><Chapter>This is chapter 1. It is not very long or interesting.<id>1</id></Chapter><Chapter>This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.<id>2</id></Chapter><Author>Anonymous</Author><Title>Sample Book</Title></Book>",
                XML.toString(j));

        j = XML.toJSONObject("<!DOCTYPE bCard 'http://www.cs.caltech.edu/~adam/schemas/bCard'><bCard><?xml default bCard        firstname = ''        lastname  = '' company   = '' email = '' homepage  = ''?><bCard        firstname = 'Rohit'        lastname  = 'Khare'        company   = 'MCI'        email     = 'khare@mci.net'        homepage  = 'http://pest.w3.org/'/><bCard        firstname = 'Adam'        lastname  = 'Rifkin'        company   = 'Caltech Infospheres Project'        email     = 'adam@cs.caltech.edu'        homepage  = 'http://www.cs.caltech.edu/~adam/'/></bCard>");
        assertEquals("{\"bCard\": {\"bCard\": [\n" +
                "  {\n" +
                "    \"company\": \"MCI\",\n" +
                "    \"email\": \"khare@mci.net\",\n" +
                "    \"firstname\": \"Rohit\",\n" +
                "    \"homepage\": \"http://pest.w3.org/\",\n" +
                "    \"lastname\": \"Khare\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"company\": \"Caltech Infospheres Project\",\n" +
                "    \"email\": \"adam@cs.caltech.edu\",\n" +
                "    \"firstname\": \"Adam\",\n" +
                "    \"homepage\": \"http://www.cs.caltech.edu/~adam/\",\n" +
                "    \"lastname\": \"Rifkin\"\n" +
                "  }\n" +
                "]}}", j.toString(2));
        assertEquals("<bCard><bCard><email>khare@mci.net</email><company>MCI</company><lastname>Khare</lastname><firstname>Rohit</firstname><homepage>http://pest.w3.org/</homepage></bCard><bCard><email>adam@cs.caltech.edu</email><company>Caltech Infospheres Project</company><lastname>Rifkin</lastname><firstname>Adam</firstname><homepage>http://www.cs.caltech.edu/~adam/</homepage></bCard></bCard>",
                XML.toString(j));

        j = XML.toJSONObject("<?xml version=\"1.0\"?><customer>    <firstName>        <text>Fred</text>    </firstName>    <ID>fbs0001</ID>    <lastName> <text>Scerbo</text>    </lastName>    <MI>        <text>B</text>    </MI></customer>");
        assertEquals("{\"customer\": {\n" +
                "  \"ID\": \"fbs0001\",\n" +
                "  \"MI\": {\"text\": \"B\"},\n" +
                "  \"firstName\": {\"text\": \"Fred\"},\n" +
                "  \"lastName\": {\"text\": \"Scerbo\"}\n" +
                "}}", j.toString(2));
        assertEquals("<customer><lastName><text>Scerbo</text></lastName><MI><text>B</text></MI><ID>fbs0001</ID><firstName><text>Fred</text></firstName></customer>",
                XML.toString(j));

        j = XML.toJSONObject("<!ENTITY tp-address PUBLIC '-//ABC University::Special Collections Library//TEXT (titlepage: name and address)//EN' 'tpspcoll.sgm'><list type='simple'><head>Repository Address </head><item>Special Collections Library</item><item>ABC University</item><item>Main Library, 40 Circle Drive</item><item>Ourtown, Pennsylvania</item><item>17654 USA</item></list>");
        assertEquals("{\"list\":{\"item\":[\"Special Collections Library\",\"ABC University\",\"Main Library, 40 Circle Drive\",\"Ourtown, Pennsylvania\",\"17654 USA\"],\"head\":\"Repository Address\",\"type\":\"simple\"}}",
                j.toString());
        assertEquals("<list><item>Special Collections Library</item><item>ABC University</item><item>Main Library, 40 Circle Drive</item><item>Ourtown, Pennsylvania</item><item>17654 USA</item><head>Repository Address</head><type>simple</type></list>",
                XML.toString(j));

        j = XML.toJSONObject("<test intertag status=ok><empty/>deluxe<blip sweet=true>&amp;&quot;toot&quot;&toot;&#x41;</blip><x>eks</x><w>bonus</w><w>bonus2</w></test>");
        assertEquals("{\"test\": {\n" +
                "  \"blip\": {\n" +
                "    \"content\": \"&\\\"toot\\\"&toot;&#x41;\",\n" +
                "    \"sweet\": true\n" +
                "  },\n" +
                "  \"content\": \"deluxe\",\n" +
                "  \"empty\": \"\",\n" +
                "  \"intertag\": \"\",\n" +
                "  \"status\": \"ok\",\n" +
                "  \"w\": [\n" +
                "    \"bonus\",\n" +
                "    \"bonus2\"\n" +
                "  ],\n" +
                "  \"x\": \"eks\"\n" +
                "}}", j.toString(2));
        assertEquals("<test><w>bonus</w><w>bonus2</w>deluxe<intertag/><status>ok</status><blip>&amp;&quot;toot&quot;&amp;toot;&amp;#x41;<sweet>true</sweet></blip><empty/><x>eks</x></test>",
                XML.toString(j));

        j = HTTP.toJSONObject("GET / HTTP/1.0\nAccept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\nAccept-Language: en-us\nUser-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\nHost: www.nokko.com\nConnection: keep-alive\nAccept-encoding: gzip, deflate\n");
        assertEquals("{\n" +
                "  \"Accept\": \"image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\",\n" +
                "  \"Accept-Language\": \"en-us\",\n" +
                "  \"Accept-encoding\": \"gzip, deflate\",\n" +
                "  \"Connection\": \"keep-alive\",\n" +
                "  \"HTTP-Version\": \"HTTP/1.0\",\n" +
                "  \"Host\": \"www.nokko.com\",\n" +
                "  \"Method\": \"GET\",\n" +
                "  \"Request-URI\": \"/\",\n" +
                "  \"User-Agent\": \"Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\"\n" +
                "}", j.toString(2));
        assertEquals("GET \"/\" HTTP/1.0\r\n" +
                "Accept-Language: en-us\r\n" +
                "Host: www.nokko.com\r\n" +
                "Accept-encoding: gzip, deflate\r\n" +
                "User-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\r\n" +
                "Connection: keep-alive\r\n" +
                "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\r\n\r\n",
                HTTP.toString(j));

        j = HTTP.toJSONObject("HTTP/1.1 200 Oki Doki\nDate: Sun, 26 May 2002 17:38:52 GMT\nServer: Apache/1.3.23 (Unix) mod_perl/1.26\nKeep-Alive: timeout=15, max=100\nConnection: Keep-Alive\nTransfer-Encoding: chunked\nContent-Type: text/html\n");
        assertEquals("{\n" +
                "  \"Connection\": \"Keep-Alive\",\n" +
                "  \"Content-Type\": \"text/html\",\n" +
                "  \"Date\": \"Sun, 26 May 2002 17:38:52 GMT\",\n" +
                "  \"HTTP-Version\": \"HTTP/1.1\",\n" +
                "  \"Keep-Alive\": \"timeout=15, max=100\",\n" +
                "  \"Reason-Phrase\": \"Oki Doki\",\n" +
                "  \"Server\": \"Apache/1.3.23 (Unix) mod_perl/1.26\",\n" +
                "  \"Status-Code\": \"200\",\n" +
                "  \"Transfer-Encoding\": \"chunked\"\n" +
                "}", j.toString(2));
        assertEquals("HTTP/1.1 200 Oki Doki\r\n" +
                "Transfer-Encoding: chunked\r\n" +
                "Date: Sun, 26 May 2002 17:38:52 GMT\r\n" +
                "Keep-Alive: timeout=15, max=100\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: Keep-Alive\r\n" +
                "Server: Apache/1.3.23 (Unix) mod_perl/1.26\r\n\r\n",
                HTTP.toString(j));

        j = new JSONObject("{nix: null, nux: false, null: 'null', 'Request-URI': '/', Method: 'GET', 'HTTP-Version': 'HTTP/1.0'}");
        assertEquals("{\n" +
                "  \"HTTP-Version\": \"HTTP/1.0\",\n" +
                "  \"Method\": \"GET\",\n" +
                "  \"Request-URI\": \"/\",\n" +
                "  \"nix\": null,\n" +
                "  \"null\": \"null\",\n" +
                "  \"nux\": false\n" +
                "}", j.toString(2));
        assertTrue(j.isNull("nix"));
        assertTrue(j.has("nix"));
        assertEquals("<Request-URI>/</Request-URI><nix>null</nix><nux>false</nux><Method>GET</Method><HTTP-Version>HTTP/1.0</HTTP-Version><null>null</null>",
                XML.toString(j));
        assertEquals("GET \"/\" HTTP/1.0\r\n" +
                "nux: false\r\n" +
                "null: null\r\n\r\n", HTTP.toString(j));

        j = XML.toJSONObject("<?xml version='1.0' encoding='UTF-8'?>" + "\n\n" + "<SOAP-ENV:Envelope" +
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

        assertEquals("{\"SOAP-ENV:Envelope\": {\n" +
                "  \"SOAP-ENV:Body\": {\"ns1:doGoogleSearch\": {\n" +
                "    \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\",\n" +
                "    \"filter\": {\n" +
                "      \"content\": true,\n" +
                "      \"xsi:type\": \"xsd:boolean\"\n" +
                "    },\n" +
                "    \"ie\": {\n" +
                "      \"content\": \"latin1\",\n" +
                "      \"xsi:type\": \"xsd:string\"\n" +
                "    },\n" +
                "    \"key\": {\n" +
                "      \"content\": \"GOOGLEKEY\",\n" +
                "      \"xsi:type\": \"xsd:string\"\n" +
                "    },\n" +
                "    \"lr\": {\"xsi:type\": \"xsd:string\"},\n" +
                "    \"maxResults\": {\n" +
                "      \"content\": 10,\n" +
                "      \"xsi:type\": \"xsd:int\"\n" +
                "    },\n" +
                "    \"oe\": {\n" +
                "      \"content\": \"latin1\",\n" +
                "      \"xsi:type\": \"xsd:string\"\n" +
                "    },\n" +
                "    \"q\": {\n" +
                "      \"content\": \"'+search+'\",\n" +
                "      \"xsi:type\": \"xsd:string\"\n" +
                "    },\n" +
                "    \"restrict\": {\"xsi:type\": \"xsd:string\"},\n" +
                "    \"safeSearch\": {\n" +
                "      \"content\": false,\n" +
                "      \"xsi:type\": \"xsd:boolean\"\n" +
                "    },\n" +
                "    \"start\": {\n" +
                "      \"content\": \"0\",\n" +
                "      \"xsi:type\": \"xsd:int\"\n" +
                "    },\n" +
                "    \"xmlns:ns1\": \"urn:GoogleSearch\"\n" +
                "  }},\n" +
                "  \"xmlns:SOAP-ENV\": \"http://schemas.xmlsoap.org/soap/envelope/\",\n" +
                "  \"xmlns:xsd\": \"http://www.w3.org/1999/XMLSchema\",\n" +
                "  \"xmlns:xsi\": \"http://www.w3.org/1999/XMLSchema-instance\"\n" +
                "}}", j.toString(2));

        assertEquals("<SOAP-ENV:Envelope><SOAP-ENV:Body><ns1:doGoogleSearch><oe>latin1<xsi:type>xsd:string</xsi:type></oe><SOAP-ENV:encodingStyle>http://schemas.xmlsoap.org/soap/encoding/</SOAP-ENV:encodingStyle><lr><xsi:type>xsd:string</xsi:type></lr><start>0<xsi:type>xsd:int</xsi:type></start><q>'+search+'<xsi:type>xsd:string</xsi:type></q><ie>latin1<xsi:type>xsd:string</xsi:type></ie><safeSearch>false<xsi:type>xsd:boolean</xsi:type></safeSearch><xmlns:ns1>urn:GoogleSearch</xmlns:ns1><restrict><xsi:type>xsd:string</xsi:type></restrict><filter>true<xsi:type>xsd:boolean</xsi:type></filter><maxResults>10<xsi:type>xsd:int</xsi:type></maxResults><key>GOOGLEKEY<xsi:type>xsd:string</xsi:type></key></ns1:doGoogleSearch></SOAP-ENV:Body><xmlns:xsd>http://www.w3.org/1999/XMLSchema</xmlns:xsd><xmlns:xsi>http://www.w3.org/1999/XMLSchema-instance</xmlns:xsi><xmlns:SOAP-ENV>http://schemas.xmlsoap.org/soap/envelope/</xmlns:SOAP-ENV></SOAP-ENV:Envelope>",
                XML.toString(j));

        j = new JSONObject("{Envelope: {Body: {\"ns1:doGoogleSearch\": {oe: \"latin1\", filter: true, q: \"'+search+'\", key: \"GOOGLEKEY\", maxResults: 10, \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\", start: 0, ie: \"latin1\", safeSearch:false, \"xmlns:ns1\": \"urn:GoogleSearch\"}}}}");
        assertEquals("{\"Envelope\": {\"Body\": {\"ns1:doGoogleSearch\": {\n" +
                "  \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\",\n" +
                "  \"filter\": true,\n" +
                "  \"ie\": \"latin1\",\n" +
                "  \"key\": \"GOOGLEKEY\",\n" +
                "  \"maxResults\": 10,\n" +
                "  \"oe\": \"latin1\",\n" +
                "  \"q\": \"'+search+'\",\n" +
                "  \"safeSearch\": false,\n" +
                "  \"start\": 0,\n" +
                "  \"xmlns:ns1\": \"urn:GoogleSearch\"\n" +
                "}}}}", j.toString(2));
        assertEquals("<Envelope><Body><ns1:doGoogleSearch><oe>latin1</oe><SOAP-ENV:encodingStyle>http://schemas.xmlsoap.org/soap/encoding/</SOAP-ENV:encodingStyle><start>0</start><q>'+search+'</q><ie>latin1</ie><safeSearch>false</safeSearch><xmlns:ns1>urn:GoogleSearch</xmlns:ns1><maxResults>10</maxResults><key>GOOGLEKEY</key><filter>true</filter></ns1:doGoogleSearch></Body></Envelope>",
                XML.toString(j));

        j = CookieList.toJSONObject("  f%oo = b+l=ah  ; o;n%40e = t.wo ");
        assertEquals("{\n" +
                "  \"f%oo\": \"b l=ah\",\n" +
                "  \"o;n@e\": \"t.wo\"\n" +
                "}", j.toString(2));
        assertEquals("o%3bn@e=t.wo;f%25oo=b l%3dah",
                CookieList.toString(j));

        j = Cookie.toJSONObject("f%oo=blah; secure ;expires = April 24, 2002");
        assertEquals("{\n" +
                "  \"expires\": \"April 24, 2002\",\n" +
                "  \"name\": \"f%oo\",\n" +
                "  \"secure\": true,\n" +
                "  \"value\": \"blah\"\n" +
                "}", j.toString(2));
        assertEquals("f%25oo=blah;expires=April 24, 2002;secure",
                Cookie.toString(j));

        j = new JSONObject("{script: 'It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers</script>so we insert a backslash before the /'}");
        assertEquals("{\"script\":\"It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers<\\/script>so we insert a backslash before the /\"}",
                j.toString());

        JSONTokener jt = new JSONTokener("{op:'test', to:'session', pre:1}{op:'test', to:'session', pre:2}");
        j = new JSONObject(jt);
        assertEquals("{\"to\":\"session\",\"op\":\"test\",\"pre\":1}",
                j.toString());
        assertEquals(1, j.optInt("pre"));
        int i = jt.skipTo('{');
        assertEquals(123, i);
        j = new JSONObject(jt);
        assertEquals("{\"to\":\"session\",\"op\":\"test\",\"pre\":2}",
                j.toString());

        a = CDL.toJSONArray("Comma delimited list test, '\"Strip\"Quotes', 'quote, comma', No quotes, 'Single Quotes', \"Double Quotes\"\n1,'2',\"3\"\n,'It is \"good,\"', \"It works.\"\n\n");

        s = CDL.toString(a);
        assertEquals("\"quote, comma\",\"StripQuotes\",Comma delimited list test\n" +
                "3,2,1\n" +
                "It works.,\"It is good,\",\n",
                s);
        assertEquals("[\n" +
                "    {\n" +
                "        \"\\\"Strip\\\"Quotes\": \"2\",\n" +
                "        \"Comma delimited list test\": \"1\",\n" +
                "        \"quote, comma\": \"3\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"\\\"Strip\\\"Quotes\": \"It is \\\"good,\\\"\",\n" +
                "        \"Comma delimited list test\": \"\",\n" +
                "        \"quote, comma\": \"It works.\"\n" +
                "    }\n" +
                "]", a.toString(4));
        a = CDL.toJSONArray(s);
        assertEquals("[\n" +
                "    {\n" +
                "        \"Comma delimited list test\": \"1\",\n" +
                "        \"StripQuotes\": \"2\",\n" +
                "        \"quote, comma\": \"3\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"Comma delimited list test\": \"\",\n" +
                "        \"StripQuotes\": \"It is good,\",\n" +
                "        \"quote, comma\": \"It works.\"\n" +
                "    }\n" +
                "]", a.toString(4));

        a = new JSONArray(" [\"<escape>\", next is an implied null , , ok,] ");
        assertEquals("[\"<escape>\",\"next is an implied null\",null,\"ok\"]",
                a.toString());
        assertEquals("<array>&lt;escape&gt;</array><array>next is an implied null</array><array>null</array><array>ok</array>",
                XML.toString(a));

        j = new JSONObject("{ fun => with non-standard forms ; forgiving => This package can be used to parse formats that are similar to but not stricting conforming to JSON; why=To make it easier to migrate existing data to JSON,one = [[1.00]]; uno=[[{1=>1}]];'+':+6e66 ;pluses=+++;empty = '' , 'double':0.666,true: TRUE, false: FALSE, null=NULL;[true] = [[!,@;*]]; string=>  o. k. ; \r oct=0666; hex=0x666; dec=666; o=0999; noh=0x0x}");
        assertEquals("{\n" +
                "    \"+\": 6.0E66,\n" +
                "    \"[true]\": [[\n" +
                "        \"!\",\n" +
                "        \"@\",\n" +
                "        \"*\"\n" +
                "    ]],\n" +
                "    \"dec\": 666,\n" +
                "    \"double\": 0.666,\n" +
                "    \"empty\": \"\",\n" +
                "    \"false\": false,\n" +
                "    \"forgiving\": \"This package can be used to parse formats that are similar to but not stricting conforming to JSON\",\n" +
                "    \"fun\": \"with non-standard forms\",\n" +
                "    \"hex\": 1638,\n" +
                "    \"noh\": \"0x0x\",\n" +
                "    \"null\": null,\n" +
                "    \"o\": 999,\n" +
                "    \"oct\": 666,\n" +
                "    \"one\": [[1]],\n" +
                "    \"pluses\": \"+++\",\n" +
                "    \"string\": \"o. k.\",\n" +
                "    \"true\": true,\n" +
                "    \"uno\": [[{\"1\": 1}]],\n" +
                "    \"why\": \"To make it easier to migrate existing data to JSON\"\n" +
                "}", j.toString(4));
        assertTrue(j.getBoolean("true"));
        assertFalse(j.getBoolean("false"));

        j = new JSONObject(j, new String[]{"dec", "oct", "hex", "missing"});
        assertEquals("{\n" +
                "    \"dec\": 666,\n" +
                "    \"hex\": 1638,\n" +
                "    \"oct\": 666\n" +
                "}", j.toString(4));

        assertEquals("[[\"<escape>\",\"next is an implied null\",null,\"ok\"],{\"oct\":666,\"dec\":666,\"hex\":1638}]",
                new JSONStringer().array().value(a).value(j).endArray().toString());

        j = new JSONObject("{string: \"98.6\", long: 2147483648, int: 2147483647, longer: 9223372036854775807, double: 9223372036854775808}");
        assertEquals("{\n" +
                "    \"double\": \"9223372036854775808\",\n" +
                "    \"int\": 2147483647,\n" +
                "    \"long\": 2147483648,\n" +
                "    \"longer\": 9223372036854775807,\n" +
                "    \"string\": \"98.6\"\n" +
                "}", j.toString(4));

        // getInt
        assertEquals(2147483647, j.getInt("int"));
        assertEquals(-2147483648, j.getInt("long"));
        assertEquals(-1, j.getInt("longer"));
        try {
            j.getInt("double");
            fail("should fail with - JSONObject[\"double\"] is not an int.");
        } catch (JSONException expected) {
        }
        try {
            assertEquals(98.6, j.getInt("string"));
            fail("should fail with - JSONObject[\"string\"] is not an int.");
        } catch (JSONException expected) {
        }

        // getLong
        assertEquals(2147483647, j.getLong("int"));
        assertEquals(2147483648l, j.getLong("long"));
        assertEquals(9223372036854775807l, j.getLong("longer"));
        try {
            j.getLong("double");
            fail("should fail with - JSONObject[\"double\"] is not a long.");
        } catch (JSONException expected) {
        }
        try {
            assertEquals(98.6, j.getLong("string"));
            fail("should fail with - JSONObject[\"string\"] is not a long.");
        } catch (JSONException expected) {
        }

        // getDouble
        assertEquals(2.147483647E9, j.getDouble("int"));
        assertEquals(2.147483648E9, j.getDouble("long"));
        assertEquals(9.223372036854776E18, j.getDouble("longer"));
        assertEquals(9223372036854775808d, j.getDouble("double"));
        assertEquals(98.6, j.getDouble("string"));

        j.put("good sized", 9223372036854775807L);
        assertEquals("{\n" +
                "    \"double\": \"9223372036854775808\",\n" +
                "    \"good sized\": 9223372036854775807,\n" +
                "    \"int\": 2147483647,\n" +
                "    \"long\": 2147483648,\n" +
                "    \"longer\": 9223372036854775807,\n" +
                "    \"string\": \"98.6\"\n" +
                "}", j.toString(4));

        a = new JSONArray("[2147483647, 2147483648, 9223372036854775807, 9223372036854775808]");
        assertEquals("[\n" +
                "    2147483647,\n" +
                "    2147483648,\n" +
                "    9223372036854775807,\n" +
                "    \"9223372036854775808\"\n" +
                "]", a.toString(4));

        List<String> expectedKeys = new ArrayList<String>(6);
        expectedKeys.add("int");
        expectedKeys.add("string");
        expectedKeys.add("longer");
        expectedKeys.add("good sized");
        expectedKeys.add("double");
        expectedKeys.add("long");

        it = j.keys();
        while (it.hasNext()) {
            s = (String) it.next();
            assertTrue(expectedKeys.remove(s));
        }
        assertEquals(0, expectedKeys.size());


        // accumulate
        j = new JSONObject();
        j.accumulate("stooge", "Curly");
        j.accumulate("stooge", "Larry");
        j.accumulate("stooge", "Moe");
        a = j.getJSONArray("stooge");
        a.put(5, "Shemp");
        assertEquals("{\"stooge\": [\n" +
                "    \"Curly\",\n" +
                "    \"Larry\",\n" +
                "    \"Moe\",\n" +
                "    null,\n" +
                "    null,\n" +
                "    \"Shemp\"\n" +
                "]}", j.toString(4));

        // write
        assertEquals("{\"stooge\":[\"Curly\",\"Larry\",\"Moe\",null,null,\"Shemp\"]}",
                j.write(new StringWriter()).toString());

        s = "<xml empty><a></a><a>1</a><a>22</a><a>333</a></xml>";
        j = XML.toJSONObject(s);
        assertEquals("{\"xml\": {\n" +
                "    \"a\": [\n" +
                "        \"\",\n" +
                "        1,\n" +
                "        22,\n" +
                "        333\n" +
                "    ],\n" +
                "    \"empty\": \"\"\n" +
                "}}", j.toString(4));
        assertEquals("<xml><a/><a>1</a><a>22</a><a>333</a><empty/></xml>",
                XML.toString(j));

        s = "<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter      <chapter>Content of the first subchapter</chapter>      <chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>";
        j = XML.toJSONObject(s);
        assertEquals("{\"book\": {\"chapter\": [\n" +
                "    \"Content of the first chapter\",\n" +
                "    {\n" +
                "        \"chapter\": [\n" +
                "            \"Content of the first subchapter\",\n" +
                "            \"Content of the second subchapter\"\n" +
                "        ],\n" +
                "        \"content\": \"Content of the second chapter\"\n" +
                "    },\n" +
                "    \"Third Chapter\"\n" +
                "]}}", j.toString(4));
        assertEquals("<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter<chapter>Content of the first subchapter</chapter><chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>",
                XML.toString(j));

        a = JSONML.toJSONArray(s);
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
                "]", a.toString(4));
        assertEquals("<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter<chapter>Content of the first subchapter</chapter><chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>",
                JSONML.toString(a));

        Collection c = null;
        Map m = null;

        j = new JSONObject(m);
        a = new JSONArray(c);
        j.append("stooge", "Joe DeRita");
        j.append("stooge", "Shemp");
        j.accumulate("stooges", "Curly");
        j.accumulate("stooges", "Larry");
        j.accumulate("stooges", "Moe");
        j.accumulate("stoogearray", j.get("stooges"));
        j.put("map", m);
        j.put("collection", c);
        j.put("array", a);
        a.put(m);
        a.put(c);
        assertEquals("{\n" +
                "    \"array\": [\n" +
                "        {},\n" +
                "        []\n" +
                "    ],\n" +
                "    \"collection\": [],\n" +
                "    \"map\": {},\n" +
                "    \"stooge\": [\n" +
                "        \"Joe DeRita\",\n" +
                "        \"Shemp\"\n" +
                "    ],\n" +
                "    \"stoogearray\": [[\n" +
                "        \"Curly\",\n" +
                "        \"Larry\",\n" +
                "        \"Moe\"\n" +
                "    ]],\n" +
                "    \"stooges\": [\n" +
                "        \"Curly\",\n" +
                "        \"Larry\",\n" +
                "        \"Moe\"\n" +
                "    ]\n" +
                "}", j.toString(4));

        s = "{plist=Apple; AnimalSmells = { pig = piggish; lamb = lambish; worm = wormy; }; AnimalSounds = { pig = oink; lamb = baa; worm = baa;  Lisa = \"Why is the worm talking like a lamb?\" } ; AnimalColors = { pig = pink; lamb = black; worm = pink; } } ";
        j = new JSONObject(s);
        assertEquals("{\n" +
                "    \"AnimalColors\": {\n" +
                "        \"lamb\": \"black\",\n" +
                "        \"pig\": \"pink\",\n" +
                "        \"worm\": \"pink\"\n" +
                "    },\n" +
                "    \"AnimalSmells\": {\n" +
                "        \"lamb\": \"lambish\",\n" +
                "        \"pig\": \"piggish\",\n" +
                "        \"worm\": \"wormy\"\n" +
                "    },\n" +
                "    \"AnimalSounds\": {\n" +
                "        \"Lisa\": \"Why is the worm talking like a lamb?\",\n" +
                "        \"lamb\": \"baa\",\n" +
                "        \"pig\": \"oink\",\n" +
                "        \"worm\": \"baa\"\n" +
                "    },\n" +
                "    \"plist\": \"Apple\"\n" +
                "}", j.toString(4));

        s = " [\"San Francisco\", \"New York\", \"Seoul\", \"London\", \"Seattle\", \"Shanghai\"]";
        a = new JSONArray(s);
        assertEquals("[\"San Francisco\",\"New York\",\"Seoul\",\"London\",\"Seattle\",\"Shanghai\"]",
                a.toString());

        s = "<a ichi='1' ni='2'><b>The content of b</b> and <c san='3'>The content of c</c><d>do</d><e></e><d>re</d><f/><d>mi</d></a>";
        j = XML.toJSONObject(s);
        assertEquals("{\"a\": {\n" +
                "  \"b\": \"The content of b\",\n" +
                "  \"c\": {\n" +
                "    \"content\": \"The content of c\",\n" +
                "    \"san\": 3\n" +
                "  },\n" +
                "  \"content\": \"and\",\n" +
                "  \"d\": [\n" +
                "    \"do\",\n" +
                "    \"re\",\n" +
                "    \"mi\"\n" +
                "  ],\n" +
                "  \"e\": \"\",\n" +
                "  \"f\": \"\",\n" +
                "  \"ichi\": 1,\n" +
                "  \"ni\": 2\n" +
                "}}", j.toString(2));
        assertEquals("<a><f/>and<d>do</d><d>re</d><d>mi</d><ichi>1</ichi><e/><b>The content of b</b><c>The content of c<san>3</san></c><ni>2</ni></a>",
                XML.toString(j));
        ja = JSONML.toJSONArray(s);
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

        s = "<Root><MsgType type=\"node\"><BatchType type=\"string\">111111111111111</BatchType></MsgType></Root>";
        j = JSONML.toJSONObject(s);
        assertEquals("{\"tagName\":\"Root\",\"childNodes\":[{\"tagName\":\"MsgType\",\"childNodes\":[{\"tagName\":\"BatchType\",\"childNodes\":[111111111111111],\"type\":\"string\"}],\"type\":\"node\"}]}",
                j.toString());
        ja = JSONML.toJSONArray(s);
        assertEquals("[\"Root\",[\"MsgType\",{\"type\":\"node\"},[\"BatchType\",{\"type\":\"string\"},111111111111111]]]",
                ja.toString());
    }

    public void testExceptions() throws Exception {
        JSONArray array = null;
        JSONObject json;
        JSONArray jsonArray;
        String str;

        try {
            array = new JSONArray("[\n\r\n\r}");
            System.out.println(array.toString());
        } catch (JSONException jsone) {
            assertEquals("Missing value at 5 [character 0 line 4]", jsone.getMessage());
        }

        try {
            array = new JSONArray("<\n\r\n\r      ");
            System.out.println(array.toString());
        } catch (JSONException jsone) {
            assertEquals("A JSONArray text must start with '[' at 1 [character 2 line 1]", jsone.getMessage());
        }

        try {
            array = new JSONArray();
            array.put(Double.NEGATIVE_INFINITY);
            array.put(Double.NaN);
            System.out.println(array.toString());
        } catch (JSONException jsone) {
            assertEquals("JSON does not allow non-finite numbers.", jsone.getMessage());
        }

        json = new JSONObject();
        try {
            System.out.println(json.getDouble("stooge"));
        } catch (JSONException jsone) {
            assertEquals("JSONObject[\"stooge\"] not found.", jsone.getMessage());
        }

        try {
            System.out.println(json.getDouble("howard"));
        } catch (JSONException jsone) {
            assertEquals("JSONObject[\"howard\"] not found.", jsone.getMessage());
        }

        try {
            System.out.println(json.put(null, "howard"));
        } catch (JSONException jsone) {
            assertEquals("Null key.", jsone.getMessage());
        }

        try {
            System.out.println(array.getDouble(0));
        } catch (JSONException jsone) {
            assertEquals("JSONArray[0] not found.", jsone.getMessage());
        }

        try {
            System.out.println(array.get(-1));
        } catch (JSONException jsone) {
            assertEquals("JSONArray[-1] not found.", jsone.getMessage());
        }

        try {
            System.out.println(array.put(Double.NaN));
        } catch (JSONException jsone) {
            assertEquals("JSON does not allow non-finite numbers.", jsone.getMessage());
        }

        try {
            json = XML.toJSONObject("<a><b>    ");
        } catch (JSONException jsone) {
            assertEquals("Unclosed tag b at 11 [character 12 line 1]", jsone.getMessage());
        }

        try {
            json = XML.toJSONObject("<a></b>    ");
        } catch (JSONException jsone) {
            assertEquals("Mismatched a and b at 6 [character 7 line 1]", jsone.getMessage());
        }

        try {
            json = XML.toJSONObject("<a></a    ");
        } catch (JSONException jsone) {
            assertEquals("Misshaped element at 11 [character 12 line 1]", jsone.getMessage());
        }

        try {
            jsonArray = new JSONArray(new Object());
            System.out.println(jsonArray.toString());
        } catch (JSONException jsone) {
            assertEquals("JSONArray initial value should be a string or collection or array.", jsone.getMessage());
        }

        try {
            str = "[)";
            array = new JSONArray(str);
            System.out.println(array.toString());
        } catch (JSONException jsone) {
            assertEquals("Expected a ',' or ']' at 3 [character 4 line 1]", jsone.getMessage());
        }

        try {
            str = "<xml";
            jsonArray = JSONML.toJSONArray(str);
            System.out.println(jsonArray.toString(4));
        } catch (JSONException jsone) {
            assertEquals("Misshaped element at 6 [character 7 line 1]", jsone.getMessage());
        }

        try {
            str = "<right></wrong>";
            jsonArray = JSONML.toJSONArray(str);
            System.out.println(jsonArray.toString(4));
        } catch (JSONException jsone) {
            assertEquals("Mismatched 'right' and 'wrong' at 15 [character 16 line 1]", jsone.getMessage());
        }

        try {
            str = "{\"koda\": true, \"koda\": true}";
            json = new JSONObject(str);
            System.out.println(json.toString(4));
        } catch (JSONException jsone) {
            assertEquals("Duplicate key \"koda\"", jsone.getMessage());
        }

        try {
            JSONStringer jj = new JSONStringer();
            str = jj
                    .object()
                    .key("bosanda")
                    .value("MARIE HAA'S")
                    .key("bosanda")
                    .value("MARIE HAA\\'S")
                    .endObject()
                    .toString();
            System.out.println(json.toString(4));
        } catch (JSONException jsone) {
            assertEquals("Duplicate key \"bosanda\"", jsone.getMessage());
        }
    }

    /**
     * Obj is a typical class that implements JSONString. It also
     * provides some beanie methods that can be used to
     * construct a JSONObject. It also demonstrates constructing
     * a JSONObject with an array of names.
     */
    class Obj implements JSONString {
        public String aString;
        public double aNumber;
        public boolean aBoolean;

        public Obj(String string, double n, boolean b) {
            this.aString = string;
            this.aNumber = n;
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
