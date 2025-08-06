package com.example.binbuddy

import android.content.Intent
import android.os.Bundle
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

class StoreActivity : AppCompatActivity() {
    private val db by lazy { AppDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        val container = findViewById<LinearLayout>(R.id.storesContainer)

        lifecycleScope.launch {
            val stores: List<StoreEntity> = withContext(Dispatchers.IO) {
                db.storeDao().getAllStores()
            }

            stores.forEach { store ->
                val btn = Button(this@StoreActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.setMargins(0, 0, 0, 24)
                    }
                    text = store.name
                    setBackgroundColor(0xFF3F51B5.toInt())
                    setTextColor(0xFFFFFFFF.toInt())

                    setOnClickListener {
                        startActivity(Intent(this@StoreActivity, ItemsActivity::class.java).apply {
                            putExtra("storeId", store.id)
                        })
                    }
                }
                container.addView(btn)
            }
        }
    }
}
