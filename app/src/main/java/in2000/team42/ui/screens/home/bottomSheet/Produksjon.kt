package in2000.team42.ui.screens.home.bottomSheet

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import in2000.team42.data.pgvis.model.KwhMonthlyResponse
import in2000.team42.ui.screens.home.HomeViewModel

@Composable
fun Produksjon(viewModel: HomeViewModel) {
    val kwhPerMonth = viewModel.kwhMonthlyData.collectAsState()

    fun getYearlyProduction(kwInp: List<KwhMonthlyResponse.MonthlyKwhData>): Float {
        var tot = 0f
        kwInp.forEach {
            tot += it.averageMonthly.toFloat()
            Log.d("Produksjon", "Måned: ${it.month}, Produksjon: ${it.averageMonthly}")
        }
        return tot
    }

    Row {
        if (kwhPerMonth.value.isEmpty()) {
            Text("Ingen data (velg posisjon med search)")
        } else {
            Column {
                Text("Årlig produksjon: ${getYearlyProduction(kwhPerMonth.value)} kWh")
                Button(
                    onClick = {
                        viewModel.updateAllApi()
                    }
                ) {
                    Text("Hent på nytt")
                }
            }

        }
    }

}