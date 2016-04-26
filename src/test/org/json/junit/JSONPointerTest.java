package org.json.junit;

import static org.junit.Assert.assertSame;

import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.JSONPointerException;
import org.json.JSONTokener;
import org.junit.Test;

public class JSONPointerTest {

    private static final JSONObject document;

    // = new JSONObject("{"
    // + "\"foo\": [\"bar\", \"baz\"], "
    // + "\"\": 0,"
    // + "\"a/b\": 1,"
    // + "\"c%d\": 2,"
    // + "\"e^f\": 3,"
    // + "\"g|h\": 4,"
    // + "\"i\\\\j\": 5,"
    // + "\"k\\\\\\\"l\": 6,"
    // + "\" \": 7,"
    // + "\"m~n\": 8"
    // + "}");

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
        new JSONPointer(null);
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

}
