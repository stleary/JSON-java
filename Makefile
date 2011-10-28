# Copyright © 2011 Jason J.A. Stephenson
#
# This file is part of JSON-java. It is distributed under the same
# license as JSON-java. Don't use it to be evil.

DOC_DIR ?= doc/

SOURCES = src/org/json/CDL.java \
          src/org/json/JSONObject.java \
          src/org/json/Cookie.java \
          src/org/json/JSONStringer.java \
          src/org/json/CookieList.java \
          src/org/json/JSONString.java \
          src/org/json/HTTP.java \
          src/org/json/JSONTokener.java \
          src/org/json/HTTPTokener.java \
          src/org/json/JSONWriter.java \
          src/org/json/JSONArray.java \
          src/org/json/JSONException.java \
          src/org/json/XML.java \
          src/org/json/JSONML.java \
          src/org/json/XMLTokener.java

ifdef WITH_JUNIT
SOURCES += \
      src/org/json/Test.java
endif

.PHONY: documentation compile jar clean

jar: compile
	jar cf JSON.jar org/

compile: $(SOURCES)
	javac -d ./ -sourcepath src/ $^

documentation: $(SOURCES)
	javadoc -d $(DOC_DIR) -doctitle JSON-java -windowtitle JSON-java $^

clean:
	-rm -rf doc/
	-rm -rf org/
	-rm JSON.jar
