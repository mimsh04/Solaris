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
    private val _incline = MutableStateFlow(0.0)
    private val _retning = MutableStateFlow(0.0)


    private val _sunRadiation = MutableStateFlow<List<DailyProfile>>(emptyList())

    val longitude = _longitude.asStateFlow()
    val latitude = _latitude.asStateFlow()
    val incline = _incline.asStateFlow()
    val retning = _retning.asStateFlow()

    fun setLongitude(longitude: Double) {
        _longitude.value = longitude
    }

    fun setLatitude(latitude: Double) {
        _latitude.value = latitude
    }

    fun updateLocation() {
        updateSolarRadiation()
    }

    private fun updateSolarRadiation(
    ) {
        viewModelScope.launch {
            _sunRadiation.value = radiationRepository.getRadiationData(
                latitude.value.toFloat(),
                longitude.value.toFloat(),
                0,
                incline.value.toFloat(),
                retning.value.toFloat()
            )
            Log.d("HomeViewModel", "Radiation data: ${_sunRadiation.value}")
        }

    }
}