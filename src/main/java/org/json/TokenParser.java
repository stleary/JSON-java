package org.json;

/**
 * Interface for token parsers.
 */
public interface TokenParser {
    /**
     * Parse the token from the given JSONTokener starting with the specified character.
     * @param tokener   The JSONTokener instance.
     * @param startChar The starting character of the token.
     * @return The parsed token as a string.
     * @throws JSONException if a syntax error occurs.
     */
    String parse(JSONTokener tokener, char startChar) throws JSONException;
}
