package in2000.team42.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle



@Composable
fun Map(modifier: Modifier = Modifier, viewModel: HomeViewModel) {

    fun settNyttPunkt(point: Point) : Boolean{
        viewModel.setLongitude(point.longitude())
        viewModel.setLatitude(point.latitude())
        viewModel.updateLocation()
        return true
    }

    MapboxMap(
        modifier = modifier.fillMaxSize(),
        onMapClickListener = { settNyttPunkt(it) },
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(10.0)
                center(Point.fromLngLat(10.7522, 59.9139))
                pitch(0.0)
                bearing(0.0)
            }
        },
    )
}