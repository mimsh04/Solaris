package in2000.team42.ui.screens.home.bottomSheetKomp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Title
        Text(
            text = "Hva koster strøm?",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Date Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Dato",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Dato:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.width(100.dp)
                )
                Text(
                    text = dateFormatter.format(selectedDate),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            NavigationButtons(
                onPrevious = { viewModel.changeDate(-1) },
                onNext = { viewModel.changeDate(1) },
                enabled = !isLoading
            )
        }

        // Time Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange, // Changed to AccessTime for time
                    contentDescription = "Klokkeslett",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Klokkeslett:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.width(100.dp) // Fixed width for alignment
                )
                Text(
                    text = timeFormatter.format(selectedTime),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            NavigationButtons(
                onPrevious = { viewModel.changeTime(-1) },
                onNext = { viewModel.changeTime(1) },
                enabled = !isLoading
            )
        }

        // Price Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                isLoading -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Strømpris:",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.width(100.dp)
                        )
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                error != null -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Strømpris:",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.width(100.dp)
                        )
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                currentPrice != null -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Strømpris:",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.width(100.dp)
                        )
                        Text(
                            text = "${"%.2f".format(currentPrice!! * 100)} øre/kWh",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Strømpris:",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.width(100.dp)
                        )
                        Text(
                            text = "Ingen pris tilgjengelig",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Region Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RegionSelector(
                selectedRegion = selectedRegion,
                onRegionSelected = { viewModel.setSelectedRegion(it) },
                enabled = !isLoading,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

