package org.json.junit;

import org.json.NumberConversionUtil;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.*;

public class NumberConversionUtilTest {

    @Test
    public void shouldParseDecimalFractionNumbersWithMultipleLeadingZeros(){
        Number number = NumberConversionUtil.stringToNumber("00.10d");
        assertEquals("Do not match",  0.10d, number.doubleValue(),0.0d);
        assertEquals("Do not match",  0.10f, number.floatValue(),0.0f);
        assertEquals("Do not match",  0, number.longValue(),0);
        assertEquals("Do not match",  0, number.intValue(),0);
    }

    @Test
    public void shouldParseDecimalFractionNumbersWithSingleLeadingZero(){
        Number number = NumberConversionUtil.stringToNumber("0.10d");
        assertEquals("Do not match",  0.10d, number.doubleValue(),0.0d);
        assertEquals("Do not match",  0.10f, number.floatValue(),0.0f);
        assertEquals("Do not match",  0, number.longValue(),0);
        assertEquals("Do not match",  0, number.intValue(),0);
    }


     @Test
    public void shouldParseDecimalFractionNumbersWithZerosAfterDecimalPoint(){
        Number number = NumberConversionUtil.stringToNumber("0.010d");
        assertEquals("Do not match",  0.010d, number.doubleValue(),0.0d);
        assertEquals("Do not match",  0.010f, number.floatValue(),0.0f);
        assertEquals("Do not match",  0, number.longValue(),0);
        assertEquals("Do not match",  0, number.intValue(),0);
    }

    @Test
    public void shouldParseMixedDecimalFractionNumbersWithMultipleLeadingZeros(){
        Number number = NumberConversionUtil.stringToNumber("00200.10d");
        assertEquals("Do not match",  200.10d, number.doubleValue(),0.0d);
        assertEquals("Do not match",  200.10f, number.floatValue(),0.0f);
        assertEquals("Do not match",  200, number.longValue(),0);
        assertEquals("Do not match",  200, number.intValue(),0);
    }

    @Test
    public void shouldParseMixedDecimalFractionNumbersWithoutLeadingZero(){
        Number number = NumberConversionUtil.stringToNumber("200.10d");
        assertEquals("Do not match",  200.10d, number.doubleValue(),0.0d);
        assertEquals("Do not match",  200.10f, number.floatValue(),0.0f);
        assertEquals("Do not match",  200, number.longValue(),0);
        assertEquals("Do not match",  200, number.intValue(),0);
    }


     @Test
    public void shouldParseMixedDecimalFractionNumbersWithZerosAfterDecimalPoint(){
        Number number = NumberConversionUtil.stringToNumber("200.010d");
        assertEquals("Do not match",  200.010d, number.doubleValue(),0.0d);
        assertEquals("Do not match",  200.010f, number.floatValue(),0.0f);
        assertEquals("Do not match",  200, number.longValue(),0);
        assertEquals("Do not match",  200, number.intValue(),0);
    }


    @Test
    public void shouldParseNegativeDecimalFractionNumbersWithMultipleLeadingZeros(){
        Number number = NumberConversionUtil.stringToNumber("-00.10d");
        assertEquals("Do not match",  -0.10d, number.doubleValue(),0.0d);
        assertEquals("Do not match",  -0.10f, number.floatValue(),0.0f);
        assertEquals("Do not match",  -0, number.longValue(),0);
        assertEquals("Do not match",  -0, number.intValue(),0);
    }

    @Test
    public void shouldParseNegativeDecimalFractionNumbersWithSingleLeadingZero(){
        Number number = NumberConversionUtil.stringToNumber("-0.10d");
        assertEquals("Do not match",  -0.10d, number.doubleValue(),0.0d);
        assertEquals("Do not match",  -0.10f, number.floatValue(),0.0f);
        assertEquals("Do not match",  -0, number.longValue(),0);
        assertEquals("Do not match",  -0, number.intValue(),0);
    }


     @Test
    public void shouldParseNegativeDecimalFractionNumbersWithZerosAfterDecimalPoint(){
        Number number = NumberConversionUtil.stringToNumber("-0.010d");
        assertEquals("Do not match",  -0.010d, number.doubleValue(),0.0d);
        assertEquals("Do not match",  -0.010f, number.floatValue(),0.0f);
        assertEquals("Do not match",  -0, number.longValue(),0);
        assertEquals("Do not match",  -0, number.intValue(),0);
    }

