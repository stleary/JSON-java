package org.json;

/*
Public Domain.
*/

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * A JSONObject is an unordered collection of name/value pairs. Its external
 * form is a string wrapped in curly braces with colons between the names and
 * values, and commas between the values and names. The internal form is an
 * object having <code>get</code> and <code>opt</code> methods for accessing
 * the values by name, and <code>put</code> methods for adding or replacing
 * values by name. The values can be any of these types: <code>Boolean</code>,
 * <code>JSONArray</code>, <code>JSONObject</code>, <code>Number</code>,
 * <code>String</code>, or the <code>JSONObject.NULL</code> object. A
 * JSONObject constructor can be used to convert an external form JSON text
 * into an internal form whose values can be retrieved with the
 * <code>get</code> and <code>opt</code> methods, or to convert values into a
 * JSON text using the <code>put</code> and <code>toString</code> methods. A
 * <code>get</code> method returns a value if one can be found, and throws an
 * exception if one cannot be found. An <code>opt</code> method returns a
 * default value instead of throwing an exception, and so is useful for
 * obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return an
 * object, which you can cast or query for type. There are also typed
 * <code>get</code> and <code>opt</code> methods that do type checking and type
 * coercion for you. The opt methods differ from the get methods in that they
 * do not throw. Instead, they return a specified value, such as null.
 * <p>
 * The <code>put</code> methods add or replace values in an object. For
 * example,
 *
 * <pre>
 * myString = new JSONObject()
 *         .put(&quot;JSON&quot;, &quot;Hello, World!&quot;).toString();
 * </pre>
 *
 * produces the string <code>{"JSON": "Hello, World"}</code>.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly conform to
 * the JSON syntax rules. The constructors are more forgiving in the texts they
 * will accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just
 * before the closing brace.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single
 * quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a
 * quote or single quote, and if they do not contain leading or trailing
 * spaces, and if they do not contain any of these characters:
 * <code>{ } [ ] / \ : , #</code> and if they do not look like numbers and
 * if they are not the reserved words <code>true</code>, <code>false</code>,
 * or <code>null</code>.</li>
 * </ul>
 *
 * @author JSON.org
 * @version 2016-08-15
 */
public class JSONObject {
    /**
     * JSONObject.NULL is equivalent to the value that JavaScript calls null,
     * whilst Java's null is equivalent to the value that JavaScript calls
     * undefined.
     */
    private static final class Null {

        /**
         * There is only intended to be a single instance of the NULL object,
         * so the clone method returns itself.
         *
         * @return NULL.
         */
        @Override
        protected final Object clone() {
            return this;
        }

        /**
         * A Null object is equal to the null value and to itself.
         *
         * @param object
         *            An object to test for nullness.
         * @return true if the object parameter is the JSONObject.NULL object or
         *         null.
         */
        @Override
        @SuppressWarnings("lgtm[java/unchecked-cast-in-equals]")
        public boolean equals(Object object) {
            return object == null || object == this;
        }
        /**
         * A Null object is equal to the null value and to itself.
         *
         * @return always returns 0.
         */
        @Override
        public int hashCode() {
            return 0;
        }

        /**
         * Get the "null" string value.
         *
         * @return The string "null".
         */
        @Override
        public String toString() {
            return "null";
        }
    }

    /**
     *  Regular Expression Pattern that matches JSON Numbers. This is primarily used for
     *  output to guarantee that we are always writing valid JSON.
     */
    static final Pattern NUMBER_PATTERN = Pattern.compile("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?");

    /**
     * The map where the JSONObject's properties are kept.
     */
    private final Map<String, Object> map;

    /**
     * An internal factory method that can be overridden if the
     * user desires JSONObject to use a specific map type.
     * <p>
     * The default is HashMap to ensure that elements are unordered per the specification.
     * JSON tends to be a portable transfer format that allows container
     * implementations to rearrange their items for faster element
     * retrieval based on associative access.
     * Therefore, an implementation ought not rely on the order of items.
     * @param capacity starting capacity. If less than 0, then use the default capacity/constructor
     * @return a new Map
     */
    protected Map<String, Object> newInternalMap(int capacity) {
        if (capacity < 0)
            return new HashMap<String, Object>();
        return new HashMap<String, Object>(capacity);
    }

    /**
     * Retrieves the type of the underlying Map in this class.
     *
     * @return The class object representing the type of the underlying Map.
     */
    public Class<? extends Map> getMapType() {
        return map.getClass();
    }

    /**
     * It is sometimes more convenient and less ambiguous to have a
     * <code>NULL</code> object than to use Java's <code>null</code> value.
     * <code>JSONObject.NULL.equals(null)</code> returns <code>true</code>.
     * <code>JSONObject.NULL.toString()</code> returns <code>"null"</code>.
     */
    public static final Object NULL = new Null();

    /**
     * Construct an empty JSONObject.
     */
    public JSONObject() {
        this.map = newInternalMap(-1);
    }

