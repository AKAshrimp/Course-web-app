import { apiRequest } from "./client";

export function getLectures() {
  return apiRequest("/api/v1/lectures");
}

export function getLecture(lectureId) {
  return apiRequest(`/api/v1/lectures/${lectureId}`);
}

export function addLectureComment(lectureId, content) {
  return apiRequest(`/api/v1/lectures/${lectureId}/comments`, {
    method: "POST",
    body: JSON.stringify({ content })
  });
}

export function getPolls() {
  return apiRequest("/api/v1/polls");
}
