package com.example.binbuddy.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * Simple store record.
 * - Each store gets an auto-generated Long primary key.
 * - `name` and `location` are free-form for now.
 */

@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val location: String
)

