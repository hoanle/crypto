package com.example.demoactivity.data.seed

import android.content.Context
import android.util.Log
import com.example.demoactivity.R
import com.example.demoactivity.domain.model.CurrencyInfo
import com.example.demoactivity.domain.repository.CryptoRepository
import com.example.demoactivity.domain.repository.FiatRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

sealed class SeedError(val resourceId: Int, val throwable: Throwable? = null) : Exception() {
    class NoFiatData(throwable: Throwable? = null) : SeedError(R.string.error_no_fiat_data, throwable)
    class NoCryptoData(throwable: Throwable? = null) : SeedError(R.string.error_no_crypto_data, throwable)
}

@Singleton
class SeedDatabase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fiatRepository: FiatRepository,
    private val cryptoRepository: CryptoRepository
) {
    companion object {
        private const val TAG = "SeedDatabase"
        private const val BATCH_SIZE_THRESHOLD = 50 // Threshold for batch processing
        private const val BATCH_SIZE = 100 // Batch size for large datasets
    }

    /**
     * Seeds Fiat data from fiat.json asset file.
     * Handles both limited and large datasets automatically.
     */
    suspend fun seedFiats(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val fiats = readFiatsFromAssets()
            if (fiats.isEmpty()) {
                Log.w(TAG, "No fiat data found in assets")
                return@withContext Result.failure(SeedError.NoFiatData())
            }

            val insertedCount = if (fiats.size <= BATCH_SIZE_THRESHOLD) {
                insertFiatsLimited(fiats)
            } else {
                insertFiatsBig(fiats)
            }

            Log.d(TAG, "Successfully seeded $insertedCount fiats")
            Result.success(insertedCount)
        } catch (e: SeedError) {
            Log.e(TAG, "Error seeding fiats", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding fiats", e)
            Result.failure(SeedError.NoFiatData(e))
        }
    }

    /**
     * Seeds Crypto data from crypto.json asset file.
     * Handles both limited and large datasets automatically.
     */
    suspend fun seedCryptos(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val cryptos = readCryptosFromAssets()
            if (cryptos.isEmpty()) {
                Log.w(TAG, "No crypto data found in assets")
                return@withContext Result.failure(SeedError.NoCryptoData())
            }

            val insertedCount = if (cryptos.size <= BATCH_SIZE_THRESHOLD) {
                insertCryptosLimited(cryptos)
            } else {
                insertCryptosBig(cryptos)
            }

            Log.d(TAG, "Successfully seeded $insertedCount cryptos")
            Result.success(insertedCount)
        } catch (e: SeedError) {
            Log.e(TAG, "Error seeding cryptos", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding cryptos", e)
            Result.failure(SeedError.NoCryptoData(e))
        }
    }

    /**
     * Reads Fiat data from fiat.json asset file.
     */
    private suspend fun readFiatsFromAssets(): List<CurrencyInfo> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("fiat.json")
                .bufferedReader()
                .use { it.readText() }

            val jsonArray = JSONArray(jsonString)
            val fiats = mutableListOf<CurrencyInfo>()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val fiat = CurrencyInfo(
                    id = jsonObject.getString("id"),
                    name = jsonObject.getString("name"),
                    symbol = jsonObject.getString("symbol"),
                    code = jsonObject.getString("code")
                )
                fiats.add(fiat)
            }

            fiats
        } catch (e: IOException) {
            Log.e(TAG, "Error reading fiat.json from assets", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing fiat.json", e)
            emptyList()
        }
    }

    /**
     * Reads Crypto data from crypto.json asset file.
     */
    private suspend fun readCryptosFromAssets(): List<CurrencyInfo> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("crypto.json")
                .bufferedReader()
                .use { it.readText() }

            val jsonArray = JSONArray(jsonString)
            val cryptos = mutableListOf<CurrencyInfo>()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val crypto = CurrencyInfo(
                    id = jsonObject.getString("id"),
                    name = jsonObject.getString("name"),
                    symbol = jsonObject.getString("symbol"),
                    code = null
                )
                cryptos.add(crypto)
            }

            cryptos
        } catch (e: IOException) {
            Log.e(TAG, "Error reading crypto.json from assets", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing crypto.json", e)
            emptyList()
        }
    }

    /**
     * Inserts limited amount of Fiat items (batch insert with transaction).
     * Used when the dataset is small (<= BATCH_SIZE_THRESHOLD).
     * Uses Room transaction for atomicity and better performance.
     */
    private suspend fun insertFiatsLimited(fiats: List<CurrencyInfo>): Int {
        return try {
            fiatRepository.insertFiats(fiats)
            Log.d(TAG, "Successfully inserted ${fiats.size} fiats in transaction")
            fiats.size
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert fiats batch", e)
            0
        }
    }

    /**
     * Inserts big amount of Fiat items (batch insert with transactions).
     * Used when the dataset is large (> BATCH_SIZE_THRESHOLD).
     * Processes items in batches using Room transactions for better performance and atomicity.
     */
    private suspend fun insertFiatsBig(fiats: List<CurrencyInfo>): Int {
        var totalInserted = 0
        val batches = fiats.chunked(BATCH_SIZE)

        for (batch in batches) {
            try {
                fiatRepository.insertFiats(batch)
                totalInserted += batch.size
                Log.d(TAG, "Inserted batch of ${batch.size} fiats (total: $totalInserted/${fiats.size})")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to insert fiat batch", e)
                // Continue with next batch even if one fails
            }
        }

        return totalInserted
    }

    /**
     * Inserts limited amount of Crypto items (batch insert with transaction).
     * Used when the dataset is small (<= BATCH_SIZE_THRESHOLD).
     * Uses Room transaction for atomicity and better performance.
     */
    private suspend fun insertCryptosLimited(cryptos: List<CurrencyInfo>): Int {
        return try {
            cryptoRepository.insertCryptos(cryptos)
            Log.d(TAG, "Successfully inserted ${cryptos.size} cryptos in transaction")
            cryptos.size
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert cryptos batch", e)
            0
        }
    }

    /**
     * Inserts big amount of Crypto items (batch insert with transactions).
     * Used when the dataset is large (> BATCH_SIZE_THRESHOLD).
     * Processes items in batches using Room transactions for better performance and atomicity.
     */
    private suspend fun insertCryptosBig(cryptos: List<CurrencyInfo>): Int {
        var totalInserted = 0
        val batches = cryptos.chunked(BATCH_SIZE)

        for (batch in batches) {
            try {
                cryptoRepository.insertCryptos(batch)
                totalInserted += batch.size
                Log.d(TAG, "Inserted batch of ${batch.size} cryptos (total: $totalInserted/${cryptos.size})")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to insert crypto batch", e)
                // Continue with next batch even if one fails
            }
        }

        return totalInserted
    }
}

