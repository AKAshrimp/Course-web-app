CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(180) NOT NULL,
    subtitle VARCHAR(260) NOT NULL,
    instructor_name VARCHAR(120) NOT NULL,
    category VARCHAR(80) NOT NULL,
    level VARCHAR(60) NOT NULL,
    rating DOUBLE NOT NULL,
    review_count INT NOT NULL,
    price_usd DOUBLE NOT NULL,
    original_price_usd DOUBLE NOT NULL,
    image_theme VARCHAR(40) NOT NULL,
    popular_score INT NOT NULL,
    published BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_courses_popular
    ON courses (published, popular_score DESC, id ASC);
