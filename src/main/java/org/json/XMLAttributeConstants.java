package org.json;

public class XMLAttributeConstants {
    private static final String NULL_ATTR = "xsi:nil";
    private static final String TYPE_ATTR = "xsi:type";

    private XMLAttributeConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    public static String getNullAttr() {
        return NULL_ATTR;
    }

    public static String getTypeAttr() {
        return TYPE_ATTR;
    }
}
