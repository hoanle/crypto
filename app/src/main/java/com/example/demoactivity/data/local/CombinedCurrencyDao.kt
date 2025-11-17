package com.example.demoactivity.data.local

import androidx.paging.PagingSource
import com.example.demoactivity.domain.model.CurrencyInfo

/**
 * DAO interface for combined currency queries (both Crypto and Fiat).
 * Provides PagingSource for efficient pagination across both tables.
 */
interface CombinedCurrencyDao {
    /**
     * Returns a PagingSource that combines all cryptos and fiats.
     * Results are sorted by name, with cryptos appearing first.
     *
     * @return PagingSource that provides paginated CurrencyInfo from both tables
     */
    fun getAllCombinedCurrenciesPaged(): PagingSource<Int, CurrencyInfo>

    /**
     * Returns a PagingSource that searches both cryptos and fiats tables.
     * Search matches:
     * - Currency name starts with the search term
     * - Currency name contains a space-prefixed partial match
     * - Currency symbol starts with the search term
     *
     * @param query The search query string
     * @return PagingSource that provides paginated CurrencyInfo matching the search query
     */
    fun searchCombinedCurrenciesPaged(query: String): PagingSource<Int, CurrencyInfo>
}

/**
 * Implementation of CombinedCurrencyDao that uses CryptoDao and FiatDao
 * to query both tables and merge results.
 */
class CombinedCurrencyDaoImpl(
    private val cryptoDao: CryptoDao,
    private val fiatDao: FiatDao
) : CombinedCurrencyDao {

    override fun getAllCombinedCurrenciesPaged(): PagingSource<Int, CurrencyInfo> {
        return CombinedCurrencyPagingSource(
            cryptoDao = cryptoDao,
            fiatDao = fiatDao,
            searchQuery = ""
        )
    }

    override fun searchCombinedCurrenciesPaged(query: String): PagingSource<Int, CurrencyInfo> {
        return CombinedCurrencyPagingSource(
            cryptoDao = cryptoDao,
            fiatDao = fiatDao,
            searchQuery = query
        )
    }
}

