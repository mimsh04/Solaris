package in2000.team42

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import in2000.team42.data.frost.FrostDatasource
import in2000.team42.data.frost.model.FrostResult
import java.net.HttpURLConnection
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Kontekst av app under test
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("in2000.team42", appContext.packageName)
    }
}

@RunWith(AndroidJUnit4::class)
class FrostDatasourceInstrumentedTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var datasource: FrostDatasource

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        datasource = FrostDatasource()
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testSuccessfulGetNearestStation() = runBlocking {
        // Lager et liksom korrekt svar for getNearestStation
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("""
                {"data": [
                    {"id": "SN18700"},
                    {"id": "SN18701"},
                    {"id": "SN18702"}
                ]}
            """)
        mockWebServer.enqueue(mockResponse)

        val result = datasource.getNearestStation(60.0, 10.0, "2024-01-01/2024-01-31")
        Log.d("test",result.toString())
        // Sjekker om resultat er null
        assertNotNull(result)

        //Sjekker antall nøkler som blir returnert er lik 3
        assertEquals(3, result?.size)
        //Bekrefter at det forste elementet i nokklene er SN18700
        assertEquals("SN18700", result?.values?.firstOrNull()?.firstOrNull())

        // Sjekker oppbygning av foresporsel
        val request = mockWebServer.takeRequest()
        assertEquals("/sources/v0.jsonld", request.path)
        assertEquals("nearest(POINT(10.0 60.0))", request.requestUrl?.queryParameter("geometry"))
        assertEquals("2024-01-01/2024-01-31", request.requestUrl?.queryParameter("validtime"))
        //Sjekker om hvert element eksisterer i spørringen
        assertEquals(true,request.requestUrl?.queryParameter("elements") != null)

    }

    @Test
    fun testSuccessfulGetWeatherData() = runBlocking {
        // Lager et liksom korrekt svar for getWeatherData
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("""
            {
              "data": [
                {
                  "sourceId": "SN18700",
                  "referenceTime": "2024-01-01T00:00:00Z",
                  "observations": [
                    {
                      "elementId": "best_estimate_mean(air_temperature P1M)",
                      "value": "10.0"
                    },
                    {
                      "elementId": "mean(snow_coverage_type P1M)",
                      "value": "0.5"
                    },
                    {
                      "elementId": "mean(cloud_area_fraction P1M)",
                      "value": "0.8"
                    }
                  ]
                }
              ]
            }
        """)
        mockWebServer.enqueue(mockResponse)

        // Lager liksom map
        val stationMap = mapOf(
            "best_estimate_mean(air_temperature P1M)" to listOf("SN18700"),
            "mean(snow_coverage_type P1M)" to listOf("SN18700"),
            "mean(cloud_area_fraction P1M)" to listOf("SN18700")
        )

        val result = datasource.getWeatherData(stationMap, "2024-01-01/2024-01-31")

        // Sjekker resultate
        if (result is FrostResult.Success) {
            assertEquals(true, result.data.isNotEmpty())
            assertEquals(10.0, result.data[0].temperature)
            assertEquals(10.0, result.data[0].cloudAreaFraction)
            assertEquals(0.5, result.data[0].snow)
        }

        // Sjekker forespørselen
        val request = mockWebServer.takeRequest()
        assertEquals("/observations/v0.jsonld", request.path)
        assertEquals("SN18700", request.requestUrl?.queryParameter("sources"))
        assertEquals("2024-01-01/2024-01-31", request.requestUrl?.queryParameter("referencetime"))
        //Sjekker hvert element eksisterer i spørringen
        assertEquals(true,request.requestUrl?.queryParameter("elements") != null)

    }
}