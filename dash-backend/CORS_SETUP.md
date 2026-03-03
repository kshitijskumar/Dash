# CORS Configuration Guide

## Overview

The backend uses environment-based CORS configuration for security while maintaining development flexibility.

## How It Works

The application reads allowed origins from the `ALLOWED_ORIGINS` environment variable. If not set, it defaults to localhost for local development.

## Configuration

### Local Development (Default)

No configuration needed! The following origins are allowed by default:
- `http://localhost:8081`
- `http://localhost:8080`
- `http://127.0.0.1:8081`
- `http://127.0.0.1:8080`

### Production Deployment

Set the `ALLOWED_ORIGINS` environment variable with a comma-separated list of allowed origins.

#### On Render.com:

1. Go to your service dashboard
2. Navigate to **Environment** tab
3. Add environment variable:
   - **Key**: `ALLOWED_ORIGINS`
   - **Value**: `https://yourdomain.com,https://www.yourdomain.com,http://localhost:8081`

#### Format:
```
ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com,http://localhost:8081
```

**Important**: No spaces between origins, only commas.

## Examples

### Production with localhost access:
```bash
ALLOWED_ORIGINS=https://dash.example.com,http://localhost:8081
```

This allows:
- ✅ Production frontend at `https://dash.example.com`
- ✅ Local development at `http://localhost:8081`
- ❌ Any other origin

### Multiple environments:
```bash
ALLOWED_ORIGINS=https://dash.example.com,https://staging.dash.example.com,http://localhost:8081,http://localhost:3000
```

### Production only (no localhost):
```bash
ALLOWED_ORIGINS=https://dash.example.com,https://www.dash.example.com
```

## Security Benefits

1. **No `anyHost()` in production**: Only explicitly allowed origins can access the API
2. **Localhost remains functional**: Developers can still test against production API
3. **Easy to manage**: Update allowed origins via environment variables without code changes
4. **Audit trail**: Clear list of who can access the API

## Testing

### Check allowed origins:
After deployment, check the logs for:
```
CORS: Allowed origin - http://localhost:8081
CORS: Allowed origin - https://yourdomain.com
```

### Test from browser console:
```javascript
fetch('https://dash-q1xx.onrender.com/dashls/kshitij/ksh1234')
  .then(r => r.json())
  .then(console.log)
```

If CORS is working:
- ✅ From allowed origin: Returns data
- ❌ From blocked origin: CORS error

## Troubleshooting

### CORS error persists:
1. Check `ALLOWED_ORIGINS` is set correctly (no spaces!)
2. Verify the origin in browser matches exactly (including protocol and port)
3. Check backend logs for "CORS: Allowed origin" messages
4. Restart the backend after changing environment variables

### Origin not working:
- Make sure to include protocol (`http://` or `https://`)
- Include port if not standard (`http://localhost:8081` not `http://localhost`)
- Check for typos in domain name

## Best Practices

✅ **DO:**
- Include localhost for development
- Use HTTPS in production
- Keep the list minimal
- Document which origins are allowed

❌ **DON'T:**
- Use `anyHost()` in production
- Add wildcards (not supported)
- Expose sensitive endpoints without CORS
- Forget to update when deploying new frontends
