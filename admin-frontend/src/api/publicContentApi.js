import { apiRequest } from "./client";

export function getLectures() {
  return apiRequest("/api/v1/lectures");
}

export function getPolls() {
  return apiRequest("/api/v1/polls");
}
