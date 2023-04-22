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

    public static List<Integer> FIT_SIZEList = new ArrayList<Integer>();

    public static Object getJSONObjectWithoutJSONArrays(String currJsonStr){
        JSONObject currJson = new JSONObject(currJsonStr);
        Iterator itr = currJson.keySet().iterator();

        while(itr.hasNext()) {
            String key = (String) itr.next();
            Object getValue = currJson.get(key);
            if(getValue instanceof  JSONArray){
                currJson.put(key, new JSONArray());
            } else if(getValue instanceof JSONObject) {
                Object rtnValue = getJSONObjectWithoutJSONArrays(getValue.toString());
                currJson.put(key, rtnValue);
            } else {
                currJson.put(key, getValue);
            }
        }

        return currJson;
    }

    public static int getFIT_SIZE(String originalJsonStr){
        JSONObject jo = (JSONObject)getJSONObjectWithoutJSONArrays(originalJsonStr);
        return jo.toString().getBytes().length;
    }

    public static Object funcDisassembling(String currJsonStr, int MAX_SIZE) {
        FIT_SIZEList.add(getFIT_SIZE(currJsonStr));
        int FIT_SIZE = Collections.max(FIT_SIZEList);
        JSONObject currJson = new JSONObject(currJsonStr);
        Iterator itr = currJson.keySet().iterator();

        while(itr.hasNext()) {
            String key = (String) itr.next();
            Object getValue = currJson.get(key);
            currJson.put(key, new JSONArray());

            if(getValue instanceof JSONArray) {
                List<Map<String, Object>> subList = JSONDivider.toList((JSONArray)getValue);
                JSONObject subJson = new JSONObject();
                subJson.put(key, subList);
                List<String> temp = funcDividingOrNot(subJson.toString(), key, MAX_SIZE-FIT_SIZE);
                for(String jsonStr : temp) {
                    Map<String, String> map = new HashMap<String, String>();
                    String ListKey = getUpperKeyString(key);
                    checkContainsListKey(key, ListKey, new JSONObject(jsonStr), map);
                    JsonStrMapList.add(map);
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
                JSONDivider.checkContainsKey(currJson, map, key);
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
        JSONObject json = new JSONObject(dividedJsonArrayStr);
        JSONArray dividedJa = json.getJSONArray(key);
        int dividedJaStrLength = dividedJa.toString().getBytes().length;
        json.put(key, new JSONArray());
        List<String> tempList = new ArrayList<String>();

        if(dividedJaStrLength > MAX_SIZE) {
            boolean isUnderMaxSize = false;
            int cnt = (int) Math.ceil(((double)dividedJaStrLength/MAX_SIZE));
            while(isUnderMaxSize==false){
                tempList = new ArrayList<String>();
                List<Map<String, Object>> list = JSONDivider.toList(dividedJa);
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
                for (int i=0; i<=tempList.size(); i++){
                    if(i == tempList.size()){
                        isUnderMaxSize = true; break;
                    }
                    if (tempList.get(i).getBytes().length > MAX_SIZE){
                        cnt += 1; break;
                    }
                }
            }
        } else {
            JSONObject subJson = new JSONObject();
            subJson.put(key, dividedJa);
            String sendingJsonStr = subJson.toString();
            tempList.add(sendingJsonStr);
        }

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

    public static List<String> divideJsonStr(String jsonStr
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
                System.out.println("dividedJsonStr : " + str);
                System.out.println("dividedJsonStr bytes length : " + str.getBytes().length);
            }
            System.out.println("MAX_SIZE : " + MAX_SIZE);
            System.out.println("originalJsonStr bytes length : " + jsonStr.getBytes().length);
            System.out.println("dividedJsonStrList size : " + JSONDivider.JsonStrArr.size());
        } // 3. logging

        return JSONDivider.JsonStrArr;
    }
}