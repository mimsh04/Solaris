package in2000.team42.ui.screens.home.map

import com.mapbox.geojson.Point
import com.mapbox.search.ApiType
import com.mapbox.search.ResponseInfo
import com.mapbox.search.ReverseGeoOptions
import com.mapbox.search.SearchCallback
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.result.SearchResult

val searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
    ApiType.SEARCH_BOX,
    SearchEngineSettings()
)

fun getAddressOfPoint (point: Point, callback : (adress : String) -> Unit) {
    val options = ReverseGeoOptions(
        center = point,
        limit = 1
    )

    searchEngine.search(options, object : SearchCallback {
        override fun onResults(results: List<SearchResult>, responseInfo: ResponseInfo) {
            if (results.isNotEmpty()) {
                val address = results[0].address?.formattedAddress()
                    ?: results[0].name
                callback(address)
            } else {
                callback("Ingen adresse funnet")
            }
        }

        override fun onError(e: Exception) {
            callback("Ingen adresse funnet")
        }
    })
}