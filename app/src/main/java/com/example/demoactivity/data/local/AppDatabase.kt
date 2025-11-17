package com.example.demoactivity.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FiatEntity::class, CryptoEntity::class],
    views = [CombinedCurrencyView::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fiatDao(): FiatDao
    abstract fun cryptoDao(): CryptoDao
    abstract fun combinedCurrencyDao(): CombinedCurrencyDao
}

