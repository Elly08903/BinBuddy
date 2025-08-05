package com.example.binbuddy.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val username: String,
    val password: String,

    @ColumnInfo(defaultValue = "0")
    val isAdmin: Boolean = false
)