package com.greenie.app.core.sharedpreference

import android.content.SharedPreferences
import javax.inject.Inject

class AppSharedPreferenceManager @Inject constructor(
    private val instance: SharedPreferences
) {
    fun setToken(token: String) {
        instance.edit().putString(FIREBASE_TOKEN, token).apply()
    }

    fun getToken(): String {
        return instance.getString(FIREBASE_TOKEN, "") ?: ""
    }
}

private const val FIREBASE_TOKEN = "firebase_token"

