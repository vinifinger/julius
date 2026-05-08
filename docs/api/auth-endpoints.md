# Auth Endpoints — API Reference

> **Base URL** `{host}/api/v1/auth`
>
> **Authentication:** These endpoints are **public** — no JWT token is required.
> They are used to obtain or create user credentials.

---

## Table of Contents

1. [Register](#1-register)
2. [Login](#2-login)
3. [Google Authentication](#3-google-authentication)
4. [Response Schemas](#4-response-schemas)

---

## 1. Register

Creates a new user account with email and password.

| Detail | Value |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/auth/register` |
| Auth | **Not required** |
| Status | `201 Created` |

### Request Body — `RegisterRequest`

| Field | Type | Required | Validation | Description |
| --- | --- | --- | --- | --- |
| `name` | `string` | ✅ | Non-blank | Full name of the user |
| `email` | `string` | ✅ | Valid email format | User's email address (unique) |
| `password` | `string` | ✅ | Minimum 8 characters | User's password |

### Request Example

```http
POST /api/v1/auth/register HTTP/1.1
Content-Type: application/json

{
  "name": "Vinícius Finger",
  "email": "vinicius@email.com",
  "password": "mySecureP@ss123"
}
```

### Response `201 Created` — `UserResponse`

```json
{
  "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "name": "Vinícius Finger",
  "email": "vinicius@email.com",
  "createdAt": "2026-05-02T21:15:42",
  "updatedAt": "2026-05-02T21:15:42"
}
```

### Error `400 Bad Request` — Duplicate email

```json
{
  "timestamp": "2026-05-02T21:20:00",
  "status": 400,
  "error": "Email already registered: vinicius@email.com"
}
```

> **UI Tip:** After successful registration, the user still needs to call the
> `/login` endpoint to obtain a JWT token.

---

## 2. Login

Authenticates a user with email/password and returns a JWT token.

| Detail | Value |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/auth/login` |
| Auth | **Not required** |
| Status | `200 OK` |

### Request Body — `LoginRequest`

| Field | Type | Required | Validation | Description |
| --- | --- | --- | --- | --- |
| `email` | `string` | ✅ | Valid email format | User's email address |
| `password` | `string` | ✅ | Non-blank | User's password |

### Request Example

```http
POST /api/v1/auth/login HTTP/1.1
Content-Type: application/json

{
  "email": "vinicius@email.com",
  "password": "mySecureP@ss123"
}
```

### Response `200 OK` — `AuthResponse`

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2aW5pY2l1c0BlbWFpbC5jb20iLCJleHAiOjE3NDg5MDk3NDJ9.abc123...",
  "expiresAt": "2026-05-03T00:15:42Z",
  "userId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "email": "vinicius@email.com"
}
```

### Error `401 Unauthorized`

```json
{
  "timestamp": "2026-05-02T21:20:00",
  "status": 401,
  "error": "Invalid credentials"
}
```

> **UI Tip:** Store the `token` securely and include it as
> `Authorization: Bearer <token>` in all subsequent API requests. Use `expiresAt`
> to implement token refresh or session expiry logic.

---

## 3. Google Authentication

Authenticates (or registers) a user via Firebase Google sign-in. If the user does
not exist yet, they are automatically created.

| Detail | Value |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/auth/google` |
| Auth | **Not required** |
| Status | `200 OK` |

### Request Body — `GoogleAuthRequest`

| Field | Type | Required | Validation | Description |
| --- | --- | --- | --- | --- |
| `idToken` | `string` | ✅ | Non-blank | Firebase ID token from Google sign-in |

### Request Example

```http
POST /api/v1/auth/google HTTP/1.1
Content-Type: application/json

{
  "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6Ijk3..."
}
```

### Response `200 OK` — `AuthResponse`

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2aW5pY2l1c0BnbWFpbC5jb20iLCJleHAiOjE3NDg5MDk3NDJ9.xyz789...",
  "expiresAt": "2026-05-03T00:15:42Z",
  "userId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "email": "vinicius@gmail.com"
}
```

### Error `401 Unauthorized`

```json
{
  "timestamp": "2026-05-02T21:20:00",
  "status": 401,
  "error": "Invalid Firebase ID token"
}
```

> **UI Tip:** Use the Firebase JS SDK to perform Google sign-in on the client side,
> obtain the `idToken`, and send it to this endpoint. The backend validates the
> token with Firebase and issues its own JWT.

---

## 4. Response Schemas

### `AuthResponse`

| Field | Type | Nullable | Description |
| --- | --- | --- | --- |
| `token` | `string` | No | JWT token to use in the `Authorization` header |
| `expiresAt` | `instant` | No | Token expiration timestamp (ISO-8601) |
| `userId` | `UUID` | No | Unique identifier of the user |
| `email` | `string` | No | Email address of the user |

### `UserResponse`

| Field | Type | Nullable | Description |
| --- | --- | --- | --- |
| `id` | `UUID` | No | Unique user identifier |
| `name` | `string` | No | Full name of the user |
| `email` | `string` | No | Email address |
| `createdAt` | `datetime` | No | Record creation timestamp |
| `updatedAt` | `datetime` | No | Last update timestamp |
