-- =========================================================================
-- V12__add_default_categories_for_existing_users.sql
-- Description: Adds default categories and their respective subcategories
-- to all existing users in the system.
-- =========================================================================

-- 1. Food (#FF9800)
INSERT INTO categories (id, user_id, name, color_hex, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), id, 'Food', '#FF9800', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP 
FROM users u 
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.user_id = u.id AND c.name = 'Food');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Delivery', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM categories c WHERE c.name = 'Food' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Delivery');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Groceries', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM categories c WHERE c.name = 'Food' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Groceries');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Restaurants', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM categories c WHERE c.name = 'Food' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Restaurants');

-- 2. Housing (#795548)
INSERT INTO categories (id, user_id, name, color_hex, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), id, 'Housing', '#795548', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP 
FROM users u 
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.user_id = u.id AND c.name = 'Housing');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Electricity', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Housing' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Electricity');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Rent', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Housing' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Rent');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Condo', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Housing' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Condo');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Internet', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Housing' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Internet');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Water', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Housing' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Water');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Gas', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Housing' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Gas');


-- 3. Health (#F44336)
INSERT INTO categories (id, user_id, name, color_hex, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), id, 'Health', '#F44336', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP 
FROM users u 
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.user_id = u.id AND c.name = 'Health');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Pharmacy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Health' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Pharmacy');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Exams', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Health' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Exams');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Appointments', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Health' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Appointments');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Gym', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Health' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Gym');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Supplements', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Health' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Supplements');


-- 4. Services (#2196F3)
INSERT INTO categories (id, user_id, name, color_hex, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), id, 'Services', '#2196F3', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP 
FROM users u 
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.user_id = u.id AND c.name = 'Services');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Subscriptions', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Services' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Subscriptions');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Service Providers', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Services' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Service Providers');


-- 5. Shopping (#9C27B0)
INSERT INTO categories (id, user_id, name, color_hex, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), id, 'Shopping', '#9C27B0', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP 
FROM users u 
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.user_id = u.id AND c.name = 'Shopping');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Clothes', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Shopping' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Clothes');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Office Supplies', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Shopping' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Office Supplies');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Shoes', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Shopping' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Shoes');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Accessories', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Shopping' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Accessories');


-- 6. Pets (#4CAF50)
INSERT INTO categories (id, user_id, name, color_hex, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), id, 'Pets', '#4CAF50', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP 
FROM users u 
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.user_id = u.id AND c.name = 'Pets');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Health Insurance', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Pets' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Health Insurance');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Food', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Pets' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Food');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Litter', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Pets' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Litter');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Toys', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Pets' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Toys');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Accessories', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Pets' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Accessories');


-- 7. Transportation (#607D8B)
INSERT INTO categories (id, user_id, name, color_hex, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), id, 'Transportation', '#607D8B', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP 
FROM users u 
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.user_id = u.id AND c.name = 'Transportation');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Fuel', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Transportation' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Fuel');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Mechanic', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Transportation' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Mechanic');

INSERT INTO subcategories (id, category_id, name, created_at, updated_at)
SELECT UUID_TO_BIN(UUID()), c.id, 'Parts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'Transportation' AND NOT EXISTS (SELECT 1 FROM subcategories sc WHERE sc.category_id = c.id AND sc.name = 'Parts');
