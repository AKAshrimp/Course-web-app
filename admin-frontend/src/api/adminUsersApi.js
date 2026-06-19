import { apiRequest } from "./client";

export function getUsers({ page = 1, size = 10, search = "", role = "ALL" } = {}) {
  const params = new URLSearchParams({
    page: String(page),
    size: String(size),
    search,
    role
  });

  return apiRequest(`/api/admin/users?${params.toString()}`);
}

export function getUser(id) {
  return apiRequest(`/api/admin/users/${id}`);
}

export function createUser(user) {
  return apiRequest("/api/admin/users", {
    method: "POST",
    body: JSON.stringify(user)
  });
}

export function updateUser(id, user) {
  return apiRequest(`/api/admin/users/${id}`, {
    method: "PUT",
    body: JSON.stringify(user)
  });
}

export function updatePassword(id, newPassword) {
  return apiRequest(`/api/admin/users/${id}/password`, {
    method: "PUT",
    body: JSON.stringify({ newPassword })
  });
}

export function deleteUser(id) {
  return apiRequest(`/api/admin/users/${id}`, {
    method: "DELETE"
  });
}
