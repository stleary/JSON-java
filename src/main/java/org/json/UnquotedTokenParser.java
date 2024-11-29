package org.json;

/**
 * Parser for unquoted tokens.
 */
public class UnquotedTokenParser implements TokenParser {
    @Override
    public String parse(JSONTokener tokener, char startChar) throws JSONException {
        StringBuilder sb = new StringBuilder();
        char c = startChar;
        for (;;) {
            if (c == 0 || Character.isWhitespace(c)) {
                return sb.toString();
            }
            sb.append(c);
            c = tokener.next();
        }
    }
}
