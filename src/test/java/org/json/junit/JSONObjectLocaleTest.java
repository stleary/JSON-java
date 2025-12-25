package org.json.junit;

/*
Public Domain.
*/

import static org.junit.Assert.*;

import java.util.*;

import org.json.*;
import org.json.junit.data.MyLocaleBean;
import org.junit.*;

/**
 * Note: This file is saved as UTF-8. Do not save as ASCII or the tests will
 * fail.
 *
 */
public class JSONObjectLocaleTest {
    /**
     * JSONObject built from a bean with locale-specific keys.
     * In the Turkish alphabet, there are 2 versions of the letter "i".
     * 'eh' I ı (dotless i)
     * 'ee' İ i (dotted i)
     * A problem can occur when parsing the public get methods for a bean.
     * If the method starts with getI... then the key name will be lowercased
     * to 'i' in English, and 'ı' in Turkish.
     * We want the keys to be consistent regardless of locale, so JSON-Java
     * lowercase operations are made to be locale-neutral by specifying
     * Locale.ROOT. This causes 'I' to be universally lowercased to 'i'
     * regardless of the locale currently in effect.
     */
    @Test
    public void jsonObjectByLocaleBean() {

        MyLocaleBean myLocaleBean = new MyLocaleBean();

        // save and restore the current default locale, to avoid any side effects on other executions in the same JVM
        Locale defaultLocale = Locale.getDefault();
        try {
            /**
             * This is just the control case which happens when the locale.ROOT
             * lowercasing behavior is the same as the current locale.
             */
            Locale.setDefault(new Locale("en"));
            JSONObject jsonen = new JSONObject(myLocaleBean);
            assertEquals("expected size 2, found: " +jsonen.length(), 2, jsonen.length());
            assertEquals("expected jsonen[i] == beanI", "beanI", jsonen.getString("i"));
            assertEquals("expected jsonen[id] == beanId", "beanId", jsonen.getString("id"));

            /**
             * Without the JSON-Java change, these keys would be stored internally as
             * starting with the letter, 'ı' (dotless i), since the lowercasing of
             * the getI and getId keys would be specific to the Turkish locale.
             */
            Locale.setDefault(new Locale("tr"));
            JSONObject jsontr = new JSONObject(myLocaleBean);
            assertEquals("expected size 2, found: " +jsontr.length(), 2, jsontr.length());
            assertEquals("expected jsontr[i] == beanI", "beanI", jsontr.getString("i"));
            assertEquals("expected jsontr[id] == beanId", "beanId", jsontr.getString("id"));
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }
}
