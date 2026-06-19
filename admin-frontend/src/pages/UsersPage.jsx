import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { deleteUser, getUsers } from "../api/adminUsersApi";
import AdminLayout from "../layout/AdminLayout";

export default function UsersPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [notice, setNotice] = useState(null);

  async function loadUsers() {
    setLoading(true);

    try {
      setUsers(await getUsers());
    } catch (error) {
      setNotice({ type: "error", text: "Failed to load users." });
    } finally {
      setLoading(false);
    }
  }

  async function handleDelete(user) {
    const confirmed = window.confirm(`Delete ${user.username}?`);
    if (!confirmed) {
      return;
    }

    try {
      await deleteUser(user.id);
      setNotice({ type: "success", text: "User deleted." });
      await loadUsers();
    } catch (error) {
      setNotice({ type: "error", text: "Failed to delete user." });
    }
  }

  useEffect(() => {
    loadUsers();
  }, []);

  return (
    <AdminLayout title="Users" subtitle="Manage student and teacher accounts">
      <section className="notion-card">
        <div className="card-header">
          <div>
            <h3>Account List</h3>
            <p>{users.length} accounts in this workspace</p>
          </div>
          <Link className="btn btn-primary" to="/users/new">
            + New User
          </Link>
        </div>

        {notice && <div className={`alert alert-${notice.type}`}>{notice.text}</div>}

        {loading ? (
          <div className="empty-state">Loading users...</div>
        ) : users.length === 0 ? (
          <div className="empty-state">No users found.</div>
        ) : (
          <div className="table-wrap">
            <table className="notion-table">
              <thead>
                <tr>
                  <th>Username</th>
                  <th>Full name</th>
                  <th>Email</th>
                  <th>Phone</th>
                  <th>Roles</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map((user) => (
                  <tr key={user.id}>
                    <td>
                      <strong>{user.username}</strong>
                    </td>
                    <td>{user.fullName}</td>
                    <td>{user.email}</td>
                    <td>{user.phoneNumber || "—"}</td>
                    <td>
                      <div className="badge-row">
                        {(user.roles || []).map((role) => (
                          <span
                            className={`badge ${role === "ROLE_TEACHER" ? "badge-purple" : "badge-green"}`}
                            key={role}
                          >
                            {role.replace("ROLE_", "")}
                          </span>
                        ))}
                      </div>
                    </td>
                    <td>
                      <div className="action-row">
                        <Link className="btn btn-secondary btn-sm" to={`/users/${user.id}`}>
                          Edit
                        </Link>
                        <button className="btn btn-danger btn-sm" type="button" onClick={() => handleDelete(user)}>
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </AdminLayout>
  );
}
