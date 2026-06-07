CREATE TABLE subcategories (
    id          BINARY(16)  NOT NULL PRIMARY KEY,
    category_id BINARY(16)  NOT NULL,
    name        VARCHAR(50) NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
) ENGINE=InnoDB;

ALTER TABLE transactions ADD COLUMN subcategory_id BINARY(16);
ALTER TABLE transactions ADD CONSTRAINT fk_transactions_subcategories FOREIGN KEY (subcategory_id) REFERENCES subcategories(id);
