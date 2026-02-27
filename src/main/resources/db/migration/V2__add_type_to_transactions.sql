-- =============================================
-- Julius — Add type column to transactions
-- =============================================

ALTER TABLE transactions
    ADD COLUMN type ENUM('REVENUE', 'EXPENSE') NOT NULL DEFAULT 'EXPENSE'
    AFTER user_id;
