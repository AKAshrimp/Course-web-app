const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

function clearAuthState() {
  localStorage.removeItem("adminToken");
  localStorage.removeItem("adminUsername");
  localStorage.removeItem("adminFullName");
  localStorage.removeItem("adminRoles");
}

export async function apiRequest(path, options = {}) {
  const { auth = true, redirectOnUnauthorized = true, ...fetchOptions } = options;
  const token = localStorage.getItem("adminToken");

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...fetchOptions,
    headers: {
      "Content-Type": "application/json",
      ...(auth && token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {})
    }
  });

  if (response.status === 401 && redirectOnUnauthorized) {
    clearAuthState();
    window.location.assign("/login");
  }

  if (!response.ok) {
    throw new Error(`API error: ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}
