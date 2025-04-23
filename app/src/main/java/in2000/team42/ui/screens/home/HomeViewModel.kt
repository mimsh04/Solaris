package in2000.team42.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import in2000.team42.data.frost.FrostDatasource
import in2000.team42.data.pgvis.PgvisDatasource
import in2000.team42.data.pgvis.PgvisRepository
import in2000.team42.data.pgvis.model.DailyProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import in2000.team42.data.frost.model.FrostData
import in2000.team42.data.frost.FrostRepository
import in2000.team42.data.pgvis.PvTech
import in2000.team42.data.pgvis.model.KwhMonthlyResponse
import in2000.team42.data.saved.*
import kotlinx.coroutines.flow.Flow

// Data class to hold API-related data
data class ApiData(
    val sunRadiation: List<DailyProfile> = emptyList(),
    val weatherData: List<FrostData> = emptyList(),
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
    var bottomSheetDetent : String = "medium",
    var adress: String = "",
)


class HomeViewModel : ViewModel() {
    private val radiationRepository = PgvisRepository(PgvisDatasource())
    private val frostRepository = FrostRepository(FrostDatasource())

    private val config = Config() // Instance of the Config class
    private val apiData = ApiData() // Instance of the ApiData class

    private val _apiData = MutableStateFlow(apiData)
    private val _config = MutableStateFlow(config)

    // Added to hold the current SheetDetent

    val apiDataFlow = _apiData.asStateFlow()
    val configFlow = _config.asStateFlow()

    fun setCoordinates(longitude: Double, latitude: Double) {
        _config.value = _config.value.copy(longitude = longitude, latitude = latitude)
    }


    fun setAddress(address: String) {
        _config.value = _config.value.copy(adress = address)
    }
    private val savedProjectDao = SavedProjectDatabase.getDatabase().savedProjectDao()
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
        Log.d("HomeViewModel", "Setting polygon: $polygon")
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
            _apiData.value = _apiData.value.copy(sunRadiation = radiationData) // Update API data
            Log.d("HomeViewModel", "Radiation data: $radiationData")
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
            _apiData.value = _apiData.value.copy(kwhMonthlyData = monthlyKwhData) // Update API data
            Log.d("HomeViewModel", "Monthly kwh data: $monthlyKwhData")
        }
    }

    private fun calculatePeakPower() = _config.value.solcelleEffekt / 100 * _config.value.areal

    private fun updateWeatherData() {
        viewModelScope.launch {
            try {
                val referenceTime = frostRepository.getLast24HoursReferenceTime()
                val weather = frostRepository.getWeatherByCoordinates(
                    latitude = _config.value.latitude,
                    longitude = _config.value.longitude,
                    referenceTime = referenceTime
                )
                _apiData.value = _apiData.value.copy(weatherData = weather) // Update API data
                Log.d("HomeViewModel", "Weather data: $weather")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to fetch weather data: ${e.message}")
                _apiData.value = _apiData.value.copy(weatherData = emptyList()) // Clear weather data on failure
            }
        }
    }
}