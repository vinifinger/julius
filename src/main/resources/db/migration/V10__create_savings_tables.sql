CREATE TABLE savings (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    color_hex VARCHAR(7),
    icon VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_savings_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE savings_history (
    id UUID PRIMARY KEY,
    savings_id UUID NOT NULL,
    account_id UUID NOT NULL,
    `type` VARCHAR(50) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_savings_history_savings FOREIGN KEY (savings_id) REFERENCES savings (id) ON DELETE CASCADE,
    CONSTRAINT fk_savings_history_account FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE
);
