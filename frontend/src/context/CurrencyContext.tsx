import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import type { CurrencyCode, CurrencyInfo } from "../types/player";

interface CurrencyContextValue {
  selectedCurrency: CurrencyCode;
  setSelectedCurrency: (currency: CurrencyCode) => void;
  availableCurrencies: CurrencyInfo[];
  setAvailableCurrencies: (currencies: CurrencyInfo[]) => void;
  selectedRate: number;
}

const CurrencyContext = createContext<CurrencyContextValue | undefined>(
  undefined
);

export function CurrencyProvider({ children }: { children: ReactNode }) {
  const [selectedCurrency, setSelectedCurrencyState] = useState<CurrencyCode>(
    (localStorage.getItem("clubops_currency") as CurrencyCode) || "GBP"
  );

  const [availableCurrencies, setAvailableCurrencies] = useState<CurrencyInfo[]>(
    []
  );

  const setSelectedCurrency = useCallback((currency: CurrencyCode) => {
    localStorage.setItem("clubops_currency", currency);
    setSelectedCurrencyState(currency);
  }, []);

  const selectedRate = useMemo(
    () =>
      availableCurrencies.find(
        (currency) => currency.code === selectedCurrency
      )?.rateFromGbp ?? 1,
    [availableCurrencies, selectedCurrency]
  );

  const value = useMemo(
    () => ({
      selectedCurrency,
      setSelectedCurrency,
      availableCurrencies,
      setAvailableCurrencies,
      selectedRate,
    }),
    [selectedCurrency, setSelectedCurrency, availableCurrencies, selectedRate]
  );

  return (
    <CurrencyContext.Provider value={value}>
      {children}
    </CurrencyContext.Provider>
  );
}

// eslint-disable-next-line react-refresh/only-export-components
export function useCurrency() {
  const context = useContext(CurrencyContext);

  if (!context) {
    throw new Error("useCurrency must be used inside CurrencyProvider");
  }

  return context;
}
