import { apiRequest } from "./client";

export function getBackendCart() {
  return apiRequest("/api/v1/cart");
}

export function addBackendCartItem(courseId) {
  return apiRequest("/api/v1/cart/items", {
    method: "POST",
    body: JSON.stringify({ courseId })
  });
}

export function syncBackendCartItems(courseIds) {
  // Used after login to merge the anonymous localStorage cart into the user's DB cart.
  return apiRequest("/api/v1/cart/items/batch", {
    method: "POST",
    body: JSON.stringify({ courseIds })
  });
}

export function removeBackendCartItem(courseId) {
  return apiRequest(`/api/v1/cart/items/${courseId}`, {
    method: "DELETE"
  });
}

export function clearBackendCart() {
  return apiRequest("/api/v1/cart/items", {
    method: "DELETE"
  });
}
