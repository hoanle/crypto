package com.example.demoactivity.domain.repository

import com.example.demoactivity.domain.model.CurrencyInfo
import kotlinx.coroutines.flow.Flow

interface FiatRepository {
    fun getAllFiats(): Flow<List<CurrencyInfo>>
    fun searchFiats(query: String): Flow<List<CurrencyInfo>>
    suspend fun getFiatById(id: String): CurrencyInfo?
    suspend fun insertFiat(fiat: CurrencyInfo)
    suspend fun insertFiats(fiats: List<CurrencyInfo>)
    suspend fun updateFiat(fiat: CurrencyInfo)
    suspend fun deleteFiat(fiat: CurrencyInfo)
    suspend fun clearAllFiats()
}

