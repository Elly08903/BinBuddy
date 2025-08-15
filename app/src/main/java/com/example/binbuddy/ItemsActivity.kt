package com.example.binbuddy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.binbuddy.data.AppDatabase
import com.example.binbuddy.data.ItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ItemsActivity
 *
 * Displays a list of inventory items, filtered by store.
 *  - Load all items for a specific store.
 *  - Search through items in real-time using a SearchView.
 *  - Navigate to ItemDetailActivity when an item is clicked.
 */
class ItemsActivity : AppCompatActivity() {

    // Database instance (lazy-loaded)
    private val db by lazy { AppDatabase.getInstance(this) }

    // RecyclerView + adapter for displaying items
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load activity items layout
        setContentView(R.layout.activity_items)

        // Get storeId from Intent extras to filter items
        var storeId = intent.getLongExtra("storeId", -1)

        // ------------------------------
        // Load items from the database
        // ------------------------------
        lifecycleScope.launch {
            val items = withContext(Dispatchers.IO) {
                if (storeId > 0) db.itemDao().getItemsForStore(storeId) // Filter by store
                else             db.itemDao().getAllItems()            // Load all items
            }
            adapter.updateList(items)
        }

        // ------------------------------
        // RecyclerView setup
        // ------------------------------
        recyclerView = findViewById(R.id.itemsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with empty list and define click behavior
        adapter = ItemAdapter(emptyList()) { selected ->

            // Open ItemDetailActivity and pass item details via Intent extras
            Intent(this, ItemDetailActivity::class.java).apply {
                putExtra("itemId",          selected.id)
                putExtra("itemTitle",       selected.title)
                putExtra("itemLocation",    selected.location)
                putExtra("itemCost",        selected.cost)
                putExtra("itemDescription", selected.description)
                putExtra("imageId", selected.imageId)
                putExtra("imageUri", selected.imageUri)
            }.also(::startActivity)
        }
        recyclerView.adapter = adapter

        // ------------------------------
        // SearchView setup
        // ------------------------------
        val searchView = findViewById<SearchView>(R.id.itemsSearchView)

        // Filter list as user types in search box.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val raw = newText.orEmpty()
                val pattern = "%$raw%"
                lifecycleScope.launch {
                    val results = withContext(Dispatchers.IO) {
                        if (raw.isBlank()) {
                            // Return all items if search query is blank
                            if (storeId > 0) db.itemDao().getItemsForStore(storeId)
                            else             db.itemDao().getAllItems()
                        } else {
                            // Filter items by search query
                            if (storeId > 0) db.itemDao().searchItemsForStore(storeId, pattern)
                            else             db.itemDao().searchItems(pattern)
                        }
                    }
                    // Update the adapter with filtered results
                    adapter.updateList(results)
                }
                return true
            }
        })
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
