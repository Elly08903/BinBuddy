package com.example.binbuddy

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * SettingsActivity
 *
 * Displays settings information. Currently hardcoded,  implementable in the future.
 */
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for this activity
        setContentView(R.layout.activity_settings)
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
