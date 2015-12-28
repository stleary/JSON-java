# JSON-Java-unit-test

Unit tests to validate the JSON-Java GitHub project code<br>

https://github.com/douglascrockford/JSON-java<br>

*These tests are a work in progress. Help from interested developers is welcome.*<br>
More coverage is needed, but more importantly, improvements to test quality is needed.<br>

Eclipse is the recommended development environment.<br>
Run individual tests or <b>JunitTestSuite</b> using <b>EclEmma Coverage</b>, or execute the **TestRunner** application directly.<br>

**You will need the following libraries for testing:**<br>
Test harness: http://junit.org<br> 
* asm-1.0.2.jar<br>
* commons-io-2.1.jar<br>
* commons-lang-2.6.jar<br>
* hamcrest-core-1.3.jar<br>
* json-path-2.1.0.jar<br>
* json-smart-2.1.1.jar<br>
* junit-4.12.jar<br>
* mockito-all-1.9.5.jar<br>
* slf4j-api-1.7.12.jar<br>
* slf-simple-1.7.12.jar<br>
* JSON-java.jar<br>
 
**To build from the command line using gradle:**
build.gradle<br>
````
\# In this example, both the JSON-java jar and the test code is built <br>
\# from the same build file, in the test code directory.
apply plugin: 'java'
jar.baseName = 'JSON-java'

sourceSets {
 main {
  java {
   srcDir '../JSON-java/src/org/json'
  }
 }
 test {
  java {
   srcDir 'src/org/json/junit'
  }
 }
}

repositories {
 mavenCentral()
}

dependencies {
    testCompile group: 'junit', name: 'junit', version: '4.+'
    testCompile group: 'com.jayway.jsonpath', name: 'json-path', version: '2.1.0'
    testCompile group: 'org.mockito', name: 'mockito-all', version: '1.9.5'
}
````

To measure coverage: http://www.eclemma.org/ (just install the latest in Eclipse)<br>

<b>Conventions</b><br>
Test filenames should consist of the name of the module being tested, with the suffix "Test". 
For example, <b>Cookie.java</b> is tested by <b>CookieTest.java</b>.
When adding a new unit test, don't forget to update <b>JunitTestSuite.java</b>.

<b>The fundamental issues with JSON-Java testing are:</b><br>
* <b>JSONObjects</b> are unordered, making simple string comparison ineffective. 
* Comparisons via **equals()** is not currently supported. Neither <b>JSONArray</b> nor <b>JSONObject</b> overrride <b>hashCode()</b> or <b>equals()</b>, so comparison defaults to the <b>Object</b> equals(), which is not useful.
* Access to the <b>JSONArray</b> and <b>JSONObject</b> internal containers for comparison is not currently available.

<b>General issues with unit testing are:</b><br>
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
| EnumTest.java | n/a | Just documenting how enums are handled. |
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
| MyEnum.java |
| MyEnumClass.java |
| MyEnumField.java |
| JunitTestSuite.java | 
| StringsResourceBundle.java | 
| TestRunner.java | 
| Util.java | 


