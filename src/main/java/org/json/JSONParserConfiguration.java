package org.json;

/**
 * Configuration class for JSON parsers.
 */
public class JSONParserConfiguration {

    private boolean circularReferenceValidated;

    /**
     * Configuration with the default values.
     */
    public JSONParserConfiguration() {
        circularReferenceValidated = true;
    }

    private JSONParserConfiguration(boolean circularReferenceValidated) {
        this.circularReferenceValidated = circularReferenceValidated;
    }

    /**
     * Retrieves the configuration about the circular dependency check.
     *
     * @return if true enables the circular reference check, false otherwise
     */
    public boolean isCircularReferenceValidated() {
        return circularReferenceValidated;
    }

    /**
     * Sets the flag that controls the underline functionality to check or not about circular reference.
     *
     * @param circularReferenceValidation if true enables the circular reference check, false otherwise.
     *                                     Default is true
     * @return a new instance of the configuration with the given value being set
     */
    public JSONParserConfiguration withCircularReferenceValidation(boolean circularReferenceValidation) {
        JSONParserConfiguration configuration = (JSONParserConfiguration) clone();
        configuration.circularReferenceValidated = circularReferenceValidation;
        return configuration;
    }

    @Override
    protected Object clone() {
        return new JSONParserConfiguration(circularReferenceValidated);
    }
}
