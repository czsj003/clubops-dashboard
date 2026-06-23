# Private Deployment Guide

This guide is for the private hosted version of ClubOps.

The public GitHub repository remains useful for local setup and code review.
The hosted app is private in practice because registration is invite-only and
local admin tooling is not deployed.

## Deployment stack

- Frontend: Vercel
- Backend: Render Docker web service
- Database: managed cloud MySQL

## 1. Managed MySQL

Create a managed MySQL database named `clubops_db`.

Record these values:

```text
DB_URL=jdbc:mysql://<host>:<port>/clubops_db?useSSL=true&requireSSL=true&serverTimezone=UTC
DB_USERNAME=<cloud-mysql-user>
DB_PASSWORD=<cloud-mysql-password>
```

If your provider gives you an official JDBC URL, prefer that URL.

## 2. Render backend

Create a Render Web Service from the GitHub repository.

Recommended settings:

```text
Name: clubops-backend
Root Directory: backend
Language: Docker
Dockerfile Path: Dockerfile
```

If you leave Render's root directory as the repository root, use:

```text
Dockerfile Path: backend/Dockerfile
```

Backend environment variables:

```env
DB_URL=
DB_USERNAME=
DB_PASSWORD=
JPA_DDL_AUTO=update
JPA_SHOW_SQL=false
JWT_SECRET=
JWT_EXPIRATION_MS=86400000
CORS_ALLOWED_ORIGINS=http://localhost:5173
REGISTRATION_MODE=INVITE_ONLY
INVITE_CODE=
SPRING_PROFILES_ACTIVE=production
PORT=8080
```

Do not commit real values for:

- `DB_PASSWORD`
- `JWT_SECRET`
- `INVITE_CODE`

After deployment, check:

```text
https://<render-backend-url>/api/health
```

Expected response:

```json
{"status":"ok"}
```

Also check:

```text
https://<render-backend-url>/api/auth/registration-config
```

Expected response:

```json
{"mode":"INVITE_ONLY"}
```

## 3. Vercel frontend

Create a Vercel project from the same GitHub repository.

Recommended settings:

```text
Framework Preset: Vite
Root Directory: frontend
Build Command: npm run build
Output Directory: dist
Install Command: npm install
```

Vercel environment variable:

```env
VITE_API_BASE_URL=https://<render-backend-url>/api
```

After changing Vercel environment variables, trigger a new deployment.

## 4. CORS回填

After Vercel gives you the production frontend URL, update the Render backend
environment variable:

```env
CORS_ALLOWED_ORIGINS=https://<vercel-frontend-url>,http://localhost:5173
```

Do not add a trailing slash to the Vercel URL.

Correct:

```text
https://clubops-dashboard.vercel.app
```

Wrong:

```text
https://clubops-dashboard.vercel.app/
```

Save the Render environment variable and redeploy the backend.

## 5. Smoke test

Test these URLs:

- `/register`
- `/login`
- `/squad`
- `/players/new`
- `/contracts`
- `/finance`

Expected registration behavior:

- Wrong invite code: rejected
- Empty invite code: rejected
- Correct invite code: account created

## 6. VIP account setup

The hosted deployment does not include local admin.

To make your own account VIP, run this in the cloud MySQL console:

```sql
UPDATE users
SET account_type = 'VIP'
WHERE email = 'your-email@example.com';
```

Log out and log back in after changing the account type.

## 7. Value database

Because local admin is not deployed, value bands can be managed by:

1. SQL in the cloud MySQL console
2. a local-only admin tool connected carefully to production
3. a future protected admin deployment

For the first private deployment, SQL is the safest simple option.
