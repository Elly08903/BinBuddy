package com.example.binbuddy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView

/**
 * ItemDetailActivity
 *
 * Displays detailed information about a selected item, including title, location, cost,
 *      description, and image.
 *  - Retrieves data passed through an Intent
 *  - Populates the layout's TextViews and ImageView with the retrieved item details.
 */
class ItemDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for this activity
        setContentView(R.layout.activity_item_detail)

        // ------------------------------
        // Retrieve item details from Intent extras
        // ------------------------------
        val title = intent.getStringExtra("itemTitle") ?: "Item"
        val location = intent.getStringExtra("itemLocation") ?: "Unknown"
        val cost = intent.getStringExtra("itemCost") ?: "Unknown"
        val description = intent.getStringExtra("itemDescription") ?: "No description"

        // ------------------------------
        // Populate TextViews with item details
        // ------------------------------
        findViewById<TextView>(R.id.itemTitle).text = title
        findViewById<TextView>(R.id.itemLocation).text = "Location: $location"
        findViewById<TextView>(R.id.itemCost).text = "Cost: $cost"
        findViewById<TextView>(R.id.itemDescription).text = "Description: $description"

        // ------------------------------
        // Handle item image display
        // ------------------------------
        val imageUriString = intent.getStringExtra("imageUri")
        val imageView = findViewById<ImageView>(R.id.itemImage)

        if (!imageUriString.isNullOrEmpty()) {
            try {
                // Try to parse the URI and load the image
                val uri = Uri.parse(imageUriString)
                imageView.setImageURI(uri)
            } catch (e: Exception) {
                // If loading from URI fails, show fallback image
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
            }
        } else {
            // If no URI provided, try loading a resource image (or fallback)
            val imageResId = intent.getIntExtra("imageId", R.drawable.ic_launcher_foreground)
            imageView.setImageResource(imageResId)
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
