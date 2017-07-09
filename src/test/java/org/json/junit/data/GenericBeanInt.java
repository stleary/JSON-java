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

    /** return the a */
    public char getA() {
        return a;
    }
    
    /** return false. should not be beanable */
    public boolean getable() {
        return false;
    }

    /** */
    public GenericBeanInt(Integer genericValue) {
        super(genericValue);
    }

}
