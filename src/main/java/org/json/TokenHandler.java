package org.json;


abstract class TokenHandler {
    abstract boolean handleToken(XMLTokener x, JSONObject context, XMLParserConfiguration config, int currentNestingDepth, Object token, String name) throws JSONException;
}
