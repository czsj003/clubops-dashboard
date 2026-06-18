import { Link, NavLink, Outlet } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

function AppLayout() {
  const { user, logout } = useAuth();

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <Link to="/squad" className="brand">
          <div className="brand-badge">CO</div>
          <div>
            <strong>ClubOps</strong>
            <span>Dashboard</span>
          </div>
        </Link>

        <nav className="sidebar-nav">
          <NavLink to="/squad">Squad</NavLink>
          <span className="nav-disabled">Contracts</span>
          <span className="nav-disabled">Finance</span>
          <span className="nav-disabled">Facilities</span>
        </nav>
      </aside>

      <div className="main-shell">
        <header className="topbar">
          <div>
            <strong>{user?.name}</strong>
            <span>Manager Profile</span>
          </div>

          <button onClick={logout}>Logout</button>
        </header>

        <Outlet />
      </div>
    </div>
  );
}

export default AppLayout;