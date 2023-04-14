package org.json;

import java.util.*;

public class JSONDivider {

    /** ref Start - "Convert JSONObject/JSONArray to a Map/List", otep.tistory.com, https://otep.tistory.com/352 */
    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)));
        }
        return map;
    }

    public static List toList(JSONArray array) throws JSONException {
        List list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    private static Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return toMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }
    /** ref End - "Convert JSONObject/JSONArray to a Map/List", otep.tistory.com, https://otep.tistory.com/352 */

    public static List<String> JsonStrArr = new ArrayList<String>();

    public static List<Map<String, String>> JsonStrMapList = new ArrayList<Map<String, String>>();

    public static Object funcDisassembling(String currJsonStr, int MAX_SIZE) {
        JSONObject currJson = new JSONObject(currJsonStr);
        Iterator itr = currJson.keySet().iterator();

        while(itr.hasNext()) {
            String key = (String) itr.next();
            Object getValue = currJson.get(key);
            currJson.put(key, new JSONArray());

            if(getValue instanceof JSONArray) {
                List<Map<String, Object>> subList = JSONDivider.toList((JSONArray)getValue);
                if(subList.size() == 1){
                    Map<String, Object> innerOneMap = subList.get(0);
                    JSONObject innerOneJO = new JSONObject(innerOneMap);
                    Map<String, String> map = new HashMap<String, String>();
                    String ListKey = getUpperKeyString(key);
                    checkContainsListKey(key, ListKey, innerOneJO, map);
                    JsonStrMapList.add(map);
                    funcDisassembling(innerOneJO.toString(), MAX_SIZE);
                }else{
                    JSONObject subJson = new JSONObject();
                    subJson.put(key, subList);
                    List<String> temp = funcDividingOrNot(subJson.toString(), key, MAX_SIZE);
                    for(String jsonStr : temp) {
                        Map<String, String> map = new HashMap<String, String>();
                        String ListKey = getUpperKeyString(key);
                        checkContainsListKey(key, ListKey, new JSONObject(jsonStr), map);
                        JsonStrMapList.add(map);
                    }
                }
            } else if(getValue instanceof JSONObject) {
                Object rtnValue = funcDisassembling(getValue.toString(), MAX_SIZE);
                currJson.put(key, rtnValue);
            } else {
                currJson.put(key, getValue);
            }
        }
        return currJson;
    }

    public static void checkContainsListKey(String key, String ListKey, JSONObject jo, Map<String, String> map){
        if(key.equals("items")){
            map.put(ListKey, new JSONObject(jo.toString()).getJSONArray("items").toString());
        }else if(key.charAt(key.length()-1)=='s' && jo.get(key) instanceof JSONArray){
            map.put(ListKey, jo.getJSONArray(key).toString());
        }else{
            map.put(ListKey, jo.toString());
        }
    }

    public static String getUpperKeyString(String key){
        String returnStr = "items";
        if(!key.equals("items")){
            if(key.charAt(key.length() - 1) == 'y'){
                returnStr =  key.substring(0, key.length()-1).concat("ies"); //StringUtils.removeEnd(key, "y").concat("ies");
            }else if(key.charAt(key.length()-1) != 's'){
                returnStr = key + "s";
            }else{
                return key;
            }
        }
        return returnStr;
    }

    public static Object setParsedJsonObjects(String jsonStr, Map<String, String> map){
        JSONObject currJson = new JSONObject(jsonStr);
        Iterator itr = currJson.keySet().iterator();

        while(itr.hasNext()) {
            String key = (String) itr.next();
            Object getValue = currJson.get(key);
            currJson.put(key, new JSONArray());

            if(getValue instanceof JSONArray) {
                List<Map<String, Object>> subList = (List<Map<String, Object>>)JSONDivider.toList((JSONArray)getValue);
                if(subList.size() == 1){
                    Map<String, Object> innerOneMap = subList.get(0);
                    JSONObject innerOneJO = new JSONObject(innerOneMap);
                    setParsedJsonObjects(innerOneJO.toString(), map);
                    JSONDivider.checkContainsKey(currJson, map, key);
                }else{
                    JSONDivider.checkContainsKey(currJson, map, key);
                }
            } else if(getValue instanceof JSONObject) {
                Object rtnValue = setParsedJsonObjects(getValue.toString(), map);
                JSONDivider.checkContainsKey(currJson, map, key);
                if(!map.containsKey(key)){ currJson.put(key, rtnValue);}
            } else {
                JSONDivider.checkContainsKey(currJson, map, key);
                if(!map.containsKey(key)){ currJson.put(key, getValue);}
            }
        }
        return currJson;
    }

    public static void checkContainsKey(JSONObject currJson, Map<String, String> map, String key){
        if(map.containsKey(key)){
            if(key.equals("items")){
                currJson.put(key, new JSONArray(map.get(key)));
            }else if(key.charAt(key.length()-1)=='s' && map.get(key).startsWith("[")){
                currJson.put(key, new JSONArray(map.get(key)));
            }else{
                currJson.put(key, new JSONObject(map.get(key)));
            }
        }
    }

    public static List<String> funcDividingOrNot(String dividedJsonArrayStr
            , String key
            , int MAX_SIZE){
        int dividedJoStrLength = dividedJsonArrayStr.getBytes().length;
        JSONObject json = new JSONObject(dividedJsonArrayStr);
        List<String> tempList = new ArrayList<String>();
        JSONArray dividedJa = json.getJSONArray(key);
        json.put(key, new JSONArray());

        if(dividedJoStrLength > MAX_SIZE) {
            List<Map<String, Object>> list = JSONDivider.toList(dividedJa);
            int cnt = (int) Math.ceil(((double)dividedJoStrLength/MAX_SIZE));
            int size = list.size();
            int idx = 0;
            for(int i = 1; i<=cnt; i++) {
                List<Map<String, Object>> subList = new ArrayList<Map<String, Object>>();
                int eIdx = i==cnt ? size : idx+(size + 1) / cnt;
                subList = list.subList(idx, eIdx);
                idx = eIdx;
                JSONObject subJson = new JSONObject();
                subJson.put(key, subList);
                String sendingJsonStr = subJson.toString();
                tempList.add(sendingJsonStr);
            }
        } else {
            JSONObject subJson = new JSONObject();
            subJson.put(key, dividedJa);
            String sendingJsonStr = subJson.toString();
            tempList.add(sendingJsonStr);
        }

        Collections.sort(tempList);
        return tempList;
    }

    public static boolean isNotDivided(Map<String, String> m){
        boolean isNotDivided = false;

        String key = m.keySet().toArray()[0].toString();
        if(m.get(key).startsWith("[")){
            if(m.keySet().toArray().length == 1 && m.get(key).length() == 2)
            {isNotDivided = true;}
        }else if(m.get(key).startsWith("{")){
            JSONObject jo = new JSONObject(m.get(key));
            Map<String, Object> map = JSONDivider.toMap(jo);
            if(map.keySet().toArray().length == 1 && map.get(map.keySet().toArray()[0]).toString().length() == 2)
            {isNotDivided = true;}
        }

        return isNotDivided;
    }

    public static List<String> disassembleJsonStr(String jsonStr
            , int MAX_SIZE
            , boolean isLogging) throws Exception{

        JSONDivider.JsonStrArr = new ArrayList<String>();
        JSONDivider.JsonStrMapList = new ArrayList<Map<String, String>>(); // 1. setting

        JSONDivider.funcDisassembling(jsonStr, MAX_SIZE);
        for(Map<String, String> m : JSONDivider.JsonStrMapList){
            JSONObject jo = (JSONObject) JSONDivider.setParsedJsonObjects(jsonStr, m);
            if(JSONDivider.isNotDivided(m)){
                throw new Exception("dividedJsonStr : " + m.toString() + " => Please enlarge the MAX_SIZE. It's too small to divide your JSON string adequately.");
            }
            JSONDivider.JsonStrArr.add(jo.toString());
        } // 2. processing

        if(isLogging){
            for(String str : JSONDivider.JsonStrArr) {
                System.out.println("disassembledJsonStr : " + str);
                System.out.println("disassembledJsonStr bytes length : " + str.getBytes().length);
            }
            System.out.println("jsonStr bytes length : " + jsonStr.getBytes().length);
            System.out.println("JsonStrList size : " + JSONDivider.JsonStrArr.size());
            System.out.println("MAX_SIZE : " + MAX_SIZE); // 3. logging
        }

        return JSONDivider.JsonStrArr;
    }

}