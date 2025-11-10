package amov.a2020157100.ecomap.utils.location

import android.location.Location

interface LocationHandler {
    var locationEnabled: Boolean
    var onLocation: ((Location) -> Unit)?
    fun startLocationUpdates()
    fun stopLocationUpdates()
}