package org.json;

class CharTokenHandler extends TokenHandler {
    @Override
    boolean handleToken(XMLTokener x, JSONObject context, XMLParserConfiguration config, int currentNestingDepth, Object token, String name) throws JSONException {
        throw x.syntaxError("Misshaped tag");
    }
}
