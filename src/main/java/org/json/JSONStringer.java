package org.json;

/*
Public Domain.
*/

import java.io.StringWriter;

/**
 * JSONStringer provides a quick and convenient way of producing JSON text
 * using a StringWriter. The texts produced strictly conform to JSON syntax rules.
 * No whitespace is added, so the results are ready for transmission or storage.
 * Each instance of JSONStringer can produce one JSON text.
 * <p>
 * This class delegates to a {@link JSONWriter} instance while providing a
 * simplified interface for string-based JSON generation. It maintains the same
 * fluent API as JSONWriter but removes direct inheritance where it wasn't fully
 * utilized.
 * <p>
 * A JSONStringer instance provides a <code>value</code> method for appending
 * values to the text, and a <code>key</code> method for adding keys before
 * values in objects. There are <code>array</code> and <code>endArray</code>
 * methods that make and bound array values, and <code>object</code> and
 * <code>endObject</code> methods which make and bound object values. All of
 * these methods return the JSONStringer instance, permitting cascade style.
 * For example:
 * <pre>
 * myString = new JSONStringer()
 *     .object()
 *         .key("JSON")
 *         .value("Hello, World!")
 *     .endObject()
 *     .toString();</pre>
 * produces the string:
 * <pre>
 * {"JSON":"Hello, World!"}</pre>
 * <p>
 * The first method called must be <code>array</code> or <code>object</code>.
 * Commas and colons are automatically added by the delegate JSONWriter.
 * Objects and arrays can be nested up to 200 levels deep.
 * <p>
 * This is often simpler than using JSONObject directly for string generation.
 *
 * @author JSON.org
 * @version 2023-07-20 (Refactored to use delegation)
 */
public class JSONStringer {
    private final JSONWriter writer;
    private final StringWriter stringWriter;

    /**
     * Constructs a fresh JSONStringer instance. Each instance can build one
     * complete JSON text. The underlying StringWriter and JSONWriter are
     * initialized automatically.
     */
    public JSONStringer() {
        this.stringWriter = new StringWriter();
        this.writer = new JSONWriter(stringWriter);
    }

    /**
     * Begins appending a new array. All values until the balancing
     * {@link #endArray()} will be appended to this array.
     *
     * @return this JSONStringer for method chaining
     * @throws JSONException If nesting is too deep or called in invalid context
     * @see JSONWriter#array()
     */
    public JSONStringer array() throws JSONException {
        writer.array();
        return this;
    }

    /**
     * Ends the current array. Must be called to balance array starts.
     *
     * @return this JSONStringer for method chaining
     * @throws JSONException If incorrectly nested
     * @see JSONWriter#endArray()
     */
    public JSONStringer endArray() throws JSONException {
        writer.endArray();
        return this;
    }

    /**
     * Begins appending a new object. All keys and values until {@link #endObject()}
     * will be appended to this object.
     *
     * @return this JSONStringer for method chaining
     * @throws JSONException If nesting is too deep or called in invalid context
     * @see JSONWriter#object()
     */
    public JSONStringer object() throws JSONException {
        writer.object();
        return this;
    }

    /**
     * Ends the current object. Must be called to balance object starts.
     *
     * @return this JSONStringer for method chaining
     * @throws JSONException If incorrectly nested
     * @see JSONWriter#endObject()
     */
    public JSONStringer endObject() throws JSONException {
        writer.endObject();
        return this;
    }

    /**
     * Appends a key to the current object. The next value will be associated
     * with this key.
     *
     * @param key The key string (must not be null)
     * @return this JSONStringer for method chaining
     * @throws JSONException If key is null or called in invalid context
     * @see JSONWriter#key(String)
     */
    public JSONStringer key(String key) throws JSONException {
        writer.key(key);
        return this;
    }

    /**
     * Appends a value to the current array or object.
     *
     * @param value The value to append (may be null, Boolean, Number, String,
     *              JSONObject, JSONArray, or JSONString)
     * @return this JSONStringer for method chaining
     * @throws JSONException If value is invalid or called in wrong context
     * @see JSONWriter#value(Object)
     */
    public JSONStringer value(Object value) throws JSONException {
        writer.value(value);
        return this;
    }

    /**
     * Returns the generated JSON text. Returns null if:
     * <ul>
     *   <li>No array/object was started
     *   <li>The JSON structure is incomplete (unbalanced arrays/objects)
     * </ul>
     *
     * @return The JSON text or null if incomplete
     * @see JSONWriter#mode
     */
    @Override
    public String toString() {
        return writer.mode == 'd' ? stringWriter.toString() : null;
    }
}