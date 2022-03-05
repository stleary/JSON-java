package org.json;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Stream;

import org.json.JSONException;
import org.json.JSONPointer;
import org.json.JSONObject;
import org.json.XML;

import static java.util.stream.Collectors.toList;

public class Example {
  public static void main(String[] args) {
    String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
        "<contact>\n"+
        "  <nick>Crista </nick>\n"+
        "  <name>Crista Lopes</name>\n" +
        "  <address>\n" +
        "    <street>Ave of Nowhere</street>\n" +
        "    <zipcode>92614</zipcode>\n" +
        "  </address>\n" +
        "</contact>";

    String xmlString2 = "<Books><book><title>AAA</title><author>ASmith</author></book><book><title>BBB</title><author>BSmith</author></book></Books>";

    String jsonString1 = "{\"Books\": {\"book\": [\n" +
        "    {\n" +
        "        \"author\": \"ASmith\",\n" +
        "        \"title\": \"AAA\"\n" +
        "    },\n" +
        "    {\n" +
        "        \"author\": \"BSmith\",\n" +
        "        \"title\": \"BBB\"\n" +
        "    }\n" +
        "]}}";

    String jsonString2 =
        "{" +
            "\"key1\":" +
            "[1,2," +
            "{\"key3\":true}" +
            "]," +
            "\"key2\":" +
            "{\"key1\":\"val1\",\"key2\":" +
            "{\"key2\":null}," +
            "\"key3\":42" +
            "}," +
            "\"key4\":" +
            "[" +
            "[\"value1\",2.1]" +
            "," +
            "[null]" +
            "]" +
            "}";


    // Function passed as 2nd arg, can be anything that takes a JSONObject and returns a JSONObject
    Function<JSONObject, JSONObject> toLowercase = (obj) -> {
      String JsonStr = obj.toString().toLowerCase();
      return new JSONObject(JsonStr);
    };

    // Milestone 5 Method returns a future that the client must get
    Future<JSONObject> futureObj = XML.toJSONObjectAsync(new StringReader(xmlString), toLowercase);

    // Get the future and put it into a JSONObject instance
    // Waits 5 seconds to retrieve the data, otherwise goes to TimeoutException
    try {
      JSONObject obj = futureObj.get(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();
    }



    /*
    //System.out.println("--------------SOURCE--------------\n" + xmlString);

    JSONObject obj = XML.toJSONObject(xmlString2);
    System.out.println(obj.toString(4));
    System.out.println("--------------RESULT--------------");

    obj.toStream().forEach(System.out::println);
    System.out.println("\nKEYS");
    List<String> keys = obj.toStream().map(Map.Entry::getKey).collect(toList());
    System.out.println("\nVALS");
    List<Object> vals = obj.toStream().map(Map.Entry::getValue).collect(toList());

    System.out.println(keys);
    System.out.println(vals);

     */



  }
}
