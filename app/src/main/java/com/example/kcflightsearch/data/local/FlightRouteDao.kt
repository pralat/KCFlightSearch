package com.example.kcflightsearch.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.kcflightsearch.data.model.FlightRoute
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightRouteDao {

    @Query("SELECT * FROM flight_route ORDER BY departure_code ASC")
    fun getAllRoutes(): Flow<List<FlightRoute>>

    @Query("""
        SELECT * FROM flight_route
        WHERE departure_code = :departureCode
        ORDER BY destination_code ASC
    """)
    fun getRoutesForDeparture(departureCode: String): Flow<List<FlightRoute>>

    @Query("""
        SELECT airport.iata_code, airport.name, airport.passengers
        FROM flight_route
        INNER JOIN airport ON flight_route.destination_code = airport.iata_code
        WHERE flight_route.departure_code = :departureCode
        ORDER BY airport.name ASC
    """)
    fun getDestinationsForDeparture(departureCode: String): Flow<List<DestinationAirport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(routes: List<FlightRoute>)

    @Transaction
    suspend fun insertAllAndWait(routes: List<FlightRoute>) {
        insertAll(routes)
    }
}

data class DestinationAirport(
    val iata_code: String,
    val name: String,
    val passengers: Long
)
