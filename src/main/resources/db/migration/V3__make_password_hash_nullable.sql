-- =============================================
-- Julius — Make password_hash nullable for social login users
-- =============================================

ALTER TABLE users MODIFY password_hash VARCHAR(255) NULL;
