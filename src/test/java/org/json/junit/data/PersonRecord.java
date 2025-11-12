package org.json.junit.data;

/**
 * A test class that mimics Java record accessor patterns.
 * Records use accessor methods without get/is prefixes (e.g., name() instead of getName()).
 * This class simulates that behavior to test JSONObject's handling of such methods.
 */
public class PersonRecord {
    private final String name;
    private final int age;
    private final boolean active;

    public PersonRecord(String name, int age, boolean active) {
        this.name = name;
        this.age = age;
        this.active = active;
    }

    // Record-style accessors (no "get" or "is" prefix)
    public String name() {
        return name;
    }

    public int age() {
        return age;
    }

    public boolean active() {
        return active;
    }
}
