package in2000.team42.ui.screens.home.bottomSheet


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import in2000.team42.R

@Composable
fun GreetingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Velkommen!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Rask guide
        Text(
            text = "For å starte, søk etter en adresse i feltet over og trykket på bygget som skal ha solcellepaneler.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        // Logo
        Image(
            painter = painterResource(id = R.drawable.hovedlogo),
            contentDescription = "App Logo",
            modifier = Modifier.size(250.dp)
        )
    }
}