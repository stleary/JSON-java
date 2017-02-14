package org.json.junit;

import static org.junit.Assert.*;

import java.util.*;

import org.json.*;
import org.junit.*;

/**
 * Note: This file is saved as UTF-8. Do not save as ASCII or the tests will
 * fail.
 *
 */
public class JSONObjectLocaleTest {
    /**
     * JSONObject built from a bean with locale-specific keys - that is, the key
     * fields are not LANG_ENGLISH.
     */
    @Test
    public void jsonObjectByLocaleBean() {

        MyLocaleBean myLocaleBean = new MyLocaleBean();

        Locale.setDefault(new Locale("en"));
        JSONObject jsonen = new JSONObject(myLocaleBean);
        System.out.println("jsonen " + jsonen);

        Locale.setDefault(new Locale("tr"));
        JSONObject jsontr = new JSONObject(myLocaleBean);
        System.out.println("jsontr " + jsontr);
        /**
         * In this test we exercise code that handles keys of 1-char and
         * multi-char length that include text from a non-English locale.
         * Turkish in this case. The JSONObject code should correctly retain the
         * non-EN_LANG chars in the key.
         */
        assertTrue("expected beanId",
                "Tlocaleüx".equals(jsonObject.getString("")));
        assertTrue("expected Tlocalü",
                "Tlocaleü".equals(jsonObject.getString("ü")));
        assertTrue("expected Tlocaleüx",
                "Tlocaleüx".equals((String)(jsonObject.query("/üx"))));
        assertTrue("expected Tlocalü",
                "Tlocaleü".equals((String)(jsonObject.query("/ü"))));
    }

}
