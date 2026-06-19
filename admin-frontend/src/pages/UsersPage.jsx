import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { deleteUser, getUsers } from "../api/adminUsersApi";
import AdminLayout from "../layout/AdminLayout";

export default function UsersPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [notice, setNotice] = useState(null);
  const [search, setSearch] = useState("");
  const [roleFilter, setRoleFilter] = useState("ALL");
  const [page, setPage] = useState(1);
  const pageSize = 10;
  const [pagination, setPagination] = useState({
    total: 0,
    totalPages: 1,
    teacherCount: 0,
    studentCount: 0
  });

  async function loadUsers({ nextPage = page, nextSearch = search, nextRole = roleFilter } = {}) {
    setLoading(true);

    try {
      const result = await getUsers({
        page: nextPage,
        size: pageSize,
        search: nextSearch.trim(),
        role: nextRole
      });
      setUsers(result.items || []);
      setPagination({
        total: result.total || 0,
        totalPages: Math.max(1, result.totalPages || 1),
        teacherCount: result.teacherCount || 0,
        studentCount: result.studentCount || 0
      });
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
  }, [page, roleFilter]);

  const totalPages = pagination.totalPages;
  const safePage = Math.min(page, totalPages);
  const pageStart = (safePage - 1) * pageSize;

  function updateSearch(value) {
    setSearch(value);
  }

  function updateRoleFilter(value) {
    setRoleFilter(value);
    setPage(1);
  }

  function submitSearch(event) {
    event.preventDefault();
    setPage(1);
    loadUsers({ nextPage: 1 });
  }

  function resetFilters() {
    setSearch("");
    setRoleFilter("ALL");
    setPage(1);
    loadUsers({ nextPage: 1, nextSearch: "", nextRole: "ALL" });
  }

  return (
    <AdminLayout title="Users" subtitle="Manage student and teacher accounts">
      <div className="user-management-grid">
        <aside className="notion-card account-structure-card">
          <div className="card-header compact">
            <div>
              <h3>Account structure</h3>
              <p>Current workspace roles</p>
            </div>
          </div>

          <div className="structure-tree">
            <button
              className={`tree-node ${roleFilter === "ALL" ? "active" : ""}`}
              type="button"
              onClick={() => updateRoleFilter("ALL")}
            >
              <span>All users</span>
              <strong>{pagination.total}</strong>
            </button>
            <button
              className={`tree-node ${roleFilter === "ROLE_TEACHER" ? "active" : ""}`}
              type="button"
              onClick={() => updateRoleFilter("ROLE_TEACHER")}
            >
              <span>Teachers</span>
              <strong>{pagination.teacherCount}</strong>
            </button>
            <button
              className={`tree-node ${roleFilter === "ROLE_STUDENT" ? "active" : ""}`}
              type="button"
              onClick={() => updateRoleFilter("ROLE_STUDENT")}
            >
              <span>Students</span>
              <strong>{pagination.studentCount}</strong>
            </button>
          </div>

          <div className="structure-note">
            Use search and role filters to narrow large datasets before editing accounts.
          </div>
        </aside>

        <section className="notion-card users-list-card">
          <div className="card-header">
            <div>
              <h3>User List</h3>
              <p>
                Showing {pagination.total === 0 ? 0 : pageStart + 1}-
                {Math.min(pageStart + users.length, pagination.total)} of {pagination.total} matched users
              </p>
            </div>
            <Link className="btn btn-primary" to="/users/new">
              + Add User
            </Link>
          </div>

          <form className="user-toolbar" onSubmit={submitSearch}>
            <label className="toolbar-field">
              <span>Keyword</span>
              <input
                className="field-input"
                onChange={(event) => updateSearch(event.target.value)}
                placeholder="Search username, name, email, phone"
                value={search}
              />
            </label>
            <label className="toolbar-field">
              <span>Role</span>
              <select
                className="field-input"
                onChange={(event) => updateRoleFilter(event.target.value)}
                value={roleFilter}
              >
                <option value="ALL">All roles</option>
                <option value="ROLE_STUDENT">Student</option>
                <option value="ROLE_TEACHER">Teacher</option>
              </select>
            </label>
            <div className="toolbar-actions">
              <button className="btn btn-primary" type="submit">
                Search
              </button>
              <button className="btn btn-secondary" type="button" onClick={resetFilters}>
                Reset
              </button>
            </div>
          </form>

          {notice && <div className={`alert alert-${notice.type}`}>{notice.text}</div>}

          {loading ? (
            <div className="empty-state">Loading users...</div>
          ) : users.length === 0 ? (
            <div className="empty-state">No users found.</div>
          ) : (
            <>
              <div className="table-wrap">
                <table className="notion-table users-table">
                  <thead>
                    <tr>
                      <th>Avatar</th>
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
                          <span className="table-avatar">{user.username?.charAt(0).toUpperCase()}</span>
                        </td>
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

              <div className="pagination-bar">
                <span>
                  Page {safePage} of {totalPages}, loading {users.length} users per request
                </span>
                <div className="pagination-actions">
                  <button
                    className="btn btn-secondary btn-sm"
                    disabled={safePage === 1}
                    onClick={() => setPage((current) => Math.max(1, current - 1))}
                    type="button"
                  >
                    Previous
                  </button>
                  <button
                    className="btn btn-secondary btn-sm"
                    disabled={safePage === totalPages}
                    onClick={() => setPage((current) => Math.min(totalPages, current + 1))}
                    type="button"
                  >
                    Next
                  </button>
                </div>
              </div>
            </>
          )}
        </section>
      </div>
    </AdminLayout>
  );
}
