package in2000.team42.data.frost

import android.util.Log
import in2000.team42.data.frost.model.FrostResult
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class FrostRepository(private val dataSource: FrostDatasource) {
    private val tag = "FrostRepository"

    fun get1YearReferenceTime(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Oslo"))
        val endTime = calendar.time // Current time

        calendar.add(Calendar.YEAR, -1)
        val startTime = calendar.time

        // Formats data with the specified format and timezone
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("Europe/Oslo")

        val startFormatted = formatter.format(startTime)
        val endFormatted = formatter.format(endTime)

        return "$startFormatted/$endFormatted"
    }

    /**
     * Retrieves weather data based on coordinates and retains null values for missing data.
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param referenceTime Time interval for data (e.g., "2024-01-01/2024-12-31")
     * @return FrostResult with a list of FrostData objects, where missing data is retained as null
     * @see FrostDatasource.getWeatherData
     */
    suspend fun getWeatherByCoordinates(latitude: Double, longitude: Double, referenceTime: String): FrostResult {
        Log.d(tag, "Getting weather data for coordinates ($latitude, $longitude) at time $referenceTime")

        val stationIds = dataSource.getNearestStation(latitude, longitude, referenceTime) ?: run {
            Log.w(tag, "No station found for coordinates ($latitude, $longitude)")
            return FrostResult.Failure("No station found for coordinates ($latitude, $longitude)")
        }
        Log.i(tag, "Using station IDs: $stationIds for weather data")

        val weatherResult = dataSource.getWeatherData(stationIds, referenceTime)
        return when (weatherResult) {
            is FrostResult.Success -> {
                Log.i(tag, "Retrieved ${weatherResult.data.size} weather data points for stations $stationIds")
                FrostResult.Success(weatherResult.data)
            }
            is FrostResult.Failure -> {
                Log.e(tag, "Failed to retrieve weather data: ${weatherResult.message}")
                weatherResult
            }
        }
    }
}