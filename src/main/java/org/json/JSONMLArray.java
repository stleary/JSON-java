package org.json;

/**
 * This provides static methods to convert an XML text into a
 * JSONArray, and to covert a JSONArray into an XML text using
 * the JsonML transform.
 *
 * @author JSON.org
 * @version 2016-01-30
 */
public class JSONMLArray {
    /**
     * Parse XML values and store them in a JSONArray.
     *
     * @param x           The XMLTokener containing the source string.
     * @param arrayForm   true if array form, false if object form.
     * @param ja          The JSONArray that is containing the current tag or null
     *                    if we are at the outermost level.
     * @param keepStrings Don't type-convert text nodes and attribute values
     * @return A JSONArray if the value is the outermost tag, otherwise null.
     * @throws JSONException if a parsing error occurs
     */
    public static Object parse(
            XMLTokener x,
            boolean arrayForm,
            JSONArray ja,
            boolean keepStrings,
            int currentNestingDepth
    ) throws JSONException {
        return parse(x, arrayForm, ja,
                keepStrings ? JSONMLParserConfiguration.KEEP_STRINGS : JSONMLParserConfiguration.ORIGINAL,
                currentNestingDepth);
    }

    /**
     * Parse XML values and store them in a JSONArray.
     *
     * @param x         The XMLTokener containing the source string.
     * @param arrayForm true if array form, false if object form.
     * @param ja        The JSONArray that is containing the current tag or null
     *                  if we are at the outermost level.
     * @param config    The parser configuration:
     *                  JSONMLParserConfiguration.ORIGINAL is the default behaviour;
     *                  JSONMLParserConfiguration.KEEP_STRINGS means Don't type-convert text nodes and attribute values.
     * @return A JSONArray if the value is the outermost tag, otherwise null.
     * @throws JSONException if a parsing error occurs
     */
    public static Object parse(
            XMLTokener x,
            boolean arrayForm,
            JSONArray ja,
            JSONMLParserConfiguration config,
            int currentNestingDepth
    ) throws JSONException {
        String attribute;
        char c;
        String closeTag = null;
        int i;
        JSONArray newja = null;
        JSONObject newjo = null;
        Object token;
        String tagName = null;

// Test for and skip past these forms:
//      <!-- ... -->
//      <![  ... ]]>
//      <!   ...   >
//      <?   ...  ?>

        while (true) {
            if (!x.more()) {
                throw x.syntaxError("Bad XML");
            }
            token = x.nextContent();
            if (token == XMLTokener.LT) {
                token = x.nextToken();
                if (token instanceof Character) {
                    if (token == XMLTokener.SLASH) {

// Close tag </

                        token = x.nextToken();
                        if (!(token instanceof String)) {
                            throw new JSONException(
                                    "Expected a closing name instead of '" +
                                            token + "'.");
                        }
                        if (x.nextToken() != XMLTokener.GT) {
                            throw x.syntaxError("Misshaped close tag");
                        }
                        return token;
                    } else if (token == XMLTokener.BANG) {

// <!

                        c = x.next();
                        if (c == '-') {
                            if (x.next() == '-') {
                                x.skipPast("-->");
                            } else {
                                x.back();
                            }
                        } else if (c == '[') {
                            token = x.nextToken();
                            if (token.equals("CDATA") && x.next() == '[') {
                                if (ja != null) {
                                    ja.put(x.nextCDATA());
                                }
                            } else {
                                throw x.syntaxError("Expected 'CDATA['");
                            }
                        } else {
                            i = 1;
                            do {
                                token = x.nextMeta();
                                if (token == null) {
                                    throw x.syntaxError("Missing '>' after '<!'.");
                                } else if (token == XMLTokener.LT) {
                                    i += 1;
                                } else if (token == XMLTokener.GT) {
                                    i -= 1;
                                }
                            } while (i > 0);
                        }
                    } else if (token == XMLTokener.QUEST) {

// <?

                        x.skipPast("?>");
                    } else {
                        throw x.syntaxError("Misshaped tag");
                    }

// Open tag <

                } else {
                    if (!(token instanceof String)) {
                        throw x.syntaxError("Bad tagName '" + token + "'.");
                    }
                    tagName = (String) token;
                    newja = new JSONArray();
                    newjo = new JSONObject();
                    if (arrayForm) {
                        newja.put(tagName);
                        if (ja != null) {
                            ja.put(newja);
                        }
                    } else {
                        newjo.put("tagName", tagName);
                        if (ja != null) {
                            ja.put(newjo);
                        }
                    }
                    token = null;
                    for (; ; ) {
                        if (token == null) {
                            token = x.nextToken();
                        }
                        if (token == null) {
                            throw x.syntaxError("Misshaped tag");
                        }
                        if (!(token instanceof String)) {
                            break;
                        }

// attribute = value

                        attribute = (String) token;
                        if (!arrayForm && ("tagName".equals(attribute) || "childNode".equals(attribute))) {
                            throw x.syntaxError("Reserved attribute.");
                        }
                        token = x.nextToken();
                        if (token == XMLTokener.EQ) {
                            token = x.nextToken();
                            if (!(token instanceof String)) {
                                throw x.syntaxError("Missing value");
                            }
                            newjo.accumulate(attribute, config.isKeepStrings() ? ((String) token) : XML.stringToValue((String) token));
                            token = null;
                        } else {
                            newjo.accumulate(attribute, "");
                        }
                    }
                    if (arrayForm && newjo.length() > 0) {
                        newja.put(newjo);
                    }

// Empty tag <.../>

                    if (token == XMLTokener.SLASH) {
                        if (x.nextToken() != XMLTokener.GT) {
                            throw x.syntaxError("Misshaped tag");
                        }
                        if (ja == null) {
                            if (arrayForm) {
                                return newja;
                            }
                            return newjo;
                        }

// Content, between <...> and </...>

                    } else {
                        if (token != XMLTokener.GT) {
                            throw x.syntaxError("Misshaped tag");
                        }

                        if (currentNestingDepth == config.getMaxNestingDepth()) {
                            throw x.syntaxError("Maximum nesting depth of " + config.getMaxNestingDepth() + " reached");
                        }

                        closeTag = (String) parse(x, arrayForm, newja, config, currentNestingDepth + 1);
                        if (closeTag != null) {
                            if (!closeTag.equals(tagName)) {
                                throw x.syntaxError("Mismatched '" + tagName +
                                        "' and '" + closeTag + "'");
                            }
                            tagName = null;
                            if (!arrayForm && newja.length() > 0) {
                                newjo.put("childNodes", newja);
                            }
                            if (ja == null) {
                                if (arrayForm) {
                                    return newja;
                                }
                                return newjo;
                            }
                        }
                    }
                }
            } else {
                if (ja != null) {
                    ja.put(token instanceof String
                            ? (config.isKeepStrings() ? XML.unescape((String) token) : XML.stringToValue((String) token))
                            : token);
                }
            }
        }
    }

    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONArray using the JsonML transform. Each XML tag is represented as
     * a JSONArray in which the first element is the tag name. If the tag has
     * attributes, then the second element will be JSONObject containing the
     * name/value pairs. If the tag contains children, then strings and
     * JSONArrays will represent the child tags.
     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     *
     * @param string The source string.
     * @return A JSONArray containing the structured data from the XML string.
     * @throws JSONException Thrown on error converting to a JSONArray
     */
    public static JSONArray toJSONArray(String string) throws JSONException {
        return (JSONArray) parse(new XMLTokener(string), true, null, JSONMLParserConfiguration.ORIGINAL, 0);
    }

    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONArray using the JsonML transform. Each XML tag is represented as
     * a JSONArray in which the first element is the tag name. If the tag has
     * attributes, then the second element will be JSONObject containing the
     * name/value pairs. If the tag contains children, then strings and
     * JSONArrays will represent the child tags.
     * As opposed to toJSONArray this method does not attempt to convert
     * any text node or attribute value to any type
     * but just leaves it as a string.
     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     *
     * @param string      The source string.
     * @param keepStrings If true, then values will not be coerced into boolean
     *                    or numeric values and will instead be left as strings
     * @return A JSONArray containing the structured data from the XML string.
     * @throws JSONException Thrown on error converting to a JSONArray
     */
    public static JSONArray toJSONArray(String string, boolean keepStrings) throws JSONException {
        return (JSONArray) parse(new XMLTokener(string), true, null, keepStrings, 0);
    }

    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONArray using the JsonML transform. Each XML tag is represented as
     * a JSONArray in which the first element is the tag name. If the tag has
     * attributes, then the second element will be JSONObject containing the
     * name/value pairs. If the tag contains children, then strings and
     * JSONArrays will represent the child tags.
     * As opposed to toJSONArray this method does not attempt to convert
     * any text node or attribute value to any type
     * but just leaves it as a string.
     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     *
     * @param string The source string.
     * @param config The parser configuration:
     *               JSONMLParserConfiguration.ORIGINAL is the default behaviour;
     *               JSONMLParserConfiguration.KEEP_STRINGS means values will not be coerced into boolean
     *               or numeric values and will instead be left as strings
     * @return A JSONArray containing the structured data from the XML string.
     * @throws JSONException Thrown on error converting to a JSONArray
     */
    public static JSONArray toJSONArray(String string, JSONMLParserConfiguration config) throws JSONException {
        return (JSONArray) parse(new XMLTokener(string), true, null, config, 0);
    }

    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONArray using the JsonML transform. Each XML tag is represented as
     * a JSONArray in which the first element is the tag name. If the tag has
     * attributes, then the second element will be JSONObject containing the
     * name/value pairs. If the tag contains children, then strings and
     * JSONArrays will represent the child content and tags.
     * As opposed to toJSONArray this method does not attempt to convert
     * any text node or attribute value to any type
     * but just leaves it as a string.
     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     *
     * @param x      An XMLTokener.
     * @param config The parser configuration:
     *               JSONMLParserConfiguration.ORIGINAL is the default behaviour;
     *               JSONMLParserConfiguration.KEEP_STRINGS means values will not be coerced into boolean
     *               or numeric values and will instead be left as strings
     * @return A JSONArray containing the structured data from the XML string.
     * @throws JSONException Thrown on error converting to a JSONArray
     */
    public static JSONArray toJSONArray(XMLTokener x, JSONMLParserConfiguration config) throws JSONException {
        return (JSONArray) parse(x, true, null, config, 0);
    }

    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONArray using the JsonML transform. Each XML tag is represented as
     * a JSONArray in which the first element is the tag name. If the tag has
     * attributes, then the second element will be JSONObject containing the
     * name/value pairs. If the tag contains children, then strings and
     * JSONArrays will represent the child content and tags.
     * As opposed to toJSONArray this method does not attempt to convert
     * any text node or attribute value to any type
     * but just leaves it as a string.
     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     *
     * @param x           An XMLTokener.
     * @param keepStrings If true, then values will not be coerced into boolean
     *                    or numeric values and will instead be left as strings
     * @return A JSONArray containing the structured data from the XML string.
     * @throws JSONException Thrown on error converting to a JSONArray
     */
    public static JSONArray toJSONArray(XMLTokener x, boolean keepStrings) throws JSONException {
        return (JSONArray) parse(x, true, null, keepStrings, 0);
    }

    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONArray using the JsonML transform. Each XML tag is represented as
     * a JSONArray in which the first element is the tag name. If the tag has
     * attributes, then the second element will be JSONObject containing the
     * name/value pairs. If the tag contains children, then strings and
     * JSONArrays will represent the child content and tags.
     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     *
     * @param x An XMLTokener.
     * @return A JSONArray containing the structured data from the XML string.
     * @throws JSONException Thrown on error converting to a JSONArray
     */
    public static JSONArray toJSONArray(XMLTokener x) throws JSONException {
        return (JSONArray) parse(x, true, null, false, 0);
    }

