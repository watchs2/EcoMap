package amov.a2020157100.ecomap
import android.app.Application
import com.google.android.gms.location.LocationServices
import amov.a2020157100.ecomap.utils.location.LocationHandler
import amov.a2020157100.ecomap.utils.location.FusedLocationHandler

class EcoMap : Application(){

    val locationHandler: LocationHandler by lazy {
        val locationProvider = LocationServices
            .getFusedLocationProviderClient(this)
        FusedLocationHandler(locationProvider)
    }
}