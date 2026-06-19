import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import { addPollComment, getPoll, votePoll } from "../api/publicContentApi";
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

function getTotalVotes(options = []) {
  return options.reduce((total, option) => total + (option.voteCount || 0), 0);
}

export default function PollDetailPage() {
  const { id } = useParams();
  const [poll, setPoll] = useState(null);
  const [selectedOptionId, setSelectedOptionId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [notice, setNotice] = useState(null);

  useEffect(() => {
    async function loadPoll() {
      try {
        const data = await getPoll(id);
        setPoll(data);
        setSelectedOptionId(data.userVoteOptionId);
      } catch (error) {
        setNotice({ type: "error", text: "Failed to load poll." });
      } finally {
        setLoading(false);
      }
    }

    loadPoll();
  }, [id]);

  async function handleVote(event) {
    event.preventDefault();
    if (!selectedOptionId) {
      setNotice({ type: "error", text: "Choose an option before voting." });
      return;
    }

    setSaving(true);
    setNotice(null);

    try {
      const result = await votePoll(id, selectedOptionId);
      const refreshedPoll = await getPoll(id);
      setPoll(refreshedPoll);
      setSelectedOptionId(refreshedPoll.userVoteOptionId);
      setNotice({ type: "success", text: result.message });
    } catch (error) {
      setNotice({ type: "error", text: "Failed to submit vote." });
    } finally {
      setSaving(false);
    }
  }

  async function handleCommentSubmit(event) {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    const content = formData.get("content")?.trim();

    if (!content) {
      setNotice({ type: "error", text: "Comment cannot be empty." });
      return;
    }

    setSaving(true);
    setNotice(null);

    try {
      const comment = await addPollComment(id, content);
      setPoll((current) => ({
        ...current,
        comments: [...(current?.comments || []), comment]
      }));
      event.currentTarget.reset();
      setNotice({ type: "success", text: "Comment added." });
    } catch (error) {
      setNotice({ type: "error", text: "Failed to add comment." });
    } finally {
      setSaving(false);
    }
  }

  const totalVotes = getTotalVotes(poll?.options);

  return (
    <AdminLayout title="Poll Detail" subtitle="Vote and discuss with classmates">
      <div className="detail-actions">
        <Link className="btn btn-secondary btn-sm" to="/">
          Back to workspace
        </Link>
      </div>

      {notice && <div className={`alert alert-${notice.type}`}>{notice.text}</div>}

      {loading ? (
        <div className="empty-state">Loading poll...</div>
      ) : !poll ? (
        <div className="empty-state">Poll not found.</div>
      ) : (
        <div className="lecture-detail-grid">
          <section className="notion-card poll-main-card">
            <span className="badge badge-green">{totalVotes} votes</span>
            <h1>{poll.question}</h1>
            <p className="detail-date">{formatDate(poll.createdAt)}</p>

            <form className="poll-options-form" onSubmit={handleVote}>
              {poll.options.map((option) => {
                const percentage = totalVotes === 0 ? 0 : Math.round(((option.voteCount || 0) / totalVotes) * 100);
                const checked = Number(selectedOptionId) === Number(option.id);

                return (
                  <label className={`poll-option-card ${checked ? "selected" : ""}`} key={option.id}>
                    <input
                      type="radio"
                      name="optionId"
                      value={option.id}
                      checked={checked}
                      onChange={() => setSelectedOptionId(option.id)}
                    />
                    <div className="poll-option-body">
                      <div className="poll-option-header">
                        <strong>{option.text}</strong>
                        <span>{option.voteCount || 0} votes</span>
                      </div>
                      <div className="poll-bar">
                        <span style={{ width: `${percentage}%` }} />
                      </div>
                    </div>
                  </label>
                );
              })}
              <button className="btn btn-primary" type="submit" disabled={saving}>
                {saving ? "Submitting..." : poll.userVoteOptionId ? "Update vote" : "Submit vote"}
              </button>
            </form>
          </section>

          <section className="notion-card lecture-comments-card">
            <div className="card-header">
              <div>
                <h3>Discussion</h3>
                <p>{poll.comments?.length || 0} comments</p>
              </div>
            </div>
            <form className="notion-form comment-form" onSubmit={handleCommentSubmit}>
              <label className="form-field">
                <span>Add a comment</span>
                <textarea className="field-input field-textarea" name="content" rows="4" placeholder="Share your thoughts..." />
              </label>
              <button className="btn btn-primary" type="submit" disabled={saving}>
                {saving ? "Posting..." : "Post comment"}
              </button>
            </form>
            {poll.comments?.length ? (
              <div className="comment-list">
                {poll.comments.map((comment) => (
                  <article className="comment-item" key={comment.id}>
                    <div className="comment-meta">
                      <strong>{comment.authorName}</strong>
                      <span>{formatDate(comment.createdAt)}</span>
                    </div>
                    <p>{comment.content}</p>
                  </article>
                ))}
              </div>
            ) : (
              <div className="empty-state">No discussion yet.</div>
            )}
          </section>
        </div>
      )}
    </AdminLayout>
  );
}
