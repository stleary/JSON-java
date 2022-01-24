package org.json;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class Main {
  public static void main(String[] args) throws IOException {
    System.out.println("-----------------------");

    String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
        "<contact>\n" +
        "  <nick>Crista </nick>\n" +
        "  <name>Crista Lopes</name>\n" +
        "  <address>\n" +
        "    <street>Ave of Nowhere</street>\n" +
        "    <zipcode>92614</zipcode>\n" +
        "  </address>\n" +
        "</contact>";

/*
    try {
      //JSONObject jobj = XML.toJSONObject(xmlString);
      JSONObject jobj = XML.toJSONObject(new StringReader(xmlString), new JSONPointer("/contact/address/street/"));
      System.out.println("-----------------------");
      System.out.println("result: " + jobj.toString(4));
    } catch (JSONException e) {
      System.out.println(e);
    }

 */

    try {
      JSONObject replacement = XML.toJSONObject("<street>Ave of the Arts</street>\n");
      System.out.println("Given replacement: " + replacement);
      JSONObject jobj = XML.toJSONObject(new StringReader(xmlString), new JSONPointer("/contact/address/street/"), replacement);
      System.out.println(jobj);
    } catch (JSONException e) {
      System.out.println(e);
    }



  }
}
