# ClubOps Coach App

ClubOps is a Football Manager-inspired coach and player management application
built with Spring Boot, React, TypeScript, and MySQL.

## Live Demo

[View Live Demo](https://clubops-dashboard.vercel.app/)

Backend API: https://clubops-dashboard.onrender.com

## Features

- User registration and JWT login
- Private hosted registration modes
- Club setup by country, league, and league group
- Country-specific first-team and youth-team generation
- Squad search and filtering
- Detailed outfield and goalkeeper player panels
- Player creation with multi-position ratings
- Native and additional player languages
- Secondary nationalities and appearance data
- Contract and release-clause management
- Finance and wage summaries
- Global currency display
- NORMAL and VIP account permissions
- VIP developer mode for hidden attributes
- Country-based player market value engine

The league system remains part of the core club model and is available for
future competition, schedule, and season development. Player value bands are
country-based and do not depend on league level.

## Tech Stack

- Java 21
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- MySQL
- React
- TypeScript
- Vite
- CSS

## Key Domain Logic

### Club and League System

Registration supports country, league, and optional league-group selection.
The selected football structure determines the teams generated for the club.

### Player Position System

Outfield players can have multiple natural positions rated 20. Goalkeepers are
locked to Goalkeeper 20 and outfield positions 1.

### Player Potential System

Players support fixed PA, random PA, and Football Manager-style negative
potential ranges.

### Contract Rules

Release-clause requirements depend on the club country. Squad numbers are
optional and unique inside each team.

### Player Value Engine

Player value uses country value bands, current ability, potential ability, age,
three reputation values, position, versatility, currency conversion, and
country-specific market upside limits.

## Local Setup

### Requirements

- Java 21
- Node.js and npm
- MySQL 8

### Backend

Copy the values from [backend/.env.example](backend/.env.example) into your
local environment or the ignored `application-local.properties` file.

```bash
cd backend
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The backend defaults to `http://localhost:8080`.

### Frontend

Copy [frontend/.env.example](frontend/.env.example) to `.env.local`, then run:

```bash
cd frontend
npm install
npm run dev
```

The frontend defaults to `http://localhost:5173`.

## Environment Variables

Deployment values are documented in
[deployment/env-template.md](deployment/env-template.md).

Required backend configuration:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGINS`
- `REGISTRATION_MODE`
- `INVITE_CODE` when using invite-only registration

Required frontend configuration:

- `VITE_API_BASE_URL`

Use `SPRING_PROFILES_ACTIVE=production` and keep
`JPA_DDL_AUTO=update` for the demo deployment.

## Deployment Status

This project is primarily designed to run locally from the public repository.
People who want to use the app can download it, configure their own database,
and run it themselves.

A private hosted version may be available for the project owner and selected
users. Public self-service registration should not be left open in that hosted
environment. New hosted users require an invite code or manual approval.

This keeps the GitHub repository useful while preventing unrestricted access to
the private hosted demo database.

## Private Hosted Demo

A private hosted version may be available on request.

The hosted version uses invite-only registration to prevent unrestricted access
to the demo database. Public self-service registration is disabled for the
hosted environment.

The public repository is intended to support local setup and code review.

## Deployment Architecture

The private hosted version uses:

- Vercel for the React/Vite frontend
- Render for the Spring Boot backend
- Managed cloud MySQL for the database

The backend is configured with environment variables for database access, JWT
signing, CORS, and invite-only registration. Local admin tooling is not deployed
or documented as part of the hosted app.

## Registration Modes

The backend supports three registration modes:

| Mode | Behavior |
|---|---|
| `OPEN` | Anyone can register |
| `INVITE_ONLY` | Users must provide a valid invite code |
| `DISABLED` | Registration is closed |

For local development:

```env
REGISTRATION_MODE=OPEN
INVITE_CODE=
```

For a private hosted version:

```env
REGISTRATION_MODE=INVITE_ONLY
INVITE_CODE=your-private-invite-code
```

Do not commit a real invite code to GitHub.

## Build

Backend:

```bash
cd backend
./mvnw clean package
```

Frontend:

```bash
cd frontend
npm run build
```

## Deployment Notes

The deployed application requires:

- A MySQL database
- Backend environment variables
- A long random JWT secret
- The deployed frontend origin in CORS
- The deployed backend `/api` URL in `VITE_API_BASE_URL`
- Demo data suitable for a portfolio walkthrough

Detailed private deployment steps are documented in
[deployment/private-deployment-guide.md](deployment/private-deployment-guide.md).

The historical day-by-day development notes are preserved in
[README-dev-log.md](README-dev-log.md) and the [notes](notes) directory.
