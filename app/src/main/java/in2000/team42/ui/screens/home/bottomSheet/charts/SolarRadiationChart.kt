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
import androidx.compose.ui.platform.LocalContext
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
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

@Composable
fun SolarRadiationChart(modifier: Modifier = Modifier, solData: List<DailyProfile>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val lineColor = Color(0xffd4ac3f)

    val context = LocalContext.current

    val monthNames = remember {
        listOf(
            context.getString(R.string.jan),
            context.getString(R.string.feb),
            context.getString(R.string.mar),
            context.getString(R.string.apr),
            context.getString(R.string.may),
            context.getString(R.string.jun),
            context.getString(R.string.jul),
            context.getString(R.string.aug),
            context.getString(R.string.sept),
            context.getString(R.string.oct),
            context.getString(R.string.nov),
            context.getString(R.string.des)
        )
    }

    // Update the chart model when solData changes
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
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.home_average_sun_radiation),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    pointSpacing = 10.dp,
                    lineProvider = LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(lineColor)),
                        )
                    ),
                ),
                startAxis = VerticalAxis.rememberStart(
                    label = rememberAxisLabelComponent(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    valueFormatter = { _, value, _ ->
                        "${value.toInt()} kWh/m²"
                    }
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    label = rememberAxisLabelComponent(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned(spacing = { 12 }) },
                    valueFormatter = { _, value, _ ->
                        val dailyProfile = solData[value.toInt()]
                        "${dailyProfile.time} ${monthNames[dailyProfile.month!! - 1]}"
                    }
                ),
            ),
            modelProducer = modelProducer,
            modifier = modifier,
        )
        Spacer(modifier = Modifier.padding(6.dp))
    }
}