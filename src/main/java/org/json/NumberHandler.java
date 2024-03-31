package org.json;

import java.util.Map;
import java.math.BigDecimal;

public class NumberHandler {
    public static boolean getBoolean(JSONArray array, int index) throws JSONException {
        Object object = array.get(index);
        if (object.equals(Boolean.FALSE)
                || (object instanceof String && ((String) object).equalsIgnoreCase("false"))) {
            return false;
        } else if (object.equals(Boolean.TRUE)
                || (object instanceof String && ((String) object).equalsIgnoreCase("true"))) {
            return true;
        }
        throw createWrongValueFormatException(index, "boolean", object, null);
    }

    public static double getDouble(JSONArray array, int index) throws JSONException {
        final Object object = array.get(index);
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }
        try {
            return Double.parseDouble(object.toString());
        } catch (Exception e) {
            throw createWrongValueFormatException(index, "double", object, e);
        }
    }

    public static BigDecimal getBigDecimal(JSONArray array, int index) throws JSONException {
        Object object = array.get(index);
        BigDecimal val = JSONObject.objectToBigDecimal(object, null);
        if (val == null) {
            throw createWrongValueFormatException(index, "BigDecimal", object, null);
        }
        return val;
    }

    public static int getInt(JSONArray array, int index) throws JSONException {
        final Object object = array.get(index);
        if (object instanceof Number) {
            return ((Number) object).intValue();
        }
        try {
            return Integer.parseInt(object.toString());
        } catch (Exception e) {
            throw createWrongValueFormatException(index, "int", object, e);
        }
    }

    public static long getLong(JSONArray array, int index) throws JSONException {
        final Object object = array.get(index);
        if (object instanceof Number) {
            return ((Number) object).longValue();
        }
        try {
            return Long.parseLong(object.toString());
        } catch (Exception e) {
            throw createWrongValueFormatException(index, "long", object, e);
        }
    }

    private static JSONException createWrongValueFormatException(int idx, String valueType, Object value,
            Throwable cause) {
        if (value == null) {
            return new JSONException("JSONArray[" + idx + "] is not a " + valueType + " (null).", cause);
        }
        if (value instanceof Map || value instanceof Iterable || value instanceof JSONObject) {
            return new JSONException("JSONArray[" + idx + "] is not a " + valueType + " (" + value.getClass() + ").",
                    cause);
        }
        return new JSONException(
                "JSONArray[" + idx + "] is not a " + valueType + " (" + value.getClass() + " : " + value + ").", cause);
    }

}