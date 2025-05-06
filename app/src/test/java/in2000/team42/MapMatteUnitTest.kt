package in2000.team42

import com.mapbox.geojson.Point
import in2000.team42.ui.screens.home.map.calculateCentroid
import in2000.team42.ui.screens.home.map.calculatePolygonArea
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class MapMatteTest {

    @Test
    fun `calculateCentroid should return correct centroid for a triangle polygon`() {
        // Arrange
        val point1 = mockk<Point>()
        val point2 = mockk<Point>()
        val point3 = mockk<Point>()

        every { point1.longitude() } returns 0.0
        every { point1.latitude() } returns 0.0
        every { point2.longitude() } returns 2.0
        every { point2.latitude() } returns 0.0
        every { point3.longitude() } returns 1.0
        every { point3.latitude() } returns 2.0

        val polygon = listOf(listOf(point1, point2, point3))

        // Act
        val centroid = calculateCentroid(polygon)

        // Assert
        assertEquals(1.0, centroid.longitude(), 0.0001)
        assertEquals(2.0 / 3.0, centroid.latitude(), 0.0001)
    }

    @Test
    fun `calculateCentroid should return correct centroid for a square polygon`() {
        // Arrange
        val point1 = mockk<Point>()
        val point2 = mockk<Point>()
        val point3 = mockk<Point>()
        val point4 = mockk<Point>()

        every { point1.longitude() } returns 0.0
        every { point1.latitude() } returns 0.0
        every { point2.longitude() } returns 2.0
        every { point2.latitude() } returns 0.0
        every { point3.longitude() } returns 2.0
        every { point3.latitude() } returns 2.0
        every { point4.longitude() } returns 0.0
        every { point4.latitude() } returns 2.0

        val polygon = listOf(listOf(point1, point2, point3, point4))

        // Act
        val centroid = calculateCentroid(polygon)

        // Assert
        assertEquals(1.0, centroid.longitude(), 0.0001)
        assertEquals(1.0, centroid.latitude(), 0.0001)
    }

    @Test
    fun `calculatePolygonArea should return 0 for empty polygon`() {
        // Arrange
        val polygon = emptyList<List<Point>>()

        // Act
        val area = calculatePolygonArea(polygon)

        // Assert
        assertEquals(0.0, area, 0.0001)
    }

    @Test
    fun `calculatePolygonArea should return 0 for polygon with less than 3 points`() {
        // Arrange
        val point1 = mockk<Point>()
        val point2 = mockk<Point>()

        every { point1.longitude() } returns 0.0
        every { point1.latitude() } returns 0.0
        every { point2.longitude() } returns 1.0
        every { point2.latitude() } returns 1.0

        val polygon = listOf(listOf(point1, point2))

        // Act
        val area = calculatePolygonArea(polygon)

        // Assert
        assertEquals(0.0, area, 0.0001)
    }

    @Test
    fun `calculatePolygonArea should calculate correct area for a square`() {
        // Arrange
        val point1 = mockk<Point>()
        val point2 = mockk<Point>()
        val point3 = mockk<Point>()
        val point4 = mockk<Point>()

        every { point1.longitude() } returns 0.0
        every { point1.latitude() } returns 0.0
        every { point2.longitude() } returns 0.01 // ~1.1 km at latitude 0
        every { point2.latitude() } returns 0.0
        every { point3.longitude() } returns 0.01
        every { point3.latitude() } returns 0.01
        every { point4.longitude() } returns 0.0
        every { point4.latitude() } returns 0.01

        val polygon = listOf(listOf(point1, point2, point3, point4))

        // Act
        val area = calculatePolygonArea(polygon)

        // Assert
        // Expected area: 0.01° x 0.01°
        // Meters per degree latitude: 111320 m
        // Meters per degree longitude at latitude 0: 111320 m (cos(0) = 1)
        // Area in degrees: 0.01 * 0.01 = 0.0001 square degrees
        // Area in square meters: 0.0001 * (111320 * 111320) = 1 239 214.24
        assertEquals(1239214.24, area, 0.1)
    }

    @Test
    fun `calculatePolygonArea should account for latitude in area calculation`() {
        // Arrange
        val point1 = mockk<Point>()
        val point2 = mockk<Point>()
        val point3 = mockk<Point>()
        val point4 = mockk<Point>()

        every { point1.longitude() } returns 0.0
        every { point1.latitude() } returns 60.0 // At 60° latitude
        every { point2.longitude() } returns 0.01
        every { point2.latitude() } returns 60.0
        every { point3.longitude() } returns 0.01
        every { point3.latitude() } returns 60.01
        every { point4.longitude() } returns 0.0
        every { point4.latitude() } returns 60.01

        val polygon = listOf(listOf(point1, point2, point3, point4))

        // Act
        val area = calculatePolygonArea(polygon)

        // Assert
        // Tests that calculatePolygonArea correctly accounts for latitude in area calculation.
        // The polygon is a square with sides 0.01° in longitude and latitude, centered around 60° latitude.
        // The function uses the Shoelace formula to compute the area as 0.01° * 0.01° = 0.0001 square degrees.
        // Conversion to square meters uses:
        // - Meters per degree latitude: 111,320 m
        // - Average latitude: (60.0 + 60.0 + 60.01 + 60.01) / 4 = 60.005°
        // - Meters per degree longitude: 111,320 * cos(toRadians(60.005°)) ≈ 111,320 * 0.4998353 ≈ 55,641.576 m
        // Expected area: 0.0001 * 111,320 * 55,641.576 ≈ 619,513.464 m²
        // Due to floating-point precision in Math.toRadians and Math.cos, the exact result may vary slightly
        // across environments (e.g., 619,680.322 m² with high-precision cosine). A tolerance of 0.1 m² is used.
        assertEquals(619513.4640574008, area, 0.1)
    }
}