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

class ProfileActivity : AppCompatActivity() {
    private lateinit var session: SessionManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        session = SessionManager(this)

        val adminNav  = findViewById<Button>(R.id.adminnav)
        val btnLogout = findViewById<Button>(R.id.logoutButton)

        val tvName        = findViewById<TextView>(R.id.tvName)
        val tvEmail       = findViewById<TextView>(R.id.tvEmail)
        val tvAccountType = findViewById<TextView>(R.id.tvAccountType)

        // Default state
        adminNav.visibility = View.GONE

        if (session.isGuest()) {
            tvName.text        = "Name: Guest"
            tvEmail.text       = "Email: —"
            tvAccountType.text = "Account Type: Guest"
        } else {
            lifecycleScope.launch {
                val user = withContext(Dispatchers.IO) { session.getLoggedInUser() }
                val isAdmin = user?.isAdmin == true

                tvName.text        = "Name: ${user?.name?.ifBlank { "(no name)" } ?: "(unknown)"}"
                tvEmail.text       = "Email: ${user?.email?.ifBlank { "(none)" } ?: "(unknown)"}"
                tvAccountType.text = if (isAdmin) "Account Type: Admin" else "Account Type: User"

                if (isAdmin) adminNav.visibility = View.VISIBLE
            }
        }

        adminNav.setOnClickListener {
            startActivity(Intent(this, AdminNavActivity::class.java))
        }

        btnLogout.setOnClickListener {
            session.logout()
            val i = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(i)
        }
    }

    fun onBackClick(v: View) = onBackPressedDispatcher.onBackPressed()

    fun onHomeClick(v: View) {
        val i = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(i)
    }
}
