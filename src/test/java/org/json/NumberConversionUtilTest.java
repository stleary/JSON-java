package org.json;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberConversionUtilTest {

    @Test
    void shouldParseDecimalFractionNumbersWithMultipleLeadingZeros(){
        Number number = NumberConversionUtil.stringToNumber("00.10d");
        assertEquals(0.10d, number.doubleValue(),0.0d,"Do not match");
        assertEquals(0.10f, number.floatValue(),0.0f,"Do not match");
        assertEquals(0, number.longValue(),0,"Do not match");
        assertEquals(0, number.intValue(),0,"Do not match");
    }

    @Test
    void shouldParseDecimalFractionNumbersWithSingleLeadingZero(){
        Number number = NumberConversionUtil.stringToNumber("0.10d");
        assertEquals(0.10d, number.doubleValue(),0.0d,"Do not match");
        assertEquals(0.10f, number.floatValue(),0.0f,"Do not match");
        assertEquals(0, number.longValue(),0,"Do not match");
        assertEquals(0, number.intValue(),0,"Do not match");
    }


    @Test
    void shouldParseDecimalFractionNumbersWithZerosAfterDecimalPoint(){
        Number number = NumberConversionUtil.stringToNumber("0.010d");
        assertEquals(0.010d, number.doubleValue(),0.0d,"Do not match");
        assertEquals(0.010f, number.floatValue(),0.0f,"Do not match");
        assertEquals(0, number.longValue(),0,"Do not match");
        assertEquals(0, number.intValue(),0,"Do not match");
    }

    @Test
    void shouldParseMixedDecimalFractionNumbersWithMultipleLeadingZeros(){
        Number number = NumberConversionUtil.stringToNumber("00200.10d");
        assertEquals(200.10d, number.doubleValue(),0.0d,"Do not match");
        assertEquals(200.10f, number.floatValue(),0.0f,"Do not match");
        assertEquals(200, number.longValue(),0,"Do not match");
        assertEquals(200, number.intValue(),0,"Do not match");
    }

    @Test
    void shouldParseMixedDecimalFractionNumbersWithoutLeadingZero(){
        Number number = NumberConversionUtil.stringToNumber("200.10d");
        assertEquals(200.10d, number.doubleValue(),0.0d,"Do not match");
        assertEquals(200.10f, number.floatValue(),0.0f,"Do not match");
        assertEquals(200, number.longValue(),0,"Do not match");
        assertEquals(200, number.intValue(),0,"Do not match");
    }


    @Test
    void shouldParseMixedDecimalFractionNumbersWithZerosAfterDecimalPoint(){
        Number number = NumberConversionUtil.stringToNumber("200.010d");
        assertEquals(200.010d, number.doubleValue(),0.0d,"Do not match");
        assertEquals(200.010f, number.floatValue(),0.0f,"Do not match");
        assertEquals(200, number.longValue(),0,"Do not match");
        assertEquals(200, number.intValue(),0,"Do not match");
    }


    @Test
    void shouldParseNegativeDecimalFractionNumbersWithMultipleLeadingZeros(){
        Number number = NumberConversionUtil.stringToNumber("-00.10d");
        assertEquals(-0.10d, number.doubleValue(),0.0d,"Do not match");
        assertEquals(-0.10f, number.floatValue(),0.0f,"Do not match");
        assertEquals(-0, number.longValue(),0,"Do not match");
        assertEquals(-0, number.intValue(),0,"Do not match");
    }

    @Test
    void shouldParseNegativeDecimalFractionNumbersWithSingleLeadingZero(){
        Number number = NumberConversionUtil.stringToNumber("-0.10d");
        assertEquals(-0.10d, number.doubleValue(),0.0d,"Do not match");
        assertEquals(-0.10f, number.floatValue(),0.0f,"Do not match");
        assertEquals(-0, number.longValue(),0,"Do not match");
        assertEquals(-0, number.intValue(),0,"Do not match");
    }


    @Test
    void shouldParseNegativeDecimalFractionNumbersWithZerosAfterDecimalPoint(){
        Number number = NumberConversionUtil.stringToNumber("-0.010d");
        assertEquals(-0.010d, number.doubleValue(),0.0d,"Do not match");
        assertEquals(-0.010f, number.floatValue(),0.0f,"Do not match");
        assertEquals(-0, number.longValue(),0,"Do not match");
        assertEquals(-0, number.intValue(),0,"Do not match");
    }

    @Test
    void shouldParseNegativeMixedDecimalFractionNumbersWithMultipleLeadingZeros(){
        Number number = NumberConversionUtil.stringToNumber("-00200.10d");
        assertEquals(-200.10d, number.doubleValue(),0.0d,"Do not match");
        assertEquals(-200.10f, number.floatValue(),0.0f,"Do not match");
        assertEquals(-200, number.longValue(),0,"Do not match");
        assertEquals(-200, number.intValue(),0,"Do not match");
    }

    @Test
    void shouldParseNegativeMixedDecimalFractionNumbersWithoutLeadingZero(){
        Number number = NumberConversionUtil.stringToNumber("-200.10d");
        assertEquals(-200.10d, number.doubleValue(),0.0d,"Do not match");
        assertEquals(-200.10f, number.floatValue(),0.0f,"Do not match");
        assertEquals(-200, number.longValue(),0,"Do not match");
        assertEquals(-200, number.intValue(),0,"Do not match");
    }


    @Test
    void shouldParseNegativeMixedDecimalFractionNumbersWithZerosAfterDecimalPoint(){
        Number number = NumberConversionUtil.stringToNumber("-200.010d");
        assertEquals(-200.010d, number.doubleValue(),0.0d,"Do not match");
        assertEquals(-200.010f, number.floatValue(),0.0f,"Do not match");
        assertEquals(-200, number.longValue(),0,"Do not match");
        assertEquals(-200, number.intValue(),0,"Do not match");
    }

    @Test
    void shouldParseNumbersWithExponents(){
        Number number = NumberConversionUtil.stringToNumber("23.45e7");
        assertEquals(23.45e7d, number.doubleValue(),0.0d,"Do not match");
        assertEquals(23.45e7f, number.floatValue(),0.0f,"Do not match");
        assertEquals(2.345E8, number.longValue(),0,"Do not match");
        assertEquals(2.345E8, number.intValue(),0,"Do not match");
    }


    @Test
    void shouldParseNegativeNumbersWithExponents(){
        Number number = NumberConversionUtil.stringToNumber("-23.45e7");
        assertEquals(-23.45e7d, number.doubleValue(),0.0d,"Do not match");
        assertEquals(-23.45e7f, number.floatValue(),0.0f,"Do not match");
        assertEquals(-2.345E8, number.longValue(),0,"Do not match");
        assertEquals(-2.345E8, number.intValue(),0,"Do not match");
    }

    @Test
    void shouldParseBigDecimal(){
        Number number = NumberConversionUtil.stringToNumber("19007199254740993.35481234487103587486413587843213584");
        assertTrue(number instanceof BigDecimal);
    }

    @Test
    void shouldParseBigInteger(){
        Number number = NumberConversionUtil.stringToNumber("1900719925474099335481234487103587486413587843213584");
        assertTrue(number instanceof BigInteger);
    }

    @Test
    void shouldIdentifyPotentialNumber(){
        assertTrue(NumberConversionUtil.potentialNumber("112.123"), "Does not identify as number");
        assertTrue(NumberConversionUtil.potentialNumber("112e123"), "Does not identify as number");
        assertTrue(NumberConversionUtil.potentialNumber("-112.123"), "Does not identify as number");
        assertTrue(NumberConversionUtil.potentialNumber("-112e23"), "Does not identify as number");
        assertFalse(NumberConversionUtil.potentialNumber("--112.123"), "Does not identify as not number");
        assertFalse(NumberConversionUtil.potentialNumber("-a112.123"), "Does not identify as not number");
        assertFalse(NumberConversionUtil.potentialNumber("a112.123"), "Does not identify as not number");
        assertFalse(NumberConversionUtil.potentialNumber("e112.123"), "Does not identify as not number");
    }

    @Test
    void shouldExpectExceptionWhenNumberIsNotFormatted(){
        assertThrows(NumberFormatException.class, () -> {
            NumberConversionUtil.stringToNumber("112.aa123");
        });
    }


}