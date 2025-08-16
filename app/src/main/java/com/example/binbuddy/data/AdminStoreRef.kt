package com.example.binbuddy.data


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index


/**
 * Cross-reference (junction) table linking Admin users to Stores.
 *
 * Many-to-many:
 *  - One admin can manage many stores
 *  - One store can be managed by many admins
 *
 * Composite primary key (adminId, storeId) prevents duplicate links.
 * CASCADE ensures the link rows are removed automatically if a User or Store is deleted.
 */
@Entity(
    tableName = "admin_store_cross_ref",
    primaryKeys = ["adminId", "storeId"],
    foreignKeys = [
        ForeignKey(UserEntity::class, ["id"], ["adminId"], onDelete = CASCADE),
        ForeignKey(StoreEntity::class, ["id"], ["storeId"], onDelete = CASCADE)
    ],
    indices = [Index("adminId"), Index("storeId")]
)
data class AdminStoreRef(
    val adminId: Long,
    val storeId: Long
)
