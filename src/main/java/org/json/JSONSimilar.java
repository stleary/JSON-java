package org.json;

/**
 * Interface for comparing JSON entities for semantic similarity.
 * @author JSON.org
 * @version 2023-07-20
 */
public interface JSONSimilar {
    /**
     * Determine if this JSON entity is similar to another object.
     * @param other The object to compare with
     * @return true if they are semantically similar
     */
    boolean similar(Object other);

    /**
     * Helper method to compare two arbitrary values according to JSON similarity rules.
     * @param a First value to compare
     * @param b Second value to compare
     * @return true if values are semantically similar
     */
    static boolean compare(Object a, Object b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a instanceof JSONSimilar) {
            return ((JSONSimilar)a).similar(b);
        }
        if (a instanceof Number && b instanceof Number) {
            return JSONSimilarUtils.areNumbersSimilar((Number)a, (Number)b);
        }
        return a.equals(b);
    }
}