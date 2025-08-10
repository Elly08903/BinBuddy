package com.example.binbuddy

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

class AdminNavActivity : AppCompatActivity() {
    private val db by lazy { AppDatabase.getInstance(this) }
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_navigation)

        recyclerView = findViewById(R.id.itemsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ItemAdapter(emptyList()) { selected ->
            Intent(this, ItemDetailActivity::class.java).apply {
                putExtra("itemId",          selected.id)
                putExtra("itemTitle",       selected.title)
                putExtra("itemLocation",    selected.location)
                putExtra("itemCost",        selected.cost)
                putExtra("itemDescription", selected.description)
                putExtra("imageId",         selected.imageId)
            }.also(::startActivity)
        }
        recyclerView.adapter = adapter

        val searchView = findViewById<SearchView>(R.id.itemsSearchView)

        lifecycleScope.launch {
            val items: List<ItemEntity> = withContext(Dispatchers.IO) {
                db.itemDao().getAllItems()
            }
            adapter.updateList(items)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val raw = newText.orEmpty()
                val pattern = "%$raw%"
                lifecycleScope.launch {
                    val results = withContext(Dispatchers.IO) {
                        if (raw.isBlank())
                            db.itemDao().getAllItems()
                        else
                            db.itemDao().searchItems(pattern)
                    }
                    adapter.updateList(results)
                }
                return true
            }
        })

        val addProduct = findViewById<Button>(R.id.addProduct)

        addProduct.setOnClickListener{
            // Inflate custom layout
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_item, null)

            // Get EditTexts from layout
            val nameInput = dialogView.findViewById<EditText>(R.id.editItemName)
            val descInput = dialogView.findViewById<EditText>(R.id.editItemDescription)
            val priceInput = dialogView.findViewById<EditText>(R.id.editItemPrice)
            val locationInput = dialogView.findViewById<EditText>(R.id.editItemLocation)


            // Build the AlertDialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Add Item")
                .setView(dialogView)
                .setPositiveButton("Add") { _, _ ->
                    val newItem = ItemEntity(
                        title = nameInput.text.toString(),
                        location = locationInput.text.toString(),
                        cost = priceInput.text.toString(),
                        description = descInput.text.toString(),
                        storeId = 1, // or choose dynamically if you want
                        imageId = R.drawable.ic_launcher_foreground // use a default image or let user pick
                    )

                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            db.itemDao().insert(newItem)
                        }
                        // Fetch updated list on main thread
                        val updatedItems = withContext(Dispatchers.IO) {
                            db.itemDao().getAllItems()
                        }
                        adapter.updateList(updatedItems)
                        // Scroll to last item (newly added)
                        recyclerView.scrollToPosition(updatedItems.size - 1)
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }

            builder.create().show()
        }
    }
}