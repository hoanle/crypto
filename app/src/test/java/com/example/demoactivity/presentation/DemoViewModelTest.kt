package com.example.demoactivity.presentation

import com.example.demoactivity.R
import com.example.demoactivity.data.seed.SeedDatabase
import com.example.demoactivity.data.seed.SeedError
import com.example.demoactivity.domain.repository.CryptoRepository
import com.example.demoactivity.domain.repository.FiatRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class DemoViewModelTest {
    private lateinit var seedDatabase: SeedDatabase
    private lateinit var fiatRepository: FiatRepository
    private lateinit var cryptoRepository: CryptoRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        seedDatabase = mockk()
        fiatRepository = mockk()
        cryptoRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): DemoViewModel {
        return DemoViewModel(seedDatabase, fiatRepository, cryptoRepository)
    }

    @Test
    fun `clearDatabase calls both repositories and invokes onSuccess`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        var successResId: Int? = null
        var errorResId: Int? = null
        coEvery { fiatRepository.clearAllFiats() } returns Unit
        coEvery { cryptoRepository.clearAllCryptos() } returns Unit

        // When
        viewModel.clearDatabase(
            onSuccess = { successResId = it },
            onError = { errorResId = it }
        )
        advanceUntilIdle()

        // Then
        assertEquals(R.string.database_cleared_success, successResId)
        assertEquals(null, errorResId)
        coVerify(exactly = 1) { fiatRepository.clearAllFiats() }
        coVerify(exactly = 1) { cryptoRepository.clearAllCryptos() }
    }

    @Test
    fun `clearDatabase invokes onError when fiat repository fails`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        var successResId: Int? = null
        var errorResId: Int? = null
        val exception = Exception("Fiat clear failed")
        coEvery { fiatRepository.clearAllFiats() } throws exception
        coEvery { cryptoRepository.clearAllCryptos() } returns Unit

        // When
        viewModel.clearDatabase(
            onSuccess = { successResId = it },
            onError = { errorResId = it }
        )
        advanceUntilIdle()

        // Then
        assertEquals(null, successResId)
        assertEquals(R.string.error_clear_database, errorResId)
    }

    @Test
    fun `clearDatabase invokes onError when crypto repository fails`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        var successResId: Int? = null
        var errorResId: Int? = null
        val exception = Exception("Crypto clear failed")
        coEvery { fiatRepository.clearAllFiats() } returns Unit
        coEvery { cryptoRepository.clearAllCryptos() } throws exception

        // When
        viewModel.clearDatabase(
            onSuccess = { successResId = it },
            onError = { errorResId = it }
        )
        advanceUntilIdle()

        // Then
        assertEquals(null, successResId)
        assertEquals(R.string.error_clear_database, errorResId)
    }

    @Test
    fun `insertToDatabase calls seedDatabase and invokes onSuccess when both succeed`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        var successResId: Int? = null
        var errorResId: Int? = null
        var errorMessage: String? = null
        coEvery { seedDatabase.seedFiats() } returns Result.success(10)
        coEvery { seedDatabase.seedCryptos() } returns Result.success(20)

        // When
        viewModel.insertToDatabase(
            onSuccess = { successResId = it },
            onError = { resId, msg -> errorResId = resId; errorMessage = msg }
        )
        advanceUntilIdle()

        // Then
        assertEquals(R.string.data_inserted_success, successResId)
        assertEquals(null, errorResId)
        assertEquals(null, errorMessage)
        coVerify(exactly = 1) { seedDatabase.seedFiats() }
        coVerify(exactly = 1) { seedDatabase.seedCryptos() }
    }

    @Test
    fun `insertToDatabase invokes onError when fiat seeding fails`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        var successResId: Int? = null
        var errorResId: Int? = null
        var errorMessage: String? = null
        val exception = Exception("Fiat seeding failed")
        coEvery { seedDatabase.seedFiats() } returns Result.failure(exception)
        coEvery { seedDatabase.seedCryptos() } returns Result.success(20)

        // When
        viewModel.insertToDatabase(
            onSuccess = { successResId = it },
            onError = { resId, msg -> errorResId = resId; errorMessage = msg }
        )
        advanceUntilIdle()

        // Then
        assertEquals(null, successResId)
        assertEquals(R.string.error_insert_fiats, errorResId)
        assertEquals("Fiat seeding failed", errorMessage)
        coVerify(exactly = 1) { seedDatabase.seedFiats() }
        // Note: seedCryptos is still called before checking fiatResult, but error is returned early
        coVerify(exactly = 1) { seedDatabase.seedCryptos() }
    }

    @Test
    fun `insertToDatabase invokes onError when crypto seeding fails`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        var successResId: Int? = null
        var errorResId: Int? = null
        var errorMessage: String? = null
        val exception = Exception("Crypto seeding failed")
        coEvery { seedDatabase.seedFiats() } returns Result.success(10)
        coEvery { seedDatabase.seedCryptos() } returns Result.failure(exception)

        // When
        viewModel.insertToDatabase(
            onSuccess = { successResId = it },
            onError = { resId, msg -> errorResId = resId; errorMessage = msg }
        )
        advanceUntilIdle()

        // Then
        assertEquals(null, successResId)
        assertEquals(R.string.error_insert_cryptos, errorResId)
        assertEquals("Crypto seeding failed", errorMessage)
        coVerify(exactly = 1) { seedDatabase.seedFiats() }
        coVerify(exactly = 1) { seedDatabase.seedCryptos() }
    }

    @Test
    fun `insertToDatabase invokes onError when exception is thrown`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        var successResId: Int? = null
        var errorResId: Int? = null
        var errorMessage: String? = null
        val exception = Exception("Unexpected error")
        coEvery { seedDatabase.seedFiats() } throws exception

        // When
        viewModel.insertToDatabase(
            onSuccess = { successResId = it },
            onError = { resId, msg -> errorResId = resId; errorMessage = msg }
        )
        advanceUntilIdle()

        // Then
        assertEquals(null, successResId)
        assertEquals(R.string.error_insert_data, errorResId)
        assertEquals("Unexpected error", errorMessage)
    }

    @Test
    fun `insertToDatabase invokes onError with SeedError when fiat seeding returns SeedError`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        var successResId: Int? = null
        var errorResId: Int? = null
        var errorMessage: String? = null
        val seedError = SeedError.NoFiatData()
        coEvery { seedDatabase.seedFiats() } returns Result.failure(seedError)
        coEvery { seedDatabase.seedCryptos() } returns Result.success(20)

        // When
        viewModel.insertToDatabase(
            onSuccess = { successResId = it },
            onError = { resId, msg -> errorResId = resId; errorMessage = msg }
        )
        advanceUntilIdle()

        // Then
        assertEquals(null, successResId)
        assertEquals(R.string.error_no_fiat_data, errorResId)
        assertEquals(null, errorMessage)
    }

    @Test
    fun `insertToDatabase invokes onError with SeedError when crypto seeding returns SeedError`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        var successResId: Int? = null
        var errorResId: Int? = null
        var errorMessage: String? = null
        val seedError = SeedError.NoCryptoData()
        coEvery { seedDatabase.seedFiats() } returns Result.success(10)
        coEvery { seedDatabase.seedCryptos() } returns Result.failure(seedError)

        // When
        viewModel.insertToDatabase(
            onSuccess = { successResId = it },
            onError = { resId, msg -> errorResId = resId; errorMessage = msg }
        )
        advanceUntilIdle()

        // Then
        assertEquals(null, successResId)
        assertEquals(R.string.error_no_crypto_data, errorResId)
        assertEquals(null, errorMessage)
    }
}

