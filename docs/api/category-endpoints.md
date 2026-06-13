# Category Endpoints — API Reference

> **Base URL** `{host}/api/v1/categories`
>
> **Authentication:** All endpoints require a valid JWT token sent via the
> `Authorization: Bearer <token>` header.

---

## Table of Contents

1. [Create Category](#1-create-category)
2. [List Categories by User](#2-list-categories-by-user)
3. [Update Category](#3-update-category)
4. [Response Schema — CategoryResponse](#response-schema--categoryresponse)
5. [Response Schema — SubcategoryResponse](#response-schema--subcategoryresponse)

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
| `type` | `string` | ✅ | Enum (`INCOME`, `EXPENSE`) | Transaction type of the category |
| `colorHex` | `string` | ❌ | — | Hex color code for UI rendering |

### Request Example

```http
POST /api/v1/categories HTTP/1.1
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

{
  "name": "Alimentação",
  "type": "EXPENSE",
  "colorHex": "#E74C3C"
}
```

### Response `201 Created` — `CategoryResponse`

```json
{
  "id": "e5f6a1b2-c3d4-4e5f-bd4e-5f6a1b2c3d4e",
  "name": "Alimentação",
  "type": "EXPENSE",
  "colorHex": "#E74C3C",
  "subcategories": [],
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

### Query Parameters

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `type` | `string` | ❌ | Filter categories by `TransactionType` (`INCOME` or `EXPENSE`) |

### Request Example

```http
GET /api/v1/categories?type=EXPENSE HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response `200 OK` — `CategoryResponse[]`

```json
[
  {
    "id": "e5f6a1b2-c3d4-4e5f-bd4e-5f6a1b2c3d4e",
    "name": "Alimentação",
    "type": "EXPENSE",
    "colorHex": "#E74C3C",
    "subcategories": [
      {
        "id": "1111a1b2-c3d4-4e5f-bd4e-5f6a1b2c3d4e",
        "categoryId": "e5f6a1b2-c3d4-4e5f-bd4e-5f6a1b2c3d4e",
        "name": "Supermercado",
        "createdAt": "2026-05-02T21:15:42",
        "updatedAt": "2026-05-02T21:15:42"
      }
    ],
    "createdAt": "2026-05-02T21:15:42",
    "updatedAt": "2026-05-02T21:15:42"
  },
  {
    "id": "f6a1b2c3-d4e5-4f6a-bd4e-5f6a1b2c3d4e",
    "name": "Moradia",
    "type": "EXPENSE",
    "colorHex": "#3498DB",
    "subcategories": [],
    "createdAt": "2026-05-02T21:15:42",
    "updatedAt": "2026-05-02T21:15:42"
  }
]
```

> **UI Tip:** Use the `colorHex` field to render category color indicators.
> Returns an empty array `[]` when the user has no categories.

---

## 3. Update Category

Updates an existing category's properties. Note that this is a full-replacement (PUT) operation, so all updatable fields must be provided.

| Detail | Value |
| --- | --- |
| Method | `PUT` |
| Path | `/api/v1/categories/{id}` |
| Auth | Required |
| Status | `200 OK` |

### Path Parameters

| Parameter | Type | Description |
| --- | --- | --- |
| `id` | `UUID` | Unique identifier of the category to update |

### Request Body — `UpdateCategoryRequest`

| Field | Type | Required | Validation | Description |
| --- | --- | --- | --- | --- |
| `name` | `string` | ✅ | Non-blank | Display name of the category |
| `type` | `string` | ✅ | Enum (`INCOME`, `EXPENSE`) | Transaction type of the category |
| `colorHex` | `string` | ❌ | — | Hex color code for UI rendering |

### Request Example

```http
PUT /api/v1/categories/e5f6a1b2-c3d4-4e5f-bd4e-5f6a1b2c3d4e HTTP/1.1
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

{
  "name": "Alimentação (Editado)",
  "type": "EXPENSE",
  "colorHex": "#FF0000"
}
```

### Response `200 OK` — `CategoryResponse`

```json
{
  "id": "e5f6a1b2-c3d4-4e5f-bd4e-5f6a1b2c3d4e",
  "name": "Alimentação (Editado)",
  "type": "EXPENSE",
  "colorHex": "#FF0000",
  "subcategories": [],
  "createdAt": "2026-05-02T21:15:42",
  "updatedAt": "2026-05-03T10:20:00"
}
```

---

## Response Schema — `CategoryResponse`

| Field | Type | Nullable | Description |
| --- | --- | --- | --- |
| `id` | `UUID` | No | Unique category identifier |
| `name` | `string` | No | Display name of the category |
| `type` | `string` | No | Transaction type (`INCOME`, `EXPENSE`) |
| `colorHex` | `string` | Yes | Hex color code (e.g. `"#E74C3C"`) |
| `subcategories` | `array` | No | List of associated subcategories |
| `createdAt` | `datetime` | No | Record creation timestamp |
| `updatedAt` | `datetime` | No | Last update timestamp |

---

## Response Schema — `SubcategoryResponse`

| Field | Type | Nullable | Description |
| --- | --- | --- | --- |
| `id` | `UUID` | No | Unique subcategory identifier |
| `categoryId` | `UUID` | No | Category reference |
| `updatedAt` | `datetime` | No | Last update timestamp |

---

## 4. Create Subcategory

Creates a new subcategory under an existing category.

| Detail | Value |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/categories/{categoryId}/subcategories` |
| Auth | Required |
| Status | `201 Created` |

### Path Parameters

| Parameter | Type | Description |
| --- | --- | --- |
| `categoryId` | `UUID` | ID of the parent category |

### Request Body

```json
{
  "name": "Supermercado"
}
```

### Response `201 Created` — `SubcategoryResponse`

```json
{
  "id": "1111a1b2-c3d4-4e5f-bd4e-5f6a1b2c3d4e",
  "categoryId": "e5f6a1b2-c3d4-4e5f-bd4e-5f6a1b2c3d4e",
  "name": "Supermercado",
  "createdAt": "2026-06-08T20:00:00",
  "updatedAt": "2026-06-08T20:00:00"
}
```

---

## 5. Update Subcategory

Updates the name of an existing subcategory.

| Detail | Value |
| --- | --- |
| Method | `PUT` |
| Path | `/api/v1/subcategories/{id}` |
| Auth | Required |
| Status | `200 OK` |

### Path Parameters

| Parameter | Type | Description |
| --- | --- | --- |
| `id` | `UUID` | ID of the subcategory to update |

### Request Body

```json
{
  "name": "Mercado e Padaria"
}
```

### Response `200 OK` — `SubcategoryResponse`

```json
{
  "id": "1111a1b2-c3d4-4e5f-bd4e-5f6a1b2c3d4e",
  "categoryId": "e5f6a1b2-c3d4-4e5f-bd4e-5f6a1b2c3d4e",
  "name": "Mercado e Padaria",
  "createdAt": "2026-06-08T20:00:00",
  "updatedAt": "2026-06-08T20:05:00"
}
```

---

## 6. Delete Subcategory

Deletes a subcategory.

| Detail | Value |
| --- | --- |
| Method | `DELETE` |
| Path | `/api/v1/subcategories/{id}` |
| Auth | Required |
| Status | `204 No Content` |
