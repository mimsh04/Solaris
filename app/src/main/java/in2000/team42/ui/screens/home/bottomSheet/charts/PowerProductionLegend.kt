package in2000.team42.ui.screens.home.bottomSheet.charts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import in2000.team42.R

@Composable

fun PowerProductionLegend(colors: List<Color>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(4) { index ->
                LegendItem(when ( index ) {
                    0 -> stringResource(R.string.HomeScreen_potential_production)
                    1 -> stringResource(R.string.HomeScreen_production_after_estimated_loss)
                    2 -> stringResource(R.string.homescreen_production_loss_to_cloud_coverage)
                    3 -> stringResource(R.string.homescreen_production_loss_to_snow)
                    else -> stringResource(R.string.homescreen_production_unknown)
                }, colors[index])
            }
        }
    }
}