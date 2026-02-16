package com.teamconfused.planmyplate.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.serializer
import kotlinx.serialization.builtins.serializer

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUserId(userId: Int) {
        prefs.edit().putInt("user_id", userId).apply()
    }

    fun getUserId(): Int {
        return prefs.getInt("user_id", -1)
    }

    fun isLoggedIn(): Boolean {
        return getUserId() != -1
    }
    
    fun setHasMealPlans(hasMealPlans: Boolean) {
        prefs.edit().putBoolean("has_meal_plans", hasMealPlans).apply()
    }
    
    fun hasMealPlans(): Boolean {
        return prefs.getBoolean("has_meal_plans", false)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun saveUserPreferences(preferences: com.teamconfused.planmyplate.model.UserPreferences) {
        val json = kotlinx.serialization.json.Json.encodeToString(com.teamconfused.planmyplate.model.UserPreferences.serializer(), preferences)
        prefs.edit().putString("user_preferences", json).apply()
    }

    fun getUserPreferences(): com.teamconfused.planmyplate.model.UserPreferences {
        val json = prefs.getString("user_preferences", null)
        return if (json != null) {
            try {
                kotlinx.serialization.json.Json.decodeFromString(com.teamconfused.planmyplate.model.UserPreferences.serializer(), json)
            } catch (e: Exception) {
                com.teamconfused.planmyplate.model.UserPreferences()
            }
        } else {
            com.teamconfused.planmyplate.model.UserPreferences()
        }
    }

    // --- Local Sandbox Storage ---

    fun saveAdditionalMeals(meals: List<com.teamconfused.planmyplate.model.AdditionalMeal>) {
        val json = kotlinx.serialization.json.Json.encodeToString(kotlinx.serialization.builtins.ListSerializer(com.teamconfused.planmyplate.model.AdditionalMeal.serializer()), meals)
        prefs.edit().putString("additional_meals", json).apply()
    }

    fun getAdditionalMeals(): List<com.teamconfused.planmyplate.model.AdditionalMeal> {
        val json = prefs.getString("additional_meals", null) ?: return emptyList()
        return try {
            kotlinx.serialization.json.Json.decodeFromString(kotlinx.serialization.builtins.ListSerializer(com.teamconfused.planmyplate.model.AdditionalMeal.serializer()), json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveHandledMeals(handled: Map<String, Set<String>>) {
        val json = kotlinx.serialization.json.Json.encodeToString(kotlinx.serialization.builtins.MapSerializer(serializer<String>(), kotlinx.serialization.builtins.SetSerializer(serializer<String>())), handled)
        prefs.edit().putString("handled_meals", json).apply()
    }

    fun getHandledMeals(): Map<String, Set<String>> {
        val json = prefs.getString("handled_meals", null) ?: return emptyMap()
        return try {
            kotlinx.serialization.json.Json.decodeFromString(kotlinx.serialization.builtins.MapSerializer(serializer<String>(), kotlinx.serialization.builtins.SetSerializer(serializer<String>())), json)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun saveConsumedCalories(calories: Map<String, Int>) {
        val json = kotlinx.serialization.json.Json.encodeToString(kotlinx.serialization.builtins.MapSerializer(serializer<String>(), serializer<Int>()), calories)
        prefs.edit().putString("consumed_calories", json).apply()
    }

    fun getConsumedCalories(): Map<String, Int> {
        val json = prefs.getString("consumed_calories", null) ?: return emptyMap()
        return try {
            kotlinx.serialization.json.Json.decodeFromString(kotlinx.serialization.builtins.MapSerializer(serializer<String>(), serializer<Int>()), json)
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
