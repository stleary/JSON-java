package org.json;

/*
Public Domain.
*/

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Use this annotation on a getter method to override the Bean name
 * parser for Bean -&gt; JSONObject mapping. A value set to empty string <code>""</code>
 * will have the Bean parser fall back to the default field name processing.
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD})
public @interface JSONPropertyName {
    /**
     * @return The name of the property as to be used in the JSON Object.
     */
    String value();
}
