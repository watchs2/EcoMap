package amov.a2020157100.ecomap.ui.composables

import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.ui.screens.MapColor
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModel
import android.graphics.drawable.BitmapDrawable
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
import android.graphics.Canvas
import android.content.Context
import android.graphics.Bitmap
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
    val recyclingPoints by firebaseViewModel.recyclingPoints
    val currentLocation by locationViewModel.currentLocation

    LaunchedEffect(Unit) {
        firebaseViewModel.getRecyclingPoints()
    }

    val currentGeoPoint = remember(currentLocation) {
        GeoPoint(
            currentLocation?.latitude ?: 0.0,
            currentLocation?.longitude ?: 0.0
        )
    }

    val context = LocalContext.current
    val recyclingPointIcons = remember(context) {
        mapOf(
            "Blue bin" to getMarkerIcon(context, R.drawable.recycable, size = 84, tintColorId = R.color.blue_bin),
            "Green bin" to getMarkerIcon(context, R.drawable.recycable, size = 84, tintColorId =R.color.green_bin),
            "Yellow bin" to getMarkerIcon(context, R.drawable.recycable, size = 84, tintColorId = R.color.yellow_bin),
            "Red bin" to getMarkerIcon(context, R.drawable.recycable, size = 84, tintColorId = R.color.red_bin),
            "Black bin" to getMarkerIcon(context, R.drawable.recycable, size = 84, tintColorId = R.color.black_bin)
        )
    }

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

                    }
                },
                update = { view ->
                    val locationMarker = view.tag as? Marker
                    view.overlays.clear()
                    if (locationMarker != null) {
                        locationMarker.position = currentGeoPoint
                        view.overlays.add(locationMarker)
                    }
                    view.controller.animateTo(currentGeoPoint)
                    for (recyclingpoint in recyclingPoints){
                        view.overlays.add(
                            Marker(view).apply {
                                position = GeoPoint(recyclingpoint.latatitude, recyclingpoint.longitude)
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = recyclingpoint.type
                                icon = recyclingPointIcons[recyclingpoint.type]
                            }
                        )
                    }
                    view.invalidate()
                }
            )
        }
    }
}
fun getMarkerIcon(
    context: Context,
    drawableId: Int,
    size: Int = 64,
    tintColorId: Int? = null
): BitmapDrawable {

    val drawable = context.resources.getDrawable(drawableId, null).mutate()

    if (tintColorId != null) {
        drawable.setTint(context.resources.getColor(tintColorId, null))
    }

    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, size, size)
    drawable.draw(canvas)

    return BitmapDrawable(context.resources, bitmap)
}