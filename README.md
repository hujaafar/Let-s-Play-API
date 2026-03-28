# Let's Play API - Postman Testing Guide

## Base URL
```
http://localhost:8081
```

---

## Setup in Postman

1. Open Postman.
2. Create a new collection named `Lets Play`.
3. For any authenticated request, add this header:
   - Key: `Authorization`
   - Value: `Bearer <token>`

---

## 1. Auth Endpoints

### 1.1 Register
- **Method:** `POST`
- **URL:** `http://localhost:8081/auth/register`
- **Headers:**
  - `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "name": "John",
  "email": "john@test.com",
  "password": "123456"
}
```
- **Expected Response:** `201 Created`
```json
{
  "token": "eyJhbGci..."
}
```
Copy the token. You will use it for all authenticated requests.

---

### 1.2 Login
- **Method:** `POST`
- **URL:** `http://localhost:8081/auth/login`
- **Headers:**
  - `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "email": "john@test.com",
  "password": "123456"
}
```
- **Expected Response:** `200 OK`
```json
{
  "token": "eyJhbGci..."
}
```

---

### 1.3 Register with invalid data (validation test)
- **Method:** `POST`
- **URL:** `http://localhost:8081/auth/register`
- **Headers:**
  - `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "name": "",
  "email": "not-an-email",
  "password": ""
}
```
- **Expected Response:** `400 Bad Request`
```json
{
  "error": "name: must not be blank, email: must be a well-formed email address, password: must not be blank"
}
```

---

### 1.4 Register duplicate email (conflict test)
- **Method:** `POST`
- **URL:** `http://localhost:8081/auth/register`
- **Headers:**
  - `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "name": "John",
  "email": "john@test.com",
  "password": "123456"
}
```
- **Expected Response:** `409 Conflict`
```json
{
  "error": "Email already in use"
}
```

---

### 1.5 Login with wrong password
- **Method:** `POST`
- **URL:** `http://localhost:8081/auth/login`
- **Headers:**
  - `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "email": "john@test.com",
  "password": "wrongpassword"
}
```
- **Expected Response:** `400 Bad Request`
```json
{
  "error": "Invalid credentials"
}
```

---

## 2. Product Endpoints

### 2.1 Get all products (public)
- **Method:** `GET`
- **URL:** `http://localhost:8081/products`
- **Headers:** none required
- **Expected Response:** `200 OK`
```json
[]
```

---

### 2.2 Create product
- **Method:** `POST`
- **URL:** `http://localhost:8081/products`
- **Headers:**
  - `Content-Type: application/json`
  - `Authorization: Bearer <your_token>`
- **Body (raw JSON):**
```json
{
  "name": "Laptop",
  "description": "Gaming laptop",
  "price": 999.99
}
```
- **Expected Response:** `201 Created`
```json
{
  "id": "69c4f5d33302ff260756962c",
  "name": "Laptop",
  "description": "Gaming laptop",
  "price": 999.99,
  "userId": "69c4f5683302ff260756962b"
}
```
Copy the product `id` for update/delete requests.

---

### 2.3 Create product without token (auth test)
- **Method:** `POST`
- **URL:** `http://localhost:8081/products`
- **Headers:**
  - `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "name": "Laptop",
  "description": "Gaming laptop",
  "price": 999.99
}
```
- **Expected Response:** `401 Unauthorized`

---

### 2.4 Get product by ID
- **Method:** `GET`
- **URL:** `http://localhost:8081/products/<product_id>`
- **Headers:** none required
- **Expected Response:** `200 OK`
```json
{
  "id": "69c4f5d33302ff260756962c",
  "name": "Laptop",
  "description": "Gaming laptop",
  "price": 999.99,
  "userId": "69c4f5683302ff260756962b"
}
```

---

### 2.5 Get product by invalid ID (not found test)
- **Method:** `GET`
- **URL:** `http://localhost:8081/products/000000000000000000000000`
- **Headers:** none required
- **Expected Response:** `404 Not Found`
```json
{
  "error": "Product not found"
}
```

---

