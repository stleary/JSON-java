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
 * Sent as a pull request at: {@link "https://github.com/douglascrockford/JSON-java/pull/103"}
 *
 * @author Amir Gur
 * @since 9/2/2014
 */

public class JSONDateUtils {

    public static String fixJSONTimeFormat(String text, SimpleDateFormat sdf, String[] jsonNames) throws ParseException {
        String ret = null;
        for(String name : jsonNames) {
            ret = fixJSONTimeFormat(ret == null ? text : ret, sdf, name);
        }
        return ret;
    }

    public static String fixJSONTimeFormat(String text, SimpleDateFormat sdf, String jsonName) throws ParseException {
        int pos = text.indexOf(jsonName);
        if(pos < 0) {
            throw new ParseException("Can't find json name: " + jsonName, pos);
        }

        int p = pos + jsonName.length() + 1;
        Date date = sdf.parse(text, new ParsePosition(p));
        if(date == null) {
            throw new IllegalArgumentException("Can't parse date pattern: " + sdf.toPattern() + ", in text: " + text + ", at pos: " + p);
        }
        String dateString = sdf.format(date);
        String relevantString = text.substring(p);
        String prefix = text.substring(0, p);
        return prefix + relevantString.replaceFirst(dateString, "\"" + dateString + "\"");
    }

    @Test
    public void testGetDate() throws ParseException {
        testOne("{date_time:2013-06-15 01:30:09.234}", "yyyy-MM-dd HH:mm:ss.SSS", "date_time");
        testOne("{date_time:2013-06-15 02:30:09.234, abc:222}", "yyyy-MM-dd HH:mm:ss.SSS", "date_time");
        testOne("{date_time:2013-06-15 03:30:09.234-0700}", "yyyy-MM-dd HH:mm:ss.SSSZ", "date_time");
        testOne("{date_time:2013-06-15 04:30:09.234-0700, abc:222}", "yyyy-MM-dd HH:mm:ss.SSSZ", "date_time");
        testOne("{ab: xyz, date_time:2013-06-15 05:30:09.234-0700}", "yyyy-MM-dd HH:mm:ss.SSSZ", "date_time");
        testOne("{date_time:2013-06-15 06:30:09.234-0700, abc:222}", "yyyy-MM-dd HH:mm:ss.SSSZ", "date_time");
        testOne("{date_time: 2013-06-15 07:30:09.234-0700, abc:222}", "yyyy-MM-dd HH:mm:ss.SSSZ", "date_time");
        try {
            testOne("{date_time:\"2013-06-15 08:30:09.234-0700\", abc:222}", "yyyy-MM-dd HH:mm:ss.SSSZ", "date_time");
        } catch(IllegalArgumentException e) {
            System.out.println("Exception for not legal input is expected here, since value starts with a double quote.");
        }
        testFew("{date_time:2013-06-15 09:30:09.234-0700, abc:222, date2_time:2013-06-15 09:30:09.234-0700}", "yyyy-MM-dd HH:mm:ss.SSSZ", new String[]{"date_time", "date2_time"});
        testFew("{date_time:2013-06-15 10:30:09.234-0700, abc:222, date2_time:2013-06-15 11:30:09.234-0700}", "yyyy-MM-dd HH:mm:ss.SSSZ", new String[]{"date_time", "date2_time"});
        testFew("{date_time:2013-06-15 12:30:09.234-0700, abc:222, date_time2:2013-06-15 12:30:09.234-0700}", "yyyy-MM-dd HH:mm:ss.SSSZ", new String[]{"date_time", "date_time2"});
        testFew("{date_time:2013-06-15 13:30:09.234-0700, abc:222, date_time2:2013-06-15 14:30:09.234-0700}", "yyyy-MM-dd HH:mm:ss.SSSZ", new String[]{"date_time", "date_time2"});
    }

    private void testOne(String jsonStr, String pattern, String jsonName) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String fixed = fixJSONTimeFormat(jsonStr, sdf, jsonName);
        JSONObject jsonObject = new JSONObject(fixed);
        Object value = jsonObject.get(jsonName);
        System.out.println("fixed: " + fixed + ", value: " + value);
    }

    private void testFew(String jsonStr, String pattern, String[] jsonNames) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String fixed = fixJSONTimeFormat(jsonStr, sdf, jsonNames);
        JSONObject jsonObject = new JSONObject(fixed);
        for(String jsonName : jsonNames) {
            Object value = jsonObject.get(jsonName);
            System.out.println("fixed: " + fixed + ", value: " + value);
        }
    }
}
