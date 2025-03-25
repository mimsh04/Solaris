package in2000.team42.ui.screens.home.bottomSheetKomp

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun NavigationButtons(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    enabled: Boolean
) {
    Row {
        IconButton(
            onClick = onPrevious,
            enabled = enabled,
            modifier = Modifier
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Forrige",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(16.dp)
            )
        }
        IconButton(
            onClick = onNext,
            enabled = enabled,
            modifier = Modifier

        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Neste",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
