/*
 * File: TestXML.java Author: JSON.org
 */
package org.json.tests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import junit.framework.TestCase;

/*
 * Copyright (c) 2002 JSON.org
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * The Software shall be used for Good, not Evil.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * The Class TestXML.
 */
public class TestXML extends TestCase
{

    /** The jsonobject. */
    JSONObject jsonobject = new JSONObject();
    
    /** The jsonarray. */
    JSONArray jsonarray = new JSONArray();
    
    /** The string. */
    String string;
	
    /**
     * Tests the toJsonObject method using Simple xml.
     */
    public static void testToJsonObject_SimpleXML()
    {
        try
        {
            String XMLString = "<!--comment--><tag1><tag2><?Skip Me?><![CDATA[--comment--]]></tag2><tag3>!123321</tag3></tag1>";
            JSONObject jo = new JSONObject();
            JSONObject jo2 = new JSONObject();
            jo2.put("tag2", "--comment--");
            jo2.put("tag3", "!123321");
            jo.put("tag1", jo2);
            assertEquals(jo.toString(), XML.toJSONObject(XMLString).toString());
        } catch (JSONException e)
        {
            assertEquals("Unterminated string at 20 [character 21 line 1]",
                    e.getMessage());
        }
    }

    /**
     * Tests the toJsonObject method using Bad name.
     */
    public static void testToJsonObject_BadName()
    {
        try
        {
            String XMLString = "<!-abc>123</!-abc>";
            XML.toJSONObject(XMLString);
            fail("Should have failed");
        } catch (JSONException e)
        {
            assertEquals("Mismatched close tag ! at 13 [character 14 line 1]",
                    e.getMessage());
        }
    }

    /**
     * Tests the toJsonObject method using Bad cdata.
     */
    public static void testToJsonObject_BadCDATA()
    {
        try
        {
            String XMLString = "<abc><![CDATA?[--comment--]]></abc>";
            XML.toJSONObject(XMLString);
            fail("Should have failed");
        } catch (JSONException e)
        {
            assertEquals("Expected 'CDATA[' at 14 [character 15 line 1]",
                    e.getMessage());
        }

        try
        {
            String XMLString = "<abc><![CDATA[--comment--]></abc>";
            XML.toJSONObject(XMLString);
            fail("Should have failed");
        } catch (JSONException e)
        {
            assertEquals("Unclosed CDATA at 34 [character 35 line 1]",
                    e.getMessage());
        }
        
        try
        {
            String XMLString = "<abc><![CDATA[--comment--]]?></abc>";
            XML.toJSONObject(XMLString);
            fail("Should have failed");
        } catch (JSONException e)
        {
            assertEquals("Unclosed CDATA at 36 [character 37 line 1]",
                    e.getMessage());
        }
        
        try
        {
            String XMLString = "<abc><![CDAT[--comment--]]></abc>";
            XML.toJSONObject(XMLString);
            fail("Should have failed");
        } catch (JSONException e)
        {
            assertEquals("Expected 'CDATA[' at 12 [character 13 line 1]",
                    e.getMessage());
        }
    }
    
    /**
     * Tests the toJsonObject method using null character.
     */
    public static void testToJsonObject_NullCharacter()
    {
        try
        {
            String XMLString = "\0";
            JSONObject jo = new JSONObject();
            assertEquals(jo.toString(), XML.toJSONObject(XMLString).toString());
        } catch (JSONException e)
        {
            fail(e.toString());
        }
    }

    /**
     * Tests the toJsonObject method using Empty cdata.
     */
    public static void testToJsonObject_EmptyCdata()
    {
        try
        {
            String XMLString = "<abc><![CDATA[]]></abc>";
            JSONObject jo = new JSONObject();
            jo.put("abc", "");
            assertEquals(jo.toString(), XML.toJSONObject(XMLString).toString());
        } catch (JSONException e)
        {
            assertEquals("Unterminated string at 20 [character 21 line 1]",
                    e.getMessage());
        }
    }

    /**
     * Tests the toJsonObject method using Bad meta.
     */
    public static void testToJsonObject_BadMeta()
    {
        try
        {
            String XMLString = "<! abc";
            XML.toJSONObject(XMLString);
            fail("Should have failed");
        } catch (JSONException e)
        {
            assertEquals("Misshaped meta tag at 7 [character 8 line 1]",
                    e.getMessage());
        }

        try
        {
            String XMLString = "<!-<abc";
            XML.toJSONObject(XMLString);
            fail("Should have failed");
        } catch (JSONException e)
        {
            assertEquals("Misshaped meta tag at 8 [character 9 line 1]",
                    e.getMessage());
        }
    }

    /**
     * Tests the toJsonObject method using Open cdata.
     */
    public static void testToJsonObject_OpenCDATA()
    {
        try
        {
            String XMLString = "<abc><![CDATA[";
            XML.toJSONObject(XMLString);
            fail("Should have failed");
        } catch (JSONException e)
        {
            assertEquals("Unclosed CDATA at 15 [character 16 line 1]",
                    e.getMessage());
        }
    }

    /**
     * Tests the toJsonObject method using Mismatched tags.
     */
    public static void testToJsonObject_MismatchedTags()
    {
        try
        {
            String XMLString = "<abc>123</def>";
            XML.toJSONObject(XMLString);
            fail("Should have failed");
        } catch (JSONException e)
        {
            assertEquals("Mismatched abc and def at 13 [character 14 line 1]",
                    e.getMessage());
        }
    }

    /**
     * Tests the toJsonObject method using Misshaped close tag.
     */
    public static void testToJsonObject_MisshapedCloseTag()
    {
        try
        {
            String XMLString = "<abc>123</abc?";
            XML.toJSONObject(XMLString);
            fail("Should have failed");
        } catch (JSONException e)
        {
            assertEquals("Misshaped close tag at 14 [character 15 line 1]",
                    e.getMessage());
        }
    }

    /**
     * Tests the toJsonObject method using unclosed tag.
     */
    public static void testToJsonObject_UnclosedTag()
    {
        try
        {
            String XMLString = "<abc>";
            XML.toJSONObject(XMLString);
            fail("Should have failed");
        } catch (JSONException e)
        {
            assertEquals("Unclosed tag abc at 6 [character 7 line 1]",
                    e.getMessage());
        }
    }

