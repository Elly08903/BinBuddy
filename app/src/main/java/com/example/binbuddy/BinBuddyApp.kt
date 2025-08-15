// BinBuddyApp.kt
package com.example.binbuddy

import android.app.Application
import android.util.Log
import com.example.binbuddy.data.AppDatabase
import com.example.binbuddy.data.SampleData
import com.example.binbuddy.data.StoreEntity
import com.example.binbuddy.data.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BinBuddyApp
 *
 * Custom Application class that runs once when the app process starts.
 *  - Restores the last logged-in user session.
 *  - Initializes the Room database in a background thread.
 *  - Seeds the database with default admin/user accounts, stores, and sample items
 *      if the tables are currently empty.
 *
 */
class BinBuddyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Restore previously logged-in user from SessionManager.
        val user = SessionManager(this).getLoggedInUser()
        Log.d("BinBuddyApp", "Restored session for user: ${user?.username}")

        // Perform database setup and seeding in background to avoid blocking UI thread.
        CoroutineScope(Dispatchers.IO).launch {

            // Lazily obtain singleton instance of the Room database.
            val db = AppDatabase.getInstance(this@BinBuddyApp)

            // ------------------------------
            // Default User Seeding
            // ------------------------------
            val userDao = db.userDao()

            // Insert admin account.
            userDao.insertUser(
                UserEntity(
                    username = "admin",
                    password = "password123",
                    name     = "Admin User",
                    email    = "admin@example.com",
                    isAdmin  = true
                )
            )

            // Insert customer account.
            userDao.insertUser(
                UserEntity(
                    username = "user",
                    password = "userpass",
                    name     = "Basic User",
                    email    = "user@example.com",
                    isAdmin  = false
                )
            )

            // ------------------------------
            // Default Store Seeding
            // ------------------------------
            val storeDao = db.storeDao()

            // Add stores only if store table is empty.
            if (storeDao.getAllStores().isEmpty()) {
                listOf("Hometown Market","Downtown Grocers","Suburban Shoppe")
                    .forEach { storeDao.insert(StoreEntity(name=it, location="Unknown")) }
            }

            // ------------------------------
            // Default Item Seeding
            // ------------------------------
            if (db.itemDao().getAllItems().isEmpty()) {
                SampleData.makeItems().forEach { db.itemDao().insert(it) }
            }
        }
    }
}
