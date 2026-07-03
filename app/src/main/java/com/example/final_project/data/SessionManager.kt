package com.example.final_project.data

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSession(userId: Long, username: String) {
        prefs.edit()
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .apply()
    }

    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, INVALID_USER_ID)

    fun getUsername(): String = prefs.getString(KEY_USERNAME, "").orEmpty()

    fun isLoggedIn(): Boolean = getUserId() != INVALID_USER_ID

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "taskflow_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        const val INVALID_USER_ID = -1L
    }
}
