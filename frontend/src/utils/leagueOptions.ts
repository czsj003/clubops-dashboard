import type { Country, FootballLeague } from "../types/auth";

export interface LeagueOption {
  value: FootballLeague;
  label: string;
  groups?: { value: string; label: string }[];
}

export const countryOptions: { value: Country; label: string }[] = [
  { value: "ENGLAND", label: "England" },
  { value: "SPAIN", label: "Spain" },
  { value: "ITALY", label: "Italy" },
  { value: "GERMANY", label: "Germany" },
  { value: "FRANCE", label: "France" },
  { value: "PORTUGAL", label: "Portugal" },
  { value: "NETHERLANDS", label: "Netherlands" },
  { value: "BELGIUM", label: "Belgium" },
  { value: "TURKEY", label: "Turkey" },
  { value: "SAUDI_ARABIA", label: "Saudi Arabia" },
  { value: "CHINA", label: "China" },
  { value: "USA", label: "United States" },
  { value: "BRAZIL", label: "Brazil" },
  { value: "ARGENTINA", label: "Argentina" },
];

function numberedGroups(count: number) {
  return Array.from({ length: count }, (_, index) => ({
    value: `GROUP_${index + 1}`,
    label: `Group ${index + 1}`,
  }));
}

export const leagueOptionsByCountry: Record<Country, LeagueOption[]> = {
  ENGLAND: [
    { value: "PREMIER_LEAGUE", label: "Premier League" },
    { value: "EFL_CHAMPIONSHIP", label: "EFL Championship" },
    { value: "EFL_LEAGUE_ONE", label: "EFL League One" },
    { value: "EFL_LEAGUE_TWO", label: "EFL League Two" },
    { value: "NATIONAL_LEAGUE", label: "National League" },
    {
      value: "NATIONAL_LEAGUE_NORTH_SOUTH",
      label: "National League North/South",
      groups: [
        { value: "NORTH", label: "North" },
        { value: "SOUTH", label: "South" },
      ],
    },
  ],
  SPAIN: [
    { value: "LA_LIGA", label: "La Liga" },
    { value: "LA_LIGA_2", label: "La Liga 2" },
    {
      value: "PRIMERA_FEDERACION",
      label: "Primera Federación",
      groups: [
        { value: "GROUP_A", label: "Group A" },
        { value: "GROUP_B", label: "Group B" },
      ],
    },
    { value: "SEGUNDA_FEDERACION", label: "Segunda Federación", groups: numberedGroups(5) },
    { value: "TERCERA_FEDERACION", label: "Tercera Federación", groups: numberedGroups(18) },
  ],
  ITALY: [
    { value: "SERIE_A", label: "Serie A" },
    { value: "SERIE_B", label: "Serie B" },
    { value: "SERIE_C", label: "Serie C", groups: numberedGroups(3) },
    { value: "SERIE_D", label: "Serie D", groups: numberedGroups(9) },
  ],
  GERMANY: [
    { value: "BUNDESLIGA", label: "Bundesliga" },
    { value: "BUNDESLIGA_2", label: "2. Bundesliga" },
    { value: "LIGA_3", label: "3. Liga" },
    {
      value: "REGIONALLIGA",
      label: "Regionalliga",
      groups: [
        { value: "WEST", label: "West" },
        { value: "BAVARIA", label: "Bavaria" },
        { value: "NORTHEAST", label: "Northeast" },
        { value: "NORTH", label: "North" },
        { value: "SOUTHWEST", label: "Southwest" },
      ],
    },
  ],
  FRANCE: [
    { value: "LIGUE_1", label: "Ligue 1" },
    { value: "LIGUE_2", label: "Ligue 2" },
    { value: "CHAMPIONNAT_NATIONAL", label: "Championnat National" },
    { value: "FRANCE_REGIONAL", label: "France Regional League", groups: numberedGroups(3) },
  ],
  PORTUGAL: [
    { value: "PRIMEIRA_LIGA", label: "Primeira Liga" },
    { value: "LIGA_PORTUGAL_2", label: "Liga Portugal 2" },
    { value: "PORTUGAL_LIGA_3", label: "Liga 3" },
    { value: "PORTUGAL_CAMPEONATO_NACIONAL", label: "Campeonato Nacional" },
  ],
  NETHERLANDS: [
    { value: "EREDIVISIE", label: "Eredivisie" },
    { value: "EERSTE_DIVISIE", label: "Eerste Divisie" },
  ],
  BELGIUM: [
    { value: "BELGIAN_PRO_LEAGUE", label: "Belgian Pro League" },
    { value: "CHALLENGER_PRO_LEAGUE", label: "Challenger Pro League" },
    {
      value: "BELGIAN_AMATEUR_FIRST_DIVISION",
      label: "Belgian Amateur First Division",
      groups: [
        { value: "FRENCH_SPEAKING", label: "French-speaking" },
        { value: "FLEMISH", label: "Flemish" },
      ],
    },
  ],
  TURKEY: [
    { value: "SUPER_LIG", label: "Süper Lig" },
    { value: "TFF_1_LIG", label: "TFF 1. Lig" },
    {
      value: "TFF_2_LIG",
      label: "TFF 2. Lig",
      groups: [
        { value: "RED", label: "Red Group" },
        { value: "WHITE", label: "White Group" },
      ],
    },
  ],
  SAUDI_ARABIA: [
    { value: "SAUDI_PRO_LEAGUE", label: "Saudi Pro League" },
    { value: "SAUDI_FIRST_DIVISION", label: "Saudi First Division" },
  ],
  CHINA: [
    { value: "CHINESE_SUPER_LEAGUE", label: "Chinese Super League" },
    { value: "CHINA_LEAGUE_ONE", label: "China League One" },
  ],
  USA: [{ value: "MLS", label: "MLS" }],
  BRAZIL: [
    { value: "BRASILEIRAO_SERIE_A", label: "Brasileirão Série A" },
    { value: "BRASILEIRAO_SERIE_B", label: "Brasileirão Série B" },
  ],
  ARGENTINA: [
    { value: "ARGENTINA_PRIMERA_DIVISION", label: "Argentine Primera División" },
    { value: "ARGENTINA_PRIMERA_NACIONAL", label: "Primera Nacional" },
  ],
};

export function formatCountry(value: Country) {
  return countryOptions.find((country) => country.value === value)?.label ?? value;
}

export function formatLeague(value: FootballLeague) {
  for (const leagues of Object.values(leagueOptionsByCountry)) {
    const found = leagues.find((league) => league.value === value);
    if (found) return found.label;
  }
  return value;
}

export function formatLeagueGroup(group: string | null | undefined) {
  if (!group) return "";

  return group
    .toLowerCase()
    .split("_")
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}
