import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { getPopularCourses } from "../api/publicContentApi";
import { useCart } from "../cart/CartContext";

function formatPrice(value) {
  const amount = Number(value || 0);
  return amount === 0 ? "Free" : `US$${amount.toFixed(2)}`;
}

export default function CartPage() {
  const cart = useCart();
  const [popularCourses, setPopularCourses] = useState([]);
  const [popularLoading, setPopularLoading] = useState(true);

  useEffect(() => {
    let active = true;

    async function loadPopularCourses() {
      try {
        const response = await getPopularCourses(0, 4);
        if (active) {
          setPopularCourses(response.items || []);
        }
      } finally {
        if (active) {
          setPopularLoading(false);
        }
      }
    }

    loadPopularCourses();

    return () => {
      active = false;
    };
  }, []);

  return (
    <main className="cart-shell">
      <section className="cart-page">
        <h1>Shopping Cart</h1>

        {cart.items.length === 0 ? (
          <div className="cart-empty">
            {/* Empty state keeps the page useful even before the user adds a course. */}
            <p>Your cart is empty – let's change that. Time to learn some new skills!</p>
            <Link className="btn btn-primary" to="/">
              Browse courses
            </Link>
          </div>
        ) : (
          <div className="cart-layout">
            <div className="cart-items">
              <div className="cart-count">{cart.count} Course in Cart</div>
              {/* Cart items come from localStorage for guests and from the backend for logged-in users. */}
              {cart.items.map((item) => (
                <article className="cart-item" key={item.id}>
                  <div className={`cart-item-art cart-item-art-${subjectTheme(item.subject)}`}>
                    <span>{item.subject}</span>
                  </div>
                  <div className="cart-item-main">
                    <h2>{item.title}</h2>
                    <p>By CourseHub instructors</p>
                    <div className="cart-item-meta">
                      <span>Bestseller</span>
                      <strong>{item.reviewCount?.toLocaleString()} reviews</strong>
                      <span>{item.lectureCount?.toLocaleString()} lectures</span>
                      <span>{item.level}</span>
                    </div>
                  </div>
                  <div className="cart-item-actions">
                    <button type="button" onClick={() => cart.removeFromCart(item.id)}>
                      Remove
                    </button>
                    <button type="button">Save for Later</button>
                    <button type="button">Move to Wishlist</button>
                  </div>
                  <strong className="cart-item-price">{formatPrice(item.price)}</strong>
                </article>
              ))}
              <button className="cart-clear-button" type="button" onClick={cart.clearCart}>
                Clear cart
              </button>
            </div>

            <aside className="cart-summary">
              {/* Summary is derived from the same cart context, so it stays in sync after remove/clear. */}
              <span>Total:</span>
              <strong>{formatPrice(cart.total)}</strong>
              <button className="btn btn-primary" type="button">
                Proceed to Checkout →
              </button>
              <small>You won't be charged yet</small>
              <button className="cart-coupon-button" type="button">
                Apply Coupon
              </button>
            </aside>
          </div>
        )}

        <section className="cart-popular-section" aria-label="Popular courses">
          <h2>Popular courses</h2>
          {popularLoading ? (
            <div className="empty-state">Loading popular courses...</div>
          ) : (
            <div className="cart-popular-grid">
              {popularCourses.map((course) => (
                <article className="cart-popular-card" key={course.id}>
                  <div className={`cart-popular-art cart-item-art-${subjectTheme(course.subject)}`}>
                    <span>{course.subject}</span>
                  </div>
                  <div className="cart-popular-body">
                    <h3>{course.title}</h3>
                    <p>{course.subscriberCount?.toLocaleString()} students</p>
                    <div className="cart-popular-badges">
                      <span>{course.level}</span>
                      <span>{course.paid ? "Paid" : "Free"}</span>
                    </div>
                    <div className="cart-popular-meta">
                      <strong>{course.lectureCount?.toLocaleString()}</strong>
                      <span>lectures</span>
                      <span>{course.reviewCount?.toLocaleString()} reviews</span>
                    </div>
                    <div className="cart-popular-footer">
                      <strong>{formatPrice(course.price)}</strong>
                      <button type="button" onClick={() => cart.addToCart(course)}>
                        {cart.isInCart(course.id) ? "Added" : "Add to cart"}
                      </button>
                    </div>
                  </div>
                </article>
              ))}
            </div>
          )}
        </section>
      </section>
    </main>
  );
}

function subjectTheme(subject) {
  const themes = {
    "Business Finance": "mint",
    "Graphic Design": "lavender",
    "Musical Instruments": "yellow",
    "Web Development": "sky"
  };

  return themes[subject] || "sky";
}
