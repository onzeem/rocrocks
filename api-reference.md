# Budgeting App API Reference

Base URL (local dev): `http://localhost:8080`

All request/response bodies are JSON. Dates are `YYYY-MM-DD` (ISO format).

---

## Users

### Create a user
`POST /api/users`

**Request body:**
```json
{
  "name": "Alex Smith",
  "email": "alex@example.com"
}
```

**Response (201):**
```json
{
  "id": 1,
  "name": "Alex Smith",
  "email": "alex@example.com"
}
```

### Get all users
`GET /api/users` → returns an array of user objects (same shape as above).

### Get one user
`GET /api/users/{id}` → returns a single user object, or 404 if not found.

---

## Categories

### Create a category
`POST /api/categories`

**Request body:**
```json
{
  "name": "Groceries",
  "description": "Food and household items"
}
```

**Response (201):**
```json
{
  "id": 1,
  "name": "Groceries",
  "description": "Food and household items"
}
```

### Get all categories
`GET /api/categories` → returns an array of category objects.

---

## Budgets

### Create a budget
`POST /api/budgets`

**Request body:**
```json
{
  "userId": 1,
  "month": 10,
  "year": 2026,
  "totalIncome": 3000.00,
  "categories": [
    { "categoryId": 1, "amount": 1200.00 },
    { "categoryId": 2, "amount": 400.00 }
  ]
}
```

**Response (201) — a budget summary:**
```json
{
  "id": 5,
  "totalIncome": 3000.00,
  "totalAllocated": 1600.00,
  "remainingIncome": 1400.00,
  "overAllocated": false,
  "items": [
    { "categoryName": "Rent", "allocatedAmount": 1200.00 },
    { "categoryName": "Groceries", "allocatedAmount": 400.00 }
  ]
}
```

### Get a budget's summary
`GET /api/budgets/{id}/summary` → same shape as the create response above.

### Get all budgets for a user
`GET /api/budgets/user/{userId}` → returns an array of budget summary objects.

### Delete a budget
`DELETE /api/budgets/{id}` → 204 No Content on success.

---

## Transactions

### Create a transaction (log spending)
`POST /api/transactions`

**Request body:**
```json
{
  "userId": 1,
  "categoryId": 1,
  "amount": 45.30,
  "description": "Trader Joe's",
  "date": "2026-10-05"
}
```

**Response (201):**
```json
{
  "id": 12,
  "categoryName": "Groceries",
  "amount": 45.30,
  "description": "Trader Joe's",
  "date": "2026-10-05"
}
```

### Get all transactions for a user
`GET /api/transactions/user/{userId}` → returns an array of transaction objects (same shape as above).

### Get transactions for a user in a specific month
`GET /api/transactions/user/{userId}/month/{year}/{month}`
Example: `GET /api/transactions/user/1/month/2026/10`

### Delete a transaction
`DELETE /api/transactions/{id}` → 204 No Content on success.

---

## Reports

### Budget vs. actual spending, by category
`GET /api/reports/budget-vs-actual/user/{userId}/{year}/{month}`
Example: `GET /api/reports/budget-vs-actual/user/1/2026/10`

**Response:**
```json
{
  "month": 10,
  "year": 2026,
  "totalBudgeted": 1600.00,
  "totalActual": 890.50,
  "totalDifference": 709.50,
  "categories": [
    {
      "categoryName": "Rent",
      "budgeted": 1200.00,
      "actual": 1200.00,
      "difference": 0.00
    },
    {
      "categoryName": "Groceries",
      "budgeted": 400.00,
      "actual": 189.50,
      "difference": 210.50
    }
  ]
}
```
> `difference` is `budgeted - actual` — positive means under budget, negative means over.
> This endpoint requires a budget to already exist for that user/month/year, or it returns a 404.

---

## Errors

All "not found" situations (missing user, category, budget, or transaction ID) return:

**Status: 404**
```json
{
  "error": "User not found with ID: 7"
}
```

The frontend should check for this shape on non-2xx responses to display error messages.

---

## Setup checklist before integrating

1. **Order matters when creating test data:** create a user → create categories → create a budget (references user + categories) → create transactions (references user + categories).
2. **CORS:** confirm the backend allows requests from the frontend's dev server origin (e.g., `http://localhost:3000`) — ask the backend dev if this isn't working yet.
3. **All money fields are numbers** (not strings) in JSON, formatted as decimals like `1200.00`.
