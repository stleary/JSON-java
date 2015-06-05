# JSON in Java [package org.json]

This package needs a new owner. I have not used it in over a decade, and I do 
not have time to maintain programs that I do not use.

If you think you can give this package a good home, please contact me.

Douglas Crockford
douglas@crockford.com

2015-02-06

## Introduction

JSON is a light-weight, language independent, data interchange format.
See http://www.JSON.org/

The files in this package implement JSON encoders/decoders in Java.
It also includes the capability to convert between JSON and XML, HTTP
headers, Cookies, and CDL.

This is a reference implementation. There is a large number of JSON packages
in Java. Perhaps someday the Java community will standardize on one. Until
then, choose carefully.

The license includes this restriction: "The software shall be used for good,
not evil." If your conscience cannot live with that, then choose a different
package.

## build manual

### General hint

This library needs jdk 1.8 in order to compile properly.

### Windows

#### Developer

 if you are using eclipse or intellij idea first execute

```
.\gradlew.bat eclipse
```

or

```
.\gradlew.bat idea
```

and point your IDE to this folder. If not you can just start right away by
editing the source code in src/main/java/. To build just follow the steps
below.

#### User

To build the jar and documentation just execute

```
.\gradlew.bat build
.\gradlew.bat javadoc
```

You will find the output in build/libs and build/docs.

To install json-java in the local maven repository use

```
.\gradlew.bat install
```

### Unix/Linux/Solaris/...

The following steps do not require that you have installed gradle on your
computer. However, if you have you can use "gradle" instead of "./gradlew".

#### Developer

If you are using eclipse or intellij idea first execute

```
./gradlew eclipse
```

or

```
./gradlew idea
```

and point your IDE to this folder. If not you can just start right away by
editing the source code in src/main/java/. To build just follow the steps
below.

#### User

To build the jar and documentation just execute

```
./gradlew build
./gradlew javadoc
```

You will find the output in build/libs and build/docs.

To install json-java in the local maven repository use

```
./gradlew install
```

## API explanation

### JSONObject.java

The JSONObject can parse text from a String or a JSONTokener
to produce a map-like object. The object provides methods for manipulating its
contents, and for producing a JSON compliant object serialization.

### JSONArray.java

The JSONObject can parse text from a String or a JSONTokener
to produce a vector-like object. The object provides methods for manipulating
its contents, and for producing a JSON compliant array serialization.

### JSONTokener.java

The JSONTokener breaks a text into a sequence of individual
tokens. It can be constructed from a String, Reader, or InputStream.

### JSONException.java

The JSONException is the standard exception type thrown
by this package.


### JSONString.java

The JSONString interface requires a toJSONString method,
allowing an object to provide its own serialization.

### JSONStringer.java

The JSONStringer provides a convenient facility for
building JSON strings.

### JSONWriter.java

The JSONWriter provides a convenient facility for building
JSON text through a writer.


### CDL.java

CDL provides support for converting between JSON and comma
delimited lists.

### Cookie.java

Cookie provides support for converting between JSON and cookies.

### CookieList.java

CookieList provides support for converting between JSON and
cookie lists.

### HTTP.java

HTTP provides support for converting between JSON and HTTP headers.

### HTTPTokener.java

HTTPTokener extends JSONTokener for parsing HTTP headers.

### XML.java

XML provides support for converting between JSON and XML.

### JSONML.java

JSONML provides support for converting between JSONML and XML.

### XMLTokener.java

XMLTokener extends JSONTokener for parsing XML text.
