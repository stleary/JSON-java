package org.json;

import java.io.IOException;
import java.io.StringReader;
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

    System.out.println("--------------SOURCE--------------\n" + xmlString);

    System.out.println("--------------DEBUG--------------");
    try {
      //JSONObject jobj = XML.toJSONObject(new StringReader(xmlString), new JSONPointer("/contact/address/street/"));
      Function<String, String> transformer = (str) -> {
        str += "_K";
        return str;
      };

      JSONObject jobj = XML.toJSONObject(new StringReader(xmlString), transformer);
      //JSONObject jobj = XML.toJSONObject(xmlString);

      System.out.println("--------------RESULT--------------");
      System.out.println(jobj.toString(4));
    } catch (JSONException e) {System.out.println(e);}
  }
}
