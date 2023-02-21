package org.json;
/*
Public Domain.
*/

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Configuration object for the XML parser. The configuration is immutable.
 * @author AylwardJ
 */
@SuppressWarnings({""})
public class XMLParserConfiguration extends ParserConfiguration {

    /**
     * The default maximum nesting depth when parsing a XML document to JSON.
     */
//    public static final int DEFAULT_MAXIMUM_NESTING_DEPTH = 512; // We could override

    /** Original Configuration of the XML Parser. */
    public static final XMLParserConfiguration ORIGINAL
        = new XMLParserConfiguration();
    /** Original configuration of the XML Parser except that values are kept as strings. */
    public static final XMLParserConfiguration KEEP_STRINGS
        = new XMLParserConfiguration().withKeepStrings(true);

    /**
     * The name of the key in a JSON Object that indicates a CDATA section. Historically this has
     * been the value "content" but can be changed. Use <code>null</code> to indicate no CDATA
     * processing.
     */
    private String cDataTagName;

    /**
     * When parsing the XML into JSON, specifies if values with attribute xsi:nil="true"
     * should be kept as attribute(<code>false</code>), or they should be converted to
     * <code>null</code>(<code>true</code>)
     */
    private boolean convertNilAttributeToNull;

    /**
     * This will allow type conversion for values in XML if xsi:type attribute is defined
     */
    private Map<String, XMLXsiTypeConverter<?>> xsiTypeMap;

    /**
     * When parsing the XML into JSON, specifies the tags whose values should be converted
     * to arrays
     */
    private Set<String> forceList;

    /**
     * Default parser configuration. Does not keep strings (tries to implicitly convert
     * values), and the CDATA Tag Name is "content".
     */
    public XMLParserConfiguration () {
        super();
        this.cDataTagName = "content";
        this.convertNilAttributeToNull = false;
        this.xsiTypeMap = Collections.emptyMap();
        this.forceList = Collections.emptySet();
    }

    /**
     * Configure the parser string processing and use the default CDATA Tag Name as "content".
     * @param keepStrings <code>true</code> to parse all values as string.
     *      <code>false</code> to try and convert XML string values into a JSON value.
     * @deprecated This constructor has been deprecated in favor of using the new builder
     *      pattern for the configuration.
     *      This constructor may be removed in a future release.
     */
    @Deprecated
    public XMLParserConfiguration (final boolean keepStrings) {
        this(keepStrings, "content", false);
    }

    /**
     * Configure the parser string processing to try and convert XML values to JSON values and
     * use the passed CDATA Tag Name the processing value. Pass <code>null</code> to
     * disable CDATA processing
     * @param cDataTagName <code>null</code> to disable CDATA processing. Any other value
     *      to use that value as the JSONObject key name to process as CDATA.
     * @deprecated This constructor has been deprecated in favor of using the new builder
     *      pattern for the configuration.
     *      This constructor may be removed in a future release.
     */
    @Deprecated
    public XMLParserConfiguration (final String cDataTagName) {
        this(false, cDataTagName, false);
    }

    /**
     * Configure the parser to use custom settings.
     * @param keepStrings <code>true</code> to parse all values as string.
     *      <code>false</code> to try and convert XML string values into a JSON value.
     * @param cDataTagName <code>null</code> to disable CDATA processing. Any other value
     *      to use that value as the JSONObject key name to process as CDATA.
     * @deprecated This constructor has been deprecated in favor of using the new builder
     *      pattern for the configuration.
     *      This constructor may be removed in a future release.
     */
    @Deprecated
    public XMLParserConfiguration (final boolean keepStrings, final String cDataTagName) {
        super(keepStrings, DEFAULT_MAXIMUM_NESTING_DEPTH);
        this.cDataTagName = cDataTagName;
        this.convertNilAttributeToNull = false;
    }

