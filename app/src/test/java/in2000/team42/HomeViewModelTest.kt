package in2000.team42

import android.util.Log
import in2000.team42.data.frost.FrostRepository
import in2000.team42.data.frost.model.FrostData
import in2000.team42.data.frost.model.FrostResult
import in2000.team42.data.pgvis.PgvisRepository
import in2000.team42.data.pgvis.PvTech
import in2000.team42.data.pgvis.model.DailyProfile
import in2000.team42.data.pgvis.model.KwhMonthlyResponse
import in2000.team42.data.solarPanels.SolarPanelModel
import in2000.team42.ui.screens.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var pgvisRepository: PgvisRepository
    private lateinit var frostRepository: FrostRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        // Mock dependencies
        pgvisRepository = mockk()
        frostRepository = mockk()

        // Mock Log for debug statements
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.w(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        
        Dispatchers.setMain(testDispatcher)

        // Initialize ViewModel and inject dependencies
        viewModel = HomeViewModel()
        setPrivateField(viewModel, "radiationRepository", pgvisRepository)
        setPrivateField(viewModel, "frostRepository", frostRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setCoordinates updates latitude and longitude in config`() = runTest {
        // Arrange
        val latitude = 59.91
        val longitude = 10.74

        // Act
        viewModel.setCoordinates(longitude, latitude)

        // Assert
        val config = viewModel.configFlow.value
        assertEquals(latitude, config.latitude, "Latitude should be updated")
        assertEquals(longitude, config.longitude, "Longitude should be updated")
    }

    @Test
    fun `setIncline updates incline in config`() = runTest {
        // Arrange
        val incline = 45.0f

        // Act
        viewModel.setIncline(incline)

        // Assert
        val config = viewModel.configFlow.value
        assertEquals(incline, config.incline, "Incline should be updated")
    }

    @Test
    fun `setDirection updates direction in config`() = runTest {
        // Arrange
        val direction = 90.0f

        // Act
        viewModel.setDirection(direction)

        // Assert
        val config = viewModel.configFlow.value
        assertEquals(direction, config.direction, "Direction should be updated")
    }

    @Test
    fun `setArea updates area in config`() = runTest {
        // Arrange
        val area = 2.0f

        // Act
        viewModel.setArea(area)

        // Assert
        val config = viewModel.configFlow.value
        assertEquals(area, config.area, "Area should be updated")
    }

    @Test
    fun `setSelectedSolarPanel updates solar panel in config`() = runTest {
        // Arrange
        val panel = SolarPanelModel(name = "TestPanel", efficiency = 20.0f)

        // Act
        viewModel.setSelectedSolarPanel(panel)

        // Assert
        val config = viewModel.configFlow.value
        assertEquals(panel, config.selectedPanelModel, "Solar panel should be updated")
    }

    @Test
    fun `updateSolarRadiation updates sunRadiation in apiData`() = runTest {
        // Arrange
        val mockRadiationData = listOf(mockk<DailyProfile>())
        coEvery {
            pgvisRepository.getRadiationData(
                lat = 0.0,
                lon = 0.0,
                month = 0,
                incline = 35.0f,
                retning = 0.0f
            )
        } returns mockRadiationData

        // Act
        viewModel.updateSolarRadiation()

        // Assert
        val apiData = viewModel.apiDataFlow.value
        assertEquals(
            mockRadiationData,
            apiData.sunRadiation,
            "Sun radiation data should be updated"
        )
        assertTrue(apiData.isLoading, "isLoading should be true until all data is fetched")
    }

    @Test
    fun `updateKwhMonthly updates kwhMonthlyData in apiData`() = runTest {
        // Arrange
        val mockKwhData = listOf(mockk<KwhMonthlyResponse.MonthlyKwhData>())
        val peakPower = 0.2f // 20% efficiency * 1m²
        coEvery {
            pgvisRepository.getMonthlyKwh(
                lat = 0.0,
                lon = 0.0,
                incline = 35.0f,
                retning = 0.0f,
                peakPower = peakPower,
                pvTech = PvTech.CRYST_SI
            )
        } returns mockKwhData

        // Act
        viewModel.setSelectedSolarPanel(SolarPanelModel(
            name = "TestPanel", efficiency = 20.0f,
            id = TODO(),
            pricePerM2 = TODO()
        ))
        viewModel.updateKwhMonthly()

        // Assert
        val apiData = viewModel.apiDataFlow.value
        assertEquals(mockKwhData, apiData.kwhMonthlyData, "Monthly kWh data should be updated")
        assertTrue(apiData.isLoading, "isLoading should be true until all data is fetched")
    }

    @Test
    fun `updateWeatherData updates weatherData on success`() = runTest {
        // Arrange
        val mockFrostData = listOf(
            FrostData(
                stationId = "SN18700",
                referenceTime = "2024-01-01",
                temperature = 5.0,
                snow = 10.0,
                cloudAreaFraction = 50.0
            )
        )
        coEvery { frostRepository.get1YearReferenceTime() } returns "2024-01-01/2024-12-31"
        coEvery {
            frostRepository.getWeatherByCoordinates(
                latitude = 0.0,
                longitude = 0.0,
                referenceTime = "2024-01-01/2024-12-31"
            )
        } returns FrostResult.Success(mockFrostData)

        // Act
        viewModel.updateWeatherData()

        // Assert
        val apiData = viewModel.apiDataFlow.value
        assertEquals(1, apiData.weatherData.size, "Weather data should contain one entry")
        with(apiData.weatherData[0]) {
            assertEquals("Jan 2024", month, "Month should be formatted correctly")
            assertEquals("5.0°C", temp, "Temperature should be formatted correctly")
            assertEquals("10.0mm", snow, "Snow should be formatted correctly")
            assertEquals("50.0%", cloud, "Cloud fraction should be formatted correctly")
        }
    }

    @Test
    fun `updateWeatherData sets dummy data on failure`() = runTest {
        // Arrange
        coEvery { frostRepository.get1YearReferenceTime() } returns "2024-01-01/2024-12-31"
        coEvery {
            frostRepository.getWeatherByCoordinates(
                latitude = 0.0,
                longitude = 0.0,
                referenceTime = "2024-01-01/2024-12-31"
            )
        } returns FrostResult.Failure("API error")

        // Act
        viewModel.updateWeatherData()

        // Assert
        val apiData = viewModel.apiDataFlow.value
        assertEquals(13, apiData.weatherData.size, "Should return 13 dummy entries")
        assertTrue(
            apiData.weatherData.all { it.month == "ukjent" && it.temp == "ukjent" && it.snow == "ukjent" && it.cloud == "ukjent" },
            "All weather data should be dummy values"
        )
    }

    @Test
    fun `updateAllApi triggers all data updates`() = runTest {
        // Arrange
        val mockRadiationData = listOf(mockk<DailyProfile>())
        val mockKwhData = listOf(mockk<KwhMonthlyResponse.MonthlyKwhData>())
        val mockFrostData = listOf(
            FrostData(
                stationId = "SN18700",
                referenceTime = "2024-01-01",
                temperature = 5.0,
                snow = null,
                cloudAreaFraction = null
            )
        )
        coEvery {
            pgvisRepository.getRadiationData(
                lat = 0.0,
                lon = 0.0,
                month = 0,
                incline = 35.0f,
                retning = 0.0f
            )
        } returns mockRadiationData
        coEvery {
            pgvisRepository.getMonthlyKwh(
                lat = 0.0,
                lon = 0.0,
                incline = 35.0f,
                retning = 0.0f,
                peakPower = 0.15f,
                pvTech = PvTech.CRYST_SI
            )
        } returns mockKwhData
        coEvery { frostRepository.get1YearReferenceTime() } returns "2024-01-01/2024-12-31"
        coEvery {
            frostRepository.getWeatherByCoordinates(
                latitude = 0.0,
                longitude = 0.0,
                referenceTime = "2024-01-01/2024-12-31"
            )
        } returns FrostResult.Success(mockFrostData)

        // Act
        viewModel.updateAllApi()

        // Assert
        val apiData = viewModel.apiDataFlow.value
        assertEquals(
            mockRadiationData,
            apiData.sunRadiation,
            "Sun radiation data should be updated"
        )
        assertEquals(mockKwhData, apiData.kwhMonthlyData, "Monthly kWh data should be updated")
        assertEquals(1, apiData.weatherData.size, "Weather data should contain one entry")
        assertFalse(apiData.isLoading, "isLoading should be false after all updates")
    }

    private fun setPrivateField(obj: Any, fieldName: String, value: Any) {
        val field = obj::class.java.getDeclaredField(fieldName)
        field.isAccessible = true
        field.set(obj, value)
        field.isAccessible = false
    }

}