package org.json.junit.data;

import java.util.Map;

public class CustomClassI {
    public Map<String, Map<String, Integer>> integerMap;

    public CustomClassI() {}
    public CustomClassI(Map<String, Map<String, Integer>> integerMap) {
        this.integerMap = integerMap;
    }
}
