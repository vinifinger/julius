-- Schema for H2 integration tests (mirrors V1__create_initial_schema.sql)

CREATE TABLE IF NOT EXISTS users (
    id            UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS accounts (
    id         UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name       VARCHAR(50) NOT NULL,
    balance    DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    currency   VARCHAR(10) NOT NULL DEFAULT 'BRL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS competences (
    id         UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    month      INT NOT NULL,
    year       INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, month, year)
);

CREATE TABLE IF NOT EXISTS categories (
    id         UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name       VARCHAR(50) NOT NULL,
    color_hex  VARCHAR(7),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transactions (
    id            UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    account_id    UUID NOT NULL REFERENCES accounts(id),
    category_id   UUID NOT NULL REFERENCES categories(id),
    competence_id UUID NOT NULL REFERENCES competences(id),
    user_id       UUID NOT NULL REFERENCES users(id),
    parent_id     UUID REFERENCES transactions(id),
    description   VARCHAR(255) NOT NULL,
    amount        DECIMAL(12, 2) NOT NULL,
    date_time     TIMESTAMP NOT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
