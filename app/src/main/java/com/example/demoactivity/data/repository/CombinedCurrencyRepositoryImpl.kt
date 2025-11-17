package com.example.demoactivity.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.demoactivity.data.local.CombinedCurrencyDao
import com.example.demoactivity.data.local.CombinedCurrencyDaoImpl
import com.example.demoactivity.data.local.CryptoDao
import com.example.demoactivity.data.local.FiatDao
import com.example.demoactivity.domain.model.CurrencyInfo
import com.example.demoactivity.domain.repository.CombinedCurrencyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CombinedCurrencyRepositoryImpl @Inject constructor(
    private val cryptoDao: CryptoDao,
    private val fiatDao: FiatDao
) : CombinedCurrencyRepository {

    private val combinedCurrencyDao: CombinedCurrencyDao = CombinedCurrencyDaoImpl(cryptoDao, fiatDao)

    override fun getAllCombinedCurrenciesPaged(): Flow<PagingData<CurrencyInfo>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { combinedCurrencyDao.getAllCombinedCurrenciesPaged() }
        ).flow
    }

    override fun searchCombinedCurrenciesPaged(query: String): Flow<PagingData<CurrencyInfo>> {
        val trimmedQuery = query.trim().replace(Regex("\\s+"), " ")
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { combinedCurrencyDao.searchCombinedCurrenciesPaged(trimmedQuery) }
        ).flow
    }
}

