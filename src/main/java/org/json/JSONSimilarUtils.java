package org.json;

/**
 * Utility class for JSON similarity comparisons.
 * @author JSON.org
 * @version 2023-07-20
 */
public final class JSONSimilarUtils {
    private static final double FLOATING_POINT_TOLERANCE = 0.000001d;

    /**
     * Compare two numbers for JSON similarity with proper handling of different numeric types.
     * @param a First number to compare
     * @param b Second number to compare
     * @return true if numbers are semantically equivalent in JSON context
     */
    static boolean areNumbersSimilar(Number a, Number b) {
        if (a.equals(b)) {
            return true;
        }
        if (a instanceof Double || a instanceof Float ||
                b instanceof Double || b instanceof Float) {
            return Math.abs(a.doubleValue() - b.doubleValue()) < FLOATING_POINT_TOLERANCE;
        }
        return a.longValue() == b.longValue();
    }
}