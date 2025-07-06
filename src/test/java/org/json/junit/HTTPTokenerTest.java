package org.json.junit;

import org.json.HTTPTokener;
import org.json.JSONException;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Tests for JSON-Java HTTPTokener.java
 */
public class HTTPTokenerTest {

    /**
     * Test parsing a simple unquoted token.
     */
    @Test
    public void parseSimpleToken() {
        HTTPTokener tokener = new HTTPTokener("Content-Type");
        String token = tokener.nextToken();
        assertEquals("Content-Type", token);
    }

    /**
     * Test parsing multiple tokens separated by whitespace.
     */
    @Test
    public void parseMultipleTokens() {
        HTTPTokener tokener = new HTTPTokener("Content-Type application/json");
        String token1 = tokener.nextToken();
        String token2 = tokener.nextToken();
        assertEquals("Content-Type", token1);
        assertEquals("application/json", token2);
    }

    /**
     * Test parsing a double-quoted token.
     */
    @Test
    public void parseDoubleQuotedToken() {
        HTTPTokener tokener = new HTTPTokener("\"application/json\"");
        String token = tokener.nextToken();
        assertEquals("application/json", token);
    }

    /**
     * Test parsing a single-quoted token.
     */
    @Test
    public void parseSingleQuotedToken() {
        HTTPTokener tokener = new HTTPTokener("'application/json'");
        String token = tokener.nextToken();
        assertEquals("application/json", token);
    }

    /**
     * Test parsing a quoted token that includes spaces and semicolons.
     */
    @Test
    public void parseQuotedTokenWithSpaces() {
        HTTPTokener tokener = new HTTPTokener("\"text/html; charset=UTF-8\"");
        String token = tokener.nextToken();
        assertEquals("text/html; charset=UTF-8", token);
    }

    /**
     * Test that unterminated quoted strings throw a JSONException.
     */
    @Test
    public void throwExceptionOnUnterminatedString() {
        HTTPTokener tokener = new HTTPTokener("\"incomplete");
        JSONException exception = assertThrows(JSONException.class, tokener::nextToken);
        assertTrue(exception.getMessage().contains("Unterminated string"));
    }

    /**
     * Test behavior with empty input string.
     */
    @Test
    public void parseEmptyInput() {
        HTTPTokener tokener = new HTTPTokener("");
        String token = tokener.nextToken();
        assertEquals("", token);
    }

    /**
     * Test behavior with input consisting only of whitespace.
     */
    @Test
    public void parseWhitespaceOnly() {
        HTTPTokener tokener = new HTTPTokener("   \t \n ");
        String token = tokener.nextToken();
        assertEquals("", token);
    }

    /**
     * Test parsing tokens separated by multiple whitespace characters.
     */
    @Test
    public void parseTokensWithMultipleWhitespace() {
        HTTPTokener tokener = new HTTPTokener("GET     /index.html");
        String method = tokener.nextToken();
        String path = tokener.nextToken();
        assertEquals("GET", method);
        assertEquals("/index.html", path);
    }

}