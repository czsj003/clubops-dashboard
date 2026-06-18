# Day 7 Notes - Squad Page and Player Main Panel

## What I built

Today I built the first frontend version of the Squad page and the Football Manager style player main panel.

I added:

- AppLayout
- Squad page
- Player detail page
- Player header
- Position panel
- Mini pitch
- Attribute panels
- Player info panel
- Contract panel
- Currency selector

## Routes added

```
/squad
/players/:id
```

## What the Squad page does
The Squad page loads:
- Club data
- Team data
- Player list data
It displays players grouped by team and allows clicking a player to open the player detail page.

## What the Player panel shows
The player panel shows:
- Name
- Age
- Nationality
- Club
- Team
- CA
- Positions
- Attributes
- Height and weight
- Personality
- Media handling style
- Foot ability
- Contract
- Bonuses
- Wage
- Squad number
- Transfer value placeholder
- Currency selector

## Goalkeeper vs outfield player
Goalkeepers use a different layout from outfield players.
Outfield players show:
- Technical
- Mental
- Physical
Goalkeepers show:
- Goalkeeping
- Mental
- Physical
- Limited Technical

## Features intentionally excluded
I decided not to build:
- Player relationships
- Playing history
- Full career stats
- Advanced transfer value engine
- Player edit form
- Contract edit form
These features would make the project too large and are not necessary for the current resume goal.

## What I learned
How to design frontend types based on backend DTOs
How to build a complex detail page from grouped API data
How to render different UI layouts based on player type
How to split a complex page into reusable components
How to build an FM-style panel without overbuilding unnecessary features

## Interview explanation
I built the player panel as a data-driven UI. The backend returns grouped attributes, positions, contract data, value data, and derived fields such as personality and media handling style. The frontend uses isGoalkeeper to decide whether to render an outfield player layout or a goalkeeper layout. This keeps the UI flexible while avoiding unnecessary features such as relationships or full playing history.

## Day 7 Polish
I refined the player main panel to more closely match the Football Manager overview page.

Changes made:
- Removed the Overview / Personal / Performance / Career tabs because only the overview panel is included in this project.
- Removed the bottom Contract / Season Stats / Career Stats panels.
- Moved contract details into a modal opened by clicking the Wage card.
- Fixed attribute ordering to match the game-style layout.
- Changed the badge next to the player name from CA to squad number.
- Added display labels for reputation instead of raw numbers.
- Added display labels for language fluency instead of raw numbers.
- Added display labels for foot strength instead of raw numbers.
- Updated contract wage and bonus display so they follow the selected currency.
- Adjusted mini pitch position coordinates.