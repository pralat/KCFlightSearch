package com.example.kcflightsearch.data.repository

import com.example.kcflightsearch.data.local.AirportDao
import com.example.kcflightsearch.data.local.DestinationAirport
import com.example.kcflightsearch.data.local.FavoriteDao
import com.example.kcflightsearch.data.local.FavoriteRoute
import com.example.kcflightsearch.data.model.Airport
import com.example.kcflightsearch.data.model.Favorite
import kotlinx.coroutines.flow.Flow

class FlightSearchRepository(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao
) {

    fun getAllAirports(): Flow<List<Airport>> = airportDao.getAllAirports()

    fun searchAirports(query: String): Flow<List<Airport>> = airportDao.searchAirports(query)

    suspend fun getAirportByCode(code: String): Airport? = airportDao.getAirportByCode(code)

    fun getDestinationsForDeparture(departureCode: String): Flow<List<DestinationAirport>> =
        favoriteDao.getDestinationsForDeparture(departureCode)

    // Favorite operations
    fun getAllFavoriteRoutes(): Flow<List<FavoriteRoute>> =
        favoriteDao.getAllFavoriteRoutes()

    fun getFavoritesForDeparture(departureCode: String): Flow<List<Favorite>> =
        favoriteDao.getFavoritesForDeparture(departureCode)

    fun isFavorite(departureCode: String, destinationCode: String): Flow<Boolean> =
        favoriteDao.isFavorite(departureCode, destinationCode)

    suspend fun addFavorite(favorite: Favorite) {
        favoriteDao.insertFavorite(favorite)
    }

    suspend fun removeFavorite(departureCode: String, destinationCode: String) {
        favoriteDao.deleteFavoriteByCodes(departureCode, destinationCode)
    }
}
