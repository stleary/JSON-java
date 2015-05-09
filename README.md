# JSON-Java-unit-test

Unit tests to validate the JSON-Java GitHub project code<br>
https://github.com/douglascrockford/JSON-java<br>

*These tests are a work in progress. Help from interested developers is welcome.*<br>
More coverage is needed, but more importantly, improvements to test quality is needed.<br>

You will need the following libraries for testing:
Test harness: http://junit.org<br>
Coverage: http://www.eclemma.org/<br>
Mockery: https://github.com/mockito/mockito 

Eclipse is the recommended development environment.
Run individual tests or <b>JunitTestSuite</b> using *EclEmma Coverage*, or execute the <b>TestRunner<b> application directly.<br>

Test filenames should consist of the name of the module being tested, with the suffix "Test". 
For example, <b>Cookie.java</b> is tested by <b>CookieTest.java</b>.
When adding a new unit test, don't forget to update <b>JunitTestSuite.java</b>.

The fundamental issues with JSON-Java testing are:
* <b>JSONObjects</b> are unordered, making simple string comparison ineffective. 
* Comparisons via **equals()** is not currently supported. Neither <b>JSONArray</b> nor <b>JSONObject</b> overrride <b>hashCode()</b> or <b>equals()</b>, so comparison defaults to the <b>Object</b> equals(), which is not useful.
* Access to the <b>JSONArray</b> and <b>JSONObject</b> internal containers for comparison is not currently available.

General issues with unit testing are:
* Just writing tests to make coverage goals tends to result in poor tests. 
* Unit tests are a form of documentation - how a given method actually works is demonstrated by the test. So for a code reviewer or future developer looking at code a good test helps explain how a function is supposed to work according to the original author. This can be difficult if you are not the original developer.
*	It is difficult to evaluate unit tests in a vacuum. You also need to see the code being tested to understand if a test is good. 
* Without unit tests it is hard to feel confident about the quality of the code, especially when fixing bugs or refactoring. Good tests prevents regressions and keeps the intent of the code correct.
* If you have unit test results along with pull requests, the reviewer has an easier time understanding your code and determining if the it works as intended.

When you start working on a test, add the empty file to the repository and update the readme, so that others will know that test is taken.

A unit test has the following stages:

| Test phase |Description |
|----|----|
| No test | No test specifically for this class has been written, or the class contains no executable code. |
| In progress | Unit tests have been started for this class. |
| Coverage > 90% | Initial goal of 90% coverage has been reached. Test quality may be questionable |
| Reasonable test cases | 90% coverage. Functionality and behavior has been confirmed |
| Checked against previous unit tests | Historical unit tests have been checked in case something important was missed |
| Completed | The unit test is completed |


| Test file name  | Coverage | Comments |
| ------------- | ------------- | ---- |
| Total coverage | 88.9% | | | 
| | | | 
| CDL.java | 98% | Reasonable test cases.  |
| Cookie.java  | 98.9%   | Reasonable test cases. |
| CookieList.java |96.5% | Reasonable test cases. |
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
| StringsResourceBundle.java | 
|TestRunner.java | 
| Util.java | 


