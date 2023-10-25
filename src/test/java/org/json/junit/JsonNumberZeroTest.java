package org.json.junit;

import org.rookout.json.JSONObject;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class JsonNumberZeroTest {

    @Test
    public void shouldParseNegativeZeroValueWithMultipleZeroDigit(){
        JSONObject jsonObject = new JSONObject("{value:-0000}");
        assertEquals("Float not recognized", -0f, jsonObject.getFloat("value"), 0.0f);
        assertEquals("Float not recognized", -0f, jsonObject.optFloat("value"), 0.0f);
        assertEquals("Float not recognized", -0f, jsonObject.optFloatObject("value"), 0.0f);
        assertEquals("Double not recognized", -0d, jsonObject.optDouble("value"), 0.0f);
        assertEquals("Double not recognized", -0.0d, jsonObject.optDoubleObject("value"), 0.0f);
        assertEquals("Double not recognized", -0.0d, jsonObject.getDouble("value"), 0.0f);
        assertEquals("Long not recognized", 0, jsonObject.optLong("value"), 0);
        assertEquals("Long not recognized", 0, jsonObject.getLong("value"), 0);
        assertEquals("Long not recognized", 0, jsonObject.optLongObject("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.optInt("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.getInt("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.optIntegerObject("value"), 0);
        assertEquals("Number not recognized", 0, jsonObject.getNumber("value").intValue(), 0);
        assertEquals("Number not recognized", 0, jsonObject.getNumber("value").longValue(), 0);
        assertEquals("BigDecimal not recognized", 0, BigDecimal.valueOf(-0).compareTo(jsonObject.getBigDecimal("value")));
        assertEquals("BigInteger not recognized",0, BigInteger.valueOf(0).compareTo(jsonObject.getBigInteger("value")));
    }

    @Test
    public void shouldParseZeroValueWithMultipleZeroDigit(){
        JSONObject jsonObject = new JSONObject("{value:0000}");
        assertEquals("Float not recognized", 0f, jsonObject.getFloat("value"), 0.0f);
        assertEquals("Float not recognized", 0f, jsonObject.optFloat("value"), 0.0f);
        assertEquals("Float not recognized", 0f, jsonObject.optFloatObject("value"), 0.0f);
        assertEquals("Double not recognized", 0d, jsonObject.optDouble("value"), 0.0f);
        assertEquals("Double not recognized", 0.0d, jsonObject.optDoubleObject("value"), 0.0f);
        assertEquals("Double not recognized", 0.0d, jsonObject.getDouble("value"), 0.0f);
        assertEquals("Long not recognized", 0, jsonObject.optLong("value"), 0);
        assertEquals("Long not recognized", 0, jsonObject.getLong("value"), 0);
        assertEquals("Long not recognized", 0, jsonObject.optLongObject("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.optInt("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.getInt("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.optIntegerObject("value"), 0);
        assertEquals("Number not recognized", 0, jsonObject.getNumber("value").intValue(), 0);
        assertEquals("Number not recognized", 0, jsonObject.getNumber("value").longValue(), 0);
        assertEquals("BigDecimal not recognized", 0, BigDecimal.valueOf(-0).compareTo(jsonObject.getBigDecimal("value")));
        assertEquals("BigInteger not recognized",0, BigInteger.valueOf(0).compareTo(jsonObject.getBigInteger("value")));
    }

}
