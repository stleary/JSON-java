package org.json;

public class TokenParsers {
    /**
     * Get the appropriate parser for the starting character.
     * @param c The starting character.
     * @return A TokenParser instance.
     */
    public static TokenParser getParser(char c) {
        if (c == '"' || c == '\'') {
            return new QuotedStringParser();
        }
        return new UnquotedTokenParser();
    }
}
