#!/bin/bash

echo 'java -Dspring.profiles.active=local -jar build/libs/basic-config-server-0.0.1.jar'
java -Dspring.profiles.active=local -jar build/libs/basic-config-server-0.0.1.jar