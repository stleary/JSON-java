package org.json.junit;

import org.json.*;

/**
 * Used in testing when a JSONString is needed
 */
class MyJsonString implements JSONString {

    @Override
    public String toJSONString() {
        return "my string";
    }
}