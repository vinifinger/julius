# Savings Endpoints

All endpoints require a valid JWT token sent in the `Authorization` header as a Bearer token.
The `X-User-ID` header is required for some requests, but normally extracted from the JWT token in the actual gateway.

---

## 1. Create Savings Vault
Creates a new savings vault.

**POST** `/api/v1/savings`

**Request Body**
```json
{
  "name": "Emergency Fund",
  "initialBalance": 0.00,
  "colorHex": "#E91E63",
  "icon": "safe"
}
```

**Response** (201 Created)
```json
{
  "id": "e0a7df29-2fa2-4c2c-8ab5-dc729c1c4e95",
  "name": "Emergency Fund",
  "balance": 0.00,
  "colorHex": "#E91E63",
  "icon": "safe"
}
```

---

## 2. List User Savings Vaults
Lists all savings vaults belonging to the user.

**GET** `/api/v1/savings`

**Response** (200 OK)
```json
[
  {
    "id": "e0a7df29-2fa2-4c2c-8ab5-dc729c1c4e95",
    "name": "Emergency Fund",
    "balance": 1500.00,
    "colorHex": "#E91E63",
    "icon": "safe"
  }
]
```

---

## 3. Get Savings Vault by ID
Gets a specific savings vault by ID.

**GET** `/api/v1/savings/{id}`

**Response** (200 OK)
```json
{
  "id": "e0a7df29-2fa2-4c2c-8ab5-dc729c1c4e95",
  "name": "Emergency Fund",
  "balance": 1500.00,
  "colorHex": "#E91E63",
  "icon": "safe"
}
```

---

## 4. Update Savings Vault
Updates the details of a savings vault.

**PUT** `/api/v1/savings/{id}`

**Request Body**
```json
{
  "name": "Travel Fund",
  "colorHex": "#2196F3",
  "icon": "airplane"
}
```

**Response** (200 OK)
```json
{
  "id": "e0a7df29-2fa2-4c2c-8ab5-dc729c1c4e95",
  "name": "Travel Fund",
  "balance": 1500.00,
  "colorHex": "#2196F3",
  "icon": "airplane"
}
```

---

## 5. Delete Savings Vault
Deletes a savings vault.

**DELETE** `/api/v1/savings/{id}`

**Response** (204 No Content)

---

## 6. Deposit to Savings Vault
Deposits money from a specified account into the savings vault.

**POST** `/api/v1/savings/{id}/deposit`

**Request Body**
```json
{
  "accountId": "a3b7df29-2fa2-4c2c-8ab5-dc729c1c4e95",
  "amount": 500.00,
  "description": "Monthly savings transfer"
}
```

**Response** (200 OK)
```json
{
  "id": "e0a7df29-2fa2-4c2c-8ab5-dc729c1c4e95",
  "name": "Emergency Fund",
  "balance": 2000.00,
  "colorHex": "#E91E63",
  "icon": "safe"
}
```

---

## 7. Withdraw from Savings Vault
Withdraws money from the savings vault and adds it to the specified account.

**POST** `/api/v1/savings/{id}/withdraw`

**Request Body**
```json
{
  "accountId": "a3b7df29-2fa2-4c2c-8ab5-dc729c1c4e95",
  "amount": 200.00,
  "description": "Emergency expense"
}
```

**Response** (200 OK)
```json
{
  "id": "e0a7df29-2fa2-4c2c-8ab5-dc729c1c4e95",
  "name": "Emergency Fund",
  "balance": 1800.00,
  "colorHex": "#E91E63",
  "icon": "safe"
}
```

---

## 8. Get Savings History
Gets the history of all deposits and withdrawals for a specific savings vault.

**GET** `/api/v1/savings/{id}/history`

**Response** (200 OK)
```json
[
  {
    "id": "h8c7df29-2fa2-4c2c-8ab5-dc729c1c4e95",
    "savingsId": "e0a7df29-2fa2-4c2c-8ab5-dc729c1c4e95",
    "accountId": "a3b7df29-2fa2-4c2c-8ab5-dc729c1c4e95",
    "type": "DEPOSIT",
    "amount": 500.00,
    "description": "Monthly savings transfer",
    "createdAt": "2026-06-08T20:15:00"
  }
]
```
