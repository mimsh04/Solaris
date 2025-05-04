package in2000.team42.ui.screens.home.map.WeatherIcon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import in2000.team42.R
import in2000.team42.ui.screens.home.DisplayWeather
import in2000.team42.ui.screens.home.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeatherIconButton(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    var showPopup by remember { mutableStateOf(false) }
    val apiData by viewModel.apiDataFlow.collectAsState()
    val weatherData = apiData.weatherData

    val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    val latestWeather = weatherData.maxByOrNull { displayWeather ->
        dateFormat.parse(displayWeather.month)?.time ?: Long.MIN_VALUE
    }

    // Parse snow and cloud values, removing units (mm for snow, % for cloud)
    val snowValue = latestWeather?.snow?.replace("mm", "")?.trim()?.toDoubleOrNull() ?: 0.0
    val cloudValue = latestWeather?.cloud?.replace("%", "")?.trim()?.toDoubleOrNull() ?: 0.0

    // Velger vær ikon basert på værdata
    val iconResource = when {
        latestWeather == null -> R.drawable.ic_unknown_weather
        snowValue > 0.0 -> R.drawable.ic_snow_cloud
        cloudValue > 70.0 -> R.drawable.ic_cloudy
        else -> R.drawable.ic_sol
    }

    Box(modifier = modifier) {
        IconButton(
            onClick = { showPopup = true }
        ) {
            Image(
                painter = painterResource(
                    id = iconResource
                ),
                contentDescription = "Weather",
                modifier = Modifier.size(24.dp)
            )
        }

        if (showPopup) {
            Popup(
                onDismissRequest = { showPopup = false },
                alignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .width(200.dp),
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Current Weather",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (latestWeather != null) {
                            Text("Temperature: ${latestWeather.temp}")
                            Text("Snow: ${latestWeather.snow}")
                            Text("Cloud Cover: ${latestWeather.cloud}")
                        } else {
                            Text("No weather data available for this area")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showPopup = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary // Solar Yellow
                            )
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherIconButtonPreview() {
    // Mock DisplayWeather for preview
    val mockWeather = DisplayWeather(
        month = "Jan 2025",
        temp = "5.0°C",
        snow = "0.0mm",
        cloud = "30.0%"
    )


    MaterialTheme {
        WeatherIconButton(viewModel = HomeViewModel())
    }
}