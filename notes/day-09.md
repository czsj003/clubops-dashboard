# Day 9 Notes - Contracts, Finance, and Player Creation Rules

## What I built

Today I completed the contract and finance pages and improved player creation
rules.

## Squad list fix

The squad list no longer shows CA or PA because those are hidden attributes.

The squad list now shows:

- Name
- Age
- Nationality
- Team status
- Value
- Wage

## Contracts

I added a Contracts page that displays all player contracts in one list.

Contract list includes:

- Player
- Team
- Squad number
- Contract type
- Start date
- End date
- Wage
- Release clause

## Finance

I added a Finance page focused on wage spending.

The finance page calculates:

- Weekly base wage
- Monthly base wage
- Yearly base wage
- Weekly auto bonuses
- Monthly auto bonuses
- Yearly auto bonuses
- Weekly theoretical max cost
- Monthly theoretical max cost
- Yearly theoretical max cost

## Player creation rules

During player creation, attributes can be set to 0.

A value of 0 means random.

After the player is created, random values are resolved into actual numbers.

Developer mode still does not allow 0 for editable attributes.

## Potential ability rules

PA can be created in three ways:

- Fixed PA
- Random PA
- Negative potential

Negative potential ranges:

- -10: 170-200
- -9.5: 160-190
- -9: 150-180
- -8.5: 140-170
- -8: 130-160
- -7.5: 120-150
- -7: 110-140
- -6.5: 100-130
- -6: 90-120
- -5.5: 80-110
- -5: 70-100
- -4.5: 60-90
- -4: 50-80
- -3.5: 40-70
- -3: 30-60
- -2.5: 20-50
- -2: 10-40
- -1.5: 0-30
- -1: 0-20

Rules:

- PA can never be lower than CA.
- Negative potential is only allowed for players under 22.
- If CA is 170 or higher, negative potential is not allowed.
- A negative potential level is invalid if its maximum PA is lower than the player's CA.

## Scope update

Facilities was removed from the app.

The project is now focused on:

- Squad management
- Player profiles
- Contracts
- Finance
- Player creation and editing
