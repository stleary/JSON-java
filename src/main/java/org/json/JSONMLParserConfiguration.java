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
     * We can override the default maximum nesting depth if needed.
     */
    public static final int DEFAULT_MAXIMUM_NESTING_DEPTH = ParserConfiguration.DEFAULT_MAXIMUM_NESTING_DEPTH;

    /** Original Configuration of the XML to JSONML Parser. */
    public static final JSONMLParserConfiguration ORIGINAL
        = new JSONMLParserConfiguration();
    /** Original configuration of the XML to JSONML Parser except that values are kept as strings. */
    public static final JSONMLParserConfiguration KEEP_STRINGS
        = new JSONMLParserConfiguration().withKeepStrings(true);

    /**
     * Default parser configuration. Does not keep strings (tries to implicitly convert values).
     */
    public JSONMLParserConfiguration() {
        super();
        this.maxNestingDepth = DEFAULT_MAXIMUM_NESTING_DEPTH;
    }

    /**
     * Configure the parser string processing and use the default CDATA Tag Name as "content".
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
        // future modifications to this method should always ensure a "deep"
        // clone in the case of collections. i.e. if a Map is added as a configuration
        // item, a new map instance should be created and if possible each value in the
        // map should be cloned as well. If the values of the map are known to also
        // be immutable, then a shallow clone of the map is acceptable.
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
