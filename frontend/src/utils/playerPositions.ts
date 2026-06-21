import type { PlayerPositionType } from "../types/player";

export const positionOptions: {
  value: PlayerPositionType;
  label: string;
}[] = [
  { value: "GOALKEEPER", label: "Goalkeeper" },
  { value: "DEFENDER_LEFT", label: "Defender Left" },
  { value: "DEFENDER_CENTRAL", label: "Defender Central" },
  { value: "DEFENDER_RIGHT", label: "Defender Right" },
  { value: "DEFENSIVE_MIDFIELDER", label: "Defensive Midfielder" },
  { value: "WING_BACK_LEFT", label: "Wing-Back Left" },
  { value: "WING_BACK_RIGHT", label: "Wing-Back Right" },
  { value: "MIDFIELDER_LEFT", label: "Midfielder Left" },
  { value: "MIDFIELDER_CENTRAL", label: "Midfielder Central" },
  { value: "MIDFIELDER_RIGHT", label: "Midfielder Right" },
  { value: "ATTACKING_MIDFIELDER_LEFT", label: "Attacking Midfielder Left" },
  { value: "ATTACKING_MIDFIELDER_CENTRAL", label: "Attacking Midfielder Central" },
  { value: "ATTACKING_MIDFIELDER_RIGHT", label: "Attacking Midfielder Right" },
  { value: "STRIKER", label: "Striker" },
];

export function createPositionRatings(
  naturalPosition?: PlayerPositionType
): Record<PlayerPositionType, number> {
  const ratings = Object.fromEntries(
    positionOptions.map((position) => [position.value, 1])
  ) as Record<PlayerPositionType, number>;

  if (naturalPosition) {
    ratings[naturalPosition] = 20;
  }

  return ratings;
}
