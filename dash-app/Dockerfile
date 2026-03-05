# Build stage
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

# Copy gradle files
COPY gradlew .
COPY gradle gradle
COPY gradle.properties .
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Copy source code
COPY composeApp composeApp

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the wasm application
RUN ./gradlew wasmJsBrowserDistribution --no-daemon

# Production stage - serve static files
FROM nginx:alpine

# Copy built files from build stage
COPY --from=build /app/composeApp/build/dist/wasmJs/productionExecutable /usr/share/nginx/html

# Copy nginx configuration if needed
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
