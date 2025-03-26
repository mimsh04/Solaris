package in2000.team42.ui.screens.home.map.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion

@Composable
fun SuggestionItem(
    suggestion: PlaceAutocompleteSuggestion,
    onClick: () -> Unit
) {
    Text(
        text = suggestion.name,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    )
}