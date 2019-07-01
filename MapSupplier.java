package org.json;

import java.util.Map;

/**
 * Interface for a map supplier which may be passed to {@link JSONObject#JSONObject(org.json.MapSupplier) }.
 * Basically a non-generic version (with fixed return type {@code Map<String, Object>}) of java.util.function.Supplier which has been introduced in Java 8.
 * This local interface has been created in order to maintain backwards compatibility with Java 6 and 7.
 * @author JSON.org
 * @version 2019-07-01
 */
public interface MapSupplier {
    
    /**
     * Returns a new, empty map for storing any Objects with keys of type String.
     * @return new, empty map of a desired implementation class
     */
    Map<String, Object> get();
    
}
