# Transaction Endpoints — API Reference

> **Base URL** `{host}/api/v1/transactions`
>
> **Authentication:** All endpoints require a valid JWT token via
> `Authorization: Bearer <token>`. The user is resolved from the token.

---

## Table of Contents

1. [Create Transaction](#1-create-transaction)
2. [Get Transaction by ID](#2-get-transaction-by-id)
3. [List Transactions by User](#3-list-transactions-by-user)
4. [Update Transaction Status](#4-update-transaction-status)
5. [Delete Transaction](#5-delete-transaction)
6. [Enums Reference](#6-enums-reference)
7. [Error Response Shapes](#7-error-response-shapes)

---

## 1. Create Transaction

Creates a new financial transaction (revenue or expense) for the user.

| Detail | Value |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/transactions` |
| Auth | Required |
| Status | `201 Created` |

### Request Body — `CreateTransactionRequest`

| Field | Type | Required | Validation | Description |
| --- | --- | --- | --- | --- |
| `accountId` | `UUID` | ✅ | Must exist | Associated account |
| `categoryId` | `UUID` | ✅ | Must exist | Associated category |
| `competenceId` | `UUID` | ✅ | Must exist | Competence period |
| `description` | `string` | ✅ | Non-blank | Human-readable label |
| `amount` | `number` | ✅ | `> 0.00` | Monetary value |
| `dateTime` | `datetime` | ✅ | ISO-8601 | When it occurred |
| `type` | `string` | ✅ | `REVENUE`/`EXPENSE` | Money flow direction |
| `status` | `string` | ✅ | `PENDING`/`PAID` | Payment state |

### Request Example

```http
POST /api/v1/transactions HTTP/1.1
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

{
  "accountId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "categoryId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "competenceId": "c9bf9e57-1685-4c89-bafb-ff5af830be8a",
  "description": "Monthly electricity bill",
  "amount": 189.50,
  "dateTime": "2026-05-02T14:30:00",
  "type": "EXPENSE",
  "status": "PENDING"
}
```

### Response `201 Created` — `TransactionResponse`

```json
{
  "id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
  "accountId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "categoryId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "competenceId": "c9bf9e57-1685-4c89-bafb-ff5af830be8a",
  "userId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "description": "Monthly electricity bill",
  "amount": 189.50,
  "dateTime": "2026-05-02T14:30:00",
  "type": "EXPENSE",
  "status": "PENDING",
  "createdAt": "2026-05-02T21:15:42",
  "updatedAt": "2026-05-02T21:15:42"
}
```

### Business Rules

- `status: "PAID"` immediately updates the account balance.
- `status: "PENDING"` does not affect the balance.
- `amount` is rounded using `HALF_EVEN` rounding.

---

## 2. Get Transaction by ID

Retrieves a single transaction by its unique identifier.

| Detail | Value |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/transactions/{id}` |
| Auth | Required |
| Status | `200 OK` |

### Path Parameters

| Parameter | Type | Description |
| --- | --- | --- |
| `id` | `UUID` | Unique identifier of the transaction |

### Request Example

```http
GET /api/v1/transactions/d290f1ee-6c54-4b01-90e6-d701748f0851 HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response `200 OK` — `TransactionResponse`

```json
{
  "id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
  "description": "Monthly electricity bill",
  "amount": 189.50,
  "type": "EXPENSE",
  "status": "PENDING"
}
```

---

## 3. List Transactions by User

Returns all transactions belonging to the authenticated user.

| Detail | Value |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/transactions` |
| Auth | Required |
| Status | `200 OK` |

### Response `200 OK` — `TransactionResponse[]`

```json
[
  {
    "id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
    "description": "Monthly electricity bill",
    "amount": 189.50,
    "type": "EXPENSE",
    "status": "PENDING"
  }
]
```

---

## 4. Update Transaction Status

Changes **only** the `status` field (`PENDING` ↔ `PAID`).

| Detail | Value |
| --- | --- |
| Method | `PATCH` |
| Path | `/api/v1/transactions/{id}/status` |
| Auth | Required |
| Status | `200 OK` |

### Request Body — `UpdateTransactionStatusRequest`

| Field | Type | Required | Validation | Description |
| --- | --- | --- | --- | --- |
| `status` | `string` | ✅ | `PENDING`/`PAID` | The new status |

### Response `200 OK` — `TransactionResponse`

```json
{
  "id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
  "status": "PAID"
}
```

### Business Rules

| Transition | Balance Effect |
| --- | --- |
| `PENDING` → `PAID` | Updated: **+amount** for `REVENUE`, **−amount** for `EXPENSE` |
| `PAID` → `PENDING` | The previous balance change is **reversed** |

---

## 5. Delete Transaction

Permanently deletes a transaction and reverses balance impact if `PAID`.

| Detail | Value |
| --- | --- |
| Method | `DELETE` |
| Path | `/api/v1/transactions/{id}` |
| Auth | Required |
| Status | `204 No Content` |

---

## 6. Enums Reference

### `TransactionType`

| Value | Description |
| --- | --- |
| `REVENUE` | Incoming money |
| `EXPENSE` | Outgoing money |

### `TransactionStatus`

| Value | Description |
| --- | --- |
| `PENDING` | Planned — does not affect balance |
| `PAID` | Confirmed — balance is impacted |

---

## 7. Error Response Shapes

### Standard Error

```json
{
  "timestamp": "2026-05-02T21:30:00",
  "status": 400,
  "error": "Error message"
}
```

### Validation Error

```json
{
  "timestamp": "2026-05-02T21:30:00",
  "status": 400,
  "error": "Validation failed",
  "fields": {
    "description": "Required"
  }
}
```

---

## Response Schema — `TransactionResponse`

| Field | Type | Nullable | Description |
| --- | --- | --- | --- |
| `id` | `UUID` | No | Unique transaction identifier |
| `accountId` | `UUID` | No | Account reference |
| `categoryId` | `UUID` | No | Category reference |
| `competenceId` | `UUID` | No | Competence reference |
| `userId` | `UUID` | No | Owner of the transaction |
| `parentId` | `UUID` | Yes | Parent ID (installments only) |
| `installmentCount` | `int` | Yes | Total installments |
| `installmentNumber`| `int` | Yes | Current index |
| `description` | `string` | No | Human-readable label |
| `amount` | `number` | No | Monetary value |
| `dateTime` | `datetime` | No | When occurred |
| `type` | `string` | No | `"REVENUE"` or `"EXPENSE"` |
| `status` | `string` | No | `"PENDING"` or `"PAID"` |
| `createdAt` | `datetime` | No | Record creation timestamp |
| `updatedAt` | `datetime` | No | Last update timestamp |

> **Note:** Fields with `null` values are omitted from the JSON response to keep
> the payload clean.
