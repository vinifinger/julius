# Dashboard Endpoints — API Reference

> **Base URL** `{host}/api/v1/dashboard`
>
> **Authentication:** All endpoints require a valid JWT token via
> `Authorization: Bearer <token>`.

---

## 1. Get Summary

Returns a financial summary for a specific competence period (total revenue,
expenses, and balance).

| Detail | Value |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/dashboard/summary` |
| Auth | Required |
| Status | `200 OK` |

### Query Parameters

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `competenceId` | `UUID` | ✅ | The competence period to summarize |

### Request Example

```http
GET /api/v1/dashboard/summary?competenceId=f4a5b6c7-d8e9-4f4a-bd4e-5f6a1b2c3d4e HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response `200 OK` — `DashboardSummaryResponse`

```json
{
  "totalRevenue": 8500.00,
  "totalExpenses": 6230.45,
  "monthlyBalance": 2269.55,
  "status": "POSITIVE"
}
```

### Response Schema

| Field | Type | Nullable | Description |
| --- | --- | --- | --- |
| `totalRevenue` | `number` | No | Sum of all `REVENUE` transactions (PAID) |
| `totalExpenses` | `number` | No | Sum of all `EXPENSE` transactions (PAID) |
| `monthlyBalance` | `number` | No | `totalRevenue - totalExpenses` |
| `status` | `string` | No | `"POSITIVE"` if balance >= 0, else `"NEGATIVE"` |

> **UI Tip:** Use `status` to conditionally style the balance (green for
> positive, red for negative).

---

## 2. Get Expenses by Category

Returns expense totals grouped by category for a specific competence period.

| Detail | Value |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/dashboard/expenses-by-category` |
| Auth | Required |
| Status | `200 OK` |

### Query Parameters

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `competenceId` | `UUID` | ✅ | The competence period to analyze |

### Request Example

```http
GET /api/v1/dashboard/expenses-by-category?competenceId=f4a5b6c7-d8e9-4f4a-bd4e-5f6a1b2c3d4e HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response `200 OK` — `ExpenseByCategoryResponse[]`

```json
[
  {
    "categoryName": "Alimentação",
    "colorHex": "#E74C3C",
    "totalAmount": 1850.00,
    "percentage": 29.69
  },
  {
    "categoryName": "Moradia",
    "colorHex": "#3498DB",
    "totalAmount": 2200.00,
    "percentage": 35.31
  }
]
```

### Response Schema — `ExpenseByCategoryResponse`

| Field | Type | Nullable | Description |
| --- | --- | --- | --- |
| `categoryName` | `string` | No | Name of the category |
| `colorHex` | `string` | Yes | Hex color for the category |
| `totalAmount` | `number` | No | Total expense amount for this category |
| `percentage` | `number` | No | Percentage of total expenses |

> **UI Tip:** Use this data to render pie/donut charts. The `colorHex` maps
> directly to each slice color, and `percentage` provides pre-calculated values.

---

## 3. Get Monthly Evolution

Returns revenue vs. expenses evolution across all competence periods for the
authenticated user.

| Detail | Value |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/dashboard/evolution` |
| Auth | Required |
| Status | `200 OK` |

### Request Example

```http
GET /api/v1/dashboard/evolution HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response `200 OK` — `MonthlyEvolutionResponse[]`

```json
[
  {
    "month": 3,
    "year": 2026,
    "competenceName": "03/2026",
    "totalRevenue": 7800.00,
    "totalExpenses": 5900.00,
    "balance": 1900.00
  },
  {
    "month": 4,
    "year": 2026,
    "competenceName": "04/2026",
    "totalRevenue": 8200.00,
    "totalExpenses": 7100.50,
    "balance": 1099.50
  }
]
```

### Response Schema — `MonthlyEvolutionResponse`

| Field | Type | Nullable | Description |
| --- | --- | --- | --- |
| `month` | `integer` | No | Month number (1–12) |
| `year` | `integer` | No | Four-digit year |
| `competenceName` | `string` | No | Formatted name (e.g. `"05/2026"`) |
| `totalRevenue` | `number` | No | Sum of revenues for this month |
| `totalExpenses` | `number` | No | Sum of expenses for this month |
| `balance` | `number` | No | `totalRevenue - totalExpenses` |

> **UI Tip:** Use this data to render bar or line charts showing financial
> trends over time. Use `competenceName` as the x-axis label.
