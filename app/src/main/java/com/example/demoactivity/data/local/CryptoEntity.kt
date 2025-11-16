package com.example.demoactivity.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.demoactivity.domain.model.CurrencyInfo

@Entity(tableName = "cryptos")
data class CryptoEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val symbol: String
) {
    fun toDomain(): CurrencyInfo = CurrencyInfo(
        id = id,
        name = name,
        symbol = symbol,
        code = null
    )
}

fun CurrencyInfo.toCryptoEntity(): CryptoEntity = CryptoEntity(
    id = id,
    name = name,
    symbol = symbol
)

