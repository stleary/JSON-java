package org.json;

/**
 * A utility class containing XML structural character constants.
 */
public class XMLStructuralCharacters {

    /** The Character '/'. */
    private static final Character SLASH = '/';

    /** The Character '?'. */
    private static final Character QUEST = '?';

    /** The Character '''. */
    private static final Character APOS = '\'';

    /** The Character '"'. */
    private static final Character QUOT = '"';

    // Private constructor to prevent instantiation
    private XMLStructuralCharacters() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // Getter methods for constants
    public static Character getSlash() {
        return SLASH;
    }

    public static Character getQuest() {
        return QUEST;
    }

    public static Character getApos() {
        return APOS;
    }

    public static Character getQuot() {
        return QUOT;
    }
}
