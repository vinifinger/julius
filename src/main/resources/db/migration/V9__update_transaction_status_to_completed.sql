-- Replace PAID with COMPLETED in transactions status
ALTER TABLE transactions MODIFY status VARCHAR(20) NOT NULL DEFAULT 'PENDING';
UPDATE transactions SET status = 'COMPLETED' WHERE status = 'PAID';
ALTER TABLE transactions MODIFY status ENUM('PENDING', 'COMPLETED') NOT NULL DEFAULT 'PENDING';
