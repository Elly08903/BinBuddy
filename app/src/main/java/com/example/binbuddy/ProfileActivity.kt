package com.example.binbuddy

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ProfileActivity.kt (add imports)
import android.widget.TextView

/**
 * ProfileActivity
 *
 * This activity displays the logged-in user's profile information.
 *  - Shows user details: name, email, account type.
 *  - Differentiates between guest, regular, and admin users.
 *  - Provides logout functionality.
 *  - Allows admin users to navigate to AdminNavActivity.
 */
class ProfileActivity : AppCompatActivity() {
    private lateinit var session: SessionManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set layout for this activity
        setContentView(R.layout.activity_profile)

        session = SessionManager(this)

        // UI Button elements
        val adminNav  = findViewById<Button>(R.id.adminnav) // Button for admin navigation
        val btnLogout = findViewById<Button>(R.id.logoutButton) // Button for logging out

        // UI TextView elements
        val tvName        = findViewById<TextView>(R.id.tvName)      // TextView showing user's name
        val tvEmail       = findViewById<TextView>(R.id.tvEmail)     // TextView showing user's email
        val tvAccountType = findViewById<TextView>(R.id.tvAccountType) // TextView showing account type

        // Default state: hide admin navigation button
        adminNav.visibility = View.GONE

        // ------------------------------
        // Load user profile data
        // ------------------------------
        if (session.isGuest()) {
            // Guest user: display limited info
            tvName.text        = "Name: Guest"
            tvEmail.text       = "Email: —"
            tvAccountType.text = "Account Type: Guest"
        } else {
            // Logged-in user: fetch data from database in a coroutine
            lifecycleScope.launch {
                val user = withContext(Dispatchers.IO) { session.getLoggedInUser() }
                val isAdmin = user?.isAdmin == true

                // Populate profile fields, using defaults for missing values
                tvName.text        = "Name: ${user?.name?.ifBlank { "(no name)" } ?: "(unknown)"}"
                tvEmail.text       = "Email: ${user?.email?.ifBlank { "(none)" } ?: "(unknown)"}"
                tvAccountType.text = if (isAdmin) "Account Type: Admin" else "Account Type: User"

                // Show admin navigation button for admin users
                if (isAdmin) adminNav.visibility = View.VISIBLE
            }
        }

        // ------------------------------
        // Admin navigation button click
        // ------------------------------
        adminNav.setOnClickListener {
            startActivity(Intent(this, AdminNavActivity::class.java))
        }

        // ------------------------------
        // Logout button click
        // ------------------------------
        btnLogout.setOnClickListener {
            // Clear session and navigate back to login screen
            session.logout()
            val i = Intent(this, LoginActivity::class.java).apply {
                // Clear activity stack to prevent back navigation
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(i)
        }
    }

    // ------------------------------
    // Navigation Button Handlers
    // ------------------------------

    /**
     * Called when the back button is clicked.
     * Delegates to the system back press dispatcher.
     */
    fun onBackClick(v: View) = onBackPressedDispatcher.onBackPressed()

    /**
     * Called when the home button is clicked.
     * Navigates back to MainActivity, clearing any intermediate activities.
     */
    fun onHomeClick(v: View) {
        val i = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(i)
    }
}
