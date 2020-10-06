/**
 * 
 */
package org.json.junit.data;

/**
 * @author john
 *
 */
public class GenericBeanInt extends GenericBean<Integer> {
    /** */
    final char a = 'A';

    /** @return the a */
    public char getA() {
        return this.a;
    }

    /**
     * Should not be beanable
     * 
     * @return false
     */
    public boolean getable() {
        return false;
    }

    /**
     * Should not be beanable
     * 
     * @return false
     */
    public boolean get() {
        return false;
    }

    /**
     * Should not be beanable
     * 
     * @return false
     */
    public boolean is() {
        return false;
    }

    /**
     * Should be beanable
     * 
     * @return false
     */
    public boolean isB() {
        return this.genericValue.equals((Integer.valueOf(this.a+1)));
    }

    /**
     * @param genericValue
     *            the value to initiate with.
     */
    public GenericBeanInt(Integer genericValue) {
        super(genericValue);
    }

    /** override to generate a bridge method */
    @Override
    public Integer getGenericValue() {
        return super.getGenericValue();
    }

}
