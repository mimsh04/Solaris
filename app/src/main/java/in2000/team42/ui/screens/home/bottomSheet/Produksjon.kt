package in2000.team42.ui.screens.home.bottomSheet


import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import in2000.team42.data.pgvis.model.KwhMonthlyResponse
import in2000.team42.data.produksjonKalkulering.calculateWithCoverage
import in2000.team42.ui.screens.home.ApiData

fun getYearlyProduction(
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
fun Produksjon(apiData: ApiData) {
    Row (modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        if (apiData.kwhMonthlyData.isEmpty() and apiData.isLoading.not()) {
            Text("Trykk på regn ut produksjon knappen", color = MaterialTheme.colorScheme.onBackground)
        } else {
            if (apiData.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                }
            } else {
                Column  (
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text("Årlig produksjon: ${getYearlyProduction(apiData).toInt()} kWh",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }


        }
    }

}