    /**
     * Configure the parser to use custom settings.
     * @param keepStrings <code>true</code> to parse all values as string.
     *      <code>false</code> to try and convert XML string values into a JSON value.
     * @param cDataTagName <code>null</code> to disable CDATA processing. Any other value
     *      to use that value as the JSONObject key name to process as CDATA.
     * @param convertNilAttributeToNull <code>true</code> to parse values with attribute xsi:nil="true" as null.
     *                                  <code>false</code> to parse values with attribute xsi:nil="true" as {"xsi:nil":true}.
     * @deprecated This constructor has been deprecated in favor of using the new builder
     *      pattern for the configuration.
     *      This constructor may be removed or marked private in a future release.
     */
    @Deprecated
    public XMLParserConfiguration (final boolean keepStrings, final String cDataTagName, final boolean convertNilAttributeToNull) {
        super(keepStrings, DEFAULT_MAXIMUM_NESTING_DEPTH);
        this.cDataTagName = cDataTagName;
        this.convertNilAttributeToNull = convertNilAttributeToNull;
    }

    /**
     * Configure the parser to use custom settings.
     * @param keepStrings <code>true</code> to parse all values as string.
     *      <code>false</code> to try and convert XML string values into a JSON value.
     * @param cDataTagName <code>null</code> to disable CDATA processing. Any other value
     *      to use that value as the JSONObject key name to process as CDATA.
     * @param convertNilAttributeToNull <code>true</code> to parse values with attribute xsi:nil="true" as null.
     *                                  <code>false</code> to parse values with attribute xsi:nil="true" as {"xsi:nil":true}.
     * @param xsiTypeMap  <code>new HashMap<String, XMLXsiTypeConverter<?>>()</code> to parse values with attribute
     *                   xsi:type="integer" as integer,  xsi:type="string" as string
     * @param forceList  <code>new HashSet<String>()</code> to parse the provided tags' values as arrays
     * @param maxNestingDepth <code>int</code> to limit the nesting depth
     */
    private XMLParserConfiguration (final boolean keepStrings, final String cDataTagName,
            final boolean convertNilAttributeToNull, final Map<String, XMLXsiTypeConverter<?>> xsiTypeMap, final Set<String> forceList,
            final int maxNestingDepth) {
        super(keepStrings, maxNestingDepth);
        this.cDataTagName = cDataTagName;
        this.convertNilAttributeToNull = convertNilAttributeToNull;
        this.xsiTypeMap = Collections.unmodifiableMap(xsiTypeMap);
        this.forceList = Collections.unmodifiableSet(forceList);
    }

    /**
     * Provides a new instance of the same configuration.
     */
    @Override
    protected XMLParserConfiguration clone() {
        // future modifications to this method should always ensure a "deep"
        // clone in the case of collections. i.e. if a Map is added as a configuration
        // item, a new map instance should be created and if possible each value in the
        // map should be cloned as well. If the values of the map are known to also
        // be immutable, then a shallow clone of the map is acceptable.
        return new XMLParserConfiguration(
                this.keepStrings,
                this.cDataTagName,
                this.convertNilAttributeToNull,
                this.xsiTypeMap,
                this.forceList,
                this.maxNestingDepth
        );
    }

    /**
     * When parsing the XML into JSON, specifies if values should be kept as strings (<code>true</code>), or if
     * they should try to be guessed into JSON values (numeric, boolean, string)
     *
     * @param newVal
     *      new value to use for the <code>keepStrings</code> configuration option.
     *
     * @return The existing configuration will not be modified. A new configuration is returned.
     */
    @Override
    public XMLParserConfiguration withKeepStrings(final boolean newVal) {
        return super.withKeepStrings(newVal);
    }

    /**
     * The name of the key in a JSON Object that indicates a CDATA section. Historically this has
     * been the value "content" but can be changed. Use <code>null</code> to indicate no CDATA
     * processing.
     *
     * @return The <code>cDataTagName</code> configuration value.
     */
    public String getcDataTagName() {
        return this.cDataTagName;
    }

    /**
     * The name of the key in a JSON Object that indicates a CDATA section. Historically this has
     * been the value "content" but can be changed. Use <code>null</code> to indicate no CDATA
     * processing.
     *
     * @param newVal
     *      new value to use for the <code>cDataTagName</code> configuration option.
     *
     * @return The existing configuration will not be modified. A new configuration is returned.
     */
    public XMLParserConfiguration withcDataTagName(final String newVal) {
        XMLParserConfiguration newConfig = this.clone();
        newConfig.cDataTagName = newVal;
        return newConfig;
    }

