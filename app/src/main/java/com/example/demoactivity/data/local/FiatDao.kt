package com.example.demoactivity.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FiatDao {
    @Query("SELECT * FROM fiats ORDER BY id ASC")
    fun getAllFiats(): Flow<List<FiatEntity>>

    @Query("""
        SELECT * FROM fiats 
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
    fun searchFiats(query: String): Flow<List<FiatEntity>>

    @Query("SELECT * FROM fiats WHERE id = :id")
    suspend fun getFiatById(id: String): FiatEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFiat(fiat: FiatEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFiats(fiats: List<FiatEntity>)

    @Transaction
    suspend fun insertFiatsBatch(fiats: List<FiatEntity>) {
        insertFiats(fiats)
    }

    @Update
    suspend fun updateFiat(fiat: FiatEntity)

    @Delete
    suspend fun deleteFiat(fiat: FiatEntity)

    @Query("DELETE FROM fiats")
    suspend fun clearAllFiats()
}

