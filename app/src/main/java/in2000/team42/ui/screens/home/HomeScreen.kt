package in2000.team42.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle

@Composable
fun HomeScreen(navController: NavHostController, modifier: Modifier = Modifier, viewModel: HomeViewModel) {
    MapboxMap(
        modifier = modifier.fillMaxSize(),
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(10.0)
                center(Point.fromLngLat(10.7522, 59.9139))
                pitch(0.0)
                bearing(0.0)
            }
        },
        style = {
            MapStyle(Style.DARK)
        }
    )
}
































