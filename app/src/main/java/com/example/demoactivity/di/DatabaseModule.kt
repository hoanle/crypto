package com.example.demoactivity.di

import android.content.Context
import androidx.room.Room
import com.example.demoactivity.data.local.AppDatabase
import com.example.demoactivity.data.local.CryptoDao
import com.example.demoactivity.data.local.DatabaseMigrations
import com.example.demoactivity.data.local.FiatDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
            .addMigrations(DatabaseMigrations.MIGRATION_1_2, DatabaseMigrations.MIGRATION_2_3)
            .build()
    }

    @Provides
    fun provideFiatDao(database: AppDatabase): FiatDao {
        return database.fiatDao()
    }

    @Provides
    fun provideCryptoDao(database: AppDatabase): CryptoDao {
        return database.cryptoDao()
    }
}

