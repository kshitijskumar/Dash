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
# Reduce from 4GB to 2GB for Gradle, 1.5GB for Kotlin daemon
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

# Build the wasm target with optimized settings
# Capture full output to build.log for debugging
RUN ./gradlew :composeApp:wasmJsBrowserDistribution \
    --no-daemon \
    --no-parallel \
    --stacktrace \
    --info \
    2>&1 | tee build.log || \
    (echo "============================================" && \
     echo "BUILD FAILED - Last 200 lines of output:" && \
     echo "============================================" && \
     tail -200 build.log && \
     echo "============================================" && \
     echo "Checking for partial build artifacts..." && \
     ls -la composeApp/build/ 2>/dev/null || true && \
     exit 1)

# Verify the build output exists
RUN ls -la composeApp/build/dist/wasmJs/productionExecutable && \
    echo "=== BUILD SUCCESSFUL ===" && \
    echo "Build artifacts:" && \
    find composeApp/build/dist/wasmJs/productionExecutable -type f | head -20

# Production stage - serve static files
FROM nginx:alpine

# Copy built files from build stage
COPY --from=build /app/composeApp/build/dist/wasmJs/productionExecutable /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Verify files were copied
RUN ls -la /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
