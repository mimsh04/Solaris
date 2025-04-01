package in2000.team42.ui.screens.home.bottomSheet

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun Vinkelinput(
    tittel: String,
    verdi: Float,
    onValueChange: (Float) -> Unit,
    range: IntRange = 0..90,
) {
    var showedVal by remember { mutableStateOf(verdi.toInt().toString()) }

    fun getOutlineColor(showedText: String): Color {
        return if (showedText.isEmpty()) Color.Red
        else if (showedText.toIntOrNull() in range) Color.Green
        else Color.Red
    }

    Row(
        modifier = Modifier
    ) {
        OutlinedTextField(
            value = showedVal,
            onValueChange = { newValue ->
                showedVal = newValue
                if (showedVal.isEmpty()) onValueChange(0f)
                newValue.toFloatOrNull()?.let {
                    if (it.toInt() in range) {
                        onValueChange(it)
                    } else {
                        onValueChange(0f)
                    }
                }
            },
            label = { Text(tittel) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.width(180.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getOutlineColor(showedVal),
                unfocusedBorderColor = getOutlineColor(showedVal)
            )
        )
    }

}