package com.example.kcflightsearch

import com.example.kcflightsearch.data.local.AirportDao
import com.example.kcflightsearch.data.local.DestinationAirport
import com.example.kcflightsearch.data.local.FlightRouteDao
import com.example.kcflightsearch.data.model.Airport
import com.example.kcflightsearch.data.model.FlightRoute
import com.example.kcflightsearch.data.repository.FlightSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FlightSearchRepositoryTest {

    private lateinit var repository: FlightSearchRepository
    private lateinit var fakeAirportDao: FakeAirportDao
    private lateinit var fakeFlightRouteDao: FakeFlightRouteDao

    @Before
    fun setup() {
        fakeAirportDao = FakeAirportDao()
        fakeFlightRouteDao = FakeFlightRouteDao()
        repository = FlightSearchRepository(fakeAirportDao, fakeFlightRouteDao)
    }

    @Test
    fun `getAllAirports returns all airports from dao`() = runTest {
        val airports = listOf(
            Airport("JFK", "John F. Kennedy International", 50000000),
            Airport("LAX", "Los Angeles International", 40000000)
        )
        fakeAirportDao.setAirports(airports)

        val result = repository.getAllAirports().first()

        assertEquals(2, result.size)
        assertEquals("JFK", result[0].iataCode)
        assertEquals("LAX", result[1].iataCode)
    }

    @Test
    fun `searchAirports with query returns matching airports`() = runTest {
        val airports = listOf(
            Airport("JFK", "John F. Kennedy International", 50000000),
            Airport("LAX", "Los Angeles International", 40000000)
        )
        fakeAirportDao.setAirports(airports)

        val result = repository.searchAirports("JFK").first()

        assertEquals(1, result.size)
        assertEquals("JFK", result[0].iataCode)
    }

    @Test
    fun `getAirportByCode returns airport when exists`() = runTest {
        val airport = Airport("JFK", "John F. Kennedy International", 50000000)
        fakeAirportDao.setAirports(listOf(airport))

        val result = repository.getAirportByCode("JFK")

        assertNotNull(result)
        assertEquals("JFK", result?.iataCode)
    }

    @Test
    fun `getAirportByCode returns null when not exists`() = runTest {
        val result = repository.getAirportByCode("XXX")

        assertNull(result)
    }

    @Test
    fun `getDestinationsForDeparture returns routes`() = runTest {
        val destinations = listOf(
            DestinationAirport("LAX", "Los Angeles International", 40000000),
            DestinationAirport("ORD", "O'Hare International", 30000000)
        )
        fakeFlightRouteDao.setDestinations(destinations)

        val result = repository.getDestinationsForDeparture("JFK").first()

        assertEquals(2, result.size)
        assertEquals("LAX", result[0].iata_code)
        assertEquals("ORD", result[1].iata_code)
    }
}

class FakeAirportDao : AirportDao {
    private var airports: List<Airport> = emptyList()

    fun setAirports(airports: List<Airport>) {
        this.airports = airports
    }

    override fun getAllAirports(): Flow<List<Airport>> = flowOf(airports)

    override fun searchAirports(query: String): Flow<List<Airport>> = flowOf(
        airports.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.iataCode.contains(query, ignoreCase = true)
        }
    )

    override suspend fun getAirportByCode(code: String): Airport? =
        airports.find { it.iataCode == code }

    override suspend fun insertAll(airports: List<Airport>) {
        this.airports = airports
    }
}

class FakeFlightRouteDao : FlightRouteDao {
    private var destinations: List<DestinationAirport> = emptyList()
    private var routes: List<FlightRoute> = emptyList()

    fun setDestinations(destinations: List<DestinationAirport>) {
        this.destinations = destinations
    }

    fun setRoutes(routes: List<FlightRoute>) {
        this.routes = routes
    }

    override fun getAllRoutes(): Flow<List<FlightRoute>> = flowOf(routes)

    override fun getRoutesForDeparture(departureCode: String): Flow<List<FlightRoute>> = flowOf(
        routes.filter { it.departureCode == departureCode }
    )

    override fun getDestinationsForDeparture(departureCode: String): Flow<List<DestinationAirport>> = flowOf(destinations)

    override suspend fun insertAll(routes: List<FlightRoute>) {
        this.routes = routes
    }
}
