package in2000.team42

import android.content.Context
import com.mapbox.geojson.Point
import in2000.team42.data.frost.FrostRepository
import in2000.team42.data.frost.model.FrostData
import in2000.team42.data.frost.model.FrostResult
import in2000.team42.data.pgvis.PgvisRepository
import in2000.team42.data.pgvis.PvTech
import in2000.team42.data.pgvis.model.DailyProfile
import in2000.team42.data.pgvis.model.KwhMonthlyResponse
import in2000.team42.data.saved.SavedProjectDao
import in2000.team42.data.saved.SavedProjectDatabase
import in2000.team42.data.saved.SavedProjectEntity
import in2000.team42.data.solarPanels.standardPanel
import in2000.team42.ui.screens.home.Config
import in2000.team42.ui.screens.home.HomeViewModel
import in2000.team42.ui.screens.home.map.getAddressOfPoint
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var pgvisRepository: PgvisRepository
    private lateinit var frostRepository: FrostRepository
    private lateinit var savedProjectDao: SavedProjectDao
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        // Mock dependencies
        pgvisRepository = mockk()
        frostRepository = mockk()
        //savedProjectDao = mockk()
        //val mockDatabase = mockk<SavedProjectDatabase>()
        //val mockContext = mockk<Context>()

        // Mock SavedProjectDatabase to prevent appContext error during HomeViewModel initialization
        //mockkStatic(SavedProjectDatabase::class)
        //every { SavedProjectDatabase.appContext } returns mockContext
        //every { SavedProjectDatabase.getDatabase() } returns mockDatabase
        //every { mockDatabase.savedProjectDao() } returns savedProjectDao

        Dispatchers.setMain(testDispatcher)

        // Initialize ViewModel after mocks are set
        viewModel = HomeViewModel()
        // Inject dependencies using reflection
        setPrivateField(viewModel, "radiationRepository", pgvisRepository)
        setPrivateField(viewModel, "frostRepository", frostRepository)
        setPrivateField(viewModel, "savedProjectDao", savedProjectDao)
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
    fun `setAddress updates address in config`() = runTest {
        // Arrange
        val address = "Test Address"

        // Act
        viewModel.setAddress(address)

        // Assert
        val config = viewModel.configFlow.value
        assertEquals(address, config.address, "Address should be updated")
    }

    @Test
    fun `setGeoAddress updates address using getAddressOfPoint`() = runTest {
        // Arrange
        val point = mockk<Point>()
        val address = "Geo Address"
        mockkStatic("in2000.team42.ui.screens.home.map.MapUtilsKt")
        coEvery { getAddressOfPoint(point, any()) } answers {
            val callback = secondArg<(String) -> Unit>()
            callback(address)
        }

        // Act
        viewModel.setGeoAddress(point)

        // Assert
        val config = viewModel.configFlow.value
        assertEquals(address, config.address, "Address should be updated from geo point")
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
        val panel = standardPanel

        // Act
        viewModel.setSelectedSolarPanel(panel)

        // Assert
        val config = viewModel.configFlow.value
        assertEquals(panel, config.selectedPanelModel, "Solar panel should be updated")
    }

    @Test
    fun `setPolygon updates polygon in config`() = runTest {
        // Arrange
        val polygon = listOf(listOf(mockk<Point>()))

        // Act
        viewModel.setPolygon(polygon)

        // Assert
        val config = viewModel.configFlow.value
        assertEquals(polygon, config.polygon, "Polygon should be updated")
    }

    @Test
    fun `setBottomSheetDetent updates bottomSheetDetent in config`() = runTest {
        // Arrange
        val detent = "large"

        // Act
        viewModel.setBottomSheetDetent(detent)

        // Assert
        val config = viewModel.configFlow.value
        assertEquals(detent, config.bottomSheetDetent, "BottomSheetDetent should be updated")
    }

    @Test
    fun `clearSolarData clears sunRadiation and kwhMonthlyData`() = runTest {
        // Arrange
        viewModel.updateAllApi() // Populate with some data
        val apiDataBefore = viewModel.apiDataFlow.value
        assertTrue(apiDataBefore.sunRadiation.isNotEmpty() || apiDataBefore.kwhMonthlyData.isNotEmpty())

        // Act
        viewModel.clearSolarData()

        // Assert
        val apiData = viewModel.apiDataFlow.value
        assertTrue(apiData.sunRadiation.isEmpty(), "Sun radiation data should be cleared")
        assertTrue(apiData.kwhMonthlyData.isEmpty(), "Monthly kWh data should be cleared")
        assertEquals(apiDataBefore.weatherData, apiData.weatherData, "Weather data should remain unchanged")
    }

    @Test
    fun `clearApiData clears all api data`() = runTest {
        // Arrange
        viewModel.updateAllApi() // Populate with some data
        val apiDataBefore = viewModel.apiDataFlow.value
        assertTrue(apiDataBefore.sunRadiation.isNotEmpty() || apiDataBefore.kwhMonthlyData.isNotEmpty() || apiDataBefore.weatherData.isNotEmpty())

        // Act
        viewModel.clearApiData()

        // Assert
        val apiData = viewModel.apiDataFlow.value
        assertTrue(apiData.sunRadiation.isEmpty(), "Sun radiation data should be cleared")
        assertTrue(apiData.kwhMonthlyData.isEmpty(), "Monthly kWh data should be cleared")
        assertTrue(apiData.weatherData.isEmpty(), "Weather data should be cleared")
        assertFalse(apiData.isLoading, "isLoading should be false")
    }

    @Test
    fun `isCurrentProjectSaved returns true when project exists`() = runTest {
        // Arrange
        val config = Config(latitude = 59.91, longitude = 10.74)
        viewModel.setCoordinates(longitude = 10.74, latitude = 59.91)
        coEvery { savedProjectDao.getAllProjects() } returns flowOf(
            listOf(SavedProjectEntity(config = config))
        )

        // Act
        val isSaved = viewModel.isCurrentProjectSaved().first()

        // Assert
        assertTrue(isSaved, "Project should be marked as saved")
    }

    @Test
    fun `isCurrentProjectSaved returns false when project does not exist`() = runTest {
        // Arrange
        viewModel.setCoordinates(longitude = 10.74, latitude = 59.91)
        coEvery { savedProjectDao.getAllProjects() } returns flowOf(emptyList())

        // Act
        val isSaved = viewModel.isCurrentProjectSaved().first()

        // Assert
        assertFalse(isSaved, "Project should not be marked as saved")
    }

    @Test
    fun `saveProject inserts new project when none exists`() = runTest {
        // Arrange
        viewModel.setCoordinates(longitude = 10.74, latitude = 59.91)
        coEvery { savedProjectDao.getAllProjects() } returns flowOf(emptyList())
        coEvery { savedProjectDao.insert(any()) } returns Unit

        // Act
        viewModel.saveProject()

        // Assert
        coVerify { savedProjectDao.insert(any()) }
    }

    @Test
    fun `saveProject updates existing project`() = runTest {
        // Arrange
        val config = Config(latitude = 59.91, longitude = 10.74)
        viewModel.setCoordinates(longitude = 10.74, latitude = 59.91)
        coEvery { savedProjectDao.getAllProjects() } returns flowOf(
            listOf(SavedProjectEntity(config = config.copy(bottomSheetDetent = "medium")))
        )
        coEvery { savedProjectDao.update(any()) } returns Unit

        // Act
        viewModel.saveProject()

        // Assert
        coVerify { savedProjectDao.update(any()) }
    }

    @Test
    fun `deleteCurrentProject deletes existing project`() = runTest {
        // Arrange
        val config = Config(latitude = 59.91, longitude = 10.74)
        viewModel.setCoordinates(longitude = 10.74, latitude = 59.91)
        coEvery { savedProjectDao.getAllProjects() } returns flowOf(
            listOf(SavedProjectEntity(config = config))
        )
        coEvery { savedProjectDao.delete(any()) } returns Unit

        // Act
        viewModel.deleteCurrentProject()

        // Assert
        coVerify { savedProjectDao.delete(any()) }
    }

    @Test
    fun `loadProject updates config and triggers api updates`() = runTest {
        // Arrange
        val config = Config(latitude = 59.91, longitude = 10.74, bottomSheetDetent = "large")
        val project = SavedProjectEntity(config = config)
        val mockRadiationData = listOf(mockk<DailyProfile>())
        val mockKwhData = listOf(mockk<KwhMonthlyResponse.MonthlyKwhData>())
        val mockFrostData = listOf(
            FrostData(
                stationId = "SN18700",
                referenceTime = "2024-01-01",
                temperature = any(),
                snow = any(),
                cloudAreaFraction = any(),
                qualityCode = any()
            )
        )
        coEvery {
            pgvisRepository.getRadiationData(
                lat = 59.91,
                lon = 10.74,
                month = 0,
                incline = 35.0f,
                direction = 0.0f
            )
        } returns mockRadiationData
        coEvery {
            pgvisRepository.getMonthlyKwh(
                lat = 59.91,
                lon = 10.74,
                incline = 35.0f,
                direction = 0.0f,
                peakPower = 0.19f, // 19% efficiency * 1m²
                pvTech = PvTech.CRYST_SI
            )
        } returns mockKwhData
        coEvery { frostRepository.get1YearReferenceTime() } returns "2024-01-01/2024-12-31"
        coEvery {
            frostRepository.getWeatherByCoordinates(
                latitude = 59.91,
                longitude = 10.74,
                referenceTime = "2024-01-01/2024-12-31"
            )
        } returns FrostResult.Success(mockFrostData)

        // Act
        viewModel.loadProject(project)

        // Assert
        val configAfter = viewModel.configFlow.value
        assertEquals("medium", configAfter.bottomSheetDetent, "BottomSheetDetent should be reset to medium")
        assertEquals(config.latitude, configAfter.latitude, "Latitude should be loaded")
        assertEquals(config.longitude, configAfter.longitude, "Longitude should be loaded")

        val apiData = viewModel.apiDataFlow.value
        assertEquals(mockRadiationData, apiData.sunRadiation, "Sun radiation data should be updated")
        assertEquals(mockKwhData, apiData.kwhMonthlyData, "Monthly kWh data should be updated")
        assertEquals(1, apiData.weatherData.size, "Weather data should contain one entry")
    }

    @Test
    fun `updateKwhMonthly updates kwhMonthlyData in apiData`() = runTest {
        // Arrange
        val mockKwhData = listOf(mockk<KwhMonthlyResponse.MonthlyKwhData>())
        val peakPower = 0.19f // 19% efficiency * 1m² (standardPanel)
        coEvery {
            pgvisRepository.getMonthlyKwh(
                lat = 0.0,
                lon = 0.0,
                incline = 35.0f,
                direction = 0.0f,
                peakPower = peakPower,
                pvTech = PvTech.CRYST_SI
            )
        } returns mockKwhData

        // Act
        viewModel.updateKwhMonthly()

        // Assert
        val apiData = viewModel.apiDataFlow.value
        assertEquals(mockKwhData, apiData.kwhMonthlyData, "Monthly kWh data should be updated")
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
                temperature = any(),
                snow = any(),
                cloudAreaFraction = any(),
                qualityCode = any()
            )
        )
        coEvery {
            pgvisRepository.getRadiationData(
                lat = 0.0,
                lon = 0.0,
                month = 0,
                incline = 35.0f,
                direction = 0.0f
            )
        } returns mockRadiationData
        coEvery {
            pgvisRepository.getMonthlyKwh(
                lat = 0.0,
                lon = 0.0,
                incline = 35.0f,
                direction = 0.0f,
                peakPower = 0.19f, // 19% efficiency * 1m²
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
        assertEquals(mockRadiationData, apiData.sunRadiation, "Sun radiation data should be updated")
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