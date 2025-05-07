package in2000.team42.ui.screens.home.bottomSheet.grafer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import in2000.team42.data.pgvis.model.KwhMonthlyResponse
import in2000.team42.data.productionCalculation.calculateWithCoverage
import in2000.team42.ui.screens.home.DisplayWeather

private val monthNames = listOf(
    "Januar", "Februar", "Mars", "April", "Mai", "Juni",
    "Juli", "August", "September", "Oktober", "November", "Desember"
)

@Composable
fun StromProduksjonGraf (
    kwhMonthlyData: List<KwhMonthlyResponse.MonthlyKwhData>,
    weatherData: List<DisplayWeather>,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val colors = listOf(
        Color(0xff6438a7),
        Color(0xff3490de),
        Color(0xff73e8dc),
        Color(0xfff6b93b)
    )
    LaunchedEffect(kwhMonthlyData, weatherData) {
        val utregnetData = calculateWithCoverage(kwhMonthlyData, weatherData)
        modelProducer.runTransaction {
            lineSeries {
                series(
                    x = utregnetData.map { it.month },
                    y = utregnetData.map { it.kWhPotential }
                )
            }
            lineSeries {
                series(
                    x = utregnetData.map { it.month },
                    y = utregnetData.map { it.kWhEtterUtregning }
                )
            }
            lineSeries {
                series(
                    x = utregnetData.map { it.month },
                    y = utregnetData.map { it.skyTap }
                )
            }
            lineSeries {
                series(
                    x = utregnetData.map { it.month },
                    y = utregnetData.map { it.snoTap }
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
            Text("Produksjon med værdata",
                style = MaterialTheme.typography.titleMedium
            )
        }

        StromProduksjonLegend(colors)
        CartesianChartHost(
            chart =
                rememberCartesianChart(

                    rememberLineCartesianLayer(
                        lineProvider = LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(
                                fill = LineCartesianLayer.LineFill.single(fill(colors[0])),
                            )
                        ),
                    ),
                    rememberLineCartesianLayer(
                        lineProvider = LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(
                                fill = LineCartesianLayer.LineFill.single(fill(colors[1])),
                            )
                        ),
                    ),
                    rememberLineCartesianLayer(
                        lineProvider = LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(
                                fill = LineCartesianLayer.LineFill.single(fill(colors[2])),
                            )
                        ),
                    ),
                    rememberLineCartesianLayer(
                        lineProvider = LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(
                                fill = LineCartesianLayer.LineFill.single(fill(colors[3])),
                            )
                        ),
                    ),
                    startAxis = VerticalAxis.rememberStart(
                        valueFormatter =
                            { _, value, _ ->
                                "${value.toInt()} kWh"
                            }
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned(
                            spacing = { 1 }
                        ) },
                        valueFormatter =
                            { _, value, _ ->
                                monthNames[value.toInt() - 1]
                            },

                        labelRotationDegrees = 90f
                    ),
                ),
            modelProducer = modelProducer,
            modifier = modifier,
        )
    }

}