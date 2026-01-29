# PlanMyPlate API Documentation

This document provides a detailed reference for the PlanMyPlate API. It is designed to help developers, including Android developers, understand and integrate with the backend services.

**Base URL:** `/api`

---

## 1. Authentication

### Sign Up
Register a new user account.

- **URL:** `/auth/signup`
- **Method:** `POST`
- **Request Body:**
  ```json
  {
    "name": "John Doe",
    "email": "john.doe@example.com",
    "password": "securePassword123"
  }
  ```
- **Response Body:**
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "email": "john.doe@example.com",
    "name": "John Doe",
    "userId": 1
  }
  ```

### Sign In
Authenticate an existing user.

- **URL:** `/auth/signin`
- **Method:** `POST`
- **Request Body:**
  ```json
  {
    "email": "john.doe@example.com",
    "password": "securePassword123"
  }
  ```
- **Response Body:**
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "email": "john.doe@example.com",
    "name": "John Doe",
    "userId": 1
  }
  ```

### Forgot Password
Initiate password reset process.

- **URL:** `/auth/forgot-password`
- **Method:** `POST`
- **Request Body:**
  ```json
  {
    "email": "john.doe@example.com"
  }
  ```
- **Response Body:**
  ```json
  {
    "message": "Password reset email sent."
  }
  ```

### Reset Password
Complete password reset.

- **URL:** `/auth/reset-password`
- **Method:** `POST`
- **Request Body:**
  ```json
  {
    "resetToken": "token-received-in-email",
    "newPassword": "newSecurePassword456"
  }
  ```
- **Response Body:**
  ```json
  {
    "message": "Password successfully reset."
  }
  ```

---

## 2. Users

### Get Current User
Retrieve profile of the currently authenticated user.

- **URL:** `/users/me`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Response Body:**
  ```json
  {
    "userId": 1,
    "name": "John Doe",
    "email": "john.doe@example.com"
  }
  ```

### Get User by ID
- **URL:** `/users/{id}`
- **Method:** `GET`
- **Response Body:** User object (details omitted for security/brevity unless full entity is returned).

### Update User
- **URL:** `/users/{id}`
- **Method:** `PUT`
- **Request Body:** User data fields to update.
- **Response Body:** Updated User object.

### Delete User
- **URL:** `/users/{id}`
- **Method:** `DELETE`
- **Response Body:**
  ```json
  {
    "message": "User deleted successfully"
  }
  ```

---

## 3. User Preferences
Manage dietary preferences, allergies, and dislikes.

### Get Preferences
- **URL:** `/user-preferences/{userId}`
- **Method:** `GET`
- **Response Body:**
  ```json
  {
    "prefId": 10,
    "userId": 1,
    "diet": "Vegan",
    "allergies": ["Peanuts", "Shellfish"],
    "dislikes": ["Mushrooms"],
    "servings": 2,
    "budget": 150.00
  }
  ```

### Set/Update Preferences
- **URL:** `/user-preferences/{userId}`
- **Method:** `POST`
- **Request Body:**
  ```json
  {
    "diet": "Vegan",
    "allergies": ["Peanuts"],
    "dislikes": [],
    "servings": 4,
    "budget": 200.00
  }
  ```
- **Response Body:** Updated preferences object (same structure as GET).

---

## 4. Recipes

### Get All Recipes
- **URL:** `/recipes`
- **Method:** `GET`
- **Response Body:** List of Recipe objects.
  ```json
  [
    {
      "recipeId": 1,
      "name": "Avocado Toast",
      "description": "Toasted bread with avocado spread...",
      "calories": 350,
      "ingredients": [ ... ]
    },
    ...
  ]
  ```

### Get Recipe by ID
- **URL:** `/recipes/{id}`
- **Method:** `GET`
- **Response Body:** Single Recipe object.

### Search Recipes
- **URL:** `/recipes/search?name=pasta`
- **Method:** `GET`
- **Response Body:** List of matching Recipe objects.

### Filter by Calories
- **URL:** `/recipes/filter/calories?minCalories=200&maxCalories=500`
- **Method:** `GET`
- **Response Body:** List of Recipe objects within calorie range.

---

## 5. Meal Plans

### Get Weekly Meal Plans
Retrieve all meal plans for a specific user.

- **URL:** `/meal-plans/user/{userId}/weekly`
- **Method:** `GET`
- **Response Body:** List of MealPlan objects.
  ```json
  [
    {
      "mpId": 5,
      "startDate": "2023-10-23",
      "duration": 7,
      "status": "ACTIVE",
      "slots": [
        {
          "slotId": 101,
          "mealType": "Breakfast",
          "date": "2023-10-23",
          "recipe": { "recipeId": 1, "name": "..." }
        }
      ]
    }
  ]
  ```

### Create Meal Plan (Simple)
Create a new empty meal plan.

- **URL:** `/meal-plans/user/{userId}`
- **Method:** `POST`
- **Request Body:**
  ```json
  {
    "startDate": "2023-11-01",
    "duration": 7
  }
  ```
- **Response Body:** Created MealPlan object.

### Create Meal Plan with Recipes
Generate a meal plan with selected recipes.

- **URL:** `/meal-plans/user/{userId}/create`
- **Method:** `POST`
- **Request Body:**
  ```json
  {
    "recipeIds": [1, 5, 12],
    "duration": 3,
    "startDate": "2023-11-01"
  }
  ```
- **Response Body:** Created MealPlan object with populated slots.

---

## 6. Grocery Lists

### Get Grocery Lists by User
- **URL:** `/grocery-lists/user/{userId}`
- **Method:** `GET`
- **Response Body:** List of GroceryList objects.

### Purchase Items
Mark specific ingredients as purchased (deduct from grocery list, add to inventory).

- **URL:** `/grocery-lists/{id}/purchase`
- **Method:** `POST`
- **Request Body:**
  ```json
  {
    "ingredientIds": [101, 102, 205]
  }
  ```
- **Response Body:** HTTP 200 OK (Empty body).

---

## 7. Inventory (Pantry)

### Get User Inventory
- **URL:** `/inventory/user/{userId}`
- **Method:** `GET`
- **Response Body:**
  ```json
  {
    "inventoryId": 1,
    "items": [
      {
        "itemId": 50,
        "ingredient": { "ingId": 101, "name": "Milk" },
        "quantity": 1,
        "unit": "Liter"
      }
    ]
  }
  ```

### Add Item to Inventory
- **URL:** `/inventory/{inventoryId}/items`
- **Method:** `POST`
- **Request Body:**
  ```json
  {
    "ingredient": { "ingId": 101 },
    "quantity": 2,
    "unit": "Pack"
  }
  ```
- **Response Body:** The created Inventory Item object.

### Remove Item from Inventory
- **URL:** `/inventory/items/{itemId}`
- **Method:** `DELETE`
- **Response Body:** HTTP 200 OK.

---

## 8. Reference Data

### Get All Diets
- **URL:** `/reference-data/diets`
- **Method:** `GET`
- **Response Body:** List of Diet objects (e.g., Vegan, Keto).

### Get All Allergies
- **URL:** `/reference-data/allergies`
- **Method:** `GET`
- **Response Body:** List of Allergy objects.

### Get All Ingredients (Dislikes)
- **URL:** `/reference-data/dislikes`
- **Method:** `GET`
- **Response Body:** List of Ingredient objects.
