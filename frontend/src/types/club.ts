import type { Country } from "./auth";

export interface Club {
  id: number;
  name: string;
  country: Country;
  league: string;
  season: string;
  reputation: number;
}

export type TeamType =
  | "FIRST_TEAM"
  | "U21"
  | "U18"
  | "B_TEAM"
  | "U23"
  | "U19"
  | "RESERVE_TEAM";

export interface Team {
  id: number;
  name: string;
  type: TeamType;
  displayOrder: number;
}