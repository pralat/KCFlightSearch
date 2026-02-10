package com.example.kcflightsearch.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kcflightsearch.data.model.Airport
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {

    @Query("SELECT * FROM airport ORDER BY name ASC")
    fun getAllAirports(): Flow<List<Airport>>

    @Query("""
        SELECT * FROM airport
        WHERE name LIKE '%' || :query || '%'
        OR iata_code LIKE '%' || :query || '%'
        ORDER BY
            CASE
                WHEN name LIKE :query || '%' THEN 1
                WHEN iata_code LIKE :query || '%' THEN 2
                ELSE 3
            END,
            name ASC
        LIMIT 10
    """)
    fun searchAirports(query: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code = :code")
    suspend fun getAirportByCode(code: String): Airport?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(airports: List<Airport>)
}
