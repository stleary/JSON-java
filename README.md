# JSON-Java-unit-test

Unit tests to validate the JSON-Java GitHub project code<br>
https://github.com/douglascrockford/JSON-java<br>

*These tests are a work in progress. Help from interested developers is welcome.*<br>
More coverage is needed, but more importantly, improvements to test quality is needed.<br>

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

A unit test has the following stages:

| Test phase |
|----|
| No test |
| In progress |
| Coverage > 90% |
| Reasonable test cases |
| Checked against previous unit tests |
| Completed |


| Test file name  | Coverage | Comments |
| ------------- | ------------- | ---- |
| Total coverage | 88.6% | | | 
| | | | 
| CDL.java | 98% | Reasonable test cases.  |
| Cookie.java  | 98.9%   | Reasonable test cases. |
| CookieList.java |96.5% | Coverage > 90% |
| HTTP.java | 98.7%| Coverage > 90% | 
| HTTPTokener.java |93.2% | No test   | 
| JSONArray.java |95.9% | Coverage > 90% | 
| JSONException.java | 26.7% | No test |
| JSONML.java | 83.2%| In progress | 
| JSONObject | 90.9% | Coverage > 90% | 
| JSONObject.Null | 87.5% | No test  | 
| JSONString.java | | No test  | 
| JSONStringer.java | 93.8%| Coverage > 90% | 
| JSONTokener.java | 72.1% | In progress | 
| JSONWriter.java | 88.9% | No test | 
| Property.java  | 94.8%  | Coverage > 90% |
| XML.java | 85.1% | In progress |
| XMLTokener.java| 82.7%| No test  | 

| Files used in test |
| ------------- |  
| JunitTestSuite.java | 
| MyBean.java | 
| StringsResourceBundle.java | 
|TestRunner.java | 
| Util.java | 


