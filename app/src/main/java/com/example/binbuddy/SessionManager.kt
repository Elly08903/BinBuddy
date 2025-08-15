package com.example.binbuddy

import android.content.Context
import com.example.binbuddy.data.AppDatabase
import com.example.binbuddy.data.UserEntity
import androidx.core.content.edit

/**
 * SessionManager
 *
 * This class handles user session management, including:
 *  - Logging in a registered user
 *  - Logging in as a guest
 *  - Checking if the current session is a guest
 *  - Retrieving the currently logged-in user
 *  - Logging out and clearing session data
 */
class SessionManager(private val ctx: Context) {

    // SharedPreferences instance for storing session info
    private val prefs = ctx.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

    companion object {
        // Keys for storing session data in SharedPreferences
        private const val KEY_USER_ID  = "logged_in_user_id"
        private const val KEY_IS_GUEST = "is_guest"
    }

    // ------------------------------
    // Normal user login
    // ------------------------------
    fun login(user: UserEntity) {
        prefs.edit {
            // Save the user's ID and mark session as non-guest
            putLong(KEY_USER_ID, user.id)
            putBoolean(KEY_IS_GUEST, false)
        }
    }

    // ------------------------------
    // Guest login
    // ------------------------------
    fun guestLogin(usingUserId: Long? = null) {
        prefs.edit {
            if (usingUserId != null) {
                // If a user ID is provided, store it
                putLong(KEY_USER_ID, usingUserId)
            } else {
                // Otherwise, remove any stored user ID
                remove(KEY_USER_ID)
            }
            // Mark session as guest
            putBoolean(KEY_IS_GUEST, true)
        }
    }

    // ------------------------------
    // Check if current session is a guest
    // ------------------------------
    fun isGuest(): Boolean = prefs.getBoolean(KEY_IS_GUEST, false)

    // ------------------------------
    // Retrieve the currently logged-in user from database
    // ------------------------------
    fun getLoggedInUser(): UserEntity? {
        val uid = prefs.getLong(KEY_USER_ID, -1L)
        if (uid < 0) return null
        return AppDatabase.getInstance(ctx).userDao().getUserById(uid)
    }

    // ------------------------------
    // Logout: clear all session data
    // ------------------------------
    fun logout() = prefs.edit { clear() }
}
