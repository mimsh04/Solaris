package in2000.team42.ui.screens.strommen

import androidx.lifecycle.ViewModel
import in2000.team42.data.strommen.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.coroutines.flow.*
import java.util.*
import java.text.SimpleDateFormat


class StrommenViewModel(
    private val repo: StrommenRepo = StrommenRepo()
) : ViewModel() {

    private val osloTimeZone = TimeZone.getTimeZone("Europe/Oslo")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        timeZone = osloTimeZone
    }
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
        timeZone = osloTimeZone
    }
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault()).apply {
        timeZone = osloTimeZone
    }

    private val _selectedDate = MutableStateFlow(getCurrentDate())
    val selectedDate: StateFlow<Date> get() = _selectedDate

    private val _selectedTime = MutableStateFlow(getCurrentHour())
    val selectedTime: StateFlow<Date> get() = _selectedTime

    private val _selectedRegion = MutableStateFlow("NO1")
    val selectedRegion: StateFlow<String> get() = _selectedRegion

    private val _currentPrice = MutableStateFlow<Double?>(null)
    val currentPrice: StateFlow<Double?> get() = _currentPrice

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    init {
        fetchPrice()
    }
    /**
     * Returnerer dagens dato satt til midnatt (00:00:00).
     */
    private fun getCurrentDate(): Date {
        val calendar = Calendar.getInstance(osloTimeZone)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
    /**
     * Returnerer nåværende time, med minutter og sekunder satt til null.
     */
    private fun getCurrentHour(): Date {
        val calendar = Calendar.getInstance(osloTimeZone)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
    /**
     * Returnerer dagens dato med tidspunkt satt til kl. 13:00.
     */
    private fun getTodayAt13(): Date {
        return Calendar.getInstance(osloTimeZone).apply {
            set(Calendar.HOUR_OF_DAY, 13)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
    /**
     * Henter strømpris for den valgte datoen og tiden.
     * Utfører diverse sjekker før API-kallet, som:
     * - Om priser for morgendagen er tilgjengelige (etter kl. 13)
     * - Om brukeren har valgt en dato lenger frem enn i morgen
     * - Om dagens priser er tilgjengelige (fra kl. 13 dagen før)
     */
    private fun fetchPrice() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val now = Calendar.getInstance(osloTimeZone).time
            val today = getCurrentDate()
            val tomorrow = Calendar.getInstance(osloTimeZone).apply {
                time = today
                add(Calendar.DAY_OF_YEAR, 1)
            }.time

            if (dateFormat.format(_selectedDate.value) == dateFormat.format(tomorrow)) {
                val currentHour = Calendar.getInstance(osloTimeZone).get(Calendar.HOUR_OF_DAY)

                if (currentHour < 13) {
                    _error.value = "Priser for i morgen vil være tilgjengelig etter kl. 13:00 i dag"
                    _isLoading.value = false
                    return@launch
                }
            }
            else if (_selectedDate.value.after(tomorrow)) {
                _error.value = "Kan bare vise priser for i dag og i morgen"
                _isLoading.value = false
                return@launch
            }

            if (dateFormat.format(_selectedDate.value) == dateFormat.format(today) &&
                now.before(getTodayAt13())) {
                _error.value = "Dagens priser er ikke tilgjengelige før kl 13 dagen før."
                _isLoading.value = false
                return@launch
            }

            val calendar = Calendar.getInstance(osloTimeZone).apply {
                time = _selectedDate.value
            }
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val result = repo.getStromPriser(
                year,
                month,
                day,
                selectedRegion.value
            )

            if (result.isSuccess) {
                val prices = result.getOrNull() ?: emptyList()

                if (prices.isEmpty()) {
                    _error.value = "Ingen tilgjengelige strømpriser for valgt dato. Prøv igjen senere."
                } else {
                    Log.d("VIEWMODEL", "Fetched ${prices.size} prices: $prices")

                    val targetCalendar = Calendar.getInstance(osloTimeZone).apply {
                        time = _selectedTime.value
                    }
                    val targetHour = targetCalendar.get(Calendar.HOUR_OF_DAY)

                    val selectedPrice = prices.find { price ->
                        try {
                            val priceTime = dateTimeFormat.parse(price.time_start)
                            val priceCalendar = Calendar.getInstance(osloTimeZone).apply {
                                time = priceTime
                            }
                            priceCalendar.get(Calendar.HOUR_OF_DAY) == targetHour
                        } catch (e: Exception) {
                            false
                        }
                    }

                    _currentPrice.value = selectedPrice?.NOK_per_kWh
                    if (_currentPrice.value == null && prices.isNotEmpty()) {
                        Log.d("VIEWMODEL", "No exact match for ${timeFormat.format(_selectedTime.value)}, using first available")
                        _currentPrice.value = prices.first().NOK_per_kWh
                    }
                    if (_currentPrice.value == null) {
                        _error.value = "Ingen pris tilgjengelig for valgt dato"
                    }
                }
            } else {
                Log.e("VIEWMODEL", "Error: ${result.exceptionOrNull()?.message}")
                _error.value = result.exceptionOrNull()?.message ?: "Ukjent feil"
            }

            _isLoading.value = false
        }
    }
    /**
     * Endrer valgt dato med en gitt mengde dager og henter nye priser.
     */
    fun changeDate(days: Int) {
        val calendar = Calendar.getInstance(osloTimeZone).apply {
            time = _selectedDate.value
            add(Calendar.DAY_OF_YEAR, days)
        }
        _selectedDate.value = calendar.time
        fetchPrice()
    }
    /**
     * Endrer valgt tid med en gitt mengde timer og henter nye priser.
     */
    fun changeTime(hours: Int) {
        val calendar = Calendar.getInstance(osloTimeZone).apply {
            time = _selectedTime.value
            add(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        _selectedTime.value = calendar.time
        fetchPrice()
    }
    /**
     * Setter valgt region og henter nye priser.
     */
    fun setSelectedRegion(region: String) {
        _selectedRegion.value = region
        fetchPrice()
    }
}