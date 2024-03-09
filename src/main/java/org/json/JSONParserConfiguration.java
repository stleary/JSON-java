package org.json;

/**
 * Configuration object for the JSON parser. The configuration is immutable.
 */
public class JSONParserConfiguration extends ParserConfiguration {
    /**
     * Used to indicate whether to overwrite duplicate key or not.
     */
    private boolean overwriteDuplicateKey;

    /**
     * Configuration with the default values.
     */
    public JSONParserConfiguration() {
        this(false);
    }

    /**
     * Configure the parser with argument overwriteDuplicateKey.
     *
     * @param overwriteDuplicateKey Indicate whether to overwrite duplicate key or not.<br>
     *                              If not, the JSONParser will throw a {@link JSONException}
     *                              when meeting duplicate keys.
     */
    public JSONParserConfiguration(boolean overwriteDuplicateKey) {
        super();
        this.overwriteDuplicateKey = overwriteDuplicateKey;
    }

    @Override
    protected JSONParserConfiguration clone() {
        JSONParserConfiguration clone = new JSONParserConfiguration(overwriteDuplicateKey);
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
     * The parser's behavior when meeting duplicate keys, controls whether the parser should
     * overwrite duplicate keys or not.
     *
     * @return The <code>overwriteDuplicateKey</code> configuration value.
     */
    public boolean isOverwriteDuplicateKey() {
        return this.overwriteDuplicateKey;
    }
}
