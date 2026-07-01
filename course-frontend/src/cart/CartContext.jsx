import { createContext, useContext, useEffect, useMemo, useState } from "react";

import {
  addBackendCartItem,
  clearBackendCart,
  getBackendCart,
  removeBackendCartItem,
  syncBackendCartItems
} from "../api/cartApi";
import { useAuth } from "../auth/AuthContext";
import {
  addCartItem,
  clearCartItems,
  getCartItems,
  removeCartItem
} from "./cartStorage.mjs";

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const auth = useAuth();
  const [items, setItems] = useState(() => getCartItems());

  useEffect(() => {
    let active = true;

    async function loadCart() {
      // Anonymous users keep using the localStorage cart.
      if (!auth.isAuthenticated) {
        setItems(getCartItems());
        return;
      }

      // After login, merge anonymous cart items into the persistent backend cart.
      const localItems = getCartItems();
      const response = localItems.length > 0
        ? await syncBackendCartItems(localItems.map((item) => item.id))
        : await getBackendCart();

      // Once the server cart is synced, localStorage should not keep stale duplicates.
      clearCartItems();
      if (active) {
        setItems(response.items || []);
      }
    }

    loadCart().catch(() => {
      if (active && !auth.isAuthenticated) {
        setItems(getCartItems());
      }
    });

    return () => {
      active = false;
    };
  }, [auth.isAuthenticated]);

  async function addToCart(course) {
    // Logged-in users write directly to the database-backed cart.
    if (auth.isAuthenticated) {
      const response = await addBackendCartItem(course.id);
      setItems(response.items || []);
      return;
    }

    setItems(addCartItem(window.localStorage, course));
  }

  async function removeFromCart(courseId) {
    // Match the storage layer to the user's auth state.
    if (auth.isAuthenticated) {
      const response = await removeBackendCartItem(courseId);
      setItems(response.items || []);
      return;
    }

    setItems(removeCartItem(window.localStorage, courseId));
  }

  async function clearCart() {
    if (auth.isAuthenticated) {
      const response = await clearBackendCart();
      setItems(response.items || []);
      return;
    }

    setItems(clearCartItems(window.localStorage));
  }

  function isInCart(courseId) {
    // Normalize IDs because API data and DOM state can mix string/number values.
    return items.some((item) => Number(item.id) === Number(courseId));
  }

  const value = useMemo(
    () => ({
      items,
      count: items.length,
      total: items.reduce((sum, item) => sum + Number(item.price || 0), 0),
      addToCart,
      removeFromCart,
      clearCart,
      isInCart
    }),
    [items]
  );

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  return useContext(CartContext);
}
