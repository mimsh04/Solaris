package in2000.team42.ui.screens.home.bottomSheet.configuration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import in2000.team42.R
import in2000.team42.data.solarPanels.SolarPanelModel
import java.util.Locale

@Composable
fun DropdownItem(
    panelInfo: SolarPanelModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        text = {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = panelInfo.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = String.format(
                        Locale.getDefault(),
                        stringResource(R.string.homescreen_effect_and_price),
                        panelInfo.efficiency,
                        panelInfo.pricePerM2
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        onClick = onClick,
        modifier = modifier
    )
}