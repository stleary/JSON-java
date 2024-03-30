package org.json;

class QuestTokenHandler extends TokenHandler {
    @Override
    boolean handleToken(XMLTokener x, JSONObject context, XMLParserConfiguration config, int currentNestingDepth, Object token, String name) throws JSONException {
        x.skipPast("?>");
        return false;
    }
}
