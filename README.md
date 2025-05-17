![Json-Java logo](https://github.com/stleary/JSON-java/blob/master/images/JsonJava.png?raw=true)

<sub><sup>image credit: Ismael Pérez Ortiz</sup></sub>


JSON in Java [package org.json]
===============================

[![Maven Central](https://img.shields.io/maven-central/v/org.json/json.svg)](https://mvnrepository.com/artifact/org.json/json)
[![Java CI with Maven](https://github.com/stleary/JSON-java/actions/workflows/pipeline.yml/badge.svg)](https://github.com/stleary/JSON-java/actions/workflows/pipeline.yml)
[![CodeQL](https://github.com/stleary/JSON-java/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/stleary/JSON-java/actions/workflows/codeql-analysis.yml)

**[Click here if you just want the latest release jar file.](https://search.maven.org/remotecontent?filepath=org/json/json/20250517/json-20250517.jar)**


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
* Designed and tested to use on Java versions 1.6 - 21


The files in this package implement JSON encoders and decoders. The package can also convert between JSON and XML, HTTP headers, Cookies, and CDL.

# If you would like to contribute to this project

For more information on contributions, please see [CONTRIBUTING.md](https://github.com/stleary/JSON-java/blob/master/docs/CONTRIBUTING.md)

Bug fixes, code improvements, and unit test coverage changes are welcome! Because this project is currently in the maintenance phase, the kinds of changes that can be accepted are limited. For more information, please read the [FAQ](https://github.com/stleary/JSON-java/wiki/FAQ).

# Build Instructions

The org.json package can be built from the command line, Maven, and Gradle. The unit tests can be executed from Maven, Gradle, or individually in an IDE e.g. Eclipse.
 
**Building from the command line**

*Build the class files from the package root directory src/main/java*
```shell
javac org/json/*.java
```

*Create the jar file in the current directory*
```shell
jar cf json-java.jar org/json/*.class
```

*Compile a program that uses the jar (see example code below)*
```shell
javac -cp .;json-java.jar Test.java (Windows)
javac -cp .:json-java.jar Test.java (Unix Systems)
```

*Test file contents*

```java
import org.json.JSONObject;
public class Test {
    public static void main(String args[]){
       JSONObject jo = new JSONObject("{ \"abc\" : \"def\" }");
       System.out.println(jo);
    }
}
```

*Execute the Test file*
```shell 
java -cp .;json-java.jar Test (Windows)
java -cp .:json-java.jar Test (Unix Systems)
```

*Expected output*

```json
{"abc":"def"}
```

 
**Tools to build the package and execute the unit tests**

Execute the test suite with Maven:
```shell
mvn clean test
```

Execute the test suite with Gradlew:

```shell
gradlew clean build test
```

*Optional* Execute the test suite in strict mode with Gradlew:

```shell
gradlew testWithStrictMode
```

*Optional* Execute the test suite in strict mode with Maven:

```shell
mvn test -P test-strict-mode 
```

# Notes

For more information, please see [NOTES.md](https://github.com/stleary/JSON-java/blob/master/docs/NOTES.md)

# Files

For more information on files, please see [FILES.md](https://github.com/stleary/JSON-java/blob/master/docs/FILES.md)

# Release history:

For the release history, please see [RELEASES.md](https://github.com/stleary/JSON-java/blob/master/docs/RELEASES.md)
