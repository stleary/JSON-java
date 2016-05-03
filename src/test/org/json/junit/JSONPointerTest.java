package org.json.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.JSONPointerException;
import org.json.JSONTokener;
import org.junit.Test;

public class JSONPointerTest {

    private static final JSONObject document;

    static {
        document = new JSONObject(new JSONTokener(
                JSONPointerTest.class.getResourceAsStream("/org/json/junit/jsonpointer-testdoc.json")));
    }

    private Object query(String pointer) {
        return new JSONPointer(pointer).queryFrom(document);
    }

    @Test
    public void emptyPointer() {
        assertSame(document, query(""));
    }

    @Test(expected = NullPointerException.class)
    public void nullPointer() {
        new JSONPointer((String) null);
    }

    @Test
    public void objectPropertyQuery() {
        assertSame(document.get("foo"), query("/foo"));
    }

    @Test
    public void arrayIndexQuery() {
        assertSame(document.getJSONArray("foo").get(0), query("/foo/0"));
    }

    @Test(expected = JSONPointerException.class)
    public void stringPropOfArrayFailure() {
        query("/foo/bar");
    }

    @Test
    public void queryByEmptyKey() {
        assertSame(document.get(""), query("/"));
    }

    @Test
    public void slashEscaping() {
        assertSame(document.get("a/b"), query("/a~1b"));
    }

    @Test
    public void tildeEscaping() {
        assertSame(document.get("m~n"), query("/m~0n"));
    }

    @Test
    public void backslashEscaping() {
        assertSame(document.get("i\\j"), query("/i\\\\j"));
    }

    @Test
    public void quotationEscaping() {
        assertSame(document.get("k\"l"), query("/k\\\\\\\"l"));
    }

    @Test
    public void whitespaceKey() {
        assertSame(document.get(" "), query("/ "));
    }

    @Test
    public void uriFragmentNotation() {
        assertSame(document.get("foo"), query("#/foo"));
    }

    @Test
    public void uriFragmentPercentHandling() {
        assertSame(document.get("c%d"), query("#/c%25d"));
        assertSame(document.get("e^f"), query("#/e%5Ef"));
        assertSame(document.get("g|h"), query("#/g%7Ch"));
        assertSame(document.get("m~n"), query("#/m~0n"));
    }

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
        assertEquals("/obj/other~0key/another~1key/\\\"/0", pointer.toString());
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

}
