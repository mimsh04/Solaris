package in2000.team42.ui.screens.home.bottomSheet

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import in2000.team42.data.pgvis.model.KwhMonthlyResponse
import in2000.team42.ui.screens.home.ApiData
import in2000.team42.ui.screens.home.HomeViewModel

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
            Text("Ingen data (velg posisjon med search)")
        } else {
            Column  (
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text("Årlig produksjon: ${getYearlyProduction(apiData.kwhMonthlyData)} kWh")
            }

        }
    }

}