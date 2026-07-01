DROP INDEX idx_courses_popular ON courses;

CREATE INDEX idx_courses_popular
    ON courses (review_count DESC, subscriber_count DESC, id ASC);
