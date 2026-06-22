# Day 12 Notes - Stability, Player Form Polish, Account Types, and Local Admin

## What I built

Day 12 focused on product stability, player creation, and permissions.

Added:

- Ethnicity, hair color, hair length, and skin color selection
- More nationality choices
- Secondary nationality editing
- Optional, team-unique squad numbers
- NORMAL and VIP account types
- VIP-only developer mode
- Country-based player value bands
- Unified API error handling
- A local-only administration system

## Player appearance

The player creation form now exposes the appearance fields that already exist
in the player model. The backend enums and frontend options use the same values.

## Nationalities

Primary and secondary nationalities use a shared option list. A primary
nationality cannot also be selected as a secondary nationality.

## Squad numbers

Squad numbers may be empty. When present, a number must be between 1 and 99 and
must be unique within the selected team. The same number may be used by a
different team.

## Club reputation

Club reputation was removed from the coach-facing entity, DTOs, registration,
and frontend displays.

## User account types

Accounts are either:

- NORMAL
- VIP

NORMAL users can create players and use the coach interface. They cannot call
player update/delete APIs, enter developer edit, or receive raw hidden
attribute groups.

VIP users can use developer edit and delete players.

## Value database

Player value bands now use:

- Country
- Reputation minimum
- Reputation maximum
- Base value
- Currency

League is no longer part of the value-band lookup or management model.
Overlapping ranges within one country are rejected.

## Local admin system

The local admin system is separate from the public React navigation. On the
local machine it is available at:

```text
http://localhost:5173/local-admin/
```

It can:

- View registered users
- Change NORMAL/VIP account type
- Delete users and related club data
- Add, edit, and delete country value bands

Its backend source, static frontend, and credentials are ignored by Git and are
not included in the public commit.

## Stability work

Frontend pages now display backend error messages consistently. The backend has
a global exception handler, and automated tests cover account defaults, VIP
access, optional squad numbers, duplicate squad numbers, value bands, and
player value calculation.
