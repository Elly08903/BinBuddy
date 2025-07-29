package com.example.binbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            view.setPadding(0, 0, 0, 0)
            insets
        }
        setContentView(R.layout.activity_main)

        val storesBtn = findViewById<Button>(R.id.storesButton)
        val profileBtn = findViewById<Button>(R.id.profileButton)
        val settingsBtn = findViewById<Button>(R.id.settingsButton)

        storesBtn.setOnClickListener {
            startActivity(Intent(this, StoreActivity::class.java))
        }

        profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        settingsBtn.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
