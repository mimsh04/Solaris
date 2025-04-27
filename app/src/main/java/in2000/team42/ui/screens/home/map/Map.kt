package in2000.team42.ui.screens.home.map

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
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
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.interactions.TypedFeaturesetDescriptor
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.search.autocomplete.PlaceAutocomplete
import in2000.team42.R
import in2000.team42.ui.screens.home.HomeViewModel
import in2000.team42.ui.screens.home.map.search.SearchBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(MapboxExperimental::class)
private suspend fun MapState.queryBuildingCoordinatesAt(point: Point): List<List<Point>>? {
    return withContext(Dispatchers.Main) {
        val selectedBuildings = queryRenderedFeatures(
            geometry = RenderedQueryGeometry(pixelForCoordinate(point)),
            descriptor = TypedFeaturesetDescriptor.Layer("building")
        )
        if (selectedBuildings.isEmpty()) {
            null
        } else {
            Log.d("HouseClick", "Feature properties: ${selectedBuildings.first().properties}")
            (selectedBuildings.first().geometry as? Polygon)?.coordinates()?.toList()
        }
    }
}


@Composable
fun Map(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    MapboxOptions.accessToken = stringResource(R.string.mapbox_access_token)
    val placeAutoComplete = PlaceAutocomplete.create()

    val focusManager = LocalFocusManager.current

    var mapClicked by remember { mutableStateOf(false) }

    val couroutineScope = rememberCoroutineScope()

    val mapState = rememberMapState()

    val config = viewModel.configFlow.collectAsState()

    var startPos = Point.fromLngLat(10.7522, 59.9139)
    var startZoom = 10.0

    fun getSheetMapOffset():Double =
         if (config.value.bottomSheetDetent == "medium") 0.00035 else 0.00008


    if (config.value.polygon != null) {
        startPos = calculateCentroid(config.value.polygon!!)
        startPos = Point.fromLngLat(
            startPos.longitude(),
            startPos.latitude() - getSheetMapOffset()
        )
        startZoom = 18.0
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(startZoom)
            center(startPos)
            pitch(0.0)
            bearing(0.0)
        }
    }

    fun loadHouse(point: Point, delay: Long = 0, onComplete: (List<List<Point>>) -> Unit = {}) {
        couroutineScope.launch {
            //viewModel.setPolygon(null)
            kotlinx.coroutines.delay(delay)
            val newPolygon = mapState.queryBuildingCoordinatesAt(point)

            if (newPolygon.isNullOrEmpty()) {
                Log.i("HouseClick", "No building found")
                return@launch
            }

            val cleanedPolygon = newPolygon[0].toMutableList()
            cleanedPolygon.removeAt(cleanedPolygon.lastIndex)

            viewModel.setPolygon(listOf(cleanedPolygon))

            viewModel.setCoordinates(
                longitude = point.longitude(),
                latitude = point.latitude()
            )
            viewModel.setAreal(
                areal = calculatePolygonArea(listOf(cleanedPolygon)).toFloat(),
            )
            viewModel.updateAllApi()
            onComplete(listOf(cleanedPolygon))

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
        val offset = getSheetMapOffset()

        loadHouse(point, onComplete = { polygon ->
            mapEaseTo(calculateCentroid(polygon), 1000, offset)
            viewModel.setGeoAddress(point)
        })
        return true
    }

    fun settNyttPunkt(point: Point) : Boolean{
        clearScreen()
        //viewModel.setGeoAddress(point)
        val offset = getSheetMapOffset()
        mapEaseTo(point, 2000, offset)
        //loadHouse(point, delay = 2400)
        return true
    }

    fun handleDraggedConrner (draggedPoint: Point, index: Int) {
        val newPolygon = config.value.polygon!![0].toMutableList()
        newPolygon[index] = draggedPoint
        val nyListe = listOf(newPolygon)
        viewModel.setAreal(calculatePolygonArea(nyListe).toFloat())
        viewModel.setPolygon(nyListe)
    }

    val pointIcon = rememberIconImage(key = "point-icon", painter = painterResource(id = R.drawable.polygon_corner))

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
            if (config.value.polygon.isNullOrEmpty().not()) {

                PolygonAnnotation(
                    points = config.value.polygon!!,

                ) {
                    fillColor = Color.Blue
                    fillOpacity = 0.3

                }
                config.value.polygon!![0].forEachIndexed { index, point ->
                    PointAnnotation(
                        point = point
                    ) {
                        iconImage = pointIcon
                        iconSize = 1.0
                        interactionsState.isDraggable = true
                        interactionsState.onDragged {
                            handleDraggedConrner(it.point, index)
                        }
                    }
                }
            }
        }

        SearchBar(
            placeAutocomplete = placeAutoComplete,
            onLocationSelected = { point -> settNyttPunkt(point) },
            modifier = Modifier.padding(top = 26.dp),
            isMapClicked = mapClicked
        )
    }
    LaunchedEffect(config.value.latitude, config.value.longitude) {
        if (config.value.latitude != 0.0 && config.value.longitude != 0.0) {
            val point = Point.fromLngLat(config.value.longitude, config.value.latitude)
            mapViewportState.easeTo(
                cameraOptions = CameraOptions.Builder()
                    .center(point)
                    .zoom(18.0)
                    .build()
            )
            loadHouse(point) // Will update polygon and area
        }
    }
}