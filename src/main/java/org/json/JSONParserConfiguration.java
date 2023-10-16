package org.json;

/**
 * Configuration class for JSON parsers.
 */
public class JSONParserConfiguration extends AbstractConfiguration {

    private boolean circularDependencyValidated;

    /**
     * Configuration with the default values.
     */
    public JSONParserConfiguration() {
        circularDependencyValidated = true;
    }

    private JSONParserConfiguration(boolean circularDependencyValidated) {
        this.circularDependencyValidated = circularDependencyValidated;
    }

    /**
     * Retrieves the configuration about the circular dependency check.
     *
     * @return if true enables the circular dependencies check, false otherwise
     */
    public boolean isCircularDependencyValidated() {
        return circularDependencyValidated;
    }

    /**
     * Sets the flag that controls the underline functionality to check or not about circular dependencies.
     *
     * @param circularDependencyValidation if true enables the circular dependencies check, false otherwise.
     *                                     Default is true
     * @return a new instance of the configuration with the given value being set
     */
    public JSONParserConfiguration withCircularDependencyValidation(boolean circularDependencyValidation) {
        return with(newInstance -> newInstance.circularDependencyValidated = circularDependencyValidation);
    }

    @Override
    protected Object clone() {
        return new JSONParserConfiguration(circularDependencyValidated);
    }
}
