package org.json.junit;

import java.util.*;

public class StringsResourceBundle extends ListResourceBundle {
    public Object[][] getContents() {
        return contents;
    }
    static final Object[][] contents = {
        {"greetings.hello", "Hello, "},
        {"greetings.world", "World!"},
        {"farewells.later", "Later, "},
        {"farewells.gator", "Alligator!"}
    };
}