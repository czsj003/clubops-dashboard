import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import type {
  Country,
  FootballLeague,
  RegistrationConfig,
  RegistrationMode,
} from "../types/auth";
import {
  countryOptions,
  leagueOptionsByCountry,
} from "../utils/leagueOptions";
import { getApiErrorMessage } from "../utils/errorUtils";

function Register() {
  const navigate = useNavigate();
  const { register } = useAuth();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [clubName, setClubName] = useState("");
  const [country, setCountry] = useState<Country>("ENGLAND");
  const [league, setLeague] =
    useState<FootballLeague>("EFL_CHAMPIONSHIP");
  const [leagueGroup, setLeagueGroup] = useState<string | null>(null);
  const [inviteCode, setInviteCode] = useState("");
  const [registrationMode, setRegistrationMode] =
    useState<RegistrationMode>("DISABLED");
  const [registrationConfigLoading, setRegistrationConfigLoading] =
    useState(true);
  const [registrationConfigError, setRegistrationConfigError] = useState("");
  const [error, setError] = useState("");

  const leagueOptions = leagueOptionsByCountry[country];
  const selectedLeague = leagueOptions.find((option) => option.value === league);
  const groupOptions = selectedLeague?.groups ?? [];

  useEffect(() => {
    async function loadRegistrationConfig() {
      try {
        const response = await api.get<RegistrationConfig>(
          "/auth/registration-config"
        );
        setRegistrationMode(response.data.mode);
        setRegistrationConfigError("");
      } catch {
        setRegistrationMode("DISABLED");
        setRegistrationConfigError(
          "Could not load registration settings. Please make sure the backend is running."
        );
      } finally {
        setRegistrationConfigLoading(false);
      }
    }

    loadRegistrationConfig();
  }, []);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError("");

    try {
      await register({
        name,
        email,
        password,
        clubName,
        country,
        league,
        leagueGroup,
        inviteCode:
          registrationMode === "INVITE_ONLY" ? inviteCode : undefined,
      });

      navigate("/dashboard");
    } catch (requestError) {
      setError(getApiErrorMessage(
        requestError,
        "Registration failed. The email may already be registered."
      ));
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>Register</h1>
        <p>Create your ClubOps account and simulated club.</p>

        {error && <div className="error-message">{error}</div>}

        {registrationConfigError && (
          <div className="error-message">{registrationConfigError}</div>
        )}

        {registrationConfigLoading && (
          <div className="form-help">Loading registration settings...</div>
        )}

        {!registrationConfigLoading && registrationMode === "DISABLED" && (
          <div className="error-message">
            Registration is currently disabled. Please contact the project owner
            for access.
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Your Name</label>
            <input
              value={name}
              onChange={(event) => setName(event.target.value)}
              type="text"
              placeholder="Your name"
            />
          </div>

          <div className="form-group">
            <label>Email</label>
            <input
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              type="email"
              placeholder="you@example.com"
            />
          </div>

          <div className="form-group">
            <label>Password</label>
            <input
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              type="password"
              placeholder="At least 6 characters"
            />
          </div>

          <div className="form-group">
            <label>Club Name</label>
            <input
              value={clubName}
              onChange={(event) => setClubName(event.target.value)}
              type="text"
              placeholder="Northbridge FC"
            />
          </div>

          <div className="form-group">
            <label>Country</label>
            <select
              value={country}
              onChange={(event) => {
                const nextCountry = event.target.value as Country;
                const nextLeagues = leagueOptionsByCountry[nextCountry];

                setCountry(nextCountry);
                setLeague(nextLeagues[0].value);
                setLeagueGroup(nextLeagues[0].groups?.[0]?.value ?? null);
              }}
            >
              {countryOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>League</label>
            <select
              value={league}
              onChange={(event) => {
                const nextLeague = event.target.value as FootballLeague;
                const nextLeagueOption = leagueOptions.find(
                  (option) => option.value === nextLeague
                );

                setLeague(nextLeague);
                setLeagueGroup(nextLeagueOption?.groups?.[0]?.value ?? null);
              }}
            >
              {leagueOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>

          {groupOptions.length > 0 && (
            <div className="form-group">
              <label>League Group</label>
              <select
                value={leagueGroup ?? ""}
                onChange={(event) => setLeagueGroup(event.target.value)}
              >
                {groupOptions.map((group) => (
                  <option key={group.value} value={group.value}>
                    {group.label}
                  </option>
                ))}
              </select>
            </div>
          )}

          {registrationMode === "INVITE_ONLY" && (
            <div className="form-group">
              <label>Invite Code</label>
              <input
                value={inviteCode}
                onChange={(event) => setInviteCode(event.target.value)}
                type="text"
                placeholder="Enter invite code"
              />
              <small className="form-hint">
                This hosted version requires an invite code.
              </small>
            </div>
          )}

          <button
            type="submit"
            disabled={
              registrationConfigLoading || registrationMode === "DISABLED"
            }
          >
            {registrationConfigLoading ? "Loading..." : "Register"}
          </button>
        </form>

        <p>
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
}

export default Register;
