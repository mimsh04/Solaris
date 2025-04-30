package in2000.team42.ui.screens.home.bottomSheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun AdresseFelt(adresse: String, modifier: Modifier = Modifier) {
    Column (modifier = modifier) {
        Column {
            Text(
                text = "Valgt adresse:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = adresse,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .width(270.dp),
                maxLines = 1,
                style = MaterialTheme.typography.titleLarge,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.padding(bottom = 8.dp))
    }

}