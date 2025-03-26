package in2000.team42.data.frost

import in2000.team42.data.frost.model.FrostData

class FrostRepository(private val dataSource: FrostDatasource) {

    suspend fun getFrostData(latitude: Double, longitude: Double): FrostData? {
        return dataSource.getFrostData(latitude, longitude)
    }

    suspend fun fetchNearestStation(latitude: Double, longitude: Double): String? {
        return dataSource.getNearestStation(latitude, longitude)
    }

}