package in2000.team42.ui.screens.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import in2000.team42.data.frost.FrostDatasource
import in2000.team42.data.pgvis.PgvisDatasource
import in2000.team42.data.pgvis.PgvisRepository
import in2000.team42.data.pgvis.model.DailyProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import in2000.team42.data.frost.model.FrostData
import in2000.team42.data.frost.FrostRepository
import in2000.team42.data.frost.model.FrostResult
import in2000.team42.data.pgvis.PvTech
import in2000.team42.data.pgvis.model.KwhMonthlyResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel : ViewModel() {
    private val radiationRepository = PgvisRepository(PgvisDatasource())
    private val frostRepository = FrostRepository(FrostDatasource())

    private val TAG = "HomeViewModel"

    private val _longitude = MutableStateFlow(0.0)
    private val _latitude = MutableStateFlow(0.0)
    private val _incline = MutableStateFlow(35f)
    private val _vinkel = MutableStateFlow(0f)

    private val _sunRadiation = MutableStateFlow<List<DailyProfile>>(emptyList())
    private val _weatherData = MutableStateFlow<List<DisplayWeather>>(emptyList())
    private val _kwhMonthlyData = MutableStateFlow<List<KwhMonthlyResponse.MonthlyKwhData>>(emptyList())

    private val _errorMessage = MutableStateFlow<String?>(null)

    val longitude = _longitude.asStateFlow()
    val latitude = _latitude.asStateFlow()
    val incline = _incline.asStateFlow()
    val vinkel = _vinkel.asStateFlow()

    val sizeUnaryOperator = _sunRadiation.asStateFlow()
    val weatherData = _weatherData.asStateFlow()
    val kwhMonthlyData = _kwhMonthlyData.asStateFlow()

    fun setLongitude(longitude: Double) {
        _longitude.value = longitude
    }

    fun setLatitude(latitude: Double) {
        _latitude.value = latitude
    }

    fun setIncline(incline: Float) {
        _incline.value = incline
    }

    fun setVinkel(vinkel: Float) {
        _vinkel.value = vinkel
    }

    fun updateAllApi() {
        updateSolarRadiation()
        updateWeatherData()
        updateKwhMonthly()
    }

    fun updateSolarRadiation(
    ) {
        viewModelScope.launch {
            _sunRadiation.value = radiationRepository.getRadiationData(
                latitude.value,
                longitude.value,
                0,
                incline.value,
                vinkel.value
            )
            Log.d(TAG, "Radiation data: ${_sunRadiation.value}")
        }

    }

    fun updateKwhMonthly(peakPower: Float = 2f, pvTech: PvTech = PvTech.CRYST_SI) {
        viewModelScope.launch {
            _kwhMonthlyData.value = radiationRepository.getMonthlyKwh(
                latitude.value,
                longitude.value,
                incline.value,
                vinkel.value,
                2f,
                pvTech
            )
            Log.d(TAG, "Monthly kwh data: ${_kwhMonthlyData.value}")
        }
    }

    // TODO: Må fikse slik at all dataen blir samlet
    private fun updateWeatherData() {
        viewModelScope.launch {
            try {
                val referenceTime = frostRepository.getLast24HoursReferenceTime()
                val weather = frostRepository.getWeatherByCoordinates(
                    latitude = _latitude.value,
                    longitude = _longitude.value,
                    referenceTime = referenceTime
                )
                when (weather) {
                    is FrostResult.Success -> {
                        val displayData = weather.data.map { it.toDisplayWeather() }
                        displayData.forEachIndexed { index, displayWeather ->
                            Log.d(TAG, "DisplayWeather [$index]: month=${displayWeather.month}, temp=${displayWeather.temp}, snow=${displayWeather.snow}, cloud=${displayWeather.cloud}")
                        }

                        _weatherData.value = displayData
                        Log.d(TAG, "Weather data: $displayData")
                    }
                    is FrostResult.Failure -> {
                        _weatherData.value = emptyList()
                        Log.e(TAG, "Failed to fetch weather data: ${weather.message}")
                    }
                }
            } catch (e: Exception) {
                _weatherData.value = emptyList()
                Log.e(TAG, "Exception fetching weather data: ${e.message}")
            }
        }
    }

    // Display-friendly data klasse
    data class DisplayWeather(
        val month: String,
        val temp: String,
        val snow: String,
        val cloud: String
    )

    // transformerer FrostData til DisplayData
    private fun FrostData.toDisplayWeather(): DisplayWeather {
        val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = inputFormat.parse(referenceTime) ?: Date()
        return DisplayWeather(
            month = dateFormat.format(date),
            temp = String.format("%.1f°C", temperature),
            snow = String.format("%.1f", snow),
            cloud = String.format("%.1f%%", cloudAreaFraction)
        )
    }
}