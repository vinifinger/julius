# Julius API — Endpoint Reference (for UI Team)

> **API Version:** `v1`
> **Base URL:** `{host}/api/v1`
> **Auth:** JWT Bearer token (obtained via `/api/v1/auth/login` or
> `/api/v1/auth/google`)

---

## Endpoint Index

| Controller | Base Path | Doc | Endpoints |
| --- | --- | --- | --- |
| Auth | `/api/v1/auth` | [auth-endpoints.md](./auth-endpoints.md) | 3 |
| Account | `/api/v1/accounts` | [account-endpoints.md](./account-endpoints.md) | 4 |
| Category | `/api/v1/categories` | [category-endpoints.md](./category-endpoints.md) | 2 |
| Competence | `/api/v1/competences` | [competence-endpoints.md](./competence-endpoints.md) | 3 |
| Transaction | `/api/v1/transactions` | [transaction-endpoints.md](./transaction-endpoints.md) | 5 |
| Installment | `/api/v1/installments` | [installment-endpoints.md](./installment-endpoints.md) | 4 |
| Dashboard | `/api/v1/dashboard` | [dashboard-endpoints.md](./dashboard-endpoints.md) | 3 |
| User | `/api/v1/users` | [user-endpoints.md](./user-endpoints.md) | 2 |
| Savings | `/api/v1/savings` | [savings-endpoints.md](./savings-endpoints.md) | 8 |

### Total: 34 endpoints

---

## Quick Reference — All Endpoints

### Auth (public — no token required)

| Method | Path | Description |
| --- | --- | --- |
| POST | `/api/v1/auth/register` | Create new user account |
| POST | `/api/v1/auth/login` | Login with email/password → JWT |
| POST | `/api/v1/auth/google` | Login via Firebase Google → JWT |

### Account

| Method | Path | Description |
| --- | --- | --- |
| POST | `/api/v1/accounts` | Create new account |
| GET | `/api/v1/accounts` | List all user accounts |
| GET | `/api/v1/accounts/{id}/balance` | Get single account with balance |
| GET | `/api/v1/accounts/total-balance` | Get sum of all account balances |

### Category

| Method | Path | Description |
| --- | --- | --- |
| POST | `/api/v1/categories` | Create new category |
| GET | `/api/v1/categories` | List all user categories |

### Competence

| Method | Path | Description |
| --- | --- | --- |
| POST | `/api/v1/competences` | Create new competence period |
| GET | `/api/v1/competences` | List all competence periods |
| GET | `/api/v1/competences/current` | Get current month's competence |

### Transaction

| Method | Path | Description |
| --- | --- | --- |
| POST | `/api/v1/transactions` | Create new transaction |
| GET | `/api/v1/transactions` | List all user transactions |
| GET | `/api/v1/transactions/{id}` | Get single transaction |
| PATCH | `/api/v1/transactions/{id}/status` | Update transaction status |
| DELETE | `/api/v1/transactions/{id}` | Delete transaction |

### Installment

| Method | Path | Description |
| --- | --- | --- |
| POST | `/api/v1/installments` | Create installment series |
| GET | `/api/v1/installments/{parentId}` | Get installment progress |
| PUT | `/api/v1/installments/{parentId}` | Update series total amount |
| PATCH | `/api/v1/installments/{parentId}/type` | Change installment type |

### Dashboard

| Method | Path | Description |
| --- | --- | --- |
| GET | `/api/v1/dashboard/summary` | Financial summary for a competence |
| GET | `/api/v1/dashboard/expenses-by-category` | Expenses grouped by category |
| GET | `/api/v1/dashboard/evolution` | Monthly revenue vs. expense trend |

### User

| Method | Path | Description |
| --- | --- | --- |
| GET | `/api/v1/users/{id}` | Get user by ID |
| GET | `/api/v1/users` | List all users |

### Savings

| Method | Path | Description |
| --- | --- | --- |
| POST | `/api/v1/savings` | Create new savings vault |
| GET | `/api/v1/savings` | List all user savings vaults |
| GET | `/api/v1/savings/{id}` | Get single savings vault |
| PUT | `/api/v1/savings/{id}` | Update savings vault |
| DELETE | `/api/v1/savings/{id}` | Delete savings vault |
| POST | `/api/v1/savings/{id}/deposit` | Deposit money into vault |
| POST | `/api/v1/savings/{id}/withdraw` | Withdraw money from vault |
| GET | `/api/v1/savings/{id}/history` | Get savings vault history |

---

## Error Response Format

All errors follow a consistent JSON shape:

```json
{
  "timestamp": "2026-05-02T21:30:00",
  "status": 400,
  "error": "Human-readable error message"
}
```

Validation errors include a `fields` map:

```json
{
  "timestamp": "2026-05-02T21:30:00",
  "status": 400,
  "error": "Validation failed",
  "fields": {
    "fieldName": "Validation message"
  }
}
```

| HTTP Status | Meaning |
| --- | --- |
| `400` | Bad request / validation / not found entity |
| `401` | Invalid or missing JWT token |
| `404` | Resource not found |
| `409` | Conflict (e.g. pending installments) |
| `500` | Unexpected server error |
