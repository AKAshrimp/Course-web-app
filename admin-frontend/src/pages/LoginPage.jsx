import { useState } from "react";
import { useNavigate } from "react-router-dom";

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
      navigate("/users");
    } catch (error) {
      setError("Login failed. Please check the account and role.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="login-shell">
      <section className="login-card notion-card">
        <div className="login-title">
          <h1>Course Admin</h1>
          <p>Sign in with a teacher account</p>
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
        </form>
      </section>
    </main>
  );
}
