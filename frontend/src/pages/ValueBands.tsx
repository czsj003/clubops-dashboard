import axios from "axios";
import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import api from "../api/axios";
import type { Country, FootballLeague } from "../types/auth";
import type { CurrencyCode, CurrencyInfo } from "../types/player";
import {
  countryOptions,
  formatCountry,
  formatLeague,
  leagueOptionsByCountry,
} from "../utils/leagueOptions";
import { formatMoney } from "../utils/formatters";

interface ValueBand {
  id: number;
  country: Country;
  league: FootballLeague;
  reputationMin: number;
  reputationMax: number;
  baseValue: number;
  currency: CurrencyCode;
}

function ValueBands() {
  const [country, setCountry] = useState<Country>("ENGLAND");
  const [league, setLeague] =
    useState<FootballLeague>("EFL_CHAMPIONSHIP");
  const [bands, setBands] = useState<ValueBand[]>([]);
  const [currencies, setCurrencies] = useState<CurrencyInfo[]>([]);
  const [reputationMin, setReputationMin] = useState(1);
  const [reputationMax, setReputationMax] = useState(10);
  const [baseValue, setBaseValue] = useState(100_000);
  const [currency, setCurrency] = useState<CurrencyCode>("GBP");
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const leagues = leagueOptionsByCountry[country];

  useEffect(() => {
    async function loadCurrencies() {
      try {
        const response = await api.get<CurrencyInfo[]>("/currencies");
        setCurrencies(response.data);
      } catch {
        setError("Failed to load currencies.");
      }
    }

    loadCurrencies();
  }, []);

  useEffect(() => {
    loadBands();
    // The selected country and league define the active database slice.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [country, league]);

  async function loadBands() {
    setLoading(true);
    setError("");

    try {
      const params = new URLSearchParams({ country, league });
      const response = await api.get<ValueBand[]>(`/value-bands?${params}`);
      setBands(response.data);
    } catch (requestError) {
      setError(getErrorMessage(requestError, "Failed to load value bands."));
    } finally {
      setLoading(false);
    }
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError("");

    if (reputationMin > reputationMax) {
      setError("Reputation minimum cannot be greater than maximum.");
      return;
    }

    setSaving(true);

    try {
      const payload = {
        country,
        league,
        reputationMin,
        reputationMax,
        baseValue,
        currency,
      };

      if (editingId === null) {
        await api.post("/value-bands", payload);
      } else {
        await api.put(`/value-bands/${editingId}`, payload);
      }

      resetEditor();
      await loadBands();
    } catch (requestError) {
      setError(getErrorMessage(requestError, "Failed to save value band."));
    } finally {
      setSaving(false);
    }
  }

  function startEditing(band: ValueBand) {
    setEditingId(band.id);
    setReputationMin(band.reputationMin);
    setReputationMax(band.reputationMax);
    setBaseValue(band.baseValue);
    setCurrency(band.currency);
    setError("");
  }

  function resetEditor() {
    setEditingId(null);
    setReputationMin(1);
    setReputationMax(10);
    setBaseValue(100_000);
    setCurrency("GBP");
  }

  async function deleteBand(id: number) {
    if (!window.confirm("Delete this player value band?")) {
      return;
    }

    try {
      await api.delete(`/value-bands/${id}`);
      if (editingId === id) resetEditor();
      await loadBands();
    } catch (requestError) {
      setError(getErrorMessage(requestError, "Failed to delete value band."));
    }
  }

  return (
    <main className="content-page">
      <div className="form-page-header">
        <div>
          <p className="eyebrow">Developer Data</p>
          <h1>Player Value Database</h1>
          <p>
            Enter base market values for each country, league, and world
            reputation range. The database starts empty.
          </p>
        </div>
      </div>

      <section className="value-band-guide">
        <strong>Data entry location</strong>
        <span>
          Sidebar → Value Database. Select a country and league, then add
          non-overlapping reputation ranges from 1 to 200.
        </span>
      </section>

      {error && <div className="error-message value-band-error">{error}</div>}

      <section className="team-section value-band-workspace">
        <div className="value-band-filter">
          <label>
            <span>Country</span>
            <select
              value={country}
              onChange={(event) => {
                const nextCountry = event.target.value as Country;
                setCountry(nextCountry);
                setLeague(leagueOptionsByCountry[nextCountry][0].value);
                resetEditor();
              }}
            >
              {countryOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </label>

          <label>
            <span>League</span>
            <select
              value={league}
              onChange={(event) => {
                setLeague(event.target.value as FootballLeague);
                resetEditor();
              }}
            >
              {leagues.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </label>
        </div>

        <form className="value-band-form" onSubmit={handleSubmit}>
          <label>
            <span>Reputation min</span>
            <input
              required
              type="number"
              min={1}
              max={200}
              value={reputationMin}
              onChange={(event) =>
                setReputationMin(Number(event.target.value))
              }
            />
          </label>

          <label>
            <span>Reputation max</span>
            <input
              required
              type="number"
              min={1}
              max={200}
              value={reputationMax}
              onChange={(event) =>
                setReputationMax(Number(event.target.value))
              }
            />
          </label>

          <label>
            <span>Base value</span>
            <input
              required
              type="number"
              min={0}
              step={1000}
              value={baseValue}
              onChange={(event) => setBaseValue(Number(event.target.value))}
            />
          </label>

          <label>
            <span>Currency</span>
            <select
              value={currency}
              onChange={(event) =>
                setCurrency(event.target.value as CurrencyCode)
              }
            >
              {(currencies.length
                ? currencies
                : [{ code: "GBP" as CurrencyCode, name: "British Pound" }]
              ).map((option) => (
                <option key={option.code} value={option.code}>
                  {option.code} - {option.name}
                </option>
              ))}
            </select>
          </label>

          <div className="value-band-form-actions">
            <button type="submit" disabled={saving}>
              {saving
                ? "Saving..."
                : editingId === null
                  ? "Add Band"
                  : "Save Changes"}
            </button>
            {editingId !== null && (
              <button
                type="button"
                className="secondary-button"
                onClick={resetEditor}
              >
                Cancel
              </button>
            )}
          </div>
        </form>
      </section>

      <section className="team-section">
        <div className="section-title-row">
          <div>
            <h2>
              {formatCountry(country)} · {formatLeague(league)}
            </h2>
            <p>{bands.length} configured reputation bands</p>
          </div>
        </div>

        {loading ? (
          <div className="empty-state">Loading value bands...</div>
        ) : bands.length === 0 ? (
          <div className="empty-state">
            No data has been entered for this league yet. Add the first
            reputation range above.
          </div>
        ) : (
          <div className="value-band-table">
            <div className="value-band-table-header">
              <span>World reputation</span>
              <span>Base value</span>
              <span>Currency</span>
              <span>Actions</span>
            </div>

            {bands.map((band) => (
              <div key={band.id} className="value-band-row">
                <span>
                  {band.reputationMin}–{band.reputationMax}
                </span>
                <strong>{formatMoney(band.baseValue, band.currency)}</strong>
                <span>{band.currency}</span>
                <div className="value-band-row-actions">
                  <button type="button" onClick={() => startEditing(band)}>
                    Edit
                  </button>
                  <button
                    type="button"
                    className="danger-button"
                    onClick={() => deleteBand(band.id)}
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}

function getErrorMessage(error: unknown, fallback: string) {
  if (axios.isAxiosError(error)) {
    const message = error.response?.data?.message;
    if (typeof message === "string") return message;
  }
  return fallback;
}

export default ValueBands;
