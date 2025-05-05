package in2000.team42.ui.screens.home.map.WeatherIcon

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreditsScreen() {
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
                text = "Krediteringer",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            CreditItem("Snø sky ikon av Freepik fra Flaticon (www.flaticon.com)")
            CreditItem("Ukjent vær ikon av Vecteezy (www.vecteezy.com)")
            CreditItem("sky ikon fra CleanPNG (www.cleanpng.com)")
            CreditItem("sol ikon av Freepik fra Flaticon (www.flaticon.com)")
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