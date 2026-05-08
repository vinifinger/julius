# Account Endpoints — API Reference

> **Base URL** `{host}/api/v1/accounts`
>
> **Authentication:** All endpoints require a valid JWT token sent via the
> `Authorization: Bearer <token>` header. The authenticated user is resolved
> automatically from the token.

---

## Table of Contents

1. [Create Account](#1-create-account)
2. [List Accounts by User](#2-list-accounts-by-user)
3. [Get Account Balance](#3-get-account-balance)
4. [Get Total Balance](#4-get-total-balance)
5. [Response Schema — AccountResponse](#response-schema--accountresponse)

---

## 1. Create Account

Creates a new financial account for the authenticated user.

| Detail | Value |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/accounts` |
| Auth | Required |
| Status | `201 Created` |

### Request Body — `CreateAccountRequest`

| Field | Type | Required | Validation | Description |
| --- | --- | --- | --- | --- |
| `name` | `string` | ✅ | Non-blank | Display name of the account (e.g. "Nubank") |
| `balance` | `number` | ✅ | `>= 0.00` | Initial balance of the account |
| `currency` | `string` | ❌ | — | ISO currency code (e.g. `"BRL"`, `"USD"`). |

### Request Example

```http
POST /api/v1/accounts HTTP/1.1
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

{
  "name": "Nubank",
  "balance": 5000.00,
  "currency": "BRL"
}
```

### Response `201 Created` — `AccountResponse`

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Nubank",
  "balance": 5000.00,
  "currency": "BRL",
  "createdAt": "2026-05-02T21:15:42",
  "updatedAt": "2026-05-02T21:15:42"
}
```

---

## 2. List Accounts by User

Returns all accounts belonging to the authenticated user.

| Detail | Value |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/accounts` |
| Auth | Required |
| Status | `200 OK` |

### Request Example

```http
GET /api/v1/accounts HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response `200 OK` — `AccountResponse[]`

```json
[
  {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "name": "Nubank",
    "balance": 4810.50,
    "currency": "BRL",
    "createdAt": "2026-05-02T21:15:42",
    "updatedAt": "2026-05-02T21:30:00"
  },
  {
    "id": "b2c3d4e5-f6a7-8901-bcde-f23456789012",
    "name": "Itaú Corrente",
    "balance": 12340.75,
    "currency": "BRL",
    "createdAt": "2026-04-10T10:00:00",
    "updatedAt": "2026-05-01T08:45:00"
  }
]
```

> **UI Tip:** Returns an empty array `[]` when the user has no accounts.

---

## 3. Get Account Balance

Retrieves a single account (with its current balance) for the authenticated user.

| Detail | Value |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/accounts/{id}/balance` |
| Auth | Required |
| Status | `200 OK` |

### Path Parameters

| Parameter | Type | Description |
| --- | --- | --- |
| `id` | `UUID` | Unique identifier of the account |

### Request Example

```http
GET /api/v1/accounts/a1b2c3d4-e5f6-7890-abcd-ef1234567890/balance HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response `200 OK` — `AccountResponse`

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Nubank",
  "balance": 4810.50,
  "currency": "BRL",
  "createdAt": "2026-05-02T21:15:42",
  "updatedAt": "2026-05-02T21:30:00"
}
```

### Error `400 Bad Request`

```json
{
  "timestamp": "2026-05-02T21:20:00",
  "status": 400,
  "error": "Account not found with id: a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

> **Note:** Returns `400` (not `404`) because the account is validated against the
> authenticated user — if the account does not exist **or** does not belong to
> the user, the same error is returned.

---

## 4. Get Total Balance

Returns the sum of all account balances for the authenticated user.

| Detail | Value |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/accounts/total-balance` |
| Auth | Required |
| Status | `200 OK` |

### Request Example

```http
GET /api/v1/accounts/total-balance HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response `200 OK` — `BalanceResponse`

```json
{
  "totalBalance": 17151.25
}
```

### Response Schema — `BalanceResponse`

| Field | Type | Nullable | Description |
| --- | --- | --- | --- |
| `totalBalance` | `number` | No | Sum of all account balances (2 decimal places) |

---

## Response Schema — `AccountResponse`

| Field | Type | Nullable | Description |
| --- | --- | --- | --- |
| `id` | `UUID` | No | Unique account identifier |
| `name` | `string` | No | Display name of the account |
| `balance` | `number` | No | Current balance (2 decimal places) |
| `currency` | `string` | Yes | ISO currency code (may be `null` if not set) |
| `createdAt` | `datetime` | No | Record creation timestamp (`yyyy-MM-ddTHH:mm:ss`) |
| `updatedAt` | `datetime` | No | Last update timestamp |
