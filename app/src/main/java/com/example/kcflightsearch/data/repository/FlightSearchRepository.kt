package com.example.kcflightsearch.data.repository

import com.example.kcflightsearch.data.local.AirportDao
import com.example.kcflightsearch.data.local.DestinationAirport
import com.example.kcflightsearch.data.local.FlightRouteDao
import com.example.kcflightsearch.data.model.Airport
import kotlinx.coroutines.flow.Flow

class FlightSearchRepository(
    private val airportDao: AirportDao,
    private val flightRouteDao: FlightRouteDao
) {

    fun getAllAirports(): Flow<List<Airport>> = airportDao.getAllAirports()

    fun searchAirports(query: String): Flow<List<Airport>> = airportDao.searchAirports(query)

    suspend fun getAirportByCode(code: String): Airport? = airportDao.getAirportByCode(code)

    fun getDestinationsForDeparture(departureCode: String): Flow<List<DestinationAirport>> =
        flightRouteDao.getDestinationsForDeparture(departureCode)

    fun getAllRoutes(): Flow<List<com.example.kcflightsearch.data.model.FlightRoute>> =
        flightRouteDao.getAllRoutes()
}
