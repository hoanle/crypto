package com.example.demoactivity.presentation

import app.cash.turbine.test
import androidx.paging.PagingData
import com.example.demoactivity.domain.model.CurrencyInfo
import com.example.demoactivity.domain.repository.CombinedCurrencyRepository
import com.example.demoactivity.domain.repository.CryptoRepository
import com.example.demoactivity.domain.repository.FiatRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyViewModelTest {
    private lateinit var cryptoRepository: CryptoRepository
    private lateinit var fiatRepository: FiatRepository
    private lateinit var combinedCurrencyRepository: CombinedCurrencyRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        cryptoRepository = mockk()
        fiatRepository = mockk()
        combinedCurrencyRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): CurrencyViewModel {
        every { cryptoRepository.getAllCryptosPaged() } returns flowOf(PagingData.empty())
        every { cryptoRepository.searchCryptosPaged(any()) } returns flowOf(PagingData.empty())
        every { fiatRepository.getAllFiatsPaged() } returns flowOf(PagingData.empty())
        every { fiatRepository.searchFiatsPaged(any()) } returns flowOf(PagingData.empty())
        every { combinedCurrencyRepository.getAllCombinedCurrenciesPaged() } returns flowOf(PagingData.empty())
        every { combinedCurrencyRepository.searchCombinedCurrenciesPaged(any()) } returns flowOf(PagingData.empty())
        return CurrencyViewModel(cryptoRepository, fiatRepository, combinedCurrencyRepository)
    }

    @Test
    fun `initial state has empty search query`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.searchQuery)
        }
    }

    @Test
    fun `updateSearchQuery updates search query`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.updateSearchQuery("BTC")
        advanceUntilIdle()

        // Then
        viewModel.searchQuery.test {
            assertEquals("BTC", awaitItem())
        }
    }

    @Test
    fun `clearSearchQuery resets search query to empty`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.updateSearchQuery("BTC")
        advanceUntilIdle()

        // When
        viewModel.clearSearchQuery()
        advanceUntilIdle()

        // Then
        viewModel.searchQuery.test {
            assertEquals("", awaitItem())
        }
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.searchQuery)
        }
    }
}

