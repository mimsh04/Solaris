package in2000.team42.ui.screens.home.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.search.autocomplete.PlaceAutocomplete
import in2000.team42.R
import in2000.team42.ui.screens.home.HomeViewModel
import in2000.team42.ui.screens.home.map.search.SearchBar
import kotlinx.coroutines.launch


@Composable
fun Map(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
)
{

    MapboxOptions.accessToken = stringResource(R.string.mapbox_access_token)
    val placeAutoComplete = PlaceAutocomplete.create()

    var mapClicked by remember { mutableStateOf(false) }

    val couroutineScope = rememberCoroutineScope()

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(10.0)
            center(Point.fromLngLat(10.7522, 59.9139))
            pitch(0.0)
            bearing(0.0)
        }
    }

    fun settNyttPunkt(point: Point) : Boolean{
        viewModel.setLongitude(point.longitude())
        viewModel.setLatitude(point.latitude())
        viewModel.updateAllApi()
        mapViewportState.easeTo(cameraOptions = CameraOptions.Builder()
            .center(point)
            .zoom(14.0)
            .pitch(0.0)
            .bearing(0.0)
            .build(),
            animationOptions = MapAnimationOptions.mapAnimationOptions {
                duration(2000)
            }
        )

        return true
    }
    val focusManager = LocalFocusManager.current
    fun onMapClicked(point: Point): Boolean {

        mapClicked = true
        focusManager.clearFocus()

        couroutineScope.launch {
            kotlinx.coroutines.delay(100)
            mapClicked = false
        }
        return true
    }


    Box(modifier = Modifier.fillMaxSize()) {

        MapboxMap(
            scaleBar = {},
            mapViewportState = mapViewportState,
            modifier = Modifier.fillMaxSize(),
            onMapClickListener = { onMapClicked(it) }
        )

        SearchBar(
            placeAutocomplete = placeAutoComplete,
            onLocationSelected = { settNyttPunkt(it) },
            modifier = Modifier.padding(top = 26.dp),
            isMapClicked = mapClicked
        )
    }




}