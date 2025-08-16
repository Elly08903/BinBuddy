package com.example.binbuddy.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Room entity representing a row in the `users` table.
 *
 * Notes:
 * - @PrimaryKey(autoGenerate = true) lets SQLite assign incremental IDs.
 * - @ColumnInfo(defaultValue = ...) values are SQL literals used when the column
 *   is added via migration or when an INSERT omits the column.
 *     • name/email default to empty string '' (must be quoted as SQL).
 *     • isAdmin default is 0 (stored as bool).
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val username: String,
    val password: String,

    @ColumnInfo(defaultValue = "''")
    val name: String = "",

    @ColumnInfo(defaultValue = "''")
    val email: String = "",

    @ColumnInfo(defaultValue = "0")
    val isAdmin: Boolean = false
)
