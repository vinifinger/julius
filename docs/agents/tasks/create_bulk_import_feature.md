# Feature Implementation Plan: Bulk Import Bank Statements

## 1. Overview
Implement a feature to bulk import financial transactions from bank statement files (CSV and OFX formats). This feature will start by supporting Nubank statements. 
The system will parse the files, map the data to the `Transaction` entity, deduplicate based on `externalId` (which must be implemented as a dependency), and automatically assign transactions to the correct `Competence` based on the transaction date.

## 2. Business Rules & Constraints
- **File Support:** Nubank CSV and OFX files.
- **Account Binding:** All imported transactions must be bound to a specific `AccountId` provided in the import request.
- **Categorization:** Since bank statements lack internal system categories, a `defaultCategoryId` must be provided in the import request. The user can manually re-categorize them later.
- **Competence Resolution:** The system must automatically resolve the `CompetenceId` based on the transaction date (`Month`/`Year`). If the competence doesn't exist, it should be created via `CompetenceUseCase`.
- **Status:** All imported transactions are inherently `COMPLETED`.
- **Deduplication:** Transactions with an `externalId` that already exists in the system will be silently skipped/ignored during the import process to prevent double counting.
- **Mapping (Nubank):**
  - Amount > 0: `TransactionType.REVENUE`
  - Amount < 0: `TransactionType.EXPENSE` (save as positive absolute value)
  - `Identificador` (CSV) or `<FITID>` (OFX): Map to `externalId`.

## 3. Impacted Components

### 3.1. Domain Layer
- **`StatementParser.java` (Interface):** Defines the contract for parsing statement files.
  ```java
  public interface StatementParser {
      List<ParsedTransaction> parse(InputStream inputStream);
      boolean supports(String fileName, String contentType);
  }
  ```
- **`ParsedTransaction.java` (Record):** Intermediate representation.
  ```java
  public record ParsedTransaction(String externalId, String description, BigDecimal amount, LocalDateTime dateTime, TransactionType type) {}
  ```

### 3.2. Application Layer
- **`ImportStatementUseCase.java`:**
  - Receives the file `InputStream`, `accountId`, `defaultCategoryId`, and `userId`.
  - Determines the correct `StatementParser` based on file extension/type.
  - Parses the file into `ParsedTransaction` list.
  - Iterates over transactions:
    - Checks `transactionRepository.existsByExternalId`. If yes, increments `ignoredCount`.
    - Resolves `CompetenceId` via `CompetenceUseCase.getOrCreate(...)`.
    - Creates `Transaction` via `TransactionUseCase.create(...)` with `status = COMPLETED`.
  - Returns a summary of the import (e.g., `importedCount`, `ignoredCount`).

### 3.3. Infrastructure Layer
- **`NubankCsvParser.java`:** Implements `StatementParser` for CSV files.
- **`NubankOfxParser.java`:** Implements `StatementParser` for OFX files.

### 3.4. Web Layer
- **`ImportTransactionController.java`:** 
  - `POST /api/v1/accounts/{accountId}/transactions/import`
  - Consumes `multipart/form-data`.
  - Params: `file` (MultipartFile), `defaultCategoryId` (UUID).
- **`ImportStatementResponse.java`:** 
  - Returns `{ "importedCount": 10, "ignoredCount": 2, "message": "..." }`

## 4. Dependencies
- **External ID for Transactions:** The `externalId` field must be added to the `Transaction` entity first (see `add_transaction_external_id.md`).

## 5. Testing
- `NubankCsvParserTest` and `NubankOfxParserTest` using sample files.
- `ImportStatementUseCaseTest` mocking the parser and repositories to verify deduplication and competence resolution.
