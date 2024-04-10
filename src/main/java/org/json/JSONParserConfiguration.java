package org.json;

/**
 * Configuration object for the JSON parser. The configuration is immutable.
 */
public class JSONParserConfiguration extends ParserConfiguration {

    /** Original Configuration of the JSON Parser. */
    public static final JSONParserConfiguration ORIGINAL = new JSONParserConfiguration();

    /** Original configuration of the JSON Parser except that values are kept as strings. */
    public static final JSONParserConfiguration KEEP_STRINGS = new JSONParserConfiguration().withKeepStrings(true);

    /**
     * Used to indicate whether to overwrite duplicate key or not.
     */
    private boolean overwriteDuplicateKey;

    /**
     * This flag, when set to true, instructs the parser to throw a JSONException if it encounters an invalid character
     * immediately following the final ']' character in the input. This is useful for ensuring strict adherence to the
     * JSON syntax, as any characters after the final closing bracket of a JSON array are considered invalid.
     */
    private boolean strictMode;

    /**
     * Configuration with the default values.
     */
    public JSONParserConfiguration() {
        super();
        this.overwriteDuplicateKey = false;
    }

    @Override
    protected JSONParserConfiguration clone() {
        JSONParserConfiguration clone = new JSONParserConfiguration();
        clone.overwriteDuplicateKey = overwriteDuplicateKey;
        clone.maxNestingDepth = maxNestingDepth;
        return clone;
    }

    /**
     * Defines the maximum nesting depth that the parser will descend before throwing an exception
     * when parsing a map into JSONObject or parsing a {@link java.util.Collection} instance into
     * JSONArray. The default max nesting depth is 512, which means the parser will throw a JsonException
     * if the maximum depth is reached.
     *
     * @param maxNestingDepth the maximum nesting depth allowed to the JSON parser
     * @return The existing configuration will not be modified. A new configuration is returned.
     */
    @SuppressWarnings("unchecked")
    @Override
    public JSONParserConfiguration withMaxNestingDepth(final int maxNestingDepth) {
        JSONParserConfiguration clone = this.clone();
        clone.maxNestingDepth = maxNestingDepth;

        return clone;
    }

    /**
     * Controls the parser's behavior when meeting duplicate keys.
     * If set to false, the parser will throw a JSONException when meeting a duplicate key.
     * Or the duplicate key's value will be overwritten.
     *
     * @param overwriteDuplicateKey defines should the parser overwrite duplicate keys.
     * @return The existing configuration will not be modified. A new configuration is returned.
     */
    public JSONParserConfiguration withOverwriteDuplicateKey(final boolean overwriteDuplicateKey) {
        JSONParserConfiguration clone = this.clone();
        clone.overwriteDuplicateKey = overwriteDuplicateKey;

        return clone;
    }


    /**
     * Sets the strict mode configuration for the JSON parser.
     * <p>
     * When strict mode is enabled, the parser will throw a JSONException if it encounters an invalid character
     * immediately following the final ']' character in the input. This is useful for ensuring strict adherence to the
     * JSON syntax, as any characters after the final closing bracket of a JSON array are considered invalid.
     *
     * @param mode a boolean value indicating whether strict mode should be enabled or not
     * @return a new JSONParserConfiguration instance with the updated strict mode setting
     */
    public JSONParserConfiguration withStrictMode(final boolean mode) {
        JSONParserConfiguration clone = this.clone();
        clone.strictMode = mode;

        return clone;
    }

    /**
     * The parser's behavior when meeting duplicate keys, controls whether the parser should
     * overwrite duplicate keys or not.
     *
     * @return The <code>overwriteDuplicateKey</code> configuration value.
     */
    public boolean isOverwriteDuplicateKey() {
        return this.overwriteDuplicateKey;
    }


    /**
     * Retrieves the current strict mode setting of the JSON parser.
     * <p>
     * Strict mode, when enabled, instructs the parser to throw a JSONException if it encounters an invalid character
     * immediately following the final ']' character in the input. This ensures strict adherence to the JSON syntax, as
     * any characters after the final closing bracket of a JSON array are considered invalid.
     *
     * @return the current strict mode setting. True if strict mode is enabled, false otherwise.
     */
    public boolean isStrictMode() {
        return this.strictMode;
    }
}
