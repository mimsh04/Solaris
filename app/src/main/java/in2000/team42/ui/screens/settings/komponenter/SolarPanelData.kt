package in2000.team42.ui.screens.settings.komponenter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import in2000.team42.R
import in2000.team42.theme.IN2000_team42Theme

@Composable
fun SolarPanelData(){

    Text(
        text = "Solar Panel Data",
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        modifier = Modifier
            .padding(top = 25.dp, bottom = 10.dp)
            .fillMaxWidth()
            .wrapContentWidth(Alignment.Start),
        color = MaterialTheme.colorScheme.onBackground
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_energy_savings_leaf_24),
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 10.dp)
            )

            Text(
                text = "Energy Output",
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 25.dp)
                    .weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "25 kWh/day",
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 25.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp),
            thickness = DividerDefaults.Thickness,
            color = MaterialTheme.colorScheme.outline
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_battery_charging_full_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 10.dp)
            )
            Text(
                text = "Battery Status",
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 40.dp, bottom = 10.dp)
                    .weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "90% Charged",
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 40.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
@Preview
fun SolarPanelDataPreview() {
    IN2000_team42Theme {
        SolarPanelData()
    }
}
