import type { Country, FootballLeague } from "./auth";

export interface Club {
  id: number;
  name: string;
  country: Country;
  league: FootballLeague;
  leagueGroup: string | null;
  season: string;
  reputation: number;
}

export type TeamType =
  | "FIRST_TEAM"
  | "U21"
  | "U20"
  | "U19"
  | "U18"
  | "B_TEAM"
  | "SECOND_TEAM"
  | "II_TEAM"
  | "RESERVE_TEAM";

export interface Team {
  id: number;
  name: string;
  type: TeamType;
  displayOrder: number;
}
