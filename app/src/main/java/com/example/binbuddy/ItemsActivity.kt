package com.example.binbuddy

import android.content.Intent
import android.os.Bundle
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
    }
}
