package com.example.binbuddy.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Projection that loads one Admin user together with the list of Stores they are allowed to manage (many-to-many via AdminStoreRef).
 *
 * Requirements:
 *  - UserEntity.id and StoreEntity.id exist and are primary keys
 *  - AdminStoreRef(adminId, storeId) is the junction table
 *  - DAO method that returns this must be annotated with @Transaction
 */
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
