package org.json.junit;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigInteger;

class JsonNumberZeroTest {

    @Test
    void shouldParseNegativeZeroValueWithMultipleZeroDigit(){
        JSONObject jsonObject = new JSONObject("{value:-0000}");
        assertEquals(-0f, jsonObject.getFloat("value"), 0.0f, "Float not recognized");
        assertEquals(-0f, jsonObject.optFloat("value"), 0.0f, "Float not recognized");
        assertEquals(-0f, jsonObject.optFloatObject("value"), 0.0f, "Float not recognized");
        assertEquals(-0d, jsonObject.optDouble("value"), 0.0f, "Double not recognized");
        assertEquals(-0.0d, jsonObject.optDoubleObject("value"), 0.0f, "Double not recognized");
        assertEquals(-0.0d, jsonObject.getDouble("value"), 0.0f, "Double not recognized");
        assertEquals(0, jsonObject.optLong("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.getLong("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.optLongObject("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.optInt("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.getInt("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.optIntegerObject("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.getNumber("value").intValue(), 0, "Number not recognized");
        assertEquals(0, jsonObject.getNumber("value").longValue(), 0, "Number not recognized");
        assertEquals(0, BigDecimal.valueOf(-0).compareTo(jsonObject.getBigDecimal("value")), "BigDecimal not recognized");
        assertEquals(0, BigInteger.valueOf(0).compareTo(jsonObject.getBigInteger("value")), "BigInteger not recognized");
    }

    @Test
    void shouldParseZeroValueWithMultipleZeroDigit(){
        JSONObject jsonObject = new JSONObject("{value:0000}");
        assertEquals(0f, jsonObject.getFloat("value"), 0.0f, "Float not recognized");
        assertEquals(0f, jsonObject.optFloat("value"), 0.0f, "Float not recognized");
        assertEquals(0f, jsonObject.optFloatObject("value"), 0.0f, "Float not recognized");
        assertEquals(0d, jsonObject.optDouble("value"), 0.0f, "Double not recognized");
        assertEquals(0.0d, jsonObject.optDoubleObject("value"), 0.0f, "Double not recognized");
        assertEquals(0.0d, jsonObject.getDouble("value"), 0.0f, "Double not recognized");
        assertEquals(0, jsonObject.optLong("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.getLong("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.optLongObject("value"), 0, "Long not recognized");
        assertEquals(0, jsonObject.optInt("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.getInt("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.optIntegerObject("value"), 0, "Integer not recognized");
        assertEquals(0, jsonObject.getNumber("value").intValue(), 0, "Number not recognized");
        assertEquals(0, jsonObject.getNumber("value").longValue(), 0, "Number not recognized");
        assertEquals(0, BigDecimal.valueOf(-0).compareTo(jsonObject.getBigDecimal("value")), "BigDecimal not recognized");
        assertEquals(0, BigInteger.valueOf(0).compareTo(jsonObject.getBigInteger("value")), "BigInteger not recognized");
    }

}
