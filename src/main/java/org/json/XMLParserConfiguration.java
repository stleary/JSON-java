package org.json;
/*
Copyright (c) 2002 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Configuration object for the XML parser. The configuration is immutable.
 * @author AylwardJ
 */
@SuppressWarnings({""})
public class XMLParserConfiguration {
    /** Original Configuration of the XML Parser. */
    public static final XMLParserConfiguration ORIGINAL
        = new XMLParserConfiguration();
    /** Original configuration of the XML Parser except that values are kept as strings. */
    public static final XMLParserConfiguration KEEP_STRINGS
        = new XMLParserConfiguration().withKeepStrings(true);

    /**
     * When parsing the XML into JSON, specifies if values should be kept as strings (<code>true</code>), or if
     * they should try to be guessed into JSON values (numeric, boolean, string)
     */
    private boolean keepStrings;
    
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
     * Default parser configuration. Does not keep strings (tries to implicitly convert
     * values), and the CDATA Tag Name is "content".
     */
    public XMLParserConfiguration () {
        this.keepStrings = false;
        this.cDataTagName = "content";
        this.convertNilAttributeToNull = false;
        this.xsiTypeMap = Collections.emptyMap();
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
     * @param cDataTagName<code>null</code> to disable CDATA processing. Any other value
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
     * @param cDataTagName<code>null</code> to disable CDATA processing. Any other value
     *      to use that value as the JSONObject key name to process as CDATA.
     * @deprecated This constructor has been deprecated in favor of using the new builder
     *      pattern for the configuration.
     *      This constructor may be removed in a future release.
     */
    @Deprecated
    public XMLParserConfiguration (final boolean keepStrings, final String cDataTagName) {
        this.keepStrings = keepStrings;
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
        this.keepStrings = keepStrings;
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
     */
    private XMLParserConfiguration (final boolean keepStrings, final String cDataTagName,
            final boolean convertNilAttributeToNull, final Map<String, XMLXsiTypeConverter<?>> xsiTypeMap ) {
        this.keepStrings = keepStrings;
        this.cDataTagName = cDataTagName;
        this.convertNilAttributeToNull = convertNilAttributeToNull;
        this.xsiTypeMap = Collections.unmodifiableMap(xsiTypeMap);
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
                this.xsiTypeMap
        );
    }
    
    /**
     * When parsing the XML into JSON, specifies if values should be kept as strings (<code>true</code>), or if
     * they should try to be guessed into JSON values (numeric, boolean, string)
     * 
     * @return The {@link #keepStrings} configuration value.
     */
    public boolean isKeepStrings() {
        return this.keepStrings;
    }

    /**
     * When parsing the XML into JSON, specifies if values should be kept as strings (<code>true</code>), or if
     * they should try to be guessed into JSON values (numeric, boolean, string)
     * 
     * @param newVal
     *      new value to use for the {@link #keepStrings} configuration option.
     * 
     * @return The existing configuration will not be modified. A new configuration is returned.
     */
    public XMLParserConfiguration withKeepStrings(final boolean newVal) {
        XMLParserConfiguration newConfig = this.clone();
        newConfig.keepStrings = newVal;
        return newConfig;
    }

    /**
     * The name of the key in a JSON Object that indicates a CDATA section. Historically this has
     * been the value "content" but can be changed. Use <code>null</code> to indicate no CDATA
     * processing.
     * 
     * @return The {@link #cDataTagName} configuration value.
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
     *      new value to use for the {@link #cDataTagName} configuration option.
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
     * @return The {@link #convertNilAttributeToNull} configuration value.
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
     *      new value to use for the {@link #convertNilAttributeToNull} configuration option.
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
     * @return {@link #xsiTypeMap} unmodifiable configuration map.
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
}
