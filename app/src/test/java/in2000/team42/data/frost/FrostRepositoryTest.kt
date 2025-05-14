package in2000.team42.data.frost

import android.util.Log
import in2000.team42.data.frost.model.FrostData
import in2000.team42.data.frost.model.FrostResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FrostRepositoryTest {

    private lateinit var dataSource: FrostDatasource
    private lateinit var repository: FrostRepository

    @Before
    fun setup() {
        dataSource = mockk()
        repository = FrostRepository(dataSource)

        // Mock Android Log class to prevent LogCat errors in unit tests
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
    }

    @Test
    fun `get1YearReferenceTime returns proper date format`() {
        val referenceTime = repository.get1YearReferenceTime()

        // Check if the format is correct (YYYY-MM-DD/YYYY-MM-DD)
        val regex = """\d{4}-\d{2}-\d{2}/\d{4}-\d{2}-\d{2}"""
        assertTrue("Reference time should match the format YYYY-MM-DD/YYYY-MM-DD",
            referenceTime.matches(Regex(regex)))

        // Verify the dates are one year apart
        val parts = referenceTime.split("/")
        val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(parts[0])
        val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(parts[1])

        val startCalendar = Calendar.getInstance()
        if (startDate != null) {
            startCalendar.time = startDate
        }

        val endCalendar = Calendar.getInstance()
        if (endDate != null) {
            endCalendar.time = endDate
        }

        // Check if the dates are approximately one year apart (365-366 days)
        val differenceInDays = (endDate.time - startDate.time) / (1000 * 60 * 60 * 24)
        assertTrue("Date difference should be approximately 365-366 days",
            differenceInDays.toDouble() in 365.0..366.0)
    }

    @Test
    fun `getWeatherByCoordinates returns success with data when data source returns valid stations and weather data`() = runBlocking {
        // Given
        val latitude = 59.9139
        val longitude = 10.7522
        val referenceTime = "2023-01-01/2024-01-01"

        val stationMap = mapOf(
            "best_estimate_mean(air_temperature P1M)" to listOf("SN18700", "SN18701"),
            "mean(snow_coverage_type P1M)" to listOf("SN18700"),
            "mean(cloud_area_fraction P1M)" to listOf("SN18700")
        )

        val frostData = listOf(
            FrostData(
                stationId = "SN18700",
                referenceTime = "2023-01-15T12:00:00Z",
                temperature = -2.5,
                snow = 0.8,
                cloudAreaFraction = 75.0
            ),
            FrostData(
                stationId = "SN18700",
                referenceTime = "2023-02-15T12:00:00Z",
                temperature = 0.5,
                snow = 0.5,
                cloudAreaFraction = 50.0
            )
        )

        coEvery { dataSource.getNearestStation(latitude, longitude, referenceTime) } returns stationMap
        coEvery { dataSource.getWeatherData(stationMap, referenceTime) } returns FrostResult.Success(frostData)

        // When
        val result = repository.getWeatherByCoordinates(latitude, longitude, referenceTime)

        // Then
        assertTrue(result is FrostResult.Success)
        val successResult = result as FrostResult.Success
        assertEquals(2, successResult.data.size)
        assertEquals(-2.5, successResult.data[0].temperature ?: 0.0, 0.01)
        assertEquals(0.8, successResult.data[0].snow ?: 0.0, 0.01)
        assertEquals(75.0, successResult.data[0].cloudAreaFraction ?: 0.0, 0.01)
    }

    @Test
    fun `getWeatherByCoordinates returns failure when no stations found`() = runBlocking {
        // Given
        val latitude = 59.9139
        val longitude = 10.7522
        val referenceTime = "2023-01-01/2024-01-01"

        coEvery { dataSource.getNearestStation(latitude, longitude, referenceTime) } returns null

        // When
        val result = repository.getWeatherByCoordinates(latitude, longitude, referenceTime)

        // Then
        assertTrue(result is FrostResult.Failure)
        val failureResult = result as FrostResult.Failure
        assertEquals("No station found for coordinates ($latitude, $longitude)", failureResult.message)
    }

    @Test
    fun `getWeatherByCoordinates returns failure when data source fails to get weather data`() = runBlocking {
        // Given
        val latitude = 59.9139
        val longitude = 10.7522
        val referenceTime = "2023-01-01/2024-01-01"

        val stationMap = mapOf(
            "best_estimate_mean(air_temperature P1M)" to listOf("SN18700"),
            "mean(snow_coverage_type P1M)" to listOf("SN18700"),
            "mean(cloud_area_fraction P1M)" to listOf("SN18700")
        )

        val errorMessage = "API request failed due to server error"

        coEvery { dataSource.getNearestStation(latitude, longitude, referenceTime) } returns stationMap
        coEvery { dataSource.getWeatherData(stationMap, referenceTime) } returns FrostResult.Failure(errorMessage)

        // When
        val result = repository.getWeatherByCoordinates(latitude, longitude, referenceTime)

        // Then
        assertTrue(result is FrostResult.Failure)
        val failureResult = result as FrostResult.Failure
        assertEquals(errorMessage, failureResult.message)
    }

    @Test
    fun `getWeatherByCoordinates handles null values in weather data`() = runBlocking {
        // Given
        val latitude = 59.9139
        val longitude = 10.7522
        val referenceTime = "2023-01-01/2024-01-01"

        val stationMap = mapOf(
            "best_estimate_mean(air_temperature P1M)" to listOf("SN18700"),
            "mean(snow_coverage_type P1M)" to listOf("SN18700"),
            "mean(cloud_area_fraction P1M)" to listOf("SN18700")
        )

        val frostData = listOf(
            FrostData(
                stationId = "SN18700",
                referenceTime = "2023-01-15T12:00:00Z",
                temperature = 2.5,
                snow = null,  // Null snow data
                cloudAreaFraction = 62.5
            ),
            FrostData(
                stationId = "SN18700",
                referenceTime = "2023-02-15T12:00:00Z",
                temperature = null,  // Null temperature data
                snow = 0.3,
                cloudAreaFraction = null  // Null cloud area fraction
            )
        )

        coEvery { dataSource.getNearestStation(latitude, longitude, referenceTime) } returns stationMap
        coEvery { dataSource.getWeatherData(stationMap, referenceTime) } returns FrostResult.Success(frostData)

        // When
        val result = repository.getWeatherByCoordinates(latitude, longitude, referenceTime)

        // Then
        assertTrue(result is FrostResult.Success)
        val successResult = result as FrostResult.Success
        assertEquals(2, successResult.data.size)

        // First data point
        assertEquals(2.5, successResult.data[0].temperature ?: 0.0, 0.01)
        assertEquals(null, successResult.data[0].snow)
        assertEquals(62.5, successResult.data[0].cloudAreaFraction ?: 0.0, 0.01)

        // Second data point
        assertEquals(null, successResult.data[1].temperature)
        assertEquals(0.3, successResult.data[1].snow ?: 0.0, 0.01)
        assertEquals(null, successResult.data[1].cloudAreaFraction)
    }
}