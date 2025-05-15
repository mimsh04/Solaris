package in2000.team42.ui.screens.home.bottomSheet.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import in2000.team42.R
import in2000.team42.ui.screens.home.ApiData

@Composable
fun AllCharts(apiData: ApiData) {
    if (apiData.isLoading) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = stringResource(R.string.loading_data_message),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.padding(12.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(start = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }

        }
    } else {
        Column {
            if (apiData.sunRadiation.isNotEmpty()) {
                Box(modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        RoundedCornerShape(16.dp)
                    )
                ) {
                    SolarRadiationChart(
                        solData = apiData.sunRadiation
                    )

                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            if (apiData.weatherData.isNotEmpty() and
                apiData.kwhMonthlyData.isNotEmpty()) {
                Box(modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        RoundedCornerShape(16.dp)
                    )
                ) {
                    PowerProductionChart(
                        kwhMonthlyData = apiData.kwhMonthlyData,
                        weatherData = apiData.weatherData,
                        modifier = Modifier.height(300.dp)
                    )
                }

            }
        }

    }
}