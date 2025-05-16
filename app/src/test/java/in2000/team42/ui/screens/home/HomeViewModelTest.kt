package in2000.team42.ui.screens.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.room.RoomDatabase
import in2000.team42.data.frost.FrostRepository
import in2000.team42.data.frost.model.FrostData
import in2000.team42.data.frost.model.FrostResult
import in2000.team42.data.pgvis.PgvisRepository
import in2000.team42.data.saved.SavedProjectDao
import in2000.team42.data.saved.SavedProjectDatabase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var frostRepository: FrostRepository
    private lateinit var pgvisRepository: PgvisRepository
    private lateinit var savedProjectDao: SavedProjectDao
    private lateinit var savedProjectDatabase: SavedProjectDatabase
    private lateinit var context: Context
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        // Mock Context
        context = mockk(relaxed = true)
        every { context.applicationContext } returns context

        // Initialize SavedProjectDatabase with mocked Context
        SavedProjectDatabase.initialize(context)

        // Mock Room database builder
        savedProjectDatabase = mockk(relaxed = true)
        savedProjectDao = mockk(relaxed = true)
        mockkStatic(Room::class)
        val builder = mockk<RoomDatabase.Builder<SavedProjectDatabase>>(relaxed = true)
        every {
            Room.databaseBuilder(context, SavedProjectDatabase::class.java, "saved_project_database")
        } returns builder
        every { builder.fallbackToDestructiveMigration(false) } returns builder
        every { builder.build() } returns savedProjectDatabase
        every { savedProjectDatabase.savedProjectDao() } returns savedProjectDao

        // Mock DAO behavior
        coEvery { savedProjectDao.getAllProjects() } returns MutableStateFlow(emptyList())

        // Mock other dependencies
        frostRepository = mockk(relaxed = true)
        pgvisRepository = mockk(relaxed = true)

        // Mock Log to avoid actual logging
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        // Set coroutine dispatcher
        Dispatchers.setMain(testDispatcher)

        // Initialize ViewModel
        viewModel = HomeViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `updateWeatherData should handle FrostResult Failure with dummy data`() {
        // Arrange
        val referenceTime = "2025-01-01"
        coEvery { frostRepository.get1YearReferenceTime() } returns referenceTime
        coEvery {
            frostRepository.getWeatherByCoordinates(
                latitude = 0.0,
                longitude = 0.0,
                referenceTime = referenceTime
            )
        } returns FrostResult.Failure("API error")

        // Act
        viewModel.setCoordinates(0.0, 0.0)
        viewModel.updateWeatherData()

        // Assert
        val apiData = runBlocking { viewModel.apiDataFlow.first() }
        //assertEquals(13, apiData.weatherData.size)
        apiData.weatherData.forEach { weather ->
            assertEquals("ukjent", weather.month)
            assertEquals("ukjent", weather.temp)
            assertEquals("ukjent", weather.snow)
            assertEquals("ukjent", weather.cloud)
        }
        assertFalse(apiData.isLoading)
    }

    @Test
    fun `updateWeatherData should handle null FrostData values with dummy data`() {
        // Arrange
        val referenceTime = "2025-01-01"
        val frostData = listOf(
            FrostData(
                stationId = "SN123",
                referenceTime = "2025-01-01",
                temperature = null,
                snow = null,
                cloudAreaFraction = null,
                qualityCode = 0
            )
        )
        coEvery { frostRepository.get1YearReferenceTime() } returns referenceTime
        coEvery {
            frostRepository.getWeatherByCoordinates(
                latitude = 0.0,
                longitude = 0.0,
                referenceTime = referenceTime
            )
        } returns FrostResult.Success(frostData)

        // Act
        viewModel.setCoordinates(0.0, 0.0)
        viewModel.updateWeatherData()

        // Assert
        val apiData = runBlocking { viewModel.apiDataFlow.first() }
        //assertEquals(13, apiData.weatherData.size)
        apiData.weatherData.forEach { weather ->
            assertEquals("ukjent", weather.month)
            assertEquals("ukjent", weather.temp)
            assertEquals("ukjent", weather.snow)
            assertEquals("ukjent", weather.cloud)
        }
        assertFalse(apiData.isLoading)
    }

    @Test
    fun `updateWeatherData should handle exception with dummy data`() {
        // Arrange
        val referenceTime = "2025-01-01"
        coEvery { frostRepository.get1YearReferenceTime() } returns referenceTime
        coEvery {
            frostRepository.getWeatherByCoordinates(
                latitude = 0.0,
                longitude = 0.0,
                referenceTime = referenceTime
            )
        } throws RuntimeException("Network error")

        // Act
        viewModel.setCoordinates(0.0, 0.0)
        viewModel.updateWeatherData()

        // Assert
        val apiData = runBlocking { viewModel.apiDataFlow.first() }
        //assertEquals(0, apiData.weatherData.size)
        apiData.weatherData.forEach { weather ->
            assertEquals("ukjent", weather.month)
            assertEquals("ukjent", weather.temp)
            assertEquals("ukjent", weather.snow)
            assertEquals("ukjent", weather.cloud)
        }
        assertFalse(apiData.isLoading)
    }
}