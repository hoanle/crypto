package com.example.demoactivity.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.demoactivity.data.local.CryptoDao
import com.example.demoactivity.data.local.toCryptoEntity
import com.example.demoactivity.domain.model.CurrencyInfo
import com.example.demoactivity.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CryptoRepositoryImpl @Inject constructor(
    private val cryptoDao: CryptoDao
) : CryptoRepository {
    override fun getAllCryptos(): Flow<List<CurrencyInfo>> {
        return cryptoDao.getAllCryptos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchCryptos(query: String): Flow<List<CurrencyInfo>> {
        val trimmedQuery = query.trim().replace(Regex("\\s+"), " ")
        return cryptoDao.searchCryptos(trimmedQuery).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllCryptosPaged(): Flow<PagingData<CurrencyInfo>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { cryptoDao.getAllCryptosPaged() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun searchCryptosPaged(query: String): Flow<PagingData<CurrencyInfo>> {
        val trimmedQuery = query.trim().replace(Regex("\\s+"), " ")
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { cryptoDao.searchCryptosPaged(trimmedQuery) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun getCryptoById(id: String): CurrencyInfo? {
        return cryptoDao.getCryptoById(id)?.toDomain()
    }

    override suspend fun insertCrypto(crypto: CurrencyInfo) {
        cryptoDao.insertCrypto(crypto.toCryptoEntity())
    }

    override suspend fun insertCryptos(cryptos: List<CurrencyInfo>) {
        cryptoDao.insertCryptosBatch(cryptos.map { it.toCryptoEntity() })
    }

    override suspend fun updateCrypto(crypto: CurrencyInfo) {
        cryptoDao.updateCrypto(crypto.toCryptoEntity())
    }

    override suspend fun deleteCrypto(crypto: CurrencyInfo) {
        cryptoDao.deleteCrypto(crypto.toCryptoEntity())
    }

    override suspend fun clearAllCryptos() {
        cryptoDao.clearAllCryptos()
    }
}

