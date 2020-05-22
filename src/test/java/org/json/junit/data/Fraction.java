package org.json.junit.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * basic fraction class, no frills.
 * @author John Aylward
 *
 */
public class Fraction extends Number implements Comparable<Fraction> {
    /**
     * serial id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * value as a big decimal.
     */
    private final BigDecimal bigDecimal;

    /**
     * value of the denominator.
     */
    private final BigInteger denominator;
    /**
     * value of the numerator.
     */
    private final BigInteger numerator;

    /**
     * @param numerator
     *            numerator
     * @param denominator
     *            denominator
     */
    public Fraction(final BigInteger numerator, final BigInteger denominator) {
        super();
        if (numerator == null || denominator == null) {
            throw new IllegalArgumentException("All values must be non-null");
        }
        if (denominator.compareTo(BigInteger.ZERO)==0) {
            throw new IllegalArgumentException("Divide by zero");
        }

        final BigInteger n;
        final BigInteger d;
        // normalize fraction
        if (denominator.signum()<0) {
            n = numerator.negate();
            d = denominator.negate();
        } else {
            n = numerator;
            d = denominator;
        }
        this.numerator = n;
        this.denominator = d;
        if (n.compareTo(BigInteger.ZERO)==0) {
            this.bigDecimal = BigDecimal.ZERO;
        } else if (n.compareTo(d)==0) {// i.e. 4/4, 10/10
            this.bigDecimal = BigDecimal.ONE;
        } else {
            this.bigDecimal = new BigDecimal(this.numerator).divide(new BigDecimal(this.denominator),
                    RoundingMode.HALF_EVEN);
        }
    }
    
    /**
     * @param numerator
     *            numerator
     * @param denominator
     *            denominator
     */
    public Fraction(final long numerator, final long denominator) {
        this(BigInteger.valueOf(numerator),BigInteger.valueOf(denominator));
    }    

    /**
     * @return the decimal
     */
    public BigDecimal bigDecimalValue() {
        return this.bigDecimal;
    }

    @Override
    public int compareTo(final Fraction o) {
        // .equals call this, so no .equals compare allowed

        // if they are the same reference, just return equals
        if (this == o) {
            return 0;
        }

        // if my denominators are already equal, just compare the numerators
        if (this.denominator.compareTo(o.denominator)==0) {
            return this.numerator.compareTo(o.numerator);
        }

        // get numerators of common denominators
        // a    x     ay   xb
        // --- --- = ---- ----
        // b    y     by   yb
        final BigInteger thisN = this.numerator.multiply(o.denominator);
        final BigInteger otherN = o.numerator.multiply(this.denominator);

        return thisN.compareTo(otherN);
    }

    @Override
    public double doubleValue() {
        return this.bigDecimal.doubleValue();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Fraction other = (Fraction) obj;
        return this.compareTo(other) == 0;
    }

    @Override
    public float floatValue() {
        return this.bigDecimal.floatValue();
    }

    /**
     * @return the denominator
     */
    public BigInteger getDenominator() {
        return this.denominator;
    }

    /**
     * @return the numerator
     */
    public BigInteger getNumerator() {
        return this.numerator;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.bigDecimal == null ? 0 : this.bigDecimal.hashCode());
        return result;
    }

    @Override
    public int intValue() {
        return this.bigDecimal.intValue();
    }

    @Override
    public long longValue() {
        return this.bigDecimal.longValue();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.numerator + "/" + this.denominator;
    }
}
