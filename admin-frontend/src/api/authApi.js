import { apiRequest } from "./client";

export function login(username, password) {
  return apiRequest("/api/auth/login", {
    auth: false,
    redirectOnUnauthorized: false,
    method: "POST",
    body: JSON.stringify({ username, password })
  });
}

export function register(user) {
  return apiRequest("/api/auth/register", {
    auth: false,
    redirectOnUnauthorized: false,
    method: "POST",
    body: JSON.stringify(user)
  });
}

export function logout() {
  return apiRequest("/api/auth/logout", {
    redirectOnUnauthorized: false,
    method: "POST"
  });
}
