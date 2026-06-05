# Competence Endpoints — API Reference

> **Base URL** `{host}/api/v1/competences`
>
> **Authentication:** All endpoints require a valid JWT token via
> `Authorization: Bearer <token>`.

---

## 1. Create Competence

| Detail | Value |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/competences` |
| Auth | Required |
| Status | `201 Created` |

### Request Body — `CreateCompetenceRequest`

| Field | Type | Required | Validation | Description |
| --- | --- | --- | --- | --- |
| `month` | `integer` | ✅ | Between `1` and `12` | Month number (1 = January) |
| `year` | `integer` | ✅ | `>= 2000` | Four-digit year |

### Request Example

```http
POST /api/v1/competences HTTP/1.1
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

{ "month": 5, "year": 2026 }
```

### Response `201 Created`

```json
{
  "id": "c9bf9e57-1685-4c89-bafb-ff5af830be8a",
  "name": "05/2026",
  "month": 5,
  "year": 2026,
  "transactionCount": 0,
  "paidAmount": 0.00,
  "pendingAmount": 0.00,
  "totalAmount": 0.00,
  "totalRevenue": 0.00,
  "totalExpense": 0.00,
  "createdAt": "2026-05-02T21:15:42",
  "updatedAt": "2026-05-02T21:15:42"
}
```

---

## 2. Get Competence by ID

| Detail | Value |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/competences/{id}` |
| Auth | Required |
| Status | `200 OK` |

### Path Parameters

| Parameter | Type | Description |
| --- | --- | --- |
| `id` | `UUID` | Unique identifier of the competence |

### Request Example

```http
GET /api/v1/competences/f4a5b6c7-d8e9-4f4a-bd4e-5f6a1b2c3d4e HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response `200 OK` — `CompetenceDetailResponse`

```json
{
  "id": "f4a5b6c7-d8e9-4f4a-bd4e-5f6a1b2c3d4e",
  "name": "05/2026",
  "month": 5,
  "year": 2026,
  "transactionCount": 15,
  "paidAmount": 1500.00,
  "pendingAmount": 500.00,
  "totalAmount": 2000.00,
  "totalRevenue": 4000.00,
  "totalExpense": 2000.00,
  "createdAt": "2026-04-07T21:21:11",
  "updatedAt": "2026-04-07T21:21:11"
}
```

---

## 3. List All Competences

| Detail | Value |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/competences` |
| Auth | Required |
| Status | `200 OK` |

### Response `200 OK` — `CompetenceResponse[]`

```json
[
  {
    "id": "05b6c7d8-...",
    "name": "06/2026",
    "month": 6,
    "year": 2026,
    "transactionCount": 12,
    "totalRevenue": 2000.00,
    "totalExpense": 1500.00,
    "createdAt": "2026-04-07T21:21:11",
    "updatedAt": "2026-04-07T21:21:11"
  },
  {
    "id": "f4a5b6c7-...",
    "name": "05/2026",
    "month": 5,
    "year": 2026,
    "transactionCount": 8,
    "totalRevenue": 1000.00,
    "totalExpense": 800.00,
    "createdAt": "2026-04-07T21:21:11",
    "updatedAt": "2026-04-07T21:21:11"
  }
]
```

> **UI Tip:** Use `name` for display in dropdown selectors. Use `id` as the value
> when filtering transactions or querying the dashboard.

---

## 4. Get Current Competence

| Detail | Value |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/competences/current` |
| Auth | Required |
| Status | `200 OK` |

### Response `200 OK` — `CompetenceDetailResponse`

```json
{
  "id": "f4a5b6c7-d8e9-4f4a-bd4e-5f6a1b2c3d4e",
  "name": "05/2026",
  "month": 5,
  "year": 2026,
  "transactionCount": 15,
  "paidAmount": 1500.00,
  "pendingAmount": 500.00,
  "totalAmount": 2000.00,
  "totalRevenue": 4000.00,
  "totalExpense": 2000.00,
  "createdAt": "2026-04-07T21:21:11",
  "updatedAt": "2026-04-07T21:21:11"
}
```

> **UI Tip:** Call this on app load to pre-select the current competence period.

---

## Response Schema — `CompetenceResponse`

| Field | Type | Nullable | Description |
| --- | --- | --- | --- |
| `id` | `UUID` | No | Unique competence identifier |
| `name` | `string` | No | Auto-generated display name (`"MM/YYYY"`) |
| `month` | `integer` | No | Month number (1–12) |
| `year` | `integer` | No | Four-digit year |
| `transactionCount` | `integer` | No | Number of transactions |
| `totalRevenue` | `number` | No | Sum of all revenue transactions |
| `totalExpense` | `number` | No | Sum of all expense transactions |
| `createdAt` | `datetime` | No | Record creation timestamp |
| `updatedAt` | `datetime` | No | Last update timestamp |

---

## Response Schema — `CompetenceDetailResponse`

| Field | Type | Nullable | Description |
| --- | --- | --- | --- |
| `id` | `UUID` | No | Unique competence identifier |
| `name` | `string` | No | Auto-generated display name (`"MM/YYYY"`) |
| `month` | `integer` | No | Month number (1–12) |
| `year` | `integer` | No | Four-digit year |
| `transactionCount` | `integer` | No | Number of transactions |
| `paidAmount` | `number` | No | Net balance of paid transactions |
| `pendingAmount` | `number` | No | Net balance of pending transactions |
| `totalAmount` | `number` | No | Net balance of all transactions |
| `totalRevenue` | `number` | No | Sum of all revenue transactions |
| `totalExpense` | `number` | No | Sum of all expense transactions |
| `createdAt` | `datetime` | No | Record creation timestamp |
| `updatedAt` | `datetime` | No | Last update timestamp |
