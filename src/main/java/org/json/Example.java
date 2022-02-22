package org.json;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
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

    //System.out.println("--------------SOURCE--------------\n" + xmlString);

    JSONObject obj = XML.toJSONObject(xmlString2);
    System.out.println(obj.toString(4));

    System.out.println("--------------DEBUG--------------");
    System.out.println("--------------RESULT--------------");
    //Stream<Map.Entry<String, Object>> stream1 = obj.toStream();
    //Stream<Map.Entry<String, Object>> stream2 = obj.toStream();

    obj.toStream().forEach(System.out::println);
    System.out.println("KEYS");
    List<String> keys = obj.toStream().map(Map.Entry::getKey).collect(toList());
    System.out.println("VALS");
    List<Object> vals = obj.toStream().map(Map.Entry::getValue).collect(toList());


    System.out.println(keys);
    System.out.println(vals);



  }
}
