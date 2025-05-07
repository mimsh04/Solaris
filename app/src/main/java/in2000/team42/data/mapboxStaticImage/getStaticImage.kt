package in2000.team42.data.mapboxStaticImage

import android.util.Log
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon

fun getStaticImage(
    longitude: Double,
    latitude: Double,
    accessToken: String,
    width: Int = 600,
    height: Int = 600,
    zoom: Int = 10,
    polygon: List<List<Point>>,
    mapType: String,
): String {
    val formatedPolygon = Polygon.fromLngLats(polygon).toJson()
    val imageLink = "https://api.mapbox.com/styles/v1/mapbox/" +
            "${mapType}/static/" +
            "geojson(${formatedPolygon})" +
            "${longitude},${latitude}," +
            "0,${zoom}/${width}x${height}?access_token=${accessToken}"
    Log.d("MapboxImage", imageLink)
    return imageLink
}