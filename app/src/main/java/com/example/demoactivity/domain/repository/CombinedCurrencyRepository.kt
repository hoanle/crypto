package com.example.demoactivity.domain.repository

import androidx.paging.PagingData
import com.example.demoactivity.domain.model.CurrencyInfo
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for combined currency operations (both Crypto and Fiat).
 * Provides paginated access to currencies from both tables.
 */
interface CombinedCurrencyRepository {
    /**
     * Returns paginated flow of all currencies (both cryptos and fiats).
     * Results are sorted by name, with cryptos appearing first.
     *
     * @return Flow of PagingData containing all currencies
     */
    fun getAllCombinedCurrenciesPaged(): Flow<PagingData<CurrencyInfo>>

    /**
     * Returns paginated flow of currencies matching the search query.
     * Searches both cryptos and fiats tables.
     *
     * @param query The search query string
     * @return Flow of PagingData containing matching currencies
     */
    fun searchCombinedCurrenciesPaged(query: String): Flow<PagingData<CurrencyInfo>>
}

