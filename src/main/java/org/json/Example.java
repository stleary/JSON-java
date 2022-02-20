package org.json;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.json.JSONException;
import org.json.JSONPointer;
import org.json.JSONObject;
import org.json.XML;

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

    JSONObject obj = XML.toJSONObject(xmlString);
    System.out.println(obj.toString(4));

    System.out.println("--------------DEBUG--------------");
    obj.toStream().forEach(System.out::println);
    //System.out.println("--------------RESULT--------------");



  }
}
