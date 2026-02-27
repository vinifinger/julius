-- =============================================
-- Julius — Initial Schema
-- =============================================

CREATE TABLE users (
    id            BINARY(16)   NOT NULL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE accounts (
    id         BINARY(16)     NOT NULL PRIMARY KEY,
    user_id    BINARY(16)     NOT NULL,
    name       VARCHAR(50)    NOT NULL,
    balance    DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    currency   VARCHAR(10)    NOT NULL DEFAULT 'BRL',
    created_at TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE competences (
    id         BINARY(16) NOT NULL PRIMARY KEY,
    user_id    BINARY(16) NOT NULL,
    month      INT    NOT NULL CHECK (month BETWEEN 1 AND 12),
    year       INT   NOT NULL,
    created_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE (user_id, month, year),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE categories (
    id         BINARY(16)  NOT NULL PRIMARY KEY,
    user_id    BINARY(16)  NOT NULL,
    name       VARCHAR(50) NOT NULL,
    color_hex  VARCHAR(7),
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE transactions (
    id            BINARY(16)     NOT NULL PRIMARY KEY,
    account_id    BINARY(16)     NOT NULL,
    category_id   BINARY(16)     NOT NULL,
    competence_id BINARY(16)     NOT NULL,
    user_id       BINARY(16)     NOT NULL,
    parent_id     BINARY(16),
    description   VARCHAR(255)   NOT NULL,
    amount        DECIMAL(12, 2) NOT NULL,
    date_time     DATETIME       NOT NULL,
    status        ENUM('PENDING', 'PAID') NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id)    REFERENCES accounts(id),
    FOREIGN KEY (category_id)   REFERENCES categories(id),
    FOREIGN KEY (competence_id) REFERENCES competences(id),
    FOREIGN KEY (user_id)       REFERENCES users(id),
    FOREIGN KEY (parent_id)     REFERENCES transactions(id)
) ENGINE=InnoDB;

-- ── Índices de performance ──
CREATE INDEX idx_transactions_date_time  ON transactions(date_time);
CREATE INDEX idx_transactions_user_id    ON transactions(user_id);
CREATE INDEX idx_transactions_account_id ON transactions(account_id);
