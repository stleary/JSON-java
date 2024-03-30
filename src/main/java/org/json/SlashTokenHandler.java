package org.json;

class SlashTokenHandler extends TokenHandler {
    @Override
    boolean handleToken(XMLTokener x, JSONObject context, XMLParserConfiguration config, int currentNestingDepth, Object token, String name) throws JSONException {
        token = x.nextToken();
        if (name == null) {
            throw x.syntaxError("Mismatched close tag " + token);
        }
        if (!token.equals(name)) {
            throw x.syntaxError("Mismatched " + name + " and " + token);
        }
        if (x.nextToken() != XML.GT) {
            throw x.syntaxError("Misshaped close tag");
        }
        return true;
    }
}
