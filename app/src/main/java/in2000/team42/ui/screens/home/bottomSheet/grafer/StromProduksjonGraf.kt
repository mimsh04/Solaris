package in2000.team42.ui.screens.home.bottomSheet.grafer

import androidx.compose.runtime.Composable
import in2000.team42.data.pgvis.model.KwhMonthlyResponse
import in2000.team42.ui.screens.home.DisplayWeather

@Composable
fun StromProduksjonGraf (
    kwhMonthlyData: List<KwhMonthlyResponse.MonthlyKwhData>,
    weatherData: List<DisplayWeather>
) {
    kwhMonthlyData[0].averageMonhtlyRadiation
}