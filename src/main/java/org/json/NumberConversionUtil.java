package org.json;

import java.math.BigDecimal;
import java.math.BigInteger;

class NumberConversionUtil {

    /**
     * Converts a string to a number using the narrowest possible type. Possible
     * returns for this function are BigDecimal, Double, BigInteger, Long, and Integer.
     * When a Double is returned, it should always be a valid Double and not NaN or +-infinity.
     *
     * @param input value to convert
     * @return Number representation of the value.
     * @throws NumberFormatException thrown if the value is not a valid number. A public
     *      caller should catch this and wrap it in a {@link JSONException} if applicable.
     */
    static Number stringToNumber(final String input) throws NumberFormatException {
        String val = input;
        if (val.startsWith(".")){
            val = "0"+val;
        }
        if (val.startsWith("-.")){
            val = "-0."+val.substring(2);
        }
        char initial = val.charAt(0);
        if ( isNumericChar(initial) || initial == '-' ) {
            // decimal representation
            if (isDecimalNotation(val)) {
                // Use a BigDecimal all the time so we keep the original
                // representation. BigDecimal doesn't support -0.0, ensure we
                // keep that by forcing a decimal.
                try {
                    BigDecimal bd = new BigDecimal(val);
                    if(initial == '-' && BigDecimal.ZERO.compareTo(bd)==0) {
                        return Double.valueOf(-0.0);
                    }
                    return bd;
                } catch (NumberFormatException retryAsDouble) {
                    // this is to support "Hex Floats" like this: 0x1.0P-1074
                    try {
                        Double d = Double.valueOf(val);
                        if(d.isNaN() || d.isInfinite()) {
                            throw new NumberFormatException("val ["+input+"] is not a valid number.");
                        }
                        return d;
                    } catch (NumberFormatException ignore) {
                        throw new NumberFormatException("val ["+input+"] is not a valid number.");
                    }
                }
            }
            val = removeLeadingZerosOfNumber(input);
            initial = val.charAt(0);
            if(initial == '0' && val.length() > 1) {
                char at1 = val.charAt(1);
                if(isNumericChar(at1)) {
                    throw new NumberFormatException("val ["+input+"] is not a valid number.");
                }
            } else if (initial == '-' && val.length() > 2) {
                char at1 = val.charAt(1);
                char at2 = val.charAt(2);
                if(at1 == '0' && isNumericChar(at2)) {
                    throw new NumberFormatException("val ["+input+"] is not a valid number.");
                }
            }
            // integer representation.
            // This will narrow any values to the smallest reasonable Object representation
            // (Integer, Long, or BigInteger)

            // BigInteger down conversion: We use a similar bitLength compare as
            // BigInteger#intValueExact uses. Increases GC, but objects hold
            // only what they need. i.e. Less runtime overhead if the value is
            // long lived.
            BigInteger bi = new BigInteger(val);
            if(bi.bitLength() <= 31){
                return Integer.valueOf(bi.intValue());
            }
            if(bi.bitLength() <= 63){
                return Long.valueOf(bi.longValue());
            }
            return bi;
        }
        throw new NumberFormatException("val ["+input+"] is not a valid number.");
    }

    /**
     * Checks if the character is a numeric digit ('0' to '9').
     *
     * @param c The character to be checked.
     * @return true if the character is a numeric digit, false otherwise.
     */
    private static boolean isNumericChar(char c) {
        return (c <= '9' && c >= '0');
    }

    /**
     * Checks if the value could be considered a number in decimal number system.
     * @param value
     * @return
     */
    static boolean potentialNumber(String value){
        if (value == null || value.isEmpty()){
            return false;
        }
        return potentialPositiveNumberStartingAtIndex(value, (value.charAt(0)=='-'?1:0));
    }

    /**
     * Tests if the value should be tried as a decimal. It makes no test if there are actual digits.
     *
     * @param val value to test
     * @return true if the string is "-0" or if it contains '.', 'e', or 'E', false otherwise.
     */
    private static boolean isDecimalNotation(final String val) {
        return val.indexOf('.') > -1 || val.indexOf('e') > -1
                || val.indexOf('E') > -1 || "-0".equals(val);
    }

    private static boolean potentialPositiveNumberStartingAtIndex(String value,int index){
        if (index >= value.length()){
            return false;
        }
        return digitAtIndex(value, (value.charAt(index)=='.'?index+1:index));
    }

    private static boolean digitAtIndex(String value, int index){
        if (index >= value.length()){
            return false;
        }
        return value.charAt(index) >= '0' && value.charAt(index) <= '9';
    }

    /**
     * For a prospective number, remove the leading zeros
     * @param value prospective number
     * @return number without leading zeros
     */
    private static String removeLeadingZerosOfNumber(String value){
        if (value.equals("-")){return value;}
        boolean negativeFirstChar = (value.charAt(0) == '-');
        int counter = negativeFirstChar ? 1:0;
        while (counter < value.length()){
            if (value.charAt(counter) != '0'){
                if (negativeFirstChar) {return "-".concat(value.substring(counter));}
                return value.substring(counter);
            }
            ++counter;
        }
        if (negativeFirstChar) {return "-0";}
        return "0";
    }
}
