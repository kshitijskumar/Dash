#!/bin/bash
set -e

echo "=== Dash App Build Script for Render ==="

# Detect and install Java if needed
if ! command -v java &> /dev/null; then
    echo "Java not found. Installing OpenJDK 17..."
    
    # Check if we're on Ubuntu/Debian
    if command -v apt-get &> /dev/null; then
        apt-get update -qq
        apt-get install -y -qq openjdk-17-jdk
        export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
    # Check if we're on Amazon Linux/RHEL
    elif command -v yum &> /dev/null; then
        yum install -y java-17-openjdk-devel
        export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
    else
        echo "ERROR: Unable to install Java automatically"
        exit 1
    fi
    
    export PATH=$JAVA_HOME/bin:$PATH
fi

# Verify Java installation
echo "Java version:"
java -version
echo "JAVA_HOME: $JAVA_HOME"

# Make gradlew executable (just in case)
chmod +x ./gradlew

# Build the WasmJS application
echo "Building WasmJS application..."
./gradlew wasmJsBrowserDistribution --no-daemon --stacktrace

echo "Build completed successfully!"
echo "Output directory: composeApp/build/dist/wasmJs/productionExecutable"
