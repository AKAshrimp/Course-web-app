import { apiRequest } from "./client";

export function login(username, password) {
  return apiRequest("/api/admin/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password })
  });
}
