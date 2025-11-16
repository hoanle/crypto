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
                _uiState.value = _uiState.value.copy(
                    cryptos = cryptos,
                    fiats = fiats,
                    currencies = cryptos + fiats,
                    isLoading = false
                )
            }.collect {}
        }
    }
}
