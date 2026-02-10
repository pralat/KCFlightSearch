package com.example.kcflightsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kcflightsearch.data.local.FlightSearchDatabase
import com.example.kcflightsearch.data.repository.FlightSearchRepository
import com.example.kcflightsearch.ui.FlightSearchApp
import com.example.kcflightsearch.ui.theme.KCFlightSearchTheme
import com.example.kcflightsearch.viewmodel.FlightSearchViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = FlightSearchDatabase.getDatabase(applicationContext)
        val repository = FlightSearchRepository(
            database.airportDao(),
            database.flightRouteDao()
        )

        setContent {
            KCFlightSearchTheme {
                val viewModel: FlightSearchViewModel = viewModel(
                    factory = FlightSearchViewModel.Factory(repository)
                )
                FlightSearchApp(viewModel = viewModel)
            }
        }
    }
}
