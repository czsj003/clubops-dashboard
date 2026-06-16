# Day 5 Notes - Player Panel Data API

## What I built

Today I built the backend data contract required for the Football Manager style player panel.

I implemented:

- Player list API
- Player detail API
- Player list response DTO
- Player detail response DTO
- Attribute group response DTO
- Position response DTO
- Language response DTO
- Secondary nationality response DTO
- Personality calculator
- Media handling style calculator
- Goalkeeper detection
- Position rule validation

## APIs added

### Get players

```
GET /api/players
```

Get player detail
GET /api/players/{id}
Important business rules
A player must have at least one position rated 20.
If Goalkeeper is rated 20, every other stored outfield position must be rated 1.
Personality is calculated from hidden attributes.
Media handling style is calculated from hidden attributes.
Goalkeepers and outfield players should use different panel layouts.
### Personality calculation
## Personality is calculated using hidden attributes such as:
- Professionalism
- Pressure
- Ambition
- Temperament
- Loyalty
- Sportsmanship
- Determination
- Controversy
- Leadership

### Media handling style calculation
## Media handling style is calculated using:
- Professionalism
- Pressure
- Temperament
- Loyalty
- Sportsmanship
- Controversy

## What I learned
How to design DTOs for a complex UI panel
How to separate entity models from response models
How to calculate derived fields in the service layer
Why goalkeeper and outfield player data should be displayed differently
Why personality and media handling style should not be stored directly when they can be derived from hidden attributes
## Interview explanation
The player panel API returns a detailed response designed for a Football Manager style profile page. The backend groups player attributes into ability, reputation, feet, personal, mental, physical, technical, and goalkeeping sections. It also calculates derived values such as personality and media handling style from hidden attributes. Goalkeepers are detected by checking if their Goalkeeper position rating is 20, which allows the frontend to render a different layout for goalkeeper profiles.