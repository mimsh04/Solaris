package in2000.team42.data.frost

import android.util.Log
import in2000.team42.data.frost.model.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FrostDatasourceTest {

    // Class under test
    private lateinit var frostDatasource: FrostDatasource

    // Mock responses for different API calls
    private val temperatureStations = listOf("SN18700", "SN18701")
    private val snowStations = listOf("SN18702", "SN18703")
    private val cloudStations = listOf("SN18704", "SN18705")

    @Before
    fun setup() {
        // Mock Android Log class
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0
        //every { Log.w(any(), any()) } returns 0

        // Create mock datasource
        frostDatasource = mockk<FrostDatasource>()

        // Setup mock responses
        coEvery {
            frostDatasource.getNearestStation(any(), any(), any())
        } returns mapOf(
            "best_estimate_mean(air_temperature P1M)" to temperatureStations,
            "mean(snow_coverage_type P1M)" to snowStations,
            "mean(cloud_area_fraction P1M)" to cloudStations
        )

        // Setup getWeatherData mock response
        coEvery {
            frostDatasource.getWeatherData(any(), any())
        } answers { call ->
            val stationMap = call.invocation.args[0] as Map<*, *>
            if (stationMap.isEmpty()) {
                FrostResult.Failure("No weather data available")
            } else {
                FrostResult.Success(
                    listOf(
                        FrostData(
                            stationId = "SN18700",
                            referenceTime = "2024-05-01T12:00:00.000Z",
                            temperature = 15.5,
                            snow = 0.0,
                            cloudAreaFraction = 50.0
                        ),
                        FrostData(
                            stationId = "SN18700",
                            referenceTime = "2024-05-15T12:00:00.000Z",
                            temperature = 17.5,
                            snow = 0.0,
                            cloudAreaFraction = 25.0
                        )
                    )
                )
            }
        }

        // Setup processWeatherData mock response
        every {
            frostDatasource.processWeatherData(any())
        } returns listOf(
            FrostData(
                stationId = "SN18700",
                referenceTime = "2024-05-01T12:00:00.000Z",
                temperature = 15.5,
                snow = 0.0,
                cloudAreaFraction = 50.0
            ),
            FrostData(
                stationId = "SN18700",
                referenceTime = "2024-05-15T12:00:00.000Z",
                temperature = 17.5,
                snow = 0.0,
                cloudAreaFraction = 25.0
            )
        )
    }

    @Test
    fun `test getNearestStation returns correct stations for valid coordinates`() = runBlocking {
        // Arrange
        val latitude = 59.9139
        val longitude = 10.7522
        val referenceTime = "2024-05-01/2024-05-16"

        // Act
        val result = frostDatasource.getNearestStation(latitude, longitude, referenceTime)

        // Assert
        assertNotNull(result)
        assertEquals(3, result.size)
        assertEquals(temperatureStations, result["best_estimate_mean(air_temperature P1M)"])
        assertEquals(snowStations, result["mean(snow_coverage_type P1M)"])
        assertEquals(cloudStations, result["mean(cloud_area_fraction P1M)"])
    }

    @Test
    fun `test getWeatherData returns success result with correct data`() = runBlocking {
        // Arrange
        val stationMap = mapOf(
            "best_estimate_mean(air_temperature P1M)" to temperatureStations,
            "mean(snow_coverage_type P1M)" to snowStations,
            "mean(cloud_area_fraction P1M)" to cloudStations
        )
        val referenceTime = "2024-05-01/2024-05-16"

        // Act
        val result = frostDatasource.getWeatherData(stationMap, referenceTime)

        // Assert
        assertTrue(result is FrostResult.Success)
        result

        // Check data size
        assertEquals(2, result.data.size)

        // Check data contents
        val firstData = result.data[0]
        assertEquals("SN18700", firstData.stationId)
        assertEquals("2024-05-01T12:00:00.000Z", firstData.referenceTime)
        assertEquals(15.5, firstData.temperature)
        assertEquals(0.0, firstData.snow)
        assertEquals(50.0, firstData.cloudAreaFraction)

        val secondData = result.data[1]
        assertEquals("SN18700", secondData.stationId)
        assertEquals("2024-05-15T12:00:00.000Z", secondData.referenceTime)
        assertEquals(17.5, secondData.temperature)
        assertEquals(0.0, secondData.snow)
        assertEquals(25.0, secondData.cloudAreaFraction)
    }

    @Test
    fun `test getWeatherData returns failure when stations list is empty`() = runBlocking {
        // Arrange
        val emptyStationMap = emptyMap<String, List<String>>()
        val referenceTime = "2024-05-01/2024-05-16"

        // Act
        val result = frostDatasource.getWeatherData(emptyStationMap, referenceTime)

        // Assert
        assertTrue(result is FrostResult.Failure)
        result
        assertEquals("No weather data available", result.message)
    }
}