import { Navigate, Route, Routes } from "react-router-dom";

import ProtectedRoute from "./auth/ProtectedRoute";
import DashboardPage from "./pages/DashboardPage";
import HomePage from "./pages/HomePage";
import LectureDetailPage from "./pages/LectureDetailPage";
import LoginPage from "./pages/LoginPage";
import PollDetailPage from "./pages/PollDetailPage";
import ProfilePage from "./pages/ProfilePage";
import RegisterPage from "./pages/RegisterPage";

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
        path="/dashboard"
        element={
          <ProtectedRoute role="ROLE_TEACHER">
            <DashboardPage />
          </ProtectedRoute>
        }
      />
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
        path="/profile"
        element={
          <ProtectedRoute>
            <ProfilePage />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
