import type { CountryCode, LanguageCode } from "../types/player";

export const languageOptions: {
  value: LanguageCode;
  label: string;
}[] = [
  { value: "ENGLISH", label: "English" },
  { value: "SPANISH", label: "Spanish" },
  { value: "ITALIAN", label: "Italian" },
  { value: "GERMAN", label: "German" },
  { value: "FRENCH", label: "French" },
  { value: "PORTUGUESE", label: "Portuguese" },
  { value: "DUTCH", label: "Dutch" },
  { value: "TURKISH", label: "Turkish" },
  { value: "ARABIC", label: "Arabic" },
  { value: "CHINESE", label: "Chinese" },
  { value: "KOREAN", label: "Korean" },
  { value: "JAPANESE", label: "Japanese" },
];

export function getDefaultLanguageForNationality(
  nationality: CountryCode
): LanguageCode {
  switch (nationality) {
    case "SPAIN":
    case "ARGENTINA":
    case "MEXICO":
      return "SPANISH";
    case "ITALY":
      return "ITALIAN";
    case "GERMANY":
      return "GERMAN";
    case "FRANCE":
    case "BELGIUM":
    case "SENEGAL":
      return "FRENCH";
    case "PORTUGAL":
    case "BRAZIL":
      return "PORTUGUESE";
    case "NETHERLANDS":
      return "DUTCH";
    case "TURKEY":
      return "TURKISH";
    case "SAUDI_ARABIA":
    case "MOROCCO":
      return "ARABIC";
    case "CHINA":
      return "CHINESE";
    case "JAPAN":
      return "JAPANESE";
    case "SOUTH_KOREA":
      return "KOREAN";
    default:
      return "ENGLISH";
  }
}
