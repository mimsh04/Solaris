package in2000.team42.ui.screens.home.map.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import kotlinx.coroutines.launch

@Composable
fun SearchBar(
    placeAutocomplete: PlaceAutocomplete,
    onLocationSelected: (Point) -> Unit,
    modifier: Modifier = Modifier,
    isMapClicked: Boolean = false
) {
    var searchQuery by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<PlaceAutocompleteSuggestion>>(emptyList()) }
    var isExpanded by remember { mutableStateOf(false) }


    LaunchedEffect(isMapClicked) {
        if (isMapClicked) {
            isExpanded = false
        }
    }

    val coroutineScope = rememberCoroutineScope()

    fun handleSuggestionClick(suggestion: PlaceAutocompleteSuggestion) {
        coroutineScope.launch {
            try {
                val result = placeAutocomplete.select(suggestion)
                val point = result.value?.coordinate!!
                onLocationSelected(point)
                searchQuery = suggestion.name
                isExpanded = false
            } catch (e: Exception) {
                Log.e("SearchBar", "Error selecting place", e)
            }
        }
    }

    fun handleSearchChange (query: String) {
        searchQuery = query
        if (query.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    val response = placeAutocomplete.suggestions(query)
                    suggestions = response.value ?: emptyList()
                    isExpanded = true
                } catch (e: Exception) {
                    Log.e("SearchBar", "Error getting suggestions", e)
                    suggestions = emptyList()
                }
            }
        } else {
            suggestions = emptyList()
            isExpanded = false
        }
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Search bar with rounded corners
            TextField(
                value = searchQuery,
                onValueChange = { query ->
                    handleSearchChange(query)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                ,
                placeholder = { Text("Søk etter adresse") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true,
                shape = MaterialTheme.shapes.extraLarge,
            )

            // Suggestions dropdown
            if (isExpanded && suggestions.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    suggestions.forEach { suggestion ->
                        SuggestionItem(suggestion, onClick = {
                            handleSuggestionClick(suggestion)
                        })
                    }
                }
            }
        }
    }
}