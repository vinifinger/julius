-- =============================================
-- Julius — Seed Mock Data
-- =============================================

-- Seed User (Password is 'password' - BCrypt hash if needed, but project uses nullable/plain for now in some parts)
-- Fixed UUID for user: f47ac10b-58cc-4372-a567-0e02b2c3d479
INSERT INTO users (id, name, email, password_hash, created_at, updated_at)
VALUES (UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Test User', 'julius@example.com', '$2a$12$W1rHOAOmQ3rY3841TciRkOT6a18AfXtyLJW18pYkYwXwP0vFA7zti', NOW(), NOW());

-- Seed Accounts
-- Fixed UUIDs: a1b2c3d4-e5f6-4a1b-bc3d-4e5f6a1b2c3d, b2c3d4e5-f6a1-4b2c-ad4e-5f6a1b2c3d4e
INSERT INTO accounts (id, user_id, name, balance, currency, created_at, updated_at)
VALUES 
(UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Conta Corrente', 5000.00, 'BRL', NOW(), NOW()),
(UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Carteira', 250.00, 'BRL', NOW(), NOW()),
(UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Nubank', 1200.00, 'BRL', NOW(), NOW());

-- Seed Categories
INSERT INTO categories (id, user_id, name, color_hex, created_at, updated_at)
VALUES 
(UNHEX('d4e5f6a1b2c34d4ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Salário', '#2ECC71', NOW(), NOW()),
(UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Alimentação', '#E74C3C', NOW(), NOW()),
(UNHEX('f6a1b2c3d4e54f6abd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Moradia', '#3498DB', NOW(), NOW()),
(UNHEX('1234567890ab4123bd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Lazer', '#F1C40F', NOW(), NOW()),
(UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Transporte', '#95A5A6', NOW(), NOW()),
(UNHEX('0123456789abcdef0123000000000001'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Saúde', '#9B59B6', NOW(), NOW()),
(UNHEX('0123456789abcdef0123000000000002'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Educação', '#34495E', NOW(), NOW()),
(UNHEX('0123456789abcdef0123000000000003'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Assinaturas', '#E67E22', NOW(), NOW()),
(UNHEX('0123456789abcdef0123000000000004'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Presentes', '#FF69B4', NOW(), NOW()),
(UNHEX('0123456789abcdef0123000000000005'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Investimentos', '#7F8C8D', NOW(), NOW()),
(UNHEX('0123456789abcdef0123000000000006'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Manutenção', '#F39C12', NOW(), NOW());

-- Seed Competences (Current: April 2026, Prev: March 2026, Feb 2026)
INSERT INTO competences (id, user_id, month, year, created_at, updated_at)
VALUES 
(UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 4, 2026, NOW(), NOW()),
(UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 3, 2026, NOW(), NOW()),
(UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 2, 2026, NOW(), NOW()),
(UNHEX('f4a5b6c7d8e94f4abd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 5, 2026, NOW(), NOW()),
(UNHEX('05b6c7d8e9f0405abd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 6, 2026, NOW(), NOW());

-- Seed Transactions
-- Salary (Revenue, Paid)
INSERT INTO transactions (id, account_id, category_id, competence_id, user_id, description, amount, date_time, type, status, created_at, updated_at)
VALUES (UNHEX('a1a1a1a1a1a14a1abd4e5f6a1b2c3d4e'), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('d4e5f6a1b2c34d4ebd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Salário Mensal', 8000.00, '2026-04-05 10:00:00', 'REVENUE', 'PAID', NOW(), NOW());

-- Rent (Expense, Paid)
INSERT INTO transactions (id, account_id, category_id, competence_id, user_id, description, amount, date_time, type, status, created_at, updated_at)
VALUES (UNHEX('b1b1b1b1b1b14b1bbd4e5f6a1b2c3d4e'), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('f6a1b2c3d4e54f6abd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Aluguel', 1500.00, '2026-04-01 08:00:00', 'EXPENSE', 'PAID', NOW(), NOW());

-- Supermarket (Expense, Paid)
INSERT INTO transactions (id, account_id, category_id, competence_id, user_id, description, amount, date_time, type, status, created_at, updated_at)
VALUES (UNHEX('c1c1c1c1c1c14c1cbd4e5f6a1b2c3d4e'), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Supermercado Mensal', 650.00, '2026-04-03 18:30:00', 'EXPENSE', 'PAID', NOW(), NOW());

-- Movie (Expense, Pending)
INSERT INTO transactions (id, account_id, category_id, competence_id, user_id, description, amount, date_time, type, status, created_at, updated_at)
VALUES (UNHEX('d1d1d1d1d1d14d1dbd4e5f6a1b2c3d4e'), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('1234567890ab4123bd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Cinema', 45.00, '2026-04-10 20:00:00', 'EXPENSE', 'PENDING', NOW(), NOW());

-- Installment Purchase (MacBook - 3 installments)
-- Parent
INSERT INTO transactions (id, account_id, category_id, competence_id, user_id, description, amount, date_time, type, status, installment_count, installment_number, created_at, updated_at)
VALUES (UNHEX('e1e1e1e1e1e14e1ebd4e5f6a1b2c3d4e'), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('1234567890ab4123bd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'MacBook M3 (1/3)', 4000.00, '2026-04-02 14:00:00', 'EXPENSE', 'PAID', 3, 1, NOW(), NOW());

INSERT INTO transactions (id, account_id, category_id, competence_id, user_id, parent_id, description, amount, date_time, type, status, installment_count, installment_number, created_at, updated_at)
VALUES 
(UNHEX('e1e1e1e1e1e14e1ebd4e5f6a1b2c3d4f'), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('1234567890ab4123bd4e5f6a1b2c3d4e'), UNHEX('f4a5b6c7d8e94f4abd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), UNHEX('e1e1e1e1e1e14e1ebd4e5f6a1b2c3d4e'), 'MacBook M3 (2/3)', 4000.00, '2026-05-02 14:00:00', 'EXPENSE', 'PENDING', 3, 2, NOW(), NOW()),
(UNHEX('e1e1e1e1e1e14e1ebd4e5f6a1b2c3d50'), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('1234567890ab4123bd4e5f6a1b2c3d4e'), UNHEX('05b6c7d8e9f0405abd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), UNHEX('e1e1e1e1e1e14e1ebd4e5f6a1b2c3d4e'), 'MacBook M3 (3/3)', 4000.00, '2026-06-02 14:00:00', 'EXPENSE', 'PENDING', 3, 3, NOW(), NOW());

-- ── Additional Bulk Transactions (50+) ──
INSERT INTO transactions (id, account_id, category_id, competence_id, user_id, description, amount, date_time, type, status, created_at, updated_at)
VALUES
-- February 2026 (Competence e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e)
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('d4e5f6a1b2c34d4ebd4e5f6a1b2c3d4e'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Salário Fev', 8000.00, '2026-02-05 10:00:00', 'REVENUE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('f6a1b2c3d4e54f6abd4e5f6a1b2c3d4e'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Aluguel Fev', 1500.00, '2026-02-01 08:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Pizzaria', 89.90, '2026-02-07 20:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000001'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Farmácia', 120.50, '2026-02-12 11:15:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000003'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Netflix', 55.90, '2026-02-15 09:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Gasolina', 250.00, '2026-02-20 17:45:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('0123456789abcdef0123000000000002'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Curso Udemy', 34.90, '2026-02-25 22:10:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Supermercado Fev', 780.20, '2026-02-03 14:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('1234567890ab4123bd4e5f6a1b2c3d4e'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Barzinho', 120.00, '2026-02-27 23:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('0123456789abcdef0123000000000006'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Reparo Pia', 150.00, '2026-02-18 10:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),

-- March 2026 (Competence d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e)
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('d4e5f6a1b2c34d4ebd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Salário Mar', 8000.00, '2026-03-05 10:00:00', 'REVENUE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('f6a1b2c3d4e54f6abd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Aluguel Mar', 1500.00, '2026-03-01 08:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000003'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Spotify', 21.90, '2026-03-10 09:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000003'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'iCloud', 14.90, '2026-03-15 09:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Restaurante Japonês', 180.00, '2026-03-14 21:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Padaria', 25.40, '2026-03-16 07:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Uber', 35.60, '2026-03-14 19:20:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Uber', 42.10, '2026-03-15 01:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000004'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Presente Aniversário', 250.00, '2026-03-20 15:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('0123456789abcdef0123000000000005'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Aporte CDB', 1000.00, '2026-03-06 10:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000002'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Mensalidade Pós', 850.00, '2026-03-10 10:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('1234567890ab4123bd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Show Local', 60.00, '2026-03-22 21:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Supermercado Mar', 820.50, '2026-03-03 14:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Gasolina', 280.00, '2026-03-18 17:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('0123456789abcdef0123000000000006'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Troca de Óleo', 350.00, '2026-03-25 09:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),

-- April 2026 (Competence c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e)
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Lanche Burger King', 54.90, '2026-04-01 12:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Café Manhã', 15.00, '2026-04-02 08:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Uber Trabalho', 28.50, '2026-04-02 09:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Uber Casa', 32.10, '2026-04-02 18:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Almoço Executivo', 38.00, '2026-04-03 12:45:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000003'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Disney+', 33.90, '2026-04-04 10:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000003'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'HBO Max', 34.90, '2026-04-04 10:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('1234567890ab4123bd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Sorvete', 12.00, '2026-04-04 15:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Compra Amazon', 145.80, '2026-04-05 14:20:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Feira', 85.00, '2026-04-05 09:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000001'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Dentista', 200.00, '2026-04-06 14:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Açougue', 120.45, '2026-04-06 17:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Uber', 18.90, '2026-04-07 10:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Cafezinho', 6.50, '2026-04-07 10:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000002'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Livro Técnico', 120.00, '2026-04-08 14:00:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Restaurante Fim de Semana', 150.00, '2026-04-11 20:00:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Pão de Queijo', 8.00, '2026-04-12 08:30:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000005'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Investimento Ações', 500.00, '2026-04-15 10:00:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('0123456789abcdef0123000000000006'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Limpeza Mensal', 200.00, '2026-04-18 09:00:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000004'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Presente Amigo', 100.00, '2026-04-20 16:00:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Suco Natural', 12.00, '2026-04-22 15:00:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Estacionamento', 25.00, '2026-04-23 18:00:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Jantar delivery', 78.50, '2026-04-25 20:30:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Água Mineral', 4.50, '2026-04-26 11:00:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('d4e5f6a1b2c34d4ebd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Bônus Performance', 2000.00, '2026-04-28 10:00:00', 'REVENUE', 'PENDING', NOW(), NOW()),

-- Batch 1: 25 Additional Transactions
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Jantar Comemorativo', 120.00, '2026-02-10 21:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('1234567890ab4123bd4e5f6a1b2c3d4e'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Revista Mensal', 25.00, '2026-02-15 10:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('0123456789abcdef0123000000000003'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Internet Fibra', 99.90, '2026-02-05 08:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Uber Shopping', 45.00, '2026-02-22 14:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Lanche Rápido', 18.50, '2026-02-23 18:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000001'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Exames Sangue', 85.00, '2026-03-05 09:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('0123456789abcdef0123000000000002'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Material Escolar', 150.00, '2026-03-02 10:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000003'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Youtube Premium', 24.90, '2026-03-25 08:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000004'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Brinquedo Sobrinho', 65.00, '2026-03-12 15:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('0123456789abcdef0123000000000005'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Investimento FII', 300.00, '2026-03-10 10:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Happy Hour Firma', 45.00, '2026-03-27 18:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Uber Reunião', 22.50, '2026-03-18 08:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Supermercado Extra', 340.00, '2026-03-20 19:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Café com Amigos', 28.00, '2026-03-21 16:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('0123456789abcdef0123000000000006'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Manutenção Ar', 280.00, '2026-03-08 09:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Rodízio Pizza', 69.90, '2026-04-03 20:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Lanche Cinema', 45.00, '2026-04-10 19:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Uber Aeroporto', 85.00, '2026-04-15 05:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Uber Volta', 78.00, '2026-04-18 22:00:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Pastel de Feira', 12.00, '2026-04-05 10:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000003'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Amazon Prime', 14.90, '2026-04-15 08:00:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000003'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Google One', 6.99, '2026-04-10 08:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('1234567890ab4123bd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Ingresso Museu', 20.00, '2026-04-04 14:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Churrasco Amigos', 150.00, '2026-04-19 13:00:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Recarga Celular', 50.00, '2026-04-22 10:00:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('0123456789abcdef0123000000000005'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Dividendos', 45.20, '2026-04-15 10:00:00', 'REVENUE', 'PAID', NOW(), NOW()),

-- Batch 2: 25 Additional Transactions (Total 50+)
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Padaria Domingo', 22.40, '2026-02-15 09:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('e3f4a5b6c7d84e3ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Uber Shopping', 31.00, '2026-02-28 15:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('0123456789abcdef0123000000000003'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Conta de Luz Mar', 210.50, '2026-03-10 10:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('0123456789abcdef0123000000000003'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Conta de Água Mar', 85.20, '2026-03-12 10:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Lanche Noite', 42.00, '2026-03-14 22:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Uber Evento', 54.00, '2026-03-20 20:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Méqui', 58.00, '2026-03-25 13:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('1234567890ab4123bd4e5f6a1b2c3d4e'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Banca de Jornal', 15.00, '2026-03-26 10:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000001'), UNHEX('d2e3f4a5b6c74d2ebd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Consulta Oftalmo', 250.00, '2026-03-30 09:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('0123456789abcdef0123000000000002'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Livro Python', 89.90, '2026-04-02 11:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Café da Tarde', 14.50, '2026-04-05 16:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Suco Detox', 18.00, '2026-04-06 09:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Estacionamento Centro', 30.00, '2026-04-08 15:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Quitanda', 55.20, '2026-04-10 17:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000003'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Dropbox', 49.90, '2026-04-12 08:00:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000004'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Presente Casamento', 400.00, '2026-04-15 11:00:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Salgado + Refri', 16.50, '2026-04-18 16:30:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('234567890abc4234bd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Uber Noturno', 62.00, '2026-04-20 23:30:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Frutas', 32.00, '2026-04-21 09:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000006'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Troca Lâmpada', 25.00, '2026-04-22 14:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Ifood Jantar', 85.00, '2026-04-25 21:00:00', 'EXPENSE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('b2c3d4e5f6a14b2cad4e5f6a1b2c3d4e'), UNHEX('e5f6a1b2c3d44e5fbd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Picolé', 8.50, '2026-04-26 15:00:00', 'EXPENSE', 'PAID', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('a1b2c3d4e5f64a1bbc3d4e5f6a1b2c3d'), UNHEX('d4e5f6a1b2c34d4ebd4e5f6a1b2c3d4e'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Freelance Design', 1500.00, '2026-04-29 10:00:00', 'REVENUE', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE(UUID(), '-', '')), UNHEX('c3d4e5f6a1b24c3dbd4e5f6a1b2c3d4e'), UNHEX('0123456789abcdef0123000000000005'), UNHEX('c1d2e3f4a5b64c1dbd4e5f6a1b2c3d4e'), UNHEX('f47ac10b58cc4372a5670e02b2c3d479'), 'Rendimentos NuConta', 12.45, '2026-04-30 08:00:00', 'REVENUE', 'PAID', NOW(), NOW());
