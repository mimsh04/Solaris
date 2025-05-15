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
import in2000.team42.data.pgvis.PvTech
import in2000.team42.data.pgvis.model.DailyProfile
import in2000.team42.data.pgvis.model.KwhMonthlyResponse
import in2000.team42.data.saved.SavedProjectDatabase
import in2000.team42.data.saved.SavedProjectEntity
import in2000.team42.data.solarPanels.SolarPanelModel
import in2000.team42.data.solarPanels.defaultPanels
import in2000.team42.ui.screens.home.map.getAddressOfPoint
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
    var direction: Float = 0f,
    var area: Float = 1f,
    var polygon: List<List<Point>>? = null,
    var bottomSheetDetent: String = "medium",
    var address: String = "",
    var selectedPanelModel: SolarPanelModel = defaultPanels[0]
)

data class DisplayWeather(
    val month: String = "ukjent",
    val temp: String = "ukjent",
    val snow: String = "ukjent",
    val cloud: String = "ukjent"
)

class HomeViewModel : ViewModel() {
    private val radiationRepository = PgvisRepository(PgvisDatasource())
    private val frostRepository = FrostRepository(FrostDatasource())
    private val tag = "HomeViewModel"
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
        _config.value = _config.value.copy(address = address)
    }


    fun setGeoAddress(point: Point) {
        getAddressOfPoint(point) {
            setAddress(it)
        }
    }

    fun isCurrentProjectSaved(): Flow<Boolean> {
        return savedProjectDao.getAllProjects().map { projects ->
            projects.any { it.config.copy(bottomSheetDetent = "") ==
                    _config.value.copy(bottomSheetDetent = "") }
        }
    }

    fun clearSolarData() {
        _apiData.value = _apiData.value.copy(
            sunRadiation = emptyList(),
            kwhMonthlyData = emptyList(),
        )
    }

    fun updateAllSolarData() {
        _apiData.value = _apiData.value.copy(isLoading = true)
        updateSolarRadiation()
        updateKwhMonthly()
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

    fun setDirection(directionAngle: Float) {
        _config.value = _config.value.copy(direction = directionAngle)
    }

    fun setArea(area: Float) {
        _config.value = _config.value.copy(area = area)
    }

    fun setSelectedSolarPanel(panel: SolarPanelModel) {
        Log.d(tag, "Panel selected: ${panel.name}")
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
                _config.value.direction
            )
            if (_apiData.value.kwhMonthlyData.isNotEmpty() and
                _apiData.value.weatherData.isNotEmpty()) {
                _apiData.value = _apiData.value.copy(isLoading = false)
            }
            _apiData.value = _apiData.value.copy(sunRadiation = radiationData)
            Log.d(tag, "Radiation data: $radiationData")
        }

    }

    fun updateKwhMonthly(pvTech: PvTech = PvTech.CRYST_SI) {
        val peakPower = calculatePeakPower()
        viewModelScope.launch {
            val monthlyKwhData = radiationRepository.getMonthlyKwh(
                _config.value.latitude,
                _config.value.longitude,
                _config.value.incline,
                _config.value.direction,
                peakPower,
                pvTech
            )
            if (_apiData.value.sunRadiation.isNotEmpty() and
                _apiData.value.weatherData.isNotEmpty()) {
                _apiData.value = _apiData.value.copy(isLoading = false)
            }
            _apiData.value = _apiData.value.copy(kwhMonthlyData = monthlyKwhData)
            Log.d(tag, "Monthly kwh data: $monthlyKwhData")
        }
    }

    private fun calculatePeakPower() =
        _config.value.selectedPanelModel.efficiency / 100 * _config.value.area

    private fun createDummyWeatherData(): List<DisplayWeather> {
        if (_apiData.value.kwhMonthlyData.isNotEmpty() and
            _apiData.value.sunRadiation.isNotEmpty()) {
            _apiData.value = _apiData.value.copy(isLoading = false)
        }
        return (0..12).map {
            DisplayWeather()
        }
    }

    fun updateWeatherData() {
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
                                Log.d(tag,
                                "DisplayWeather [$index]: month=${displayWeather.month}," +
                                    "temp=${displayWeather.temp}, snow=${displayWeather.snow}, " +
                                    "cloud=${displayWeather.cloud}")
                            }
                            createDummyWeatherData()
                            _apiData.value = _apiData.value.copy(weatherData = displayData)
                            Log.d(tag, "Weather data updated: $displayData")
                        } else {
                            val dummyData = createDummyWeatherData()
                            _apiData.value = _apiData.value.copy(weatherData = dummyData)
                            Log.w(tag, "Weather data is empty or all values are null")
                        }

                    }
                    is FrostResult.Failure -> {
                        val dummyData = createDummyWeatherData()
                        _apiData.value = _apiData.value.copy(weatherData = dummyData)
                        Log.e(tag, "Failed to fetch weather data: ${weather.message}")
                    }
                }
            } catch (e: Exception) {
                val dummyData = createDummyWeatherData()
                _apiData.value = _apiData.value.copy(weatherData = dummyData)
                Log.e(tag, "Exception fetching weather data: ${e.message}", e)
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
            temp = temperature?.let { String.format("%.1f°C", it) } ?: "ukjent",
            snow = snow?.let { String.format("%.1fmm", it) } ?: "ukjent",
            cloud = cloudAreaFraction?.let { String.format("%.1f%%", it) } ?: "ukjent"
        )
    }
}