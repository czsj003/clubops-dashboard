export type Country =
  | "ENGLAND"
  | "SPAIN"
  | "ITALY"
  | "GERMANY"
  | "FRANCE"
  | "PORTUGAL"
  | "NETHERLANDS"
  | "BELGIUM"
  | "TURKEY"
  | "SAUDI_ARABIA"
  | "CHINA"
  | "USA"
  | "BRAZIL"
  | "ARGENTINA";

export type FootballLeague =
  | "PREMIER_LEAGUE"
  | "EFL_CHAMPIONSHIP"
  | "EFL_LEAGUE_ONE"
  | "EFL_LEAGUE_TWO"
  | "NATIONAL_LEAGUE"
  | "NATIONAL_LEAGUE_NORTH_SOUTH"
  | "LA_LIGA"
  | "LA_LIGA_2"
  | "PRIMERA_FEDERACION"
  | "SEGUNDA_FEDERACION"
  | "TERCERA_FEDERACION"
  | "SERIE_A"
  | "SERIE_B"
  | "SERIE_C"
  | "SERIE_D"
  | "BUNDESLIGA"
  | "BUNDESLIGA_2"
  | "LIGA_3"
  | "REGIONALLIGA"
  | "LIGUE_1"
  | "LIGUE_2"
  | "CHAMPIONNAT_NATIONAL"
  | "FRANCE_REGIONAL"
  | "PRIMEIRA_LIGA"
  | "LIGA_PORTUGAL_2"
  | "PORTUGAL_LIGA_3"
  | "PORTUGAL_CAMPEONATO_NACIONAL"
  | "EREDIVISIE"
  | "EERSTE_DIVISIE"
  | "BELGIAN_PRO_LEAGUE"
  | "CHALLENGER_PRO_LEAGUE"
  | "BELGIAN_AMATEUR_FIRST_DIVISION"
  | "SUPER_LIG"
  | "TFF_1_LIG"
  | "TFF_2_LIG"
  | "SAUDI_PRO_LEAGUE"
  | "SAUDI_FIRST_DIVISION"
  | "CHINESE_SUPER_LEAGUE"
  | "CHINA_LEAGUE_ONE"
  | "MLS"
  | "BRASILEIRAO_SERIE_A"
  | "BRASILEIRAO_SERIE_B"
  | "ARGENTINA_PRIMERA_DIVISION"
  | "ARGENTINA_PRIMERA_NACIONAL";

export interface User {
  id: number;
  name: string;
  email: string;
  accountType: "NORMAL" | "VIP";
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  clubName: string;
  country: Country;
  league: FootballLeague;
  leagueGroup: string | null;
}

export interface LoginRequest {
  email: string;
  password: string;
}
