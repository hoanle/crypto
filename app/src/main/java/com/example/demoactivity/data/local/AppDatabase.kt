package com.example.demoactivity.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FiatEntity::class, CryptoEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fiatDao(): FiatDao
    abstract fun cryptoDao(): CryptoDao
}

