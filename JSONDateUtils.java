package org.json;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

/**
 * Use where a date format is known to have colons the values and lack wrapping double quotes, such as "{date_time:2013-06-15 09:30:09.234}"
 * And the value will be correctly wrapped with double quotes.
 *
 * @author Amir Gur
 * @since 9/2/2014
 */

public class JSONDateUtils {

    public static String fixJSONTimeFormat(String text, String jsonName, SimpleDateFormat sdf) throws ParseException {
        int pos = text.indexOf(jsonName);
        if(pos < 0) {
            throw new ParseException("Can't find json name: " + jsonName, pos);
        }

        int p = pos + jsonName.length() + 1;
        Date date = sdf.parse(text, new ParsePosition(p));
        if(date == null) {
            throw new IllegalArgumentException("Can't parse date pattern: " + sdf.toPattern() + ", in text: " + text + ", at pos: " + pos );
        }
        String dateString = sdf.format(date);
        return text.replace(dateString, "\"" + dateString + "\"");
    }

    @Test
    public void testGetDate() throws ParseException {
        testOne("{date_time:2013-06-15 01:30:09.234}", "yyyy-MM-dd HH:mm:ss.SSS");
        testOne("{date_time:2013-06-15 02:30:09.234, abc:222}", "yyyy-MM-dd HH:mm:ss.SSS");
        testOne("{date_time:2013-06-15 03:30:09.234-0700}", "yyyy-MM-dd HH:mm:ss.SSSZ");
        testOne("{date_time:2013-06-15 04:30:09.234-0700, abc:222}", "yyyy-MM-dd HH:mm:ss.SSSZ");
        testOne("{ab: xyz, date_time:2013-06-15 05:30:09.234-0700}", "yyyy-MM-dd HH:mm:ss.SSSZ");
        testOne("{date_time:2013-06-15 06:30:09.234-0700, abc:222}", "yyyy-MM-dd HH:mm:ss.SSSZ");
        testOne("{date_time:         2013-06-15 07:30:09.234-0700, abc:222}", "yyyy-MM-dd HH:mm:ss.SSSZ");
        try {
            testOne("{date_time:\"2013-06-15 08:30:09.234-0700\", abc:222}", "yyyy-MM-dd HH:mm:ss.SSSZ");
        } catch(IllegalArgumentException e) {
            System.out.println("Exception for not legal input is expected here, since value starts with a double quote.");
        }
    }

    private void testOne(String jsonStr, String pattern) throws ParseException {
        String jsonName = "date_time";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String fixed = fixJSONTimeFormat(jsonStr, jsonName, sdf);
        JSONObject jsonObject = new JSONObject(fixed);
        Object value = jsonObject.get(jsonName);
        System.out.println("fixed: " + fixed + ", value: " + value);
    }

}
