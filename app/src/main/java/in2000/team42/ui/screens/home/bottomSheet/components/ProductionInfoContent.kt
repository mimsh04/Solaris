package in2000.team42.ui.screens.home.bottomSheet.components


import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import in2000.team42.R

@Composable
fun ProductionInfoContent(yearlyKwh: Int) {
    val infoText = when {
        yearlyKwh == 0 -> stringResource(R.string.production_info_zero)
        yearlyKwh >= 10000 -> stringResource(
            R.string.production_info_high,
            yearlyKwh,
            (yearlyKwh / 365.0).toInt()
        )
        yearlyKwh in 5000..9999 -> stringResource(
            R.string.production_info_medium,
            yearlyKwh,
            (yearlyKwh / 365.0).toInt()
        )
        else -> stringResource(
            R.string.production_info_low,
            yearlyKwh,
            (yearlyKwh / 365.0).toInt()
        )
    }

    Text(
        text = infoText,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Normal
    )
}