#!/bin/sh

# build_type 有 release 或 test 等

cd config-server; ./gradlew clean build; cd ..
cd webservice-registry; ./gradlew clean build; cd ..

cd base-common; ./gradlew clean build publishToMavenLocal; cd ..

cd auth-server-common; ./gradlew clean build; cd ..
cd auth-server; ./gradlew clean build -P build_type=release; cd ..

cd trace-service; ./gradlew clean build -P build_type=release; cd ..
