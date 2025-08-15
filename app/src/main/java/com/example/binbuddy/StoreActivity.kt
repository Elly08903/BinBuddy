package com.example.binbuddy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.binbuddy.data.AppDatabase
import com.example.binbuddy.data.StoreEntity
import com.example.binbuddy.data.StoreDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * StoreActivity
 *
 * This activity displays a list of all stores in the app as dynamically generated buttons.
 * Clicking a store button navigates the user to the ItemsActivity filtered by that store.
 */
class StoreActivity : AppCompatActivity() {
    // Database instance (lazy-loaded)
    private val db by lazy { AppDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for this activity
        setContentView(R.layout.activity_store)

        // LinearLayout container where store buttons will be dynamically added
        val container = findViewById<LinearLayout>(R.id.storesContainer)

        lifecycleScope.launch {
            // Fetch all stores from the database
            val stores: List<StoreEntity> = withContext(Dispatchers.IO) {
                db.storeDao().getAllStores()
            }

            // Dynamically create a Button for each store
            stores.forEach { store ->
                val btn = Button(this@StoreActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.setMargins(0, 0, 0, 24) // Add spacing between buttons
                    }
                    // Set button text to store name
                    text = store.name

                    // Set background and text color
                    setBackgroundColor(0xFF3F51B5.toInt())
                    setTextColor(0xFFFFFFFF.toInt())

                    // Navigate to ItemsActivity filtered by this store when clicked
                    setOnClickListener {
                        startActivity(Intent(this@StoreActivity, ItemsActivity::class.java).apply {
                            putExtra("storeId", store.id)
                        })
                    }
                }

                // Add the button to the LinearLayout container
                container.addView(btn)
            }
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
