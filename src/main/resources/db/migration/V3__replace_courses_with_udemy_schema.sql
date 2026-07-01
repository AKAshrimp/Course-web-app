DROP TABLE IF EXISTS courses;

CREATE TABLE courses (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    paid BOOLEAN NOT NULL,
    price DOUBLE NOT NULL,
    subscriber_count INT NOT NULL,
    review_count INT NOT NULL,
    lecture_count INT NOT NULL,
    level VARCHAR(80) NOT NULL,
    content_duration DOUBLE NOT NULL,
    published_at TIMESTAMP NOT NULL,
    subject VARCHAR(120) NOT NULL
);

CREATE INDEX idx_courses_popular
    ON courses (subscriber_count DESC, review_count DESC, id ASC);

CREATE INDEX idx_courses_subject
    ON courses (subject);
