# Day 8 Notes - Search, Global Currency, Developer Mode, and Player CRUD

## What I built

Today I expanded the app from a player viewer into a player management tool.

I added:
- Player search
- Team filtering
- Position filtering
- Nationality filtering
- Global currency context
- Currency selector on the squad page
- Global currency persistence through localStorage
- New player creation
- Player deletion
- Developer edit mode
- Hidden attribute editing

## Scope change
I removed staff management from the project scope.
The app is now focused on helping coaches view and manage players.

## Hidden attributes
CA and PA are hidden attributes.
They are not shown on the main squad page.
They are only visible and editable in developer mode.

## Developer mode rules

In developer mode:
- Attributes cannot be 0.
- Normal attributes must be 1-20.
- CA, PA, and reputation values must be 1-200.
- PA cannot be lower than CA.
- At least one position must be 20.
- If Goalkeeper is 20, all other stored positions must be 1.

## Player creation

Coaches can now create new players by entering:
- Personal information
- Team
- Position
- Nationality
- Attributes
- Contract data

## Player deletion
A player can be deleted from the club from the player panel.

Deleting a player also deletes:
- Contract bonuses
- Player contract
- Languages
- Secondary nationalities
- Positions
- Attributes

## What I learned
- How to build filtered backend APIs
- How to keep hidden attributes out of the normal UI
- How to use a global React context for currency
- How to design a developer-only editing workflow
- How to perform cascading manual deletion safely
- How to reduce project scope to keep the product focused

## Interview explanation
I added player search and filtering to make the squad page usable as a management tool. I also introduced a global currency system so money values are consistent across the squad page, player panel, contract modal, and future finance pages. CA and PA are treated as hidden attributes, so they are not displayed in the normal coach interface. They are only visible in developer mode, where player attributes can be edited with stricter validation.

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
