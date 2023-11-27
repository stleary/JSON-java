package org.json;

/*
Public Domain.
*/

/**
 * This provides static methods to convert an XML text into a
 * JSONObject, and to covert a JSONObject into an XML text using
 * the JsonML transform.
 *
 * @author JSON.org
 * @version 2016-01-30
 */
public class JSONMLObject {


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a "tagName" property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a "childNodes" property which
     * will be an array of strings and JsonML JSONObjects.

     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     * @param string The XML source text.
     * @return A JSONObject containing the structured data from the XML string.
     * @throws JSONException Thrown on error converting to a JSONObject
     */
    public static JSONObject toJSONObject(String string) throws JSONException {
        return (JSONObject) JSONMLArray.parse(new XMLTokener(string), false, null, false, 0);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a "tagName" property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a "childNodes" property which
     * will be an array of strings and JsonML JSONObjects.

     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     * @param string The XML source text.
     * @param keepStrings If true, then values will not be coerced into boolean
     *  or numeric values and will instead be left as strings
     * @return A JSONObject containing the structured data from the XML string.
     * @throws JSONException Thrown on error converting to a JSONObject
     */
    public static JSONObject toJSONObject(String string, boolean keepStrings) throws JSONException {
        return (JSONObject) JSONMLArray.parse(new XMLTokener(string), false, null, keepStrings, 0);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a "tagName" property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a "childNodes" property which
     * will be an array of strings and JsonML JSONObjects.

     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     * @param string The XML source text.
     * @param config  The parser configuration:
     *     JSONMLParserConfiguration.ORIGINAL is the default behaviour;
     *     JSONMLParserConfiguration.KEEP_STRINGS means values will not be coerced into boolean
     *       or numeric values and will instead be left as strings
     * @return A JSONObject containing the structured data from the XML string.
     * @throws JSONException Thrown on error converting to a JSONObject
     */
    public static JSONObject toJSONObject(String string, JSONMLParserConfiguration config) throws JSONException {
        return (JSONObject) JSONMLArray.parse(new XMLTokener(string), false, null, config, 0);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a "tagName" property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a "childNodes" property which
     * will be an array of strings and JsonML JSONObjects.

     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     * @param x An XMLTokener of the XML source text.
     * @return A JSONObject containing the structured data from the XML string.
     * @throws JSONException Thrown on error converting to a JSONObject
     */
    public static JSONObject toJSONObject(XMLTokener x) throws JSONException {
           return (JSONObject) JSONMLArray.parse(x, false, null, false, 0);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a "tagName" property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a "childNodes" property which
     * will be an array of strings and JsonML JSONObjects.

     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     * @param x An XMLTokener of the XML source text.
     * @param keepStrings If true, then values will not be coerced into boolean
     *  or numeric values and will instead be left as strings
     * @return A JSONObject containing the structured data from the XML string.
     * @throws JSONException Thrown on error converting to a JSONObject
     */
    public static JSONObject toJSONObject(XMLTokener x, boolean keepStrings) throws JSONException {
           return (JSONObject) JSONMLArray.parse(x, false, null, keepStrings, 0);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a "tagName" property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a "childNodes" property which
     * will be an array of strings and JsonML JSONObjects.

     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     * @param x An XMLTokener of the XML source text.
     * @param config  The parser configuration:
     *     JSONMLParserConfiguration.ORIGINAL is the default behaviour;
     *     JSONMLParserConfiguration.KEEP_STRINGS means values will not be coerced into boolean
     *       or numeric values and will instead be left as strings
     * @return A JSONObject containing the structured data from the XML string.
     * @throws JSONException Thrown on error converting to a JSONObject
     */
    public static JSONObject toJSONObject(XMLTokener x, JSONMLParserConfiguration config) throws JSONException {
        return (JSONObject) JSONMLArray.parse(x, false, null, config, 0);
    }


    /**
     * Reverse the JSONML transformation, making an XML text from a JSONObject.
     * The JSONObject must contain a "tagName" property. If it has children,
     * then it must have a "childNodes" property containing an array of objects.
     * The other properties are attributes with string values.
     * @param jo A JSONObject.
     * @return An XML string.
     * @throws JSONException Thrown on error converting to a string
     */
    public static String toString(JSONObject jo) throws JSONException {
        StringBuilder sb = new StringBuilder();
        int                 i;
        JSONArray           ja;
        int                 length;
        Object              object;
        String              tagName;
        Object              value;

//Emit <tagName

        tagName = jo.optString("tagName");
        if (tagName == null) {
            return XML.escape(jo.toString());
        }
        XML.noSpace(tagName);
        tagName = XML.escape(tagName);
        sb.append('<');
        sb.append(tagName);

//Emit the attributes

        // Don't use the new entrySet API to maintain Android support
        for (final String key : jo.keySet()) {
            if (!"tagName".equals(key) && !"childNodes".equals(key)) {
                XML.noSpace(key);
                value = jo.opt(key);
                if (value != null) {
                    sb.append(' ');
                    sb.append(XML.escape(key));
                    sb.append('=');
                    sb.append('"');
                    sb.append(XML.escape(value.toString()));
                    sb.append('"');
                }
            }
        }

//Emit content in body

        ja = jo.optJSONArray("childNodes");
        if (ja == null) {
            sb.append('/');
            sb.append('>');
        } else {
            sb.append('>');
            length = ja.length();
            for (i = 0; i < length; i += 1) {
                object = ja.get(i);
                if (object != null) {
                    if (object instanceof String) {
                        sb.append(XML.escape(object.toString()));
                    } else if (object instanceof JSONObject) {
                        sb.append(toString((JSONObject)object));
                    } else if (object instanceof JSONArray) {
                        sb.append(JSONMLArray.toString((JSONArray) object));
                    } else {
                        sb.append(object.toString());
                    }
                }
            }
            sb.append('<');
            sb.append('/');
            sb.append(tagName);
            sb.append('>');
        }
        return sb.toString();
    }
}
