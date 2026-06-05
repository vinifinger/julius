# Implementation Plan: PUT Endpoints for Category and Account

## Goal Description

Add `PUT /api/v1/categories/{id}` and `PUT /api/v1/accounts/{id}` endpoints to allow users to fully update the mutable fields of an existing Category or Account. These are full-replacement updates (PUT semantics), so the client must send all updatable fields in the request body.

## User Review Required

> [!IMPORTANT]
> **Account Balance via PUT**: The `PUT /api/v1/accounts/{id}` endpoint will allow updating `name` and `currency` only. The `balance` field is **excluded** from direct updates because it is managed exclusively by transaction processing logic. Allowing direct balance manipulation would break the integrity of the financial ledger. If you'd like to allow manual balance adjustments, please let me know and I'll design a separate reconciliation mechanism.

## Proposed Changes

---

### Category — API Layer (DTOs and Controller)

#### [NEW] `src/main/java/com/finance/app/web/dto/request/UpdateCategoryRequest.java`
- Create a new record with the same fields as `CreateCategoryRequest` (all required for PUT semantics):
  - `@NotBlank String name` (max 50 chars)
  - `@Pattern String colorHex` (optional, `#RRGGBB` format)

#### [MODIFY] `src/main/java/com/finance/app/web/controller/CategoryController.java`
- Add a `@PutMapping("/{id}")` method that accepts `@PathVariable UUID id` and `@Valid @RequestBody UpdateCategoryRequest request`.
- Delegates to `categoryUseCase.update(id, request)`.
- Returns `200 OK` with the updated `CategoryResponse`.

---

### Category — Application Layer (Use Case)

#### [MODIFY] `src/main/java/com/finance/app/application/usecase/CategoryUseCase.java`
- Add `public CategoryResponse update(UUID id, UpdateCategoryRequest request)` method.
- Logic:
  1. Find the existing category by ID (throw `CategoryNotFoundException` if not found).
  2. Overwrite `name` and `colorHex` with the request values.
  3. Set `updatedAt` to `LocalDateTime.now()`.
  4. Save and return the updated `CategoryResponse`.

---

### Account — API Layer (DTOs and Controller)

#### [NEW] `src/main/java/com/finance/app/web/dto/request/UpdateAccountRequest.java`
- Create a new record with updatable fields only (excludes `balance`):
  - `@NotBlank String name` (max 50 chars)
  - `@Size String currency` (max 10 chars)

#### [MODIFY] `src/main/java/com/finance/app/web/controller/AccountController.java`
- Add a `@PutMapping("/{id}")` method that accepts `@PathVariable UUID id` and `@Valid @RequestBody UpdateAccountRequest request`.
- Delegates to `accountUseCase.update(id, request, userId)`.
- Returns `200 OK` with the updated `AccountResponse`.

---

### Account — Application Layer (Use Case)

#### [MODIFY] `src/main/java/com/finance/app/application/usecase/AccountUseCase.java`
- Add `public AccountResponse update(UUID id, UpdateAccountRequest request, UUID userId)` method.
- Logic:
  1. Find the existing account by ID and userId (throw `AccountNotFoundException` if not found).
  2. Overwrite `name` and `currency` with the request values.
  3. Set `updatedAt` to `LocalDateTime.now()`.
  4. Save and return the updated `AccountResponse`.

---

### Testing

#### [MODIFY] `src/test/java/com/finance/app/application/usecase/CategoryUseCaseTest.java`
- Add `Update` nested test class with tests:
  - Should update name and colorHex successfully.
  - Should throw `CategoryNotFoundException` when category does not exist.

#### [MODIFY] `src/test/java/com/finance/app/application/usecase/AccountUseCaseTest.java`
- Add `Update` nested test class with tests:
  - Should update name and currency successfully.
  - Should throw `AccountNotFoundException` when account does not exist.

---

### Documentation

#### [MODIFY] `docs/api/category-endpoints.md`
- Add section `5. Update Category` with request body, response, and business rules.
- Renumber `Error Response Shapes` to section 6.

#### [MODIFY] `docs/api/account-endpoints.md`
- Add section `5. Update Account` with request body, response, and business rules.
- Renumber `Error Response Shapes` to section 6.

## Verification Plan

### Automated Tests
- Run `./gradlew build` to compile and execute all unit tests.
- Verify the new `Update` test classes pass for both Category and Account.

### Manual Verification
- Use Postman or cURL to `PUT` an existing category/account and verify the response reflects the updated fields.
