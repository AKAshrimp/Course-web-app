import { apiRequest } from "./client";

export function login(username, password) {
  return apiRequest("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password })
  });
}

export function register(user) {
  return apiRequest("/api/auth/register", {
    method: "POST",
    body: JSON.stringify(user)
  });
}
