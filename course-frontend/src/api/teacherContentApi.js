import { apiRequest } from "./client";

export function getDashboard() {
  return apiRequest("/api/teacher/content/dashboard");
}

export function createLecture(lecture) {
  return apiRequest("/api/teacher/content/lectures", {
    method: "POST",
    body: JSON.stringify(lecture)
  });
}

export function deleteLecture(id) {
  return apiRequest(`/api/teacher/content/lectures/${id}`, {
    method: "DELETE"
  });
}

export function createPoll(poll) {
  return apiRequest("/api/teacher/content/polls", {
    method: "POST",
    body: JSON.stringify(poll)
  });
}

export function deletePoll(id) {
  return apiRequest(`/api/teacher/content/polls/${id}`, {
    method: "DELETE"
  });
}
