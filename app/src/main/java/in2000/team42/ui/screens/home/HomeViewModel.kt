package in2000.team42.ui.screens.home

import android.util.Log
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
import in2000.team42.data.pgvis.PvTech
import in2000.team42.data.pgvis.model.KwhMonthlyResponse

class HomeViewModel : ViewModel() {
    private val radiationRepository = PgvisRepository(PgvisDatasource())
    private val frostRepository = FrostRepository(FrostDatasource())

    private val _longitude = MutableStateFlow(0.0)
    private val _latitude = MutableStateFlow(0.0)
    private val _incline = MutableStateFlow(35f)
    private val _vinkel = MutableStateFlow(0f)

    private val _sunRadiation = MutableStateFlow<List<DailyProfile>>(emptyList())
    private val _weatherData = MutableStateFlow<List<FrostData>>(emptyList())
    private val _kwhMonthlyData = MutableStateFlow<List<KwhMonthlyResponse.MonthlyKwhData>>(emptyList())

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
            Log.d("HomeViewModel", "Radiation data: ${_sunRadiation.value}")
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
            Log.d("HomeViewModel", "Monthly kwh data: ${_kwhMonthlyData.value}")
        }
    }

    private fun updateWeatherData() {
        viewModelScope.launch {
            try {
                val referenceTime = frostRepository.getLast24HoursReferenceTime()
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