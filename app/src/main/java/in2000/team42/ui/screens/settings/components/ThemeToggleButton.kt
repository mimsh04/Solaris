package in2000.team42.ui.screens.settings.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ThemeToggleButton(isDarkTheme: Boolean, onThemeToggle: () -> Unit) {
    Button(
        onClick = onThemeToggle,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = if (isDarkTheme) "Bytt til lys tema" else "Bytt til mørkt tema",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}