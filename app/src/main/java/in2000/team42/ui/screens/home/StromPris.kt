package in2000.team42.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import in2000.team42.ui.screens.HKS.HKSViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePicker(viewModel: HKSViewModel) {
    val selectedDate = remember { mutableStateOf(viewModel.selectedDate.value ?: Calendar.getInstance()) }
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    // Update state when LiveData changes
    LaunchedEffect(viewModel.selectedDate) {
        viewModel.selectedDate.value?.let { selectedDate.value = it }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        IconButton(onClick = { viewModel.changeDate(-1) }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Previous day")
        }
        Text(
            text = dateFormat.format(selectedDate.value.time),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = { viewModel.changeDate(1) }) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Next day")
        }
    }
}

@Composable
fun TimePicker(viewModel: HKSViewModel) {
    val selectedTime = remember { mutableStateOf(viewModel.selectedTime.value ?: Calendar.getInstance()) }
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    LaunchedEffect(viewModel.selectedTime) {
        viewModel.selectedTime.value?.let { selectedTime.value = it }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        IconButton(onClick = { viewModel.changeTime(-1) }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Previous hour")
        }
        Text(
            text = timeFormat.format(selectedTime.value.time),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = { viewModel.changeTime(1) }) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Next hour")
        }
    }
}

@Composable
fun RegionSelector(viewModel: HKSViewModel) {
    val selectedRegion = remember { mutableStateOf(viewModel.selectedRegion.value ?: "NO1") }
    var expanded by remember { mutableStateOf(false) }
    val regions = listOf("NO1", "NO2", "NO3", "NO4", "NO5")

    LaunchedEffect(viewModel.selectedRegion) {
        viewModel.selectedRegion.value?.let { selectedRegion.value = it }
    }

    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Region: ${selectedRegion.value}")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            regions.forEach { region ->
                DropdownMenuItem(
                    text = { Text(region) },
                    onClick = {
                        viewModel.setSelectedRegion(region)
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PriceDisplay(viewModel: HKSViewModel) {
    val currentPrice = remember { mutableStateOf(viewModel.currentPrice.value) }
    val isLoading = remember { mutableStateOf(viewModel.isLoading.value ?: false) }
    val error = remember { mutableStateOf(viewModel.error.value) }
    val showTomorrowMessage = remember { mutableStateOf(viewModel.showTomorrowMessage.value ?: false) }

    LaunchedEffect(viewModel.currentPrice) {
        currentPrice.value = viewModel.currentPrice.value
    }
    LaunchedEffect(viewModel.isLoading) {
        viewModel.isLoading.value?.let { isLoading.value = it }
    }
    LaunchedEffect(viewModel.error) {
        error.value = viewModel.error.value
    }
    LaunchedEffect(viewModel.showTomorrowMessage) {
        viewModel.showTomorrowMessage.value?.let { showTomorrowMessage.value = it }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        when {
            isLoading.value -> {
                CircularProgressIndicator()
                Text(
                    text = "Laster...",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            error.value != null -> {
                Text(
                    text = "Feil: ${error.value}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
            showTomorrowMessage.value -> {
                Text(
                    text = "Priser for morgendagen er tilgjengelige etter kl. 13 i dag",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            currentPrice.value != null -> {
                Text(
                    text = "Pris: ${"%.2f".format(currentPrice.value!! * 100)} øre/kWh",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            else -> {
                Text(
                    text = "Ingen pris tilgjengelig ennå",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}