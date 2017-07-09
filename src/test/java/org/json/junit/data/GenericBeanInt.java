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
        return a;
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
