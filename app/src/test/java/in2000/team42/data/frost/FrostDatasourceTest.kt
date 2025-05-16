package in2000.team42.data.frost

import in2000.team42.data.frost.model.FrostData
import in2000.team42.data.frost.model.FrostErrorResponse
import in2000.team42.data.frost.model.FrostObservation
import in2000.team42.data.frost.model.FrostResponse
import in2000.team42.data.frost.model.FrostResult
import in2000.team42.data.frost.model.Observation
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Field
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FrostDatasourceTest {

    private lateinit var frostDatasource: FrostDatasource
    private val mockClient = mockk<HttpClient>()
    private val mockResponse = mockk<HttpResponse>()
    private val testDispatcher = StandardTestDispatcher()

    private val latitude = 59.91
    private val longitude = 10.74
    private val referenceTime = "2024-01-01/2024-12-31"

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        frostDatasource = FrostDatasource()

        // Use reflection to set the mocked HttpClient
        val clientField: Field = FrostDatasource::class.java.getDeclaredField("client")
        clientField.isAccessible = true
        clientField.set(frostDatasource, mockClient)
        clientField.isAccessible = false
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getNearestStation returns station IDs for valid coordinates`() = runTest(testDispatcher) {
        // Arrange
        val sourceResponse = """
        {
            "data": [
                {"id": "SN18700"},
                {"id": "SN18701"}
            ]
        }
        """.trimIndent()

        val elements = listOf(
            "best_estimate_mean(air_temperature P1M)",
            "mean(snow_coverage_type P1M)",
            "mean(cloud_area_fraction P1M)"
        )

        elements.forEach { element ->
            coEvery {
                mockClient.get("https://frost.met.no/sources/v0.jsonld") {
                    parameter("geometry", "nearest(POINT($longitude $latitude))")
                    parameter("validtime", referenceTime)
                    parameter("nearestmaxcount", 2)
                    parameter("elements", element)
                }
            } returns mockResponse
        }

        coEvery { mockResponse.body<String>() } returns sourceResponse
        coEvery { mockResponse.status } returns HttpStatusCode.OK

        // Act
        val result = frostDatasource.getNearestStation(latitude, longitude, referenceTime)

        // Assert
        assertNotNull(result)
        assertEquals(3, result.size)
        elements.forEach { element ->
            assertEquals(listOf("SN18700", "SN18701"), result[element])
        }
    }

    @Test
    fun `getNearestStation returns null when no stations found`() = runTest(testDispatcher) {
        // Arrange
        val elements = listOf(
            "best_estimate_mean(air_temperature P1M)",
            "mean(snow_coverage_type P1M)",
            "mean(cloud_area_fraction P1M)"
        )

        elements.forEach { element ->
            coEvery {
                mockClient.get("https://frost.met.no/sources/v0.jsonld") {
                    parameter("geometry", "nearest(POINT($longitude $latitude))")
                    parameter("validtime", referenceTime)
                    parameter("nearestmaxcount", 2)
                    parameter("elements", element)
                }
            } throws Exception("API error")
        }

        // Act
        val result = frostDatasource.getNearestStation(latitude, longitude, referenceTime)

        // Assert
        assertNull(result)
    }

    @Test
    fun `getNearestStation handles partial station data`() = runTest(testDispatcher) {
        // Arrange
        val sourceResponseTemp = """
        {
            "data": [
                {"id": "SN18700"}
            ]
        }
        """.trimIndent()

        val sourceResponseEmpty = """
        {
            "data": []
        }
        """.trimIndent()

        coEvery {
            mockClient.get("https://frost.met.no/sources/v0.jsonld") {
                parameter("geometry", "nearest(POINT($longitude $latitude))")
                parameter("validtime", referenceTime)
                parameter("nearestmaxcount", 2)
                parameter("elements", "best_estimate_mean(air_temperature P1M)")
            }
        } returns mockResponse

        coEvery {
            mockClient.get("https://frost.met.no/sources/v0.jsonld") {
                parameter("geometry", "nearest(POINT($longitude $latitude))")
                parameter("validtime", referenceTime)
                parameter("nearestmaxcount", 2)
                parameter("elements", "mean(snow_coverage_type P1M)")
            }
        } returns mockResponse

        coEvery {
            mockClient.get("https://frost.met.no/sources/v0.jsonld") {
                parameter("geometry", "nearest(POINT($longitude $latitude))")
                parameter("validtime", referenceTime)
                parameter("nearestmaxcount", 2)
                parameter("elements", "mean(cloud_area_fraction P1M)")
            }
        } returns mockResponse

        coEvery { mockResponse.body<String>() } returnsMany listOf(
            sourceResponseTemp,
            sourceResponseEmpty,
            sourceResponseEmpty
        )
        coEvery { mockResponse.status } returns HttpStatusCode.OK

        // Act
        val result = frostDatasource.getNearestStation(latitude, longitude, referenceTime)

        // Assert
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(listOf("SN18700"), result["best_estimate_mean(air_temperature P1M)"])
        assertNull(result["mean(snow_coverage_type P1M)"])
        assertNull(result["mean(cloud_area_fraction P1M)"])
    }

    @Test
    fun `getWeatherData returns Success with valid data`() = runTest(testDispatcher) {
        // Arrange
        val stationMap = mapOf(
            "best_estimate_mean(air_temperature P1M)" to listOf("SN18700"),
            "mean(snow_coverage_type P1M)" to listOf("SN18700"),
            "mean(cloud_area_fraction P1M)" to listOf("SN18700")
        )

        val frostResponse = FrostResponse(
            data = listOf(
                FrostObservation(
                    sourceId = "SN18700:0",
                    referenceTime = "2024-01-01T00:00:00Z",
                    observations = listOf(
                        Observation(
                            elementId = "best_estimate_mean(air_temperature P1M)",
                            value = 5.0f,
                            qualityCode = 0
                        ),
                        Observation(
                            elementId = "mean(snow_coverage_type P1M)",
                            value = 10.0f,
                            qualityCode = 0
                        ),
                        Observation(
                            elementId = "mean(cloud_area_fraction P1M)",
                            value = 8.0f,
                            qualityCode = 0
                        )
                    )
                )
            )
        )

        coEvery {
            mockClient.get("https://frost.met.no/observations/v0.jsonld") {
                parameter("sources", "SN18700")
                parameter("elements", any<String>())
                parameter("referencetime", referenceTime)
            }
        } returns mockResponse

        coEvery { mockResponse.status } returns HttpStatusCode.OK
        coEvery { mockResponse.body<FrostResponse>() } returns frostResponse

        // Act
        val result = frostDatasource.getWeatherData(stationMap, referenceTime)

        // Assert
        assertTrue(result is FrostResult.Success)
        val data = result.data
        assertEquals(1, data.size)
        assertEquals("SN18700", data[0].stationId)
        assertEquals("2024-01-01T00:00:00Z", data[0].referenceTime)
        assertEquals(5.0, data[0].temperature)
        assertEquals(10.0, data[0].snow)
        assertEquals(100.0, data[0].cloudAreaFraction)
    }

    @Test
    fun `getWeatherData returns Failure when API returns error`() = runTest(testDispatcher) {
        // Arrange
        val stationMap = mapOf(
            "best_estimate_mean(air_temperature P1M)" to listOf("SN18700")
        )

        val errorResponse = FrostErrorResponse(
            error = FrostErrorResponse.ErrorDetails(
                code = 404,
                message = "Not found",
                reason = "No data available"
            )
        )

        coEvery {
            mockClient.get("https://frost.met.no/observations/v0.jsonld") {
                parameter("sources", "SN18700")
                parameter("elements", "best_estimate_mean(air_temperature P1M)")
                parameter("referencetime", referenceTime)
            }
        } returns mockResponse

        coEvery { mockResponse.status } returns HttpStatusCode.NotFound
        coEvery { mockResponse.body<FrostErrorResponse>() } returns errorResponse

        // Act
        val result = frostDatasource.getWeatherData(stationMap, referenceTime)

        // Assert
        assertTrue(result is FrostResult.Failure)
        assertEquals("No data available", result.message)
    }

    @Test
    fun `getWeatherData returns Failure when no valid responses`() = runTest(testDispatcher) {
        // Arrange
        val stationMap = mapOf(
            "best_estimate_mean(air_temperature P1M)" to listOf("SN18700")
        )

        coEvery {
            mockClient.get("https://frost.met.no/observations/v0.jsonld") {
                parameter("sources", "SN18700")
                parameter("elements", "best_estimate_mean(air_temperature P1M)")
                parameter("referencetime", referenceTime)
            }
        } throws Exception("Network error")

        // Act
        val result = frostDatasource.getWeatherData(stationMap, referenceTime)

        // Assert
        assertTrue(result is FrostResult.Failure)
        assertEquals("No weather data available", result.message)
    }

    @Test
    fun `processWeatherData handles empty responses`() = runTest(testDispatcher) {
        // Arrange
        val responses = emptyList<FrostResponse>()

        // Act
        val result = frostDatasource.processWeatherData(responses)

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `processWeatherData handles null values in observations`() = runTest(testDispatcher) {
        // Arrange
        val responses = listOf(
            FrostResponse(
                data = listOf(
                    FrostObservation(
                        sourceId = "SN18700:0",
                        referenceTime = "2024-01-01T00:00:00Z",
                        observations = listOf(
                            Observation(
                                elementId = "best_estimate_mean(air_temperature P1M)",
                                value = null,
                                qualityCode = 0
                            )
                        )
                    )
                )
            )
        )

        // Act
        val result = frostDatasource.processWeatherData(responses)

        // Assert
        assertEquals(1, result.size)
        assertEquals("2024-01-01T00:00:00Z", result[0].referenceTime)
        assertNull(result[0].temperature)
        assertNull(result[0].snow)
        assertNull(result[0].cloudAreaFraction)
    }
}