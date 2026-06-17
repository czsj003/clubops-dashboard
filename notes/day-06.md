# Day 6 Notes - Player Contract, Bonus, Currency, and Value Data

## What I built

Today I completed the remaining backend data required for the player main panel.

I added:

- PlayerContract entity
- ContractBonus entity
- CurrencyCode enum
- CurrencyService
- Contract DTOs
- Player transfer value placeholder
- Contract data in PlayerDetailResponse
- Currency data in PlayerDetailResponse
- Weekly wage and value data in PlayerListItemResponse
- Seed contracts and bonuses for players

## Features added

Player contracts now include:

- Contract start date
- Contract end date
- Contract type
- Wage amount
- Wage currency
- Wage display period
- Team
- Squad number
- Release clause
- Contract bonuses

## Contract bonus rules

Appearance Fee is auto-generated:
```
Appearance Fee = weekly wage / 5
```

Unused Substitute Fee is auto-generated:
```
Unused Substitute Fee = weekly wage / 20
```

Other bonus types are optional:
- Goal Bonus
- Assist Bonus
- Clean Sheet Bonus
- International Cap Bonus
- Currency system

The system now supports:
- GBP
- EUR
- TRY
- SAR
- CNY
- USD
- BRL
- ARS
GBP is the base currency.
Exchange rates are currently hardcoded in CurrencyService. In the future, this can be moved to a database table or external source.

## Transfer value
I added estimated_value_in_gbp to players as a placeholder.
The full transfer value engine is not implemented yet because it requires country, league, age, ability, potential, reputation, and market-specific multipliers.

## Release clause rules for the future
Release clause rules should depend on country:
- Spain: release clause required
- France: release clause forbidden
- Other countries: release clause optional
For Day 6, England players have no release clause by default.

## What I learned
How to model player contracts separately from players
How to model contract bonuses as one-to-many data
How to add a currency abstraction before building finance features
How to extend PlayerDetailResponse without mixing UI logic into entities
Why transfer value calculation should be a separate future engine
## Interview explanation
I modeled player contracts as a separate table because contract data has its own lifecycle and is different from player identity or player attributes. Contract bonuses are stored in a separate table because each contract can have multiple bonuses. I also added a basic currency system with GBP as the base currency, which will later be shared by player contracts, staff contracts, player values, and club finances.