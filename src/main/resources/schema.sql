CREATE TABLE IF NOT EXISTS users (
    id            UUID PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS subjects (
    id           UUID PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(255) NOT NULL,
    user_id UUID NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS professors (
    id           UUID PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    email        VARCHAR(255) UNIQUE NOT NULL,
    user_id UUID NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS subjects_professors (
    subject_id   UUID,
    professor_id UUID,
    PRIMARY KEY (subject_id, professor_id),
    FOREIGN KEY (subject_id) REFERENCES subjects (id),
    FOREIGN KEY (professor_id) REFERENCES professors (id)
);

CREATE TABLE IF NOT EXISTS grades (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    value DOUBLE PRECISION NOT NULL,
    subject_id UUID NOT NULL,
    FOREIGN KEY (subject_id) REFERENCES subjects (id)
);