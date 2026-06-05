# Transaction Filters and Competence Endpoint Updates

This plan details the implementation of transaction filters for the `GET /transactions` endpoint and updates to the competence endpoints to return `totalRevenue` and `totalExpense` regardless of transaction status.

## User Review Required

> [!IMPORTANT]
> The previous response fields `paidAmount`, `pendingAmount`, and `totalAmount` will be removed from `CompetenceResponse` as requested, replacing them with `totalRevenue` and `totalExpense`. Since this is a breaking change to the API, please confirm this is strictly intended for all consumers of the competence endpoints.

## Open Questions

None at this moment.

## Proposed Changes

---

### Domain Layer

#### [MODIFY] [CompetenceTransactionAmountSummary.java](file:///c:/Users/Vinicius%20Finger/Documents/projects/julius/src/main/java/com/finance/app/domain/entity/CompetenceTransactionAmountSummary.java)
- Remove the `TransactionStatus status` field since we are no longer grouping or filtering by status.

#### [NEW] [TransactionFilter.java](file:///c:/Users/Vinicius%20Finger/Documents/projects/julius/src/main/java/com/finance/app/domain/entity/TransactionFilter.java)
- Create a new record containing all the filter criteria: `competenceId`, `status`, `type`, `subtype`, `userId` (mandatory), `categoryId`, `accountId`, `description`, `startDate`, `endDate`.

#### [MODIFY] [TransactionRepository.java](file:///c:/Users/Vinicius%20Finger/Documents/projects/julius/src/main/java/com/finance/app/domain/repository/TransactionRepository.java)
- Add a new method: `List<Transaction> findByFilter(TransactionFilter filter);`

---

### Infrastructure Layer

#### [MODIFY] [TransactionJpaRepository.java](file:///c:/Users/Vinicius%20Finger/Documents/projects/julius/src/main/java/com/finance/app/infrastructure/persistence/repository/TransactionJpaRepository.java)
- Extend `JpaSpecificationExecutor<TransactionEntity>`.
- Update `@Query` for `sumAmountsGroupedByCompetence` and `sumAmountsByCompetenceId` to remove `t.status` from both the `GROUP BY` clause and the constructor invocation.

#### [NEW] [TransactionSpecification.java](file:///c:/Users/Vinicius%20Finger/Documents/projects/julius/src/main/java/com/finance/app/infrastructure/persistence/repository/specification/TransactionSpecification.java)
- Create a class with a static method that returns a `Specification<TransactionEntity>` built using JPA Criteria API. It will dynamically add predicates for each non-null field in `TransactionFilter`.
  - For `description`, use a case-insensitive `like %...%` query.
  - For dates, verify `dateTime >= startDate` and `dateTime < endDate.plusDays(1)`.

#### [MODIFY] [TransactionRepositoryImpl.java](file:///c:/Users/Vinicius%20Finger/Documents/projects/julius/src/main/java/com/finance/app/infrastructure/persistence/TransactionRepositoryImpl.java)
- Implement `findByFilter(TransactionFilter filter)` using `transactionJpaRepository.findAll(TransactionSpecification.from(filter))`.

---

### Application Layer

#### [MODIFY] [CompetenceUseCase.java](file:///c:/Users/Vinicius%20Finger/Documents/projects/julius/src/main/java/com/finance/app/application/usecase/CompetenceUseCase.java)
- Update `listAll`, `getCurrent` and related private methods to aggregate `totalRevenue` and `totalExpense` solely by `type`, ignoring `status`.
- Add `getById(UUID id, UUID userId)` to retrieve a competence by its ID, ensuring it belongs to the user, and map it with the new aggregated amounts.

#### [MODIFY] [TransactionUseCase.java](file:///c:/Users/Vinicius%20Finger/Documents/projects/julius/src/main/java/com/finance/app/application/usecase/TransactionUseCase.java)
- Add a new method `listTransactions(TransactionFilter filter)` that calls the new repository method and maps the results.

---

### Web/Presentation Layer

#### [MODIFY] [CompetenceResponse.java](file:///c:/Users/Vinicius%20Finger/Documents/projects/julius/src/main/java/com/finance/app/web/dto/response/CompetenceResponse.java)
- Replace `paidAmount`, `pendingAmount`, and `totalAmount` with `totalRevenue` and `totalExpense`. Update the `fromDomain` builder method.

#### [MODIFY] [CompetenceController.java](file:///c:/Users/Vinicius%20Finger/Documents/projects/julius/src/main/java/com/finance/app/web/controller/CompetenceController.java)
- Add `@GetMapping("/{id}")` endpoint returning `ResponseEntity<CompetenceResponse>`. Call `competenceUseCase.getById`.

#### [NEW] [TransactionFilterRequest.java](file:///c:/Users/Vinicius%20Finger/Documents/projects/julius/src/main/java/com/finance/app/web/dto/request/TransactionFilterRequest.java)
- Create a record to capture query parameters (`@RequestParam` equivalences) for transaction filtering.

#### [MODIFY] [TransactionController.java](file:///c:/Users/Vinicius%20Finger/Documents/projects/julius/src/main/java/com/finance/app/web/controller/TransactionController.java)
- Update `GET /transactions` to accept `@ModelAttribute TransactionFilterRequest request`. Map this to the `TransactionFilter` and call `transactionUseCase.listTransactions(filter)`.

---

## Verification Plan

### Automated Tests
- Run Gradle test suite for the updated UseCases and Controllers to verify logic.
  `./gradlew clean test --console=plain`
- Specifically test the new JPA Specifications by running `TransactionEntityRepositoryTest` or adding a new slice test if necessary.
- Ensure all tests use BDD-style and verify that assertions conform to the new structure of `CompetenceResponse`.

### Manual Verification
- Manually review the output format from a mock local run if necessary to assure endpoints behave as intended.
