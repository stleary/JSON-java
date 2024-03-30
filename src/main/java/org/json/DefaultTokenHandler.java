package org.json;

class DefaultTokenHandler extends TokenHandler {
    @Override
    boolean handleToken(XMLTokener x, JSONObject context, XMLParserConfiguration config, int currentNestingDepth, Object token, String name) throws JSONException {
        String tagName = (String) token;
        token = null;
        JSONObject jsonObject = new JSONObject();
        boolean nilAttributeFound = false;
        XMLXsiTypeConverter<?> xmlXsiTypeConverter = null;
        for (;;) {
            if (token == null) {
                token = x.nextToken();
            }
            // attribute = value
            if (token instanceof String) {

                String string = (String) token;
                token = x.nextToken();
                if (token == XML.EQ) {
                    token = x.nextToken();
                    if (!(token instanceof String)) {
                        throw x.syntaxError("Missing value");
                    }

                    if (config.isConvertNilAttributeToNull()
                            && XML.NULL_ATTR.equals(string)
                            && Boolean.parseBoolean((String) token)) {
                        nilAttributeFound = true;
                    } else if(config.getXsiTypeMap() != null && !config.getXsiTypeMap().isEmpty()
                            && XML.TYPE_ATTR.equals(string)) {
                        xmlXsiTypeConverter = config.getXsiTypeMap().get(token);
                    } else if (!nilAttributeFound) {
                        jsonObject.accumulate(string,
                                config.isKeepStrings()
                                        ? ((String) token)
                                        : XML.stringToValue((String) token));
                    }
                    token = null;
                } else {
                    jsonObject.accumulate(string, "");
                }


            } else if (token == XML.SLASH) {
                // Empty tag <.../>
                if (x.nextToken() != XML.GT) {
                    throw x.syntaxError("Misshaped tag");
                }
                if (config.getForceList().contains(tagName)) {
                    // Force the value to be an array
                    if (nilAttributeFound) {
                        context.append(tagName, JSONObject.NULL);
                    } else if (jsonObject.length() > 0) {
                        context.append(tagName, jsonObject);
                    } else {
                        context.put(tagName, new JSONArray());
                    }
                } else {
                    if (nilAttributeFound) {
                        context.accumulate(tagName, JSONObject.NULL);
                    } else if (jsonObject.length() > 0) {
                        context.accumulate(tagName, jsonObject);
                    } else {
                        context.accumulate(tagName, "");
                    }
                }
                return false;

            } else if (token == XML.GT) {
                String string;
                // Content, between <...> and </...>
                for (;;) {
                    token = x.nextContent();
                    if (token == null) {
                        if (tagName != null) {
                            throw x.syntaxError("Unclosed tag " + tagName);
                        }
                        return false;
                    } else if (token instanceof String) {
                        string = (String) token;
                        if (string.length() > 0) {
                            if(xmlXsiTypeConverter != null) {
                                jsonObject.accumulate(config.getcDataTagName(),
                                        XML.stringToValue(string, xmlXsiTypeConverter));
                            } else {
                                jsonObject.accumulate(config.getcDataTagName(),
                                        config.isKeepStrings() ? string : XML.stringToValue(string));
                            }
                        }

                    } else if (token == XML.LT) {
                        // Nested element
                        if (currentNestingDepth == config.getMaxNestingDepth()) {
                            throw x.syntaxError("Maximum nesting depth of " + config.getMaxNestingDepth() + " reached");
                        }

                        if (XML.parse(x, jsonObject, tagName, config, currentNestingDepth + 1)) {
                            if (config.getForceList().contains(tagName)) {
                                // Force the value to be an array
                                if (jsonObject.length() == 0) {
                                    context.put(tagName, new JSONArray());
                                } else if (jsonObject.length() == 1
                                        && jsonObject.opt(config.getcDataTagName()) != null) {
                                    context.append(tagName, jsonObject.opt(config.getcDataTagName()));
                                } else {
                                    context.append(tagName, jsonObject);
                                }
                            } else {
                                if (jsonObject.length() == 0) {
                                    context.accumulate(tagName, "");
                                } else if (jsonObject.length() == 1
                                        && jsonObject.opt(config.getcDataTagName()) != null) {
                                    context.accumulate(tagName, jsonObject.opt(config.getcDataTagName()));
                                } else {
                                    if (!config.shouldTrimWhiteSpace()) {
                                        removeEmpty(jsonObject, config);
                                    }
                                    context.accumulate(tagName, jsonObject);
                                }
                            }

                            return false;
                        }
                    }
                }
            } else {
                throw x.syntaxError("Misshaped tag");
            }
        }
    }


    /**
     * This method removes any JSON entry which has the key set by XMLParserConfiguration.cDataTagName
     * and contains whitespace as this is caused by whitespace between tags. See test XMLTest.testNestedWithWhitespaceTrimmingDisabled.
     * @param jsonObject JSONObject which may require deletion
     * @param config The XMLParserConfiguration which includes the cDataTagName
     */
    private static void removeEmpty(final JSONObject jsonObject, final XMLParserConfiguration config) {
        if (jsonObject.has(config.getcDataTagName()))  {
            final Object s = jsonObject.get(config.getcDataTagName());
            if (s instanceof String) {
                if (isStringAllWhiteSpace(s.toString())) {
                    jsonObject.remove(config.getcDataTagName());
                }
            }
            else if (s instanceof JSONArray) {
                final JSONArray sArray = (JSONArray) s;
                for (int k = sArray.length()-1; k >= 0; k--){
                    final Object eachString = sArray.get(k);
                    if (eachString instanceof String) {
                        String s1 = (String) eachString;
                        if (isStringAllWhiteSpace(s1)) {
                            sArray.remove(k);
                        }
                    }
                }
                if (sArray.isEmpty()) {
                    jsonObject.remove(config.getcDataTagName());
                }
            }
        }
    }



    private static boolean isStringAllWhiteSpace(final String s) {
        for (int k = 0; k<s.length(); k++){
            final char eachChar = s.charAt(k);
            if (!Character.isWhitespace(eachChar)) {
                return false;
            }
        }
        return true;
    }

}
