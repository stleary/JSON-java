package org.json;

/*
Public Domain.
 */

/**
 * This provides static methods to convert comma delimited text into a
 * JSONArray, and to convert a JSONArray into comma delimited text. Comma
 * delimited text is a very popular format for data interchange. It is
 * understood by most database, spreadsheet, and organizer programs.
 * <p>
 * Each row of text represents a row in a table or a data record. Each row
 * ends with a NEWLINE character. Each row contains one or more values.
 * Values are separated by commas. A value can contain any character except
 * for comma, unless is is wrapped in single quotes or double quotes.
 * <p>
 * The first row usually contains the names of the columns.
 * <p>
 * A comma delimited list can be converted into a JSONArray of JSONObjects.
 * The names for the elements in the JSONObjects can be taken from the names
 * in the first row.
 * @author JSON.org
 * @version 2016-05-01
 */
public class CDL {

    /**
     * Get the next value. The value can be wrapped in quotes. The value can
     * be empty.
     * @param x A JSONTokener of the source text.
     * @return The value string, or null if empty.
     * @throws JSONException if the quoted string is badly formed.
     */
    private static String getValue(JSONTokener x) throws JSONException {
        char c;
        char q;
        StringBuilder sb;
        do {
            c = x.next();
        } while (c == ' ' || c == '\t');
        switch (c) {
        case 0:
            return null;
        case '"':
        case '\'':
            q = c;
            sb = new StringBuilder();
            for (;;) {
                c = x.next();
                if (c == q) {
                    //Handle escaped double-quote
                    char nextC = x.next();
                    if(nextC != '\"') {
                        // if our quote was the end of the file, don't step
                        if(nextC > 0) {
                            x.back();
                        }
                        break;
                    }
                }
                if (c == 0 || c == '\n' || c == '\r') {
                    throw x.syntaxError("Missing close quote '" + q + "'.");
                }
                sb.append(c);
            }
            return sb.toString();
        case ',':
            x.back();
            return "";
        default:
            x.back();
            return x.nextTo(',');
        }
    }

    /**
     * Produce a JSONArray of strings from a row of comma delimited values.
     * @param x A JSONTokener of the source text.
     * @return A JSONArray of strings.
     * @throws JSONException if a called function fails
     */
    public static JSONArray rowToJSONArray(JSONTokener x) throws JSONException {
        JSONArray ja = new JSONArray();
        for (;;) {
            String value = getValue(x);
            char c = x.next();
            if (value == null ||
                    (ja.length() == 0 && value.length() == 0 && c != ',')) {
                return null;
            }
            ja.put(value);
            for (;;) {
                if (c == ',') {
                    break;
                }
                if (c != ' ') {
                    if (c == '\n' || c == '\r' || c == 0) {
                        return ja;
                    }
                    throw x.syntaxError("Bad character '" + c + "' (" +
                            (int)c + ").");
                }
                c = x.next();
            }
        }
    }

    /**
     * Produce a JSONObject from a row of comma delimited text, using a
     * parallel JSONArray of strings to provides the names of the elements.
     * @param names A JSONArray of names. This is commonly obtained from the
     *  first row of a comma delimited text file using the rowToJSONArray
     *  method.
     * @param x A JSONTokener of the source text.
     * @return A JSONObject combining the names and values.
     * @throws JSONException if a called function fails
     */
    public static JSONObject rowToJSONObject(JSONArray names, JSONTokener x)
            throws JSONException {
        JSONArray ja = rowToJSONArray(x);
        return ja != null ? ja.toJSONObject(names) :  null;
    }

