package com.example.binbuddy.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    fun getUserByUsername(username: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(user: UserEntity): Long
}