package org.json;

import java.util.function.BiFunction;

public class PropertyNamingStrategies {

    public String toLowerCamelCase(String input) {
        return input;
    }

    public String toUpperCamelCase(String input) {
        if (input == null || input.isEmpty()){
            return input; // garbage in, garbage out
        }
        // Replace first lower-case letter with upper-case equivalent
        char c = input.charAt(0);
        char uc = Character.toUpperCase(c);
        if (c == uc) {
            return input;
        }
        StringBuilder sb = new StringBuilder(input);
        sb.setCharAt(0, uc);
        return sb.toString();
    }

    public String toSnakeCase(String input) {
        if (input == null) return input; // garbage in, garbage out
        int length = input.length();
        StringBuilder result = new StringBuilder(length * 2);
        int resultLength = 0;
        boolean wasPrevTranslated = false;
        for (int i = 0; i < length; i++)
        {
            char c = input.charAt(i);
            if (i > 0 || c != '_') // skip first starting underscore
            {
                if (Character.isUpperCase(c))
                {
                    if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != '_')
                    {
                        result.append('_');
                        resultLength++;
                    }
                    c = Character.toLowerCase(c);
                    wasPrevTranslated = true;
                }
                else
                {
                    wasPrevTranslated = false;
                }
                result.append(c);
                resultLength++;
            }
        }
        return resultLength > 0 ? result.toString() : input;
    }

    public String toUpperSnakeCase(String input) {
        String output = toSnakeCase(input);
        if (output == null)
            return null;
        return toSnakeCase(input).toUpperCase();
    }

    public String toLowerCase(String input) {
        return input.toLowerCase();
    }

    public String toKebabCase(String input) {
        return translateLowerCaseWithSeparator(input, '-');
    }

    public String toLowerDotCase(String input) {
        return translateLowerCaseWithSeparator(input, '.');
    }

    private String translateLowerCaseWithSeparator(final String input, final char separator) {
        if (input == null) {
            return input; // garbage in, garbage out
        }
        final int length = input.length();
        if (length == 0) {
            return input;
        }

        final StringBuilder result = new StringBuilder(length + (length >> 1));
        int upperCount = 0;
        for (int i = 0; i < length; ++i) {
            char ch = input.charAt(i);
            char lc = Character.toLowerCase(ch);

            if (lc == ch) { // lower-case letter means we can get new word
                // but need to check for multi-letter upper-case (acronym), where assumption
                // is that the last upper-case char is start of a new word
                if (upperCount > 1) {
                    // so insert hyphen before the last character now
                    result.insert(result.length() - 1, separator);
                }
                upperCount = 0;
            } else {
                // Otherwise starts new word, unless beginning of string
                if ((upperCount == 0) && (i > 0)) {
                    result.append(separator);
                }
                ++upperCount;
            }
            result.append(lc);
        }
        return result.toString();
    }

    public enum Strategies {
        LOWER_CAMEL_CASE(PropertyNamingStrategies::toLowerCamelCase),
        UPPER_CAMEL_CASE(PropertyNamingStrategies::toUpperCamelCase),
        SNAKE_CASE(PropertyNamingStrategies::toSnakeCase),
        UPPER_SNAKE_CASE(PropertyNamingStrategies::toUpperSnakeCase),
        LOWER_CASE(PropertyNamingStrategies::toLowerCase),
        KEBAB_CASE(PropertyNamingStrategies::toKebabCase),
        LOWER_DOT_CASE(PropertyNamingStrategies::toLowerDotCase);

        final BiFunction<PropertyNamingStrategies, String, String> transformer;

        public BiFunction<PropertyNamingStrategies, String, String> getTransformer() {
            return transformer;
        }

        Strategies(BiFunction<PropertyNamingStrategies, String, String> transformer) {
            this.transformer = transformer;
        }
    }
}
