package org.json;


public class MyTest {

public static void main(String[] args) {
    String jsonStr = "{ \"Number1\" : 123.456 , \"Number2\" : [ 223.456 , 323.456 ] }";
    JSONObject json = new JSONObject(jsonStr);
    Float number1 = json.getFloat("Number1");
    System.out.println(json);
    JSONArray jsonArr = json.getJSONArray("Number2");
    System.out.println(jsonArr);
    System.out.println(number1);
    Float number2 = jsonArr.getFloat(0);
    System.out.println(number2);
    Float number3 = jsonArr.getFloat(1);
    System.out.println(number3);
}
}