package org.json;

/**
 * An enum class that is supposed to be used in {@link JSONParserConfiguration},
 * it dedicates which way should be used to handle duplicate keys.
 */
public enum JSONDuplicateKeyStrategy {
    /**
     * The default value. And this is the way it used to be in the previous versions.<br>
     * The JSONParser will throw an {@link JSONException} when meet duplicate key.
     */
    THROW_EXCEPTION,

    /**
     * The JSONParser will ignore duplicate keys and won't overwrite the value of the key.
     */
    IGNORE,

    /**
     * The JSONParser will overwrite the old value of the key.
     */
    OVERWRITE,

    /**
     * The JSONParser will try to merge the values of the duplicate key into a {@link JSONArray}.
     */
    MERGE_INTO_ARRAY
}
