package in2000.team42.ui.screens.home.map.weatherIcon

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import in2000.team42.R

@Composable
fun CreditsScreen(context: Context) {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = androidx.compose.ui.Alignment.Start
        ) {
            Text(
                text = context.getString(R.string.credits_label),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            CreditItem(context.getString(R.string.snow_cloud_icon_attribution))
            CreditItem(context.getString(R.string.unknown_weather_icon_attribution))
            CreditItem(context.getString(R.string.cloud_icon_attribution))
            CreditItem(context.getString(R.string.sun_icon_attribution))
        }
    }
}

@Composable
fun CreditItem(credit: String) {
    Text(
        text = "• $credit",
        style = MaterialTheme.typography.bodyMedium,
        fontSize = 16.sp,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}