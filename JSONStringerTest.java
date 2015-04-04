package org.json.junit;

import org.json.*;
import org.junit.Test;


/**
 * Tests for JSON-Java JSONStringer.java
 */
public class JSONStringerTest {

    @Test(expected=JSONException.class)
    public void nullKeyException() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object();
        jsonStringer.key(null);
    }

    @Test(expected=JSONException.class)
    public void outOfSequenceException() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.key("hi");
    }

    @Test(expected=JSONException.class)
    public void missplacedArrayException() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object().endObject();
        jsonStringer.array();
    }

    @Test(expected=JSONException.class)
    public void missplacedEndArrayException() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object();
        jsonStringer.endArray();
    }

    @Test(expected=JSONException.class)
    public void missplacedEndObjectException() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.array();
        jsonStringer.endObject();
    }

    @Test(expected=JSONException.class)
    public void missplacedObjectException() {
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object().endObject();
        jsonStringer.object();
    }

    @Test(expected=JSONException.class)
    public void exceedNestDepthException() {
        new JSONStringer().object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object().
        key("k").object().key("k").object().key("k").object().key("k").object().key("k").object();
    }

    @Test
    public void simpleObjectString() {
        String expectedStr = 
            "{"+
                "\"trueValue\":true,"+
                "\"falseValue\":false,"+
                "\"nullValue\":null,"+
                "\"stringValue\":\"hello world!\","+
                "\"complexStringValue\":\"h\be\tllo w\u1234orld!\","+
                "\"intValue\":42,"+
                "\"doubleValue\":-23.45e67"+
            "}";
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object();
        jsonStringer.key("trueValue").value(true);
        jsonStringer.key("falseValue").value(false);
        jsonStringer.key("nullValue").value(null);
        jsonStringer.key("stringValue").value("hello world!");
        jsonStringer.key("complexStringValue").value("h\be\tllo w\u1234orld!");
        jsonStringer.key("intValue").value(42);
        jsonStringer.key("doubleValue").value(-23.45e67);
        jsonStringer.endObject();
        String str = jsonStringer.toString();
        JSONObject jsonObject = new JSONObject(str);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

    @Test
    public void simpleArrayString() {
        String expectedStr = 
            "["+
                "true,"+
                "false,"+
                "null,"+
                "\"hello world!\","+
                "42,"+
                "-23.45e67"+
            "]";
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.array();
        jsonStringer.value(true);
        jsonStringer.value(false);
        jsonStringer.value(null);
        jsonStringer.value("hello world!");
        jsonStringer.value(42);
        jsonStringer.value(-23.45e67);
        jsonStringer.endArray();
        String str = jsonStringer.toString();
        JSONArray jsonArray = new JSONArray(str);
        JSONArray expectedJsonArray = new JSONArray(expectedStr);
        Util.compareActualVsExpectedJsonArrays(jsonArray, expectedJsonArray);
    }

    @Test
    public void complexObjectString() {
        String expectedStr = 
            "{"+
                "\"trueValue\":true,"+
                "\"falseValue\":false,"+
                "\"nullValue\":null,"+
                "\"stringValue\":\"hello world!\","+
                "\"object2\":{"+
                    "\"k1\":\"v1\","+
                    "\"k2\":\"v2\","+
                    "\"k3\":\"v3\","+
                    "\"array1\":["+
                        "1,"+
                        "2,"+
                        "{"+
                            "\"k4\":\"v4\","+
                            "\"k5\":\"v5\","+
                            "\"k6\":\"v6\","+
                            "\"array2\":["+
                                "5,"+
                                "6,"+
                                "7,"+
                                "8"+
                            "]"+
                        "},"+
                        "3,"+
                        "4"+
                    "]"+
                "},"+
                "\"complexStringValue\":\"h\be\tllo w\u1234orld!\","+
                "\"intValue\":42,"+
                "\"doubleValue\":-23.45e67"+
            "}";
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object();
        jsonStringer.key("trueValue").value(true);
        jsonStringer.key("falseValue").value(false);
        jsonStringer.key("nullValue").value(null);
        jsonStringer.key("stringValue").value("hello world!");
        jsonStringer.key("object2").object();
        jsonStringer.key("k1").value("v1");
        jsonStringer.key("k2").value("v2");
        jsonStringer.key("k3").value("v3");
        jsonStringer.key("array1").array();
        jsonStringer.value(1);
        jsonStringer.value(2);
        jsonStringer.object();
        jsonStringer.key("k4").value("v4");
        jsonStringer.key("k5").value("v5");
        jsonStringer.key("k6").value("v6");
        jsonStringer.key("array2").array();
        jsonStringer.value(5);
        jsonStringer.value(6);
        jsonStringer.value(7);
        jsonStringer.value(8);
        jsonStringer.endArray();
        jsonStringer.endObject();
        jsonStringer.value(3);
        jsonStringer.value(4);
        jsonStringer.endArray();
        jsonStringer.endObject();
        jsonStringer.key("complexStringValue").value("h\be\tllo w\u1234orld!");
        jsonStringer.key("intValue").value(42);
        jsonStringer.key("doubleValue").value(-23.45e67);
        jsonStringer.endObject();
        String str = jsonStringer.toString();
        JSONObject jsonObject = new JSONObject(str);
        JSONObject expectedJsonObject = new JSONObject(expectedStr);
        Util.compareActualVsExpectedJsonObjects(jsonObject, expectedJsonObject);
    }

}
