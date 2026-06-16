# Day 4 Notes - Football Manager Style Player Model

## What I built
Today I implemented the first version of the Player data model.

I created:
- Player entity
- PlayerAttribute entity
- PlayerPosition entity
- PlayerLanguage entity
- PlayerSecondaryNationality entity
- Player repositories
- Player enums
- Dev player seed endpoint

## What changed from the original plan
The original plan had a much simpler player model.

I expanded it because this project is intended to simulate a Football Manager style club operations dashboard. A simple player table would not be enough to represent player profiles, positions, languages, nationalities, and detailed attributes.

## Database changes
Created:
- players
- player_attributes
- player_positions
- player_languages
- player_secondary_nationalities

## Important relationships
Club 1 - N Player

Team 1 - N Player

Player 1 - 1 PlayerAttribute

Player 1 - N PlayerPosition

Player 1 - N PlayerLanguage

Player 1 - N PlayerSecondaryNationality

## Important business rules
- The player display name uses commonName if available.
- If commonName is not available, displayName uses firstName + lastName.
- PA cannot be lower than CA.
- Attributes use 0 as a future random-generation marker.
- Goalkeeping attributes are only meaningful for goalkeepers.
- Languages use a 1-10 fluency scale.
- Positions use a 1-20 rating scale.
- Each player must have at least one natural position rated 20.
- If Goalkeeper is rated 20, every other stored position must be rated 1.
- This rule cannot be fully validated inside the PlayerPosition entity because it depends on all position records for the same player.
- For now, the validation is implemented in the seed service. Later, it should be extracted into a reusable PlayerPositionValidator for the real Player Create/Update APIs.

## What I learned
- How to model complex player data in a relational database
- Why multi-value data should be placed in separate tables
- How to use one-to-one and one-to-many relationships in JPA
- How to use enum fields with EnumType.STRING
- How to seed related data in one transaction

## Hardest problem
The hardest problem was deciding how much of the Football Manager style player model to implement now.

## How I solved it
I split the player system into phases. Today I implemented only the core player profile and attribute model. Contracts, injuries, bans, relationships, playing history, and personality will be implemented later as separate modules.

## Interview explanation
The Player model is designed to support a Football Manager style dashboard. I separated identity data from ability attributes by using a Player table and a PlayerAttribute table. Multi-value data such as positions, languages, and secondary nationalities are stored in separate tables instead of being stored as comma-separated strings. This keeps the schema normalized and makes future filtering and analytics easier.