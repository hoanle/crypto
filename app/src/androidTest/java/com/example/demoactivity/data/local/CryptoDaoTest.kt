package com.example.demoactivity.data.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.demoactivity.data.local.CryptoEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CryptoDaoTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var cryptoDao: CryptoDao

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertCrypto_insertsSuccessfully() = runTest {
        // Given
        val crypto = CryptoEntity(id = "BTC", name = "Bitcoin", symbol = "BTC")

        // When
        cryptoDao.insertCrypto(crypto)

        // Then
        val retrieved = cryptoDao.getCryptoById("BTC")
        assertNotNull(retrieved)
        assertEquals("BTC", retrieved?.id)
        assertEquals("Bitcoin", retrieved?.name)
        assertEquals("BTC", retrieved?.symbol)
    }

    @Test
    fun getAllCryptos_returnsInsertedCryptos() = runTest {
        // Given
        val crypto1 = CryptoEntity(id = "BTC", name = "Bitcoin", symbol = "BTC")
        val crypto2 = CryptoEntity(id = "ETH", name = "Ethereum", symbol = "ETH")

        cryptoDao.insertCrypto(crypto1)
        cryptoDao.insertCrypto(crypto2)

        // When
        val cryptos = cryptoDao.getAllCryptos().first()

        // Then
        assertEquals(2, cryptos.size)
        assertEquals("BTC", cryptos[0].id)
        assertEquals("ETH", cryptos[1].id)
    }

    @Test
    fun getCryptoById_returnsCorrectCrypto() = runTest {
        // Given
        val crypto = CryptoEntity(id = "BTC", name = "Bitcoin", symbol = "BTC")
        cryptoDao.insertCrypto(crypto)

        // When
        val retrieved = cryptoDao.getCryptoById("BTC")

        // Then
        assertNotNull(retrieved)
        assertEquals("BTC", retrieved?.id)
        assertEquals("Bitcoin", retrieved?.name)
        assertEquals("BTC", retrieved?.symbol)
    }

    @Test
    fun getCryptoById_returnsNullWhenNotFound() = runTest {
        // When
        val retrieved = cryptoDao.getCryptoById("UNKNOWN")

        // Then
        assertNull(retrieved)
    }

    @Test
    fun updateCrypto_updatesExistingCrypto() = runTest {
        // Given
        val crypto = CryptoEntity(id = "BTC", name = "Bitcoin", symbol = "BTC")
        cryptoDao.insertCrypto(crypto)

        // When
        val updated = CryptoEntity(id = "BTC", name = "Bitcoin Updated", symbol = "BTC")
        cryptoDao.updateCrypto(updated)

        // Then
        val retrieved = cryptoDao.getCryptoById("BTC")
        assertNotNull(retrieved)
        assertEquals("Bitcoin Updated", retrieved?.name)
    }

    @Test
    fun deleteCrypto_removesCrypto() = runTest {
        // Given
        val crypto = CryptoEntity(id = "BTC", name = "Bitcoin", symbol = "BTC")
        cryptoDao.insertCrypto(crypto)

        // When
        cryptoDao.deleteCrypto(crypto)

        // Then
        val retrieved = cryptoDao.getCryptoById("BTC")
        assertNull(retrieved)
    }

    @Test
    fun searchCryptos_returnsMatchingCryptosByName() = runTest {
        // Given
        val crypto1 = CryptoEntity(id = "BTC", name = "Bitcoin", symbol = "BTC")
        val crypto2 = CryptoEntity(id = "ETH", name = "Ethereum", symbol = "ETH")
        cryptoDao.insertCrypto(crypto1)
        cryptoDao.insertCrypto(crypto2)

        // When
        val results = cryptoDao.searchCryptos("Bit").first()

        // Then
        assertEquals(1, results.size)
        assertEquals("BTC", results[0].id)
    }

    @Test
    fun searchCryptos_returnsMatchingCryptosBySymbol() = runTest {
        // Given
        val crypto1 = CryptoEntity(id = "BTC", name = "Bitcoin", symbol = "BTC")
        val crypto2 = CryptoEntity(id = "ETH", name = "Ethereum", symbol = "ETH")
        cryptoDao.insertCrypto(crypto1)
        cryptoDao.insertCrypto(crypto2)

        // When
        val results = cryptoDao.searchCryptos("ETH").first()

        // Then
        assertEquals(1, results.size)
        assertEquals("ETH", results[0].id)
    }

    @Test
    fun clearAllCryptos_removesAllCryptos() = runTest {
        // Given
        val crypto1 = CryptoEntity(id = "BTC", name = "Bitcoin", symbol = "BTC")
        val crypto2 = CryptoEntity(id = "ETH", name = "Ethereum", symbol = "ETH")
        cryptoDao.insertCrypto(crypto1)
        cryptoDao.insertCrypto(crypto2)

        // When
        cryptoDao.clearAllCryptos()

        // Then
        val cryptos = cryptoDao.getAllCryptos().first()
        assertEquals(0, cryptos.size)
    }
}

