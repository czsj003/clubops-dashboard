import { useEffect, useState } from "react";
import api from "../api/axios";
import { useCurrency } from "../context/CurrencyContext";
import type { CurrencyCode, FinanceSummary } from "../types/player";
import { convertFromGbp, formatMoney } from "../utils/formatters";

function Finance() {
  const [finance, setFinance] = useState<FinanceSummary | null>(null);
  const [error, setError] = useState("");
  const { selectedCurrency, selectedRate } = useCurrency();

  useEffect(() => {
    async function loadFinance() {
      try {
        const response = await api.get<FinanceSummary>("/finance");
        setFinance(response.data);
      } catch {
        setError("Failed to load finance summary.");
      }
    }

    loadFinance();
  }, []);

  if (!finance && !error) {
    return <main className="content-page">Loading finance...</main>;
  }

  if (!finance) {
    return (
      <main className="content-page">
        <div className="error-message">{error}</div>
      </main>
    );
  }

  return (
    <main className="content-page">
      <div className="form-page-header">
        <div>
          <p className="eyebrow">Finance</p>
          <h1>Squad Wage Overview</h1>
          <p>
            Wage spending for {finance.playerCount} contracted players. Bonus
            totals are theoretical.
          </p>
        </div>
      </div>

      <section className="finance-grid">
        <FinanceCard title="Weekly Base Wage" value={finance.weeklyBaseWage} selectedRate={selectedRate} selectedCurrency={selectedCurrency} />
        <FinanceCard title="Monthly Base Wage" value={finance.monthlyBaseWage} selectedRate={selectedRate} selectedCurrency={selectedCurrency} />
        <FinanceCard title="Yearly Base Wage" value={finance.yearlyBaseWage} selectedRate={selectedRate} selectedCurrency={selectedCurrency} />
        <FinanceCard title="Weekly Auto Bonuses" value={finance.weeklyAutoBonusCost} selectedRate={selectedRate} selectedCurrency={selectedCurrency} />
        <FinanceCard title="Monthly Auto Bonuses" value={finance.monthlyAutoBonusCost} selectedRate={selectedRate} selectedCurrency={selectedCurrency} />
        <FinanceCard title="Yearly Auto Bonuses" value={finance.yearlyAutoBonusCost} selectedRate={selectedRate} selectedCurrency={selectedCurrency} />
        <FinanceCard title="Weekly Theoretical Max" value={finance.weeklyMaxCost} selectedRate={selectedRate} selectedCurrency={selectedCurrency} highlight />
        <FinanceCard title="Monthly Theoretical Max" value={finance.monthlyMaxCost} selectedRate={selectedRate} selectedCurrency={selectedCurrency} highlight />
        <FinanceCard title="Yearly Theoretical Max" value={finance.yearlyMaxCost} selectedRate={selectedRate} selectedCurrency={selectedCurrency} highlight />
      </section>
    </main>
  );
}

function FinanceCard({
  title,
  value,
  selectedRate,
  selectedCurrency,
  highlight = false,
}: {
  title: string;
  value: number;
  selectedRate: number;
  selectedCurrency: CurrencyCode;
  highlight?: boolean;
}) {
  return (
    <div className={highlight ? "finance-card highlight" : "finance-card"}>
      <span>{title}</span>
      <strong>
        {formatMoney(convertFromGbp(value, selectedRate), selectedCurrency)}
      </strong>
    </div>
  );
}

export default Finance;
