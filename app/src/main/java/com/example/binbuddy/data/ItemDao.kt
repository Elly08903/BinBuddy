// app/src/main/java/com/example/binbuddy/data/ItemDao.kt
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
      SELECT * FROM items
      WHERE title   LIKE '%' || :q || '%'
         OR location LIKE '%' || :q || '%'
         OR description LIKE '%' || :q || '%'
      ORDER BY title ASC
    """)
    fun searchItems(q: String): List<ItemEntity>

    @Query("DELETE FROM items")
    fun deleteAllItems()
}


