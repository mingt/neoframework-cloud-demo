#!/bin/sh

# build_type 有 release 或 test 等

cd base-common; ./gradlew clean build publishToMavenLocal -P build_type=release; cd ..
cd trace-service; ./gradlew clean build -P build_type=release; cd ..
