package com.example.binbuddy

import android.content.Context
import com.example.binbuddy.data.AppDatabase
import com.example.binbuddy.data.UserEntity
import androidx.core.content.edit

class SessionManager(private val ctx: Context) {
    private val prefs = ctx.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID  = "logged_in_user_id"
        private const val KEY_IS_GUEST = "is_guest"
    }

    fun login(user: UserEntity) {
        prefs.edit {
            putLong(KEY_USER_ID, user.id)
            putBoolean(KEY_IS_GUEST, false)
        }
    }

    fun guestLogin(usingUserId: Long? = null) {
        prefs.edit {
            if (usingUserId != null) {
                putLong(KEY_USER_ID, usingUserId)
            } else {
                remove(KEY_USER_ID)
            }
            putBoolean(KEY_IS_GUEST, true)
        }
    }

    fun isGuest(): Boolean = prefs.getBoolean(KEY_IS_GUEST, false)

    fun getLoggedInUser(): UserEntity? {
        val uid = prefs.getLong(KEY_USER_ID, -1L)
        if (uid < 0) return null
        return AppDatabase.getInstance(ctx).userDao().getUserById(uid)
    }

    fun logout() = prefs.edit { clear() }
}
