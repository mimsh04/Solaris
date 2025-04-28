package in2000.team42.ui.screens.home.bottomSheet.grafer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.runBlocking
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import in2000.team42.data.pgvis.model.DailyProfile

private val monthNames = listOf(
    "Januar", "Februar", "Mars", "April", "Mai", "Juni",
    "Juli", "August", "September", "Oktober", "November", "Desember"
)
private fun formatMonthTime (dailyProfile: DailyProfile ) =

    "${dailyProfile.time} ${monthNames[dailyProfile.month!! - 1]}"


@Composable
fun Solradiasjon(modifier: Modifier = Modifier, solData: List<DailyProfile>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries {
                series(
                    x = solData.indices.map { it.toFloat() },
                    y = solData.map { it.globalIrradiance ?: 0f }
                )
            }
        }
    }
    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(
                pointSpacing = 10.dp
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
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
}