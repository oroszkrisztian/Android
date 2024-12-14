package com.tasty.recipesapp.auth.manager

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object TokenManager {
    private const val PREF_NAME = "AuthPrefs"
    private const val KEY_TOKEN = "auth_token"
    private const val TAG = "TokenManager"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setToken(token: String) {
        Log.d(TAG, "Saving token to SharedPreferences")
        prefs.edit().putString(KEY_TOKEN, "Bearer $token").apply()
    }

    fun getToken(): String? {
        val token = prefs.getString(KEY_TOKEN, null)
        Log.d(TAG, "Retrieved token: ${token?.take(10)}...")
        return token
    }

    fun clearToken() {
        Log.d(TAG, "Clearing token")
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    fun hasToken(): Boolean {
        return getToken() != null
    }
}