### 2.6 Update product (owner)
- **Method:** `PUT`
- **URL:** `http://localhost:8081/products/<product_id>`
- **Headers:**
  - `Content-Type: application/json`
  - `Authorization: Bearer <your_token>`
- **Body (raw JSON):**
```json
{
  "name": "Laptop Pro",
  "description": "Updated description",
  "price": 1299.99
}
```
- **Expected Response:** `200 OK`
```json
{
  "id": "69c4f5d33302ff260756962c",
  "name": "Laptop Pro",
  "description": "Updated description",
  "price": 1299.99,
  "userId": "69c4f5683302ff260756962b"
}
```

---

### 2.7 Delete product (owner)
- **Method:** `DELETE`
- **URL:** `http://localhost:8081/products/<product_id>`
- **Headers:**
  - `Authorization: Bearer <your_token>`
- **Expected Response:** `204 No Content` (empty body)

---

### 2.8 Forbidden - delete another user's product (step-by-step)
1. Register user A (John).
Method: `POST`
URL: `http://localhost:8081/auth/register`
Headers: `Content-Type: application/json`
Body:
```json
{ "name": "John", "email": "john@test.com", "password": "123456" }
```
Expected: `201 Created` with `token`. Save it as `tokenA`.

2. User A creates a product.
Method: `POST`
URL: `http://localhost:8081/products`
Headers: `Content-Type: application/json` and `Authorization: Bearer tokenA`
Body:
```json
{ "name": "Laptop", "description": "Gaming", "price": 999.99 }
```
Expected: `201 Created`. Save `id` as `productId`.

3. Register user B (Jane).
Method: `POST`
URL: `http://localhost:8081/auth/register`
Headers: `Content-Type: application/json`
Body:
```json
{ "name": "Jane", "email": "jane@test.com", "password": "123456" }
```
Expected: `201 Created` with `token`. Save it as `tokenB`.

4. User B tries to delete John’s product.
Method: `DELETE`
URL: `http://localhost:8081/products/<productId>`
Headers: `Authorization: Bearer tokenB`
Expected: `403 Forbidden`
```json
{ "error": "You do not own this product" }
```

---

## 3. User Endpoints (Admin Only)

### Admin setup (one time)
1. Register an admin user.
Method: `POST`
URL: `http://localhost:8081/auth/register`
Headers: `Content-Type: application/json`
Body:
```json
{ "name": "Admin", "email": "admin@test.com", "password": "admin123" }
```

2. Promote the user to admin in MongoDB Compass.
Open `letsplay` database > `users` collection, edit the admin user and set:
`role: "ROLE_ADMIN"`

3. Login as admin and copy the token.
Method: `POST`
URL: `http://localhost:8081/auth/login`
Headers: `Content-Type: application/json`
Body:
```json
{ "email": "admin@test.com", "password": "admin123" }
```
Expected: `200 OK` with `token`. Save it as `adminToken`.

---

### 3.1 Get all users
Method: `GET`
URL: `http://localhost:8081/users`
Headers: `Authorization: Bearer adminToken`
Expected: `200 OK`

---

### 3.2 Get user by ID
Method: `GET`
URL: `http://localhost:8081/users/<user_id>`
Headers: `Authorization: Bearer adminToken`
Expected: `200 OK`

---

### 3.3 Update user
Method: `PUT`
URL: `http://localhost:8081/users/<user_id>`
Headers: `Content-Type: application/json` and `Authorization: Bearer adminToken`
Body:
```json
{
  "name": "John Updated",
  "email": "john.updated@test.com",
  "password": "newpassword",
  "role": "ROLE_USER"
}
```
Expected: `200 OK`

---

### 3.4 Delete user
Method: `DELETE`
URL: `http://localhost:8081/users/<user_id>`
Headers: `Authorization: Bearer adminToken`
Expected: `204 No Content`

---

### 3.5 Access users as normal user (forbidden)
Method: `GET`
URL: `http://localhost:8081/users`
Headers: `Authorization: Bearer tokenA`
Expected: `403 Forbidden`
```json
{ "error": "Access denied" }
```

---

