# API Routes Documentation

## Endpoints

### Health Check
**GET** `/health`

Returns the health status of the application and Firebase initialization status.

**Response (Firebase initialized):**
```json
{
  "status": "healthy",
  "firebase": "initialized"
}
```

**Response (Firebase not initialized):**
```json
{
  "status": "healthy",
  "firebase": "not initialized"
}
```

---

### Get All Data
**GET** `/all`

Retrieves all documents from the "data" collection.

**Response (Success - 200):**
```json
{
  "collection": "data",
  "count": 1,
  "documents": [
    {
      "id": "doc123",
      "data": {
        "userId": "user123",
        "token": "abc123",
        "links": [
          {"url": "https://example.com", "title": "Example"}
        ]
      }
    }
  ]
}
```

**Response (Firebase not initialized - 503):**
```json
{
  "error": "Firebase is not initialized"
}
```

**Response (Internal Error - 500):**
```json
{
  "error": "Failed to fetch data",
  "message": "error details"
}
```

---

### Get User Dashboard Links
**GET** `/dashls/{userId}/{token}`

Retrieves the user's dashboard links by userId and token. Returns only userId and links (token is omitted from response).

**Path Parameters:**
- `userId` (required): The user's ID
- `token` (required): Authentication token

**Example Request:**
```bash
curl http://localhost:8080/dashls/user123/abc123
```

**Response (Success - 200):**
```json
{
  "userId": "user123",
  "links": [
    {
      "url": "https://example.com",
      "title": "Example Link",
      "description": "A sample link"
    },
    {
      "url": "https://google.com",
      "title": "Google"
    }
  ]
}
```

**Response (userId missing - 400):**
```json
{
  "error": "userId is required"
}
```

**Response (token missing - 400):**
```json
{
  "error": "token is required"
}
```

**Response (No matching data - 404):**
```json
{
  "error": "No data found for the provided userId and token"
}
```

**Response (Firebase not initialized - 503):**
```json
{
  "error": "Firebase is not initialized"
}
```

**Response (Internal Error - 500):**
```json
{
  "error": "Failed to fetch data",
  "message": "error details"
}
```

---

## Security Considerations

### ⚠️ Token in URL Path

**Current Implementation (Quick Pet Project):**
```
GET /dashls/{userId}/{token}
```

The token is passed as a URL path parameter. This is **NOT secure** for production use.

**Why this is problematic:**
- ✗ Tokens appear in server logs
- ✗ Tokens visible in browser history
- ✗ Tokens cached by proxies/CDNs
- ✗ Tokens visible in network monitoring tools
- ✗ Tokens can be leaked through referrer headers

**Future Improvements (TODO):**

1. **Use Authorization Header (Recommended):**
```bash
curl -H "Authorization: Bearer {token}" \
  http://localhost:8080/dashls/{userId}
```

2. **Use POST with Request Body:**
```bash
curl -X POST http://localhost:8080/dashls \
  -H "Content-Type: application/json" \
  -d '{"userId": "user123", "token": "abc123"}'
```

3. **Use Proper Authentication:**
- Implement JWT tokens
- Use Firebase Authentication on the client
- Verify Firebase ID tokens on the server
- Use session-based authentication

---

## Implementation Details

### Query Strategy

The `/dashls/{userId}/{token}` endpoint uses Firestore compound queries:

```kotlin
firestore.collection("data")
    .whereEqualTo("userId", userId)
    .whereEqualTo("token", token)
    .limit(1)
    .get()
```

**Requirements:**
- Firestore composite index may be required
- Create index: `data` collection with fields `userId` (Ascending) + `token` (Ascending)

**To create the index:**
1. Go to Firebase Console > Firestore Database > Indexes
2. Create composite index with:
   - Collection: `data`
   - Fields: `userId` (Ascending), `token` (Ascending)
   - Query scope: Collection

Or wait for the error message with a direct link to create the index when you first run the query.

### Response Fields

The endpoint explicitly returns only `userId` and `links`:
- ✓ `userId`: User identifier
- ✓ `links`: Array of link objects
- ✗ `token`: Omitted for security (not included in response)

### Error Handling

The endpoint validates:
1. Firebase initialization status
2. Required parameters (userId, token)
3. Data existence
4. Query execution errors

All errors return appropriate HTTP status codes and descriptive JSON error messages.
