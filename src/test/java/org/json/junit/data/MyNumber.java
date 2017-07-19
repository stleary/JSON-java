package org.json.junit.data;

import java.math.BigDecimal;

/**
 * Number override for testing. Number overrides should always override
 * toString, hashCode, and Equals.
 * 
 * @see <a
 *      href="https://docs.oracle.com/javase/tutorial/java/data/numberclasses.html">The
 *      Numbers Classes</a>
 * @see <a
 *      href="https://docs.oracle.com/javase/tutorial/java/data/numberformat.html">Formatting
 *      Numeric Print Output</a>
 * 
 * @author John Aylward
 */
public class MyNumber extends Number {
    private Number number = BigDecimal.valueOf(42);
    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * @return number!
     */
    public Number getNumber() {
        return this.number;
    }

    @Override
    public int intValue() {
        return getNumber().intValue();
    }

    @Override
    public long longValue() {
        return getNumber().longValue();
    }

    @Override
    public float floatValue() {
        return getNumber().floatValue();
    }

    @Override
    public double doubleValue() {
        return getNumber().doubleValue();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     * 
     * Number overrides should in general always override the  toString method.
     */
    @Override
    public String toString() {
        return getNumber().toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.number == null) ? 0 : this.number.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MyNumber)) {
            return false;
        }
        MyNumber other = (MyNumber) obj;
        if (this.number == null) {
            if (other.number != null) {
                return false;
            }
        } else if (!this.number.equals(other.number)) {
            return false;
        }
        return true;
    }

}
