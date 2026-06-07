# Installment Endpoints — API Reference

> **Base URL** `{host}/api/v1/installments`
>
> **Authentication:** All endpoints require a valid JWT token via
> `Authorization: Bearer <token>`.

---

## 1. Create Installment Series

Creates a series of installment transactions. The system generates multiple
individual transactions — one per installment — each linked to the same parent.

| Detail | Value |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/installments` |
| Auth | Required |
| Status | `201 Created` |

### Request Body — `CreateInstallmentRequest`

| Field | Type | Required | Validation | Description |
| --- | --- | --- | --- | --- |
| `accountId` | `UUID` | ✅ | Must exist | Account for all transactions |
| `categoryId` | `UUID` | ✅ | Must exist | Category classification |
| `subcategoryId` | `UUID` | ❌ | Must exist if provided | Associated subcategory |
| `competenceId` | `UUID` | ✅ | Must exist | Starting competence period |
| `description` | `string` | ✅ | Non-blank | Label (e.g. "Laptop purchase") |
| `totalAmount` | `number` | ❌ | — | Total value of the purchase |
| `installmentAmount` | `number` | ❌ | — | Amount per installment |
| `installments` | `integer` | ✅ | `>= 2` | Number of installments |
| `dateTime` | `datetime` | ✅ | ISO-8601 | Date of the first installment |
| `type` | `string` | ✅ | `REVENUE`/`EXPENSE` | Direction of the money flow |
| `status` | `string` | ✅ | `PENDING`/`COMPLETED` | Initial status |

> **Note on amounts:** You must provide **exactly one** of `totalAmount` or
> `installmentAmount`. If `totalAmount` is provided, the system divides it
> equally. If `installmentAmount` is provided, the total is calculated as
> `installmentAmount × installments`.

### Request Example

```http
POST /api/v1/installments HTTP/1.1
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

{
  "accountId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "categoryId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "subcategoryId": null,
  "competenceId": "c9bf9e57-1685-4c89-bafb-ff5af830be8a",
  "description": "Laptop purchase",
  "totalAmount": 4999.00,
  "installments": 12,
  "dateTime": "2026-05-05T10:00:00",
  "type": "EXPENSE",
  "status": "PENDING"
}
```

### Response `201 Created` — `InstallmentSeriesResponse`

```json
{
  "parentId": "aabbccdd-1122-3344-5566-778899001122",
  "description": "Laptop purchase",
  "totalAmount": 4999.00,
  "totalInstallments": 12,
  "completedInstallments": 0,
  "pendingInstallments": 12,
  "completedAmount": 0.00,
  "pendingAmount": 4999.00
}
```

---

## 2. Get Installment Progress

Returns the current progress of an installment series.

| Detail | Value |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/installments/{parentId}` |
| Auth | Required |
| Status | `200 OK` |

### Path Parameters

| Parameter | Type | Description |
| --- | --- | --- |
| `parentId` | `UUID` | The parent transaction ID of the installment series |

### Request Example

```http
GET /api/v1/installments/aabbccdd-1122-3344-5566-778899001122 HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response `200 OK` — `InstallmentSeriesResponse`

```json
{
  "parentId": "aabbccdd-1122-3344-5566-778899001122",
  "description": "Laptop purchase",
  "totalAmount": 4999.00,
  "totalInstallments": 12,
  "completedInstallments": 3,
  "pendingInstallments": 9,
  "completedAmount": 1249.75,
  "pendingAmount": 3749.25
}
```

---

## 3. Update Installment Series Amount

Recalculates the remaining installment amounts based on a new total.

| Detail | Value |
| --- | --- |
| Method | `PUT` |
| Path | `/api/v1/installments/{parentId}` |
| Auth | Required |
| Status | `200 OK` |

### Request Body — `UpdateInstallmentRequest`

| Field | Type | Required | Validation | Description |
| --- | --- | --- | --- | --- |
| `newTotalAmount` | `number` | ✅ | `> 0.00` | The new total for the series |

### Request Example

```http
PUT /api/v1/installments/aabbccdd-1122-3344-5566-778899001122 HTTP/1.1
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

{
  "newTotalAmount": 5499.00
}
```

### Response `200 OK` — `InstallmentSeriesResponse`

```json
{
  "parentId": "aabbccdd-1122-3344-5566-778899001122",
  "description": "Laptop purchase",
  "totalAmount": 5499.00,
  "totalInstallments": 12,
  "completedInstallments": 3,
  "pendingInstallments": 9,
  "completedAmount": 1249.75,
  "pendingAmount": 4249.25
}
```

---

## 4. Change Installment Type

Changes the transaction type (`REVENUE` ↔ `EXPENSE`) for all transactions.

| Detail | Value |
| --- | --- |
| Method | `PATCH` |
| Path | `/api/v1/installments/{parentId}/type` |
| Auth | Required |
| Status | `200 OK` |

### Query Parameters

| Parameter | Type | Required | Values | Description |
| --- | --- | --- | --- | --- |
| `type` | `string` | ✅ | `REVENUE`/`EXPENSE` | The new transaction type |

### Request Example

```http
PATCH /api/v1/installments/aabb-1122/type?type=REVENUE HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response `200 OK` — `InstallmentSeriesResponse`

```json
{
  "parentId": "aabbccdd-1122-3344-5566-778899001122",
  "description": "Laptop purchase",
  "totalAmount": 4999.00,
  "totalInstallments": 12,
  "completedInstallments": 3,
  "pendingInstallments": 9,
  "completedAmount": 1249.75,
  "pendingAmount": 3749.25
}
```

---

## Response Schema — `InstallmentSeriesResponse`

| Field | Type | Nullable | Description |
| --- | --- | --- | --- |
| `parentId` | `UUID` | No | Parent transaction ID linking installments |
| `description` | `string` | No | Label of the installment series |
| `totalAmount` | `number` | No | Total amount across all installments |
| `totalInstallments` | `integer` | No | Number of installments in the series |
| `completedInstallments` | `integer` | No | How many installments have been paid |
| `pendingInstallments`| `integer` | No | How many installments are pending |
| `completedAmount` | `number` | No | Sum of amounts for completed installments |
| `pendingAmount` | `number` | No | Sum of amounts for pending installments |

> **UI Tip:** Use `completedInstallments / totalInstallments` to render a progress bar.
