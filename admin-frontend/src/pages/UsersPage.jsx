import { useEffect, useMemo, useState } from "react";
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

  const filteredUsers = useMemo(() => {
    const searchText = search.trim().toLowerCase();

    return users.filter((user) => {
      const matchesSearch =
        !searchText ||
        [user.username, user.fullName, user.email, user.phoneNumber]
          .filter(Boolean)
          .some((value) => value.toLowerCase().includes(searchText));
      const matchesRole = roleFilter === "ALL" || (user.roles || []).includes(roleFilter);

      return matchesSearch && matchesRole;
    });
  }, [roleFilter, search, users]);

  const teacherCount = users.filter((user) => (user.roles || []).includes("ROLE_TEACHER")).length;
  const studentCount = users.filter((user) => (user.roles || []).includes("ROLE_STUDENT")).length;
  const totalPages = Math.max(1, Math.ceil(filteredUsers.length / pageSize));
  const safePage = Math.min(page, totalPages);
  const pageStart = (safePage - 1) * pageSize;
  const pageUsers = filteredUsers.slice(pageStart, pageStart + pageSize);

  function updateSearch(value) {
    setSearch(value);
    setPage(1);
  }

  function updateRoleFilter(value) {
    setRoleFilter(value);
    setPage(1);
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
            <button className="tree-node active" type="button" onClick={() => updateRoleFilter("ALL")}>
              <span>All users</span>
              <strong>{users.length}</strong>
            </button>
            <button className="tree-node" type="button" onClick={() => updateRoleFilter("ROLE_TEACHER")}>
              <span>Teachers</span>
              <strong>{teacherCount}</strong>
            </button>
            <button className="tree-node" type="button" onClick={() => updateRoleFilter("ROLE_STUDENT")}>
              <span>Students</span>
              <strong>{studentCount}</strong>
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
                Showing {filteredUsers.length === 0 ? 0 : pageStart + 1}-
                {Math.min(pageStart + pageSize, filteredUsers.length)} of {filteredUsers.length} matched users
              </p>
            </div>
            <Link className="btn btn-primary" to="/users/new">
              + Add User
            </Link>
          </div>

          <div className="user-toolbar">
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
            <button className="btn btn-secondary" type="button" onClick={() => updateSearch("")}>
              Reset
            </button>
          </div>

          {notice && <div className={`alert alert-${notice.type}`}>{notice.text}</div>}

          {loading ? (
            <div className="empty-state">Loading users...</div>
          ) : filteredUsers.length === 0 ? (
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
                    {pageUsers.map((user) => (
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
                  Page {safePage} of {totalPages}, {pageSize} per page
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
