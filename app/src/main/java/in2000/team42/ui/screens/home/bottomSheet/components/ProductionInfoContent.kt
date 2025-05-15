package in2000.team42.ui.screens.home.bottomSheet.components


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import in2000.team42.R

@SuppressLint("LocalContextConfigurationRead")
@Composable
fun ProductionInfoContent(yearlyKwh: Int) {
    val context = LocalContext.current
    val currentLocale = context.resources.configuration.locales[0].language
    Log.d("ProductionInfo","Current locale in ProductionInfoContent: $currentLocale")
    //val testString = remember(currentLocale) { context.getString(R.string.production_info_zero) }
    val testString = context.getString(R.string.production_info_zero)
    println("ProductionInfoContent loaded string: $testString")

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