package com.example.demoactivity.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoactivity.domain.model.CurrencyInfo
import com.example.demoactivity.domain.usecase.GetAllCryptosUseCase
import com.example.demoactivity.domain.usecase.GetAllFiatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CurrencyUiState(
    val currencies: List<CurrencyInfo> = emptyList(),
    val cryptos: List<CurrencyInfo> = emptyList(),
    val fiats: List<CurrencyInfo> = emptyList(),
    val filteredCurrencies: List<CurrencyInfo> = emptyList(),
    val filteredCryptos: List<CurrencyInfo> = emptyList(),
    val filteredFiats: List<CurrencyInfo> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val getAllCryptosUseCase: GetAllCryptosUseCase,
    private val getAllFiatsUseCase: GetAllFiatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    init {
        loadCurrencies()
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            combine(
                getAllCryptosUseCase(),
                getAllFiatsUseCase()
            ) { cryptos, fiats ->
                val allCurrencies = cryptos + fiats
                val query = _uiState.value.searchQuery
                _uiState.value = _uiState.value.copy(
                    cryptos = cryptos,
                    fiats = fiats,
                    currencies = allCurrencies,
                    filteredCurrencies = filterCurrencies(allCurrencies, query),
                    filteredCryptos = filterCurrencies(cryptos, query),
                    filteredFiats = filterCurrencies(fiats, query),
                    isLoading = false
                )
            }.collect {}
        }
    }

    fun updateSearchQuery(query: String) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            searchQuery = query,
            filteredCurrencies = filterCurrencies(currentState.currencies, query),
            filteredCryptos = filterCurrencies(currentState.cryptos, query),
            filteredFiats = filterCurrencies(currentState.fiats, query)
        )
    }

    /**
     * Filters currencies based on search query with improved matching rules.
     * 
     * Matching rules:
     * 1. Currency name starts with the search term
     * 2. Currency name contains a space-prefixed partial match
     * 3. Currency symbol starts with the search term
     */
    private fun filterCurrencies(
        currencies: List<CurrencyInfo>,
        query: String
    ): List<CurrencyInfo> {
        if (query.isBlank()) {
            return currencies
        }

        val searchQuery = query.lowercase().trim()
        return currencies.filter { currency ->
            val nameLower = currency.name.lowercase()
            val symbolLower = currency.symbol.lowercase()

            // Rule 1: Currency name starts with the search term
            nameLower.startsWith(searchQuery) ||
            // Rule 2: Currency name contains a space-prefixed partial match
            nameLower.contains(" $searchQuery") ||
            // Rule 3: Currency symbol starts with the search term
            symbolLower.startsWith(searchQuery)
        }
    }
}
