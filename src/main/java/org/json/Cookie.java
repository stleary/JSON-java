package org.json;

import java.util.Locale;

/*
Public Domain.
*/

/**
 * Convert a web browser cookie specification to a JSONObject and back.
 * JSON and Cookies are both notations for name/value pairs.
 * See also: <a href="https://tools.ietf.org/html/rfc6265">https://tools.ietf.org/html/rfc6265</a>
 * @author JSON.org
 * @version 2015-12-09
 */
public class Cookie {

    /**
     * Produce a copy of a string in which the characters '+', '%', '=', ';'
     * and control characters are replaced with "%hh". This is a gentle form
     * of URL encoding, attempting to cause as little distortion to the
     * string as possible. The characters '=' and ';' are meta characters in
     * cookies. By convention, they are escaped using the URL-encoding. This is
     * only a convention, not a standard. Often, cookies are expected to have
     * encoded values. We encode '=' and ';' because we must. We encode '%' and
     * '+' because they are meta characters in URL encoding.
     * @param string The source string.
     * @return       The escaped result.
     */
    public static String escape(String string) {
        char            c;
        String          s = string.trim();
        int             length = s.length();
        StringBuilder   sb = new StringBuilder(length);
        for (int i = 0; i < length; i += 1) {
            c = s.charAt(i);
            if (c < ' ' || c == '+' || c == '%' || c == '=' || c == ';') {
                sb.append('%');
                sb.append(Character.forDigit((char)((c >>> 4) & 0x0f), 16));
                sb.append(Character.forDigit((char)(c & 0x0f), 16));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    /**
     * Convert a cookie specification string into a JSONObject. The string
     * must contain a name value pair separated by '='. The name and the value
     * will be unescaped, possibly converting '+' and '%' sequences. The
     * cookie properties may follow, separated by ';', also represented as
     * name=value (except the Attribute properties like "Secure" or "HttpOnly",
     * which do not have a value. The value {@link Boolean#TRUE} will be used for these).
     * The name will be stored under the key "name", and the value will be
     * stored under the key "value". This method does not do checking or
     * validation of the parameters. It only converts the cookie string into
     * a JSONObject. All attribute names are converted to lower case keys in the
     * JSONObject (HttpOnly =&gt; httponly). If an attribute is specified more than
     * once, only the value found closer to the end of the cookie-string is kept.
     * @param string The cookie specification string.
     * @return A JSONObject containing "name", "value", and possibly other
     *  members.
     * @throws JSONException If there is an error parsing the Cookie String.
     * Cookie strings must have at least one '=' character and the 'name'
     * portion of the cookie must not be blank.
     */
    public static JSONObject toJSONObject(String string) {
        final JSONObject     jo = new JSONObject();
        String         name;
        Object         value;
        
        
        JSONTokener x = new JSONTokener(string);
        
        name = unescape(x.nextTo('=').trim());
        //per RFC6265, if the name is blank, the cookie should be ignored.
        if("".equals(name)) {
            throw new JSONException("Cookies must have a 'name'");
        }
        jo.put("name", name);
        // per RFC6265, if there is no '=', the cookie should be ignored.
        // the 'next' call here throws an exception if the '=' is not found.
        x.next('=');
        jo.put("value", unescape(x.nextTo(';')).trim());
        // discard the ';'
        x.next();
        // parse the remaining cookie attributes
        while (x.more()) {
            name = unescape(x.nextTo("=;")).trim().toLowerCase(Locale.ROOT);
            // don't allow a cookies attributes to overwrite its name or value.
            if("name".equalsIgnoreCase(name)) {
                throw new JSONException("Illegal attribute name: 'name'");
            }
            if("value".equalsIgnoreCase(name)) {
                throw new JSONException("Illegal attribute name: 'value'");
            }
            // check to see if it's a flag property
            if (x.next() != '=') {
                value = Boolean.TRUE;
            } else {
                value = unescape(x.nextTo(';')).trim();
                x.next();
            }
            // only store non-blank attributes
            if(!"".equals(name) && !"".equals(value)) {
                jo.put(name, value);
            }
        }
        return jo;
    }


    /**
     * Convert a JSONObject into a cookie specification string. The JSONObject
     * must contain "name" and "value" members (case insensitive).
     * If the JSONObject contains other members, they will be appended to the cookie
     * specification string. User-Agents are instructed to ignore unknown attributes,
     * so ensure your JSONObject is using only known attributes.
     * See also: <a href="https://tools.ietf.org/html/rfc6265">https://tools.ietf.org/html/rfc6265</a>
     * @param jo A JSONObject
     * @return A cookie specification string
     * @throws JSONException thrown if the cookie has no name.
     */
    public static String toString(JSONObject jo) throws JSONException {
        StringBuilder sb = new StringBuilder();
        
        String name = null;
        Object value = null;
        for(String key : jo.keySet()){
            if("name".equalsIgnoreCase(key)) {
                name = jo.getString(key).trim();
            }
            if("value".equalsIgnoreCase(key)) {
                value=jo.getString(key).trim();
            }
            if(name != null && value != null) {
                break;
            }
        }
        
        if(name == null || "".equals(name.trim())) {
            throw new JSONException("Cookie does not have a name");
        }
        if(value == null) {
            value = "";
        }
        
        sb.append(escape(name));
        sb.append("=");
        sb.append(escape((String)value));
        
        for(String key : jo.keySet()){
            if("name".equalsIgnoreCase(key)
                    || "value".equalsIgnoreCase(key)) {
                // already processed above
                continue;
            }
            value = jo.opt(key);
            if(value instanceof Boolean) {
                if(Boolean.TRUE.equals(value)) {
                    sb.append(';').append(escape(key));
                }
                // don't emit false values
            } else {
                sb.append(';')
                    .append(escape(key))
                    .append('=')
                    .append(escape(value.toString()));
            }
        }
        
        return sb.toString();
    }

    /**
     * Convert <code>%</code><i>hh</i> sequences to single characters, and
     * convert plus to space.
     * @param string A string that may contain
     *      <code>+</code>&nbsp;<small>(plus)</small> and
     *      <code>%</code><i>hh</i> sequences.
     * @return The unescaped string.
     */
    public static String unescape(String string) {
        int length = string.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            char c = string.charAt(i);
            if (c == '+') {
                c = ' ';
            } else if (c == '%' && i + 2 < length) {
                int d = JSONTokener.dehexchar(string.charAt(i + 1));
                int e = JSONTokener.dehexchar(string.charAt(i + 2));
                if (d >= 0 && e >= 0) {
                    c = (char)(d * 16 + e);
                    i += 2;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
