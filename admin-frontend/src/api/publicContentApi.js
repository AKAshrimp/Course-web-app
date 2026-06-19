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
