package com.example.demoactivity.data.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.demoactivity.data.local.FiatEntity
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
class FiatDaoTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var fiatDao: FiatDao

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertFiat_insertsSuccessfully() = runTest {
        // Given
        val fiat = FiatEntity(id = "USD", name = "US Dollar", symbol = "$", code = "USD")

        // When
        fiatDao.insertFiat(fiat)

        // Then
        val retrieved = fiatDao.getFiatById("USD")
        assertNotNull(retrieved)
        assertEquals("USD", retrieved?.id)
        assertEquals("US Dollar", retrieved?.name)
        assertEquals("$", retrieved?.symbol)
        assertEquals("USD", retrieved?.code)
    }

    @Test
    fun getAllFiats_returnsInsertedFiats() = runTest {
        // Given
        val fiat1 = FiatEntity(id = "USD", name = "US Dollar", symbol = "$", code = "USD")
        val fiat2 = FiatEntity(id = "SGD", name = "Singapore Dollar", symbol = "$", code = "SGD")

        fiatDao.insertFiat(fiat1)
        fiatDao.insertFiat(fiat2)

        // When
        val fiats = fiatDao.getAllFiats().first()

        // Then
        assertEquals(2, fiats.size)
        assertEquals("SGD", fiats[0].id)
        assertEquals("USD", fiats[1].id)
    }

    @Test
    fun getFiatById_returnsCorrectFiat() = runTest {
        // Given
        val fiat = FiatEntity(id = "USD", name = "US Dollar", symbol = "$", code = "USD")
        fiatDao.insertFiat(fiat)

        // When
        val retrieved = fiatDao.getFiatById("USD")

        // Then
        assertNotNull(retrieved)
        assertEquals("USD", retrieved?.id)
        assertEquals("US Dollar", retrieved?.name)
        assertEquals("$", retrieved?.symbol)
        assertEquals("USD", retrieved?.code)
    }

    @Test
    fun getFiatById_returnsNullWhenNotFound() = runTest {
        // When
        val retrieved = fiatDao.getFiatById("UNKNOWN")

        // Then
        assertNull(retrieved)
    }

    @Test
    fun updateFiat_updatesExistingFiat() = runTest {
        // Given
        val fiat = FiatEntity(id = "USD", name = "US Dollar", symbol = "$", code = "USD")
        fiatDao.insertFiat(fiat)

        // When
        val updated = FiatEntity(id = "USD", name = "US Dollar Updated", symbol = "$", code = "USD")
        fiatDao.updateFiat(updated)

        // Then
        val retrieved = fiatDao.getFiatById("USD")
        assertNotNull(retrieved)
        assertEquals("US Dollar Updated", retrieved?.name)
    }

    @Test
    fun deleteFiat_removesFiat() = runTest {
        // Given
        val fiat = FiatEntity(id = "USD", name = "US Dollar", symbol = "$", code = "USD")
        fiatDao.insertFiat(fiat)

        // When
        fiatDao.deleteFiat(fiat)

        // Then
        val retrieved = fiatDao.getFiatById("USD")
        assertNull(retrieved)
    }

    @Test
    fun searchFiats_returnsMatchingFiatsByName() = runTest {
        // Given
        val fiat1 = FiatEntity(id = "USD", name = "US Dollar", symbol = "$", code = "USD")
        val fiat2 = FiatEntity(id = "SGD", name = "Singapore Dollar", symbol = "$", code = "SGD")
        fiatDao.insertFiat(fiat1)
        fiatDao.insertFiat(fiat2)

        // When
        val results = fiatDao.searchFiats("US").first()

        // Then
        assertEquals(1, results.size)
        assertEquals("USD", results[0].id)
    }

    @Test
    fun searchFiats_returnsMatchingFiatsBySymbol() = runTest {
        // Given
        val fiat1 = FiatEntity(id = "EUR", name = "Euro", symbol = "€", code = "EUR")
        val fiat2 = FiatEntity(id = "SGD", name = "Singapore Dollar", symbol = "$", code = "SGD")
        fiatDao.insertFiat(fiat1)
        fiatDao.insertFiat(fiat2)

        // When - search for "€" which is the symbol for EUR
        val results = fiatDao.searchFiats("€").first()

        // Then
        assertEquals(1, results.size)
        assertEquals("EUR", results[0].id)
    }

    @Test
    fun clearAllFiats_removesAllFiats() = runTest {
        // Given
        val fiat1 = FiatEntity(id = "USD", name = "US Dollar", symbol = "$", code = "USD")
        val fiat2 = FiatEntity(id = "SGD", name = "Singapore Dollar", symbol = "$", code = "SGD")
        fiatDao.insertFiat(fiat1)
        fiatDao.insertFiat(fiat2)

        // When
        fiatDao.clearAllFiats()

        // Then
        val fiats = fiatDao.getAllFiats().first()
        assertEquals(0, fiats.size)
    }
}

