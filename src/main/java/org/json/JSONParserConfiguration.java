package org.json;

/**
 * Configuration object for the JSON parser. The configuration is immutable.
 */
public class JSONParserConfiguration extends ParserConfiguration {
    /**
     * The way should be used to handle duplicate keys.
     */
    private JSONDuplicateKeyStrategy duplicateKeyStrategy;

    /**
     * Configuration with the default values.
     */
    public JSONParserConfiguration() {
        this(JSONDuplicateKeyStrategy.THROW_EXCEPTION);
    }

    /**
     * Configure the parser with {@link JSONDuplicateKeyStrategy}.
     *
     * @param duplicateKeyStrategy Indicate which way should be used to handle duplicate keys.
     */
    public JSONParserConfiguration(JSONDuplicateKeyStrategy duplicateKeyStrategy) {
        super();
        this.duplicateKeyStrategy = duplicateKeyStrategy;
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

    public JSONParserConfiguration withDuplicateKeyStrategy(final JSONDuplicateKeyStrategy duplicateKeyStrategy) {
        JSONParserConfiguration newConfig = this.clone();
        newConfig.duplicateKeyStrategy = duplicateKeyStrategy;

        return newConfig;
    }

    public JSONDuplicateKeyStrategy getDuplicateKeyStrategy() {
        return this.duplicateKeyStrategy;
    }
}
