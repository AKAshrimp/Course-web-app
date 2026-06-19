import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import { register } from "../api/authApi";

export default function RegisterPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(event) {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    const user = {
      username: formData.get("username")?.trim(),
      password: formData.get("password"),
      fullName: formData.get("fullName")?.trim(),
      email: formData.get("email")?.trim(),
      phoneNumber: formData.get("phoneNumber")?.trim()
    };

    if (!user.username || !user.password || !user.fullName || !user.email) {
      setError("Username, password, full name, and email are required.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      await register(user);
      navigate("/login");
    } catch (error) {
      setError("Registration failed. Username or email may already exist.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="login-shell">
      <section className="login-card notion-card">
        <div className="login-title">
          <h1>Create account</h1>
          <p>Join the course workspace as a student</p>
        </div>

        {error && <div className="alert alert-error">{error}</div>}

        <form className="notion-form" onSubmit={handleSubmit}>
          <label className="form-field">
            <span>Username</span>
            <input className="field-input" name="username" placeholder="kelvin" autoComplete="username" />
          </label>
          <label className="form-field">
            <span>Password</span>
            <input className="field-input" name="password" type="password" autoComplete="new-password" />
          </label>
          <label className="form-field">
            <span>Full name</span>
            <input className="field-input" name="fullName" placeholder="Kelvin Chan" />
          </label>
          <label className="form-field">
            <span>Email</span>
            <input className="field-input" name="email" type="email" placeholder="kelvin@example.com" />
          </label>
          <label className="form-field">
            <span>Phone number</span>
            <input className="field-input" name="phoneNumber" placeholder="91234567" />
          </label>
          <button className="btn btn-primary btn-block" type="submit" disabled={loading}>
            {loading ? "Creating..." : "Create account"}
          </button>
          <Link className="auth-link" to="/login">
            Already have an account? Sign in
          </Link>
        </form>
      </section>
    </main>
  );
}
