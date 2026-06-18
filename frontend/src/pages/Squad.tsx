import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import type { Club, Team } from "../types/club";
import type { PlayerListItem } from "../types/player";
import { formatMoney, formatPosition } from "../utils/formatters";

function Squad() {
  const [club, setClub] = useState<Club | null>(null);
  const [teams, setTeams] = useState<Team[]>([]);
  const [players, setPlayers] = useState<PlayerListItem[]>([]);
  const [activeTeamId, setActiveTeamId] = useState<number | "ALL">("ALL");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadSquad() {
      try {
        const [clubResponse, teamsResponse, playersResponse] = await Promise.all([
          api.get<Club>("/club"),
          api.get<Team[]>("/teams"),
          api.get<PlayerListItem[]>("/players"),
        ]);

        setClub(clubResponse.data);
        setTeams(teamsResponse.data);
        setPlayers(playersResponse.data);
      } catch (err) {
        setError("Failed to load squad data.");
      } finally {
        setLoading(false);
      }
    }

    loadSquad();
  }, []);

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
                        <small>
                          {player.isGoalkeeper ? "GK" : "Outfield"} ·{" "}
                          {player.fullName}
                        </small>
                      </span>

                      <span>{player.age}</span>
                      <span>{player.nationality}</span>
                      <span>{player.currentAbility}</span>
                      <span>{player.potentialAbility}</span>
                      <span>{formatMoney(player.estimatedValueInGbp, "GBP")}</span>
                      <span>
                        {player.weeklyWage
                          ? `${formatMoney(player.weeklyWage, player.wageCurrency ?? "GBP")} p/w`
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