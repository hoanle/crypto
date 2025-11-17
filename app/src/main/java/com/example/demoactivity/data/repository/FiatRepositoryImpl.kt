package com.example.demoactivity.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.demoactivity.data.local.FiatDao
import com.example.demoactivity.data.local.toFiatEntity
import com.example.demoactivity.domain.model.CurrencyInfo
import com.example.demoactivity.domain.repository.FiatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FiatRepositoryImpl @Inject constructor(
    private val fiatDao: FiatDao
) : FiatRepository {
    override fun getAllFiats(): Flow<List<CurrencyInfo>> {
        return fiatDao.getAllFiats().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchFiats(query: String): Flow<List<CurrencyInfo>> {
        val trimmedQuery = query.trim().replace(Regex("\\s+"), " ")
        return fiatDao.searchFiats(trimmedQuery).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllFiatsPaged(): Flow<PagingData<CurrencyInfo>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { fiatDao.getAllFiatsPaged() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun searchFiatsPaged(query: String): Flow<PagingData<CurrencyInfo>> {
        val trimmedQuery = query.trim().replace(Regex("\\s+"), " ")
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { fiatDao.searchFiatsPaged(trimmedQuery) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun getFiatById(id: String): CurrencyInfo? {
        return fiatDao.getFiatById(id)?.toDomain()
    }

    override suspend fun insertFiat(fiat: CurrencyInfo) {
        fiatDao.insertFiat(fiat.toFiatEntity())
    }

    override suspend fun insertFiats(fiats: List<CurrencyInfo>) {
        fiatDao.insertFiatsBatch(fiats.map { it.toFiatEntity() })
    }

    override suspend fun updateFiat(fiat: CurrencyInfo) {
        fiatDao.updateFiat(fiat.toFiatEntity())
    }

    override suspend fun deleteFiat(fiat: CurrencyInfo) {
        fiatDao.deleteFiat(fiat.toFiatEntity())
    }

    override suspend fun clearAllFiats() {
        fiatDao.clearAllFiats()
    }
}

