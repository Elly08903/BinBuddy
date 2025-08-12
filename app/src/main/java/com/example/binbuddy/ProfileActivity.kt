package com.example.binbuddy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        session = SessionManager(this)

        val adminNav   = findViewById<Button>(R.id.adminnav)
        val btnLogout  = findViewById<Button>(R.id.logoutButton)

        // Hide admin button
        adminNav.visibility = View.GONE

        // Only admins may see it
        if (!session.isGuest()) {
            lifecycleScope.launch {
                val isAdmin = withContext(Dispatchers.IO) {
                    session.getLoggedInUser()?.isAdmin == true
                }
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
