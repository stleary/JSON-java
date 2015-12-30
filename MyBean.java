package org.json.junit;

import java.io.*;

/**
 * Used in testing when Bean behavior is needed
 */
interface MyBean {
    public Integer getIntKey();
    public Double getDoubleKey();
    public String getStringKey();
    public String getEscapeStringKey();
    public Boolean isTrueKey();
    public Boolean isFalseKey();
    public StringReader getStringReaderKey();
}