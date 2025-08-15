package com.example.binbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.binbuddy.data.AppDatabase
import com.example.binbuddy.data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * LoginActivity
 *
 * Handles user authentication for the app.
 *  - Login with username and password.
 *  - Guest login (auto-creates a default non-admin user if necessary).
 */
class LoginActivity : AppCompatActivity() {

    // Session manager for managing login state
    private lateinit var session: SessionManager

    // Database instance (lazy-loaded)
    private val db by lazy { AppDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the layout for this activity
        setContentView(R.layout.activity_login)

        session = SessionManager(this)

        // Input fields for username and password
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        // Buttons for login and guest access
        val btnLogin  = findViewById<Button>(R.id.btnLogin)
        val btnGuest  = findViewById<Button>(R.id.btnGuest)

        // ------------------------------
        // Login button behavior
        // ------------------------------
        btnLogin.setOnClickListener {
            val user = etUsername.text.toString()
            val pass = etPassword.text.toString()

            lifecycleScope.launch {
                // Fetch user from database by username in IO thread
                val dbUser: UserEntity? = withContext(Dispatchers.IO) {
                    db.userDao().getUserByUsername(user)
                }
                if (dbUser != null && dbUser.password == pass) {
                    // Valid credentials: save session and open MainActivity
                    session.login(dbUser)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    // Invalid credentials: show a toast message
                    Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // ------------------------------
        // Guest login button behavior
        // -----------------------------
        btnGuest.setOnClickListener {
            lifecycleScope.launch {
                // Fetch the default guest user or create it if it does not exist
                val user = withContext(Dispatchers.IO) {
                    db.userDao().getUserByUsername("user")
                        ?: UserEntity(username = "user", password = "userpass", isAdmin = false).also {
                            db.userDao().insertUser(it)
                        }.let { db.userDao().getUserByUsername("user") }
                }

                // Mark session as guest login
                SessionManager(this@LoginActivity).guestLogin(usingUserId = user!!.id)

                // Open StoreActivity for guest users
                startActivity(Intent(this@LoginActivity, StoreActivity::class.java))
                finish()
            }
        }

    }
}
