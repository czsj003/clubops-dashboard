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

## Day 8 Progress

Refined the app into a player-focused coach tool.

Added:
- Player search
- Team filter
- Position filter
- Nationality filter
- Global currency selection
- Currency persistence with localStorage
- Currency conversion on Squad page
- Currency conversion on Player page
- Currency conversion in Contract modal
- New player creation
- Player deletion
- Developer edit mode
- Hidden attribute editing in developer mode

Scope change:
- Staff management has been removed from the project scope.
- The app now focuses on squad and player management for coaches.

Important rules:
- CA and PA are hidden attributes and are not shown on the main squad page.
- Hidden attributes are only visible in developer edit mode.
- Developer edit mode does not allow attributes to be 0.
- In developer mode, normal attributes must be 1-20.
- In developer mode, CA / PA / reputation values must be 1-200.

correction:
- Fixed developer-mode validation for goalkeeper attributes.
- Outfield players now keep goalkeeping attributes at 0.
- Outfield players do not show goalkeeping attributes in developer edit mode.
- Outfield players cannot be created or edited as natural goalkeepers unless their main position is Goalkeeper.
- Goalkeeper players are locked to Goalkeeper as their natural position.
- Goalkeeper attributes are only editable for goalkeeper players.
- Rebuilt the player creation form into structured sections:
  - Personal information
  - Team and position
  - Ability and reputation
  - Feet
  - Personal attributes
  - Mental attributes
  - Physical attributes
  - Technical attributes
  - Goalkeeping attributes when applicable
  - Contract
- Rebuilt developer edit mode into grouped attribute panels instead of raw JSON.

## Day 9 Progress

Added contracts, finance, and creation rules.

Fixed:

- Squad list no longer displays CA or PA.
- Squad list now displays team status, value, and wage.
- Facilities was removed from the app.

Added:

- Contracts page
- Contract list API
- Finance page
- Finance summary API
- Wage spending calculations
- Weekly, monthly, and yearly wage summaries
- Theoretical max wage cost including bonuses

Player creation rules:

- Attributes can be 0 during creation.
- 0 means random.
- Developer edit mode still does not allow 0, except non-goalkeeper goalkeeping attributes.
- PA can never be lower than CA.
- PA can be fixed, random, or negative potential.
- Negative potential is only allowed for players under 22.
- Players with CA 170 or higher cannot use negative potential.
- Negative potential ranges must be able to produce PA greater than or equal to CA.

## Day 10 Progress

Implemented club setup by country and league.

Supported club countries:

- England, Spain, Italy, Germany, France
- Portugal, Netherlands, Belgium, Turkey
- Saudi Arabia, China, USA, Brazil, Argentina

Registration now provides country-specific league choices and league groups.
The backend validates that the selected league belongs to the country and that
grouped leagues receive a valid group.

Default teams are created from each country's football structure:

- England: First Team, U21, U18
- Spain: First Team, B Team, U19
- Italy: First Team, U20, U18
- Germany: First Team, II Team, U19
- France: First Team, Second Team, U19
- Portugal: First Team, B Team, U19
- Netherlands: First Team, Second Team, U19
- Belgium: First Team, B Team, U18
- Turkey: First Team, U19
- Saudi Arabia: First Team, Reserve Team, U19
- China: First Team, U21, U19
- USA: First Team
- Brazil and Argentina: First Team, Reserve Team, U20

Release clause rules:

- Spain, Portugal, Brazil, and Argentina require release clauses.
- France forbids release clauses.
- Other supported countries allow optional release clauses.

## Day 11 Progress

Added multi-position support, a goalkeeper-specific overview, and the first
database-backed player value system.

Position changes:

- Outfield players can have multiple natural positions rated 20.
- Outfield players must have at least one outfield position rated 20.
- Goalkeepers are locked to Goalkeeper 20 and outfield positions 1.
- Player creation and developer edit expose every position rating.
- Multiple natural positions display as `AML / AMR`.

Goalkeeper overview:

- Goalkeeping and mental attributes remain dedicated panels.
- Physical, selected technical attributes, and outfield rating share a compact
  goalkeeper side panel.

Player value system:

- Added the empty `player_value_bands` database table.
- Each row stores country, league, world-reputation range, base value, and
  currency.
- Final value adjusts the matching base value by age, CA, PA, and reputation.
- A fallback formula is used when no database band matches.
- Player value is recalculated after player creation and developer updates.
- Overlapping reputation ranges for the same country are rejected.

### Where to enter player value data

Value data is managed only through the private local admin interface:

```text
http://localhost:5173/local-admin/
```

After local admin login, select a country and enter non-overlapping
world-reputation ranges from 1 to 200. No country value data is preloaded.

## Day 12 Progress

Improved player creation, validation, account permissions, and value database
structure.

Changes:

- Added ethnicity, hair color, hair length, and skin color selection.
- Added more nationality options and secondary nationality editing.
- Removed club reputation from the coach-facing model and UI.
- Squad numbers are optional and unique inside each team.
- Added `NORMAL` and `VIP` account types.
- NORMAL users can create players but cannot edit or delete them.
- VIP users can access developer edit and hidden player attributes.
- Value bands are country-based and no longer depend on league.
- Removed value database editing from the public application.
- Added unified frontend API error handling.
- Added backend global exception responses.

Local-only administration:

- A private local admin system manages users and country value bands.
- It can change accounts between NORMAL and VIP.
- Deleting a user also removes their club, teams, players, contracts, and
  related player data.
- The local admin implementation and credentials are excluded from Git using
  `.gitignore`.

## Day 13 Progress

Improved player languages, position display, local value-band editing, and
market value calculation.

Changes:

- Players automatically receive a native language from primary nationality.
- Native language is always saved as fluency 10 and cannot be removed.
- Creation and developer edit support additional languages from fluency 1-10.
- Languages display with native language first, then by fluency.
- The position panel only lists positions rated 12 or higher.
- Position tags and pitch dots use seven rating color bands.
- The local admin value editor displays all 20 standard reputation ranges.
- All country ranges can be saved in one operation.
- Blank value fields are saved as 0 and values such as 2500 are accepted.
- Potential value growth is adjusted by country market strength.
- Low-market countries cap age and potential inflation while mature markets
  retain a stronger prospect premium.
