package com.example.demoactivity.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.demoactivity.domain.model.CurrencyInfo

@Entity(tableName = "fiats")
data class FiatEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val symbol: String,
    val code: String
) {
    fun toDomain(): CurrencyInfo = CurrencyInfo(
        id = id,
        name = name,
        symbol = symbol,
        code = code
    )
}

fun CurrencyInfo.toFiatEntity(): FiatEntity = FiatEntity(
    id = id,
    name = name,
    symbol = symbol,
    code = code ?: ""
)