    /**
     * When parsing the XML into JSON, specifies if values with attribute xsi:nil="true"
     * should be kept as attribute(<code>false</code>), or they should be converted to
     * <code>null</code>(<code>true</code>)
     *
     * @return The <code>convertNilAttributeToNull</code> configuration value.
     */
    public boolean isConvertNilAttributeToNull() {
        return this.convertNilAttributeToNull;
    }

    /**
     * When parsing the XML into JSON, specifies if values with attribute xsi:nil="true"
     * should be kept as attribute(<code>false</code>), or they should be converted to
     * <code>null</code>(<code>true</code>)
     *
     * @param newVal
     *      new value to use for the <code>convertNilAttributeToNull</code> configuration option.
     *
     * @return The existing configuration will not be modified. A new configuration is returned.
     */
    public XMLParserConfiguration withConvertNilAttributeToNull(final boolean newVal) {
        XMLParserConfiguration newConfig = this.clone();
        newConfig.convertNilAttributeToNull = newVal;
        return newConfig;
    }

    /**
     * When parsing the XML into JSON, specifies that the values with attribute xsi:type
     * will be converted to target type defined to client in this configuration
     * {@code Map<String, XMLXsiTypeConverter<?>>} to parse values with attribute
     * xsi:type="integer" as integer,  xsi:type="string" as string
     * @return <code>xsiTypeMap</code> unmodifiable configuration map.
     */
    public Map<String, XMLXsiTypeConverter<?>> getXsiTypeMap() {
        return this.xsiTypeMap;
    }

    /**
     * When parsing the XML into JSON, specifies that the values with attribute xsi:type
     * will be converted to target type defined to client in this configuration
     * {@code Map<String, XMLXsiTypeConverter<?>>} to parse values with attribute
     * xsi:type="integer" as integer,  xsi:type="string" as string
     * @param xsiTypeMap  {@code new HashMap<String, XMLXsiTypeConverter<?>>()} to parse values with attribute
     *                   xsi:type="integer" as integer,  xsi:type="string" as string
     * @return The existing configuration will not be modified. A new configuration is returned.
     */
    public XMLParserConfiguration withXsiTypeMap(final Map<String, XMLXsiTypeConverter<?>> xsiTypeMap) {
        XMLParserConfiguration newConfig = this.clone();
        Map<String, XMLXsiTypeConverter<?>> cloneXsiTypeMap = new HashMap<String, XMLXsiTypeConverter<?>>(xsiTypeMap);
        newConfig.xsiTypeMap = Collections.unmodifiableMap(cloneXsiTypeMap);
        return newConfig;
    }

    /**
     * When parsing the XML into JSON, specifies that tags that will be converted to arrays
     * in this configuration {@code Set<String>} to parse the provided tags' values as arrays
     * @return <code>forceList</code> unmodifiable configuration set.
     */
    public Set<String> getForceList() {
        return this.forceList;
    }

    /**
     * When parsing the XML into JSON, specifies that tags that will be converted to arrays
     * in this configuration {@code Set<String>} to parse the provided tags' values as arrays
     * @param forceList  {@code new HashSet<String>()} to parse the provided tags' values as arrays
     * @return The existing configuration will not be modified. A new configuration is returned.
     */
    public XMLParserConfiguration withForceList(final Set<String> forceList) {
        XMLParserConfiguration newConfig = this.clone();
        Set<String> cloneForceList = new HashSet<String>(forceList);
        newConfig.forceList = Collections.unmodifiableSet(cloneForceList);
        return newConfig;
    }

    /**
     * Defines the maximum nesting depth that the parser will descend before throwing an exception
     * when parsing the XML into JSON. The default max nesting depth is 512, which means the parser
     * will throw a JsonException if the maximum depth is reached.
     * Using any negative value as a parameter is equivalent to setting no limit to the nesting depth,
     * which means the parses will go as deep as the maximum call stack size allows.
     * @param maxNestingDepth the maximum nesting depth allowed to the XML parser
     * @return The existing configuration will not be modified. A new configuration is returned.
     */
    @Override
    public XMLParserConfiguration withMaxNestingDepth(int maxNestingDepth) {
        return super.withMaxNestingDepth(maxNestingDepth);
    }
}
