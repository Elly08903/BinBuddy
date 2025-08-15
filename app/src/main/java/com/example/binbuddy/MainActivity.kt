package com.example.binbuddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.binbuddy.data.AppDatabase
import com.example.binbuddy.data.SampleData
import com.example.binbuddy.data.StoreEntity
import com.example.binbuddy.data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * MainActivity
 *
 * This is the main landing screen of the app after login or guest access.
 *  - Initializes default data (items, users, stores) if database is empty.
 *  - Shows a login prompt link for guest users.
 */
class MainActivity : AppCompatActivity() {

    // Tag used for logging
    private val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ------------------------------
        // User session check
        // ------------------------------
        val user = SessionManager(this).getLoggedInUser()
        if (user == null) {
            // No user logged in: redirect to login screen
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // ------------------------------
        // Window insets and full-screen layout setup
        // ------------------------------
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            // Reset padding to handle custom layout and insets
            view.setPadding(0, 0, 0, 0)
            insets
        }

        // Set layout for this activity
        setContentView(R.layout.activity_main)

        // Get database instance
        val db = AppDatabase.getInstance(this)

        // ------------------------------
        // Initialize default data in database
        // ------------------------------
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val itemDao = db.itemDao()
                // Insert sample items if DB is empty
                if (itemDao.getAllItems().isEmpty()) {
                    SampleData.makeItems().forEach { itemDao.insert(it) }
                }

                // Insert admin account.
                val userDao = db.userDao()
                if (userDao.getUserByUsername("admin") == null) {
                    userDao.insertUser(
                        UserEntity(
                            username = "admin",
                            password = "password123",
                            isAdmin = true
                        )
                    )
                }

                // Insert customer account.
                if (userDao.getUserByUsername("user") == null) {
                    userDao.insertUser(
                        UserEntity(
                            username = "user",
                            password = "userpass",
                            isAdmin = false
                        )
                    )
                }

                // Insert default stores if none exist
                val storeDao = db.storeDao()
                if (storeDao.getAllStores().isEmpty()) {
                    listOf("Hometown Market", "Downtown Grocers", "Suburban Shoppe")
                        .forEach { name ->
                            val id = storeDao.insert(StoreEntity(name = name, location = "Unknown"))
                            Log.d(tag, "Inserted store '$name' with id=$id")
                        }
                }

                // Log all stores for debugging
                val allStores = storeDao.getAllStores()
                Log.d(tag, "All stores in DB (${allStores.size}): ${allStores.joinToString { it.name }}")

            }
        }

        // ------------------------------
        // Navigation buttons setup
        // ------------------------------
        // Stores button
        findViewById<Button>(R.id.storesButton).setOnClickListener {
            startActivity(Intent(this, StoreActivity::class.java))
        }

        val session = SessionManager(this)
        val profileBtn = findViewById<Button>(R.id.profileButton)

        // ------------------------------
        // Guest login prompt link
        // ------------------------------
        findViewById<TextView?>(R.id.tvLoginPrompt)?.let { tv ->
            tv.visibility = if (session.isGuest()) View.VISIBLE else View.GONE
            tv.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        // Profile button click behavior
        profileBtn.setOnClickListener {
            if (session.isGuest()) {
                // Guests cannot access profile
                Toast.makeText(this, "Profile is unavailable for guest accounts.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                return@setOnClickListener
            }
            // Logged-in users go to ProfileActivity
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Disable profile button visually and functionally for guests
        if (session.isGuest()) {
            profileBtn.isEnabled = false
            profileBtn.alpha = 0.5f
        }

        // Settings button navigates to SettingsActivity
        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
