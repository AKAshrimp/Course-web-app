import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { createLecture, createPoll, deleteLecture, deletePoll, getDashboard } from "../api/teacherContentApi";
import AdminLayout from "../layout/AdminLayout";

export default function DashboardPage() {
  const [dashboard, setDashboard] = useState({ lectures: [], polls: [], userCount: 0, lectureCount: 0, pollCount: 0 });
  const [loading, setLoading] = useState(true);
  const [notice, setNotice] = useState(null);

  async function loadDashboard() {
    setLoading(true);
    try {
      setDashboard(await getDashboard());
    } catch (error) {
      setNotice({ type: "error", text: "Failed to load dashboard." });
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadDashboard();
  }, []);

  async function handleCreateLecture(event) {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    const lecture = {
      title: formData.get("title")?.trim(),
      description: formData.get("description")?.trim()
    };

    if (!lecture.title || !lecture.description) {
      setNotice({ type: "error", text: "Lecture title and description are required." });
      return;
    }

    try {
      await createLecture(lecture);
      event.currentTarget.reset();
      setNotice({ type: "success", text: "Lecture created." });
      await loadDashboard();
    } catch (error) {
      setNotice({ type: "error", text: "Failed to create lecture." });
    }
  }

  async function handleCreatePoll(event) {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    const options = formData.getAll("options").map((option) => option.trim()).filter(Boolean);
    const poll = {
      question: formData.get("question")?.trim(),
      options
    };

    if (!poll.question || options.length < 2) {
      setNotice({ type: "error", text: "Poll question and at least two options are required." });
      return;
    }

    try {
      await createPoll(poll);
      event.currentTarget.reset();
      setNotice({ type: "success", text: "Poll created." });
      await loadDashboard();
    } catch (error) {
      setNotice({ type: "error", text: "Failed to create poll." });
    }
  }

  async function handleDeleteLecture(lecture) {
    if (!window.confirm(`Delete lecture "${lecture.title}"?`)) {
      return;
    }

    try {
      await deleteLecture(lecture.id);
      setNotice({ type: "success", text: "Lecture deleted." });
      await loadDashboard();
    } catch (error) {
      setNotice({ type: "error", text: "Failed to delete lecture." });
    }
  }

  async function handleDeletePoll(poll) {
    if (!window.confirm(`Delete poll "${poll.question}"?`)) {
      return;
    }

    try {
      await deletePoll(poll.id);
      setNotice({ type: "success", text: "Poll deleted." });
      await loadDashboard();
    } catch (error) {
      setNotice({ type: "error", text: "Failed to delete poll." });
    }
  }

  return (
    <AdminLayout title="Teacher Dashboard" subtitle="Manage lectures and polls from React">
      {notice && <div className={`alert alert-${notice.type}`}>{notice.text}</div>}

      <section className="stats-grid">
        <div className="metric-card">
          <span>Users</span>
          <strong>{dashboard.userCount}</strong>
        </div>
        <div className="metric-card">
          <span>Lectures</span>
          <strong>{dashboard.lectureCount}</strong>
        </div>
        <div className="metric-card">
          <span>Polls</span>
          <strong>{dashboard.pollCount}</strong>
        </div>
      </section>

      {loading ? (
        <div className="empty-state">Loading dashboard...</div>
      ) : (
        <div className="dashboard-grid">
          <section className="notion-card">
            <div className="card-header">
              <div>
                <h3>Create lecture</h3>
                <p>Add a lecture resource for students</p>
              </div>
            </div>
            <form className="notion-form" onSubmit={handleCreateLecture}>
              <label className="form-field">
                <span>Title</span>
                <input className="field-input" name="title" placeholder="Week 3" />
              </label>
              <label className="form-field">
                <span>Description</span>
                <textarea className="field-input field-textarea" name="description" placeholder="Lecture summary..." />
              </label>
              <button className="btn btn-primary" type="submit">
                Create lecture
              </button>
            </form>
          </section>

          <section className="notion-card">
            <div className="card-header">
              <div>
                <h3>Create poll</h3>
                <p>Collect student feedback</p>
              </div>
            </div>
            <form className="notion-form" onSubmit={handleCreatePoll}>
              <label className="form-field">
                <span>Question</span>
                <input className="field-input" name="question" placeholder="Which topic should we revisit?" />
              </label>
              {[1, 2, 3, 4].map((number) => (
                <label className="form-field" key={number}>
                  <span>Option {number}</span>
                  <input className="field-input" name="options" placeholder={`Option ${number}`} />
                </label>
              ))}
              <button className="btn btn-primary" type="submit">
                Create poll
              </button>
            </form>
          </section>

          <section className="notion-card">
            <div className="card-header">
              <div>
                <h3>Lectures</h3>
                <p>{dashboard.lectures?.length || 0} items</p>
              </div>
            </div>
            <div className="admin-item-list">
              {dashboard.lectures?.map((lecture) => (
                <article className="admin-item" key={lecture.id}>
                  <div>
                    <strong>{lecture.title}</strong>
                    <span>{lecture.description}</span>
                  </div>
                  <div className="action-row">
                    <Link className="btn btn-secondary btn-sm" to={`/lectures/${lecture.id}`}>
                      View
                    </Link>
                    <button className="btn btn-danger btn-sm" type="button" onClick={() => handleDeleteLecture(lecture)}>
                      Delete
                    </button>
                  </div>
                </article>
              ))}
            </div>
          </section>

          <section className="notion-card">
            <div className="card-header">
              <div>
                <h3>Polls</h3>
                <p>{dashboard.polls?.length || 0} items</p>
              </div>
            </div>
            <div className="admin-item-list">
              {dashboard.polls?.map((poll) => (
                <article className="admin-item" key={poll.id}>
                  <div>
                    <strong>{poll.question}</strong>
                    <span>{poll.options?.length || 0} options</span>
                  </div>
                  <div className="action-row">
                    <Link className="btn btn-secondary btn-sm" to={`/polls/${poll.id}`}>
                      View
                    </Link>
                    <button className="btn btn-danger btn-sm" type="button" onClick={() => handleDeletePoll(poll)}>
                      Delete
                    </button>
                  </div>
                </article>
              ))}
            </div>
          </section>
        </div>
      )}
    </AdminLayout>
  );
}
