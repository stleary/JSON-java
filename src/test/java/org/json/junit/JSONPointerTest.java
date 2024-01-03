package org.json.junit;

/*
Public Domain.
*/

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.JSONPointerException;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;

class JSONPointerTest {

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
    void emptyPointer() {
        assertTrue(new JSONObject(EXPECTED_COMPLETE_DOCUMENT).similar(query("")));
    }

    @SuppressWarnings("unused")
    @Test
    void nullPointer() {
        assertThrows(NullPointerException.class, () -> {
            new JSONPointer((String)null);
        });
    }

    @Test
    void objectPropertyQuery() {
        assertEquals("[\"bar\",\"baz\"]", query("/foo").toString());
    }

    @Test
    void arrayIndexQuery() {
        assertEquals("bar", query("/foo/0"));
    }

    @Test
    void stringPropOfArrayFailure() {
        assertThrows(JSONPointerException.class, () -> {
            query("/foo/bar");
        });
    }

    @Test
    void queryByEmptyKey() {
        assertEquals(0, query("/"));
    }

    @Test
    void queryByEmptyKeySubObject() {
        JSONObject json = new JSONObject("{\"\":\"empty key of an object with an empty key\",\"subKey\":\"Some" +
                " other value\"}");
        JSONObject obj = (JSONObject) query("/obj/");
        assertTrue(json.similar(obj));
    }

    @Test
    void queryByEmptyKeySubObjectSubOject() {
        assertEquals("empty key of an object with an empty key", query("/obj//"));
    }

    @Test
    void queryByEmptyKeySubObjectValue() {
        assertEquals("Some other value", query("/obj//subKey"));
    }

    @Test
    void slashEscaping() {
        assertEquals(1, query("/a~1b"));
    }

    @Test
    void tildeEscaping() {
        assertEquals(8, query("/m~0n"));
    }

    /**
     * We pass backslashes as-is
     * 
     * @see <a href="https://tools.ietf.org/html/rfc6901#section-3">rfc6901 section 3</a>
     */
    @Test
    void backslashHandling() {
        assertEquals(5, query("/i\\j"));
    }

    /**
     * We pass quotations as-is
     * 
     * @see <a href="https://tools.ietf.org/html/rfc6901#section-3">rfc6901 section 3</a>
     */
    @Test
    void quotationHandling() {
        assertEquals(6, query("/k\"l"));
    }

    @Test
    void whitespaceKey() {
        assertEquals(7, query("/ "));
    }

    @Test
    void uriFragmentNotation() {
        assertEquals("[\"bar\",\"baz\"]", query("#/foo").toString());
    }

    @Test
    void uriFragmentNotationRoot() {
        assertTrue(new JSONObject(EXPECTED_COMPLETE_DOCUMENT).similar(query("#")));
    }

    @Test
    void uriFragmentPercentHandling() {
        assertEquals(2, query("#/c%25d"));
        assertEquals(3, query("#/e%5Ef"));
        assertEquals(4, query("#/g%7Ch"));
        assertEquals(8, query("#/m~0n"));
    }

