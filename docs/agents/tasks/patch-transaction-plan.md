# Implementation Plan: PATCH Endpoint for Transactions

This document outlines the approach to implement a new `PATCH` endpoint for updating fields of an existing transaction.

## Goal Description

Provide a flexible `PATCH /api/v1/transactions/{id}` endpoint that allows clients to update any modifiable field of a transaction without requiring the entire object. The system must accurately adjust account balances when critical fields (`accountId`, `amount`, `type`, or `status`) are modified on a completed transaction.

## User Review Required

> [!WARNING]  
> If a user updates the `amount`, `type`, or `accountId` of a transaction that belongs to an **Installment Series** (`parentId != null`), should we allow it? Doing so might desynchronize the transaction from its siblings in the series. I propose we **allow it**, as this provides maximum flexibility to the user to fix one-off mistakes, but please confirm if we should restrict edits on installment transactions.

## Open Questions

1. Should the existing `PATCH /api/v1/transactions/{id}/status` endpoint be deprecated in favor of this new generalized `PATCH` endpoint, or should they coexist? (I recommend letting them coexist for backward compatibility).

## Proposed Changes

---

### API Layer (DTOs and Controllers)

#### [NEW] `src/main/java/com/finance/app/web/dto/request/UpdateTransactionRequest.java`
- Create a new record where all fields are optional (Nullable).
- Fields: `UUID accountId`, `UUID categoryId`, `UUID competenceId`, `String description`, `BigDecimal amount`, `LocalDateTime dateTime`, `TransactionType type`, `TransactionStatus status`.
- Add `@Positive` constraints on `amount` if present.

#### [MODIFY] `src/main/java/com/finance/app/web/controller/TransactionController.java`
- Add a new `@PatchMapping("/{id}")` mapped to an `update` method.
- Will take `@PathVariable UUID id` and `@Valid @RequestBody UpdateTransactionRequest request`.
- Call `transactionUseCase.update(...)` and return the `TransactionResponse`.

---

### Application Layer (Use Cases)

#### [MODIFY] `src/main/java/com/finance/app/application/usecase/TransactionUseCase.java`
- Add `public TransactionResponse update(UUID id, UpdateTransactionRequest request)` method.
- **Balance Update Strategy:**
  1. Retrieve the existing transaction.
  2. Check if the update affects the balance (changes in `accountId`, `amount`, `type`, or `status`).
  3. If the transaction was `COMPLETED` before the update and the balance is affected, **reverse** the old transaction's effect on the old account.
  4. Apply the field updates to the transaction entity.
  5. If the new status is `COMPLETED` and the balance is affected, **process** the new transaction's effect on the new (or same) account.
  6. Save the modified transaction and account(s).

---

### Documentation

#### [MODIFY] `docs/api/transaction-endpoints.md`
- Add a new section `Update Transaction (Partial)` detailing the new `PATCH` endpoint, payload, and balance adjustment business rules.

## Verification Plan

### Automated Tests
- Unit tests in `TransactionUseCaseTest.java` verifying:
  - Updating a simple text field (no balance change).
  - Updating the `amount` of a `COMPLETED` transaction (old balance reversed, new balance applied).
  - Updating the `type` (`EXPENSE` -> `REVENUE`) and checking balance adjustments.
  - Updating the `accountId` of a `COMPLETED` transaction (moving a transaction between accounts).
  - Validation failures if passing invalid negative amounts.

### Manual Verification
- Start the application and use Postman or cURL to `PATCH` an existing transaction, checking if the database reflects the new properties and the `Account` table reflects the correct new balance.
