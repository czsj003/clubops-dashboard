# Day 10 Notes - Club Country, League, Team System, and Release Clause Rules

## What I built

Today I implemented the club setup system.

When a user registers, they now select:

- Country
- League
- League group when required

The selected country, league, and optional group are saved to the club.

## Supported countries

The app now supports England, Spain, Italy, Germany, France, Portugal,
the Netherlands, Belgium, Turkey, Saudi Arabia, China, the USA, Brazil,
and Argentina.

## League system

Some leagues require groups, including National League North/South,
Primera Federación, Segunda Federación, Tercera Federación, Serie C,
Serie D, Regionalliga, Belgian Amateur First Division, and TFF 2. Lig.

Backend validation ensures that a league belongs to the selected country,
that grouped leagues receive a valid group, and that non-grouped leagues do
not receive one.

## Team system

Default teams are now created based on the club country. This reflects the
different reserve and youth structures used across football systems.

## Release clause rules

- Spain, Portugal, Brazil, and Argentina: required
- France: forbidden
- Other supported countries: optional

The player creation form shows, requires, or hides the release clause field
according to the club country. The backend enforces the same policy so clients
cannot bypass it.

## Why this matters

This lays the foundation for a future player value engine based on country,
league level, CA, PA, age, contract, and reputation.

## Interview explanation

I added a country and league setup system during registration. The selected
country controls league options, default team creation, and release clause
rules. I separated those rules into dedicated services so new football systems
can be added without hardcoding policy in controllers.
