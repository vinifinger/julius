# Category Endpoints — API Reference

> **Base URL** `{host}/api/v1/categories`
>
> **Authentication:** All endpoints require a valid JWT token sent via the
> `Authorization: Bearer <token>` header.

---

## Table of Contents

1. [Create Category](#1-create-category)
2. [List Categories by User](#2-list-categories-by-user)
3. [Response Schema — CategoryResponse](#response-schema--categoryresponse)

---

## 1. Create Category

Creates a new spending/income category for the authenticated user.

| Detail | Value |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/categories` |
| Auth | Required |
| Status | `201 Created` |

### Request Body — `CreateCategoryRequest`

| Field | Type | Required | Validation | Description |
| --- | --- | --- | --- | --- |
| `name` | `string` | ✅ | Non-blank | Display name of the category |
| `colorHex` | `string` | ❌ | — | Hex color code for UI rendering |

### Request Example

```http
POST /api/v1/categories HTTP/1.1
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

{
  "name": "Alimentação",
  "colorHex": "#E74C3C"
}
```

### Response `201 Created` — `CategoryResponse`

```json
{
  "id": "e5f6a1b2-c3d4-4e5f-bd4e-5f6a1b2c3d4e",
  "name": "Alimentação",
  "colorHex": "#E74C3C",
  "createdAt": "2026-05-02T21:15:42",
  "updatedAt": "2026-05-02T21:15:42"
}
```

---

## 2. List Categories by User

Returns all categories belonging to the authenticated user.

| Detail | Value |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/categories` |
| Auth | Required |
| Status | `200 OK` |

### Request Example

```http
GET /api/v1/categories HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response `200 OK` — `CategoryResponse[]`

```json
[
  {
    "id": "e5f6a1b2-c3d4-4e5f-bd4e-5f6a1b2c3d4e",
    "name": "Alimentação",
    "colorHex": "#E74C3C",
    "createdAt": "2026-05-02T21:15:42",
    "updatedAt": "2026-05-02T21:15:42"
  },
  {
    "id": "f6a1b2c3-d4e5-4f6a-bd4e-5f6a1b2c3d4e",
    "name": "Moradia",
    "colorHex": "#3498DB",
    "createdAt": "2026-05-02T21:15:42",
    "updatedAt": "2026-05-02T21:15:42"
  }
]
```

> **UI Tip:** Use the `colorHex` field to render category color indicators.
> Returns an empty array `[]` when the user has no categories.

---

## Response Schema — `CategoryResponse`

| Field | Type | Nullable | Description |
| --- | --- | --- | --- |
| `id` | `UUID` | No | Unique category identifier |
| `name` | `string` | No | Display name of the category |
| `colorHex` | `string` | Yes | Hex color code (e.g. `"#E74C3C"`) |
| `createdAt` | `datetime` | No | Record creation timestamp |
| `updatedAt` | `datetime` | No | Last update timestamp |
