package in2000.team42.ui.screens.home.map

import android.util.Log
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
import com.mapbox.geojson.Polygon
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.RenderedQueryGeometry
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapState
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotationState
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.interactions.TypedFeaturesetDescriptor
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.search.autocomplete.PlaceAutocomplete
import in2000.team42.R
import in2000.team42.ui.screens.home.HomeViewModel
import in2000.team42.ui.screens.home.map.search.SearchBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(MapboxExperimental::class)
private suspend fun MapState.queryBuildingCoordinatesAt(point: Point): List<List<Point>>? {
    // Use withContext to ensure map operations run on the main thread
    return withContext(Dispatchers.Main) {
        val selectedBuildings = queryRenderedFeatures(
            geometry = RenderedQueryGeometry(pixelForCoordinate(point)),
            descriptor = TypedFeaturesetDescriptor.Layer("building")
        )
        if (selectedBuildings.isEmpty()) {
            Log.d("HouseClick", "Clicked outside of building")
            null
        } else {
            Log.d("HouseClick", "Feature properties: ${selectedBuildings.first().properties}")
            (selectedBuildings.first().geometry as? Polygon)?.coordinates()?.toList()
        }
    }
}

private fun calculatePolygonArea(mapPolygon: List<List<Point>>): Double {
    if (mapPolygon.isEmpty()) {
        return 0.0
    }

    // Use the exterior ring of the polygon (first list of points)
    val vertices = mapPolygon[0]
    if (vertices.size < 3) {
        return 0.0
    }

    // Calculate using the Shoelace formula
    var area = 0.0
    for (i in 0 until vertices.size - 1) {
        // Convert to local projected coordinates for more accurate area calculation
        // A simple approximation for small areas is to use longitude and latitude directly
        val lng1 = vertices[i].longitude()
        val lat1 = vertices[i].latitude()
        val lng2 = vertices[i + 1].longitude()
        val lat2 = vertices[i + 1].latitude()

        area += (lng1 * lat2) - (lng2 * lat1)
    }

    // Close the polygon
    val lastIndex = vertices.size - 1
    val lng1 = vertices[lastIndex].longitude()
    val lat1 = vertices[lastIndex].latitude()
    val lng2 = vertices[0].longitude()
    val lat2 = vertices[0].latitude()
    area += (lng1 * lat2) - (lng2 * lat1)

    // Take absolute value and divide by 2
    area = Math.abs(area) / 2.0

    // Convert to square meters using an approximation
    // This factor varies with latitude, better calculations would use a proper projection
    val latMid = vertices.map { it.latitude() }.average()
    val lonFactor = Math.cos(Math.toRadians(latMid))
    val metersPerDegreeLat = 111320.0 // Approximate meters per degree of latitude
    val metersPerDegreeLon = metersPerDegreeLat * lonFactor

    return area * metersPerDegreeLat * metersPerDegreeLon
}

@Composable
fun Map(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
)
{

    MapboxOptions.accessToken = stringResource(R.string.mapbox_access_token)
    val placeAutoComplete = PlaceAutocomplete.create()

    val focusManager = LocalFocusManager.current

    var mapClicked by remember { mutableStateOf(false) }

    val couroutineScope = rememberCoroutineScope()
    
    val mapState = rememberMapState()

    var mapPolygon: List<List<Point>>? by remember { mutableStateOf(null) }

    val polygonAnnotationState = remember { PolygonAnnotationState() }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(10.0)
            center(Point.fromLngLat(10.7522, 59.9139))
            pitch(0.0)
            bearing(0.0)
        }
    }

    fun loadHouse(point: Point, delay: Long = 0, onComplete: () -> Unit = {}) {
        couroutineScope.launch {
            mapPolygon = null
            kotlinx.coroutines.delay(delay)
            mapPolygon = mapState.queryBuildingCoordinatesAt(point)
            Log.d("Map", mapPolygon.toString())

            if (mapPolygon.isNullOrEmpty()) {
                return@launch
            }
            onComplete()
            viewModel.setLongitude(point.longitude())
            viewModel.setLatitude(point.latitude())
            viewModel.setAreal(calculatePolygonArea(mapPolygon!!).toFloat())
            viewModel.updateAllApi()

        }
    }
    fun mapEaseTo (point: Point, duration: Long, latOffset: Double) {
        mapViewportState.easeTo(cameraOptions = CameraOptions.Builder()
            .center(point.let {
                Point.fromLngLat(it.longitude(), it.latitude() - latOffset)
            })
            .zoom(18.0)
            .pitch(0.0)
            .bearing(0.0)
            .build(),
            animationOptions = MapAnimationOptions.mapAnimationOptions {
                duration(duration)
            }

        )
    }

    fun clearScreen () {
        couroutineScope.launch {
            mapClicked = true
            focusManager.clearFocus()

            kotlinx.coroutines.delay(100)
            mapClicked = false
        }
    }

    fun onMapClicked(point: Point): Boolean {
        clearScreen()
        loadHouse(point, onComplete = {
            mapEaseTo(point, 1000, 0.0004)
        })
        return true
    }

    fun settNyttPunkt(point: Point) : Boolean{

        clearScreen()
        mapEaseTo(point, 2000, 0.0004)
        loadHouse(point, delay = 2400)


        return true
    }

    Box(modifier = Modifier.fillMaxSize()) {

        MapboxMap(
            scaleBar = {},
            mapViewportState = mapViewportState,
            modifier = Modifier.fillMaxSize(),
            onMapClickListener = { onMapClicked(it) },
            mapState = mapState,
            style = {
                MapStyle(style = Style.MAPBOX_STREETS)
            },
        ) {
            if (mapPolygon.isNullOrEmpty().not()) {

                PolygonAnnotation(
                    points = mapPolygon!!,

                )
            }
        }

        SearchBar(
            placeAutocomplete = placeAutoComplete,
            onLocationSelected = { settNyttPunkt(it) },
            modifier = Modifier.padding(top = 26.dp),
            isMapClicked = mapClicked
        )
    }
    
}