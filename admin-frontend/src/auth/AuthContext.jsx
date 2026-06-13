import { createContext, useContext, useMemo, useState } from "react";

import { login as loginRequest } from "../api/authApi";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem("adminToken"));
  const [username, setUsername] = useState(() => localStorage.getItem("adminUsername"));

  async function login(usernameValue, password) {
    const result = await loginRequest(usernameValue, password);
    localStorage.setItem("adminToken", result.token);
    localStorage.setItem("adminUsername", result.username);
    setToken(result.token);
    setUsername(result.username);
  }

  function logout() {
    localStorage.removeItem("adminToken");
    localStorage.removeItem("adminUsername");
    setToken(null);
    setUsername(null);
  }

  const value = useMemo(
    () => ({
      isAuthenticated: Boolean(token),
      username,
      login,
      logout
    }),
    [token, username]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
