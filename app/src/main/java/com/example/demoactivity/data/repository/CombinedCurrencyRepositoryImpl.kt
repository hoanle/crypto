package com.example.demoactivity.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.demoactivity.data.local.CombinedCurrencyDao
import com.example.demoactivity.data.local.CombinedCurrencyView
import com.example.demoactivity.domain.model.CurrencyInfo
import com.example.demoactivity.domain.repository.CombinedCurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CombinedCurrencyRepositoryImpl @Inject constructor(
    private val combinedCurrencyDao: CombinedCurrencyDao
) : CombinedCurrencyRepository {

    override fun getAllCombinedCurrenciesPaged(): Flow<PagingData<CurrencyInfo>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { combinedCurrencyDao.getAllCombinedCurrenciesPaged() }
        ).flow.map { pagingData: PagingData<CombinedCurrencyView> ->
            pagingData.map { view: CombinedCurrencyView ->
                view.toDomain()
            }
        }
    }

    override fun searchCombinedCurrenciesPaged(query: String): Flow<PagingData<CurrencyInfo>> {
        val trimmedQuery = query.trim().replace(Regex("\\s+"), " ")
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { combinedCurrencyDao.searchCombinedCurrenciesPaged(trimmedQuery) }
        ).flow.map { pagingData: PagingData<CombinedCurrencyView> ->
            pagingData.map { view: CombinedCurrencyView ->
                view.toDomain()
            }
        }
    }
}

