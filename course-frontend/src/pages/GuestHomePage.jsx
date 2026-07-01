import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { getCoursesBySubject, getPopularCourses } from "../api/publicContentApi";
import { useAuth } from "../auth/AuthContext";
import { useCart } from "../cart/CartContext";

const PAGE_SIZE = 4;
const skillCategories = [
  {
    name: "Business Finance",
    subject: "Business Finance"
  },
  {
    name: "Graphic Design",
    subject: "Graphic Design"
  },
  {
    name: "Musical Instruments",
    subject: "Musical Instruments"
  },
  {
    name: "Web Design",
    subject: "Web Development"
  }
];

function formatPrice(value) {
  const amount = Number(value || 0);
  return amount === 0 ? "Free" : `US$${amount.toFixed(2)}`;
}

function formatDuration(value) {
  const hours = Number(value || 0);
  return hours === 1 ? "1 total hour" : `${hours.toFixed(hours % 1 === 0 ? 0 : 1)} total hours`;
}

function getSubjectTheme(subject) {
  const themes = {
    "Business Finance": "mint",
    "Graphic Design": "lavender",
    "Musical Instruments": "yellow",
    "Web Development": "sky"
  };

  return themes[subject] || "sky";
}

function CourseCard({ course }) {
  const cart = useCart();
  const subject = course.subject || "Course";
  const theme = getSubjectTheme(subject);
  const subjectInitials = subject
    .split(" ")
    .map((word) => word[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();

  return (
    <article className="guest-course-card">
      <div className={`guest-course-art guest-course-art-${theme}`}>
        <div className="guest-course-art-orb">{subjectInitials}</div>
        <span>{subject}</span>
      </div>
      <div className="guest-course-body">
        <h3>{course.title}</h3>
        <span className="guest-course-instructor">{course.subscriberCount?.toLocaleString()} students</span>
        <div className="guest-course-badges">
          <span>{course.level}</span>
          <span>{course.paid ? "Paid" : "Free"}</span>
        </div>
        <div className="guest-course-meta">
          <strong>{course.lectureCount?.toLocaleString()}</strong>
          <span>lectures</span>
          <span>{course.reviewCount?.toLocaleString()} reviews</span>
        </div>
        <div className="guest-course-price">
          <strong>{formatPrice(course.price)}</strong>
        </div>
      </div>
      <aside className="guest-course-preview" aria-label={`${course.title} preview`}>
        <h3>{course.title}</h3>
        <div className="guest-preview-meta">
          <span>{subject}</span>
          <strong>{course.subscriberCount?.toLocaleString()} students</strong>
        </div>
        <p className="guest-preview-detail">
          {formatDuration(course.contentDuration)} · {course.level || "All Levels"} · {course.lectureCount?.toLocaleString()} lectures
        </p>
        <p className="guest-preview-description">
          Explore a real Udemy dataset course with pricing, demand, reviews, duration, and subject data imported from CSV.
        </p>
        <ul>
          <li>Compare popularity using real subscriber counts</li>
          <li>Review course depth through lecture and duration data</li>
          <li>Filter-ready subject data for future Redis caching</li>
        </ul>
        <button className="guest-add-cart-button" type="button" onClick={() => cart.addToCart(course)}>
          {/* The cart context decides whether this writes to localStorage or the backend. */}
          {cart.isInCart(course.id) ? "Added to cart" : "Add to cart"}
        </button>
      </aside>
    </article>
  );
}

export default function GuestHomePage() {
  const auth = useAuth();
  const cart = useCart();
  const [page, setPage] = useState(0);
  const [coursePage, setCoursePage] = useState({ items: [], totalPages: 0, total: 0 });
  const [loading, setLoading] = useState(true);
  const [notice, setNotice] = useState(null);
  const [activeSkill, setActiveSkill] = useState(skillCategories[0]);
  const [skillPage, setSkillPage] = useState({ items: [], totalPages: 0, total: 0 });
  const [skillLoading, setSkillLoading] = useState(true);
  const [skillNotice, setSkillNotice] = useState(null);

  useEffect(() => {
    let active = true;

    async function loadCourses() {
      setLoading(true);
      setNotice(null);
      try {
        const data = await getPopularCourses(page, PAGE_SIZE);
        if (active) {
          setCoursePage(data);
        }
      } catch (error) {
        if (active) {
          setNotice("Popular courses are temporarily unavailable.");
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadCourses();

    return () => {
      active = false;
    };
  }, [page]);

  useEffect(() => {
    let active = true;

    async function loadSkillCourses() {
      setSkillLoading(true);
      setSkillNotice(null);
      try {
        const data = await getCoursesBySubject(activeSkill.subject, 0, PAGE_SIZE);
        if (active) {
          setSkillPage(data);
        }
      } catch (error) {
        if (active) {
          setSkillNotice("Skill courses are temporarily unavailable.");
        }
      } finally {
        if (active) {
          setSkillLoading(false);
        }
      }
    }

    loadSkillCourses();

    return () => {
      active = false;
    };
  }, [activeSkill]);

  const hasPrevious = page > 0;
  const hasNext = page + 1 < coursePage.totalPages;
  const isLoggedIn = auth.isAuthenticated;
  const primaryHeroLink = isLoggedIn ? "/workspace" : "/register";
  const primaryHeroText = isLoggedIn ? "Continue learning" : "Get Personal Plan";

  async function handleLogout() {
    await auth.logout();
  }

  return (
    <div className="guest-shell">
      <div className="guest-promo">
        <strong>Limited time offer</strong>
        <span>Learn career-ready skills from US$10.99. New courses are added regularly.</span>
        <button type="button" aria-label="Dismiss promotion">
          ×
        </button>
      </div>

      <header className="guest-nav">
        <Link className="guest-brand" to="/">
          <span className="guest-brand-mark">⌃</span>
          CourseHub
        </Link>
        <nav className="guest-nav-links" aria-label="Guest navigation">
          <a href="#popular-courses">Explore</a>
          <Link to={isLoggedIn ? "/workspace" : "/login"}>My learning</Link>
          {auth.isTeacher && <Link to="/dashboard">Teacher dashboard</Link>}
        </nav>
        <label className="guest-search">
          <span>⌕</span>
          <input type="search" placeholder="Search for anything" />
        </label>
        <div className="guest-actions">
          <Link className="guest-icon-button guest-cart-button" to="/cart" aria-label={`Cart with ${cart.count} courses`}>
            <svg className="guest-cart-icon" viewBox="0 0 24 24" aria-hidden="true">
              <path d="M7 18.5a1.5 1.5 0 1 0 0 3 1.5 1.5 0 0 0 0-3Zm10 0a1.5 1.5 0 1 0 0 3 1.5 1.5 0 0 0 0-3ZM3 3h2.2l2.1 10.6A2 2 0 0 0 9.3 15h7.9a2 2 0 0 0 1.9-1.4L21 7H7.1" />
            </svg>
            {/* Show a badge only after at least one course has been added. */}
            {cart.count > 0 && <span>{cart.count}</span>}
          </Link>
          {isLoggedIn ? (
            <>
              <Link className="btn btn-secondary" to="/profile">
                Profile
              </Link>
              <button className="btn btn-primary" type="button" onClick={handleLogout}>
                Logout
              </button>
            </>
          ) : (
            <>
              <Link className="btn btn-secondary" to="/login">
                Login
              </Link>
              <Link className="btn btn-primary" to="/register">
                Register
              </Link>
            </>
          )}
          <button className="guest-icon-button" type="button" aria-label="Language">
            ◎
          </button>
        </div>
      </header>

      <main>
        <section className="guest-hero">
          <button className="guest-hero-arrow guest-hero-arrow-left" type="button" aria-label="Previous hero">
            ‹
          </button>
          <div className="guest-hero-card">
            <h1>Build in-demand skills</h1>
            <p>Get access to 26,000 courses from real-world experts with one subscription.</p>
            <div className="guest-hero-actions">
              <Link className="btn btn-primary" to={primaryHeroLink}>
                {primaryHeroText}
              </Link>
              <a className="btn btn-secondary" href="#popular-courses">
                Learn AI
              </a>
            </div>
          </div>
          <div className="guest-hero-visual">
            <img src="/hero-learning.jpg" alt="Learner with career skill icons" />
          </div>
          <button className="guest-hero-arrow guest-hero-arrow-right" type="button" aria-label="Next hero">
            ›
          </button>
        </section>

        <section className="guest-section" id="popular-courses">
          <div className="guest-section-header">
            <div>
              <h2>Popular courses</h2>
            </div>
          </div>

          <div className="guest-course-frame">
            <button className="carousel-button carousel-button-left" type="button" onClick={() => setPage((value) => value - 1)} disabled={!hasPrevious || loading} aria-label="Previous courses">
              ‹
            </button>
            <button className="carousel-button carousel-button-right" type="button" onClick={() => setPage((value) => value + 1)} disabled={!hasNext || loading} aria-label="Next courses">
              ›
            </button>

            {notice && <div className="empty-state">{notice}</div>}
            {loading ? (
              <div className="empty-state">Loading popular courses...</div>
            ) : coursePage.items.length === 0 ? (
              <div className="empty-state">No popular courses yet.</div>
            ) : (
              <div className="guest-course-grid">
                {coursePage.items.map((course) => (
                  <CourseCard course={course} key={course.id} />
                ))}
              </div>
            )}
          </div>

        </section>

        <section className="guest-section guest-skills-section">
          <div className="guest-section-header">
            <div>
              <h2>Skills to transform your career and life</h2>
              <p>Practical learning paths for creative, business, and technical growth.</p>
            </div>
          </div>

          <div className="guest-skill-tabs" aria-label="Skill categories">
            {skillCategories.map((skill) => (
              <button className={activeSkill.name === skill.name ? "active" : ""} type="button" key={skill.name} onClick={() => setActiveSkill(skill)}>
                {skill.name}
              </button>
            ))}
          </div>

          <div className="guest-course-frame">
            {skillNotice && <div className="empty-state">{skillNotice}</div>}
            {skillLoading ? (
              <div className="empty-state">Loading {activeSkill.name} courses...</div>
            ) : skillPage.items.length === 0 ? (
              <div className="empty-state">No {activeSkill.name} courses yet.</div>
            ) : (
              <div className="guest-course-grid">
                {skillPage.items.map((course) => (
                  <CourseCard course={course} key={course.id} />
                ))}
              </div>
            )}
          </div>

          <a className="guest-skill-link" href="#popular-courses">
            Show all featured skill courses →
          </a>
        </section>
      </main>
    </div>
  );
}
