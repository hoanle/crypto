package com.example.demoactivity.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CryptoDao {
    @Query("SELECT * FROM cryptos ORDER BY id ASC")
    fun getAllCryptos(): Flow<List<CryptoEntity>>

    @Query("SELECT * FROM cryptos ORDER BY name ASC")
    fun getAllCryptosPaged(): PagingSource<Int, CryptoEntity>

    @Query("""
        SELECT * FROM cryptos 
        WHERE LOWER(name) LIKE LOWER(:query) || '%' 
           OR LOWER(name) LIKE '% ' || LOWER(:query) || '%'
           OR LOWER(symbol) LIKE LOWER(:query) || '%'
        ORDER BY 
            CASE 
                WHEN LOWER(name) LIKE LOWER(:query) || '%' THEN 1
                WHEN LOWER(symbol) LIKE LOWER(:query) || '%' THEN 2
                ELSE 3
            END,
            name ASC
    """)
    fun searchCryptos(query: String): Flow<List<CryptoEntity>>

    @Query("""
        SELECT * FROM cryptos 
        WHERE LOWER(name) LIKE LOWER(:query) || '%' 
           OR LOWER(name) LIKE '% ' || LOWER(:query) || '%'
           OR LOWER(symbol) LIKE LOWER(:query) || '%'
        ORDER BY 
            CASE 
                WHEN LOWER(name) LIKE LOWER(:query) || '%' THEN 1
                WHEN LOWER(symbol) LIKE LOWER(:query) || '%' THEN 2
                ELSE 3
            END,
            name ASC
    """)
    fun searchCryptosPaged(query: String): PagingSource<Int, CryptoEntity>

    @Query("SELECT * FROM cryptos WHERE id = :id")
    suspend fun getCryptoById(id: String): CryptoEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrypto(crypto: CryptoEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCryptos(cryptos: List<CryptoEntity>)

    @Transaction
    suspend fun insertCryptosBatch(cryptos: List<CryptoEntity>) {
        insertCryptos(cryptos)
    }

    @Update
    suspend fun updateCrypto(crypto: CryptoEntity)

    @Delete
    suspend fun deleteCrypto(crypto: CryptoEntity)

    @Query("DELETE FROM cryptos")
    suspend fun clearAllCryptos()
}

