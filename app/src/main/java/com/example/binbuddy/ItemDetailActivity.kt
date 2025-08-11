package com.example.binbuddy

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView

class ItemDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        val title = intent.getStringExtra("itemTitle") ?: "Item"
        val location = intent.getStringExtra("itemLocation") ?: "Unknown"
        val cost = intent.getStringExtra("itemCost") ?: "Unknown"
        val description = intent.getStringExtra("itemDescription") ?: "No description"

        findViewById<TextView>(R.id.itemTitle).text = title
        findViewById<TextView>(R.id.itemLocation).text = "Location: $location"
        findViewById<TextView>(R.id.itemCost).text = "Cost: $cost"
        findViewById<TextView>(R.id.itemDescription).text = "Description: $description"

        // You can build out the Image Logic here
    }
    fun onBackClick(v: View) = onBackPressedDispatcher.onBackPressed()

    fun onHomeClick(v: View) {
        val i = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(i)
    }
}
