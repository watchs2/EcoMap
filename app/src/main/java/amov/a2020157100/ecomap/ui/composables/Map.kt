package amov.a2020157100.ecomap.ui.composables

import amov.a2020157100.ecomap.R
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
import amov.a2020157100.ecomap.ui.theme.BackgroundMap
import amov.a2020157100.ecomap.ui.theme.BinBlack
import amov.a2020157100.ecomap.ui.theme.BinBlue
import amov.a2020157100.ecomap.ui.theme.BinGreen
import amov.a2020157100.ecomap.ui.theme.BinRed
import amov.a2020157100.ecomap.ui.theme.BinYellow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat

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
            "Blue bin" to getMarkerIcon(context, R.drawable.recycable, size = 84, tint = BinBlue),
            "Green bin" to getMarkerIcon(context, R.drawable.recycable, size = 84, tint = BinGreen),
            "Yellow bin" to getMarkerIcon(context, R.drawable.recycable, size = 84, tint = BinYellow),
            "Red bin" to getMarkerIcon(context, R.drawable.recycable, size = 84, tint = BinRed),
            "Black bin" to getMarkerIcon(context, R.drawable.recycable, size = 84, tint = BinBlack)
        )
    }

    DisposableEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
        onDispose { }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundMap)
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
                            title = context.getString(R.string.my_location)
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
                    //todo
                    // A animação constante pode ser intrusiva se o utilizador estiver a mexer no mapa,
                    // considera usar if(firstLoad) ou similar. Mas mantive conforme o original:
                    view.controller.animateTo(currentGeoPoint)

                    for (recyclingpoint in recyclingPoints) {
                        val titleResId = when(recyclingpoint.type) {
                            "Blue bin" -> R.string.bin_blue
                            "Green bin" -> R.string.bin_green
                            "Yellow bin" -> R.string.bin_yellow
                            "Red bin" -> R.string.bin_red
                            "Black bin" -> R.string.bin_black
                            else -> R.string.bin_unknown
                        }

                        view.overlays.add(
                            Marker(view).apply {
                                position = GeoPoint(recyclingpoint.latatitude, recyclingpoint.longitude)
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = context.getString(titleResId)
                                icon = recyclingPointIcons[recyclingpoint.type]
                                    ?: getMarkerIcon(context, R.drawable.recycable, size = 84, tint = Color.Gray)
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
    tint: Color
): BitmapDrawable {

    val drawable = ContextCompat.getDrawable(context, drawableId)?.mutate()
        ?: return BitmapDrawable(context.resources)

    drawable.setTint(tint.toArgb())

    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, size, size)
    drawable.draw(canvas)

    return BitmapDrawable(context.resources, bitmap)
}