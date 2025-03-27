package in2000.team42.data.frost

import android.util.Log // Added import for Logcat
import in2000.team42.data.frost.model.FrostData

class FrostRepository(private val dataSource: FrostDatasource) {
    private val TAG = "FrostRepository" // Logcat tag for this class

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