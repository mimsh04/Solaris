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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "Incline angle",
                modifier = Modifier.padding(end = 12.dp)
            )
            OutlinedTextField(
                value = incline.toInt().toString(),
                onValueChange = { newValue ->
                    newValue.toIntOrNull()?.let {
                        if (it in 0..90) onInclineChange(it.toFloat())
                    }
                },
                label = { Text("Incline (0-90°)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Direction (Azimuth) Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "Direction angle",
                modifier = Modifier.padding(end = 12.dp)
            )
            OutlinedTextField(
                value = direction.toInt().toString(),
                onValueChange = { newValue ->
                    newValue.toIntOrNull()?.let {
                        val normalizedValue = it % 360
                        onDirectionChange(if (normalizedValue < 0) (normalizedValue + 360).toFloat() else normalizedValue.toFloat())
                    }
                },
                label = { Text("Direction (0-360°)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}