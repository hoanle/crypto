package com.example.demoactivity.presentation

import app.cash.turbine.test
import com.example.demoactivity.domain.model.CurrencyInfo
import com.example.demoactivity.domain.usecase.GetAllCryptosUseCase
import com.example.demoactivity.domain.usecase.GetAllFiatsUseCase
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
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyViewModelTest {
    private lateinit var getAllCryptosUseCase: GetAllCryptosUseCase
    private lateinit var getAllFiatsUseCase: GetAllFiatsUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getAllCryptosUseCase = mockk()
        getAllFiatsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        cryptosFlow: Flow<List<CurrencyInfo>> = flowOf(emptyList()),
        fiatsFlow: Flow<List<CurrencyInfo>> = flowOf(emptyList())
    ): CurrencyViewModel {
        every { getAllCryptosUseCase() } returns cryptosFlow
        every { getAllFiatsUseCase() } returns fiatsFlow
        return CurrencyViewModel(getAllCryptosUseCase, getAllFiatsUseCase)
    }

    @Test
    fun `initial state has empty lists`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(emptyList<CurrencyInfo>(), state.cryptos)
            assertEquals(emptyList<CurrencyInfo>(), state.fiats)
            assertEquals(emptyList<CurrencyInfo>(), state.currencies)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `loadCurrencies combines cryptos and fiats correctly`() = runTest(testDispatcher) {
        // Given
        val cryptos = listOf(
            CurrencyInfo(id = "BTC", name = "Bitcoin", symbol = "BTC", code = null),
            CurrencyInfo(id = "ETH", name = "Ethereum", symbol = "ETH", code = null)
        )
        val fiats = listOf(
            CurrencyInfo(id = "USD", name = "US Dollar", symbol = "$", code = "USD"),
            CurrencyInfo(id = "SGD", name = "Singapore Dollar", symbol = "$", code = "SGD")
        )
        val viewModel = createViewModel(
            cryptosFlow = flowOf(cryptos),
            fiatsFlow = flowOf(fiats)
        )
        advanceUntilIdle()

        // When & Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(cryptos, state.cryptos)
            assertEquals(fiats, state.fiats)
            assertEquals(cryptos + fiats, state.currencies)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `currencies list is updated when cryptos change`() = runTest(testDispatcher) {
        // Given
        val initialCryptos = listOf(
            CurrencyInfo(id = "BTC", name = "Bitcoin", symbol = "BTC", code = null)
        )
        val fiats = listOf(
            CurrencyInfo(id = "USD", name = "US Dollar", symbol = "$", code = "USD")
        )
        val viewModel = createViewModel(
            cryptosFlow = flowOf(initialCryptos),
            fiatsFlow = flowOf(fiats)
        )
        advanceUntilIdle()

        // When & Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(initialCryptos.size + fiats.size, state.currencies.size)
            assertEquals(initialCryptos + fiats, state.currencies)
        }
    }
}

