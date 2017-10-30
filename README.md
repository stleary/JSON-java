# JSON-Java-unit-test

Unit tests to validate the JSON-Java GitHub project code<br>

https://github.com/stleary/JSON-java<br>

Gradle and Eclipse is the recommended build tool and IDE.<br>
Run individual tests or <b>JunitTestSuite</b> using <b>EclEmma Coverage</b>, or execute the **TestRunner** application directly.<br>

**The following libraries are required:**<br>
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
 
**To build from the command line using gradle:**<br>
Until the unit tests are merged into the JSON-Java project, the code has to be wired by hand. <br>
\# In an empty directory of your choice, clone JSON-Java-unit-test:<br>
````
git clone https://github.com/stleary/JSON-Java-unit-test.git .
````
\# Create a directory structure for the JSON-Java code
````
# Windows 10 version
mkdir src\main\java\org\json
# *nix version
mkdir -p src/main/java/org/json
````
\# clone JSON-Java 
````
#Windows version
git clone https://github.com/stleary/JSON-Java.git src\main\java\org\json

#*Nix version
git clone https://github.com/stleary/JSON-Java.git src/main/java/org/json
````
\# Build, then execute the unit tests and code coverage
````
gradle clean build test jacocoTestReport

````
\# Eclipse setup requires the Gradle IDE plug-in<br>
\# I use Gradle IDE	3.6.4.201503050952-RELEASE	org.springsource.ide.eclipse.gradle.feature.feature.group	Pivotal Software, Inc.<br>
\# From the Eclipse IDE:
````
File > Import > Gradle project > (navigate to your directory) > Build Model > (Select your directory) > Finish
(It is not necessary to run "gradle eclipse" on the project, from the command line)
````

Unit test results will be in build\reports\tests\index.html<br>
Code coverage will be in build\reports\jacoco\html\index.html

To create an Eclipse project, you will need the Eclipse Gradle plug-in, available from the Eclipse Marketplace. I am currently using Gradle IDE	3.6.4.201503050952-RELEASE<br>
Select File > Import > Gradle > Gradle project <br>
Browse to the directory where you cloned JSON-Java-unit-test<br>
Select Build model<br>
Select built project<br>

<b>Conventions</b><br>
Test filenames should consist of the name of the module being tested, with the suffix "Test". 
For example, <b>Cookie.java</b> is tested by <b>CookieTest.java</b>.
When adding a new unit test, don't forget to update <b>JunitTestSuite.java</b>.

<b>The fundamental issues with JSON-Java testing are:</b><br>
* <b>JSONObjects</b> are unordered, making simple string comparison ineffective. 
* Comparisons via **equals()** is not currently supported. Neither <b>JSONArray</b> nor <b>JSONObject</b> override <b>hashCode()</b> or <b>equals()</b>, so comparison defaults to the <b>Object</b> equals(), which is not useful.
* Access to the <b>JSONArray</b> and <b>JSONObject</b> internal containers for comparison is not currently available.

<b>General issues with unit testing are:</b><br>
* Just writing tests to make coverage goals tends to result in poor tests. 
* Unit tests are a form of documentation - how a given method actually works is demonstrated by the test. So for a code reviewer or future developer looking at code a good test helps explain how a function is supposed to work according to the original author. This can be difficult if you are not the original developer.
*	It is difficult to evaluate unit tests in a vacuum. You also need to see the code being tested to understand if a test is good. 
* Without unit tests it is hard to feel confident about the quality of the code, especially when fixing bugs or refactoring. Good tests prevents regressions and keeps the intent of the code correct.
* If you have unit test results along with pull requests, the reviewer has an easier time understanding your code and determining if the it works as intended.

When you start working on a test, add the empty file to the repository and update the readme, so that others will know that test is taken.

**Caveats:**
JSON-Java is Java 1.6-compatible, but JSON-Java-unit-tests requires Java 1.8. If you see this error when building JSON-Java-unit-test, make sure you have 1.8 installed, on your path, and set in JAVA_HOME:
```
Execution failed for task ':compileJava'.
> invalid flag: -parameters
```

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
| Total coverage | 90.6% | | | 
| | | | 
| CDL.java | 98.8% | Reasonable test cases.  |
| Cookie.java  | 98.9%   | Reasonable test cases. |
| CookieList.java |96.5% | Reasonable test cases. |
| HTTP.java | 98.8%| Coverage > 90% | 
| HTTPTokener.java |93.2% | No test   | 
| JSONArray.java |88.3% | Reasonable test cases. Need new tests for newer API functions | 
| JSONException.java | 100% | No test |
| JSONML.java | 84.4%| In progress | 
| JSONObject | 96.7% | Reasonable test cases | 
| JSONObject.Null | 77.8% | No test  | 
| JSONPointer | 96.3% | Reasonable test cases  | 
| JSONPointerException | 100% | No test  | 
| JSONString.java | | No test  | 
| JSONStringer.java | 93.8%| Coverage > 90% | 
| JSONTokener.java | 87.5% | In progress | 
| JSONWriter.java | 89.15% | No test | 
| Property.java  | 95.8%  | Coverage > 90% |
| XML.java | 77.3% | In progress |
| XMLTokener.java| 82.4%| No test  | 

| Files used in test |
| ------------- |  
| EnumTest.java |
| MyBean.java |
| MyBigNumberBean.java |
| MyEnum.java |
| MyEnumClass.java |
| MyEnumField.java |
| MyJsonString.java |
| MyPublicClass.java |
| PropertyTest.java |
| JunitTestSuite.java | 
| StringsResourceBundle.java | 
| TestRunner.java | 
| Util.java | 


