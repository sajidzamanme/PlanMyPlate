# PlanMyPlate API Documentation

## Base URL
```
http://localhost:8081/api
```

---

## 1. Authentication Endpoints

### 1.1 User Signup
- **Method:** POST
- **Endpoint:** `/auth/signup`
- **Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```
- **Response:** 
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "john@example.com",
  "name": "John Doe",
  "userId": 1
}
```

### 1.2 User Signin
- **Method:** POST
- **Endpoint:** `/auth/signin`
- **Request Body:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```
- **Response:** Same as Signup

### 1.3 Forgot Password
- **Method:** POST
- **Endpoint:** `/auth/forgot-password`
- **Request Body:**
```json
{
  "email": "john@example.com"
}
```
- **Response:**
```json
{
  "message": "Password reset token sent to email. Token: 1234"
}
```

### 1.4 Reset Password
- **Method:** POST
- **Endpoint:** `/auth/reset-password`
- **Request Body:**
```json
{
  "resetToken": "1234",
  "newPassword": "newpassword123"
}
```
- **Response:**
```json
{
  "message": "Password reset successfully"
}
```

---

## 2. User Endpoints

### 2.1 Get Current User (Authenticated)
- **Method:** GET
- **Endpoint:** `/users/me`
- **Headers:** `Authorization: Bearer <token>`
- **Response:**
```json
{
  "userId": 1,
  "email": "john@example.com",
  "name": "John Doe",
  "userName": "johndoe"
}
```

### 2.2 Get User by ID
- **Method:** GET
- **Endpoint:** `/users/{userId}`
- **Response:**
```json
{
  "userId": 1,
  "userName": "johndoe",
  "name": "John Doe",
  "email": "john@example.com",
  "password": "encrypted",
  "age": 28,
  "weight": 75.5,
  "budget": 500.00,
  "allergies": [
    {
      "allergyId": 1,
      "allergyName": "peanuts"
    }
  ],
  "dislikes": [
    {
      "ingId": 5,
      "name": "mushrooms",
      "price": 30.00
    }
  ]
}
```

### 2.3 Update User
- **Method:** PUT
- **Endpoint:** `/users/{userId}`
- **Request Body:**
```json
{
  "name": "Jane Doe",
  "userName": "janedoe",
  "age": 25,
  "weight": 65.0,
  "budget": 600.00
}
```

### 2.4 Delete User
- **Method:** DELETE
- **Endpoint:** `/users/{userId}`
- **Response:**
```json
{
  "message": "User deleted successfully"
}
```

---

## 3. Recipe Endpoints

### 3.1 Get All Recipes
- **Method:** GET
- **Endpoint:** `/recipes`
- **Response:** Array of recipes

### 3.2 Get Recipe by ID
- **Method:** GET
- **Endpoint:** `/recipes/{id}`

### 3.3 Create Recipe
- **Method:** POST
- **Endpoint:** `/recipes`
- **Request Body:**
```json
{
  "name": "Pasta Carbonara",
  "description": "Italian pasta dish",
  "calories": 450
}
```

### 3.4 Update Recipe
- **Method:** PUT
- **Endpoint:** `/recipes/{id}`
- **Request Body:** Same format as create

### 3.5 Delete Recipe
- **Method:** DELETE
- **Endpoint:** `/recipes/{id}`

### 3.6 Search Recipes by Name
- **Method:** GET
- **Endpoint:** `/recipes/search?name=pasta`

### 3.7 Filter Recipes by Calories
- **Method:** GET
- **Endpoint:** `/recipes/filter/calories?minCalories=300&maxCalories=500`

---

## 4. Meal Plan Endpoints

### 4.1 Get All Meal Plans for User
- **Method:** GET
- **Endpoint:** `/meal-plans/user/{userId}`

### 4.2 Get Meal Plan by ID
- **Method:** GET
- **Endpoint:** `/meal-plans/{id}`

### 4.3 Create Meal Plan
- **Method:** POST
- **Endpoint:** `/meal-plans/user/{userId}`
- **Request Body:**
```json
{
  "duration": 7,
  "status": "active"
}
```

### 4.3.1 Create Meal Plan with Recipes (Auto-Populates Grocery List)
- **Method:** POST
- **Endpoint:** `/meal-plans/user/{userId}/create`
- **Request Body:**
```json
{
  "recipeIds": [1, 2, 3],
  "duration": 7,
  "startDate": "2026-02-01"
}
```
- **Response:** Created MealPlan object. Ingredients from selected recipes are automatically added to the user's active Grocery List.

### 4.4 Update Meal Plan
- **Method:** PUT
- **Endpoint:** `/meal-plans/{id}`
- **Request Body:**
```json
{
  "status": "completed",
  "duration": 7
}
```

### 4.5 Delete Meal Plan
- **Method:** DELETE
- **Endpoint:** `/meal-plans/{id}`

### 4.6 Get Meal Plans by Status
- **Method:** GET
- **Endpoint:** `/meal-plans/user/{userId}/status/{status}`

### 4.7 Get Weekly Meal Plans
- **Method:** GET
- **Endpoint:** `/meal-plans/user/{userId}/weekly`

---

## 5. Grocery List Endpoints

### 5.1 Get All Grocery Lists for User
- **Method:** GET
- **Endpoint:** `/grocery-lists/user/{userId}`

### 5.2 Get Grocery List by ID
- **Method:** GET
- **Endpoint:** `/grocery-lists/{id}`

### 5.3 Create Grocery List
- **Method:** POST
- **Endpoint:** `/grocery-lists/user/{userId}`
- **Request Body:**
```json
{
  "status": "active"
}
```

### 5.4 Update Grocery List
- **Method:** PUT
- **Endpoint:** `/grocery-lists/{id}`
- **Request Body:**
```json
{
  "status": "completed"
}
```

### 5.4.1 Purchase Items (Move to Inventory)
- **Method:** POST
- **Endpoint:** `/grocery-lists/{id}/purchase`
- **Request Body:**
```json
{
  "ingredientIds": [101, 102, 105]
}
```
- **Response:** 200 OK. Items are moved to Inventory and removed from the Grocery List.

### 5.5 Delete Grocery List
- **Method:** DELETE
- **Endpoint:** `/grocery-lists/{id}`

### 5.6 Get Grocery Lists by Status
- **Method:** GET
- **Endpoint:** `/grocery-lists/user/{userId}/status/{status}`

---

## 6. Inventory Endpoints

### 6.1 Get Inventory for User
- **Method:** GET
- **Endpoint:** `/inventory/user/{userId}`

### 6.2 Get Inventory by ID
- **Method:** GET
- **Endpoint:** `/inventory/{id}`

### 6.3 Create Inventory for User
- **Method:** POST
- **Endpoint:** `/inventory/user/{userId}`

### 6.4 Update Inventory
- **Method:** PUT
- **Endpoint:** `/inventory/{id}`
- **Request Body:**
```json
{}
```

### 6.5 Delete Inventory
- **Method:** DELETE
- **Endpoint:** `/inventory/{id}`

### 6.6 Get Inventory Items
- **Method:** GET
- **Endpoint:** `/inventory/{inventoryId}/items`

### 6.7 Add Item to Inventory
- **Method:** POST
- **Endpoint:** `/inventory/{inventoryId}/items`
- **Request Body:**
```json
{
  "quantity": 5,
  "expiryDate": "2026-02-28",
  "ingredient": {
    "ingId": 1
  }
}
```

### 6.8 Remove Item from Inventory
- **Method:** DELETE
- **Endpoint:** `/inventory/items/{itemId}`

---

## 7. Ingredient Endpoints

### 7.1 Get All Ingredients
- **Method:** GET
- **Endpoint:** `/ingredients`

### 7.2 Get Ingredient by ID
- **Method:** GET
- **Endpoint:** `/ingredients/{id}`

### 7.3 Create Ingredient
- **Method:** POST
- **Endpoint:** `/ingredients`
- **Request Body:**
```json
{
  "name": "Tomato",
  "price": 50.00
}
```

### 7.4 Update Ingredient
- **Method:** PUT
- **Endpoint:** `/ingredients/{id}`
- **Request Body:** Same format as create

### 7.5 Delete Ingredient
- **Method:** DELETE
- **Endpoint:** `/ingredients/{id}`

### 7.6 Search Ingredients by Name
- **Method:** GET
- **Endpoint:** `/ingredients/search?name=tomato`

### 7.7 Filter Ingredients by Price
- **Method:** GET
- **Endpoint:** `/ingredients/price/range?minPrice=10&maxPrice=100`

---

## 9. Reference Data Endpoints

### 9.1 Get All Diets
- **Method:** GET
- **Endpoint:** `/reference-data/diets`
- **Response:** List of all available diets.
```json
[
  {
    "dietId": 1,
    "dietName": "Vegetarian"
  },
  {
    "dietId": 2,
    "dietName": "Vegan"
  }
]
```

### 9.2 Get All Allergies
- **Method:** GET
- **Endpoint:** `/reference-data/allergies`
- **Response:** List of all available allergies.
```json
[
  {
    "allergyId": 1,
    "allergyName": "Peanuts"
  },
  {
    "allergyId": 2,
    "allergyName": "Shellfish"
  }
]
```

### 9.3 Get All Dislikes (Ingredients)
- **Method:** GET
- **Endpoint:** `/reference-data/dislikes`
- **Response:** List of ingredients that can be disliked. Same format as ingredients.


---

## 8. User Preferences Endpoints

### 8.1 Set User Preferences
- **Method:** POST
- **Endpoint:** `/user-preferences/{userId}`
- **Request Body:**
```json
{
  "diet": "Vegetarian",
  "allergies": "peanuts, shellfish",
  "dislikes": "mushrooms, onions",
  "servings": 2,
  "budget": 500.00
}
```
- **Response:**
```json
{
  "prefId": 1,
  "userId": 1,
  "diet": "Vegetarian",
  "allergies": "peanuts, shellfish",
  "dislikes": "mushrooms, onions",
  "servings": 2,
  "budget": 500.00
}
```

### 8.2 Get User Preferences
- **Method:** GET
- **Endpoint:** `/user-preferences/{userId}`
- **Response:**
```json
{
  "prefId": 1,
  "userId": 1,
  "diet": "Vegetarian",
  "allergies": "peanuts, shellfish",
  "dislikes": "mushrooms, onions",
  "servings": 2,
  "budget": 500.00
}
```

---

## Error Response Format

All errors follow this format:
```json
{
  "timestamp": "2026-01-28T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error description here"
}
```

---

## Authentication

Most endpoints (except `/auth/*`, public endpoints) require JWT token in the header:
```
Authorization: Bearer <your_jwt_token>
```

---

## Status Codes

- **200 OK** - Successful GET/PUT
- **201 Created** - Successful POST
- **400 Bad Request** - Invalid request
- **404 Not Found** - Resource not found
- **500 Internal Server Error** - Server error

---

## Rate Limiting

No rate limiting is currently implemented. In production, add rate limiting middleware.

---

## Version
v1.0.0 - Last Updated: January 28, 2026
