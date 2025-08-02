// app/src/main/java/com/example/binbuddy/data/AppDatabase.kt
package com.example.binbuddy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ItemEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventory.db"
                )
                    // .fallbackToDestructiveMigration() // for class projects, easy migrations
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
