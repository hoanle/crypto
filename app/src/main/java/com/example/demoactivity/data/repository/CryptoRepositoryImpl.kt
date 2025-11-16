package com.example.demoactivity.data.repository

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
        return cryptoDao.searchCryptos(query).map { entities ->
            entities.map { it.toDomain() }
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

