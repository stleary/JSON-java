package org.json;

/**
 * Parser for quoted strings.
 */
public class QuotedStringParser implements TokenParser {
    @Override
    public String parse(JSONTokener tokener, char startChar) throws JSONException {
        char q = startChar;
        StringBuilder sb = new StringBuilder();
        for (;;) {
            char c = tokener.next();
            if (c < ' ') {
                throw tokener.syntaxError("Unterminated string.");
            }
            if (c == q) {
                return sb.toString();
            }
            sb.append(c);
        }
    }
}