    /**
     * Reverse the JSONML transformation, making an XML text from a JSONArray.
     *
     * @param ja A JSONArray.
     * @return An XML string.
     * @throws JSONException Thrown on error converting to a string
     */
    public static String toString(JSONArray ja) throws JSONException {
        int i;
        JSONObject jo;
        int length;
        Object object;
        StringBuilder sb = new StringBuilder();
        String tagName;

// Emit <tagName

        tagName = ja.getString(0);
        XML.noSpace(tagName);
        tagName = XML.escape(tagName);
        sb.append('<');
        sb.append(tagName);

        object = ja.opt(1);
        if (object instanceof JSONObject) {
            i = 2;
            jo = (JSONObject) object;

// Emit the attributes

            // Don't use the new entrySet API to maintain Android support
            for (final String key : jo.keySet()) {
                final Object value = jo.opt(key);
                XML.noSpace(key);
                if (value != null) {
                    sb.append(' ');
                    sb.append(XML.escape(key));
                    sb.append('=');
                    sb.append('"');
                    sb.append(XML.escape(value.toString()));
                    sb.append('"');
                }
            }
        } else {
            i = 1;
        }

// Emit content in body

        length = ja.length();
        if (i >= length) {
            sb.append('/');
            sb.append('>');
        } else {
            sb.append('>');
            do {
                object = ja.get(i);
                i += 1;
                if (object != null) {
                    if (object instanceof String) {
                        sb.append(XML.escape(object.toString()));
                    } else if (object instanceof JSONObject) {
                        sb.append(JSONMLObject.toString((JSONObject) object));
                    } else if (object instanceof JSONArray) {
                        sb.append(toString((JSONArray) object));
                    } else {
                        sb.append(object.toString());
                    }
                }
            } while (i < length);
            sb.append('<');
            sb.append('/');
            sb.append(tagName);
            sb.append('>');
        }
        return sb.toString();
    }
}