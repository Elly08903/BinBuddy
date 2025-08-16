package com.example.binbuddy.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction


// DAO for stores
@Dao
interface StoreDao {

    /**
     * Loads one admin and all of the stores they manage via the AdminStoreRef junction.
     */
    @Transaction
    @Query("SELECT * FROM users WHERE isAdmin = 1 AND id = :adminId")
    fun getAdminWithStores(adminId: Long): AdminWithStores

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCrossRef(ref: AdminStoreRef)

    @Query("SELECT * FROM stores ORDER BY name ASC")
    fun getAllStores(): List<StoreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(store: StoreEntity): Long
}