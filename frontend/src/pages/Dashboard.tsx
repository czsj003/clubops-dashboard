import { useEffect, useState } from "react";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import type { Club, Team } from "../types/club";

function Dashboard() {
  const { user, logout } = useAuth();

  const [club, setClub] = useState<Club | null>(null);
  const [teams, setTeams] = useState<Team[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadDashboardData() {
      try {
        const [clubResponse, teamsResponse] = await Promise.all([
          api.get<Club>("/club"),
          api.get<Team[]>("/teams"),
        ]);

        setClub(clubResponse.data);
        setTeams(teamsResponse.data);
      } catch {
        setError("Failed to load club dashboard data.");
      } finally {
        setLoading(false);
      }
    }

    loadDashboardData();
  }, []);

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1>ClubOps Dashboard</h1>
          <p>Welcome, {user?.name}</p>
        </div>

        <button onClick={logout}>Logout</button>
      </header>

      {loading && <p>Loading dashboard...</p>}

      {error && <div className="error-message">{error}</div>}

      {!loading && club && (
        <main className="dashboard-grid">
          <section className="card">
            <h2>{club.name}</h2>
            <p>Country System: {formatCountry(club.country)}</p>
            <p>League: {club.league}</p>
            <p>Season: {club.season}</p>
            <p>Reputation: {club.reputation}/100</p>
          </section>

          <section className="card">
            <h2>Teams</h2>

            <div className="team-list">
              {teams.map((team) => (
                <div key={team.id} className="team-row">
                  <div>
                    <strong>{team.name}</strong>
                    <p>{formatTeamType(team.type)}</p>
                  </div>

                  <span>#{team.displayOrder}</span>
                </div>
              ))}
            </div>
          </section>
        </main>
      )}
    </div>
  );
}

function formatCountry(country: Club["country"]) {
  switch (country) {
    case "ENGLAND":
      return "England";
    case "SPAIN":
      return "Spain";
    case "GERMANY":
      return "Germany";
    case "ITALY":
      return "Italy";
    case "FRANCE":
      return "France";
    default:
      return country;
  }
}

function formatTeamType(type: Team["type"]) {
  switch (type) {
    case "FIRST_TEAM":
      return "First Team";
    case "U21":
      return "U21";
    case "U18":
      return "U18";
    case "B_TEAM":
      return "B Team";
    case "U23":
      return "U23";
    case "U19":
      return "U19";
    case "RESERVE_TEAM":
      return "Reserve Team";
    default:
      return type;
  }
}

export default Dashboard;
