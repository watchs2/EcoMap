package amov.a2020157100.ecomap.ui.viewmodels

import amov.a2020157100.ecomap.model.User
import android.location.Location
import androidx.lifecycle.ViewModel
import amov.a2020157100.ecomap.utils.location.LocationHandler
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider


class LocationViewModelFactory(
    private val locationHandler: LocationHandler
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            return LocationViewModel(locationHandler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class LocationViewModel(
    private val locationHandler: LocationHandler
): ViewModel(){
    //Location
    var hasLocationPermission: Boolean = false
    private val _currentLocation = mutableStateOf(Location(null))


    init {
        locationHandler.onLocation =  { location ->
            _currentLocation.value = location

        }
    }

    val currentLocation: State<Location?>
        get() = _currentLocation

    fun startLocationUpdates(){
        if(hasLocationPermission){
            locationHandler.startLocationUpdates()
        }
    }

    fun stopLocationUpdates(){
       locationHandler.stopLocationUpdates()
    }


}