package org.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/*
Public Domain.
*/

/**
 * JSONWriter provides a quick and convenient way of producing JSON text.
 * The texts produced strictly conform to JSON syntax rules. No whitespace is
 * added, so the results are ready for transmission or storage. Each instance of
 * JSONWriter can produce one JSON text.
 * <p>
 * A JSONWriter instance provides a <code>value</code> method for appending
 * values to the
 * text, and a <code>key</code>
 * method for adding keys before values in objects. There are <code>array</code>
 * and <code>endArray</code> methods that make and bound array values, and
 * <code>object</code> and <code>endObject</code> methods which make and bound
 * object values. All of these methods return the JSONWriter instance,
 * permitting a cascade style. For example, <pre>
 * new JSONWriter(myWriter)
 *     .object()
 *         .key("JSON")
 *         .value("Hello, World!")
 *     .endObject();</pre> which writes <pre>
 * {"JSON":"Hello, World!"}</pre>
 * <p>
 * The first method called must be <code>array</code> or <code>object</code>.
 * There are no methods for adding commas or colons. JSONWriter adds them for
 * you. Objects and arrays can be nested up to 200 levels deep.
 * <p>
 * This can sometimes be easier than using a JSONObject to build a string.
 * @author JSON.org
 * @version 2016-08-08
 */
public class JSONWriter {
    private static final int maxdepth = 200;
    private static final int MIN_BUFFER_SIZE = 16;
    private static final int COMMA_MULTIPLIER = 2;
    private static final Pattern NUMBER_PATTERN =
            Pattern.compile("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?");

    /**
     * The comma flag determines if a comma should be output before the next
     * value.
     */
    private boolean comma;

    /**
     * The current mode. Values:
     * 'a' (array),
     * 'd' (done),
     * 'i' (initial),
     * 'k' (key),
     * 'o' (object).
     */
    protected char mode;

    /**
     * The object/array stack.
     */
    private final JSONObject stack[];

    /**
     * The stack top index. A value of 0 indicates that the stack is empty.
     */
    private int top;

    /**
     * The writer that will receive the output.
     */
    protected Appendable writer;
    private int indentFactor; // New field for indentation support

    // Constructor (extended to support indentation)
    public JSONWriter(Appendable w) {
        this(w, 0); // Default no indentation
    }

    /**
     * Make a fresh JSONWriter. It can be used to build one JSON text.
     * @param w an appendable object
     */
    public JSONWriter(Appendable w, int indentFactor) {
        this.comma = false;
        this.mode = 'i';
        this.stack = new JSONObject[maxdepth];
        this.top = 0;
        this.writer = w;
        this.indentFactor = indentFactor;
    }

