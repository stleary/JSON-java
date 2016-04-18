package org.json.junit;

import static org.junit.Assert.assertSame;

import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.JSONPointerException;
import org.junit.Test;

public class JSONPointerTest {

    private static final JSONObject document = new JSONObject("{"
            + "\"foo\": [\"bar\", \"baz\"], "
            + "\"\": 0,"
            + "\"a/b\": 1,"
            + "\"c%d\": 2,"
            + "\"e^f\": 3,"
            + "\"g|h\": 4," + "\"i\\\\j\": 5,"
            + "\"k\\\"l\": 6,"
            + "\" \": 7,"
            + "\"m~n\": 8"
            + "}");

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
    public void uriFragmentNotation() {
        assertSame(document.get("foo"), query("#/foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void syntaxError() {
        new JSONPointer("key");
    }

}
