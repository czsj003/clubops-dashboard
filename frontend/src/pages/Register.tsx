import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import type { Country } from "../types/auth";

function Register() {
  const navigate = useNavigate();
  const { register } = useAuth();

  const [name, setName] = useState("Justin Tao");
  const [email, setEmail] = useState("justin@example.com");
  const [password, setPassword] = useState("123456");
  const [clubName, setClubName] = useState("Northbridge FC");
  const [country, setCountry] = useState<Country>("ENGLAND");
  const [error, setError] = useState("");

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
      });

      navigate("/dashboard");
    } catch {
      setError("Registration failed. The email may already be registered.");
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>Register</h1>
        <p>Create your ClubOps account and simulated club.</p>

        {error && <div className="error-message">{error}</div>}

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
            <label>Country System</label>
            <select
              value={country}
              onChange={(event) => setCountry(event.target.value as Country)}
            >
              <option value="ENGLAND">England - First Team, U21, U18</option>
              <option value="SPAIN" disabled>
                Spain - Coming later
              </option>
              <option value="ITALY" disabled>
                Italy - Coming later
              </option>
              <option value="GERMANY" disabled>
                Germany - Coming later
              </option>
              <option value="FRANCE" disabled>
                France - Coming later
              </option>
            </select>
          </div>

          <button type="submit">Register</button>
        </form>

        <p>
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
}

export default Register;
