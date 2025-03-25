package in2000.team42.ui.screens.home.bottomSheetKomp



import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import in2000.team42.ui.screens.strommen.StrommenViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StrommenContent() {
    val viewModel: StrommenViewModel = viewModel()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedTime by viewModel.selectedTime.collectAsState()
    val selectedRegion by viewModel.selectedRegion.collectAsState()
    val currentPrice by viewModel.currentPrice.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val dateFormatter = remember {
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Europe/Oslo")
        }
    }
    val timeFormatter = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Europe/Oslo")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hva koster strøm?",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            IconButton(
                onClick = { viewModel.changeDate(-1) },
                enabled = !isLoading
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Forrige dag")
            }
            Text(
                text = "Dato: ${dateFormatter.format(selectedDate)}",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            IconButton(
                onClick = { viewModel.changeDate(1) },
                enabled = !isLoading
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Neste dag")
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            IconButton(
                onClick = { viewModel.changeTime(-1) },
                enabled = !isLoading
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Forrige time")
            }
            Text(
                text = "Klokkeslett: ${timeFormatter.format(selectedTime)}",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            IconButton(
                onClick = { viewModel.changeTime(1) },
                enabled = !isLoading
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Neste time")
            }
        }

        RegionSelector(
            selectedRegion = selectedRegion,
            onRegionSelected = { viewModel.setSelectedRegion(it) },
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            error != null -> {
                Text(
                    text = error!!,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
            currentPrice != null -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Strømpris:",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = "${"%.2f".format(currentPrice!! * 100)} øre/kWh",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
            else -> {
                Text(
                    text = "Ingen pris tilgjengelig",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun RegionSelector(
    selectedRegion: String,
    onRegionSelected: (String) -> Unit,
    enabled: Boolean = true
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

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        Button(
            onClick = { expanded = true },
            enabled = enabled
        ) {
            Text("Region: $selectedDisplayName")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            regions.forEach { (regionCode, displayName) ->
                DropdownMenuItem(
                    text = { Text(displayName) },
                    onClick = {
                        onRegionSelected(regionCode)
                        expanded = false
                    }
                )
            }
        }
    }
}