    @SuppressWarnings("unused")
    @Test
    void syntaxError() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JSONPointer("key");
        });
    }

    @Test
    void arrayIndexFailure() {
        assertThrows(JSONPointerException.class, () -> {
            query("/foo/2");
        });
    }

    @Test
    void primitiveFailure() {
        assertThrows(JSONPointerException.class, () -> {
            query("/obj/key/failure");
        });
    }

    @Test
    void builderTest() {
        JSONPointer pointer = JSONPointer.builder()
                .append("obj")
                .append("other~key").append("another/key")
                .append(0)
                .build();
        assertEquals("val", pointer.queryFrom(document));
    }

    @Test
    void nullToken() {
        assertThrows(NullPointerException.class, () -> {
            JSONPointer.builder().append(null);
        });
    }

    @Test
    void toStringEscaping() {
        JSONPointer pointer = JSONPointer.builder()
                .append("obj")
                .append("other~key").append("another/key")
                .append("\"")
                .append(0)
                .build();
        assertEquals("/obj/other~0key/another~1key/\"/0", pointer.toString());
    }

    @Test
    void emptyPointerToString() {
        assertEquals("", new JSONPointer("").toString());
    }

    @Test
    void toURIFragment() {
        assertEquals("#/c%25d", new JSONPointer("/c%d").toURIFragment());
        assertEquals("#/e%5Ef", new JSONPointer("/e^f").toURIFragment());
        assertEquals("#/g%7Ch", new JSONPointer("/g|h").toURIFragment());
        assertEquals("#/m%7En", new JSONPointer("/m~n").toURIFragment());
    }

    @Test
    void tokenListIsCopiedInConstructor() {
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
    void queryFromJSONObject() {
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
        assertEquals("hello world!", obj, "Expected 'hello world!'");
        obj = jsonObject.query("/arrayKey/1");
        assertEquals(Integer.valueOf(1), obj, "Expected 1");
        obj = jsonObject.query("/objectKey/b");
        assertEquals("bVal", obj, "Expected bVal");
        try {
            obj = jsonObject.query("/a/b/c");
            assertTrue(false, "Expected JSONPointerException");
        } catch (JSONPointerException e) {
            assertEquals("value [null] is not an array or object therefore its key b cannot be resolved", e.getMessage(), "Expected bad key/value exception");
        }
    }

    /**
     * Coverage for JSONObject query(JSONPointer)
     */
    @Test
    void queryFromJSONObjectUsingPointer() {
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
        assertEquals("hello world!", obj, "Expected 'hello world!'");
        obj = jsonObject.query(new JSONPointer("/arrayKey/1"));
        assertEquals(Integer.valueOf(1), obj, "Expected 1");
        obj = jsonObject.query(new JSONPointer("/objectKey/b"));
        assertEquals("bVal", obj, "Expected bVal");
        try {
            obj = jsonObject.query(new JSONPointer("/a/b/c"));
            assertTrue(false, "Expected JSONPointerException");
        } catch (JSONPointerException e) {
            assertEquals("value [null] is not an array or object therefore its key b cannot be resolved", e.getMessage(), "Expected bad key/value exception");
        }
    }

    /**
     * Coverage for JSONObject optQuery(JSONPointer)
     */
    @Test
    void optQueryFromJSONObjectUsingPointer() {
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
        assertEquals("hello world!", obj, "Expected 'hello world!'");
        obj = jsonObject.optQuery(new JSONPointer("/arrayKey/1"));
        assertEquals(Integer.valueOf(1), obj, "Expected 1");
        obj = jsonObject.optQuery(new JSONPointer("/objectKey/b"));
        assertEquals("bVal", obj, "Expected bVal");
        obj = jsonObject.optQuery(new JSONPointer("/a/b/c"));
        assertTrue(obj == null, "Expected null");
    }

    /**
     * Coverage for JSONArray query(String)
     */
    @Test
    void queryFromJSONArray() {
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
        assertEquals("hello world!", obj, "Expected 'hello world!'");
        obj = jsonArray.query("/1/1");
        assertEquals(Integer.valueOf(1), obj, "Expected 1");
        obj = jsonArray.query("/2/b");
        assertEquals("bVal", obj, "Expected bVal");
        try {
            obj = jsonArray.query("/a/b/c");
            assertTrue(false, "Expected JSONPointerException");
        } catch (JSONPointerException e) {
            assertEquals("a is not an array index", e.getMessage(), "Expected bad index exception");
        }
    }

    /**
     * Coverage for JSONArray query(JSONPointer)
     */
    @Test
    void queryFromJSONArrayUsingPointer() {
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
        assertEquals("hello world!", obj, "Expected 'hello world!'");
        obj = jsonArray.query(new JSONPointer("/1/1"));
        assertEquals(Integer.valueOf(1), obj, "Expected 1");
        obj = jsonArray.query(new JSONPointer("/2/b"));
        assertEquals("bVal", obj, "Expected bVal");
        try {
            obj = jsonArray.query(new JSONPointer("/a/b/c"));
            assertTrue(false, "Expected JSONPointerException");
        } catch (JSONPointerException e) {
            assertEquals("a is not an array index", e.getMessage(), "Expected bad index exception");
        }
    }

    /**
     * Coverage for JSONArray optQuery(JSONPointer)
     */
    @Test
    void optQueryFromJSONArrayUsingPointer() {
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
        assertEquals("hello world!", obj, "Expected 'hello world!'");
        obj = jsonArray.optQuery(new JSONPointer("/1/1"));
        assertEquals(Integer.valueOf(1), obj, "Expected 1");
        obj = jsonArray.optQuery(new JSONPointer("/2/b"));
        assertEquals("bVal", obj, "Expected bVal");
        obj = jsonArray.optQuery(new JSONPointer("/a/b/c"));
        assertTrue(obj == null, "Expected null");
    }

    /**
     * When creating a jsonObject we need to parse escaped characters "\\\\"
     *  --> it's the string representation of  "\\", so when query'ing via the JSONPointer 
     *  we DON'T escape them
     *  
     */
    @Test
    void queryFromJSONObjectUsingPointer0() {
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
