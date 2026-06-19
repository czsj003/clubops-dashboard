import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import type { Club, Team } from "../types/club";
import type { PlayerListItem } from "../types/player";
import { formatMoney } from "../utils/formatters";
import { useCurrency } from "../context/CurrencyContext";
import type { CurrencyCode, PlayerPositionType } from "../types/player";
import { convertFromGbp } from "../utils/formatters";

function Squad() {
  const [club, setClub] = useState<Club | null>(null);
  const [teams, setTeams] = useState<Team[]>([]);
  const [players, setPlayers] = useState<PlayerListItem[]>([]);
  const [activeTeamId, setActiveTeamId] = useState<number | "ALL">("ALL");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const {
    selectedCurrency,
    setSelectedCurrency,
    availableCurrencies,
    setAvailableCurrencies,
    selectedRate,
  } = useCurrency();

  const [search, setSearch] = useState("");
  const [position, setPosition] = useState<PlayerPositionType | "">("");
  const [nationality, setNationality] = useState("");

  useEffect(() => {
    async function loadSquadMetadata() {
      try {
        const [clubResponse, teamsResponse, currencyResponse] =
          await Promise.all([
            api.get<Club>("/club"),
            api.get<Team[]>("/teams"),
            api.get("/currencies"),
          ]);

        setClub(clubResponse.data);
        setTeams(teamsResponse.data);
        setAvailableCurrencies(currencyResponse.data);
      } catch {
        setError("Failed to load squad data.");
      } finally {
        setLoading(false);
      }
    }

    loadSquadMetadata();
  }, [setAvailableCurrencies]);

  useEffect(() => {
    async function loadPlayers() {
      const params = new URLSearchParams();

      if (search.trim()) params.set("search", search.trim());
      if (activeTeamId !== "ALL") params.set("teamId", String(activeTeamId));
      if (position) params.set("position", position);
      if (nationality) params.set("nationality", nationality);

      try {
        const response = await api.get<PlayerListItem[]>(
          `/players?${params}`
        );
        setPlayers(response.data);
      } catch {
        setError("Failed to filter squad players.");
      }
    }

    loadPlayers();
  }, [search, activeTeamId, position, nationality]);

  const filteredPlayers = useMemo(() => {
    if (activeTeamId === "ALL") return players;
    return players.filter((player) => player.teamId === activeTeamId);
  }, [players, activeTeamId]);

  const playersByTeam = useMemo(() => {
    return teams.map((team) => ({
      team,
      players: filteredPlayers.filter((player) => player.teamId === team.id),
    }));
  }, [teams, filteredPlayers]);

  if (loading) {
    return <main className="content-page">Loading squad...</main>;
  }

  if (error) {
    return <main className="content-page error-message">{error}</main>;
  }

  return (
    <main className="content-page">
      <section className="squad-hero">
        <div>
          <p className="eyebrow">Squad Overview</p>
          <h1>{club?.name}</h1>
          <p>
            {club?.country} · {club?.league} · {club?.season}
          </p>
        </div>

        <div className="squad-hero-stats">
          <div>
            <strong>{players.length}</strong>
            <span>Players</span>
          </div>
          <div>
            <strong>{teams.length}</strong>
            <span>Teams</span>
          </div>
          <div>
            <strong>{club?.reputation}</strong>
            <span>Reputation</span>
          </div>
        </div>
      </section>

      <section className="squad-toolbar">
        <input
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          placeholder="Search players..."
        />

        <select
          value={position}
          onChange={(event) => setPosition(event.target.value as PlayerPositionType | "")}
        >
          <option value="">All Positions</option>
          <option value="GOALKEEPER">GK</option>
          <option value="DEFENDER_LEFT">DL</option>
          <option value="DEFENDER_CENTRAL">DC</option>
          <option value="DEFENDER_RIGHT">DR</option>
          <option value="DEFENSIVE_MIDFIELDER">DM</option>
          <option value="MIDFIELDER_CENTRAL">MC</option>
          <option value="ATTACKING_MIDFIELDER_CENTRAL">AMC</option>
          <option value="STRIKER">ST</option>
        </select>

        <select
          value={nationality}
          onChange={(event) => setNationality(event.target.value)}
        >
          <option value="">All Nationalities</option>
          <option value="ENGLAND">England</option>
          <option value="PORTUGAL">Portugal</option>
          <option value="FRANCE">France</option>
          <option value="SPAIN">Spain</option>
          <option value="GERMANY">Germany</option>
          <option value="ITALY">Italy</option>
          <option value="BRAZIL">Brazil</option>
          <option value="ARGENTINA">Argentina</option>
        </select>

        <select
          value={selectedCurrency}
          onChange={(event) =>
            setSelectedCurrency(event.target.value as CurrencyCode)
          }
        >
          {availableCurrencies.map((currency) => (
            <option key={currency.code} value={currency.code}>
              {currency.code} - {currency.name}
            </option>
          ))}
        </select>

        <Link to="/players/new" className="new-player-button">
          + New Player
        </Link>
      </section>

      <section className="team-tabs">
        <button
          className={activeTeamId === "ALL" ? "active" : ""}
          onClick={() => setActiveTeamId("ALL")}
        >
          All Players
        </button>

        {teams.map((team) => (
          <button
            key={team.id}
            className={activeTeamId === team.id ? "active" : ""}
            onClick={() => setActiveTeamId(team.id)}
          >
            {team.name.replace(`${club?.name} `, "")}
          </button>
        ))}
      </section>

      <section className="squad-board">
        {playersByTeam.map(({ team, players }) => {
          if (activeTeamId !== "ALL" && activeTeamId !== team.id) {
            return null;
          }

          return (
            <div key={team.id} className="team-section">
              <div className="section-title-row">
                <div>
                  <h2>{team.name}</h2>
                  <p>{players.length} players</p>
                </div>
              </div>

              {players.length === 0 ? (
                <div className="empty-state">No players in this team yet.</div>
              ) : (
                <div className="player-table">
                  <div className="player-table-header">
                    <span>Name</span>
                    <span>Age</span>
                    <span>Nat</span>
                    <span>CA</span>
                    <span>PA</span>
                    <span>Value</span>
                    <span>Wage</span>
                  </div>

                  {players.map((player) => (
                    <Link
                      key={player.id}
                      to={`/players/${player.id}`}
                      className="player-row"
                    >
                      <span className="player-name-cell">
                        <strong>{player.displayName}</strong>
                        <small>{player.fullName}</small>
                      </span>

                      <span>{player.age}</span>
                      <span>{player.nationality}</span>
                      <span>{player.teamName.replace(`${club?.name} `, "")}</span>

                      <span>
                        {formatMoney(
                          convertFromGbp(player.estimatedValueInGbp, selectedRate),
                          selectedCurrency
                        )}
                      </span>

                      <span>
                        {player.weeklyWage
                          ? `${formatMoney(
                            convertFromGbp(player.weeklyWage, selectedRate),
                            selectedCurrency
                          )} p/w`
                          : "N/A"}
                      </span>
                    </Link>
                  ))}
                </div>
              )}
            </div>
          );
        })}
      </section>
    </main>
  );
}

export default Squad;
