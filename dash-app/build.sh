#!/bin/bash

# Install Java if not present
if [ -z "$JAVA_HOME" ]; then
  echo "Installing Java 17..."
  apt-get update
  apt-get install -y openjdk-17-jdk
  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
  export PATH=$JAVA_HOME/bin:$PATH
fi

echo "Java version:"
java -version

echo "Building Wasm application..."
./gradlew wasmJsBrowserDistribution
