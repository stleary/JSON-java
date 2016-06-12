#!/bin/bash
javac -d . -cp . *.java zip/*.java
jar cvf json.jar org
