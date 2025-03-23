package in2000.team42.ui.screens.HKS

import androidx.lifecycle.ViewModel
import in2000.team42.data.HvaKosterStrømmen.HvaKosterStrømmenRepo
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class HKSViewModel : ViewModel() {
    private val repo = HvaKosterStrømmenRepo()

    private val _selectedDate = MutableStateFlow(getCurrentDate())
    val selectedDate: StateFlow<Date> get() = _selectedDate

    private val _selectedTime = MutableStateFlow(getCurrentHour())
    val selectedTime: StateFlow<Date> get() = _selectedTime

    private val _selectedRegion = MutableStateFlow("NO5")
    val selectedRegion: StateFlow<String> get() = _selectedRegion

    private val _currentPrice = MutableStateFlow<Double?>(null)
    val currentPrice: StateFlow<Double?> get() = _currentPrice

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _showTomorrowMessage = MutableStateFlow(false)
    val showTomorrowMessage: StateFlow<Boolean> get() = _showTomorrowMessage

    init {
        fetchPrice()
    }

    private fun getCurrentDate(): Date {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Oslo"))
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    private fun getCurrentHour(): Date {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Oslo"))
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    fun fetchPrice() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _showTomorrowMessage.value = false

            val now = getCurrentDate()
            val nowTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Oslo")).time
            val yesterday13Cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Oslo")).apply {
                add(Calendar.DAY_OF_YEAR, -1)
                set(Calendar.HOUR_OF_DAY, 13)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val yesterday13 = yesterday13Cal.time

            if (_selectedDate.value.after(now)) {
                _showTomorrowMessage.value = true
                _isLoading.value = false
                return@launch
            } else if (_selectedDate.value == now && nowTime.before(yesterday13)) {
                _error.value = "Dagens priser er ikke tilgjengelige før kl 13 dagen før."
                _isLoading.value = false
                return@launch
            }

            val dateCal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Oslo")).apply {
                time = _selectedDate.value
            }
            val result = repo.getStrømPriser(
                dateCal.get(Calendar.YEAR),
                dateCal.get(Calendar.MONTH) + 1,
                dateCal.get(Calendar.DAY_OF_MONTH),
                _selectedRegion.value
            )
            when {
                result.isSuccess -> {
                    val prices = result.getOrNull() ?: emptyList()
                    Log.d("VIEWMODEL", "Fetched ${prices.size} prices: $prices")
                    val selectedPrice = prices.find { price ->
                        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
                        formatter.timeZone = TimeZone.getTimeZone("Europe/Oslo")
                        val startTime = formatter.parse(price.time_start)
                        val startCal = Calendar.getInstance().apply { time = startTime }
                        val selectedCal = Calendar.getInstance().apply { time = _selectedTime.value }
                        startCal.get(Calendar.HOUR_OF_DAY) == selectedCal.get(Calendar.HOUR_OF_DAY)
                    }
                    _currentPrice.value = selectedPrice?.NOK_per_kWh
                    if (_currentPrice.value == null && prices.isNotEmpty()) {
                        Log.d("VIEWMODEL", "No exact match for ${_selectedTime.value}, using first available")
                        _currentPrice.value = prices.first().NOK_per_kWh
                    }
                    if (_currentPrice.value == null) {
                        _error.value = "Ingen pris tilgjengelig for valgt dato"
                    }
                }
                result.isFailure -> {
                    Log.e("VIEWMODEL", "Error: ${result.exceptionOrNull()?.message}")
                    _error.value = result.exceptionOrNull()?.message ?: "Ukjent feil"
                }
            }
            _isLoading.value = false
        }
    }

    fun changeDate(days: Int) {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Oslo")).apply {
            time = _selectedDate.value
            add(Calendar.DAY_OF_YEAR, days)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        _selectedDate.value = cal.time
        fetchPrice()
    }

    fun changeTime(hours: Int) {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Oslo")).apply {
            time = _selectedTime.value
            add(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        _selectedTime.value = cal.time
        fetchPrice()
    }

    fun setSelectedRegion(region: String) {
        _selectedRegion.value = region
        fetchPrice()
    }
}