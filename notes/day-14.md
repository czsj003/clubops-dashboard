# Day 14 Notes - Deployment Preparation

## What I did

Today I prepared the application for deployment without choosing a hosting
provider or adding Docker.

## Configuration

Environment-specific values now use environment variables:

- Database URL
- Database username
- Database password
- JPA schema behavior
- SQL logging
- JWT secret
- JWT expiration
- CORS allowed origins
- Backend port
- Frontend API base URL

The production profile keeps `ddl-auto=update`, disables SQL logging, and
disables Open Session in View.

## League system

The club country, league, and league-group system remains unchanged. Player
market value is independent of league, but league data remains important for
future competition, schedule, and season systems.

## Local admin cleanup

The local admin backend, static frontend, credentials, and local configuration
remain ignored by Git. The public React application has no admin route or
navigation link.

## Build checks

Deployment requires:

```bash
cd backend
./mvnw clean package

cd frontend
npm run build
```

## Deployment requirements

- MySQL database
- Backend environment variables
- Frontend API environment variable
- Correct CORS origin
- Demo account and representative data

## Security notes

- JWT secrets must not be committed.
- Local admin credentials must not be committed or deployed.
- NORMAL users cannot access developer editing.
- VIP users can access developer editing.
