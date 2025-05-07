package in2000.team42.ui.screens.home.bottomSheet.grafer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import in2000.team42.ui.screens.home.ApiData

@Composable

fun AlleGrafer(apiData: ApiData) {
    if (apiData.isLoading) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row {
                Text(
                    text = "Laster inn data...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp).padding(start = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            }

        }
    } else {
        Column {
            if (apiData.sunRadiation.isNotEmpty()) {
                Solradiasjon(
                    solData = apiData.sunRadiation
                )
            }
            if (apiData.weatherData.isNotEmpty() and
                apiData.kwhMonthlyData.isNotEmpty()) {
                StromProduksjonGraf(
                    kwhMonthlyData = apiData.kwhMonthlyData,
                    weatherData = apiData.weatherData,
                    modifier = Modifier.height(300.dp)
                )
            }
        }

    }
}