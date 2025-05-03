# Milestone 2

For the Milestone 2 of SWE262P, 2 new functions were added:

```java
static JSONObject toJSONObject(Reader reader, JSONPointer path) 
static JSONObject toJSONObject(Reader reader, JSONPointer path, JSONObject replacement) 
```

The first one takes in 2 parameters, a `Reader` object which contains some XML input and a `JSONPointer` object that includes a json path for querying, and returns a `JSONObject` object which has the corresponding path, or throw an error if that path does not exist.

The first one takes in 3 parameters, a `Reader` object which contains some XML input and a `JSONPointer` object that includes a json path for querying, and a `JSONObject` for replacement. And returns a new `JSONObject` object which has the corresponding path replaced with the new given object, or throw an error if that path does not exist.

Both new functions are placed in the `XML.java` file.

The test cases of the functions are placed under the `org.json.junit.milestone2.tests` package, and to run the test case which deals with large file input, first create a folder named `xml_files` under the `/src/test/resources` path, and put the xml files for testing under this path.