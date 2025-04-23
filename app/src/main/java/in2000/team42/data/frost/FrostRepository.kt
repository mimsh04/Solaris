package in2000.team42.data.frost

import android.util.Log
import in2000.team42.data.frost.model.FrostData
import in2000.team42.data.frost.model.FrostResult
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class FrostRepository(private val dataSource: FrostDatasource) {
    private val TAG = "FrostRepository" // Logcat tag for this class

    fun get1YearReferenceTime(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Oslo"))
        val endTime = calendar.time // Current time

        // Subtract 24 hours
        calendar.add(Calendar.YEAR, -1)
        val startTime = calendar.time

        // Formatterer data med
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("Europe/Oslo")

        val startFormatted = formatter.format(startTime)
        val endFormatted = formatter.format(endTime)

        return "$startFormatted/$endFormatted"
    }

    /**
     * Henter værdata basert på koordinater og sikrer at alle FrostData-objekter har verdier som ikke er null
     * ved å erstatte nullverdier med standardverdier (f.eks. 0.0).
     *
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param referenceTime Tidintervall for data (e.g., "2024-01-01/2024-12-31")
     * @return FrostResult med en liste av FrostData-objekter, der nullverdier erstattes med standardverdier.
     * @see FrostDatasource.getWeatherData
     */
    suspend fun getWeatherByCoordinates(latitude: Double, longitude: Double, referenceTime: String): FrostResult {
        val referenceTimeTest = "2024-01-01/2024-12-31"
        Log.d(TAG, "Getting weather data for coordinates ($latitude, $longitude) at time $referenceTime")

        val stationIds = dataSource.getNearestStation(latitude, longitude, referenceTime) ?: run {
            Log.w(TAG, "No station found for coordinates ($latitude, $longitude)")
            return FrostResult.Failure("No station found for coordinates ($latitude, $longitude)")
        }
        Log.i(TAG, "Using station IDs: $stationIds for weather data")

        val weatherResult = dataSource.getWeatherData(stationIds, referenceTime)
        return when (weatherResult) {
            is FrostResult.Success -> {
                // Erstatter null-verdier med standardverdier
                val usableData = weatherResult.data.map { data ->
                    FrostData(
                        stationId = data.stationId,
                        referenceTime = data.referenceTime,
                        temperature = data.temperature ?: 0.0,
                        snow = data.snow ?: 0.0,
                        cloudAreaFraction = data.cloudAreaFraction ?: 0.0,
                        qualityCode = data.qualityCode
                    )
                }
                Log.i(TAG, "Retrieved ${usableData.size} weather data points for stations $stationIds")
                FrostResult.Success(usableData)
            }
            is FrostResult.Failure -> {
                Log.e(TAG, "Failed to retrieve weather data: ${weatherResult.message}")
                weatherResult
            }
        }
    }
}