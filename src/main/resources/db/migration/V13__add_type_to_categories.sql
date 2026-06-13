-- V13__add_type_to_categories.sql

ALTER TABLE categories ADD COLUMN type VARCHAR(20);

-- Existing default categories are mainly expenses. 
-- We'll set them to EXPENSE, and also 'Uncategorized' will become EXPENSE initially.
UPDATE categories SET type = 'EXPENSE' WHERE type IS NULL;

-- Make it NOT NULL after filling existing records
ALTER TABLE categories MODIFY COLUMN type VARCHAR(20) NOT NULL;

-- Now, let's insert default REVENUE categories and subcategories for existing users.

-- 1. Salary (#4CAF50)
INSERT INTO categories (id, user_id, name, color_hex, type, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), id, 'Salary', '#4CAF50', 'REVENUE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP 
FROM users u 
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.user_id = u.id AND c.name = 'Salary' AND c.type = 'REVENUE');

-- Subcategories for Salary are typically not needed, but let's add some basic ones if we want, or just leave it without subcategories.
-- Actually, the user asked for "Salary", "Investments", "Other Income". Let's not add subcategories for Salary.

-- 2. Investments (#2196F3)
INSERT INTO categories (id, user_id, name, color_hex, type, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), id, 'Investments', '#2196F3', 'REVENUE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP 
FROM users u 
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.user_id = u.id AND c.name = 'Investments' AND c.type = 'REVENUE');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Dividends', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Investments' AND c.type = 'REVENUE' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Dividends');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Interest', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Investments' AND c.type = 'REVENUE' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Interest');

-- 3. Other Income (#FF9800)
INSERT INTO categories (id, user_id, name, color_hex, type, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), id, 'Other Income', '#FF9800', 'REVENUE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP 
FROM users u 
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.user_id = u.id AND c.name = 'Other Income' AND c.type = 'REVENUE');

-- Also create the Uncategorized for REVENUE for all users, because earlier 'Uncategorized' became EXPENSE
INSERT INTO categories (id, user_id, name, color_hex, type, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), id, 'Uncategorized', '#808080', 'REVENUE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP 
FROM users u 
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.user_id = u.id AND c.name = 'Uncategorized' AND c.type = 'REVENUE');
