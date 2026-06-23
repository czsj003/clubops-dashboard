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
REGISTRATION_MODE=INVITE_ONLY
INVITE_CODE=
SPRING_PROFILES_ACTIVE=production
PORT=8080
```

`JWT_SECRET` must be a long random secret and must not be committed.

Multiple CORS origins can be supplied as a comma-separated list.

`REGISTRATION_MODE` can be `OPEN`, `INVITE_ONLY`, or `DISABLED`. For a private
hosted deployment, use `INVITE_ONLY` and configure `INVITE_CODE` in the hosting
provider environment. Do not commit a real invite code.

## Frontend

```text
VITE_API_BASE_URL=https://your-backend-domain.com/api
```

Vite environment variables are embedded during the frontend build. Configure
this value before running `npm run build`.

## Private deployment flow

1. Create managed MySQL and copy the JDBC values into Render.
2. Deploy the backend to Render using `backend/Dockerfile`.
3. Confirm `/api/health` returns `{"status":"ok"}`.
4. Deploy the frontend to Vercel with `frontend` as the root directory.
5. Set `VITE_API_BASE_URL` in Vercel to the Render backend `/api` URL.
6. Copy the Vercel domain into `CORS_ALLOWED_ORIGINS` on Render and redeploy.

Keep the real `JWT_SECRET`, `DB_PASSWORD`, and `INVITE_CODE` out of Git.
