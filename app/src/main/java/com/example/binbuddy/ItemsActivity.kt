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

class ItemsActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

        var storeId = intent.getLongExtra("storeId", -1)

        lifecycleScope.launch {
            val items = withContext(Dispatchers.IO) {
                if (storeId > 0) db.itemDao().getItemsForStore(storeId)
                else             db.itemDao().getAllItems()
            }
            adapter.updateList(items)
        }

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

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val raw = newText.orEmpty()
                val pattern = "%$raw%"
                lifecycleScope.launch {
                    val results = withContext(Dispatchers.IO) {
                        if (raw.isBlank()) {
                            if (storeId > 0) db.itemDao().getItemsForStore(storeId)
                            else             db.itemDao().getAllItems()
                        } else {
                            if (storeId > 0) db.itemDao().searchItemsForStore(storeId, pattern)
                            else             db.itemDao().searchItems(pattern)
                        }
                    }
                    adapter.updateList(results)
                }
                return true
            }
        })
    }
    fun onBackClick(v: View) = onBackPressedDispatcher.onBackPressed()

    fun onHomeClick(v: View) {
        val i = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(i)
    }
}
