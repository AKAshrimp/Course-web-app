import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { getLectures, getPolls } from "../api/publicContentApi";
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

function getTotalVotes(poll) {
  return (poll.options || []).reduce((total, option) => total + (option.voteCount || 0), 0);
}

export default function HomePage() {
  const [lectures, setLectures] = useState([]);
  const [polls, setPolls] = useState([]);
  const [loading, setLoading] = useState(true);
  const [notice, setNotice] = useState(null);

  useEffect(() => {
    async function loadContent() {
      try {
        const [lectureData, pollData] = await Promise.all([getLectures(), getPolls()]);
        setLectures(lectureData);
        setPolls(pollData);
      } catch (error) {
        setNotice({ type: "error", text: "Failed to load course content." });
      } finally {
        setLoading(false);
      }
    }

    loadContent();
  }, []);

  return (
    <AdminLayout title="Course Workspace" subtitle="Browse lectures and active polls">
      <section className="hero-panel">
        <span className="eyebrow">Notion-inspired course hub</span>
        <h1>Bring lectures, polls, and students into one calm workspace.</h1>
        <p>
          This React frontend now uses a custom design system instead of Ant Design, keeping the interface lighter and
          easier to tailor.
        </p>
      </section>

      {notice && <div className={`alert alert-${notice.type}`}>{notice.text}</div>}

      {loading ? (
        <div className="empty-state">Loading workspace...</div>
      ) : (
        <div className="content-grid">
          <section className="notion-card">
            <div className="card-header">
              <div>
                <h3>Lectures</h3>
                <p>{lectures.length} lecture resources</p>
              </div>
            </div>

            {lectures.length === 0 ? (
              <div className="empty-state">No lectures yet.</div>
            ) : (
              <div className="stack">
                {lectures.map((lecture) => (
                  <article className="content-card content-card-sky" key={lecture.id}>
                    <div>
                      <span className="badge badge-purple">Lecture</span>
                      <h4>{lecture.title}</h4>
                      <p>{lecture.description}</p>
                    </div>
                    <div className="content-card-footer">
                      <span>{formatDate(lecture.createdAt)}</span>
                      <Link className="btn btn-secondary btn-sm" to={`/lectures/${lecture.id}`}>
                        View
                      </Link>
                    </div>
                  </article>
                ))}
              </div>
            )}
          </section>

          <section className="notion-card">
            <div className="card-header">
              <div>
                <h3>Polls</h3>
                <p>{polls.length} active discussions</p>
              </div>
            </div>

            {polls.length === 0 ? (
              <div className="empty-state">No polls yet.</div>
            ) : (
              <div className="stack">
                {polls.map((poll) => (
                  <article className="content-card content-card-mint" key={poll.id}>
                    <div>
                      <span className="badge badge-green">{getTotalVotes(poll)} votes</span>
                      <h4>{poll.question}</h4>
                      <p>{poll.options?.length || 0} options available</p>
                    </div>
                    <div className="content-card-footer">
                      <span>{formatDate(poll.createdAt)}</span>
                      <Link className="btn btn-secondary btn-sm" to={`/polls/${poll.id}`}>
                        View
                      </Link>
                    </div>
                  </article>
                ))}
              </div>
            )}
          </section>
        </div>
      )}
    </AdminLayout>
  );
}
