/*

*    Author: Umut Altindere

*    Email: altindereumut@gmail.com

*/

package com.example.flightsearchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flightsearchapp.data.AppDatabase
import com.example.flightsearchapp.repository.FlightRepository
import com.example.flightsearchapp.ui.theme.FlightSearchAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the database and DAO
        val database = AppDatabase.getDatabase(this)
        val flightSearchDao = database.flightSearchDao()
        val repository = FlightRepository(flightSearchDao)

        // Creating a ViewModelFactory with the repository
        val viewModelFactory = FlightSearchViewModelFactory(repository)

        setContent {
            FlightSearchAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    val viewModel: FlightSearchViewModel = viewModel(factory = viewModelFactory)

                    NavHost(navController = navController, startDestination = "search") {
                        composable("search") {
                            FlightSearchScreen(viewModel, navController)
                        }
                        composable("airportDetails/{iataCode}") { backStackEntry ->
                            val iataCode = backStackEntry.arguments?.getString("iataCode")
                            FlightDetailsScreen(iataCode ?: "", viewModel, navController)
                        }
                    }
                }
            }
        }
    }
}


