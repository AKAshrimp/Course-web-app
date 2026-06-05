CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(30),
    CONSTRAINT uk_users_username UNIQUE (username)
);

CREATE TABLE lectures (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE polls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(255),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    file_path VARCHAR(255),
    file_type VARCHAR(255),
    created_at TIMESTAMP,
    lecture_id BIGINT,
    CONSTRAINT fk_materials_lecture FOREIGN KEY (lecture_id) REFERENCES lectures (id)
);

CREATE TABLE lecture_materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    upload_time TIMESTAMP NOT NULL,
    lecture_id BIGINT,
    CONSTRAINT fk_lecture_materials_lecture FOREIGN KEY (lecture_id) REFERENCES lectures (id)
);

CREATE TABLE poll_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    text VARCHAR(150) NOT NULL,
    poll_id BIGINT,
    CONSTRAINT fk_poll_options_poll FOREIGN KEY (poll_id) REFERENCES polls (id)
);

CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    lecture_id BIGINT,
    poll_id BIGINT,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_comments_lecture FOREIGN KEY (lecture_id) REFERENCES lectures (id),
    CONSTRAINT fk_comments_poll FOREIGN KEY (poll_id) REFERENCES polls (id)
);

CREATE TABLE votes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    poll_id BIGINT NOT NULL,
    option_id BIGINT NOT NULL,
    voted_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_votes_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_votes_poll FOREIGN KEY (poll_id) REFERENCES polls (id),
    CONSTRAINT fk_votes_option FOREIGN KEY (option_id) REFERENCES poll_options (id)
);
