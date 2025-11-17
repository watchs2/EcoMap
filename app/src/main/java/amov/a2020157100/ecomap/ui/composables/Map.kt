package amov.a2020157100.ecomap.ui.composables

import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import org.osmdroid.config.Configuration
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint


@Composable
fun Map(
    locationViewModel: LocationViewModel,
    modifier: Modifier = Modifier
) {

    locationViewModel.startLocationUpdates()
    val currentLocation = locationViewModel.currentLocation.value

    val currentGeoPoint = remember(currentLocation) {
        GeoPoint(
            currentLocation?.latitude ?: 0.0,
            currentLocation?.longitude ?: 0.0
        )
    }


    val context = LocalContext.current


    DisposableEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
        onDispose {

        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Lat: ${currentLocation?.latitude ?: "--"}")
            Text(text = "Lon: ${currentLocation?.longitude ?: "--"}")
        }
        Spacer(Modifier.height(16.dp))


        Box(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.9f) // Use mais espaço para o mapa
                .background(Color.LightGray)
        ) {
            AndroidView(
                factory = {
                    MapView(it).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(18.0)
                        controller.setCenter(currentGeoPoint)


                        val locationMarker = Marker(this).apply {
                            position = currentGeoPoint
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = "Minha Localização"

                        }
                        overlays.add(locationMarker)

                        tag = locationMarker
                    }
                },
                update = { view ->

                    view.controller.animateTo(currentGeoPoint)


                    val locationMarker = view.tag as? Marker
                    locationMarker?.position = currentGeoPoint
                    view.invalidate()
                }
            )
        }
    }
}





