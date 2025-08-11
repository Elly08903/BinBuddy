package com.example.binbuddy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.binbuddy.data.AppDatabase
import com.example.binbuddy.data.SampleData
import com.example.binbuddy.data.StoreEntity
import com.example.binbuddy.data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val tag = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // if no user, send to login screen
        val user = SessionManager(this).getLoggedInUser()
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            view.setPadding(0, 0, 0, 0)
            insets
        }
        setContentView(R.layout.activity_main)

        val db = AppDatabase.getInstance(this)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val itemDao = db.itemDao()
                if (itemDao.getAllItems().isEmpty()) {
                    SampleData.makeItems().forEach { itemDao.insert(it) }
                }

                val userDao = db.userDao()
                if (userDao.getUserByUsername("admin") == null) {
                    userDao.insertUser(
                        UserEntity(
                            username = "admin",
                            password = "password123",
                            isAdmin = true
                        )
                    )
                }

                if (userDao.getUserByUsername("user") == null) {
                    userDao.insertUser(
                        UserEntity(
                            username = "user",
                            password = "userpass",
                            isAdmin = false
                        )
                    )
                }

                val storeDao = db.storeDao()
                if (storeDao.getAllStores().isEmpty()) {
                    listOf("Hometown Market", "Downtown Grocers", "Suburban Shoppe")
                        .forEach { name ->
                            val id = storeDao.insert(StoreEntity(name = name, location = "Unknown"))
                            Log.d(tag, "Inserted store '$name' with id=$id")
                        }
                }

                val allStores = storeDao.getAllStores()
                Log.d(tag, "All stores in DB (${allStores.size}): ${allStores.joinToString { it.name }}")

            }
        }

        findViewById<Button>(R.id.storesButton).setOnClickListener {
            startActivity(Intent(this, StoreActivity::class.java))
        }
        findViewById<Button>(R.id.profileButton).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
