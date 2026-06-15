# Day 2 Notes - User Auth and JWT

## What I built

Today I implemented the authentication foundation for ClubOps Dashboard.

I built:

- User entity
- UserRepository
- Register API
- Login API
- JWT generation
- JWT validation
- JWT authentication filter
- Protected backend routes
- `/api/auth/me`
- Frontend Login page
- Frontend Register page
- AuthContext
- ProtectedRoute
- Axios token interceptor

## What I learned

- How Spring Security uses UserDetails
- How BCrypt password hashing works
- How JWT stores the authenticated user's identity
- How to protect backend routes with a filter
- How to send JWT tokens through the Authorization header
- How React Context can store global authentication state
- How frontend route protection works

## Database changes

Created the `users` table through JPA.

Important fields:

- id
- name
- email
- password_hash
- created_at
- updated_at

## APIs added

### Register

```txt
POST /api/auth/register
```

## Login
POST /api/auth/login

## Current User
GET /api/auth/me

## Requires
Authorization: Bearer token

## Hardest problem
To be filled in after implementation.
## How I solved it
To be filled in after implementation.
## Interview explanation
I implemented stateless authentication using JWT. When a user registers or logs in, the backend validates the credentials and returns a signed JWT. The frontend stores this token in localStorage and attaches it to future API requests through an Axios interceptor. On the backend, a custom JWT filter reads the token from the Authorization header, validates it, loads the user from the database, and sets the authenticated user in Spring Security's SecurityContext.