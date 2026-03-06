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

# CRITICAL: Further reduce memory for stability
RUN echo "" >> gradle.properties && \
    echo "# Docker build overrides - further optimized" >> gradle.properties && \
    echo "org.gradle.jvmargs=-Xmx1536M -Dfile.encoding=UTF-8 -XX:MaxMetaspaceSize=384m" >> gradle.properties && \
    echo "kotlin.daemon.jvmargs=-Xmx1024M" >> gradle.properties && \
    echo "kotlin.incremental=false" >> gradle.properties && \
    echo "org.gradle.parallel=false" >> gradle.properties && \
    echo "org.gradle.caching=false" >> gradle.properties && \
    echo "org.gradle.configureondemand=false" >> gradle.properties

# Copy gradle configuration files
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies
RUN ./gradlew --version --no-daemon

# Copy source code
COPY composeApp composeApp

# Build with detailed logging
RUN echo "=== Starting WasmJS build $(date) ===" && \
    ./gradlew :composeApp:wasmJsBrowserDistribution \
    --no-daemon \
    --no-parallel \
    --stacktrace \
    --console=plain \
    2>&1 | tee build.log || \
    (echo "=== BUILD FAILED $(date) ===" && \
     tail -100 build.log && \
     exit 1)

# DIAGNOSTIC: Find where files actually are
RUN echo "=== Build completed, searching for output ===" && \
    echo "--- Checking expected location ---" && \
    ls -la composeApp/build/dist/wasmJs/ 2>/dev/null || echo "dist/wasmJs/ not found" && \
    echo "--- Searching for index.html anywhere in build ---" && \
    find composeApp/build -name "index.html" -type f 2>/dev/null || echo "No index.html found" && \
    echo "--- Searching for .wasm files ---" && \
    find composeApp/build -name "*.wasm" -type f 2>/dev/null | head -5 || echo "No .wasm files found" && \
    echo "--- Top-level build directory ---" && \
    ls -la composeApp/build/ 2>/dev/null || echo "Build directory not found" && \
    echo "--- All subdirectories in build (depth 3) ---" && \
    find composeApp/build -maxdepth 3 -type d 2>/dev/null | head -30

# Create output directory and copy whatever we find
RUN mkdir -p /app/final-output && \
    if [ -d "composeApp/build/dist/wasmJs/productionExecutable" ]; then \
        echo "✓ Found productionExecutable"; \
        cp -r composeApp/build/dist/wasmJs/productionExecutable/* /app/final-output/; \
    elif [ -d "composeApp/build/dist/wasmJs/developmentExecutable" ]; then \
        echo "✓ Found developmentExecutable"; \
        cp -r composeApp/build/dist/wasmJs/developmentExecutable/* /app/final-output/; \
    elif find composeApp/build -name "index.html" -type f | grep -q .; then \
        echo "✓ Found index.html, copying its parent directory"; \
        INDEX_DIR=$(find composeApp/build -name "index.html" -type f | head -1 | xargs dirname); \
        echo "Copying from: $INDEX_DIR"; \
        cp -r $INDEX_DIR/* /app/final-output/; \
    else \
        echo "✗ No suitable output found!"; \
        echo "=== Checking if webpack/distribution task actually ran ==="; \
        grep -i "webpack\|distribution" build.log | tail -20; \
        exit 1; \
    fi && \
    echo "=== Final output contents ===" && \
    ls -lah /app/final-output/ && \
    echo "=== Verifying index.html ===" && \
    test -f /app/final-output/index.html || (echo "ERROR: No index.html in output!" && exit 1)

# Production stage - serve static files
FROM nginx:alpine

# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*

# Copy built files from standardized output location
COPY --from=build /app/final-output/ /usr/share/nginx/html/

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Verify files were copied successfully
RUN echo "=== Nginx html directory contents ===" && \
    ls -lah /usr/share/nginx/html/ && \
    echo "=== Verifying index.html exists ===" && \
    test -f /usr/share/nginx/html/index.html && echo "✓ index.html found" || (echo "✗ ERROR: index.html not found!" && exit 1)

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
