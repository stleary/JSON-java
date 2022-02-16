# Milestone 3

Project milestone 3 assignment for 262P programming styles. This work for this assignment was done by both **Trent Lilley** and **Joseph Lee**.

## Simplifying Assumptions
- As of now the additional functions do not support JSON arrays.

## For the Graders
- A custom parse function derived from the original was used. The important changes to the original can be found in XML.java in lines 1489-1496 and lines 1524-1532.
- The tag name extracted from the XML is converted by the key transformer just before it is sent to the accumulator be transformed into JSON.
- 4 test cases were appended to the bottom of XMLTest.java

<hr />

# Milestone 2

Project milestone 2 assignment for 262P programming styles. This work for this assignment was done by both **Trent Lilley** and **Joseph Lee**.

## Simplifying Assumptions

- As of now the additional functions do not support JSON arrays and assumes keypaths are unique to a single key.

## Notes For the Grader

- All additional code was appended to the bottom of XML.java. All tests were appended to the bottom of XMLTest.java.
- The first method searches for and extracts the sub-object as XML and then runs this through the library's original XML parser.
- The second method uses an almost identical, slightly modified version of the library's XML parser. The crucial changes are denoted by comments prefixed with REPLACE:

<hr/>

![Json-Java logo](https://github.com/stleary/JSON-java/blob/master/images/JsonJava.png?raw=true)

<sub><sup>image credit: Ismael Pérez Ortiz</sup></sub>


JSON in Java [package org.json]
===============================

[![Maven Central](https://img.shields.io/maven-central/v/org.json/json.svg)](https://mvnrepository.com/artifact/org.json/json)

**[Click here if you just want the latest release jar file.](https://search.maven.org/remotecontent?filepath=org/json/json/20211205/json-20211205.jar)**


# Overview

[JSON](http://www.JSON.org/) is a light-weight language-independent data interchange format.

The JSON-Java package is a reference implementation that demonstrates how to parse JSON documents into Java objects and how to generate new JSON documents from the Java classes.

Project goals include:
* Reliable and consistent results
* Adherence to the JSON specification 
* Easy to build, use, and include in other projects
* No external dependencies
* Fast execution and low memory footprint
* Maintain backward compatibility
* Designed and tested to use on Java versions 1.6 - 1.11

The files in this package implement JSON encoders and decoders. The package can also convert between JSON and XML, HTTP headers, Cookies, and CDL.

The license includes this restriction: ["The software shall be used for good, not evil."](https://en.wikipedia.org/wiki/Douglas_Crockford#%22Good,_not_Evil%22) If your conscience cannot live with that, then choose a different package.

# If you would like to contribute to this project

For more information on contributions, please see [CONTRIBUTING.md](https://github.com/stleary/JSON-java/blob/master/docs/CONTRIBUTING.md)

Bug fixes, code improvements, and unit test coverage changes are welcome! Because this project is currently in the maintenance phase, the kinds of changes that can be accepted are limited. For more information, please read the [FAQ](https://github.com/stleary/JSON-java/wiki/FAQ).

# Build Instructions

The org.json package can be built from the command line, Maven, and Gradle. The unit tests can be executed from Maven, Gradle, or individually in an IDE e.g. Eclipse.
 
**Building from the command line**

*Build the class files from the package root directory src/main/java*
````
javac org/json/*.java
````

*Create the jar file in the current directory*
````
jar cf json-java.jar org/json/*.class
````

*Compile a program that uses the jar (see example code below)*
````
javac -cp .;json-java.jar Test.java (Windows)
javac -cp .:json-java.jar Test.java (Unix Systems)
````

*Test file contents*

````
import org.json.JSONObject;
public class Test {
    public static void main(String args[]){
       JSONObject jo = new JSONObject("{ \"abc\" : \"def\" }");
       System.out.println(jo.toString());
    }
}
````

*Execute the Test file*
```` 
java -cp .;json-java.jar Test (Windows)
java -cp .:json-java.jar Test (Unix Systems)
````

*Expected output*

````
{"abc":"def"}
````

 
**Tools to build the package and execute the unit tests**

Execute the test suite with Maven:
```
mvn clean test
```

Execute the test suite with Gradlew:

```
gradlew clean build test
```

# Notes

For more information, please see [NOTES.md](https://github.com/stleary/JSON-java/blob/master/docs/NOTES.md)

# Files

For more information on files, please see [FILES.md](https://github.com/stleary/JSON-java/blob/master/docs/FILES.md)

# Release history:

For the release history, please see [RELEASES.md](https://github.com/stleary/JSON-java/blob/master/docs/RELEASES.md)
