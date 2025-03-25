package in2000.team42.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import in2000.team42.R


@Composable
fun Map(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel)
{

    MapboxOptions.accessToken = stringResource(R.string.mapbox_access_token)
    val placeAutoComplete = PlaceAutocomplete.create()

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
        mapViewportState.setCameraOptions {
            center(point)
            zoom(14.0) // Zoom in when a new point is selected
        }
        return true
    }


    Box(modifier = modifier.fillMaxSize()) {

        MapboxMap(
            //onMapClickListener = { settNyttPunkt(it) },
            //scaleBar = {},
            mapViewportState = mapViewportState,
            modifier = Modifier.fillMaxSize()
        )

        // Search bar positioned at the top
        SearchBar(
            placeAutocomplete = placeAutoComplete,
            onLocationSelected = { settNyttPunkt(it) },
            modifier = Modifier.padding(top = 16.dp)
        )
    }




}