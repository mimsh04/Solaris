package in2000.team42.data.frost

import android.util.Log
import in2000.team42.data.frost.model.FrostResult
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class FrostRepository(private val dataSource: FrostDatasource) {
    private val TAG = "FrostRepository"

    fun get1YearReferenceTime(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Oslo"))
        val endTime = calendar.time // Current time

        calendar.add(Calendar.YEAR, -1)
        val startTime = calendar.time

        // Formatterer data med gitt format og tidzone
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("Europe/Oslo")

        val startFormatted = formatter.format(startTime)
        val endFormatted = formatter.format(endTime)

        return "$startFormatted/$endFormatted"
    }

    /**
     * Henter værdata basert på koordinater og beholder null-verdier for manglende data.
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param referenceTime Tidintervall for data (e.g., "2024-01-01/2024-12-31")
     * @return FrostResult med en liste av FrostData-objekter, der manglende data beholdes som null
     * @see FrostDatasource.getWeatherData
     */
    suspend fun getWeatherByCoordinates(latitude: Double, longitude: Double, referenceTime: String): FrostResult {
        Log.d(TAG, "Getting weather data for coordinates ($latitude, $longitude) at time $referenceTime")

        val stationIds = dataSource.getNearestStation(latitude, longitude, referenceTime) ?: run {
            Log.w(TAG, "No station found for coordinates ($latitude, $longitude)")
            return FrostResult.Failure("No station found for coordinates ($latitude, $longitude)")
        }
        Log.i(TAG, "Using station IDs: $stationIds for weather data")

        val weatherResult = dataSource.getWeatherData(stationIds, referenceTime)
        return when (weatherResult) {
            is FrostResult.Success -> {
                Log.i(TAG, "Retrieved ${weatherResult.data.size} weather data points for stations $stationIds")
                FrostResult.Success(weatherResult.data)
            }
            is FrostResult.Failure -> {
                Log.e(TAG, "Failed to retrieve weather data: ${weatherResult.message}")
                weatherResult
            }
        }
    }
}