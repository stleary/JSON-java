package org.json.junit;

/*
Copyright (c) 2020 JSON.org

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.JSONPointerException;
import org.json.JSONTokener;
import org.junit.Test;

public class JSONPointerTest {

    private static final JSONObject document;
    private static final String EXPECTED_COMPLETE_DOCUMENT = "{\"\":0,\" \":7,\"g|h\":4,\"c%d\":2,\"k\\\"l\":6,\"a/b\":1,\"i\\\\j\":5," +
    		"\"obj\":{\"\":{\"\":\"empty key of an object with an empty key\",\"subKey\":\"Some other value\"}," +
            "\"other~key\":{\"another/key\":[\"val\"]},\"key\":\"value\"},\"foo\":[\"bar\",\"baz\"],\"e^f\":3," +
            "\"m~n\":8}";

    
    static {
        @SuppressWarnings("resource")
        InputStream resourceAsStream = JSONPointerTest.class.getClassLoader().getResourceAsStream("jsonpointer-testdoc.json");
        if(resourceAsStream == null) {
            throw new ExceptionInInitializerError("Unable to locate test file. Please check your development environment configuration");
        }
        document = new JSONObject(new JSONTokener(resourceAsStream));
    }

    private Object query(String pointer) {
        return new JSONPointer(pointer).queryFrom(document);
    }

    @Test
    public void emptyPointer() {
        assertTrue(new JSONObject(EXPECTED_COMPLETE_DOCUMENT).similar(query("")));
    }

    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void nullPointer() {
        new JSONPointer((String) null);
    }

    @Test
    public void objectPropertyQuery() {
        assertEquals("[\"bar\",\"baz\"]", query("/foo").toString());
    }

    @Test
    public void arrayIndexQuery() {
        assertEquals("bar", query("/foo/0"));
    }

    @Test(expected = JSONPointerException.class)
    public void stringPropOfArrayFailure() {
        query("/foo/bar");
    }

    @Test
    public void queryByEmptyKey() {
        assertEquals(0, query("/"));
    }

    @Test
    public void queryByEmptyKeySubObject() {
        assertEquals( "{\"\":\"empty key of an object with an empty key\",\"subKey\":\"Some" +
                " other value\"}", query("/obj/").toString());
    }

    @Test
    public void queryByEmptyKeySubObjectSubOject() {
        assertEquals("empty key of an object with an empty key", query("/obj//"));
    }
    
    @Test
    public void queryByEmptyKeySubObjectValue() {
        assertEquals("Some other value", query("/obj//subKey"));
    }

    @Test
    public void slashEscaping() {
        assertEquals(1, query("/a~1b"));
    }

    @Test
    public void tildeEscaping() {
        assertEquals(8, query("/m~0n"));
    }

    /**
     * We pass backslashes as-is
     * 
     * @see <a href="https://tools.ietf.org/html/rfc6901#section-3">rfc6901 section 3</a>
     */
    @Test
    public void backslashHandling() {
        assertEquals(5, query("/i\\j"));
    }
    
    /**
     * We pass quotations as-is
     * 
     * @see <a href="https://tools.ietf.org/html/rfc6901#section-3">rfc6901 section 3</a>
     */
    @Test
    public void quotationHandling() {
        assertEquals(6, query("/k\"l"));
    }
   
    @Test
    public void whitespaceKey() {
        assertEquals(7, query("/ "));
    }

    @Test
    public void uriFragmentNotation() {
        assertEquals("[\"bar\",\"baz\"]", query("#/foo").toString());
    }

    @Test
    public void uriFragmentNotationRoot() {
        assertTrue(new JSONObject(EXPECTED_COMPLETE_DOCUMENT).similar(query("#")));
    }

    @Test
    public void uriFragmentPercentHandling() {
        assertEquals(2, query("#/c%25d"));
        assertEquals(3, query("#/e%5Ef"));
        assertEquals(4, query("#/g%7Ch"));
        assertEquals(8, query("#/m~0n"));
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void syntaxError() {
        new JSONPointer("key");
    }

    @Test(expected = JSONPointerException.class)
    public void arrayIndexFailure() {
        query("/foo/2");
    }

    @Test(expected = JSONPointerException.class)
    public void primitiveFailure() {
        query("/obj/key/failure");
    }
    
    @Test
    public void builderTest() {
        JSONPointer pointer = JSONPointer.builder()
                .append("obj")
                .append("other~key").append("another/key")
                .append(0)
                .build();
        assertEquals("val", pointer.queryFrom(document));
    }
    
    @Test(expected = NullPointerException.class)
    public void nullToken() {
        JSONPointer.builder().append(null);
    }
    
    @Test
    public void toStringEscaping() {
        JSONPointer pointer = JSONPointer.builder()
                .append("obj")
                .append("other~key").append("another/key")
                .append("\"")
                .append(0)
                .build();
        assertEquals("/obj/other~0key/another~1key/\"/0", pointer.toString());
    }
    
    @Test
    public void emptyPointerToString() {
        assertEquals("", new JSONPointer("").toString());
    }
    
    @Test
    public void toURIFragment() {
        assertEquals("#/c%25d", new JSONPointer("/c%d").toURIFragment());
        assertEquals("#/e%5Ef", new JSONPointer("/e^f").toURIFragment());
        assertEquals("#/g%7Ch", new JSONPointer("/g|h").toURIFragment());
        assertEquals("#/m%7En", new JSONPointer("/m~n").toURIFragment());
    }
    
    @Test
    public void tokenListIsCopiedInConstructor() {
        JSONPointer.Builder b = JSONPointer.builder().append("key1");
        JSONPointer jp1 = b.build();
        b.append("key2");
        JSONPointer jp2 = b.build();
        if(jp1.toString().equals(jp2.toString())) {
            fail("Oops, my pointers are sharing a backing array");
        }
    }

    /**
     * Coverage for JSONObject query(String)
     */
    @Test
    public void queryFromJSONObject() {
        String str = "{"+
                "\"stringKey\":\"hello world!\","+
                "\"arrayKey\":[0,1,2],"+
                "\"objectKey\": {"+
                    "\"a\":\"aVal\","+
                    "\"b\":\"bVal\""+
                "}"+
            "}";    
        JSONObject jsonObject = new JSONObject(str);
        Object obj = jsonObject.query("/stringKey");
        assertTrue("Expected 'hello world!'", "hello world!".equals(obj));
        obj = jsonObject.query("/arrayKey/1");
        assertTrue("Expected 1", Integer.valueOf(1).equals(obj));
        obj = jsonObject.query("/objectKey/b");
        assertTrue("Expected bVal", "bVal".equals(obj));
        try {
            obj = jsonObject.query("/a/b/c");
            assertTrue("Expected JSONPointerException", false);
        } catch (JSONPointerException e) {
            assertTrue("Expected bad key/value exception",
                    "value [null] is not an array or object therefore its key b cannot be resolved".
                    equals(e.getMessage()));
        }
    }

    /**
     * Coverage for JSONObject query(JSONPointer)
     */
    @Test
    public void queryFromJSONObjectUsingPointer() {
        String str = "{"+
                "\"stringKey\":\"hello world!\","+
                "\"arrayKey\":[0,1,2],"+
                "\"objectKey\": {"+
                    "\"a\":\"aVal\","+
                    "\"b\":\"bVal\""+
                "}"+
            "}";    
        JSONObject jsonObject = new JSONObject(str);
        Object obj = jsonObject.query(new JSONPointer("/stringKey"));
        assertTrue("Expected 'hello world!'", "hello world!".equals(obj));
        obj = jsonObject.query(new JSONPointer("/arrayKey/1"));
        assertTrue("Expected 1", Integer.valueOf(1).equals(obj));
        obj = jsonObject.query(new JSONPointer("/objectKey/b"));
        assertTrue("Expected bVal", "bVal".equals(obj));
        try {
            obj = jsonObject.query(new JSONPointer("/a/b/c"));
            assertTrue("Expected JSONPointerException", false);
        } catch (JSONPointerException e) {
            assertTrue("Expected bad key/value exception",
                    "value [null] is not an array or object therefore its key b cannot be resolved".
                    equals(e.getMessage()));
        }
    }

    /**
     * Coverage for JSONObject optQuery(JSONPointer)
     */
    @Test
    public void optQueryFromJSONObjectUsingPointer() {
        String str = "{"+
                "\"stringKey\":\"hello world!\","+
                "\"arrayKey\":[0,1,2],"+
                "\"objectKey\": {"+
                    "\"a\":\"aVal\","+
                    "\"b\":\"bVal\""+
                "}"+
            "}";    
        JSONObject jsonObject = new JSONObject(str);
        Object obj = jsonObject.optQuery(new JSONPointer("/stringKey"));
        assertTrue("Expected 'hello world!'", "hello world!".equals(obj));
        obj = jsonObject.optQuery(new JSONPointer("/arrayKey/1"));
        assertTrue("Expected 1", Integer.valueOf(1).equals(obj));
        obj = jsonObject.optQuery(new JSONPointer("/objectKey/b"));
        assertTrue("Expected bVal", "bVal".equals(obj));
        obj = jsonObject.optQuery(new JSONPointer("/a/b/c"));
        assertTrue("Expected null", obj == null);
    }
    
    /**
     * Coverage for JSONArray query(String)
     */
    @Test
    public void queryFromJSONArray() {
        String str = "["+
                "\"hello world!\","+
                "[0,1,2],"+
                "{"+
                    "\"a\":\"aVal\","+
                    "\"b\":\"bVal\""+
                "}"+
            "]";    
        JSONArray jsonArray = new JSONArray(str);
        Object obj = jsonArray.query("/0");
        assertTrue("Expected 'hello world!'", "hello world!".equals(obj));
        obj = jsonArray.query("/1/1");
        assertTrue("Expected 1", Integer.valueOf(1).equals(obj));
        obj = jsonArray.query("/2/b");
        assertTrue("Expected bVal", "bVal".equals(obj));
        try {
            obj = jsonArray.query("/a/b/c");
            assertTrue("Expected JSONPointerException", false);
        } catch (JSONPointerException e) {
            assertTrue("Expected bad index exception",
                    "a is not an array index".equals(e.getMessage()));
        }
    }

    /**
     * Coverage for JSONArray query(JSONPointer)
     */
    @Test
    public void queryFromJSONArrayUsingPointer() {
        String str = "["+
                "\"hello world!\","+
                "[0,1,2],"+
                "{"+
                    "\"a\":\"aVal\","+
                    "\"b\":\"bVal\""+
                "}"+
            "]";    
        JSONArray jsonArray = new JSONArray(str);
        Object obj = jsonArray.query(new JSONPointer("/0"));
        assertTrue("Expected 'hello world!'", "hello world!".equals(obj));
        obj = jsonArray.query(new JSONPointer("/1/1"));
        assertTrue("Expected 1", Integer.valueOf(1).equals(obj));
        obj = jsonArray.query(new JSONPointer("/2/b"));
        assertTrue("Expected bVal", "bVal".equals(obj));
        try {
            obj = jsonArray.query(new JSONPointer("/a/b/c"));
            assertTrue("Expected JSONPointerException", false);
        } catch (JSONPointerException e) {
            assertTrue("Expected bad index exception",
                    "a is not an array index".equals(e.getMessage()));
        }
    }

    /**
     * Coverage for JSONArray optQuery(JSONPointer)
     */
    @Test
    public void optQueryFromJSONArrayUsingPointer() {
        String str = "["+
                "\"hello world!\","+
                "[0,1,2],"+
                "{"+
                    "\"a\":\"aVal\","+
                    "\"b\":\"bVal\""+
                "}"+
            "]";    
        JSONArray jsonArray = new JSONArray(str);
        Object obj = jsonArray.optQuery(new JSONPointer("/0"));
        assertTrue("Expected 'hello world!'", "hello world!".equals(obj));
        obj = jsonArray.optQuery(new JSONPointer("/1/1"));
        assertTrue("Expected 1", Integer.valueOf(1).equals(obj));
        obj = jsonArray.optQuery(new JSONPointer("/2/b"));
        assertTrue("Expected bVal", "bVal".equals(obj));
        obj = jsonArray.optQuery(new JSONPointer("/a/b/c"));
        assertTrue("Expected null", obj == null);
    }
    
    /**
     * When creating a jsonObject we need to parse escaped characters "\\\\"
     *  --> it's the string representation of  "\\", so when query'ing via the JSONPointer 
     *  we DON'T escape them
     *  
     */
    @Test
    public void queryFromJSONObjectUsingPointer0() {
    	String str = "{"+
                "\"string\\\\\\\\Key\":\"hello world!\","+

                "\"\\\\\":\"slash test\"," + 
                "}"+
                "}";
            JSONObject jsonObject = new JSONObject(str);
            //Summary of issue: When a KEY in the jsonObject is "\\\\" --> it's held
            // as "\\" which means when querying, we need to use "\\"
            Object twoBackslahObj = jsonObject.optQuery(new JSONPointer("/\\"));
            assertEquals("slash test", twoBackslahObj);

            Object fourBackslashObj = jsonObject.optQuery(new JSONPointer("/string\\\\Key"));
            assertEquals("hello world!", fourBackslashObj);
    }
}
