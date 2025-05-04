package in2000.team42.ui.screens.home.map.WeatherIcon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.composables.core.Icon
import in2000.team42.R
import in2000.team42.ui.screens.home.DisplayWeather
import in2000.team42.ui.screens.home.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun WeatherIconButton(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    var showPopup by remember { mutableStateOf(false) }
    var showImageInfo by remember { mutableStateOf(false) }
    val apiData by viewModel.apiDataFlow.collectAsState()
    val weatherData = apiData.weatherData

    val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    val latestWeather = weatherData.maxByOrNull { displayWeather ->
        dateFormat.parse(displayWeather.month)?.time ?: Long.MIN_VALUE
    }

    // Parser værdata til riktig format for å sammenligne hvilket ikon som skal vises
    val snowValue = latestWeather?.snow?.replace("mm", "")?.trim()?.toDoubleOrNull() ?: 0.0
    val cloudValue = latestWeather?.cloud?.replace("%", "")?.trim()?.toDoubleOrNull() ?: 0.0

    // Velger vær ikon basert på værdata
    val iconResource = when {
        weatherData.isEmpty() -> R.drawable.ic_unknown_weather
        latestWeather == null -> R.drawable.ic_unknown_weather
        snowValue > 0.0 -> R.drawable.ic_snow_cloud
        cloudValue > 60.0 -> R.drawable.ic_cloudy
        else -> R.drawable.ic_sol
    }

    Box(modifier = modifier) {
        IconButton(
            onClick = { showPopup = true }
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp) // Størrelse for bakgrunn til ikon
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconResource),
                    contentDescription = "Weather",
                    modifier = Modifier
                        .size(40.dp) // Størrelse for ikon
                        .padding(4.dp), // Padding gjør at ikon vil alltid være mindre enn bakgrunnen
                    colorFilter = if (iconResource == R.drawable.ic_unknown_weather) {
                        ColorFilter.tint(MaterialTheme.colorScheme.onSurface) // Apply gray tint only to ic_unknown_weather
                    } else {
                        null // No color filter for other drawables
                    }
                )
            }
        }

        if (showPopup) {
            Popup(
                onDismissRequest = { showPopup = false },
                alignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .width(250.dp),
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Text(
                                    text = "Gjennomsnittlig vær for måneden",
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                IconButton(
                                    onClick = { showImageInfo = !showImageInfo },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .align(Alignment.CenterEnd)
                                        .offset(x = ((10).dp)) // Offset fra midten til høyre siden
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = "Bildeinfo",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        if (weatherData.isEmpty() || latestWeather == null) {
                            Text(
                                text = "Værdata ukjent.\n\nKunne ikke hente værdata fra dette området.\n\nPrøv å velge en annen adresse.",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text("Temperatur: ${latestWeather.temp}")
                            Text("Snø: ${latestWeather.snow}")
                            Text("Skydekke: ${latestWeather.cloud}")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showPopup = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Lukk")
                        }
                    }
                }
            }
        }

        if (showImageInfo) {
            Popup(
                onDismissRequest = { showImageInfo = false },
                alignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .wrapContentHeight()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 8.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CreditsScreen()
                            Button(
                                onClick = { showImageInfo = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Lukk")
                            }
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