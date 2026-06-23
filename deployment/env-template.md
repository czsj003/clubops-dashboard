# Deployment Environment Variables

## Backend

```text
DB_URL=jdbc:mysql://<host>:<port>/<database>?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=
DB_PASSWORD=
JPA_DDL_AUTO=update
JPA_SHOW_SQL=false
JWT_SECRET=
JWT_EXPIRATION_MS=86400000
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
SPRING_PROFILES_ACTIVE=production
PORT=8080
```

`JWT_SECRET` must be a long random secret and must not be committed.

Multiple CORS origins can be supplied as a comma-separated list.

## Frontend

```text
VITE_API_BASE_URL=https://your-backend-domain.com/api
```

Vite environment variables are embedded during the frontend build. Configure
this value before running `npm run build`.
