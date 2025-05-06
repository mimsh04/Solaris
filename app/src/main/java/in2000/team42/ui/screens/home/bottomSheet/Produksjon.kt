package in2000.team42.ui.screens.home.bottomSheet


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import in2000.team42.data.pgvis.model.KwhMonthlyResponse
import in2000.team42.ui.screens.home.ApiData

@Composable
fun Produksjon(apiData: ApiData) {

    fun getYearlyProduction(kwInp: List<KwhMonthlyResponse.MonthlyKwhData>): Float {
        var tot = 0f
        kwInp.forEach {
            tot += it.averageMonthly.toFloat()
        }
        return tot
    }

    Row (modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        if (apiData.kwhMonthlyData.isEmpty()) {
            Text("Trykk på regn ut produksjon knappen", color = MaterialTheme.colorScheme.onBackground)
        } else {
            Column  (
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text("Årlig produksjon: ${getYearlyProduction(apiData.kwhMonthlyData)} kWh",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                    )
            }

        }
    }

}