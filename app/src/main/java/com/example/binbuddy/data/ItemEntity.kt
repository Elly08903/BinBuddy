// app/src/main/java/com/example/binbuddy/data/ItemEntity.kt
package com.example.binbuddy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val location: String,
    val cost: String,
    val description: String
)
