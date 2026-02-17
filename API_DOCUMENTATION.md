# PlanMyPlate API Documentation

This document provides a detailed reference for the PlanMyPlate API. It is designed to help developers, including Android developers, understand and integrate with the backend services.

**Base URL:** `/api`

**Postman Collection:** A pre-configured Postman collection is available at [PlanMyPlate_Postman_Collection.json](file:///home/sajidzaman/Study/dbms/PlanMyPlate/PlanMyPlate_Postman_Collection.json). See the [Postman Guide](file:///home/sajidzaman/Study/dbms/PlanMyPlate/POSTMAN_GUIDE.md) for setup and testing workflows.

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
      "imageUrl": "https://example.com/avocado_toast.jpg",
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

### Create Recipe
Create a new custom recipe with ingredients.

- **URL:** `/recipes`
- **Method:** `POST`
- **Request Body:**
  ```json
  {
    "name": "Custom Pasta",
    "description": "My special pasta recipe",
    "calories": 450,
    "prepTime": 30,
    "cookTime": 20,
    "servings": 4,
    "instructions": "1. Boil water\n2. Cook pasta\n3. Add sauce",
    "imageUrl": "https://example.com/custom_pasta.jpg",
    "ingredients": [
      {
        "ingId": 101,
        "quantity": 200,
        "unit": "grams"
      },
      {
        "ingId": 205,
        "quantity": 100,
        "unit": "ml"
      }
    ]
  }
  ```
- **Response Body:** Created Recipe object with assigned `recipeId`.

### Update Recipe
Update an existing recipe.

- **URL:** `/recipes/{id}`
- **Method:** `PUT`
- **Request Body:** Same as Create Recipe
- **Response Body:** Updated Recipe object.

### Delete Recipe
Delete a recipe.

- **URL:** `/recipes/{id}`
- **Method:** `DELETE`
- **Response Body:**
  ```json
  {
    "message": "Recipe deleted successfully"
  }
  ```

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
  ```json
  [
    {
      "listId": 1,
      "dateCreated": "2023-11-01",
      "status": "active",
      "userId": 1,
      "userId": 1,
      "items": [
        {
          "id": 501,
          "ingredient": {
            "ingId": 101,
            "name": "Milk",
            "price": 2.50
          },
          "quantity": 1,
          "unit": "Gallon"
        },
        {
          "id": 502,
          "ingredient": {
            "ingId": 102,
            "name": "Eggs",
            "price": 3.00
          },
          "quantity": 12,
          "unit": "Count"
        }
      ]
    }
  ]
  ```

### Purchase Items
Mark specific grocery list items as purchased.
- **Action**: These items are **removed** from the grocery list and **added** to the user's inventory with their quantities and units preserved.
- **Note**: If the user doesn't have an inventory yet, one will be automatically created.

- **URL:** `/grocery-lists/{id}/purchase`
- **Method:** `POST`
- **Request Body:**
  ```json
  {
    "items": [
      { "itemId": 501, "quantity": 1 },
      { "itemId": 502, "quantity": 12 },
      { "itemId": 503, "quantity": 2 }
    ]
  }
  ```
  > [!IMPORTANT]
  > Use grocery list **item IDs** (from the `id` field in grocery list items) and the **quantity** purchased.
  
  > [!NOTE]
  > If the purchased quantity is less than the amount in the grocery list, the item will remain in the list with the updated quantity. If it's equal or greater, the item is removed.

- **Response Body:** HTTP 200 OK (Empty body).

### Update Grocery List Item
Update quantity or unit of a specific item in the grocery list.

- **URL:** `/grocery-lists/{listId}/items/{itemId}`
- **Method:** `PUT`
- **Request Body:**
  ```json
  {
    "quantity": 3,
    "unit": "Pack"
  }
  ```
- **Response Body:** Updated `GroceryListItem` object.

---

## 7. Inventory (Pantry)

### Get User Inventory
Retrieve the user's inventory (pantry).
- **Note**: If the user doesn't have an inventory yet, this will return a 404. An inventory is automatically created when items are first purchased from a grocery list.

- **URL:** `/inventory/user/{userId}`
- **Method:** `GET`
- **Response Body:**
  ```json
  {
    "invId": 1,
    "lastUpdate": "2026-02-18",
    "user": { "userId": 1 },
    "items": [
      {
        "itemId": 50,
        "ingredient": { "ingId": 101, "name": "Milk", "price": 2.50 },
        "quantity": 2,
        "unit": "Liter",
        "dateAdded": "2026-02-18",
        "expiryDate": "2026-02-25"
      }
    ]
  }
  ```

### Add Item to Inventory
Manually add an item to the inventory.

- **URL:** `/inventory/{inventoryId}/items`
- **Method:** `POST`
- **Request Body:**
  ```json
  {
    "ingredient": { "ingId": 101 },
    "quantity": 2,
    "unit": "Liter",
    "dateAdded": "2026-02-18",
    "expiryDate": "2026-02-25"
  }
  ```
- **Response Body:** The created `InvItem` object.

### Update Inventory Item
Update quantity or expiry date of a specific inventory item.
- **Action**: If quantity is set to 0, the item is removed.

- **URL:** `/inventory/items/{itemId}`
- **Method:** `PUT`
- **Request Body:**
  ```json
  {
    "quantity": 5,
    "expiryDate": "2023-12-31"
  }
  ```
- **Response Body:** Updated `InvItem` object, or empty if deleted.
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

---

## 9. File Uploads

### Upload Image
Upload an image file to the server and get a URL to use in recipes. 

> [!NOTE]
> The `imageUrl` field in Recipe endpoints accepts either an external online URL (e.g., Unsplash) or the internal URL returned by this upload endpoint.

- **URL:** `/api/files/upload`
- **Method:** `POST`
- **Request Parameters:**
    - `file`: The image file (MultipartFile)
- **Response Body:**
  ```json
  {
    "url": "http://localhost:8081/uploads/uuid-filename.jpg",
    "filename": "uuid-filename.jpg"
  }
  ```

---

## 10. AI Recipe Generation

### Generate Recipe with AI
Generate a custom recipe using Google Gemini AI based on user preferences and constraints.

- **URL:** `/api/ai/generate-recipe`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer <token>`
- **Request Body:**
  ```json
  {
    "availableIngredients": ["chicken", "tomatoes", "garlic", "pasta"],
    "maxCalories": 600,
    "cuisineType": "Italian",
    "allergies": ["peanuts"],
    "dietaryPreference": "None",
    "mood": "Comfort Food",
    "servings": 4,
    "maxCookingTime": 45
  }
  ```

**Request Parameters:**
- `availableIngredients` (optional): List of ingredients you have available
- `maxCalories` (optional): Maximum calories per serving (50-5000)
- `cuisineType` (optional): Desired cuisine (e.g., Italian, Indian, Mexican, Chinese, American)
- `allergies` (optional): List of allergens to avoid
- `dietaryPreference` (optional): Dietary restriction (e.g., Vegan, Vegetarian, Keto, Paleo, None)
- `mood` (optional): Occasion or mood (e.g., Comfort Food, Quick & Easy, Fancy Dinner, Healthy)
- `servings` (required): Number of servings (1-20)
- `maxCookingTime` (optional): Maximum total cooking time in minutes (5-300)

- **Response Body (201 Created):**
  ```json
  {
    "recipeId": 123,
    "name": "Creamy Chicken Pasta",
    "description": "A comforting Italian-style pasta with tender chicken and rich tomato sauce",
    "calories": 580,
    "prepTime": 15,
    "cookTime": 25,
    "servings": 4,
    "instructions": "1. Boil pasta according to package directions...\n2. Season and cook chicken...\n3. Prepare sauce...",
    "imageUrl": null,
    "recipeIngredients": [
      {
        "id": 1,
        "ingredient": {
          "ingId": 1,
          "name": "Chicken Breast",
          "price": 8.50
        },
        "quantity": 500,
        "unit": "grams"
      },
      {
        "id": 2,
        "ingredient": {
          "ingId": 2,
          "name": "Pasta",
          "price": 2.00
        },
        "quantity": 400,
        "unit": "grams"
      }
    ]
  }
  ```

> [!NOTE]
> - The AI will create new ingredients in the database if they don't already exist
> - Generated recipes are automatically saved to your database
> - The more specific your requirements, the better the AI-generated recipe will be
> - Set your Gemini API key in `application.properties`: `gemini.api.key=YOUR_KEY`

---

## 11. Error Handling

The API uses standard HTTP status codes and returns a structured JSON error response for all failure cases.

### Error Response Structure
```json
{
  "timestamp": "2026-02-11T18:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Recipe not found with id: 123"
}
```

### Generate Detailed Meal Plan with AI
Generate a weekly meal plan (21 meals) based on user preferences.

- **URL:** `/api/ai/generate-meal-plan`
- **Method:** `POST`
- **Parameters:**
    - `userId`: ID of the user (required)
    - `startDate`: Start date in YYYY-MM-DD format (optional, defaults to today)
- **Response Body:** Created `MealPlan` object with 21 slots.
  ```json
  {
    "mpId": 12,
    "startDate": "2026-02-12",
    "duration": 7,
    "status": "ACTIVE",
    "slots": [
      {
        "id": 101,
        "mealType": "Breakfast",
        "dayNumber": 1,
        "recipe": { ... }
      },
      ...
    ]
  }
  ```

### Common Status Codes
| Status Code | Description | Scenario |
|-------------|-------------|----------|
| `200 OK` | Success | Request completed successfully. |
| `201 Created` | Created | Resource (like a Recipe) was successfully created. |
| `400 Bad Request` | Client Error | Missing required fields, invalid format, or business logic validation. |
| `401 Unauthorized` | Auth Error | Missing or invalid JWT token. |
| `404 Not Found` | Not Found | Requested resource (User, Recipe, Ingredient, etc.) does not exist. |
| `409 Conflict` | Conflict | Resource already exists (e.g., duplicate email during Sign Up). |
| `500 Server Error` | Internal Error| Something went wrong on the server. |

---
