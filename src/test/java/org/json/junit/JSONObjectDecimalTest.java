package org.json.junit;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigInteger;

class JSONObjectDecimalTest {

    @Test
    void shouldParseDecimalNumberThatStartsWithDecimalPoint(){
        JSONObject jsonObject = new JSONObject("{value:0.50}");
        assertEquals(0.5f, jsonObject.getFloat("value"), 0.0f, "Float not recognized");
        assertEquals(0.5f, jsonObject.optFloat("value"), 0.0f, "Float not recognized");
        assertEquals(0.5f, jsonObject.optFloatObject("value"), 0.0f, "Float not recognized");
        assertEquals(0.5d, jsonObject.optDouble("value"), 0.0f, "Double not recognized");
        assertEquals(0.5d, jsonObject.optDoubleObject("value"), 0.0f, "Double not recognized");
        assertEquals(0.5d, jsonObject.getDouble("value"), 0.0f, "Double not recognized");
        assertEquals(0, jsonObject.optLong("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.getLong("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.optLongObject("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.optInt("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.getInt("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.optIntegerObject("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.getNumber("value").intValue(), 0, "Number not recognized");
        assertEquals(0, jsonObject.getNumber("value").longValue(), 0, "Number not recognized");
        assertEquals(0, BigDecimal.valueOf(.5).compareTo(jsonObject.getBigDecimal("value")), "BigDecimal not recognized");
        assertEquals(0, BigInteger.valueOf(0).compareTo(jsonObject.getBigInteger("value")), "BigInteger not recognized");
    }


    @Test
    void shouldParseNegativeDecimalNumberThatStartsWithDecimalPoint(){
        JSONObject jsonObject = new JSONObject("{value:-.50}");
        assertEquals(-0.5f, jsonObject.getFloat("value"), 0.0f, "Float not recognized");
        assertEquals(-0.5f, jsonObject.optFloat("value"), 0.0f, "Float not recognized");
        assertEquals(-0.5f, jsonObject.optFloatObject("value"), 0.0f, "Float not recognized");
        assertEquals(-0.5d, jsonObject.optDouble("value"), 0.0f, "Double not recognized");
        assertEquals(-0.5d, jsonObject.optDoubleObject("value"), 0.0f, "Double not recognized");
        assertEquals(-0.5d, jsonObject.getDouble("value"), 0.0f, "Double not recognized");
        assertEquals(0, jsonObject.optLong("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.getLong("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.optLongObject("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.optInt("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.getInt("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.optIntegerObject("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.getNumber("value").intValue(), 0, "Number not recognized");
        assertEquals(0, jsonObject.getNumber("value").longValue(), 0, "Number not recognized");
        assertEquals(0, BigDecimal.valueOf(-.5).compareTo(jsonObject.getBigDecimal("value")), "BigDecimal not recognized");
        assertEquals(0, BigInteger.valueOf(0).compareTo(jsonObject.getBigInteger("value")), "BigInteger not recognized");
    }

    @Test
    void shouldParseDecimalNumberThatHasZeroBeforeWithDecimalPoint(){
        JSONObject jsonObject = new JSONObject("{value:00.050}");
        assertEquals(0.05f, jsonObject.getFloat("value"), 0.0f, "Float not recognized");
        assertEquals(0.05f, jsonObject.optFloat("value"), 0.0f, "Float not recognized");
        assertEquals(0.05f, jsonObject.optFloatObject("value"), 0.0f, "Float not recognized");
        assertEquals(0.05d, jsonObject.optDouble("value"), 0.0f, "Double not recognized");
        assertEquals(0.05d, jsonObject.optDoubleObject("value"), 0.0f, "Double not recognized");
        assertEquals(0.05d, jsonObject.getDouble("value"), 0.0f, "Double not recognized");
        assertEquals(0, jsonObject.optLong("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.getLong("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.optLongObject("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.optInt("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.getInt("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.optIntegerObject("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.getNumber("value").intValue(), 0, "Number not recognized");
        assertEquals(0, jsonObject.getNumber("value").longValue(), 0, "Number not recognized");
        assertEquals(0, BigDecimal.valueOf(.05).compareTo(jsonObject.getBigDecimal("value")), "BigDecimal not recognized");
        assertEquals(0, BigInteger.valueOf(0).compareTo(jsonObject.getBigInteger("value")), "BigInteger not recognized");
    }

    @Test
    void shouldParseNegativeDecimalNumberThatHasZeroBeforeWithDecimalPoint(){
        JSONObject jsonObject = new JSONObject("{value:-00.050}");
        assertEquals(-0.05f, jsonObject.getFloat("value"), 0.0f, "Float not recognized");
        assertEquals(-0.05f, jsonObject.optFloat("value"), 0.0f, "Float not recognized");
        assertEquals(-0.05f, jsonObject.optFloatObject("value"), 0.0f, "Float not recognized");
        assertEquals(-0.05d, jsonObject.optDouble("value"), 0.0f, "Double not recognized");
        assertEquals(-0.05d, jsonObject.optDoubleObject("value"), 0.0f, "Double not recognized");
        assertEquals(-0.05d, jsonObject.getDouble("value"), 0.0f, "Double not recognized");
        assertEquals(0, jsonObject.optLong("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.getLong("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.optLongObject("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.optInt("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.getInt("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.optIntegerObject("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.getNumber("value").intValue(), 0, "Number not recognized");
        assertEquals(0, jsonObject.getNumber("value").longValue(), 0, "Number not recognized");
        assertEquals(0, BigDecimal.valueOf(-.05).compareTo(jsonObject.getBigDecimal("value")), "BigDecimal not recognized");
        assertEquals(0, BigInteger.valueOf(0).compareTo(jsonObject.getBigInteger("value")), "BigInteger not recognized");
    }


}