    /**
     * Tests the stringToValue method using true.
     */
    public static void testStringToValue_true()
    {
        assertEquals(Boolean.TRUE, XML.stringToValue("true"));
        assertEquals(Boolean.TRUE, XML.stringToValue("tRUe"));
        assertEquals(Boolean.TRUE, XML.stringToValue("TruE"));
        assertEquals(Boolean.TRUE, XML.stringToValue("TRUE"));
    }

    /**
     * Tests the stringToValue method using false.
     */
    public static void testStringToValue_false()
    {
        assertEquals(Boolean.FALSE, XML.stringToValue("false"));
        assertEquals(Boolean.FALSE, XML.stringToValue("fALSe"));
        assertEquals(Boolean.FALSE, XML.stringToValue("FalsE"));
        assertEquals(Boolean.FALSE, XML.stringToValue("FALSE"));
    }

    /**
     * Tests the stringToValue method using blank.
     */
    public static void testStringToValue_blank()
    {
        assertEquals("", XML.stringToValue(""));
    }

    /**
     * Tests the stringToValue method using null.
     */
    public static void testStringToValue_null()
    {
        assertEquals(JSONObject.NULL, XML.stringToValue("null"));
    }

    /**
     * Tests the stringToValue method using numbers.
     */
    public static void testStringToValue_Numbers()
    {
        assertEquals(0, XML.stringToValue("0"));
        assertEquals(10, XML.stringToValue("10"));
        assertEquals(-10, XML.stringToValue("-10"));
        assertEquals(34.5, XML.stringToValue("34.5"));
        assertEquals(-34.5, XML.stringToValue("-34.5"));
        assertEquals(34054535455454355L, XML.stringToValue("34054535455454355"));
        assertEquals(-34054535455454355L, XML.stringToValue("-34054535455454355"));
        assertEquals("00123", XML.stringToValue("00123"));
        assertEquals("-00123", XML.stringToValue("-00123"));
        assertEquals(123, XML.stringToValue("0123"));
        assertEquals(-123, XML.stringToValue("-0123"));
        assertEquals("-", XML.stringToValue("-"));
        assertEquals("-0abc", XML.stringToValue("-0abc"));
    }

    /**
     * Tests the toJsonObject method using Misshaped tag.
     */
    public static void testToJsonObject_MisshapedTag()
    {
        try
        {
            String XMLString = "<=abc>123<=abc>";
            XML.toJSONObject(XMLString);
            fail("Should have failed");
        } catch (JSONException e)
        {
            assertEquals("Misshaped tag at 2 [character 3 line 1]",
                    e.getMessage());
        }
        try
        {
            String XMLString = "<abc=>123<abc=>";
            XML.toJSONObject(XMLString);
            fail("Should have failed");
        } catch (JSONException e)
        {
            assertEquals("Misshaped tag at 5 [character 6 line 1]",
                    e.getMessage());
        }
    }

    /**
     * Tests the toJsonObject method using Attributes.
     *
     * @throws Exception the exception
     */
    public static void testToJsonObject_Attributes() throws Exception
    {
        String XMLString = "<abc \"abc\"=\"123\">123</abc>";
        JSONObject jo = new JSONObject();
        JSONObject jo2 = new JSONObject();
        jo2.put("content", 123);
        jo2.put("abc", 123);
        jo.put("abc", jo2);
        assertEquals(jo.toString(), XML.toJSONObject(XMLString).toString());
    }

    /**
     * Tests the toJsonObject method using attributes with open string.
     */
    public static void testToJsonObject_AttributesWithOpenString()
    {
        try
        {
            String XMLString = "<abc \"abc>123</abc>";
            XML.toJSONObject(XMLString);
            fail("Should have failed");
        } catch (JSONException e)
        {
            assertEquals("Unterminated string at 20 [character 21 line 1]",
                    e.getMessage());
        }
    }

    /**
     * Tests the toJsonObject method using Attributes with ampersand.
     */
    public static void testToJsonObject_AttributesWithAmpersand()
    {

        try
        {
            String XMLString = "<abc \"abc&nbsp;\">123</abc>";
            JSONObject jo = new JSONObject();
            JSONObject jo2 = new JSONObject();
            jo2.put("content", 123);
            jo2.put("abc&nbsp;", "");
            jo.put("abc", jo2);
            assertEquals(jo.toString(), XML.toJSONObject(XMLString).toString());
        } catch (JSONException e)
        {
            assertEquals("Unterminated string at 20 [character 21 line 1]",
                    e.getMessage());
        }
    }

    /**
     * Tests the toJsonObject method using Attributes missing value.
     */
    public static void testToJsonObject_AttributesMissingValue()
    {
        try
        {
            String XMLString = "<abc \"abc\"=>123</abc>";
            XML.toJSONObject(XMLString);
            fail("Should have failed");
        } catch (JSONException e)
        {
            assertEquals("Missing value at 12 [character 13 line 1]",
                    e.getMessage());
        }
    }

