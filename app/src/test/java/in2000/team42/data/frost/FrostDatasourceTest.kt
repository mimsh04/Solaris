package in2000.team42.data.frost

import in2000.team42.data.frost.model.FrostData
import in2000.team42.data.frost.model.FrostErrorResponse
import in2000.team42.data.frost.model.FrostObservation
import in2000.team42.data.frost.model.FrostResponse
import in2000.team42.data.frost.model.FrostResult
import in2000.team42.data.frost.model.Observation
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FrostDatasourceTest {

    private lateinit var frostDatasource: FrostDatasource
    private val mockClient = mockk<HttpClient>()
    private val mockResponse = mockk<HttpResponse>()

    private val latitude = 59.91
    private val longitude = 10.74
    private val referenceTime = "2024-01-01/2024-12-31"

    @Before
    fun setup() {
        // Mock the HttpClient creation
        mockkStatic(CIO::class, Auth::class, ContentNegotiation::class)
        frostDatasource = FrostDatasource()
        // Use reflection to set the private client field
        val clientField = FrostDatasource::class.java.getDeclaredField("client")
        clientField.isAccessible = true
        clientField.set(frostDatasource, mockClient)

        // Mock Json configuration
        every { Json { any() } } returns Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
    }

    @Test
    fun `getNearestStation returns station IDs for valid coordinates`() = runBlocking {
        // Arrange
        val sourceResponse = """
        {
            "data": [
                {"id": "SN18700"},
                {"id": "SN18701"}
            ]
        }
    """.trimIndent()

        coEvery {
            mockClient.get("https://frost.met.no/sources/v0.jsonld") {
                parameter("geometry", "nearest(POINT($longitude $latitude))")
                parameter("validtime", referenceTime)
                parameter("nearestmaxcount", 2)
                parameter("elements", any<String>())
            }
        } returns mockResponse

        coEvery { mockResponse.body<String>() } returns sourceResponse
        coEvery { mockResponse.status } returns HttpStatusCode.OK

        // Act
        val result = frostDatasource.getNearestStation(latitude, longitude, referenceTime)

        // Assert
        assertNotNull(result)
        assertEquals(3, result.size) // One for each element (temp, snow, cloud)
        assertEquals(listOf("SN18700", "SN18701"), result["best_estimate_mean(air_temperature P1M)"])
        assertEquals(listOf("SN18700", "SN18701"), result["mean(snow_coverage_type P1M)"])
        assertEquals(listOf("SN18700", "SN18701"), result["mean(cloud_area_fraction P1M)"])
    }

    @Test
    fun `getNearestStation returns null when no stations found`() = runBlocking {
        // Arrange
        coEvery {
            mockClient.get("https://frost.met.no/sources/v0.jsonld") {
                parameter("geometry", "nearest(POINT($longitude $latitude))")
                parameter("validtime", referenceTime)
                parameter("nearestmaxcount", 2)
                parameter("elements", any<String>())
            }
        } throws Exception("API error")

        // Act
        val result = frostDatasource.getNearestStation(latitude, longitude, referenceTime)

        // Assert
        assertNull(result)
    }

    @Test
    fun `getWeatherData returns Success with valid data`() = runBlocking {
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
                        )
                    )
                ),
                FrostObservation(
                    sourceId = "SN18700:0",
                    referenceTime = "2024-01-01T00:00:00Z",
                    observations = listOf(
                        Observation(
                            elementId = "mean(snow_coverage_type P1M)",
                            value = 10.0f,
                            qualityCode = 0
                        )
                    )
                ),
                FrostObservation(
                    sourceId = "SN18700:0",
                    referenceTime = "2024-01-01T00:00:00Z",
                    observations = listOf(
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
        assertEquals(5.0, data[0].temperature)
        assertEquals(10.0, data[0].snow)
        assertEquals(100.0, data[0].cloudAreaFraction) // 8.0 * 12.5
    }

    @Test
    fun `getWeatherData returns Failure when API returns error`() = runBlocking {
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
                parameter("elements", any<String>())
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
    fun `processWeatherData combines data correctly`() = runBlocking {
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
                                value = 5.0f,
                                qualityCode = 0
                            )
                        )
                    )
                )
            ),
            FrostResponse(
                data = listOf(
                    FrostObservation(
                        sourceId = "SN18700:0",
                        referenceTime = "2024-01-01T00:00:00Z",
                        observations = listOf(
                            Observation(
                                elementId = "mean(cloud_area_fraction P1M)",
                                value = 8.0f,
                                qualityCode = 0
                            )
                        )
                    )
                )
            )
        )

        // Use reflection to access private method
        val processWeatherDataMethod = FrostDatasource::class.java.getDeclaredMethod(
            "processWeatherData",
            List::class.java
        )
        processWeatherDataMethod.isAccessible = true

        // Act
        val result = withContext(Dispatchers.IO) {
            processWeatherDataMethod.invoke(frostDatasource, responses) as List<FrostData>
        }

        // Assert
        assertEquals(1, result.size)
        val frostData = result[0]
        assertEquals("SN18700", frostData.stationId)
        assertEquals("2024-01-01T00:00:00Z", frostData.referenceTime)
        assertEquals(5.0, frostData.temperature)
        assertEquals(100.0, frostData.cloudAreaFraction) // 8.0 * 12.5
        assertNull(frostData.snow)
    }

    @Test
    fun `processWeatherData handles empty responses`() = runBlocking {
        // Arrange
        val responses = emptyList<FrostResponse>()

        // Use reflection to access private method
        val processWeatherDataMethod = FrostDatasource::class.java.getDeclaredMethod(
            "processWeatherData",
            List::class.java
        )
        processWeatherDataMethod.isAccessible = true

        // Act
        val result = withContext(Dispatchers.IO) {
            processWeatherDataMethod.invoke(frostDatasource, responses) as List<FrostData>
        }

        // Assert
        assertTrue(result.isEmpty())
    }

}