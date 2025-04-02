package in2000.team42.data.frost

import android.util.Log // Added import for Logcat
import in2000.team42.data.frost.model.FrostData
import in2000.team42.data.frost.model.FrostResult
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class FrostRepository(private val dataSource: FrostDatasource) {
    private val TAG = "FrostRepository" // Logcat tag for this class

    fun getLast24HoursReferenceTime(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Oslo"))
        val endTime = calendar.time // Current time

        // Subtract 24 hours
        calendar.add(Calendar.HOUR_OF_DAY, -24)
        val startTime = calendar.time

        // Formatterer data med
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("Europe/Oslo")

        val startFormatted = formatter.format(startTime)
        val endFormatted = formatter.format(endTime)

        return "$startFormatted/$endFormatted"
    }

    /**
     * @see FrostDatasource.getWeatherData
     */
    suspend fun getWeatherByCoordinates(latitude: Double, longitude: Double, referenceTime: String): FrostResult {
        val referenceTimeTest = "2024-01-01/2024-12-31"
        Log.d(TAG, "Getting weather data for coordinates ($latitude, $longitude) at time $referenceTimeTest")

        val stationIds = dataSource.getNearestStation(latitude, longitude, referenceTimeTest) ?: run {
            Log.w(TAG, "No station found for coordinates ($latitude, $longitude)")
            return FrostResult.Failure("No station found for coordinates ($latitude, $longitude)")
        }
        Log.i(TAG, "Using station IDs: $stationIds for weather data")

        val weatherResult = dataSource.getWeatherData(stationIds, referenceTimeTest)
        when (weatherResult) {
            is FrostResult.Success -> Log.i(TAG, "Retrieved ${weatherResult.data.size} weather data points for stations $stationIds")
            is FrostResult.Failure -> Log.e(TAG, "Failed to retrieve weather data: ${weatherResult.message}")
        }
        return weatherResult
    }
}