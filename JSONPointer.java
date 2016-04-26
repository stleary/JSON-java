package org.json;

import static java.lang.String.format;
import static java.util.Collections.emptyList;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class JSONPointer {

    private List<String> refTokens;

    public JSONPointer(String pointer) {
        if (pointer == null) {
            throw new NullPointerException("pointer cannot be null");
        }
        if (pointer.isEmpty()) {
            refTokens = emptyList();
            return;
        }
        if (pointer.startsWith("#/")) {
            pointer = pointer.substring(2);
            try {
                pointer = URLDecoder.decode(pointer, "utf-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else if (pointer.startsWith("/")) {
            pointer = pointer.substring(1);
        } else {
            throw new IllegalArgumentException("a JSON pointer should start with '/' or '#/'");
        }
        refTokens = new ArrayList<String>();
        for (String token : pointer.split("/")) {
            refTokens.add(unescape(token));
        }
    }

    private String unescape(String token) {
        return token.replace("~1", "/").replace("~0", "~")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    public Object queryFrom(JSONObject document) {
        if (refTokens.isEmpty()) {
            return document;
        }
        Object current = document;
        for (String token : refTokens) {
            if (current instanceof JSONObject) {
                current = ((JSONObject) current).opt(unescape(token));
            } else if (current instanceof JSONArray) {
                current = readByIndexToken(current, unescape(token));
            }
        }
        return current;
    }

    private Object readByIndexToken(Object current, String indexToken) {
        try {
            return ((JSONArray) current).opt(Integer.parseInt(unescape(indexToken)));
        } catch (NumberFormatException e) {
            throw new JSONPointerException(format("%s is not an array index", unescape(indexToken)), e);
        }
    }

}
