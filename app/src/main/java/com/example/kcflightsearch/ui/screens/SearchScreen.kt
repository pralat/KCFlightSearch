package com.example.kcflightsearch.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kcflightsearch.data.local.FavoriteRoute
import com.example.kcflightsearch.data.model.Airport
import com.example.kcflightsearch.viewmodel.FlightSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: FlightSearchViewModel,
    onAirportSelected: (Airport) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val favoriteRoutes by viewModel.allFavoriteRoutes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flight Search") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search airports") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear"
                            )
                        }
                    }
                },
                singleLine = true
            )

            // Show favorites when search is empty
            if (searchQuery.isEmpty() && favoriteRoutes.isNotEmpty()) {
                Text(
                    text = "Favorite Routes",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(favoriteRoutes) { route ->
                        FavoriteRouteItem(
                            route = route,
                            onClick = {
                                viewModel.onAirportSelected(
                                    Airport(
                                        id = 0,
                                        name = route.departure_name,
                                        iataCode = route.departure_code,
                                        passengers = route.passengers
                                    )
                                )
                                onAirportSelected(
                                    Airport(
                                        id = 0,
                                        name = route.departure_name,
                                        iataCode = route.departure_code,
                                        passengers = route.passengers
                                    )
                                )
                            },
                            onUnfavoriteClick = {
                                viewModel.toggleFavorite(route.departure_code, route.destination_code, true)
                            }
                        )
                    }
                }
            } else if (searchQuery.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchResults) { airport ->
                        AirportItem(
                            airport = airport,
                            onClick = { onAirportSelected(airport) }
                        )
                    }
                }
            } else {
                // No favorites and no search query - show empty state message
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Search for an airport to find flights",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AirportItem(
    airport: Airport,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = airport.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = airport.iataCode,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = formatPassengers(airport.passengers),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FavoriteRouteItem(
    route: com.example.kcflightsearch.data.local.FavoriteRoute,
    onClick: () -> Unit,
    onUnfavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = route.destination_name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Box(
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${route.departure_name} (${route.departure_code})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
//                            text = " → ", // arrow sat too low on line
                            text = " --> ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = route.destination_code,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
            IconButton(onClick = onUnfavoriteClick) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Remove from favorites",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun formatPassengers(passengers: Long): String {
    return if (passengers >= 1000000) {
        String.format("%.1fM", passengers / 1000000.0)
    } else if (passengers >= 1000) {
        String.format("%.1fK", passengers / 1000.0)
    } else {
        passengers.toString()
    }
}
