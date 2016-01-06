package org.json.junit;

import java.math.*;

/**
 * Used in testing when a Bean containing big numbers is needed
 */
interface MyBigNumberBean {
    public BigInteger getBigInteger();
    public BigDecimal getBigDecimal();
}