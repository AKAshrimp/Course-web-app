import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { getMyVotes, getProfile, updateProfile } from "../api/publicContentApi";
import AdminLayout from "../layout/AdminLayout";

function formatDate(value) {
  if (!value) {
    return "No date";
  }

  return new Intl.DateTimeFormat("en", {
    dateStyle: "medium",
    timeStyle: "short"
  }).format(new Date(value));
}

export default function ProfilePage() {
  const [profile, setProfile] = useState(null);
  const [votes, setVotes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [notice, setNotice] = useState(null);

  useEffect(() => {
    async function loadProfile() {
      try {
        const [profileData, voteData] = await Promise.all([getProfile(), getMyVotes()]);
        setProfile(profileData);
        setVotes(voteData);
      } catch (error) {
        setNotice({ type: "error", text: "Failed to load profile." });
      } finally {
        setLoading(false);
      }
    }

    loadProfile();
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    const payload = {
      fullName: formData.get("fullName")?.trim(),
      email: formData.get("email")?.trim(),
      phoneNumber: formData.get("phoneNumber")?.trim()
    };

    if (!payload.fullName || !payload.email) {
      setNotice({ type: "error", text: "Full name and email are required." });
      return;
    }

    setSaving(true);
    setNotice(null);

    try {
      const updated = await updateProfile(payload);
      setProfile(updated);
      setNotice({ type: "success", text: "Profile updated." });
    } catch (error) {
      setNotice({ type: "error", text: "Failed to update profile." });
    } finally {
      setSaving(false);
    }
  }

  return (
    <AdminLayout title="Profile" subtitle="Manage your account and review your poll history">
      {notice && <div className={`alert alert-${notice.type}`}>{notice.text}</div>}

      {loading ? (
        <div className="empty-state">Loading profile...</div>
      ) : !profile ? (
        <div className="empty-state">Profile unavailable.</div>
      ) : (
        <div className="profile-grid">
          <section className="notion-card">
            <div className="card-header">
              <div>
                <h3>Personal details</h3>
                <p>Username cannot be changed</p>
              </div>
            </div>
            <form className="notion-form" onSubmit={handleSubmit}>
              <label className="form-field">
                <span>Username</span>
                <input className="field-input" value={profile.username} disabled />
              </label>
              <label className="form-field">
                <span>Full name</span>
                <input className="field-input" name="fullName" defaultValue={profile.fullName} />
              </label>
              <label className="form-field">
                <span>Email</span>
                <input className="field-input" name="email" type="email" defaultValue={profile.email} />
              </label>
              <label className="form-field">
                <span>Phone number</span>
                <input className="field-input" name="phoneNumber" defaultValue={profile.phoneNumber || ""} />
              </label>
              <div className="badge-row">
                {(profile.roles || []).map((role) => (
                  <span className="badge badge-purple" key={role}>
                    {role.replace("ROLE_", "")}
                  </span>
                ))}
              </div>
              <button className="btn btn-primary" type="submit" disabled={saving}>
                {saving ? "Saving..." : "Save changes"}
              </button>
            </form>
          </section>

          <section className="notion-card">
            <div className="card-header">
              <div>
                <h3>My votes</h3>
                <p>{votes.length} submitted votes</p>
              </div>
            </div>
            {votes.length ? (
              <div className="vote-list">
                {votes.map((vote) => (
                  <Link className="vote-item" key={vote.id} to={`/polls/${vote.pollId}`}>
                    <div>
                      <strong>{vote.pollQuestion}</strong>
                      <span>Your choice: {vote.optionText}</span>
                    </div>
                    <span>{formatDate(vote.votedAt)}</span>
                  </Link>
                ))}
              </div>
            ) : (
              <div className="empty-state">No votes submitted yet.</div>
            )}
          </section>
        </div>
      )}
    </AdminLayout>
  );
}
