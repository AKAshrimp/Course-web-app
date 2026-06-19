import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import { createUser, getUser, updatePassword, updateUser } from "../api/adminUsersApi";
import AdminLayout from "../layout/AdminLayout";

const initialForm = {
  username: "",
  password: "",
  fullName: "",
  email: "",
  phoneNumber: "",
  roles: ["ROLE_STUDENT"]
};

export default function UserFormPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditing = Boolean(id);
  const [form, setForm] = useState(initialForm);
  const [loading, setLoading] = useState(false);
  const [notice, setNotice] = useState(null);

  useEffect(() => {
    if (!isEditing) {
      return;
    }

    getUser(id)
      .then((user) => {
        setForm({
          username: user.username,
          password: "",
          fullName: user.fullName,
          email: user.email,
          phoneNumber: user.phoneNumber || "",
          roles: user.roles || ["ROLE_STUDENT"]
        });
      })
      .catch(() => setNotice({ type: "error", text: "Failed to load user." }));
  }, [id, isEditing]);

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  function handleRoleChange(event) {
    const { checked, value } = event.target;
    setForm((current) => {
      const roles = checked ? [...current.roles, value] : current.roles.filter((role) => role !== value);
      return { ...current, roles };
    });
  }

  function validateForm() {
    if (!form.username.trim()) {
      return "Username is required.";
    }
    if (!isEditing && !form.password) {
      return "Password is required.";
    }
    if (!form.fullName.trim()) {
      return "Full name is required.";
    }
    if (!form.email.trim()) {
      return "Email is required.";
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      return "Enter a valid email.";
    }
    if (form.roles.length === 0) {
      return "Select at least one role.";
    }
    return "";
  }

  async function handleSubmit(event) {
    event.preventDefault();
    const validationError = validateForm();
    if (validationError) {
      setNotice({ type: "error", text: validationError });
      return;
    }

    setLoading(true);
    setNotice(null);

    try {
      if (isEditing) {
        await updateUser(id, form);
        if (form.password) {
          await updatePassword(id, form.password);
        }
      } else {
        await createUser(form);
      }

      navigate("/users");
    } catch (error) {
      setNotice({ type: "error", text: "Failed to save user." });
    } finally {
      setLoading(false);
    }
  }

  return (
    <AdminLayout
      title={isEditing ? "Edit User" : "New User"}
      subtitle={isEditing ? "Update account details and roles" : "Create a new course website account"}
    >
      <section className="notion-card">
        {notice && <div className={`alert alert-${notice.type}`}>{notice.text}</div>}

        <form className="notion-form user-form" onSubmit={handleSubmit}>
          <label className="form-field">
            <span>Username</span>
            <input
              className="field-input"
              disabled={isEditing}
              name="username"
              onChange={handleChange}
              value={form.username}
            />
          </label>

          <label className="form-field">
            <span>{isEditing ? "New password" : "Password"}</span>
            <input
              className="field-input"
              name="password"
              onChange={handleChange}
              placeholder={isEditing ? "Leave blank to keep current password" : ""}
              type="password"
              value={form.password}
            />
          </label>

          <label className="form-field">
            <span>Full name</span>
            <input className="field-input" name="fullName" onChange={handleChange} value={form.fullName} />
          </label>

          <label className="form-field">
            <span>Email</span>
            <input className="field-input" name="email" onChange={handleChange} type="email" value={form.email} />
          </label>

          <label className="form-field">
            <span>Phone number</span>
            <input className="field-input" name="phoneNumber" onChange={handleChange} value={form.phoneNumber} />
          </label>

          <fieldset className="role-fieldset">
            <legend>Roles</legend>
            <label className="checkbox-card">
              <input
                checked={form.roles.includes("ROLE_STUDENT")}
                onChange={handleRoleChange}
                type="checkbox"
                value="ROLE_STUDENT"
              />
              <span>Student</span>
            </label>
            <label className="checkbox-card">
              <input
                checked={form.roles.includes("ROLE_TEACHER")}
                onChange={handleRoleChange}
                type="checkbox"
                value="ROLE_TEACHER"
              />
              <span>Teacher</span>
            </label>
          </fieldset>

          <div className="button-row">
            <button className="btn btn-primary" type="submit" disabled={loading}>
              {loading ? "Saving..." : "Save"}
            </button>
            <button className="btn btn-secondary" type="button" onClick={() => navigate("/users")}>
              Cancel
            </button>
          </div>
        </form>
      </section>
    </AdminLayout>
  );
}
