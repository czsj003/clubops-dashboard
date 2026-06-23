# Day 16 Notes - Private Deployment

## Goal

Today I prepared the app for a private hosted deployment.

The public GitHub repository remains available for local setup, but the hosted
version uses invite-only registration.

## Deployment stack

- Frontend: Vercel
- Backend: Render
- Database: managed cloud MySQL

## Access control

The hosted app should use:

```env
REGISTRATION_MODE=INVITE_ONLY
```

Only users with the invite code can register.

Normal users can create and view players but cannot use developer mode.

VIP users can use developer mode.

## Backend

The backend is prepared as a Docker-based Render web service.

Important environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGINS`
- `REGISTRATION_MODE`
- `INVITE_CODE`

The backend exposes:

```text
/api/health
```

Expected response:

```json
{"status":"ok"}
```

## Frontend

The frontend is prepared for Vercel.

Important environment variable:

```env
VITE_API_BASE_URL=https://your-render-backend-url/api
```

## Database

The app uses managed MySQL.

Hibernate uses:

```env
JPA_DDL_AUTO=update
```

for the private demo deployment.

## Security notes

- Invite code is not committed to GitHub.
- JWT secret is not committed to GitHub.
- Database password is not committed to GitHub.
- Local admin tooling is not deployed.
- Hosted URL is not listed publicly in README.

## What still requires manual work

- Create the cloud MySQL database.
- Create the Render web service.
- Add real backend secrets in Render.
- Create the Vercel project.
- Add `VITE_API_BASE_URL` in Vercel.
- Copy the Vercel URL back into Render `CORS_ALLOWED_ORIGINS`.
- Test invite-only registration against the hosted app.
