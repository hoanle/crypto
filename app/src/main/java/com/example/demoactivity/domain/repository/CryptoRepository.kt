package com.example.demoactivity.domain.repository

import androidx.paging.PagingData
import com.example.demoactivity.domain.model.CurrencyInfo
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {
    fun getAllCryptos(): Flow<List<CurrencyInfo>>
    fun searchCryptos(query: String): Flow<List<CurrencyInfo>>
    
    fun getAllCryptosPaged(): Flow<PagingData<CurrencyInfo>>
    fun searchCryptosPaged(query: String): Flow<PagingData<CurrencyInfo>>
    
    suspend fun getCryptoById(id: String): CurrencyInfo?
    suspend fun insertCrypto(crypto: CurrencyInfo)
    suspend fun insertCryptos(cryptos: List<CurrencyInfo>)
    suspend fun updateCrypto(crypto: CurrencyInfo)
    suspend fun deleteCrypto(crypto: CurrencyInfo)
    suspend fun clearAllCryptos()
}

