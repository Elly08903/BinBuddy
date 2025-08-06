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

class BinBuddyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val user = SessionManager(this).getLoggedInUser()
        Log.d("BinBuddyApp", "Restored session for user: ${user?.username}")

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(this@BinBuddyApp)

            val userDao = db.userDao()
            if (userDao.getUserByUsername("admin") == null) {
                userDao.insertUser(UserEntity(username="admin", password = "password123", isAdmin =  true))
            }
            if (userDao.getUserByUsername("user") == null) {
                userDao.insertUser(UserEntity(username="user", password = "userpass", isAdmin = false))
            }

            val storeDao = db.storeDao()
            if (storeDao.getAllStores().isEmpty()) {
                listOf("Hometown Market","Downtown Grocers","Suburban Shoppe")
                    .forEach { storeDao.insert(StoreEntity(name=it, location="Unknown")) }
            }

            if (db.itemDao().getAllItems().isEmpty()) {
                SampleData.makeItems().forEach { db.itemDao().insert(it) }
            }
        }
    }
}
