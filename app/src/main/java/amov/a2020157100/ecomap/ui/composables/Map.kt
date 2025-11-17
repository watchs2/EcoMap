package amov.a2020157100.ecomap.ui.composables

import amov.a2020157100.ecomap.ui.screens.MapColor
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
    firebaseViewModel: FirebaseViewModel,
    locationViewModel: LocationViewModel,
    modifier: Modifier = Modifier
) {
    val recyclingPoints = firebaseViewModel.recyclingPoints.value
    locationViewModel.startLocationUpdates()
    val currentLocation = locationViewModel.currentLocation.value

    LaunchedEffect(recyclingPoints) {
        firebaseViewModel.getRecyclingPoints()
    }

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
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MapColor)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
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

                        for(recyclingpoint in firebaseViewModel.recyclingPoints.value){
                            overlays.add(
                                Marker(this).apply {
                                    position = GeoPoint(recyclingpoint.latatitude, recyclingpoint.longitude)
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    title = recyclingpoint.type
                                }
                            )
                        }

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





