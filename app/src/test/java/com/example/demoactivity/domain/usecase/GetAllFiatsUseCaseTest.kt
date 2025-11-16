package com.example.demoactivity.domain.usecase

import app.cash.turbine.test
import com.example.demoactivity.domain.model.CurrencyInfo
import com.example.demoactivity.domain.repository.FiatRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAllFiatsUseCaseTest {
    private lateinit var repository: FiatRepository
    private lateinit var useCase: GetAllFiatsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetAllFiatsUseCase(repository)
    }

    @Test
    fun `invoke returns flow of fiats from repository`() = runTest {
        // Given
        val fiats = listOf(
            CurrencyInfo(id = "USD", name = "US Dollar", symbol = "$", code = "USD"),
            CurrencyInfo(id = "SGD", name = "Singapore Dollar", symbol = "$", code = "SGD")
        )
        coEvery { repository.getAllFiats() } returns flowOf(fiats)

        // When
        val result = useCase()

        // Then
        result.test {
            assertEquals(fiats, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns empty list when repository returns empty list`() = runTest {
        // Given
        coEvery { repository.getAllFiats() } returns flowOf(emptyList())

        // When
        val result = useCase()

        // Then
        result.test {
            assertEquals(emptyList<CurrencyInfo>(), awaitItem())
            awaitComplete()
        }
    }
}

