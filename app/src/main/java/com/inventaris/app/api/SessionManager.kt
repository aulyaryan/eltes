package com.inventaris.app.api

import android.content.Context
import android.content.SharedPreferences
import com.inventaris.app.InventarisApp

object SessionManager {
    private val prefs: SharedPreferences by lazy {
        InventarisApp.instance.getSharedPreferences("inv_session", Context.MODE_PRIVATE)
    }

    fun save(username: String, nama: String, level: String, sessionId: String = "") {
        prefs.edit()
            .putString("u", username).putString("n", nama)
            .putString("l", level).putString("sid", sessionId)
            .putBoolean("ok", true).apply()
    }

    fun saveSessionId(sessionId: String) {
        prefs.edit().putString("sid", sessionId).apply()
    }

    fun getSessionId(): String = prefs.getString("sid", "") ?: ""

    fun isLoggedIn() = prefs.getBoolean("ok", false)
    fun username() = prefs.getString("u", "") ?: ""
    fun nama() = prefs.getString("n", "") ?: ""
    fun level() = prefs.getString("l", "") ?: ""
    fun isAdmin() = level() == "admin"
    fun logout() { prefs.edit().clear().apply() }
}
