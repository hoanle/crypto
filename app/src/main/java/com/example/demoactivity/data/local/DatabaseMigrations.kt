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

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Drop existing view if it exists (in case it was created without backticks)
            database.execSQL("DROP VIEW IF EXISTS combined_currencies")
            database.execSQL("DROP VIEW IF EXISTS `combined_currencies`")
            
            // Create combined_currencies view for efficient UNION queries
            // Note: The SQL after "AS" must exactly match the @DatabaseView value
            database.execSQL(
                "CREATE VIEW `combined_currencies` AS SELECT id, name, symbol, NULL as code FROM cryptos UNION ALL SELECT id, name, symbol, code FROM fiats"
            )
        }
    }
}

