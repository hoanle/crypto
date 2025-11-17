package com.example.demoactivity.data.repository

import app.cash.turbine.test
import com.example.demoactivity.data.local.FiatDao
import com.example.demoactivity.data.local.FiatEntity
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

class FiatRepositoryImplTest {
    private lateinit var fiatDao: FiatDao
    private lateinit var repository: FiatRepositoryImpl

    @Before
    fun setup() {
        fiatDao = mockk()
        repository = FiatRepositoryImpl(fiatDao)
    }

    @Test
    fun `getAllFiats returns flow of domain fiats`() = runTest {
        // Given
        val entities = listOf(
            FiatEntity(id = "USD", name = "US Dollar", symbol = "$", code = "USD"),
            FiatEntity(id = "SGD", name = "Singapore Dollar", symbol = "$", code = "SGD")
        )
        every { fiatDao.getAllFiats() } returns flowOf(entities)

        // When
        val result = repository.getAllFiats()

        // Then
        result.test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertEquals("USD", items[0].id)
            assertEquals("US Dollar", items[0].name)
            assertEquals("USD", items[0].code)
            assertEquals("SGD", items[1].id)
            assertEquals("Singapore Dollar", items[1].name)
            assertEquals("SGD", items[1].code)
            awaitComplete()
        }
    }

    @Test
    fun `searchFiats returns flow of filtered domain fiats`() = runTest {
        // Given
        val entities = listOf(
            FiatEntity(id = "USD", name = "US Dollar", symbol = "$", code = "USD")
        )
        every { fiatDao.searchFiats("USD") } returns flowOf(entities)

        // When
        val result = repository.searchFiats("USD")

        // Then
        result.test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("USD", items[0].id)
            awaitComplete()
        }
    }

    @Test
    fun `searchFiats normalizes multiple whitespaces to single space`() = runTest {
        // Given
        val entities = listOf(
            FiatEntity(id = "USD", name = "US Dollar", symbol = "$", code = "USD")
        )
        every { fiatDao.searchFiats("US Dollar") } returns flowOf(entities)

        // When - Query with multiple whitespaces between words
        val result = repository.searchFiats("US  Dollar")

        // Then - Should normalize to single space and call DAO with normalized query
        result.test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("USD", items[0].id)
            awaitComplete()
        }
        io.mockk.verify(exactly = 1) { fiatDao.searchFiats("US Dollar") }
    }

    @Test
    fun `getFiatById returns domain fiat when found`() = runTest {
        // Given
        val entity = FiatEntity(id = "USD", name = "US Dollar", symbol = "$", code = "USD")
        coEvery { fiatDao.getFiatById("USD") } returns entity

        // When
        val result = repository.getFiatById("USD")

        // Then
        assertEquals("USD", result?.id)
        assertEquals("US Dollar", result?.name)
        assertEquals("$", result?.symbol)
        assertEquals("USD", result?.code)
    }

    @Test
    fun `getFiatById returns null when not found`() = runTest {
        // Given
        coEvery { fiatDao.getFiatById("UNKNOWN") } returns null

        // When
        val result = repository.getFiatById("UNKNOWN")

        // Then
        assertEquals(null, result)
    }

    @Test
    fun `insertFiat converts domain to entity and calls dao`() = runTest {
        // Given
        val fiat = CurrencyInfo(id = "USD", name = "US Dollar", symbol = "$", code = "USD")
        coEvery { fiatDao.insertFiat(any()) } returns Unit

        // When
        repository.insertFiat(fiat)

        // Then
        coVerify(exactly = 1) { fiatDao.insertFiat(any()) }
    }

    @Test
    fun `updateFiat converts domain to entity and calls dao`() = runTest {
        // Given
        val fiat = CurrencyInfo(id = "USD", name = "US Dollar", symbol = "$", code = "USD")
        coEvery { fiatDao.updateFiat(any()) } returns Unit

        // When
        repository.updateFiat(fiat)

        // Then
        coVerify(exactly = 1) { fiatDao.updateFiat(any()) }
    }

    @Test
    fun `deleteFiat converts domain to entity and calls dao`() = runTest {
        // Given
        val fiat = CurrencyInfo(id = "USD", name = "US Dollar", symbol = "$", code = "USD")
        coEvery { fiatDao.deleteFiat(any()) } returns Unit

        // When
        repository.deleteFiat(fiat)

        // Then
        coVerify(exactly = 1) { fiatDao.deleteFiat(any()) }
    }

    @Test
    fun `clearAllFiats calls dao`() = runTest {
        // Given
        coEvery { fiatDao.clearAllFiats() } returns Unit

        // When
        repository.clearAllFiats()

        // Then
        coVerify(exactly = 1) { fiatDao.clearAllFiats() }
    }
}

