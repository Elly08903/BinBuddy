package com.example.binbuddy.data

import androidx.room.*

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY title ASC")
    fun getAllItems(): List<ItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: ItemEntity): Long

    @Delete
    fun delete(item: ItemEntity)

    @Update
    fun update(item: ItemEntity)

    @Query("""
    SELECT *
      FROM items
     WHERE title       LIKE :pattern
        OR location    LIKE :pattern
        OR description LIKE :pattern
     ORDER BY title ASC
  """)
    fun searchItems(pattern: String): List<ItemEntity>

    @Query("DELETE FROM items")
    fun deleteAllItems()
}


