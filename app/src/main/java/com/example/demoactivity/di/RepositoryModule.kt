package com.example.demoactivity.di

import com.example.demoactivity.data.repository.CombinedCurrencyRepositoryImpl
import com.example.demoactivity.data.repository.CryptoRepositoryImpl
import com.example.demoactivity.data.repository.FiatRepositoryImpl
import com.example.demoactivity.domain.repository.CombinedCurrencyRepository
import com.example.demoactivity.domain.repository.CryptoRepository
import com.example.demoactivity.domain.repository.FiatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindFiatRepository(
        fiatRepositoryImpl: FiatRepositoryImpl
    ): FiatRepository

    @Binds
    @Singleton
    abstract fun bindCryptoRepository(
        cryptoRepositoryImpl: CryptoRepositoryImpl
    ): CryptoRepository

    @Binds
    @Singleton
    abstract fun bindCombinedCurrencyRepository(
        combinedCurrencyRepositoryImpl: CombinedCurrencyRepositoryImpl
    ): CombinedCurrencyRepository
}

