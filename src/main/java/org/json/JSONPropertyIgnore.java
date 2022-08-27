package org.json;

/*
Public Domain.
*/

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({METHOD})
/**
 * Use this annotation on a getter method to override the Bean name
 * parser for Bean -&gt; JSONObject mapping. If this annotation is
 * present at any level in the class hierarchy, then the method will
 * not be serialized from the bean into the JSONObject.
 */
public @interface JSONPropertyIgnore { }
