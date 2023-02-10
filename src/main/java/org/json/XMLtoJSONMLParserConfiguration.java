package org.json;
/*
Public Domain.
*/

/**
 * Configuration object for the XML to JSONML parser. The configuration is immutable.
 */
@SuppressWarnings({""})
public class XMLtoJSONMLParserConfiguration {
    /**
     * Used to indicate there's no defined limit to the maximum nesting depth when parsing a XML
     * document to JSONML.
     */
    public static final int UNDEFINED_MAXIMUM_NESTING_DEPTH = -1;

    /**
     * The default maximum nesting depth when parsing a XML document to JSONML.
     */
    public static final int DEFAULT_MAXIMUM_NESTING_DEPTH = 512;

    /** Original Configuration of the XML to JSONML Parser. */
    public static final XMLtoJSONMLParserConfiguration ORIGINAL
        = new XMLtoJSONMLParserConfiguration();
    /** Original configuration of the XML to JSONML Parser except that values are kept as strings. */
    public static final XMLtoJSONMLParserConfiguration KEEP_STRINGS
        = new XMLtoJSONMLParserConfiguration().withKeepStrings(true);

    /**
     * When parsing the XML into JSONML, specifies if values should be kept as strings (<code>true</code>), or if
     * they should try to be guessed into JSON values (numeric, boolean, string)
     */
    private boolean keepStrings;

    /**
     * The maximum nesting depth when parsing a XML document to JSONML.
     */
    private int maxNestingDepth = DEFAULT_MAXIMUM_NESTING_DEPTH;

    /**
     * Default parser configuration. Does not keep strings (tries to implicitly convert values).
     */
    public XMLtoJSONMLParserConfiguration() {
        this.keepStrings = false;
    }

    /**
     * Configure the parser string processing and use the default CDATA Tag Name as "content".
     * @param keepStrings <code>true</code> to parse all values as string.
     *      <code>false</code> to try and convert XML string values into a JSON value.
     * @param maxNestingDepth <code>int</code> to limit the nesting depth
     */
    public XMLtoJSONMLParserConfiguration(final boolean keepStrings, final int maxNestingDepth) {
        this.keepStrings = keepStrings;
        this.maxNestingDepth = maxNestingDepth;
    }

    /**
     * Provides a new instance of the same configuration.
     */
    @Override
    protected XMLtoJSONMLParserConfiguration clone() {
        // future modifications to this method should always ensure a "deep"
        // clone in the case of collections. i.e. if a Map is added as a configuration
        // item, a new map instance should be created and if possible each value in the
        // map should be cloned as well. If the values of the map are known to also
        // be immutable, then a shallow clone of the map is acceptable.
        return new XMLtoJSONMLParserConfiguration(
                this.keepStrings,
                this.maxNestingDepth
        );
    }

    /**
     * When parsing the XML into JSONML, specifies if values should be kept as strings (<code>true</code>), or if
     * they should try to be guessed into JSON values (numeric, boolean, string)
     *
     * @return The <code>keepStrings</code> configuration value.
     */
    public boolean isKeepStrings() {
        return this.keepStrings;
    }

    /**
     * When parsing the XML into JSONML, specifies if values should be kept as strings (<code>true</code>), or if
     * they should try to be guessed into JSON values (numeric, boolean, string)
     *
     * @param newVal
     *      new value to use for the <code>keepStrings</code> configuration option.
     *
     * @return The existing configuration will not be modified. A new configuration is returned.
     */
    public XMLtoJSONMLParserConfiguration withKeepStrings(final boolean newVal) {
        XMLtoJSONMLParserConfiguration newConfig = this.clone();
        newConfig.keepStrings = newVal;
        return newConfig;
    }

    /**
     * The maximum nesting depth that the parser will descend before throwing an exception
     * when parsing the XML into JSONML.
     * @return the maximum nesting depth set for this configuration
     */
    public int getMaxNestingDepth() {
        return maxNestingDepth;
    }

    /**
     * Defines the maximum nesting depth that the parser will descend before throwing an exception
     * when parsing the XML into JSONML. The default max nesting depth is 512, which means the parser
     * will throw a JsonException if the maximum depth is reached.
     * Using any negative value as a parameter is equivalent to setting no limit to the nesting depth,
     * which means the parses will go as deep as the maximum call stack size allows.
     * @param maxNestingDepth the maximum nesting depth allowed to the XML parser
     * @return The existing configuration will not be modified. A new configuration is returned.
     */
    public XMLtoJSONMLParserConfiguration withMaxNestingDepth(int maxNestingDepth) {
        XMLtoJSONMLParserConfiguration newConfig = this.clone();

        if (maxNestingDepth > UNDEFINED_MAXIMUM_NESTING_DEPTH) {
            newConfig.maxNestingDepth = maxNestingDepth;
        } else {
            newConfig.maxNestingDepth = UNDEFINED_MAXIMUM_NESTING_DEPTH;
        }

        return newConfig;
    }
}
