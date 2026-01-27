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
}
