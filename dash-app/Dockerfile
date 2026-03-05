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

# Override memory settings for cloud build environment
RUN echo "" >> gradle.properties && \
    echo "# Docker build overrides" >> gradle.properties && \
    echo "org.gradle.jvmargs=-Xmx2048M -Dfile.encoding=UTF-8 -XX:MaxMetaspaceSize=512m" >> gradle.properties && \
    echo "kotlin.daemon.jvmargs=-Xmx1536M" >> gradle.properties && \
    echo "org.gradle.parallel=false" >> gradle.properties && \
    echo "org.gradle.caching=false" >> gradle.properties

# Copy gradle configuration files
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies
RUN ./gradlew --version --no-daemon

# Copy source code
COPY composeApp composeApp

# Build the wasm application
RUN ./gradlew :composeApp:wasmJsBrowserDistribution \
    --no-daemon \
    --no-parallel \
    --stacktrace

# Verify the build output and show what was created
RUN echo "=== Verifying build output ===" && \
    ls -la composeApp/build/dist/wasmJs/productionExecutable/ && \
    echo "=== Files in output directory ===" && \
    find composeApp/build/dist/wasmJs/productionExecutable -type f | head -20

# Production stage - serve static files
FROM nginx:alpine

# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*

# Copy built files from build stage
COPY --from=build /app/composeApp/build/dist/wasmJs/productionExecutable/ /usr/share/nginx/html/

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Verify files were copied successfully
RUN echo "=== Nginx html directory contents ===" && \
    ls -la /usr/share/nginx/html/ && \
    echo "=== Verifying index.html exists ===" && \
    test -f /usr/share/nginx/html/index.html && echo "✓ index.html found" || (echo "✗ ERROR: index.html not found!" && exit 1)

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
