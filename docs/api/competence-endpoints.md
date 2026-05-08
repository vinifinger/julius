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
  "createdAt": "2026-05-02T21:15:42",
  "updatedAt": "2026-05-02T21:15:42"
}
```

---

## 2. List All Competences

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
    "createdAt": "2026-04-07T21:21:11",
    "updatedAt": "2026-04-07T21:21:11"
  },
  {
    "id": "f4a5b6c7-...",
    "name": "05/2026",
    "month": 5,
    "year": 2026,
    "createdAt": "2026-04-07T21:21:11",
    "updatedAt": "2026-04-07T21:21:11"
  }
]
```

> **UI Tip:** Use `name` for display in dropdown selectors. Use `id` as the value
> when filtering transactions or querying the dashboard.

---

## 3. Get Current Competence

| Detail | Value |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/competences/current` |
| Auth | Required |
| Status | `200 OK` |

### Response `200 OK`

```json
{
  "id": "f4a5b6c7-d8e9-4f4a-bd4e-5f6a1b2c3d4e",
  "name": "05/2026",
  "month": 5,
  "year": 2026,
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
| `createdAt` | `datetime` | No | Record creation timestamp |
| `updatedAt` | `datetime` | No | Last update timestamp |
