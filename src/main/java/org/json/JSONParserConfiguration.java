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
        return new JSONParserConfiguration();
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONParserConfiguration withMaxNestingDepth(final int maxNestingDepth) {
        return super.withMaxNestingDepth(maxNestingDepth);
    }

    public JSONParserConfiguration withOverwriteDuplicateKey(final boolean overwriteDuplicateKey) {
        JSONParserConfiguration newConfig = this.clone();
        newConfig.overwriteDuplicateKey = overwriteDuplicateKey;

        return newConfig;
    }

    public boolean isOverwriteDuplicateKey() {
        return this.overwriteDuplicateKey;
    }
}
