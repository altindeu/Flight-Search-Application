/*

*    Author: Umut Altindere

*    Email: altindereumut@gmail.com

*/

package com.example.flightsearchapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.collectAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flightsearchapp.model.Airport
import com.example.flightsearchapp.model.Favorite

@Composable
// Function for inputting the departure airport search queries
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onExecuteSearch: () -> Unit  // Used in the FlightSearchScreen for executing the search
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.Transparent, // Set the surface color to transparent for a better display
        shape = RoundedCornerShape(8.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            label = { Text("Enter departure airport") },  // Label for search query
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            leadingIcon = {
                // Icon for "Search"
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            }
        )
    }
}

@Composable
// Function for adjusting the airport suggestions from the search bar
fun SuggestionList(
    suggestions: List<Airport>,
    onSuggestionSelect: (Airport) -> Unit
) {
    // LazyColumn for scrollable list, with adjustments on the ui part
    LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
        items(suggestions) { airport ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(50)
                    )
                    .clickable { onSuggestionSelect(airport) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${airport.iata_code} - ${airport.name}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
// Function for creating the favorites route and displaying the list
fun FavoriteList(
    favorites: List<Favorite>,
    airports: List<Airport>,
    onRemoveFavorite: (Favorite) -> Unit
) {
    LazyColumn {
        items(favorites, key = { favorite -> favorite.id }) { favorite ->
            // Finding the airports for their departure and arrival codes
            val departureAirport = airports.find { it.iata_code == favorite.departure_code }
            val destinationAirport = airports.find { it.iata_code == favorite.destination_code }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "DEPART",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${favorite.departure_code} - ${departureAirport?.name ?: "Unknown"}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "ARRIVE",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${favorite.destination_code} - ${destinationAirport?.name ?: "Unknown"}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    IconButton(
                        onClick = { onRemoveFavorite(favorite) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Unfavorite",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}


@Composable
// Function for displaying the individual flight details
fun FlightItem(
    // Variables that containing the information of the specified table names
    departureAirport: Airport,
    destinationAirport: Airport,
    isFavorite: Boolean,
    onFavoriteToggle: (Airport) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "DEPART",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${departureAirport.iata_code} - ${departureAirport.name}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "ARRIVE",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${destinationAirport.iata_code} - ${destinationAirport.name}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal)
                )
            }
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Default.Star,
                contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onFavoriteToggle(destinationAirport) }
            )
        }
    }
}


@Composable
// Function for displaying the details of the flights from the specific airports
fun FlightDetailsScreen(departIataCode: String, viewModel: FlightSearchViewModel, navController: NavController) {
    // States for the departure and destination airport details
    val departAirport by viewModel.getAirportNameByIataCode(departIataCode)
        .collectAsState(initial = null)
    val destinationFlights by viewModel.getFlightsFromAirport(departIataCode)
        .collectAsState(initial = listOf())

    Column {
        // Navigate back button (Text used over an Icon)
        TextButton(onClick = { navController.navigateUp() }) {
            Text("Go Back")
        }

        // Header for departure airport
        departAirport?.let { airport ->
            Text(
                text = "Flights from ${airport.iata_code}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Left
            )
        }

        // If any destination flights are available
        if (destinationFlights.isEmpty()) {
            Text("No flights available.")
        } else {
            // Displaying the list of flights with the lazy column
            LazyColumn {
                items(destinationFlights) { destinationAirport ->
                    var isFavorite by remember { mutableStateOf(false) }

                    // Check if the airport is favorite
                    LaunchedEffect(destinationAirport) {
                        isFavorite = viewModel.isAirportFavorite(destinationAirport.iata_code)  // Tracks the favorite status of each flight
                    }

                    // Flight item
                    departAirport?.let { departAirportObj ->
                        FlightItem(
                            departureAirport = departAirportObj,
                            destinationAirport = destinationAirport,
                            isFavorite = isFavorite,
                            onFavoriteToggle = { selectedAirport ->
                                viewModel.onToggleFavorite(departAirportObj.iata_code, selectedAirport, isFavorite)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun FlightSearchScreen(viewModel: FlightSearchViewModel, navController: NavController) {
    // States for the viewmodel queries
    val searchQuery by viewModel.searchQuery.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState(initial = emptyList())
    val favorites by viewModel.favorites.collectAsState(initial = emptyList())
    val allAirports by viewModel.allAirports.collectAsState()

    Column {
        // Title "Flight Search"
        Text(
            text = "Flight Search",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        SearchBar(
            query = searchQuery,
            onQueryChanged = { query -> viewModel.onSearchQueryChanged(query) },
            onExecuteSearch = {}  // Searching is done live with the query state so no op required for here
        )

        if (searchQuery.isEmpty()) {
            // "Favorite routes" for favorite option
            Text(
                text = "Favorite routes",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .align(Alignment.Start)
            )

            // Shows the favorites if the search query is empty
            // Passing the list of all airports to the FavoriteList composable
            FavoriteList(favorites = favorites, airports = allAirports, onRemoveFavorite = { favorite ->
                viewModel.onRemoveFavorite(favorite.departure_code, favorite.destination_code)
            })
        } else {
            // Otherwise, showing the suggestions based on the search query
            SuggestionList(suggestions = suggestions, onSuggestionSelect = { airport ->
                viewModel.onAirportSelected(airport, navController) // Navigating to details screen
            })
        }
    }
}

