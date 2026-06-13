import { Navigate, Route, Routes } from "react-router-dom";

import { ConfigProvider } from "antd";

import ProtectedRoute from "./auth/ProtectedRoute";
import LoginPage from "./pages/LoginPage";
import UserFormPage from "./pages/UserFormPage";
import UsersPage from "./pages/UsersPage";

export default function App() {
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: "#1f4ed8",
          borderRadius: 10
        }
      }}
    >
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route
          path="/users"
          element={
            <ProtectedRoute>
              <UsersPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/users/new"
          element={
            <ProtectedRoute>
              <UserFormPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/users/:id"
          element={
            <ProtectedRoute>
              <UserFormPage />
            </ProtectedRoute>
          }
        />
        <Route path="*" element={<Navigate to="/users" replace />} />
      </Routes>
    </ConfigProvider>
  );
}
