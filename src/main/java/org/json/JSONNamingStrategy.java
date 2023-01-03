package org.json;

/*
Public Domain.
*/

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use this annotation on class level to use a specific Naming Strategy.
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.TYPE})
public @interface JSONNamingStrategy {
    /**
     * @return The class' properties following the specified naming strategy.
     */
    PropertyNamingStrategies.Strategies value() default PropertyNamingStrategies.Strategies.LOWER_CAMEL_CASE;
}
