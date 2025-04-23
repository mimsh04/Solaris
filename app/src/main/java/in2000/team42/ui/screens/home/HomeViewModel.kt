package in2000.team42.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import in2000.team42.data.frost.FrostDatasource
import in2000.team42.data.frost.FrostRepository
import in2000.team42.data.frost.model.FrostData
import in2000.team42.data.frost.model.FrostResult
import in2000.team42.data.pgvis.PgvisDatasource
import in2000.team42.data.pgvis.PgvisRepository
import in2000.team42.data.pgvis.model.DailyProfile
import in2000.team42.data.pgvis.PvTech
import in2000.team42.data.pgvis.model.KwhMonthlyResponse
import in2000.team42.data.saved.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data class to hold API-related data
data class ApiData(
    val sunRadiation: List<DailyProfile> = emptyList(),
    val weatherData: List<DisplayWeather> = emptyList(), // Updated to use DisplayWeather
    val kwhMonthlyData: List<KwhMonthlyResponse.MonthlyKwhData> = emptyList()
)

// Data class to hold user-configurable parameters
data class Config(
    var longitude: Double = 0.0,
    var latitude: Double = 0.0,
    var incline: Float = 35f,
    var vinkel: Float = 0f,
    var areal: Float = 1f,
    var solcelleEffekt: Float = 15f,
    var polygon: List<List<Point>>? = null,
    var bottomSheetDetent: String = "medium",
    var adress: String = "",
)

// Display-friendly data class for weather
data class DisplayWeather(
    val month: String,
    val temp: String,
    val snow: String,
    val cloud: String
)

class HomeViewModel : ViewModel() {
    private val radiationRepository = PgvisRepository(PgvisDatasource())
    private val frostRepository = FrostRepository(FrostDatasource())
    private val TAG = "HomeViewModel"

    private val config = Config() // Instance of the Config class
    private val apiData = ApiData() // Instance of the ApiData class

    private val _apiData = MutableStateFlow(apiData)
    private val _config = MutableStateFlow(config)

    val apiDataFlow = _apiData.asStateFlow()
    val configFlow = _config.asStateFlow()

    private val savedProjectDao = SavedProjectDatabase.getDatabase().savedProjectDao()

    fun setCoordinates(longitude: Double, latitude: Double) {
        _config.value = _config.value.copy(longitude = longitude, latitude = latitude)
    }

    fun setAddress(address: String) {
        _config.value = _config.value.copy(adress = address)
    }

    fun saveProject() {
        viewModelScope.launch {
            savedProjectDao.insert(
                SavedProjectEntity(
                    config = _config.value
                )
            )
        }
    }

    fun getSavedProjects(): Flow<List<SavedProjectEntity>> {
        return savedProjectDao.getAllProjects()
    }

    fun deleteProject(project: SavedProjectEntity) {
        viewModelScope.launch {
            savedProjectDao.delete(project)
        }
    }

    fun setIncline(incline: Float) {
        _config.value = _config.value.copy(incline = incline)
    }

    fun setVinkel(vinkel: Float) {
        _config.value = _config.value.copy(vinkel = vinkel)
    }

    // For setting areal and solcelleEffekt
    fun setAreal(areal: Float) {
        _config.value = _config.value.copy(areal = areal)
    }

    fun setSolcelleEffekt(solcelleEffekt: Float) {
        _config.value = _config.value.copy(solcelleEffekt = solcelleEffekt)
    }

    fun setPolygon(polygon: List<List<Point>>?) {
        Log.d(TAG, "Setting polygon: $polygon")
        _config.value = _config.value.copy(polygon = polygon)
    }

    fun setBottomSheetDetent(bottomSheetDetent: String) {
        _config.value = _config.value.copy(bottomSheetDetent = bottomSheetDetent)
    }

    fun updateAllApi() {
        launchDataUpdates()
    }

    private fun launchDataUpdates() {
        updateSolarRadiation()
        updateWeatherData()
        updateKwhMonthly()
    }

    private fun updateSolarRadiation() {
        viewModelScope.launch {
            val radiationData = radiationRepository.getRadiationData(
                _config.value.latitude,
                _config.value.longitude,
                0,
                _config.value.incline,
                _config.value.vinkel
            )
            _apiData.value = _apiData.value.copy(sunRadiation = radiationData)
            Log.d(TAG, "Radiation data: $radiationData")
        }

    }

    fun updateKwhMonthly(pvTech: PvTech = PvTech.CRYST_SI) {
        val peakPower = calculatePeakPower()
        viewModelScope.launch {
            val monthlyKwhData = radiationRepository.getMonthlyKwh(
                _config.value.latitude,
                _config.value.longitude,
                _config.value.incline,
                _config.value.vinkel,
                peakPower,
                pvTech
            )
            _apiData.value = _apiData.value.copy(kwhMonthlyData = monthlyKwhData)
            Log.d(TAG, "Monthly kwh data: $monthlyKwhData")
        }
    }

    private fun calculatePeakPower() = _config.value.solcelleEffekt / 100 * _config.value.areal

    private fun updateWeatherData() {
        viewModelScope.launch {
            try {
                val referenceTime = frostRepository.get1YearReferenceTime()
                val weather = frostRepository.getWeatherByCoordinates(
                    latitude = _config.value.latitude,
                    longitude = _config.value.longitude,
                    referenceTime = referenceTime
                )
                when (weather) {
                    is FrostResult.Success -> {
                        val displayData = weather.data.map { it.toDisplayWeather() }
                        displayData.forEachIndexed { index, displayWeather ->
                            Log.d(TAG, "DisplayWeather [$index]: month=${displayWeather.month}, temp=${displayWeather.temp}, snow=${displayWeather.snow}, cloud=${displayWeather.cloud}")
                        }
                        _apiData.value = _apiData.value.copy(weatherData = displayData)
                        Log.d(TAG, "Weather data updated: $displayData")
                    }
                    is FrostResult.Failure -> {
                        _apiData.value = _apiData.value.copy(weatherData = emptyList())
                        Log.e(TAG, "Failed to fetch weather data: ${weather.message}")
                    }
                }
            } catch (e: Exception) {
                _apiData.value = _apiData.value.copy(weatherData = emptyList())
                Log.e(TAG, "Exception fetching weather data: ${e.message}", e)
            }
        }
    }

    // Transformerer FrostData til display vennlig data
    private fun FrostData.toDisplayWeather(): DisplayWeather {
        val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(referenceTime) ?: Date()
        return DisplayWeather(
            month = dateFormat.format(date),
            temp = String.format("%.1f°C", temperature),
            snow = String.format("%.1f", snow),
            cloud = String.format("%.1f%%", cloudAreaFraction)
        )
    }
}