package in2000.team42.ui.screens.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.FrostDatasource
import in2000.team42.data.pgvis.PgvisDatasource
import in2000.team42.data.pgvis.PgvisRepository
import in2000.team42.data.pgvis.model.DailyProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import models.FrostData
import in2000.team42.data.frost.FrostRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {
    private val radiationRepository = PgvisRepository(PgvisDatasource())
    private val frostRepository = FrostRepository(FrostDatasource())

    private val _longitude = MutableStateFlow(0.0)
    private val _latitude = MutableStateFlow(0.0)
    private val _incline = MutableStateFlow(35f)
    private val _vinkel = MutableStateFlow(0f)

    private val _sunRadiation = MutableStateFlow<List<DailyProfile>>(emptyList())
    private val _weatherData = MutableStateFlow<List<FrostData>>(emptyList())

    val longitude = _longitude.asStateFlow()
    val latitude = _latitude.asStateFlow()
    val incline = _incline.asStateFlow()
    val vinkel = _vinkel.asStateFlow()
    val weatherData = _weatherData.asStateFlow()

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateAllApi() {
        updateSolarRadiation()
        updateWeatherData()
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
            Log.d("HomeViewModel", "Radiation data: ${_sunRadiation.value}")
        }

    }

    // Måtte legge til minimums krav for API 26, skal se på å finne en løsning
    // som ikke krever et minimums krav for API
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLast24HoursReferenceTime(): String {
        val now = Instant.now()
        val start = now.minusSeconds(24 * 60 * 60) // 24 hours ago
        val formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .withZone(ZoneId.of("UTC"))

        val startTime = formatter.format(start)
        val endTime = formatter.format(now)
        return "$startTime/$endTime" // e.g., "2025-03-25T12:00:00Z/2025-03-26T12:00:00Z"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateWeatherData() {
        viewModelScope.launch {
            try {
                val referenceTime = getLast24HoursReferenceTime()
                val weather = frostRepository.getWeatherByCoordinates(
                    latitude = _latitude.value,
                    longitude = _longitude.value,
                    referenceTime = referenceTime
                )
                _weatherData.value = weather
                Log.d("HomeViewModel", "Weather data: $weather")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to fetch weather data: ${e.message}")
                _weatherData.value = emptyList()
            }
        }
    }
}