    /**
     * Produce a comma delimited text row from a JSONArray. Values containing
     * the comma character will be quoted. Troublesome characters may be
     * removed.
     * @param ja A JSONArray of strings.
     * @return A string ending in NEWLINE.
     */
    public static String rowToString(JSONArray ja) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ja.length(); i += 1) {
            if (i > 0) {
                sb.append(',');
            }
            Object object = ja.opt(i);
            if (object != null) {
                String string = object.toString();
                if (string.length() > 0 && (string.indexOf(',') >= 0 ||
                        string.indexOf('\n') >= 0 || string.indexOf('\r') >= 0 ||
                        string.indexOf(0) >= 0 || string.charAt(0) == '"')) {
                    sb.append('"');
                    int length = string.length();
                    for (int j = 0; j < length; j += 1) {
                        char c = string.charAt(j);
                        if (c >= ' ' && c != '"') {
                            sb.append(c);
                        }
                    }
                    sb.append('"');
                } else {
                    sb.append(string);
                }
            }
        }
        sb.append('\n');
        return sb.toString();
    }

    /**
     * Produce a JSONArray of JSONObjects from a comma delimited text string,
     * using the first row as a source of names.
     * @param string The comma delimited text.
     * @return A JSONArray of JSONObjects.
     * @throws JSONException if a called function fails
     */
    public static JSONArray toJSONArray(String string) throws JSONException {
        return toJSONArray(new JSONTokener(string));
    }

    /**
     * Produce a JSONArray of JSONObjects from a comma delimited text string,
     * using the first row as a source of names.
     * @param x The JSONTokener containing the comma delimited text.
     * @return A JSONArray of JSONObjects.
     * @throws JSONException if a called function fails
     */
    public static JSONArray toJSONArray(JSONTokener x) throws JSONException {
        return toJSONArray(rowToJSONArray(x), x);
    }

    /**
     * Produce a JSONArray of JSONObjects from a comma delimited text string
     * using a supplied JSONArray as the source of element names.
     * @param names A JSONArray of strings.
     * @param string The comma delimited text.
     * @return A JSONArray of JSONObjects.
     * @throws JSONException if a called function fails
     */
    public static JSONArray toJSONArray(JSONArray names, String string)
            throws JSONException {
        return toJSONArray(names, new JSONTokener(string));
    }

    /**
     * Produce a JSONArray of JSONObjects from a comma delimited text string
     * using a supplied JSONArray as the source of element names.
     * @param names A JSONArray of strings.
     * @param x A JSONTokener of the source text.
     * @return A JSONArray of JSONObjects.
     * @throws JSONException if a called function fails
     */
    public static JSONArray toJSONArray(JSONArray names, JSONTokener x)
            throws JSONException {
        if (names == null || names.length() == 0) {
            return null;
        }
        JSONArray ja = new JSONArray();
        for (;;) {
            JSONObject jo = rowToJSONObject(names, x);
            if (jo == null) {
                break;
            }
            ja.put(jo);
        }
        if (ja.length() == 0) {
            return null;
        }
        return ja;
    }


    /**
     * Produce a comma delimited text from a JSONArray of JSONObjects. The
     * first row will be a list of names obtained by inspecting the first
     * JSONObject.
     * @param ja A JSONArray of JSONObjects.
     * @return A comma delimited text.
     * @throws JSONException if a called function fails
     */
    public static String toString(JSONArray ja) throws JSONException {
        JSONObject jo = ja.optJSONObject(0);
        if (jo != null) {
            JSONArray names = jo.names();
            if (names != null) {
                return rowToString(names) + toString(names, ja);
            }
        }
        return null;
    }

    /**
     * Produce a comma delimited text from a JSONArray of JSONObjects using
     * a provided list of names. The list of names is not included in the
     * output.
     * @param names A JSONArray of strings.
     * @param ja A JSONArray of JSONObjects.
     * @return A comma delimited text.
     * @throws JSONException if a called function fails
     */
    public static String toString(JSONArray names, JSONArray ja)
            throws JSONException {
        if (names == null || names.length() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ja.length(); i += 1) {
            JSONObject jo = ja.optJSONObject(i);
            if (jo != null) {
                sb.append(rowToString(jo.toJSONArray(names)));
            }
        }
        return sb.toString();
    }
}
