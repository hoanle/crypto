package com.example.demoactivity.domain.usecase

import com.example.demoactivity.domain.model.CurrencyInfo
import com.example.demoactivity.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCryptosUseCase @Inject constructor(
    private val repository: CryptoRepository
) {
    operator fun invoke(): Flow<List<CurrencyInfo>> = repository.getAllCryptos()
}
