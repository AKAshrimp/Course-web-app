import { Navigate, Route, Routes } from "react-router-dom";

import ProtectedRoute from "./auth/ProtectedRoute";
import HomePage from "./pages/HomePage";
import LectureDetailPage from "./pages/LectureDetailPage";
import LoginPage from "./pages/LoginPage";
import PollDetailPage from "./pages/PollDetailPage";
import RegisterPage from "./pages/RegisterPage";
import UserFormPage from "./pages/UserFormPage";
import UsersPage from "./pages/UsersPage";

export default function App() {
  return (
    <Routes>
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <HomePage />
          </ProtectedRoute>
        }
      />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="/lectures/:id"
        element={
          <ProtectedRoute>
            <LectureDetailPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/polls/:id"
        element={
          <ProtectedRoute>
            <PollDetailPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/users"
        element={
          <ProtectedRoute role="ROLE_TEACHER">
            <UsersPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/users/new"
        element={
          <ProtectedRoute role="ROLE_TEACHER">
            <UserFormPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/users/:id"
        element={
          <ProtectedRoute role="ROLE_TEACHER">
            <UserFormPage />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
