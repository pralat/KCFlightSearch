package com.example.kcflightsearch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kcflightsearch.data.local.DestinationAirport
import com.example.kcflightsearch.data.local.PreferencesManager
import com.example.kcflightsearch.data.local.FavoriteRoute
import com.example.kcflightsearch.data.model.Airport
import com.example.kcflightsearch.data.model.Favorite
import com.example.kcflightsearch.data.repository.FlightSearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class FlightSearchViewModel(
    private val repository: FlightSearchRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedAirport = MutableStateFlow<Airport?>(null)
    val selectedAirport: StateFlow<Airport?> = _selectedAirport.asStateFlow()

    val searchResults: StateFlow<List<Airport>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllAirports()
            } else {
                repository.searchAirports(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteRoutes: StateFlow<List<DestinationAirport>> = _selectedAirport
        .flatMapLatest { airport ->
            if (airport != null) {
                repository.getDestinationsForDeparture(airport.iataCode)
            } else {
                kotlinx.coroutines.flow.flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // All favorite routes for displaying on search screen when query is empty
    val allFavoriteRoutes: StateFlow<List<FavoriteRoute>> =
        repository.getAllFavoriteRoutes()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    init {
        // Load saved search query from preferences
        viewModelScope.launch {
            val savedQuery = preferencesManager.searchQuery.first()
            _searchQuery.value = savedQuery
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            preferencesManager.saveSearchQuery(query)
        }
    }

    fun onAirportSelected(airport: Airport) {
        _selectedAirport.value = airport
    }

    fun clearSelection() {
        _selectedAirport.value = null
    }

    fun toggleFavorite(departureCode: String, destinationCode: String, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) {
                repository.removeFavorite(departureCode, destinationCode)
            } else {
                repository.addFavorite(Favorite(departureCode = departureCode, destinationCode = destinationCode))
            }
        }
    }

    fun isFavorite(departureCode: String, destinationCode: String): Flow<Boolean> {
        return repository.isFavorite(departureCode, destinationCode)
    }

    class Factory(
        private val repository: FlightSearchRepository,
        private val preferencesManager: PreferencesManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FlightSearchViewModel::class.java)) {
                return FlightSearchViewModel(repository, preferencesManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
