# Day 8 Notes - Player Management

## What I built

Today I added player management tools around the existing squad and player
detail panels.

Added:

- Player search and filters
- Structured player creation
- Developer attribute editing
- Player deletion
- Contract input
- Team and position selection

## Day 8 Correction - Goalkeeper Attribute Rules

I fixed an important rule conflict in developer mode.

Originally, developer mode rejected every attribute with value 0. That was
wrong because outfield players should have goalkeeping attributes set to 0.

Updated rules:

- Outfield players have goalkeeping attributes locked at 0.
- Outfield players do not show goalkeeping attributes in developer edit mode.
- Outfield players cannot have Goalkeeper rated 20.
- Goalkeepers are locked to Goalkeeper as their natural position.
- Goalkeeper attributes are editable only for goalkeeper players.
- Non-goalkeeper attributes still cannot be 0 in developer mode.

I also redesigned the new player form and developer edit page to use grouped
form sections instead of raw JSON. This makes the app feel more like a real
coach-facing management tool.
