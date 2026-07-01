import test from "node:test";
import assert from "node:assert/strict";

import {
  addCartItem,
  clearCartItems,
  getCartItems,
  getCartTotal,
  removeCartItem
} from "../src/cart/cartStorage.mjs";

function createStorage() {
  const data = new Map();
  return {
    getItem(key) {
      return data.get(key) || null;
    },
    setItem(key, value) {
      data.set(key, value);
    },
    removeItem(key) {
      data.delete(key);
    }
  };
}

const reactCourse = {
  id: 625204,
  title: "The Web Developer Bootcamp",
  price: 200,
  paid: true,
  reviewCount: 27445,
  lectureCount: 342,
  level: "All Levels",
  contentDuration: 43,
  subject: "Web Development"
};

test("cart storage adds courses once and totals prices", () => {
  const storage = createStorage();

  addCartItem(storage, reactCourse);
  addCartItem(storage, reactCourse);

  assert.equal(getCartItems(storage).length, 1);
  assert.equal(getCartTotal(storage), 200);
});

test("cart storage removes and clears courses", () => {
  const storage = createStorage();

  addCartItem(storage, reactCourse);
  removeCartItem(storage, reactCourse.id);
  assert.deepEqual(getCartItems(storage), []);

  addCartItem(storage, reactCourse);
  clearCartItems(storage);
  assert.deepEqual(getCartItems(storage), []);
});
