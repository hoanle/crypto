package com.example.demoactivity.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query

/**
 * DAO for combined currency queries using UNION at the database level.
 * Provides efficient pagination and search across both Crypto and Fiat tables.
 */
@Dao
interface CombinedCurrencyDao {
    /**
     * Returns a PagingSource that combines all cryptos and fiats.
     *
     * @return PagingSource that provides paginated CombinedCurrencyView from both tables
     */
    @Query("SELECT * FROM combined_currencies")
    fun getAllCombinedCurrenciesPaged(): PagingSource<Int, CombinedCurrencyView>

    /**
     * Returns a PagingSource that searches both cryptos and fiats tables.
     * Search matches (as per Step 14 requirements):
     * - Currency name starts with the search term
     * - Currency name contains a space-prefixed partial match
     * - Currency symbol starts with the search term
     *
     * Results are ordered by:
     * 1. Name matches first
     * 2. Symbol matches second
     *
     * @param query The search query string (should be trimmed and normalized)
     * @return PagingSource that provides paginated CombinedCurrencyView matching the search query
     */
    @Query("""
        SELECT * FROM combined_currencies 
        WHERE LOWER(name) LIKE LOWER(:query) || '%' 
           OR LOWER(name) LIKE '% ' || LOWER(:query) || '%'
           OR LOWER(symbol) LIKE LOWER(:query) || '%'
        ORDER BY 
            CASE 
                WHEN LOWER(name) LIKE LOWER(:query) || '%' THEN 1
                WHEN LOWER(symbol) LIKE LOWER(:query) || '%' THEN 2
                ELSE 3
            END
    """)
    fun searchCombinedCurrenciesPaged(query: String): PagingSource<Int, CombinedCurrencyView>
}
