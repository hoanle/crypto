package com.example.demoactivity.data.local

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.demoactivity.domain.model.CurrencyInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Custom PagingSource that combines results from both Crypto and Fiat tables.
 * Queries both tables separately, merges results, and paginates the combined list.
 * 
 * Strategy: For each page load, fetch data from both tables using Flow queries,
 * merge and sort the results, then return the appropriate page slice.
 * 
 * Note: This approach loads all matching items from both tables for merging.
 * For very large datasets, consider using a database view or UNION query approach.
 */
class CombinedCurrencyPagingSource(
    private val cryptoDao: CryptoDao,
    private val fiatDao: FiatDao,
    private val searchQuery: String
) : PagingSource<Int, CurrencyInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CurrencyInfo> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize

            // Fetch data from both tables in parallel
            val (cryptos, fiats) = withContext(Dispatchers.IO) {
                val trimmedQuery = searchQuery.trim().replace(Regex("\\s+"), " ")
                val cryptoData = if (trimmedQuery.isBlank()) {
                    cryptoDao.getAllCryptos().first().map { it.toDomain() }
                } else {
                    cryptoDao.searchCryptos(trimmedQuery).first().map { it.toDomain() }
                }

                val fiatData = if (trimmedQuery.isBlank()) {
                    fiatDao.getAllFiats().first().map { it.toDomain() }
                } else {
                    fiatDao.searchFiats(trimmedQuery).first().map { it.toDomain() }
                }

                Pair(cryptoData, fiatData)
            }

            // Merge and sort: Cryptos first, then Fiats
            val merged = (cryptos + fiats)

            // Calculate pagination
            val startIndex = page * pageSize
            val endIndex = minOf(startIndex + pageSize, merged.size)

            if (startIndex >= merged.size) {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            } else {
                val pageData = merged.subList(startIndex, endIndex)

                LoadResult.Page(
                    data = pageData,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (endIndex >= merged.size) null else page + 1
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CurrencyInfo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