    /**
     * Construct a JSONObject from a subset of another JSONObject. An array of
     * strings is used to identify the keys that should be copied. Missing keys
     * are ignored.
     *
     * @param jo
     *            A JSONObject.
     * @param names
     *            An array of strings.
     */
    public JSONObject(JSONObject jo, String ... names) {
        this(names.length);
        for (int i = 0; i < names.length; i += 1) {
            try {
                this.putOnce(names[i], jo.opt(names[i]));
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Construct a JSONObject from a JSONTokener.
     *
     * @param x
     *            A JSONTokener object containing the source string.
     * @throws JSONException
     *             If there is a syntax error in the source string or a
     *             duplicated key.
     */
    public JSONObject(JSONTokener x) throws JSONException {
        this(x, x.getJsonParserConfiguration());
    }

    /**
     * Construct a JSONObject from a JSONTokener with custom json parse configurations.
     *
     * @param x
     *            A JSONTokener object containing the source string.
     * @param jsonParserConfiguration
     *            Variable to pass parser custom configuration for json parsing.
     * @throws JSONException
     *             If there is a syntax error in the source string or a
     *             duplicated key.
     */
    public JSONObject(JSONTokener x, JSONParserConfiguration jsonParserConfiguration) throws JSONException {
        this();
        char c;
        String key;

        boolean isInitial = x.getPrevious() == 0;

        if (x.nextClean() != '{') {
            throw x.syntaxError("A JSONObject text must begin with '{'");
        }
        for (;;) {
            c = x.nextClean();
            switch (c) {
            case 0:
                throw x.syntaxError("A JSONObject text must end with '}'");
            case '}':
                if (isInitial && jsonParserConfiguration.isStrictMode() && x.nextClean() != 0) {
                    throw x.syntaxError("Strict mode error: Unparsed characters found at end of input text");
                }
                return;
            default:
                key = x.nextSimpleValue(c).toString();
            }

            // The key is followed by ':'.

            c = x.nextClean();
            if (c != ':') {
                throw x.syntaxError("Expected a ':' after a key");
            }

            // Use syntaxError(..) to include error location

            if (key != null) {
                // Check if key exists
                boolean keyExists = this.opt(key) != null;
                if (keyExists && !jsonParserConfiguration.isOverwriteDuplicateKey()) {
                    throw x.syntaxError("Duplicate key \"" + key + "\"");
                }

                Object value = x.nextValue();
                // Only add value if non-null
                if (value != null) {
                    this.put(key, value);
                }
            }

            // Pairs are separated by ','.

            switch (x.nextClean()) {
            case ';':
                // In strict mode semicolon is not a valid separator
                if (jsonParserConfiguration.isStrictMode()) {
                    throw x.syntaxError("Strict mode error: Invalid character ';' found");
                }
            case ',':
                if (x.nextClean() == '}') {
                    // trailing commas are not allowed in strict mode
                    if (jsonParserConfiguration.isStrictMode()) {
                        throw x.syntaxError("Strict mode error: Expected another object element");
                    }
                    return;
                }
                if (x.end()) {
                    throw x.syntaxError("A JSONObject text must end with '}'");
                }
                x.back();
                break;
            case '}':
                if (isInitial && jsonParserConfiguration.isStrictMode() && x.nextClean() != 0) {
                    throw x.syntaxError("Strict mode error: Unparsed characters found at end of input text");
                }
                return;
            default:
                throw x.syntaxError("Expected a ',' or '}'");
            }
        }
    }

    /**
     * Construct a JSONObject from a Map.
     *
     * @param m
     *            A map object that can be used to initialize the contents of
     *            the JSONObject.
     * @throws JSONException
     *            If a value in the map is non-finite number.
     * @throws NullPointerException
     *            If a key in the map is <code>null</code>
     */
    public JSONObject(Map<?, ?> m) {
      this(m, 0, new JSONParserConfiguration());
    }

    /**
     * Construct a JSONObject from a Map with custom json parse configurations.
     *
     * @param m
     *            A map object that can be used to initialize the contents of
     *            the JSONObject.
     * @param jsonParserConfiguration
     *            Variable to pass parser custom configuration for json parsing.
     */
    public JSONObject(Map<?, ?> m, JSONParserConfiguration jsonParserConfiguration) {
        this(m, 0, jsonParserConfiguration);
    }

    /**
     * Construct a JSONObject from a map with recursion depth.
     */
    private JSONObject(Map<?, ?> m, int recursionDepth, JSONParserConfiguration jsonParserConfiguration) {
        if (recursionDepth > jsonParserConfiguration.getMaxNestingDepth()) {
          throw new JSONException("JSONObject has reached recursion depth limit of " + jsonParserConfiguration.getMaxNestingDepth());
        }
        if (m == null) {
            this.map = newInternalMap(-1);
        } else {
            this.map = newInternalMap(m.size());
        	for (final Entry<?, ?> e : m.entrySet()) {
        	    if(e.getKey() == null) {
        	        throw new NullPointerException("Null key.");
        	    }
                final Object value = e.getValue();
                if (value != null || jsonParserConfiguration.isUseNativeNulls()) {
                    testValidity(value);
                    this.map.put(String.valueOf(e.getKey()), wrap(value, recursionDepth + 1, jsonParserConfiguration));
                }
            }
        }
    }

    /**
     * Construct a JSONObject from an Object using bean getters. It reflects on
     * all of the public methods of the object. For each of the methods with no
     * parameters and a name starting with <code>"get"</code> or
     * <code>"is"</code> followed by an uppercase letter, the method is invoked,
     * and a key and the value returned from the getter method are put into the
     * new JSONObject.
     * <p>
     * The key is formed by removing the <code>"get"</code> or <code>"is"</code>
     * prefix. If the second remaining character is not upper case, then the
     * first character is converted to lower case.
     * <p>
     * Methods that are <code>static</code>, return <code>void</code>,
     * have parameters, or are "bridge" methods, are ignored.
     * <p>
     * For example, if an object has a method named <code>"getName"</code>, and
     * if the result of calling <code>object.getName()</code> is
     * <code>"Larry Fine"</code>, then the JSONObject will contain
     * <code>"name": "Larry Fine"</code>.
     * <p>
     * The {@link JSONPropertyName} annotation can be used on a bean getter to
     * override key name used in the JSONObject. For example, using the object
     * above with the <code>getName</code> method, if we annotated it with:
     * <pre>
     * &#64;JSONPropertyName("FullName")
     * public String getName() { return this.name; }
     * </pre>
     * The resulting JSON object would contain <code>"FullName": "Larry Fine"</code>
     * <p>
     * Similarly, the {@link JSONPropertyName} annotation can be used on non-
     * <code>get</code> and <code>is</code> methods. We can also override key
     * name used in the JSONObject as seen below even though the field would normally
     * be ignored:
     * <pre>
     * &#64;JSONPropertyName("FullName")
     * public String fullName() { return this.name; }
     * </pre>
     * The resulting JSON object would contain <code>"FullName": "Larry Fine"</code>
     * <p>
     * The {@link JSONPropertyIgnore} annotation can be used to force the bean property
     * to not be serialized into JSON. If both {@link JSONPropertyIgnore} and
     * {@link JSONPropertyName} are defined on the same method, a depth comparison is
     * performed and the one closest to the concrete class being serialized is used.
     * If both annotations are at the same level, then the {@link JSONPropertyIgnore}
     * annotation takes precedent and the field is not serialized.
     * For example, the following declaration would prevent the <code>getName</code>
     * method from being serialized:
     * <pre>
     * &#64;JSONPropertyName("FullName")
     * &#64;JSONPropertyIgnore
     * public String getName() { return this.name; }
     * </pre>
     *
     * @param bean
     *            An object that has getter methods that should be used to make
     *            a JSONObject.
     * @throws JSONException
     *            If a getter returned a non-finite number.
     */
    public JSONObject(Object bean) {
        this();
        this.populateMap(bean, new JSONParserConfiguration());
    }

    public JSONObject(Object bean, JSONParserConfiguration jsonParserConfiguration) {
        this();
        this.populateMap(bean, jsonParserConfiguration);
    }

    private JSONObject(Object bean, Set<Object> objectsRecord) {
        this();
        this.populateMap(bean, objectsRecord, new JSONParserConfiguration());
    }

    /**
     * Construct a JSONObject from an Object, using reflection to find the
     * public members. The resulting JSONObject's keys will be the strings from
     * the names array, and the values will be the field values associated with
     * those keys in the object. If a key is not found or not visible, then it
     * will not be copied into the new JSONObject.
     *
     * @param object
     *            An object that has fields that should be used to make a
     *            JSONObject.
     * @param names
     *            An array of strings, the names of the fields to be obtained
     *            from the object.
     */
    public JSONObject(Object object, String ... names) {
        this(names.length);
        Class<?> c = object.getClass();
        for (int i = 0; i < names.length; i += 1) {
            String name = names[i];
            try {
                this.putOpt(name, c.getField(name).get(object));
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Construct a JSONObject from a source JSON text string. This is the most
     * commonly used JSONObject constructor.
     *
     * @param source
     *            A string beginning with <code>{</code>&nbsp;<small>(left
     *            brace)</small> and ending with <code>}</code>
     *            &nbsp;<small>(right brace)</small>.
     * @exception JSONException
     *                If there is a syntax error in the source string or a
     *                duplicated key.
     */
    public JSONObject(String source) throws JSONException {
        this(source, new JSONParserConfiguration());
    }

    /**
     * Construct a JSONObject from a source JSON text string with custom json parse configurations.
     * This is the most commonly used JSONObject constructor.
     *
     * @param source
     *            A string beginning with <code>{</code>&nbsp;<small>(left
     *            brace)</small> and ending with <code>}</code>
     *            &nbsp;<small>(right brace)</small>.
     * @param jsonParserConfiguration
     *            Variable to pass parser custom configuration for json parsing.
     * @exception JSONException
     *                If there is a syntax error in the source string or a
     *                duplicated key.
     */
    public JSONObject(String source, JSONParserConfiguration jsonParserConfiguration) throws JSONException {
        this(new JSONTokener(source, jsonParserConfiguration), jsonParserConfiguration);
    }

    /**
     * Construct a JSONObject from a ResourceBundle.
     *
     * @param baseName
     *            The ResourceBundle base name.
     * @param locale
     *            The Locale to load the ResourceBundle for.
     * @throws JSONException
     *             If any JSONExceptions are detected.
     */
    public JSONObject(String baseName, Locale locale) throws JSONException {
        this();
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale,
                Thread.currentThread().getContextClassLoader());

// Iterate through the keys in the bundle.

        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (key != null) {

// Go through the path, ensuring that there is a nested JSONObject for each
// segment except the last. Add the value using the last segment's name into
// the deepest nested JSONObject.

                String[] path = ((String) key).split("\\.");
                int last = path.length - 1;
                JSONObject target = this;
                for (int i = 0; i < last; i += 1) {
                    String segment = path[i];
                    JSONObject nextTarget = target.optJSONObject(segment);
                    if (nextTarget == null) {
                        nextTarget = new JSONObject();
                        target.put(segment, nextTarget);
                    }
                    target = nextTarget;
                }
                target.put(path[last], bundle.getString((String) key));
            }
        }
    }

    /**
     * Constructor to specify an initial capacity of the internal map. Useful for library
     * internal calls where we know, or at least can best guess, how big this JSONObject
     * will be.
     *
     * @param initialCapacity initial capacity of the internal map.
     */
    protected JSONObject(int initialCapacity){
        this.map = newInternalMap(initialCapacity);
    }

    /**
     * Accumulate values under a key. It is similar to the put method except
     * that if there is already an object stored under the key then a JSONArray
     * is stored under the key to hold all of the accumulated values. If there
     * is already a JSONArray, then the new value is appended to it. In
     * contrast, the put method replaces the previous value.
     *
     * If only one value is accumulated that is not a JSONArray, then the result
     * will be the same as using put. But if multiple values are accumulated,
     * then the result will be like append.
     *
     * @param key
     *            A key string.
     * @param value
     *            An object to be accumulated under the key.
     * @return this.
     * @throws JSONException
     *            If the value is non-finite number.
     * @throws NullPointerException
     *            If the key is <code>null</code>.
     */
    public JSONObject accumulate(String key, Object value) throws JSONException {
        testValidity(value);
        Object object = this.opt(key);
        if (object == null) {
            this.put(key,
                    value instanceof JSONArray ? new JSONArray().put(value)
                            : value);
        } else if (object instanceof JSONArray) {
            ((JSONArray) object).put(value);
        } else {
            this.put(key, new JSONArray().put(object).put(value));
        }
        return this;
    }

    /**
     * Append values to the array under a key. If the key does not exist in the
     * JSONObject, then the key is put in the JSONObject with its value being a
     * JSONArray containing the value parameter. If the key was already
     * associated with a JSONArray, then the value parameter is appended to it.
     *
     * @param key
     *            A key string.
     * @param value
     *            An object to be accumulated under the key.
     * @return this.
     * @throws JSONException
     *            If the value is non-finite number or if the current value associated with
     *             the key is not a JSONArray.
     * @throws NullPointerException
     *            If the key is <code>null</code>.
     */
    public JSONObject append(String key, Object value) throws JSONException {
        testValidity(value);
        Object object = this.opt(key);
        if (object == null) {
            this.put(key, new JSONArray().put(value));
        } else if (object instanceof JSONArray) {
            this.put(key, ((JSONArray) object).put(value));
        } else {
            throw wrongValueFormatException(key, "JSONArray", null, null);
        }
        return this;
    }

    /**
     * Produce a string from a double. The string "null" will be returned if the
     * number is not finite.
     *
     * @param d
     *            A double.
     * @return A String.
     */
    public static String doubleToString(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            return "null";
        }

// Shave off trailing zeros and decimal point, if possible.

        String string = Double.toString(d);
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0
                && string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    /**
     * Get the value object associated with a key.
     *
     * @param key
     *            A key string.
     * @return The object associated with the key.
     * @throws JSONException
     *             if the key is not found.
     */
    public Object get(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        Object object = this.opt(key);
        if (object == null) {
            throw new JSONException("JSONObject[" + quote(key) + "] not found.");
        }
        return object;
    }

    /**
     * Get the enum value associated with a key.
     *
     * @param <E>
     *            Enum Type
     * @param clazz
     *           The type of enum to retrieve.
     * @param key
     *           A key string.
     * @return The enum value associated with the key
     * @throws JSONException
     *             if the key is not found or if the value cannot be converted
     *             to an enum.
     */
    public <E extends Enum<E>> E getEnum(Class<E> clazz, String key) throws JSONException {
        E val = optEnum(clazz, key);
        if(val==null) {
            // JSONException should really take a throwable argument.
            // If it did, I would re-implement this with the Enum.valueOf
            // method and place any thrown exception in the JSONException
            throw wrongValueFormatException(key, "enum of type " + quote(clazz.getSimpleName()), opt(key), null);
        }
        return val;
    }

    /**
     * Get the boolean value associated with a key.
     *
     * @param key
     *            A key string.
     * @return The truth.
     * @throws JSONException
     *             if the value is not a Boolean or the String "true" or
     *             "false".
     */
    public boolean getBoolean(String key) throws JSONException {
        Object object = this.get(key);
        if (Boolean.FALSE.equals(object)
                || (object instanceof String && "false".equalsIgnoreCase((String) object))) {
            return false;
        } else if (Boolean.TRUE.equals(object)
                || (object instanceof String && "true".equalsIgnoreCase((String) object))) {
            return true;
        }
        throw wrongValueFormatException(key, "Boolean", object, null);
    }

    /**
     * Get the BigInteger value associated with a key.
     *
     * @param key
     *            A key string.
     * @return The numeric value.
     * @throws JSONException
     *             if the key is not found or if the value cannot
     *             be converted to BigInteger.
     */
    public BigInteger getBigInteger(String key) throws JSONException {
        Object object = this.get(key);
        BigInteger ret = objectToBigInteger(object, null);
        if (ret != null) {
            return ret;
        }
        throw wrongValueFormatException(key, "BigInteger", object, null);
    }

    /**
     * Get the BigDecimal value associated with a key. If the value is float or
     * double, the {@link BigDecimal#BigDecimal(double)} constructor will
     * be used. See notes on the constructor for conversion issues that may
     * arise.
     *
     * @param key
     *            A key string.
     * @return The numeric value.
     * @throws JSONException
     *             if the key is not found or if the value
     *             cannot be converted to BigDecimal.
     */
    public BigDecimal getBigDecimal(String key) throws JSONException {
        Object object = this.get(key);
        BigDecimal ret = objectToBigDecimal(object, null);
        if (ret != null) {
            return ret;
        }
        throw wrongValueFormatException(key, "BigDecimal", object, null);
    }

    /**
     * Get the double value associated with a key.
     *
     * @param key
     *            A key string.
     * @return The numeric value.
     * @throws JSONException
     *             if the key is not found or if the value is not a Number
     *             object and cannot be converted to a number.
     */
    public double getDouble(String key) throws JSONException {
        final Object object = this.get(key);
        if(object instanceof Number) {
            return ((Number)object).doubleValue();
        }
        try {
            return Double.parseDouble(object.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(key, "double", object, e);
        }
    }

    /**
     * Get the float value associated with a key.
     *
     * @param key
     *            A key string.
     * @return The numeric value.
     * @throws JSONException
     *             if the key is not found or if the value is not a Number
     *             object and cannot be converted to a number.
     */
    public float getFloat(String key) throws JSONException {
        final Object object = this.get(key);
        if(object instanceof Number) {
            return ((Number)object).floatValue();
        }
        try {
            return Float.parseFloat(object.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(key, "float", object, e);
        }
    }

    /**
     * Get the Number value associated with a key.
     *
     * @param key
     *            A key string.
     * @return The numeric value.
     * @throws JSONException
     *             if the key is not found or if the value is not a Number
     *             object and cannot be converted to a number.
     */
    public Number getNumber(String key) throws JSONException {
        Object object = this.get(key);
        try {
            if (object instanceof Number) {
                return (Number)object;
            }
            return stringToNumber(object.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(key, "number", object, e);
        }
    }

    /**
     * Get the int value associated with a key.
     *
     * @param key
     *            A key string.
     * @return The integer value.
     * @throws JSONException
     *             if the key is not found or if the value cannot be converted
     *             to an integer.
     */
    public int getInt(String key) throws JSONException {
        final Object object = this.get(key);
        if(object instanceof Number) {
            return ((Number)object).intValue();
        }
        try {
            return Integer.parseInt(object.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(key, "int", object, e);
        }
    }

    /**
     * Get the JSONArray value associated with a key.
     *
     * @param key
     *            A key string.
     * @return A JSONArray which is the value.
     * @throws JSONException
     *             if the key is not found or if the value is not a JSONArray.
     */
    public JSONArray getJSONArray(String key) throws JSONException {
        Object object = this.get(key);
        if (object instanceof JSONArray) {
            return (JSONArray) object;
        }
        throw wrongValueFormatException(key, "JSONArray", object, null);
    }

    /**
     * Get the JSONObject value associated with a key.
     *
     * @param key
     *            A key string.
     * @return A JSONObject which is the value.
     * @throws JSONException
     *             if the key is not found or if the value is not a JSONObject.
     */
    public JSONObject getJSONObject(String key) throws JSONException {
        Object object = this.get(key);
        if (object instanceof JSONObject) {
            return (JSONObject) object;
        }
        throw wrongValueFormatException(key, "JSONObject", object, null);
    }

    /**
     * Get the long value associated with a key.
     *
     * @param key
     *            A key string.
     * @return The long value.
     * @throws JSONException
     *             if the key is not found or if the value cannot be converted
     *             to a long.
     */
    public long getLong(String key) throws JSONException {
        final Object object = this.get(key);
        if(object instanceof Number) {
            return ((Number)object).longValue();
        }
        try {
            return Long.parseLong(object.toString());
        } catch (Exception e) {
            throw wrongValueFormatException(key, "long", object, e);
        }
    }

    /**
     * Get an array of field names from a JSONObject.
     *
     * @param jo
     *            JSON object
     * @return An array of field names, or null if there are no names.
     */
    public static String[] getNames(JSONObject jo) {
        if (jo.isEmpty()) {
            return null;
        }
        return jo.keySet().toArray(new String[jo.length()]);
    }

    /**
     * Get an array of public field names from an Object.
     *
     * @param object
     *            object to read
     * @return An array of field names, or null if there are no names.
     */
    public static String[] getNames(Object object) {
        if (object == null) {
            return null;
        }
        Class<?> klass = object.getClass();
        Field[] fields = klass.getFields();
        int length = fields.length;
        if (length == 0) {
            return null;
        }
        String[] names = new String[length];
        for (int i = 0; i < length; i += 1) {
            names[i] = fields[i].getName();
        }
        return names;
    }

    /**
     * Get the string associated with a key.
     *
     * @param key
     *            A key string.
     * @return A string which is the value.
     * @throws JSONException
     *             if there is no string value for the key.
     */
    public String getString(String key) throws JSONException {
        Object object = this.get(key);
        if (object instanceof String) {
            return (String) object;
        }
        throw wrongValueFormatException(key, "string", object, null);
    }

    /**
     * Determine if the JSONObject contains a specific key.
     *
     * @param key
     *            A key string.
     * @return true if the key exists in the JSONObject.
     */
    public boolean has(String key) {
        return this.map.containsKey(key);
    }

    /**
     * Increment a property of a JSONObject. If there is no such property,
     * create one with a value of 1 (Integer). If there is such a property, and if it is
     * an Integer, Long, Double, Float, BigInteger, or BigDecimal then add one to it.
     * No overflow bounds checking is performed, so callers should initialize the key
     * prior to this call with an appropriate type that can handle the maximum expected
     * value.
     *
     * @param key
     *            A key string.
     * @return this.
     * @throws JSONException
     *             If there is already a property with this name that is not an
     *             Integer, Long, Double, or Float.
     */
    public JSONObject increment(String key) throws JSONException {
        Object value = this.opt(key);
        if (value == null) {
            this.put(key, 1);
        } else if (value instanceof Integer) {
            this.put(key, ((Integer) value).intValue() + 1);
        } else if (value instanceof Long) {
            this.put(key, ((Long) value).longValue() + 1L);
        } else if (value instanceof BigInteger) {
            this.put(key, ((BigInteger)value).add(BigInteger.ONE));
        } else if (value instanceof Float) {
            this.put(key, ((Float) value).floatValue() + 1.0f);
        } else if (value instanceof Double) {
            this.put(key, ((Double) value).doubleValue() + 1.0d);
        } else if (value instanceof BigDecimal) {
            this.put(key, ((BigDecimal)value).add(BigDecimal.ONE));
        } else {
            throw new JSONException("Unable to increment [" + quote(key) + "].");
        }
        return this;
    }

    /**
     * Determine if the value associated with the key is <code>null</code> or if there is no
     * value.
     *
     * @param key
     *            A key string.
     * @return true if there is no value associated with the key or if the value
     *        is the JSONObject.NULL object.
     */
    public boolean isNull(String key) {
        return JSONObject.NULL.equals(this.opt(key));
    }

    /**
     * Get an enumeration of the keys of the JSONObject. Modifying this key Set will also
     * modify the JSONObject. Use with caution.
     *
     * @see Set#iterator()
     *
     * @return An iterator of the keys.
     */
    public Iterator<String> keys() {
        return this.keySet().iterator();
    }

    /**
     * Get a set of keys of the JSONObject. Modifying this key Set will also modify the
     * JSONObject. Use with caution.
     *
     * @see Map#keySet()
     *
     * @return A keySet.
     */
    public Set<String> keySet() {
        return this.map.keySet();
    }

    /**
     * Get a set of entries of the JSONObject. These are raw values and may not
     * match what is returned by the JSONObject get* and opt* functions. Modifying
     * the returned EntrySet or the Entry objects contained therein will modify the
     * backing JSONObject. This does not return a clone or a read-only view.
     *
     * Use with caution.
     *
     * @see Map#entrySet()
     *
     * @return An Entry Set
     */
    protected Set<Entry<String, Object>> entrySet() {
        return this.map.entrySet();
    }

    /**
     * Get the number of keys stored in the JSONObject.
     *
     * @return The number of keys in the JSONObject.
     */
    public int length() {
        return this.map.size();
    }

    /**
     * Removes all of the elements from this JSONObject.
     * The JSONObject will be empty after this call returns.
     */
    public void clear() {
        this.map.clear();
    }

    /**
     * Check if JSONObject is empty.
     *
     * @return true if JSONObject is empty, otherwise false.
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * Produce a JSONArray containing the names of the elements of this
     * JSONObject.
     *
     * @return A JSONArray containing the key strings, or null if the JSONObject
     *        is empty.
     */
    public JSONArray names() {
    	if(this.map.isEmpty()) {
    		return null;
    	}
        return new JSONArray(this.map.keySet());
    }

    /**
     * Produce a string from a Number.
     *
     * @param number
     *            A Number
     * @return A String.
     * @throws JSONException
     *             If n is a non-finite number.
     */
    public static String numberToString(Number number) throws JSONException {
        if (number == null) {
            throw new JSONException("Null pointer");
        }
        testValidity(number);

        // Shave off trailing zeros and decimal point, if possible.

        String string = number.toString();
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0
                && string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    /**
     * Get an optional value associated with a key.
     *
     * @param key
     *            A key string.
     * @return An object which is the value, or null if there is no value.
     */
    public Object opt(String key) {
        return key == null ? null : this.map.get(key);
    }

    /**
     * Get the enum value associated with a key.
     *
     * @param <E>
     *            Enum Type
     * @param clazz
     *            The type of enum to retrieve.
     * @param key
     *            A key string.
     * @return The enum value associated with the key or null if not found
     */
    public <E extends Enum<E>> E optEnum(Class<E> clazz, String key) {
        return this.optEnum(clazz, key, null);
    }

    /**
     * Get the enum value associated with a key.
     *
     * @param <E>
     *            Enum Type
     * @param clazz
     *            The type of enum to retrieve.
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default in case the value is not found
     * @return The enum value associated with the key or defaultValue
     *            if the value is not found or cannot be assigned to <code>clazz</code>
     */
    public <E extends Enum<E>> E optEnum(Class<E> clazz, String key, E defaultValue) {
        try {
            Object val = this.opt(key);
            if (NULL.equals(val)) {
                return defaultValue;
            }
            if (clazz.isAssignableFrom(val.getClass())) {
                // we just checked it!
                @SuppressWarnings("unchecked")
                E myE = (E) val;
                return myE;
            }
            return Enum.valueOf(clazz, val.toString());
        } catch (IllegalArgumentException e) {
            return defaultValue;
        } catch (NullPointerException e) {
            return defaultValue;
        }
    }

    /**
     * Get an optional boolean associated with a key. It returns false if there
     * is no such key, or if the value is not Boolean.TRUE or the String "true".
     *
     * @param key
     *            A key string.
     * @return The truth.
     */
    public boolean optBoolean(String key) {
        return this.optBoolean(key, false);
    }

    /**
     * Get an optional boolean associated with a key. It returns the
     * defaultValue if there is no such key, or if it is not a Boolean or the
     * String "true" or "false" (case insensitive).
     *
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default.
     * @return The truth.
     */
    public boolean optBoolean(String key, boolean defaultValue) {
        Object val = this.opt(key);
        if (NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Boolean){
            return ((Boolean) val).booleanValue();
        }
        try {
            // we'll use the get anyway because it does string conversion.
            return this.getBoolean(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get an optional boolean object associated with a key. It returns false if there
     * is no such key, or if the value is not Boolean.TRUE or the String "true".
     *
     * @param key
     *            A key string.
     * @return The truth.
     */
    public Boolean optBooleanObject(String key) {
        return this.optBooleanObject(key, false);
    }

    /**
     * Get an optional boolean object associated with a key. It returns the
     * defaultValue if there is no such key, or if it is not a Boolean or the
     * String "true" or "false" (case insensitive).
     *
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default.
     * @return The truth.
     */
    public Boolean optBooleanObject(String key, Boolean defaultValue) {
        Object val = this.opt(key);
        if (NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Boolean){
            return ((Boolean) val).booleanValue();
        }
        try {
            // we'll use the get anyway because it does string conversion.
            return this.getBoolean(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get an optional BigDecimal associated with a key, or the defaultValue if
     * there is no such key or if its value is not a number. If the value is a
     * string, an attempt will be made to evaluate it as a number. If the value
     * is float or double, then the {@link BigDecimal#BigDecimal(double)}
     * constructor will be used. See notes on the constructor for conversion
     * issues that may arise.
     *
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default.
     * @return An object which is the value.
     */
    public BigDecimal optBigDecimal(String key, BigDecimal defaultValue) {
        Object val = this.opt(key);
        return objectToBigDecimal(val, defaultValue);
    }

    /**
     * @param val value to convert
     * @param defaultValue default value to return is the conversion doesn't work or is null.
     * @return BigDecimal conversion of the original value, or the defaultValue if unable
     *          to convert.
     */
    static BigDecimal objectToBigDecimal(Object val, BigDecimal defaultValue) {
        return objectToBigDecimal(val, defaultValue, true);
    }

    /**
     * @param val value to convert
     * @param defaultValue default value to return is the conversion doesn't work or is null.
     * @param exact When <code>true</code>, then {@link Double} and {@link Float} values will be converted exactly.
     *      When <code>false</code>, they will be converted to {@link String} values before converting to {@link BigDecimal}.
     * @return BigDecimal conversion of the original value, or the defaultValue if unable
     *          to convert.
     */
    static BigDecimal objectToBigDecimal(Object val, BigDecimal defaultValue, boolean exact) {
        if (NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof BigDecimal){
            return (BigDecimal) val;
        }
        if (val instanceof BigInteger){
            return new BigDecimal((BigInteger) val);
        }
        if (val instanceof Double || val instanceof Float){
            if (!numberIsFinite((Number)val)) {
                return defaultValue;
            }
            if (exact) {
                return new BigDecimal(((Number)val).doubleValue());
            }
            // use the string constructor so that we maintain "nice" values for doubles and floats
            // the double constructor will translate doubles to "exact" values instead of the likely
            // intended representation
            return new BigDecimal(val.toString());
        }
        if (val instanceof Long || val instanceof Integer
                || val instanceof Short || val instanceof Byte){
            return new BigDecimal(((Number) val).longValue());
        }
        // don't check if it's a string in case of unchecked Number subclasses
        try {
            return new BigDecimal(val.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get an optional BigInteger associated with a key, or the defaultValue if
     * there is no such key or if its value is not a number. If the value is a
     * string, an attempt will be made to evaluate it as a number.
     *
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default.
     * @return An object which is the value.
     */
    public BigInteger optBigInteger(String key, BigInteger defaultValue) {
        Object val = this.opt(key);
        return objectToBigInteger(val, defaultValue);
    }

    /**
     * @param val value to convert
     * @param defaultValue default value to return is the conversion doesn't work or is null.
     * @return BigInteger conversion of the original value, or the defaultValue if unable
     *          to convert.
     */
    static BigInteger objectToBigInteger(Object val, BigInteger defaultValue) {
        if (NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof BigInteger){
            return (BigInteger) val;
        }
        if (val instanceof BigDecimal){
            return ((BigDecimal) val).toBigInteger();
        }
        if (val instanceof Double || val instanceof Float){
            if (!numberIsFinite((Number)val)) {
                return defaultValue;
            }
            return new BigDecimal(((Number) val).doubleValue()).toBigInteger();
        }
        if (val instanceof Long || val instanceof Integer
                || val instanceof Short || val instanceof Byte){
            return BigInteger.valueOf(((Number) val).longValue());
        }
        // don't check if it's a string in case of unchecked Number subclasses
        try {
            // the other opt functions handle implicit conversions, i.e.
            // jo.put("double",1.1d);
            // jo.optInt("double"); -- will return 1, not an error
            // this conversion to BigDecimal then to BigInteger is to maintain
            // that type cast support that may truncate the decimal.
            final String valStr = val.toString();
            if(isDecimalNotation(valStr)) {
                return new BigDecimal(valStr).toBigInteger();
            }
            return new BigInteger(valStr);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get an optional double associated with a key, or NaN if there is no such
     * key or if its value is not a number. If the value is a string, an attempt
     * will be made to evaluate it as a number.
     *
     * @param key
     *            A string which is the key.
     * @return An object which is the value.
     */
    public double optDouble(String key) {
        return this.optDouble(key, Double.NaN);
    }

    /**
     * Get an optional double associated with a key, or the defaultValue if
     * there is no such key or if its value is not a number. If the value is a
     * string, an attempt will be made to evaluate it as a number.
     *
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default.
     * @return An object which is the value.
     */
    public double optDouble(String key, double defaultValue) {
        Number val = this.optNumber(key);
        if (val == null) {
            return defaultValue;
        }
        return val.doubleValue();
    }

    /**
     * Get an optional Double object associated with a key, or NaN if there is no such
     * key or if its value is not a number. If the value is a string, an attempt
     * will be made to evaluate it as a number.
     *
     * @param key
     *            A string which is the key.
     * @return An object which is the value.
     */
    public Double optDoubleObject(String key) {
        return this.optDoubleObject(key, Double.NaN);
    }

    /**
     * Get an optional Double object associated with a key, or the defaultValue if
     * there is no such key or if its value is not a number. If the value is a
     * string, an attempt will be made to evaluate it as a number.
     *
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default.
     * @return An object which is the value.
     */
    public Double optDoubleObject(String key, Double defaultValue) {
        Number val = this.optNumber(key);
        if (val == null) {
            return defaultValue;
        }
        return val.doubleValue();
    }

    /**
     * Get the optional float value associated with an index. NaN is returned
     * if there is no value for the index, or if the value is not a number and
     * cannot be converted to a number.
     *
     * @param key
     *            A key string.
     * @return The value.
     */
    public float optFloat(String key) {
        return this.optFloat(key, Float.NaN);
    }

    /**
     * Get the optional float value associated with an index. The defaultValue
     * is returned if there is no value for the index, or if the value is not a
     * number and cannot be converted to a number.
     *
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default value.
     * @return The value.
     */
    public float optFloat(String key, float defaultValue) {
        Number val = this.optNumber(key);
        if (val == null) {
            return defaultValue;
        }
        final float floatValue = val.floatValue();
        // if (Float.isNaN(floatValue) || Float.isInfinite(floatValue)) {
        // return defaultValue;
        // }
        return floatValue;
    }

    /**
     * Get the optional Float object associated with an index. NaN is returned
     * if there is no value for the index, or if the value is not a number and
     * cannot be converted to a number.
     *
     * @param key
     *            A key string.
     * @return The object.
     */
    public Float optFloatObject(String key) {
        return this.optFloatObject(key, Float.NaN);
    }

    /**
     * Get the optional Float object associated with an index. The defaultValue
     * is returned if there is no value for the index, or if the value is not a
     * number and cannot be converted to a number.
     *
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default object.
     * @return The object.
     */
    public Float optFloatObject(String key, Float defaultValue) {
        Number val = this.optNumber(key);
        if (val == null) {
            return defaultValue;
        }
        final Float floatValue = val.floatValue();
        // if (Float.isNaN(floatValue) || Float.isInfinite(floatValue)) {
        // return defaultValue;
        // }
        return floatValue;
    }

    /**
     * Get an optional int value associated with a key, or zero if there is no
     * such key or if the value is not a number. If the value is a string, an
     * attempt will be made to evaluate it as a number.
     *
     * @param key
     *            A key string.
     * @return An object which is the value.
     */
    public int optInt(String key) {
        return this.optInt(key, 0);
    }

    /**
     * Get an optional int value associated with a key, or the default if there
     * is no such key or if the value is not a number. If the value is a string,
     * an attempt will be made to evaluate it as a number.
     *
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default.
     * @return An object which is the value.
     */
    public int optInt(String key, int defaultValue) {
        final Number val = this.optNumber(key, null);
        if (val == null) {
            return defaultValue;
        }
        return val.intValue();
    }

    /**
     * Get an optional Integer object associated with a key, or zero if there is no
     * such key or if the value is not a number. If the value is a string, an
     * attempt will be made to evaluate it as a number.
     *
     * @param key
     *            A key string.
     * @return An object which is the value.
     */
    public Integer optIntegerObject(String key) {
        return this.optIntegerObject(key, 0);
    }

    /**
     * Get an optional Integer object associated with a key, or the default if there
     * is no such key or if the value is not a number. If the value is a string,
     * an attempt will be made to evaluate it as a number.
     *
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default.
     * @return An object which is the value.
     */
    public Integer optIntegerObject(String key, Integer defaultValue) {
        final Number val = this.optNumber(key, null);
        if (val == null) {
            return defaultValue;
        }
        return val.intValue();
    }

    /**
     * Get an optional JSONArray associated with a key. It returns null if there
     * is no such key, or if its value is not a JSONArray.
     *
     * @param key
     *            A key string.
     * @return A JSONArray which is the value.
     */
    public JSONArray optJSONArray(String key) {
        return this.optJSONArray(key, null);
    }

    /**
     * Get an optional JSONArray associated with a key, or the default if there
     * is no such key, or if its value is not a JSONArray.
     *
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default.
     * @return A JSONArray which is the value.
     */
    public JSONArray optJSONArray(String key, JSONArray defaultValue) {
        Object object = this.opt(key);
        return object instanceof JSONArray ? (JSONArray) object : defaultValue;
    }

    /**
     * Get an optional JSONObject associated with a key. It returns null if
     * there is no such key, or if its value is not a JSONObject.
     *
     * @param key
     *            A key string.
     * @return A JSONObject which is the value.
     */
    public JSONObject optJSONObject(String key) { return this.optJSONObject(key, null); }

    /**
     * Get an optional JSONObject associated with a key, or the default if there
     * is no such key or if the value is not a JSONObject.
     *
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default.
     * @return An JSONObject which is the value.
     */
    public JSONObject optJSONObject(String key, JSONObject defaultValue) {
        Object object = this.opt(key);
        return object instanceof JSONObject ? (JSONObject) object : defaultValue;
    }

    /**
     * Get an optional long value associated with a key, or zero if there is no
     * such key or if the value is not a number. If the value is a string, an
     * attempt will be made to evaluate it as a number.
     *
     * @param key
     *            A key string.
     * @return An object which is the value.
     */
    public long optLong(String key) {
        return this.optLong(key, 0);
    }

    /**
     * Get an optional long value associated with a key, or the default if there
     * is no such key or if the value is not a number. If the value is a string,
     * an attempt will be made to evaluate it as a number.
     *
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default.
     * @return An object which is the value.
     */
    public long optLong(String key, long defaultValue) {
        final Number val = this.optNumber(key, null);
        if (val == null) {
            return defaultValue;
        }

        return val.longValue();
    }

    /**
     * Get an optional Long object associated with a key, or zero if there is no
     * such key or if the value is not a number. If the value is a string, an
     * attempt will be made to evaluate it as a number.
     *
     * @param key
     *            A key string.
     * @return An object which is the value.
     */
    public Long optLongObject(String key) {
        return this.optLongObject(key, 0L);
    }

    /**
     * Get an optional Long object associated with a key, or the default if there
     * is no such key or if the value is not a number. If the value is a string,
     * an attempt will be made to evaluate it as a number.
     *
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default.
     * @return An object which is the value.
     */
    public Long optLongObject(String key, Long defaultValue) {
        final Number val = this.optNumber(key, null);
        if (val == null) {
            return defaultValue;
        }

        return val.longValue();
    }

    /**
     * Get an optional {@link Number} value associated with a key, or <code>null</code>
     * if there is no such key or if the value is not a number. If the value is a string,
     * an attempt will be made to evaluate it as a number ({@link BigDecimal}). This method
     * would be used in cases where type coercion of the number value is unwanted.
     *
     * @param key
     *            A key string.
     * @return An object which is the value.
     */
    public Number optNumber(String key) {
        return this.optNumber(key, null);
    }

    /**
     * Get an optional {@link Number} value associated with a key, or the default if there
     * is no such key or if the value is not a number. If the value is a string,
     * an attempt will be made to evaluate it as a number. This method
     * would be used in cases where type coercion of the number value is unwanted.
     *
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default.
     * @return An object which is the value.
     */
    public Number optNumber(String key, Number defaultValue) {
        Object val = this.opt(key);
        if (NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Number){
            return (Number) val;
        }

        try {
            return stringToNumber(val.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get an optional string associated with a key. It returns an empty string
     * if there is no such key. If the value is not a string and is not null,
     * then it is converted to a string.
     *
     * @param key
     *            A key string.
     * @return A string which is the value.
     */
    public String optString(String key) {
        return this.optString(key, "");
    }

    /**
     * Get an optional string associated with a key. It returns the defaultValue
     * if there is no such key.
     *
     * @param key
     *            A key string.
     * @param defaultValue
     *            The default.
     * @return A string which is the value.
     */
    public String optString(String key, String defaultValue) {
        Object object = this.opt(key);
        return NULL.equals(object) ? defaultValue : object.toString();
    }

    /**
     * Populates the internal map of the JSONObject with the bean properties. The
     * bean can not be recursive.
     *
     * @see JSONObject#JSONObject(Object)
     *
     * @param bean
     *            the bean
     * @throws JSONException
     *            If a getter returned a non-finite number.
     */
    private void populateMap(Object bean, JSONParserConfiguration jsonParserConfiguration) {
        populateMap(bean, Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>()), jsonParserConfiguration);
    }

    private void populateMap(Object bean, Set<Object> objectsRecord, JSONParserConfiguration jsonParserConfiguration) {
        Class<?> klass = bean.getClass();

        // If klass is a System class then set includeSuperClass to false.

        boolean includeSuperClass = klass.getClassLoader() != null;

        Method[] methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();
        for (final Method method : methods) {
            final int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers)
                    && !Modifier.isStatic(modifiers)
                    && method.getParameterTypes().length == 0
                    && !method.isBridge()
                    && method.getReturnType() != Void.TYPE
                    && isValidMethodName(method.getName())) {
                final String key = getKeyNameFromMethod(method);
                if (key != null && !key.isEmpty()) {
                    try {
                        final Object result = method.invoke(bean);
                        if (result != null || jsonParserConfiguration.isUseNativeNulls()) {
                            // check cyclic dependency and throw error if needed
                            // the wrap and populateMap combination method is
                            // itself DFS recursive
                            if (objectsRecord.contains(result)) {
                                throw recursivelyDefinedObjectException(key);
                            }

                            objectsRecord.add(result);

                            testValidity(result);
                            this.map.put(key, wrap(result, objectsRecord));

                            objectsRecord.remove(result);

                            // we don't use the result anywhere outside of wrap
                            // if it's a resource we should be sure to close it
                            // after calling toString
                            if (result instanceof Closeable) {
                                try {
                                    ((Closeable) result).close();
                                } catch (IOException ignore) {
                                }
                            }
                        }
                    } catch (IllegalAccessException ignore) {
                    } catch (IllegalArgumentException ignore) {
                    } catch (InvocationTargetException ignore) {
                    }
                }
            }
        }
    }

    private static boolean isValidMethodName(String name) {
        return !"getClass".equals(name) && !"getDeclaringClass".equals(name);
    }

    private static String getKeyNameFromMethod(Method method) {
        final int ignoreDepth = getAnnotationDepth(method, JSONPropertyIgnore.class);
        if (ignoreDepth > 0) {
            final int forcedNameDepth = getAnnotationDepth(method, JSONPropertyName.class);
            if (forcedNameDepth < 0 || ignoreDepth <= forcedNameDepth) {
                // the hierarchy asked to ignore, and the nearest name override
                // was higher or non-existent
                return null;
            }
        }
        JSONPropertyName annotation = getAnnotation(method, JSONPropertyName.class);
        if (annotation != null && annotation.value() != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }
        String key;
        final String name = method.getName();
        if (name.startsWith("get") && name.length() > 3) {
            key = name.substring(3);
        } else if (name.startsWith("is") && name.length() > 2) {
            key = name.substring(2);
        } else {
            return null;
        }
        // if the first letter in the key is not uppercase, then skip.
        // This is to maintain backwards compatibility before PR406
        // (https://github.com/stleary/JSON-java/pull/406/)
        if (key.length() == 0 || Character.isLowerCase(key.charAt(0))) {
            return null;
        }
        if (key.length() == 1) {
            key = key.toLowerCase(Locale.ROOT);
        } else if (!Character.isUpperCase(key.charAt(1))) {
            key = key.substring(0, 1).toLowerCase(Locale.ROOT) + key.substring(1);
        }
        return key;
    }

    /**
     * Searches the class hierarchy to see if the method or it's super
     * implementations and interfaces has the annotation.
     *
     * @param <A>
     *            type of the annotation
     *
     * @param m
     *            method to check
     * @param annotationClass
     *            annotation to look for
     * @return the {@link Annotation} if the annotation exists on the current method
     *         or one of its super class definitions
     */
    private static <A extends Annotation> A getAnnotation(final Method m, final Class<A> annotationClass) {
        // if we have invalid data the result is null
        if (m == null || annotationClass == null) {
            return null;
        }

        if (m.isAnnotationPresent(annotationClass)) {
            return m.getAnnotation(annotationClass);
        }

        // if we've already reached the Object class, return null;
        Class<?> c = m.getDeclaringClass();
        if (c.getSuperclass() == null) {
            return null;
        }

        // check directly implemented interfaces for the method being checked
        for (Class<?> i : c.getInterfaces()) {
            try {
                Method im = i.getMethod(m.getName(), m.getParameterTypes());
                return getAnnotation(im, annotationClass);
            } catch (final SecurityException ex) {
                continue;
            } catch (final NoSuchMethodException ex) {
                continue;
            }
        }

        //If the superclass is Object, no annotations will be found any more
        if (Object.class.equals(c.getSuperclass()))
            return null;

        try {
            return getAnnotation(
                    c.getSuperclass().getMethod(m.getName(), m.getParameterTypes()),
                    annotationClass);
        } catch (final SecurityException ex) {
            return null;
        } catch (final NoSuchMethodException ex) {
            return null;
        }
    }

    /**
     * Searches the class hierarchy to see if the method or it's super
     * implementations and interfaces has the annotation. Returns the depth of the
     * annotation in the hierarchy.
     *
     * @param m
     *            method to check
     * @param annotationClass
     *            annotation to look for
     * @return Depth of the annotation or -1 if the annotation is not on the method.
     */
    private static int getAnnotationDepth(final Method m, final Class<? extends Annotation> annotationClass) {
        // if we have invalid data the result is -1
        if (m == null || annotationClass == null) {
            return -1;
        }

        if (m.isAnnotationPresent(annotationClass)) {
            return 1;
        }

        // if we've already reached the Object class, return -1;
        Class<?> c = m.getDeclaringClass();
        if (c.getSuperclass() == null) {
            return -1;
        }

        // check directly implemented interfaces for the method being checked
        for (Class<?> i : c.getInterfaces()) {
            try {
                Method im = i.getMethod(m.getName(), m.getParameterTypes());
                int d = getAnnotationDepth(im, annotationClass);
                if (d > 0) {
                    // since the annotation was on the interface, add 1
                    return d + 1;
                }
            } catch (final SecurityException ex) {
                continue;
            } catch (final NoSuchMethodException ex) {
                continue;
            }
        }

        //If the superclass is Object, no annotations will be found any more
        if (Object.class.equals(c.getSuperclass()))
            return -1;

        try {
            int d = getAnnotationDepth(
                    c.getSuperclass().getMethod(m.getName(), m.getParameterTypes()),
                    annotationClass);
            if (d > 0) {
                // since the annotation was on the superclass, add 1
                return d + 1;
            }
            return -1;
        } catch (final SecurityException ex) {
            return -1;
        } catch (final NoSuchMethodException ex) {
            return -1;
        }
    }

    /**
     * Put a key/boolean pair in the JSONObject.
     *
     * @param key
     *            A key string.
     * @param value
     *            A boolean which is the value.
     * @return this.
     * @throws JSONException
     *            If the value is non-finite number.
     * @throws NullPointerException
     *            If the key is <code>null</code>.
     */
    public JSONObject put(String key, boolean value) throws JSONException {
        return this.put(key, value ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Put a key/value pair in the JSONObject, where the value will be a
     * JSONArray which is produced from a Collection.
     *
     * @param key
     *            A key string.
     * @param value
     *            A Collection value.
     * @return this.
     * @throws JSONException
     *            If the value is non-finite number.
     * @throws NullPointerException
     *            If the key is <code>null</code>.
     */
    public JSONObject put(String key, Collection<?> value) throws JSONException {
        return this.put(key, new JSONArray(value));
    }

    /**
     * Put a key/double pair in the JSONObject.
     *
     * @param key
     *            A key string.
     * @param value
     *            A double which is the value.
     * @return this.
     * @throws JSONException
     *            If the value is non-finite number.
     * @throws NullPointerException
     *            If the key is <code>null</code>.
     */
    public JSONObject put(String key, double value) throws JSONException {
        return this.put(key, Double.valueOf(value));
    }

    /**
     * Put a key/float pair in the JSONObject.
     *
     * @param key
     *            A key string.
     * @param value
     *            A float which is the value.
     * @return this.
     * @throws JSONException
     *            If the value is non-finite number.
     * @throws NullPointerException
     *            If the key is <code>null</code>.
     */
    public JSONObject put(String key, float value) throws JSONException {
        return this.put(key, Float.valueOf(value));
    }

    /**
     * Put a key/int pair in the JSONObject.
     *
     * @param key
     *            A key string.
     * @param value
     *            An int which is the value.
     * @return this.
     * @throws JSONException
     *            If the value is non-finite number.
     * @throws NullPointerException
     *            If the key is <code>null</code>.
     */
    public JSONObject put(String key, int value) throws JSONException {
        return this.put(key, Integer.valueOf(value));
    }

    /**
     * Put a key/long pair in the JSONObject.
     *
     * @param key
     *            A key string.
     * @param value
     *            A long which is the value.
     * @return this.
     * @throws JSONException
     *            If the value is non-finite number.
     * @throws NullPointerException
     *            If the key is <code>null</code>.
     */
    public JSONObject put(String key, long value) throws JSONException {
        return this.put(key, Long.valueOf(value));
    }

    /**
     * Put a key/value pair in the JSONObject, where the value will be a
     * JSONObject which is produced from a Map.
     *
     * @param key
     *            A key string.
     * @param value
     *            A Map value.
     * @return this.
     * @throws JSONException
     *            If the value is non-finite number.
     * @throws NullPointerException
     *            If the key is <code>null</code>.
     */
    public JSONObject put(String key, Map<?, ?> value) throws JSONException {
        return this.put(key, new JSONObject(value));
    }

    /**
     * Put a key/value pair in the JSONObject. If the value is <code>null</code>, then the
     * key will be removed from the JSONObject if it is present.
     *
     * @param key
     *            A key string.
     * @param value
     *            An object which is the value. It should be of one of these
     *            types: Boolean, Double, Integer, JSONArray, JSONObject, Long,
     *            String, or the JSONObject.NULL object.
     * @return this.
     * @throws JSONException
     *            If the value is non-finite number.
     * @throws NullPointerException
     *            If the key is <code>null</code>.
     */
    public JSONObject put(String key, Object value) throws JSONException {
        if (key == null) {
            throw new NullPointerException("Null key.");
        }
        if (value != null) {
            testValidity(value);
            this.map.put(key, value);
        } else {
            this.remove(key);
        }
        return this;
    }

    /**
     * Put a key/value pair in the JSONObject, but only if the key and the value
     * are both non-null, and only if there is not already a member with that
     * name.
     *
     * @param key
     *            key to insert into
     * @param value
     *            value to insert
     * @return this.
     * @throws JSONException
     *             if the key is a duplicate
     */
    public JSONObject putOnce(String key, Object value) throws JSONException {
        if (key != null && value != null) {
            if (this.opt(key) != null) {
                throw new JSONException("Duplicate key \"" + key + "\"");
            }
            return this.put(key, value);
        }
        return this;
    }

    /**
     * Put a key/value pair in the JSONObject, but only if the key and the value
     * are both non-null.
     *
     * @param key
     *            A key string.
     * @param value
     *            An object which is the value. It should be of one of these
     *            types: Boolean, Double, Integer, JSONArray, JSONObject, Long,
     *            String, or the JSONObject.NULL object.
     * @return this.
     * @throws JSONException
     *             If the value is a non-finite number.
     */
    public JSONObject putOpt(String key, Object value) throws JSONException {
        if (key != null && value != null) {
            return this.put(key, value);
        }
        return this;
    }

    /**
     * Creates a JSONPointer using an initialization string and tries to
     * match it to an item within this JSONObject. For example, given a
     * JSONObject initialized with this document:
     * <pre>
     * {
     *     "a":{"b":"c"}
     * }
     * </pre>
     * and this JSONPointer string:
     * <pre>
     * "/a/b"
     * </pre>
     * Then this method will return the String "c".
     * A JSONPointerException may be thrown from code called by this method.
     *
     * @param jsonPointer string that can be used to create a JSONPointer
     * @return the item matched by the JSONPointer, otherwise null
     */
    public Object query(String jsonPointer) {
        return query(new JSONPointer(jsonPointer));
    }
    /**
     * Uses a user initialized JSONPointer  and tries to
     * match it to an item within this JSONObject. For example, given a
     * JSONObject initialized with this document:
     * <pre>
     * {
     *     "a":{"b":"c"}
     * }
     * </pre>
     * and this JSONPointer:
     * <pre>
     * "/a/b"
     * </pre>
     * Then this method will return the String "c".
     * A JSONPointerException may be thrown from code called by this method.
     *
     * @param jsonPointer string that can be used to create a JSONPointer
     * @return the item matched by the JSONPointer, otherwise null
     */
    public Object query(JSONPointer jsonPointer) {
        return jsonPointer.queryFrom(this);
    }

    /**
     * Queries and returns a value from this object using {@code jsonPointer}, or
     * returns null if the query fails due to a missing key.
     *
     * @param jsonPointer the string representation of the JSON pointer
     * @return the queried value or {@code null}
     * @throws IllegalArgumentException if {@code jsonPointer} has invalid syntax
     */
    public Object optQuery(String jsonPointer) {
    	return optQuery(new JSONPointer(jsonPointer));
    }

    /**
     * Queries and returns a value from this object using {@code jsonPointer}, or
     * returns null if the query fails due to a missing key.
     *
     * @param jsonPointer The JSON pointer
     * @return the queried value or {@code null}
     * @throws IllegalArgumentException if {@code jsonPointer} has invalid syntax
     */
    public Object optQuery(JSONPointer jsonPointer) {
        try {
            return jsonPointer.queryFrom(this);
        } catch (JSONPointerException e) {
            return null;
        }
    }

    /**
     * Produce a string in double quotes with backslash sequences in all the
     * right places. A backslash will be inserted within &lt;/, producing
     * &lt;\/, allowing JSON text to be delivered in HTML. In JSON text, a
     * string cannot contain a control character or an unescaped quote or
     * backslash.
     *
     * @param string
     *            A String
     * @return A String correctly formatted for insertion in a JSON text.
     */
    @SuppressWarnings("resource")
    public static String quote(String string) {
        if (string == null || string.isEmpty()) {
            return "\"\"";
        }
        Writer sw = new StringBuilderWriter(string.length() + 2);
        try {
            return quote(string, sw).toString();
        } catch (IOException ignored) {
            // will never happen - we are writing to a string writer
            return "";
        }
    }

    /**
     * Quotes a string and appends the result to a given Writer.
     *
     * @param string The input string to be quoted.
     * @param w      The Writer to which the quoted string will be appended.
     * @return The same Writer instance after appending the quoted string.
     * @throws IOException If an I/O error occurs while writing to the Writer.
     */
    public static Writer quote(String string, Writer w) throws IOException {
        if (string == null || string.isEmpty()) {
            w.write("\"\"");
            return w;
        }

        char b;
        char c = 0;
        String hhhh;
        int i;
        int len = string.length();

        w.write('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                w.write('\\');
                w.write(c);
                break;
            case '/':
                if (b == '<') {
                    w.write('\\');
                }
                w.write(c);
                break;
            case '\b':
                w.write("\\b");
                break;
            case '\t':
                w.write("\\t");
                break;
            case '\n':
                w.write("\\n");
                break;
            case '\f':
                w.write("\\f");
                break;
            case '\r':
                w.write("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
                        || (c >= '\u2000' && c < '\u2100')) {
                    w.write("\\u");
                    hhhh = Integer.toHexString(c);
                    w.write("0000", 0, 4 - hhhh.length());
                    w.write(hhhh);
                } else {
                    w.write(c);
                }
            }
        }
        w.write('"');
        return w;
    }

    /**
     * Remove a name and its value, if present.
     *
     * @param key
     *            The name to be removed.
     * @return The value that was associated with the name, or null if there was
     *         no value.
     */
    public Object remove(String key) {
        return this.map.remove(key);
    }

    /**
     * Determine if two JSONObjects are similar.
     * They must contain the same set of names which must be associated with
     * similar values.
     *
     * @param other The other JSONObject
     * @return true if they are equal
     */
    public boolean similar(Object other) {
        try {
            if (!(other instanceof JSONObject)) {
                return false;
            }
            if (!this.keySet().equals(((JSONObject)other).keySet())) {
                return false;
            }
            for (final Entry<String,?> entry : this.entrySet()) {
                String name = entry.getKey();
                Object valueThis = entry.getValue();
                Object valueOther = ((JSONObject)other).get(name);
                if(valueThis == valueOther) {
                	continue;
                }
                if(valueThis == null) {
                	return false;
                }
                if (valueThis instanceof JSONObject) {
                    if (!((JSONObject)valueThis).similar(valueOther)) {
                        return false;
                    }
                } else if (valueThis instanceof JSONArray) {
                    if (!((JSONArray)valueThis).similar(valueOther)) {
                        return false;
                    }
                } else if (valueThis instanceof Number && valueOther instanceof Number) {
                    if (!isNumberSimilar((Number)valueThis, (Number)valueOther)) {
                    	return false;
                    }
                } else if (valueThis instanceof JSONString && valueOther instanceof JSONString) {
                    if (!((JSONString) valueThis).toJSONString().equals(((JSONString) valueOther).toJSONString())) {
                    	return false;
                    }
                } else if (!valueThis.equals(valueOther)) {
                    return false;
                }
            }
            return true;
        } catch (Throwable exception) {
            return false;
        }
    }

    /**
     * Compares two numbers to see if they are similar.
     *
     * If either of the numbers are Double or Float instances, then they are checked to have
     * a finite value. If either value is not finite (NaN or &#177;infinity), then this
     * function will always return false. If both numbers are finite, they are first checked
     * to be the same type and implement {@link Comparable}. If they do, then the actual
     * {@link Comparable#compareTo(Object)} is called. If they are not the same type, or don't
     * implement Comparable, then they are converted to {@link BigDecimal}s. Finally the
     * BigDecimal values are compared using {@link BigDecimal#compareTo(BigDecimal)}.
     *
     * @param l the Left value to compare. Can not be <code>null</code>.
     * @param r the right value to compare. Can not be <code>null</code>.
     * @return true if the numbers are similar, false otherwise.
     */
    static boolean isNumberSimilar(Number l, Number r) {
        if (!numberIsFinite(l) || !numberIsFinite(r)) {
            // non-finite numbers are never similar
            return false;
        }

        // if the classes are the same and implement Comparable
        // then use the built in compare first.
        if(l.getClass().equals(r.getClass()) && l instanceof Comparable) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            int compareTo = ((Comparable)l).compareTo(r);
            return compareTo==0;
        }

        // BigDecimal should be able to handle all of our number types that we support through
        // documentation. Convert to BigDecimal first, then use the Compare method to
        // decide equality.
        final BigDecimal lBigDecimal = objectToBigDecimal(l, null, false);
        final BigDecimal rBigDecimal = objectToBigDecimal(r, null, false);
        if (lBigDecimal == null || rBigDecimal == null) {
            return false;
        }
        return lBigDecimal.compareTo(rBigDecimal) == 0;
    }

    private static boolean numberIsFinite(Number n) {
        if (n instanceof Double && (((Double) n).isInfinite() || ((Double) n).isNaN())) {
            return false;
        } else if (n instanceof Float && (((Float) n).isInfinite() || ((Float) n).isNaN())) {
            return false;
        }
        return true;
    }

    /**
     * Tests if the value should be tried as a decimal. It makes no test if there are actual digits.
     *
     * @param val value to test
     * @return true if the string is "-0" or if it contains '.', 'e', or 'E', false otherwise.
     */
    protected static boolean isDecimalNotation(final String val) {
        return val.indexOf('.') > -1 || val.indexOf('e') > -1
                || val.indexOf('E') > -1 || "-0".equals(val);
    }

    /**
     * Try to convert a string into a number, boolean, or null. If the string
     * can't be converted, return the string.
     *
     * @param string
     *            A String. can not be null.
     * @return A simple JSON value.
     * @throws NullPointerException
     *             Thrown if the string is null.
     */
    // Changes to this method must be copied to the corresponding method in
    // the XML class to keep full support for Android
    public static Object stringToValue(String string) {
        if ("".equals(string)) {
            return string;
        }

        // check JSON key words true/false/null
        if ("true".equalsIgnoreCase(string)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(string)) {
            return Boolean.FALSE;
        }
        if ("null".equalsIgnoreCase(string)) {
            return JSONObject.NULL;
        }

        /*
         * If it might be a number, try converting it. If a number cannot be
         * produced, then the value will just be a string.
         */

        char initial = string.charAt(0);
        if ((initial >= '0' && initial <= '9') || initial == '-') {
            try {
                return stringToNumber(string);
            } catch (Exception ignore) {
            }
        }
        return string;
    }

    /**
     * Converts a string to a number using the narrowest possible type. Possible
     * returns for this function are BigDecimal, Double, BigInteger, Long, and Integer.
     * When a Double is returned, it should always be a valid Double and not NaN or +-infinity.
     *
     * @param val value to convert
     * @return Number representation of the value.
     * @throws NumberFormatException thrown if the value is not a valid number. A public
     *      caller should catch this and wrap it in a {@link JSONException} if applicable.
     */
    protected static Number stringToNumber(final String val) throws NumberFormatException {
        char initial = val.charAt(0);
        if ((initial >= '0' && initial <= '9') || initial == '-') {
            // decimal representation
            if (isDecimalNotation(val)) {
                // Use a BigDecimal all the time so we keep the original
                // representation. BigDecimal doesn't support -0.0, ensure we
                // keep that by forcing a decimal.
                try {
                    BigDecimal bd = new BigDecimal(val);
                    if(initial == '-' && BigDecimal.ZERO.compareTo(bd)==0) {
                        return Double.valueOf(-0.0);
                    }
                    return bd;
                } catch (NumberFormatException retryAsDouble) {
                    // this is to support "Hex Floats" like this: 0x1.0P-1074
                    try {
                        Double d = Double.valueOf(val);
                        if(d.isNaN() || d.isInfinite()) {
                            throw new NumberFormatException("val ["+val+"] is not a valid number.");
                        }
                        return d;
                    } catch (NumberFormatException ignore) {
                        throw new NumberFormatException("val ["+val+"] is not a valid number.");
                    }
                }
            }
            // block items like 00 01 etc. Java number parsers treat these as Octal.
            if(initial == '0' && val.length() > 1) {
                char at1 = val.charAt(1);
                if(at1 >= '0' && at1 <= '9') {
                    throw new NumberFormatException("val ["+val+"] is not a valid number.");
                }
            } else if (initial == '-' && val.length() > 2) {
                char at1 = val.charAt(1);
                char at2 = val.charAt(2);
                if(at1 == '0' && at2 >= '0' && at2 <= '9') {
                    throw new NumberFormatException("val ["+val+"] is not a valid number.");
                }
            }
            // integer representation.
            // This will narrow any values to the smallest reasonable Object representation
            // (Integer, Long, or BigInteger)

            // BigInteger down conversion: We use a similar bitLength compare as
            // BigInteger#intValueExact uses. Increases GC, but objects hold
            // only what they need. i.e. Less runtime overhead if the value is
            // long lived.
            BigInteger bi = new BigInteger(val);
            if(bi.bitLength() <= 31){
                return Integer.valueOf(bi.intValue());
            }
            if(bi.bitLength() <= 63){
                return Long.valueOf(bi.longValue());
            }
            return bi;
        }
        throw new NumberFormatException("val ["+val+"] is not a valid number.");
    }

    /**
     * Throw an exception if the object is a NaN or infinite number.
     *
     * @param o
     *            The object to test.
     * @throws JSONException
     *             If o is a non-finite number.
     */
    public static void testValidity(Object o) throws JSONException {
        if (o instanceof Number && !numberIsFinite((Number) o)) {
            throw new JSONException("JSON does not allow non-finite numbers.");
        }
    }

    /**
     * Produce a JSONArray containing the values of the members of this
     * JSONObject.
     *
     * @param names
     *            A JSONArray containing a list of key strings. This determines
     *            the sequence of the values in the result.
     * @return A JSONArray of values.
     * @throws JSONException
     *             If any of the values are non-finite numbers.
     */
    public JSONArray toJSONArray(JSONArray names) throws JSONException {
        if (names == null || names.isEmpty()) {
            return null;
        }
        JSONArray ja = new JSONArray();
        for (int i = 0; i < names.length(); i += 1) {
            ja.put(this.opt(names.getString(i)));
        }
        return ja;
    }

    /**
     * Make a JSON text of this JSONObject. For compactness, no whitespace is
     * added. If this would not result in a syntactically correct JSON text,
     * then null will be returned instead.
     * <p><b>
     * Warning: This method assumes that the data structure is acyclical.
     * </b>
     *
     * @return a printable, displayable, portable, transmittable representation
     *         of the object, beginning with <code>{</code>&nbsp;<small>(left
     *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
     *         brace)</small>.
     */
    @Override
    public String toString() {
        try {
            return this.toString(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Make a pretty-printed JSON text of this JSONObject.
     *
     * <p>If <pre>{@code indentFactor > 0}</pre> and the {@link JSONObject}
     * has only one key, then the object will be output on a single line:
     * <pre>{@code {"key": 1}}</pre>
     *
     * <p>If an object has 2 or more keys, then it will be output across
     * multiple lines: <pre>{@code {
     *  "key1": 1,
     *  "key2": "value 2",
     *  "key3": 3
     * }}</pre>
     * <p><b>
     * Warning: This method assumes that the data structure is acyclical.
     * </b>
     *
     * @param indentFactor
     *            The number of spaces to add to each level of indentation.
     * @return a printable, displayable, portable, transmittable representation
     *         of the object, beginning with <code>{</code>&nbsp;<small>(left
     *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
     *         brace)</small>.
     * @throws JSONException
     *             If the object contains an invalid number.
     */
    @SuppressWarnings("resource")
    public String toString(int indentFactor) throws JSONException {
        // 6 characters are the minimum to serialise a key value pair e.g.: "k":1,
        // and we don't want to oversize the initial capacity
        int initialSize = map.size() * 6;
        Writer w = new StringBuilderWriter(Math.max(initialSize, 16));
        return this.write(w, indentFactor, 0).toString();
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
    	// moves the implementation to JSONWriter as:
    	// 1. It makes more sense to be part of the writer class
    	// 2. For Android support this method is not available. By implementing it in the Writer
    	//    Android users can use the writer with the built in Android JSONObject implementation.
        return JSONWriter.valueToString(value);
    }

    /**
     * Wrap an object, if necessary. If the object is <code>null</code>, return the NULL
     * object. If it is an array or collection, wrap it in a JSONArray. If it is
     * a map, wrap it in a JSONObject. If it is a standard property (Double,
     * String, et al) then it is already wrapped. Otherwise, if it comes from
     * one of the java packages, turn it into a string. And if it doesn't, try
     * to wrap it in a JSONObject. If the wrapping fails, then null is returned.
     *
     * @param object
     *            The object to wrap
     * @return The wrapped value
     */
    public static Object wrap(Object object) {
        return wrap(object, null);
    }

    /**
     * Wrap an object, if necessary. If the object is <code>null</code>, return the NULL
     * object. If it is an array or collection, wrap it in a JSONArray. If it is
     * a map, wrap it in a JSONObject. If it is a standard property (Double,
     * String, et al) then it is already wrapped. Otherwise, if it comes from
     * one of the java packages, turn it into a string. And if it doesn't, try
     * to wrap it in a JSONObject. If the wrapping fails, then null is returned.
     *
     * @param object
     *            The object to wrap
     * @param recursionDepth
     *            Variable for tracking the count of nested object creations.
     * @param jsonParserConfiguration
     *            Variable to pass parser custom configuration for json parsing.
     * @return The wrapped value
     */
    static Object wrap(Object object, int recursionDepth, JSONParserConfiguration jsonParserConfiguration) {
      return wrap(object, null, recursionDepth, jsonParserConfiguration);
    }

    private static Object wrap(Object object, Set<Object> objectsRecord) {
      return wrap(object, objectsRecord, 0, new JSONParserConfiguration());
    }

    private static Object wrap(Object object, Set<Object> objectsRecord, int recursionDepth, JSONParserConfiguration jsonParserConfiguration) {
        try {
            if (NULL.equals(object)) {
                return NULL;
            }
            if (object instanceof JSONObject || object instanceof JSONArray
                    || NULL.equals(object) || object instanceof JSONString
                    || object instanceof Byte || object instanceof Character
                    || object instanceof Short || object instanceof Integer
                    || object instanceof Long || object instanceof Boolean
                    || object instanceof Float || object instanceof Double
                    || object instanceof String || object instanceof BigInteger
                    || object instanceof BigDecimal || object instanceof Enum) {
                return object;
            }

            if (object instanceof Collection) {
                Collection<?> coll = (Collection<?>) object;
                return new JSONArray(coll, recursionDepth, jsonParserConfiguration);
            }
            if (object.getClass().isArray()) {
                return new JSONArray(object);
            }
            if (object instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) object;
                return new JSONObject(map, recursionDepth, jsonParserConfiguration);
            }
            Package objectPackage = object.getClass().getPackage();
            String objectPackageName = objectPackage != null ? objectPackage
                    .getName() : "";
            if (objectPackageName.startsWith("java.")
                    || objectPackageName.startsWith("javax.")
                    || object.getClass().getClassLoader() == null) {
                return object.toString();
            }
            if (objectsRecord != null) {
                return new JSONObject(object, objectsRecord);
            }
            return new JSONObject(object);
        }
        catch (JSONException exception) {
            throw exception;
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * Write the contents of the JSONObject as JSON text to a writer. For
     * compactness, no whitespace is added.
     * <p><b>
     * Warning: This method assumes that the data structure is acyclical.
     * </b>
     * @param writer the writer object
     * @return The writer.
     * @throws JSONException if a called function has an error
     */
    public Writer write(Writer writer) throws JSONException {
        return this.write(writer, 0, 0);
    }

    @SuppressWarnings("resource")
    static final Writer writeValue(Writer writer, Object value,
            int indentFactor, int indent) throws JSONException, IOException {
        if (value == null || value.equals(null)) {
            writer.write("null");
        } else if (value instanceof JSONString) {
            // JSONString must be checked first, so it can overwrite behaviour of other types below
            Object o;
            try {
                o = ((JSONString) value).toJSONString();
            } catch (Exception e) {
                throw new JSONException(e);
            }
            writer.write(o != null ? o.toString() : quote(value.toString()));
        } else if (value instanceof String) {
            // assuming most values are Strings, so testing it early
            quote(value.toString(), writer);
            return writer;
        } else if (value instanceof Number) {
            // not all Numbers may match actual JSON Numbers. i.e. fractions or Imaginary
            final String numberAsString = numberToString((Number) value);
            if(NUMBER_PATTERN.matcher(numberAsString).matches()) {
                writer.write(numberAsString);
            } else {
                // The Number value is not a valid JSON number.
                // Instead we will quote it as a string
                quote(numberAsString, writer);
            }
        } else if (value instanceof Boolean) {
            writer.write(value.toString());
        } else if (value instanceof Enum<?>) {
            writer.write(quote(((Enum<?>)value).name()));
        } else if (value instanceof JSONObject) {
            ((JSONObject) value).write(writer, indentFactor, indent);
        } else if (value instanceof JSONArray) {
            ((JSONArray) value).write(writer, indentFactor, indent);
        } else if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            new JSONObject(map).write(writer, indentFactor, indent);
        } else if (value instanceof Collection) {
            Collection<?> coll = (Collection<?>) value;
            new JSONArray(coll).write(writer, indentFactor, indent);
        } else if (value.getClass().isArray()) {
            new JSONArray(value).write(writer, indentFactor, indent);
        } else {
            quote(value.toString(), writer);
        }
        return writer;
    }

    static final void indent(Writer writer, int indent) throws IOException {
        for (int i = 0; i < indent; i += 1) {
            writer.write(' ');
        }
    }

    /**
     * Write the contents of the JSONObject as JSON text to a writer.
     *
     * <p>If <pre>{@code indentFactor > 0}</pre> and the {@link JSONObject}
     * has only one key, then the object will be output on a single line:
     * <pre>{@code {"key": 1}}</pre>
     *
     * <p>If an object has 2 or more keys, then it will be output across
     * multiple lines: <pre>{@code {
     *  "key1": 1,
     *  "key2": "value 2",
     *  "key3": 3
     * }}</pre>
     * <p><b>
     * Warning: This method assumes that the data structure is acyclical.
     * </b>
     *
     * @param writer
     *            Writes the serialized JSON
     * @param indentFactor
     *            The number of spaces to add to each level of indentation.
     * @param indent
     *            The indentation of the top level.
     * @return The writer.
     * @throws JSONException if a called function has an error or a write error
     * occurs
     */
    @SuppressWarnings("resource")
    public Writer write(Writer writer, int indentFactor, int indent)
            throws JSONException {
        try {
            boolean needsComma = false;
            final int length = this.length();
            writer.write('{');

            if (length == 1) {
            	final Entry<String,?> entry = this.entrySet().iterator().next();
                final String key = entry.getKey();
                writer.write(quote(key));
                writer.write(':');
                if (indentFactor > 0) {
                    writer.write(' ');
                }
                try{
                    writeValue(writer, entry.getValue(), indentFactor, indent);
                } catch (Exception e) {
                    throw new JSONException("Unable to write JSONObject value for key: " + key, e);
                }
            } else if (length != 0) {
                final int newIndent = indent + indentFactor;
                for (final Entry<String,?> entry : this.entrySet()) {
                    if (needsComma) {
                        writer.write(',');
                    }
                    if (indentFactor > 0) {
                        writer.write('\n');
                    }
                    indent(writer, newIndent);
                    final String key = entry.getKey();
                    writer.write(quote(key));
                    writer.write(':');
                    if (indentFactor > 0) {
                        writer.write(' ');
                    }
                    try {
                        writeValue(writer, entry.getValue(), indentFactor, newIndent);
                    } catch (Exception e) {
                        throw new JSONException("Unable to write JSONObject value for key: " + key, e);
                    }
                    needsComma = true;
                }
                if (indentFactor > 0) {
                    writer.write('\n');
                }
                indent(writer, indent);
            }
            writer.write('}');
            return writer;
        } catch (IOException exception) {
            throw new JSONException(exception);
        }
    }

    /**
     * Returns a java.util.Map containing all of the entries in this object.
     * If an entry in the object is a JSONArray or JSONObject it will also
     * be converted.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a java.util.Map containing the entries of this object
     */
    public Map<String, Object> toMap() {
        Map<String, Object> results = new HashMap<String, Object>();
        for (Entry<String, Object> entry : this.entrySet()) {
            Object value;
            if (entry.getValue() == null || NULL.equals(entry.getValue())) {
                value = null;
            } else if (entry.getValue() instanceof JSONObject) {
                value = ((JSONObject) entry.getValue()).toMap();
            } else if (entry.getValue() instanceof JSONArray) {
                value = ((JSONArray) entry.getValue()).toList();
            } else {
                value = entry.getValue();
            }
            results.put(entry.getKey(), value);
        }
        return results;
    }

    /**
     * Create a new JSONException in a common format for incorrect conversions.
     * @param key name of the key
     * @param valueType the type of value being coerced to
     * @param cause optional cause of the coercion failure
     * @return JSONException that can be thrown.
     */
    private static JSONException wrongValueFormatException(
            String key,
            String valueType,
            Object value,
            Throwable cause) {
        if(value == null) {

            return new JSONException(
                    "JSONObject[" + quote(key) + "] is not a " + valueType + " (null)."
                    , cause);
        }
        // don't try to toString collections or known object types that could be large.
        if(value instanceof Map || value instanceof Iterable || value instanceof JSONObject) {
            return new JSONException(
                    "JSONObject[" + quote(key) + "] is not a " + valueType + " (" + value.getClass() + ")."
                    , cause);
        }
        return new JSONException(
                "JSONObject[" + quote(key) + "] is not a " + valueType + " (" + value.getClass() + " : " + value + ")."
                , cause);
    }

    /**
     * Create a new JSONException in a common format for recursive object definition.
     * @param key name of the key
     * @return JSONException that can be thrown.
     */
    private static JSONException recursivelyDefinedObjectException(String key) {
        return new JSONException(
            "JavaBean object contains recursively defined member variable of key " + quote(key)
        );
    }

    /**
     * For a prospective number, remove the leading zeros
     * @param value prospective number
     * @return number without leading zeros
     */
    private static String removeLeadingZerosOfNumber(String value){
        if ("-".equals(value)){return value;}
        boolean negativeFirstChar = (value.charAt(0) == '-');
        int counter = negativeFirstChar ? 1:0;
        while (counter < value.length()){
            if (value.charAt(counter) != '0'){
                if (negativeFirstChar) {return "-".concat(value.substring(counter));}
                return value.substring(counter);
            }
            ++counter;
        }
        if (negativeFirstChar) {return "-0";}
        return "0";
    }
}
