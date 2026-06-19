import { Navigate } from "react-router-dom";

import { useAuth } from "./AuthContext";

export default function ProtectedRoute({ children, role }) {
  const auth = useAuth();

  if (!auth.isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (role && !auth.roles.includes(role)) {
    return <Navigate to="/" replace />;
  }

  return children;
}
