# Environment Setup Guide for Firebase

This guide shows you different ways to set up Firebase environment variables for the Dash backend.

## Quick Start: Using the Run Script (Easiest)

From the project root directory:

```bash
./run-with-firebase.sh
```

This script automatically loads the Firebase credentials and starts the server.

## Option 1: Export in Terminal

For a single terminal session:

```bash
# Set the project ID
export FIREBASE_PROJECT_ID="dash-93353"

# Load the service account JSON
export FIREBASE_SERVICE_ACCOUNT_JSON=$(cat ~/Downloads/dash-93353-firebase-adminsdk-fbsvc-81751c15fc.json)

# Run the application
cd dash-backend
./gradlew run
```

## Option 2: IntelliJ IDEA / Android Studio Run Configuration

1. Open IntelliJ IDEA or Android Studio
2. Go to **Run** → **Edit Configurations...**
3. Select your Ktor application configuration (or create a new one)
4. In the **Environment variables** field, click the folder icon or **Browse** button
5. Add the following variables:

   **Name:** `FIREBASE_PROJECT_ID`  
   **Value:** `dash-93353`

   **Name:** `FIREBASE_SERVICE_ACCOUNT_JSON`  
   **Value:** (Paste the entire JSON content from the file)

   To get the JSON content:
   ```bash
   cat ~/Downloads/dash-93353-firebase-adminsdk-fbsvc-81751c15fc.json
   ```

6. Click **OK** and run the application

### Screenshot Guide for IntelliJ:
- Environment variables should be in format: `NAME=value;NAME2=value2`
- Or use the table editor (recommended) to add each variable separately

## Option 3: Shell Profile (Permanent)

Add to your `~/.zshrc` or `~/.bash_profile`:

```bash
# Firebase Configuration for Dash Backend
export FIREBASE_PROJECT_ID="dash-93353"
export FIREBASE_SERVICE_ACCOUNT_JSON=$(cat ~/Downloads/dash-93353-firebase-adminsdk-fbsvc-81751c15fc.json)
```

Then reload your shell:
```bash
source ~/.zshrc  # or source ~/.bash_profile
```

**⚠️ Warning:** This exposes credentials in every terminal session. Only use for local development.

## Option 4: Gradle Run with Inline Environment

```bash
cd dash-backend
FIREBASE_PROJECT_ID="dash-93353" \
FIREBASE_SERVICE_ACCOUNT_JSON=$(cat ~/Downloads/dash-93353-firebase-adminsdk-fbsvc-81751c15fc.json) \
./gradlew run
```

## Verify Setup

Once the server is running, check if Firebase is initialized:

```bash
curl http://localhost:8080/health
```

Expected response when working:
```json
{
  "status": "healthy",
  "firebase": "initialized"
}
```

## Test the /all Endpoint

Fetch all documents from the "data" collection:

```bash
curl http://localhost:8080/all
```

Expected response:
```json
{
  "collection": "data",
  "count": 1,
  "documents": [
    {
      "id": "document-id",
      "data": {
        "field1": "value1",
        "field2": "value2"
      }
    }
  ]
}
```

## Troubleshooting

### "Firebase configuration not found"
- Check that environment variables are set before starting the app
- Verify variable names are exactly: `FIREBASE_PROJECT_ID` and `FIREBASE_SERVICE_ACCOUNT_JSON`

### "Failed to initialize Firebase"
- Verify the JSON file exists at the specified path
- Check that the JSON is valid (no syntax errors)
- Ensure the service account has proper permissions in Firebase Console

### JSON with newlines causing issues
- The private key contains `\n` characters - this is normal
- Make sure you're using `$(cat file.json)` to read the entire file correctly
- Don't manually copy-paste the JSON with line breaks

## Security Best Practices

- ✓ Use the run script for local development
- ✓ Never commit the service account JSON to git
- ✓ Keep the JSON file in a secure location
- ✗ Don't share the service account JSON file
- ✗ Don't expose it in logs or error messages

## Production Deployment

For production environments:
- Use Google Cloud Secret Manager
- Use environment-specific service accounts
- Enable minimal required permissions (least privilege principle)
- Consider using GCP default credentials if deploying to Cloud Run/GKE
