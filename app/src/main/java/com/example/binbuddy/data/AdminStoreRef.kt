package com.example.binbuddy.data


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index

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
