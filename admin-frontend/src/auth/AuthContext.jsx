import { createContext, useContext, useMemo, useState } from "react";

import { login as loginRequest, logout as logoutRequest } from "../api/authApi";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem("adminToken"));
  const [username, setUsername] = useState(() => localStorage.getItem("adminUsername"));
  const [fullName, setFullName] = useState(() => localStorage.getItem("adminFullName"));
  const [roles, setRoles] = useState(() => JSON.parse(localStorage.getItem("adminRoles") || "[]"));

  async function login(usernameValue, password) {
    const result = await loginRequest(usernameValue, password);
    localStorage.setItem("adminToken", result.token);
    localStorage.setItem("adminUsername", result.username);
    localStorage.setItem("adminFullName", result.fullName || result.username);
    localStorage.setItem("adminRoles", JSON.stringify(result.roles || []));
    setToken(result.token);
    setUsername(result.username);
    setFullName(result.fullName || result.username);
    setRoles(result.roles || []);
    return result;
  }

  async function logout() {
    try {
      await logoutRequest();
    } finally {
      localStorage.removeItem("adminToken");
      localStorage.removeItem("adminUsername");
      localStorage.removeItem("adminFullName");
      localStorage.removeItem("adminRoles");
      setToken(null);
      setUsername(null);
      setFullName(null);
      setRoles([]);
    }
  }

  const value = useMemo(
    () => ({
      isAuthenticated: Boolean(token),
      username,
      fullName,
      roles,
      isTeacher: roles.includes("ROLE_TEACHER"),
      login,
      logout
    }),
    [fullName, roles, token, username]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