    /**
     * Tests the toJsonObject method using empty tag.
     */
    public static void testToJsonObject_EmptyTag()
    {
        try
        {
            String XMLString = "<abc />";
            JSONObject jo = new JSONObject();
            jo.put("abc", "");
            assertEquals(jo.toString(), XML.toJSONObject(XMLString).toString());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Tests the toJsonObject method using empty tag with attributes.
     */
    public static void testToJsonObject_EmptyTagWithAttributes()
    {
        try
        {
            String XMLString = "<abc 'def'='jkk' />";
            JSONObject jo = new JSONObject();
            JSONObject jo2 = new JSONObject();
            jo2.put("def","jkk");
            jo.put("abc", jo2);
            assertEquals(jo.toString(), XML.toJSONObject(XMLString).toString());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Tests the toJsonObject method using broken empty tag.
     */
    public static void testToJsonObject_BrokenEmptyTag()
    {
        try
        {
            String XMLString = "<abc /?>";
            XML.toJSONObject(XMLString).toString();
            fail("Should have failed");
        } catch (JSONException e)
        {
            assertEquals("Misshaped tag at 7 [character 8 line 1]",
                    e.getMessage());
        }
    }
    
    /**
     * Tests the toString method using jSON object.
     */
    {
        try
        {
            JSONObject jo = new JSONObject();
            jo.put("abc", "123");
            assertEquals("<abc>123</abc>", XML.toString(jo));
        } catch (JSONException e)
        {
        	e.printStackTrace();
        }
    }

    /**
     * Tests the toString method using empty json object.
     */
    public static void testToString_EmptyJsonObject()
    {
        try
        {
            JSONObject jo = new JSONObject();
            assertEquals("", XML.toString(jo));
        } catch (JSONException e)
        {
        	e.printStackTrace();
        }
    }
    
    /**
     * Tests the toString method using jSON object and name.
     */
    public static void testToString_JsonObjectAndName()
    {
        try
        {
            JSONObject jo = new JSONObject();
            jo.put("abc", "123");
            assertEquals("<my name><abc>123</abc></my name>", XML.toString(jo, "my name"));
        } catch (JSONException e)
        {
        	e.printStackTrace();
        }
    }
    
    /**
     * Tests the toString method using empty json object and name.
     */
    public static void testToString_EmptyJsonObjectAndName()
    {
        try
        {
            JSONObject jo = new JSONObject();
            assertEquals("<my name></my name>", XML.toString(jo, "my name"));
        } catch (JSONException e)
        {
        	e.printStackTrace();
        }
    }
    
    /**
     * Tests the toString method using empty json object and empty name.
     */
    public static void testToString_EmptyJsonObjectAndEmptyName()
    {
        try
        {
            JSONObject jo = new JSONObject();
            assertEquals("<></>", XML.toString(jo, ""));
        } catch (JSONException e)
        {
        	e.printStackTrace();
        }
    }
    
    /**
     * Tests the toString method using json object with null string value.
     */
    public static void testToString_JsonObjectWithNullStringValue()
    {
        try
        {
            JSONObject jo = new JSONObject();
            jo.put("abc", "null");
            assertEquals("<my name><abc>null</abc></my name>", XML.toString(jo, "my name"));
        } catch (JSONException e)
        {
        	fail(e.toString());
        }
    }
        
    /**
     * Tests the toString method using json object with json object null value.
     */
    public static void testToString_JsonObjectWithJSONObjectNullValue()
    {
        try
        {
            JSONObject jo = new JSONObject();
            jo.put("abc", JSONObject.NULL);
            assertEquals("<my name><abc>null</abc></my name>", XML.toString(jo, "my name"));
        } catch (JSONException e)
        {
        	fail(e.toString());
        }
    }
    
    /**
     * Tests the toString method using json object with null key.
     */
    public static void testToString_JsonObjectWithNullKey()
    {
        try
        {
            JSONObject jo = new JSONObject();
            jo.put(null, "abc");
            XML.toString(jo, "my name");
            fail("Should have thrown Exception");
        } catch (JSONException e)
        {
        	assertEquals("Null key.", e.getMessage());
        }
    }
    
    /**
     * Tests the toString method using json object with integer.
     */
    public static void testToString_JsonObjectWithInteger()
    {
        try
        {
            JSONObject jo = new JSONObject();
            jo.put("abc", 45);
            assertEquals("<my name><abc>45</abc></my name>", XML.toString(jo, "my name"));
        } catch (JSONException e)
        {
        	fail(e.toString());
        }
    }
    
    /**
     * Tests the toString method using json object with content key int value.
     */
    public static void testToString_JsonObjectWithContentKeyIntValue()
    {
        try
        {
            JSONObject jo = new JSONObject();
            jo.put("content", 45);
            assertEquals("<my name>45</my name>", XML.toString(jo, "my name"));
        } catch (JSONException e)
        {
        	fail(e.toString());
        }
    }
    
    /**
     * Tests the toString method using json object with content key json array value.
     */
    public static void testToString_JsonObjectWithContentKeyJsonArrayValue()
    {
        try
        {
            JSONObject jo = new JSONObject();
            JSONArray ja = new JSONArray();
            ja.put("123");
            ja.put(72);
            jo.put("content", ja);
            assertEquals("<my name>123\n72</my name>", XML.toString(jo, "my name"));
        } catch (JSONException e)
        {
        	fail(e.toString());
        }
    }
    
    /**
     * Tests the toString method using json object with content key string value.
     */
    public static void testToString_JsonObjectWithContentKeyStringValue()
    {
        try
        {
            JSONObject jo = new JSONObject();
            jo.put("content", "42");
            assertEquals("<my name>42</my name>", XML.toString(jo, "my name"));
        } catch (JSONException e)
        {
        	fail(e.toString());
        }
    }
    
    /**
     * Tests the toString method using json object with json array value.
     */
    public static void testToString_JsonObjectWithJsonArrayValue()
    {
        try
        {
            JSONObject jo = new JSONObject();
            JSONArray ja = new JSONArray();
            ja.put("123");
            ja.put(72);
            jo.put("abc", ja);
            assertEquals("<my name><abc>123</abc><abc>72</abc></my name>", XML.toString(jo, "my name"));
        } catch (JSONException e)
        {
        	fail(e.toString());
        }
    }
    
    /**
     * Tests the toString method using json object with json array of json arrays value.
     */
    public static void testToString_JsonObjectWithJsonArrayOfJsonArraysValue()
    {
        try
        {
            JSONObject jo = new JSONObject();
            JSONArray ja = new JSONArray();
            JSONArray ja2 = new JSONArray();
            JSONArray ja3 = new JSONArray();
            ja2.put("cat");
            ja.put(ja2);
            ja.put(ja3);
            jo.put("abc", ja);
            assertEquals("<my name><abc><array>cat</array></abc><abc></abc></my name>", XML.toString(jo, "my name"));
        } catch (JSONException e)
        {
        	fail(e.toString());
        }
    }
    
    /**
     * Tests the toString method using array.
     */
    public static void testToString_Array()
    {
        try
        {
            String[] strings = {"abc", "123"};
            assertEquals("<my name>abc</my name><my name>123</my name>", XML.toString(strings, "my name"));
        } catch (JSONException e)
        {
        	fail(e.toString());
        }
    }
    
    /**
     * Tests the toString method using json array.
     */
    public static void testToString_JsonArray()
    {
        try
        {
            JSONArray ja = new JSONArray();
            ja.put("hi");
            ja.put("bye");
            assertEquals("<my name>hi</my name><my name>bye</my name>", XML.toString(ja, "my name"));
        } catch (JSONException e)
        {
        	fail(e.toString());
        }
    }
    
    /**
     * Tests the toString method using empty string.
     */
    public static void testToString_EmptyString()
    {
        try
        {
            assertEquals("<my name/>", XML.toString("", "my name"));
        } catch (JSONException e)
        {
        	fail(e.toString());
        }
    }
    
    /**
     * Tests the toString method using string no name.
     */
    public static void testToString_StringNoName()
    {
        try
        {
            assertEquals("\"123\"", XML.toString("123"));
        } catch (JSONException e)
        {
        	fail(e.toString());
        }
    }
    
    /**
     * Tests the escape method.
     */
    public static void testEscape()
    {
        try
        {
            assertEquals("\"&amp;&lt;&gt;&quot;&apos;\"", XML.toString("&<>\"'"));
        } catch (JSONException e)
        {
        	fail(e.toString());
        }
    }
    
    /**
     * Tests the noSpace method using empty string.
     */
    public static void testNoSpace_EmptyString()
    {
        try
        {
            XML.noSpace("");
            fail("Should have thrown exception");
        } catch (JSONException e)
        {
        	assertEquals("Empty string.", e.getMessage());
        }
    }
    
    /**
     * Tests the noSpace method using string with no spaces.
     */
    public static void testNoSpace_StringWithNoSpaces()
    {
        try
        {
            XML.noSpace("123");
        } catch (JSONException e)
        {
        	fail(e.toString());
        }
    }
    
    /**
     * Tests the noSpace method using string with spaces.
     */
    public static void testNoSpace_StringWithSpaces()
    {
        try
        {
            XML.noSpace("1 23");
            fail("Should have thrown exception");
        } catch (JSONException e)
        {
        	assertEquals("'1 23' contains a space character.", e.getMessage());
        }
    }
    
    /**
     * Test.
     */
    public static void testNoSpace()
    {
        try
        {
            XML.noSpace("1 23");
            fail("Should have thrown exception");
        } catch (JSONException e)
        {
        	assertEquals("'1 23' contains a space character.", e.getMessage());
        }
    }
    
    /**
     * Tests the XML method.
     *
     * @throws Exception the exception
     */
    public void testXML() throws Exception
    {

        jsonobject = XML
                .toJSONObject("<![CDATA[This is a collection of test patterns and examples for json.]]>  Ignore the stuff past the end.  ");
        assertEquals(
                "{\"content\":\"This is a collection of test patterns and examples for json.\"}",
                jsonobject.toString());
        assertEquals(
                "This is a collection of test patterns and examples for json.",
                jsonobject.getString("content"));

        string = "<test><blank></blank><empty/></test>";
        jsonobject = XML.toJSONObject(string);
        assertEquals("{\"test\": {\n  \"blank\": \"\",\n  \"empty\": \"\"\n}}",
                jsonobject.toString(2));
        assertEquals("<test><blank/><empty/></test>", XML.toString(jsonobject));

        string = "<subsonic-response><playlists><playlist id=\"476c65652e6d3375\" int=\"12345678901234567890123456789012345678901234567890213991133777039355058536718668104339937\"/><playlist id=\"50617274792e78737066\"/></playlists></subsonic-response>";
        jsonobject = XML.toJSONObject(string);
        assertEquals(
                "{\"subsonic-response\":{\"playlists\":{\"playlist\":[{\"id\":\"476c65652e6d3375\",\"int\":\"12345678901234567890123456789012345678901234567890213991133777039355058536718668104339937\"},{\"id\":\"50617274792e78737066\"}]}}}",
                jsonobject.toString());
    }
    
    /**
     * Tests the XML2 method.
     */
    public void testXML2()
    {
    	try {
			jsonobject = XML
			        .toJSONObject("<?xml version='1.0' encoding='UTF-8'?>"
			                + "\n\n"
			                + "<SOAP-ENV:Envelope"
			                + " xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\""
			                + " xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\""
			                + " xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">"
			                + "<SOAP-ENV:Body><ns1:doGoogleSearch"
			                + " xmlns:ns1=\"urn:GoogleSearch\""
			                + " SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
			                + "<key xsi:type=\"xsd:string\">GOOGLEKEY</key> <q"
			                + " xsi:type=\"xsd:string\">'+search+'</q> <start"
			                + " xsi:type=\"xsd:int\">0</start> <maxResults"
			                + " xsi:type=\"xsd:int\">10</maxResults> <filter"
			                + " xsi:type=\"xsd:boolean\">true</filter> <restrict"
			                + " xsi:type=\"xsd:string\"></restrict> <safeSearch"
			                + " xsi:type=\"xsd:boolean\">false</safeSearch> <lr"
			                + " xsi:type=\"xsd:string\"></lr> <ie"
			                + " xsi:type=\"xsd:string\">latin1</ie> <oe"
			                + " xsi:type=\"xsd:string\">latin1</oe>"
			                + "</ns1:doGoogleSearch>"
			                + "</SOAP-ENV:Body></SOAP-ENV:Envelope>");
		

        assertEquals(
                "{\"SOAP-ENV:Envelope\": {\n  \"SOAP-ENV:Body\": {\"ns1:doGoogleSearch\": {\n    \"oe\": {\n      \"content\": \"latin1\",\n      \"xsi:type\": \"xsd:string\"\n    },\n    \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\",\n    \"lr\": {\"xsi:type\": \"xsd:string\"},\n    \"start\": {\n      \"content\": 0,\n      \"xsi:type\": \"xsd:int\"\n    },\n    \"q\": {\n      \"content\": \"'+search+'\",\n      \"xsi:type\": \"xsd:string\"\n    },\n    \"ie\": {\n      \"content\": \"latin1\",\n      \"xsi:type\": \"xsd:string\"\n    },\n    \"safeSearch\": {\n      \"content\": false,\n      \"xsi:type\": \"xsd:boolean\"\n    },\n    \"xmlns:ns1\": \"urn:GoogleSearch\",\n    \"restrict\": {\"xsi:type\": \"xsd:string\"},\n    \"filter\": {\n      \"content\": true,\n      \"xsi:type\": \"xsd:boolean\"\n    },\n    \"maxResults\": {\n      \"content\": 10,\n      \"xsi:type\": \"xsd:int\"\n    },\n    \"key\": {\n      \"content\": \"GOOGLEKEY\",\n      \"xsi:type\": \"xsd:string\"\n    }\n  }},\n  \"xmlns:xsd\": \"http://www.w3.org/1999/XMLSchema\",\n  \"xmlns:xsi\": \"http://www.w3.org/1999/XMLSchema-instance\",\n  \"xmlns:SOAP-ENV\": \"http://schemas.xmlsoap.org/soap/envelope/\"\n}}",
                jsonobject.toString(2));

        assertEquals(
                "<SOAP-ENV:Envelope><SOAP-ENV:Body><ns1:doGoogleSearch><oe>latin1<xsi:type>xsd:string</xsi:type></oe><SOAP-ENV:encodingStyle>http://schemas.xmlsoap.org/soap/encoding/</SOAP-ENV:encodingStyle><lr><xsi:type>xsd:string</xsi:type></lr><start>0<xsi:type>xsd:int</xsi:type></start><q>&apos;+search+&apos;<xsi:type>xsd:string</xsi:type></q><ie>latin1<xsi:type>xsd:string</xsi:type></ie><safeSearch>false<xsi:type>xsd:boolean</xsi:type></safeSearch><xmlns:ns1>urn:GoogleSearch</xmlns:ns1><restrict><xsi:type>xsd:string</xsi:type></restrict><filter>true<xsi:type>xsd:boolean</xsi:type></filter><maxResults>10<xsi:type>xsd:int</xsi:type></maxResults><key>GOOGLEKEY<xsi:type>xsd:string</xsi:type></key></ns1:doGoogleSearch></SOAP-ENV:Body><xmlns:xsd>http://www.w3.org/1999/XMLSchema</xmlns:xsd><xmlns:xsi>http://www.w3.org/1999/XMLSchema-instance</xmlns:xsi><xmlns:SOAP-ENV>http://schemas.xmlsoap.org/soap/envelope/</xmlns:SOAP-ENV></SOAP-ENV:Envelope>",
                XML.toString(jsonobject));
    	} catch (JSONException e) {
			fail(e.toString());
		}
    }

    /**
     * Tests the constructor method.
     */
    public static void testConstructor()
    {
        XML xml = new XML();
        assertEquals("XML", xml.getClass().getSimpleName());
    }
    
    /**
     * Tests the toJSONObject method using unclosed tag.
     */
    public void testToJSONObject_UnclosedTag()
    {
        try {
            jsonobject = XML.toJSONObject("<a><b>    ");
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Unclosed tag b at 11 [character 12 line 1]",
                    jsone.getMessage());
        }
    }
    
    /**
     * Tests the toJSONObject method using mismatched tags.
     */
    public void testToJSONObject_MismatchedTags()
    {
        try {
            jsonobject = XML.toJSONObject("<a></b>    ");
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Mismatched a and b at 6 [character 7 line 1]",
                    jsone.getMessage());
        }
    }
    
    /**
     * Tests the toJSONObject method using open tag.
     */
    public void testToJSONObject_OpenTag()
    {
        try {
            jsonobject = XML.toJSONObject("<a></a    ");
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Misshaped element at 11 [character 12 line 1]",
                    jsone.getMessage());
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
            assertEquals(
                    "<list of lists><array>1</array><array>2</array><array>3</array></list of lists><list of lists><array>4</array><array>5</array><array>6</array></list of lists>",
                    XML.toString(jsonobject));
        } catch (JSONException e)
        {
            fail(e.toString());
        }
    }

    /**
     * Tests the toJSONObject method using xml recipe.
     */
    public void testToJSONObject_XmlRecipe()
    {
        try
        {
            string = "<recipe name=\"bread\" prep_time=\"5 mins\" cook_time=\"3 hours\"> <title>Basic bread</title> <ingredient amount=\"8\" unit=\"dL\">Flour</ingredient> <ingredient amount=\"10\" unit=\"grams\">Yeast</ingredient> <ingredient amount=\"4\" unit=\"dL\" state=\"warm\">Water</ingredient> <ingredient amount=\"1\" unit=\"teaspoon\">Salt</ingredient> <instructions> <step>Mix all ingredients together.</step> <step>Knead thoroughly.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Knead again.</step> <step>Place in a bread baking tin.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Bake in the oven at 180(degrees)C for 30 minutes.</step> </instructions> </recipe> ";
            jsonobject = XML.toJSONObject(string);
            assertEquals(
                    "{\"recipe\": {\n    \"title\": \"Basic bread\",\n    \"cook_time\": \"3 hours\",\n    \"instructions\": {\"step\": [\n        \"Mix all ingredients together.\",\n        \"Knead thoroughly.\",\n        \"Cover with a cloth, and leave for one hour in warm room.\",\n        \"Knead again.\",\n        \"Place in a bread baking tin.\",\n        \"Cover with a cloth, and leave for one hour in warm room.\",\n        \"Bake in the oven at 180(degrees)C for 30 minutes.\"\n    ]},\n    \"name\": \"bread\",\n    \"ingredient\": [\n        {\n            \"content\": \"Flour\",\n            \"amount\": 8,\n            \"unit\": \"dL\"\n        },\n        {\n            \"content\": \"Yeast\",\n            \"amount\": 10,\n            \"unit\": \"grams\"\n        },\n        {\n            \"content\": \"Water\",\n            \"amount\": 4,\n            \"unit\": \"dL\",\n            \"state\": \"warm\"\n        },\n        {\n            \"content\": \"Salt\",\n            \"amount\": 1,\n            \"unit\": \"teaspoon\"\n        }\n    ],\n    \"prep_time\": \"5 mins\"\n}}",
                    jsonobject.toString(4));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the toJSONObject method using personel record.
     */
    public void testToJSONObject_PersonelRecord()
    {
        try
        {
            string = "<person created=\"2006-11-11T19:23\" modified=\"2006-12-31T23:59\">\n <firstName>Robert</firstName>\n <lastName>Smith</lastName>\n <address type=\"home\">\n <street>12345 Sixth Ave</street>\n <city>Anytown</city>\n <state>CA</state>\n <postalCode>98765-4321</postalCode>\n </address>\n </person>";
            jsonobject = XML.toJSONObject(string);
            assertEquals(
                    "{\"person\": {\n    \"lastName\": \"Smith\",\n    \"address\": {\n        \"postalCode\": \"98765-4321\",\n        \"street\": \"12345 Sixth Ave\",\n        \"state\": \"CA\",\n        \"type\": \"home\",\n        \"city\": \"Anytown\"\n    },\n    \"created\": \"2006-11-11T19:23\",\n    \"firstName\": \"Robert\",\n    \"modified\": \"2006-12-31T23:59\"\n}}",
                    jsonobject.toString(4));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the toString method using symbols.
     */
    public void testToString_Symbols()
    {
        try
        {
            jsonobject = new JSONObject(
                    "{slashes: '///', closetag: '</script>', backslash:'\\\\', ei: {quotes: '\"\\''},eo: {a: '\"quoted\"', b:\"don't\"}, quotes: [\"'\", '\"']}");
            assertEquals(
                    "{\n  \"quotes\": [\n    \"'\",\n    \"\\\"\"\n  ],\n  \"slashes\": \"///\",\n  \"ei\": {\"quotes\": \"\\\"'\"},\n  \"eo\": {\n    \"b\": \"don't\",\n    \"a\": \"\\\"quoted\\\"\"\n  },\n  \"closetag\": \"<\\/script>\",\n  \"backslash\": \"\\\\\"\n}",
                    jsonobject.toString(2));
            assertEquals(
                    "<quotes>&apos;</quotes><quotes>&quot;</quotes><slashes>///</slashes><ei><quotes>&quot;&apos;</quotes></ei><eo><b>don&apos;t</b><a>&quot;quoted&quot;</a></eo><closetag>&lt;/script&gt;</closetag><backslash>\\</backslash>",
                    XML.toString(jsonobject));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the toJsonObject method using json information.
     */
    public void testToJsonObject_JsonInformation()
    {
        try
        {
            string = "<xml one = 1 two=' \"2\" '><five></five>First \u0009&lt;content&gt;<five></five> This is \"content\". <three>  3  </three>JSON does not preserve the sequencing of elements and contents.<three>  III  </three>  <three>  T H R E E</three><four/>Content text is an implied structure in XML. <six content=\"6\"/>JSON does not have implied structure:<seven>7</seven>everything is explicit.<![CDATA[CDATA blocks<are><supported>!]]></xml>";
            jsonobject = XML.toJSONObject(string);
            assertEquals(
                    "{\"xml\": {\n  \"content\": [\n    \"First \\t<content>\",\n    \"This is \\\"content\\\".\",\n    \"JSON does not preserve the sequencing of elements and contents.\",\n    \"Content text is an implied structure in XML.\",\n    \"JSON does not have implied structure:\",\n    \"everything is explicit.\",\n    \"CDATA blocks<are><supported>!\"\n  ],\n  \"two\": \" \\\"2\\\" \",\n  \"seven\": 7,\n  \"five\": [\n    \"\",\n    \"\"\n  ],\n  \"one\": 1,\n  \"three\": [\n    3,\n    \"III\",\n    \"T H R E E\"\n  ],\n  \"four\": \"\",\n  \"six\": {\"content\": 6}\n}}",
                    jsonobject.toString(2));
            assertEquals(
                    "<xml>First \t&lt;content&gt;\n"
                            + "This is &quot;content&quot;.\n"
                            + "JSON does not preserve the sequencing of elements and contents.\n"
                            + "Content text is an implied structure in XML.\n"
                            + "JSON does not have implied structure:\n"
                            + "everything is explicit.\n"
                            + "CDATA blocks&lt;are&gt;&lt;supported&gt;!<two> &quot;2&quot; </two><seven>7</seven><five/><five/><one>1</one><three>3</three><three>III</three><three>T H R E E</three><four/><six>6</six></xml>",
                    XML.toString(jsonobject));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the toString method using json array of int array.
     */
    public void testToString_JsonArrayOfIntArray()
    {
        try
        {
            int ar[] =
            { 1, 2, 3 };
            jsonarray = new JSONArray(ar);
            assertEquals("[1,2,3]", jsonarray.toString());
            assertEquals("<array>1</array><array>2</array><array>3</array>",
                    XML.toString(ar));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the toString method using table mapping.
     */
    public void testToString_TableMapping()
    {
        try
        {
            string = "<mapping><empty/>   <class name = \"Customer\">      <field name = \"ID\" type = \"string\">         <bind-xml name=\"ID\" node=\"attribute\"/>      </field>      <field name = \"FirstName\" type = \"FirstName\"/>      <field name = \"MI\" type = \"MI\"/>      <field name = \"LastName\" type = \"LastName\"/>   </class>   <class name = \"FirstName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"MI\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"LastName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class></mapping>";
            jsonobject = XML.toJSONObject(string);

            assertEquals(
                    "{\"mapping\": {\n  \"empty\": \"\",\n  \"class\": [\n    {\n      \"field\": [\n        {\n          \"bind-xml\": {\n            \"node\": \"attribute\",\n            \"name\": \"ID\"\n          },\n          \"name\": \"ID\",\n          \"type\": \"string\"\n        },\n        {\n          \"name\": \"FirstName\",\n          \"type\": \"FirstName\"\n        },\n        {\n          \"name\": \"MI\",\n          \"type\": \"MI\"\n        },\n        {\n          \"name\": \"LastName\",\n          \"type\": \"LastName\"\n        }\n      ],\n      \"name\": \"Customer\"\n    },\n    {\n      \"field\": {\n        \"bind-xml\": {\n          \"node\": \"text\",\n          \"name\": \"text\"\n        },\n        \"name\": \"text\"\n      },\n      \"name\": \"FirstName\"\n    },\n    {\n      \"field\": {\n        \"bind-xml\": {\n          \"node\": \"text\",\n          \"name\": \"text\"\n        },\n        \"name\": \"text\"\n      },\n      \"name\": \"MI\"\n    },\n    {\n      \"field\": {\n        \"bind-xml\": {\n          \"node\": \"text\",\n          \"name\": \"text\"\n        },\n        \"name\": \"text\"\n      },\n      \"name\": \"LastName\"\n    }\n  ]\n}}",
                    jsonobject.toString(2));
            assertEquals(
                    "<mapping><empty/><class><field><bind-xml><node>attribute</node><name>ID</name></bind-xml><name>ID</name><type>string</type></field><field><name>FirstName</name><type>FirstName</type></field><field><name>MI</name><type>MI</type></field><field><name>LastName</name><type>LastName</type></field><name>Customer</name></class><class><field><bind-xml><node>text</node><name>text</name></bind-xml><name>text</name></field><name>FirstName</name></class><class><field><bind-xml><node>text</node><name>text</name></bind-xml><name>text</name></field><name>MI</name></class><class><field><bind-xml><node>text</node><name>text</name></bind-xml><name>text</name></field><name>LastName</name></class></mapping>",
                    XML.toString(jsonobject));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the toString method using book info.
     */
    public void testToString_BookInfo()
    {
        try
        {
            jsonobject = XML
                    .toJSONObject("<?xml version=\"1.0\" ?><Book Author=\"Anonymous\"><Title>Sample Book</Title><Chapter id=\"1\">This is chapter 1. It is not very long or interesting.</Chapter><Chapter id=\"2\">This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.</Chapter></Book>");
            assertEquals(
                    "{\"Book\": {\n  \"Chapter\": [\n    {\n      \"content\": \"This is chapter 1. It is not very long or interesting.\",\n      \"id\": 1\n    },\n    {\n      \"content\": \"This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.\",\n      \"id\": 2\n    }\n  ],\n  \"Author\": \"Anonymous\",\n  \"Title\": \"Sample Book\"\n}}",
                    jsonobject.toString(2));
            assertEquals(
                    "<Book><Chapter>This is chapter 1. It is not very long or interesting.<id>1</id></Chapter><Chapter>This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.<id>2</id></Chapter><Author>Anonymous</Author><Title>Sample Book</Title></Book>",
                    XML.toString(jsonobject));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the toJsonObject method using table of contents.
     */
    public void testToJsonObject_TableOfContents()
    {
        try
        {
            string = "<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter      <chapter>Content of the first subchapter</chapter>      <chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>";
            jsonobject = XML.toJSONObject(string);
            assertEquals(
                    "{\"book\": {\"chapter\": [\n \"Content of the first chapter\",\n {\n  \"content\": \"Content of the second chapter\",\n  \"chapter\": [\n   \"Content of the first subchapter\",\n   \"Content of the second subchapter\"\n  ]\n },\n \"Third Chapter\"\n]}}",
                    jsonobject.toString(1));
            assertEquals(
                    "<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter<chapter>Content of the first subchapter</chapter><chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>",
                    XML.toString(jsonobject));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the toJsonObject method using simple xml.
     */
    public void testToJsonObject_SimpleXml()
    {
        try
        {
            string = "<xml empty><a></a><a>1</a><a>22</a><a>333</a></xml>";
            jsonobject = XML.toJSONObject(string);
            assertEquals("{\"xml\": {\n" + "    \"a\": [\n" + "        \"\",\n"
                    + "        1,\n" + "        22,\n" + "        333\n"
                    + "    ],\n" + "    \"empty\": \"\"\n" + "}}",
                    jsonobject.toString(4));
            assertEquals("<xml><a/><a>1</a><a>22</a><a>333</a><empty/></xml>",
                    XML.toString(jsonobject));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the toJsonObject method using html escapes.
     */
    public void testToJsonObject_HtmlEscapes()
    {
        try
        {
            jsonobject = XML
                    .toJSONObject("<test intertag zero=0 status=ok><empty/>deluxe<blip sweet=true>&amp;&quot;toot&quot;&toot;&#x41;</blip><x>eks</x><w>bonus</w><w>bonus2</w></test>");
            assertEquals(
                    "{\"test\": {\n  \"w\": [\n    \"bonus\",\n    \"bonus2\"\n  ],\n  \"content\": \"deluxe\",\n  \"intertag\": \"\",\n  \"status\": \"ok\",\n  \"blip\": {\n    \"content\": \"&\\\"toot\\\"&toot;&#x41;\",\n    \"sweet\": true\n  },\n  \"empty\": \"\",\n  \"zero\": 0,\n  \"x\": \"eks\"\n}}",
                    jsonobject.toString(2));
            assertEquals(
                    "<test><w>bonus</w><w>bonus2</w>deluxe<intertag/><status>ok</status><blip>&amp;&quot;toot&quot;&amp;toot;&amp;#x41;<sweet>true</sweet></blip><empty/><zero>0</zero><x>eks</x></test>",
                    XML.toString(jsonobject));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the toJsonObject method using phone book.
     */
    public void testToJsonObject_PhoneBook()
    {
        try
        {
            jsonobject = XML
                    .toJSONObject("<!DOCTYPE bCard 'http://www.cs.caltech.edu/~adam/schemas/bCard'><bCard><?xml default bCard        firstname = ''        lastname  = '' company   = '' email = '' homepage  = ''?><bCard        firstname = 'Rohit'        lastname  = 'Khare'        company   = 'MCI'        email     = 'khare@mci.net'        homepage  = 'http://pest.w3.org/'/><bCard        firstname = 'Adam'        lastname  = 'Rifkin'        company   = 'Caltech Infospheres Project'        email     = 'adam@cs.caltech.edu'        homepage  = 'http://www.cs.caltech.edu/~adam/'/></bCard>");
            assertEquals(
                    "{\"bCard\": {\"bCard\": [\n  {\n    \"email\": \"khare@mci.net\",\n    \"company\": \"MCI\",\n    \"lastname\": \"Khare\",\n    \"firstname\": \"Rohit\",\n    \"homepage\": \"http://pest.w3.org/\"\n  },\n  {\n    \"email\": \"adam@cs.caltech.edu\",\n    \"company\": \"Caltech Infospheres Project\",\n    \"lastname\": \"Rifkin\",\n    \"firstname\": \"Adam\",\n    \"homepage\": \"http://www.cs.caltech.edu/~adam/\"\n  }\n]}}",
                    jsonobject.toString(2));
            assertEquals(
                    "<bCard><bCard><email>khare@mci.net</email><company>MCI</company><lastname>Khare</lastname><firstname>Rohit</firstname><homepage>http://pest.w3.org/</homepage></bCard><bCard><email>adam@cs.caltech.edu</email><company>Caltech Infospheres Project</company><lastname>Rifkin</lastname><firstname>Adam</firstname><homepage>http://www.cs.caltech.edu/~adam/</homepage></bCard></bCard>",
                    XML.toString(jsonobject));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the toJsonObject method using customer info.
     */
    public void testToJsonObject_CustomerInfo()
    {
        try
        {
            jsonobject = XML
                    .toJSONObject("<?xml version=\"1.0\"?><customer>    <firstName>        <text>Fred</text>    </firstName>    <ID>fbs0001</ID>    <lastName> <text>Scerbo</text>    </lastName>    <MI>        <text>B</text>    </MI></customer>");
            assertEquals(
                    "{\"customer\": {\n  \"lastName\": {\"text\": \"Scerbo\"},\n  \"MI\": {\"text\": \"B\"},\n  \"ID\": \"fbs0001\",\n  \"firstName\": {\"text\": \"Fred\"}\n}}",
                    jsonobject.toString(2));
            assertEquals(
                    "<customer><lastName><text>Scerbo</text></lastName><MI><text>B</text></MI><ID>fbs0001</ID><firstName><text>Fred</text></firstName></customer>",
                    XML.toString(jsonobject));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the toJsonObject method using library catalog.
     */
    public void testToJsonObject_LibraryCatalog()
    {
        try
        {
            jsonobject = XML
                    .toJSONObject("<!ENTITY tp-address PUBLIC '-//ABC University::Special Collections Library//TEXT (titlepage: name and address)//EN' 'tpspcoll.sgm'><list type='simple'><head>Repository Address </head><item>Special Collections Library</item><item>ABC University</item><item>Main Library, 40 Circle Drive</item><item>Ourtown, Pennsylvania</item><item>17654 USA</item></list>");
            assertEquals(
                    "{\"list\":{\"item\":[\"Special Collections Library\",\"ABC University\",\"Main Library, 40 Circle Drive\",\"Ourtown, Pennsylvania\",\"17654 USA\"],\"head\":\"Repository Address\",\"type\":\"simple\"}}",
                    jsonobject.toString());
            assertEquals(
                    "<list><item>Special Collections Library</item><item>ABC University</item><item>Main Library, 40 Circle Drive</item><item>Ourtown, Pennsylvania</item><item>17654 USA</item><head>Repository Address</head><type>simple</type></list>",
                    XML.toString(jsonobject));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the toString method using xml within xml.
     */
    public void testToString_XmlWithinXml()
    {
        try{
            jsonarray = new JSONArray(
                    " [\"<escape>\", next is an implied null , , ok,] ");
            assertEquals("[\"<escape>\",\"next is an implied null\",null,\"ok\"]",
                    jsonarray.toString());
            assertEquals(
                    "<array>&lt;escape&gt;</array><array>next is an implied null</array><array>null</array><array>ok</array>",
                    XML.toString(jsonarray));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the toString method using email.
     */
    public void testToString_Email()
    {
        try{
            jsonobject = new JSONObject(
                    "{Envelope: {Body: {\"ns1:doGoogleSearch\": {oe: \"latin1\", filter: true, q: \"'+search+'\", key: \"GOOGLEKEY\", maxResults: 10, \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\", start: 0, ie: \"latin1\", safeSearch:false, \"xmlns:ns1\": \"urn:GoogleSearch\"}}}}");
            assertEquals(
                    "{\"Envelope\": {\"Body\": {\"ns1:doGoogleSearch\": {\n  \"oe\": \"latin1\",\n  \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\",\n  \"start\": 0,\n  \"q\": \"'+search+'\",\n  \"ie\": \"latin1\",\n  \"safeSearch\": false,\n  \"xmlns:ns1\": \"urn:GoogleSearch\",\n  \"maxResults\": 10,\n  \"key\": \"GOOGLEKEY\",\n  \"filter\": true\n}}}}",
                    jsonobject.toString(2));
            assertEquals(
                    "<Envelope><Body><ns1:doGoogleSearch><oe>latin1</oe><SOAP-ENV:encodingStyle>http://schemas.xmlsoap.org/soap/encoding/</SOAP-ENV:encodingStyle><start>0</start><q>&apos;+search+&apos;</q><ie>latin1</ie><safeSearch>false</safeSearch><xmlns:ns1>urn:GoogleSearch</xmlns:ns1><maxResults>10</maxResults><key>GOOGLEKEY</key><filter>true</filter></ns1:doGoogleSearch></Body></Envelope>",
                    XML.toString(jsonobject));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the toString method using http header.
     */
    public void testToString_HttpHeader()
    {
        try{
            jsonobject = new JSONObject(
                    "{nix: null, nux: false, null: 'null', 'Request-URI': '/', Method: 'GET', 'HTTP-Version': 'HTTP/1.0'}");
            assertEquals(
                    "{\n  \"Request-URI\": \"/\",\n  \"nix\": null,\n  \"nux\": false,\n  \"Method\": \"GET\",\n  \"HTTP-Version\": \"HTTP/1.0\",\n  \"null\": \"null\"\n}",
                    jsonobject.toString(2));
            assertTrue(jsonobject.isNull("nix"));
            assertTrue(jsonobject.has("nix"));
            assertEquals(
                    "<Request-URI>/</Request-URI><nix>null</nix><nux>false</nux><Method>GET</Method><HTTP-Version>HTTP/1.0</HTTP-Version><null>null</null>",
                    XML.toString(jsonobject));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
}
