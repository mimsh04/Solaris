package in2000.team42.data.frost

import in2000.team42.model.frost.FrostData

class FrostRepository(private val dataSource: FrostDataSource) {

    suspend fun fetchFrostDataByCoords(latitude: Double, longitude: Double): FrostData? {
        return dataSource.fetchFrostDataByCoords(latitude, longitude)
    }
}