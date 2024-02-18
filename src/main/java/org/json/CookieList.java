package org.json;

/*
Public Domain.
 */

/**
 * Convert a web browser cookie list string to a JSONObject and back.
 * @author JSON.org
 * @version 2015-12-09
 */
public class CookieList {

    /**
     * Constructs a new CookieList object.
     */
    public CookieList() {
    }
 
    /**
     * Convert a cookie list into a JSONObject. A cookie list is a sequence
     * of name/value pairs. The names are separated from the values by '='.
     * The pairs are separated by ';'. The names and the values
     * will be unescaped, possibly converting '+' and '%' sequences.
     *
     * To add a cookie to a cookie list,
     * cookielistJSONObject.put(cookieJSONObject.getString("name"),
     *     cookieJSONObject.getString("value"));
     * @param string  A cookie list string
     * @return A JSONObject
     * @throws JSONException if a called function fails
     */
    public static JSONObject toJSONObject(String string) throws JSONException {
        JSONObject jo = new JSONObject();
        JSONTokener x = new JSONTokener(string);
        while (x.more()) {
            String name = Cookie.unescape(x.nextTo('='));
            x.next('=');
            jo.put(name, Cookie.unescape(x.nextTo(';')));
            x.next();
        }
        return jo;
    }

    /**
     * Convert a JSONObject into a cookie list. A cookie list is a sequence
     * of name/value pairs. The names are separated from the values by '='.
     * The pairs are separated by ';'. The characters '%', '+', '=', and ';'
     * in the names and values are replaced by "%hh".
     * @param jo A JSONObject
     * @return A cookie list string
     * @throws JSONException if a called function fails
     */
    public static String toString(JSONObject jo) throws JSONException {
        boolean             isEndOfPair = false;
        final StringBuilder sb = new StringBuilder();
        // Don't use the new entrySet API to maintain Android support
        for (final String key : jo.keySet()) {
            final Object value = jo.opt(key);
            if (!JSONObject.NULL.equals(value)) {
                if (isEndOfPair) {
                    sb.append(';');
                }
                sb.append(Cookie.escape(key));
                sb.append("=");
                sb.append(Cookie.escape(value.toString()));
                isEndOfPair = true;
            }
        }
        return sb.toString();
    }
}
