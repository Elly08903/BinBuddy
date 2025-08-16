package com.example.binbuddy.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

// DAO for users
@Dao
interface UserDao {
    // Fetch a single user by (assumed unique) username, returns null when no matching row exists.
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    fun getUserByUsername(username: String): UserEntity?

    // Look up a user by primary key.
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Long): UserEntity?


    // Insert a new user, return value is the new rowId, or -1L if the insert was ignored.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(user: UserEntity): Long

    // Update the entire row matching the primary key inside `user`.
    @Update
    fun updateUser(user: UserEntity)
}