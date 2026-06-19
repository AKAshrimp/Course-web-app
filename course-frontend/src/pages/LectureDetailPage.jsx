import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import { addLectureComment, getLecture } from "../api/publicContentApi";
import AdminLayout from "../layout/AdminLayout";

const API_BASE_URL = "http://localhost:8080";

function formatDate(value) {
  if (!value) {
    return "No date";
  }

  return new Intl.DateTimeFormat("en", {
    dateStyle: "medium",
    timeStyle: "short"
  }).format(new Date(value));
}

export default function LectureDetailPage() {
  const { id } = useParams();
  const [lecture, setLecture] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [notice, setNotice] = useState(null);

  useEffect(() => {
    async function loadLecture() {
      try {
        const data = await getLecture(id);
        setLecture(data);
      } catch (error) {
        setNotice({ type: "error", text: "Failed to load lecture." });
      } finally {
        setLoading(false);
      }
    }

    loadLecture();
  }, [id]);

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
      const comment = await addLectureComment(id, content);
      setLecture((current) => ({
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

  return (
    <AdminLayout title="Lecture Detail" subtitle="Read materials and join the discussion">
      <div className="detail-actions">
        <Link className="btn btn-secondary btn-sm" to="/">
          Back to workspace
        </Link>
      </div>

      {notice && <div className={`alert alert-${notice.type}`}>{notice.text}</div>}

      {loading ? (
        <div className="empty-state">Loading lecture...</div>
      ) : !lecture ? (
        <div className="empty-state">Lecture not found.</div>
      ) : (
        <div className="lecture-detail-grid">
          <section className="notion-card lecture-main-card">
            <span className="badge badge-purple">Lecture</span>
            <h1>{lecture.title}</h1>
            <p className="detail-date">{formatDate(lecture.createdAt)}</p>
            <p className="lecture-description">{lecture.description}</p>
          </section>

          <section className="notion-card">
            <div className="card-header">
              <div>
                <h3>Materials</h3>
                <p>{lecture.materials?.length || 0} resources</p>
              </div>
            </div>
            {lecture.materials?.length ? (
              <div className="resource-list">
                {lecture.materials.map((material) => (
                  <a className="resource-item" key={material.id} href={`${API_BASE_URL}/lecture/material/${material.id}/download`}>
                    <div>
                      <strong>{material.title || material.fileName}</strong>
                      <span>{material.fileName}</span>
                    </div>
                    <span className="badge badge-green">Download</span>
                  </a>
                ))}
              </div>
            ) : (
              <div className="empty-state">No materials uploaded yet.</div>
            )}
          </section>

          <section className="notion-card lecture-comments-card">
            <div className="card-header">
              <div>
                <h3>Discussion</h3>
                <p>{lecture.comments?.length || 0} comments</p>
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
            {lecture.comments?.length ? (
              <div className="comment-list">
                {lecture.comments.map((comment) => (
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
