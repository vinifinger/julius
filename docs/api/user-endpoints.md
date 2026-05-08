# User Endpoints — API Reference

> **Base URL** `{host}/api/v1/users`
>
> **Authentication:** All endpoints require a valid JWT token via
> `Authorization: Bearer <token>`.

---

## 1. Get User by ID

Retrieves a single user by their unique identifier.

| Detail | Value |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/users/{id}` |
| Auth | Required |
| Status | `200 OK` |

### Path Parameters

| Parameter | Type | Description |
| --- | --- | --- |
| `id` | `UUID` | Unique identifier of the user |

### Request Example

```http
GET /api/v1/users/7c9e6679-7425-40de-944b-e07fc1f90ae7 HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response `200 OK` — `UserResponse`

```json
{
  "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "name": "Vinícius Finger",
  "email": "vinicius@email.com",
  "createdAt": "2026-05-02T21:15:42",
  "updatedAt": "2026-05-02T21:15:42"
}
```

### Error `404 Not Found`

```json
{
  "timestamp": "2026-05-02T21:20:00",
  "status": 404,
  "error": "User not found with id: 7c9e6679-7425-40de-944b-e07fc1f90ae7"
}
```

---

## 2. List All Users

Returns all registered users.

| Detail | Value |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/users` |
| Auth | Required |
| Status | `200 OK` |

### Response `200 OK` — `UserResponse[]`

```json
[
  {
    "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
    "name": "Vinícius Finger",
    "email": "vinicius@email.com",
    "createdAt": "2026-05-02T21:15:42",
    "updatedAt": "2026-05-02T21:15:42"
  }
]
```

---

## Response Schema — `UserResponse`

| Field | Type | Nullable | Description |
| --- | --- | --- | --- |
| `id` | `UUID` | No | Unique user identifier |
| `name` | `string` | No | Full name of the user |
| `email` | `string` | No | Email address |
| `createdAt` | `datetime` | No | Record creation timestamp |
| `updatedAt` | `datetime` | No | Last update timestamp |
