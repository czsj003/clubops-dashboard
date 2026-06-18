# ClubOps Dashboard

ClubOps Dashboard is a full-stack football club operations management dashboard built with React, TypeScript, Java Spring Boot, and MySQL.

## Purpose

This project is designed to practice and demonstrate:

- Java Spring Boot backend development
- MySQL relational database design
- REST API development
- JWT authentication
- CRUD operations
- SQL aggregation queries
- React TypeScript frontend development
- Dashboard analytics and charts

## Tech Stack

### Frontend

- React
- TypeScript
- Vite
- Axios
- React Router
- Recharts

### Backend

- Java
- Spring Boot
- Spring Data JPA
- Spring Security
- Maven

### Database

- MySQL

## Current Status

## Day 1 Progress

- Project folders created
- Spring Boot backend initialized
- React TypeScript frontend initialized
- MySQL database created
- Backend health check endpoint added

## Day 2 Progress

Implemented authentication foundation:
- Created User entity and repository
- Added password hashing with BCrypt
- Implemented register API
- Implemented login API
- Implemented JWT token generation and validation
- Added JWT authentication filter
- Protected backend routes with Spring Security
- Added `/api/auth/me`
- Created frontend Login and Register pages
- Added AuthContext for global authentication state
- Added ProtectedRoute
- Configured Axios to attach JWT token automatically

## Day 3 Progress

Implemented the Club and Team foundation:
- Created Club entity
- Created Team entity
- Added Country enum
- Added TeamType enum
- Added ClubRepository and TeamRepository
- Updated registration flow to automatically create a Club
- Added country-based team system design
- England now creates:
  - First Team
  - U21
  - U18
- Added GET /api/club
- Added PUT /api/club
- Added GET /api/teams
- Updated frontend registration form with club name and country system
- Updated Dashboard to display club and team information

## Day 4 Progress

Implemented the first version of the Football Manager style player data model.

Added:
- Player entity
- PlayerAttribute entity
- PlayerPosition entity
- PlayerLanguage entity
- PlayerSecondaryNationality entity
- Player repositories
- CountryCode enum
- LanguageCode enum
- PlayerPositionType enum
- Appearance enums
- Development player seed endpoint

Created the following tables:
- players
- player_attributes
- player_positions
- player_languages
- player_secondary_nationalities

Important design decisions:
- Player identity/profile data is stored separately from ability attributes.
- Positions are stored in a separate table because each player can have multiple position ratings.
- Languages are stored in a separate table because each player can speak multiple languages.
- Secondary nationalities are stored in a separate table because a player can have multiple secondary nationalities.
- PA cannot be lower than CA.
- Goalkeeping attributes are stored as 0 for non-goalkeepers for now.

Additional Day 4 position rules:
- Each player must have at least one position rated 20.
- If Goalkeeper is rated 20, all other stored outfield positions must be rated 1.
- Position combination rules are validated in the service layer because a single PlayerPosition entity cannot inspect all positions for the same player.

## Day 5 Progress

Implemented the backend data contract for the Football Manager style player panel.

Added:
- GET /api/players
- GET /api/players/{id}
- Player list DTO
- Player detail DTO
- Attribute group DTO
- Position DTO
- Language DTO
- Secondary nationality DTO
- Player personality enum
- Media handling style enum
- Player personality calculator
- Media handling style calculator
- Goalkeeper detection logic
- Position rule validation

Important rules:
- Each player must have at least one position rated 20.
- If Goalkeeper is rated 20, all other stored positions must be rated 1.
- Personality is calculated from hidden attributes.
- Media handling style is calculated from hidden attributes.
- Goalkeeper and outfield player panels use different frontend display logic.

## Day 6 Progress

Completed the remaining backend data required for the player main panel.

Added:
- Player contract model
- Contract bonus model
- Currency system foundation
- Player estimated transfer value placeholder
- Contract data in player detail API
- Bonus data in player detail API
- Currency list in player detail API
- Weekly wage display in player list API

Contract fields include:
- Start date
- End date
- Contract type
- Wage amount
- Wage display period
- Wage currency
- Team
- Squad number
- Release clause
- Contract bonuses

Contract bonus rules:
- Appearance Fee is automatically generated as weekly wage / 5.
- Unused Substitute Fee is automatically generated as weekly wage / 20.
- Other bonuses are optional.

Currency system:
- GBP
- EUR
- TRY
- SAR
- CNY
- USD
- BRL
- ARS

Transfer value:
- Added estimated value in GBP as a placeholder.
- Full transfer value calculation will be implemented later.

## Day 7 Progress

Built the first frontend version of the Football Manager style squad and player panel.

Added:
- App layout with sidebar and topbar
- Squad page
- Team tabs
- Player list grouped by team
- Player detail route
- Football Manager style player header
- Position panel with mini pitch
- Attribute panels
- Different outfield and goalkeeper layouts
- Player info panel
- Contract panel
- Currency selector for transfer value display

Current frontend routes:
- `/squad`
- `/players/:id`

The player panel currently focuses on the main overview panel only. Relationships, playing history, full career stats, and advanced transfer value calculation are intentionally excluded from this version.

Polish:
- Removed unused player detail tabs.
- Removed bottom panels that are outside the current project scope.
- Added a contract modal opened from the Wage card.
- Updated attribute ordering to match the Football Manager style panel.
- Added currency conversion for contract wage and bonuses.
- Changed the player header badge to show squad number instead of CA.
- Added label-based display for reputation, languages, and foot ability.