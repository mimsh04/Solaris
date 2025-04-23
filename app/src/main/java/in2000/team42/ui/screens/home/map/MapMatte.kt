package in2000.team42.ui.screens.home.map

import com.mapbox.geojson.Point

fun calculateCentroid(input: List<List<Point>>): Point {

    var sumLat = 0.0
    var sumLon = 0.0
    val polygon = input[0]
    polygon.forEach {
        sumLat += it.latitude()
        sumLon += it.longitude()
    }

    return Point.fromLngLat(
        sumLon / polygon.size,
        sumLat / polygon.size
    )
}

fun calculatePolygonArea(mapPolygon: List<List<Point>>): Double {
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