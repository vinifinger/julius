-- =============================================
-- Julius — Installment Fields for Transactions
-- =============================================

ALTER TABLE transactions
    ADD COLUMN installment_count  INT NULL AFTER parent_id,
    ADD COLUMN installment_number INT NULL AFTER installment_count;

CREATE INDEX idx_transactions_parent_id ON transactions(parent_id);
