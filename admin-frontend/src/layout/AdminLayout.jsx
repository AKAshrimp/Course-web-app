import { Link, useLocation, useNavigate } from "react-router-dom";

import { useAuth } from "../auth/AuthContext";

export default function AdminLayout({ children, title, subtitle }) {
  const auth = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  function handleLogout() {
    auth.logout();
    navigate("/login");
  }

  return (
    <div className="admin-layout">
      <aside className="admin-sider">
        <div className="brand">
          <h1>Course Admin</h1>
          <p>Account Management</p>
        </div>
        <nav className="side-nav" aria-label="Admin navigation">
          <Link className={`side-nav-link ${location.pathname === "/" ? "active" : ""}`} to="/">
            <span aria-hidden="true">🏠</span>
            Home
          </Link>
          <Link
            className={`side-nav-link ${location.pathname.startsWith("/users") ? "active" : ""}`}
            to="/users"
          >
            <span aria-hidden="true">👥</span>
            Users
          </Link>
        </nav>
      </aside>

      <section className="admin-main">
        <header className="admin-header">
          <div>
            <h2>{title}</h2>
            {subtitle && <p>{subtitle}</p>}
          </div>
          <div className="header-user">
            <span className="avatar">{auth.username?.charAt(0).toUpperCase()}</span>
            <span className="header-username">{auth.username}</span>
            <button className="btn btn-secondary" type="button" onClick={handleLogout}>
              Logout
            </button>
          </div>
        </header>

        <main className="admin-content">{children}</main>
      </section>
    </div>
  );
}
