package in2000.team42.ui.screens.home.bottomSheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdresseFelt(adresse: String, modifier: Modifier = Modifier) {
    Column {
        if (adresse.isNotEmpty()) {
            Column (
                modifier = modifier,
            ) {
                Text(
                    text = "Valgt adresse:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = adresse,
                    modifier = modifier,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
            }

        } else {
            Text(
                text = "Bruk søkefeltet eller trykk på bygg på kartet for å velge en adresse",
                modifier = modifier
            )
        }
        Spacer(modifier = Modifier.padding(bottom = 8.dp))
    }

}