    @Test
    public void shouldParseNegativeMixedDecimalFractionNumbersWithMultipleLeadingZeros(){
        Number number = NumberConversionUtil.stringToNumber("-00200.10d");
        assertEquals("Do not match",  -200.10d, number.doubleValue(),0.0d);
        assertEquals("Do not match",  -200.10f, number.floatValue(),0.0f);
        assertEquals("Do not match",  -200, number.longValue(),0);
        assertEquals("Do not match",  -200, number.intValue(),0);
    }

    @Test
    public void shouldParseNegativeMixedDecimalFractionNumbersWithoutLeadingZero(){
        Number number = NumberConversionUtil.stringToNumber("-200.10d");
        assertEquals("Do not match",  -200.10d, number.doubleValue(),0.0d);
        assertEquals("Do not match",  -200.10f, number.floatValue(),0.0f);
        assertEquals("Do not match",  -200, number.longValue(),0);
        assertEquals("Do not match",  -200, number.intValue(),0);
    }


     @Test
    public void shouldParseNegativeMixedDecimalFractionNumbersWithZerosAfterDecimalPoint(){
        Number number = NumberConversionUtil.stringToNumber("-200.010d");
        assertEquals("Do not match",  -200.010d, number.doubleValue(),0.0d);
        assertEquals("Do not match",  -200.010f, number.floatValue(),0.0f);
        assertEquals("Do not match",  -200, number.longValue(),0);
        assertEquals("Do not match",  -200, number.intValue(),0);
    }

    @Test
    public void shouldParseNumbersWithExponents(){
        Number number = NumberConversionUtil.stringToNumber("23.45e7");
        assertEquals("Do not match",  23.45e7d, number.doubleValue(),0.0d);
        assertEquals("Do not match",  23.45e7f, number.floatValue(),0.0f);
        assertEquals("Do not match",  2.345E8, number.longValue(),0);
        assertEquals("Do not match",  2.345E8, number.intValue(),0);
    }


    @Test
    public void shouldParseNegativeNumbersWithExponents(){
        Number number = NumberConversionUtil.stringToNumber("-23.45e7");
        assertEquals("Do not match",  -23.45e7d, number.doubleValue(),0.0d);
        assertEquals("Do not match",  -23.45e7f, number.floatValue(),0.0f);
        assertEquals("Do not match",  -2.345E8, number.longValue(),0);
        assertEquals("Do not match",  -2.345E8, number.intValue(),0);
    }

    @Test
    public void shouldParseBigDecimal(){
        Number number = NumberConversionUtil.stringToNumber("19007199254740993.35481234487103587486413587843213584");
        assertTrue(number instanceof BigDecimal);
    }

    @Test
    public void shouldParseBigInteger(){
        Number number = NumberConversionUtil.stringToNumber("1900719925474099335481234487103587486413587843213584");
        assertTrue(number instanceof BigInteger);
    }

    @Test
    public void shouldIdentifyPotentialNumber(){
        assertTrue("Does not identify as number", NumberConversionUtil.potentialNumber("112.123"));
        assertTrue("Does not identify as number", NumberConversionUtil.potentialNumber("112e123"));
        assertTrue("Does not identify as number", NumberConversionUtil.potentialNumber("-112.123"));
        assertTrue("Does not identify as number", NumberConversionUtil.potentialNumber("-112e23"));
        assertFalse("Does not identify as not number", NumberConversionUtil.potentialNumber("--112.123"));
        assertFalse("Does not identify as not number", NumberConversionUtil.potentialNumber("-a112.123"));
        assertFalse("Does not identify as not number", NumberConversionUtil.potentialNumber("a112.123"));
        assertFalse("Does not identify as not number", NumberConversionUtil.potentialNumber("e112.123"));
    }

    @Test(expected = NumberFormatException.class)
    public void shouldExpectExceptionWhenNumberIsNotFormatted(){
        NumberConversionUtil.stringToNumber("112.aa123");
    }


}