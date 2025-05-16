package in2000.team42.ui.screens.home.bottomSheet.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import in2000.team42.R
import in2000.team42.data.productionCalculation.calculateWithCoverage
import in2000.team42.ui.screens.home.ApiData
import in2000.team42.utils.LocalizationManager

private fun getYearlyProduction(
    apiData: ApiData
): Double {
    val calculatedData = calculateWithCoverage(apiData.kwhMonthlyData, apiData.weatherData)
    var tot = 0.0
    calculatedData.forEach {
        tot += it.kWhAfterCalculation
    }
    return tot
}

@Composable
fun Production(apiData: ApiData) {
    var showInfoDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current // Get the current context

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (apiData.isLoading.not() and
            apiData.weatherData.isNotEmpty() and
            apiData.kwhMonthlyData.isNotEmpty()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\uD83D\uDCC6" + LocalizationManager.getString(context, R.string.home_result_annual_production),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = MaterialTheme.typography.titleLarge.fontSize * 1.35f
                        ),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                CircleShape
                            )
                            .clickable { showInfoDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "i",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    " ⚡" + LocalizationManager.getString(context, R.string.home_label_production) + " ${getYearlyProduction(apiData).toInt()} kWh",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    "💰 " + LocalizationManager.getString(context, R.string.home_estimated_savings) + " ${(getYearlyProduction(apiData) * 0.5).toInt()} NOK",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }

    if (showInfoDialog) {
        Dialog(
            onDismissRequest = { showInfoDialog = false },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Box(
                modifier = Modifier
                    .width(600.dp)
                    .height(500.dp)
                    .background(
                        MaterialTheme.colorScheme.background,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = LocalizationManager.getString(context, R.string.home_expected_operation_off_grid),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    ProductionInfoContent(yearlyKwh = getYearlyProduction(apiData).toInt())
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(
                        onClick = { showInfoDialog = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(LocalizationManager.getString(context, R.string.home_production_close_button))
                    }
                }
            }
        }
    }
}