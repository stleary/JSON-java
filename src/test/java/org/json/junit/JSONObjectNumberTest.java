package org.json.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class JSONObjectNumberTest {
    private final String objectString;
    private Integer value = 50;

    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {"{value:50}", 1},
            {"{value:50.0}", 1},
            {"{value:5e1}", 1},
            {"{value:5E1}", 1},
            {"{value:5e1}", 1},
            {"{value:'50'}", 1},
            {"{value:-50}", -1},
            {"{value:-50.0}", -1},
            {"{value:-5e1}", -1},
            {"{value:-5E1}", -1},
            {"{value:-5e1}", -1},
            {"{value:'-50'}", -1}
            // JSON does not support octal or hex numbers;
            // see https://stackoverflow.com/a/52671839/6323312
            // "{value:062}", // octal 50
            // "{value:0x32}" // hex 50
        });
    }

    public JSONObjectNumberTest(String objectString, int resultIsNegative) {
        this.objectString = objectString;
        this.value *= resultIsNegative;
    }

    private JSONObject object;

    @Before
    public void setJsonObject() {
        object = new JSONObject(objectString);
    }

    @Test
    public void testGetNumber() {
        assertEquals(value.intValue(), object.getNumber("value").intValue());
    }

    @Test
    public void testGetBigDecimal() {
        assertTrue(BigDecimal.valueOf(value).compareTo(object.getBigDecimal("value")) == 0);
    }

    @Test
    public void testGetBigInteger() {
        assertEquals(BigInteger.valueOf(value), object.getBigInteger("value"));
    }

    @Test
    public void testGetFloat() {
        assertEquals(value.floatValue(), object.getFloat("value"), 0.0f);
    }

    @Test
    public void testGetDouble() {
        assertEquals(value.doubleValue(), object.getDouble("value"), 0.0d);
    }

    @Test
    public void testGetInt() {
        assertEquals(value.intValue(), object.getInt("value"));
    }

    @Test
    public void testGetLong() {
        assertEquals(value.longValue(), object.getLong("value"));
    }

    @Test
    public void testOptNumber() {
        assertEquals(value.intValue(), object.optNumber("value").intValue());
    }

    @Test
    public void testOptBigDecimal() {
        assertTrue(BigDecimal.valueOf(value).compareTo(object.optBigDecimal("value", null)) == 0);
    }

    @Test
    public void testOptBigInteger() {
        assertEquals(BigInteger.valueOf(value), object.optBigInteger("value", null));
    }

    @Test
    public void testOptFloat() {
        assertEquals(value.floatValue(), object.optFloat("value"), 0.0f);
    }

    @Test
    public void testOptFloatObject() {
        assertEquals((Float) value.floatValue(), object.optFloatObject("value"), 0.0f);
    }

    @Test
    public void testOptDouble() {
        assertEquals(value.doubleValue(), object.optDouble("value"), 0.0d);
    }

    @Test
    public void testOptDoubleObject() {
        assertEquals((Double) value.doubleValue(), object.optDoubleObject("value"), 0.0d);
    }

    @Test
    public void testOptInt() {
        assertEquals(value.intValue(), object.optInt("value"));
    }

    @Test
    public void testOptIntegerObject() {
        assertEquals((Integer) value.intValue(), object.optIntegerObject("value"));
    }

    @Test
    public void testOptLong() {
        assertEquals(value.longValue(), object.optLong("value"));
    }

    @Test
    public void testOptLongObject() {
        assertEquals((Long) value.longValue(), object.optLongObject("value"));
    }
}
