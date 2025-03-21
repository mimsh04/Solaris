package in2000.team42.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import in2000.team42.data.pgvis.PgvisDatasource
import in2000.team42.data.pgvis.PgvisRepository
import in2000.team42.data.pgvis.model.DailyProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val radiationRepository = PgvisRepository(PgvisDatasource())

    private val _longitude = MutableStateFlow(0.0)
    private val _latitude = MutableStateFlow(0.0)
    private val _incline = MutableStateFlow(0f)
    private val _vinkel = MutableStateFlow(0f)

    private val _sunRadiation = MutableStateFlow<List<DailyProfile>>(emptyList())

    val longitude = _longitude.asStateFlow()
    val latitude = _latitude.asStateFlow()
    val incline = _incline.asStateFlow()
    val vinkel = _vinkel.asStateFlow()

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
    }

    private fun updateSolarRadiation(
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
}