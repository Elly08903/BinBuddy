package com.example.binbuddy.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class AdminWithStores(
    @Embedded val admin: UserEntity,

    @Relation(
        parentColumn  = "id",
        entityColumn  = "id",
        associateBy   = Junction(
            value        = AdminStoreRef::class,
            parentColumn = "adminId",
            entityColumn = "storeId"
        )
    )
    val stores: List<StoreEntity>
)
