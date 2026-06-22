import type { CountryCode } from "../types/player";

export const nationalityOptions: {
  value: CountryCode;
  label: string;
}[] = [
  { value: "ENGLAND", label: "England" },
  { value: "SCOTLAND", label: "Scotland" },
  { value: "WALES", label: "Wales" },
  { value: "NORTHERN_IRELAND", label: "Northern Ireland" },
  { value: "IRELAND", label: "Ireland" },
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
  { value: "JAPAN", label: "Japan" },
  { value: "SOUTH_KOREA", label: "South Korea" },
  { value: "USA", label: "United States" },
  { value: "CANADA", label: "Canada" },
  { value: "MEXICO", label: "Mexico" },
  { value: "BRAZIL", label: "Brazil" },
  { value: "ARGENTINA", label: "Argentina" },
  { value: "NIGERIA", label: "Nigeria" },
  { value: "GHANA", label: "Ghana" },
  { value: "SENEGAL", label: "Senegal" },
  { value: "MOROCCO", label: "Morocco" },
  { value: "OTHER", label: "Other" },
];
