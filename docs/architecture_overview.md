# PlanMyPlate - Architecture Overview

Welcome to the project! This document explains how the app starts and how its parts fit together.

## 🚀 App Entry Point: `PlanMyPlateApplication`
Located at: `app/src/main/java/com/teamconfused/planmyplate/PlanMyPlateApplication.kt`

This is the first code that runs when the app is opened. It initializes **Koin**, which is our Dependency Injection (DI) framework. Without this, the app wouldn't know how to create ViewModels or Repositories.

## 🖼️ UI Entry Point: `MainActivity`
Located at: `app/src/main/java/com/teamconfused/planmyplate/MainActivity.kt`

The "Single Activity" of the app. It:
1. Enables edge-to-edge display.
2. Applies the **Compose Theme**.
3. Starts the **NavGraph**, which controls which screen is currently visible.

## 🛠️ Dependency Injection (Koin)
The `di` folder contains modules that tell Koin how to create objects:

| Module | Responsibility |
| :--- | :--- |
| **AppModule** | Network services (Retrofit) and Session Management. |
| **RepositoryModule** | Data fetching logic (API/Database). |
| **UseCaseModule** | Business logic (e.g., `FilterRecipesUseCase`). |
| **ViewModelModule** | UI state management for specific screens. |

### 💡 Koin Cheat Sheet
*   **`single`**: One instance for the entire app (e.g., Network Client).
*   **`factory`**: A new instance every time (e.g., Use Cases).
*   **`viewModel`**: Special lifecycle-aware instance for UI screens.
*   **`get()`**: Automatically finds and injects the required dependency.

## 🗄️ The Repository Pattern
Located at: `app/src/main/java/com/teamconfused/planmyplate/data/repository/`

Repositories are the "Middleman" between our data sources (API/Database) and our Business Logic.
*   **Interface (Domain Layer)**: Defines *what* data we need (e.g., `getRecipes()`).
*   **Implementation (Data Layer)**: Handles *how* to get it (using Retrofit or local storage).
*   **Why?** This keeps our code clean and makes it easy to swap data sources without breaking the UI.

### How to use DI in a Screen?
In Compose, you can simply get a ViewModel like this:
```kotlin
val viewModel: MyViewModel = koinViewModel()
```
Koin will automatically look at the `ViewModelModule`, see what dependencies `MyViewModel` needs (like a Repository), find those in the `RepositoryModule`, and build everything for you!
