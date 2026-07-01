import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import { useAuth } from "../auth/AuthContext";

export default function LoginPage() {
  const auth = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(event) {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    const username = formData.get("username")?.trim();
    const password = formData.get("password");

    if (!username || !password) {
      setError("Please enter username and password.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      await auth.login(username, password);
      navigate("/");
    } catch (error) {
      setError("Login failed. Please check your username and password.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="login-shell">
      <section className="login-card notion-card">
        <div className="login-title">
          <h1>Course Workspace</h1>
          <p>Sign in with a student or teacher account</p>
        </div>

        {error && <div className="alert alert-error">{error}</div>}

        <form className="notion-form" onSubmit={handleSubmit}>
          <label className="form-field">
            <span>Username</span>
            <input className="field-input" name="username" placeholder="teacher" autoComplete="username" />
          </label>

          <label className="form-field">
            <span>Password</span>
            <input
              className="field-input"
              name="password"
              type="password"
              placeholder="password"
              autoComplete="current-password"
            />
          </label>

          <button className="btn btn-primary btn-block" type="submit" disabled={loading}>
            {loading ? "Signing in..." : "Login"}
          </button>
          <Link className="auth-link" to="/register">
            Need a student account? Register
          </Link>
        </form>
      </section>
    </main>
  );
}
