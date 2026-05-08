# Installment Feature — Technical Deep Dive & Business Rules

The **Installment Feature** in Julius allows users to record purchases or
revenues that are spread across multiple months. Rather than a single entry, the
system generates a series of linked transactions that automatically populate
future financial periods (Competences).

---

## 1. Core Logic & Generation

### 1.1 Creation Rules

When creating an installment series, the following rules apply:

- **Minimum Count:** A series must have at least **2 installments**.
- **Amount Calculation:** Users can provide either the `totalAmount` or the
  `installmentAmount`.
  - If `totalAmount` is provided: `installmentAmount = totalAmount / installments`.
  - If `installmentAmount` is provided: `totalAmount = installmentAmount * installments`.
  - If both are provided: The system validates that `totalAmount` is
    approximately equal to `installmentAmount * installments` (allowing for
    minor rounding differences).

### 1.2 Chronological Distribution

Installments are distributed across consecutive **Competences** (Month/Year).

- The first installment starts at the provided `competenceId` and `dateTime`.
- Subsequent installments are assigned to the same day of the month in following
  months.
- The system automatically handles year rollovers (e.g., December 2026 →
  January 2027).
- If a Competence for a future month does not exist, the system creates it
  automatically.

### 1.3 Precision & Residue Handling

To ensure financial accuracy, Julius uses `BigDecimal` with `HALF_EVEN` rounding.

- **Residue Management:** Since some amounts cannot be perfectly divided (e.g.,
  $100.00 / 3 = 33.333...), the system applies the **residue to the last
  installment**.
- **Calculation Flow:**
  1. Calculate `installmentAmount` rounded to 2 decimal places.
  2. Multiply by `(n - 1)` installments.
  3. Subtract this sum from the `totalAmount`.
  4. The result is the exact value of the `n-th` installment.

---

## 2. Data Architecture

### 2.1 Parent-Child Relationship

There is no dedicated `Installment` table. Instead, Julius uses a
**Self-Referencing Transaction Model**:

- All installments in a series are regular `Transaction` entities.
- They are linked together by a `parentId`.
- The `parentId` is the `UUID` of the **first installment** in the series.
- Each transaction stores its own `installmentNumber` (e.g., 1, 2, 3) and the
  `installmentCount` (e.g., 12).

### 2.2 Domain Entities

- **`Transaction`:** Stores the individual record, amount, status, and
  installment metadata.
- **`InstallmentSeries` (Domain Record):** A virtual projection used to
  represent the series summary (total paid, total pending, progress).

---

## 3. Dynamic Updates & Life Cycle

### 3.1 Updating Total Amount

Users can modify the total value of an ongoing installment series.

- **Rule of Preservation:** Already **PAID** installments are never modified.
- **Rule of Redistribution:** The difference between the new total and the sum
  of paid installments is redistributed across the remaining **PENDING**
  installments.
- **Validation:** The `newTotalAmount` must be greater than the sum of already
  paid installments.

### 3.2 Type Reversal

Changing the type of a series (e.g., from `EXPENSE` to `REVENUE`):

- The system iterates through all transactions in the series.
- For **PAID** transactions, it calls `transactionService.reverseTransaction()`
  to roll back the previous impact on the Account balance, updates the type, and
  then calls `processTransaction()` to apply the new impact.
- **PENDING** transactions simply have their type updated.

### 3.3 Account Balance Interaction

- Only **PAID** installments affect the Account balance.
- If a series is created with status `PAID`, all generated transactions
  immediately affect the account (though typically only the first one is paid
  initially).

---

## 4. API Specification Summary

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/v1/installments` | `POST` | Create a new series of N transactions. |
| `/api/v1/installments/{parentId}` | `GET` | Retrieve a summary (paid/pending counts and sums). |
| `/api/v1/installments/{parentId}` | `PUT` | Update the total amount and redistribute to pending. |
| `/api/v1/installments/{parentId}/type` | `PATCH` | Change the series direction (Revenue/Expense). |

---

## 5. Error Handling & Validations

| Exception | Scenario |
|-----------|----------|
| `InstallmentValidationException` | Installments < 2, or new total < paid sum, or divergence in amounts. |
| `AccountNotFoundException` | The specified account does not exist or belongs to another user. |
| `TransactionNotFoundException` | The `parentId` provided does not match any existing transactions. |
| `CompetenceNotFoundException` | The starting period provided is invalid. |
