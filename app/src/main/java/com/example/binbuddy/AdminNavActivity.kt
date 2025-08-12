package com.example.binbuddy

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
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper


class AdminNavActivity : AppCompatActivity() {
    private val db by lazy { AppDatabase.getInstance(this) }
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val session = SessionManager(this)
        if (session.isGuest()) {
            finish()
            return
        }

        lifecycleScope.launch {
            val isAdmin = withContext(Dispatchers.IO) {
                session.getLoggedInUser()?.isAdmin == true
            }
            if (!isAdmin) {
                finish()
                return@launch
            }

            setContentView(R.layout.admin_navigation)

            recyclerView = findViewById(R.id.itemsRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this@AdminNavActivity)

            adapter = ItemAdapter(emptyList()) { selected ->
                Intent(this@AdminNavActivity, ItemDetailActivity::class.java).apply {
                    putExtra("itemId", selected.id)
                    putExtra("itemTitle", selected.title)
                    putExtra("itemLocation", selected.location)
                    putExtra("itemCost", selected.cost)
                    putExtra("itemDescription", selected.description)
                    putExtra("imageId", selected.imageId)
                }.also { startActivity(it) }
            }
            recyclerView.adapter = adapter

            val swipeToDelete = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    rv: RecyclerView,
                    vh: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false

                override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                    val pos = vh.adapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                        ?: return
                    val item = adapter.getItemAt(pos)

                    AlertDialog.Builder(this@AdminNavActivity)
                        .setTitle("Delete item?")
                        .setMessage("Remove “${item.title}” from inventory?")
                        .setPositiveButton("Delete") { _, _ ->
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) { db.itemDao().delete(item) }
                                adapter.removeAt(pos)
                            }
                        }
                        .setNegativeButton("Cancel") { d, _ ->
                            d.dismiss()
                            adapter.notifyItemChanged(pos)
                        }
                        .setOnCancelListener { adapter.notifyItemChanged(pos) }
                        .show()
                }
            }
            ItemTouchHelper(swipeToDelete).attachToRecyclerView(recyclerView)

            val searchView = findViewById<SearchView>(R.id.itemsSearchView)
            launch {
                val items = withContext(Dispatchers.IO) { db.itemDao().getAllItems() }
                adapter.updateList(items)
            }
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    val raw = newText.orEmpty()
                    val pattern = "%$raw%"
                    lifecycleScope.launch {
                        val results = withContext(Dispatchers.IO) {
                            if (raw.isBlank()) db.itemDao().getAllItems()
                            else db.itemDao().searchItems(pattern)
                        }
                        adapter.updateList(results)
                    }
                    return true
                }
            })

            val addProduct = findViewById<Button>(R.id.addProduct)
            addProduct.setOnClickListener {
                val dialogView = layoutInflater.inflate(R.layout.dialog_add_item, null)
                val nameInput     = dialogView.findViewById<EditText>(R.id.editItemName)
                val descInput     = dialogView.findViewById<EditText>(R.id.editItemDescription)
                val priceInput    = dialogView.findViewById<EditText>(R.id.editItemPrice)
                val locationInput = dialogView.findViewById<EditText>(R.id.editItemLocation)
                val spStore       = dialogView.findViewById<Spinner>(R.id.spStore)

                lifecycleScope.launch {
                    val stores = withContext(Dispatchers.IO) { db.storeDao().getAllStores() }
                    spStore.adapter = ArrayAdapter(
                        this@AdminNavActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        stores.map { it.name }
                    )

                    AlertDialog.Builder(this@AdminNavActivity)
                        .setTitle("Add Item")
                        .setView(dialogView)
                        .setPositiveButton("Add") { _, _ ->
                            val idx = spStore.selectedItemPosition
                            val storeId = stores.getOrNull(idx)?.id ?: return@setPositiveButton

                            val newItem = ItemEntity(
                                title = nameInput.text.toString(),
                                location = locationInput.text.toString(),
                                cost = priceInput.text.toString(),
                                description = descInput.text.toString(),
                                storeId = storeId,
                                imageId = R.drawable.ic_launcher_foreground
                            )

                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) { db.itemDao().insert(newItem) }
                                val updated = withContext(Dispatchers.IO) { db.itemDao().getAllItems() }
                                adapter.updateList(updated)
                                recyclerView.scrollToPosition(updated.size - 1)
                            }
                        }
                        .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
                        .show()
                }
            }
        }
    }
}
