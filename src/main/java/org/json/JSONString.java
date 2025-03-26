package org.json;

/*
Public Domain.
 */

/**
 * The <code>JSONString</code> interface allows a <code>toJSONString()</code>
 * method so that a class can change the behavior of
 * <code>JSONObject.toString()</code>, <code>JSONArray.toString()</code>,
 * and <code>JSONWriter.value(</code>Object<code>)</code>. The
 * <code>toJSONString</code> method will be used instead of the default behavior
 * of using the Object's <code>toString()</code> method and quoting the result.
 */
public interface JSONString extends JSONSimilar {
    /**
     * The <code>toJSONString</code> method allows a class to produce its own JSON
     * serialization.
     *
     * @return A strictly syntactically correct JSON text.
     */
    public String toJSONString();

    /**
     * Determine if two JSONStrings are similar by comparing their serialized forms.
     * @param other The other JSONString
     * @return true if their JSON representations are equal
     */
    @Override
    default boolean similar(Object other) {
        if (!(other instanceof JSONString)) {
            return false;
        }
        return this.toJSONString().equals(((JSONString)other).toJSONString());
    }
}
