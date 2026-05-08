# Julius: Project Concept & Business Rules

## 1. Project Overview

Julius is a personal finance management application designed to help users track
their financial health, organize their expenses and revenues, and gain insights
into their spending habits over time. The application is built around the
concept of monthly tracking, categorizing transactions, and managing real-world
account balances.

This document outlines the core business concepts and rules, serving as a
foundational guide for the UI/UX design and frontend implementation.

## 2. Core Concepts & Entities

### 2.1 User

The central entity of the system. Every financial record (accounts, transactions,
categories) is isolated and belongs exclusively to a single User.

**Authentication:** Supports both standard credentials (email/password) and
Social Login.

### 2.2 Account

Represents a user's real-world financial source (e.g., Checking Account,
Savings, Wallet, Credit Card).

- **Attributes:** Name, Currency, and Balance.
- **Behavior:** The balance represents the current real-world amount. It
  dynamically updates based on the user's realized financial activities.

### 2.3 Competence

A time-based grouping mechanism, representing a specific Month and Year
(e.g., October 2026).

- **Purpose:** All transactions are tied to a competence, allowing the user to
  view their financial status, budgets, and summaries on a month-by-month basis.

### 2.4 Category

A classification system for transactions (e.g., "Food", "Salary", "Transport").

- **Attributes:** Name and Color Hexadecimal code.
- **Purpose:** Used heavily in dashboards to visually group and analyze
  spending habits.

### 2.5 Transaction

The core financial entry in the system.

**Types:**
- `REVENUE` (Income/Money in)
- `EXPENSE` (Outcome/Money out)

**Statuses:**
- `PENDING`: A planned transaction that has not yet occurred.
- `PAID`: A realized transaction that has officially occurred.

**Relations:** Every transaction must belong to a User, an Account, a Category,
and a Competence.

### 2.6 Installment Series (Purchases over time)

Represents a single transaction spread across multiple, consecutive months
(competences).

- If a user makes a purchase in 3 installments, the system creates 3 distinct
  transactions linked by a Parent ID.
- Each installment transaction is automatically assigned to consecutive future
  competences.

## 3. Key Business Rules

### 3.1 Account Balance Processing

- **Strict Realization Rule:** Only transactions with a `PAID` status affect the
  Account balance. `PENDING` transactions are considered projections and do not
  alter the current balance.
- **State Transitions:**
  - Changing a transaction from `PENDING` to `PAID` immediately applies the
    value to the account balance.
  - Changing a transaction from `PAID` to `PENDING` (or deleting it)
    automatically reverses the previous impact on the account balance.

### 3.2 Installment Logic & Constraints

- **Minimum Requirement:** An installment series must consist of at least 2
  installments.
- **Residue Handling:** When a total amount cannot be divided equally by the
  number of installments (e.g., 100 divided by 3), the system calculates the
  exact decimal installment amount and applies any remaining minor fraction
  (residue) to the last installment to ensure the total is perfectly accurate.
- **Updating Installments:**
  - Users can update the total amount of an ongoing installment series.
  - **Constraint:** The system only updates the remaining (`PENDING`)
    installments. The new total amount provided must be greater than the sum of
    the installments that have already been `PAID`.
  - **Type Changing:** Changing an installment series type (e.g., from Expense
    to Revenue) will retroactively reverse the account balance for already
    `PAID` installments and re-apply them correctly.

### 3.3 Dashboard & Analytics Calculations

The system generates insights based on the selected Competence (Month/Year).

**Monthly Summary:**
- Calculates Total Revenue and Total Expenses for the month.
- **Monthly Balance:** Total Revenue - Total Expenses.
- **Status Indicator:** Returns `POSITIVE` (Balance > 0), `NEGATIVE` (Balance < 0),
  or `NEUTRAL` (Balance = 0).

**Expenses by Category:**
- Groups all expenses within a month by their Category.
- Calculates the percentage of each category against the total monthly expenses
  to show where the user's money is going.

**Monthly Evolution (Trend):**
- Aggregates the Total Revenue, Total Expenses, and Balance across the last 6
  active competences (months) to generate a historical trend line for the user.