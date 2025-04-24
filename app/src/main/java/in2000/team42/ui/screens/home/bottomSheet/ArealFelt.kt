package in2000.team42.ui.screens.home.bottomSheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import in2000.team42.R

@Composable
fun ArealFelt(areal: Float, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = String.format("%.2f m²", areal),
            style = MaterialTheme.typography.titleMedium,
        )
        Image(
            painter = painterResource(id = R.drawable.square),
            contentDescription = "Areal",
            modifier = Modifier
                .padding(start = 8.dp)
                .size(24.dp)
        )

    }
}