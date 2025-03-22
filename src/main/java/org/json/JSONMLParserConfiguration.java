package org.json;

/*
Public Domain.
*/

/**
 * Configuration object for the XML to JSONML parser. The configuration is immutable.
 */
@SuppressWarnings({""})
public class JSONMLParserConfiguration extends ParserConfiguration {

    /**
     * Default maximum nesting depth for the XML to JSONML parser.
     */
    private static final int DEFAULT_MAXIMUM_NESTING_DEPTH = ParserConfiguration.DEFAULT_MAXIMUM_NESTING_DEPTH;

    /** Original Configuration of the XML to JSONML Parser. */
    private static final JSONMLParserConfiguration ORIGINAL
            = new JSONMLParserConfiguration();
    /** Original configuration of the XML to JSONML Parser except that values are kept as strings. */
    private static final JSONMLParserConfiguration KEEP_STRINGS
            = new JSONMLParserConfiguration().withKeepStrings(true);

    /**
     * Returns the default maximum nesting depth for the XML to JSONML parser.
     *
     * @return The default maximum nesting depth.
     */
    public static int getDefaultMaximumNestingDepth() {
        return DEFAULT_MAXIMUM_NESTING_DEPTH;
    }

    /**
     * Returns the original configuration of the XML to JSONML parser.
     *
     * @return The original configuration.
     */
    public static JSONMLParserConfiguration getOriginalConfiguration() {
        return ORIGINAL;
    }

    /**
     * Returns the configuration of the XML to JSONML parser that keeps values as strings.
     *
     * @return The configuration that keeps values as strings.
     */
    public static JSONMLParserConfiguration getKeepStringsConfiguration() {
        return KEEP_STRINGS;
    }

    /**
     * Default parser configuration. Does not keep strings (tries to implicitly convert values).
     */
    public JSONMLParserConfiguration() {
        super();
        this.maxNestingDepth = DEFAULT_MAXIMUM_NESTING_DEPTH;
    }

    /**
     * Configure the parser string processing and use the default CDATA Tag Name as "content".
     *
     * @param keepStrings <code>true</code> to parse all values as string.
     *      <code>false</code> to try and convert XML string values into a JSON value.
     * @param maxNestingDepth <code>int</code> to limit the nesting depth
     */
    protected JSONMLParserConfiguration(final boolean keepStrings, final int maxNestingDepth) {
        super(keepStrings, maxNestingDepth);
    }

    /**
     * Provides a new instance of the same configuration.
     */
    @Override
    protected JSONMLParserConfiguration clone() {
        return new JSONMLParserConfiguration(
                this.keepStrings,
                this.maxNestingDepth
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONMLParserConfiguration withKeepStrings(final boolean newVal) {
        return super.withKeepStrings(newVal);
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONMLParserConfiguration withMaxNestingDepth(int maxNestingDepth) {
        return super.withMaxNestingDepth(maxNestingDepth);
    }
}