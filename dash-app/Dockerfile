FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

# Tools needed by Gradle/Kotlin during dependency resolution.
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    libatomic1 \
    && rm -rf /var/lib/apt/lists/*

# Copy Gradle project metadata first to improve Docker layer caching.
COPY gradlew .
COPY gradlew.bat .
COPY gradle gradle
COPY gradle.properties .
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY composeApp/build.gradle.kts composeApp/build.gradle.kts

RUN chmod +x ./gradlew

# Copy sources and build the Compose WASM web distribution.
COPY composeApp composeApp
RUN ./gradlew :composeApp:wasmJsBrowserDistribution --no-daemon --stacktrace --console=plain

FROM nginx:alpine

# Serve the generated web build.
COPY --from=build /app/composeApp/build/dist/wasmJs/productionExecutable /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
