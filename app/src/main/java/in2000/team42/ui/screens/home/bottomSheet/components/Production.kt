package in2000.team42.ui.screens.home.bottomSheet.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import in2000.team42.data.productionCalculation.calculateWithCoverage
import in2000.team42.ui.screens.home.ApiData

private fun getYearlyProduction(
    apiData: ApiData

): Double {
    val calculatedData = calculateWithCoverage(apiData.kwhMonthlyData, apiData.weatherData)
    var tot = 0.0
    calculatedData.forEach {
        tot += it.kWhEtterUtregning
    }
    return tot
}

@Composable
fun Production(apiData: ApiData) {
    Row (modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        if (apiData.isLoading.not() and apiData.weatherData.isNotEmpty()) {
            Column  (
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\uD83D\uDCC6 Årlig resulatat",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize * 1.35f
                    ),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    " ⚡ Produksjon: ${getYearlyProduction(apiData).toInt()} kWh",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(6.dp))

                Text(
                    "💰 Anslått besparelse: ${(getYearlyProduction(apiData) * 0.5).toInt()} kr",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    }
}