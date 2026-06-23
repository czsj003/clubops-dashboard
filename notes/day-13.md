# Day 13 Notes - Languages, Position Display, Admin Value Bands, and Value Formula

## What I built

Today I improved four systems:

- Player language handling
- Position panel display
- Local admin value-band editing
- Player market value calculation

## Languages

Players automatically receive a native language based on primary nationality.
Native language is always fluency 10 and cannot be changed or removed.

Users can add extra languages with fluency 1-10. The player panel shows the
native language first and sorts remaining languages by fluency.

## Position display

The position panel only lists positions rated 12 or higher and sorts them from
highest to lowest.

Position colors:

- 1: no color
- 2-4: red
- 5-8: dark orange
- 9-11: orange
- 12-14: light yellow
- 15-17: light green
- 18-20: green

The mini pitch still shows every position point using the same color scale.

## Local admin value-band editor

The local-only editor displays all standard ranges from 1-10 through 191-200
at once. A single Save All operation replaces the selected country's table.

Blank fields are saved as 0. Values no longer need to be multiples of 1000.

## Market value formula

The previous formula added the same absolute potential premium in every
country. This made high-potential players unrealistically expensive in lower
market countries.

Potential growth now uses:

- Country value-band anchor
- Effective reputation from current, home, and world reputation
- Current ability
- Age
- Potential gap
- Position and versatility
- Country-specific potential factor
- Country-specific upside cap

England retains a strong premium for elite young prospects. China and other
lower-market countries limit that premium relative to their local value-band
anchor.
