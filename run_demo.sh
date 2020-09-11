#!/usr/bin/env bash

DIR=`dirname "$0"`
pushd $DIR > /dev/null

mvn -q clean install

providers=(./example-impl-1/target/*.jar ./example-impl-2/target/*.jar ./example-impl-3/target/*.jar)

export MAVEN_OPTS="--enable-preview"
mvn -q exec:java -pl example-client "-Dexec.args=${providers[*]@Q}"
