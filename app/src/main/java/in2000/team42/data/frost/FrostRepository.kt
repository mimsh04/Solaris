package in2000.team42.data.frost

import in2000.team42.data.frost.model.FrostData

class FrostRepository(private val dataSource: FrostDatasource) {

    suspend fun fetchFrostDataByCoords(latitude: Double, longitude: Double): FrostData? {
        return dataSource.fetchFrostDataByCoords(latitude, longitude)
    }

    suspend fun fetchNearestStation(latitude: Double, longitude: Double): String {
        return dataSource.fetchNearestStation(latitude, longitude)
    }

}