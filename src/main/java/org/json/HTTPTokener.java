package org.json;

/**
 * The HTTPTokener extends the JSONTokener to provide additional methods
 * for the parsing of HTTP headers.
 * @author JSON.org
 * @version 2015-12-09
 */
public class HTTPTokener extends JSONTokener {

    /**
     * Construct an HTTPTokener from a string.
     * @param string A source string.
     */
    public HTTPTokener(String string) {
        super(string);
    }

    /**
     * Get the next token or string. This is used in parsing HTTP headers.
     * @return A String.
     * @throws JSONException if a syntax error occurs
     */
    public String nextToken() throws JSONException {
        char c = nextNonWhitespace();
        TokenParser parser = TokenParsers.getParser(c);
        return parser.parse(this, c);
    }

    /**
     * Consume whitespace characters and return the first non-whitespace character.
     * @return The first non-whitespace character.
     * @throws JSONException if an error occurs while reading.
     */
    private char nextNonWhitespace() throws JSONException {
        char c;
        do {
            c = next();
        } while (Character.isWhitespace(c));
        return c;
    }
}
