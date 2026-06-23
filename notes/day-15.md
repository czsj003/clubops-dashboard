# Day 15 Notes - Private Hosted Access Control

## Goal

Today I added access control for a private hosted version of the app.

The public GitHub repository can still be used for local setup, but the hosted
version can be restricted so only invited users can register.

## Registration modes

The backend now supports three registration modes:

- `OPEN`
- `INVITE_ONLY`
- `DISABLED`

## OPEN

Anyone can register.

This is useful for local development.

## INVITE_ONLY

Users must provide a valid invite code.

This is the recommended mode for a private hosted demo.

## DISABLED

Registration is fully closed.

This can be used if accounts are created manually.

## Why this matters

I do not want the hosted app to be publicly open to anyone.

With invite-only registration, I can deploy the app privately, share it with
selected people, and prevent random users from creating accounts or polluting
the database.

## Environment variables

Backend:

```env
REGISTRATION_MODE=INVITE_ONLY
INVITE_CODE=your-private-code
```

## Security note

The invite code should never be committed to GitHub.
