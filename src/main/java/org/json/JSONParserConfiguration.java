package org.json;

public class JSONParserConfiguration extends AbstractConfiguration {

    private boolean circularDependencyValidated;

    public JSONParserConfiguration() {
        circularDependencyValidated = true;
    }

    private JSONParserConfiguration(boolean circularDependencyValidated) {
        this.circularDependencyValidated = circularDependencyValidated;
    }

    public boolean isCircularDependencyValidated() {
        return circularDependencyValidated;
    }

    public JSONParserConfiguration withCircularDependencyValidation(boolean circularDependencyValidation) {
        return with(newInstance -> newInstance.circularDependencyValidated = circularDependencyValidation);
    }

    @Override
    protected Object clone() {
        return new JSONParserConfiguration(circularDependencyValidated);
    }
}
