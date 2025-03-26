package in2000.team42.ui.screens.home.bottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp


@Composable
fun RegionSelector(
    selectedRegion: String,
    onRegionSelected: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val regions = listOf(
        "NO1" to "Oslo / Øst-Norge",
        "NO2" to "Kristiansand / Sør-Norge",
        "NO3" to "Trondheim / Midt-Norge",
        "NO4" to "Tromsø / Nord-Norge",
        "NO5" to "Bergen / Vest-Norge"
    )

    val selectedDisplayName = regions.find { it.first == selectedRegion }?.second ?: selectedRegion

    Box(modifier = modifier) {
        Button(
            onClick = { expanded = true },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Region: $selectedDisplayName",
                    style = MaterialTheme.typography.bodyLarge
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Velg region"
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            regions.forEach { (regionCode, displayName) ->
                DropdownMenuItem(
                    text = { Text(displayName, style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onRegionSelected(regionCode)
                        expanded = false
                    },
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}