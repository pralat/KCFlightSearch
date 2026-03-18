package com.example.kcflightsearch.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kcflightsearch.data.model.Favorite
import kotlinx.coroutines.flow.Flow

data class DestinationAirport(
    val iata_code: String,
    val name: String,
    val passengers: Long
)

data class FavoriteRoute(
    val departure_code: String,
    val destination_code: String,
    val destination_name: String,
    val passengers: Long
)

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite ORDER BY departure_code ASC")
    fun getAllFavorites(): Flow<List<Favorite>>

    // Get all favorite destinations with airport details (for search screen)
    @Query("""
        SELECT favorite.departure_code, favorite.destination_code, airport.name as destination_name, airport.passengers
        FROM favorite
        INNER JOIN airport ON favorite.destination_code = airport.iata_code
        ORDER BY airport.name ASC
    """)
    fun getAllFavoriteRoutes(): Flow<List<FavoriteRoute>>

    @Query("SELECT * FROM favorite WHERE departure_code = :departureCode ORDER BY destination_code ASC")
    fun getFavoritesForDeparture(departureCode: String): Flow<List<Favorite>>

    // Get all flight routes (destinations) for a departure airport
    // All other airports are potential destinations from the selected airport
    @Query("""
        SELECT iata_code, name, passengers
        FROM airport
        WHERE iata_code != :departureCode
        ORDER BY name ASC
    """)
    fun getDestinationsForDeparture(departureCode: String): Flow<List<DestinationAirport>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode)")
    fun isFavorite(departureCode: String, destinationCode: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    suspend fun deleteFavoriteByCodes(departureCode: String, destinationCode: String)
}
