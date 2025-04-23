package in2000.team42.ui.screens.home.map.WeatherIcon

import android.annotation.SuppressLint
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
import in2000.team42.ui.screens.home.HomeViewModel

@Composable
fun WeatherIconButton(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    var showPopup by remember { mutableStateOf(false) }
    val weatherData by viewModel.weatherData.collectAsState()

    // Select the latest DisplayWeather (or null if empty)
    val latestWeather = weatherData.maxByOrNull { it.month }

    // Determine the icon based on weather conditions
    val iconResource = when {
        latestWeather == null -> R.drawable.ic_unknown_weather
        latestWeather.snow.toDoubleOrNull()?.let { it > 0.0 } == true -> R.drawable.ic_snow_cloud
        latestWeather.cloud.toDoubleOrNull()?.let { it > 50.0 } == true -> R.drawable.ic_cloudy
        else -> R.drawable.ic_sol
    }

    Box(modifier = modifier) {
        IconButton(
            onClick = { showPopup = true }
        ) {
            Image(
                painter = painterResource(id = iconResource),
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
                            Text("Temperatur: ${latestWeather.temp}")
                            Text("Snø: ${latestWeather.snow}mm")
                            Text("Værdekke: ${latestWeather.cloud}")
                        } else {
                            Text("Ingen værdata tilgjengelig for dette området")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showPopup = false }
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun WeatherIconButtonPreview() {
    MaterialTheme {
        WeatherIconButton(viewModel = HomeViewModel())
    }
}