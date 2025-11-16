package com.example.demoactivity.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create fiats table
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS fiats (
                    id TEXT NOT NULL PRIMARY KEY,
                    name TEXT NOT NULL,
                    symbol TEXT NOT NULL,
                    code TEXT NOT NULL
                )
                """.trimIndent()
            )

            // Create cryptos table
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS cryptos (
                    id TEXT NOT NULL PRIMARY KEY,
                    name TEXT NOT NULL,
                    symbol TEXT NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Drop items table as it's no longer needed
            database.execSQL("DROP TABLE IF EXISTS items")
        }
    }
}

