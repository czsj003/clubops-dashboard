import { useAuth } from "../context/AuthContext";

function Dashboard() {
  const { user, logout } = useAuth();

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1>ClubOps Dashboard</h1>
          <p>Welcome, {user?.name}</p>
        </div>

        <button onClick={logout}>Logout</button>
      </header>

      <main>
        <div className="card">
          <h2>Dashboard</h2>
          <p>You are logged in as {user?.email}.</p>
          <p>Day 2 authentication is working.</p>
        </div>
      </main>
    </div>
  );
}

export default Dashboard;