# Day 3 Notes - Club and Team System

## What I built

Today I implemented the Club and Team foundation.

I built:

- Club entity
- Team entity
- Country enum
- TeamType enum
- ClubRepository
- TeamRepository
- ClubService
- ClubController
- TeamService
- TeamController
- TeamSystemFactory
- Country-based default team creation
- Frontend register form updates
- Dashboard club and team display

## What changed from the original plan

The original plan used First Team, B Team, and U19 as default teams.

I changed this because football club structures vary by country.

For the first version, I implemented England as the default country system:

- First Team
- U21
- U18

Other countries are reserved for future expansion.

## Database changes

Created the `clubs` table.

Important fields:

- id
- user_id
- name
- country
- league
- season
- reputation
- created_at
- updated_at

Created the `teams` table.

Important fields:

- id
- club_id
- name
- type
- display_order
- created_at
- updated_at

## APIs added

### Get current club

```
GET /api/club
```

## Update current club
PUT /api/club

## Get current club teams
GET /api/teams

## What I learned
- How to model a one-to-one relationship between User and Club
- How to model a many-to-one relationship between Team and Club
- How to use enums in JPA with EnumType.STRING
- Why business rules should not be hardcoded directly inside AuthService
- How to use a factory class to create country-specific default teams
- How to use @Transactional when one operation creates multiple related records

## Hardest problem
The hardest problem was deciding how to handle default team creation because different countries use different football structures.
## How I solved it
I created a TeamSystemFactory that creates default teams based on the club's country. For now, England creates First Team, U21, and U18. Other countries are reserved for future implementation.
## Interview explanation
When a user registers, the system creates a User, a Club, and default Teams in one transaction. I used a country-based TeamSystemFactory because football club structures vary by country. For example, the England system in this project creates First Team, U21, and U18 teams. This keeps the registration flow clean and makes the business logic easier to extend later.