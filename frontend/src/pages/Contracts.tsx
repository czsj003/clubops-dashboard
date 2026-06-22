import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import { useCurrency } from "../context/CurrencyContext";
import type { ContractListItem } from "../types/player";
import {
  convertFromGbp,
  formatContractType,
  formatDateShort,
  formatMoney,
  formatWagePeriod,
} from "../utils/formatters";
import { getApiErrorMessage } from "../utils/errorUtils";

function Contracts() {
  const [contracts, setContracts] = useState<ContractListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const { selectedCurrency, selectedRate } = useCurrency();

  useEffect(() => {
    async function loadContracts() {
      try {
        const response = await api.get<ContractListItem[]>("/contracts");
        setContracts(response.data);
      } catch (requestError) {
        setError(getApiErrorMessage(
          requestError,
          "Failed to load player contracts."
        ));
      } finally {
        setLoading(false);
      }
    }

    loadContracts();
  }, []);

  if (loading) {
    return <main className="content-page">Loading contracts...</main>;
  }

  return (
    <main className="content-page">
      <div className="form-page-header">
        <div>
          <p className="eyebrow">Contracts</p>
          <h1>Player Contracts</h1>
          <p>Review all player contracts, wages, squad numbers, and clauses.</p>
        </div>
      </div>

      {error && <div className="error-message">{error}</div>}

      <section className="team-section">
        <div className="contract-table-header">
          <span>Player</span>
          <span>Team</span>
          <span>Number</span>
          <span>Type</span>
          <span>Dates</span>
          <span>Wage</span>
          <span>Release Clause</span>
        </div>

        {contracts.length === 0 ? (
          <div className="empty-state">No player contracts found.</div>
        ) : (
          contracts.map((contract) => (
            <Link
              key={contract.playerId}
              to={`/players/${contract.playerId}`}
              className="contract-table-row"
            >
              <strong>{contract.playerName}</strong>
              <span>{contract.teamName}</span>
              <span>{contract.squadNumber ?? "N/A"}</span>
              <span>{formatContractType(contract.contractType)}</span>
              <span>
                {formatDateShort(contract.startDate)} -{" "}
                {formatDateShort(contract.endDate)}
              </span>
              <span>
                {formatMoney(
                  convertFromGbp(contract.wageAmount, selectedRate),
                  selectedCurrency
                )}{" "}
                {formatWagePeriod(contract.wageDisplayPeriod)}
              </span>
              <span>
                {contract.releaseClauseAmount
                  ? formatMoney(
                      convertFromGbp(
                        contract.releaseClauseAmount,
                        selectedRate
                      ),
                      selectedCurrency
                    )
                  : "None"}
              </span>
            </Link>
          ))
        )}
      </section>
    </main>
  );
}

export default Contracts;
