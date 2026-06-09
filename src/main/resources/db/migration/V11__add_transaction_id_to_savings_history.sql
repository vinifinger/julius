ALTER TABLE savings_history
ADD COLUMN transaction_id BINARY(16);

ALTER TABLE savings_history
ADD CONSTRAINT fk_savings_history_transaction FOREIGN KEY (transaction_id) REFERENCES transactions (id) ON DELETE SET NULL;
