package org.json.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import org.json.JSONObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class JSONObjectNumberTest {

    private Integer value = 50;

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {"{value:0050}", 1},
            {"{value:0050.0000}", 1},
            {"{value:-0050}", -1},
            {"{value:-0050.0000}", -1},
            {"{value:50.0}", 1},
            {"{value:5e1}", 1},
            {"{value:5E1}", 1},
            {"{value:5e1}", 1},
            {"{value:'50'}", 1},
            {"{value:-50}", -1},
            {"{value:-50.0}", -1},
            {"{value:-5e1}", -1},
            {"{value:-0005e1}", -1},
            {"{value:-5E1}", -1},
            {"{value:-5e1}", -1},
            {"{value:'-50'}", -1}
            // JSON does not support octal or hex numbers;
            // see https://stackoverflow.com/a/52671839/6323312
            // "{value:062}", // octal 50
            // "{value:0x32}" // hex 50
        });
    }

    public void initJSONObjectNumberTest(String objectString, int resultIsNegative) {
        this.value *= resultIsNegative;
        this.object = new JSONObject(objectString);
    }

    private JSONObject object;



    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void getNumber(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals(value.intValue(), object.getNumber("value").intValue());
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void getBigDecimal(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals(0, BigDecimal.valueOf(value).compareTo(object.getBigDecimal("value")));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void getBigInteger(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals(BigInteger.valueOf(value), object.getBigInteger("value"));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void getFloat(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals(value.floatValue(), object.getFloat("value"), 0.0f);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void getDouble(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals(value.doubleValue(), object.getDouble("value"), 0.0d);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void getInt(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals(value.intValue(), object.getInt("value"));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void getLong(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals(value.longValue(), object.getLong("value"));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void optNumber(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals(value.intValue(), object.optNumber("value").intValue());
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void optBigDecimal(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals(0, BigDecimal.valueOf(value).compareTo(object.optBigDecimal("value", null)));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void optBigInteger(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals(BigInteger.valueOf(value), object.optBigInteger("value", null));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void optFloat(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals(value.floatValue(), object.optFloat("value"), 0.0f);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void optFloatObject(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals((Float) value.floatValue(), object.optFloatObject("value"), 0.0f);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void optDouble(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals(value.doubleValue(), object.optDouble("value"), 0.0d);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void optDoubleObject(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals((Double) value.doubleValue(), object.optDoubleObject("value"), 0.0d);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void optInt(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals(value.intValue(), object.optInt("value"));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void optIntegerObject(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals((Integer) value.intValue(), object.optIntegerObject("value"));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void optLong(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals(value.longValue(), object.optLong("value"));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void optLongObject(String objectString, int resultIsNegative) {
        initJSONObjectNumberTest(objectString, resultIsNegative);
        assertEquals((Long) value.longValue(), object.optLongObject("value"));
    }
}
