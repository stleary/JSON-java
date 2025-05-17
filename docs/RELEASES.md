# Release history:

JSON-java releases can be found by searching the Maven repository for groupId "org.json"
and artifactId "json". For example:
[https://search.maven.org/search?q=g:org.json%20AND%20a:json&core=gav](https://search.maven.org/search?q=g:org.json%20AND%20a:json&core=gav)

~~~
20250517    Strict mode hardening and recent commits

20250107    Restore moditect in pom.xml

20241224    Strict mode opt-in feature, and recent commits. This release does not contain module-info.class.
It is not recommended if you need this feature.

20240303    Revert optLong/getLong changes, and recent commits.

20240205    Recent commits.

20231013    First release with minimum Java version 1.8. Recent commits, including fixes for CVE-2023-5072.

20230618    Final release with Java 1.6 compatibility. Future releases will require Java 1.8 or greater.

20230227    Fix for CVE-2022-45688 and recent commits

20220924    New License - public domain, and some minor updates

20220320    Wrap StackOverflow with JSONException

20211205    Recent commits and some bug fixes for similar()

20210307    Recent commits and potentially breaking fix to JSONPointer

20201115    Recent commits and first release after project structure change

20200518    Recent commits and snapshot before project structure change

20190722    Recent commits

20180813    POM change to include Automatic-Module-Name (#431)
            JSONObject(Map) now throws an exception if any of a map keys are null (#405)

20180130    Recent commits

20171018    Checkpoint for recent commits.

20170516    Roll up recent commits.

20160810    Revert code that was breaking opt*() methods.

20160807    This release contains a bug in the JSONObject.opt*() and JSONArray.opt*() methods,
it is not recommended for use.
Java 1.6 compatability fixed, JSONArray.toList() and JSONObject.toMap(),
RFC4180 compatibility, JSONPointer, some exception fixes, optional XML type conversion.
Contains the latest code as of 7 Aug 2016

20160212    Java 1.6 compatibility, OSGi bundle. Contains the latest code as of 12 Feb 2016.

20151123    JSONObject and JSONArray initialization with generics. Contains the latest code as of 23 Nov 2015.

20150729    Checkpoint for Maven central repository release. Contains the latest code
as of 29 July 2015.
~~~
