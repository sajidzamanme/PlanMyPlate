package com.teamconfused.planmyplate.util

import android.content.Context
import android.content.SharedPreferences

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
}
