package org.json.junit.milestone2.tests;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.XML;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

public class XMLJsonPointerQueryingTest {
    @Test
    public void testXML() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
                "<contact>\n"+
                "  <nick>Crista </nick>\n"+
                "  <name>Crista Lopes</name>\n" +
                "  <address>\n" +
                "    <street>Ave of Nowhere</street>\n" +
                "    <zipcode>92614</zipcode>\n" +
                "  </address>\n" +
                "</contact>";

        try {
            // notice that there cannot be a '/' at the end
            JSONObject jobj = XML.toJSONObject(new StringReader(xmlString), new JSONPointer("/contact/address/street"));
            System.out.println(jobj);
        } catch (JSONException e) {
            System.out.println(e);
        }
    }

    @Test
    public void testXMLWithArray() {
        String xml =
                "<catalog>" +
                        "<book id='bk101'><title>XML Developer's Guide</title></book>" +
                        "<book id='bk102'><title>Midnight Rain</title></book>" +
                        "</catalog>";

        try (Reader reader = new StringReader(xml)) {
            JSONPointer ptr = new JSONPointer("/catalog/book/1");
            JSONObject node = XML.toJSONObject(reader, ptr);
            System.out.println(node);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPointerWithResourceFile() throws Exception {
        // test performance on large xml files
        // this program is expected to finish early due to early appearance of requested path
        try (Reader reader = new InputStreamReader(
                getClass().getResourceAsStream("/xml_files/large_file.xml"))) {

            JSONPointer ptr = new JSONPointer("/mediawiki/siteinfo/namespaces");
            JSONObject node = XML.toJSONObject(reader, ptr);
            System.out.println(node);
        }
    }

    @Test
    public void testXMLWithNonexistentPath() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
                "<contact>\n"+
                "  <nick>Crista </nick>\n"+
                "  <name>Crista Lopes</name>\n" +
                "  <address>\n" +
                "    <street>Ave of Nowhere</street>\n" +
                "    <zipcode>92614</zipcode>\n" +
                "  </address>\n" +
                "</contact>";

        try {
            // zipcode is not a sub-path of /contact/address/street/, thus should throw an error
            JSONObject jobj = XML.toJSONObject(new StringReader(xmlString), new JSONPointer("/contact/address/street/zipcode"));
            System.out.println(jobj);
        } catch (JSONException e) {
            System.out.println(e);
        }
    }
}
