package com.example.demoactivity.data.repository

import app.cash.turbine.test
import com.example.demoactivity.data.local.CryptoDao
import com.example.demoactivity.data.local.CryptoEntity
import com.example.demoactivity.domain.model.CurrencyInfo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CryptoRepositoryImplTest {
    private lateinit var cryptoDao: CryptoDao
    private lateinit var repository: CryptoRepositoryImpl

    @Before
    fun setup() {
        cryptoDao = mockk()
        repository = CryptoRepositoryImpl(cryptoDao)
    }

    @Test
    fun `getAllCryptos returns flow of domain cryptos`() = runTest {
        // Given
        val entities = listOf(
            CryptoEntity(id = "BTC", name = "Bitcoin", symbol = "BTC"),
            CryptoEntity(id = "ETH", name = "Ethereum", symbol = "ETH")
        )
        every { cryptoDao.getAllCryptos() } returns flowOf(entities)

        // When
        val result = repository.getAllCryptos()

        // Then
        result.test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertEquals("BTC", items[0].id)
            assertEquals("Bitcoin", items[0].name)
            assertEquals("ETH", items[1].id)
            assertEquals("Ethereum", items[1].name)
            awaitComplete()
        }
    }

    @Test
    fun `searchCryptos returns flow of filtered domain cryptos`() = runTest {
        // Given
        val entities = listOf(
            CryptoEntity(id = "BTC", name = "Bitcoin", symbol = "BTC")
        )
        every { cryptoDao.searchCryptos("BTC") } returns flowOf(entities)

        // When
        val result = repository.searchCryptos("BTC")

        // Then
        result.test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("BTC", items[0].id)
            awaitComplete()
        }
    }

    @Test
    fun `getCryptoById returns domain crypto when found`() = runTest {
        // Given
        val entity = CryptoEntity(id = "BTC", name = "Bitcoin", symbol = "BTC")
        coEvery { cryptoDao.getCryptoById("BTC") } returns entity

        // When
        val result = repository.getCryptoById("BTC")

        // Then
        assertEquals("BTC", result?.id)
        assertEquals("Bitcoin", result?.name)
        assertEquals("BTC", result?.symbol)
        assertEquals(null, result?.code)
    }

    @Test
    fun `getCryptoById returns null when not found`() = runTest {
        // Given
        coEvery { cryptoDao.getCryptoById("UNKNOWN") } returns null

        // When
        val result = repository.getCryptoById("UNKNOWN")

        // Then
        assertEquals(null, result)
    }

    @Test
    fun `insertCrypto converts domain to entity and calls dao`() = runTest {
        // Given
        val crypto = CurrencyInfo(id = "BTC", name = "Bitcoin", symbol = "BTC", code = null)
        coEvery { cryptoDao.insertCrypto(any()) } returns Unit

        // When
        repository.insertCrypto(crypto)

        // Then
        coVerify(exactly = 1) { cryptoDao.insertCrypto(any()) }
    }

    @Test
    fun `updateCrypto converts domain to entity and calls dao`() = runTest {
        // Given
        val crypto = CurrencyInfo(id = "BTC", name = "Bitcoin", symbol = "BTC", code = null)
        coEvery { cryptoDao.updateCrypto(any()) } returns Unit

        // When
        repository.updateCrypto(crypto)

        // Then
        coVerify(exactly = 1) { cryptoDao.updateCrypto(any()) }
    }

    @Test
    fun `deleteCrypto converts domain to entity and calls dao`() = runTest {
        // Given
        val crypto = CurrencyInfo(id = "BTC", name = "Bitcoin", symbol = "BTC", code = null)
        coEvery { cryptoDao.deleteCrypto(any()) } returns Unit

        // When
        repository.deleteCrypto(crypto)

        // Then
        coVerify(exactly = 1) { cryptoDao.deleteCrypto(any()) }
    }

    @Test
    fun `clearAllCryptos calls dao`() = runTest {
        // Given
        coEvery { cryptoDao.clearAllCryptos() } returns Unit

        // When
        repository.clearAllCryptos()

        // Then
        coVerify(exactly = 1) { cryptoDao.clearAllCryptos() }
    }
}

