package org.json.junit.data;

import java.io.StringReader;

/**
 * 
 * @author John Aylward
 *
 * @param <T>
 *            generic number value
 */
public class GenericBean<T extends Number & Comparable<T>> implements MyBean {
    /**
     * @param genericValue
     *            value to initiate with
     */
    public GenericBean(T genericValue) {
        super();
        this.genericValue = genericValue;
    }

    /** */
    protected T genericValue;
    /** to be used by the calling test to see how often the getter is called */
    public int genericGetCounter;
    /** to be used by the calling test to see how often the setter is called */
    public int genericSetCounter;

    /** @return the genericValue */
    public T getGenericValue() {
        this.genericGetCounter++;
        return this.genericValue;
    }

    /**
     * @param genericValue
     *            generic value to set
     */
    public void setGenericValue(T genericValue) {
        this.genericSetCounter++;
        this.genericValue = genericValue;
    }

    @Override
    public Integer getIntKey() {
        return Integer.valueOf(42);
    }

    @Override
    public Double getDoubleKey() {
        return Double.valueOf(4.2);
    }

    @Override
    public String getStringKey() {
        return "MyString Key";
    }

    @Override
    public String getEscapeStringKey() {
        return "\"My String with \"s";
    }

    @Override
    public Boolean isTrueKey() {
        return Boolean.TRUE;
    }

    @Override
    public Boolean isFalseKey() {
        return Boolean.FALSE;
    }

    @Override
    public StringReader getStringReaderKey() {
        return new StringReader("Some String Value in a reader");
    }

}
