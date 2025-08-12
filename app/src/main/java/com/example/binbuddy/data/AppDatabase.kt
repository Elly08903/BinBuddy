package com.example.binbuddy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ItemEntity::class,
        StoreEntity::class,
        UserEntity::class,
        AdminStoreRef::class
    ],
    version = 10,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun userDao(): UserDao
    abstract fun storeDao(): StoreDao

    companion object {
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS stores(
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        location TEXT NOT NULL
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS items_new(
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        location TEXT NOT NULL,
                        cost TEXT NOT NULL,
                        description TEXT NOT NULL,
                        storeId INTEGER NOT NULL DEFAULT 1,
                        imageId INTEGER NOT NULL DEFAULT 0,
                        imageUri TEXT,
                        FOREIGN KEY(storeId) REFERENCES stores(id) ON DELETE CASCADE
                    )
                """.trimIndent())

                try {
                    db.execSQL("""
                        INSERT INTO items_new (id, title, location, cost, description, storeId, imageId)
                        SELECT id, title, location, cost, description, 1 AS storeId,
                               COALESCE(imageId, 0)
                        FROM items
                    """.trimIndent())
                } catch (_: Exception) { }

                db.execSQL("DROP TABLE IF EXISTS items")
                db.execSQL("ALTER TABLE items_new RENAME TO items")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_items_storeId ON items(storeId)")
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS admin_store_refs(
                        adminId INTEGER NOT NULL,
                        storeId INTEGER NOT NULL,
                        PRIMARY KEY(adminId, storeId),
                        FOREIGN KEY(adminId) REFERENCES users(id) ON DELETE CASCADE,
                        FOREIGN KEY(storeId) REFERENCES stores(id) ON DELETE CASCADE
                    )
                """.trimIndent())
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE items ADD COLUMN imageUri TEXT")
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN name TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE users ADD COLUMN email TEXT NOT NULL DEFAULT ''")
            }
        }

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventory.db"
                )
                    .addMigrations(
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9,
                        MIGRATION_9_10
                    )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigrationFrom(true,1,2,3,4)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
