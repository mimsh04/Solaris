package in2000.team42.ui.screens.home.bottomSheet.charts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import in2000.team42.R
import in2000.team42.data.pgvis.model.DailyProfile

private val monthNames = listOf(
    "Januar", "Februar", "Mars", "April", "Mai", "Juni",
    "Juli", "August", "September", "Oktober", "November", "Desember"
)
private fun formatMonthTime (dailyProfile: DailyProfile ) =

    "${dailyProfile.time} ${monthNames[dailyProfile.month!! - 1]}"


@Composable
fun SolarRadiationChart(modifier: Modifier = Modifier, solData: List<DailyProfile>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val lineColor = Color(0xffd4ac3f)
    LaunchedEffect(solData) {
        modelProducer.runTransaction {
            lineSeries {
                series(
                    x = solData.indices.map { it.toFloat() },
                    y = solData.map { it.globalIrradiance ?: 0f }
                )
            }
        }
    }
    Column {
        Box (
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.HomeScreen_avg_sunradiation),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        CartesianChartHost(
            chart =
                rememberCartesianChart(
                    rememberLineCartesianLayer(
                        pointSpacing = 10.dp,
                        lineProvider = LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(
                                fill = LineCartesianLayer.LineFill.single(fill(lineColor)),
                            )
                        ),
                    ),
                    startAxis = VerticalAxis.rememberStart(
                        valueFormatter =
                            { _, value, _ ->
                                "${value.toInt()} kWh/m²"
                            }
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned(
                            spacing = {12}
                        ) },
                        valueFormatter =
                            { _, value, _ ->
                                formatMonthTime(solData[value.toInt()])
                            }
                        ,
                    ),
                ),
            modelProducer = modelProducer,
            modifier = modifier,
        )
        Spacer(modifier = Modifier.padding(6.dp))
    }

}