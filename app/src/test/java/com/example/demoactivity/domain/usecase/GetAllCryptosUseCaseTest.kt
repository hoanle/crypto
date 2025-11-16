package com.example.demoactivity.domain.usecase

import app.cash.turbine.test
import com.example.demoactivity.domain.model.CurrencyInfo
import com.example.demoactivity.domain.repository.CryptoRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAllCryptosUseCaseTest {
    private lateinit var repository: CryptoRepository
    private lateinit var useCase: GetAllCryptosUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetAllCryptosUseCase(repository)
    }

    @Test
    fun `invoke returns flow of cryptos from repository`() = runTest {
        // Given
        val cryptos = listOf(
            CurrencyInfo(id = "BTC", name = "Bitcoin", symbol = "BTC", code = null),
            CurrencyInfo(id = "ETH", name = "Ethereum", symbol = "ETH", code = null)
        )
        coEvery { repository.getAllCryptos() } returns flowOf(cryptos)

        // When
        val result = useCase()

        // Then
        result.test {
            assertEquals(cryptos, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns empty list when repository returns empty list`() = runTest {
        // Given
        coEvery { repository.getAllCryptos() } returns flowOf(emptyList())

        // When
        val result = useCase()

        // Then
        result.test {
            assertEquals(emptyList<CurrencyInfo>(), awaitItem())
            awaitComplete()
        }
    }
}

