# Feature Implementation Plan: Add External ID to Transactions

## 1. Overview
As a prerequisite for the Bulk Import feature, we need a way to uniquely identify transactions originating from external sources (like bank statements) to prevent duplicate imports. This plan details the addition of an `externalId` field to the `Transaction` domain entity.

## 2. Business Rules
- **Uniqueness:** The `externalId` must be unique across all transactions.
- **Deduplication:** When creating a transaction, if an `externalId` is provided, the system must check if a transaction with that ID already exists. If it does, the import should ignore/skip it (or the API should return a conflict if it's a single creation).
- **Optional:** The field is nullable, as manually created transactions won't have an `externalId`.

## 3. Impacted Components

### 3.1. Database Migration
- **File:** `src/main/resources/db/migration/V8__add_external_id_to_transactions.sql`
- **Action:** Add `external_id` column to `transactions` table.
```sql
ALTER TABLE transactions ADD COLUMN external_id VARCHAR(255) UNIQUE;
```

### 3.2. Infrastructure Layer
- **`TransactionEntity.java`:** Add `externalId` field.
  ```java
  @Column(name = "external_id", unique = true)
  private String externalId;
  ```
- **`TransactionRepository.java`:** Add method to check existence.
  ```java
  boolean existsByExternalId(String externalId);
  ```
- **`TransactionRepositoryImpl.java`:** Implement the new method.
- **`TransactionMapper.java`:** Update mappings to include `externalId`.

### 3.3. Domain Layer
- **`Transaction.java`:** Add `externalId` field to the domain entity and update `create` factory methods.

### 3.4. Application Layer
- **`TransactionUseCase.java`:** 
  - Update creation logic to check `transactionRepository.existsByExternalId(externalId)`.
  - Throw a new `DuplicateTransactionException` (or similar) if it exists.

### 3.5. Web Layer
- **`TransactionRequest.java`:** Add `externalId` as an optional field.
- **`TransactionResponse.java`:** Include `externalId` in the response.

## 4. Testing
- Update existing tests in `TransactionUseCaseTest` to include `externalId` in mock data.
- Add new test case: `shouldThrowExceptionWhenCreatingTransactionWithExistingExternalId`.
