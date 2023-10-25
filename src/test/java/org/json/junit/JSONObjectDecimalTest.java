package org.json.junit;

import org.rookout.json.JSONObject;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class JSONObjectDecimalTest {

    @Test
    public void shouldParseDecimalNumberThatStartsWithDecimalPoint(){
        JSONObject jsonObject = new JSONObject("{value:0.50}");
        assertEquals("Float not recognized", 0.5f, jsonObject.getFloat("value"), 0.0f);
        assertEquals("Float not recognized", 0.5f, jsonObject.optFloat("value"), 0.0f);
        assertEquals("Float not recognized", 0.5f, jsonObject.optFloatObject("value"), 0.0f);
        assertEquals("Double not recognized", 0.5d, jsonObject.optDouble("value"), 0.0f);
        assertEquals("Double not recognized", 0.5d, jsonObject.optDoubleObject("value"), 0.0f);
        assertEquals("Double not recognized", 0.5d, jsonObject.getDouble("value"), 0.0f);
        assertEquals("Long not recognized", 0, jsonObject.optLong("value"), 0);
        assertEquals("Long not recognized", 0, jsonObject.getLong("value"), 0);
        assertEquals("Long not recognized", 0, jsonObject.optLongObject("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.optInt("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.getInt("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.optIntegerObject("value"), 0);
        assertEquals("Number not recognized", 0, jsonObject.getNumber("value").intValue(), 0);
        assertEquals("Number not recognized", 0, jsonObject.getNumber("value").longValue(), 0);
        assertEquals("BigDecimal not recognized", 0, BigDecimal.valueOf(.5).compareTo(jsonObject.getBigDecimal("value")));
        assertEquals("BigInteger not recognized",0, BigInteger.valueOf(0).compareTo(jsonObject.getBigInteger("value")));
    }



    @Test
    public void shouldParseNegativeDecimalNumberThatStartsWithDecimalPoint(){
        JSONObject jsonObject = new JSONObject("{value:-.50}");
        assertEquals("Float not recognized", -0.5f, jsonObject.getFloat("value"), 0.0f);
        assertEquals("Float not recognized", -0.5f, jsonObject.optFloat("value"), 0.0f);
        assertEquals("Float not recognized", -0.5f, jsonObject.optFloatObject("value"), 0.0f);
        assertEquals("Double not recognized", -0.5d, jsonObject.optDouble("value"), 0.0f);
        assertEquals("Double not recognized", -0.5d, jsonObject.optDoubleObject("value"), 0.0f);
        assertEquals("Double not recognized", -0.5d, jsonObject.getDouble("value"), 0.0f);
        assertEquals("Long not recognized", 0, jsonObject.optLong("value"), 0);
        assertEquals("Long not recognized", 0, jsonObject.getLong("value"), 0);
        assertEquals("Long not recognized", 0, jsonObject.optLongObject("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.optInt("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.getInt("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.optIntegerObject("value"), 0);
        assertEquals("Number not recognized", 0, jsonObject.getNumber("value").intValue(), 0);
        assertEquals("Number not recognized", 0, jsonObject.getNumber("value").longValue(), 0);
        assertEquals("BigDecimal not recognized", 0, BigDecimal.valueOf(-.5).compareTo(jsonObject.getBigDecimal("value")));
        assertEquals("BigInteger not recognized",0, BigInteger.valueOf(0).compareTo(jsonObject.getBigInteger("value")));
    }

    @Test
    public void shouldParseDecimalNumberThatHasZeroBeforeWithDecimalPoint(){
        JSONObject jsonObject = new JSONObject("{value:00.050}");
        assertEquals("Float not recognized", 0.05f, jsonObject.getFloat("value"), 0.0f);
        assertEquals("Float not recognized", 0.05f, jsonObject.optFloat("value"), 0.0f);
        assertEquals("Float not recognized", 0.05f, jsonObject.optFloatObject("value"), 0.0f);
        assertEquals("Double not recognized", 0.05d, jsonObject.optDouble("value"), 0.0f);
        assertEquals("Double not recognized", 0.05d, jsonObject.optDoubleObject("value"), 0.0f);
        assertEquals("Double not recognized", 0.05d, jsonObject.getDouble("value"), 0.0f);
        assertEquals("Long not recognized", 0, jsonObject.optLong("value"), 0);
        assertEquals("Long not recognized", 0, jsonObject.getLong("value"), 0);
        assertEquals("Long not recognized", 0, jsonObject.optLongObject("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.optInt("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.getInt("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.optIntegerObject("value"), 0);
        assertEquals("Number not recognized", 0, jsonObject.getNumber("value").intValue(), 0);
        assertEquals("Number not recognized", 0, jsonObject.getNumber("value").longValue(), 0);
        assertEquals("BigDecimal not recognized", 0, BigDecimal.valueOf(.05).compareTo(jsonObject.getBigDecimal("value")));
        assertEquals("BigInteger not recognized",0, BigInteger.valueOf(0).compareTo(jsonObject.getBigInteger("value")));
    }

    @Test
    public void shouldParseNegativeDecimalNumberThatHasZeroBeforeWithDecimalPoint(){
        JSONObject jsonObject = new JSONObject("{value:-00.050}");
        assertEquals("Float not recognized", -0.05f, jsonObject.getFloat("value"), 0.0f);
        assertEquals("Float not recognized", -0.05f, jsonObject.optFloat("value"), 0.0f);
        assertEquals("Float not recognized", -0.05f, jsonObject.optFloatObject("value"), 0.0f);
        assertEquals("Double not recognized", -0.05d, jsonObject.optDouble("value"), 0.0f);
        assertEquals("Double not recognized", -0.05d, jsonObject.optDoubleObject("value"), 0.0f);
        assertEquals("Double not recognized", -0.05d, jsonObject.getDouble("value"), 0.0f);
        assertEquals("Long not recognized", 0, jsonObject.optLong("value"), 0);
        assertEquals("Long not recognized", 0, jsonObject.getLong("value"), 0);
        assertEquals("Long not recognized", 0, jsonObject.optLongObject("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.optInt("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.getInt("value"), 0);
        assertEquals("Integer not recognized", 0, jsonObject.optIntegerObject("value"), 0);
        assertEquals("Number not recognized", 0, jsonObject.getNumber("value").intValue(), 0);
        assertEquals("Number not recognized", 0, jsonObject.getNumber("value").longValue(), 0);
        assertEquals("BigDecimal not recognized", 0, BigDecimal.valueOf(-.05).compareTo(jsonObject.getBigDecimal("value")));
        assertEquals("BigInteger not recognized",0, BigInteger.valueOf(0).compareTo(jsonObject.getBigInteger("value")));
    }


}
