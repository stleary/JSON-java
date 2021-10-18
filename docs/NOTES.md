# Notes

**Recent directory structure change**

_Due to a recent commit - [#515 Merge tests and pom and code](https://github.com/stleary/JSON-java/pull/515) - the structure of the project has changed from a flat directory containing all of the Java files to a directory structure that includes unit tests and several tools used to build the project jar and run the unit tests. If you have difficulty using the new structure, please open an issue so we can work through it._

**Implementation notes**

Numeric types in this package comply with
[ECMA-404: The JSON Data Interchange Format](http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf) and
[RFC 8259: The JavaScript Object Notation (JSON) Data Interchange Format](https://tools.ietf.org/html/rfc8259#section-6).
This package fully supports `Integer`, `Long`, and `Double` Java types. Partial support
for `BigInteger` and `BigDecimal` values in `JSONObject` and `JSONArray` objects is provided
in the form of `get()`, `opt()`, and `put()` API methods.

Although 1.6 compatibility is currently supported, it is not a project goal and might be
removed in some future release.

In compliance with RFC8259 page 10 section 9, the parser is more lax with what is valid
JSON then the Generator. For Example, the tab character (U+0009) is allowed when reading
JSON Text strings, but when output by the Generator, the tab is properly converted to \t in
the string. Other instances may occur where reading invalid JSON text does not cause an
error to be generated. Malformed JSON Texts such as missing end " (quote) on strings or
invalid number formats (1.2e6.3) will cause errors as such documents can not be read
reliably.

Some notable exceptions that the JSON Parser in this library accepts are:
* Unquoted keys `{ key: "value" }`
* Unquoted values `{ "key": value }`
* Unescaped literals like "tab" in string values `{ "key": "value   with an unescaped tab" }`
* Numbers out of range for `Double` or `Long` are parsed as strings

Recent pull requests added a new method `putAll` on the JSONArray. The `putAll` method
works similarly to other `put` methods in that it does not call `JSONObject.wrap` for items
added. This can lead to inconsistent object representation in JSONArray structures.

For example, code like this will create a mixed JSONArray, some items wrapped, others
not:

```java
SomeBean[] myArr = new SomeBean[]{ new SomeBean(1), new SomeBean(2) };
// these will be wrapped
JSONArray jArr = new JSONArray(myArr);
// these will not be wrapped
jArr.putAll(new SomeBean[]{ new SomeBean(3), new SomeBean(4) });
```

For structure consistency, it would be recommended that the above code is changed
to look like 1 of 2 ways.

Option 1:
```Java
SomeBean[] myArr = new SomeBean[]{ new SomeBean(1), new SomeBean(2) };
JSONArray jArr = new JSONArray();
// these will not be wrapped
jArr.putAll(myArr);
// these will not be wrapped
jArr.putAll(new SomeBean[]{ new SomeBean(3), new SomeBean(4) });
// our jArr is now consistent.
```

Option 2:
```Java
SomeBean[] myArr = new SomeBean[]{ new SomeBean(1), new SomeBean(2) };
// these will be wrapped
JSONArray jArr = new JSONArray(myArr);
// these will be wrapped
jArr.putAll(new JSONArray(new SomeBean[]{ new SomeBean(3), new SomeBean(4) }));
// our jArr is now consistent.
```

**Unit Test Conventions**

Test filenames should consist of the name of the module being tested, with the suffix "Test". 
For example, <b>Cookie.java</b> is tested by <b>CookieTest.java</b>.

<b>The fundamental issues with JSON-Java testing are:</b><br>
* <b>JSONObjects</b> are unordered, making simple string comparison ineffective. 
* Comparisons via **equals()** is not currently supported. Neither <b>JSONArray</b> nor <b>JSONObject</b> override <b>hashCode()</b> or <b>equals()</b>, so comparison defaults to the <b>Object</b> equals(), which is not useful.
* Access to the <b>JSONArray</b> and <b>JSONObject</b> internal containers for comparison is not currently available.

<b>General issues with unit testing are:</b><br>
* Just writing tests to make coverage goals tends to result in poor tests. 
* Unit tests are a form of documentation - how a given method works is demonstrated by the test. So for a code reviewer or future developer looking at code a good test helps explain how a function is supposed to work according to the original author. This can be difficult if you are not the original developer.
*   It is difficult to evaluate unit tests in a vacuum. You also need to see the code being tested to understand if a test is good. 
* Without unit tests, it is hard to feel confident about the quality of the code, especially when fixing bugs or refactoring. Good tests prevent regressions and keep the intent of the code correct.
* If you have unit test results along with pull requests, the reviewer has an easier time understanding your code and determining if it works as intended.