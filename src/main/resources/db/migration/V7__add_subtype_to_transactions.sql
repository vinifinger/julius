-- =============================================
-- Julius — Add subtype column to transactions
-- =============================================

ALTER TABLE transactions
    ADD COLUMN subtype ENUM('FIXED', 'VARIABLE') NULL
    AFTER type;
