package in2000.team42.ui.screens.home.map.WeatherIcon

import in2000.team42.R
import android.R.attr.contentDescription
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
//import androidx.compose.material.icons.filled.Cloud
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun WeatherIconButton(
    modifier: Modifier = Modifier
) {
    var showPopup by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = { showPopup = true }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.sun_icon_in2000),
                contentDescription = "Weather",
                tint = MaterialTheme.colorScheme.primary
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
                        modifier = Modifier
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Current Weather",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Dummy weather data
                        Text("Temperatur: 19°C")
                        Text("Tilstand: Sunny")
                        Text("Værdekke: 65%")

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

// Preview function
@Preview(showBackground = true)
@Composable
fun WeatherIconButtonPreview() {
    MaterialTheme {
        WeatherIconButton()
    }
}