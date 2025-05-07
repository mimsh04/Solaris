package in2000.team42.data.mapboxStaticImage

import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun getStaticImage(
    longitude: Double,
    latitude: Double,
    accessToken: String,
    width: Int = 600,
    height: Int = 600,
    zoom: Float = 10f,
    polygon: List<List<Point>>,
    mapType: String,
): String {
    val fixedPolygon = polygon[0].toMutableList()
    fixedPolygon.add(fixedPolygon[0])
    val formattedPolygon = Polygon.fromLngLats(listOf(fixedPolygon)).toJson()
    val encodedPolygon = URLEncoder.encode(formattedPolygon, StandardCharsets.UTF_8.toString())
    val imageLink = "https://api.mapbox.com/styles/v1/mapbox/" +
            "${mapType.split("/").last()}/static/" +
            "geojson%28${encodedPolygon}%29/" +
            "${longitude},${latitude}," +
            "${zoom},0,0/${width}x${height}?access_token=${accessToken}"
    return imageLink
}