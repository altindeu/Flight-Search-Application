/*

*    Author: Umut Altindere

*    Email: altindereumut@gmail.com

*/

package com.example.flightsearchapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flightsearchapp.repository.FlightRepository

// Creating the instances of the FlightSearchViewModel with the repository
class FlightSearchViewModelFactory(private val repository: FlightRepository) : ViewModelProvider.Factory {
    // Creating the new instance of the modelClass
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightSearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlightSearchViewModel(repository) as T
        }
        // If the view model that is requested isn't the FlightSearchViewModel, then it throws exception
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
