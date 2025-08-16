package com.example.binbuddy.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
/**
 * ItemEntity represents a single inventory item that belongs to a Store.
 * - The foreign key enforces that every item references an existing store.
 * - onDelete = CASCADE means deleting a store will delete all its items.
 * - Index on storeId speeds up "items by store" queries.
 */
@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = StoreEntity::class,
            parentColumns = ["id"],
            childColumns = ["storeId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("storeId")]
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val location: String,
    val cost: String,
    val description: String,
    val storeId: Long,
    val imageId: Int = 0,
    val imageUri: String? = null
)