# JSON-Java-unit-test

Unit tests to validate the JSON-Java GitHub project code (https://github.com/douglascrockford/JSON-java).<br>

Test harness: http://junit.org<br>
Coverage: http://www.eclemma.org/<br>

Eclipse is the recommended development environment.
Run individual tests or <b>JunitTestSuite</b> using *EclEmma Coverage*, or execute the <b>TestRunner<b> application directly.<br>

Test filenames should consist of the name of the module being tested, with the suffix "Test". 
For example, <b>Cookie.java</b> is tested by <b>CookieTest.java</b>.
When adding a new unit test, don't forget to update <b>JunitTestSuite.java</b>.

The fundamental issues with JSON-Java testing are:
* <b>JSONObjects</b> are unordered, making simple string comparison ineffective. 
* Comparisons via **equals()** is not currently supported. Neither <b>JSONArray</b> nor <b>JSONObject</b> overrride <b>hashCode()</b> or <b>equals()</b>, so comparison defaults to the <b>Object</b> equals(), which is not useful.
* Access to the <b>JSONArray</b> and <b>JSONObject</b> internal containers for comparison is not currently available.
* <b>JSONObject</b> sometimes wraps entries in quotes, other times does not, complicating comparisons.

When you start working on a test, add the empty file to the repository and update the readme, so that others will know that test is taken.

A unit test is considered complete when the coverage is >= 90% as measured by EclEmma.

| Test file name  | Coverage | Comments |
| ------------- | ------------- | ---- |
| Total coverage | 88.7% | | | 
| | | | 
| CDLTest.java | 94.8% | Completed  |
| CookieTest.java  | 97.5%   | Completed |
| CookieListTest.java |96.5% | Completed |
| HTTPTest.java | 98.7%| Completed | 
| HTTPTokene.java |93.2% |(no test file)  | 
| JSONArrayTest.java |95.9% | Completed | 
| JSONException.java | 26.7% | (no test file) |
| JSONMLTest.java | 83.2%| Completed | 
| JSONObjectTest | 90.9% | Completed | 
| JSONObject.Null | 87.5% | (no test file) | 
| JSONString.java | | (no lines to test)  | 
| JSONStringerTest.java | 93.8%| Completed | 
| JSONTokenerTest.java | 72.1% | In progress | 
| JSONWriter.java | 88.9% | (no test file) | 
| PropertyTest.java  | 94.8%  | Completed |
| XMLTest.java | 85.1% | Completed |
| XMLTokener.java| 82.7%| (no test file) | 

|| Files used in test ||
| JunitTestSuite.java |
| MyBean.java |
| StringsResourceBundle.java |
|TestRUnner.java |
| Util.java |


