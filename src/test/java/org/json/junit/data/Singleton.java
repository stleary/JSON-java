package org.json.junit.data;

/**
 * Sample singleton for use with bean testing.
 * 
 * @author John Aylward
 *
 */
public final class Singleton {
    /** */
    private int someInt;
    /** */
    private String someString;
    /** single instance. */
    private static final Singleton INSTANCE = new Singleton();

    /** @return the singleton instance. */
    public static final Singleton getInstance() {
        return INSTANCE;
    }

    /** */
    private Singleton() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Already instantiated");
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return INSTANCE;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.someInt;
        result = prime * result + ((this.someString == null) ? 0 : this.someString.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Singleton other = (Singleton) obj;
        if (this.someInt != other.someInt)
            return false;
        if (this.someString == null) {
            if (other.someString != null)
                return false;
        } else if (!this.someString.equals(other.someString))
            return false;
        return true;
    }
}
