import { apiRequest } from "./client";

export function getDashboard() {
  return apiRequest("/api/admin/content/dashboard");
}

export function createLecture(lecture) {
  return apiRequest("/api/admin/content/lectures", {
    method: "POST",
    body: JSON.stringify(lecture)
  });
}

export function deleteLecture(id) {
  return apiRequest(`/api/admin/content/lectures/${id}`, {
    method: "DELETE"
  });
}

export function createPoll(poll) {
  return apiRequest("/api/admin/content/polls", {
    method: "POST",
    body: JSON.stringify(poll)
  });
}

export function deletePoll(id) {
  return apiRequest(`/api/admin/content/polls/${id}`, {
    method: "DELETE"
  });
}
