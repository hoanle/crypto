package com.example.demoactivity.domain.usecase

import com.example.demoactivity.domain.model.CurrencyInfo
import com.example.demoactivity.domain.repository.FiatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllFiatsUseCase @Inject constructor(
    private val repository: FiatRepository
) {
    operator fun invoke(): Flow<List<CurrencyInfo>> = repository.getAllFiats()
}
