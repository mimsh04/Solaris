package in2000.team42.ui.screens.home.bottomSheet

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun Vinkelinput(
    tittel: String,
    verdi: Float,
    onValueChange: (Float) -> Unit,
    range: IntRange = 0..90,
    icon: ImageVector
) {

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Angle",
            modifier = Modifier.padding(end = 12.dp)
        )
        OutlinedTextField(
            value = verdi.toInt().toString(),
            onValueChange = { newValue ->
                newValue.toIntOrNull()?.let {
                    if (it in range) onValueChange(it.toFloat())
                }
            },
            label = { Text(tittel) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }

}