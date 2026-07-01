export const CART_STORAGE_KEY = "courseCartItems";

export function getCartItems(storage = window.localStorage) {
  try {
    // Treat broken or missing localStorage data as an empty cart.
    const rawItems = storage.getItem(CART_STORAGE_KEY);
    return rawItems ? JSON.parse(rawItems) : [];
  } catch (error) {
    return [];
  }
}

export function addCartItem(storage = window.localStorage, course) {
  const items = getCartItems(storage);
  // Keep local cart behavior idempotent: one course should appear only once.
  if (items.some((item) => Number(item.id) === Number(course.id))) {
    return items;
  }

  const nextItems = [...items, normalizeCourse(course)];
  saveCartItems(storage, nextItems);
  return nextItems;
}

export function removeCartItem(storage = window.localStorage, courseId) {
  const nextItems = getCartItems(storage).filter((item) => Number(item.id) !== Number(courseId));
  saveCartItems(storage, nextItems);
  return nextItems;
}

export function clearCartItems(storage = window.localStorage) {
  storage.removeItem(CART_STORAGE_KEY);
  return [];
}

export function getCartTotal(storage = window.localStorage) {
  return getCartItems(storage).reduce((total, item) => total + Number(item.price || 0), 0);
}

function saveCartItems(storage, items) {
  storage.setItem(CART_STORAGE_KEY, JSON.stringify(items));
}

function normalizeCourse(course) {
  // Store only the fields needed to render the cart without another API call.
  return {
    id: course.id,
    title: course.title,
    paid: course.paid,
    price: Number(course.price || 0),
    subscriberCount: course.subscriberCount,
    reviewCount: course.reviewCount,
    lectureCount: course.lectureCount,
    level: course.level,
    contentDuration: course.contentDuration,
    subject: course.subject
  };
}
