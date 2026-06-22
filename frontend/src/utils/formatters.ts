import type {
    ContractBonusType,
    CurrencyCode,
    PlayerContractType,
    PlayerPositionType,
    WageDisplayPeriod,
} from "../types/player";

export function formatEnum(value: string | null | undefined) {
    if (!value) return "N/A";

    return value
        .toLowerCase()
        .split("_")
        .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
        .join(" ");
}

export function formatPosition(type: PlayerPositionType) {
    const map: Record<PlayerPositionType, string> = {
        GOALKEEPER: "GK",
        DEFENDER_LEFT: "DL",
        DEFENDER_CENTRAL: "DC",
        DEFENDER_RIGHT: "DR",
        DEFENSIVE_MIDFIELDER: "DM",
        WING_BACK_LEFT: "WBL",
        WING_BACK_RIGHT: "WBR",
        MIDFIELDER_LEFT: "ML",
        MIDFIELDER_CENTRAL: "MC",
        MIDFIELDER_RIGHT: "MR",
        ATTACKING_MIDFIELDER_LEFT: "AML",
        ATTACKING_MIDFIELDER_CENTRAL: "AMC",
        ATTACKING_MIDFIELDER_RIGHT: "AMR",
        STRIKER: "ST",
    };

    return map[type];
}

export function formatContractType(type: PlayerContractType) {
    return formatEnum(type);
}

export function formatWagePeriod(period: WageDisplayPeriod) {
    switch (period) {
        case "WEEKLY":
            return "p/w";
        case "MONTHLY":
            return "p/m";
        case "YEARLY":
            return "p/a";
        default:
            return "";
    }
}

export function formatBonusType(type: ContractBonusType) {
    return formatEnum(type);
}

export function getCurrencySymbol(currency: CurrencyCode) {
    switch (currency) {
        case "GBP":
            return "£";
        case "EUR":
            return "€";
        case "TRY":
            return "₺";
        case "SAR":
            return "﷼";
        case "CNY":
            return "¥";
        case "USD":
            return "$";
        case "BRL":
            return "R$";
        case "ARS":
            return "$";
        default:
            return "";
    }
}

export function formatMoney(
    amount: number | null | undefined,
    currency: CurrencyCode = "GBP"
) {
    if (amount === null || amount === undefined) return "N/A";

    const symbol = getCurrencySymbol(currency);
    const rounded = Math.round(amount);

    if (Math.abs(amount) >= 1_000_000) {
        return `${symbol}${(amount / 1_000_000).toFixed(2)}M`;
    }

    return `${symbol}${rounded.toLocaleString()}`;
}

export function convertFromGbp(
    amountInGbp: number | null | undefined,
    rateFromGbp: number
) {
    if (amountInGbp === null || amountInGbp === undefined) return null;
    return amountInGbp * rateFromGbp;
}

export function formatHeight(cm: number) {
    return `${cm} cm`;
}

export function formatWeight(kg: number) {
    return `${kg} kg`;
}

function parseLocalDate(date: string) {
    const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(date);

    if (!match) {
        return new Date(date);
    }

    const [, year, month, day] = match;
    return new Date(Number(year), Number(month) - 1, Number(day));
}

export function formatDate(date: string) {
    return parseLocalDate(date).toLocaleDateString();
}

export function formatReputation(value: number | null | undefined) {
    if (value === null || value === undefined) return "Unknown";

    if (value < 10) return "Obscure";
    if (value < 25) return "Local";
    if (value < 50) return "Regional";
    if (value < 100) return "National";
    if (value < 150) return "Continental";
    return "Worldwide";
}

export function formatLanguageFluency(value: number | null | undefined) {
    if (value === null || value === undefined) return "Unknown";

    if (value <= 1) return "None";
    if (value <= 4) return "Basic";
    if (value <= 6) return "Good";
    return "Fluent";
}

export function formatFootAbility(value: number | null | undefined) {
    if (value === null || value === undefined) return "Unknown";

    if (value <= 4) return "Very Weak";
    if (value <= 8) return "Weak";
    if (value <= 11) return "Reasonable";
    if (value <= 14) return "Fairly Strong";
    if (value <= 17) return "Strong";
    return "Very Strong";
}

export function formatDateShort(date: string) {
    const parsed = parseLocalDate(date);

    return parsed.toLocaleDateString(undefined, {
        year: "numeric",
        month: "numeric",
        day: "numeric",
    });
}
