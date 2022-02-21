package org.json;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONPointer;
import org.json.JSONObject;
import org.json.XML;

public class ExampleStream {
    public static void main(String[] args) {
        JSONObject obj = XML.toJSONObject(
                "<Books>" +
                        "<book>" +
                            "<title>AAA</title>" +
                            "<author>ASmith</author>" +
                        "</book>" +
                        "<book>" +
                            "<title>BBB</title>" +
                            "<author>BSmith</author>" +
                        "</book>" +
                        "<book>" +
                        "<title>CCC</title>" +
                        "<author>CSmith</author>" +
                        "</book>" +
                        "<book>" +
                        "<title>DDD</title>" +
                        "<author>DSmith</author>" +
                        "</book>" +
                        "</Books>"
        );

        System.out.println("EX 1:");
        JSONPointer ptr = new JSONPointer("/Books/book");
        obj.toStream().forEach(node -> System.out.println("Node: " + node.toString()));

        System.out.println("EX 1.2:");
        obj.toStream().filter(node -> node.has("author"))
        .forEach(node -> node.put("Rating", "TBD"));
        System.out.println(obj.toString(4));
//

        System.out.println("EX 2:");
        List<String> titles = obj.toStream()
                .filter(node -> node.has("title"))
                .map(node -> node.get("title").toString())
                .collect(Collectors.toList());

        System.out.println("Titles List:");
        titles.forEach(name -> System.out.println(name));

        System.out.println("EX 3:");
        obj.toStream()
                .filter(node -> node.has("title"))
                .forEach(node -> node.put("title", "new_" + node.get("title")));

        System.out.println("Modified Titles:");
        System.out.println(obj.toString(4));


//    obj.toStream().forEach(node -> do some transformation, possibly based on the path of the node);
//    List<String> titles = obj.toStream().map(node -> extract value for key "title").collect(Collectors.toList());
//      obj.toStream().filter(node -> node with certain properties).forEach(node -> do some transformation);
    }
}
