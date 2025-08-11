package com.example.binbuddy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val adminNav = findViewById<Button>(R.id.adminnav)

        adminNav.setOnClickListener {
            startActivity(Intent(this, AdminNavActivity::class.java))
        }
    }

    fun onBackClick(v: View) = onBackPressedDispatcher.onBackPressed()

    fun onHomeClick(v: View) {
        val i = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(i)
    }
}