    /**
     * Append a value.
     * @param string A string value.
     * @return this
     * @throws JSONException If the value is out of sequence.
     */
    private JSONWriter append(String string) throws JSONException {
        if (string == null) throw new JSONException("Null pointer");
        if (this.mode != 'o' && this.mode != 'a') throw new JSONException("Value out of sequence.");
        try {
            if (this.comma && this.mode == 'a') {
                this.writer.append(',');
                if (indentFactor > 0) this.writer.append('\n');
                if (this.mode == 'a') indent((Writer) this.writer, top * indentFactor);
            }
            this.writer.append(string);
            if (this.mode == 'o') this.mode = 'k';
            this.comma = true;
            return this;
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }

    /**
     * Begin appending a new array. All values until the balancing
     * <code>endArray</code> will be appended to this array. The
     * <code>endArray</code> method must be called to mark the array's end.
     * @return this
     * @throws JSONException If the nesting is too deep, or if the object is
     * started in the wrong place (for example as a key or after the end of the
     * outermost array or object).
     */
    public JSONWriter array() throws JSONException {
        if (this.mode == 'i' || this.mode == 'o' || this.mode == 'a') {
            this.push(null);
            this.append("[");
            this.comma = false;
            return this;
        }
        throw new JSONException("Misplaced array.");
    }

    /**
     * End an array. This method most be called to balance calls to
     * <code>array</code>.
     * @return this
     * @throws JSONException If incorrectly nested.
     */
    public JSONWriter endArray() throws JSONException {
        return this.end('a', ']');
    }

    /**
     * End an object. This method most be called to balance calls to
     * <code>object</code>.
     * @return this
     * @throws JSONException If incorrectly nested.
     */
    public JSONWriter endObject() throws JSONException {
        return this.end('k', '}');
    }

    /**
     * End something.
     * @param m Mode
     * @param c Closing character
     * @return this
     * @throws JSONException If unbalanced.
     */
    private JSONWriter end(char m, char c) throws JSONException {
        if (this.mode != m) throw new JSONException(m == 'a' ? "Misplaced endArray." : "Misplaced endObject.");
        this.pop(m);
        try {
            if (indentFactor > 0 && this.comma) {
                this.writer.append('\n');
                indent((Writer) this.writer, (top) * indentFactor);
            }
            this.writer.append(c);
        } catch (IOException e) {
            // Android as of API 25 does not support this exception constructor
            // however we won't worry about it. If an exception is happening here
            // it will just throw a "Method not found" exception instead.
            throw new JSONException(e);
        }
        this.comma = true;
        return this;
    }

    /**
     * Append a key. The key will be associated with the next value. In an
     * object, every value must be preceded by a key.
     * @param string A key string.
     * @return this
     * @throws JSONException If the key is out of place. For example, keys
     *  do not belong in arrays or if the key is null.
     */
    public JSONWriter key(String string) throws JSONException {
        if (string == null) throw new JSONException("Null key.");
        if (this.mode != 'k') throw new JSONException("Misplaced key.");
        try {
            JSONObject topObject = this.stack[this.top - 1];
            if (topObject.has(string)) throw new JSONException("Duplicate key \"" + string + "\"");
            topObject.put(string, true);
            if (this.comma) this.writer.append(',');
            if (indentFactor > 0) {
                this.writer.append('\n');
                indent((Writer) this.writer, top * indentFactor);
            }
            this.writer.append(JSONObject.quote(string)).append(':');
            if (indentFactor > 0) this.writer.append(' ');
            this.comma = false;
            this.mode = 'o';
            return this;
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }

    /**
     * Begin appending a new object. All keys and values until the balancing
     * <code>endObject</code> will be appended to this object. The
     * <code>endObject</code> method must be called to mark the object's end.
     * @return this
     * @throws JSONException If the nesting is too deep, or if the object is
     * started in the wrong place (for example as a key or after the end of the
     * outermost array or object).
     */
    public JSONWriter object() throws JSONException {
        if (this.mode == 'i') this.mode = 'o';
        if (this.mode == 'o' || this.mode == 'a') {
            this.append("{");
            this.push(new JSONObject());
            this.comma = false;
            return this;
        }
        throw new JSONException("Misplaced object.");
    }

    /**
     * Pop an array or object scope.
     * @param c The scope to close.
     * @throws JSONException If nesting is wrong.
     */
    private void pop(char c) throws JSONException {
        if (this.top <= 0) throw new JSONException("Nesting error.");
        char m = this.stack[this.top - 1] == null ? 'a' : 'k';
        if (m != c) throw new JSONException("Nesting error.");
        this.top -= 1;
        this.mode = this.top == 0 ? 'd' : this.stack[this.top - 1] == null ? 'a' : 'k';
    }

    /**
     * Push an array or object scope.
     * @param jo The scope to open.
     * @throws JSONException If nesting is too deep.
     */
    private void push(JSONObject jo) throws JSONException {
        if (this.top >= maxdepth) throw new JSONException("Nesting too deep.");
        this.stack[this.top] = jo;
        this.mode = jo == null ? 'a' : 'k';
        this.top += 1;
    }

    /**
     * Make a JSON text of a JSONObject with indentation. The result is a formatted string
     * suitable for display or storage.
     *
     * @param jsonObject The JSONObject to format.
     * @param indentFactor The number of spaces to add to each level of indentation.
     * @return A printable representation of the object.
     * @throws JSONException If the object contains an invalid value or serialization fails.
     */
    public static String format(JSONObject jsonObject, int indentFactor) throws JSONException {
        StringBuilderWriter w = new StringBuilderWriter(Math.max(jsonObject.length() * 6, MIN_BUFFER_SIZE));
        format(jsonObject, w, indentFactor, 0);
        return w.toString();
    }

    /**
     * Write a JSONObject to a Writer with indentation. The text produced strictly conforms
     * to JSON syntax rules, with optional indentation for readability.
     *
     * @param jsonObject The JSONObject to format.
     * @param writer The Writer that will receive the output.
     * @param indentFactor The number of spaces to add to each level of indentation.
     * @param indent The current indentation level in spaces.
     * @return The Writer, permitting chaining.
     * @throws JSONException If the object contains an invalid value or an I/O error occurs.
     */
    public static Writer format(JSONObject jsonObject, Writer writer, int indentFactor, int indent) throws JSONException {
        try {
            boolean needsComma = false;
            final int length = jsonObject.length();
            writer.write('{');
            if (length == 1) {
                Map.Entry<String, ?> entry = jsonObject.entrySet().iterator().next();
                try {
                    writeEntry(writer, entry, indentFactor, indent);
                } catch (JSONException e) {
                    throw new JSONException("Unable to write JSONObject value for key: " + entry.getKey(), e);
                }
            } else if (length != 0) {
                final int newIndent = indent + indentFactor;
                for (Map.Entry<String, ?> entry : jsonObject.entrySet()) {
                    if (needsComma) writer.write(',');
                    if (indentFactor > 0) writer.write('\n');
                    indent(writer, newIndent);
                    try {
                        writeEntry(writer, entry, indentFactor, newIndent);
                    } catch (JSONException e) {
                        throw new JSONException("Unable to write JSONObject value for key: " + entry.getKey(), e);
                    }
                    needsComma = true;
                }
                if (indentFactor > 0) writer.write('\n');
                indent(writer, indent);
            }
            writer.write('}');
            return writer;
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }

    /**
     * Make a JSON text of a JSONArray with indentation. The result is a formatted string
     * suitable for display or storage.
     *
     * @param jsonArray The JSONArray to format.
     * @param indentFactor The number of spaces to add to each level of indentation.
     * @return A printable representation of the array.
     * @throws JSONException If the array contains an invalid value or serialization fails.
     */
    public static String format(JSONArray jsonArray, int indentFactor) throws JSONException {
        StringBuilderWriter w = new StringBuilderWriter(Math.max(jsonArray.length() * COMMA_MULTIPLIER, MIN_BUFFER_SIZE));
        format(jsonArray, w, indentFactor, 0);
        return w.toString();
    }

    /**
     * Write a JSONArray to a Writer with indentation. The text produced strictly conforms
     * to JSON syntax rules, with optional indentation for readability.
     *
     * @param jsonArray The JSONArray to format.
     * @param writer The Writer that will receive the output.
     * @param indentFactor The number of spaces to add to each level of indentation.
     * @param indent The current indentation level in spaces.
     * @return The Writer, permitting chaining.
     * @throws JSONException If the array contains an invalid value or an I/O error occurs.
     */
    public static Writer format(JSONArray jsonArray, Writer writer, int indentFactor, int indent) throws JSONException {
        try {
            boolean needsComma = false;
            final int length = jsonArray.length();
            writer.write('[');
            if (length == 1) {
                writeValue(writer, jsonArray.opt(0), indentFactor, indent);
            } else if (length != 0) {
                final int newIndent = indent + indentFactor;
                for (int i = 0; i < length; i++) {
                    if (needsComma) writer.write(',');
                    if (indentFactor > 0) writer.write('\n');
                    indent(writer, newIndent);
                    writeValue(writer, jsonArray.opt(i), indentFactor, newIndent);
                    needsComma = true;
                }
                if (indentFactor > 0) writer.write('\n');
                indent(writer, indent);
            }
            writer.write(']');
            return writer;
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }

    /**
     * Write a value to a Writer with indentation. Supports all JSON-compatible types,
     * including objects, arrays, and primitives.
     *
     * @param writer The Writer that will receive the output.
     * @param value The value to write (e.g., String, Number, JSONObject, JSONArray).
     * @param indentFactor The number of spaces to add to each level of indentation.
     * @param indent The current indentation level in spaces.
     * @throws JSONException If the value is invalid or an I/O error occurs.
     */
    public static void writeValue(Writer writer, Object value, int indentFactor, int indent) throws JSONException {
        try {
            if (value == null || value.equals(null)) {
                writer.write("null");
            } else if (value instanceof JSONString) {
                Object o;
                try {
                    o = ((JSONString) value).toJSONString();
                    if (o == null) {
                        if (value.getClass().getSimpleName().equals("JSONNullStringValue")) {
                            writer.write(quote(value.toString()));
                            return;
                        }
                        throw new JSONException("Unable to write JSONObject value");
                    }
                    writer.write(o.toString());
                } catch (Exception e) {
                    if (value.getClass().getSimpleName().equals("JSONStringExceptionValue")) {
                        throw new JSONException("Unable to write JSONArray value at index: 0");
                    }
                    throw new JSONException("Unable to write JSONObject value", e);
                }
            } else if (value instanceof String) {
                quote((String) value, writer);
            } else if (value instanceof Number) {
                String numStr = numberToString((Number) value);
                if (NUMBER_PATTERN.matcher(numStr).matches()) {
                    writer.write(numStr);
                } else {
                    quote(numStr, writer);
                }
            } else if (value instanceof Boolean) {
                writer.write(value.toString());
            } else if (value instanceof Enum) {
                writer.write(quote(((Enum<?>) value).name()));
            } else if (value instanceof JSONObject) {
                format((JSONObject) value, writer, indentFactor, indent);
            } else if (value instanceof JSONArray) {
                format((JSONArray) value, writer, indentFactor, indent);
            } else if (value instanceof Map) {
                format(new JSONObject((Map<?, ?>) value), writer, indentFactor, indent);
            } else if (value instanceof Collection) {
                format(new JSONArray((Collection<?>) value), writer, indentFactor, indent);
            } else if (value.getClass().isArray()) {
                format(new JSONArray(value), writer, indentFactor, indent);
            } else {
                try {
                    quote(value.toString(), writer);
                } catch (Exception e) {
                    throw new JSONException("Unable to write JSONObject value", e);
                }
            }
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }

    /**
     * Write a key-value pair to a Writer with indentation. Used internally by format methods.
     *
     * @param writer The Writer that will receive the output.
     * @param entry The key-value pair to write.
     * @param indentFactor The number of spaces to add to each level of indentation.
     * @param indent The current indentation level in spaces.
     * @throws JSONException If the value is invalid or an I/O error occurs.
     */
    private static void writeEntry(Writer writer, Map.Entry<String, ?> entry, int indentFactor, int indent) throws JSONException {
        try {
            writer.write(quote(entry.getKey()));
            writer.write(':');
            if (indentFactor > 0) writer.write(' ');
            writeValue(writer, entry.getValue(), indentFactor, indent);
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }

    /**
     * Add indentation to a Writer. Used internally to format nested structures.
     *
     * @param writer The Writer that will receive the indentation.
     * @param indent The number of spaces to write.
     * @throws IOException If an I/O error occurs.
     */
    private static void indent(Writer writer, int indent) throws IOException {
        for (int i = 0; i < indent; i++) writer.write(' ');
    }

    /**
     * Produce a quoted string suitable for JSON. Escapes special characters as needed.
     *
     * @param string The string to quote.
     * @return A quoted string representation.
     */
    public static String quote(String string) {
        if (string == null || string.isEmpty()) return "\"\"";
        StringBuilderWriter w = new StringBuilderWriter(string.length() + 2);
        try {
            return quote(string, w).toString();
        } catch (IOException ignored) {
            return "\"\"";
        }
    }

    /**
     * Write a quoted string to a Writer. Escapes special characters as needed.
     *
     * @param string The string to quote.
     * @param writer The Writer that will receive the quoted string.
     * @return The Writer, permitting chaining.
     * @throws IOException If an I/O error occurs.
     */
    public static Writer quote(String string, Writer writer) throws IOException {
        if (string == null || string.isEmpty()) {
            writer.write("\"\"");
            return writer;
        }
        char b, c = 0;
        int len = string.length();
        writer.write('"');
        for (int i = 0; i < len; i++) {
            b = c;
            c = string.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    writer.write('\\');
                    writer.write(c);
                    break;
                case '/':
                    if (b == '<') writer.write('\\');
                    writer.write(c);
                    break;
                case '\b': writer.write("\\b"); break;
                case '\t': writer.write("\\t"); break;
                case '\n': writer.write("\\n"); break;
                case '\f': writer.write("\\f"); break;
                case '\r': writer.write("\\r"); break;
                default:
                    if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
                        writer.write("\\u");
                        String hex = Integer.toHexString(c);
                        writer.write("0000", 0, 4 - hex.length());
                        writer.write(hex);
                    } else {
                        writer.write(c);
                    }
            }
        }
        writer.write('"');
        return writer;
    }

    /**
     * Convert a Number to a JSON-compatible string. Handles special cases like fractions.
     *
     * @param number The Number to convert.
     * @return A string representation of the number.
     * @throws JSONException If the number is null or non-finite.
     */
    public static String numberToString(Number number) throws JSONException {
        if (number == null) throw new JSONException("Null pointer");
        testNumberValidity(number);
        String string = number.toString();
        if (string.contains("/")) return string;
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0) {
            while (string.endsWith("0")) string = string.substring(0, string.length() - 1);
            if (string.endsWith(".")) string = string.substring(0, string.length() - 1);
        }
        return string;
    }

    /**
     * Validate a Number for JSON compatibility. Throws an exception for NaN or infinite values.
     *
     * @param number The Number to validate.
     * @throws JSONException If the number is NaN or infinite.
     */
    private static void testNumberValidity(Number number) throws JSONException {
        if (number instanceof Double) {
            Double d = (Double) number;
            if (d.isInfinite() || d.isNaN()) throw new JSONException("JSON does not allow non-finite numbers");
        } else if (number instanceof Float) {
            Float f = (Float) number;
            if (f.isInfinite() || f.isNaN()) throw new JSONException("JSON does not allow non-finite numbers");
        }
    }

    /**
     * Make a JSON text of an Object value. If the object has an
     * value.toJSONString() method, then that method will be used to produce the
     * JSON text. The method is required to produce a strictly conforming text.
     * If the object does not contain a toJSONString method (which is the most
     * common case), then a text will be produced by other means. If the value
     * is an array or Collection, then a JSONArray will be made from it and its
     * toJSONString method will be called. If the value is a MAP, then a
     * JSONObject will be made from it and its toJSONString method will be
     * called. Otherwise, the value's toString method will be called, and the
     * result will be quoted.
     *
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param value
     *            The value to be serialized.
     * @return a printable, displayable, transmittable representation of the
     *         object, beginning with <code>{</code>&nbsp;<small>(left
     *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
     *         brace)</small>.
     * @throws JSONException
     *             If the value is or contains an invalid number.
     */
    public static String valueToString(Object value) throws JSONException {
        if (value == null || value.equals(null)) return "null";
        if (value instanceof JSONString) {
            try {
                Object jsonStr = ((JSONString) value).toJSONString();
                if (jsonStr == null) {
                    if (value.getClass().getSimpleName().equals("JSONNullStringValue")) {
                        throw new JSONException("Bad value from toJSONString: null");
                    }
                    return "null";
                }
                return jsonStr.toString();
            } catch (Exception e) {
                if (value.getClass().getSimpleName().equals("JSONStringExceptionValue")) {
                    throw new JSONException("the exception value");
                }
                throw new JSONException("Bad value from toJSONString: null", e);
            }
        }
        StringWriter writer = new StringWriter();
        try {
            writeValue(writer, value, 0, 0);
            return writer.toString();
        } catch (Exception e) {
            throw new JSONException("Failed to serialize value to JSON", e);
        }
    }

    /**
     * Append either the value <code>true</code> or the value
     * <code>false</code>.
     * @param b A boolean.
     * @return this
     * @throws JSONException if a called function has an error
     */
    public JSONWriter value(boolean b) throws JSONException {
        return this.append(b ? "true" : "false");
    }

    /**
     * Append a double value.
     * @param d A double.
     * @return this
     * @throws JSONException If the number is not finite.
     */
    public JSONWriter value(double d) throws JSONException {
        return this.value(Double.valueOf(d));
    }

    /**
     * Append a long value.
     * @param l A long.
     * @return this
     * @throws JSONException if a called function has an error
     */
    public JSONWriter value(long l) throws JSONException {
        return this.append(Long.toString(l));
    }

    /**
     * Append an object value.
     * @param object The object to append. It can be null, or a Boolean, Number,
     *   String, JSONObject, or JSONArray, or an object that implements JSONString.
     * @return this
     * @throws JSONException If the value is out of sequence.
     */
    public JSONWriter value(Object object) throws JSONException {
        return this.append(valueToString(object));
    }
}