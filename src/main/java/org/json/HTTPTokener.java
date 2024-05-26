package org.json;

/*
Public Domain.
*/

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
        char currentChar;
        char quoteChar;
        StringBuilder sb = new StringBuilder();
        do {
            currentChar = next();
        } while (Character.isWhitespace(currentChar));
        if (currentChar == '"' || currentChar == '\'') {
            quoteChar = currentChar;
            for (;;) {
                currentChar = next();
                if (currentChar < ' ') {
                    throw syntaxError("Unterminated string.");
                }
                if (currentChar == quoteChar) {
                    return sb.toString();
                }
                sb.append(currentChar);
            }
        }
        for (;;) {
            if (currentChar == 0 || Character.isWhitespace(currentChar)) {
                return sb.toString();
            }
            sb.append(currentChar);
            currentChar = next();
        }
    }
}
