# Docker Build Troubleshooting Guide

## Common Build Errors and Solutions

### Error: "gradle dependencies failed with exit code 1"

**Full Error:**
```
error: failed to solve: process "/bin/sh -c gradle dependencies --no-daemon" did not complete successfully: exit code: 1
```

**Cause:** 
- Missing Gradle wrapper files
- Using wrong Gradle command

**Solution:**
✅ **Fixed in updated Dockerfile**
- Now uses `./gradlew` (project's wrapper) instead of `gradle`
- Properly copies `gradlew`, `gradlew.bat`, and `gradle/` folder
- Makes `gradlew` executable with `chmod +x`

---

### Error: "Could not find or load main class"

**Cause:** JAR file not built correctly or wrong path

**Solution:**
1. Verify the JAR is being built:
   ```dockerfile
   RUN ./gradlew buildFatJar --no-daemon
   ```

2. Check the JAR path in COPY command:
   ```dockerfile
   COPY --from=builder /app/build/libs/*-all.jar app.jar
   ```

---

### Error: "Port already in use"

**Cause:** Another service using port 8080

**Solution for Local Testing:**
```bash
# Use different port
docker run -p 8081:8080 your-image

# Or stop conflicting service
lsof -ti:8080 | xargs kill
```

**Solution for Render:**
- Render handles port mapping automatically
- Your app uses `PORT` env var (defaults to 8080 locally)

---

### Error: "Cannot connect to the Docker daemon"

**Cause:** Docker not running

**Solution:**
1. Start Docker Desktop (macOS/Windows)
2. Or start Docker service (Linux):
   ```bash
   sudo systemctl start docker
   ```

---

### Error: "No space left on device"

**Cause:** Docker using too much disk space

**Solution:**
```bash
# Clean up unused images and containers
docker system prune -a

# Remove build cache
docker builder prune
```

---

## Testing Docker Build Locally

### 1. Build the Image

```bash
cd dash-backend

# Build with tag
docker build -t dash-backend:latest .

# Build with build args (if needed)
docker build --build-arg JAVA_OPTS="-Xmx1g" -t dash-backend:latest .
```

### 2. Run the Container

```bash
# Run with environment variables
docker run -p 8080:8080 \
  -e FIREBASE_PROJECT_ID="dash-93353" \
  -e FIREBASE_SERVICE_ACCOUNT_JSON='{"type":"service_account",...}' \
  dash-backend:latest

# Run in detached mode
docker run -d -p 8080:8080 \
  -e FIREBASE_PROJECT_ID="dash-93353" \
  -e FIREBASE_SERVICE_ACCOUNT_JSON='{"type":"service_account",...}' \
  --name dash-backend \
  dash-backend:latest
```

### 3. Check Logs

```bash
# View logs
docker logs dash-backend

# Follow logs in real-time
docker logs -f dash-backend
```

### 4. Test the Application

```bash
# Health check
curl http://localhost:8080/health

# Dashboard endpoint
curl http://localhost:8080/dashls/user123/token123
```

### 5. Stop and Clean Up

```bash
# Stop container
docker stop dash-backend

# Remove container
docker rm dash-backend

# Remove image
docker rmi dash-backend:latest
```

---

## Dockerfile Improvements Explained

### Before (Problematic)
```dockerfile
FROM gradle:8.5-jdk17 AS builder
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon  # ❌ Uses system gradle, not project wrapper
```

### After (Fixed)
```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS builder
COPY gradlew ./                       # ✅ Copy wrapper script
COPY gradlew.bat ./
COPY gradle ./gradle                  # ✅ Copy wrapper JAR
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
RUN chmod +x gradlew                  # ✅ Make executable
RUN ./gradlew dependencies --no-daemon  # ✅ Use project wrapper
```

**Benefits:**
- ✅ Uses exact Gradle version specified in `gradle/wrapper/gradle-wrapper.properties`
- ✅ Consistent builds across environments
- ✅ No version mismatch issues
- ✅ Smaller base image (JDK-alpine vs gradle image)

---

## Build Performance Tips

### 1. Layer Caching

Order matters! Put things that change less frequently first:

```dockerfile
# Good order (best caching)
COPY gradlew ./                    # Rarely changes
COPY gradle ./gradle               # Rarely changes  
COPY *.gradle.kts gradle.properties ./  # Changes occasionally
RUN ./gradlew dependencies         # Heavy operation, cached if above unchanged
COPY src ./src                     # Changes frequently
RUN ./gradlew buildFatJar         # Only re-runs if src changes
```

### 2. Multi-stage Build

```dockerfile
# Stage 1: Build (large image with JDK)
FROM eclipse-temurin:17-jdk-alpine AS builder
# ... build steps ...

# Stage 2: Runtime (small image with only JRE)
FROM eclipse-temurin:17-jre-alpine
COPY --from=builder /app/build/libs/*-all.jar app.jar
```

**Result:**
- Build image: ~500MB (not shipped)
- Runtime image: ~200MB (deployed to Render)

### 3. Use .dockerignore

Prevents copying unnecessary files:
```
build/
.gradle/
.git/
*.md
.idea/
```

**Result:** Faster builds, smaller context

---

## Render-Specific Notes

### Port Configuration

**In application.yaml:**
```yaml
ktor:
    deployment:
        port: 8080        # Default
        port: ${?PORT}    # Overrides if PORT env var exists
```

**In Dockerfile:**
```dockerfile
ENV PORT=8080  # Default for local testing
# Render will override with its own PORT value (usually 10000)
```

### Environment Variables

**Required in Render:**
- `FIREBASE_PROJECT_ID`
- `FIREBASE_SERVICE_ACCOUNT_JSON`

**Automatically set by Render:**
- `PORT` (don't set manually)

### Health Checks

Render automatically pings your `/health` endpoint:
```bash
curl https://your-app.onrender.com/health
```

Make sure it returns 200 OK when Firebase is initialized.

---

## Debugging Build Failures on Render

### 1. Check Render Logs

In Render Dashboard:
- Go to your service
- Click "Logs" tab
- Look for error messages during build

### 2. Common Issues

**Issue:** "Can't find file"
- Check `.dockerignore` isn't excluding needed files
- Verify files are committed to Git

**Issue:** "Out of memory"
- Build might be too large for free tier
- Consider simpler build or upgrade plan

**Issue:** "Build timeout"
- Free tier has build time limits
- Optimize Dockerfile (use caching better)
- Or upgrade to paid plan

### 3. Test Locally First

Always test Docker build locally before pushing:
```bash
docker build -t test .
docker run -p 8080:8080 test
```

---

## Quick Reference

### Build Locally
```bash
docker build -t dash-backend .
```

### Run Locally
```bash
docker run -p 8080:8080 \
  -e FIREBASE_PROJECT_ID="your-project" \
  -e FIREBASE_SERVICE_ACCOUNT_JSON='...' \
  dash-backend
```

### Push to Git (triggers Render deploy)
```bash
git add .
git commit -m "Update Dockerfile"
git push origin main
```

### Monitor Render Deploy
1. Go to Render Dashboard
2. Select your service
3. Watch "Events" tab for deploy progress
4. Check "Logs" tab for any errors
