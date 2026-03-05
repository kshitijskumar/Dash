# Build stage
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

# Install necessary tools and dependencies
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    libatomic1 \
    && rm -rf /var/lib/apt/lists/*

# Install Android SDK (minimal - just what's needed for gradle configuration)
ENV ANDROID_HOME=/opt/android-sdk
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

RUN mkdir -p $ANDROID_HOME/cmdline-tools && \
    cd $ANDROID_HOME/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip && \
    unzip -q commandlinetools-linux-9477386_latest.zip && \
    rm commandlinetools-linux-9477386_latest.zip && \
    mv cmdline-tools latest

# Accept licenses and install minimal SDK components
RUN yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses || true
RUN $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager \
    "platform-tools" \
    "platforms;android-34" \
    "build-tools;34.0.0"

# Copy gradle wrapper and properties first (for caching)
COPY gradlew .
COPY gradlew.bat .
COPY gradle gradle
COPY gradle.properties .

# Create local.properties for Android SDK location
RUN echo "sdk.dir=$ANDROID_HOME" > local.properties

# CRITICAL FIX: Override memory settings for cloud build environment
RUN echo "" >> gradle.properties && \
    echo "# Docker build overrides - optimized for cloud environments" >> gradle.properties && \
    echo "org.gradle.jvmargs=-Xmx2048M -Dfile.encoding=UTF-8 -XX:MaxMetaspaceSize=512m" >> gradle.properties && \
    echo "kotlin.daemon.jvmargs=-Xmx1536M" >> gradle.properties && \
    echo "org.gradle.parallel=false" >> gradle.properties && \
    echo "org.gradle.caching=false" >> gradle.properties

# Copy gradle configuration files
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies (this layer will be cached)
RUN ./gradlew --version --no-daemon

# Copy source code
COPY composeApp composeApp

# DIAGNOSTIC: List all available wasmJs tasks
RUN echo "=== Available WasmJS Tasks ===" && \
    ./gradlew tasks --all --no-daemon 2>&1 | grep -i "wasmjs" || echo "No wasmJs tasks found in grep"

# Build with the distribution task and capture output
RUN ./gradlew :composeApp:wasmJsBrowserDistribution \
    --no-daemon \
    --no-parallel \
    --stacktrace \
    2>&1 | tee build.log

# DIAGNOSTIC: Show what was actually built
RUN echo "=== Build completed, investigating output ===" && \
    echo "--- Checking build/dist structure ---" && \
    ls -la composeApp/build/dist/ 2>/dev/null || echo "No dist/ directory" && \
    ls -la composeApp/build/dist/wasmJs/ 2>/dev/null || echo "No dist/wasmJs/ directory" && \
    echo "--- Looking for any wasmJs directories ---" && \
    find composeApp/build -type d -name "*wasmJs*" -o -name "*wasm*" 2>/dev/null || echo "No wasm directories found" && \
    echo "--- Looking for .wasm and .js files ---" && \
    find composeApp/build -type f \( -name "*.wasm" -o -name "*.mjs" -o -name "*.js" \) 2>/dev/null | head -30 || echo "No wasm/js files found" && \
    echo "--- Full build directory listing (top level) ---" && \
    ls -la composeApp/build/

# Find and copy to a standard location
RUN mkdir -p /app/output && \
    if [ -d "composeApp/build/dist/wasmJs/productionExecutable" ]; then \
        echo "✓ Found: productionExecutable"; \
        cp -r composeApp/build/dist/wasmJs/productionExecutable/* /app/output/; \
    elif [ -d "composeApp/build/dist/wasmJs/developmentExecutable" ]; then \
        echo "✓ Found: developmentExecutable"; \
        cp -r composeApp/build/dist/wasmJs/developmentExecutable/* /app/output/; \
    elif [ -d "composeApp/build/compileSync/wasmJs/main/productionExecutable/kotlin" ]; then \
        echo "✓ Found: compileSync location"; \
        cp -r composeApp/build/compileSync/wasmJs/main/productionExecutable/kotlin/* /app/output/; \
    else \
        echo "✗ Could not find standard output directory"; \
        echo "=== Searching entire build for index.html ==="; \
        find composeApp/build -name "index.html" -type f; \
        echo "=== Full directory tree (limited depth) ==="; \
        find composeApp/build -maxdepth 4 -type d; \
        exit 1; \
    fi && \
    echo "=== Output copied to /app/output ===" && \
    ls -la /app/output

# Production stage - serve static files
FROM nginx:alpine

# Copy built files from standardized location
COPY --from=build /app/output /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Verify files were copied
RUN echo "=== Files in nginx html directory ===" && \
    ls -la /usr/share/nginx/html && \
    echo "=== Checking for index.html ===" && \
    if [ -f "/usr/share/nginx/html/index.html" ]; then \
        echo "✓ index.html found"; \
    else \
        echo "✗ index.html NOT found"; \
        find /usr/share/nginx/html -type f | head -20; \
    fi

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
