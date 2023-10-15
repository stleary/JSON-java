package org.json;

public class JSONParserConfiguration {
    private final boolean circularDependencyValidation;

    private JSONParserConfiguration() {
        circularDependencyValidation = false;
    }

    private JSONParserConfiguration(boolean circularDependencyValidation) {
        this.circularDependencyValidation = circularDependencyValidation;
    }

    public static JSONParserConfiguration defaultInstance() {
        return new JSONParserConfiguration();
    }

    public static JSONParserConfiguration.Builder newInstance() {
        return new Builder();
    }

    public boolean isCircularDependencyValidation() {
        return circularDependencyValidation;
    }

    public static class Builder {
        private boolean circularDependencyValidation;

        public boolean isCircularDependencyValidation() {
            return circularDependencyValidation;
        }

        public Builder setCircularDependencyValidation(boolean circularDependencyValidation) {
            this.circularDependencyValidation = circularDependencyValidation;
            return this;
        }

        public JSONParserConfiguration build() {
            return new JSONParserConfiguration(
                circularDependencyValidation
            );
        }
    }
}
