package org.json;
/**
 * A utility class containing XML special character constants.
 */
public class XMLSpecialCharacters {

    /** The Character '&amp;'. */
    private static final Character AMP = '&';

    /** The Character '&lt;'. */
    private static final Character LT = '<';

    /** The Character <pre>{@code '>'. }</pre> */
    private static final Character GT = '>';

    /** The Character '='. */
    private static final Character EQ = '=';

    /** The Character '!'. */
    private static final Character BANG = '!';

    // Private constructor to prevent instantiation
    private XMLSpecialCharacters() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // Getter methods for constants
    public static Character getAmp() {
        return AMP;
    }

    public static Character getLt() {
        return LT;
    }

    public static Character getGt() {
        return GT;
    }

    public static Character getEq() {
        return EQ;
    }

    public static Character getBang() {
        return BANG;
    }
}