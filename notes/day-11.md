# Day 11 Notes - Multi-position Players, Goalkeeper Panel, and Player Value System

## What I built

Day 11 improved three systems:

- Player position editing
- Goalkeeper panel layout
- Player value calculation and data entry

## Multi-position support

Outfield players can now have multiple natural positions.

Example:

```text
AML = 20
AMR = 20
ST = 15
GK = 1
```

The player displays as `AML / AMR`.

Outfield players cannot have Goalkeeper rated 20 and must have at least one
outfield position rated 20. Goalkeepers must have Goalkeeper rated 20, with
all stored outfield positions locked to 1.

Both the player creation page and developer editor expose all position
ratings.

## Goalkeeper panel

The goalkeeper overview now uses:

- Positions and mini pitch
- Goalkeeping
- Mental
- Physical, technical, and outfield rating
- Player information

This is closer to the compact Football Manager goalkeeper layout.

## Player value database

Hibernate creates an empty table named:

```text
player_value_bands
```

No country or league value data is preloaded.

Each row stores:

- Country
- League
- World-reputation minimum and maximum
- Base value
- Currency

The backend rejects a league that does not belong to the selected country and
rejects overlapping reputation ranges for the same country and league.

## Where to enter country value data

Start the frontend and backend, sign in, and click **Value Database** in the
left sidebar.

Direct route:

```text
http://localhost:5173/value-bands
```

Choose the country and league, then add non-overlapping reputation bands from
1 to 200. Existing rows can be edited or deleted from the table.

## Value calculation

When a matching value band exists:

```text
base value
× CA multiplier
× PA multiplier
× age multiplier
× reputation multiplier
```

The configured currency is converted back to GBP before multipliers are
applied. If no band matches, the app uses a CA/PA fallback formula.

Player value is refreshed after player creation and developer editing.

## Why this design

Country and league market data belongs in a database, not hardcoded source
code. The visual data-entry page allows the model to grow one country and
league at a time without code changes.
