package org.json.junit.data;

/**
 * Sample singleton done as an Enum for use with bean testing.
 * 
 * @author John Aylward
 *
 */
public enum SingletonEnum {
    /**
     * the singleton instance.
     */
    INSTANCE;
    /** */
    private int someInt;
    /** */
    private String someString;

    /** single instance. */

    /**
     * @return the singleton instance. In a real application, I'd hope no one did
     *         this to an enum singleton.
     */
    public static final SingletonEnum getInstance() {
        return INSTANCE;
    }

    /** */
    private SingletonEnum() {
    }

    /** @return someInt */
    public int getSomeInt() {
        return this.someInt;
    }

    /**
     * sets someInt.
     * 
     * @param someInt
     *            the someInt to set
     */
    public void setSomeInt(int someInt) {
        this.someInt = someInt;
    }

    /** @return someString */
    public String getSomeString() {
        return this.someString;
    }

    /**
     * sets someString.
     * 
     * @param someString
     *            the someString to set
     */
    public void setSomeString(String someString) {
        this.someString = someString;
    }
}
