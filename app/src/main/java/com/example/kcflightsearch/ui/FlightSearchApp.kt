package com.example.kcflightsearch.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kcflightsearch.ui.screens.FlightRouteScreen
import com.example.kcflightsearch.ui.screens.SearchScreen
import com.example.kcflightsearch.viewmodel.FlightSearchViewModel

sealed class Screen(val route: String) {
    data object Search : Screen("search")
    data object FlightRoutes : Screen("flight_routes")
}

@Composable
fun FlightSearchApp(
    viewModel: FlightSearchViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Search.route
    ) {
        composable(Screen.Search.route) {
            SearchScreen(
                viewModel = viewModel,
                onAirportSelected = { airport ->
                    viewModel.onAirportSelected(airport)
                    navController.navigate(Screen.FlightRoutes.route)
                }
            )
        }

        composable(Screen.FlightRoutes.route) {
            FlightRouteScreen(
                viewModel = viewModel,
                onBackClick = {
                    viewModel.clearSelection()
                    navController.popBackStack()
                }
            )
        }
    }
}
