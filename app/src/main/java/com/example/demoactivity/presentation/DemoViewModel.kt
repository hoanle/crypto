package com.example.demoactivity.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoactivity.R
import com.example.demoactivity.data.seed.SeedError
import com.example.demoactivity.data.seed.SeedDatabase
import com.example.demoactivity.domain.repository.CryptoRepository
import com.example.demoactivity.domain.repository.FiatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DemoViewModel @Inject constructor(
    private val seedDatabase: SeedDatabase,
    private val fiatRepository: FiatRepository,
    private val cryptoRepository: CryptoRepository
) : ViewModel() {

    fun clearDatabase(onSuccess: (Int) -> Unit, onError: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                fiatRepository.clearAllFiats()
                cryptoRepository.clearAllCryptos()
                withContext(Dispatchers.Main) {
                    onSuccess(R.string.database_cleared_success)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(R.string.error_clear_database)
                }
            }
        }
    }

    fun insertToDatabase(onSuccess: (Int) -> Unit, onError: (Int, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val fiatResult = seedDatabase.seedFiats()
                val cryptoResult = seedDatabase.seedCryptos()

                fiatResult.getOrElse { error ->
                    withContext(Dispatchers.Main) {
                        val errorMessage = if (error is SeedError) {
                            null
                        } else {
                            error.message
                        }
                        onError(
                            if (error is SeedError) error.resourceId else R.string.error_insert_fiats,
                            errorMessage
                        )
                    }
                    return@launch
                }

                cryptoResult.getOrElse { error ->
                    withContext(Dispatchers.Main) {
                        val errorMessage = if (error is SeedError) {
                            null
                        } else {
                            error.message
                        }
                        onError(
                            if (error is SeedError) error.resourceId else R.string.error_insert_cryptos,
                            errorMessage
                        )
                    }
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    onSuccess(R.string.data_inserted_success)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(R.string.error_insert_data, e.message)
                }
            }
        }
    }
}

