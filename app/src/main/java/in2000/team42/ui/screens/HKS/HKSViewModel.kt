package in2000.team42.ui.screens.HKS

import androidx.lifecycle.ViewModel
import in2000.team42.data.HvaKosterStrømmen.HvaKosterStrømmenRepo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


import java.util.Calendar

class HKSViewModel(
    private val repo: HvaKosterStrømmenRepo = HvaKosterStrømmenRepo()
) : ViewModel() {

    private val _selectedDate = MutableLiveData(Calendar.getInstance())
    val selectedDate: LiveData<Calendar> get() = _selectedDate

    private val _selectedTime = MutableLiveData(Calendar.getInstance())
    val selectedTime: LiveData<Calendar> get() = _selectedTime

    private val _selectedRegion = MutableLiveData("NO1")
    val selectedRegion: LiveData<String> get() = _selectedRegion

    private val _currentPrice = MutableLiveData<Double?>()
    val currentPrice: LiveData<Double?> get() = _currentPrice

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _showTomorrowMessage = MutableLiveData(false)
    val showTomorrowMessage: LiveData<Boolean> get() = _showTomorrowMessage

    init {
        fetchPrice()
    }

    fun fetchPrice() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _showTomorrowMessage.value = false

            val now = Calendar.getInstance()
            val yesterday = Calendar.getInstance().apply {
                time = now.time
                add(Calendar.DAY_OF_MONTH, -1)
                set(Calendar.HOUR_OF_DAY, 13)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Updated logic for handling tomorrow's URL response
            if (_selectedDate.value!!.after(now)) {
                val tomorrowResult = repo.getStrømPriser(
                    _selectedDate.value!!.get(Calendar.YEAR),
                    _selectedDate.value!!.get(Calendar.MONTH) + 1,
                    _selectedDate.value!!.get(Calendar.DAY_OF_MONTH),
                    _selectedRegion.value!!
                )
                if (tomorrowResult.isSuccess) {
                    _showTomorrowMessage.value = true
                } else {
                    _error.value = "URL for morgendagens strømpriser svarer ikke. Prøv igjen senere."
                }
                _isLoading.value = false
                return@launch
            } else if (_selectedDate.value!!.timeInMillis == now.timeInMillis && now.before(yesterday)) {
                _error.value = "Dagens priser er ikke tilgjengelige før kl 13 dagen før."
                _isLoading.value = false
                return@launch
            }

            val result = repo.getStrømPriser(
                _selectedDate.value!!.get(Calendar.YEAR),
                _selectedDate.value!!.get(Calendar.MONTH) + 1,
                _selectedDate.value!!.get(Calendar.DAY_OF_MONTH),
                _selectedRegion.value!!
            )

            if (result.isSuccess) {
                val prices = result.getOrNull() ?: emptyList()

                if (prices.isEmpty()) {
                    _error.value = "Ingen tilgjengelige strømpriser for valgt dato. Prøv igjen senere."
                } else {
                    val selectedPrice = prices.find { price ->
                        val startTime = Calendar.getInstance().apply {
                            time = repo.parseTime(price.time_start)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val targetTime = Calendar.getInstance().apply {
                            time = _selectedTime.value!!.time
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        startTime.timeInMillis == targetTime.timeInMillis
                    }
                    _currentPrice.value = selectedPrice?.NOK_per_kWh
                    if (_currentPrice.value == null && prices.isNotEmpty()) {
                        _currentPrice.value = prices.first().NOK_per_kWh
                    }
                    if (_currentPrice.value == null) {
                        _error.value = "Ingen pris tilgjengelig for valgt dato"
                    }
                }
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Ukjent feil"
            }

            _isLoading.value = false
        }
    }

    fun changeDate(days: Int) {
        _selectedDate.value = _selectedDate.value!!.apply {
            add(Calendar.DAY_OF_MONTH, days)
        }
        fetchPrice()
    }

    fun changeTime(hours: Int) {
        _selectedTime.value = _selectedTime.value!!.apply {
            add(Calendar.HOUR_OF_DAY, hours)
        }
        fetchPrice()
    }

    fun setSelectedRegion(region: String) {
        _selectedRegion.value = region
        fetchPrice()
    }
}