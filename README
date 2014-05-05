JSON in Java [package org.json]

Douglas Crockford
douglas@crockford.com

2011-02-02


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

The package compiles on Java 1.8.


JSONObject.java: The JSONObject can parse text from a String or a JSONTokener
to produce a map-like object. The object provides methods for manipulating its
contents, and for producing a JSON compliant object serialization.

JSONArray.java: The JSONObject can parse text from a String or a JSONTokener
to produce a vector-like object. The object provides methods for manipulating
its contents, and for producing a JSON compliant array serialization.

JSONTokener.java: The JSONTokener breaks a text into a sequence of individual
tokens. It can be constructed from a String, Reader, or InputStream.

JSONException.java: The JSONException is the standard exception type thrown
by this package.


JSONString.java: The JSONString interface requires a toJSONString method,
allowing an object to provide its own serialization.

JSONStringer.java: The JSONStringer provides a convenient facility for
building JSON strings.

JSONWriter.java: The JSONWriter provides a convenient facility for building
JSON text through a writer.


CDL.java: CDL provides support for converting between JSON and comma
delimited lists.

Cookie.java: Cookie provides support for converting between JSON and cookies.

CookieList.java: CookieList provides support for converting between JSON and
cookie lists.

HTTP.java: HTTP provides support for converting between JSON and HTTP headers.

HTTPTokener.java: HTTPTokener extends JSONTokener for parsing HTTP headers.

XML.java: XML provides support for converting between JSON and XML.

JSONML.java: JSONML provides support for converting between JSONML and XML.

XMLTokener.java: XMLTokener extends JSONTokener for parsing XML text.
