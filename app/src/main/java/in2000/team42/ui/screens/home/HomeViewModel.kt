package in2000.team42.ui.screens.home

import android.annotation.SuppressLint
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
import in2000.team42.data.solarPanels.SolarPanelModel
import in2000.team42.data.solarPanels.defaultPanels
import in2000.team42.ui.screens.home.map.getAdressOfPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class ApiData(
    val sunRadiation: List<DailyProfile> = emptyList(),
    val weatherData: List<DisplayWeather> = emptyList(), // Updated to use DisplayWeather
    val kwhMonthlyData: List<KwhMonthlyResponse.MonthlyKwhData> = emptyList(),
    val isLoading: Boolean = false,
)

data class Config(
    var longitude: Double = 0.0,
    var latitude: Double = 0.0,
    var incline: Float = 35f,
    var vinkel: Float = 0f,
    var areal: Float = 1f,
    var polygon: List<List<Point>>? = null,
    var bottomSheetDetent: String = "medium",
    var adress: String = "",
    var selectedPanelModel: SolarPanelModel = defaultPanels[0]
)

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
    private val savedProjectDao = SavedProjectDatabase.getDatabase().savedProjectDao()


    private val config = Config()
    private val apiData = ApiData()

    private val _apiData = MutableStateFlow(apiData)
    private val _config = MutableStateFlow(config)

    val apiDataFlow = _apiData.asStateFlow()
    val configFlow = _config.asStateFlow()

    fun setCoordinates(longitude: Double, latitude: Double) {
        _config.value = _config.value.copy(longitude = longitude, latitude = latitude)
    }

    fun setAddress(address: String) {
        _config.value = _config.value.copy(adress = address)
    }


    fun setGeoAddress(point: Point) {
        getAdressOfPoint(point) {
            setAddress(it)
        }
    }

    fun isCurrentProjectSaved(): Flow<Boolean> {
        return savedProjectDao.getAllProjects().map { projects ->
            projects.any { it.config.copy(bottomSheetDetent = "") ==
                    _config.value.copy(bottomSheetDetent = "") }
        }
    }


    fun saveProject() {
        viewModelScope.launch {
            val currentConfig = _config.value
            val normalizedConfig = currentConfig.copy(bottomSheetDetent = "")
            savedProjectDao.getAllProjects().first()
                .firstOrNull { it.config.copy(bottomSheetDetent = "") == normalizedConfig }
                ?.let { existing ->
                    savedProjectDao.update(existing.copy(config = currentConfig))
                } ?: run {
                savedProjectDao.insert(SavedProjectEntity(config = currentConfig))
            }
        }
    }

    fun deleteCurrentProject() {
        viewModelScope.launch {
            val normalizedConfig = _config.value.copy(bottomSheetDetent = "")
            savedProjectDao.getAllProjects().first()
                .firstOrNull { it.config.copy(bottomSheetDetent = "") == normalizedConfig }
                ?.let { savedProjectDao.delete(it) }
        }
    }

    fun loadProject(project: SavedProjectEntity) {
        _config.value = project.config.copy(
            bottomSheetDetent = "medium"
        )
        _apiData.value = ApiData()
        viewModelScope.launch {
            updateAllApi()
        }
    }

    fun setIncline(incline: Float) {
        _config.value = _config.value.copy(incline = incline)
    }

    fun setVinkel(vinkel: Float) {
        _config.value = _config.value.copy(vinkel = vinkel)
    }

    fun setAreal(areal: Float) {
        _config.value = _config.value.copy(areal = areal)
    }

    fun setSelectedSolarPanel(panel: SolarPanelModel) {
        Log.d(TAG, "Panel selected: ${panel.name}")
        _config.value = _config.value.copy(selectedPanelModel = panel)
    }

    fun setPolygon(polygon: List<List<Point>>?) {
        _config.value = _config.value.copy(polygon = polygon)
    }

    fun setBottomSheetDetent(bottomSheetDetent: String) {
        _config.value = _config.value.copy(bottomSheetDetent = bottomSheetDetent)
    }

    fun clearApiData() {
        _apiData.value = ApiData()
    }

    fun updateAllApi() {
        _apiData.value = ApiData(isLoading = true)
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

    private fun calculatePeakPower() =
        _config.value.selectedPanelModel.efficiency / 100 * _config.value.areal

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
                        val isValidData = weather.data.any { data ->
                            data.temperature != null ||
                            data.snow != null ||
                            data.cloudAreaFraction != null
                        }
                        if (isValidData) {
                            val displayData = weather.data.mapNotNull { it.toDisplayWeather() }
                            displayData.forEachIndexed { index, displayWeather ->
                                Log.d(TAG,
                                "DisplayWeather [$index]: month=${displayWeather.month}," +
                                    "temp=${displayWeather.temp}, snow=${displayWeather.snow}, " +
                                    "cloud=${displayWeather.cloud}")
                            }
                            _apiData.value = _apiData.value.copy(weatherData = displayData)
                            Log.d(TAG, "Weather data updated: $displayData")
                        } else {
                            _apiData.value = _apiData.value.copy(weatherData = emptyList())
                            Log.w(TAG, "Weather data is empty or all values are null")
                        }
                        _apiData.value = _apiData.value.copy(isLoading = false)
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

    @SuppressLint("DefaultLocale")
    private fun FrostData.toDisplayWeather(): DisplayWeather? {
        if (temperature == null && snow == null && cloudAreaFraction == null) {
            return null
        }
        val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(referenceTime) ?: Date()
        return DisplayWeather(
            month = dateFormat.format(date),
            temp = temperature?.let { String.format("%.1f°C", it) } ?: "Ukjent",
            snow = snow?.let { String.format("%.1fmm", it) } ?: "Ukjent",
            cloud = cloudAreaFraction?.let { String.format("%.1f%%", it) } ?: "Ukjent"
        )
    }
}