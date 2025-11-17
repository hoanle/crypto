package com.example.demoactivity.data.local

import androidx.room.DatabaseView
import com.example.demoactivity.domain.model.CurrencyInfo

/**
 * Database view that combines results from both Crypto and Fiat tables using UNION.
 * This allows efficient querying and pagination at the database level.
 * 
 * Schema:
 * - id: Currency identifier
 * - name: Currency name
 * - symbol: Currency symbol
 * - code: Currency code (NULL for cryptos, actual code for fiats)
 */
@DatabaseView(
    viewName = "combined_currencies",
    value = "SELECT id, name, symbol, NULL as code FROM cryptos UNION ALL SELECT id, name, symbol, code FROM fiats"
)
data class CombinedCurrencyView(
    val id: String,
    val name: String,
    val symbol: String,
    val code: String?
) {
    fun toDomain(): CurrencyInfo = CurrencyInfo(
        id = id,
        name = name,
        symbol = symbol,
        code = code
    )
}

