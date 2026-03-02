# Firebase Firestore Integration

This document describes how to set up Firebase Firestore integration with the Dash backend.

## Prerequisites

1. A Firebase project with Firestore enabled
2. A service account JSON key file from Firebase Console

## Getting the Service Account JSON

1. Go to Firebase Console > Project Settings > Service Accounts
2. Click "Generate New Private Key"
3. Download the JSON file

## Environment Variables

Set the following environment variables before running the application:

### Required Environment Variables

```bash
# Your Firebase Project ID
export FIREBASE_PROJECT_ID="your-project-id"

# Service account JSON (entire JSON content as a string)
export FIREBASE_SERVICE_ACCOUNT_JSON='{"type":"service_account","project_id":"your-project-id",...}'
```

### Setting Environment Variables

**Option 1: Export in terminal (for development)**
```bash
export FIREBASE_PROJECT_ID="your-project-id"
export FIREBASE_SERVICE_ACCOUNT_JSON='<paste entire JSON content here>'
./gradlew run
```

**Option 2: Create a run configuration in IntelliJ IDEA**
1. Edit Run Configuration
2. Add environment variables in the "Environment variables" field
3. Format: `FIREBASE_PROJECT_ID=your-id;FIREBASE_SERVICE_ACCOUNT_JSON={"type":"service_account",...}`

**Option 3: Use a script**
```bash
#!/bin/bash
# run.sh
export FIREBASE_PROJECT_ID="your-project-id"
export FIREBASE_SERVICE_ACCOUNT_JSON=$(cat /path/to/service-account.json)
./gradlew run
```

## Verification

The backend will start successfully even without Firebase configuration, but Firebase services won't be available.

### Check Firebase Status

```bash
# Check if Firebase is initialized
curl http://localhost:8080/health
```

Response when Firebase is initialized:
```json
{
  "status": "healthy",
  "firebase": "initialized"
}
```

Response when Firebase is not initialized:
```json
{
  "status": "healthy",
  "firebase": "not initialized"
}
```

## Usage in Code

Access Firestore through the `FirebaseService`:

```kotlin
// Check if Firebase is initialized
if (FirebaseService.isInitialized()) {
    val firestore = FirebaseService.getFirestore()
    
    // Use Firestore API directly
    val collection = firestore.collection("your-collection")
    // ... perform operations
}
```

Or use the `FirestoreRepository` helper:

```kotlin
val repository = FirestoreRepository()
val firestore = repository.getFirestore()
```

## Security Notes

- Never commit the service account JSON to version control
- Never expose the service account JSON in logs or error messages
- Use environment variables or secret management systems
- In production, consider using GCP default credentials if running on Cloud Run/GKE

## Troubleshooting

### "Firebase has not been initialized"
- Ensure environment variables are set correctly before starting the application
- Check logs for Firebase initialization errors
- Verify the service account JSON is valid

### "Invalid service account JSON"
- Verify the JSON is valid and complete
- Ensure no extra quotes or escaping issues
- Try reading from file if environment variable is causing issues

### Permission errors
- Verify the service account has the necessary Firestore permissions
- Check Firebase Console > IAM & Admin for proper roles (typically "Cloud Datastore User" or "Firebase Admin")

### Build runs without Firebase
- This is expected behavior - the app gracefully handles missing Firebase config
- Firebase will only be available if environment variables are properly set
- Check `/health` endpoint to verify Firebase status
