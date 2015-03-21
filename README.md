# JSON-Java-unit-test
Unit tests to validate the JSON-Java GitHub project code (https://github.com/douglascrockford/JSON-java).<br>

Test harness: http://junit.org<br>
Coverage: http://www.eclemma.org/<br>

Eclipse is the recommended development environment.
Run individual tests or *JunitTestSuite* using *EclEmma Coverage*, or execute the _TestRunner_ application directly.<br>

Test filenames should consist of the name of the module being tested, with the suffix "Test". 
For example, *Cookie.java* is tested by *CookieTest.java*.
When adding a new unit test, don't forget to update *JunitTestSuite.java*.

The fundamental issues with JSON-Java testing are:
* *JSONObjects* are unordered, making simple string comparison ineffective. 
* Comparisons via equals() is not currently supported. Neither JSONArray nor JSONObject overrride hashCode() or equals(), so comparison defaults to the Object equals(), which is not useful.
* Access to the JSONArray and JSONObject internal containers for comparison is not currently available.
* JSONObject sometimes wraps entries in quotes, other times does not, complicating comparisons.

When you start working on a test, add the empty file to the repository, so that others will know that test is taken.

A unit test is considered complete when the coverage is >= 90% as measured by EclEmma.

| Test file name  | Coverage | Comments |
| ------------- | ------------- |
| CookieTest.java  | 97.5%   | Completed |
| PropertyTest.java  | 94.8%  | Completed |
| CDLTest.java | 94.8% | Relies too much on string tests, needs to be reworked |

<b>Completed tests:</b><br>
CDLTest.java<br>
CookieTest.java<br>
PropertyTest.java<br>
<b>In progress:</b><br>
XMLTest.java<br>


