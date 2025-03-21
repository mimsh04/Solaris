package in2000.team42.ui.screens.home.bottomSheetKomp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun Vinkelinputs(
    modifier: Modifier = Modifier,
    incline: Float,
    direction: Float,
    onInclineChange: (Float) -> Unit,
    onDirectionChange: (Float) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Angle (Incline) Input
        Vinkelinput(
            "Angle (0-90°)",
            incline,
            onInclineChange,
            0..90,
            Icons.Default.Build
        )

        Vinkelinput(
            "Direction (-180 - 180°)",
            direction,
            onDirectionChange,
            -180..180,
            Icons.Default.Build
        )
    }
}