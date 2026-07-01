import { apiRequest } from "./client";

export function getPopularCourses(page = 0, size = 4) {
  return apiRequest(`/api/v1/courses/popular?page=${page}&size=${size}`, { auth: false });
}

export function getCoursesBySubject(subject, page = 0, size = 4) {
  const params = new URLSearchParams({
    subject,
    page: String(page),
    size: String(size)
  });

  return apiRequest(`/api/v1/courses?${params.toString()}`, { auth: false });
}

export function getLectures() {
  return apiRequest("/api/v1/lectures", { auth: false });
}

export function getLecture(lectureId) {
  return apiRequest(`/api/v1/lectures/${lectureId}`, { auth: false });
}

export function addLectureComment(lectureId, content) {
  return apiRequest(`/api/v1/lectures/${lectureId}/comments`, {
    method: "POST",
    body: JSON.stringify({ content })
  });
}

export function getPolls() {
  return apiRequest("/api/v1/polls", { auth: false });
}

export function getPoll(pollId) {
  return apiRequest(`/api/v1/polls/${pollId}`);
}

export function votePoll(pollId, optionId) {
  return apiRequest(`/api/v1/polls/${pollId}/vote`, {
    method: "POST",
    body: JSON.stringify({ optionId })
  });
}

export function addPollComment(pollId, content) {
  return apiRequest(`/api/v1/polls/${pollId}/comments`, {
    method: "POST",
    body: JSON.stringify({ content })
  });
}

export function getProfile() {
  return apiRequest("/api/v1/profile");
}

export function updateProfile(profile) {
  return apiRequest("/api/v1/profile", {
    method: "PUT",
    body: JSON.stringify(profile)
  });
}

export function getMyVotes() {
  return apiRequest("/api/v1/me/votes");
}
