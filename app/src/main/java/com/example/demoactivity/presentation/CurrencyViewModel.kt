package com.example.demoactivity.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.demoactivity.domain.model.CurrencyInfo
import com.example.demoactivity.domain.repository.CombinedCurrencyRepository
import com.example.demoactivity.domain.repository.CryptoRepository
import com.example.demoactivity.domain.repository.FiatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)

data class CurrencyUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val cryptoRepository: CryptoRepository,
    private val fiatRepository: FiatRepository,
    private val combinedCurrencyRepository: CombinedCurrencyRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    /**
     * Flow of paginated cryptos.
     * Automatically updates when search query changes.
     */
    val cryptosPaged: Flow<PagingData<CurrencyInfo>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                cryptoRepository.getAllCryptosPaged()
            } else {
                cryptoRepository.searchCryptosPaged(query)
            }
        }
        .cachedIn(viewModelScope)

    /**
     * Flow of paginated fiats.
     * Automatically updates when search query changes.
     */
    val fiatsPaged: Flow<PagingData<CurrencyInfo>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                fiatRepository.getAllFiatsPaged()
            } else {
                fiatRepository.searchFiatsPaged(query)
            }
        }
        .cachedIn(viewModelScope)

    /**
     * Flow of paginated combined currencies (both cryptos and fiats).
     * Automatically updates when search query changes.
     */
    val combinedCurrenciesPaged: Flow<PagingData<CurrencyInfo>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                combinedCurrencyRepository.getAllCombinedCurrenciesPaged()
            } else {
                combinedCurrencyRepository.searchCombinedCurrenciesPaged(query)
            }
        }
        .cachedIn(viewModelScope)

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
        _uiState.value = _uiState.value.copy(searchQuery = "")
    }
}
