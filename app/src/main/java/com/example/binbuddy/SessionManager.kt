package com.example.binbuddy

import android.content.Context
import com.example.binbuddy.data.UserEntity
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("binbuddy_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun login(user: UserEntity) {
        prefs.edit()
            .putString("current_user", gson.toJson(user))
            .apply()
    }

    fun logout() {
        prefs.edit().remove("current_user").apply()
    }

    fun getLoggedInUser(): UserEntity? {
        val json = prefs.getString("current_user", null) ?: return null
        return gson.fromJson(json, UserEntity::class.java)
    }
}
