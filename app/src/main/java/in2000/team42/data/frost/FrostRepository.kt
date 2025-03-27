package in2000.team42.data.frost

import android.util.Log // Added import for Logcat
import in2000.team42.data.frost.model.FrostData
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

    suspend fun getWeatherByCoordinates(latitude: Double, longitude: Double, referenceTime: String): List<FrostData> {
        Log.d(TAG, "Getting weather data for coordinates ($latitude, $longitude) at time $referenceTime")

        val stationId = dataSource.getNearestStation(latitude, longitude) ?: run {
            Log.w(TAG, "No station found for coordinates ($latitude, $longitude)")
            return emptyList()
        }
        Log.i(TAG, "Using station ID: $stationId for weather data")

        val weatherData = dataSource.getWeatherData(stationId, referenceTime)
        Log.i(TAG, "Retrieved ${weatherData.size} weather data points for station $stationId")

        return weatherData
    }
}