package org.json.junit;

import org.json.JSONDivider;
import org.json.JSONObject;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * You can get details about JSONDivider in /JSON-java/docs/JSONDivider/README.md
 *
 * First of all, JSONDivider take off JSONArray from original JSONObject if it has more length then MAX_SIZE.
 * The divided target would be JSONArray. if your JSONObject has multiple JSONArray, JSONDivider examine each length of them.
 * If a JSONArray has more length then MAX_SIZE, JSONDivider would divide it.
 * Divided JSONObjects whould maintain original structure but it's inside JSONArray(s) may be divided.
 * If you want use it appropriately and want to get benefit from it,
 * !! JSONDivider need specific JSONObject structure, not random one. !!
 * */
public class JSONDividerTest {

    /**
     * JSONDivider can divide a JSON String which has structure like below.
     * - {"items":{"Products":{"Product":[{"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}]}}}
     * */
    @Test
    public void testAItems(){
        try {
            int MAX_SIZE = 100000;
            String AItems = new String(Files.readAllBytes(Paths.get("docs/JSONDivider/AItems.json"))).toString();
            JSONObject AJson = new JSONObject(AItems);

            List<String> strList = JSONDivider.divideJsonStr(AJson.toString(), MAX_SIZE, true);

            for(String jsonStr : strList){
                assertTrue("It is resized under MAX_SIZE you give.", jsonStr.getBytes().length < MAX_SIZE);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * JSONDivider can divide a JSON String which has structure like below.
     * - {"items":[{"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}]}
     * */
    @Test
    public void testBItems(){
        try {
            int MAX_SIZE = 100000;
            String BItems = new String(Files.readAllBytes(Paths.get("docs/JSONDivider/BItems.json"))).toString();
            JSONObject BJson = new JSONObject(BItems);

            List<String> strList = JSONDivider.divideJsonStr(BJson.toString(), MAX_SIZE, true);

            for(String jsonStr : strList){
                assertTrue("It is divided under MAX_SIZE you give.", jsonStr.getBytes().length < MAX_SIZE);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
