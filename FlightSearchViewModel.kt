/*

*    Author: Umut Altindere

*    Email: altindereumut@gmail.com

*/

package com.example.flightsearchapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.flightsearchapp.model.Airport
import com.example.flightsearchapp.model.Favorite
import com.example.flightsearchapp.repository.FlightRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlightSearchViewModel(private val repository: FlightRepository) : ViewModel() {
    // Mutable state for favorites list and search queries
    private val _favorites = MutableStateFlow<List<Favorite>>(emptyList())
    val favorites: StateFlow<List<Favorite>> = _favorites.asStateFlow()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        // Load the initial list of favorites on view model
        viewModelScope.launch {
            repository.allFavorites.collect { favoritesList ->
                _favorites.value = favoritesList
            }
        }
    }

    // Displaying all the airports from the repository as a state flow
    val allAirports: StateFlow<List<Airport>> = repository.allAirports.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Generating the suggestions from the search query
    val suggestions: Flow<List<Airport>> = _searchQuery
        .debounce(300)  // Adding the debounce for reducing the emission numbers
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(emptyList())
            } else {
                repository.getAirportSearchResults(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Updating the search query
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    // Favorite status toggled on for an airport
    fun onToggleFavorite(departureCode: String, destinationAirport: Airport, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            val favorite = Favorite(0, departureCode, destinationAirport.iata_code)
            _favorites.update { currentFavorites ->
                if (isCurrentlyFavorite) {
                    // If it's favorite, remove from favorites
                    repository.removeFavoriteByCodes(departureCode, destinationAirport.iata_code)
                    currentFavorites.filterNot { it.departure_code == departureCode && it.destination_code == destinationAirport.iata_code }
                } else {
                    // Else, add to favorites
                    repository.addFavorite(favorite)
                    currentFavorites + favorite
                }
            }
        }
    }

    // Get the airport details from iata code
    fun getAirportNameByIataCode(iataCode: String): Flow<Airport> {
        return repository.getAirportNameByIataCode(iataCode).conflate()
    }

    // Airport selection for the navigation
    fun onAirportSelected(airport: Airport, navController: NavController) {
        val route = "airportDetails/${airport.iata_code}"
        navController.navigate(route)
    }

    // Get flights from the specific airport
    fun getFlightsFromAirport(iataCode: String): Flow<List<Airport>> {
        return repository.getFlightsFromAirport(iataCode)
    }

    // Checking for airport is in the favorites list
    suspend fun isAirportFavorite(iataCode: String): Boolean {
        return repository.isAirportFavorited(iataCode)
    }

    // Removing an airport from the favorite list
    fun onRemoveFavorite(departureCode: String, destinationCode: String) {
        viewModelScope.launch {
            repository.removeFavoriteByCodes(departureCode, destinationCode)
        }
    }
}
